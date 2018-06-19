package com.jli.workflow.components;

import com.jli.workflow.metadata.Workflow;
import com.jli.workflow.metadata.task.Task;

public interface TaskRunner {
    void run(Workflow workflow, Task task);
}
