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

import static junit.framework.Assert.assertNotNull;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author <a href="mailto:slandelle@excilys.com">Stephane LANDELLE</a>
 */
public class InjectLoggerTest {

	/**
	 * Check that injection works fine with correct usage
	 */
	@Test
	public void testCorrectInjection() {

		ApplicationContext ctx = new ClassPathXmlApplicationContext("com/excilys/ebi/utils/spring/log/slf4j/bean-with-logger-context.xml");
		BeanWithLogger bean = ctx.getBean(BeanWithLogger.class);
		assertNotNull("logger not injected", bean.getLogger());
	}

	/**
	 * Check that an IllegalStateException is thrown if the logger member is
	 * static
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testStaticLoggerInjectionFailure() throws Throwable {

		try {
			new ClassPathXmlApplicationContext("com/excilys/ebi/utils/spring/log/slf4j/bean-with-static-logger-context.xml");

		} catch (RuntimeException e) {
			throw e.getCause();
		}
	}
}
