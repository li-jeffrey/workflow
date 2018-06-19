package com.jli.workflow.metadata.task;

import com.jli.workflow.components.ForkTaskRunner;
import com.jli.workflow.components.TaskRunner;
import com.jli.workflow.metadata.Workflow;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

@Getter
@Setter
public class ForkTask implements Task {

    private String name;
    private String referenceName;
    private final Map<String, Object> inputParameters = new HashMap<>();
    private ForkTaskRunner runner;

    private final Queue<Task> left = new LinkedList<>();
    private final Queue<Task> right = new LinkedList<>();

    @Override
    public void execute(Workflow workflow) {
        runner.run(workflow, this);
    }

    public void setRunner(@NonNull TaskRunner runner) {
        if (!(runner instanceof ForkTaskRunner)){
            throw new UnsupportedOperationException("Runner not instance of SimpleTaskRunner");
        }

        this.runner = (ForkTaskRunner) runner;
    }
}
