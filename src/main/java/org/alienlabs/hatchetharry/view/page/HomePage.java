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
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

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
import org.alienlabs.hatchetharry.view.component.SidePlaceholderMoveBehavior;
import org.alienlabs.hatchetharry.view.component.SidePlaceholderPanel;
import org.alienlabs.hatchetharry.view.component.TeamInfoModalWindow;
import org.alienlabs.hatchetharry.view.component.UntapAllBehavior;
import org.alienlabs.hatchetharry.view.component.UpdateDataBoxBehavior;
import org.apache.wicket.Application;
import org.apache.wicket.Component;
import org.apache.wicket.Session;
import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.atmosphere.Subscribe;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.model.Model;
import org.apache.wicket.protocol.http.servlet.ServletWebRequest;
import org.apache.wicket.request.http.WebResponse;
import org.apache.wicket.request.resource.PackageResourceReference;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.template.PackageTextTemplate;
import org.apache.wicket.util.template.TextTemplate;
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
public class HomePage extends TestReportPage
{
	private static final long serialVersionUID = 1L;

	static final Logger LOGGER = LoggerFactory.getLogger(HomePage.class);

	@SpringBean
	PersistenceService persistenceService;
	@SpringBean
	RuntimeDataGenerator runtimeDataGenerator;

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
	WebMarkupContainer playCardParent;

	final WebMarkupContainer handCardsPlaceholder;
	WebMarkupContainer thumbsPlaceholder;

	private AjaxLink<Void> endTurnLink;
	private AjaxLink<Void> untapAllLink;
	private AjaxLink<Void> untapAndDrawLink;

	UntapAllBehavior untapAllBehavior;

	private BookmarkablePageLink<UntapAllPage> untapAllPage;

	WebMarkupContainer endTurnPlaceholder;
	WebMarkupContainer untapAllPlaceholder;
	WebMarkupContainer untapAndDrawPlaceholder;

	public NotifierPanel notifierPanel;

	private WebMarkupContainer dataBoxParent;

	private DataBox dataBox;

	private final BookmarkablePageLink<SidePlaceholderMovePage> sidePlaceholderMove;

	private final WebMarkupContainer firstSidePlaceholderParent;
	private final WebMarkupContainer secondSidePlaceholderParent;

	private PlayCardFromHandBehavior playCardBehavior;
	ClockPanel clockPanel;

	public HomePage()
	{
		this.setOutputMarkupId(true);
		this.setVersioned(false);

		// Resources
		this.addHeadResources();

		this.add(new BookmarkablePageLink<NotifierPage>("notifierStart", NotifierPage.class));

		this.parentPlaceholder = new WebMarkupContainer("cardParent");
		this.parentPlaceholder.setOutputMarkupId(true);

		this.playCardParent = new WebMarkupContainer("playCardParentPlaceholder");
		this.playCardParent.setOutputMarkupId(true);
		this.parentPlaceholder.add(this.playCardParent);
		this.add(this.parentPlaceholder);

		this.handCardsPlaceholder = new WebMarkupContainer("handCardsPlaceholder");
		this.handCardsPlaceholder.setMarkupId("handCardsPlaceholder");
		this.handCardsPlaceholder.setOutputMarkupId(true);
		this.add(this.handCardsPlaceholder);
		// Welcome message
		this.add(new Label("message",
				"version 0.0.8 (release Emperor Wicket), built on Monday, 17th of September 2012."));

		// Comet clock channel
		this.clockPanel = new ClockPanel("clockPanel", Model.of("###"));
		this.clockPanel.setOutputMarkupId(true);
		this.add(this.clockPanel);

		if (!HatchetHarrySession.get().isGameCreated())
		{
			this.player = this.createPlayer();
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

		this.buildDock();

		final WebMarkupContainer balduParent = new WebMarkupContainer("balduParent");
		balduParent.setOutputMarkupId(true);

		final MagicCard card = this.persistenceService.findCardByName("Balduvian Horde");
		if ((null != card)
				&& (!HatchetHarrySession.get().isMySidePlaceholderInSesion(
						HatchetHarrySession.get().getPlayer().getSide())))
		{
			balduParent.add(new CardPanel("baldu", card.getSmallImageFilename(), card
					.getBigImageFilename(), card.getUuidObject()));
		}
		else
		{
			balduParent.add(new WebMarkupContainer("baldu"));
		}
		this.add(balduParent);

		this.generateAboutLink();
		this.generateTeamInfoLink();

		final GameNotifierBehavior notif = new GameNotifierBehavior(this);
		this.add(notif);

		final ServletWebRequest servletWebRequest = (ServletWebRequest)this.getPage().getRequest();
		final HttpServletRequest request = servletWebRequest.getContainerRequest();
		request.getRequestedSessionId();

		this.sidePlaceholderMove = new BookmarkablePageLink<SidePlaceholderMovePage>(
				"sidePlaceholderMove", SidePlaceholderMovePage.class);
		this.sidePlaceholderMove.setMarkupId("sidePlaceholderMove");
		this.add(this.sidePlaceholderMove);

		this.secondSidePlaceholderParent = new WebMarkupContainer("secondSidePlaceholderParent");
		this.secondSidePlaceholderParent.setOutputMarkupId(true);
		final WebMarkupContainer secondSidePlaceholder = new WebMarkupContainer(
				"secondSidePlaceholder");
		secondSidePlaceholder.setOutputMarkupId(true);
		this.secondSidePlaceholderParent.add(secondSidePlaceholder);

		this.firstSidePlaceholderParent = new WebMarkupContainer("firstSidePlaceholderParent");
		this.firstSidePlaceholderParent.setOutputMarkupId(true);
		final WebMarkupContainer firstSidePlaceholder = new WebMarkupContainer(
				"firstSidePlaceholder");
		firstSidePlaceholder.setOutputMarkupId(true);
		this.firstSidePlaceholderParent.add(firstSidePlaceholder);

		this.add(this.secondSidePlaceholderParent, this.firstSidePlaceholderParent);

		this.generateCreateGameLink(this.player, this.handCardsPlaceholder,
				this.firstSidePlaceholderParent);
		this.generateJoinGameLink(this.player, this.handCardsPlaceholder,
				this.secondSidePlaceholderParent);

		this.generatePlayCardLink(this.hand);
		this.generatePlayCardsBehaviorsForAllOpponents();

		this.generateDrawCardLink();

		// Comet chat channel
		this.add(new ChatPanel("chatPanel", this.player.getId()));

		this.buildEndTurnLink();
		this.buildUntapAllLink();
		this.buildUntapAndDrawLink();

		if (HatchetHarrySession.get().isGameCreated())
		{
			this.restoreBattlefieldState();
			HatchetHarrySession.get().setGameCreated();
		}

		if (HatchetHarrySession.get().getGameId() != 0)
		{
			this.buildDataBox(HatchetHarrySession.get().getGameId());
		}
		else
		{
			this.buildDataBox(this.player.getGames().iterator().next().getId());
		}

	}

	private void buildDock()
	{
		final AjaxLink<Void> showHandLink = new AjaxLink<Void>("handLink")
		{
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(final AjaxRequestTarget target)
			{
				final Component galleryToUpdate;
				final boolean isHandDisplayed = HatchetHarrySession.get().isHandDisplayed();
				galleryToUpdate = isHandDisplayed
						? new WebMarkupContainer("gallery")
						: new HandComponent("gallery");

				HomePage.this.handCardsPlaceholder.addOrReplace(galleryToUpdate);
				HatchetHarrySession.get().setHandDisplayed(!isHandDisplayed);

				target.add(HomePage.this.handCardsPlaceholder);
				target.appendJavaScript("var theInt = null; var $crosslink, $navthumb; var curclicked = 0; theInterval = function(cur) { if (typeof cur != 'undefined') curclicked = cur; $crosslink.removeClass('active-thumb'); $navthumb.eq(curclicked).parent().addClass('active-thumb'); jQuery('.stripNav ul li a').eq(curclicked).trigger('click'); $crosslink.removeClass('active-thumb'); $navthumb.eq(curclicked).parent().addClass('active-thumb'); jQuery('.stripNav ul li a').eq(curclicked).trigger('click'); curclicked++; if (6 == curclicked) curclicked = 0; }; jQuery('#main-photo-slider').codaSlider(); $navthumb = jQuery('.nav-thumb'); $crosslink = jQuery('.cross-link'); $navthumb.click(function() { var $this = jQuery(this); theInterval($this.parent().attr('href').slice(1) - 1); return false; }); theInterval();");
			}

			@Override
			protected void onComponentTag(final ComponentTag tag)
			{
				super.onComponentTag(tag);

				if (tag.getName().equalsIgnoreCase("a") || tag.getName().equalsIgnoreCase("link")
						|| tag.getName().equalsIgnoreCase("area"))
				{
					tag.put("href", "#");
				}
				else
				{
					this.disableLink(tag);
				}

			}
		};

		this.add(showHandLink);
	}

	private void buildEndTurnLink()
	{
		this.endTurnPlaceholder = new WebMarkupContainer("endTurnPlaceholder");
		this.endTurnPlaceholder.setMarkupId("endTurnPlaceholder");
		this.endTurnPlaceholder.setOutputMarkupId(true);

		this.notifierPanel = new NotifierPanel("notifierPanel", HomePage.this, HatchetHarrySession
				.get().getPlayer().getSide(), "has declared the end of his turn.",
				this.dataBoxParent, HatchetHarrySession.get().getGameId());
		this.notifierPanel.setOutputMarkupId(true);

		this.endTurnLink = new AjaxLink<Void>("endTurnLink")
		{
			private static final long serialVersionUID = 6590465665519989765L;

			@Override
			public void onClick(final AjaxRequestTarget target)
			{
				target.appendJavaScript("Wicket.Ajax.get('"
						+ HomePage.this.notifierPanel.getCallbackUrl()
						+ "&title="
						+ HatchetHarrySession.get().getPlayer().getSide()
						+ "&text=has declared the end of his turn.&show=true', function() { }, null, null);");
			}

		};
		this.endTurnLink.setMarkupId("endTurnLink");
		this.endTurnLink.setOutputMarkupId(true);

		this.endTurnPlaceholder.add(this.endTurnLink, this.notifierPanel);
		this.add(this.endTurnPlaceholder);
	}

	private void buildUntapAllLink()
	{
		this.untapAllPlaceholder = new WebMarkupContainer("untapAllPlaceholder");
		this.untapAllPlaceholder.setMarkupId("untapAllPlaceholder");
		this.untapAllPlaceholder.setOutputMarkupId(true);

		this.untapAllBehavior = new UntapAllBehavior();

		this.untapAllLink = new AjaxLink<Void>("untapAllLink")
		{
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(final AjaxRequestTarget target)
			{
				final ServletWebRequest servletWebRequest = (ServletWebRequest)target.getPage()
						.getRequest();
				HomePage.LOGGER.info("untap all");
				final HttpServletRequest request = servletWebRequest.getContainerRequest();
				target.appendJavaScript("Wicket.Ajax.get('"
						+ HomePage.this.untapAllBehavior.getCallbackUrl() + "&sessionid="
						+ request.getRequestedSessionId() + "&playerId="
						+ HatchetHarrySession.get().getPlayer().getId()
						+ "', function() { }, null, null);");
			}

		};
		this.untapAllLink.setMarkupId("untapAllLink");
		this.untapAllLink.setOutputMarkupId(true);
		this.untapAllLink.add(this.untapAllBehavior);

		this.untapAllPlaceholder.add(this.untapAllLink);
		this.add(this.untapAllPlaceholder);

		this.untapAllPage = new BookmarkablePageLink<UntapAllPage>("untapAllPage",
				UntapAllPage.class);
		this.add(this.untapAllPage);
	}

	private void buildUntapAndDrawLink()
	{
		this.untapAndDrawPlaceholder = new WebMarkupContainer("untapAndDrawPlaceholder");
		this.untapAndDrawPlaceholder.setMarkupId("untapAndDrawPlaceholder");
		this.untapAndDrawPlaceholder.setOutputMarkupId(true);

		this.untapAndDrawLink = new AjaxLink<Void>("untapAndDrawLink")
		{
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(final AjaxRequestTarget target)
			{
				HomePage.LOGGER.info("untap and draw");
				target.appendJavaScript("jQuery('#untapAllLink').click(); setTimeout(\"jQuery('#drawCardLink').click();\", 1000);");
			}

		};
		this.untapAndDrawLink.setMarkupId("untapAndDrawLink");
		this.untapAndDrawLink.setOutputMarkupId(true);

		this.untapAndDrawPlaceholder.add(this.untapAndDrawLink);
		this.add(this.untapAndDrawPlaceholder);
	}

	private void buildDataBox(final long _gameId)
	{
		this.dataBoxParent = new WebMarkupContainer("dataBoxParent");
		this.dataBoxParent.setMarkupId("dataBoxParent");
		this.dataBoxParent.setOutputMarkupId(true);
		HatchetHarrySession.get().setDataBoxParent(this.dataBoxParent);

		this.dataBox = new DataBox("dataBox", _gameId, this);
		HatchetHarrySession.get().setDataBox(this.dataBox);
		final UpdateDataBoxBehavior behavior = new UpdateDataBoxBehavior(_gameId, this,
				this.dataBox);
		this.dataBox.add(behavior);
		this.dataBox.setOutputMarkupId(true);
		this.dataBoxParent.add(this.dataBox);

		this.add(this.dataBoxParent);
		HomePage.LOGGER.info("building DataBox with gameId= " + _gameId);
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

	protected Player createPlayer()
	{
		final ServletWebRequest servletWebRequest = (ServletWebRequest)this.getPage().getRequest();
		final HttpServletRequest request = servletWebRequest.getContainerRequest();
		final String jsessionid = request.getRequestedSessionId();

		HatchetHarrySession.get().setGameCreated();

		if (this.persistenceService.getFirstPlayer() == null)
		{
			return this.createPlayerAndDeck(jsessionid, "infrared", "infrared", 20l, 1l);
		}
		return this.createPlayerAndDeck(jsessionid, "ultraviolet", "ultraviolet", 20l, 2l);
	}

	private Player createPlayerAndDeck(final String _jsessionid, final String _side,
			final String _name, final Long _lifePoints, final Long id)
	{
		final Player p = new Player();
		p.setSide(_side);
		p.setName(_name);
		p.setJsessionid(_jsessionid);
		p.setLifePoints(_lifePoints);
		p.setId(this.persistenceService.savePlayer(p));

		final Set<Game> games = new HashSet<Game>();
		final Game game = this.persistenceService.createGame(p, 1l);
		games.add(game);
		p.setGames(games);

		HatchetHarrySession.get().setPlayerHasBeenCreated();
		HatchetHarrySession.get().setPlayer(p);
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

		HatchetHarrySession.get().setGameId(game.getId());
		HatchetHarrySession.get().setPlayer(p);
		this.player = p;
		return p;
	}

	private void generatePlayCardsBehaviorsForAnOpponent(final String opponentId)
	{
		for (int i = 1; i < 61; i++)
		{
			final WebMarkupContainer cardPlaceholder = new WebMarkupContainer("cardPlaceholder"
					+ opponentId + i);
			this.playCardParent.addOrReplace(cardPlaceholder);
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
		HomePage.LOGGER.info("Generating link");

		this.playCardLink = new WebMarkupContainer("playCardLink");
		this.playCardLink.setMarkupId("playCardPlaceholder0");
		this.playCardLink.setOutputMarkupId(true);

		if (mc.size() > 0)
		{
			this.playCardBehavior = new PlayCardFromHandBehavior(this.playCardParent,
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
					final ArrayList<MagicCard> list = HatchetHarrySession.get()
							.getFirstCardsInHand();
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
					target.add(HomePage.this.handCardsPlaceholder);
					target.appendJavaScript("jQuery(document).ready(function() { var theInt = null; var $crosslink, $navthumb; var curclicked = 0; theInterval = function(cur) { if (typeof cur != 'undefined') curclicked = cur; $crosslink.removeClass('active-thumb'); $navthumb.eq(curclicked).parent().addClass('active-thumb'); jQuery('.stripNav ul li a').eq(curclicked).trigger('click'); $crosslink.removeClass('active-thumb'); $navthumb.eq(curclicked).parent().addClass('active-thumb'); jQuery('.stripNav ul li a').eq(curclicked).trigger('click'); curclicked++; if (6 == curclicked) curclicked = 0; }; jQuery('#main-photo-slider').codaSlider(); $navthumb = jQuery('.nav-thumb'); $crosslink = jQuery('.cross-link'); $navthumb.click(function() { var $this = jQuery(this); theInterval($this.parent().attr('href').slice(1) - 1); return false; }); theInterval(); });");

					final ServletWebRequest servletWebRequest = (ServletWebRequest)target.getPage()
							.getRequest();
					final HttpServletRequest request = servletWebRequest.getContainerRequest();
					final Meteor meteor = Meteor.build(request, new LinkedList<BroadcastFilter>(),
							null);
					meteor.addListener((AtmosphereResourceEventListener)target.getPage());
					final String message = HatchetHarrySession.get().getPlayer().getSide() + ":::"
							+ "has drawn a card!" + ":::" + request.getRequestedSessionId()
							+ ":::padding";
					meteor.broadcast(message);
				}
			}

			@Override
			protected void onComponentTag(final ComponentTag tag)
			{
				super.onComponentTag(tag);

				if (tag.getName().equalsIgnoreCase("a") || tag.getName().equalsIgnoreCase("link")
						|| tag.getName().equalsIgnoreCase("area"))
				{
					tag.put("href", "");
				}
				else
				{
					this.disableLink(tag);
				}

			}
		};

		drawCardLink.setOutputMarkupId(true).setMarkupId("drawCardLink");
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
		final WebMarkupContainer c = new WebMarkupContainer("headResources");
		c.add(new Behavior()
		{
			private static final long serialVersionUID = 1L;

			@Override
			public void renderHead(final Component component, final IHeaderResponse response)
			{
				response.render(JavaScriptHeaderItem.forReference(new PackageResourceReference(
						HomePage.class, "script/jquery-1.7.2.js")));
				response.render(JavaScriptHeaderItem.forReference(new PackageResourceReference(
						HomePage.class, "script/jquery/jquery-ui-1.8.18.core.mouse.widget.js")));
				response.render(JavaScriptHeaderItem.forReference(new PackageResourceReference(
						HomePage.class, "script/jquery.atmosphere.js")));
				response.render(JavaScriptHeaderItem.forReference(new PackageResourceReference(
						AbstractDefaultAjaxBehavior.class, "res/js/wicket-event-jquery.min.js")));
				response.render(JavaScriptHeaderItem.forReference(new PackageResourceReference(
						AbstractDefaultAjaxBehavior.class, "res/js/wicket-ajax-jquery.min.js")));
				response.render(JavaScriptHeaderItem.forReference(new PackageResourceReference(
						HomePage.class, "script/tour/jquery.easing.1.3.js")));
				response.render(JavaScriptHeaderItem.forReference(new PackageResourceReference(
						HomePage.class, "script/tour/jquery.storage.js")));
				response.render(JavaScriptHeaderItem.forReference(new PackageResourceReference(
						HomePage.class, "script/tour/jquery.tour.js")));
				response.render(JavaScriptHeaderItem.forReference(new PackageResourceReference(
						HomePage.class, "script/menubar/jquery.metadata.js")));
				response.render(JavaScriptHeaderItem.forReference(new PackageResourceReference(
						HomePage.class, "script/menubar/jquery.hoverIntent.js")));
				response.render(JavaScriptHeaderItem.forReference(new PackageResourceReference(
						HomePage.class, "script/menubar/mbMenu.js")));
				// response.render(JavaScriptHeaderItem.forReference(new
				// PackageResourceReference(
				// HomePage.class, "script/menubar/jquery.jqDock.js")));
				response.render(JavaScriptHeaderItem.forReference(new PackageResourceReference(
						HomePage.class, "script/menubar/yahoo-dom-event.js")));
				response.render(JavaScriptHeaderItem.forReference(new PackageResourceReference(
						HomePage.class, "script/menubar/animation.js")));
				response.render(JavaScriptHeaderItem.forReference(new PackageResourceReference(
						HomePage.class, "script/menubar/utilities.js")));
				response.render(JavaScriptHeaderItem.forReference(new PackageResourceReference(
						HomePage.class, "script/menubar/container_core.js")));
				response.render(JavaScriptHeaderItem.forReference(new PackageResourceReference(
						HomePage.class, "script/menubar/menu.js")));
				response.render(JavaScriptHeaderItem.forReference(new PackageResourceReference(
						HomePage.class, "script/menubar/element.js")));
				// response.render(JavaScriptHeaderItem.forReference(new
				// PackageResourceReference(
				// HomePage.class, "script/menubar/jquery.jqDock.js")));
				response.render(JavaScriptHeaderItem.forReference(new PackageResourceReference(
						HomePage.class, "script/dock/dock.js")));
				response.render(JavaScriptHeaderItem.forReference(new PackageResourceReference(
						HomePage.class, "script/qunitTests/qUnit.js")));
				response.render(JavaScriptHeaderItem.forReference(new PackageResourceReference(
						HomePage.class, "script/qunitTests/codeUnderTest.js")));
				response.render(JavaScriptHeaderItem.forReference(new PackageResourceReference(
						HomePage.class, "script/qunitTests/HomePageTests.js")));
				response.render(JavaScriptHeaderItem.forReference(new PackageResourceReference(
						HomePage.class, "script/jquery/mootools.v1.11.js")));
				response.render(JavaScriptHeaderItem.forReference(new PackageResourceReference(
						HomePage.class, "script/gallery/jquery-easing-1.3.pack.js")));
				response.render(JavaScriptHeaderItem.forReference(new PackageResourceReference(
						HomePage.class, "script/gallery/jquery-easing-compatibility.1.2.pack.js")));
				response.render(JavaScriptHeaderItem.forReference(new PackageResourceReference(
						HomePage.class, "script/gallery/coda-slider.1.1.1.pack.js")));
				response.render(JavaScriptHeaderItem.forReference(new PackageResourceReference(
						HomePage.class, "script/gallery/gallery.js")));
				response.render(JavaScriptHeaderItem.forReference(new PackageResourceReference(
						HomePage.class, "script/rotate/jQueryRotate.2.1.js")));
				response.render(JavaScriptHeaderItem.forReference(new PackageResourceReference(
						HomePage.class,
						"script/draggableHandle/jquery.ui.draggable.sidePlaceholder.js")));

				response.render(CssHeaderItem.forReference(new PackageResourceReference(
						HomePage.class, "stylesheet/menu.css")));
				response.render(CssHeaderItem.forReference(new PackageResourceReference(
						HomePage.class, "stylesheet/layout.css")));
				response.render(CssHeaderItem.forReference(new PackageResourceReference(
						HomePage.class, "stylesheet/menu_black.css")));
				response.render(CssHeaderItem.forReference(new PackageResourceReference(
						HomePage.class, "stylesheet/jquery.jquerytour.css")));
				response.render(CssHeaderItem.forReference(new PackageResourceReference(
						HomePage.class, "stylesheet/myStyle.css")));
				response.render(CssHeaderItem.forReference(new PackageResourceReference(
						HomePage.class, "stylesheet/galleryStyle.css")));
				response.render(CssHeaderItem.forReference(new PackageResourceReference(
						HomePage.class, "stylesheet/jquery.gritter.css")));
				response.render(CssHeaderItem.forReference(new PackageResourceReference(
						HomePage.class, "stylesheet/fixed4all.css")));
				response.render(CssHeaderItem.forReference(new PackageResourceReference(
						HomePage.class, "stylesheet/fixed4ie.css")));
				response.render(CssHeaderItem.forReference(new PackageResourceReference(
						HomePage.class, "stylesheet/prettyPhoto.css")));
				response.render(CssHeaderItem.forReference(new PackageResourceReference(
						HomePage.class, "stylesheet/toolbarStyle.css")));
				response.render(CssHeaderItem.forReference(new PackageResourceReference(
						HomePage.class, "stylesheet/tipsy.css")));
				response.render(CssHeaderItem.forReference(new PackageResourceReference(
						HomePage.class, "script/dock/dock.css")));

				response.render(JavaScriptHeaderItem.forReference(new PackageResourceReference(
						HomePage.class, "script/toolbar/jquery.prettyPhoto.js")));
				response.render(JavaScriptHeaderItem.forReference(new PackageResourceReference(
						HomePage.class, "script/toolbar/jquery.tipsy.js")));
				response.render(JavaScriptHeaderItem.forReference(new PackageResourceReference(
						HomePage.class, "script/notifier/jquery.gritter.min.js")));
			}
		});
		this.add(c);
	}

	protected void buildHandMarkup()
	{
		final Component galleryToUpdate;
		final boolean isHandDisplayed = HatchetHarrySession.get().isHandDisplayed();
		galleryToUpdate = isHandDisplayed ? new HandComponent("gallery") : new WebMarkupContainer(
				"gallery");

		galleryToUpdate.setOutputMarkupId(true);
		this.handCardsPlaceholder.add(galleryToUpdate);
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
			final ArrayList<MagicCard> cards = new ArrayList<MagicCard>();

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
		this.aboutWindow.setContent(new AboutModalWindow(this.aboutWindow.getContentId(),
				this.aboutWindow));
		this.aboutWindow.setCssClassName(ModalWindow.CSS_CLASS_BLUE);
		this.aboutWindow.setMaskType(ModalWindow.MaskType.SEMI_TRANSPARENT);
		this.add(this.aboutWindow);

		final AjaxLink<Void> aboutLink = new AjaxLink<Void>("aboutLink")
		{
			private static final long serialVersionUID = 8140325977385015896L;

			@Override
			public void onClick(final AjaxRequestTarget target)
			{
				target.appendJavaScript("Wicket.Window.unloadConfirmation = false;");
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
				target.appendJavaScript("Wicket.Window.unloadConfirmation = false;");
				HomePage.this.teamInfoWindow.show(target);
			}
		};

		teamInfoLink.setOutputMarkupId(true);
		this.teamInfoWindow.setOutputMarkupId(true);
		this.add(teamInfoLink);
	}

	protected void generateCreateGameLink(final Player _player,
			final WebMarkupContainer _handCardsParent,
			final WebMarkupContainer sidePlaceholderParent)
	{
		this.createGameWindow = new ModalWindow("createGameWindow");
		this.createGameWindow.setInitialWidth(475);
		this.createGameWindow.setInitialHeight(290);
		this.createGameWindow.setTitle("Create a game");

		this.createGameWindow.setContent(new CreateGameModalWindow(this.createGameWindow,
				this.createGameWindow.getContentId(), _player, _handCardsParent,
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
				target.appendJavaScript("Wicket.Window.unloadConfirmation = false;");
				HomePage.this.createGameWindow.show(target);
			}
		};

		createGameLink.setOutputMarkupId(true);
		this.createGameWindow.setOutputMarkupId(true);
		this.add(createGameLink);
	}

	protected void generateJoinGameLink(final Player _player,
			final WebMarkupContainer _handCardsParent,
			final WebMarkupContainer sidePlaceholderParent)
	{
		this.joinGameWindow = new ModalWindow("joinGameWindow");
		this.joinGameWindow.setInitialWidth(475);
		this.joinGameWindow.setInitialHeight(290);
		this.joinGameWindow.setTitle("Join a game");

		this.joinGameWindow.setContent(new JoinGameModalWindow(this.joinGameWindow,
				this.joinGameWindow.getContentId(), _player, _handCardsParent, this.dataBoxParent,
				this));
		this.joinGameWindow.setCssClassName(ModalWindow.CSS_CLASS_BLUE);
		this.joinGameWindow.setMaskType(ModalWindow.MaskType.SEMI_TRANSPARENT);
		this.add(this.joinGameWindow);

		final AjaxLink<Void> createGameLink = new AjaxLink<Void>("joinGameLink")
		{
			private static final long serialVersionUID = 4097315677385015896L;

			@Override
			public void onClick(final AjaxRequestTarget target)
			{
				target.appendJavaScript("Wicket.Window.unloadConfirmation = false;");
				HomePage.this.joinGameWindow.show(target);
			}
		};

		createGameLink.setOutputMarkupId(true);
		this.joinGameWindow.setOutputMarkupId(true);
		this.add(createGameLink);
	}

	@Subscribe
	public void updateTime(final AjaxRequestTarget target, final Date event)
	{
		this.clockPanel.setDefaultModelObject(event.toString());
		target.add(this.clockPanel);
	}

	@Override
	protected void configureResponse(final WebResponse response)
	{
		if (HatchetHarrySession.get() != null)
		{
			final Locale originalLocale = HatchetHarrySession.get().getLocale();
			HatchetHarrySession.get().setLocale(originalLocale);
		}

		final String encoding = "text/html;charset=utf-8";
		response.setContentType(encoding);
		super.configureResponse(response);
	}

	private void restoreBattlefieldState()
	{
		final Component galleryToUpdate;
		final boolean isHandDisplayed = HatchetHarrySession.get().isHandDisplayed();
		galleryToUpdate = isHandDisplayed ? new HandComponent("gallery") : new WebMarkupContainer(
				"gallery");

		galleryToUpdate.setOutputMarkupId(true);
		this.handCardsPlaceholder.addOrReplace(galleryToUpdate);

		for (final CardPanel cp : HatchetHarrySession.get().getAllCardsInBattleField())
		{
			this.playCardParent.addOrReplace(cp);
		}

		final List<SidePlaceholderPanel> allSides = HatchetHarrySession.get()
				.getMySidePlaceholder();
		for (final SidePlaceholderPanel spp : allSides)
		{
			final ServletWebRequest servletWebRequest = (ServletWebRequest)this.getPage()
					.getRequest();
			final HttpServletRequest request = servletWebRequest.getContainerRequest();
			final String jsessionid = request.getRequestedSessionId();

			if ("firstSidePlaceholder".equals(spp.getId()))
			{
				final SidePlaceholderMoveBehavior spmb = new SidePlaceholderMoveBehavior(spp,
						spp.getUuid(), jsessionid, HomePage.this, HatchetHarrySession.get()
								.getPlayer().getSide(), HomePage.this.getDataBoxParent(),
						HatchetHarrySession.get().getGameId());
				spp.setVersioned(false);
				spp.removeAll();
				spp.add(spmb);
				spp.setOutputMarkupId(true);
				this.firstSidePlaceholderParent.addOrReplace(spp);
				this.addOrReplace(this.firstSidePlaceholderParent);
				HatchetHarrySession.get().setFirstSideMoveCallbackUrl(
						spmb.getCallbackUrl().toString());
			}
			else if ("secondSidePlaceholder".equals(spp.getId()))
			{
				final String side = ("infrared".equals(HatchetHarrySession.get().getPlayer()
						.getSide())) ? "ultraviolet" : "infrared";
				final SidePlaceholderMoveBehavior spmb = new SidePlaceholderMoveBehavior(spp,
						spp.getUuid(), jsessionid, HomePage.this, side,
						HomePage.this.getDataBoxParent(), HatchetHarrySession.get().getGameId());
				spp.add(spmb);
				spp.setOutputMarkupId(true);
				this.secondSidePlaceholderParent.addOrReplace(spp);
				HatchetHarrySession.get().setSecondSideMoveCallbackUrl(
						spmb.getCallbackUrl().toString());
			}
		}

		this.add(new Behavior()
		{
			private static final long serialVersionUID = 1L;

			@Override
			public void renderHead(final Component component, final IHeaderResponse response)
			{
				HomePage.this.restoreStateOfAllCardsInBattlefield(response);
			}

		});
	}

	void restoreStateOfAllCardsInBattlefield(final IHeaderResponse response)
	{
		final StringBuffer js = new StringBuffer();
		final Collection<CardPanel> allCards = Collections
				.synchronizedCollection(HatchetHarrySession.get().getAllCardsInBattleField());

		for (final CardPanel cp : allCards)
		{
			try
			{
				HatchetHarrySession.get().setPlaceholderNumber(
						HatchetHarrySession.get().getPlaceholderNumber() + 1);

				final MagicCard mc = HomePage.this.persistenceService.getCardFromUuid(cp.getUuid());
				if (null != mc)
				{
					js.append("var card = jQuery(\"#menutoggleButton" + cp.getUuid() + "\"); "
							+ "card.css(\"position\", \"absolute\"); " + "card.css(\"left\", \""
							+ mc.getX() + "px\");" + "card.css(\"top\", \"" + mc.getY()
							+ "px\");\n");
					js.append("jQuery(\"#card" + cp.getUuid() + "\").easyTooltip({"
							+ "useElement: \"cardTooltip" + cp.getUuid() + "\"});\n");

					if (mc.isTapped())
					{
						js.append("jQuery('#card" + cp.getUuid() + "').rotate(90);");
					}
					else
					{
						js.append("jQuery('#card" + cp.getUuid() + "').rotate(0);");
					}
				}

			}
			catch (final IllegalArgumentException e)
			{
				HomePage.LOGGER.error("error parsing UUID of moved card", e);
			}
		}

		final List<SidePlaceholderPanel> allSides = HatchetHarrySession.get()
				.getMySidePlaceholder();
		HomePage.LOGGER.info("size: " + allSides.size());

		final ServletWebRequest servletWebRequest = (ServletWebRequest)this.getPage().getRequest();
		final HttpServletRequest request = servletWebRequest.getContainerRequest();
		final String jsessionid = request.getRequestedSessionId();

		for (final SidePlaceholderPanel spp : allSides)
		{
			String callbackUrl = "";
			if ("firstSidePlaceholder".equals(spp.getId()))
			{
				callbackUrl = HatchetHarrySession.get().getFirstSideMoveCallbackUrl();
			}
			else if ("secondSidePlaceholder".equals(spp.getId()))
			{
				callbackUrl = HatchetHarrySession.get().getSecondSideMoveCallbackUrl();
			}
			final HashMap<String, Object> variables = new HashMap<String, Object>();
			variables.put("url", callbackUrl);
			variables.put("uuid", spp.getUuid());
			variables.put("uuidValidForJs", spp.getUuid().toString().replace("-", "_"));
			variables.put("jsessionid", jsessionid);
			variables.put("side", spp.getSide());

			final TextTemplate template = new PackageTextTemplate(HomePage.class,
					"script/draggableHandle/initSidePlaceholderDrag.js");
			template.interpolate(variables);
			js.append("\n" + template.asString() + "\n");
		}

		for (final SidePlaceholderPanel s : allSides)
		{
			HomePage.LOGGER.info("side: " + s.getUuid() + ", X= " + s.getPosX() + ", Y= "
					+ s.getPosY());
			js.append("var card = jQuery('#sidePlaceholder" + s.getUuid() + "'); "
					+ "card.css('position', 'absolute'); " + "card.css('left', '" + s.getPosX()
					+ "px'); " + "card.css('top', '" + s.getPosY() + "px'); ");
		}
		// Don't show website tour on page refresh
		js.append("jQuery('#tourcontrols').remove();");

		response.render(JavaScriptHeaderItem.forScript(js.toString(), "homePage"));
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

	public WebMarkupContainer getDataBoxParent()
	{
		return this.dataBoxParent;
	}

}
