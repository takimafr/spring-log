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
package com.excilys.ebi.utils.spring.log.logback.test;

import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.io.support.ResourcePatternUtils;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.support.AbstractTestExecutionListener;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.ResourceUtils;
import org.springframework.util.StringUtils;

import com.excilys.ebi.utils.spring.log.logback.LogbackConfigurer;

/**
 * Spring test framework TestExecutionListener for configuring Logback on JUnit
 * tests setup and teardown.
 * 
 * It looks for a {@Logback} annotation to get the config file
 * location
 * 
 * @see #processLocation for conventions on how the resource is loaded
 * 
 * @author <a href="mailto:slandelle@excilys.com">Stephane LANDELLE</a>
 */
public class LogbackConfigurerTestExecutionListener extends AbstractTestExecutionListener {

	/**
	 * Default resource name = "logback-test.xml"
	 */
	private static final String DEFAULT_RESOURCE_NAME = "logback-test.xml";

	@Override
	public void beforeTestClass(TestContext testContext) throws Exception {

		Logback annotation = AnnotationUtils.findAnnotation(testContext.getTestClass(), Logback.class);

		if (annotation != null) {
			String location = processLocation(testContext.getTestClass(), annotation.value());
			LogbackConfigurer.initLogging(location);
		}
	}

	@Override
	public void afterTestClass(TestContext testContext) throws Exception {
		LogbackConfigurer.shutdownLogging();
	}

	/**
	 * If the supplied <code>location</code> is <code>null</code> or
	 * <em>empty</em>, a default location will be
	 * {@link #generateDefaultLocation(Class) generated} for the specified
	 * {@link Class class} and the {@link #getResourceName resource file name} ;
	 * otherwise, the supplied <code>location</code> will be
	 * {@link #modifyLocation modified} if necessary and returned.
	 * 
	 * @param clazz
	 *            the class with which the location is associated: to be used
	 *            when generating a default location
	 * @param location
	 *            the unmodified location to use for configuring logback (can be
	 *            <code>null</code> or empty)
	 * @return a logback config file location
	 * @see #generateDefaultLocation
	 * @see #modifyLocation
	 */
	public final String processLocation(Class<?> clazz, String location) {
		return (!StringUtils.hasLength(location)) ? generateDefaultLocation(clazz) : modifyLocation(clazz, location);
	}

	protected String getResourceName() {
		return DEFAULT_RESOURCE_NAME;
	}

	/**
	 * Generates the default classpath resource location based on the supplied
	 * class.
	 * <p>
	 * For example, if the supplied class is <code>com.example.MyTest</code>,
	 * the generated location will be a string with a value of
	 * &quot;classpath:/com/example/<code>&lt;suffix&gt;</code>&quot;, where
	 * <code>&lt;suffix&gt;</code> is the value of the
	 * {@link #getResourceName() resource name} string.
	 * <p>
	 * Subclasses can override this method to implement a different
	 * <em>default location generation</em> strategy.
	 * 
	 * @param clazz
	 *            the class for which the default locations are to be generated
	 * @return an array of default application context resource locations
	 * @see #getResourceSuffix()
	 */
	private String generateDefaultLocation(Class<?> clazz) {
		Assert.notNull(clazz, "Class must not be null");
		return ResourceUtils.CLASSPATH_URL_PREFIX + StringUtils.cleanPath(ClassUtils.classPackageAsResourcePath(clazz) + "/" + getResourceName());
	}

	/**
	 * Generate a modified version of the supplied location and returns it.
	 * <p>
	 * A plain path, e.g. &quot;context.xml&quot;, will be treated as a
	 * classpath resource from the same package in which the specified class is
	 * defined. A path starting with a slash is treated as a fully qualified
	 * class path location, e.g.: &quot;/com/example/whatever/foo.xml&quot;. A
	 * path which references a URL (e.g., a path prefixed with
	 * {@link ResourceUtils#CLASSPATH_URL_PREFIX classpath:},
	 * {@link ResourceUtils#FILE_URL_PREFIX file:}, <code>http:</code>, etc.)
	 * will be added to the results unchanged.
	 * <p>
	 * Subclasses can override this method to implement a different
	 * <em>location modification</em> strategy.
	 * 
	 * @param clazz
	 *            the class with which the locations are associated
	 * @param locations
	 *            the resource location to be modified
	 * @return the modified application context resource location
	 */
	protected String modifyLocation(Class<?> clazz, String location) {
		String modifiedLocation = null;
		if (location.startsWith("/")) {
			modifiedLocation = ResourceUtils.CLASSPATH_URL_PREFIX + location;
		} else if (!ResourcePatternUtils.isUrl(location)) {
			modifiedLocation = ResourceUtils.CLASSPATH_URL_PREFIX + StringUtils.cleanPath(ClassUtils.classPackageAsResourcePath(clazz) + "/" + location);
		} else {
			modifiedLocation = StringUtils.cleanPath(location);
		}
		return modifiedLocation;
	}
}
