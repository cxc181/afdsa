package com.yuqian.itax.common.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.serializer.SimpleDateFormatSerializer;
import com.alibaba.fastjson.serializer.ToStringSerializer;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import java.beans.PropertyDescriptor;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.*;

/**
 * @ClassName ObjectUtil
 * @Description 通用工具类
 * @Author jiangni
 * @Date 2019/7/15
 * @Version 1.0
 */
public class ObjectUtil {

    public static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    public static final String DUBBO_EXCEPTION_MESSAGE_REGEX = "[\\s\\S]*:(\r\n)[\\s\\S]*";


    private static final SerializeConfig SERIALIZE_CONFIG = new SerializeConfig();


    static {
        String format = DEFAULT_DATE_FORMAT;
        SERIALIZE_CONFIG.put(Date.class, new SimpleDateFormatSerializer(format));
        SERIALIZE_CONFIG.put(java.sql.Date.class, new SimpleDateFormatSerializer(format));
        SERIALIZE_CONFIG.put(Timestamp.class, new SimpleDateFormatSerializer(format));
        SERIALIZE_CONFIG.put(BigInteger.class, new ToStringSerializer());
        SERIALIZE_CONFIG.put(Long.class, new ToStringSerializer());
    }


    /**
     * 全球统一Id
     *
     * @return
     */
    public static String getUUID() {
        return getUUID(true);
    }

    /**
     * 全球统一Id
     *
     * @return
     */
    public static String getUUID(boolean removeSlash) {
        UUID uuid = UUID.randomUUID();
        String str = uuid.toString();
        if (removeSlash) {
            return str.replaceAll("-", "");
        } else {
            return str;
        }
    }

    /**
     * 获得字符串
     *
     * @param buffer
     * @param charset
     * @return
     */
    public static String getString(byte[] buffer, String charset) {
        if (buffer == null) {
            throw new IllegalArgumentException("buffer is null");
        }
        try {
            return new String(buffer, charset);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * Object转换为格式化JSON字符串
     *
     * @param object
     * @param prettyFormat 是否格式化JSON字符串
     * @return
     */
    public static String json(Object object, boolean prettyFormat) {
        if (prettyFormat) {
            return JSON.toJSONString(object, SERIALIZE_CONFIG, SerializerFeature.PrettyFormat, SerializerFeature.WriteEnumUsingName);
        }
        return JSON.toJSONString(object, SERIALIZE_CONFIG);
    }

    /**
     * 实例化对象
     *
     * @param clazz
     * @return
     */
    public static <T> T newInstanceClass(Class<T> clazz) {
        try {
            return clazz.newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * copy对象属性，包括null值
     *
     * @param dest
     * @param src
     */
    public static void copyObject(Object src, Object dest) {
        try {
            BeanUtils.copyProperties(src, dest);
        } catch (Exception e) {
            throw new RuntimeException("对象参数复制异常", e);
        }
    }

    /**
     * copyList对象，包括null值
     *
     * @param <T>
     * @param srcs
     * @param dests
     * @param clazz 目标类型
     */
    public static <S, T> void copyListObject(List<S> srcs, List<T> dests, Class<T> clazz) {
        try {
            for (S src : srcs) {
                T dest = clazz.newInstance();
                copyObject(src, dest);
                dests.add(dest);
            }
        } catch (Exception e) {
            throw new RuntimeException("对象参数复制异常", e);
        }
    }

    /**
     * copy对象属性，不包括null值
     *
     * @param dest
     * @param src
     */
    public static void copyNotNullObject(Object src, Object dest) {
        if (dest != null && src != null) {
            final BeanWrapper wrapper = new BeanWrapperImpl(src);
            PropertyDescriptor[] pds = wrapper.getPropertyDescriptors();
            Set<String> emptyNames = new HashSet<>();
            for (PropertyDescriptor pd : pds) {
                Object srcValue = wrapper.getPropertyValue(pd.getName());
                if (srcValue == null) {
                    emptyNames.add(pd.getName());
                }
            }
            BeanUtils.copyProperties(src, dest, emptyNames.toArray(new String[]{}));
        }
    }


    /**
     * copy对象属性，不包括null值
     *
     * @param dest
     * @param src
     */
    public static void copyNotBlankObject(Object src, Object dest) {
        if (dest != null && src != null) {
            final BeanWrapper wrapper = new BeanWrapperImpl(src);
            PropertyDescriptor[] pds = wrapper.getPropertyDescriptors();
            Set<String> emptyNames = new HashSet<>();
            for (PropertyDescriptor pd : pds) {
                Object srcValue = wrapper.getPropertyValue(pd.getName());
                if (srcValue == null || ((srcValue instanceof String) && ((String) srcValue).isEmpty())) {
                    emptyNames.add(pd.getName());
                }
            }
            BeanUtils.copyProperties(src, dest, emptyNames.toArray(new String[]{}));
        }
    }

    /**
     * 获得泛型真实类型
     *
     * @param clazz       泛型类子类
     * @param target      泛型变量声明类
     * @param genericName 泛型变量名
     * @return
     */
    public static List<Class<?>> genericClass(Class<?> clazz, Class<?> target, String genericName) {
        Class<?> initClass = clazz;
        List<Class<?>> genericClass = new ArrayList<>();
        do {
            Type type = clazz.getGenericSuperclass();
            if (type != null && ParameterizedType.class.isAssignableFrom(type.getClass())) {
                ParameterizedType parameterizedType = ParameterizedType.class.cast(type);
                clazz = (Class<?>) parameterizedType.getRawType();
                if (target.equals(clazz)) {
                    Type[] types = parameterizedType.getActualTypeArguments();
                    for (Type t : types) {
                        if (TypeVariable.class.isAssignableFrom(t.getClass())) {
                            TypeVariable<?> typeVariable = TypeVariable.class.cast(t);
                            if (typeVariable.getName().equalsIgnoreCase(genericName)) {
                                target = (Class<?>) typeVariable.getGenericDeclaration();
                                clazz = initClass;
                                break;
                            }
                            continue;
                        } else {
                            genericClass.add((Class<?>) t);
                        }
                    }
                }
            } else {
                clazz = null;
            }
        } while (clazz != null);
        return genericClass;
    }

    /**
     * 获得泛型真实类型
     *
     * @param clazz  泛型类子类
     * @param target 泛型变量声明类
     * @return
     */
    public static List<Class<?>> genericClass(Class<?> clazz, Class<?> target) {
        Class<?> initClass = clazz;
        List<Class<?>> genericClass = new ArrayList<Class<?>>();
        do {
            Type type = clazz.getGenericSuperclass();
            if (type != null && ParameterizedType.class.isAssignableFrom(type.getClass())) {
                ParameterizedType parameterizedType = ParameterizedType.class.cast(type);
                clazz = (Class<?>) parameterizedType.getRawType();
                if (target.equals(clazz)) {
                    Type[] types = parameterizedType.getActualTypeArguments();
                    if (TypeVariable.class.isAssignableFrom(types[0].getClass())) {
                        TypeVariable<?> typeVariable = TypeVariable.class.cast(types[0]);
                        target = (Class<?>) typeVariable.getGenericDeclaration();
                        clazz = initClass;
                    } else {
                        for (Type t : types) {
                            genericClass.add((Class<?>) t);
                        }
                    }
                }
            } else {
                clazz = null;
            }
        } while (clazz != null);
        return genericClass;
    }

    /**
     * 获得程序路径
     *
     * @return
     */
    public static String getProgramPath() {
        String path = null;
        try {
            path = new File(".").getCanonicalPath();
        } catch (IOException e) {
            path = System.getProperty("user.dir");
        }
        return path;
    }
}
