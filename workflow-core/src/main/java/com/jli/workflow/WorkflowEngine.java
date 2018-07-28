package com.jli.workflow;

import com.google.common.io.PatternFilenameFilter;
import com.jli.workflow.execution.ForkTaskRunner;
import com.jli.workflow.metadata.Workflow;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.Map;
import java.util.concurrent.ExecutorService;

@Slf4j
public class WorkflowEngine {

    @Getter
    private static WorkflowEngine instance;

    private final WorkflowExecutor workflowExecutor;

    private WorkflowEngine(WorkflowExecutor workflowExecutor) {
        this.workflowExecutor = workflowExecutor;
    }

    public static WorkflowEngine start() {
        WorkflowConfiguration configuration = WorkflowConfigurationImpl.getDefault();
        return start(configuration);
    }

    public static synchronized WorkflowEngine start(WorkflowConfiguration configuration) {
        if (instance != null) {
            throw new IllegalStateException("Engine is already started");
        }

        ExecutorService executorService = configuration.getExecutorService();
        WorkflowExecutor workflowExecutor = WorkflowExecutor.init(executorService,
                configuration.getWorkflowEventListener());

        TaskRegistry.setForkTaskRunner(new ForkTaskRunner(executorService));
        instance = new WorkflowEngine(workflowExecutor);

        if (configuration.isAutoLoadFromDirectory()) {
            instance.loadFromDirectory(configuration.getWorkflowDirectory());
        }

        return instance;
    }

    public void loadFromDirectory(File directory) {
        if(! (directory.isDirectory())) {
            throw new IllegalArgumentException("Path does not exist or is not a directory");
        }

        for (File workflowDefinition : directory.listFiles(new PatternFilenameFilter("*.json"))) {
            try {
                loadFromFile(workflowDefinition);
            } catch (Exception e) {
                log.error("Could not load workflow from file {}", workflowDefinition.getName(), e);
            }
        }
    }

    public void loadFromFile(File file) {
        Workflow workflow = WorkflowMapper.mapWorkflow(file);
        WorkflowStore.putTemplate(workflow);
    }

    public int run(String workflowName, Map<String, Object> inputParameters) {
        Workflow runtimeCopy = WorkflowStore.getTemplate(workflowName).getCopy();
        WorkflowInputMapper.mapWorkflow(runtimeCopy, inputParameters);

        return workflowExecutor.submit(runtimeCopy);
    }
}
