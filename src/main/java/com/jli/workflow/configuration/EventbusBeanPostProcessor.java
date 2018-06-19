package com.jli.workflow.configuration;

import com.jli.workflow.annotation.RegisterToEventBus;
import lombok.extern.slf4j.Slf4j;
import org.greenrobot.eventbus.EventBus;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

@Slf4j
public class EventbusBeanPostProcessor implements BeanPostProcessor {

    private EventBus eventBus;

    public EventbusBeanPostProcessor(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if(bean.getClass().isAnnotationPresent(RegisterToEventBus.class)) {
            eventBus.register(bean);
            log.info("Eventbus is registering bean of name " + beanName);
        }
        return bean;
    }

}
