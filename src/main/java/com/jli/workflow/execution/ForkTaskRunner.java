package com.jli.workflow.execution;

import com.jli.workflow.WorkflowExecutor;
import com.jli.workflow.metadata.ForkTask;
import com.jli.workflow.metadata.Task;
import com.jli.workflow.metadata.Workflow;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

@Slf4j
public class ForkTaskRunner implements TaskRunner {

    private final ExecutorService executorService;

    public ForkTaskRunner(ExecutorService executorService) {
        this.executorService = executorService;
    }

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

    @Override
    public String getReferenceName() {
        return "";
    }

    private void runAllTasks(List<Task> tasks, Workflow workflow) {
        for (Task task : tasks) {
            task.execute(workflow);
        }
    }

    private void fail(Integer workflowId, Throwable e) {
        WorkflowExecutor.abortTask(new TaskAbort(workflowId, TaskStatus.FAILED, e.getMessage()));
        log.error("Error while executing task ", e);
    }
}
