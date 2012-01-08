package org.alienlabs.hatchetharry.integrationTest;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.thoughtworks.selenium.DefaultSelenium;
import com.thoughtworks.selenium.SeleneseTestCase;

public class VerifyQUnitTests extends SeleneseTestCase
{
	@Override
	@Before
	public void setUp()
	{
		this.selenium = new DefaultSelenium("localhost", 4444, "*firefox", "http://localhost:8080/");
		this.selenium.start();
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
		this.selenium.stop();
	}

}
