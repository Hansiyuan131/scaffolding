package com.yuanstack.sca.service.system.assembly.worker;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * @description: CompletableFutureScheduleUtils
 * @author: hansiyuan
 * @date: 2022/6/29 11:59 AM
 */
public class CompletableFutureScheduleUtils {

    public static ScheduledExecutorService singleThreadExecutor = Executors.newSingleThreadScheduledExecutor();

    public static <T> CompletableFuture<T> schedule(
            ScheduledExecutorService executor,
            Supplier<T> task,
            long delay,
            TimeUnit unit) {
        CompletableFuture<T> completableFuture = new CompletableFuture<>();
        executor.schedule(
                () -> {
                    try {
                        return completableFuture.complete(task.get());
                    } catch (Throwable t) {
                        return completableFuture.completeExceptionally(t);
                    }
                },
                delay,
                unit
        );
        return completableFuture;
    }

    public static <T> CompletableFuture<T> submit(
            ScheduledExecutorService executor,
            Supplier<T> task) {

        CompletableFuture<T> completableFuture = new CompletableFuture<>();
        executor.submit(
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
}

