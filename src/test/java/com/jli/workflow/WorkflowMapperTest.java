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
import java.util.List;
import java.util.concurrent.ExecutorService;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

@RunWith(JUnit4.class)
public class WorkflowMapperTest {

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
}