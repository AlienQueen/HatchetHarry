package org.alienlabs.hatchetharry.clientSideTest;

import java.io.Serializable;

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

public class BasicClockTest implements Serializable
{
	private transient WicketTester tester;
	private HatchetHarryApplication webApp;

	private static final long serialVersionUID = 1L;

	@Before
	public void setUp()
	{
		this.webApp = new HatchetHarryApplication()
		{
			private static final long serialVersionUID = 1L;
			// note in this case the application context is in the default
			// package
			transient ApplicationContext context = new ClassPathXmlApplicationContext(
					new String[] { "applicationContext.xml" });

			@Override
			public void init()
			{
				this.getComponentInstantiationListeners().add(
						new SpringComponentInjector(this, this.context, true));
				this.setMistletoeTest(true);
			}
		};
		this.tester = new WicketTester(this.webApp);
		final ApplicationContext context = new ClassPathXmlApplicationContext(
				new String[] { "applicationContext.xml" });
		this.tester.getApplication().getComponentInstantiationListeners()
				.add(new SpringComponentInjector(this.tester.getApplication(), context, true));
	}

	@Test
	public void clockShouldAppearAndDisplaySomethingThenDisplaySomethingDifferentAfter10SecondsTest()
	{
		this.tester.startPage(HomePage.class);

		this.tester.assertComponent("clockPanel:clock", Label.class);
		final Label clockBefore = (Label)this.tester
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

		final Label clockAfter = (Label)this.tester
				.getComponentFromLastRenderedPage("clockPanel:clock");

		final String after = clockAfter.getDefaultModelObjectAsString();
		Assert.assertFalse(before.equals(after));
	}

}
