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

import java.io.FileNotFoundException;

import javax.servlet.ServletContext;

import org.springframework.util.ResourceUtils;
import org.springframework.util.SystemPropertyUtils;
import org.springframework.web.util.WebUtils;

import ch.qos.logback.core.joran.spi.JoranException;

import com.excilys.ebi.utils.spring.log.logback.LogbackConfigurer;

/**
 * Convenience class that performs custom Logback initialization for web
 * environments, allowing for log file paths within the web application,
 * inspired by {@link org.springframework.web.util.Log4jWebConfigurer}.
 * <p>
 * Supports an init parameter at the servlet context level (that is,
 * context-param entries in web.xml):
 * <ul>
 * <li><i>"logbackConfigLocation":</i><br>
 * Location of the Logback config file; either a "classpath:" location (e.g.
 * "classpath:myLogback.xml"), an absolute file URL (e.g.
 * "file:C:/logback.properties), or a plain path relative to the web application
 * root directory (e.g. "/WEB-INF/logback.xml"). If not specified, default
 * Logback initialization will apply ("logback.xml" or "logback-test.xml" in the
 * class path; see Logback documentation for details).
 * </ul>
 * <p>
 * Note: <code>initLogging</code> should be called before any other Spring
 * activity (when using Logback), for proper initialization before any Spring
 * logging attempts.
 * <p>
 * 
 * @author <a href="mailto:slandelle@excilys.com">Stephane LANDELLE</a>
 */
public final class LogbackWebConfigurer {

	/** Parameter specifying the location of the logback config file. */
	public static final String CONFIG_LOCATION_PARAM = "logbackConfigLocation";

	/**
	 * Instantiates a new logback web configurer.
	 */
	private LogbackWebConfigurer() {
	}

	/**
	 * Initialize Logback, including setting the web app root system property.
	 * 
	 * @param servletContext
	 *            the current ServletContext
	 * @see org.springframework.web.util.WebUtils#setWebAppRootSystemProperty
	 */
	public static void initLogging(ServletContext servletContext) {

		// Only perform custom Logback initialization in case of a config file.
		String location = getConfigLocation(servletContext);

		if (location != null) {
			// Perform actual Logback initialization; else rely on Logback's
			// default initialization.
			try {
				// Return a URL (e.g. "classpath:" or "file:") as-is;
				// consider a plain file path as relative to the web application
				// root directory.
				if (!ResourceUtils.isUrl(location)) {
					// Resolve system property placeholders before resolving
					// real path.
					location = SystemPropertyUtils.resolvePlaceholders(location);
					location = WebUtils.getRealPath(servletContext, location);
				}

				// Write log message to server log.
				servletContext.log("Initializing Logback from [" + location + "]");

				// Initialize
				LogbackConfigurer.initLogging(location);
			} catch (FileNotFoundException ex) {
				throw new IllegalArgumentException("Invalid 'logbackConfigLocation' parameter: " + ex.getMessage());
			} catch (JoranException e) {
				throw new RuntimeException("Unexpected error while configuring logback", e);
			}
		}
	}

	/**
	 * Search for a specified config location, first in the servlet context and
	 * then as a System property
	 * 
	 * @param servletContext
	 *            the servletContext
	 * @return the config location
	 */
	private static String getConfigLocation(ServletContext servletContext) {
		String location = servletContext.getInitParameter(CONFIG_LOCATION_PARAM);
		if (location == null) {
			location = System.getProperty(CONFIG_LOCATION_PARAM);
		}
		return location;
	}

	/**
	 * Shut down Logback, properly releasing all file locks and resetting the
	 * web app root system property.
	 * 
	 * @param servletContext
	 *            the current ServletContext
	 * @see WebUtils#removeWebAppRootSystemProperty
	 */
	public static void shutdownLogging(ServletContext servletContext) {
		servletContext.log("Shutting down Logback");
		LogbackConfigurer.shutdownLogging();
	}
}
