package com.jli.workflow.execution;

import com.jli.workflow.core.WorkflowStore;
import com.jli.workflow.metadata.Workflow;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

@Service
@Slf4j
public class WorkflowExecutorImpl implements WorkflowExecutor {

    private final ExecutorService executorService;

    @Autowired
    public WorkflowExecutorImpl(ExecutorService executorService) {
        this.executorService = executorService;
    }

    @Override
    public int start(Workflow workflow) {
        workflow.setStartTime(Instant.now().toEpochMilli());
        workflow.setStatus(WorkflowStatus.RUNNING);
        WorkflowStore.put(workflow);
        WorkflowEventListener.register(workflow.getId(), executorService.submit(workflow::execute));

        return workflow.getId();
    }

    @Override
    public Future<?> execute(int workflowId) {
        if(!WorkflowStore.contains(workflowId)) {
            return null;
        }

        Workflow workflow = WorkflowStore.get(workflowId);
        if(workflow.isComplete()) {
            handleCompletion(workflow);
            return null;
        }

        return executorService.submit(workflow::execute);
    }

    @Override
    public void terminate(int workflowId) {
        Workflow workflow = WorkflowStore.get(workflowId);
        workflow.setEndTime(Instant.now().toEpochMilli());
        workflow.setStatus(WorkflowStatus.TERMINATED);
    }

    @Override
    public void fail(int workflowId) {
        Workflow workflow = WorkflowStore.get(workflowId);
        workflow.setEndTime(Instant.now().toEpochMilli());
        workflow.setStatus(WorkflowStatus.FAILED);
    }

    private void handleCompletion(Workflow workflow) {
        workflow.setEndTime(Instant.now().toEpochMilli());
        workflow.setStatus(WorkflowStatus.COMPLETE);
    }
}
