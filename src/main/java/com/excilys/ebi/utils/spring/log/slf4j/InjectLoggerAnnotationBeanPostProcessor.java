/**
 * Copyright 2011-2012 eBusiness Information, Groupe Excilys (www.excilys.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 		http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.excilys.ebi.utils.spring.log.slf4j;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.ReflectionUtils.FieldCallback;

/**
 * <p>
 * Injects loggers into new bean instances based on fields marked with
 * {@link InjectLogger} annotation.
 * </p>
 * <p>
 * Simplification of the work of David Winterfeldt on Spring By Example to only
 * target slf4j.
 * </p>
 * <p>
 * Warning : as only field injection is supported and happens after
 * instanciation, this strategy won't work if the logger is required inside the
 * constructor.
 * </p>
 * <p>
 * Warning : injecting into a static member is prohibited as it's non sense
 * </p>
 * 
 * @author <a href="mailto:slandelle@excilys.com">Stephane LANDELLE</a>
 */
@Component
public class InjectLoggerAnnotationBeanPostProcessor implements BeanPostProcessor, Ordered {

	public Object postProcessBeforeInitialization(Object bean, String beanName) {
		processLogger(bean);
		return bean;
	}

	public Object postProcessAfterInitialization(Object bean, String beanName) {
		return bean;
	}

	/**
	 * Lowest precedence
	 */
	public int getOrder() {
		return Ordered.LOWEST_PRECEDENCE;
	}

	/**
	 * Processes a bean's fields for injection if it has a {@link InjectLogger}
	 * annotation.
	 */
	protected void processLogger(final Object bean) {
		final Class<?> clazz = bean.getClass();

		ReflectionUtils.doWithFields(clazz, new FieldCallback() {
			public void doWith(Field field) {
				Annotation annotation = field.getAnnotation(InjectLogger.class);

				if (annotation != null) {
					int modifiers = field.getModifiers();
					Assert.isTrue(!Modifier.isStatic(modifiers), "InjectLogger annotation is not supported on static fields");
					Assert.isTrue(!Modifier.isFinal(modifiers), "InjectLogger annotation is not supported on final fields");

					ReflectionUtils.makeAccessible(field);

					Logger logger = LoggerFactory.getLogger(clazz);

					ReflectionUtils.setField(field, bean, logger);
				}
			}
		});
	}
}
