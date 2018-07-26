package com.jli.workflow.metadata;

import com.jli.workflow.execution.ForkTaskRunner;
import com.jli.workflow.execution.TaskRunner;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class ForkTask implements Task {

    private String name;
    private String referenceName;
    private final Map<String, Object> inputParameters = new HashMap<>();
    private ForkTaskRunner runner;

    private List<Task> left;
    private List<Task> right;

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

    public Map<String, Object> getInputParameters() {
        throw new UnsupportedOperationException();
    }

    public String getReferenceName() {
        throw new UnsupportedOperationException();
    }

    public void setReferenceName(String referenceName) {
        throw new UnsupportedOperationException();
    }
}
