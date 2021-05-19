package com.uddernetworks.snapchatauto;

import android.os.Handler;
import android.os.Looper;

import java.util.List;
import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

class UiExecutor {
    private static final ExecutorService INSTANCE =
            new HandlerExecutorService(new Handler(Looper.getMainLooper()));

    static ExecutorService get() {
        return INSTANCE;
    }

    /**
     * A single-threaded {@link ExecutorService} that runs jobs on the thread represented by the
     * given {@link Handler}. If we're on the Handler's thread already, it will run the job
     * immediately. It does not support shutdowns, and will throw if they are invoked.
     */
    private static class HandlerExecutorService extends AbstractExecutorService {
        private final Handler mHandler;

        HandlerExecutorService(Handler handler) {
            mHandler = handler;
        }

        @Override
        public Future<?> submit(Runnable task) {
            return submit(task, null);
        }

        @Override
        public <T> Future<T> submit(Runnable task, T result) {
            return submit(Executors.callable(task, result));
        }

        @Override
        public <T> Future<T> submit(Callable<T> task) {
            FutureTask<T> futureTask = new FutureTask<T>(task);
            if (Looper.myLooper() == mHandler.getLooper()) {
                // Already on the right thread, so execute normally.
                futureTask.run();
            } else {
                mHandler.post(futureTask);
            }
            return futureTask;
        }

        @Override
        public void execute(Runnable runnable) {
            mHandler.post(runnable);
        }

        @Override
        public void shutdown() {
            throw new UnsupportedOperationException();
        }

        @Override
        public List<Runnable> shutdownNow() {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean isShutdown() {
            return false;
        }

        @Override
        public boolean isTerminated() {
            return false;
        }

        @Override
        public boolean awaitTermination(long l, TimeUnit timeUnit) throws InterruptedException {
            throw new UnsupportedOperationException();
        }
    }

    private UiExecutor() {}
}
