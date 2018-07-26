package com.jli.workflow.execution;

import com.jli.workflow.WorkflowExecutor;
import com.jli.workflow.metadata.Task;
import com.jli.workflow.metadata.Workflow;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.helpers.MessageFormatter;

@Slf4j
public abstract class SimpleTaskRunner implements TaskRunner {

    @Override
    public void run(Workflow workflow, Task task) {
        try {
            runInternal(workflow, task);
        } catch (Exception e) {
            fail(workflow.getId(), e);
        }
    }

    @Override
    public abstract String getReferenceName();

    protected abstract void runInternal(Workflow workflow, Task task);

    private void fail(Integer workflowId, Throwable e) {
        WorkflowExecutor.abortTask(new TaskAbort(workflowId, TaskStatus.FAILED, e.getMessage()));
        log.error("Error while executing task ", e);
    }

    protected void terminate(Integer workflowId, String message) {
        WorkflowExecutor.abortTask(new TaskAbort(workflowId, TaskStatus.TERMINATED, message));
        log.info("Workflow terminated: {}", message);
    }

    protected void terminate(Integer workflowId, String message, Object... args) {
        String builtMsg = MessageFormatter.arrayFormat(message, args).getMessage();
        WorkflowExecutor.abortTask(new TaskAbort(workflowId, TaskStatus.TERMINATED, builtMsg));
        log.info("Workflow terminated: {}", builtMsg);
    }
}
