package com.jli.workflow.monitoring;

import com.jli.workflow.metadata.Workflow;

public interface WorkflowEventListener {
    default void onTermination(Workflow workflow) {

    }

    default void onFailure(Workflow workflow) {

    }

    default void onCancellation(Workflow workflow) {

    }

    default void onCompletion(Workflow workflow) {

    }
}
