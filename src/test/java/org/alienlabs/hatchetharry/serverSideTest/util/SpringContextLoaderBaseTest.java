package org.alienlabs.hatchetharry.serverSideTest.util;

import java.io.IOException;
import java.util.List;

import org.alienlabs.hatchetharry.HatchetHarryApplication;
import org.alienlabs.hatchetharry.HatchetHarrySession;
import org.alienlabs.hatchetharry.model.MagicCard;
import org.alienlabs.hatchetharry.model.Player;
import org.alienlabs.hatchetharry.service.PersistenceService;
import org.alienlabs.hatchetharry.view.component.PlayCardFromHandBehavior;
import org.alienlabs.hatchetharry.view.page.HomePage;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.atmosphere.EventBus;
import org.apache.wicket.atmosphere.config.AtmosphereLogLevel;
import org.apache.wicket.atmosphere.config.AtmosphereTransport;
import org.apache.wicket.atmosphere.tester.AtmosphereTester;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringComponentInjector;
import org.apache.wicket.util.tester.FormTester;
import org.apache.wicket.util.tester.WicketTester;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class SpringContextLoaderBaseTest
{
	public ClassPathXmlApplicationContext CLASS_PATH_XML_APPLICATION_CONTEXT = new ClassPathXmlApplicationContext(
		new String[] { "applicationContext.xml", "applicationContextTest.xml" });
	public transient ApplicationContext context;
	protected transient WicketTester tester;
	protected HatchetHarryApplication webApp;
	protected String pageDocument;
	protected PersistenceService persistenceService;
	protected AtmosphereTester waTester;

	@Before
	public void setUpBeforeClassWithMocks() throws IOException
	{
		// Init the EventBus
		this.webApp = new HatchetHarryApplication()
		{
			@Override
			public void init()
			{
				SpringContextLoaderBaseTest.this.context = SpringContextLoaderBaseTest.this.CLASS_PATH_XML_APPLICATION_CONTEXT;
				this.getComponentInstantiationListeners().add(
					new SpringComponentInjector(this, SpringContextLoaderBaseTest.this.context,
						true));

				this.eventBus = new EventBus(this);
				this.eventBus.addRegistrationListener(this);
				this.eventBus.getParameters().setTransport(AtmosphereTransport.WEBSOCKET);
				this.eventBus.getParameters().setLogLevel(AtmosphereLogLevel.DEBUG);

				this.getMarkupSettings().setStripWicketTags(false);
				this.getDebugSettings().setOutputComponentPath(true);
			}
		};
		this.tester = new WicketTester(this.webApp);
		this.persistenceService = this.context.getBean(PersistenceService.class);

		// start and render the test page
		final PageParameters pp = new PageParameters();
		pp.add("test", "test");
		this.waTester = new AtmosphereTester(this.tester, new HomePage(pp));
	}

	@After
	public void tearDownAfterClass()
	{
		// SpringContextLoaderBaseTest.context.getBean(PersistenceService.class).resetDb();
		HatchetHarrySession.get().reinitSession();
	}

	public Long startAGameAndPlayACard()
	{
		// Create game
		this.tester.assertComponent("createGameLink", AjaxLink.class);
		this.tester.clickLink("createGameLink", true);

		final FormTester createGameForm = this.tester
			.newFormTester("createGameWindow:content:form");
		createGameForm.setValue("name", "Zala");
		createGameForm.setValue("sideInput", "0");
		createGameForm.setValue("deckParent:decks", "0");
		createGameForm.submit();

		Player p = this.persistenceService.getAllPlayersOfGame(
			HatchetHarrySession.get().getGameId()).get(0);
		Assert.assertEquals(60, p.getDeck().getCards().size());


		// Retrieve PlayCardFromHandBehavior
		this.tester.assertComponent("playCardLink", WebMarkupContainer.class);
		final WebMarkupContainer playCardLink = (WebMarkupContainer)this.tester
			.getComponentFromLastRenderedPage("playCardLink");
		final PlayCardFromHandBehavior pcfhb = (PlayCardFromHandBehavior)playCardLink
			.getBehaviors().get(0);

		// For the moment, we should have no card in the battlefield
		final Long gameId = HatchetHarrySession.get().getGameId();
		final List<MagicCard> allCardsInBattlefield = this.persistenceService
			.getAllCardsInBattleFieldForAGame(gameId);
		Assert.assertEquals(0, allCardsInBattlefield.size());

		// Play a card
		this.tester.getRequest().setParameter("card",
			HatchetHarrySession.get().getFirstCardsInHand().get(0).getUuid());
		this.tester.executeBehavior(pcfhb);
		// this.tester.assertRenderedPage(HomePage.class);
		// We still should not have more cards that the number of cards in the
		// deck
		p = this.persistenceService.getAllPlayersOfGame(HatchetHarrySession.get().getGameId()).get(
			0);
		Assert.assertEquals(60, p.getDeck().getCards().size());

		return gameId;
	}

    public void newHomePage() throws IOException
	{
		final PageParameters pp = new PageParameters();
		pp.add("test", "test");
		this.tester.startPage(new HomePage(pp));
	}
}
