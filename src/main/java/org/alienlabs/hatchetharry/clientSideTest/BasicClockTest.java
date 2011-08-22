package org.alienlabs.hatchetharry.clientSideTest;

import org.alienlabs.hatchetharry.view.page.HomePage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.util.tester.WicketTester;
import org.junit.Assert;
import org.junit.Test;

public class BasicClockTest
{

	private static WicketTester tester;

	@Test
	public void clockShouldAppearAndDisplaySomethingThenDisplaySomethingDifferentAfter10SecondsTest()
	{
		BasicClockTest.tester = new WicketTester(HomePage.class);
		BasicClockTest.tester.startPage(HomePage.class);

		BasicClockTest.tester.assertComponent("clockPanel:clock", Label.class);
		final Label clockBefore = (Label)BasicClockTest.tester
				.getComponentFromLastRenderedPage("clockPanel:clock");
		final String before = clockBefore.getDefaultModelObjectAsString();
		Assert.assertFalse(before.equals(""));

		try
		{
			Thread.sleep(3000);
		}
		catch (final InterruptedException e)
		{
			e.printStackTrace();
		}

		final Label clockAfter = (Label)BasicClockTest.tester
				.getComponentFromLastRenderedPage("clockPanel:clock");
		Assert.assertFalse(before.equals(""));

		final String after = clockAfter.getDefaultModelObjectAsString();
		Assert.assertFalse(before.equals(after));
	}

}
