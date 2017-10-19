package com.liaobushi.query;

/**
 * Created by liaozhongjun on 2017/10/19.
 */

public interface BaasCall<T> {

    void enqueue(Callback<T> callback);

    interface Callback<T> {
        void onResponse(T response);
    }
}
