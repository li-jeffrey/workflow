package com.jli.workflow;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.jli.workflow.monitoring.WorkflowEventListener;
import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

@Setter
public class WorkflowConfigurationImpl implements WorkflowConfiguration {

    private static final int DEFAULT_WORKFLOW_THREADS = 4;

    private ExecutorService executorService;
    private int workflowThreads = DEFAULT_WORKFLOW_THREADS;

    @Getter
    private WorkflowEventListener workflowEventListener;

    @Getter
    private File workflowDirectory;

    @Getter
    private boolean autoLoadFromDirectory = true;

    public static WorkflowConfiguration getDefault() {
        WorkflowConfiguration configuration = new WorkflowConfigurationImpl();
        configuration.setWorkflowThreads(WorkflowConfigurationImpl.DEFAULT_WORKFLOW_THREADS);

        ThreadFactory factory = new ThreadFactoryBuilder().setNameFormat("workflow-%d").build();
        configuration.setExecutorService(Executors.newFixedThreadPool(WorkflowConfigurationImpl.DEFAULT_WORKFLOW_THREADS, factory));

        return configuration;
    }

    @Override
    public ExecutorService getExecutorService() {
        if (executorService == null) {
            ThreadFactory factory = new ThreadFactoryBuilder().setNameFormat("workflow-%d").build();
            executorService = Executors.newFixedThreadPool(workflowThreads, factory);
        }

        return executorService;
    }
}
