package com.yuqian.itax.api.annotation.lock;

import cn.hutool.json.JSONUtil;
import com.yuqian.itax.common.base.exception.BusinessException;
import com.yuqian.itax.util.util.Md5Util;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.Objects;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Redis 分布式锁
 *
 * @date 2020/8/26
 */
@Component
@Aspect
@Slf4j
public class RedisLockAspect {

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 存储目前有效的key定义
     */
    private static ConcurrentLinkedQueue<RedisLockDefinitionHolder> holderList = new ConcurrentLinkedQueue();


    /**
     * 线程池，维护keyAliveTime
     */
    private static final ScheduledExecutorService SCHEDULER = new ScheduledThreadPoolExecutor(1,
            new BasicThreadFactory.Builder().namingPattern("redisLock-schedule-pool").daemon(true).build());

    {
        // 两秒执行一次「续时」操作
        SCHEDULER.scheduleAtFixedRate(() -> {
            // 这里记得加 try-catch，否者报错后定时任务将不会再执行=-=
            Iterator<RedisLockDefinitionHolder> iterator = holderList.iterator();
            try {
                while (iterator.hasNext()) {
                    RedisLockDefinitionHolder holder = iterator.next();
                    // 判空
                    if (holder == null) {
                        iterator.remove();
                        continue;
                    }

                    // 判断 key 是否还有效，无效的话进行移除
                    if (redisTemplate.opsForValue().get(holder.getBusinessKey()) == null) {
                        iterator.remove();
                        continue;
                    }

                    // 超时重试次数，超过时给线程设定中断
                    if (holder.getCurrentCount() > holder.getTryCount()) {
                        holder.getCurrentTread().interrupt();
                        iterator.remove();
                        continue;
                    }

                    // 判断是否进入最后三分之一时间
                    long curTime = System.currentTimeMillis();
                    boolean shouldExtend = (holder.getLastModifyTime() + holder.getModifyPeriod()) <= curTime;
                    if (shouldExtend) {
                        holder.setLastModifyTime(curTime);
                        redisTemplate.expire(holder.getBusinessKey(), holder.getLockTime(), TimeUnit.SECONDS);
                        log.info("业务唯一 key : [" + holder.getBusinessKey() + "], 重试计数 : " + holder.getCurrentCount());
                        holder.setCurrentCount(holder.getCurrentCount() + 1);
                    }
                }
            } catch (Exception e) {
                log.error("有错误，请再检查一下", e);
            }
        }, 0, 2, TimeUnit.SECONDS);
    }

    /**
     * @annotation 中的路径表示拦截特定注解
     */
    @Pointcut("@annotation(com.yuqian.itax.api.annotation.lock.RedisLockAnnotation)")
    public void redisLockPC() {
    }

    /**
     * Around 前后进行加锁和释放锁
     */
    @Around(value = "redisLockPC()")
    public Object around(ProceedingJoinPoint pjp) throws Throwable {
        // 解析请求头
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = Objects.requireNonNull(attributes).getRequest();
        String oemCode = request.getHeader("oemCode");
        String token = request.getHeader("token");

        // 解析参数
        Method method = resolveMethod(pjp);
        RedisLockAnnotation annotation = method.getAnnotation(RedisLockAnnotation.class);
        RedisLockTypeEnum typeEnum = annotation.typeEnum();
        Object[] bodyArgs = pjp.getArgs();
        if (null == bodyArgs || bodyArgs.length == 0) {
            throw new BusinessException("重复提交验证必须含有 body 内容参数");
        }
        // 处理 body 参数，把参数 md5 成一个字符串做 key
        String lockKey = Md5Util.md5ByGuava(JSONUtil.toJsonStr(bodyArgs));

        // 生成业务唯一 key
        String businessKey = typeEnum.getUniqueKey(lockKey);
        String uniqueValue = oemCode + "," + token;
        // 加锁
        Object result = null;
        try {
            boolean isSuccess = redisTemplate.opsForValue().setIfAbsent(businessKey, uniqueValue);
            log.info("是否存在 key:" + isSuccess);
            if (isSuccess) {
                redisTemplate.expire(businessKey, annotation.lockTime(), TimeUnit.SECONDS);
                Thread currentThread = Thread.currentThread();
                holderList.add(new RedisLockDefinitionHolder(businessKey, annotation.lockTime(), System.currentTimeMillis(),
                        currentThread, annotation.tryCount()));
                result = pjp.proceed();
                // 线程被中断，抛出异常，中断此次请求
                if (currentThread.isInterrupted()) {
                    log.error("线程被中断 =-=");
                    throw new InterruptedException("线程被中断");
                }
                return result;
            }
        } catch (InterruptedException e) {
            log.error("中断异常，回滚事务", e);
            Thread.currentThread().interrupt();
            throw new Exception("中断异常，请再次发送请求");
        } finally {
            // 请求结束后，强制删掉 key，释放锁
            redisTemplate.delete(businessKey);
            log.info("释放锁, 业务唯一 key [" + businessKey + "]");
        }

        log.info("重复请求，请稍后再试!");
        throw new BusinessException("重复请求，请稍后再试!");
    }

    /**
     * 解析参数
     */
    private Method resolveMethod(ProceedingJoinPoint pjp) {
        MethodSignature signature = (MethodSignature) pjp.getSignature();
        Class<?> targetClass = pjp.getTarget().getClass();

        Method method = getDeclaredMethodFor(
                targetClass,
                signature.getName(),
                signature.getMethod().getParameterTypes());
        if (method == null) {
            throw new IllegalStateException("无法解析目标方法: " + signature.getMethod().getName());
        }
        return method;
    }

    /**
     * 获取指定类上的指定方法
     *
     * @param clazz          指定类
     * @param name           指定方法
     * @param parameterTypes 参数类型列表
     * @return 找到就返回method，否则返回null
     */
    public static Method getDeclaredMethodFor(Class<?> clazz, String name, Class<?>... parameterTypes) {
        try {
            return clazz.getDeclaredMethod(name, parameterTypes);
        } catch (NoSuchMethodException e) {
            Class<?> superClass = clazz.getSuperclass();
            if (superClass != null) {
                return getDeclaredMethodFor(superClass, name, parameterTypes);
            }
        }
        return null;
    }
}
