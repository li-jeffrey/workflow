package com.jli.workflow.metadata;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.tuple.Pair;

@Getter
@Setter
public class JoinTask extends SimpleTask {
    private Pair<String, String> joinOn;

    @Override
    public Task getCopy() {
        JoinTask task = new JoinTask();
        task.setName(getName());
        task.setReferenceName(getReferenceName());
        task.setRunner(getRunner());
        task.setJoinOn(joinOn);
        task.getInputParameters().putAll(getInputParameters());

        return task;
    }
}
