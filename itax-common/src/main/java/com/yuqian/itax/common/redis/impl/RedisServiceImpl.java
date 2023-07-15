package com.yuqian.itax.common.redis.impl;

import com.alibaba.fastjson.JSON;
import com.yuqian.itax.common.redis.RedisService;
import com.yuqian.itax.common.util.CollectionUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.concurrent.TimeUnit;


@SuppressWarnings({"rawtypes", "unchecked"})
@Service("redisService")
public class RedisServiceImpl implements RedisService {

	@Autowired
	private StringRedisTemplate stringRedisTemplate;

	@Autowired
	private RedisTemplate redisTemplate;
	
	@Override
	public void set(String key, Object value) {
		set(key, value, 0);
	}	
	
	 //加锁
    /*
    * @param key id
    * @param value 当前时间+超时时间
    *
    * */
	 @Override
    public  boolean lock(String key,String value,int timeOut){
        if (stringRedisTemplate.opsForValue().setIfAbsent(key,value)){
        	stringRedisTemplate.expire(key, timeOut, TimeUnit.SECONDS);
            return true;//加锁成功就返回true
        }       
        String currentValue=stringRedisTemplate.opsForValue().get(key);
        //如果锁过期
        if (!StringUtils.isEmpty(currentValue) && Long.parseLong(currentValue)<System.currentTimeMillis()){//存储时间要小于当前时间        	
            //获取上一个锁的时间
            String oldValue=stringRedisTemplate.opsForValue().getAndSet(key,value);
            if (!StringUtils.isEmpty(oldValue)&& oldValue.equals(currentValue)){//上一个时间不为空,并且等于当前时间
            	stringRedisTemplate.expire(key, timeOut, TimeUnit.SECONDS);
                return true;
            }
        }        
        return  false;//失败返回false
    }
 
 
 
    //解锁
	@Override
	public void set(String key, Object value, int timeout) {
		String jsonValue = null;
		if (value instanceof String) {
			jsonValue = (String) value;
		} else {
			jsonValue = JSON.toJSONString(value);
		}
		stringRedisTemplate.opsForValue().set(key, jsonValue);
		if (timeout > 0) { // 超时时间大于0 设置超时时间
			stringRedisTemplate.expire(key, timeout, TimeUnit.SECONDS);
		}
	}

	@Override
	public void unlock(String key,String value){//执行删除可能出现异常需要捕获
		try {
			String currentValue = stringRedisTemplate.opsForValue().get(key);
			if (!StringUtils.isEmpty(currentValue) && currentValue.equals(value)) {//如果不为空,就删除锁
				stringRedisTemplate.opsForValue().getOperations().delete(key);
			}
		}catch (Exception e){
			throw new RuntimeException("[redis分布式锁] 解锁异常",e);
		}
	}

	@Override
	public String get(String key) {
		return stringRedisTemplate.opsForValue().get(key);
	}

	@Override
	public void delete(String key) {
		stringRedisTemplate.delete(key);
	}

	@Override
	public void setObject(String key, Object value) {
		redisTemplate.opsForValue().set(key, value); 
	}
	
	@Override
	public void setObject(String key, Object value, int timeOut) {
		redisTemplate.opsForValue().set(key, value); 
		if (timeOut > 0) { // 超时时间大于0 设置超时时间
			redisTemplate.expire(key, timeOut, TimeUnit.MINUTES);
		}
	}

	@Override
	public Object getObject(String key) {
		return redisTemplate.boundValueOps(key).get();
	}

	@Override
	public Long increment(String key, long delta) {
		return stringRedisTemplate.opsForValue().increment(key,delta);
	}

	@Override
	public Long increment(String key, long delta, int timeOut, boolean reSetTime) {
		Long increment = stringRedisTemplate.opsForValue().increment(key, delta);
		if(!reSetTime) {
			//不重置时间
			stringRedisTemplate.expire(key, stringRedisTemplate.getExpire(key), TimeUnit.SECONDS);
		}else if(timeOut > 0) {
			//重置时间，并且超时时间大于0
			stringRedisTemplate.expire(key, timeOut, TimeUnit.SECONDS);
		}
		return increment;
	}
	
	@Override
	public Set<String> keys(String pattern) {
		return stringRedisTemplate.keys(pattern);
	}

	@Override
	public void deleteAllAndSet(String pattern, String key, Object value, int timeout) {
		Set<String> keys = keys(pattern);
		if(CollectionUtil.isNotEmpty(keys)) {
			for (String key1 : keys) {
				delete(key1);
			}
		}
		set(key, value, timeout);
	}
}
