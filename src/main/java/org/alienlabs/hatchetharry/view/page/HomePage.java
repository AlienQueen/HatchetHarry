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

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.alienlabs.hatchetharry.HatchetHarryApplication;
import org.alienlabs.hatchetharry.HatchetHarrySession;
import org.alienlabs.hatchetharry.model.CardZone;
import org.alienlabs.hatchetharry.model.Deck;
import org.alienlabs.hatchetharry.model.Game;
import org.alienlabs.hatchetharry.model.MagicCard;
import org.alienlabs.hatchetharry.model.Player;
import org.alienlabs.hatchetharry.model.channel.JoinGameCometChannel;
import org.alienlabs.hatchetharry.model.channel.JoinGameNotificationCometChannel;
import org.alienlabs.hatchetharry.model.channel.NotifierAction;
import org.alienlabs.hatchetharry.model.channel.NotifierCometChannel;
import org.alienlabs.hatchetharry.model.channel.SimplePredicate;
import org.alienlabs.hatchetharry.model.channel.UntapAllCometChannel;
import org.alienlabs.hatchetharry.model.channel.UpdateDataBoxCometChannel;
import org.alienlabs.hatchetharry.service.PersistenceService;
import org.alienlabs.hatchetharry.service.RuntimeDataGenerator;
import org.alienlabs.hatchetharry.view.component.AboutModalWindow;
import org.alienlabs.hatchetharry.view.component.CardPanel;
import org.alienlabs.hatchetharry.view.component.ChatPanel;
import org.alienlabs.hatchetharry.view.component.ClockPanel;
import org.alienlabs.hatchetharry.view.component.CreateGameModalWindow;
import org.alienlabs.hatchetharry.view.component.DataBox;
import org.alienlabs.hatchetharry.view.component.GameNotifierBehavior;
import org.alienlabs.hatchetharry.view.component.GraveyardComponent;
import org.alienlabs.hatchetharry.view.component.HandComponent;
import org.alienlabs.hatchetharry.view.component.JoinGameModalWindow;
import org.alienlabs.hatchetharry.view.component.NotifierPanel;
import org.alienlabs.hatchetharry.view.component.PlayCardFromGraveyardBehavior;
import org.alienlabs.hatchetharry.view.component.PlayCardFromHandBehavior;
import org.alienlabs.hatchetharry.view.component.SidePlaceholderMoveBehavior;
import org.alienlabs.hatchetharry.view.component.SidePlaceholderPanel;
import org.alienlabs.hatchetharry.view.component.TeamInfoModalWindow;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.atmosphere.EventBus;
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
	ModalWindow teamInfoWindowResponsive;
	ModalWindow aboutWindowResponsive;
	ModalWindow createGameWindow;
	ModalWindow joinGameWindow;

	Player player;
	Deck deck;
	BookmarkablePageLink<PlayCardPage> playCardPage;
	List<MagicCard> hand;
	private final WebMarkupContainer parentPlaceholder;
	WebMarkupContainer playCardLink;
	WebMarkupContainer playCardParent;
	WebMarkupContainer playCardFromGraveyardLink;

	final WebMarkupContainer galleryParent;
	final WebMarkupContainer graveyardParent;
	WebMarkupContainer thumbsPlaceholder;
	WebMarkupContainer graveyardThumbsPlaceholder;

	private AjaxLink<Void> endTurnLink;
	private AjaxLink<Void> untapAllLink;
	private AjaxLink<Void> untapAndDrawLink;

	private BookmarkablePageLink<UntapAllPage> untapAllPage;

	WebMarkupContainer endTurnPlaceholder;
	WebMarkupContainer untapAllPlaceholder;
	WebMarkupContainer untapAndDrawPlaceholder;

	public NotifierPanel notifierPanel;

	private WebMarkupContainer dataBoxParent;

	private Component dataBox;

	private final BookmarkablePageLink<SidePlaceholderMovePage> sidePlaceholderMove;

	private final WebMarkupContainer firstSidePlaceholderParent;
	private final WebMarkupContainer secondSidePlaceholderParent;

	private PlayCardFromHandBehavior playCardBehavior;
	private PlayCardFromGraveyardBehavior playCardFromGraveyardBehavior;
	ClockPanel clockPanel;

	public HomePage()
	{
		this.setOutputMarkupId(true);

		// Resources
		this.addHeadResources();

		this.add(new BookmarkablePageLink<NotifierPage>("notifierStart", NotifierPage.class));

		this.parentPlaceholder = new WebMarkupContainer("cardParent");
		this.parentPlaceholder.setOutputMarkupId(true);

		this.playCardParent = new WebMarkupContainer("playCardParentPlaceholder");
		this.playCardParent.setOutputMarkupId(true);
		this.parentPlaceholder.add(this.playCardParent);
		this.add(this.parentPlaceholder);

		this.galleryParent = new WebMarkupContainer("galleryParent");
		this.galleryParent.setMarkupId("galleryParent");
		this.galleryParent.setOutputMarkupId(true);
		this.add(this.galleryParent);

		this.graveyardParent = new WebMarkupContainer("graveyardParent");
		this.graveyardParent.setMarkupId("graveyardParent");
		this.graveyardParent.setOutputMarkupId(true);
		this.add(this.graveyardParent);

		// Welcome message
		final Label message1 = new Label("message1", "version 0.2.0 (release Pass Me By),");
		final Label message2 = new Label("message2", "built on Sunday, 20th of January 2013.");
		this.add(message1, message2);

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
		this.buildGraveyardMarkup();

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

		// Links from the menubar
		this.aboutWindow = new ModalWindow("aboutWindow");
		this.aboutWindow = this.generateAboutLink("aboutLink", this.aboutWindow);
		this.teamInfoWindow = new ModalWindow("teamInfoWindow");
		this.teamInfoWindow = this.generateTeamInfoLink("teamInfoLink", this.teamInfoWindow);

		/*
		 * Links from the drop-down menu, which appears when the width of the
		 * view port is < than its height. (AKA a little bit of responsive Web
		 * design)
		 */
		this.aboutWindowResponsive = new ModalWindow("aboutWindowResponsive");
		this.aboutWindowResponsive = this.generateAboutLink("aboutLinkResponsive",
				this.aboutWindowResponsive);
		this.teamInfoWindow = new ModalWindow("teamInfoWindowResponsive");
		this.teamInfoWindow = this.generateTeamInfoLink("teamInfoLinkResponsive",
				this.teamInfoWindow);

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
		this.secondSidePlaceholderParent.setMarkupId("secondSidePlaceholderParent");
		final WebMarkupContainer secondSidePlaceholder = new WebMarkupContainer(
				"secondSidePlaceholder");
		secondSidePlaceholder.setOutputMarkupId(true);
		secondSidePlaceholder.setMarkupId("secondSidePlaceholder");
		this.secondSidePlaceholderParent.add(secondSidePlaceholder);

		this.firstSidePlaceholderParent = new WebMarkupContainer("firstSidePlaceholderParent");
		this.firstSidePlaceholderParent.setOutputMarkupId(true);
		this.firstSidePlaceholderParent.setMarkupId("firstSidePlaceholderParent");
		final WebMarkupContainer firstSidePlaceholder = new WebMarkupContainer(
				"firstSidePlaceholder");
		firstSidePlaceholder.setOutputMarkupId(true);
		this.firstSidePlaceholderParent.add(firstSidePlaceholder);

		this.add(this.secondSidePlaceholderParent, this.firstSidePlaceholderParent);

		this.generateCreateGameLink(this.player, this.galleryParent,
				this.firstSidePlaceholderParent);
		this.generateJoinGameLink(this.player, this.galleryParent, this.secondSidePlaceholderParent);

		this.generatePlayCardLink(this.hand);
		this.generatePlayCardFromGraveyardLink();
		this.generatePlayCardsBehaviorsForAllOpponents();
		// this.generatePlayCardsFromGraveyardBehaviorsForAllOpponents();

		this.generateDrawCardLink();

		// Comet chat channel
		this.add(new ChatPanel("chatPanel", this.player.getId()));

		this.buildEndTurnLink();
		this.buildUntapAllLink();
		this.buildUntapAndDrawLink();
		this.buildCombatLink();

		if (HatchetHarrySession.get().isGameCreated())
		{
			this.restoreBattlefieldState();
			HatchetHarrySession.get().setGameCreated();
		}
		else
		{
			HatchetHarrySession.get().setGameId(0l);
		}

		if (HatchetHarrySession.get().getGameId() != 0)
		{
			this.buildDataBox(HatchetHarrySession.get().getGameId());
		}
		else
		{
			this.buildEmptyDataBox();
		}

		this.generateResetDbLink();
		this.generateHideAllTooltipsLink();
	}

	private void generateHideAllTooltipsLink()
	{
		this.add(new AjaxLink<Void>("hideAllTooltipsLink")
		{
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(final AjaxRequestTarget target)
			{
				target.appendJavaScript("jQuery('.cardTooltip').attr('style', 'display: none;');");
			}

		});
	}

	private void generateResetDbLink()
	{
		this.add(new AjaxLink<Void>("resetDbLink")
		{
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(final AjaxRequestTarget target)
			{
				HomePage.LOGGER.info("reset DB");
				HomePage.this.persistenceService.resetDb();
				target.appendJavaScript("alert('The database has been reset, please clear your cookies and refresh this page (F5)!');");
			}

		});
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

				HomePage.this.galleryParent.addOrReplace(galleryToUpdate);
				HatchetHarrySession.get().setHandDisplayed(!isHandDisplayed);

				target.add(HomePage.this.galleryParent);
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

		final AjaxLink<Void> showGraveyardLink = new AjaxLink<Void>("graveyardLink")
		{
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(final AjaxRequestTarget target)
			{
				final Component graveyardToUpdate;
				final boolean isGraveyardDisplayed = HatchetHarrySession.get()
						.isGraveyardDisplayed();
				graveyardToUpdate = isGraveyardDisplayed
						? new WebMarkupContainer("graveyard")
						: new GraveyardComponent("graveyard");

				HomePage.this.graveyardParent.addOrReplace(graveyardToUpdate);
				target.add(HomePage.this.graveyardParent);

				if (!isGraveyardDisplayed)
				{
					target.appendJavaScript("var theIntGraveyard = null; var $crosslinkGraveyard, $navthumbGraveyard; var curclickedGraveyard = 0; theIntervalGraveyard = function(cur) { if (typeof cur != 'undefined') curclickedGraveyard = cur; $crosslinkGraveyard.removeClass('active-thumbGraveyard'); $navthumbGraveyard.eq(curclickedGraveyard).parent().addClass('active-thumbGraveyard'); jQuery('.stripNavGraveyard ul li a').eq(curclickedGraveyard).trigger('click'); $crosslinkGraveyard.removeClass('active-thumbGraveyard'); $navthumbGraveyard.eq(curclickedGraveyard).parent().addClass('active-thumbGraveyard'); jQuery('.stripNavGraveyard ul li a').eq(curclickedGraveyard).trigger('click'); curclickedGraveyard++; if (6 == curclickedGraveyard) curclickedGraveyard = 0; }; jQuery('#graveyard-main-photo-slider').codaSliderGraveyard(); $navthumbGraveyard = jQuery('.graveyard-nav-thumb'); $crosslinkGraveyard = jQuery('.graveyard-cross-link'); $navthumbGraveyard.click(function() { var $this = jQuery(this); theIntervalGraveyard($this.parent().attr('href').slice(1) - 1); return false; }); theIntervalGraveyard();");
				}

				HatchetHarrySession.get().setGraveyardDisplayed(!isGraveyardDisplayed);
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

		this.add(showGraveyardLink);
	}

	private void buildEndTurnLink()
	{
		this.endTurnPlaceholder = new WebMarkupContainer("endTurnPlaceholder");
		this.endTurnPlaceholder.setMarkupId("endTurnPlaceholder");
		this.endTurnPlaceholder.setOutputMarkupId(true);

		// TODO remove this in favor of the wicket-atmosphere implementation:
		this.notifierPanel = new NotifierPanel("notifierPanel", HomePage.this, HatchetHarrySession
				.get().getPlayer().getSide(), "has declared the end of his turn.",
				this.dataBoxParent, HatchetHarrySession.get().getGameId());
		this.notifierPanel.setOutputMarkupId(true);

		this.endTurnLink = new AjaxLink<Void>("endTurnLink")
		{
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(final AjaxRequestTarget target)
			{
				final Player me = HatchetHarrySession.get().getPlayer();
				final Long gameId = HomePage.this.persistenceService
						.getPlayer(HatchetHarrySession.get().getPlayer().getId()).getGames()
						.iterator().next().getId();
				final List<BigInteger> allPlayersInGame = HomePage.this.persistenceService
						.giveAllPlayersFromGame(gameId);

				for (int i = 0; i < allPlayersInGame.size(); i++)
				{
					final Long playerToWhomToSend = allPlayersInGame.get(i).longValue();
					final String pageUuid = HatchetHarryApplication.getCometResources().get(
							playerToWhomToSend);
					final NotifierCometChannel ncc = new NotifierCometChannel(
							NotifierAction.END_OF_TURN_ACTION, null, me.getId(), me.getName(),
							me.getSide(), null, null, null);

					EventBus.get().post(ncc, pageUuid);
				}

				HatchetHarrySession.get().setCombatInProgress(false);
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

		this.untapAllLink = new AjaxLink<Void>("untapAllLink")
		{
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(final AjaxRequestTarget target)
			{
				final Long gameId = HatchetHarrySession.get().getGameId();
				final List<BigInteger> allPlayersInGame = HomePage.this.persistenceService
						.giveAllPlayersFromGame(gameId);

				for (int i = 0; i < allPlayersInGame.size(); i++)
				{
					final Long playerToWhomToSend = allPlayersInGame.get(i).longValue();
					final String pageUuid = HatchetHarryApplication.getCometResources().get(
							playerToWhomToSend);

					final UntapAllCometChannel uacc = new UntapAllCometChannel(HatchetHarrySession
							.get().getGameId(), HatchetHarrySession.get().getPlayer().getId());
					EventBus.get().post(uacc, pageUuid);
				}
			}

		};
		this.untapAllLink.setMarkupId("untapAllLink");
		this.untapAllLink.setOutputMarkupId(true);

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

		this.dataBox = new DataBox("dataBox", _gameId);
		HatchetHarrySession.get().setDataBox(this.dataBox);
		this.dataBox.setOutputMarkupId(true);
		this.dataBoxParent.add(this.dataBox);

		this.add(this.dataBoxParent);
		HomePage.LOGGER.info("building DataBox with gameId= " + _gameId);
	}

	private void buildEmptyDataBox()
	{
		this.dataBoxParent = new WebMarkupContainer("dataBoxParent");
		this.dataBoxParent.setMarkupId("dataBoxParent");
		this.dataBoxParent.setOutputMarkupId(true);
		HatchetHarrySession.get().setDataBoxParent(this.dataBoxParent);

		this.dataBox = new WebMarkupContainer("dataBox");
		HatchetHarrySession.get().setDataBox(this.dataBox);
		this.dataBox.setOutputMarkupId(true);
		this.dataBoxParent.add(this.dataBox);

		this.add(this.dataBoxParent);
		HomePage.LOGGER.info("building * empty * DataBox with gameId= 0");
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
		final Game game = this.persistenceService.createGame(p, 0l);
		games.add(game);
		p.setGames(games);

		HatchetHarrySession.get().setPlayerHasBeenCreated();
		HatchetHarrySession.get().setPlayer(p);

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

		// HatchetHarrySession.get().setGameId(game.getId());
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
			cardPlaceholder.setOutputMarkupId(true);
			cardPlaceholder.setMarkupId("cardPlaceholder" + opponentId + i);

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
					this.galleryParent, mc.get(0).getUuidObject(), 0, HatchetHarrySession.get()
							.getPlayer().getSide());
			this.playCardLink.add(this.playCardBehavior);
		}

		this.playCardLink.setMarkupId("playCardLink0");
		this.playCardLink.setOutputMarkupId(true);
		playCardPlaceholder.add(this.playCardLink);

		this.add(playCardPlaceholder);
	}

	private void generatePlayCardFromGraveyardLink()
	{
		HomePage.LOGGER.info("Generating playCardFromGraveyard link");

		final WebMarkupContainer playCardFromGraveyardPlaceholder = new WebMarkupContainer(
				"playCardFromGraveyardPlaceholder");
		playCardFromGraveyardPlaceholder.setMarkupId("playCardFromGraveyardPlaceholder0");
		playCardFromGraveyardPlaceholder.setOutputMarkupId(true);

		this.playCardFromGraveyardLink = new WebMarkupContainer("playCardFromGraveyardLink");
		this.playCardFromGraveyardLink.setMarkupId("playCardFromGraveyardLink0");
		this.playCardFromGraveyardLink.setOutputMarkupId(true);

		this.playCardFromGraveyardBehavior = new PlayCardFromGraveyardBehavior(this.playCardParent,
				HatchetHarrySession.get().getPlayer().getSide());
		this.playCardFromGraveyardLink.add(this.playCardFromGraveyardBehavior);

		this.playCardFromGraveyardLink.setMarkupId("playCardFromGraveyardLink0");
		this.playCardFromGraveyardLink.setOutputMarkupId(true);
		playCardFromGraveyardPlaceholder.add(this.playCardFromGraveyardLink);

		this.add(playCardFromGraveyardPlaceholder);

		// TODO: put this in PlayCardFromGraveyardBehavior
		// target.appendJavaScript("jQuery(\"#menu_4_0\").hide(); ");
	}

	private void buildCombatLink()
	{
		final WebMarkupContainer combatPlaceholder = new WebMarkupContainer("combatPlaceholder");
		combatPlaceholder.setMarkupId("combatPlaceholder");
		combatPlaceholder.setOutputMarkupId(true);

		final AjaxLink<Void> combatLink = new AjaxLink<Void>("combatLink")
		{
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(final AjaxRequestTarget target)
			{
				HomePage.LOGGER.info("clicked on declare combat");
				final Long gameId = HomePage.this.persistenceService
						.getPlayer(HatchetHarrySession.get().getPlayer().getId()).getGames()
						.iterator().next().getId();
				final List<BigInteger> allPlayersInGame = HomePage.this.persistenceService
						.giveAllPlayersFromGame(gameId);

				for (int i = 0; i < allPlayersInGame.size(); i++)
				{
					final Long playerToWhomToSend = allPlayersInGame.get(i).longValue();
					final String pageUuid = HatchetHarryApplication.getCometResources().get(
							playerToWhomToSend);

					final NotifierCometChannel ncc = new NotifierCometChannel(
							NotifierAction.COMBAT_IN_PROGRESS_ACTION, null, null,
							HatchetHarrySession.get().getPlayer().getName(), "", "", "",
							HatchetHarrySession.get().isCombatInProgress());

					EventBus.get().post(ncc, pageUuid);
				}

				HatchetHarrySession.get().setCombatInProgress(
						!HatchetHarrySession.get().isCombatInProgress());
			}
		};
		combatLink.setMarkupId("combatLink");
		combatLink.setOutputMarkupId(true);

		combatPlaceholder.add(combatLink);

		this.add(combatPlaceholder);
	}

	private void generateDrawCardLink()
	{
		final AjaxLink<String> drawCardLink = new AjaxLink<String>("drawCardLink")
		{
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(final AjaxRequestTarget target)
			{
				final HatchetHarrySession session = HatchetHarrySession.get();

				if ((session.getDeck() != null) && (session.getDeck().getCards() != null)
						&& (session.getDeck().getCards().size() > 0))
				{
					final MagicCard card = session.getDeck().getCards().get(0);
					card.setZone(CardZone.HAND);
					HomePage.this.persistenceService.saveCard(card);

					final ArrayList<MagicCard> list = session.getFirstCardsInHand();
					list.add(card);

					final Deck d = session.getDeck();
					final List<MagicCard> deckList = d.getCards();
					deckList.remove(card);
					d.setCards(deckList);

					session.setFirstCardsInHand(list);
					session.setDeck(d);

					final HandComponent gallery = new HandComponent("gallery");
					gallery.setOutputMarkupId(true);

					HomePage.this.galleryParent.addOrReplace(gallery);
					target.add(HomePage.this.galleryParent);
					target.appendJavaScript("jQuery(document).ready(function() { var theInt = null; var $crosslink, $navthumb; var curclicked = 0; theInterval = function(cur) { if (typeof cur != 'undefined') curclicked = cur; $crosslink.removeClass('active-thumb'); $navthumb.eq(curclicked).parent().addClass('active-thumb'); jQuery('.stripNav ul li a').eq(curclicked).trigger('click'); $crosslink.removeClass('active-thumb'); $navthumb.eq(curclicked).parent().addClass('active-thumb'); jQuery('.stripNav ul li a').eq(curclicked).trigger('click'); curclicked++; if (6 == curclicked) curclicked = 0; }; jQuery('#main-photo-slider').codaSlider(); $navthumb = jQuery('.nav-thumb'); $crosslink = jQuery('.cross-link'); $navthumb.click(function() { var $this = jQuery(this); theInterval($this.parent().attr('href').slice(1) - 1); return false; }); theInterval(); });");

					final Player me = session.getPlayer();
					final Long gameId = HomePage.this.persistenceService
							.getPlayer(HatchetHarrySession.get().getPlayer().getId()).getGames()
							.iterator().next().getId();
					final List<BigInteger> allPlayersInGame = HomePage.this.persistenceService
							.giveAllPlayersFromGame(gameId);

					for (int i = 0; i < allPlayersInGame.size(); i++)
					{
						final Long playerToWhomToSend = allPlayersInGame.get(i).longValue();
						final String pageUuid = HatchetHarryApplication.getCometResources().get(
								playerToWhomToSend);

						final NotifierCometChannel ncc = new NotifierCometChannel(
								NotifierAction.DRAW_CARD_ACTION, null, me.getId(), me.getName(),
								me.getSide(), null, null, null);
						EventBus.get().post(ncc, pageUuid);
					}
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
						HomePage.class, "script/jquery/jquery.js")));
				response.render(JavaScriptHeaderItem.forReference(new PackageResourceReference(
						HomePage.class, "script/jquery/jquery-ui-1.8.18.core.mouse.widget.js")));
				response.render(JavaScriptHeaderItem.forReference(new PackageResourceReference(
						AbstractDefaultAjaxBehavior.class, "res/js/wicket-event-jquery.min.js")));
				response.render(JavaScriptHeaderItem.forReference(new PackageResourceReference(
						AbstractDefaultAjaxBehavior.class, "res/js/wicket-ajax-jquery.min.js")));
				response.render(JavaScriptHeaderItem.forReference(new PackageResourceReference(
						HomePage.class, "script/tour/jquery.easing.1.3.js")));
				response.render(JavaScriptHeaderItem.forReference(new PackageResourceReference(
						HomePage.class, "script/tour/jquery.cookie.js")));
				response.render(JavaScriptHeaderItem.forReference(new PackageResourceReference(
						HomePage.class, "script/tour/modernizr.mq.js")));
				response.render(JavaScriptHeaderItem.forReference(new PackageResourceReference(
						HomePage.class, "script/tour/jquery.joyride-1.0.5.js")));
				response.render(JavaScriptHeaderItem.forReference(new PackageResourceReference(
						HomePage.class, "script/menubar/jquery.metadata.js")));
				response.render(JavaScriptHeaderItem.forReference(new PackageResourceReference(
						HomePage.class, "script/menubar/jquery.hoverIntent.js")));
				response.render(JavaScriptHeaderItem.forReference(new PackageResourceReference(
						HomePage.class, "script/menubar/mbMenu.js")));
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
				response.render(JavaScriptHeaderItem.forReference(new PackageResourceReference(
						HomePage.class, "script/dock/dock.js")));
				response.render(JavaScriptHeaderItem.forReference(new PackageResourceReference(
						HomePage.class, "script/qunitTests/qUnit.js")));
				response.render(JavaScriptHeaderItem.forReference(new PackageResourceReference(
						HomePage.class, "script/qunitTests/codeUnderTest.js")));
				response.render(JavaScriptHeaderItem.forReference(new PackageResourceReference(
						HomePage.class, "script/qunitTests/HomePageTests.js")));
				response.render(JavaScriptHeaderItem.forReference(new PackageResourceReference(
						HomePage.class, "script/gallery/jquery-easing-compatibility.1.2.pack.js")));
				response.render(JavaScriptHeaderItem.forReference(new PackageResourceReference(
						HomePage.class, "script/gallery/coda-slider.1.1.1.pack.js")));
				response.render(JavaScriptHeaderItem.forReference(new PackageResourceReference(
						HomePage.class, "script/gallery/coda-sliderGraveyard.1.1.1.pack.js")));
				response.render(JavaScriptHeaderItem.forReference(new PackageResourceReference(
						HomePage.class, "script/gallery/gallery.js")));
				response.render(JavaScriptHeaderItem.forReference(new PackageResourceReference(
						HomePage.class, "script/gallery/graveyard.js")));
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
						HomePage.class, "stylesheet/galleryStyle.css")));
				response.render(CssHeaderItem.forReference(new PackageResourceReference(
						HomePage.class, "stylesheet/graveyardStyle.css")));
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
				response.render(CssHeaderItem.forReference(new PackageResourceReference(
						HomePage.class, "stylesheet/joyride-1.0.5.css")));
				response.render(CssHeaderItem.forReference(new PackageResourceReference(
						HomePage.class, "stylesheet/demo-style.css")));
				response.render(CssHeaderItem.forReference(new PackageResourceReference(
						HomePage.class, "stylesheet/mobile.css")));

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
		this.galleryParent.add(galleryToUpdate);
	}

	protected void buildGraveyardMarkup()
	{
		final Component graveyardToUpdate;
		final boolean isGraveyardDisplayed = HatchetHarrySession.get().isGraveyardDisplayed();
		graveyardToUpdate = isGraveyardDisplayed
				? new GraveyardComponent("graveyard")
				: new WebMarkupContainer("graveyard");

		graveyardToUpdate.setOutputMarkupId(true);
		this.graveyardParent.add(graveyardToUpdate);
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
				final MagicCard mc = this.deck.getCards().get(i);
				mc.setZone(CardZone.HAND);
				this.persistenceService.saveCard(mc);

				cards.add(i, mc);
				HatchetHarrySession.get().addCardIdInHand(i, i);
			}

			HatchetHarrySession.get().setFirstCardsInHand(cards);
			HatchetHarrySession.get().setHandHasBeenCreated();

			this.hand = cards;
			return cards;
		}

		return new ArrayList<MagicCard>();
	}

	protected ModalWindow generateAboutLink(final String id, final ModalWindow window)
	{
		window.setInitialWidth(450);
		window.setInitialHeight(675);
		window.setTitle("About HatchetHarry");
		window.setContent(new AboutModalWindow(window.getContentId(), window));
		window.setCssClassName(ModalWindow.CSS_CLASS_BLUE);
		window.setMaskType(ModalWindow.MaskType.SEMI_TRANSPARENT);
		this.add(window);

		final AjaxLink<Void> aboutLink = new AjaxLink<Void>(id)
		{
			private static final long serialVersionUID = 8140325977385015896L;

			@Override
			public void onClick(final AjaxRequestTarget target)
			{
				target.appendJavaScript("Wicket.Window.unloadConfirmation = false;");
				target.appendJavaScript("jQuery(\"#box_menu_clone\").hide(); ");
				window.show(target);
			}
		};

		aboutLink.setOutputMarkupId(true);
		window.setOutputMarkupId(true);
		this.add(aboutLink);
		return window;
	}

	protected ModalWindow generateTeamInfoLink(final String id, final ModalWindow window)
	{
		window.setInitialWidth(475);
		window.setInitialHeight(528);
		window.setTitle("HatchetHarry Team info");
		window.setContent(new TeamInfoModalWindow(window.getContentId()));
		window.setCssClassName(ModalWindow.CSS_CLASS_BLUE);
		window.setMaskType(ModalWindow.MaskType.SEMI_TRANSPARENT);
		this.add(window);

		final AjaxLink<Void> teamInfoLink = new AjaxLink<Void>(id)
		{
			private static final long serialVersionUID = 8140325977385015896L;

			@Override
			public void onClick(final AjaxRequestTarget target)
			{
				target.appendJavaScript("Wicket.Window.unloadConfirmation = false;");
				target.appendJavaScript("jQuery(\"#box_menu_clone\").hide(); ");
				window.show(target);
			}
		};

		teamInfoLink.setOutputMarkupId(true);
		window.setOutputMarkupId(true);
		this.add(teamInfoLink);
		return window;
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

	@Subscribe
	public void aPlayerJoinedIn(final AjaxRequestTarget target, final JoinGameCometChannel event)
	{
		final String jsessionid = HatchetHarrySession.get().getId();

		if (!jsessionid.equals(event.getJsessionid()))
		{
			final SidePlaceholderPanel spp = new SidePlaceholderPanel("secondSidePlaceholder",
					event.getSide(), this, event.getUuid());

			final SidePlaceholderMoveBehavior spmb = new SidePlaceholderMoveBehavior(spp,
					event.getUuid(), jsessionid, this, this.getDataBoxParent(), HatchetHarrySession
							.get().getGameId());
			spp.add(spmb);
			spp.setOutputMarkupId(true);
			HomePage.LOGGER.info("### aPlayerJoinedIn(), gameId: "
					+ HatchetHarrySession.get().getGameId());

			final HatchetHarrySession session = HatchetHarrySession.get();
			session.putMySidePlaceholderInSesion(event.getSide());


			this.secondSidePlaceholderParent.addOrReplace(spp);
			target.add(this.secondSidePlaceholderParent);

			HomePage.LOGGER.info("### " + event.getUuid());
			final int posX = ("infrared".equals(event.getSide())) ? 300 : 900;
			HomePage.LOGGER.info("### aPlayerJoinedIn(), posX: " + posX);
			HomePage.LOGGER.info("### aPlayerJoinedIn(), jsessionid from event: "
					+ event.getJsessionid());
			HomePage.LOGGER.info("### aPlayerJoinedIn(), jsessionid from request: " + jsessionid);

			target.appendJavaScript("window.setTimeout(function() { var card = jQuery('#sidePlaceholder"
					+ event.getUuid()
					+ "'); "
					+ "card.css('position', 'absolute'); "
					+ "card.css('left', '"
					+ posX
					+ "px'); "
					+ "card.css('top', '500px'); "
					+ "jQuery(\"#"
					+ spp.getMarkupId()
					+ "\").draggable({ handle : \"#handleImage"
					+ event.getUuid()
					+ "\" });"
					+ "jQuery(\"#handleImage"
					+ spp.getUuid()
					+ "\").data(\"url\",\"" + spmb.getCallbackUrl() + "\"); }, 3000); ");

			spp.setPosX(posX);
			spp.setPosY(500);
			session.setMySidePlaceholder(spp);
		}
	}

	@Subscribe
	public void displayNotification(final AjaxRequestTarget target, final NotifierCometChannel event)
	{
		switch (event.getAction())
		{
			case DRAW_CARD_ACTION :
				target.appendJavaScript("jQuery.gritter.add({ title : '"
						+ event.getPlayerName()
						+ "', text : \"has drawn a card!\" , image : 'image/logoh2.gif', sticky : false, time : ''});");
				break;

			case END_OF_TURN_ACTION :
				target.appendJavaScript("jQuery.gritter.add({ title : '"
						+ event.getPlayerName()
						+ "', text : \"has declared the end of his (her) turn!\" , image : 'image/logoh2.gif', sticky : false, time : ''});");
				break;

			case PLAY_CARD_FROM_HAND_ACTION :
				target.appendJavaScript("jQuery.gritter.add({ title : '" + event.getPlayerName()
						+ "', text : \"has played '" + event.getCardName()
						+ "'!\", image : 'image/logoh2.gif', sticky : false, time : ''});");
				break;

			case PLAY_CARD_FROM_GRAVEYARD_ACTION :
				target.appendJavaScript("jQuery.gritter.add({ title : '"
						+ event.getPlayerName()
						+ "', text : \"has played '"
						+ event.getCardName()
						+ "' from graveyard!\", image : 'image/logoh2.gif', sticky : false, time : ''});");
				break;

			case COMBAT_IN_PROGRESS_ACTION :
				if (event.isCombatInProgress())
				{
					target.appendJavaScript("jQuery.gritter.add({ title : '"
							+ event.getPlayerName()
							+ "', text : 'has finished combat', image : 'image/logoh2.gif', sticky : false, time : ''});");
				}
				else
				{
					target.appendJavaScript("jQuery.gritter.add({ title : '"
							+ event.getPlayerName()
							+ "', text : 'is declaring combat!', image : 'image/logoh2.gif', sticky : false, time : ''});");
				}
				break;
		}
	}

	@Subscribe
	public void untapAll(final AjaxRequestTarget target, final UntapAllCometChannel event)
	{
		final List<MagicCard> allCardsInBattlefieldOnMySide = this.persistenceService
				.getAllCardsInBattleFieldForAPlayer(event.getPlayerId());

		final StringBuffer buf = new StringBuffer();

		for (final MagicCard mc : allCardsInBattlefieldOnMySide)
		{
			buf.append("jQuery('#card" + mc.getUuid().toString() + "').rotate(0); ");
			mc.setTapped(false);
			this.persistenceService.saveCard(mc);
		}

		target.appendJavaScript(buf.toString());
	}

	@Subscribe(filter = SimplePredicate.class)
	public void displayJoinGameMessage(final AjaxRequestTarget target,
			final JoinGameNotificationCometChannel event)
	{
		final String fromSession = HatchetHarrySession.get().getPlayer().getName();
		final String fromEvent = event.getPlayerName();
		final boolean cond = fromSession.equals(fromEvent);

		System.out.println("&&& fromSession: " + fromSession + ", fromEvent: " + fromEvent
				+ ", equals? " + cond);

		System.out.println("ééé jsessionid: " + event.getJsessionid());

		if (cond)
		{
			target.appendJavaScript("var id = jQuery(jQuery('input[name=\"jsessionid\"]')[1]).attr(\"value\"); if ((typeof id != 'undefined') && (id != \""
					+ event.getJsessionid()
					+ "\") && (typeof window.gameId != 'undefined') && (window.gameId == "
					+ event.getGameId()
					+ ")) { jQuery.gritter.add({ title : 'A player joined in', text : 'Ready to play?', image : 'image/logoh2.gif', sticky : false, time : ''}); }");
		}
	}

	@Subscribe
	public void updateDataBox(final AjaxRequestTarget target, final UpdateDataBoxCometChannel event)
	{
		final DataBox db = new DataBox("dataBox", Long.valueOf(event.getGameId()));
		this.getDataBoxParent().addOrReplace(db);
		db.setOutputMarkupId(true);
		target.add(this.getDataBoxParent());
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
		this.galleryParent.addOrReplace(galleryToUpdate);

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
						spp.getUuid(), jsessionid, HomePage.this, HomePage.this.getDataBoxParent(),
						HatchetHarrySession.get().getGameId());
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
				final SidePlaceholderMoveBehavior spmb = new SidePlaceholderMoveBehavior(spp,
						spp.getUuid(), jsessionid, HomePage.this, HomePage.this.getDataBoxParent(),
						HatchetHarrySession.get().getGameId());
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

	public WebMarkupContainer getGraveyardParent()
	{
		return this.graveyardParent;
	}

	public WebMarkupContainer getPlayCardParent()
	{
		return this.playCardParent;
	}

}
