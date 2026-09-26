package com.pivothub.common.util;

public class TLUtil {
    public static final ThreadLocal<String> USER_ID = new ThreadLocal<>();

    public static <T> T get(ThreadLocal<T> threadLocal) {
        return threadLocal.get();
    }

    public static <T> void set(ThreadLocal<T> threadLocal, T value) {
        threadLocal.set(value);
    }

    public static <T> void remove(ThreadLocal<T> threadLocal) {
        threadLocal.remove();
    }
}
