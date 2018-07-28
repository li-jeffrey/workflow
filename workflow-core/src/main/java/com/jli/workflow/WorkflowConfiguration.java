package com.jli.workflow;

import com.jli.workflow.monitoring.WorkflowEventListener;

import java.util.concurrent.ExecutorService;

public interface WorkflowConfiguration {

    ExecutorService getExecutorService();

    WorkflowEventListener getWorkflowEventListener();

    java.io.File getWorkflowDirectory();

    boolean isAutoLoadFromDirectory();

    void setExecutorService(ExecutorService executorService);

    void setWorkflowThreads(int workflowThreads);

    void setWorkflowDirectory(java.io.File workflowDirectory);

    void setAutoLoadFromDirectory(boolean autoLoadFromDirectory);

    void setWorkflowEventListener(WorkflowEventListener workflowEventListener);
}
