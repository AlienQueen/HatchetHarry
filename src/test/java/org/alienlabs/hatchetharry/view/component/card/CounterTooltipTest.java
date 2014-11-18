package org.alienlabs.hatchetharry.view.component.card;

import org.alienlabs.hatchetharry.serverSideTest.util.SpringContextLoaderBase;
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
public class CounterTooltipTest extends SpringContextLoaderBase
{
	@Test
	public void testCardTooltip() throws Exception
	{
		// Start a game and play a card
		super.startAGameAndPlayACard("displayTooltips", "true");
		SpringContextLoaderBase.tester.startPage(HomePage.class);
		SpringContextLoaderBase.tester.assertRenderedPage(HomePage.class);

		// Assert it's a card and not a token
		SpringContextLoaderBase.tester.assertComponent(
				"parentPlaceholder:magicCardsForSide1:1:cardPanel:tooltip",
				MagicCardTooltipPanel.class);

		// Put a blah counter
		FormTester form = SpringContextLoaderBase.tester
				.newFormTester("parentPlaceholder:magicCardsForSide1:1:cardPanel:tooltip:counterPanel:form");
		form.setValue("counterAddName", "blah");
		SpringContextLoaderBase.tester
				.executeAjaxEvent(
						"parentPlaceholder:magicCardsForSide1:1:cardPanel:tooltip:counterPanel:form:submit",
						"onclick");

		SpringContextLoaderBase.tester.startPage(HomePage.class);
		SpringContextLoaderBase.tester.assertRenderedPage(HomePage.class);

		// Assert that there is 1 blah counter on card
		SpringContextLoaderBase.tester
				.assertLabel(
						"parentPlaceholder:magicCardsForSide1:1:cardPanel:tooltip:counterPanel:counters:0:setCounterForm:counterName",
						"blah");
		SpringContextLoaderBase.tester
				.assertLabel(
						"parentPlaceholder:magicCardsForSide1:1:cardPanel:tooltip:counterPanel:counters:0:setCounterForm:numberOfCounters",
						"1");

		// Put a charge counter
		form = SpringContextLoaderBase.tester
				.newFormTester("parentPlaceholder:magicCardsForSide1:1:cardPanel:tooltip:counterPanel:form");
		form.setValue("counterAddName", "charge");
		SpringContextLoaderBase.tester
				.executeAjaxEvent(
						"parentPlaceholder:magicCardsForSide1:1:cardPanel:tooltip:counterPanel:form:submit",
						"onclick");

		HomePage hp = SpringContextLoaderBase.tester.startPage(HomePage.class);
		SpringContextLoaderBase.tester.assertRenderedPage(HomePage.class);

		// Assert that there is 1 charge counter on card
		SpringContextLoaderBase.tester
				.assertLabel(
						"parentPlaceholder:magicCardsForSide1:1:cardPanel:tooltip:counterPanel:counters:1:setCounterForm:counterName",
						"charge");
		SpringContextLoaderBase.tester
				.assertLabel(
						"parentPlaceholder:magicCardsForSide1:1:cardPanel:tooltip:counterPanel:counters:1:setCounterForm:numberOfCounters",
						"1");

		// Add 1 more blah counter to card
		SpringContextLoaderBase.tester
				.clickLink(
						"parentPlaceholder:magicCardsForSide1:1:cardPanel:tooltip:counterPanel:counters:0:setCounterForm:addCounterLink",
						true);

		SpringContextLoaderBase.tester.startPage(HomePage.class);
		SpringContextLoaderBase.tester.assertRenderedPage(HomePage.class);

		// Assert that there are 2 blah counters on card
		SpringContextLoaderBase.tester
				.assertLabel(
						"parentPlaceholder:magicCardsForSide1:1:cardPanel:tooltip:counterPanel:counters:0:setCounterForm:counterName",
						"blah");
		SpringContextLoaderBase.tester
				.assertLabel(
						"parentPlaceholder:magicCardsForSide1:1:cardPanel:tooltip:counterPanel:counters:0:setCounterForm:numberOfCounters",
						"2");

		// Put 10 blah counters on card
		form = SpringContextLoaderBase.tester
				.newFormTester("parentPlaceholder:magicCardsForSide1:1:cardPanel:tooltip:counterPanel:counters:0:setCounterForm");
		form.setValue("setCounterButton", "10");
		SpringContextLoaderBase.tester
				.executeAjaxEvent(
						"parentPlaceholder:magicCardsForSide1:1:cardPanel:tooltip:counterPanel:counters:0:setCounterForm:setCounterSubmit",
						"onclick");

		SpringContextLoaderBase.tester.startPage(HomePage.class);
		SpringContextLoaderBase.tester.assertRenderedPage(HomePage.class);

		// Assert that there are 10 blah counters on card
		SpringContextLoaderBase.tester
				.assertLabel(
						"parentPlaceholder:magicCardsForSide1:1:cardPanel:tooltip:counterPanel:counters:0:setCounterForm:counterName",
						"blah");
		SpringContextLoaderBase.tester
				.assertLabel(
						"parentPlaceholder:magicCardsForSide1:1:cardPanel:tooltip:counterPanel:counters:0:setCounterForm:numberOfCounters",
						"10");

		// Remove a blah counter on card
		SpringContextLoaderBase.tester
				.clickLink(
						"parentPlaceholder:magicCardsForSide1:1:cardPanel:tooltip:counterPanel:counters:0:setCounterForm:removeCounterLink",
						true);

		SpringContextLoaderBase.tester.startPage(HomePage.class);
		SpringContextLoaderBase.tester.assertRenderedPage(HomePage.class);

		// Assert that there are 9 blah counters on card
		SpringContextLoaderBase.tester
				.assertLabel(
						"parentPlaceholder:magicCardsForSide1:1:cardPanel:tooltip:counterPanel:counters:0:setCounterForm:counterName",
						"blah");
		SpringContextLoaderBase.tester
				.assertLabel(
						"parentPlaceholder:magicCardsForSide1:1:cardPanel:tooltip:counterPanel:counters:0:setCounterForm:numberOfCounters",
						"9");

		// Remove all blah counters from card
		form = SpringContextLoaderBase.tester
				.newFormTester("parentPlaceholder:magicCardsForSide1:1:cardPanel:tooltip:counterPanel:counters:0:setCounterForm");
		form.setValue("setCounterButton", "0");
		SpringContextLoaderBase.tester
				.executeAjaxEvent(
						"parentPlaceholder:magicCardsForSide1:1:cardPanel:tooltip:counterPanel:counters:0:setCounterForm:setCounterSubmit",
						"onclick");

		// Assert that there are no more blah counters on card
		SpringContextLoaderBase.waTester.switchOffTestMode();
		String pageDocument = SpringContextLoaderBase.waTester.getPushedResponse();
		Assert.assertTrue(pageDocument
				.contains("infrared has cleared 'blah' counters on infrared's card or token"));

		SpringContextLoaderBase.tester.startPage(HomePage.class);
		SpringContextLoaderBase.tester.assertRenderedPage(HomePage.class);

		// Put a blah counter again
		form = SpringContextLoaderBase.tester
				.newFormTester("parentPlaceholder:magicCardsForSide1:1:cardPanel:tooltip:counterPanel:form");
		form.setValue("counterAddName", "blah");
		SpringContextLoaderBase.tester
				.executeAjaxEvent(
						"parentPlaceholder:magicCardsForSide1:1:cardPanel:tooltip:counterPanel:form:submit",
						"onclick");

		SpringContextLoaderBase.tester.startPage(HomePage.class);
		SpringContextLoaderBase.tester.assertRenderedPage(HomePage.class);

		// Assert that there is 1 blah counter on card
		SpringContextLoaderBase.tester
				.assertLabel(
						"parentPlaceholder:magicCardsForSide1:1:cardPanel:tooltip:counterPanel:counters:0:setCounterForm:counterName",
						"blah");
		SpringContextLoaderBase.tester
				.assertLabel(
						"parentPlaceholder:magicCardsForSide1:1:cardPanel:tooltip:counterPanel:counters:0:setCounterForm:numberOfCounters",
						"1");

		// Add 1 more blah counter to card
		SpringContextLoaderBase.tester
				.clickLink(
						"parentPlaceholder:magicCardsForSide1:1:cardPanel:tooltip:counterPanel:counters:0:setCounterForm:addCounterLink",
						true);

		hp = SpringContextLoaderBase.tester.startPage(HomePage.class);
		SpringContextLoaderBase.tester.assertRenderedPage(HomePage.class);

		// Assert that there are 2 blah counters on card
		SpringContextLoaderBase.tester
				.assertLabel(
						"parentPlaceholder:magicCardsForSide1:1:cardPanel:tooltip:counterPanel:counters:0:setCounterForm:counterName",
						"blah");
		SpringContextLoaderBase.tester
				.assertLabel(
						"parentPlaceholder:magicCardsForSide1:1:cardPanel:tooltip:counterPanel:counters:0:setCounterForm:numberOfCounters",
						"2");

		// Create a token
		SpringContextLoaderBase.tester.assertComponent("createTokenLink", AjaxLink.class);
		SpringContextLoaderBase.tester.clickLink("createTokenLink", true);

		tester.assertComponentOnAjaxResponse(hp.getCreateTokenModalWindow());
		final ModalWindow window = hp.getCreateTokenModalWindow();
		SpringContextLoaderBase.tester.assertVisible(window.getPageRelativePath() + ":"
				+ window.getContentId());

		SpringContextLoaderBase.tester.assertComponent(
				window.getPageRelativePath() + ":" + window.getContentId() + ":form", Form.class);
		final FormTester form4 = SpringContextLoaderBase.tester.newFormTester(window
				.getPageRelativePath() + ":" + window.getContentId() + ":form");
		form4.setValue("type", "rat");
		form4.setValue("creatureTypes", "rat");
		tester.executeAjaxEvent(window.getPageRelativePath() + ":" + window.getContentId()
				+ ":form:createToken", "onclick");

		SpringContextLoaderBase.tester.startPage(HomePage.class);
		SpringContextLoaderBase.tester.assertRenderedPage(HomePage.class);

		// Assert token on battlefield
		SpringContextLoaderBase.tester.assertComponent(
				"parentPlaceholder:magicCardsForSide1:2:cardPanel:tooltip:counterPanel",
				CounterTooltip.class);


		// Add a poison counter to token
		form = SpringContextLoaderBase.tester
				.newFormTester("parentPlaceholder:magicCardsForSide1:2:cardPanel:tooltip:counterPanel:form");
		form.setValue("counterAddName", "poison");
		SpringContextLoaderBase.tester
				.executeAjaxEvent(
						"parentPlaceholder:magicCardsForSide1:2:cardPanel:tooltip:counterPanel:form:submit",
						"onclick");

		SpringContextLoaderBase.tester.startPage(HomePage.class);
		SpringContextLoaderBase.tester.assertRenderedPage(HomePage.class);

		// Verify that there is one poison counter on token
		SpringContextLoaderBase.tester
				.assertLabel(
						"parentPlaceholder:magicCardsForSide1:2:cardPanel:tooltip:counterPanel:counters:0:setCounterForm:counterName",
						"poison");
		SpringContextLoaderBase.tester
				.assertLabel(
						"parentPlaceholder:magicCardsForSide1:2:cardPanel:tooltip:counterPanel:counters:0:setCounterForm:numberOfCounters",
						"1");

		// Add 1 more poison counter to token
		SpringContextLoaderBase.tester
				.clickLink(
						"parentPlaceholder:magicCardsForSide1:2:cardPanel:tooltip:counterPanel:counters:0:setCounterForm:addCounterLink",
						true);

		SpringContextLoaderBase.tester.startPage(HomePage.class);
		SpringContextLoaderBase.tester.assertRenderedPage(HomePage.class);

		// Assert that there are 2 poison counters on token
		SpringContextLoaderBase.tester
				.assertLabel(
						"parentPlaceholder:magicCardsForSide1:2:cardPanel:tooltip:counterPanel:counters:0:setCounterForm:counterName",
						"poison");
		SpringContextLoaderBase.tester
				.assertLabel(
						"parentPlaceholder:magicCardsForSide1:2:cardPanel:tooltip:counterPanel:counters:0:setCounterForm:numberOfCounters",
						"2");

		// Put 10 poison counters on token
		form = SpringContextLoaderBase.tester
				.newFormTester("parentPlaceholder:magicCardsForSide1:2:cardPanel:tooltip:counterPanel:counters:0:setCounterForm");
		form.setValue("setCounterButton", "10");
		SpringContextLoaderBase.tester
				.executeAjaxEvent(
						"parentPlaceholder:magicCardsForSide1:2:cardPanel:tooltip:counterPanel:counters:0:setCounterForm:setCounterSubmit",
						"onclick");

		SpringContextLoaderBase.tester.startPage(HomePage.class);
		SpringContextLoaderBase.tester.assertRenderedPage(HomePage.class);

		// Assert that there are 10 poison counters on token
		SpringContextLoaderBase.tester
				.assertLabel(
						"parentPlaceholder:magicCardsForSide1:2:cardPanel:tooltip:counterPanel:counters:0:setCounterForm:counterName",
						"poison");
		SpringContextLoaderBase.tester
				.assertLabel(
						"parentPlaceholder:magicCardsForSide1:2:cardPanel:tooltip:counterPanel:counters:0:setCounterForm:numberOfCounters",
						"10");

		// Remove a poison counter on token
		SpringContextLoaderBase.tester
				.clickLink(
						"parentPlaceholder:magicCardsForSide1:2:cardPanel:tooltip:counterPanel:counters:0:setCounterForm:removeCounterLink",
						true);

		SpringContextLoaderBase.tester.startPage(HomePage.class);
		SpringContextLoaderBase.tester.assertRenderedPage(HomePage.class);

		// Assert that there are 9 poison counters on token
		SpringContextLoaderBase.tester
				.assertLabel(
						"parentPlaceholder:magicCardsForSide1:2:cardPanel:tooltip:counterPanel:counters:0:setCounterForm:counterName",
						"poison");
		SpringContextLoaderBase.tester
				.assertLabel(
						"parentPlaceholder:magicCardsForSide1:2:cardPanel:tooltip:counterPanel:counters:0:setCounterForm:numberOfCounters",
						"9");

		// Add a blah counter to token
		form = SpringContextLoaderBase.tester
				.newFormTester("parentPlaceholder:magicCardsForSide1:2:cardPanel:tooltip:counterPanel:form");
		form.setValue("counterAddName", "blah");
		SpringContextLoaderBase.tester
				.executeAjaxEvent(
						"parentPlaceholder:magicCardsForSide1:2:cardPanel:tooltip:counterPanel:form:submit",
						"onclick");

		SpringContextLoaderBase.tester.startPage(HomePage.class);
		SpringContextLoaderBase.tester.assertRenderedPage(HomePage.class);

		// Verify that there is one blah counter on token, don't forget that the
		// counters appear in alphabetic order
		SpringContextLoaderBase.tester
				.assertLabel(
						"parentPlaceholder:magicCardsForSide1:2:cardPanel:tooltip:counterPanel:counters:0:setCounterForm:counterName",
						"blah");
		SpringContextLoaderBase.tester
				.assertLabel(
						"parentPlaceholder:magicCardsForSide1:2:cardPanel:tooltip:counterPanel:counters:0:setCounterForm:numberOfCounters",
						"1");

		// Add 1 more blah counter to token
		SpringContextLoaderBase.tester
				.clickLink(
						"parentPlaceholder:magicCardsForSide1:2:cardPanel:tooltip:counterPanel:counters:0:setCounterForm:addCounterLink",
						true);

		SpringContextLoaderBase.tester.startPage(HomePage.class);
		SpringContextLoaderBase.tester.assertRenderedPage(HomePage.class);

		// Verify that there is two blah counters on token
		SpringContextLoaderBase.tester
				.assertLabel(
						"parentPlaceholder:magicCardsForSide1:2:cardPanel:tooltip:counterPanel:counters:0:setCounterForm:counterName",
						"blah");
		SpringContextLoaderBase.tester
				.assertLabel(
						"parentPlaceholder:magicCardsForSide1:2:cardPanel:tooltip:counterPanel:counters:0:setCounterForm:numberOfCounters",
						"2");

		SpringContextLoaderBase.tester.startPage(HomePage.class);
		SpringContextLoaderBase.tester.assertRenderedPage(HomePage.class);


		// Remove all poison counters from token
		form = SpringContextLoaderBase.tester
				.newFormTester("parentPlaceholder:magicCardsForSide1:2:cardPanel:tooltip:counterPanel:counters:1:setCounterForm");
		form.setValue("setCounterButton", "0");
		SpringContextLoaderBase.tester
				.executeAjaxEvent(
						"parentPlaceholder:magicCardsForSide1:2:cardPanel:tooltip:counterPanel:counters:1:setCounterForm:setCounterSubmit",
						"onclick");

		// Assert that there are no more poison counters on token
		SpringContextLoaderBase.waTester.switchOffTestMode();
		pageDocument = SpringContextLoaderBase.waTester.getPushedResponse();
		Assert.assertTrue(pageDocument
				.contains("infrared has cleared 'poison' counters on infrared's card or token: token"));

		// Add a poison counter to token
		form = SpringContextLoaderBase.tester
				.newFormTester("parentPlaceholder:magicCardsForSide1:2:cardPanel:tooltip:counterPanel:form");
		form.setValue("counterAddName", "poison");
		SpringContextLoaderBase.tester
				.executeAjaxEvent(
						"parentPlaceholder:magicCardsForSide1:2:cardPanel:tooltip:counterPanel:form:submit",
						"onclick");

		SpringContextLoaderBase.tester.startPage(HomePage.class);
		SpringContextLoaderBase.tester.assertRenderedPage(HomePage.class);

		// Verify that there is one poison counter on token
		SpringContextLoaderBase.tester
				.assertLabel(
						"parentPlaceholder:magicCardsForSide1:2:cardPanel:tooltip:counterPanel:counters:1:setCounterForm:counterName",
						"poison");
		SpringContextLoaderBase.tester
				.assertLabel(
						"parentPlaceholder:magicCardsForSide1:2:cardPanel:tooltip:counterPanel:counters:1:setCounterForm:numberOfCounters",
						"1");

		// Add 1 more poison counter to token
		SpringContextLoaderBase.tester
				.clickLink(
						"parentPlaceholder:magicCardsForSide1:2:cardPanel:tooltip:counterPanel:counters:1:setCounterForm:addCounterLink",
						true);

		SpringContextLoaderBase.tester.startPage(HomePage.class);
		SpringContextLoaderBase.tester.assertRenderedPage(HomePage.class);

		// Assert that there are 2 poison counters on token
		SpringContextLoaderBase.tester
				.assertLabel(
						"parentPlaceholder:magicCardsForSide1:2:cardPanel:tooltip:counterPanel:counters:1:setCounterForm:counterName",
						"poison");
		SpringContextLoaderBase.tester
				.assertLabel(
						"parentPlaceholder:magicCardsForSide1:2:cardPanel:tooltip:counterPanel:counters:1:setCounterForm:numberOfCounters",
						"2");

		// Remove a blah counter from token
		SpringContextLoaderBase.tester
				.clickLink(
						"parentPlaceholder:magicCardsForSide1:2:cardPanel:tooltip:counterPanel:counters:0:setCounterForm:removeCounterLink",
						true);

		SpringContextLoaderBase.tester.startPage(HomePage.class);
		SpringContextLoaderBase.tester.assertRenderedPage(HomePage.class);

		// Assert that there are 1 blah counter on token
		SpringContextLoaderBase.tester
				.assertLabel(
						"parentPlaceholder:magicCardsForSide1:2:cardPanel:tooltip:counterPanel:counters:0:setCounterForm:counterName",
						"blah");
		SpringContextLoaderBase.tester
				.assertLabel(
						"parentPlaceholder:magicCardsForSide1:2:cardPanel:tooltip:counterPanel:counters:0:setCounterForm:numberOfCounters",
						"1");
	}
}
