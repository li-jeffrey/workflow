package com.jli.workflow;

import com.jli.workflow.execution.ForkTaskRunner;
import com.jli.workflow.execution.JoinTaskRunner;
import com.jli.workflow.execution.SimpleTaskRunner;
import com.jli.workflow.metadata.ForkTask;
import com.jli.workflow.metadata.JoinTask;
import com.jli.workflow.metadata.Task;
import com.jli.workflow.metadata.Workflow;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;

import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

@RunWith(JUnit4.class)
public class MapperTest {

    @Mock
    private ExecutorService executorService;

    @RequiredArgsConstructor
    private static class MockSimpleTaskRunner extends SimpleTaskRunner {

        @Getter
        private final String referenceName;

        @Override
        protected void runInternal(Workflow workflow, Task task) {

        }
    }

    @RequiredArgsConstructor
    private static class MockJoinTaskRunner extends JoinTaskRunner {

        @Getter
        private final String referenceName;

        @Override
        protected void runInternal(Workflow workflow, Task task) {

        }
    }

    @Before
    public void setUp() {
        TaskRegistry.register(new MockSimpleTaskRunner("task_1"));
        TaskRegistry.register(new MockSimpleTaskRunner("task_2"));
        TaskRegistry.register(new MockJoinTaskRunner("task_join"));
        TaskRegistry.setForkTaskRunner(new ForkTaskRunner(executorService));
    }

    @Test
    public void mapsJsonToWorkflow() throws Exception {
        URL resource = this.getClass().getResource("/TestDefinition.json");

        Workflow workflow = WorkflowMapper.mapWorkflow(new File(resource.toURI()));

        assertThat(workflow.getName(), is("kitchensink"));
        assertThat(workflow.getDescription(), is("kitchensink workflow"));

        List<Task> workflowTasks = workflow.getTasks();
        assertThat(workflowTasks.size(), is(3));
        assertThat(workflowTasks.get(1) instanceof ForkTask, is(true));

        ForkTask forkTask = (ForkTask) workflowTasks.get(1);
        assertThat(forkTask.getLeft().size(), is(1));
        assertThat(forkTask.getRight().size(), is(1));

        assertThat(workflowTasks.get(2) instanceof JoinTask, is(true));
        JoinTask joinTask = (JoinTask) workflowTasks.get(2);
        assertThat(joinTask.getJoinOn(), is(ImmutablePair.of("task2a", "task2b")));
    }

    @Test
    public void mapInputParameters() throws Exception {
        URL resource = this.getClass().getResource("/TestDefinition.json");

        Workflow workflow = WorkflowMapper.mapWorkflow(new File(resource.toURI()));
        Map<String, Object> inputParams = new HashMap<>();
        inputParams.put("mod", 4);
        inputParams.put("oddEven", true);
        inputParams.put("evenOdd", false);

        Map<String, Object> nestedInput = new HashMap<>();
        nestedInput.put("task2a", 0.3);
        nestedInput.put("task2b", 0.7);

        inputParams.put("weight", nestedInput);

        WorkflowInputMapper.mapWorkflow(workflow, inputParams);
        assertThat(workflow.getTasks().get(0).getInputParameters().get("mod"), is("4"));
        assertThat(workflow.getTasks().get(0).getInputParameters().get("oddEven"), is("true"));
        assertThat(workflow.getTasks().get(2).getInputParameters().get("task2a"), is("0.3"));
        assertThat(workflow.getTasks().get(2).getInputParameters().get("task2b"), is("0.7"));
    }
}