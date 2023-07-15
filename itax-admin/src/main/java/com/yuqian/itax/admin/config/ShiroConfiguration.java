package com.yuqian.itax.admin.config;

import com.yuqian.itax.admin.shiro.AuthRealm;
import com.yuqian.itax.admin.shiro.AuthcFilter;
import com.yuqian.itax.admin.shiro.FormAuthenticationFilter;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;
import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.mgt.eis.AbstractSessionDAO;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.filter.mgt.DefaultFilter;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.util.Assert;

import javax.servlet.Filter;
import java.io.IOException;
import java.io.Serializable;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * shiro配置
 *
 * @author seer
 * @date 2018/2/1 15:41
 */
@Configuration
@Slf4j
public class ShiroConfiguration {

	@Value("${permission-config.perms.isAuthc}")
	private boolean isAuthc;
	/**
	 * 缓存 key 前缀
	 */
	private static final String SHIRO_REDIS_CACHE_KEY_PREFIX = ShiroConfiguration.class.getName() + "_shiro.redis.cache_";

	/**
	 * session key 前缀
	 */
	private static final String SHIRO_REDIS_SESSION_KEY_PREFIX = ShiroConfiguration.class.getName() + "shiro.redis.session_";

	private static final String MESSAGE = "redisConnection is null";

	/**
	 * Filter 工厂
	 * <p>
	 * 通过自定义 Filter 实现校验逻辑的重写和返回值的定义 {@link ShiroFilterFactoryBean#setFilters(Map)
	 * 对一个 URL 要进行多个 Filter 的校验。通过 {@link ShiroFilterFactoryBean#setFilterChainDefinitions(String)} 实现
	 * 通过 {@link ShiroFilterFactoryBean#setFilterChainDefinitionMap(Map)} 实现的拦截不方便实现实现多 Filter 校验，所以这里没有使用
	 * <p>
	 * 权限的名称可以随便指定的，和 URL 配置的 Filter 有关，这里使用 {@link DefaultFilter} 默认的的权限定义，覆盖了原权限拦截器
	 * 授权Filter {@link AuthcFilter}
	 *
	 * @param securityManager
	 * @return
	 */
	@Bean
	public ShiroFilterFactoryBean shiroFilter(SecurityManager securityManager) {
		ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
		shiroFilterFactoryBean.setSecurityManager(securityManager);

		Map<String, Filter> filterMap = new LinkedHashMap<>();
		filterMap.put(DefaultFilter.authc.toString(), new AuthcFilter());
		filterMap.put(DefaultFilter.anon.toString(), new FormAuthenticationFilter());
		shiroFilterFactoryBean.setFilters(filterMap);

		shiroFilterFactoryBean.setFilterChainDefinitionMap(filterChainDefinitionMap()); //获取拦截url规则
		return shiroFilterFactoryBean;
	}

	/**
	 * 安全管理器
	 *
	 * @param authRealm                自定义 realm {@link #authRealm(CacheManager)}
	 * @param shiroRedisSessionManager 自定义 session 管理器 {@link #shiroRedisSessionManager(RedisTemplate)}
	 * @return @link org.apache.shiro.mgt.SecurityManager}
	 */
	@Bean
	public SecurityManager securityManager(AuthRealm authRealm, DefaultWebSessionManager shiroRedisSessionManager) {
		DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
		securityManager.setRealm(authRealm);
		securityManager.setSessionManager(shiroRedisSessionManager);
		return securityManager;
	}

	/**
	 * 用户Realm
	 * <p>
	 * shiro默认缓存这里还有点坑需要填
	 *
	 * @return
	 */
	@Bean
	public AuthRealm authRealm(CacheManager shiroRedisCacheManager) {
		AuthRealm userRealm = new AuthRealm();
		userRealm.setCachingEnabled(false);
		userRealm.setAuthenticationCachingEnabled(false);
		userRealm.setAuthorizationCachingEnabled(false);
		userRealm.setCacheManager(shiroRedisCacheManager);
		return userRealm;
	}

	/**
	 * 缓存管理器
	 *
	 * @param redisTemplate shiro的对象总是有这样那样的问题，所以 redisTemplate 使用 {@link org.springframework.data.redis.serializer.JdkSerializationRedisSerializer} 序列化值
	 * @return
	 */
	@Bean
	public CacheManager shiroRedisCacheManager(RedisTemplate redisTemplate) {
		//seer 2018/6/28 17:07 缓存这里反序列化有点问题，需要重写一下
		return new CacheManager() {
			@Override
			public <K, V> Cache<K, V> getCache(String s) throws CacheException {
				log.trace("shiro redis cache manager get cache. name={} ", s);
				return new Cache<K, V>() {
					@Override
					public V get(K k) throws CacheException {
						log.trace("shiro redis cache get.{} K={}", s, k);
						return ((V) redisTemplate.opsForValue().get(generateCacheKey(s, k)));
					}

					@Override
					public V put(K k, V v) throws CacheException {
						log.trace("shiro redis cache put.{} K={} V={}", s, k, v);
						V result = (V) redisTemplate.opsForValue().get(generateCacheKey(s, k));

						redisTemplate.opsForValue().set(generateCacheKey(s, k), v);
						return result;
					}

					@Override
					public V remove(K k) throws CacheException {
						log.trace("shiro redis cache remove.{} K={}", s, k);
						V result = (V) redisTemplate.opsForValue().get(generateCacheKey(s, k));

						redisTemplate.delete(generateCacheKey(s, k));
						return result;
					}

					/**
					 * clear
					 * <p>
					 *     redis keys 命令会造成堵塞
					 *     redis scan 命令不会造成堵塞
					 *
					 * @throws CacheException
					 */
					@Override
					public void clear() throws CacheException {
						log.trace("shiro redis cache clear.{}", s);
						RedisConnection redisConnection = redisTemplate.getConnectionFactory().getConnection();
						Assert.notNull(redisConnection, MESSAGE);
						try (Cursor<byte[]> cursor = redisConnection.scan(ScanOptions.scanOptions()
								.match(generateCacheKey(s, "*"))
								.count(Integer.MAX_VALUE)
								.build())) {
							while (cursor.hasNext()) {
								redisConnection.del(cursor.next());
							}
						} catch (IOException e) {
							log.error("shiro redis cache clear exception", e);
						}
					}

					@Override
					public int size() {
						log.trace("shiro redis cache size.{}", s);
						AtomicInteger count = new AtomicInteger(0);
						RedisConnection redisConnection = redisTemplate.getConnectionFactory().getConnection();
						Assert.notNull(redisConnection, MESSAGE);
						try (Cursor<byte[]> cursor = redisConnection.scan(ScanOptions.scanOptions()
								.match(generateCacheKey(s, "*"))
								.count(Integer.MAX_VALUE)
								.build())) {
							while (cursor.hasNext()) {
								count.getAndIncrement();
							}
						} catch (IOException e) {
							log.error("shiro redis cache size exception", e);
						}
						return count.get();
					}

					@Override
					public Set<K> keys() {
						log.trace("shiro redis cache keys.{}", s);
						StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
						Set<K> keys = new HashSet<>();
						RedisConnection redisConnection = redisTemplate.getConnectionFactory().getConnection();
						Assert.notNull(redisConnection, MESSAGE);
						try (Cursor<byte[]> cursor = redisConnection.scan(ScanOptions.scanOptions()
								.match(generateCacheKey(s, "*"))
								.count(Integer.MAX_VALUE)
								.build())) {
							while (cursor.hasNext()) {
								keys.add((K) stringRedisSerializer.deserialize(cursor.next()));
							}
						} catch (IOException e) {
							log.error("shiro redis cache keys exception", e);
						}
						return keys;
					}

					@Override
					public Collection<V> values() {
						return new ArrayList<>();
					}
				};
			}
		};
	}


	/**
	 * session管理器
	 *
	 * @param redisTemplate shiro的对象总是有这样那样的问题，所以 redisTemplate 使用 {@link org.springframework.data.redis.serializer.JdkSerializationRedisSerializer} 序列化值
	 * @return
	 */
	@Bean
	public DefaultWebSessionManager shiroRedisSessionManager(RedisTemplate redisTemplate) {
		DefaultWebSessionManager defaultWebSessionManager = new DefaultWebSessionManager();
		defaultWebSessionManager.setGlobalSessionTimeout(1800000);
		defaultWebSessionManager.setSessionValidationInterval(900000);
		defaultWebSessionManager.setDeleteInvalidSessions(true);
		defaultWebSessionManager.setSessionDAO(
				new AbstractSessionDAO() {
					@Override
					protected Serializable doCreate(Session session) {
						Serializable sessionId = this.generateSessionId(session);
						log.trace("shiro redis session create. sessionId={}", sessionId);
						this.assignSessionId(session, sessionId);
						redisTemplate.opsForValue().set(generateSessionKey(sessionId), session, session.getTimeout(), TimeUnit.MILLISECONDS);
						return sessionId;
					}

					@Override
					protected Session doReadSession(Serializable sessionId) {
						log.trace("shiro redis session read. sessionId={}", sessionId);
						return (Session) redisTemplate.opsForValue().get(generateSessionKey(sessionId));
					}

					@Override
					public void update(Session session)  {
						log.trace("shiro redis session update. sessionId={}", session.getId());
						redisTemplate.opsForValue().set(generateSessionKey(session.getId()), session, session.getTimeout(), TimeUnit.MILLISECONDS);
					}

					@Override
					public void delete(Session session) {
						log.trace("shiro redis session delete. sessionId={}", session.getId());
						redisTemplate.delete(generateSessionKey(session.getId()));
					}

					@Override
					public Collection<Session> getActiveSessions() {
						Set<Session> sessionSet = new HashSet<>();
						RedisConnection redisConnection = redisTemplate.getConnectionFactory().getConnection();
						Assert.notNull(redisConnection, MESSAGE);
						try (Cursor<byte[]> cursor = redisConnection.scan(ScanOptions.scanOptions()
								.match(generateSessionKey("*"))
								.count(Integer.MAX_VALUE)
								.build())) {
							while (cursor.hasNext()) {
								Session session = (Session) redisTemplate.opsForValue().get(cursor.next());
								sessionSet.add(session);
							}
						} catch (IOException e) {
							log.error("shiro redis session getActiveSessions exception", e);
						}
						return sessionSet;
					}
				}
		);

		return defaultWebSessionManager;
	}

	/**
	 * 获取拦截规则
	 *
	 * @return
	 */
	private Map<String, String> filterChainDefinitionMap() {
		Map<String, String> map = new LinkedHashMap<>();
		map.put("/system/login", "anon"); //登录需要认证才能访问
		map.put("/js/**", "anon"); //静态js资源
		map.put("/css/**", "anon"); //静态css资源
		map.put("/img/**", "anon"); //静态图片资源
		map.put("/plugins/**", "anon"); //静态插件资源
		map.put("/system/logout", "anon"); //登出
		if(isAuthc) {
			map.put("/**", "authc"); //登录需要认证才能访问
		}else{
		map.put("/**", "anon");
		}
		return map;
	}

	/**
	 * 生成 缓存 key
	 *
	 * @param name
	 * @param key
	 * @return
	 */
	private String generateCacheKey(String name, Object key) {
		return SHIRO_REDIS_CACHE_KEY_PREFIX + name + "_" + key;
	}

	/**
	 * 生成 session key
	 *
	 * @param key
	 * @return
	 */
	private String generateSessionKey(Object key) {
		return SHIRO_REDIS_SESSION_KEY_PREFIX + "_" + key;
	}

}