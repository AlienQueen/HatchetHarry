package org.alienlabs.hatchetharry.integrationTest;

import org.alienlabs.hatchetharry.view.component.CardRotateBehavior;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thoughtworks.selenium.DefaultSelenium;
import com.thoughtworks.selenium.SeleneseTestCase;


public class VerifyQUnitTests extends SeleneseTestCase
{
	private static final Logger LOGGER = LoggerFactory.getLogger(CardRotateBehavior.class);

	@Override
	@Before
	public void setUp()
	{
		try
		{
			super.setUp();
			this.selenium = new DefaultSelenium("localhost", 4444, "*firefox",
					"http://localhost:8080/");
			this.selenium.start();
		}
		catch (final Exception e)
		{
			e.printStackTrace();
		}
	}

	@Test
	public void testQunit()
	{
		this.selenium.open("/");
		this.verifyTrue(this.selenium.isTextPresent("8 passed, 0 failed"));
	}

	@Override
	@After
	public void tearDown()
	{
		try
		{
			super.tearDown();
		}
		catch (final Exception e)
		{
			VerifyQUnitTests.LOGGER.error("error in VerifyQUnitTests#tearDown()!", e);
		}
		this.selenium.stop();
	}

}
