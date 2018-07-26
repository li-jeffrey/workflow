package com.jli.workflow.execution;

import com.jli.workflow.metadata.Workflow;
import com.jli.workflow.metadata.Task;

public interface TaskRunner {
    void run(Workflow workflow, Task task);
    String getReferenceName();
}
