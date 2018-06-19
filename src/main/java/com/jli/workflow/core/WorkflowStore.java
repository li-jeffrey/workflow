package com.jli.workflow.core;

import com.jli.workflow.metadata.Workflow;
import org.springframework.util.Assert;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class WorkflowStore {

    private static final Map<Integer, Workflow> store = new ConcurrentHashMap<>();

    public static Workflow put(Workflow workflow) {
        Assert.notNull(workflow.getId(), "Workflow id is null");
        return store.put(workflow.getId(), workflow);
    }

    public static Workflow get(Integer id) {
        return id != null && store.containsKey(id) ? store.get(id) : null;
    }

    public static boolean contains(Integer id) {
        return id != null && store.containsKey(id);
    }
}
