package com.yuqian.itax.common.redis;

import java.util.Set;

/**
 * redis服务接口
 * 
 * @author 刘献廷
 */
public interface RedisService {
	
	/**
	 * 设置单个对象信息
	 *
	 * @author LiuXianTing
	 * @param key
	 * @param value
	 */
	public void set(String key, Object value);  
	
	/**
	 * 设置map
	 *
	 * @author LiuXianTing
	 * @param key
	 * @param value
	 */
	public void setObject(String key, Object value);  
	
	/**
	 * 设置单个对象信息
	 * 有超时时间
	 *
	 * @author LiuXianTing
	 * @param key
	 * @param value
	 * @param timeout 0=不超时
	 */
	public void set(String key, Object value, int timeout);
    
	/**
	 * 根据key获取对象信息
	 *
	 * @author LiuXianTing
	 * @param key
	 * @return
	 */
    public String get(String key);
    
    /**
	 * 根据key获取对象信息
	 *
	 * @author LiuXianTing
	 * @param key
	 * @return
	 */
    public Object getObject(String key);
    
    /**
     * 根据key删除数据信息
     *
     * @author LiuXianTing
     * @param key
     */
    public void delete(String key);
    
    /**
     * 加锁
     * @param key id
     * @param value 当前时间+超时时间
     */    
    public boolean lock(String key, String value, int timeOut);

    //解锁
    public void unlock(String key, String value);

	void setObject(String key, Object value, int timeOut);

	/**
	 * redis计数
	 * @param key redis的键
	 * @param delta 每次增加量，如果为3，则每次增加3
	 * @return 返回增加后的结果
	 */
	public Long increment(String key, long delta);

	/**
	 * redis计数
	 * @param key redis的键
	 * @param delta 每次增加量，如果为3，则每次增加3
	 * @param timeOut 超时时间
	 * @param reSetTime 是否重置超时时间，true重置
	 * @return 返回增加后的结果
	 */
	public Long increment(String key, long delta, int timeOut, boolean reSetTime);

	/**
	 * redis根据正则表达式模糊匹配key
	 * @param pattern
	 * @return
	 */
	public Set<String> keys(String pattern);
	
	/**
	 * redis根据正则表达式模糊匹配key，删除原来所有key，并设置新的key
	 * @param pattern
	 * @param key
	 * @param value
	 * @param timeout
	 */
	public void deleteAllAndSet(String pattern, String key, Object value, int timeout);
}
