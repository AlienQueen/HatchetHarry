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
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.alienlabs.hatchetharry.HatchetHarrySession;
import org.alienlabs.hatchetharry.model.Deck;
import org.alienlabs.hatchetharry.model.Game;
import org.alienlabs.hatchetharry.model.MagicCard;
import org.alienlabs.hatchetharry.model.Player;
import org.alienlabs.hatchetharry.service.PersistenceService;
import org.alienlabs.hatchetharry.view.component.AboutModalWindow;
import org.alienlabs.hatchetharry.view.component.CardPanel;
import org.alienlabs.hatchetharry.view.component.ChatPanel;
import org.alienlabs.hatchetharry.view.component.ClockPanel;
import org.alienlabs.hatchetharry.view.component.CreateGameModalWindow;
import org.alienlabs.hatchetharry.view.component.DataBox;
import org.alienlabs.hatchetharry.view.component.HandComponent;
import org.alienlabs.hatchetharry.view.component.JoinGameModalWindow;
import org.alienlabs.hatchetharry.view.component.PlayCardFromHandBehavior;
import org.alienlabs.hatchetharry.view.component.TeamInfoModalWindow;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.CSSPackageResource;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.resources.CompressedResourceReference;
import org.apache.wicket.markup.html.resources.JavaScriptReference;
import org.apache.wicket.protocol.http.servlet.ServletWebRequest;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.atmosphere.cpr.AtmosphereResourceEvent;
import org.atmosphere.cpr.AtmosphereResourceEventListener;
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

	static final Logger logger = LoggerFactory.getLogger(HomePage.class);

	@SpringBean
	PersistenceService persistenceService;
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

	private final WebMarkupContainer handCardsPlaceholder;
	WebMarkupContainer thumbsPlaceholder;

	public HomePage()
	{
		this.setOutputMarkupId(true);

		// Resources
		this.addHeadResources();

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
		this.add(new Label("message", "version 0.0.3 built on Thursday, 1st of September 2011"));

		// Comet clock channel
		this.add(new ClockPanel("clockPanel"));

		this.generateAboutLink();
		this.generateTeamInfoLink();

		if (!HatchetHarrySession.get().isGameCreated())
		{
			HatchetHarrySession.get().setGameId(this.createPlayer());
		}

		// Placeholders for CardPanel-adding with AjaxRequestTarget
		this.createCardPanelPlaceholders();

		// Hand
		this.buildHandCards();
		this.buildHandMarkup();


		this.generatePlayCardLink(this.hand);
		this.generatePlayCardsBehaviorsForAllOpponents();

		final WebMarkupContainer balduParent = new WebMarkupContainer("balduParent");
		balduParent.setOutputMarkupId(true);
		final MagicCard card = this.persistenceService.findCardByName("Balduvian Horde");
		balduParent.add(new CardPanel("baldu", card.getSmallImageFilename(), card
				.getBigImageFilename(), card.getUuidObject()));
		this.add(balduParent);

		this.player = HatchetHarrySession.get().getPlayer();
		this.generateCreateGameLink(this.player);
		this.generateJoinGameLink(this.player, balduParent, this.handCardsPlaceholder);

		// Comet chat channel
		this.add(new ChatPanel("chatPanel", this.player.getId()));

		this.buildDataBox(HatchetHarrySession.get().getGameId());
	}

	private void buildDataBox(final long _gameId)
	{
		this.add(new DataBox("dataBox", _gameId));
	}

	public synchronized void buildHandCards()
	{
		if (HatchetHarrySession.get().getHandHasBeenCreated())
		{
			this.hand = HatchetHarrySession.get().getFirstCardsInHand();
		}
		else
		{
			this.hand = this.createFirstCards();
		}
	}

	protected synchronized long createPlayer()
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

	@SuppressWarnings("unchecked")
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
		this.deck.setCards((List<MagicCard>)this.persistenceService.getAllCardsFromDeck(id));
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

		this.playCardLink.add(new PlayCardFromHandBehavior(this.playCardParent1,
				this.handCardsPlaceholder, mc.get(0).getUuidObject(), 0));

		this.playCardLink.setMarkupId("playCardLink0");
		this.playCardLink.setOutputMarkupId(true);
		playCardPlaceholder.add(this.playCardLink);

		this.add(playCardPlaceholder);
	}

	protected void createCardPanelPlaceholders()
	{
		final WebMarkupContainer cardPlaceholder = new WebMarkupContainer("cardPlaceholder");
		cardPlaceholder.setOutputMarkupId(true);
		this.add(cardPlaceholder);
	}

	protected void addHeadResources()
	{
		this.add(new JavaScriptReference("jQuery-1.6.2.js", HomePage.class,
				"script/jquery/jquery-1.6.2.min.js"));
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

		this.add(CSSPackageResource.getHeaderContribution(new CompressedResourceReference(
				HomePage.class, "stylesheet/menu.css")));
		this.add(CSSPackageResource.getHeaderContribution(new CompressedResourceReference(
				HomePage.class, "stylesheet/layout.css")));
		this.add(CSSPackageResource.getHeaderContribution(new CompressedResourceReference(
				HomePage.class, "stylesheet/menu_black.css")));
		this.add(CSSPackageResource.getHeaderContribution(new CompressedResourceReference(
				HomePage.class, "stylesheet/jquery.jquerytour.css")));
		this.add(CSSPackageResource.getHeaderContribution(new CompressedResourceReference(
				HomePage.class, "stylesheet/myStyle.css")));
		this.add(CSSPackageResource.getHeaderContribution(new CompressedResourceReference(
				HomePage.class, "stylesheet/galleryStyle.css")));

		this.add(CSSPackageResource.getHeaderContribution(new CompressedResourceReference(
				HomePage.class, "stylesheet/fixed4all.css")));
		this.add(CSSPackageResource.getHeaderContribution(new CompressedResourceReference(
				HomePage.class, "stylesheet/fixed4ie.css")));
		this.add(CSSPackageResource.getHeaderContribution(new CompressedResourceReference(
				HomePage.class, "stylesheet/prettyPhoto.css")));
		this.add(CSSPackageResource.getHeaderContribution(new CompressedResourceReference(
				HomePage.class, "stylesheet/toolbarStyle.css")));
		this.add(CSSPackageResource.getHeaderContribution(new CompressedResourceReference(
				HomePage.class, "stylesheet/tipsy.css")));
		this.add(new JavaScriptReference("jquery.prettyPhoto.js", HomePage.class,
				"script/toolbar/jquery.prettyPhoto.js"));
		this.add(new JavaScriptReference("jquery.tipsy.js", HomePage.class,
				"script/toolbar/jquery.tipsy.js"));
	}

	protected void buildHandMarkup()
	{

		final HandComponent gallery = new HandComponent("gallery");
		gallery.setOutputMarkupId(true);
		this.handCardsPlaceholder.add(gallery);
	}

	@SuppressWarnings("unchecked")
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
			this.deck.setCards((List<MagicCard>)this.persistenceService
					.getAllCardsFromDeck(this.deck.getId()));
			final List<MagicCard> cards = new ArrayList<MagicCard>();

			if (!HatchetHarrySession.get().getHandCardsHaveBeenBuilt())
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

	protected void generateCreateGameLink(final Player _player)
	{
		this.createGameWindow = new ModalWindow("createGameWindow");
		this.createGameWindow.setInitialWidth(475);
		this.createGameWindow.setInitialHeight(290);
		this.createGameWindow.setTitle("Create a game");

		this.createGameWindow.setContent(new CreateGameModalWindow(this.createGameWindow,
				this.createGameWindow.getContentId(), _player));
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
			final WebMarkupContainer _balduParent, final WebMarkupContainer _handCardsParent)
	{
		this.joinGameWindow = new ModalWindow("joinGameWindow");
		this.joinGameWindow.setInitialWidth(475);
		this.joinGameWindow.setInitialHeight(290);
		this.joinGameWindow.setTitle("Create a game");

		this.joinGameWindow.setContent(new JoinGameModalWindow(this.joinGameWindow,
				this.joinGameWindow.getContentId(), _player, _balduParent, _handCardsParent,
				this.thumbsPlaceholder));
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
		final Locale originalLocale = HatchetHarrySession.get().getLocale();
		HatchetHarrySession.get().setLocale(Locale.ENGLISH);
		super.configureResponse();

		final String encoding = "text/html;charset=utf-8";

		this.getResponse().setContentType(encoding);
		HatchetHarrySession.get().setLocale(originalLocale);
	}

	@Required
	public void setPersistenceService(final PersistenceService _persistenceService)
	{
		this.persistenceService = _persistenceService;
	}

}
