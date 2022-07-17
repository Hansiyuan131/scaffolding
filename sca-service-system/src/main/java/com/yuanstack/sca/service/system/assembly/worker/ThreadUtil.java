package com.yuanstack.sca.service.system.assembly.worker;

import java.util.Random;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @description: 线程工具类
 * @author: hansiyuan
 * @date: 2022/6/29 11:35 AM
 */
public class ThreadUtil {

    private static volatile ExecutorService executor;

    volatile static BlockingQueue<Future<?>> queue;
    /**
     * 实例化CompletionService
     */
    volatile static CompletionService<?> completionService;


    static {
        initExecutor();
        queue = new LinkedBlockingDeque<Future<?>>(10);
        completionService = new ExecutorCompletionService(executor, queue);
        Runtime.getRuntime().addShutdownHook(new Thread(ThreadUtil::shutdown, "Thread-ShutdownHook"));
    }

    /**
     * 初始化线程池
     */
    private static void initExecutor() {
        if (executor == null) {
            synchronized (ThreadUtil.class) {
                if (executor == null) {
                    executor = new ThreadPoolExecutor(
                            Runtime.getRuntime().availableProcessors() * 3,
                            200, 1,
                            TimeUnit.MINUTES, new ArrayBlockingQueue<Runnable>(10000), new DefaultThreadFactory());
                }
            }
        }
    }

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        // 提交10个任务
        for (int i = 0; i < 10; i++) {
            int finalI = i;
            ThreadUtil.queue.put(ThreadUtil.submit(new Callable<String>() {
                @Override
                public String call() throws InterruptedException {
                    long startTime = System.currentTimeMillis();
                    int sleepTime = new Random().nextInt(1000);
                    System.out.println("Thread" + Thread.currentThread().getName());
                    if ("pool-1-thread-2".equals(Thread.currentThread().getName())) {
                        // TimeUnit.SECONDS.sleep(10);
                        throw new RuntimeException("草世家");
                    }
                    Thread.sleep(sleepTime);
                    long endTime = System.currentTimeMillis() - startTime;
                    System.out.println("Thread" + "线程" + finalI + "睡了" + endTime + "毫秒" + Thread.currentThread().getName());
                    return "线程" + finalI + "睡了" + endTime + "毫秒";
                }
            }));
        }


        // 输出结果
        for (int i = 0; i < 10; i++) {
            // 获取包含返回结果的future对象（若整个阻塞队列中还没有一条线程返回结果，
            // 那么调用take将会被阻塞，当然你可以调用poll，不会被阻塞，若没有结果会返回null，
            // poll和take返回正确的结果后会将该结果从队列中删除）
            Future<?> future = ThreadUtil.completionService.poll();
            // 从future中取出执行结果，这里存储的future已经拥有执行结果，get不会被阻塞
            Object result = future.get();
            System.out.println(result);
        }
        ThreadUtil.shutdown();

    }

    /**
     * 线程工厂
     */
    static class DefaultThreadFactory implements ThreadFactory {
        private static final AtomicInteger poolNumber = new AtomicInteger(1);
        private final AtomicInteger threadNumber = new AtomicInteger(1);
        private final ThreadGroup threadGroup;
        private final String namePrefix;

        public DefaultThreadFactory() {
            SecurityManager sm = System.getSecurityManager();
            this.threadGroup = sm != null ? sm.getThreadGroup() : Thread.currentThread().getThreadGroup();
            this.namePrefix = "pool-" + DefaultThreadFactory.poolNumber.getAndIncrement() + "-thread-";
        }

        @Override
        public Thread newThread(Runnable r) {
            Thread t = new Thread(this.threadGroup, r, this.namePrefix + this.threadNumber.getAndIncrement(), 0);
            if (t.isDaemon()) {
                t.setDaemon(false);
            }
            if (t.getPriority() != Thread.NORM_PRIORITY) {
                t.setPriority(Thread.NORM_PRIORITY);
            }
            return t;
        }
    }

    /**
     * 异步执行一个任务并返回结果
     *
     * @param task 任务
     * @return 任务结果
     */
    public static <V> Future<V> submit(Callable<V> task) {
        return executor.submit(task);
    }

    /**
     * 异步执行，超时会停止任务
     *
     * @param task
     * @param timeout 超时时间为秒
     * @return
     */
    public static <T> T submit(Callable<T> task, int timeout) {
        Future<T> future = executor.submit(task);
        T t;
        try {
            t = future.get(timeout, TimeUnit.SECONDS);
            return t;
        } catch (Exception e) {
            e.printStackTrace();
            future.cancel(true);
            task = null;
            t = null;
        }
        return null;
    }

    /**
     * 异步执行一个任务并返回结果
     *
     * @param task 任务
     */
    public static Future<?> submit(Runnable task) {
        return executor.submit(task);
    }

    /**
     * 异步执行任务没有返回结果
     *
     * @param task
     */
    public static void execute(Runnable task) {
        executor.execute(task);
    }

    /**
     * 关闭服务
     */
    public static void shutdown() {
        if (null != executor) {
            executor.shutdown();
        }
    }

}

