package org.alienlabs.hatchetharry.clientSideTest;

import org.alienlabs.hatchetharry.view.page.HomePage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.util.tester.WicketTester;
import org.junit.Assert;
import org.junit.Test;

public class BasicClockTest
{

	private WicketTester tester;

	@Test
	public void clockShouldAppearAndDisplaySomethingThenDisplaySomethingDifferentAfter10SecondsTest()
	{
		this.tester = new WicketTester(HomePage.class);
		this.tester.startPage(HomePage.class);

		this.tester.assertComponent("clockPanel:clock", Label.class);
		final Label clockBefore = (Label)this.tester
				.getComponentFromLastRenderedPage("clockPanel:clock");
		final String before = clockBefore.getDefaultModelObjectAsString();
		Assert.assertFalse(before.equals(""));

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
		Assert.assertFalse(before.equals(""));

		final String after = clockAfter.getDefaultModelObjectAsString();
		Assert.assertFalse(before.equals(after));
	}

}
