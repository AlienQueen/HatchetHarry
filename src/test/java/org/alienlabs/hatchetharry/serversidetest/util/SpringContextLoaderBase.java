package org.alienlabs.hatchetharry.serversidetest.util;

import java.util.List;

import org.alienlabs.hatchetharry.HatchetHarryApplication;
import org.alienlabs.hatchetharry.HatchetHarrySession;
import org.alienlabs.hatchetharry.model.MagicCard;
import org.alienlabs.hatchetharry.model.Player;
import org.alienlabs.hatchetharry.service.PersistenceService;
import org.alienlabs.hatchetharry.view.component.zone.PlayCardFromHandBehavior;
import org.alienlabs.hatchetharry.view.page.HomePage;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.atmosphere.EventBus;
import org.apache.wicket.atmosphere.config.AtmosphereLogLevel;
import org.apache.wicket.atmosphere.config.AtmosphereTransport;
import org.apache.wicket.atmosphere.tester.AtmosphereTester;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringComponentInjector;
import org.apache.wicket.util.tester.FormTester;
import org.apache.wicket.util.tester.WicketTester;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:applicationContext.xml",
		"classpath:applicationContextTest.xml" })
public class SpringContextLoaderBase
{
	protected static AtmosphereTester waTester;
	protected static transient WicketTester tester;
	private static HatchetHarryApplication webApp;
	protected static PersistenceService persistenceService;

	@Autowired
	protected ApplicationContext context;

	@Before
	public void setUp() throws Exception
	{
		webApp = new HatchetHarryApplication()
		{
			private static final long serialVersionUID = 1L;

			@Override
			public void init()
			{
				this.getComponentInstantiationListeners().add(
						new SpringComponentInjector(this, SpringContextLoaderBase.this.context,
								true));

				this.eventBus = new EventBus(this);
				this.eventBus.addRegistrationListener(this);
				this.eventBus.getParameters().setTransport(AtmosphereTransport.WEBSOCKET);
				this.eventBus.getParameters().setLogLevel(AtmosphereLogLevel.INFO);

				this.getMarkupSettings().setStripWicketTags(false);
				this.getDebugSettings().setOutputComponentPath(true);
			}
		};

		// start and render the test page
		tester = new WicketTester(webApp);
		persistenceService = this.context.getBean(PersistenceService.class);
		Assert.assertNotNull(persistenceService);
		waTester = new AtmosphereTester(tester, new HomePage(new PageParameters()));
	}

	@After
	public void tearDown()
	{
		webApp.newSession(tester.getRequestCycle().getRequest(), tester.getRequestCycle()
				.getResponse());
		persistenceService.resetDb();
	}

	public void startAGameAndPlayACard(final String... pageParameters) throws Exception
	{
		// Create game
		final String paramName = pageParameters.length > 1 ? pageParameters[0] : "";
		final String paramValue = pageParameters.length > 1 ? pageParameters[1] : "";
		SpringContextLoaderBase.tester.startPage(new HomePage(new PageParameters().add(paramName,
				paramValue)));
		SpringContextLoaderBase.tester.assertRenderedPage(HomePage.class);

		SpringContextLoaderBase.tester.assertComponent("createGameLink", AjaxLink.class);
		SpringContextLoaderBase.tester.clickLink("createGameLink", true);

		final FormTester createGameForm = SpringContextLoaderBase.tester
				.newFormTester("createGameWindow:content:form");
		createGameForm.setValue("name", "Zala");
		createGameForm.setValue("sideInput", "1");
		createGameForm.setValue("deckParent:decks", "1");
		createGameForm.setValue("formats", "1");
		createGameForm.setValue("numberOfPlayers", "2");

		if ((pageParameters.length > 0) && ("ajaxSubmit".equals(pageParameters[0])))
		{
			SpringContextLoaderBase.tester.executeAjaxEvent("createGameWindow:content:form:submit",
					"onclick");
		}
		else
		{
			createGameForm.submit();
		}

		SpringContextLoaderBase.tester.startPage(HomePage.class);
		SpringContextLoaderBase.tester.assertRenderedPage(HomePage.class);

		Player p = SpringContextLoaderBase.persistenceService.getAllPlayersOfGame(
				HatchetHarrySession.get().getGameId().longValue()).get(0);
		Assert.assertEquals(60, p.getDeck().getCards().size());
		final PlayCardFromHandBehavior pcfhb = getFirstPlayCardFromHandBehavior();

		// For the moment, we should have no card in the battlefield
		final Long gameId = HatchetHarrySession.get().getGameId();
		final List<MagicCard> allCardsInBattlefield = SpringContextLoaderBase.persistenceService
				.getAllCardsInBattlefieldForAGame(gameId);
		Assert.assertEquals(0, allCardsInBattlefield.size());

		// Play a card
		SpringContextLoaderBase.tester.getRequest().setParameter("card",
				HatchetHarrySession.get().getFirstCardsInHand().get(0).getUuid());
		SpringContextLoaderBase.tester.executeBehavior(pcfhb);

		// One card on the battlefield, 6 in the hand
		Assert.assertEquals(1, SpringContextLoaderBase.persistenceService
				.getAllCardsInBattlefieldForAGame(gameId).size());
		Assert.assertEquals(6, SpringContextLoaderBase.persistenceService
				.getAllCardsInHandForAGameAndADeck(gameId, p.getDeck().getDeckId()).size());

		// We still should not have more cards that the number of cards in the
		// deck
		p = SpringContextLoaderBase.persistenceService.getAllPlayersOfGame(
				HatchetHarrySession.get().getGameId().longValue()).get(0);
		Assert.assertEquals(60, p.getDeck().getCards().size());
	}

	// Retrieve PlayCardFromHandBehavior
	protected static PlayCardFromHandBehavior getFirstPlayCardFromHandBehavior()
	{
		tester.assertComponent("galleryParent:gallery:handCards:0", ListItem.class);
		final ListItem<MagicCard> playCardLink = (ListItem<MagicCard>)tester
				.getComponentFromLastRenderedPage("galleryParent:gallery:handCards:0");
		final PlayCardFromHandBehavior b = (PlayCardFromHandBehavior)playCardLink.getBehaviors()
				.get(0);
		Assert.assertNotNull(b);
		return b;
	}
}
