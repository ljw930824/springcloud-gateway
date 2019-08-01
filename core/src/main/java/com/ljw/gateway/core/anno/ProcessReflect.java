package com.ljw.gateway.core.anno;

import java.lang.reflect.Method;

/**
 * @ClassName: ProcessReflect
 * @Description: TODO
 * @Author: ljw
 * @Date: 2019/7/4 15:44
 **/
public class ProcessReflect {
    /**
     * @Author ljw
     * @Description 反射注解实现
     * @Date 2019/7/4
     * @Param [clazz]
     * @return void
     **/
    public void parseMethod(final Class<?> clazz) throws Exception {
        final Object obj = clazz.getConstructor(new Class[] {}).newInstance(new Object[] {});
        final Method[] methods = clazz.getDeclaredMethods();
        for (final Method method : methods) {
            final Reflect my = method.getAnnotation(Reflect.class);
            if (null != my) {
                method.invoke(obj, my.name());
                method.invoke(obj, my.toString());
            }
        }
    }
}
