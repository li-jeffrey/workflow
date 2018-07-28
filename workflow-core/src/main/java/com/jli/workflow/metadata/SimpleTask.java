package com.jli.workflow.metadata;

import com.jli.workflow.execution.SimpleTaskRunner;
import com.jli.workflow.execution.TaskRunner;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class SimpleTask implements Task {
    private String name;
    private String referenceName;
    private final Map<String, Object> inputParameters = new HashMap<>();

    private SimpleTaskRunner runner;

    @Override
    public void execute(Workflow workflow) {
        runner.run(workflow, this);
    }

    public void setRunner(@NonNull TaskRunner runner) {
        if (!(runner instanceof SimpleTaskRunner)){
            throw new UnsupportedOperationException("Runner not instance of SimpleTaskRunner");
        }

        this.runner = (SimpleTaskRunner) runner;
    }

    @Override
    public Task getCopy() {
        SimpleTask task = new SimpleTask();
        task.setName(name);
        task.setReferenceName(referenceName);
        task.setRunner(runner);
        task.getInputParameters().putAll(inputParameters);

        return task;
    }
}
