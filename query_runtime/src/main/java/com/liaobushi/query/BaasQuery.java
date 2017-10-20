package com.liaobushi.query;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by liaozhongjun on 2017/10/13.
 */

public class BaasQuery<T> {

    private static Map<Class<?>,Object> sInstanceCache;

    public static <T> T query(Class<T> cl) {

        if (sInstanceCache == null) {
            sInstanceCache = new HashMap<>();
        }
        T instance = (T) sInstanceCache.get(cl);
        if (instance != null) {
            return instance;
        }
        try {
            instance = (T) Class.forName(cl.getName() + "Imp").newInstance();
            sInstanceCache.put(cl, instance);
            return instance;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }
}
