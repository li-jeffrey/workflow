package com.jli.workflow.metadata;

import com.jli.workflow.execution.ForkTaskRunner;
import com.jli.workflow.execution.TaskRunner;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
@Setter
public class ForkTask implements Task {

    private String name;
    private String referenceName;
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

    @Override
    public Task getCopy() {
        ForkTask task = new ForkTask();
        task.setName(name);
        task.setRunner(runner);
        task.setLeft(left.stream().map(Task::getCopy).collect(Collectors.toList()));
        task.setRight(right.stream().map(Task::getCopy).collect(Collectors.toList()));

        return task;
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
