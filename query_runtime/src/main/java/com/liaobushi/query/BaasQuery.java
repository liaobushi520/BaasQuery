package com.liaobushi.query;

/**
 * Created by liaozhongjun on 2017/10/13.
 */

public class BaasQuery {

    public static <T> T query(Class<T> cl) {
        try {
            return (T) Class.forName(cl.getName() + "Imp").newInstance();
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
