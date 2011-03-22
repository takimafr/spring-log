/*
 * Copyright 2010-2011 Excilys (www.excilys.com)
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
package com.excilys.utils.spring.log.logback;

import java.io.FileNotFoundException;
import java.net.URL;

import org.slf4j.bridge.SLF4JBridgeHandler;
import org.springframework.util.ResourceUtils;
import org.springframework.util.SystemPropertyUtils;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.selector.ContextSelector;
import ch.qos.logback.classic.util.ContextInitializer;
import ch.qos.logback.classic.util.ContextSelectorStaticBinder;
import ch.qos.logback.core.joran.spi.JoranException;

/**
 * Convenience class that features simple methods for custom Logback
 * configuration, inspired by {@link org.springframework.util.Log4jConfigurer}.
 * <p>
 * Only needed for non-default Logback initialization with a custom config
 * location. By default, Logback will simply read its configuration from a
 * "logback.xml" or "logback-test.xml" file in the root of the class path.
 * <p>
 * For web environments, the analogous LogbackWebConfigurer class can be found
 * in the web package, reading in its configuration from context-params in
 * web.xml. In a J2EE web application, Logback is usually set up via
 * LogbackConfigListener, delegating to LogbackWebConfigurer underneath.
 * <p>
 * <b>Do not forget to configure logback for JUL!!!</b>
 * <p>
 * <code>
 * <contextListener class="ch.qos.logback.classic.jul.LevelChangePropagator">
 *   <resetJUL>true</resetJUL>
 * </contextListener>
 * </code>
 * 
 * @author <a href="mailto:slandelle@excilys.com">Stephane LANDELLE</a>
 */
public class LogbackConfigurer {

	/**
	 * Instantiates a new logback configurer.
	 */
	private LogbackConfigurer() {
	}

	/**
	 * Initialize logback from the given file. JUL is correctly handled and
	 * redirected on SLF4J (@see
	 * http://logback.qos.ch/manual/configuration.html#LevelChangePropagator).
	 * 
	 * @param location
	 *            the location of the config file: either a "classpath:"
	 *            location (e.g. "classpath:logback.xml"), an absolute file URL
	 *            (e.g. "file:C:/logback.xml), or a plain absolute path in the
	 *            file system (e.g. "C:/logback.xml")
	 * @throws java.io.FileNotFoundException
	 *             if the location specifies an invalid file path
	 * @throws FileNotFoundException
	 *             the file not found exception
	 * @throws JoranException
	 *             the joran exception
	 */
	public static void initLogging(String location) throws FileNotFoundException, JoranException {
		String resolvedLocation = SystemPropertyUtils.resolvePlaceholders(location);
		URL url = ResourceUtils.getURL(resolvedLocation);
		ContextSelector selector = ContextSelectorStaticBinder.getSingleton().getContextSelector();
		LoggerContext loggerContext = selector.getLoggerContext();
		// in the current version logback automatically configures at startup
		// the context, so we have to reset it
		loggerContext.reset();
		ContextInitializer contextInitializer = new ContextInitializer(loggerContext);
		contextInitializer.configureByResource(url);

		// reset JUL
		// don't forget to configure the LevelChangePropagator contextListener
		// in the config file!!!
		SLF4JBridgeHandler.install();
	}

	/**
	 * Shut down logback.
	 * <p>
	 * This isn't strictly necessary, but recommended for shutting down logback
	 * in a scenario where the host VM stays alive (for example, when shutting
	 * down an application in a J2EE environment).
	 */
	public static void shutdownLogging() {
		ContextSelector selector = ContextSelectorStaticBinder.getSingleton().getContextSelector();
		LoggerContext loggerContext = selector.getLoggerContext();
		String loggerContextName = loggerContext.getName();
		LoggerContext context = selector.detachLoggerContext(loggerContextName);
		context.reset();
	}
}