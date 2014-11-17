package org.alienlabs.hatchetharry.view.component.card;

import org.alienlabs.hatchetharry.serverSideTest.util.SpringContextLoaderBaseTest;
import org.alienlabs.hatchetharry.view.page.HomePage;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.util.tester.FormTester;
import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;

@ContextConfiguration(locations = { "classpath:applicationContext.xml",
		"classpath:applicationContextTest.xml" })
public class CounterTooltipTest extends SpringContextLoaderBaseTest
{
	@Test
	public void testCardTooltip() throws Exception
	{
		// Start a game and play a card
		super.startAGameAndPlayACard("displayTooltips", "true");
		SpringContextLoaderBaseTest.tester.startPage(HomePage.class);
		SpringContextLoaderBaseTest.tester.assertRenderedPage(HomePage.class);

		// Assert it's a card and not a token
		super.tester.assertComponent("parentPlaceholder:magicCardsForSide1:1:cardPanel:tooltip",
				MagicCardTooltipPanel.class);

		// Put a blah counter
		final FormTester form1 = super.tester
				.newFormTester("parentPlaceholder:magicCardsForSide1:1:cardPanel:tooltip:counterPanel:form");
		form1.setValue("counterAddName", "blah");
		form1.submit("submit");

		SpringContextLoaderBaseTest.tester.startPage(HomePage.class);
		SpringContextLoaderBaseTest.tester.assertRenderedPage(HomePage.class);

		super.tester
				.assertLabel(
						"parentPlaceholder:magicCardsForSide1:1:cardPanel:tooltip:counterPanel:counters:0:setCounterForm:counterName",
						"blah");
		super.tester
				.assertLabel(
						"parentPlaceholder:magicCardsForSide1:1:cardPanel:tooltip:counterPanel:counters:0:setCounterForm:numberOfCounters",
						"1");

		// Put a charge counter
		final FormTester form2 = super.tester
				.newFormTester("parentPlaceholder:magicCardsForSide1:1:cardPanel:tooltip:counterPanel:form");
		form2.setValue("counterAddName", "charge");
		form2.submit("submit");

		HomePage hp = SpringContextLoaderBaseTest.tester.startPage(HomePage.class);
		SpringContextLoaderBaseTest.tester.assertRenderedPage(HomePage.class);

		super.tester
				.assertLabel(
						"parentPlaceholder:magicCardsForSide1:1:cardPanel:tooltip:counterPanel:counters:1:setCounterForm:counterName",
						"charge");
		super.tester
				.assertLabel(
						"parentPlaceholder:magicCardsForSide1:1:cardPanel:tooltip:counterPanel:counters:1:setCounterForm:numberOfCounters",
						"1");

		// Create a token
		super.tester.assertComponent("createTokenLink", AjaxLink.class);
		super.tester.clickLink("createTokenLink", true);

		tester.assertComponentOnAjaxResponse(hp.getCreateTokenModalWindow());
		final ModalWindow window = hp.getCreateTokenModalWindow();
		SpringContextLoaderBaseTest.tester.assertVisible(window.getPageRelativePath() + ":"
				+ window.getContentId());

		super.tester.assertComponent(window.getPageRelativePath() + ":" + window.getContentId()
				+ ":form", Form.class);
		final FormTester form3 = super.tester.newFormTester(window.getPageRelativePath() + ":"
				+ window.getContentId() + ":form");
		form3.setValue("type", "rat");
		form3.setValue("creatureTypes", "rat");
		form3.submit("createToken");

		SpringContextLoaderBaseTest.tester.startPage(HomePage.class);
		SpringContextLoaderBaseTest.tester.assertRenderedPage(HomePage.class);

		// Assert token on battlefield
		super.tester.assertComponent(
				"parentPlaceholder:magicCardsForSide1:1:cardPanel:tooltip:counterPanel",
				CounterTooltip.class);

		// Add a poison counter to token
		final FormTester form4 = super.tester
				.newFormTester("parentPlaceholder:magicCardsForSide1:1:cardPanel:tooltip:counterPanel:form");
		form4.setValue("counterAddName", "poison");
		form4.submit("submit");

		SpringContextLoaderBaseTest.tester.startPage(HomePage.class);
		SpringContextLoaderBaseTest.tester.assertRenderedPage(HomePage.class);

		super.tester
				.assertLabel(
						"parentPlaceholder:magicCardsForSide1:1:cardPanel:tooltip:counterPanel:counters:2:setCounterForm:counterName",
						"poison");
		super.tester
				.assertLabel(
						"parentPlaceholder:magicCardsForSide1:1:cardPanel:tooltip:counterPanel:counters:2:setCounterForm:numberOfCounters",
						"1");

		// Add 1 more poison counter to token
		super.tester
				.clickLink(
						"parentPlaceholder:magicCardsForSide1:1:cardPanel:tooltip:counterPanel:counters:2:setCounterForm:addCounterLink",
						true);

		SpringContextLoaderBaseTest.tester.startPage(HomePage.class);
		SpringContextLoaderBaseTest.tester.assertRenderedPage(HomePage.class);

		// Assert that there are 2 poison counters on token
		super.tester
				.assertLabel(
						"parentPlaceholder:magicCardsForSide1:1:cardPanel:tooltip:counterPanel:counters:2:setCounterForm:counterName",
						"poison");
		super.tester
				.assertLabel(
						"parentPlaceholder:magicCardsForSide1:1:cardPanel:tooltip:counterPanel:counters:2:setCounterForm:numberOfCounters",
						"2");

		// Put 10 poison counters on card
		final FormTester form5 = super.tester
				.newFormTester("parentPlaceholder:magicCardsForSide1:1:cardPanel:tooltip:counterPanel:counters:2:setCounterForm");
		form5.setValue("setCounterButton", "10");
		form5.submit("setCounterSubmit");

		SpringContextLoaderBaseTest.tester.startPage(HomePage.class);
		SpringContextLoaderBaseTest.tester.assertRenderedPage(HomePage.class);
		// Assert that there are 10 poison counters on token
		super.tester
				.assertLabel(
						"parentPlaceholder:magicCardsForSide1:1:cardPanel:tooltip:counterPanel:counters:2:setCounterForm:counterName",
						"poison");
		super.tester
				.assertLabel(
						"parentPlaceholder:magicCardsForSide1:1:cardPanel:tooltip:counterPanel:counters:2:setCounterForm:numberOfCounters",
						"10");

		// Remove a poison counter on token
		super.tester
				.clickLink(
						"parentPlaceholder:magicCardsForSide1:1:cardPanel:tooltip:counterPanel:counters:2:setCounterForm:removeCounterLink",
						true);

		SpringContextLoaderBaseTest.tester.startPage(HomePage.class);
		SpringContextLoaderBaseTest.tester.assertRenderedPage(HomePage.class);

		// Assert that there are 9 poison counters on token
		super.tester
				.assertLabel(
						"parentPlaceholder:magicCardsForSide1:1:cardPanel:tooltip:counterPanel:counters:2:setCounterForm:counterName",
						"poison");
		super.tester
				.assertLabel(
						"parentPlaceholder:magicCardsForSide1:1:cardPanel:tooltip:counterPanel:counters:2:setCounterForm:numberOfCounters",
						"9");

		// Add a blah counter to token
		final FormTester form6 = super.tester
				.newFormTester("parentPlaceholder:magicCardsForSide1:1:cardPanel:tooltip:counterPanel:form");
		form6.setValue("counterAddName", "blah");
		form6.submit("submit");

		SpringContextLoaderBaseTest.tester.startPage(HomePage.class);
		SpringContextLoaderBaseTest.tester.assertRenderedPage(HomePage.class);

		// Verify that there is one blah counter on token, don't forget that the
		// counters appear in alphabetic order
		super.tester
				.assertLabel(
						"parentPlaceholder:magicCardsForSide1:1:cardPanel:tooltip:counterPanel:counters:1:setCounterForm:counterName",
						"blah");
		super.tester
				.assertLabel(
						"parentPlaceholder:magicCardsForSide1:1:cardPanel:tooltip:counterPanel:counters:1:setCounterForm:numberOfCounters",
						"1");

		// Add 1 more blah counter to token
		super.tester
				.clickLink(
						"parentPlaceholder:magicCardsForSide1:1:cardPanel:tooltip:counterPanel:counters:1:setCounterForm:addCounterLink",
						true);

		SpringContextLoaderBaseTest.tester.startPage(HomePage.class);
		SpringContextLoaderBaseTest.tester.assertRenderedPage(HomePage.class);

		// Verify that there is two blah counters on token
		super.tester
				.assertLabel(
						"parentPlaceholder:magicCardsForSide1:1:cardPanel:tooltip:counterPanel:counters:1:setCounterForm:counterName",
						"blah");
		super.tester
				.assertLabel(
						"parentPlaceholder:magicCardsForSide1:1:cardPanel:tooltip:counterPanel:counters:1:setCounterForm:numberOfCounters",
						"2");

		SpringContextLoaderBaseTest.tester.startPage(HomePage.class);
		SpringContextLoaderBaseTest.tester.assertRenderedPage(HomePage.class);


		// Remove all poison counters from token
		final FormTester form7 = super.tester
				.newFormTester("parentPlaceholder:magicCardsForSide1:1:cardPanel:tooltip:counterPanel:counters:1:setCounterForm");
		form7.setValue("setCounterButton", "1");
		form7.submit("setCounterSubmit");

		SpringContextLoaderBaseTest.tester.startPage(HomePage.class);
		SpringContextLoaderBaseTest.tester.assertRenderedPage(HomePage.class);

		// Assert that there are no more poison counters on card
		super.tester
				.assertLabel(
						"parentPlaceholder:magicCardsForSide1:1:cardPanel:tooltip:counterPanel:counters:1:setCounterForm:counterName",
						"blah");

		// Remove a blah counter from card
		super.tester
				.clickLink(
						"parentPlaceholder:magicCardsForSide1:1:cardPanel:tooltip:counterPanel:counters:1:setCounterForm:removeCounterLink",
						true);

		SpringContextLoaderBaseTest.tester.startPage(HomePage.class);
		SpringContextLoaderBaseTest.tester.assertRenderedPage(HomePage.class);

		// Assert that there are 1 blah counter on card
		super.tester
				.assertLabel(
						"parentPlaceholder:magicCardsForSide1:1:cardPanel:tooltip:counterPanel:counters:0:setCounterForm:counterName",
						"blah");
		super.tester
				.assertLabel(
						"parentPlaceholder:magicCardsForSide1:1:cardPanel:tooltip:counterPanel:counters:0:setCounterForm:numberOfCounters",
						"1");
	}

}
