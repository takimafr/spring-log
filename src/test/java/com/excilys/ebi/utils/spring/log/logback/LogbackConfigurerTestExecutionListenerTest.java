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
package com.excilys.ebi.utils.spring.log.logback;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import com.excilys.ebi.utils.spring.log.logback.test.Logback;
import com.excilys.ebi.utils.spring.log.logback.test.LogbackConfigurerTestExecutionListener;
import com.excilys.ebi.utils.spring.log.slf4j.InjectLogger;

/**
 * Test for {@link LogbackConfigurer}
 * 
 * 
 * @author <a href="mailto:slandelle@excilys.com">Stephane LANDELLE</a>
 */
@RunWith(SpringJUnit4ClassRunner.class)
@TestExecutionListeners({ LogbackConfigurerTestExecutionListener.class, DependencyInjectionTestExecutionListener.class })
@ContextConfiguration("applicationContext.xml")
@Logback
public class LogbackConfigurerTestExecutionListenerTest {

	@InjectLogger
	private Logger logger;

	/**
	 * The logback-test.xml defines a Console output. Console output is
	 * temporarily trapped so that we can check what is written.
	 */
	@Test
	public void testConsoleLogger() {

		PrintStream stdout = System.out;
		String log = null;

		try {
			// replace console standard output
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			PrintStream ps = new PrintStream(os);
			System.setOut(ps);

			logger.debug("something in DEBUG");

			log = os.toString();

		} finally {
			// restore console standard output
			System.setOut(stdout);
		}

		Assert.assertTrue("Output log is incorrect", log.contains("DEBUG c.e.e.u.s.l.l.LogbackConfigurerTestExecutionListenerTest.testConsoleLogger - something in DEBUG"));
	}
}
