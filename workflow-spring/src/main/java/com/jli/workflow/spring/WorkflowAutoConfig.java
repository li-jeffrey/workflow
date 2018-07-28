package com.jli.workflow.spring;

import com.jli.workflow.WorkflowEngine;
import com.jli.workflow.monitoring.WorkflowEventListener;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WorkflowAutoConfig {

    @Bean
    @ConditionalOnMissingBean
    public WorkflowEventListener workflowEventListener() {
        return new WorkflowEventListener() { };
    }

    @Bean
    @ConditionalOnMissingBean
    public WorkflowEngine workflowEngine(WorkflowProperties workflowProperties,
                                         WorkflowEventListener workflowEventListener) {

        workflowProperties.setWorkflowEventListener(workflowEventListener);
        return WorkflowEngine.start(workflowProperties);
    }

}
