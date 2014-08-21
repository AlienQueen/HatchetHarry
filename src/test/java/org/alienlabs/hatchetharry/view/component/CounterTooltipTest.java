package org.alienlabs.hatchetharry.view.component;

import org.alienlabs.hatchetharry.serverSideTest.util.SpringContextLoaderBaseTest;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.util.tester.FormTester;
import org.junit.Test;

import static org.junit.Assert.fail;


public class CounterTooltipTest extends SpringContextLoaderBaseTest
{
	@Test
	public void testCardTooltip()
	{
		// Start a game and play a card
		super.startAGameAndPlayACard(SpringContextLoaderBaseTest.tester,
			SpringContextLoaderBaseTest.CLASS_PATH_XML_APPLICATION_CONTEXT);
		SpringContextLoaderBaseTest.waTester.switchOffTestMode();

		// Assert it's a card and not a token
		SpringContextLoaderBaseTest.tester.assertComponent(
			"parentPlaceholder:tooltips:1:cardTooltip", MagicCardTooltipPanel.class);

		// Put a blah counter
		final FormTester form1 = SpringContextLoaderBaseTest.tester
			.newFormTester("parentPlaceholder:tooltips:1:cardTooltip:counterPanel:form");
		form1.setValue("counterAddName", "blah");
		form1.submit("submit");

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

		SpringContextLoaderBaseTest.tester
			.assertLabel(
				"parentPlaceholder:tooltips:3:cardTooltip:counterPanel:counters:1:setCounterForm:counterName",
				"charge");
		SpringContextLoaderBaseTest.tester
			.assertLabel(
				"parentPlaceholder:tooltips:3:cardTooltip:counterPanel:counters:1:setCounterForm:numberOfCounters",
				"1");

		// Create a token
		SpringContextLoaderBaseTest.tester.assertComponent("createTokenLink", AjaxLink.class);
		SpringContextLoaderBaseTest.tester.clickLink("createTokenLink", true);
		final FormTester form3 = SpringContextLoaderBaseTest.tester
			.newFormTester("createTokenWindow:content:form");
		form3.setValue("type", "rat");
		form3.submit("createToken");

		// Assert token on battlefield
		SpringContextLoaderBaseTest.tester.assertComponent(
			"parentPlaceholder:magicCards:2:cardPanel:cardHandle", WebMarkupContainer.class);
		SpringContextLoaderBaseTest.tester.assertComponent(
			"parentPlaceholder:tooltips:4:cardTooltip", TokenTooltipPanel.class);


		// Add a poison counter to token
		final FormTester form4 = SpringContextLoaderBaseTest.tester
			.newFormTester("parentPlaceholder:tooltips:4:cardTooltip:counterPanel:form");
		form4.setValue("counterAddName", "poison");
		form4.submit("submit");

		SpringContextLoaderBaseTest.tester
			.assertLabel(
				"parentPlaceholder:tooltips:5:cardTooltip:counterPanel:counters:0:setCounterForm:counterName",
				"poison");
		SpringContextLoaderBaseTest.tester
			.assertLabel(
				"parentPlaceholder:tooltips:5:cardTooltip:counterPanel:counters:0:setCounterForm:numberOfCounters",
				"1");

		// Add 1 more blah counter to card
		SpringContextLoaderBaseTest.tester
			.clickLink(
				"parentPlaceholder:tooltips:3:cardTooltip:counterPanel:counters:0:setCounterForm:addCounterLink",
				true);

		// Assert that there are 2 blah counters on card
		SpringContextLoaderBaseTest.tester
			.assertLabel(
				"parentPlaceholder:tooltips:6:cardTooltip:counterPanel:counters:0:setCounterForm:counterName",
				"blah");
		SpringContextLoaderBaseTest.tester
			.assertLabel(
				"parentPlaceholder:tooltips:6:cardTooltip:counterPanel:counters:0:setCounterForm:numberOfCounters",
				"2");

		// Put 10 charge counters on card
		final FormTester form5 = SpringContextLoaderBaseTest.tester
			.newFormTester("parentPlaceholder:tooltips:6:cardTooltip:counterPanel:counters:1:setCounterForm");
		form5.setValue("setCounterButton", "10");
		form5.submit("setCounterSubmit");

		// Assert that there are 10 charge counters on card
		SpringContextLoaderBaseTest.tester
			.assertLabel(
				"parentPlaceholder:tooltips:7:cardTooltip:counterPanel:counters:1:setCounterForm:counterName",
				"charge");
		SpringContextLoaderBaseTest.tester
                .assertLabel(
				"parentPlaceholder:tooltips:7:cardTooltip:counterPanel:counters:1:setCounterForm:numberOfCounters",
				"10");

		// Remove a charge counter on card
		SpringContextLoaderBaseTest.tester
			.clickLink(
				"parentPlaceholder:tooltips:7:cardTooltip:counterPanel:counters:1:setCounterForm:removeCounterLink",
				true);

		// Assert that there are 9 charge counters on card
		SpringContextLoaderBaseTest.tester
			.assertLabel(
				"parentPlaceholder:tooltips:8:cardTooltip:counterPanel:counters:1:setCounterForm:counterName",
				"charge");
		SpringContextLoaderBaseTest.tester
			.assertLabel(
				"parentPlaceholder:tooltips:8:cardTooltip:counterPanel:counters:1:setCounterForm:numberOfCounters",
				"9");

		// Remove all charge counters from card
		final FormTester form6 = SpringContextLoaderBaseTest.tester
			.newFormTester("parentPlaceholder:tooltips:8:cardTooltip:counterPanel:counters:1:setCounterForm");
		form6.setValue("setCounterButton", "0");
		form6.submit("setCounterSubmit");

		// Assert that there are no more charge counters on card
		try
		{
			SpringContextLoaderBaseTest.tester
				.assertLabel(
					"parentPlaceholder:tooltips:9:cardTooltip:counterPanel:counters:1:setCounterForm:counterName",
					"charge");
			fail();
		}
		catch (AssertionError e)
		{
			// Success
		}

		// Remove a blah counter from card
		SpringContextLoaderBaseTest.tester
			.clickLink(
				"parentPlaceholder:tooltips:9:cardTooltip:counterPanel:counters:0:setCounterForm:removeCounterLink",
				true);

		// Assert that there are 1 blah counter on card
		SpringContextLoaderBaseTest.tester
			.assertLabel(
				"parentPlaceholder:tooltips:10:cardTooltip:counterPanel:counters:0:setCounterForm:counterName",
				"blah");
		SpringContextLoaderBaseTest.tester
			.assertLabel(
				"parentPlaceholder:tooltips:10:cardTooltip:counterPanel:counters:0:setCounterForm:numberOfCounters",
				"1");

		// Remove another blah counter from card
		SpringContextLoaderBaseTest.tester
			.clickLink(
				"parentPlaceholder:tooltips:10:cardTooltip:counterPanel:counters:0:setCounterForm:removeCounterLink",
				true);

		// Assert that there are no more blah counters on card
		try
		{
			SpringContextLoaderBaseTest.tester
				.assertLabel(
					"parentPlaceholder:tooltips:11:cardTooltip:counterPanel:counters:0:setCounterForm:counterName",
                        "blah");
			fail();
		}
		catch (AssertionError e)
		{
			// Success
		}
	}

}
