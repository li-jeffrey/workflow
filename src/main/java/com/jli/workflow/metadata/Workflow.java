package com.jli.workflow.metadata;

import com.google.common.base.Stopwatch;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import lombok.*;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

@Getter
@RequiredArgsConstructor
public class Workflow {

    @Setter(AccessLevel.PRIVATE)
    private Integer id;
    private final String name;
    private final String description;

    @Setter(AccessLevel.PRIVATE)
    private String runtimeMessage;

    private final List<Task> tasks;
    private final AtomicReference<WorkflowStatus> status = new AtomicReference<>(WorkflowStatus.QUEUED);
    private final Stopwatch stopwatch = Stopwatch.createUnstarted();
    private final Cache<String, Object> cache = CacheBuilder.newBuilder().build();

    @Getter(AccessLevel.NONE)
    private final AtomicInteger taskIndex = new AtomicInteger(0);

    public synchronized void start(int id) {
        if (!status.compareAndSet(WorkflowStatus.QUEUED, WorkflowStatus.RUNNING)) {
            throw new IllegalStateException("Workflow#" + id + " has already started");
        }
        setId(id);
        stopwatch.start();
        executeNext();
    }

    public synchronized void executeNext() {
        tasks.get(taskIndex.getAndIncrement()).execute(this);
    }

    public synchronized void fail(String reason) {
        stop();
        if(!status.compareAndSet(WorkflowStatus.RUNNING, WorkflowStatus.FAILED)) {
            throw new IllegalStateException("Could not fail workflow#" + id);
        }
        setRuntimeMessage(reason);
    }

    public synchronized void terminate(String reason) {
        stop();
        if(!status.compareAndSet(WorkflowStatus.RUNNING, WorkflowStatus.TERMINATED)) {
            throw new IllegalStateException("Could not terminate workflow#" + id);
        }
        setRuntimeMessage(reason);
    }

    public synchronized void cancel(String message) {
        stop();
        if(!status.compareAndSet(WorkflowStatus.RUNNING, WorkflowStatus.CANCELLED)) {
            throw new IllegalStateException("Could not cancel workflow#" + id);
        }
        setRuntimeMessage(message);
    }

    public synchronized void complete() {
        stop();
        if(!status.compareAndSet(WorkflowStatus.RUNNING, WorkflowStatus.COMPLETE)) {
            throw new IllegalStateException("Could not complete workflow#" + id);
        }
    }

    private void stop() {
        stopwatch.stop();
    }

    public void putResult(String key, Object result) {
        cache.put(key, result);
    }

    public void putResults(Map<String, Object> results) {
        cache.putAll(results);
    }

    public Object getResult(String key) {
        return cache.getIfPresent(key);
    }

    public boolean isComplete() {
        return status.get() == WorkflowStatus.RUNNING && taskIndex.get() >= tasks.size();
    }

    public WorkflowStatus getStatus() {
        return status.get();
    }

    public long getElapsed() {
        return stopwatch.elapsed(TimeUnit.SECONDS);
    }

}
