/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved
 */
package de.hybris.platform.cockpit.zk.mock;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.Locale;
import java.util.Map;

import javax.servlet.ServletContext;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.NoSuchMessageException;
import org.springframework.core.ResolvableType;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.web.context.WebApplicationContext;


/**
 * @author Jacek
 */
public class DummyWebApplicationContext implements WebApplicationContext
{
	private final ApplicationContext applicationContext;
	private ServletContext servletCtx;


	/**
	 * @param applicationContext
	 */
	public DummyWebApplicationContext(final ApplicationContext applicationContext, final ServletContext servlet)
	{
		this.applicationContext = applicationContext;
	}

	@Override
	public ServletContext getServletContext()
	{//
		return servletCtx;
	}

	@Override
	public AutowireCapableBeanFactory getAutowireCapableBeanFactory() throws IllegalStateException
	{
		return applicationContext.getAutowireCapableBeanFactory();
	}

	@Override
	public String getDisplayName()
	{
		return applicationContext.getDisplayName();
	}

	@Override
	public String getApplicationName()
	{
		return applicationContext.getApplicationName();
	}

	@Override
	public String getId()
	{
		return applicationContext.getId();
	}

	@Override
	public ApplicationContext getParent()
	{
		return applicationContext.getParent();
	}

	@Override
	public long getStartupDate()
	{
		return applicationContext.getStartupDate();
	}

	@Override
	public boolean containsBeanDefinition(final String beanName)
	{
		return applicationContext.containsBeanDefinition(beanName);
	}

	@Override
	public <A extends Annotation> A findAnnotationOnBean(final String beanName, final Class<A> annotationType)
	{
		return applicationContext.findAnnotationOnBean(beanName, annotationType);
	}

	@Override
	public <A extends Annotation> A findAnnotationOnBean(final String beanName, final Class<A> annotationType, final boolean allowFactoryBeanInit)
	{
		return applicationContext.findAnnotationOnBean(beanName, annotationType, allowFactoryBeanInit);
	}

	@Override
	public int getBeanDefinitionCount()
	{
		return applicationContext.getBeanDefinitionCount();
	}

	@Override
	public String[] getBeanDefinitionNames()
	{
		return applicationContext.getBeanDefinitionNames();
	}

	@Override
	public <T> ObjectProvider<T> getBeanProvider(final Class<T> aClass, final boolean b)
	{
		return null;
	}

	@Override
	public <T> ObjectProvider<T> getBeanProvider(final ResolvableType resolvableType, final boolean b)
	{
		return null;
	}

	@Override
	public String[] getBeanNamesForType(final ResolvableType resolvableType)
	{
		return applicationContext.getBeanNamesForType(resolvableType);
	}

	@Override
	public String[] getBeanNamesForType(final ResolvableType type, final boolean includeNonSingletons,
			final boolean allowEagerInit)
	{
		return applicationContext.getBeanNamesForType(type, includeNonSingletons, allowEagerInit);
	}

	@Override
	public String[] getBeanNamesForType(final Class type)
	{
		return applicationContext.getBeanNamesForType(type);
	}

	@Override
	public String[] getBeanNamesForType(final Class type, final boolean includeNonSingletons, final boolean allowEagerInit)
	{
		return applicationContext.getBeanNamesForType(type, includeNonSingletons, allowEagerInit);
	}

	@Override
	public <T> Map<String, T> getBeansOfType(final Class<T> type) throws BeansException
	{
		return applicationContext.getBeansOfType(type);
	}

	@Override
	public <T> Map<String, T> getBeansOfType(final Class<T> type, final boolean includeNonSingletons, final boolean allowEagerInit)
			throws BeansException
	{
		return applicationContext.getBeansOfType(type, includeNonSingletons, allowEagerInit);
	}

	@Override
	public Map<String, Object> getBeansWithAnnotation(final Class<? extends Annotation> annotationType) throws BeansException
	{
		return applicationContext.getBeansWithAnnotation(annotationType);
	}

	@Override
	public boolean containsBean(final String name)
	{
		return applicationContext.containsBean(name);
	}

	@Override
	public String[] getAliases(final String name)
	{
		return applicationContext.getAliases(name);
	}

	@Override
	public Object getBean(final String name) throws BeansException
	{
		return applicationContext.getBean(name);
	}

	@Override
	public <T> T getBean(final Class<T> requiredType) throws BeansException
	{
		return applicationContext.getBean(requiredType);
	}

	@Override
	public <T> T getBean(final String name, final Class<T> requiredType) throws BeansException
	{
		return applicationContext.getBean(name, requiredType);
	}

	@Override
	public Object getBean(final String name, final Object... args) throws BeansException
	{
		return applicationContext.getBean(name, args);
	}

	@Override
	public Class<?> getType(final String name) throws NoSuchBeanDefinitionException
	{
		return applicationContext.getType(name);
	}

	@Override
	public Class<?> getType(final String name, final boolean allowFactoryBeanInit) throws NoSuchBeanDefinitionException
	{
		return applicationContext.getType(name, allowFactoryBeanInit);
	}

	@Override
	public boolean isPrototype(final String name) throws NoSuchBeanDefinitionException
	{
		return applicationContext.isPrototype(name);
	}

	@Override
	public boolean isTypeMatch(final String s, final ResolvableType resolvableType) throws NoSuchBeanDefinitionException
	{
		return applicationContext.isTypeMatch(s, resolvableType);
	}

	@Override
	public boolean isSingleton(final String name) throws NoSuchBeanDefinitionException
	{
		return applicationContext.isSingleton(name);
	}

	@Override
	public boolean isTypeMatch(final String name, final Class targetType) throws NoSuchBeanDefinitionException
	{
		return applicationContext.isTypeMatch(name, targetType);
	}

	@Override
	public boolean containsLocalBean(final String name)
	{
		return applicationContext.containsLocalBean(name);
	}

	@Override
	public BeanFactory getParentBeanFactory()
	{
		return applicationContext.getParentBeanFactory();
	}

	@Override
	public String getMessage(final MessageSourceResolvable resolvable, final Locale locale) throws NoSuchMessageException
	{
		return applicationContext.getMessage(resolvable, locale);
	}

	@Override
	public String getMessage(final String code, final Object[] args, final Locale locale) throws NoSuchMessageException
	{
		return applicationContext.getMessage(code, args, locale);
	}

	@Override
	public String getMessage(final String code, final Object[] args, final String defaultMessage, final Locale locale)
	{
		return applicationContext.getMessage(code, args, defaultMessage, locale);
	}

	@Override
	public void publishEvent(final ApplicationEvent event)
	{
		applicationContext.publishEvent(event);
	}

	@Override
	public void publishEvent(final Object o)
	{
		applicationContext.publishEvent(o);
	}

	@Override
	public Resource[] getResources(final String locationPattern) throws IOException
	{
		return applicationContext.getResources(locationPattern);
	}

	@Override
	public ClassLoader getClassLoader()
	{
		return applicationContext.getClassLoader();
	}

	@Override
	public Resource getResource(final String location)
	{
		return applicationContext.getResource(location);
	}

	@Override
	public Environment getEnvironment()
	{
		return applicationContext.getEnvironment();
	}

	@Override
	public <T> T getBean(final Class<T> tClass, final Object... objects) throws BeansException
	{
		return applicationContext.getBean(tClass, objects);
	}

	@Override
	public <T> ObjectProvider<T> getBeanProvider(final Class<T> aClass)
	{
		return applicationContext.getBeanProvider(aClass);
	}

	@Override
	public <T> ObjectProvider<T> getBeanProvider(final ResolvableType resolvableType)
	{
		return applicationContext.getBeanProvider(resolvableType);
	}


	@Override
	public String[] getBeanNamesForAnnotation(final Class<? extends Annotation> aClass)
	{
		return applicationContext.getBeanNamesForAnnotation(aClass);
	}

}
