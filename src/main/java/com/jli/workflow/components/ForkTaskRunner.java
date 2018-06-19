package com.jli.workflow.components;

import com.jli.workflow.execution.TaskResult;
import com.jli.workflow.execution.TaskStatus;
import com.jli.workflow.metadata.Workflow;
import com.jli.workflow.metadata.task.ForkTask;
import com.jli.workflow.metadata.task.Task;
import lombok.extern.slf4j.Slf4j;
import org.greenrobot.eventbus.EventBus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

@Component
@Slf4j
public class ForkTaskRunner implements TaskRunner {

    @Autowired
    private ExecutorService executorService;

    @Autowired
    private EventBus eventBus;

    @Override
    public void run(Workflow workflow, Task task) {
        try {
            ForkTask forkTask = (ForkTask) task;
            CompletableFuture<Void> leftTask = CompletableFuture.runAsync(
                    () -> runAllTasks(forkTask.getLeft(), workflow), executorService);

            CompletableFuture<Void> rightTask = CompletableFuture.runAsync(
                    () -> runAllTasks(forkTask.getRight(), workflow), executorService);

            CompletableFuture.allOf(leftTask, rightTask).get();
        } catch (Exception e) {
            fail(workflow.getId(), e);
        }
    }

    private void runAllTasks(Queue<Task> tasks, Workflow workflow) {
        while(!tasks.isEmpty()) {
            tasks.remove().execute(workflow);
        }
    }

    private void fail(Integer workflowId, Throwable e) {
        eventBus.post(new TaskResult(workflowId, TaskStatus.FAILED, e.getMessage()));
        log.error("Error while executing task ", e);
    }
}
