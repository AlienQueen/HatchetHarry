package org.alienlabs.hatchetharry.view.component.card;

import org.alienlabs.hatchetharry.serverSideTest.util.SpringContextLoaderBaseTest;
import org.alienlabs.hatchetharry.view.page.HomePage;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.util.tester.FormTester;
import org.junit.Assert;
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
		SpringContextLoaderBaseTest.tester.assertComponent(
				"parentPlaceholder:magicCardsForSide1:1:cardPanel:tooltip",
				MagicCardTooltipPanel.class);

		// Put a blah counter
		FormTester form = SpringContextLoaderBaseTest.tester
				.newFormTester("parentPlaceholder:magicCardsForSide1:1:cardPanel:tooltip:counterPanel:form");
		form.setValue("counterAddName", "blah");
		SpringContextLoaderBaseTest.tester
				.executeAjaxEvent(
						"parentPlaceholder:magicCardsForSide1:1:cardPanel:tooltip:counterPanel:form:submit",
						"onclick");

		SpringContextLoaderBaseTest.tester.startPage(HomePage.class);
		SpringContextLoaderBaseTest.tester.assertRenderedPage(HomePage.class);

		// Assert that there is 1 blah counter on card
		SpringContextLoaderBaseTest.tester
				.assertLabel(
						"parentPlaceholder:magicCardsForSide1:1:cardPanel:tooltip:counterPanel:counters:0:setCounterForm:counterName",
						"blah");
		SpringContextLoaderBaseTest.tester
				.assertLabel(
						"parentPlaceholder:magicCardsForSide1:1:cardPanel:tooltip:counterPanel:counters:0:setCounterForm:numberOfCounters",
						"1");

		// Put a charge counter
		form = SpringContextLoaderBaseTest.tester
				.newFormTester("parentPlaceholder:magicCardsForSide1:1:cardPanel:tooltip:counterPanel:form");
		form.setValue("counterAddName", "charge");
		SpringContextLoaderBaseTest.tester
				.executeAjaxEvent(
						"parentPlaceholder:magicCardsForSide1:1:cardPanel:tooltip:counterPanel:form:submit",
						"onclick");

		HomePage hp = SpringContextLoaderBaseTest.tester.startPage(HomePage.class);
		SpringContextLoaderBaseTest.tester.assertRenderedPage(HomePage.class);

		// Assert that there is 1 charge counter on card
		SpringContextLoaderBaseTest.tester
				.assertLabel(
						"parentPlaceholder:magicCardsForSide1:1:cardPanel:tooltip:counterPanel:counters:1:setCounterForm:counterName",
						"charge");
		SpringContextLoaderBaseTest.tester
				.assertLabel(
						"parentPlaceholder:magicCardsForSide1:1:cardPanel:tooltip:counterPanel:counters:1:setCounterForm:numberOfCounters",
						"1");

		// Add 1 more blah counter to card
		SpringContextLoaderBaseTest.tester
				.clickLink(
						"parentPlaceholder:magicCardsForSide1:1:cardPanel:tooltip:counterPanel:counters:0:setCounterForm:addCounterLink",
						true);

		SpringContextLoaderBaseTest.tester.startPage(HomePage.class);
		SpringContextLoaderBaseTest.tester.assertRenderedPage(HomePage.class);

		// Assert that there are 2 blah counters on card
		SpringContextLoaderBaseTest.tester
				.assertLabel(
						"parentPlaceholder:magicCardsForSide1:1:cardPanel:tooltip:counterPanel:counters:0:setCounterForm:counterName",
						"blah");
		SpringContextLoaderBaseTest.tester
				.assertLabel(
						"parentPlaceholder:magicCardsForSide1:1:cardPanel:tooltip:counterPanel:counters:0:setCounterForm:numberOfCounters",
						"2");

		// Put 10 blah counters on card
		form = SpringContextLoaderBaseTest.tester
				.newFormTester("parentPlaceholder:magicCardsForSide1:1:cardPanel:tooltip:counterPanel:counters:0:setCounterForm");
		form.setValue("setCounterButton", "10");
		SpringContextLoaderBaseTest.tester
				.executeAjaxEvent(
						"parentPlaceholder:magicCardsForSide1:1:cardPanel:tooltip:counterPanel:counters:0:setCounterForm:setCounterSubmit",
						"onclick");

		SpringContextLoaderBaseTest.tester.startPage(HomePage.class);
		SpringContextLoaderBaseTest.tester.assertRenderedPage(HomePage.class);

		// Assert that there are 10 blah counters on card
		SpringContextLoaderBaseTest.tester
				.assertLabel(
						"parentPlaceholder:magicCardsForSide1:1:cardPanel:tooltip:counterPanel:counters:0:setCounterForm:counterName",
						"blah");
		SpringContextLoaderBaseTest.tester
				.assertLabel(
						"parentPlaceholder:magicCardsForSide1:1:cardPanel:tooltip:counterPanel:counters:0:setCounterForm:numberOfCounters",
						"10");

		// Remove a blah counter on card
		SpringContextLoaderBaseTest.tester
				.clickLink(
						"parentPlaceholder:magicCardsForSide1:1:cardPanel:tooltip:counterPanel:counters:0:setCounterForm:removeCounterLink",
						true);

		SpringContextLoaderBaseTest.tester.startPage(HomePage.class);
		SpringContextLoaderBaseTest.tester.assertRenderedPage(HomePage.class);

		// Assert that there are 9 blah counters on card
		SpringContextLoaderBaseTest.tester
				.assertLabel(
						"parentPlaceholder:magicCardsForSide1:1:cardPanel:tooltip:counterPanel:counters:0:setCounterForm:counterName",
						"blah");
		SpringContextLoaderBaseTest.tester
				.assertLabel(
						"parentPlaceholder:magicCardsForSide1:1:cardPanel:tooltip:counterPanel:counters:0:setCounterForm:numberOfCounters",
						"9");

		// Remove all blah counters from card
		form = SpringContextLoaderBaseTest.tester
				.newFormTester("parentPlaceholder:magicCardsForSide1:1:cardPanel:tooltip:counterPanel:counters:0:setCounterForm");
		form.setValue("setCounterButton", "0");
		SpringContextLoaderBaseTest.tester
				.executeAjaxEvent(
						"parentPlaceholder:magicCardsForSide1:1:cardPanel:tooltip:counterPanel:counters:0:setCounterForm:setCounterSubmit",
						"onclick");

		// Assert that there are no more blah counters on card
		SpringContextLoaderBaseTest.waTester.switchOffTestMode();
		String pageDocument = SpringContextLoaderBaseTest.waTester.getPushedResponse();
		Assert.assertTrue(pageDocument
				.contains("infrared has cleared 'blah' counters on infrared's card or token"));

		SpringContextLoaderBaseTest.tester.startPage(HomePage.class);
		SpringContextLoaderBaseTest.tester.assertRenderedPage(HomePage.class);

		// Put a blah counter again
		form = SpringContextLoaderBaseTest.tester
				.newFormTester("parentPlaceholder:magicCardsForSide1:1:cardPanel:tooltip:counterPanel:form");
		form.setValue("counterAddName", "blah");
		SpringContextLoaderBaseTest.tester
				.executeAjaxEvent(
						"parentPlaceholder:magicCardsForSide1:1:cardPanel:tooltip:counterPanel:form:submit",
						"onclick");

		SpringContextLoaderBaseTest.tester.startPage(HomePage.class);
		SpringContextLoaderBaseTest.tester.assertRenderedPage(HomePage.class);

		// Assert that there is 1 blah counter on card
		SpringContextLoaderBaseTest.tester
				.assertLabel(
						"parentPlaceholder:magicCardsForSide1:1:cardPanel:tooltip:counterPanel:counters:0:setCounterForm:counterName",
						"blah");
		SpringContextLoaderBaseTest.tester
				.assertLabel(
						"parentPlaceholder:magicCardsForSide1:1:cardPanel:tooltip:counterPanel:counters:0:setCounterForm:numberOfCounters",
						"1");

		// Add 1 more blah counter to card
		SpringContextLoaderBaseTest.tester
				.clickLink(
						"parentPlaceholder:magicCardsForSide1:1:cardPanel:tooltip:counterPanel:counters:0:setCounterForm:addCounterLink",
						true);

		hp = SpringContextLoaderBaseTest.tester.startPage(HomePage.class);
		SpringContextLoaderBaseTest.tester.assertRenderedPage(HomePage.class);

		// Assert that there are 2 blah counters on card
		SpringContextLoaderBaseTest.tester
				.assertLabel(
						"parentPlaceholder:magicCardsForSide1:1:cardPanel:tooltip:counterPanel:counters:0:setCounterForm:counterName",
						"blah");
		SpringContextLoaderBaseTest.tester
				.assertLabel(
						"parentPlaceholder:magicCardsForSide1:1:cardPanel:tooltip:counterPanel:counters:0:setCounterForm:numberOfCounters",
						"2");

		// Create a token
		SpringContextLoaderBaseTest.tester.assertComponent("createTokenLink", AjaxLink.class);
		SpringContextLoaderBaseTest.tester.clickLink("createTokenLink", true);

		tester.assertComponentOnAjaxResponse(hp.getCreateTokenModalWindow());
		final ModalWindow window = hp.getCreateTokenModalWindow();
		SpringContextLoaderBaseTest.tester.assertVisible(window.getPageRelativePath() + ":"
				+ window.getContentId());

		SpringContextLoaderBaseTest.tester.assertComponent(window.getPageRelativePath() + ":"
				+ window.getContentId() + ":form", Form.class);
		final FormTester form4 = SpringContextLoaderBaseTest.tester.newFormTester(window
				.getPageRelativePath() + ":" + window.getContentId() + ":form");
		form4.setValue("type", "rat");
		form4.setValue("creatureTypes", "rat");
		tester.executeAjaxEvent(window.getPageRelativePath() + ":" + window.getContentId()
				+ ":form:createToken", "onclick");

		SpringContextLoaderBaseTest.tester.startPage(HomePage.class);
		SpringContextLoaderBaseTest.tester.assertRenderedPage(HomePage.class);

		// Assert token on battlefield
		SpringContextLoaderBaseTest.tester.assertComponent(
				"parentPlaceholder:magicCardsForSide1:2:cardPanel:tooltip:counterPanel",
				CounterTooltip.class);


		// Add a poison counter to token
		form = SpringContextLoaderBaseTest.tester
				.newFormTester("parentPlaceholder:magicCardsForSide1:2:cardPanel:tooltip:counterPanel:form");
		form.setValue("counterAddName", "poison");
		SpringContextLoaderBaseTest.tester
				.executeAjaxEvent(
						"parentPlaceholder:magicCardsForSide1:2:cardPanel:tooltip:counterPanel:form:submit",
						"onclick");

		SpringContextLoaderBaseTest.tester.startPage(HomePage.class);
		SpringContextLoaderBaseTest.tester.assertRenderedPage(HomePage.class);

		// Verify that there is one poison counter on token
		SpringContextLoaderBaseTest.tester
				.assertLabel(
						"parentPlaceholder:magicCardsForSide1:2:cardPanel:tooltip:counterPanel:counters:0:setCounterForm:counterName",
						"poison");
		SpringContextLoaderBaseTest.tester
				.assertLabel(
						"parentPlaceholder:magicCardsForSide1:2:cardPanel:tooltip:counterPanel:counters:0:setCounterForm:numberOfCounters",
						"1");

		// Add 1 more poison counter to token
		SpringContextLoaderBaseTest.tester
				.clickLink(
						"parentPlaceholder:magicCardsForSide1:2:cardPanel:tooltip:counterPanel:counters:0:setCounterForm:addCounterLink",
						true);

		SpringContextLoaderBaseTest.tester.startPage(HomePage.class);
		SpringContextLoaderBaseTest.tester.assertRenderedPage(HomePage.class);

		// Assert that there are 2 poison counters on token
		SpringContextLoaderBaseTest.tester
				.assertLabel(
						"parentPlaceholder:magicCardsForSide1:2:cardPanel:tooltip:counterPanel:counters:0:setCounterForm:counterName",
						"poison");
		SpringContextLoaderBaseTest.tester
				.assertLabel(
						"parentPlaceholder:magicCardsForSide1:2:cardPanel:tooltip:counterPanel:counters:0:setCounterForm:numberOfCounters",
						"2");

		// Put 10 poison counters on token
		form = SpringContextLoaderBaseTest.tester
				.newFormTester("parentPlaceholder:magicCardsForSide1:2:cardPanel:tooltip:counterPanel:counters:0:setCounterForm");
		form.setValue("setCounterButton", "10");
		SpringContextLoaderBaseTest.tester
				.executeAjaxEvent(
						"parentPlaceholder:magicCardsForSide1:2:cardPanel:tooltip:counterPanel:counters:0:setCounterForm:setCounterSubmit",
						"onclick");

		SpringContextLoaderBaseTest.tester.startPage(HomePage.class);
		SpringContextLoaderBaseTest.tester.assertRenderedPage(HomePage.class);

		// Assert that there are 10 poison counters on token
		SpringContextLoaderBaseTest.tester
				.assertLabel(
						"parentPlaceholder:magicCardsForSide1:2:cardPanel:tooltip:counterPanel:counters:0:setCounterForm:counterName",
						"poison");
		SpringContextLoaderBaseTest.tester
				.assertLabel(
						"parentPlaceholder:magicCardsForSide1:2:cardPanel:tooltip:counterPanel:counters:0:setCounterForm:numberOfCounters",
						"10");

		// Remove a poison counter on token
		SpringContextLoaderBaseTest.tester
				.clickLink(
						"parentPlaceholder:magicCardsForSide1:2:cardPanel:tooltip:counterPanel:counters:0:setCounterForm:removeCounterLink",
						true);

		SpringContextLoaderBaseTest.tester.startPage(HomePage.class);
		SpringContextLoaderBaseTest.tester.assertRenderedPage(HomePage.class);

		// Assert that there are 9 poison counters on token
		SpringContextLoaderBaseTest.tester
				.assertLabel(
						"parentPlaceholder:magicCardsForSide1:2:cardPanel:tooltip:counterPanel:counters:0:setCounterForm:counterName",
						"poison");
		SpringContextLoaderBaseTest.tester
				.assertLabel(
						"parentPlaceholder:magicCardsForSide1:2:cardPanel:tooltip:counterPanel:counters:0:setCounterForm:numberOfCounters",
						"9");

		// Add a blah counter to token
		form = SpringContextLoaderBaseTest.tester
				.newFormTester("parentPlaceholder:magicCardsForSide1:2:cardPanel:tooltip:counterPanel:form");
		form.setValue("counterAddName", "blah");
		SpringContextLoaderBaseTest.tester
				.executeAjaxEvent(
						"parentPlaceholder:magicCardsForSide1:2:cardPanel:tooltip:counterPanel:form:submit",
						"onclick");

		SpringContextLoaderBaseTest.tester.startPage(HomePage.class);
		SpringContextLoaderBaseTest.tester.assertRenderedPage(HomePage.class);

		// Verify that there is one blah counter on token, don't forget that the
		// counters appear in alphabetic order
		SpringContextLoaderBaseTest.tester
				.assertLabel(
						"parentPlaceholder:magicCardsForSide1:2:cardPanel:tooltip:counterPanel:counters:0:setCounterForm:counterName",
						"blah");
		SpringContextLoaderBaseTest.tester
				.assertLabel(
						"parentPlaceholder:magicCardsForSide1:2:cardPanel:tooltip:counterPanel:counters:0:setCounterForm:numberOfCounters",
						"1");

		// Add 1 more blah counter to token
		SpringContextLoaderBaseTest.tester
				.clickLink(
						"parentPlaceholder:magicCardsForSide1:2:cardPanel:tooltip:counterPanel:counters:0:setCounterForm:addCounterLink",
						true);

		SpringContextLoaderBaseTest.tester.startPage(HomePage.class);
		SpringContextLoaderBaseTest.tester.assertRenderedPage(HomePage.class);

		// Verify that there is two blah counters on token
		SpringContextLoaderBaseTest.tester
				.assertLabel(
						"parentPlaceholder:magicCardsForSide1:2:cardPanel:tooltip:counterPanel:counters:0:setCounterForm:counterName",
						"blah");
		SpringContextLoaderBaseTest.tester
				.assertLabel(
						"parentPlaceholder:magicCardsForSide1:2:cardPanel:tooltip:counterPanel:counters:0:setCounterForm:numberOfCounters",
						"2");

		SpringContextLoaderBaseTest.tester.startPage(HomePage.class);
		SpringContextLoaderBaseTest.tester.assertRenderedPage(HomePage.class);


		// Remove all poison counters from token
		form = SpringContextLoaderBaseTest.tester
				.newFormTester("parentPlaceholder:magicCardsForSide1:2:cardPanel:tooltip:counterPanel:counters:1:setCounterForm");
		form.setValue("setCounterButton", "0");
		SpringContextLoaderBaseTest.tester
				.executeAjaxEvent(
						"parentPlaceholder:magicCardsForSide1:2:cardPanel:tooltip:counterPanel:counters:1:setCounterForm:setCounterSubmit",
						"onclick");

		// Assert that there are no more poison counters on token
		SpringContextLoaderBaseTest.waTester.switchOffTestMode();
		pageDocument = SpringContextLoaderBaseTest.waTester.getPushedResponse();
		Assert.assertTrue(pageDocument
				.contains("infrared has cleared 'poison' counters on infrared's card or token: token"));

		// Add a poison counter to token
		form = SpringContextLoaderBaseTest.tester
				.newFormTester("parentPlaceholder:magicCardsForSide1:2:cardPanel:tooltip:counterPanel:form");
		form.setValue("counterAddName", "poison");
		SpringContextLoaderBaseTest.tester
				.executeAjaxEvent(
						"parentPlaceholder:magicCardsForSide1:2:cardPanel:tooltip:counterPanel:form:submit",
						"onclick");

		SpringContextLoaderBaseTest.tester.startPage(HomePage.class);
		SpringContextLoaderBaseTest.tester.assertRenderedPage(HomePage.class);

		// Verify that there is one poison counter on token
		SpringContextLoaderBaseTest.tester
				.assertLabel(
						"parentPlaceholder:magicCardsForSide1:2:cardPanel:tooltip:counterPanel:counters:1:setCounterForm:counterName",
						"poison");
		SpringContextLoaderBaseTest.tester
				.assertLabel(
						"parentPlaceholder:magicCardsForSide1:2:cardPanel:tooltip:counterPanel:counters:1:setCounterForm:numberOfCounters",
						"1");

		// Add 1 more poison counter to token
		SpringContextLoaderBaseTest.tester
				.clickLink(
						"parentPlaceholder:magicCardsForSide1:2:cardPanel:tooltip:counterPanel:counters:1:setCounterForm:addCounterLink",
						true);

		SpringContextLoaderBaseTest.tester.startPage(HomePage.class);
		SpringContextLoaderBaseTest.tester.assertRenderedPage(HomePage.class);

		// Assert that there are 2 poison counters on token
		SpringContextLoaderBaseTest.tester
				.assertLabel(
						"parentPlaceholder:magicCardsForSide1:2:cardPanel:tooltip:counterPanel:counters:1:setCounterForm:counterName",
						"poison");
		SpringContextLoaderBaseTest.tester
				.assertLabel(
						"parentPlaceholder:magicCardsForSide1:2:cardPanel:tooltip:counterPanel:counters:1:setCounterForm:numberOfCounters",
						"2");

		// Remove a blah counter from token
		SpringContextLoaderBaseTest.tester
				.clickLink(
						"parentPlaceholder:magicCardsForSide1:2:cardPanel:tooltip:counterPanel:counters:0:setCounterForm:removeCounterLink",
						true);

		SpringContextLoaderBaseTest.tester.startPage(HomePage.class);
		SpringContextLoaderBaseTest.tester.assertRenderedPage(HomePage.class);

		// Assert that there are 1 blah counter on token
		SpringContextLoaderBaseTest.tester
				.assertLabel(
						"parentPlaceholder:magicCardsForSide1:2:cardPanel:tooltip:counterPanel:counters:0:setCounterForm:counterName",
						"blah");
		SpringContextLoaderBaseTest.tester
				.assertLabel(
						"parentPlaceholder:magicCardsForSide1:2:cardPanel:tooltip:counterPanel:counters:0:setCounterForm:numberOfCounters",
						"1");
	}
}
