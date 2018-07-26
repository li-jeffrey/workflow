package com.jli.workflow;

import com.jli.workflow.execution.ForkTaskRunner;
import com.jli.workflow.execution.TaskRunner;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TaskRegistry {

    @Getter
    @Setter
    private static ForkTaskRunner forkTaskRunner;

    private static final Map<String, TaskRunner> REGISTRY = new ConcurrentHashMap<>();

    public static void register(TaskRunner taskRunner) {
        REGISTRY.put(taskRunner.getReferenceName(), taskRunner);
    }

    public static TaskRunner getRunner(String referenceName) {
        return REGISTRY.get(referenceName);
    }
}
