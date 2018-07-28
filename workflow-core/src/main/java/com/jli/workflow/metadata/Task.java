package com.jli.workflow.metadata;

import com.jli.workflow.execution.TaskRunner;

public interface Task {

    void setName(String name);

    void setReferenceName(String referenceName);

    String getName();

    String getReferenceName();

    java.util.Map<String, Object> getInputParameters();

    void execute(Workflow workflow);

    void setRunner(TaskRunner runner);

    Task getCopy();
}
