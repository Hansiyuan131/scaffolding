package com.yuanstack.sca.service.system.assembly.worker;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

/**
 * @description: CompletableFutureUtils
 * @author: hansiyuan
 * @date: 2022/6/29 12:00 PM
 */
public class CompletableFutureUtils {

    public static volatile ThreadPoolExecutor executorService;

    static {
        initExecutor();
        Runtime.getRuntime().addShutdownHook(new Thread(CompletableFutureUtils::shutdown, "CompletableFuture-ShutdownHook"));
    }

    /**
     * 初始化线程池
     */
    private static void initExecutor() {
        if (executorService == null) {
            synchronized (CompletableFutureUtils.class) {
                if (executorService == null) {
                    executorService = new ThreadPoolExecutor(
                            Runtime.getRuntime().availableProcessors() * 3,
                            200, 1,
                            TimeUnit.MINUTES,
                            new ArrayBlockingQueue<Runnable>(10000),
                            new CompletableFutureUtils.NamedThreadFactory("GetVenueInfo", true));
                }
            }
        }
    }

    /**
     * 线程工厂
     */
    public static class NamedThreadFactory implements ThreadFactory {
        //前缀
        protected static final AtomicInteger POOL_SEQ = new AtomicInteger(1);

        protected final AtomicInteger mThreadNum = new AtomicInteger(1);
        /**
         * 线程前缀递增计数器
         */
        protected final String mPrefix;
        /**
         * 线程前缀
         */
        protected final boolean mDaemon;
        /**
         * 是否是精灵线程（守护线程）
         */
        protected final ThreadGroup mGroup;

        public NamedThreadFactory() {
            this("pool-" + POOL_SEQ.getAndIncrement(), false);
        }

        public NamedThreadFactory(String prefix) {
            this(prefix, false);
        }

        public NamedThreadFactory(String prefix, boolean daemon) {
            mPrefix = prefix + "-thread-";
            mDaemon = daemon;
            SecurityManager s = System.getSecurityManager();
            //安全管理器的应用场合，一般用在测试未知的且认为有恶意的程序
            /*默认的安全管理器配置文件是 $JAVA_HOME/jre/lib/security/java.policy，即当未指定配置文件时，将会使用该配置*/
            mGroup = (s == null) ? Thread.currentThread().getThreadGroup() : s.getThreadGroup();
        }

        @Override
        public Thread newThread(Runnable runnable) {
            String name = mPrefix + mThreadNum.getAndIncrement();
            Thread ret = new Thread(mGroup, runnable, name, 0);
            ret.setDaemon(mDaemon);
            /**是否是精灵线程*/
            return ret;
        }

        public ThreadGroup getThreadGroup() {
            return mGroup;
        }
    }

    /**
     * 异步执行一个任务并返回结果
     *
     * @param task 任务
     * @return 任务结果
     */
    public static <T> CompletableFuture<T> submit(
            ExecutorService executorService,
            Supplier<T> task) {

        CompletableFuture<T> completableFuture = new CompletableFuture<>();
        executorService.submit(
                () -> {
                    try {
                        return completableFuture.complete(task.get());
                    } catch (Throwable t) {
                        return completableFuture.completeExceptionally(t);
                    }
                }
        );
        return completableFuture;
    }

    /**
     * 异步执行，超时会停止任务
     *
     * @param task
     * @param timeout 超时时间为秒
     * @return
     */
    public static <T> T submit(ExecutorService executorService, Supplier<T> task, int timeout) {
        CompletableFuture<T> completableFuture = new CompletableFuture<>();
        Supplier<T> finalTask = task;
        executorService.submit(
                () -> {
                    try {
                        return completableFuture.complete(finalTask.get());
                    } catch (Throwable t) {
                        return completableFuture.completeExceptionally(t);
                    }
                }
        );
        T t;
        try {
            t = completableFuture.get(timeout, TimeUnit.SECONDS);
            return t;
        } catch (Exception e) {
            e.printStackTrace();
            completableFuture.cancel(true);
            task = null;
            t = null;
        }
        return null;
    }

    /**
     * 关闭服务
     */
    public static void shutdown() {
        if (null != executorService) {
            executorService.shutdown();
        }
    }

    /**
     * 异步执行任务没有返回结果
     */
    public static void execute(Runnable task) {
        executorService.execute(task);
    }

    /**
     * 异步执行一个任务并返回结果
     */
    public static Future<?> submit(Runnable task) {
        return executorService.submit(task);
    }

}

