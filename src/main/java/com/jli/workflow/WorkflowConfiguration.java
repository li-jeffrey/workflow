package com.jli.workflow;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

@Setter
public class WorkflowConfiguration {

    private static final int DEFAULT_WORKFLOW_THREADS = 4;

    private ExecutorService executorService;
    private int workflowThreads = DEFAULT_WORKFLOW_THREADS;

    @Getter
    private File workflowDirectory;
    private boolean autoLoadFromDirectory;

    public static WorkflowConfiguration getDefault() {
        WorkflowConfiguration configuration = new WorkflowConfiguration();
        configuration.setWorkflowThreads(DEFAULT_WORKFLOW_THREADS);

        ThreadFactory factory = new ThreadFactoryBuilder().setNameFormat("workflow-%d").build();
        configuration.setExecutorService(Executors.newFixedThreadPool(DEFAULT_WORKFLOW_THREADS, factory));

        return configuration;
    }

    public ExecutorService getExecutorService() {
        if (executorService == null) {
            ThreadFactory factory = new ThreadFactoryBuilder().setNameFormat("workflow-%d").build();
            executorService = Executors.newFixedThreadPool(workflowThreads, factory);
        }

        return executorService;
    }
}
