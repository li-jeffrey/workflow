package com.jli.workflow;

import com.fasterxml.jackson.core.type.TypeReference;
import com.jli.workflow.metadata.*;
import com.jli.workflow.util.ObjectMapperHolder;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.tuple.ImmutablePair;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@UtilityClass
public class WorkflowMapper {

    @SneakyThrows
    public static Workflow mapWorkflow(File file) {
        Map<String, Object> initialMap = ObjectMapperHolder.getMapper().readValue(file, new TypeReference<Map<String, Object>>() {});
        List<Map<String, Object>> tasksJson = (List<Map<String, Object>>) initialMap.get("tasks");
        List<Task> tasks = mapTasks(tasksJson);

        return new Workflow((String) initialMap.get("name"), (String) initialMap.get("description"), tasks);
    }

    private static List<Task> mapTasks(List<Map<String, Object>> taskDefs) {
        List<Task> tasks = new LinkedList<>();
        for (Map<String, Object> taskDef : taskDefs) {
            Task task;
            switch ((String) taskDef.get("type")) {
                case "SIMPLE":
                    task = mapSimpleTask(taskDef);
                    break;
                case "JOIN":
                    task = mapJoinTask(taskDef);
                    break;
                case "FORK":
                    task = mapForkTask(taskDef);
                    break;
                default:
                    throw new IllegalStateException("Unknown task type: " + taskDef.get("type"));

            }
            tasks.add(task);
        }

        return tasks;
    }

    private static SimpleTask mapSimpleTask(Map<String, Object> taskDef) {
        SimpleTask simpleTask = new SimpleTask();
        simpleTask.setName((String )taskDef.get("name"));
        simpleTask.setRunner(TaskRegistry.getRunner((String) taskDef.get("referenceName")));
        simpleTask.getInputParameters().putAll((Map<String, Object>) taskDef.get("inputParameters"));

        return simpleTask;
    }

    private static JoinTask mapJoinTask(Map<String, Object> taskDef) {
        JoinTask joinTask = new JoinTask();
        joinTask.setName((String) taskDef.get("name"));
        joinTask.setRunner(TaskRegistry.getRunner((String) taskDef.get("referenceName")));
        joinTask.getInputParameters().putAll((Map<String, Object>) taskDef.get("inputParameters"));

        List<String> joinOn = (List<String>) taskDef.get("joinOn");
        joinTask.setJoinOn(ImmutablePair.of(joinOn.get(0), joinOn.get(1)));

        return joinTask;
    }

    private static ForkTask mapForkTask(Map<String, Object> taskDef) {
        ForkTask forkTask = new ForkTask();
        forkTask.setName((String) taskDef.get("name"));
        forkTask.setRunner(TaskRegistry.getForkTaskRunner());

        List<List<Map<String, Object>>> forkTasksJson = (List<List<Map<String, Object>>>) taskDef.get("forkTasks");
        forkTask.setLeft(mapTasks(forkTasksJson.get(0)));
        forkTask.setRight(mapTasks(forkTasksJson.get(1)));

        return forkTask;
    }
}
