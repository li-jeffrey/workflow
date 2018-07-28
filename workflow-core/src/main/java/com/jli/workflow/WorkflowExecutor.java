package com.jli.workflow;

import com.jli.workflow.execution.TaskAbort;
import com.jli.workflow.metadata.Workflow;
import com.jli.workflow.monitoring.WorkflowEventListener;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class WorkflowExecutor {

    @Getter
    private static WorkflowExecutor instance;

    private final ExecutorService executorService;

    private final WorkflowEventListener workflowEventListener;

    private final AtomicInteger idGenerator = new AtomicInteger(0);

    private final Map<Integer, Future<?>> runningTasks = new ConcurrentHashMap<>();

    private final ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);

    static WorkflowExecutor init(ExecutorService executorService, WorkflowEventListener workflowEventListener) {
        instance = new WorkflowExecutor(executorService, workflowEventListener);
        instance.startListening();

        return instance;
    }

    private void startListening() {  // start listening for workflow events
        scheduledExecutorService.scheduleWithFixedDelay(this::checkTaskStatuses, 1000, 1000,
                TimeUnit.MILLISECONDS);
    }

    int submit(Workflow workflow) {
        int id = idGenerator.incrementAndGet();
        WorkflowStore.putRuntime(id, workflow);
        runningTasks.put(id, executorService.submit(() -> workflow.start(id)));

        return workflow.getId();
    }

    public void abortTask(TaskAbort abortInfo) {
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

    private void cancelTask(int workflowId) {
        Future<?> task = runningTasks.get(workflowId);
        if (!task.isCancelled()) {
            task.cancel(true);
        }
        runningTasks.remove(workflowId);
    }

    private void handleTermination(int workflowId, String reason) {
        Workflow workflow = WorkflowStore.getRuntime(workflowId);
        workflow.terminate(reason);
        workflowEventListener.onTermination(workflow);
    }

    private void handleFailure(int workflowId, String reason) {
        Workflow workflow = WorkflowStore.getRuntime(workflowId);
        workflow.fail(reason);
        workflowEventListener.onFailure(workflow);
    }

    private void handleCancellation(int workflowId, String reason) {
        Workflow workflow = WorkflowStore.getRuntime(workflowId);
        workflow.cancel(reason);
        workflowEventListener.onCancellation(workflow);
    }

    private void checkTaskStatuses() {
        runningTasks.forEach((id, task) -> {
            if (task.isDone()) {
                Workflow workflow = WorkflowStore.getRuntime(id);
                if(workflow.isComplete()) {
                    workflow.complete();
                    runningTasks.remove(id);
                    workflowEventListener.onCompletion(workflow);
                } else {
                    log.info("Workflow#{}: executing next task.", id);
                    runningTasks.put(id, executorService.submit(workflow::executeNext));
                }
            }
        });
    }
}
