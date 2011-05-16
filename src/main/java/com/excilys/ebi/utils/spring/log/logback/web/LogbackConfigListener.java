/*
 * Copyright 2010-2011 eBusiness Information, Groupe Excilys (www.excilys.com)
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.excilys.ebi.utils.spring.log.logback.web;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * Bootstrap listener for custom Logback initialization in a web environment.
 * Delegates to LogbackWebConfigurer (see its javadoc for configuration
 * details), inspired by {@see org.springframework.web.util.Log4jConfigListener}
 * .
 * <p/>
 * This listener should be registered before ContextLoaderListener in web.xml,
 * when using custom Logback initialization.
 * <p/>
 * <p>
 * Only Servlet 2.4+ supported as previous containers do not initialize
 * listeners before servlets.
 * 
 * @author <a href="mailto:slandelle@excilys.com">Stephane LANDELLE</a>
 */
public class LogbackConfigListener implements ServletContextListener {

	/**
	 * {@inheritDoc}
	 */
	public void contextInitialized(ServletContextEvent event) {
		LogbackWebConfigurer.initLogging(event.getServletContext());
	}

	/**
	 * {@inheritDoc}
	 */
	public void contextDestroyed(ServletContextEvent event) {
		LogbackWebConfigurer.shutdownLogging(event.getServletContext());
	}
}
