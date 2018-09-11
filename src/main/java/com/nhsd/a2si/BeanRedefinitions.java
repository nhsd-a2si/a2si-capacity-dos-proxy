package com.nhsd.a2si;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.stereotype.Component;

@Component
public class BeanRedefinitions implements BeanFactoryPostProcessor {
	private static final Logger LOGGER = LoggerFactory.getLogger(BeanRedefinitions.class);		

	// The bean definitions of httpClient and requestConfig are loaded from Capacity-Service-Client
	// and have timeouts and concurrent connection limits that are too small for DoS calls
	// This Post Process Bean Factory replaces the above with versions taken from 
	// DosProxyConfiguration
	    
	@Override
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {

		LOGGER.info("removing httpClient");
		beanFactory.destroyBean(beanFactory.getBean("requestConfig"));
		beanFactory.destroyBean(beanFactory.getBean("httpClient"));
		
		((DefaultListableBeanFactory) beanFactory).destroySingleton("requestConfig");
		((DefaultListableBeanFactory) beanFactory).destroySingleton("httpClient");
		
		LOGGER.info("replacing httpClient with dosHttpClient");
		beanFactory.configureBean(beanFactory.getBean("dosRequestConfig"), "requestConfig");
		beanFactory.configureBean(beanFactory.getBean("dosHttpClient"), "httpClient");
		beanFactory.registerSingleton("requestConfig", beanFactory.getBean("dosRequestConfig"));
		beanFactory.registerSingleton("httpClient", beanFactory.getBean("dosHttpClient"));

	}
}