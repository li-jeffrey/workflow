package com.jli.workflow.spring;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.jli.workflow.WorkflowConfiguration;
import com.jli.workflow.monitoring.WorkflowEventListener;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;


@Configuration
@ConfigurationProperties(prefix = "workflow")
public class WorkflowProperties implements WorkflowConfiguration {

    private int threads;

    private String directory;

    private boolean autoLoad;

    @Getter
    @Setter
    private WorkflowEventListener workflowEventListener;


    @Override
    public ExecutorService getExecutorService() {
        ThreadFactory factory = new ThreadFactoryBuilder().setNameFormat("workflow-%d").build();
        return Executors.newFixedThreadPool(threads, factory);
    }

    @Override
    public File getWorkflowDirectory() {
        return new File(directory);
    }

    @Override
    public boolean isAutoLoadFromDirectory() {
        return autoLoad;
    }

    @Override
    public void setExecutorService(ExecutorService executorService) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setWorkflowThreads(int workflowThreads) {
        this.threads = workflowThreads;
    }

    @Override
    public void setWorkflowDirectory(File workflowDirectory) {
        this.directory = workflowDirectory.getPath();
    }

    @Override
    public void setAutoLoadFromDirectory(boolean autoLoadFromDirectory) {
        this.autoLoad = autoLoadFromDirectory;
    }
}
