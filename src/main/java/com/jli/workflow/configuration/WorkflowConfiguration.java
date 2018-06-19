package com.jli.workflow.configuration;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.greenrobot.eventbus.EventBus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

@Configuration
@EnableScheduling
@ComponentScan(basePackages = "com.jli.workflow")
public class WorkflowConfiguration {

    @Bean
    public TaskScheduler taskScheduler() {
        return new ConcurrentTaskScheduler(Executors.newScheduledThreadPool(2));
    }

    @Bean
    public EventBus eventBus() {
        return EventBus.builder()
                .executorService(Executors.newSingleThreadExecutor())
                .build();
    }

    @Bean
    public static BeanPostProcessor eventbusBeanPostProcessor(EventBus eventBus) {
        return new EventbusBeanPostProcessor(eventBus);
    }

    @Bean
    public ExecutorService workflowThreadPool() {
        ThreadFactory factory = new ThreadFactoryBuilder().setNameFormat("workflow-%d").build();
        return Executors.newFixedThreadPool(4, factory);
    }
}
