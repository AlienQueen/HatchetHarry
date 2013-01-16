package org.alienlabs.hatchetharry.integrationTest;


//public class VerifyQUnitTests extends SeleneseTestCase
//{
//	Selenium selenium;
//
//	@Override
//	@Before
//	public void setUp()
//	{
//		try
//		{
//			super.setUp();
//			this.selenium = new DefaultSelenium("localhost", 4444, "*firefox",
//					"http://localhost:8080/");
//			this.selenium.start();
//		}
//		catch (final Exception e)
//		{
//			e.printStackTrace();
//		}
//	}
//
//	@Test
//	public void testQunit()
//	{
//		this.selenium.open("/");
//		this.verifyTrue(this.selenium.isTextPresent("8 passed, 0 failed"));
//	}
//
//	@Override
//	@After
//	public void tearDown()
//	{
//		try
//		{
//			super.tearDown();
//		}
//		catch (final Exception e)
//		{
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		this.selenium.stop();
//	}
//
// }
