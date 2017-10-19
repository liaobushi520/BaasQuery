package com.liaobushi.query;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import jdk.nashorn.internal.codegen.CompilerConstants;

/**
 * Created by liaozhongjun on 2017/10/19.
 */

public class DefaultBaasCall<T> implements BaasCall<T> {

    private Callable<T> executableTask;

    private static final ExecutorService DEFAULT_QUERY_THREAD_POOL = Executors.newCachedThreadPool();

    public DefaultBaasCall(Callable<T> callable) {
        executableTask = callable;
    }

    @Override
    public void enqueue(final Callback<T> callback) {
        DEFAULT_QUERY_THREAD_POOL.submit(new WrapperRunnable<T>(executableTask, callback));
    }


    private static class WrapperRunnable<T> implements Runnable {
        private Callback<T> mCallback;
        private Callable<T> mCallable;

        public WrapperRunnable(Callable<T> callable, Callback<T> callback) {
            mCallable = callable;
            mCallback = callback;
        }

        @Override
        public void run() {
            T result = null;
            try {
                result = mCallable.call();
            } catch (Exception e) {
                e.printStackTrace();
            }
            mCallback.onResponse(result);
        }
    }
}
