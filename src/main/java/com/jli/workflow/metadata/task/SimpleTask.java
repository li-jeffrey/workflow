package com.jli.workflow.metadata.task;

import com.jli.workflow.components.SimpleTaskRunner;
import com.jli.workflow.components.TaskRunner;
import com.jli.workflow.metadata.Workflow;
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
}
