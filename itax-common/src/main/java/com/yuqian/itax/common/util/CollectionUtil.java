package com.yuqian.itax.common.util;

import java.util.Collection;

/**
 * 集合工具类
 *
 * @date: 2017年7月12日 下午2:48:58 
 * @author LiuXianTing
 */
public class CollectionUtil {
	
	/**
	 * 判断集合是否为非空
	 *
	 * @author LiuXianTing
	 * @param collections
	 * @return
	 */
	public static boolean isNotEmpty(Collection<?> collections) {
		return null != collections &&collections.size()>0;
	}
	
	/**
	 * 判断集合是否为空
	 *
	 * @author LiuXianTing
	 * @param collections
	 * @return
	 */
	public static boolean isEmpty(Collection<?> collections) {
		return !isNotEmpty(collections);
	}
}
