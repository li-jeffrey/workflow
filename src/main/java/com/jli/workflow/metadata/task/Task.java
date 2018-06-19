package com.jli.workflow.metadata.task;

import com.jli.workflow.components.TaskRunner;
import com.jli.workflow.metadata.Workflow;

public interface Task {

    void setName(String name);

    void setReferenceName(String referenceName);

    String getName();

    String getReferenceName();

    java.util.Map<String, Object> getInputParameters();

    void execute(Workflow workflow);

    void setRunner(TaskRunner runner);
}
