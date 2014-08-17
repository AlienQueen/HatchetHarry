package org.alienlabs.hatchetharry.view.component;

import org.alienlabs.hatchetharry.serverSideTest.util.SpringContextLoaderBaseTest;
import org.apache.wicket.util.tester.FormTester;
import org.junit.Test;


public class CounterTooltipTest extends SpringContextLoaderBaseTest
{

	@Test
	public void testCardTooltip()
	{
		// Start a game and play a card
		super.startAGameAndPlayACard(SpringContextLoaderBaseTest.tester,
				SpringContextLoaderBaseTest.CLASS_PATH_XML_APPLICATION_CONTEXT);

		String atmosphereResponse = SpringContextLoaderBaseTest.waTester.getPushedResponse();
		SpringContextLoaderBaseTest.waTester.switchOnTestMode();

		// Put a blah counter
		final FormTester form1 = SpringContextLoaderBaseTest.tester
				.newFormTester("parentPlaceholder:tooltips:1:cardTooltip:counterPanel:form");
		form1.setValue("counterAddName", "blah");
		form1.submit("submit");

		atmosphereResponse = SpringContextLoaderBaseTest.waTester.getPushedResponse();
		SpringContextLoaderBaseTest.waTester.switchOnTestMode();

		SpringContextLoaderBaseTest.tester
				.assertLabel(
						"parentPlaceholder:tooltips:2:cardTooltip:counterPanel:counters:0:setCounterForm:counterName",
						"blah");
		SpringContextLoaderBaseTest.tester
				.assertLabel(
						"parentPlaceholder:tooltips:2:cardTooltip:counterPanel:counters:0:setCounterForm:numberOfCounters",
						"1");

		// Put a charge counter
		final FormTester form2 = SpringContextLoaderBaseTest.tester
				.newFormTester("parentPlaceholder:tooltips:2:cardTooltip:counterPanel:form");
		form2.setValue("counterAddName", "charge");
		form2.submit("submit");

		atmosphereResponse = SpringContextLoaderBaseTest.waTester.getPushedResponse();
		SpringContextLoaderBaseTest.waTester.switchOnTestMode();

		SpringContextLoaderBaseTest.tester
				.assertLabel(
						"parentPlaceholder:tooltips:3:cardTooltip:counterPanel:counters:1:setCounterForm:counterName",
						"charge");
		SpringContextLoaderBaseTest.tester
				.assertLabel(
						"parentPlaceholder:tooltips:3:cardTooltip:counterPanel:counters:1:setCounterForm:numberOfCounters",
						"1");

		// Add 1 more blah counter
		SpringContextLoaderBaseTest.tester
				.clickLink(
						"parentPlaceholder:tooltips:3:cardTooltip:counterPanel:counters:0:setCounterForm:addCounterLink",
						true);

		// Assert that there are 2 blah counters
		SpringContextLoaderBaseTest.tester
				.assertLabel(
						"parentPlaceholder:tooltips:4:cardTooltip:counterPanel:counters:0:setCounterForm:counterName",
						"blah");
		SpringContextLoaderBaseTest.tester
				.assertLabel(
						"parentPlaceholder:tooltips:4:cardTooltip:counterPanel:counters:0:setCounterForm:numberOfCounters",
						"2");

		// Put 10 charge counters
		final FormTester form3 = SpringContextLoaderBaseTest.tester
				.newFormTester("parentPlaceholder:tooltips:4:cardTooltip:counterPanel:counters:1:setCounterForm");
		form3.setValue("setCounterButton", "10");
		form3.submit("setCounterSubmit");

		atmosphereResponse = SpringContextLoaderBaseTest.waTester.getPushedResponse();
		SpringContextLoaderBaseTest.waTester.switchOnTestMode();

		// Assert that there are 10 charge counters
		SpringContextLoaderBaseTest.tester
				.assertLabel(
						"parentPlaceholder:tooltips:5:cardTooltip:counterPanel:counters:1:setCounterForm:counterName",
						"charge");
		SpringContextLoaderBaseTest.tester
				.assertLabel(
						"parentPlaceholder:tooltips:5:cardTooltip:counterPanel:counters:1:setCounterForm:numberOfCounters",
						"10");
	}

}
