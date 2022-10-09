package com.example.springapp;

import lombok.extern.slf4j.Slf4j;
import org.outofoffice.eidaspringsupport.EidaInitializer;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class EidaInitializeProcessor implements BeanFactoryPostProcessor {

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        EidaInitializer.init(beanFactory, SpringAppApplication.class);
    }

}
