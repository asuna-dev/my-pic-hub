package org.zepe.pichub.util;

/**
 * @author zzpus
 * @datetime 2025/4/28 17:46
 * @description
 */

import com.baomidou.mybatisplus.core.toolkit.support.SFunction;

import java.lang.invoke.*;
import java.lang.reflect.Method;

public class LambdaGenerator {

    /**
     * 根据字段名动态生成 Lambda 表达式
     */
    public static <T> SFunction<T, ?> createGetterFunction(Class<T> clazz, String fieldName) {
        try {
            String getterName = "get" + Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);
            Method getter = clazz.getMethod(getterName);

            MethodHandles.Lookup lookup = MethodHandles.lookup();
            MethodHandle handle = lookup.unreflect(getter);

            CallSite site = LambdaMetafactory.altMetafactory(lookup,
                "apply",
                MethodType.methodType(SFunction.class),
                MethodType.methodType(Object.class, Object.class),
                handle,
                MethodType.methodType(getter.getReturnType(), clazz),
                LambdaMetafactory.FLAG_SERIALIZABLE
            );
            return (SFunction<T, ?>)site.getTarget().invoke();
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

}

