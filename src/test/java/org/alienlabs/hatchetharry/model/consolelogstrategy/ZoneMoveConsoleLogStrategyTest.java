package org.alienlabs.hatchetharry.model.consolelogstrategy;

import org.alienlabs.hatchetharry.HatchetHarrySession;
import org.alienlabs.hatchetharry.serversidetest.util.SpringContextLoaderBase;
import org.alienlabs.hatchetharry.view.component.zone.PlayCardFromHandBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.util.tester.FormTester;
import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;

import static org.junit.Assert.assertTrue;

/**
 * Created by nostromo on 19/01/15.
 */
@ContextConfiguration(locations = { "classpath:applicationContext.xml",
		"classpath:applicationContextTest.xml" }) public class ZoneMoveConsoleLogStrategyTest
		extends SpringContextLoaderBase
{
	@Test public void testZoneMoveConsoleLogStrategy()
	{
		SpringContextLoaderBase.tester.assertComponent("createMatchLink", AjaxLink.class);
		SpringContextLoaderBase.tester.clickLink("createMatchLink", true);

		final FormTester createGameForm = SpringContextLoaderBase.tester
				.newFormTester("createMatchWindow:content:form");
		createGameForm.setValue("name", "Zala");
		createGameForm.setValue("sideInput", "1");
		createGameForm.setValue("deckParent:decks", "1");
		createGameForm.setValue("formats", "1");
		createGameForm.setValue("numberOfPlayers", "2");

		SpringContextLoaderBase.tester
				.executeAjaxEvent("createMatchWindow:content:form:submit", "onclick");

		final PlayCardFromHandBehavior behavior = getFirstPlayCardFromHandBehavior();
		SpringContextLoaderBase.tester.getRequest().setParameter("card",
				HatchetHarrySession.get().getFirstCardsInHand().get(0).getUuid());
		SpringContextLoaderBase.tester.executeBehavior(behavior);

		try
		{
			Thread.sleep(5000);
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}

		String message = behavior.getLogger().getMessage();
		assertTrue(message.contains(
				"Zala has put <a class='consoleCard' style='color: white;' href='#' title='<img src=\\\"cards/"));
		assertTrue(message.contains(".jpg\\\"></img>'>"));
		assertTrue(message.contains("</a> from Hand to Battlefield"));
	}
}
