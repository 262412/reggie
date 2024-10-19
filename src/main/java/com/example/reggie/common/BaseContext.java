package com.example.reggie.common;

public class BaseContext {
    // 使用ThreadLocal来存储当前线程的ID，确保每个线程都有自己的ID副本，避免数据共享和同步问题
    private static ThreadLocal<Long> threadLocal = new ThreadLocal<>();

    /**
     * 设置当前线程的ID
     *
     * @param id 需要设置的线程ID
     */
    public static void setCurrentId(Long id) {
        threadLocal.set(id);
    }

    /**
     * 获取当前线程的ID
     *
     * @return 当前线程的ID，如果未设置则返回null
     */
    public static Long getCurrentId() {
        return threadLocal.get();
    }
}
