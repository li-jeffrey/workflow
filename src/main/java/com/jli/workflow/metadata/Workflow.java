package com.jli.workflow.metadata;

import com.jli.workflow.execution.WorkflowStatus;
import com.jli.workflow.metadata.task.Task;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;

@Getter
@Setter
public class Workflow {
    private Integer id;
    private String name;
    private String description;
    private Queue<Task> tasks;
    private WorkflowStatus status = WorkflowStatus.QUEUED;
    private long startTime;
    private long endTime;

    private final Map<String, Object> computationResults = new ConcurrentHashMap<>();

    public void execute() {
        tasks.remove().execute(this);
    }

    public void addComputationResult(String key, Object result) {
        computationResults.put(key, result);
    }

    public void addComputationResult(Map<String, Object> result) {
        computationResults.putAll(result);
    }

    public boolean isComplete() {
        return tasks.size() == 0;
    }

    public long getElapsed() {
        return endTime == 0 ? Instant.now().toEpochMilli() - startTime : endTime - startTime;
    }
}
