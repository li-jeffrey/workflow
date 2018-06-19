package com.jli.workflow.execution;

import com.jli.workflow.metadata.Workflow;

import java.util.concurrent.Future;

public interface WorkflowExecutor {
    int start(Workflow workflow);

    Future<?> execute(int workflowId);

    void terminate(int workflowId);

    void fail(int workflowId);
}
