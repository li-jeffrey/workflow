package com.jli.workflow.execution;

import com.jli.workflow.annotation.RegisterToEventBus;
import lombok.extern.slf4j.Slf4j;
import org.greenrobot.eventbus.Subscribe;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;

@Component
@Slf4j
@RegisterToEventBus
public class WorkflowEventListener {

    private final WorkflowExecutor workflowExecutor;

    private static final Map<Integer, Future<?>> runningTasks = new ConcurrentHashMap<>();

    public static void register(int id, Future<?> task) {
        runningTasks.put(id, task);
    }

    @Autowired
    public WorkflowEventListener(WorkflowExecutor workflowExecutor) {
        this.workflowExecutor = workflowExecutor;
    }

    @Subscribe
    public void onTaskUpdate(TaskResult result) {
        Future<?> task = runningTasks.get(result.getWorkflowId());
        switch (result.getStatus()) {
            case FAILED:
                if (!task.isCancelled()) {
                    task.cancel(true);
                }
                runningTasks.remove(result.getWorkflowId());
                log.error("Workflow failed with message: {}", result.getMessage());
                workflowExecutor.terminate(result.getWorkflowId());
                break;
            case TERMINATED:
                if (!task.isCancelled()) {
                    task.cancel(true);
                }
                runningTasks.remove(result.getWorkflowId());
                log.info("Workflow terminated with message: {}", result.getMessage());
                workflowExecutor.fail(result.getWorkflowId());
                break;
        }
    }

    @Scheduled(fixedDelay = 1000)
    public void checkTaskStatuses() {
        runningTasks.forEach((id, task) -> {
            if (task.isDone()) {
                Future<?> next = workflowExecutor.execute(id);
                if (next != null) {
                    runningTasks.put(id, next);
                } else {
                    runningTasks.remove(id);
                }
            }
        });
    }
}
