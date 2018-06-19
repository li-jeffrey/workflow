package com.jli.workflow.components;

import com.jli.workflow.execution.TaskResult;
import com.jli.workflow.execution.TaskStatus;
import com.jli.workflow.metadata.Workflow;
import com.jli.workflow.metadata.task.Task;
import lombok.extern.slf4j.Slf4j;
import org.greenrobot.eventbus.EventBus;
import org.slf4j.helpers.MessageFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public abstract class SimpleTaskRunner implements TaskRunner {

    @Autowired
    private EventBus eventBus;

    @Override
    public void run(Workflow workflow, Task task) {
        try {
            runInternal(workflow, task);
        } catch (Exception e) {
            fail(workflow.getId(), e);
        }
    }

    protected abstract void runInternal(Workflow workflow, Task task);

    protected void fail(Integer workflowId, Throwable e) {
        eventBus.post(new TaskResult(workflowId, TaskStatus.FAILED, e.getMessage()));
        log.error("Error while executing task ", e);
    }

    protected void terminate(Integer workflowId, String message) {
        eventBus.post(new TaskResult(workflowId, TaskStatus.TERMINATED, message));
        log.info("Workflow terminated: {}", message);
    }

    protected void terminate(Integer workflowId, String message, Object... args) {
        String builtMsg = MessageFormatter.arrayFormat(message, args).getMessage();
        eventBus.post(new TaskResult(workflowId, TaskStatus.TERMINATED, builtMsg));
        log.info("Workflow terminated: {}", builtMsg);
    }
}
