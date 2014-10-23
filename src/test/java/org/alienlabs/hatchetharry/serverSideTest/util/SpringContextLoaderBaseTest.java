package org.alienlabs.hatchetharry.serverSideTest.util;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.alienlabs.hatchetharry.HatchetHarryApplication;
import org.alienlabs.hatchetharry.HatchetHarrySession;
import org.alienlabs.hatchetharry.model.Deck;
import org.alienlabs.hatchetharry.model.Game;
import org.alienlabs.hatchetharry.model.MagicCard;
import org.alienlabs.hatchetharry.model.Player;
import org.alienlabs.hatchetharry.service.DataGenerator;
import org.alienlabs.hatchetharry.service.ImportDeckService;
import org.alienlabs.hatchetharry.service.PersistenceService;
import org.alienlabs.hatchetharry.view.component.zone.PlayCardFromHandBehavior;
import org.alienlabs.hatchetharry.view.page.HomePage;
import org.apache.wicket.Page;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.atmosphere.EventBus;
import org.apache.wicket.atmosphere.config.AtmosphereLogLevel;
import org.apache.wicket.atmosphere.config.AtmosphereTransport;
import org.apache.wicket.atmosphere.tester.AtmosphereTester;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringComponentInjector;
import org.apache.wicket.spring.test.ApplicationContextMock;
import org.apache.wicket.util.tester.FormTester;
import org.apache.wicket.util.tester.WicketTester;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;

public class SpringContextLoaderBaseTest
{
	// public ClassPathXmlApplicationContext CLASS_PATH_XML_APPLICATION_CONTEXT
	// = new ClassPathXmlApplicationContext(
	// new String[] { "applicationContext.xml", "applicationContextTest.xml" });
	public transient ApplicationContextMock context;
	protected transient WicketTester tester;
	protected HatchetHarryApplication webApp;
	protected String pageDocument;
	protected PersistenceService persistenceService;
	protected AtmosphereTester waTester;

	@Before
	public void setUpWithMocks() throws Exception
	{
		// Init the EventBus
		this.webApp = new HatchetHarryApplication()
		{
			/**
			 *
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public void init()
			{
				// SpringContextLoaderBaseTest.this.context = new
				// ApplicationContextMock();//
				// SpringContextLoaderBaseTest.this.CLASS_PATH_XML_APPLICATION_CONTEXT;
				// this.getComponentInstantiationListeners().add(
				// new SpringComponentInjector(this,
				// SpringContextLoaderBaseTest.this.context,
				// true));

				this.eventBus = new EventBus(this);
				this.eventBus.addRegistrationListener(this);
				this.eventBus.getParameters().setTransport(AtmosphereTransport.WEBSOCKET);
				this.eventBus.getParameters().setLogLevel(AtmosphereLogLevel.DEBUG);

				this.getMarkupSettings().setStripWicketTags(false);
				this.getDebugSettings().setOutputComponentPath(true);
			}
		};


		this.tester = new WicketTester(this.webApp);
		this.context = new ApplicationContextMock();
		this.tester
				.getApplication()
				.getComponentInstantiationListeners()
				.add(new SpringComponentInjector(this.tester.getApplication(),
						SpringContextLoaderBaseTest.this.context));
		this.initApplicationContextMock();
		this.persistenceService = this.context.getBean(PersistenceService.class);

		// start and render the test page
		final PageParameters pp = new PageParameters();
		pp.add("test", "test");
		Page hp = new HomePage(pp);
		hp = this.tester.startPage(hp);
		this.waTester = new AtmosphereTester(this.tester, hp);
	}

	private void initApplicationContextMock()
	{
		final PersistenceService persistenceServiceMock = mock(PersistenceService.class);
		final Game game = new Game();
		game.setId(1L);
		final Set<Player> players = new HashSet<Player>();
		final Player p = new Player();
		p.setHandDisplayed(true);
		p.setGame(game);
		p.setId(1L);
		p.setLifePoints(20L);
		players.add(p);
		game.setPlayers(players);

		when(persistenceServiceMock.createGameAndPlayer((Game)any(), (Player)any())).thenReturn(
				game);

		final Deck deck = new Deck();
		deck.setDeckId(1L);
		deck.setPlayerId(p.getId());
		when(persistenceServiceMock.getDeckByDeckArchiveName("aggro-combo Red / Black"))
				.thenReturn(deck);

		final List<MagicCard> cards = this.initHand(deck);
		final MagicCard baldu = new MagicCard("baldu", "baldu", "baldu", "baldu", "baldu",
				"infrared", null, 0);
		baldu.setUuidObject(UUID.fromString("249c4f0b-cad0-4606-b5ea-eaee8866a347"));
		baldu.setDeck(deck);
		final List<Player> playersAsList = new ArrayList<Player>();
		playersAsList.addAll(players);

		when(persistenceServiceMock.getAllCardsFromDeck((String)any())).thenReturn(cards);
		when(persistenceServiceMock.getPlayer(anyLong())).thenReturn(p);
		when(persistenceServiceMock.getGame(anyLong())).thenReturn(game);
		when(persistenceServiceMock.findCardByName("Balduvian Horde")).thenReturn(baldu);
		when(persistenceServiceMock.getCardFromUuid((UUID)any())).thenReturn(baldu);
		when(persistenceServiceMock.getPlayer(anyLong())).thenReturn(p);
		when(persistenceServiceMock.getAllPlayersOfGame(anyLong())).thenReturn(playersAsList);

		final DataGenerator dataGeneratorMock = mock(DataGenerator.class);
		final ImportDeckService importDeckServiceMock = mock(ImportDeckService.class);
		this.addMock("persistenceService", persistenceServiceMock);
		this.addMock("dataGenerator", dataGeneratorMock);
		this.addMock("importDeckService", importDeckServiceMock);
	}

	private List<MagicCard> initHand(final Deck deck)
	{
		final List<MagicCard> cards = new ArrayList<MagicCard>();

		for (int i = 0; i < 7; i++)
		{
			final MagicCard card = new MagicCard("test" + i, "test" + i, "test" + i, "test" + i,
					"test" + i, "infrared" + i, null, 0);
			card.setDeck(deck);
			card.setUuidObject(UUID.randomUUID());
			cards.add(card);
		}

		return cards;
	}

	@After
	public void tearDown()
	{
		// SpringContextLoaderBaseTest.this.context.getBean(PersistenceService.class).resetDb();
		// HatchetHarrySession.get().reinitSession();
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
		Assert.assertEquals(7, p.getDeck().getCards().size());


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
		Assert.assertEquals(7, p.getDeck().getCards().size());

		return gameId;
	}

	public void newHomePage() throws Exception
	{
		final PageParameters pp = new PageParameters();
		pp.add("test", "test");
		this.tester.startPage(new HomePage(pp));
	}

	/**
	 * Adds mock to the mock application context.
	 * 
	 * @param beanName
	 *            The name of the mock bean.
	 * @param mock
	 *            The mock object.
	 */
	protected void addMock(final String beanName, final Object mock)
	{
		this.context.putBean(beanName, mock);
	}
}
