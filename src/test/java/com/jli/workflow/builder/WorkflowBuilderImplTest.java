package com.jli.workflow.builder;

import com.jli.workflow.components.ForkTaskRunner;
import com.jli.workflow.components.SimpleTaskRunner;
import com.jli.workflow.components.TaskRunner;
import com.jli.workflow.metadata.Workflow;
import com.jli.workflow.metadata.task.ForkTask;
import com.jli.workflow.metadata.task.JoinTask;
import com.jli.workflow.metadata.task.SimpleTask;
import com.jli.workflow.metadata.task.Task;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

public class WorkflowBuilderImplTest {
    private WorkflowBuilder workflowBuilder;

    private ForkTaskRunner forkTaskRunner = new ForkTaskRunner();

    class MockTaskRunner extends SimpleTaskRunner {

        @Override
        protected void runInternal(Workflow workflow, Task task) {

        }
    }

    @Before
    public void setUp() {
        Map<String, TaskRunner> taskRunners = new HashMap<>();
        taskRunners.put("preprocess", new MockTaskRunner());
        taskRunners.put("parallel", new MockTaskRunner());
        taskRunners.put("parallel1", new MockTaskRunner());
        taskRunners.put("join", new MockTaskRunner());
        taskRunners.put("join1", new MockTaskRunner());

        workflowBuilder = new WorkflowBuilderImpl(taskRunners, forkTaskRunner);
    }

    @Test
    public void shouldBuildWorkflow() {
        Workflow workflow = new Workflow();
        Task task = new SimpleTask();
        task.setReferenceName("preprocess");

        Task parallelTask = new SimpleTask();
        parallelTask.setReferenceName("parallel");

        Task parallelTask1 = new SimpleTask();
        parallelTask1.setReferenceName("parallel1");

        Task joinTask = new JoinTask();
        joinTask.setReferenceName("join");

        Task joinTask1 = new JoinTask();
        joinTask1.setReferenceName("join1");

        ForkTask forkTaskA = new ForkTask();
        ForkTask forkTaskB = new ForkTask();
        forkTaskB.getLeft().add(parallelTask1);
        forkTaskB.getRight().add(parallelTask1);

        forkTaskA.getLeft().add(parallelTask);
        forkTaskA.getRight().addAll(Arrays.asList(forkTaskB, joinTask1));

        workflow.setTasks(new LinkedList<>(Arrays.asList(task, forkTaskA, joinTask)));

        workflowBuilder.build(workflow);
        assertThat(workflow.getId(), is(1));
    }
}