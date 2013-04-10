package org.alienlabs.hatchetharry.serverSideTest;

import java.util.List;
import java.util.UUID;

import org.alienlabs.hatchetharry.HatchetHarryApplication;
import org.alienlabs.hatchetharry.HatchetHarrySession;
import org.alienlabs.hatchetharry.model.MagicCard;
import org.alienlabs.hatchetharry.service.PersistenceService;
import org.alienlabs.hatchetharry.view.component.CardMoveBehavior;
import org.alienlabs.hatchetharry.view.component.HandComponent;
import org.alienlabs.hatchetharry.view.component.PlayCardFromHandBehavior;
import org.alienlabs.hatchetharry.view.component.PutToGraveyardFromBattlefieldBehavior;
import org.alienlabs.hatchetharry.view.page.HomePage;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.spring.injection.annot.SpringComponentInjector;
import org.apache.wicket.util.tester.FormTester;
import org.apache.wicket.util.tester.TagTester;
import org.apache.wicket.util.tester.WicketTester;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Non regression tests using the WicketTester.
 */
public class NonRegressionTest
{
	static final ClassPathXmlApplicationContext CLASS_PATH_XML_APPLICATION_CONTEXT = new ClassPathXmlApplicationContext(
			new String[] { "applicationContext.xml" });
	protected static transient WicketTester tester;
	protected static HatchetHarryApplication webApp;
	protected static transient ApplicationContext context;
	protected static String pageDocument;

	@BeforeClass
	public static void setUpBeforeClass()
	{
		NonRegressionTest.webApp = new HatchetHarryApplication()
		{
			private static final long serialVersionUID = 1L;


			@Override
			public void init()
			{
				NonRegressionTest.context = NonRegressionTest.CLASS_PATH_XML_APPLICATION_CONTEXT;
				this.getComponentInstantiationListeners().add(
						new SpringComponentInjector(this, NonRegressionTest.context, true));
				NonRegressionTest.context.getBean(PersistenceService.class).resetDb();
			}
		};

		NonRegressionTest.tester = new WicketTester(NonRegressionTest.webApp);

		// start and render the test page
		NonRegressionTest.tester.startPage(HomePage.class);

		// assert rendered page class
		NonRegressionTest.tester.assertRenderedPage(HomePage.class);

		NonRegressionTest.pageDocument = NonRegressionTest.tester.getLastResponse().getDocument();
	}

	@AfterClass
	public static void tearDownAfterClass()
	{
		NonRegressionTest.context.getBean(PersistenceService.class).resetDb();
	}

	@Test
	public void testDrawingACardShouldRaiseTheNumberOfCardsInHandToHeight()
	{
		// Init
		// assert hand is present
		NonRegressionTest.tester.assertComponent("galleryParent:gallery", HandComponent.class);

		// assert URL of a thumbnail
		List<TagTester> tagTester = TagTester.createTagsByAttribute(NonRegressionTest.pageDocument,
				"class", "nav-thumb", false);
		Assert.assertNotNull(tagTester);

		// assert number of thumbnails
		Assert.assertEquals(7, tagTester.size());

		Assert.assertNotNull(tagTester.get(0).getAttribute("src"));
		Assert.assertTrue(tagTester.get(0).getAttribute("src").contains(".jpg"));

		// Run
		NonRegressionTest.tester.assertComponent("drawCardLink", AjaxLink.class);
		NonRegressionTest.tester.clickLink("drawCardLink", true);

		// Verify
		NonRegressionTest.pageDocument = NonRegressionTest.tester.getLastResponse().getDocument();
		final String header = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><ajax-response><component id=\"galleryParent\" ><![CDATA[<span id=\"galleryParent\">";
		final int toRemoveHeader = NonRegressionTest.pageDocument.indexOf(header);
		NonRegressionTest.pageDocument = NonRegressionTest.pageDocument.substring(toRemoveHeader
				+ header.length());
		final int toRemoveFooter = NonRegressionTest.pageDocument.indexOf("]]></component>");
		NonRegressionTest.pageDocument = NonRegressionTest.pageDocument
				.substring(0, toRemoveFooter);

		tagTester = TagTester.createTagsByAttribute(NonRegressionTest.pageDocument, "class",
				"nav-thumb", false);
		Assert.assertNotNull(tagTester);

		// assert number of thumbnails
		Assert.assertEquals(8, tagTester.size());

		Assert.assertNotNull(tagTester.get(3).getAttribute("src"));
		Assert.assertTrue(tagTester.get(3).getAttribute("src").contains(".jpg"));
	}

	@Test
	/** 
	 * Init: we create a game, we play two cards, we move them to know positions
	 * Run: then, we put the first card to the graveyard
	 * Verify: the second card shall not have moved.
	 * 
	 */
	public void testPuttingACardToGraveyardShouldNotMoveCardsInBattlefield()
	{
		// Create game
		NonRegressionTest.tester.assertComponent("createGameLink", AjaxLink.class);
		NonRegressionTest.tester.clickLink("createGameLink", true);

		NonRegressionTest.pageDocument = NonRegressionTest.tester.getLastResponse().getDocument();

		final FormTester createGameForm = NonRegressionTest.tester
				.newFormTester("createGameWindow:content:form");
		createGameForm.setValue("name", "Zala");
		createGameForm.setValue("sideInput", "0");
		createGameForm.setValue("deckParent:decks", "0");
		createGameForm.submit();

		// Retrieve PlayCardFromHandBehavior
		NonRegressionTest.tester.assertComponent("playCardPlaceholder", WebMarkupContainer.class);
		NonRegressionTest.tester.assertComponent("playCardPlaceholder:playCardLink",
				WebMarkupContainer.class);
		final WebMarkupContainer playCardLink = (WebMarkupContainer)NonRegressionTest.tester
				.getComponentFromLastRenderedPage("playCardPlaceholder:playCardLink");
		final PlayCardFromHandBehavior pcfhb = (PlayCardFromHandBehavior)playCardLink
				.getBehaviors().get(0);

		// For the moment, we should have no card in the battlefield
		final Long gameId = HatchetHarrySession.get().getGameId();
		final PersistenceService persistenceService = NonRegressionTest.context
				.getBean(PersistenceService.class);
		List<MagicCard> allCardsInBattlefield = persistenceService
				.getAllCardsInBattleFieldForAGame(gameId);
		Assert.assertEquals(0, allCardsInBattlefield.size());

		// Play two cards
		NonRegressionTest.tester.getRequest().setParameter("card",
				HatchetHarrySession.get().getFirstCardsInHand().get(0).getUuid());
		NonRegressionTest.tester.executeBehavior(pcfhb);

		NonRegressionTest.tester.getRequest().setParameter("card",
				HatchetHarrySession.get().getFirstCardsInHand().get(1).getUuid());
		NonRegressionTest.tester.executeBehavior(pcfhb);

		// We should have two cards on the battlefield
		// Of coordinates (100l + currentPlaceholderId*16) & (100l +
		// (currentPlaceholderId+1)*16)
		allCardsInBattlefield = persistenceService.getAllCardsInBattleFieldForAGame(gameId);
		Assert.assertEquals(2, allCardsInBattlefield.size());

		final MagicCard firstCard = persistenceService.getCardFromUuid(UUID
				.fromString(allCardsInBattlefield.get(0).getUuid()));
		Assert.assertTrue((firstCard.getX().longValue() == 116l)
				|| (firstCard.getX().longValue() == 132l) || (firstCard.getX().longValue() == 148l));
		Assert.assertTrue((firstCard.getY().longValue() == 116l)
				|| (firstCard.getY().longValue() == 132l) || (firstCard.getY().longValue() == 148l));

		final MagicCard secondCard = persistenceService.getCardFromUuid(UUID
				.fromString(allCardsInBattlefield.get(1).getUuid()));
		Assert.assertTrue((secondCard.getX().longValue() == 116l)
				|| (secondCard.getX().longValue() == 132l)
				|| (secondCard.getX().longValue() == 148l));
		Assert.assertTrue((secondCard.getY().longValue() == 116l)
				|| (secondCard.getY().longValue() == 132l)
				|| (secondCard.getY().longValue() == 148l));

		// Move the two cards
		NonRegressionTest.tester.assertRenderedPage(HomePage.class);
		final HomePage hp = (HomePage)NonRegressionTest.tester.getLastRenderedPage();
		final WebMarkupContainer firstCardButton = (WebMarkupContainer)hp
				.get("parentPlaceholder:handCards:0:cardPanel:cardHandle:menutoggleButton");
		Assert.assertNotNull(firstCardButton);
		final CardMoveBehavior firstMoveBehavior = firstCardButton.getBehaviors(
				CardMoveBehavior.class).get(0);
		Assert.assertNotNull(firstMoveBehavior);

		final WebMarkupContainer secondCardPanelButton = (WebMarkupContainer)hp
				.get("parentPlaceholder:handCards:1:cardPanel:cardHandle:menutoggleButton");
		Assert.assertNotNull(secondCardPanelButton);
		final CardMoveBehavior secondMoveBehavior = secondCardPanelButton.getBehaviors(
				CardMoveBehavior.class).get(0);
		Assert.assertNotNull(secondMoveBehavior);

		NonRegressionTest.tester.getRequest().setParameter("posX", "100");
		NonRegressionTest.tester.getRequest().setParameter("posY", "100");
		NonRegressionTest.tester.executeBehavior(firstMoveBehavior);

		NonRegressionTest.tester.getRequest().setParameter("posX", "200");
		NonRegressionTest.tester.getRequest().setParameter("posY", "200");
		NonRegressionTest.tester.executeBehavior(secondMoveBehavior);

		final MagicCard _firstCard = persistenceService.getCardFromUuid(UUID
				.fromString(allCardsInBattlefield.get(0).getUuid()));
		Assert.assertTrue(_firstCard.getX().longValue() == 100l);
		Assert.assertTrue(_firstCard.getY().longValue() == 100l);

		MagicCard _secondCard = persistenceService.getCardFromUuid(UUID
				.fromString(allCardsInBattlefield.get(1).getUuid()));
		Assert.assertTrue((_secondCard.getX().longValue() == 200l));
		Assert.assertTrue((_secondCard.getY().longValue() == 200l));

		// Put the first card to graveyard
		final PutToGraveyardFromBattlefieldBehavior graveyardBehavior = firstCardButton
				.getBehaviors(PutToGraveyardFromBattlefieldBehavior.class).get(0);
		Assert.assertNotNull(graveyardBehavior);
		NonRegressionTest.tester.executeBehavior(graveyardBehavior);

		// We expect the second card not to move
		_secondCard = persistenceService.getCardFromUuid(UUID.fromString(allCardsInBattlefield.get(
				1).getUuid()));
		Assert.assertTrue((_secondCard.getX().longValue() == 200l));
		Assert.assertTrue((_secondCard.getY().longValue() == 200l));
	}
}
