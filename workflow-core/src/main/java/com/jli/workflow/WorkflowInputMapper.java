package com.jli.workflow;

import com.jli.workflow.metadata.*;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.text.StrSubstitutor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@UtilityClass
public class WorkflowInputMapper {

    public static void mapWorkflow(Workflow workflow, Map<String, Object> inputArgs) {
        Map<String, Object> flattenedInput = new HashMap<>();
        flattenMap("", inputArgs, flattenedInput);
        mapTasks(workflow.getTasks(), flattenedInput);
    }

    private static void mapTasks(List<Task> tasks, Map<String, Object> inputArgs) {
        for (Task task : tasks) {
            if (task instanceof SimpleTask) {
                mapSimpleTask((SimpleTask) task, inputArgs);
            } else if (task instanceof ForkTask) {
                mapForkTask((ForkTask) task, inputArgs);
            }
        }
    }

    private static void mapSimpleTask(SimpleTask simpleTask, Map<String, Object> inputArgs) {
        StrSubstitutor sub = new StrSubstitutor(inputArgs);
        sub.setValueDelimiter(":");
        for (Map.Entry<String, Object> paramEntry : simpleTask.getInputParameters().entrySet()) {
            paramEntry.setValue(sub.replace(paramEntry.getValue()));
        }
    }

    private static void mapForkTask(ForkTask forkTask, Map<String, Object> inputArgs) {
        mapTasks(forkTask.getLeft(), inputArgs);
        mapTasks(forkTask.getRight(), inputArgs);
    }

    private static void flattenMap(String currentPath, Object node, Map<String, Object> map) {
        if (node instanceof Map) {
            String pathPrefix = currentPath.isEmpty() ? "" : currentPath + ".";
            for (Map.Entry entry : ((Map<String, Object>) node).entrySet()) {
                flattenMap(pathPrefix + entry.getKey(), entry.getValue(), map);
            }
        } else if (node instanceof List) {
            List<Object> listNode = (List<Object>) node;
            for (int i = 0; i < listNode.size(); i++) {
                flattenMap(currentPath + "[" + i + "]", listNode.get(i), map);
            }
        } else {
            map.put(currentPath, node);
        }
    }
}
