package com.dataiku.dip.utils;

import java.util.ArrayList;
import java.util.List;

public class ContextClassLoaderStack {
    private static ThreadLocal<List<ClassLoader>> lstack = new ThreadLocal<List<ClassLoader>>();
    
    public static void pushClassLoader(ClassLoader loader) {
        if (lstack.get() ==  null) lstack.set(new ArrayList<ClassLoader>());
        lstack.get().add(Thread.currentThread().getContextClassLoader());
    }
    public static void popClassLoader() {
        if (lstack.get() ==  null || lstack.get().size() == 0) throw new Error("No class loader stacked");
        ClassLoader prev = lstack.get().remove(lstack.get().size() - 1);
        Thread.currentThread().setContextClassLoader(prev);
    }
}
