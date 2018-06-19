package com.jli.workflow.builder;

import com.jli.workflow.components.ForkTaskRunner;
import com.jli.workflow.components.TaskRunner;
import com.jli.workflow.metadata.Workflow;
import com.jli.workflow.metadata.task.ForkTask;
import com.jli.workflow.metadata.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class WorkflowBuilderImpl implements WorkflowBuilder {

    private final Map<String, TaskRunner> taskRunners;

    private final ForkTaskRunner forkTaskRunner;

    private final AtomicInteger idGenerator = new AtomicInteger(0);

    @Autowired
    public WorkflowBuilderImpl(Map<String, TaskRunner> taskRunners, ForkTaskRunner forkTaskRunner) {
        this.taskRunners = taskRunners;
        this.forkTaskRunner = forkTaskRunner;
    }

    @Override
    public void build(Workflow workflow) {
        for(Task task : workflow.getTasks()) {
            buildTask(task);
        }

        workflow.setId(idGenerator.incrementAndGet());
    }

    private void buildTask(Task task) {
        if(task instanceof ForkTask) {
            buildForkTask((ForkTask) task);
        } else {
            buildSimpleTask(task);
        }
    }

    private void buildForkTask(ForkTask forkTask) {
        forkTask.setRunner(forkTaskRunner);
        for(Task task : forkTask.getRight()) {
            buildTask(task);
        }

        for(Task task: forkTask.getLeft()) {
            buildTask(task);
        }
    }

    private void buildSimpleTask(Task task) {
        task.setRunner(taskRunners.get(task.getReferenceName()));
    }
}
