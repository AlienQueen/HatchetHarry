package org.alienlabs.hatchetharry.clientSideTest;

import org.alienlabs.hatchetharry.HatchetHarryApplication;
import org.alienlabs.hatchetharry.view.page.HomePage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.spring.injection.annot.SpringComponentInjector;
import org.apache.wicket.util.tester.WicketTester;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class BasicClockTest
{
	private static WicketTester tester;
	private static HatchetHarryApplication webApp;

	@Before
	public void setUp()
	{
		BasicClockTest.webApp = new HatchetHarryApplication()
		{
			private static final long serialVersionUID = 1L;
			// note in this case the application context is in the default
			// package
			ApplicationContext context = new ClassPathXmlApplicationContext(
					new String[] { "applicationContext.xml" });

			@Override
			public void init()
			{
				this.addComponentInstantiationListener(new SpringComponentInjector(this,
						this.context, true));
				this.setMistletoeTest(true);
			}
		};
		BasicClockTest.tester = new WicketTester(BasicClockTest.webApp);
		final ApplicationContext context = new ClassPathXmlApplicationContext(
				new String[] { "applicationContext.xml" });
		BasicClockTest.tester.getApplication().addComponentInstantiationListener(
				new SpringComponentInjector(BasicClockTest.tester.getApplication(), context, true));
	}

	@Test
	public void clockShouldAppearAndDisplaySomethingThenDisplaySomethingDifferentAfter10SecondsTest()
	{
		BasicClockTest.tester.startPage(HomePage.class);

		BasicClockTest.tester.assertComponent("clockPanel:clock", Label.class);
		final Label clockBefore = (Label)BasicClockTest.tester
				.getComponentFromLastRenderedPage("clockPanel:clock");
		final String before = clockBefore.getDefaultModelObjectAsString();
		Assert.assertFalse("".equals("before"));

		try
		{
			Thread.sleep(10000);
		}
		catch (final InterruptedException e)
		{
			e.printStackTrace();
		}

		final Label clockAfter = (Label)BasicClockTest.tester
				.getComponentFromLastRenderedPage("clockPanel:clock");

		final String after = clockAfter.getDefaultModelObjectAsString();
		Assert.assertFalse(before.equals(after));
	}

}
