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

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.alienlabs.hatchetharry.HatchetHarryApplication;
import org.alienlabs.hatchetharry.HatchetHarrySession;
import org.alienlabs.hatchetharry.model.CardZone;
import org.alienlabs.hatchetharry.model.Deck;
import org.alienlabs.hatchetharry.model.Game;
import org.alienlabs.hatchetharry.model.MagicCard;
import org.alienlabs.hatchetharry.model.Player;
import org.alienlabs.hatchetharry.model.Side;
import org.alienlabs.hatchetharry.model.Token;
import org.alienlabs.hatchetharry.model.User;
import org.alienlabs.hatchetharry.model.channel.AddSideCometChannel;
import org.alienlabs.hatchetharry.model.channel.AddSidesFromOtherBrowsersCometChannel;
import org.alienlabs.hatchetharry.model.channel.ArrowDrawCometChannel;
import org.alienlabs.hatchetharry.model.channel.CardMoveCometChannel;
import org.alienlabs.hatchetharry.model.channel.CardRotateCometChannel;
import org.alienlabs.hatchetharry.model.channel.CardZoneMoveCometChannel;
import org.alienlabs.hatchetharry.model.channel.CardZoneMoveNotifier;
import org.alienlabs.hatchetharry.model.channel.ConsoleLogCometChannel;
import org.alienlabs.hatchetharry.model.channel.CountCardsCometChannel;
import org.alienlabs.hatchetharry.model.channel.DestroyTokenCometChannel;
import org.alienlabs.hatchetharry.model.channel.JoinGameNotificationCometChannel;
import org.alienlabs.hatchetharry.model.channel.MoveSideCometChannel;
import org.alienlabs.hatchetharry.model.channel.NotifierAction;
import org.alienlabs.hatchetharry.model.channel.NotifierCometChannel;
import org.alienlabs.hatchetharry.model.channel.PlayCardFromGraveyardCometChannel;
import org.alienlabs.hatchetharry.model.channel.PlayCardFromHandCometChannel;
import org.alienlabs.hatchetharry.model.channel.PlayTopLibraryCardCometChannel;
import org.alienlabs.hatchetharry.model.channel.PutToExileFromBattlefieldCometChannel;
import org.alienlabs.hatchetharry.model.channel.PutToGraveyardCometChannel;
import org.alienlabs.hatchetharry.model.channel.PutToHandFromBattlefieldCometChannel;
import org.alienlabs.hatchetharry.model.channel.PutTokenOnBattlefieldCometChannel;
import org.alienlabs.hatchetharry.model.channel.PutTopLibraryCardToGraveyardCometChannel;
import org.alienlabs.hatchetharry.model.channel.PutTopLibraryCardToHandCometChannel;
import org.alienlabs.hatchetharry.model.channel.RevealHandCometChannel;
import org.alienlabs.hatchetharry.model.channel.RevealTopLibraryCardCometChannel;
import org.alienlabs.hatchetharry.model.channel.SwitchDrawModeCometChannel;
import org.alienlabs.hatchetharry.model.channel.UntapAllCometChannel;
import org.alienlabs.hatchetharry.model.channel.UpdateCardPanelCometChannel;
import org.alienlabs.hatchetharry.model.channel.UpdateDataBoxCometChannel;
import org.alienlabs.hatchetharry.model.channel.UpdateTokenPanelCometChannel;
import org.alienlabs.hatchetharry.model.channel.consolelog.AbstractConsoleLogStrategy;
import org.alienlabs.hatchetharry.model.channel.consolelog.ConsoleLogStrategy;
import org.alienlabs.hatchetharry.model.channel.consolelog.ConsoleLogType;
import org.alienlabs.hatchetharry.service.PersistenceService;
import org.alienlabs.hatchetharry.service.RuntimeDataGenerator;
import org.alienlabs.hatchetharry.view.clientsideutil.JavaScriptUtils;
import org.alienlabs.hatchetharry.view.component.AboutModalWindow;
import org.alienlabs.hatchetharry.view.component.CardPanel;
import org.alienlabs.hatchetharry.view.component.ChatPanel;
import org.alienlabs.hatchetharry.view.component.ClockPanel;
import org.alienlabs.hatchetharry.view.component.ConferencePanel;
import org.alienlabs.hatchetharry.view.component.CountCardsModalWindow;
import org.alienlabs.hatchetharry.view.component.CreateGameModalWindow;
import org.alienlabs.hatchetharry.view.component.CreateTokenModalWindow;
import org.alienlabs.hatchetharry.view.component.DataBox;
import org.alienlabs.hatchetharry.view.component.ExileComponent;
import org.alienlabs.hatchetharry.view.component.ExternalImage;
import org.alienlabs.hatchetharry.view.component.FacebookLoginBehavior;
import org.alienlabs.hatchetharry.view.component.GameNotifierBehavior;
import org.alienlabs.hatchetharry.view.component.GraveyardComponent;
import org.alienlabs.hatchetharry.view.component.HandComponent;
import org.alienlabs.hatchetharry.view.component.ImportDeckModalWindow;
import org.alienlabs.hatchetharry.view.component.JoinGameModalWindow;
import org.alienlabs.hatchetharry.view.component.LoginModalWindow;
import org.alienlabs.hatchetharry.view.component.MagicCardTooltipPanel;
import org.alienlabs.hatchetharry.view.component.MessageRedisplayBehavior;
import org.alienlabs.hatchetharry.view.component.PlayCardFromGraveyardBehavior;
import org.alienlabs.hatchetharry.view.component.PlayCardFromHandBehavior;
import org.alienlabs.hatchetharry.view.component.RedrawArrowsBehavior;
import org.alienlabs.hatchetharry.view.component.RevealTopLibraryCardModalWindow;
import org.alienlabs.hatchetharry.view.component.SidePlaceholderPanel;
import org.alienlabs.hatchetharry.view.component.TeamInfoModalWindow;
import org.alienlabs.hatchetharry.view.component.TokenTooltipPanel;
import org.alienlabs.hatchetharry.view.component.UserPreferencesModalWindow;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.atmosphere.JQueryWicketAtmosphereResourceReference;
import org.apache.wicket.atmosphere.Subscribe;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow.WindowClosedCallback;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.ListDataProvider;
import org.apache.wicket.model.Model;
import org.apache.wicket.protocol.http.servlet.ServletWebRequest;
import org.apache.wicket.request.http.WebResponse;
import org.apache.wicket.request.resource.PackageResourceReference;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;
import org.wicketstuff.facebook.FacebookSdk;

import ch.qos.mistletoe.wicket.TestReportPage;

import com.aplombee.QuickView;
import com.google.common.io.Files;

/**
 * HatchetHarry one and only WebPage
 * 
 * @author Andrey Belyaev
 * @author Zala Goupil
 */
public class HomePage extends TestReportPage
{
	final HatchetHarrySession session;

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
	ModalWindow importDeckWindow;
	ModalWindow revealTopLibraryCardWindow;
	ModalWindow revealTopLibraryCardWindowResponsive;
	private final List<ModalWindow> allOpenRevealTopLibraryCardWindows;
	ModalWindow createTokenWindow;
	ModalWindow countCardsWindow;
	ModalWindow loginWindow;
	ModalWindow preferencesWindow;

	Player player;
	Deck deck;
	List<MagicCard> hand;
	private final WebMarkupContainer parentPlaceholder;
	WebMarkupContainer playCardLink;
	// TODO remove this
	WebMarkupContainer playCardParent;
	WebMarkupContainer playCardFromGraveyardLink;

	final WebMarkupContainer galleryParent;
	final WebMarkupContainer galleryRevealParent;
	final Component galleryReveal;

	final WebMarkupContainer graveyardParent;
	final WebMarkupContainer exileParent;
	WebMarkupContainer thumbsPlaceholder;
	WebMarkupContainer graveyardThumbsPlaceholder;

	private AjaxLink<Void> endTurnLink;
	private AjaxLink<Void> inResponseLink;
	private AjaxLink<Void> fineForMeLink;
	private AjaxLink<Void> untapAllLink;
	private AjaxLink<Void> untapAndDrawLink;

	WebMarkupContainer endTurnPlaceholder;
	WebMarkupContainer inResponsePlaceholder;
	WebMarkupContainer fineForMePlaceholder;
	WebMarkupContainer untapAllPlaceholder;
	WebMarkupContainer untapAndDrawPlaceholder;

	private WebMarkupContainer dataBoxParent;

	private Component dataBox;

	private final WebMarkupContainer firstSidePlaceholderParent;
	private final WebMarkupContainer secondSidePlaceholderParent;

	private PlayCardFromHandBehavior playCardBehavior;
	ClockPanel clockPanel;

	private AjaxLink<Void> createGameLink;
	private AjaxLink<Void> joinGameLink;

	private QuickView<MagicCard> allCardsInBattlefield;
	private List<MagicCard> allMagicCardsInBattlefield;

	private QuickView<MagicCard> allTooltips;
	private List<MagicCard> allTooltipsInBattlefield;

	private final WebMarkupContainer sideParent;

	private final QuickView<Player> allSidesInGame;

	private final List<Player> allPlayerSidesInGame;

	private final WebMarkupContainer drawModeParent;

	private Label username;

	private final WebMarkupContainer usernameParent;

	private final WebMarkupContainer conferenceParent;

	public HomePage() throws IOException
	{
		this.session = HatchetHarrySession.get();

		final ServletWebRequest servletWebRequest = (ServletWebRequest)this.getRequest();
		final HttpServletRequest request = servletWebRequest.getContainerRequest();
		final String req = request.getQueryString();

		if ((req != null) && req.contains("endGame=true"))
		{
			HomePage.LOGGER.info("restart game for player: " + this.session.getPlayer().getId()
					+ " & game: " + this.session.getGameId());

			final ConsoleLogStrategy logger = AbstractConsoleLogStrategy.chooseStrategy(
					ConsoleLogType.GAME, null, null, null, null,
					this.session.getPlayer().getName(), null, null, null, false,
					this.session.getGameId());
			final NotifierCometChannel ncc = new NotifierCometChannel(
					NotifierAction.END_GAME_ACTION, null, null, this.session.getPlayer().getName(),
					null, null, null, null, "");
			final List<BigInteger> allPlayersInGameExceptMe = this.persistenceService
					.giveAllPlayersFromGameExceptMe(this.session.getGameId(), this.session
							.getPlayer().getId());

			for (int i = 0; i < allPlayersInGameExceptMe.size(); i++)
			{
				final Long playerToWhomToSend = allPlayersInGameExceptMe.get(i).longValue();
				final String pageUuid = HatchetHarryApplication.getCometResources().get(
						playerToWhomToSend);
				HatchetHarryApplication.get().getEventBus()
						.post(new ConsoleLogCometChannel(logger), pageUuid);
				HatchetHarryApplication.get().getEventBus().post(ncc, pageUuid);
			}

			this.session.invalidate();
			throw new RestartResponseException(HomePage.class);
		}

		// Resources
		this.addHeadResources();

		final FacebookSdk fsdk = new FacebookSdk("fb-root", "1398596203720626");
		fsdk.setFbAdmins("goupilpierre@wanadoo.fr");
		this.add(fsdk);

		// Side
		this.sideParent = new WebMarkupContainer("sideParent");
		this.sideParent.setOutputMarkupId(true);

		this.allPlayerSidesInGame = this.persistenceService.getAllPlayersOfGame(HatchetHarrySession
				.get().getGameId());
		final ListDataProvider<Player> data = new ListDataProvider<Player>(
				this.allPlayerSidesInGame);

		this.allSidesInGame = this.populateSides(data);

		this.sideParent.add(this.allSidesInGame);
		this.add(this.sideParent);

		// Hand
		this.parentPlaceholder = new WebMarkupContainer("parentPlaceholder");
		this.parentPlaceholder.setOutputMarkupId(true);
		this.add(this.parentPlaceholder);

		this.galleryParent = new WebMarkupContainer("galleryParent");
		this.galleryParent.setMarkupId("galleryParent");
		this.galleryParent.setOutputMarkupId(true);
		this.add(this.galleryParent);

		this.galleryRevealParent = new WebMarkupContainer("galleryRevealParent");
		this.galleryRevealParent.setMarkupId("galleryRevealParent");
		this.galleryRevealParent.setOutputMarkupId(true);
		this.galleryReveal = new WebMarkupContainer("galleryReveal");
		this.galleryReveal.setOutputMarkupId(true);
		this.galleryRevealParent.add(this.galleryReveal);
		this.add(this.galleryRevealParent);

		this.graveyardParent = new WebMarkupContainer("graveyardParent");
		this.graveyardParent.setMarkupId("graveyardParent");
		this.graveyardParent.setOutputMarkupId(true);
		this.add(this.graveyardParent);

		this.exileParent = new WebMarkupContainer("exileParent");
		this.exileParent.setMarkupId("exileParent");
		this.exileParent.setOutputMarkupId(true);
		this.add(this.exileParent);

		// Welcome message
		final Label message1 = new Label("message1",
				"version 0.7.0 (release Merry kiss my tralala),");
		final Label message2 = new Label("message2", "built on Wednesday, 25th of December 2013.");
		this.add(message1, message2);

		// Comet clock channel
		this.clockPanel = new ClockPanel("clockPanel", Model.of("###"));
		this.clockPanel.setOutputMarkupId(true);
		this.clockPanel.setMarkupId("clock");
		this.add(this.clockPanel);

		// Sides
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

		final WebMarkupContainer balduParent = new WebMarkupContainer("balduParent");
		balduParent.setOutputMarkupId(true);
		balduParent.setMarkupId("tour_6");

		if (!this.session.isGameCreated())
		{
			this.createPlayer();

			this.buildHandCards();
			this.buildHandMarkup();
			this.buildDataBox(this.player.getGame().getId());
			final MagicCard card = this.persistenceService.findCardByName("Balduvian Horde");
			if ((null != card)
					&& (!this.session.isMySidePlaceholderInSesion(this.session.getPlayer()
							.getSide().getSideName())))
			{
				balduParent.add(new CardPanel("baldu", card.getSmallImageFilename(), card
						.getUuidObject(), this.player));
				this.session.getAllMagicCardsInBattleField().add(card);
			}
		}
		else
		{
			this.player = this.session.getPlayer();

			this.buildHandCards();
			this.restoreBattlefieldState();
			this.buildDataBox(this.player.getGame().getId());

			balduParent.add(new WebMarkupContainer("baldu"));
		}
		this.add(balduParent);

		// Placeholders for CardPanel-adding with AjaxRequestTarget
		this.createCardPanelPlaceholders();

		this.buildGraveyardMarkup();
		this.buildExileMarkup();
		this.buildDock();

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
		this.aboutWindow = this.generateAboutLink("aboutLinkResponsive", this.aboutWindow);
		this.teamInfoWindow = this.generateTeamInfoLink("teamInfoLinkResponsive",
				this.teamInfoWindow);

		final GameNotifierBehavior notif = new GameNotifierBehavior(this);
		this.add(notif);

		this.createGameWindow = new ModalWindow("createGameWindow");
		this.add(this.createGameWindow = this.generateCreateGameModalWindow("createGameLink",
				this.player, this.firstSidePlaceholderParent, this.createGameWindow));
		this.add(this.createGameWindow = this.generateCreateGameModalWindow(
				"createGameLinkResponsive", this.player, this.firstSidePlaceholderParent,
				this.createGameWindow));

		this.joinGameWindow = new ModalWindow("joinGameWindow");
		this.add(this.joinGameWindow = this.generateJoinGameModalWindow("joinGameLink",
				this.player, this.joinGameWindow));
		this.add(this.joinGameWindow = this.generateJoinGameModalWindow("joinGameLinkResponsive",
				this.player, this.joinGameWindow));

		this.generatePlayCardLink(this.hand);
		this.add(this.generatePlayCardFromGraveyardLink("playCardFromGraveyardLinkDesktop"));
		this.add(this.generatePlayCardFromGraveyardLink("playCardFromGraveyardLinkResponsive"));
		this.generateCardPanels();

		this.generateDrawCardLink();
		final RedrawArrowsBehavior rab = new RedrawArrowsBehavior(this.player.getGame().getId());
		this.add(rab);

		// Comet chat channel
		this.add(new ChatPanel("chatPanel", this.player.getId()));

		this.buildEndTurnLink();
		this.buildInResponseLink();
		this.buildFineForMeLink();
		this.buildUntapAllLink();
		this.buildUntapAndDrawLink();
		this.buildCombatLink();

		this.importDeckWindow = new ModalWindow("importDeckWindow");
		this.generateImportDeckLink("importDeckLink", this.importDeckWindow);
		this.generateImportDeckLink("importDeckLinkResponsive", this.importDeckWindow);

		this.allOpenRevealTopLibraryCardWindows = new ArrayList<ModalWindow>();
		this.generateRevealTopLibraryCardLink("revealTopLibraryCardLink",
				"revealTopLibraryCardWindow");
		this.generateRevealTopLibraryCardLink("revealTopLibraryCardLinkResponsive",
				"revealTopLibraryCardWindowResponsive");

		this.createTokenWindow = new ModalWindow("createTokenWindow");
		this.generateCreateTokenLink("createTokenLink", this.createTokenWindow);
		this.generateCreateTokenLink("createTokenLinkResponsive", this.createTokenWindow);

		this.countCardsWindow = new ModalWindow("countCardsWindow");
		this.generateCountCardsLink("countCardsLink", this.countCardsWindow);
		this.generateCountCardsLink("countCardsLinkResponsive", this.countCardsWindow);
		this.generateInsertDivisionLink("insertDivisionLink");
		this.generateInsertDivisionLink("insertDivisionLinkResponsive");
		this.generateShuffleLibraryLink("shuffleLibraryLink");
		this.generateShuffleLibraryLink("shuffleLibraryLinkResponsive");

		this.loginWindow = new ModalWindow("loginWindow");
		this.generateLoginLink("loginLink", this.loginWindow);
		this.generateLoginLink("loginLinkResponsive", this.loginWindow);
		final FacebookLoginBehavior flb = new FacebookLoginBehavior();
		this.add(flb);

		this.preferencesWindow = new ModalWindow("preferencesWindow");
		this.generatePreferencesLink("preferencesLink", this.preferencesWindow);
		this.generatePreferencesLink("preferencesLinkResponsive", this.preferencesWindow);

		this.generateEndGameLink("endGameLink");
		this.generateEndGameLink("endGameLinkResponsive");
		this.generateHideAllTooltipsLink("hideAllTooltipsLink");
		this.generateHideAllTooltipsLink("hideAllTooltipsLinkResponsive");

		this.conferenceParent = new WebMarkupContainer("conferenceParent");
		this.conferenceParent.setOutputMarkupId(true);
		final ConferencePanel conference = new ConferencePanel("conference");
		conference.setOutputMarkupId(true);
		this.conferenceParent.add(conference);
		this.add(this.conferenceParent);
		this.generateOpenConferenceLink("conferenceOpener");
		this.generateOpenConferenceLink("conferenceOpenerResponsive");

		this.generateRevealHandLink("revealHandLink");
		this.generateRevealHandLink("revealHandLinkResponsive");

		// For console logs & chat messages
		this.add(new MessageRedisplayBehavior(HatchetHarrySession.get().getGameId()));

		this.drawModeParent = new WebMarkupContainer("drawModeParent");
		this.drawModeParent.setOutputMarkupId(true);

		if (this.persistenceService.getGame(HatchetHarrySession.get().getGameId()).isDrawMode())
		{
			this.drawModeParent.add(new ExternalImage("drawModeOn", "image/draw_mode_on.png"));
		}
		else
		{
			this.drawModeParent.add(new WebMarkupContainer("drawModeOn").setVisible(false));
		}

		this.add(this.drawModeParent);

		if (this.session.isLoggedIn())
		{
			this.username = new Label("username", "Logged in as " + this.session.getUsername());
			this.username.setOutputMarkupId(true);
		}
		else
		{
			this.username = new Label("username", "Not logged in");
			this.username.setOutputMarkupId(true);
		}

		this.usernameParent = new WebMarkupContainer("usernameParent");
		this.usernameParent.setOutputMarkupId(true);
		this.usernameParent.add(this.username);
		this.add(this.usernameParent);
	}

	// TODO: really necessary?
	private final void generateCardPanels()
	{
		this.generateCardListView(this.persistenceService
				.getAllCardsInBattleFieldForAGame(this.player.getGame().getId()), false);
	}

	private void generateHideAllTooltipsLink(final String id)
	{
		this.add(new AjaxLink<Void>(id)
		{
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(final AjaxRequestTarget target)
			{
				target.appendJavaScript(JavaScriptUtils.HIDE_ALL_TOOLTIPS);
				target.appendJavaScript(JavaScriptUtils.HIDE_MENUS);
			}

		});
	}

	private void generateOpenConferenceLink(final String id)
	{
		this.add(new AjaxLink<Void>(id)
		{
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(final AjaxRequestTarget target)
			{
				target.prependJavaScript(JavaScriptUtils.HIDE_MENUS);
				target.appendJavaScript("jQuery('#conference').dialog('open');");

				if (null != HomePage.this.session.getUsername())
				{
					final User user = HomePage.this.persistenceService
							.getUser(HomePage.this.session.getUsername());
					HomePage.LOGGER.info("###user: " + user);

					if (null != user)
					{
						target.appendJavaScript("jQuery('#txtDisplayName').val('" + user.getLogin()
								+ "');");
						target.appendJavaScript("jQuery('#txtPrivateIdentity').val('"
								+ user.getPrivateIdentity() + "');");
						target.appendJavaScript("jQuery('#txtPublicIdentity').val('"
								+ user.getIdentity() + "');");
						target.appendJavaScript("jQuery('#txtPassword').val('" + user.getPassword()
								+ "');");
						target.appendJavaScript("jQuery('#txtRealm').val('" + user.getRealm()
								+ "');");
					}
				}
			}

		});
	}

	private void generateRevealHandLink(final String id)
	{
		this.add(new AjaxLink<Void>(id)
		{
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(final AjaxRequestTarget target)
			{
				final Long gameId = HomePage.this.session.getGameId();
				final List<BigInteger> allPlayersInGameExceptMe = HomePage.this.persistenceService
						.giveAllPlayersFromGameExceptMe(gameId, HomePage.this.session.getPlayer()
								.getId());

				final NotifierCometChannel ncc = new NotifierCometChannel(
						NotifierAction.REVEAL_HAND, null, null, HomePage.this.session.getPlayer()
								.getName(), "", "", "", null, "");
				final ConsoleLogStrategy logger = AbstractConsoleLogStrategy.chooseStrategy(
						ConsoleLogType.REVEAL_HAND, null, null, null, null, HomePage.this.session
								.getPlayer().getName(), null, null, null, false, gameId);
				final RevealHandCometChannel rhcc = new RevealHandCometChannel(gameId,
						HomePage.this.session.getPlayer().getId(), HomePage.this.session
								.getPlayer().getDeck().getDeckId());

				for (int i = 0; i < allPlayersInGameExceptMe.size(); i++)
				{
					final Long playerToWhomToSend = allPlayersInGameExceptMe.get(i).longValue();
					final String pageUuid = HatchetHarryApplication.getCometResources().get(
							playerToWhomToSend);


					HatchetHarryApplication.get().getEventBus().post(ncc, pageUuid);
					HatchetHarryApplication.get().getEventBus()
							.post(new ConsoleLogCometChannel(logger), pageUuid);
					HatchetHarryApplication.get().getEventBus().post(rhcc, pageUuid);
				}

				final String myPageUuid = HatchetHarryApplication.getCometResources().get(
						HomePage.this.session.getPlayer().getId());
				HatchetHarryApplication.get().getEventBus().post(ncc, myPageUuid);
				HatchetHarryApplication.get().getEventBus()
						.post(new ConsoleLogCometChannel(logger), myPageUuid);
			}

		});
	}

	private void generateEndGameLink(final String id)
	{
		this.add(new AjaxLink<Void>(id)
		{
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(final AjaxRequestTarget target)
			{
				HomePage.LOGGER.info("end game");
				target.appendJavaScript("var r = confirm('Are you sure that you want to end this game?'); if (r==true) { window.location = window.location + '?endGame=true'; }; ");
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
				final Player _player = HomePage.this.persistenceService
						.getPlayer(HomePage.this.session.getPlayer().getId());
				final boolean isHandDisplayed = _player.isHandDisplayed();

				if (isHandDisplayed)
				{
					HomePage.this.galleryParent.addOrReplace(new WebMarkupContainer("gallery"));
					target.add(HomePage.this.galleryParent);
				}
				else
				{
					JavaScriptUtils.updateHand(target);

				}

				_player.setHandDisplayed(!isHandDisplayed);
				HomePage.this.persistenceService.updatePlayer(_player);
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

		final AjaxLink<Void> drawModeLink = new AjaxLink<Void>("drawModeLink")
		{
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(final AjaxRequestTarget target)
			{
				final Game g = HomePage.this.persistenceService.getGame(HomePage.this.session
						.getGameId());
				g.setDrawMode(!g.isDrawMode());
				HomePage.this.persistenceService.updateGame(g);

				if (!g.isDrawMode())
				{
					HomePage.this.persistenceService.deleteAllArrowsForAGame(g.getId());
				}

				final List<BigInteger> allPlayersInGame = HomePage.this.persistenceService
						.giveAllPlayersFromGame(HomePage.this.session.getGameId());

				for (int i = 0; i < allPlayersInGame.size(); i++)
				{
					final Long playerToWhomToSend = allPlayersInGame.get(i).longValue();
					final String pageUuid = HatchetHarryApplication.getCometResources().get(
							playerToWhomToSend);
					final SwitchDrawModeCometChannel sdmcc = new SwitchDrawModeCometChannel(
							g.isDrawMode());

					HatchetHarryApplication.get().getEventBus().post(sdmcc, pageUuid);
				}
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

		this.add(drawModeLink);

		final AjaxLink<Void> showGraveyardLink = new AjaxLink<Void>("graveyardLink")
		{
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(final AjaxRequestTarget target)
			{
				final Player _player = HomePage.this.persistenceService
						.getPlayer(HomePage.this.session.getPlayer().getId());
				final Boolean isGraveyardDisplayed = _player.isGraveyardDisplayed();

				if ((isGraveyardDisplayed != null) && isGraveyardDisplayed)
				{
					HomePage.this.graveyardParent.addOrReplace(new WebMarkupContainer("graveyard"));
					target.add(HomePage.this.graveyardParent);
				}
				else
				{
					JavaScriptUtils.updateGraveyard(target);

				}

				if ((isGraveyardDisplayed != null) && (isGraveyardDisplayed.booleanValue() == true))
				{
					_player.setGraveyardDisplayed(false);
				}
				else
				{
					_player.setGraveyardDisplayed(true);
				}
				HomePage.this.persistenceService.updatePlayer(_player);
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

		final AjaxLink<Void> showExileLink = new AjaxLink<Void>("exileLink")
		{
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(final AjaxRequestTarget target)
			{
				final Player _player = HomePage.this.persistenceService
						.getPlayer(HomePage.this.session.getPlayer().getId());
				final Boolean isExileDisplayed = _player.isExileDisplayed();

				if ((isExileDisplayed != null) && isExileDisplayed)
				{
					HomePage.this.exileParent.addOrReplace(new WebMarkupContainer("exile"));
					target.add(HomePage.this.exileParent);
				}
				else
				{
					JavaScriptUtils.updateExile(target, _player.getGame().getId(), _player.getId(),
							_player.getDeck().getDeckId());

				}

				if ((isExileDisplayed != null) && (isExileDisplayed.booleanValue() == true))
				{
					_player.setExileDisplayed(false);
				}
				else
				{
					_player.setExileDisplayed(true);
				}

				HomePage.this.persistenceService.updatePlayer(_player);
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

		this.add(showExileLink);
	}

	private void buildEndTurnLink()
	{
		this.endTurnPlaceholder = new WebMarkupContainer("endTurnPlaceholder");
		this.endTurnPlaceholder.setOutputMarkupId(true);

		this.endTurnLink = new AjaxLink<Void>("endTurnLink")
		{
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(final AjaxRequestTarget target)
			{
				final Player me = HomePage.this.session.getPlayer();
				final Long gameId = HomePage.this.persistenceService
						.getPlayer(HomePage.this.session.getPlayer().getId()).getGame().getId();

				final ConsoleLogStrategy logger = AbstractConsoleLogStrategy.chooseStrategy(
						ConsoleLogType.END_OF_TURN, null, null, null, null, HomePage.this.session
								.getPlayer().getName(), null, null, null, false,
						HomePage.this.session.getGameId());

				final List<BigInteger> allPlayersInGame = HomePage.this.persistenceService
						.giveAllPlayersFromGame(gameId);

				for (int i = 0; i < allPlayersInGame.size(); i++)
				{
					final Long playerToWhomToSend = allPlayersInGame.get(i).longValue();
					final String pageUuid = HatchetHarryApplication.getCometResources().get(
							playerToWhomToSend);
					final NotifierCometChannel ncc = new NotifierCometChannel(
							NotifierAction.END_OF_TURN_ACTION, null, me.getId(), me.getName(), me
									.getSide().getSideName(), null, null, null, "");

					HatchetHarryApplication.get().getEventBus().post(ncc, pageUuid);
					HatchetHarryApplication.get().getEventBus()
							.post(new ConsoleLogCometChannel(logger), pageUuid);
				}

				HomePage.this.session.setCombatInProgress(false);
			}

		};
		this.endTurnLink.setMarkupId("endTurnLink");
		this.endTurnLink.setOutputMarkupId(true);

		this.endTurnPlaceholder.add(this.endTurnLink);
		this.add(this.endTurnPlaceholder);
	}

	private void buildInResponseLink()
	{
		this.inResponsePlaceholder = new WebMarkupContainer("inResponsePlaceholder");
		this.inResponsePlaceholder.setOutputMarkupId(true);

		this.inResponseLink = new AjaxLink<Void>("inResponseLink")
		{
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(final AjaxRequestTarget target)
			{
				final Game game = HomePage.this.persistenceService.getGame(HomePage.this.session
						.getGameId());

				final Player me = HomePage.this.session.getPlayer();
				final List<BigInteger> allPlayersInGame = HomePage.this.persistenceService
						.giveAllPlayersFromGame(game.getId());

				for (int i = 0; i < allPlayersInGame.size(); i++)
				{
					final Long playerToWhomToSend = allPlayersInGame.get(i).longValue();
					final String pageUuid = HatchetHarryApplication.getCometResources().get(
							playerToWhomToSend);
					final NotifierCometChannel ncc = new NotifierCometChannel(
							NotifierAction.IN_RESPONSE_ACTION, null, null, me.getName(), null,
							null, null, null, "");

					HatchetHarryApplication.get().getEventBus().post(ncc, pageUuid);
				}

			}

		};
		this.inResponseLink.setMarkupId("inResponseLink");
		this.inResponseLink.setOutputMarkupId(true);

		this.inResponsePlaceholder.add(this.inResponseLink);
		this.add(this.inResponsePlaceholder);
	}

	private void buildFineForMeLink()
	{
		this.fineForMePlaceholder = new WebMarkupContainer("fineForMePlaceholder");
		this.fineForMePlaceholder.setOutputMarkupId(true);

		this.fineForMeLink = new AjaxLink<Void>("fineForMeLink")
		{
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(final AjaxRequestTarget target)
			{
				final Game game = HomePage.this.persistenceService.getGame(HomePage.this.session
						.getGameId());

				final Player me = HomePage.this.session.getPlayer();
				final List<BigInteger> allPlayersInGame = HomePage.this.persistenceService
						.giveAllPlayersFromGame(game.getId());

				for (int i = 0; i < allPlayersInGame.size(); i++)
				{
					final Long playerToWhomToSend = allPlayersInGame.get(i).longValue();
					final String pageUuid = HatchetHarryApplication.getCometResources().get(
							playerToWhomToSend);
					final NotifierCometChannel ncc = new NotifierCometChannel(
							NotifierAction.FINE_FOR_ME_ACTION, null, null, me.getName(), null,
							null, null, null, "");

					HatchetHarryApplication.get().getEventBus().post(ncc, pageUuid);
				}
			}
		};

		this.fineForMeLink.setOutputMarkupId(true).setMarkupId("fineForMeLink");
		this.fineForMePlaceholder.add(this.fineForMeLink);
		this.add(this.fineForMePlaceholder);
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
				final Long gameId = HomePage.this.session.getGameId();
				final List<BigInteger> allPlayersInGame = HomePage.this.persistenceService
						.giveAllPlayersFromGame(gameId);

				final List<MagicCard> allCards = HomePage.this.persistenceService
						.getAllCardsAndTokensInBattlefieldForAGameAndAPlayer(gameId,
								HatchetHarrySession.get().getPlayer().getId(), HatchetHarrySession
										.get().getPlayer().getDeck().getDeckId());
				for (int i = 0; i < allCards.size(); i++)
				{
					final MagicCard mc = allCards.get(i);

					if (null != mc)
					{
						mc.setTapped(false);

						if (null != mc.getToken())
						{
							mc.getToken().setTapped(false);
							HomePage.this.persistenceService.updateToken(mc.getToken());
						}

						HomePage.this.persistenceService.updateCard(mc);
					}
				}

				final ConsoleLogStrategy logger = AbstractConsoleLogStrategy.chooseStrategy(
						ConsoleLogType.TAP_UNTAP, null, null, null, null, HatchetHarrySession.get()
								.getPlayer().getName(), null, null, null, false, gameId);

				for (int i = 0; i < allPlayersInGame.size(); i++)
				{
					final Long playerToWhomToSend = allPlayersInGame.get(i).longValue();
					final String pageUuid = HatchetHarryApplication.getCometResources().get(
							playerToWhomToSend);

					final UntapAllCometChannel uacc = new UntapAllCometChannel(gameId,
							HatchetHarrySession.get().getPlayer().getId(), HatchetHarrySession
									.get().getPlayer().getDeck().getDeckId(), HatchetHarrySession
									.get().getPlayer().getName(), allCards);
					HatchetHarryApplication.get().getEventBus().post(uacc, pageUuid);

					HatchetHarryApplication.get().getEventBus()
							.post(new ConsoleLogCometChannel(logger), pageUuid);
				}
			}

		};
		this.untapAllLink.setMarkupId("untapAllLink");
		this.untapAllLink.setOutputMarkupId(true);

		this.untapAllPlaceholder.add(this.untapAllLink);
		this.add(this.untapAllPlaceholder);
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
				target.appendJavaScript("jQuery('#untapAllLink').click(); setTimeout(\"jQuery('#drawCardLink').click();\", 250);");
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
		this.session.setDataBoxParent(this.dataBoxParent);

		this.dataBox = new DataBox("dataBox", _gameId);
		this.session.setDataBox(this.dataBox);
		this.dataBox.setOutputMarkupId(true);
		this.dataBoxParent.add(this.dataBox);

		this.add(this.dataBoxParent);
		HomePage.LOGGER.info("building DataBox with gameId= " + _gameId);
	}

	private final void buildHandCards()
	{
		if (this.session.isHandHasBeenCreated())
		{
			this.hand = this.session.getFirstCardsInHand();
		}
		else
		{
			this.hand = this.createFirstCards();
		}
	}

	private void createPlayer() throws IOException
	{
		final ServletWebRequest servletWebRequest = (ServletWebRequest)this.getPage().getRequest();
		final HttpServletRequest request = servletWebRequest.getContainerRequest();
		final String jsessionid = request.getRequestedSessionId();

		this.session.setGameCreated();

		if (this.persistenceService.getFirstPlayer() == null)
		{
			this.createPlayerAndDeck(jsessionid, "infrared", "infrared");
		}
		else
		{
			this.createPlayerAndDeck(jsessionid, "ultraviolet", "ultraviolet");
		}
	}

	private void createPlayerAndDeck(final String _jsessionid, final String _side,
			final String _name) throws IOException
	{
		Player p = new Player();
		final Side side = new Side();
		p.setSide(side);
		p.getSide().setSideName(_side);
		p.setName(_name);
		p.setJsessionid(_jsessionid);
		p.setLifePoints(20l);

		Game game = new Game();
		game = this.persistenceService.createGameAndPlayer(game, p);
		p = game.getPlayers().iterator().next();
		p.setGame(game);

		this.session.setPlayerHasBeenCreated();

		this.deck = this.runtimeDataGenerator.generateData(game.getId(), p.getId());
		this.deck.setCards(this.deck.shuffleLibrary(this.deck.getCards()));
		this.deck.setPlayerId(p.getId());

		p.setDeck(this.deck);
		this.persistenceService.updatePlayer(p);
		this.session.setGameId(game.getId());

		this.session.setPlayer(p);
		this.player = p;
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
			this.playCardBehavior = new PlayCardFromHandBehavior(mc.get(0).getUuidObject(), 0,
					this.session.getPlayer().getSide().getSideName());
			this.playCardLink.add(this.playCardBehavior);
		}

		this.playCardLink.setMarkupId("playCardLink0");
		this.playCardLink.setOutputMarkupId(true);
		playCardPlaceholder.add(this.playCardLink);

		this.add(playCardPlaceholder);
	}

	private WebMarkupContainer generatePlayCardFromGraveyardLink(final String id)
	{
		HomePage.LOGGER.info("Generating playCardFromGraveyard link");

		final WebMarkupContainer _playCardFromGraveyardLink = new WebMarkupContainer(id);
		_playCardFromGraveyardLink.setMarkupId(id);
		_playCardFromGraveyardLink.setOutputMarkupId(true);

		final PlayCardFromGraveyardBehavior _playCardFromGraveyardBehavior = new PlayCardFromGraveyardBehavior(
				this.session.getPlayer().getSide().getSideName());
		_playCardFromGraveyardLink.add(_playCardFromGraveyardBehavior);

		return _playCardFromGraveyardLink;
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
				HomePage.this.session.setCombatInProgress(!HomePage.this.session
						.isCombatInProgress());
				final Long gameId = HomePage.this.session.getGameId();
				final List<BigInteger> allPlayersInGame = HomePage.this.persistenceService
						.giveAllPlayersFromGame(gameId);

				final NotifierCometChannel ncc = new NotifierCometChannel(
						NotifierAction.COMBAT_IN_PROGRESS_ACTION, null, null, HomePage.this.session
								.getPlayer().getName(), "", "", "",
						HomePage.this.session.isCombatInProgress(), "");
				final ConsoleLogStrategy logger = AbstractConsoleLogStrategy.chooseStrategy(
						ConsoleLogType.COMBAT, null, null, HomePage.this.session
								.isCombatInProgress(), null, HomePage.this.session.getPlayer()
								.getName(), null, null, null, false, gameId);

				for (int i = 0; i < allPlayersInGame.size(); i++)
				{
					final Long playerToWhomToSend = allPlayersInGame.get(i).longValue();
					final String pageUuid = HatchetHarryApplication.getCometResources().get(
							playerToWhomToSend);


					HatchetHarryApplication.get().getEventBus().post(ncc, pageUuid);
					HatchetHarryApplication.get().getEventBus()
							.post(new ConsoleLogCometChannel(logger), pageUuid);
				}

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
				final List<MagicCard> cards = HomePage.this.persistenceService
						.getAllCardsInLibraryForDeckAndPlayer(HomePage.this.session.getPlayer()
								.getGame().getId(), HomePage.this.session.getPlayer().getId(),
								HomePage.this.session.getPlayer().getDeck().getDeckId());

				if ((cards != null) && (!cards.isEmpty()))
				{
					final MagicCard card = cards.get(0);

					final Deck _deck = HomePage.this.persistenceService
							.getDeck(HomePage.this.session.getPlayer().getDeck().getDeckId());
					_deck.getCards().remove(card);
					HomePage.this.persistenceService.saveDeck(_deck);

					card.setZone(CardZone.HAND);
					HomePage.this.persistenceService.updateCard(card);

					final ArrayList<MagicCard> list = HomePage.this.session.getFirstCardsInHand();
					list.add(card);
					HomePage.this.session.setFirstCardsInHand(list);

					JavaScriptUtils.updateHand(target);

					final Player me = HomePage.this.session.getPlayer();
					final Long gameId = HomePage.this.persistenceService
							.getPlayer(HomePage.this.session.getPlayer().getId()).getGame().getId();

					final Deck d = me.getDeck();
					final List<MagicCard> _hand = d
							.reorderMagicCards(HomePage.this.persistenceService
									.getAllCardsInHandForAGameAndAPlayer(gameId, me.getId(),
											d.getDeckId()));
					HomePage.this.persistenceService.updateAllMagicCards(_hand);
					final List<MagicCard> library = d
							.reorderMagicCards(HomePage.this.persistenceService
									.getAllCardsInLibraryForDeckAndPlayer(gameId, me.getId(),
											d.getDeckId()));
					HomePage.this.persistenceService.updateAllMagicCards(library);

					final List<BigInteger> allPlayersInGame = HomePage.this.persistenceService
							.giveAllPlayersFromGame(gameId);

					final ConsoleLogStrategy logger = AbstractConsoleLogStrategy.chooseStrategy(
							ConsoleLogType.DRAW_CARD, null, null, null, null, HatchetHarrySession
									.get().getPlayer().getName(), null, null, null, null, gameId);

					for (int i = 0; i < allPlayersInGame.size(); i++)
					{
						final Long playerToWhomToSend = allPlayersInGame.get(i).longValue();
						final String pageUuid = HatchetHarryApplication.getCometResources().get(
								playerToWhomToSend);

						final NotifierCometChannel ncc = new NotifierCometChannel(
								NotifierAction.DRAW_CARD_ACTION, null, me.getId(), me.getName(), me
										.getSide().getSideName(), null, null, null, "");

						try
						{
							HatchetHarryApplication.get().getEventBus().post(ncc, pageUuid);
						}
						catch (final NullPointerException ex)
						{
							// NPE in unit tests
							HomePage.LOGGER
									.error("exception thrown while posting in event bus", ex);
						}

						try
						{
							HatchetHarryApplication.get().getEventBus()
									.post(new ConsoleLogCometChannel(logger), pageUuid);
						}
						catch (final NullPointerException ex)
						{
							// NPE in unit tests
							HomePage.LOGGER
									.error("exception thrown while posting in event bus", ex);
						}

					}
				}
				else
				{
					throw new RuntimeException(
							"You've lost since you have no more card to draw. All your base are belong to us!");
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

	private void createCardPanelPlaceholders()
	{
		final WebMarkupContainer cardPlaceholder = new WebMarkupContainer("cardPlaceholder");
		cardPlaceholder.setOutputMarkupId(true);
		this.add(cardPlaceholder);
	}

	private void addHeadResources()
	{
		final WebMarkupContainer c = new WebMarkupContainer("headResources");
		c.add(new Behavior()
		{
			private static final long serialVersionUID = 1L;

			@Override
			public void renderHead(final Component component, final IHeaderResponse response)
			{
				super.renderHead(component, response);

				response.render(JavaScriptHeaderItem
						.forReference(JQueryWicketAtmosphereResourceReference.get()));
				response.render(JavaScriptHeaderItem.forReference(new PackageResourceReference(
						HomePage.class, "script/draggableHandle/jquery.ui.core.min-1.9.2.js")));
				response.render(JavaScriptHeaderItem.forReference(new PackageResourceReference(
						HomePage.class, "script/draggableHandle/jquery.ui.widget.min-1.9.2.js")));
				response.render(JavaScriptHeaderItem.forReference(new PackageResourceReference(
						HomePage.class, "script/draggableHandle/jquery.ui.mouse.min-1.9.2.js")));
				response.render(JavaScriptHeaderItem.forReference(new PackageResourceReference(
						HomePage.class, "script/draggableHandle/jquery.ui.touch-punch.js")));
				response.render(JavaScriptHeaderItem.forReference(new PackageResourceReference(
						HomePage.class, "script/draggableHandle/jquery.ui.draggable.min-1.9.2.js")));
				response.render(JavaScriptHeaderItem.forReference(new PackageResourceReference(
						HomePage.class, "script/draggableHandle/jquery.ui.droppable.min-1.9.2.js")));
				response.render(JavaScriptHeaderItem.forReference(new PackageResourceReference(
						HomePage.class, "script/draggableHandle/jquery.ui.position-1.9.2.js")));
				response.render(JavaScriptHeaderItem.forReference(new PackageResourceReference(
						HomePage.class, "script/jquery.ui.dialog-1.9.2.js")));
				response.render(JavaScriptHeaderItem.forReference(new PackageResourceReference(
						HomePage.class, "script/draggableHandle/offset.js")));
				response.render(JavaScriptHeaderItem.forReference(new PackageResourceReference(
						HomePage.class, "script/tour/jquery.easing.1.3.js")));
				response.render(JavaScriptHeaderItem.forReference(new PackageResourceReference(
						HomePage.class, "script/tour/jquery.cookie.js")));
				response.render(JavaScriptHeaderItem.forReference(new PackageResourceReference(
						HomePage.class, "script/tour/pageguide.js")));
				response.render(JavaScriptHeaderItem.forReference(new PackageResourceReference(
						HomePage.class, "script/menubar/jMenu.jquery.js")));
				response.render(JavaScriptHeaderItem.forReference(new PackageResourceReference(
						HomePage.class, "script/dock/jquery.jqdock.min.js")));
				response.render(JavaScriptHeaderItem.forReference(new PackageResourceReference(
						HomePage.class, "script/qunitTests/qUnit-1.11.0-min.js")));
				response.render(JavaScriptHeaderItem.forReference(new PackageResourceReference(
						HomePage.class, "script/conference/SIPml-api.js")));
				response.render(JavaScriptHeaderItem.forReference(new PackageResourceReference(
						HomePage.class, "script/conference/webrtc4all.js")));
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
						HomePage.class, "script/gallery/coda-sliderExile.1.1.1.pack.js")));
				response.render(JavaScriptHeaderItem.forReference(new PackageResourceReference(
						HomePage.class, "script/gallery/gallery.js")));
				response.render(JavaScriptHeaderItem.forReference(new PackageResourceReference(
						HomePage.class, "script/gallery/graveyard.js")));
				response.render(JavaScriptHeaderItem.forReference(new PackageResourceReference(
						HomePage.class, "script/gallery/exile.js")));
				response.render(JavaScriptHeaderItem.forReference(new PackageResourceReference(
						HomePage.class, "script/rotate/jQueryRotate.2.1.js")));
				response.render(JavaScriptHeaderItem.forReference(new PackageResourceReference(
						HomePage.class, "script/draggableHandle/jquery.hammer.min.js")));
				response.render(JavaScriptHeaderItem.forReference(new PackageResourceReference(
						HomePage.class, "script/notificon.js")));
				response.render(JavaScriptHeaderItem.forReference(new PackageResourceReference(
						HomePage.class, "script/jquery.jsPlumb-1.5.3-min.js")));

				response.render(CssHeaderItem.forReference(new PackageResourceReference(
						HomePage.class, "stylesheet/jMenu.jquery.css")));
				response.render(CssHeaderItem.forReference(new PackageResourceReference(
						HomePage.class, "stylesheet/layout.css")));
				response.render(CssHeaderItem.forReference(new PackageResourceReference(
						HomePage.class, "stylesheet/menu_black.css")));
				response.render(CssHeaderItem.forReference(new PackageResourceReference(
						HomePage.class, "stylesheet/pageguide.css")));
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
						HomePage.class, "stylesheet/demo-style.css")));
				response.render(CssHeaderItem.forReference(new PackageResourceReference(
						HomePage.class, "stylesheet/mobile.css")));
				response.render(CssHeaderItem.forReference(new PackageResourceReference(
						HomePage.class, "stylesheet/blue_gradient_table.css")));
				response.render(CssHeaderItem.forReference(new PackageResourceReference(
						HomePage.class, "stylesheet/tipsy.css")));
				response.render(CssHeaderItem.forReference(new PackageResourceReference(
						HomePage.class, "stylesheet/jquery-ui dialog.css")));
				response.render(CssHeaderItem.forReference(new PackageResourceReference(
						HomePage.class, "stylesheet/qunit-1.12.0.css")));
				response.render(CssHeaderItem.forReference(new PackageResourceReference(
						HomePage.class, "stylesheet/myStyle.css")));

				response.render(JavaScriptHeaderItem.forReference(new PackageResourceReference(
						HomePage.class, "script/toolbar/jquery.prettyPhoto.js")));
				response.render(JavaScriptHeaderItem.forReference(new PackageResourceReference(
						HomePage.class, "script/notifier/jquery.gritter.min.js")));
				response.render(JavaScriptHeaderItem.forReference(new PackageResourceReference(
						HomePage.class, "script/viewportSize-min.js")));
				response.render(JavaScriptHeaderItem.forReference(new PackageResourceReference(
						HomePage.class, "script/jquery.tipsy.js")));
				response.render(JavaScriptHeaderItem.forReference(new PackageResourceReference(
						HomePage.class, "script/google-analytics.js")));
			}
		});
		this.add(c);
	}

	private void buildHandMarkup()
	{
		final Component galleryToUpdate;
		final boolean isHandDisplayed = this.persistenceService.getPlayer(
				this.session.getPlayer().getId()).isHandDisplayed();
		galleryToUpdate = isHandDisplayed ? new HandComponent("gallery") : new WebMarkupContainer(
				"gallery");

		galleryToUpdate.setOutputMarkupId(true);
		this.galleryParent.add(galleryToUpdate);
	}

	private void buildGraveyardMarkup()
	{
		final Component graveyardToUpdate;
		final Boolean isGraveyardDisplayed = this.persistenceService.getPlayer(
				this.session.getPlayer().getId()).isGraveyardDisplayed();
		graveyardToUpdate = ((isGraveyardDisplayed != null) && isGraveyardDisplayed)
				? new GraveyardComponent("graveyard")
				: new WebMarkupContainer("graveyard");

		graveyardToUpdate.setOutputMarkupId(true);
		this.graveyardParent.add(graveyardToUpdate);
	}

	private void buildExileMarkup()
	{
		final Component exileToUpdate;
		final Boolean isExileDisplayed = this.persistenceService.getPlayer(
				this.session.getPlayer().getId()).isExileDisplayed();
		exileToUpdate = ((isExileDisplayed != null) && isExileDisplayed) ? new ExileComponent(
				"exile") : new WebMarkupContainer("exile");

		exileToUpdate.setOutputMarkupId(true);
		this.exileParent.add(exileToUpdate);
	}

	private List<MagicCard> createFirstCards()
	{
		if (this.session.isPlayerCreated())
		{
			this.player = this.session.getPlayer();
			this.deck = this.persistenceService.getDeck(this.player.getDeck().getDeckId());
			if (this.deck == null)
			{
				this.deck = this.persistenceService.getDeck(1l);
				this.player.setDeck(this.deck);
			}
			this.deck.setCards(this.persistenceService.getAllCardsFromDeck(this.deck.getDeckId()));
			final ArrayList<MagicCard> cards = new ArrayList<MagicCard>();

			if (!this.session.isHandCardsHaveBeenBuilt())
			{
				this.deck.setCards(this.deck.shuffleLibrary(this.deck.getCards()));
			}

			for (int i = 0; i < 7; i++)
			{
				final MagicCard mc = this.deck.getCards().get(i);
				mc.setZone(CardZone.HAND);
				mc.setGameId(this.session.getPlayer().getGame().getId());
				this.persistenceService.updateCard(mc);

				cards.add(i, mc);
			}

			this.session.setFirstCardsInHand(cards);
			this.session.setHandHasBeenCreated();

			this.hand = cards;
			return cards;
		}

		return new ArrayList<MagicCard>();
	}

	private ModalWindow generateAboutLink(final String id, final ModalWindow window)
	{
		window.setInitialWidth(450);
		window.setInitialHeight(675);
		window.setTitle("About HatchetHarry");
		window.setContent(new AboutModalWindow(window.getContentId(), window));
		window.setCssClassName(ModalWindow.CSS_CLASS_GRAY);
		window.setMaskType(ModalWindow.MaskType.SEMI_TRANSPARENT);
		this.add(window);

		final AjaxLink<Void> aboutLink = new AjaxLink<Void>(id)
		{
			private static final long serialVersionUID = 8140325977385015896L;

			@Override
			public void onClick(final AjaxRequestTarget target)
			{
				target.appendJavaScript(JavaScriptUtils.HIDE_MENUS);
				target.appendJavaScript("Wicket.Window.unloadConfirmation = false;");
				window.show(target);
			}
		};

		aboutLink.setOutputMarkupId(true);
		window.setOutputMarkupId(true);
		this.add(aboutLink);
		return window;
	}

	private ModalWindow generateTeamInfoLink(final String id, final ModalWindow window)
	{
		window.setInitialWidth(475);
		window.setInitialHeight(655);
		window.setTitle("HatchetHarry Team info");
		window.setContent(new TeamInfoModalWindow(window.getContentId(), window));
		window.setCssClassName(ModalWindow.CSS_CLASS_GRAY);
		window.setMaskType(ModalWindow.MaskType.SEMI_TRANSPARENT);
		this.add(window);

		final AjaxLink<Void> teamInfoLink = new AjaxLink<Void>(id)
		{
			private static final long serialVersionUID = 8140325977385015896L;

			@Override
			public void onClick(final AjaxRequestTarget target)
			{
				target.appendJavaScript(JavaScriptUtils.HIDE_MENUS);
				target.appendJavaScript("Wicket.Window.unloadConfirmation = false;");
				window.show(target);
			}
		};

		teamInfoLink.setOutputMarkupId(true);
		window.setOutputMarkupId(true);
		this.add(teamInfoLink);
		return window;
	}

	private ModalWindow generateCreateGameModalWindow(final String id, final Player _player,
			final WebMarkupContainer sidePlaceholderParent, final ModalWindow window)
	{
		window.setInitialWidth(475);
		window.setInitialHeight(290);
		window.setTitle("Create a game");

		window.setContent(new CreateGameModalWindow(window, window.getContentId(), _player,
				sidePlaceholderParent, this));
		window.setCssClassName(ModalWindow.CSS_CLASS_GRAY);
		window.setMaskType(ModalWindow.MaskType.SEMI_TRANSPARENT);

		this.createGameLink = new AjaxLink<Void>(id)
		{
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(final AjaxRequestTarget _target)
			{
				_target.appendJavaScript(JavaScriptUtils.HIDE_MENUS);
				_target.appendJavaScript("Wicket.Window.unloadConfirmation = false;");
				window.show(_target);
			}
		};

		this.createGameLink.setOutputMarkupId(true).setMarkupId(id);
		this.createGameWindow.setOutputMarkupId(true);

		this.add(this.createGameLink);

		return window;
	}

	private ModalWindow generateJoinGameModalWindow(final String id, final Player _player,
			final ModalWindow window)
	{
		window.setInitialWidth(475);
		window.setInitialHeight(290);
		window.setTitle("Join a game");

		window.setContent(new JoinGameModalWindow(window, window.getContentId(), _player,
				this.dataBoxParent, this));
		window.setCssClassName(ModalWindow.CSS_CLASS_GRAY);
		window.setMaskType(ModalWindow.MaskType.SEMI_TRANSPARENT);

		this.joinGameLink = new AjaxLink<Void>(id)
		{
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(final AjaxRequestTarget _target)
			{
				_target.appendJavaScript(JavaScriptUtils.HIDE_MENUS);
				_target.appendJavaScript("Wicket.Window.unloadConfirmation = false;");
				window.show(_target);
			}
		};

		this.joinGameLink.setOutputMarkupId(true).setMarkupId(id);
		window.setOutputMarkupId(true);

		this.add(this.joinGameLink);

		return window;
	}

	private void generateImportDeckLink(final String id, final ModalWindow window)
	{
		window.setInitialWidth(475);
		window.setInitialHeight(290);
		window.setTitle("Import a deck");

		window.setContent(new ImportDeckModalWindow(window.getContentId()));
		window.setCssClassName(ModalWindow.CSS_CLASS_GRAY);
		window.setMaskType(ModalWindow.MaskType.SEMI_TRANSPARENT);
		this.add(window);

		final AjaxLink<Void> importDeckLink = new AjaxLink<Void>(id)
		{
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(final AjaxRequestTarget target)
			{
				target.appendJavaScript(JavaScriptUtils.HIDE_MENUS);
				target.appendJavaScript("Wicket.Window.unloadConfirmation = false;");
				HomePage.this.importDeckWindow.show(target);
			}
		};

		importDeckLink.setOutputMarkupId(true);
		this.importDeckWindow.setOutputMarkupId(true);
		this.add(importDeckLink);
	}

	private void generateRevealTopLibraryCardLink(final String id, final String idModalWindow)
	{
		final ModalWindow window = new ModalWindow(idModalWindow);
		window.setWindowClosedCallback(new WindowClosedCallback()
		{
			private static final long serialVersionUID = 1L;

			@Override
			public void onClose(final AjaxRequestTarget target)
			{
				if (HomePage.this.session.getTopCardIndex().longValue() > 0l)
				{
					HomePage.this.session.setTopCardIndex(HomePage.this.session.getTopCardIndex()
							.longValue() - 1l);
				}

			}
		});
		window.setInitialWidth(500);
		window.setInitialHeight(510);

		final List<MagicCard> allCardsInLibrary = this.persistenceService
				.getAllCardsInLibraryForDeckAndPlayer(this.session.getGameId(), this.session
						.getPlayer().getId(), this.session.getPlayer().getDeck().getDeckId());
		final MagicCard firstCard = allCardsInLibrary
				.get(this.session.getTopCardIndex().intValue());
		window.setContent(new RevealTopLibraryCardModalWindow(window.getContentId(), window,
				firstCard));

		window.setCssClassName(ModalWindow.CSS_CLASS_GRAY);
		window.setMaskType(ModalWindow.MaskType.SEMI_TRANSPARENT);
		window.setOutputMarkupId(true);

		if (id.equals("revealTopLibraryCardLink"))
		{
			this.revealTopLibraryCardWindow = window;
			this.add(this.revealTopLibraryCardWindow);
		}
		else
		{
			this.revealTopLibraryCardWindowResponsive = window;
			this.add(this.revealTopLibraryCardWindowResponsive);
		}

		final AjaxLink<Void> revealTopLibraryCardLink = new AjaxLink<Void>(id)
		{
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(final AjaxRequestTarget target)
			{
				if (allCardsInLibrary.isEmpty())
				{
					return;
				}

				final List<MagicCard> _allCardsInLibrary = HomePage.this.persistenceService
						.getAllCardsInLibraryForDeckAndPlayer(HomePage.this.session.getGameId(),
								HomePage.this.session.getPlayer().getId(), HomePage.this.session
										.getPlayer().getDeck().getDeckId());
				final MagicCard _firstCard = _allCardsInLibrary.get(HomePage.this.session
						.getTopCardIndex().intValue());
				final String topCardName = _firstCard.getBigImageFilename();

				final String cardPath = ResourceBundle.getBundle(
						HatchetHarryApplication.class.getCanonicalName()).getString(
						"SharedResourceFolder");
				final String cardPathAndName = cardPath.replace("/cards", "") + topCardName;
				final File from = new File(cardPathAndName);
				final File to = new File(cardPath + "topLibraryCard.jpg");

				try
				{
					Files.copy(from, to);
				}
				catch (final IOException e)
				{
					HomePage.LOGGER.error("could not copy from: " + cardPathAndName + " to: "
							+ cardPath + "topLibraryCard.jpg", e);
				}

				final Long gameId = HomePage.this.persistenceService
						.getPlayer(HomePage.this.session.getPlayer().getId()).getGame().getId();
				final RevealTopLibraryCardCometChannel chan = new RevealTopLibraryCardCometChannel(
						HomePage.this.session.getPlayer().getName(), _firstCard,
						HomePage.this.session.getTopCardIndex());
				final ConsoleLogStrategy logger = AbstractConsoleLogStrategy.chooseStrategy(
						ConsoleLogType.REVEAL_TOP_CARD_OF_LIBRARY, null, null, null,
						_firstCard.getTitle(), HomePage.this.session.getPlayer().getName(), null,
						HomePage.this.session.getTopCardIndex() + 1l, null, false,
						HomePage.this.session.getGameId());
				final List<BigInteger> allPlayersInGame = HomePage.this.persistenceService
						.giveAllPlayersFromGame(gameId);

				for (int i = 0; i < allPlayersInGame.size(); i++)
				{
					final Long playerToWhomToSend = allPlayersInGame.get(i).longValue();
					final String pageUuid = HatchetHarryApplication.getCometResources().get(
							playerToWhomToSend);

					HatchetHarryApplication.get().getEventBus().post(chan, pageUuid);
					HatchetHarryApplication.get().getEventBus()
							.post(new ConsoleLogCometChannel(logger), pageUuid);
				}
			}
		};

		revealTopLibraryCardLink.setOutputMarkupId(true).setMarkupId(id);
		this.add(revealTopLibraryCardLink);
	}

	private void generateCreateTokenLink(final String id, final ModalWindow window)
	{
		window.setInitialWidth(500);
		window.setInitialHeight(510);

		window.setContent(new CreateTokenModalWindow(window.getContentId(), window));
		window.setCssClassName(ModalWindow.CSS_CLASS_GRAY);
		window.setMaskType(ModalWindow.MaskType.SEMI_TRANSPARENT);
		window.setTitle("Create a token");
		window.setOutputMarkupId(true);
		this.add(window);

		final AjaxLink<Void> createTokenLink = new AjaxLink<Void>(id)
		{
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(final AjaxRequestTarget target)
			{
				target.appendJavaScript(JavaScriptUtils.HIDE_MENUS);
				target.appendJavaScript("Wicket.Window.unloadConfirmation = false;");

				HomePage.this.createTokenWindow.show(target);
			}
		};

		createTokenLink.setOutputMarkupId(true).setMarkupId(id);
		this.add(createTokenLink);
	}

	private void generateCountCardsLink(final String id, final ModalWindow window)
	{
		window.setInitialWidth(740);
		window.setInitialHeight(550);

		window.setContent(new CountCardsModalWindow(window.getContentId(), this.session.getGameId()));
		window.setCssClassName(ModalWindow.CSS_CLASS_GRAY);
		window.setMaskType(ModalWindow.MaskType.SEMI_TRANSPARENT);
		window.setOutputMarkupId(true);
		this.add(window);

		final AjaxLink<Void> countCardsLink = new AjaxLink<Void>(id)
		{
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(final AjaxRequestTarget target)
			{
				target.appendJavaScript(JavaScriptUtils.HIDE_MENUS);

				final Long gameId = HomePage.this.persistenceService
						.getPlayer(HomePage.this.session.getPlayer().getId()).getGame().getId();

				final List<BigInteger> allPlayersInGame = HomePage.this.persistenceService
						.giveAllPlayersFromGame(gameId);

				for (int i = 0; i < allPlayersInGame.size(); i++)
				{
					final Long playerToWhomToSend = allPlayersInGame.get(i).longValue();
					final String pageUuid = HatchetHarryApplication.getCometResources().get(
							playerToWhomToSend);
					final CountCardsCometChannel cccc = new CountCardsCometChannel(gameId,
							HomePage.this.session.getPlayer().getName());

					// For unit tests: for Christ sake, Emond, do something for
					// us!
					try
					{
						HatchetHarryApplication.get().getEventBus().post(cccc, pageUuid);
					}
					catch (final NullPointerException e)
					{
						target.appendJavaScript(JavaScriptUtils.HIDE_MENUS);
						target.appendJavaScript("Wicket.Window.unloadConfirmation = false;");

						HomePage.this.countCardsWindow.setTitle(cccc.getRequestingPlayerName()
								+ " asks the number of cards by zone for each player of game #"
								+ cccc.getGameId() + ": ");
						HomePage.this.countCardsWindow.setContent(new CountCardsModalWindow(
								HomePage.this.countCardsWindow.getContentId(), cccc.getGameId()));

						HomePage.this.countCardsWindow.show(target);
					}
				}
			}
		};

		countCardsLink.setOutputMarkupId(true);
		this.add(countCardsLink);
	}

	private void generateLoginLink(final String id, final ModalWindow window)
	{
		window.setInitialWidth(300);
		window.setInitialHeight(200);
		window.setTitle("HatchetHarry login");
		window.setContent(new LoginModalWindow(window.getContentId(), this.session.getGameId(),
				window));
		window.setCssClassName(ModalWindow.CSS_CLASS_GRAY);
		window.setMaskType(ModalWindow.MaskType.SEMI_TRANSPARENT);
		window.setOutputMarkupId(true);
		window.setCloseButtonCallback(new ModalWindow.CloseButtonCallback()
		{
			private static final long serialVersionUID = 1L;

			@Override
			public boolean onCloseButtonClicked(final AjaxRequestTarget target)
			{
				target.appendJavaScript("authenticateUserWithFacebook();");
				return true;
			}
		});
		this.add(window);

		final AjaxLink<Void> loginLink = new AjaxLink<Void>(id)
		{
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(final AjaxRequestTarget target)
			{
				target.appendJavaScript(JavaScriptUtils.HIDE_MENUS);
				target.appendJavaScript("Wicket.Window.unloadConfirmation = false;");
				HomePage.this.loginWindow.show(target);
			}
		};

		loginLink.setOutputMarkupId(true);
		this.add(loginLink);
	}

	private void generatePreferencesLink(final String id, final ModalWindow window)
	{
		window.setInitialWidth(630);
		window.setInitialHeight(300);
		window.setTitle("User preferences");
		window.setCssClassName(ModalWindow.CSS_CLASS_GRAY);
		window.setMaskType(ModalWindow.MaskType.SEMI_TRANSPARENT);
		window.setOutputMarkupId(true);
		this.add(window);

		final AjaxLink<Void> preferencesLink = new AjaxLink<Void>(id)
		{
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(final AjaxRequestTarget target)
			{
				window.setContent(new UserPreferencesModalWindow(window.getContentId(),
						HomePage.this.session.getGameId(), window));
				target.appendJavaScript(JavaScriptUtils.HIDE_MENUS);
				target.appendJavaScript("Wicket.Window.unloadConfirmation = false;");
				HomePage.this.preferencesWindow.show(target);
			}
		};

		preferencesLink.setOutputMarkupId(true);
		this.add(preferencesLink);
	}

	private void generateInsertDivisionLink(final String id)
	{
		final AjaxLink<Void> insertDivisionLink = new AjaxLink<Void>(id)
		{
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(final AjaxRequestTarget target)
			{
				final Long gameId = HomePage.this.session.getGameId();
				final List<BigInteger> allPlayersInGame = HomePage.this.persistenceService
						.giveAllPlayersFromGame(gameId);

				final ConsoleLogStrategy logger = AbstractConsoleLogStrategy
						.chooseStrategy(ConsoleLogType.INSERT_DIVISION, null, null, null, null,
								HomePage.this.session.getPlayer().getName(), null, null, null,
								null, gameId);

				for (int i = 0; i < allPlayersInGame.size(); i++)
				{
					final Long playerToWhomToSend = allPlayersInGame.get(i).longValue();
					final String pageUuid = HatchetHarryApplication.getCometResources().get(
							playerToWhomToSend);
					HatchetHarryApplication.get().getEventBus()
							.post(new ConsoleLogCometChannel(logger), pageUuid);
				}
			}
		};

		insertDivisionLink.setOutputMarkupId(true).setMarkupId(id);
		this.add(insertDivisionLink);
	}

	private void generateShuffleLibraryLink(final String id)
	{
		final AjaxLink<Void> insertDivisionLink = new AjaxLink<Void>(id)
		{
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(final AjaxRequestTarget target)
			{
				final Long gameId = HomePage.this.session.getGameId();
				final List<BigInteger> allPlayersInGame = HomePage.this.persistenceService
						.giveAllPlayersFromGame(gameId);

				final ConsoleLogStrategy logger = AbstractConsoleLogStrategy
						.chooseStrategy(ConsoleLogType.SHUFFLE_LIBRARY, null, null, null, null,
								HomePage.this.session.getPlayer().getName(), null, null, null,
								null, gameId);

				final Player me = HomePage.this.session.getPlayer();
				final NotifierCometChannel ncc = new NotifierCometChannel(
						NotifierAction.SHUFFLE_LIBRARY_ACTION, null, me.getId(), me.getName(), me
								.getSide().getSideName(), null, null, null, "");

				final List<MagicCard> allCardsInLibrary = HomePage.this.persistenceService
						.getAllCardsInLibraryForDeckAndPlayer(HomePage.this.session.getGameId(),
								HomePage.this.session.getPlayer().getId(), HomePage.this.session
										.getPlayer().getDeck().getDeckId());
				Collections.shuffle(allCardsInLibrary);
				Collections.shuffle(allCardsInLibrary);
				Collections.shuffle(allCardsInLibrary);
				for (int i = 0; i < allCardsInLibrary.size(); i++)
				{
					allCardsInLibrary.get(i).setZoneOrder(Long.valueOf(i));
				}

				HomePage.this.persistenceService.updateAllMagicCards(allCardsInLibrary);

				for (int i = 0; i < allPlayersInGame.size(); i++)
				{
					final Long playerToWhomToSend = allPlayersInGame.get(i).longValue();
					final String pageUuid = HatchetHarryApplication.getCometResources().get(
							playerToWhomToSend);
					HatchetHarryApplication.get().getEventBus()
							.post(new ConsoleLogCometChannel(logger), pageUuid);
					HatchetHarryApplication.get().getEventBus().post(ncc, pageUuid);
				}
			}
		};

		insertDivisionLink.setOutputMarkupId(true).setMarkupId(id);
		this.add(insertDivisionLink);
	}

	@Subscribe
	public void updateTime(final AjaxRequestTarget target, final Date event)
	{
		target.prependJavaScript("if (document.activeElement.tagName !== 'INPUT') { var chatPos = document.getElementById('chat').scrollTop; document.getElementById('clockLabel').innerHTML = '"
				+ event.toString() + "'; document.getElementById('chat').scrollTop = chatPos; }");
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
				HomePage.this.session.setPlayerEndingHerTurn(event.getPlayerName());
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

			case PUT_CARD_TO_GRAVGEYARD_FROM_BATTLEFIELD_ACTION :
				target.appendJavaScript("jQuery.gritter.add({ title : '"
						+ event.getPlayerName()
						+ "', text : \"has put '"
						+ event.getCardName()
						+ "' to "
						+ (event.getTargetPlayerName().equals(event.getPlayerName())
								? "his (her)"
								: event.getTargetPlayerName() + "'s")
						+ " graveyard\", image : 'image/logoh2.gif', sticky : false, time : ''});");
				break;

			case PUT_CARD_TO_HAND_FROM_BATTLEFIELD_ACTION :
				target.appendJavaScript("jQuery.gritter.add({ title : '"
						+ event.getPlayerName()
						+ "', text : \"has put '"
						+ event.getCardName()
						+ "' to "
						+ (event.getTargetPlayerName().equals(event.getPlayerName())
								? "his (her)"
								: event.getTargetPlayerName() + "'s")
						+ " hand from the battlefield\", image : 'image/logoh2.gif', sticky : false, time : ''});");
				break;

			case PUT_CARD_TO_EXILE_FROM_BATTLEFIELD_ACTION :
				target.appendJavaScript("jQuery.gritter.add({ title : '"
						+ event.getPlayerName()
						+ "', text : \"has put '"
						+ event.getCardName()
						+ "' to "
						+ (event.getTargetPlayerName().equals(event.getPlayerName())
								? "his (her)"
								: event.getTargetPlayerName() + "'s")
						+ " exile from the battlefield\", image : 'image/logoh2.gif', sticky : false, time : ''});");
				break;

			case COMBAT_IN_PROGRESS_ACTION :
				if ((null != event.isCombatInProgress())
						&& (event.isCombatInProgress().booleanValue() == false))
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

			case PLAY_TOP_LIBRARY_CARD_ACTION :
				target.appendJavaScript("jQuery.gritter.add({ title : '"
						+ event.getPlayerName()
						+ "', text : \"has played the top card of "
						+ (event.getPlayerName().equals(event.getTargetPlayerName())
								? "his (her) "
								: event.getTargetPlayerName() + "'s ") + "library, which is: "
						+ event.getCardName()
						+ "\", image : 'image/logoh2.gif', sticky : false, time : ''});");
				break;
			case PUT_TOP_LIBRARY_CARD_TO_HAND_ACTION :
				target.appendJavaScript("jQuery.gritter.add({ title : '"
						+ event.getPlayerName()
						+ "', text : \"has put the top card of "
						+ (event.getPlayerName().equals(event.getTargetPlayerName())
								? "his (her) "
								: event.getTargetPlayerName() + "'s ")
						+ "library in "
						+ (event.getPlayerName().equals(event.getTargetPlayerName())
								? "his (her) "
								: event.getTargetPlayerName() + "'s ") + "hand, and it is: "
						+ event.getCardName()
						+ "\", image : 'image/logoh2.gif', sticky : false, time : ''});");
				break;
			case PUT_TOP_LIBRARY_CARD_TO_GRAVEYARD_ACTION :
				target.appendJavaScript("jQuery.gritter.add({ title : '"
						+ event.getPlayerName()
						+ "', text : \"has put the top card of "
						+ (event.getPlayerName().equals(event.getTargetPlayerName())
								? "his (her) "
								: event.getTargetPlayerName() + "'s ")
						+ "library in "
						+ (event.getPlayerName().equals(event.getTargetPlayerName())
								? "his (her) "
								: event.getTargetPlayerName() + "'s ") + "graveyard, and it is: "
						+ event.getCardName()
						+ "\", image : 'image/logoh2.gif', sticky : false, time : ''});");
				break;
			case PUT_TOKEN_ON_BATTLEFIELD_ACTION :
				target.appendJavaScript("jQuery.gritter.add({ title : '" + event.getPlayerName()
						+ "', text : \"has put a " + event.getCardName()
						+ " token on the battlefield"
						+ "\", image : 'image/logoh2.gif', sticky : false, time : ''});");
				break;
			case DESTROY_TOKEN_ACTION :
				target.appendJavaScript("jQuery.gritter.add({ title : '" + event.getPlayerName()
						+ "', text : \"has destroyed a " + event.getCardName() + " token"
						+ "\", image : 'image/logoh2.gif', sticky : false, time : ''});");
				break;

			case SHUFFLE_LIBRARY_ACTION :
				target.appendJavaScript("jQuery.gritter.add({ title : '"
						+ event.getPlayerName()
						+ "', text : 'has shuffled his (her) library', image : 'image/logoh2.gif', sticky : false, time : ''});");
				break;

			case END_GAME_ACTION :
				target.appendJavaScript("jQuery.gritter.add({ title : '"
						+ event.getPlayerName()
						+ "', text : 'has put an end to the game', image : 'image/logoh2.gif', sticky : false, time : '', class_name: 'gritter-light'});");
				break;

			case IN_RESPONSE_ACTION :
				target.appendJavaScript("jQuery.gritter.add({ title : '"
						+ event.getPlayerName()
						+ "', text : 'has an action to play in response', image : 'image/logoh2.gif', sticky : false, time : '', class_name: 'gritter-light'});");
				break;

			case FINE_FOR_ME_ACTION :
				target.appendJavaScript("jQuery.gritter.add({ title : '"
						+ event.getPlayerName()
						+ "', text : 'said : \"Fine for me!\"', image : 'image/logoh2.gif', sticky : false, time : ''});");
				break;

			case REVEAL_HAND :
				target.appendJavaScript("jQuery.gritter.add({ title : '"
						+ event.getPlayerName()
						+ "', text : 'reveals his (her) hand', image : 'image/logoh2.gif', sticky : false, time : ''});");
				break;

			// TODO: split this notifier action and the one of
			// card counters
			default :
				throw new IllegalArgumentException(
						"can not treat this case in HomePage#displayNotification()");
		}
	}

	@Subscribe
	public void untapAll(final AjaxRequestTarget target, final UntapAllCometChannel event)
	{
		final StringBuilder buil = new StringBuilder();

		for (int i = 0; i < event.getCardsToUntap().size(); i++)
		{
			final MagicCard mc = event.getCardsToUntap().get(i);
			buil.append("jQuery('#card" + mc.getUuid().replace("-", "_") + "').rotate(0); ");
			mc.setTapped(false);
			this.persistenceService.updateCard(mc);
		}
		target.appendJavaScript(buil.toString());
	}

	/**
	 * @param event
	 *            not used, since Comet channels are managed by
	 *            JoinGameModalWindow
	 */
	@Subscribe
	public void displayJoinGameMessage(final AjaxRequestTarget target,
			final JoinGameNotificationCometChannel event)
	{
		target.appendJavaScript("jQuery.gritter.add({ title : 'A player joined in!', text : 'Ready to play?', image : 'image/logoh2.gif', sticky : false, time : ''});");
	}

	@Subscribe
	public void updateDataBox(final AjaxRequestTarget target, final UpdateDataBoxCometChannel event)
	{
		final DataBox db = new DataBox("dataBox", event.getGameId());
		this.getDataBoxParent().addOrReplace(db);
		db.setOutputMarkupId(true);
		target.add(this.getDataBoxParent());
	}

	@Subscribe
	public void updateCardTooltip(final AjaxRequestTarget target,
			final UpdateCardPanelCometChannel event)
	{
		final MagicCard mc = event.getMagicCard();

		for (int i = 0; i < this.getAllTooltips().size(); i++)
		{
			final MagicCard targetCard = this.getAllTooltips().getItem(i).getModelObject();

			if ((targetCard != null) && (mc != null) && (targetCard.getUuid().equals(mc.getUuid())))
			{
				this.getAllTooltipsInBattlefield().remove(
						this.getAllTooltips().getItem(i).getModelObject());
				this.getAllTooltips().remove(this.getAllTooltips().getItem(i));
				this.getAllTooltipsInBattlefield().add(mc);
				this.getAllTooltips().addNewItems(mc);
				break;
			}
		}

		target.appendJavaScript(JavaScriptUtils.HIDE_ALL_TOOLTIPS);

		switch (event.getAction())
		{
			case ADD_COUNTER_ACTION :
				target.appendJavaScript("jQuery.gritter.add({ title : '"
						+ event.getRequestingPlayerName() + "', text : \"has put "
						+ event.getTargetNumberOfCounters() + " " + event.getCounterName()
						+ " counter(s) on " + event.getTargetPlayerName() + "'s card: "
						+ event.getCardName()
						+ "\" , image : 'image/logoh2.gif', sticky : false, time : ''});");
				break;
			case REMOVE_COUNTER_ACTION :
				target.appendJavaScript("jQuery.gritter.add({ title : '"
						+ event.getRequestingPlayerName() + "', text : \"has put "
						+ event.getTargetNumberOfCounters() + " " + event.getCounterName()
						+ " counter(s) on " + event.getTargetPlayerName() + "'s card: "
						+ event.getCardName()
						+ "\" , image : 'image/logoh2.gif', sticky : false, time : ''});");
				break;
			case CLEAR_COUNTER_ACTION :
				target.appendJavaScript("jQuery.gritter.add({ title : '"
						+ event.getRequestingPlayerName() + "', text : \"has cleared the "
						+ event.getCounterName() + " counter(s) on " + event.getTargetPlayerName()
						+ "'s card: " + event.getCardName()
						+ "\" , image : 'image/logoh2.gif', sticky : false, time : ''});");
				break;
			case SET_COUNTER_ACTION :
				target.appendJavaScript("jQuery.gritter.add({ title : '"
						+ event.getRequestingPlayerName()
						+ "', text : \"has removed "
						+ event.getOriginalNumberOfCounters()
						+ " "
						+ event.getCounterName()
						+ " counter(s) on "
						+ event.getTargetPlayerName()
						+ "'s card: "
						+ event.getCardName()
						+ " and replaced them with "
						+ event.getTargetNumberOfCounters()
						+ " counter(s)\" , image : 'image/logoh2.gif', sticky : false, time : ''});");
				break;
			// $CASES-OMITTED$
			// TODO: split this card counters notifier action
			// and the one of general messages
			// $CASES-OMITTED$
			default :
				throw new IllegalArgumentException(
						"can not treat this case in HomePage#updateCardTooltip(): "
								+ event.getAction());
		}
	}

	@Subscribe
	public void updateTokenTooltip(final AjaxRequestTarget target,
			final UpdateTokenPanelCometChannel event)
	{
		final Token token = event.getToken();

		for (int i = 0; i < this.getAllTooltips().size(); i++)
		{
			final MagicCard targetCard = this.getAllTooltips().getItem(i).getModelObject();

			if (((targetCard.getToken()) != null)
					&& (token.getUuid().equals(targetCard.getToken().getUuid())))
			{
				this.getAllTooltipsInBattlefield().remove(targetCard);
				this.getAllTooltips().remove(this.getAllTooltips().getItem(i));
				this.getAllTooltipsInBattlefield().add(targetCard);
				this.getAllTooltips().addNewItems(targetCard);
				break;
			}
		}

		target.appendJavaScript(JavaScriptUtils.HIDE_ALL_TOOLTIPS);

		switch (event.getAction())
		{
			case ADD_COUNTER_ACTION :
				target.appendJavaScript("jQuery.gritter.add({ title : '"
						+ event.getRequestingPlayerName() + "', text : \"has put "
						+ event.getTargetNumberOfCounters() + " " + event.getCounterName()
						+ " counter(s) on " + event.getTargetPlayerName() + "'s token: "
						+ token.getCreatureTypes()
						+ "\" , image : 'image/logoh2.gif', sticky : false, time : ''});");
				break;
			case REMOVE_COUNTER_ACTION :
				target.appendJavaScript("jQuery.gritter.add({ title : '"
						+ event.getRequestingPlayerName() + "', text : \"has put "
						+ event.getTargetNumberOfCounters() + " " + event.getCounterName()
						+ " counter(s) on " + event.getTargetPlayerName() + "'s token: "
						+ token.getCreatureTypes()
						+ "\" , image : 'image/logoh2.gif', sticky : false, time : ''});");
				break;
			case CLEAR_COUNTER_ACTION :
				target.appendJavaScript("jQuery.gritter.add({ title : '"
						+ event.getRequestingPlayerName() + "', text : \"has cleared the "
						+ event.getCounterName() + " counter(s) on " + event.getTargetPlayerName()
						+ "'s " + token.getCreatureTypes() + " token"
						+ "\" , image : 'image/logoh2.gif', sticky : false, time : ''});");
				break;
			case SET_COUNTER_ACTION :
				target.appendJavaScript("jQuery.gritter.add({ title : '"
						+ event.getRequestingPlayerName()
						+ "', text : \"has removed "
						+ event.getOriginalNumberOfCounters()
						+ " "
						+ event.getCounterName()
						+ " counter(s) on "
						+ event.getTargetPlayerName()
						+ "'s "
						+ token.getCreatureTypes()
						+ " token "
						+ " and replaced them with "
						+ event.getTargetNumberOfCounters()
						+ " counter(s)\" , image : 'image/logoh2.gif', sticky : false, time : ''});");
				break;
			// $CASES-OMITTED$
			// TODO: split this card counters notifier action
			// and the one of general messages
			// $CASES-OMITTED$
			default :
				throw new IllegalArgumentException(
						"can not treat this case in HomePage#updateTokenTooltip(): "
								+ event.getAction());
		}
	}

	@Subscribe
	public void removeCardFromBattlefield(final AjaxRequestTarget target,
			final PutToGraveyardCometChannel event)
	{
		if (event.isShouldUpdateGraveyard())
		{
			JavaScriptUtils.updateGraveyard(target, event.getGameId(), event.getTargetPlayerId(),
					event.getDeckId());
		}
		JavaScriptUtils.updateCardsAndRestoreStateInBattlefield(target, this.persistenceService,
				event.getGameId(), event.getMagicCard(), false);
	}

	@Subscribe
	public void exileCardFromBattlefield(final AjaxRequestTarget target,
			final PutToExileFromBattlefieldCometChannel event)
	{
		if (event.isShouldUpdateExile())
		{
			JavaScriptUtils.updateExile(target, event.getGameId(), event.getTargetPlayerId(),
					event.getDeckId());
		}

		JavaScriptUtils.updateCardsAndRestoreStateInBattlefield(target, this.persistenceService,
				event.getGameId(), event.getMc(), false);
	}

	@Subscribe
	public void moveCard(final AjaxRequestTarget target, final CardMoveCometChannel event)
	{
		final MagicCard mc = event.getMc();

		mc.setX(Long.parseLong(event.getMouseX()));
		mc.setY(Long.parseLong(event.getMouseY()));

		target.appendJavaScript("var card = jQuery('#cardHandle"
				+ event.getUniqueid().replace("-", "_") + "');"
				+ "card.css('position', 'absolute'); card.css('left', '" + event.getMouseX()
				+ "px'); card.css('top', '" + event.getMouseY() + "px');");

		final Boolean drawMode = this.persistenceService.getGame(event.getGameId()).isDrawMode();

		if ((drawMode != null) && drawMode.booleanValue())
		{
			target.appendJavaScript("jQuery('._jsPlumb_connector').remove(); jQuery('._jsPlumb_overlay').remove(); jQuery('._jsPlumb_endpoint').remove(); "
					+ "for (var index = 0; index < arrows.length; index++) { "
					+ "var e0 = jsPlumb.addEndpoint(arrows[index]['source']); "
					+ "var e1 = jsPlumb.addEndpoint(arrows[index]['target']); "
					+ "jsPlumb.connect({ source:e0, target:e1, connector:['Bezier', { curviness:70 }], overlays : [ "
					+ "					['Label', {location:0.7, id:'label', events:{ } }], ['Arrow', { "
					+ "						cssClass:'l1arrow',  location:0.5, width:40,length:40 }]]}); }; ");

			target.appendJavaScript("var plumbSource, plumbTarget; "
					+ "jQuery('.clickableCard').unbind('click'); "
					+ "jQuery('.clickableCard').click(function (event) { "
					+ "if (cardAlreadySelected) { "
					+ "	cardAlreadySelected = false; "
					+ "	plumbTarget = jQuery('#' + event.target.id).parent().parent().parent().parent().attr('id'); "
					+ " Wicket.Ajax.get({ 'u' : jQuery('#' + plumbTarget).data('arrowDrawUrl') + '&source=' + plumbSource + '&target=' + plumbTarget}); "
					+ "} else { "
					+ "	cardAlreadySelected = true; "
					+ "	plumbSource = jQuery('#' + event.target.id).parent().parent().parent().parent().attr('id'); "
					+ "}}); ");
		}
	}

	@Subscribe
	public void rotateCard(final AjaxRequestTarget target, final CardRotateCometChannel event)
	{
		final MagicCard mc = event.getMc();
		mc.setTapped(event.isTapped());

		final StringBuilder buil = new StringBuilder();

		if (event.isTapped())
		{
			buil.append("window.setTimeout(function() { jQuery('#card"
					+ event.getCardUuid().replace("-", "_")
					+ "').rotate(90); window.setTimeout(function() {");
			buil.append("jQuery('#card" + event.getCardUuid().replace("-", "_")
					+ "').rotate(0); window.setTimeout(function() {");
			buil.append("jQuery('#card" + event.getCardUuid().replace("-", "_")
					+ "').rotate(90); }, 500); }, 500); }, 500);");
		}
		else
		{
			buil.append("window.setTimeout(function() {jQuery('#card"
					+ event.getCardUuid().replace("-", "_")
					+ "').rotate(0); window.setTimeout(function() {");
			buil.append("jQuery('#card" + event.getCardUuid().replace("-", "_")
					+ "').rotate(90); window.setTimeout(function() {");
			buil.append("jQuery('#card" + event.getCardUuid().replace("-", "_")
					+ "').rotate(0); }, 500); }, 500); }, 500);");
		}

		target.appendJavaScript(buil.toString());
	}

	@Subscribe
	public void putToHandFromBattlefield(final AjaxRequestTarget target,
			final PutToHandFromBattlefieldCometChannel event)
	{
		if (event.isShouldUpdateHand())
		{
			JavaScriptUtils.updateHand(target, event.getGameId(), event.getTargetPlayerId(),
					event.getDeckId());
		}
		JavaScriptUtils.updateCardsAndRestoreStateInBattlefield(target, this.persistenceService,
				event.getGameId(), event.getMc(), false);
	}

	@Subscribe
	public void playCardFromHand(final AjaxRequestTarget target,
			final PlayCardFromHandCometChannel event)
	{
		final MagicCard mc = event.getMagicCard();

		JavaScriptUtils.updateCardsAndRestoreStateInBattlefield(target, this.persistenceService,
				event.getGameId(), mc, true);
	}

	@Subscribe
	public void playTopLibraryCard(final AjaxRequestTarget target,
			final PlayTopLibraryCardCometChannel event)
	{
		final MagicCard mc = event.getCard();

		JavaScriptUtils.updateCardsAndRestoreStateInBattlefield(target, this.persistenceService,
				event.getGameId(), mc, true);
	}

	@Subscribe
	public void putTokenOnBattlefield(final AjaxRequestTarget target,
			final PutTokenOnBattlefieldCometChannel event)
	{
		final MagicCard mc = event.getMagicCard();
		mc.setX(event.getSide().getX());
		mc.setY(event.getSide().getY());

		JavaScriptUtils.updateCardsAndRestoreStateInBattlefield(target, this.persistenceService,
				event.getGameId(), mc, true);
	}

	@Subscribe
	public void putTopLibraryCardToHand(final AjaxRequestTarget target,
			final PutTopLibraryCardToHandCometChannel event)
	{
		if (event.getPlayerId().longValue() == HatchetHarrySession.get().getPlayer().getId()
				.longValue())
		{
			final Player p = this.persistenceService.getPlayer(event.getPlayerId());
			p.setHandDisplayed(true);
			this.persistenceService.updatePlayer(p);
			JavaScriptUtils.updateHand(target, event.getGameId(), event.getPlayerId(),
					event.getDeckId());
		}
	}

	@Subscribe
	public void putTopLibraryCardToGraveyard(final AjaxRequestTarget target,
			final PutTopLibraryCardToGraveyardCometChannel event)
	{
		if (event.getPlayerId().longValue() == HatchetHarrySession.get().getPlayer().getId()
				.longValue())
		{
			final Player p = this.persistenceService.getPlayer(event.getPlayerId());
			p.setGraveyardDisplayed(true);
			this.persistenceService.updatePlayer(p);
			JavaScriptUtils.updateGraveyard(target, event.getGameId(), event.getPlayerId(),
					event.getDeckId());
		}
	}

	@Subscribe
	public void playCardFromGraveyard(final AjaxRequestTarget target,
			final PlayCardFromGraveyardCometChannel event)
	{
		final MagicCard mc = event.getMagicCard();
		JavaScriptUtils.updateCardsAndRestoreStateInBattlefield(target, this.persistenceService,
				event.getGameId(), mc, true);
	}

	@Subscribe
	public void revealTopLibraryCard(final AjaxRequestTarget target,
			final RevealTopLibraryCardCometChannel event)
	{
		target.appendJavaScript(JavaScriptUtils.HIDE_MENUS);
		target.appendJavaScript("Wicket.Window.unloadConfirmation = false;");

		this.revealTopLibraryCardWindow.setTitle("This is the top card #"
				+ (event.getIndex().longValue() + 1l) + " of " + event.getPlayerName()
				+ "'s library: ");
		this.revealTopLibraryCardWindow.setContent(new RevealTopLibraryCardModalWindow(
				this.revealTopLibraryCardWindow.getContentId(), this.revealTopLibraryCardWindow,
				event.getCard()));

		this.allOpenRevealTopLibraryCardWindows.add(this.revealTopLibraryCardWindow);
		this.revealTopLibraryCardWindow.show(target);
	}

	@Subscribe
	public void countCards(final AjaxRequestTarget target, final CountCardsCometChannel event)
	{
		target.appendJavaScript(JavaScriptUtils.HIDE_MENUS);
		target.appendJavaScript("Wicket.Window.unloadConfirmation = false;");

		this.countCardsWindow.setTitle(event.getRequestingPlayerName()
				+ " asks the number of cards by zone for each player of game #" + event.getGameId()
				+ ": ");
		this.countCardsWindow.setContent(new CountCardsModalWindow(this.countCardsWindow
				.getContentId(), event.getGameId()));

		this.countCardsWindow.show(target);
	}

	@SuppressWarnings("incomplete-switch")
	@Subscribe
	public void cardZoneChange(final AjaxRequestTarget target, final CardZoneMoveCometChannel event)
	{
		final MagicCard mc = event.getCard();
		final Deck d = event.getDeck();
		final boolean isRequestingPlayerSameThanTargetedPlayer = event.getPlayerId().longValue() == HatchetHarrySession
				.get().getPlayer().getId().longValue();

		if (!event.getTargetZone().equals(CardZone.LIBRARY))
		{
			mc.setZone(event.getTargetZone());
			this.persistenceService.updateCard(mc);
		}
		else
		{
			return;
		}


		boolean hasAlreadyDisplayedHand = false;
		boolean hasAlreadyDisplayedGraveyard = false;
		boolean hasAlreadyDisplayedExile = false;

		// TODO: other cases
		switch (event.getTargetZone())
		{
			case BATTLEFIELD :
				mc.setX(event.getSide().getX());
				mc.setY(event.getSide().getY());
				this.persistenceService.updateCard(mc);
				JavaScriptUtils.updateCardsAndRestoreStateInBattlefield(target,
						this.persistenceService, event.getGameId(), mc, true);
				break;
			case HAND :
				if (isRequestingPlayerSameThanTargetedPlayer)
				{
					JavaScriptUtils.updateHand(target, event.getGameId(), event.getPlayerId(),
							d.getDeckId());
					hasAlreadyDisplayedHand = true;
				}
				break;
			case GRAVEYARD :
				if (isRequestingPlayerSameThanTargetedPlayer)
				{
					JavaScriptUtils.updateGraveyard(target, event.getGameId(), event.getPlayerId(),
							d.getDeckId());
					hasAlreadyDisplayedGraveyard = true;
				}
				break;
			case EXILE :
				if (isRequestingPlayerSameThanTargetedPlayer)
				{
					JavaScriptUtils.updateExile(target, event.getGameId(), event.getPlayerId(),
							d.getDeckId());
					hasAlreadyDisplayedExile = true;
				}
				break;
			case LIBRARY :
				break;
			default :
				throw new UnsupportedOperationException();
		}

		// TODO: other cases
		switch (event.getSourceZone())
		{
			case HAND :
				if (isRequestingPlayerSameThanTargetedPlayer && !hasAlreadyDisplayedHand)
				{
					JavaScriptUtils.updateHand(target, event.getGameId(), event.getPlayerId(),
							d.getDeckId());
				}
				break;
			case GRAVEYARD :
				if (isRequestingPlayerSameThanTargetedPlayer && !hasAlreadyDisplayedGraveyard)
				{
					JavaScriptUtils.updateGraveyard(target, event.getGameId(), event.getPlayerId(),
							d.getDeckId());
				}
				break;
			case EXILE :
				if (isRequestingPlayerSameThanTargetedPlayer && !hasAlreadyDisplayedExile)
				{
					JavaScriptUtils.updateExile(target, event.getGameId(), event.getPlayerId(),
							d.getDeckId());
				}
				break;
			default :
				throw new UnsupportedOperationException();
		}
	}

	@Subscribe
	public void cardZoneChangeNotify(final AjaxRequestTarget target,
			final CardZoneMoveNotifier event)
	{
		if (event.getTargetZone().equals(CardZone.LIBRARY))
		{
			return;
		}
		target.appendJavaScript("jQuery.gritter.add({ title : '"
				+ event.getRequestingPlayer()
				+ "', text : \"has moved "
				+ (event.getOwnerPlayer().equals(event.getRequestingPlayer()) ? "his (her)" : event
						.getOwnerPlayer() + "'s") + " card: " + event.getCard().getTitle()
				+ " from " + event.getSourceZone() + " to " + event.getTargetZone()
				+ "\", image : 'image/logoh2.gif', sticky : false, time : ''});");
	}

	@Subscribe
	public void destroyToken(final AjaxRequestTarget target, final DestroyTokenCometChannel event)
	{
		JavaScriptUtils.updateCardsAndRestoreStateInBattlefield(target, this.persistenceService,
				event.getGameId(), event.getCard(), false);
	}

	@Subscribe
	public void addSide(final AjaxRequestTarget target, final AddSideCometChannel event)
	{
		HomePage.LOGGER.info("addSide");
		this.allPlayerSidesInGame.add(event.getPlayer());
		this.allSidesInGame.addNewItems(event.getPlayer());
	}

	@Subscribe
	public void addSideFromOtherBrowsers(final AjaxRequestTarget target,
			final AddSidesFromOtherBrowsersCometChannel event)
	{
		HomePage.LOGGER.info("addSideFromOtherBrowsers");

		final List<Player> opponents = event.getOpponents();

		for (int i = 0; i < opponents.size(); i++)
		{
			this.allPlayerSidesInGame.add(opponents.get(i));
			this.allSidesInGame.addNewItems(opponents.get(i));
		}

	}

	@Subscribe
	public void moveSide(final AjaxRequestTarget target, final MoveSideCometChannel event)
	{
		target.appendJavaScript("jQuery('#sidePlaceholder"
				+ event.getUuid().toString().replace("-", "_") + "').css({top: '"
				+ event.getSideY() + "px', left: '" + event.getSideX()
				+ "px', position:'absolute'}); ");
	}

	@Subscribe
	public void displayArrow(final AjaxRequestTarget target, final ArrowDrawCometChannel event)
	{
		if (!event.getSource().equals(event.getTarget()))
		{
			target.appendJavaScript("jQuery('._jsPlumb_endpoint_full').remove(); "
					+ "var e0 = jsPlumb.addEndpoint("
					+ event.getSource()
					+ " ); "
					+ "var e1 = jsPlumb.addEndpoint("
					+ event.getTarget()
					+ "); "
					+ " arrows.push({ 'source' : "
					+ event.getSource()
					+ ", 'target' : "
					+ event.getTarget()
					+ " }); "
					+ "	jsPlumb.connect({ source:e0, target:e1, connector:['Bezier', { curviness:70 }], overlays : [ "
					+ "					['Label', {location:0.7, id:'label', events:{ "
					+ "							} }], ['Arrow', { "
					+ "						cssClass:'l1arrow',  location:0.5, width:40,length:40 }]] }); ");
		}
	}

	@Subscribe
	public void switchDrawMode(final AjaxRequestTarget target,
			final SwitchDrawModeCometChannel event)
	{
		if (event.isDrawMode())
		{
			target.appendJavaScript("jQuery.gritter.add({ title : 'Draw mode ON', text : \"You are now in draw mode!\" , image : 'image/logoh2.gif', sticky : false, time : ''});");
			target.appendJavaScript("arrows = new Array(); drawMode = true; ");

			target.appendJavaScript("cardAlreadySelected = false; "
					+ "var plumbSource, plumbTarget; "
					+ "jQuery('.clickableCard').unbind('click');  "
					+ "jQuery('.clickableCard').unbind('tap');  "
					+ "jQuery('.clickableCard').click(function (event) { "
					+ "if (cardAlreadySelected) { "
					+ "	cardAlreadySelected = false; "
					+ "	plumbTarget = jQuery('#' + event.target.id).parent().parent().parent().parent().attr('id'); "
					+ " Wicket.Ajax.get({ 'u' : jQuery('#' + plumbTarget).data('arrowDrawUrl') + '&source=' + plumbSource + '&target=' + plumbTarget}); "
					+ "} else { "
					+ "	cardAlreadySelected = true; "
					+ "	plumbSource = jQuery('#' + event.target.id).parent().parent().parent().parent().attr('id'); "
					+ "}});");

			final ExternalImage img = new ExternalImage("drawModeOn", "image/draw_mode_on.png");
			this.getDrawModeParent().addOrReplace(img);
			target.add(this.getDrawModeParent());
			target.appendJavaScript("jQuery('[title]').tipsy({gravity: 's'}); ");
		}
		else
		{
			target.appendJavaScript("jQuery.gritter.add({ title : 'Draw mode OFF', text : \"You are now in normal mode!\" , image : 'image/logoh2.gif', sticky : false, time : ''});");

			final StringBuilder buil = new StringBuilder();
			buil.append("arrows = new Array(); drawMode = false; ");
			buil.append("jQuery('.clickableCard').unbind('click'); jQuery('._jsPlumb_connector').remove(); jQuery('._jsPlumb_overlay').remove(); jQuery('._jsPlumb_endpoint').remove(); ");

			for (final MagicCard mc : this.getAllMagicCardsInBattlefield())
			{
				final String uuidValidForJs = mc.getUuid().replace("-", "_");

				buil.append("jQuery('#card" + uuidValidForJs
						+ "').click(function(e) {  jQuery('#cardTooltip" + uuidValidForJs
						+ "').attr('style', 'display: block; position: absolute; left: "
						+ (mc.getX() + 127) + "px; top: " + (mc.getY() + 56)
						+ "px; z-index: 1;'); jQuery('#cardTooltip" + uuidValidForJs
						+ " > span').attr('style', 'display: block;'); }); ");

				// For mobile
				buil.append("var hammertime" + uuidValidForJs + " = jQuery('#card" + uuidValidForJs
						+ "').hammer(); ");
				buil.append("hammertime" + uuidValidForJs + ".on('tap', function(ev) { ");
				buil.append("jQuery('#cardTooltip" + uuidValidForJs
						+ "').attr('style', 'display: block; position: absolute; left: "
						+ (mc.getX() + 127) + "px; top: " + (mc.getY() + 56)
						+ "px; z-index: 1;'); jQuery('#cardTooltip" + uuidValidForJs
						+ " > span').attr('style', 'display: block;'); }); ");

				buil.append("jQuery('#cardTooltip" + uuidValidForJs + "').hide(); ");
			}

			target.appendJavaScript(buil.toString());

			final WebMarkupContainer img = new WebMarkupContainer("drawModeOn");
			this.getDrawModeParent().addOrReplace(img.setVisible(false));
			target.add(this.getDrawModeParent());
		}

	}

	@Subscribe
	public void logToConsole(final AjaxRequestTarget target, final ConsoleLogCometChannel event)
	{
		event.getLogger().logToConsole(target);
	}

	@Subscribe
	public void revealHand(final AjaxRequestTarget target, final RevealHandCometChannel event)
	{
		JavaScriptUtils.revealHand(target, event.getGame(), event.getPlayer(), event.getDeck());
	}

	@Override
	protected void configureResponse(final WebResponse response)
	{
		if (this.session != null)
		{
			final Locale originalLocale = this.session.getLocale();
			this.session.setLocale(originalLocale);
		}

		final String encoding = "text/html;charset=utf-8";
		response.setContentType(encoding);
		super.configureResponse(response);
	}

	private final void restoreBattlefieldState()
	{
		final Component galleryToUpdate;
		final Boolean isHandDisplayed = this.persistenceService.getPlayer(
				this.session.getPlayer().getId()).isHandDisplayed();
		galleryToUpdate = isHandDisplayed ? new HandComponent("gallery") : new WebMarkupContainer(
				"gallery");

		galleryToUpdate.setOutputMarkupId(true);
		this.galleryParent.addOrReplace(galleryToUpdate);

		// TODO use PersistenceService#getAllCardsInBattleFieldForAGame()
		for (final CardPanel cp : this.session.getAllCardPanelsInBattleField())
		{
			this.playCardParent.addOrReplace(cp);
		}

		this.add(new Behavior()
		{
			private static final long serialVersionUID = 1L;

			@Override
			public void renderHead(final Component component, final IHeaderResponse response)
			{
				super.renderHead(component, response);
				HomePage.this.restoreStateOfAllCardsInBattlefield(response);
			}

		});
	}

	final void restoreStateOfAllCardsInBattlefield(final IHeaderResponse response)
	{
		final StringBuilder js = new StringBuilder();
		// TODO use PersistenceService#getAllCardsInBattleFieldForAGame()
		final Collection<CardPanel> allCards = Collections.synchronizedCollection(this.session
				.getAllCardPanelsInBattleField());

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

					if (mc.isTapped())
					{
						js.append("jQuery('#card" + cp.getUuid().toString().replace("-", "_")
								+ "').rotate(90);");
					}
					else
					{
						js.append("jQuery('#card" + cp.getUuid().toString().replace("-", "_")
								+ "').rotate(0);");
					}
				}

			}
			catch (final IllegalArgumentException e)
			{
				HomePage.LOGGER.error("error parsing UUID of moved card", e);
			}
		}

		final List<SidePlaceholderPanel> allSides = this.session.getMySidePlaceholder();
		HomePage.LOGGER.info("size: " + allSides.size());

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

	public final WebMarkupContainer getDataBoxParent()
	{
		return this.dataBoxParent;
	}

	public WebMarkupContainer getGraveyardParent()
	{
		return this.graveyardParent;
	}

	public WebMarkupContainer getExileParent()
	{
		return this.exileParent;
	}

	public WebMarkupContainer getGalleryParent()
	{
		return this.galleryParent;
	}

	public WebMarkupContainer getGalleryRevealParent()
	{
		return this.galleryRevealParent;
	}

	public WebMarkupContainer getPlayCardParent()
	{
		return this.playCardParent;
	}

	public WebMarkupContainer getParentPlaceholder()
	{
		return this.parentPlaceholder;
	}

	public QuickView<MagicCard> generateCardListView(
			final List<MagicCard> _allMagicCardsInBattlefield, final boolean replace)
	{
		if (null == _allMagicCardsInBattlefield)
		{
			final List<MagicCard> newCards = new ArrayList<MagicCard>();
			this.allMagicCardsInBattlefield = newCards;
			this.allTooltipsInBattlefield = newCards;
		}
		else
		{
			this.allMagicCardsInBattlefield = _allMagicCardsInBattlefield;
			this.allTooltipsInBattlefield = _allMagicCardsInBattlefield;
		}

		final ListDataProvider<MagicCard> data = new ListDataProvider<MagicCard>(
				this.allMagicCardsInBattlefield);

		this.allCardsInBattlefield = new QuickView<MagicCard>("magicCards", data)
		{
			private static final long serialVersionUID = 1L;

			@Override
			protected void populate(final Item<MagicCard> item)
			{
				final MagicCard mc = item.getModelObject();
				final CardPanel cp = new CardPanel("cardPanel", mc.getSmallImageFilename(),
						mc.getUuidObject(), HomePage.this.persistenceService.getPlayer(mc.getDeck()
								.getPlayerId()));
				cp.setOutputMarkupId(true);
				item.add(cp);
			}
		};
		this.allCardsInBattlefield.setOutputMarkupId(true);

		this.allTooltips = new QuickView<MagicCard>("tooltips", data)
		{
			private static final long serialVersionUID = 1L;

			@Override
			protected void populate(final Item<MagicCard> item)
			{
				final MagicCard mc = item.getModelObject();

				if (null == mc.getToken())
				{
					final MagicCardTooltipPanel cardBubbleTip = new MagicCardTooltipPanel(
							"cardTooltip", mc.getUuidObject(), mc.getBigImageFilename(),
							mc.getOwnerSide(), mc);
					cardBubbleTip.setOutputMarkupId(true);
					cardBubbleTip.setMarkupId("cardTooltip" + mc.getUuid().replace("-", "_"));
					cardBubbleTip.add(new AttributeModifier("style",
							"display: none; position: relative; left: 50%; top: 50%; z-index: 1;"));

					item.add(cardBubbleTip);
				}
				else
				{
					final TokenTooltipPanel cardBubbleTip = new TokenTooltipPanel("cardTooltip",
							mc.getToken());
					cardBubbleTip.setOutputMarkupId(true);
					cardBubbleTip.setMarkupId("cardTooltip" + mc.getUuid().replace("-", "_"));
					cardBubbleTip.add(new AttributeModifier("style",
							"display: none; position: absolute; left: " + mc.getX() + "px; top: "
									+ mc.getY() + "px; z-index: 1;"));

					item.add(cardBubbleTip);
				}
			}
		};

		this.allTooltips.setOutputMarkupId(true);

		if (replace)
		{
			this.parentPlaceholder.addOrReplace(this.allCardsInBattlefield, this.allTooltips);
		}
		else
		{
			this.parentPlaceholder.add(this.allCardsInBattlefield, this.allTooltips);
		}

		return this.allCardsInBattlefield;
	}

	public final QuickView<MagicCard> getAllCardsInBattlefield()
	{
		return this.allCardsInBattlefield;
	}

	public final List<MagicCard> getAllMagicCardsInBattlefield()
	{
		return this.allMagicCardsInBattlefield;
	}

	public final QuickView<MagicCard> getAllTooltips()
	{
		return this.allTooltips;
	}

	public final List<MagicCard> getAllTooltipsInBattlefield()
	{
		return this.allTooltipsInBattlefield;
	}

	public WebMarkupContainer getSideParent()
	{
		return this.sideParent;
	}

	@SuppressWarnings("static-method")
	private QuickView<Player> populateSides(final ListDataProvider<Player> data)
	{
		return new QuickView<Player>("sides", data)
		{
			private static final long serialVersionUID = 1L;

			@Override
			protected void populate(final Item<Player> item)
			{
				try
				{
					final UUID uuid = UUID.fromString(item.getModelObject().getSideUuid());
					item.add(new SidePlaceholderPanel("side", item.getModelObject().getSide()
							.getSideName(), HomePage.this, uuid, item.getModelObject()));
				}
				catch (final Exception e)
				{
					// At first page load, no player is already available, so we
					// don't use a SidePlaceholderPanel
					item.add(new WebMarkupContainer("side"));
				}
			}

		};
	}

	public List<Player> getAllPlayersInGame()
	{
		return this.allPlayerSidesInGame;
	}

	public WebMarkupContainer getDrawModeParent()
	{
		return this.drawModeParent;
	}

	public List<ModalWindow> getAllOpenRevealTopLibraryCardWindows()
	{
		return this.allOpenRevealTopLibraryCardWindows;
	}

	public WebMarkupContainer getUsernameParent()
	{
		return this.usernameParent;
	}

	public WebMarkupContainer getConferenceParent()
	{
		return this.conferenceParent;
	}

}