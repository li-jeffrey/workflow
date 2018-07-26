package com.jli.workflow;

import com.jli.workflow.execution.TaskAbort;
import com.jli.workflow.metadata.Workflow;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class WorkflowExecutor {

    private final ExecutorService executorService;

    private final AtomicInteger idGenerator = new AtomicInteger(0);

    private static final Map<Integer, Future<?>> RUNNING_TASKS = new ConcurrentHashMap<>();

    private final ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);

    public WorkflowExecutor(ExecutorService executorService) {
        this.executorService = executorService;
    }

    void init() { // start listening for workflow events
        scheduledExecutorService.scheduleWithFixedDelay(this::checkTaskStatuses, 1000, 1000,
                TimeUnit.MILLISECONDS);
    }

    int submit(Workflow workflow) {
        int id = idGenerator.incrementAndGet();
        WorkflowStore.putRuntime(id, workflow);
        RUNNING_TASKS.put(id, executorService.submit(() -> workflow.start(id)));

        return workflow.getId();
    }

    public static void abortTask(TaskAbort abortInfo) {
        cancelTask(abortInfo.getWorkflowId());
        switch (abortInfo.getStatus()) {
            case FAILED:
                handleFailure(abortInfo.getWorkflowId(), abortInfo.getMessage());
                break;
            case TERMINATED:
                handleTermination(abortInfo.getWorkflowId(), abortInfo.getMessage());
                break;
            case CANCELLED:
                handleCancellation(abortInfo.getWorkflowId(), abortInfo.getMessage());
                break;
            default:
                break;
        }
    }

    private static void cancelTask(int workflowId) {
        Future<?> task = RUNNING_TASKS.get(workflowId);
        if (!task.isCancelled()) {
            task.cancel(true);
        }
        RUNNING_TASKS.remove(workflowId);
    }

    private static void handleTermination(int workflowId, String reason) {
        Workflow workflow = WorkflowStore.getRuntime(workflowId);
        workflow.terminate(reason);
    }

    private static void handleFailure(int workflowId, String reason) {
        Workflow workflow = WorkflowStore.getRuntime(workflowId);
        workflow.fail(reason);
    }

    private static void handleCancellation(int workflowId, String reason) {
        Workflow workflow = WorkflowStore.getRuntime(workflowId);
        workflow.cancel(reason);
    }

    private void checkTaskStatuses() {
        RUNNING_TASKS.forEach((id, task) -> {
            if (task.isDone()) {
                Workflow workflow = WorkflowStore.getRuntime(id);
                if(workflow.isComplete()) {
                    workflow.complete();
                    RUNNING_TASKS.remove(id);
                } else {
                    log.info("Workflow#{}: executing next task.", id);
                    RUNNING_TASKS.put(id, executorService.submit(workflow::executeNext));
                }
            }
        });
    }
}
