package com.jli.workflow;

import com.jli.workflow.configuration.WorkflowConfiguration;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest(classes = WorkflowIT.TestConfig.class)
@RunWith(SpringRunner.class)
public class WorkflowIT {

    @Import(WorkflowConfiguration.class)
    class TestConfig {

    }

    @Test
    public void startUp(){

    }
}
