package com.xiaojukeji.kafka.manager.common.utils;

import com.xiaojukeji.kafka.manager.common.entity.ao.common.FutureTaskDelayQueueData;
import com.xiaojukeji.kafka.manager.common.utils.factory.DefaultThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

/**
 * Future工具类
 */
public class FutureUtil<T> {
    private static final Logger LOGGER = LoggerFactory.getLogger(FutureUtil.class);

    private ThreadPoolExecutor executor;

    private Map<Long/*currentThreadId*/, DelayQueue<FutureTaskDelayQueueData<T>>> futuresMap;

    private FutureUtil() {
    }

    public static <T> FutureUtil<T> init(String name, int corePoolSize, int maxPoolSize, int queueSize) {
        FutureUtil<T> futureUtil = new FutureUtil<>();

        futureUtil.executor = new ThreadPoolExecutor(
                corePoolSize,
                maxPoolSize,
                3000,
                TimeUnit.MILLISECONDS,
                new LinkedBlockingDeque<>(queueSize),
                new DefaultThreadFactory("KM-FutureUtil-" + name),
                new ThreadPoolExecutor.DiscardOldestPolicy() //对拒绝任务不抛弃，而是抛弃队列里面等待最久的一个线程，然后把拒绝任务加到队列。
        );

        futureUtil.futuresMap = new ConcurrentHashMap<>();
        return futureUtil;
    }

    public Future<T> directSubmitTask(Callable<T> callable) {
        return executor.submit(callable);
    }

    public Future<T> directSubmitTask(Runnable runnable) {
        return (Future<T>) executor.submit(runnable);
    }

    /**
     * 必须配合 waitExecute使用 否则容易会撑爆内存
     */
    public FutureUtil<T> runnableTask(String taskName, Integer timeoutUnisMs, Callable<T> callable) {
        Long currentThreadId = Thread.currentThread().getId();

        futuresMap.putIfAbsent(currentThreadId, new DelayQueue<>());

        DelayQueue<FutureTaskDelayQueueData<T>> delayQueueData = futuresMap.get(currentThreadId);

        delayQueueData.put(new FutureTaskDelayQueueData<>(taskName, executor.submit(callable), timeoutUnisMs + System.currentTimeMillis()));

        return this;
    }

    public FutureUtil<T> runnableTask(String taskName, Integer timeoutUnisMs, Runnable runnable) {
        Long currentThreadId = Thread.currentThread().getId();

        futuresMap.putIfAbsent(currentThreadId, new DelayQueue<>());

        DelayQueue<FutureTaskDelayQueueData<T>> delayQueueData = futuresMap.get(currentThreadId);

        delayQueueData.put(new FutureTaskDelayQueueData<T>(taskName, (Future<T>) executor.submit(runnable), timeoutUnisMs + System.currentTimeMillis()));

        return this;
    }

    public void waitExecute() {
        this.waitResult();
    }

    public void waitExecute(Integer stepWaitTimeUnitMs) {
        this.waitResult(stepWaitTimeUnitMs);
    }

    public List<T> waitResult() {
        return waitResult(null);
    }

    /**
     * 等待结果
     * @param stepWaitTimeUnitMs 超时时间达到后，没有完成时，继续等待的时间
     */
    public List<T> waitResult(Integer stepWaitTimeUnitMs) {
        Long currentThreadId = Thread.currentThread().getId();

        DelayQueue<FutureTaskDelayQueueData<T>> delayQueueData = futuresMap.remove(currentThreadId);
        if(delayQueueData == null || delayQueueData.isEmpty()) {
            return new ArrayList<>();
        }

        List<T> resultList = new ArrayList<>();
        while (!delayQueueData.isEmpty()) {
            try {
                // 不进行阻塞，直接获取第一个任务
                FutureTaskDelayQueueData<T> queueData = delayQueueData.peek();
                if (queueData.getFutureTask().isDone()) {
                    // 如果第一个已经完成了，则移除掉第一个，然后获取其result
                    delayQueueData.remove(queueData);
                    resultList.add(queueData.getFutureTask().get());
                    continue;
                }

                // 如果第一个未完成，则阻塞10ms，判断是否达到超时时间了。
                // 这里的10ms不建议设置较大，因为任务可能在这段时间内完成了，此时如果设置的较大，会导致迟迟不能返回，从而影响接口调用的性能
                queueData = delayQueueData.poll(10, TimeUnit.MILLISECONDS);
                if (queueData == null) {
                    continue;
                }

                // 在到达超时时间后，任务没有完成，但是没有完成的原因可能是因为任务一直处于等待状态导致的。
                // 因此这里再给一段补充时间，看这段时间内是否可以完成任务。
                stepWaitResult(queueData, stepWaitTimeUnitMs);

                // 达到超时时间
                if (queueData.getFutureTask().isDone()) {
                    // 任务已经完成
                    resultList.add(queueData.getFutureTask().get());
                    continue;
                }

                // 达到超时时间，但是任务未完成，则打印日志并强制取消
                LOGGER.error("class=FutureUtil||method=waitExecute||taskName={}||msg=cancel task", queueData.getTaskName());

                queueData.getFutureTask().cancel(true);
            } catch (Exception e) {
                LOGGER.error("class=FutureUtil||method=waitExecute||msg=exception", e);
            }
        }

        return resultList;
    }

    private T stepWaitResult(FutureTaskDelayQueueData<T> queueData, Integer stepWaitTimeUnitMs) {
        if (stepWaitTimeUnitMs == null) {
            return null;
        }

        try {
            return queueData.getFutureTask().get(stepWaitTimeUnitMs, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            // 达到超时时间，但是任务未完成，则打印日志并强制取消
            LOGGER.error("class=FutureUtil||method=stepWaitResult||taskName={}||errMsg=exception", queueData.getTaskName(), e);
        }

        return null;
    }
}
