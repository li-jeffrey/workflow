package com.jli.workflow.execution;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class TaskAbort {
    private final Integer workflowId;
    private final TaskStatus status;
    private final String message;
}
