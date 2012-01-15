/*
 *
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2007-2008 Sun Microsystems, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License. You can obtain
 * a copy of the License at https://glassfish.dev.java.net/public/CDDL+GPL.html
 * or glassfish/bootstrap/legal/LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at glassfish/bootstrap/legal/LICENSE.txt.
 * Sun designates this particular file as subject to the "Classpath" exception
 * as provided by Sun in the GPL Version 2 section of the License file that
 * accompanied this code.  If applicable, add the following below the License
 * Header, with the fields enclosed by brackets [] replaced by your own
 * identifying information: "Portions Copyrighted [year]
 * [name of copyright owner]"
 *
 * Contributor(s):
 *
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 *
 */
package org.alienlabs.hatchetharry.view.page;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.alienlabs.hatchetharry.HatchetHarryApplication;
import org.alienlabs.hatchetharry.HatchetHarrySession;
import org.alienlabs.hatchetharry.model.Deck;
import org.alienlabs.hatchetharry.model.Game;
import org.alienlabs.hatchetharry.model.MagicCard;
import org.alienlabs.hatchetharry.model.Player;
import org.alienlabs.hatchetharry.service.PersistenceService;
import org.alienlabs.hatchetharry.service.RuntimeDataGenerator;
import org.alienlabs.hatchetharry.view.component.AboutModalWindow;
import org.alienlabs.hatchetharry.view.component.CardPanel;
import org.alienlabs.hatchetharry.view.component.ChatPanel;
import org.alienlabs.hatchetharry.view.component.ClockPanel;
import org.alienlabs.hatchetharry.view.component.CreateGameModalWindow;
import org.alienlabs.hatchetharry.view.component.DataBox;
import org.alienlabs.hatchetharry.view.component.GameNotifierBehavior;
import org.alienlabs.hatchetharry.view.component.HandComponent;
import org.alienlabs.hatchetharry.view.component.JoinGameModalWindow;
import org.alienlabs.hatchetharry.view.component.NotifierPanel;
import org.alienlabs.hatchetharry.view.component.PlayCardFromHandBehavior;
import org.alienlabs.hatchetharry.view.component.TeamInfoModalWindow;
import org.alienlabs.hatchetharry.view.component.UpdateDataBoxBehavior;
import org.apache.wicket.Application;
import org.apache.wicket.ResourceReference;
import org.apache.wicket.Session;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.CSSPackageResource;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.resources.JavaScriptReference;
import org.apache.wicket.protocol.http.servlet.ServletWebRequest;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.atmosphere.cpr.AtmosphereResourceEvent;
import org.atmosphere.cpr.AtmosphereResourceEventListener;
import org.atmosphere.cpr.BroadcastFilter;
import org.atmosphere.cpr.Meteor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import ch.qos.mistletoe.wicket.TestReportPage;

/**
 * Bootstrap class
 * 
 * @author Andrey Belyaev
 */
public class HomePage extends TestReportPage implements AtmosphereResourceEventListener
{
	private static final long serialVersionUID = 1L;

	static final Logger logger = LoggerFactory.getLogger(HomePage.class);

	@SpringBean
	transient PersistenceService persistenceService;
	@SpringBean
	transient RuntimeDataGenerator runtimeDataGenerator;

	ModalWindow teamInfoWindow;
	ModalWindow aboutWindow;
	ModalWindow createGameWindow;
	ModalWindow joinGameWindow;

	Player player;
	Deck deck;
	BookmarkablePageLink<PlayCardPage> playCardPage;
	List<MagicCard> hand;
	private final WebMarkupContainer parentPlaceholder;
	WebMarkupContainer playCardLink;
	WebMarkupContainer playCardParent1;

	final WebMarkupContainer handCardsPlaceholder;
	WebMarkupContainer thumbsPlaceholder;

	private AjaxLink<Void> endTurnLink;

	WebMarkupContainer endTurnPlaceholder;

	public NotifierPanel notifierPanel;

	private WebMarkupContainer dataBoxParent;

	private DataBox dataBox;

	private final BookmarkablePageLink<SidePlaceholderMovePage> sidePlaceholderMove;

	private final WebMarkupContainer firstSidePlaceholderParent;
	private final WebMarkupContainer secondSidePlaceholderParent;

	private PlayCardFromHandBehavior playCardBehavior;

	public HomePage()
	{
		this.setOutputMarkupId(true);

		// Resources
		this.addHeadResources();

		this.add(new BookmarkablePageLink<NotifierPage>("notifierStart", NotifierPage.class));

		this.parentPlaceholder = new WebMarkupContainer("cardParent");
		this.parentPlaceholder.setOutputMarkupId(true);

		this.playCardParent1 = new WebMarkupContainer("playCardParentPlaceholder1");
		this.playCardParent1.setOutputMarkupId(true);
		this.parentPlaceholder.add(this.playCardParent1);
		this.add(this.parentPlaceholder);

		this.handCardsPlaceholder = new WebMarkupContainer("handCardsPlaceholder");
		this.handCardsPlaceholder.setMarkupId("handCardsPlaceholder");
		this.handCardsPlaceholder.setOutputMarkupId(true);
		this.add(this.handCardsPlaceholder);
		// Welcome message
		this.add(new Label("message",
				"version 0.0.5 (release EEV), built on Sunday, 15th of January 2012."));

		// Comet clock channel
		this.add(new ClockPanel("clockPanel"));


		if (!HatchetHarrySession.get().isGameCreated())
		{
			HatchetHarrySession.get().setGameId(this.createPlayer());
		}
		else
		{
			this.player = HatchetHarrySession.get().getPlayer();
		}

		this.deck = this.persistenceService.getDeck(1l);
		if (null == this.deck)
		{
			this.runtimeDataGenerator.generateData();
			this.deck = this.persistenceService.getDeck(1l);
			this.persistenceService.saveDeck(this.deck);
		}

		this.deck = this.persistenceService.getDeck(2l);
		if (null == this.deck)
		{
			this.runtimeDataGenerator.generateData();
			this.deck = this.persistenceService.getDeck(2l);
			this.persistenceService.saveDeck(this.deck);
		}

		// Placeholders for CardPanel-adding with AjaxRequestTarget
		this.createCardPanelPlaceholders();

		// Hand
		this.buildHandCards();
		this.buildHandMarkup();


		final WebMarkupContainer balduParent = new WebMarkupContainer("balduParent");
		balduParent.setOutputMarkupId(true);

		final MagicCard card = this.persistenceService.findCardByName("Balduvian Horde");
		if (null != card)
		{
			balduParent.add(new CardPanel("baldu", card.getSmallImageFilename(), card
					.getBigImageFilename(), card.getUuidObject()));
		}
		else
		{
			balduParent.add(new WebMarkupContainer("baldu"));
		}
		this.add(balduParent);

		this.buildDataBox(HatchetHarrySession.get().getGameId());

		this.generateAboutLink();
		this.generateTeamInfoLink();

		final GameNotifierBehavior notif = new GameNotifierBehavior(this);
		this.add(notif);

		final ServletWebRequest servletWebRequest = (ServletWebRequest)this.getPage().getRequest();
		final HttpServletRequest request = servletWebRequest.getHttpServletRequest();
		request.getRequestedSessionId();

		this.sidePlaceholderMove = new BookmarkablePageLink<SidePlaceholderMovePage>(
				"sidePlaceholderMove", SidePlaceholderMovePage.class);
		this.sidePlaceholderMove.add(new SimpleAttributeModifier("id", "sidePlaceholderMove"));
		this.add(this.sidePlaceholderMove);

		this.secondSidePlaceholderParent = new WebMarkupContainer("secondSidePlaceholderParent");
		this.secondSidePlaceholderParent.setOutputMarkupId(true);
		// secondSidePlaceholderParent.add(new SidePlaceholderMoveBehavior(
		// secondSidePlaceholderParent, UUID.randomUUID(), jsessionid, this));
		final WebMarkupContainer secondSidePlaceholder = new WebMarkupContainer(
				"secondSidePlaceholder");
		secondSidePlaceholder.setOutputMarkupId(true);
		this.secondSidePlaceholderParent.add(secondSidePlaceholder);

		this.firstSidePlaceholderParent = new WebMarkupContainer("firstSidePlaceholderParent");
		this.firstSidePlaceholderParent.setOutputMarkupId(true);
		// firstSidePlaceholderParent.add(new
		// SidePlaceholderMoveBehavior(firstSidePlaceholderParent,
		// UUID.randomUUID(), jsessionid, this));
		final WebMarkupContainer firstSidePlaceholder = new WebMarkupContainer(
				"firstSidePlaceholder");
		firstSidePlaceholder.setOutputMarkupId(true);
		this.firstSidePlaceholderParent.add(firstSidePlaceholder);

		this.add(this.secondSidePlaceholderParent, this.firstSidePlaceholderParent);

		this.generateCreateGameLink(this.player, this.handCardsPlaceholder, notif.getCallbackUrl(),
				this.firstSidePlaceholderParent);
		this.generateJoinGameLink(this.player, this.handCardsPlaceholder, notif.getCallbackUrl(),
				this.secondSidePlaceholderParent);

		this.generatePlayCardLink(this.hand);
		this.generatePlayCardsBehaviorsForAllOpponents();

		this.generateDrawCardLink();

		// Comet chat channel
		this.add(new ChatPanel("chatPanel", this.player.getId()));

		this.buildEndTurnLink();
	}

	private void buildEndTurnLink()
	{
		this.endTurnPlaceholder = new WebMarkupContainer("endTurnPlaceholder");
		this.endTurnPlaceholder.setMarkupId("endTurnPlaceholder");
		this.endTurnPlaceholder.setOutputMarkupId(true);

		this.notifierPanel = new NotifierPanel("notifierPanel", HomePage.this, HatchetHarrySession
				.get().getPlayer().getSide(), "has declared the end of his turn.");
		this.notifierPanel.setOutputMarkupId(true);

		this.endTurnLink = new AjaxLink<Void>("endTurnLink")
		{
			private static final long serialVersionUID = 6590465665519989765L;

			@Override
			public void onClick(final AjaxRequestTarget target)
			{
				target.appendJavascript("wicketAjaxGet('"
						+ HomePage.this.notifierPanel.getCallbackUrl()
						+ "&title="
						+ HatchetHarrySession.get().getPlayer().getName()
						+ "&text=has declared the end of his turn.&show=true', function() { }, null, null);");
			}

		};
		this.endTurnLink.setMarkupId("endTurnLink");
		this.endTurnLink.setOutputMarkupId(true);

		this.endTurnPlaceholder.add(this.endTurnLink, this.notifierPanel);
		this.add(this.endTurnPlaceholder);
	}

	private void buildDataBox(final long _gameId)
	{
		this.dataBoxParent = new WebMarkupContainer("dataBoxParent");
		this.dataBoxParent.setOutputMarkupId(true);

		final UpdateDataBoxBehavior behavior = new UpdateDataBoxBehavior(this.dataBoxParent,
				_gameId, this);
		this.dataBox = new DataBox("dataBox", _gameId, this.dataBoxParent, this);
		HatchetHarrySession.get().setDataBox(this.dataBox);
		this.dataBox.add(behavior);
		this.dataBox.setOutputMarkupId(true);
		this.dataBoxParent.add(this.dataBox);

		this.add(this.dataBoxParent);
	}

	public void buildHandCards()
	{
		if (HatchetHarrySession.get().isHandHasBeenCreated())
		{
			this.hand = HatchetHarrySession.get().getFirstCardsInHand();
		}
		else
		{
			this.hand = this.createFirstCards();
		}
	}

	protected long createPlayer()
	{
		final ServletWebRequest servletWebRequest = (ServletWebRequest)this.getPage().getRequest();
		final HttpServletRequest request = servletWebRequest.getHttpServletRequest();
		final String jsessionid = request.getRequestedSessionId();

		if (this.persistenceService.getFirstPlayer() == null)
		{
			return this.createPlayerAndDeck(jsessionid, "infrared", "infrared", 20l, 1l);
		}
		return this.createPlayerAndDeck(jsessionid, "ultraviolet", "ultraviolet", 20l, 2l);
	}

	private Long createPlayerAndDeck(final String _jsessionid, final String _side,
			final String _name, final Long _lifePoints, final Long id)
	{
		final Player p = new Player();
		p.setSide(_side);
		p.setName(_name);
		p.setJsessionid(_jsessionid);
		p.setLifePoints(_lifePoints);
		p.setId(this.persistenceService.savePlayer(p));

		final List<Game> games = new ArrayList<Game>();
		games.add(this.persistenceService.createNewGame(p));
		p.setGame(games);

		HatchetHarrySession.get().setPlayerHasBeenCreated();
		HatchetHarrySession.get().setPlayer(p);
		HatchetHarrySession.get().setPlayerLetter(p.getId() == 1 ? "a" : "b");
		HatchetHarrySession.get().setPlaceholderNumber(1);

		this.deck = this.persistenceService.getDeck(id);
		if (null == this.deck)
		{
			this.runtimeDataGenerator.generateData();
			this.deck = this.persistenceService.getDeck(id);
		}
		this.deck.setCards(this.persistenceService.getAllCardsFromDeck(id));
		this.deck.setCards(this.deck.shuffleLibrary());
		this.deck.setPlayerId(id);
		this.deck = this.persistenceService.saveDeck(this.deck);

		HatchetHarrySession.get().setPlayer(p);
		HatchetHarrySession.get().setGameCreated();
		this.player = p;
		return p.getId();
	}

	private void generatePlayCardsBehaviorsForAnOpponent(final String opponentId)
	{
		for (int i = 1; i < 61; i++)
		{
			final WebMarkupContainer cardPlaceholder = new WebMarkupContainer("cardPlaceholder"
					+ opponentId + i);
			this.playCardParent1.add(cardPlaceholder);
		}
	}

	private void generatePlayCardsBehaviorsForAllOpponents()
	{
		this.generatePlayCardsBehaviorsForAnOpponent("a");
		this.generatePlayCardsBehaviorsForAnOpponent("b");
	}

	private void generatePlayCardLink(final List<MagicCard> mc)
	{
		final WebMarkupContainer playCardPlaceholder = new WebMarkupContainer("playCardPlaceholder");
		playCardPlaceholder.setMarkupId("playCardPlaceholder0");
		playCardPlaceholder.setOutputMarkupId(true);
		HomePage.logger.info("Generating link");

		this.playCardLink = new WebMarkupContainer("playCardLink");
		this.playCardLink.setMarkupId("playCardPlaceholder0");
		this.playCardLink.setOutputMarkupId(true);

		if (mc.size() > 0)
		{
			this.playCardBehavior = new PlayCardFromHandBehavior(this.playCardParent1,
					this.handCardsPlaceholder, mc.get(0).getUuidObject(), 0,
					((HatchetHarrySession)Session.get()).getPlayer().getSide());
			this.playCardLink.add(this.playCardBehavior);
		}

		this.playCardLink.setMarkupId("playCardLink0");
		this.playCardLink.setOutputMarkupId(true);
		playCardPlaceholder.add(this.playCardLink);

		this.add(playCardPlaceholder);
	}

	private void generateDrawCardLink()
	{
		final AjaxLink<String> drawCardLink = new AjaxLink<String>("drawCardLink")
		{
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(final AjaxRequestTarget target)
			{
				if ((HatchetHarrySession.get().getDeck() != null)
						&& (HatchetHarrySession.get().getDeck().getCards() != null)
						&& (HatchetHarrySession.get().getDeck().getCards().size() > 0))
				{
					final MagicCard card = HatchetHarrySession.get().getDeck().getCards().get(0);
					final List<MagicCard> list = HatchetHarrySession.get().getFirstCardsInHand();
					list.add(card);

					final Deck d = HatchetHarrySession.get().getDeck();
					final List<MagicCard> deckList = d.getCards();
					deckList.remove(card);
					d.setCards(deckList);

					HatchetHarrySession.get().setFirstCardsInHand(list);
					HatchetHarrySession.get().setDeck(d);

					final HandComponent gallery = new HandComponent("gallery");
					gallery.setOutputMarkupId(true);

					((HatchetHarryApplication)Application.get()).setPlayer(HatchetHarrySession
							.get().getPlayer());

					HomePage.this.handCardsPlaceholder.addOrReplace(gallery);
					target.addComponent(HomePage.this.handCardsPlaceholder);
					target.appendJavascript("jQuery(document).ready(function() { var theInt = null; var $crosslink, $navthumb; var curclicked = 0; theInterval = function(cur) { if (typeof cur != 'undefined') curclicked = cur; $crosslink.removeClass('active-thumb'); $navthumb.eq(curclicked).parent().addClass('active-thumb'); jQuery('.stripNav ul li a').eq(curclicked).trigger('click'); $crosslink.removeClass('active-thumb'); $navthumb.eq(curclicked).parent().addClass('active-thumb'); jQuery('.stripNav ul li a').eq(curclicked).trigger('click'); curclicked++; if (6 == curclicked) curclicked = 0; }; jQuery('#main-photo-slider').codaSlider(); $navthumb = jQuery('.nav-thumb'); $crosslink = jQuery('.cross-link'); $navthumb.click(function() { var $this = jQuery(this); theInterval($this.parent().attr('href').slice(1) - 1); return false; }); theInterval(); });");

					final ServletWebRequest servletWebRequest = (ServletWebRequest)target.getPage()
							.getRequest();
					final HttpServletRequest request = servletWebRequest.getHttpServletRequest();
					final Meteor meteor = Meteor.build(request, new LinkedList<BroadcastFilter>(),
							null);
					meteor.addListener((AtmosphereResourceEventListener)target.getPage());
					final String message = HatchetHarrySession.get().getPlayer().getSide() + ":::"
							+ "has drawn a card!" + ":::" + request.getRequestedSessionId()
							+ ":::padding";
					meteor.broadcast(message);
				}
			}
		};
		this.add(drawCardLink);


	}

	protected void createCardPanelPlaceholders()
	{
		final WebMarkupContainer cardPlaceholder = new WebMarkupContainer("cardPlaceholder");
		cardPlaceholder.setOutputMarkupId(true);
		this.add(cardPlaceholder);
	}

	protected void addHeadResources()
	{
		// this.add(new JavaScriptReference("jquery-1.6.4.js", HomePage.class,
		// "script/jquery-1.6.4.js"));
		// this.add(new JavaScriptReference("jquery.atmosphere.js",
		// HomePage.class,
		// "script/jquery.atmosphere.js"));
		this.add(new JavaScriptReference("jquery.easing.1.3.js", HomePage.class,
				"script/tour/jquery.easing.1.3.js"));
		this.add(new JavaScriptReference("jquery.storage.js", HomePage.class,
				"script/tour/jquery.storage.js"));
		this.add(new JavaScriptReference("jquery.tour.js", HomePage.class,
				"script/tour/jquery.tour.js"));
		this.add(new JavaScriptReference("jquery.metadata.js", HomePage.class,
				"script/menubar/jquery.metadata.js"));
		this.add(new JavaScriptReference("jquery.hoverIntent.js", HomePage.class,
				"script/menubar/jquery.hoverIntent.js"));
		this.add(new JavaScriptReference("mbMenu.js", HomePage.class, "script/menubar/mbMenu.js"));
		this.add(new JavaScriptReference("jqDock.js", HomePage.class,
				"script/menubar/jquery.jqDock.js"));
		this.add(new JavaScriptReference("qUnit.js", HomePage.class, "script/qunitTests/qUnit.js"));
		this.add(new JavaScriptReference("codeUnderTest.js", HomePage.class,
				"script/qunitTests/codeUnderTest.js"));
		this.add(new JavaScriptReference("HomePageTests.js", HomePage.class,
				"script/qunitTests/HomePageTests.js"));

		this.add(new JavaScriptReference("mootools.v1.11", HomePage.class,
				"script/jquery/mootools.v1.11.js"));
		this.add(new JavaScriptReference("jquery-easing-1.3.pack.js", HomePage.class,
				"script/gallery/jquery-easing-1.3.pack.js"));
		this.add(new JavaScriptReference("jquery-easing-compatibility.1.2.pack.js", HomePage.class,
				"script/gallery/jquery-easing-compatibility.1.2.pack.js"));
		this.add(new JavaScriptReference("coda-slider.1.1.1.pack.js", HomePage.class,
				"script/gallery/coda-slider.1.1.1.pack.js"));
		this.add(new JavaScriptReference("gallery.js", HomePage.class, "script/gallery/gallery.js"));

		this.add(new JavaScriptReference("jQueryRotate.2.1.js", HomePage.class,
				"script/rotate/jQueryRotate.2.1.js"));

		this.add(CSSPackageResource.getHeaderContribution(new ResourceReference(HomePage.class,
				"stylesheet/menu.css")));
		this.add(CSSPackageResource.getHeaderContribution(new ResourceReference(HomePage.class,
				"stylesheet/layout.css")));
		this.add(CSSPackageResource.getHeaderContribution(new ResourceReference(HomePage.class,
				"stylesheet/menu_black.css")));
		this.add(CSSPackageResource.getHeaderContribution(new ResourceReference(HomePage.class,
				"stylesheet/jquery.jquerytour.css")));
		this.add(CSSPackageResource.getHeaderContribution(new ResourceReference(HomePage.class,
				"stylesheet/myStyle.css")));
		this.add(CSSPackageResource.getHeaderContribution(new ResourceReference(HomePage.class,
				"stylesheet/galleryStyle.css")));
		this.add(CSSPackageResource.getHeaderContribution(new ResourceReference(HomePage.class,
				"stylesheet/jquery.gritter.css")));

		this.add(CSSPackageResource.getHeaderContribution(new ResourceReference(HomePage.class,
				"stylesheet/fixed4all.css")));
		this.add(CSSPackageResource.getHeaderContribution(new ResourceReference(HomePage.class,
				"stylesheet/fixed4ie.css")));
		this.add(CSSPackageResource.getHeaderContribution(new ResourceReference(HomePage.class,
				"stylesheet/prettyPhoto.css")));
		this.add(CSSPackageResource.getHeaderContribution(new ResourceReference(HomePage.class,
				"stylesheet/toolbarStyle.css")));
		this.add(CSSPackageResource.getHeaderContribution(new ResourceReference(HomePage.class,
				"stylesheet/tipsy.css")));
		this.add(new JavaScriptReference("jquery.prettyPhoto.js", HomePage.class,
				"script/toolbar/jquery.prettyPhoto.js"));
		this.add(new JavaScriptReference("jquery.tipsy.js", HomePage.class,
				"script/toolbar/jquery.tipsy.js"));
		this.add(new JavaScriptReference("jquery.gritter.min.js", HomePage.class,
				"script/notifier/jquery.gritter.min.js"));
	}

	protected void buildHandMarkup()
	{

		final HandComponent gallery = new HandComponent("gallery");
		gallery.setOutputMarkupId(true);
		this.handCardsPlaceholder.add(gallery);
	}

	protected List<MagicCard> createFirstCards()
	{
		if (HatchetHarrySession.get().isPlayerCreated())
		{
			this.player = HatchetHarrySession.get().getPlayer();
			this.deck = this.persistenceService.getDeck(this.player.getId());
			if (this.deck == null)
			{
				this.deck = this.persistenceService.getDeck(1l);
			}
			this.deck.setCards(this.persistenceService.getAllCardsFromDeck(this.deck.getId()));
			final List<MagicCard> cards = new ArrayList<MagicCard>();

			if (!HatchetHarrySession.get().isHandCardsHaveBeenBuilt())
			{
				this.deck.shuffleLibrary();
			}

			for (int i = 0; i < 7; i++)
			{
				cards.add(i, this.deck.getCards().get(i));
				HatchetHarrySession.get().addCardIdInHand(i, i);
			}

			HatchetHarrySession.get().setFirstCardsInHand(cards);
			HatchetHarrySession.get().setHandHasBeenCreated();

			this.hand = cards;
			return cards;
		}

		return new ArrayList<MagicCard>();
	}

	protected void generateAboutLink()
	{
		this.aboutWindow = new ModalWindow("aboutWindow");
		this.aboutWindow.setInitialWidth(450);
		this.aboutWindow.setInitialHeight(675);
		this.aboutWindow.setTitle("About HatchetHarry");
		this.aboutWindow.setContent(new AboutModalWindow(this.aboutWindow.getContentId()));
		this.aboutWindow.setCssClassName(ModalWindow.CSS_CLASS_BLUE);
		this.aboutWindow.setMaskType(ModalWindow.MaskType.SEMI_TRANSPARENT);
		this.add(this.aboutWindow);

		final AjaxLink<Void> aboutLink = new AjaxLink<Void>("aboutLink")
		{
			private static final long serialVersionUID = 8140325977385015896L;

			@Override
			public void onClick(final AjaxRequestTarget target)
			{
				target.appendJavascript("Wicket.Window.unloadConfirmation = false;");
				HomePage.this.aboutWindow.show(target);
			}
		};

		aboutLink.setOutputMarkupId(true);
		this.aboutWindow.setOutputMarkupId(true);
		this.add(aboutLink);
	}

	protected void generateTeamInfoLink()
	{
		this.teamInfoWindow = new ModalWindow("teamInfoWindow");
		this.teamInfoWindow.setInitialWidth(475);
		this.teamInfoWindow.setInitialHeight(528);
		this.teamInfoWindow.setTitle("HatchetHarry Team info");
		this.teamInfoWindow.setContent(new TeamInfoModalWindow(this.teamInfoWindow.getContentId()));
		this.teamInfoWindow.setCssClassName(ModalWindow.CSS_CLASS_BLUE);
		this.teamInfoWindow.setMaskType(ModalWindow.MaskType.SEMI_TRANSPARENT);
		this.add(this.teamInfoWindow);

		final AjaxLink<Void> teamInfoLink = new AjaxLink<Void>("teamInfoLink")
		{
			private static final long serialVersionUID = 8140325977385015896L;

			@Override
			public void onClick(final AjaxRequestTarget target)
			{
				target.appendJavascript("Wicket.Window.unloadConfirmation = false;");
				HomePage.this.teamInfoWindow.show(target);
			}
		};

		teamInfoLink.setOutputMarkupId(true);
		this.teamInfoWindow.setOutputMarkupId(true);
		this.add(teamInfoLink);
	}

	protected void generateCreateGameLink(final Player _player,
			final WebMarkupContainer _handCardsParent, final CharSequence _url,
			final WebMarkupContainer sidePlaceholderParent)
	{
		this.createGameWindow = new ModalWindow("createGameWindow");
		this.createGameWindow.setInitialWidth(475);
		this.createGameWindow.setInitialHeight(290);
		this.createGameWindow.setTitle("Create a game");

		this.createGameWindow.setContent(new CreateGameModalWindow(this.createGameWindow,
				this.createGameWindow.getContentId(), _player, _handCardsParent, _url,
				sidePlaceholderParent, this));
		this.createGameWindow.setCssClassName(ModalWindow.CSS_CLASS_BLUE);
		this.createGameWindow.setMaskType(ModalWindow.MaskType.SEMI_TRANSPARENT);
		this.add(this.createGameWindow);

		final AjaxLink<Void> createGameLink = new AjaxLink<Void>("createGameLink")
		{
			private static final long serialVersionUID = 4097315677385015896L;

			@Override
			public void onClick(final AjaxRequestTarget target)
			{
				target.appendJavascript("Wicket.Window.unloadConfirmation = false;");
				HomePage.this.createGameWindow.show(target);
			}
		};

		createGameLink.setOutputMarkupId(true);
		this.createGameWindow.setOutputMarkupId(true);
		this.add(createGameLink);
	}

	protected void generateJoinGameLink(final Player _player,
			final WebMarkupContainer _handCardsParent, final CharSequence _url,
			final WebMarkupContainer sidePlaceholderParent)
	{
		this.joinGameWindow = new ModalWindow("joinGameWindow");
		this.joinGameWindow.setInitialWidth(475);
		this.joinGameWindow.setInitialHeight(290);
		this.joinGameWindow.setTitle("Join a game");

		this.joinGameWindow.setContent(new JoinGameModalWindow(this.joinGameWindow,
				this.joinGameWindow.getContentId(), _player, _handCardsParent, _url,
				this.dataBoxParent, this, sidePlaceholderParent));
		this.joinGameWindow.setCssClassName(ModalWindow.CSS_CLASS_BLUE);
		this.joinGameWindow.setMaskType(ModalWindow.MaskType.SEMI_TRANSPARENT);
		this.add(this.joinGameWindow);

		final AjaxLink<Void> createGameLink = new AjaxLink<Void>("joinGameLink")
		{
			private static final long serialVersionUID = 4097315677385015896L;

			@Override
			public void onClick(final AjaxRequestTarget target)
			{
				target.appendJavascript("Wicket.Window.unloadConfirmation = false;");
				HomePage.this.joinGameWindow.show(target);
			}
		};

		createGameLink.setOutputMarkupId(true);
		this.joinGameWindow.setOutputMarkupId(true);
		this.add(createGameLink);
	}

	@Override
	public void onBroadcast(
			final AtmosphereResourceEvent<HttpServletRequest, HttpServletResponse> event)
	{
		HomePage.logger.info("onBroadcast(): {}", event.getMessage());

		// If we are using long-polling, resume the connection as soon as we get
		// an event.
		final String transport = event.getResource().getRequest()
				.getHeader("X-Atmosphere-Transport");
		if ((transport != null) && transport.equalsIgnoreCase("long-polling"))
		{
			final Meteor meteor = Meteor.lookup(event.getResource().getRequest());
			meteor.removeListener(this);
			meteor.resume();
		}
	}

	@Override
	public void onSuspend(
			final AtmosphereResourceEvent<HttpServletRequest, HttpServletResponse> event)
	{
		final String transport = event.getResource().getRequest()
				.getHeader("X-Atmosphere-Transport");
		final HttpServletRequest req = event.getResource().getRequest();
		HomePage.logger.info("Suspending the %s response from ip {}:{}",
				new Object[] { transport == null ? "websocket" : transport, req.getRemoteAddr(),
						req.getRemotePort() });
	}

	@Override
	public void onResume(
			final AtmosphereResourceEvent<HttpServletRequest, HttpServletResponse> event)
	{
		final String transport = event.getResource().getRequest()
				.getHeader("X-Atmosphere-Transport");
		final HttpServletRequest req = event.getResource().getRequest();
		HomePage.logger.info("Resuming the {} response from ip {}:{}",
				new Object[] { transport == null ? "websocket" : transport, req.getRemoteAddr(),
						req.getRemotePort() });
	}

	@Override
	public void onDisconnect(
			final AtmosphereResourceEvent<HttpServletRequest, HttpServletResponse> event)
	{
		final String transport = event.getResource().getRequest()
				.getHeader("X-Atmosphere-Transport");
		final HttpServletRequest req = event.getResource().getRequest();
		HomePage.logger.info("{} connection dropped from ip {}:{}",
				new Object[] { transport == null ? "websocket" : transport, req.getRemoteAddr(),
						req.getRemotePort() });
	}

	@Override
	public void onThrowable(
			final AtmosphereResourceEvent<HttpServletRequest, HttpServletResponse> event)
	{
		HomePage.logger.info("onThrowable()", event.throwable());
	}

	@Override
	protected void configureResponse()
	{
		if (HatchetHarrySession.get() != null)
		{
			final Locale originalLocale = HatchetHarrySession.get().getLocale();
			HatchetHarrySession.get().setLocale(originalLocale);
		}

		final String encoding = "text/html;charset=utf-8";
		this.getResponse().setContentType(encoding);
		super.configureResponse();
	}

	@Required
	public void setPersistenceService(final PersistenceService _persistenceService)
	{
		this.persistenceService = _persistenceService;
	}

	@Required
	public void setRuntimeDataGenerator(final RuntimeDataGenerator _runtimeDataGenerator)
	{
		this.runtimeDataGenerator = _runtimeDataGenerator;
	}

	public WebMarkupContainer getFirstSidePlaceholderParent()
	{
		return this.firstSidePlaceholderParent;
	}

	public WebMarkupContainer getSecondSidePlaceholderParent()
	{
		return this.secondSidePlaceholderParent;
	}

	public PlayCardFromHandBehavior getPlayCardBehavior()
	{
		return this.playCardBehavior;
	}

}
