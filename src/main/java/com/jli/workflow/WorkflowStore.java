package com.jli.workflow;

import com.jli.workflow.metadata.Workflow;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class WorkflowStore {

    private static final Map<Integer, Workflow> RUNTIME_STORE = new ConcurrentHashMap<>();

    private static final Map<String, Workflow> TEMPLATE_STORE = new ConcurrentHashMap<>();

    public static Workflow getRuntime(int workflowId) {
        return RUNTIME_STORE.get(workflowId);
    }

    static Workflow putRuntime(int id, Workflow workflow) {
        if (RUNTIME_STORE.containsKey(id)) {
            throw new IllegalStateException("Workflow with id " + id + " exists");
        }
        return RUNTIME_STORE.put(id, workflow);
    }

    public static Workflow getTemplate(String workflowName) {
        return TEMPLATE_STORE.get(workflowName);
    }

    static Workflow putTemplate(Workflow workflow) {
        return TEMPLATE_STORE.put(workflow.getName(), workflow);
    }
}
