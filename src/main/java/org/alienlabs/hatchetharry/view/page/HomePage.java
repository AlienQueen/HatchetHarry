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
import org.alienlabs.hatchetharry.model.PlayerAndCard;
import org.alienlabs.hatchetharry.model.Side;
import org.alienlabs.hatchetharry.model.User;
import org.alienlabs.hatchetharry.model.channel.AddSideCometChannel;
import org.alienlabs.hatchetharry.model.channel.AddSidesFromOtherBrowsersCometChannel;
import org.alienlabs.hatchetharry.model.channel.ArrowDrawCometChannel;
import org.alienlabs.hatchetharry.model.channel.AskMulliganCometChannel;
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
import org.alienlabs.hatchetharry.model.channel.ReorderCardCometChannel;
import org.alienlabs.hatchetharry.model.channel.RevealHandCometChannel;
import org.alienlabs.hatchetharry.model.channel.RevealTopLibraryCardCometChannel;
import org.alienlabs.hatchetharry.model.channel.StopRevealingHandCometChannel;
import org.alienlabs.hatchetharry.model.channel.SwitchDrawModeCometChannel;
import org.alienlabs.hatchetharry.model.channel.UntapAllCometChannel;
import org.alienlabs.hatchetharry.model.channel.UpdateDataBoxCometChannel;
import org.alienlabs.hatchetharry.model.channel.consolelog.AbstractConsoleLogStrategy;
import org.alienlabs.hatchetharry.model.channel.consolelog.ConsoleLogStrategy;
import org.alienlabs.hatchetharry.model.channel.consolelog.ConsoleLogType;
import org.alienlabs.hatchetharry.service.DataGenerator;
import org.alienlabs.hatchetharry.service.PersistenceService;
import org.alienlabs.hatchetharry.view.clientsideutil.BattlefieldService;
import org.alienlabs.hatchetharry.view.clientsideutil.EventBusPostService;
import org.alienlabs.hatchetharry.view.component.card.CardPanel;
import org.alienlabs.hatchetharry.view.component.card.RedrawArrowsBehavior;
import org.alienlabs.hatchetharry.view.component.gui.ChatPanel;
import org.alienlabs.hatchetharry.view.component.gui.ClockPanel;
import org.alienlabs.hatchetharry.view.component.gui.ConferencePanel;
import org.alienlabs.hatchetharry.view.component.gui.DataBox;
import org.alienlabs.hatchetharry.view.component.gui.ExileComponent;
import org.alienlabs.hatchetharry.view.component.gui.ExternalImage;
import org.alienlabs.hatchetharry.view.component.gui.FacebookLoginBehavior;
import org.alienlabs.hatchetharry.view.component.gui.GameNotifierBehavior;
import org.alienlabs.hatchetharry.view.component.gui.GraveyardComponent;
import org.alienlabs.hatchetharry.view.component.gui.HandComponent;
import org.alienlabs.hatchetharry.view.component.gui.ImportDeckDialog;
import org.alienlabs.hatchetharry.view.component.gui.MessageRedisplayBehavior;
import org.alienlabs.hatchetharry.view.component.gui.ReorderCardInBattlefieldBehavior;
import org.alienlabs.hatchetharry.view.component.gui.SidePlaceholderPanel;
import org.alienlabs.hatchetharry.view.component.modalwindow.AboutModalWindow;
import org.alienlabs.hatchetharry.view.component.modalwindow.AskMulliganModalWindow;
import org.alienlabs.hatchetharry.view.component.modalwindow.CountCardsModalWindow;
import org.alienlabs.hatchetharry.view.component.modalwindow.CreateGameModalWindow;
import org.alienlabs.hatchetharry.view.component.modalwindow.CreateTokenModalWindow;
import org.alienlabs.hatchetharry.view.component.modalwindow.JoinGameModalWindow;
import org.alienlabs.hatchetharry.view.component.modalwindow.JoinGameWithoutIdModalWindow;
import org.alienlabs.hatchetharry.view.component.modalwindow.LoginModalWindow;
import org.alienlabs.hatchetharry.view.component.modalwindow.MulliganModalWindow;
import org.alienlabs.hatchetharry.view.component.modalwindow.RevealTopLibraryCardModalWindow;
import org.alienlabs.hatchetharry.view.component.modalwindow.TeamInfoModalWindow;
import org.alienlabs.hatchetharry.view.component.modalwindow.UserPreferencesModalWindow;
import org.alienlabs.hatchetharry.view.component.zone.PlayCardFromGraveyardBehavior;
import org.apache.wicket.Component;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.atmosphere.Subscribe;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxLink;
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
import org.apache.wicket.request.mapper.parameter.PageParameters;
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
@edu.umd.cs.findbugs.annotations.SuppressFBWarnings(value = { "SE_INNER_CLASS",
		"SIC_INNER_SHOULD_BE_STATIC_ANON", "PREDICTABLE_RANDOM" }, justification = "SE_INNER_CLASS + SIC_INNER_SHOULD_BE_STATIC_ANON: In Wicket, serializable inner classes are common. And as the parent Page is serialized as well, this is no concern. This is no bad practice in Wicket. PREDICTABLE_RANDOM: SecureRandom is awfully slow and we don't need strong RNG")
public class HomePage extends TestReportPage
{
	private static final long serialVersionUID = 1L;
	static final Logger LOGGER = LoggerFactory.getLogger(HomePage.class);

	final HatchetHarrySession session;
	final WebMarkupContainer galleryParent;
	private final WebMarkupContainer galleryRevealParent;
	private final Component galleryReveal;
	final WebMarkupContainer graveyardParent;
	final WebMarkupContainer exileParent;
	private final List<ModalWindow> allOpenRevealTopLibraryCardWindows;
	private final WebMarkupContainer parentPlaceholder;
	private final WebMarkupContainer opponentParentPlaceholder;
	private final WebMarkupContainer firstSidePlaceholderParent;
	private final WebMarkupContainer secondSidePlaceholderParent;
	private final WebMarkupContainer sideParent;
	private final QuickView<Player> allSidesInGame;
	private final List<Player> allPlayerSidesInGame;
	private final WebMarkupContainer drawModeParent;
	private final WebMarkupContainer usernameParent;
	private final WebMarkupContainer gameIdParent;

	private final WebMarkupContainer conferenceParent;
	private final ModalWindow mulliganWindow;
	private final ModalWindow askMulliganWindow;

	@SpringBean
	PersistenceService persistenceService;
	@SpringBean
	private DataGenerator dataGenerator;

	private ModalWindow teamInfoWindow;
	private ModalWindow aboutWindow;
	ModalWindow teamInfoWindowResponsive;
	ModalWindow aboutWindowResponsive;
	private ModalWindow createGameWindow;
	private ModalWindow joinGameWindow;
	private ModalWindow joinGameWithoutIdWindow;
	private final ImportDeckDialog importDeckDialog;
	private ModalWindow revealTopLibraryCardWindow;
	ModalWindow createTokenWindow;
	private final ModalWindow countCardsWindow;
	final ModalWindow loginWindow;
	final ModalWindow preferencesWindow;
	Player player;
	private Deck deck;
	List<MagicCard> hand;
	WebMarkupContainer playCardLink;
	// TODO remove this
	WebMarkupContainer playCardParent;
	WebMarkupContainer playCardFromGraveyardLink;
	WebMarkupContainer thumbsPlaceholder;
	WebMarkupContainer graveyardThumbsPlaceholder;
	private WebMarkupContainer endTurnPlaceholder;
	private WebMarkupContainer inResponsePlaceholder;
	private WebMarkupContainer fineForMePlaceholder;
	private WebMarkupContainer untapAllPlaceholder;
	private WebMarkupContainer untapAndDrawPlaceholder;
	private final ClockPanel clockPanel;
	private AjaxLink<Void> endTurnLink;
	private AjaxLink<Void> inResponseLink;
	private AjaxLink<Void> fineForMeLink;
	private AjaxLink<Void> untapAllLink;
	private AjaxLink<Void> untapAndDrawLink;
	private WebMarkupContainer dataBoxParent;
	private Component dataBox;
	private AjaxLink<Void> createGameLink;
	private AjaxLink<Void> joinGameLink;
	private AjaxLink<Void> joinGameWithoutIdLink;
	private QuickView<MagicCard> allCardsInBattlefieldForSide1;
	private List<MagicCard> allMagicCardsInBattlefieldForSide1;
	private QuickView<MagicCard> allCardsInBattlefieldForSide2;
	private List<MagicCard> allMagicCardsInBattlefieldForSide2;
	private Label username;
	private Label gameId;

	@edu.umd.cs.findbugs.annotations.SuppressFBWarnings(value = "EC_UNRELATED_TYPES", justification = "If we put 'test'.equals(pp.get('test').toString()) it breaks everything!")
	public HomePage(final PageParameters pp) throws Exception
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
					this.session.getPlayer().getName(), null, null, null, Boolean.FALSE,
					this.session.getGameId());
			final NotifierCometChannel ncc = new NotifierCometChannel(
					NotifierAction.END_GAME_ACTION, null, null, this.session.getPlayer().getName(),
					null, null, null, null, "");

			final List<BigInteger> allPlayersInGameExceptMe = this.persistenceService
					.giveAllPlayersFromGameExceptMe(this.session.getGameId(), this.session
							.getPlayer().getId());
			EventBusPostService.post(allPlayersInGameExceptMe, new ConsoleLogCometChannel(logger),
					ncc);

			this.session.reinitSession();
			throw new RestartResponseException(HomePage.class);
		}

		if ((pp != null) && (pp.get("displayTooltips") != null)
				&& ("true".equals(pp.get("displayTooltips").toString())))
		{
			this.session.setDisplayTooltips(Boolean.TRUE);
		}

		// Resources
		this.addHeadResources();

		final FacebookSdk fsdk = new FacebookSdk("fb-root", "1398596203720626");
		fsdk.setFbAdmins("goupilpierre@wanadoo.fr");
		this.add(fsdk);

		if (this.session.isGameCreated().booleanValue())
		{
			this.gameId = new Label("gameId", "Game id: " + this.session.getGameId().longValue());
			this.gameId.setOutputMarkupId(true);
		}
		else
		{
			this.gameId = new Label("gameId", "No game at the moment");
			this.gameId.setOutputMarkupId(true);
		}

		this.gameIdParent = new WebMarkupContainer("gameIdParent");
		this.gameIdParent.setOutputMarkupId(true);
		this.gameIdParent.add(this.gameId);
		this.add(this.gameIdParent);

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

		this.parentPlaceholder = new WebMarkupContainer("parentPlaceholder");
		this.parentPlaceholder.setOutputMarkupId(true);
		this.add(this.parentPlaceholder);

		this.opponentParentPlaceholder = new WebMarkupContainer("opponentParentPlaceholder");
		this.opponentParentPlaceholder.setOutputMarkupId(true);
		this.add(this.opponentParentPlaceholder);

		if (!this.session.isGameCreated().booleanValue())
		{
			this.dataGenerator.afterPropertiesSet();
			this.createPlayer();

			this.buildHandCards();
			this.buildHandMarkup();
			this.buildDataBox(this.player.getGame().getId().longValue());
		}
		else
		{
			if (this.session.getPlayer() == null)
			{
				this.createPlayer();
			}
			else
			{
				this.restoreBattlefieldState();
			}
			this.player = this.session.getPlayer();

			this.buildHandCards();
			this.buildDataBox(this.player.getGame().getId().longValue());
		}

		// Side
		this.sideParent = new WebMarkupContainer("sideParent");
		this.sideParent.setOutputMarkupId(true);

		this.allPlayerSidesInGame = this.persistenceService.getAllPlayersOfGame(this.session
				.getGameId().longValue());
		final ListDataProvider<Player> data = new ListDataProvider<Player>(
				this.allPlayerSidesInGame);

		this.allSidesInGame = this.populateSides(data);

		this.sideParent.add(this.allSidesInGame);
		this.add(this.sideParent);

		this.graveyardParent = new WebMarkupContainer("graveyardParent");
		this.graveyardParent.setMarkupId("graveyardParent");
		this.graveyardParent.setOutputMarkupId(true);
		this.add(this.graveyardParent);

		this.exileParent = new WebMarkupContainer("exileParent");
		this.exileParent.setMarkupId("exileParent");
		this.exileParent.setOutputMarkupId(true);
		this.add(this.exileParent);

		// Welcome message
		final Label message1 = new Label("message1", "version 0.23.0 (release Battlefield),");
		final Label message2 = new Label("message2", "built on Sunday, 23th of November 2014.");
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

		this.mulliganWindow = new ModalWindow("mulliganWindow");
		this.generateMulliganLink("mulliganLink", this.mulliganWindow);
		this.generateMulliganLink("mulliganLinkResponsive", this.mulliganWindow);
		this.askMulliganWindow = new ModalWindow("askMulliganWindow");
		this.askMulliganWindow.setInitialWidth(500);
		this.askMulliganWindow.setInitialHeight(100);
		this.askMulliganWindow.setCssClassName(ModalWindow.CSS_CLASS_GRAY);
		this.askMulliganWindow.setMaskType(ModalWindow.MaskType.SEMI_TRANSPARENT);
		this.add(this.askMulliganWindow);

		this.teamInfoWindow = this.generateTeamInfoLink("teamInfoLinkResponsive",
				this.teamInfoWindow);

		final GameNotifierBehavior notif = new GameNotifierBehavior(this);
		this.add(notif);

		this.createGameWindow = new ModalWindow("createGameWindow");
		this.add(this.createGameWindow = this.generateCreateGameModalWindow("createGameLink",
				this.player, this.createGameWindow));
		this.add(this.createGameWindow = this.generateCreateGameModalWindow(
				"createGameLinkResponsive", this.player, this.createGameWindow));

		this.joinGameWindow = new ModalWindow("joinGameWindow");
		this.add(this.joinGameWindow = this.generateJoinGameModalWindow("joinGameLink",
				this.player, this.joinGameWindow));
		this.add(this.joinGameWindow = this.generateJoinGameModalWindow("joinGameLinkResponsive",
				this.player, this.joinGameWindow));

		this.joinGameWithoutIdWindow = new ModalWindow("joinGameWithoutIdWindow");
		this.add(this.joinGameWithoutIdWindow = this.generateJoinGameWithoutIdModalWindow(
				"joinGameWithoutIdLink", this.player, this.joinGameWithoutIdWindow));
		this.add(this.joinGameWithoutIdWindow = this.generateJoinGameWithoutIdModalWindow(
				"joinGameWithoutIdLinkResponsive", this.player, this.joinGameWithoutIdWindow));

		this.generatePlayCardLink();
		this.add(this.generatePlayCardFromGraveyardLink("playCardFromGraveyardLinkDesktop"));
		this.add(this.generatePlayCardFromGraveyardLink("playCardFromGraveyardLinkResponsive"));
		this.generateCardPanels();

		this.generateDrawCardLink();
		final RedrawArrowsBehavior rab = new RedrawArrowsBehavior(this.player.getGame().getId());
		this.add(rab);

		// Comet chat channel
		this.add(new ChatPanel("chatPanel"));

		this.buildEndTurnLink();
		this.buildInResponseLink();
		this.buildFineForMeLink();
		this.buildUntapAllLink();
		this.buildUntapAndDrawLink();
		this.buildCombatLink();

		this.importDeckDialog = new ImportDeckDialog("importDeckDialog");
		this.add(this.importDeckDialog);
		this.generateImportDeckLink("importDeckLink");
		this.generateImportDeckLink("importDeckLinkResponsive");

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
		this.generateDiscardAtRandomLink("discardAtRandomLink");
		this.generateDiscardAtRandomLink("discardAtRandomLinkResponsive");
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
		this.add(new MessageRedisplayBehavior(this.session.getGameId()));

		this.drawModeParent = new WebMarkupContainer("drawModeParent");
		this.drawModeParent.setOutputMarkupId(true);

		if (this.session.getPlayer().getGame().isDrawMode().booleanValue())
		{
			this.drawModeParent.add(new ExternalImage("drawModeOn", "image/draw_mode_on.png"));
		}
		else
		{
			this.drawModeParent.add(new WebMarkupContainer("drawModeOn").setVisible(false));
		}

		this.add(this.drawModeParent);

		if (this.session.isLoggedIn().booleanValue())
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

	// TODO: mutualize with restoreBattlefieldState
	private final void generateCardPanels()
	{
		final List<MagicCard> allCardsInBattlefield = this.persistenceService
				.getAllCardsInBattlefieldForAGame(this.session.getGameId());
		LOGGER.info("allCardsInBattlefield.size(): " + allCardsInBattlefield.size());
		final List<MagicCard> allCardsInBattlefieldForPlayer1 = new ArrayList<MagicCard>();
		final List<MagicCard> allCardsInBattlefieldForPlayer2 = new ArrayList<MagicCard>();

		for (final MagicCard mc : allCardsInBattlefield)
		{
			if (mc.getDeck().getDeckId().longValue() == this.session.getPlayer().getDeck()
					.getDeckId().longValue())
			{
				allCardsInBattlefieldForPlayer1.add(mc);
			}
			else
			{
				allCardsInBattlefieldForPlayer2.add(mc);
			}
		}

		this.generateCardListViewForSide1(allCardsInBattlefieldForPlayer1);
		this.generateCardListViewForSide2(allCardsInBattlefieldForPlayer2);
	}

	private void generateHideAllTooltipsLink(final String id)
	{
		this.add(new AjaxLink<Void>(id)
		{
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(final AjaxRequestTarget target)
			{
				target.appendJavaScript(BattlefieldService.HIDE_ALL_TOOLTIPS);
				target.appendJavaScript(BattlefieldService.HIDE_MENUS);
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
				target.prependJavaScript(BattlefieldService.HIDE_MENUS);
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
				final Long _gameId = HomePage.this.session.getGameId();
				final List<BigInteger> allPlayersInGameExceptMe = HomePage.this.persistenceService
						.giveAllPlayersFromGameExceptMe(_gameId, HomePage.this.session.getPlayer()
								.getId());

				final NotifierCometChannel ncc = new NotifierCometChannel(
						NotifierAction.REVEAL_HAND, null, null, HomePage.this.session.getPlayer()
								.getName(), "", "", "", null, "");
				final ConsoleLogStrategy logger = AbstractConsoleLogStrategy.chooseStrategy(
						ConsoleLogType.REVEAL_HAND, null, null, null, null, HomePage.this.session
								.getPlayer().getName(), null, null, null, Boolean.FALSE, _gameId);
				final RevealHandCometChannel rhcc = new RevealHandCometChannel(_gameId,
						HomePage.this.session.getPlayer().getId(), HomePage.this.session
								.getPlayer().getDeck().getDeckId());
				EventBusPostService.post(allPlayersInGameExceptMe, ncc, new ConsoleLogCometChannel(
						logger), rhcc);

				final List<BigInteger> playerToWhomToSend = new ArrayList<BigInteger>();
				playerToWhomToSend.add(BigInteger.valueOf(HomePage.this.session.getPlayer().getId()
						.longValue()));
				EventBusPostService.post(playerToWhomToSend, ncc,
						new ConsoleLogCometChannel(logger));
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

			@SuppressWarnings("boxing")
			@Override
			public void onClick(final AjaxRequestTarget target)
			{
				final Player _player = HomePage.this.persistenceService
						.getPlayer(HomePage.this.session.getPlayer().getId());
				final Boolean isHandDisplayed = _player.isHandDisplayed();

				if (isHandDisplayed)
				{
					HomePage.this.galleryParent.addOrReplace(new WebMarkupContainer("gallery"));
					target.add(HomePage.this.galleryParent);
				}
				else
				{
					BattlefieldService.updateHand(target);

				}

				_player.setHandDisplayed(!isHandDisplayed);
				HomePage.this.session.setPlayer(_player);
				HomePage.this.persistenceService.updatePlayer(_player);
			}
		};

		this.add(showHandLink);

		final AjaxLink<Void> drawModeLink = new AjaxLink<Void>("drawModeLink")
		{
			private static final long serialVersionUID = 1L;

			@Override
			@SuppressWarnings("boxing")
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
				EventBusPostService.post(allPlayersInGame, new SwitchDrawModeCometChannel(g
						.isDrawMode().booleanValue()));
			}

			@Override
			// TODE:remove me
			protected void onComponentTag(final ComponentTag tag)
			{
				super.onComponentTag(tag);

				if ("a".equalsIgnoreCase(tag.getName()) || "link".equalsIgnoreCase(tag.getName())
						|| "area".equalsIgnoreCase(tag.getName()))
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

				if ((isGraveyardDisplayed != null) && isGraveyardDisplayed.booleanValue())
				{
					HomePage.this.graveyardParent.addOrReplace(new WebMarkupContainer("graveyard"));
					target.add(HomePage.this.graveyardParent);
				}
				else
				{
					BattlefieldService.updateGraveyard(target);

				}

				if ((isGraveyardDisplayed != null) && (isGraveyardDisplayed.booleanValue() == true))
				{
					_player.setGraveyardDisplayed(Boolean.FALSE);
				}
				else
				{
					_player.setGraveyardDisplayed(Boolean.TRUE);
				}

				HomePage.this.session.setPlayer(_player);
				HomePage.this.persistenceService.updatePlayer(_player);
			}

			@Override
			// TODE:remove me
			protected void onComponentTag(final ComponentTag tag)
			{
				super.onComponentTag(tag);

				if ("a".equalsIgnoreCase(tag.getName()) || "link".equalsIgnoreCase(tag.getName())
						|| "area".equalsIgnoreCase(tag.getName()))
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

				if ((isExileDisplayed != null) && isExileDisplayed.booleanValue())
				{
					HomePage.this.exileParent.addOrReplace(new WebMarkupContainer("exile"));
					target.add(HomePage.this.exileParent);
				}
				else
				{
					BattlefieldService.updateExile(target, _player.getGame().getId(),
							_player.getId(), _player.getDeck().getDeckId());

				}

				if ((isExileDisplayed != null) && (isExileDisplayed.booleanValue() == true))
				{
					_player.setExileDisplayed(Boolean.FALSE);
				}
				else
				{
					_player.setExileDisplayed(Boolean.TRUE);
				}

				HomePage.this.session.setPlayer(_player);
				HomePage.this.persistenceService.updatePlayer(_player);
			}

			// TODE:remove me
			@Override
			protected void onComponentTag(final ComponentTag tag)
			{
				super.onComponentTag(tag);

				if ("a".equalsIgnoreCase(tag.getName()) || "link".equalsIgnoreCase(tag.getName())
						|| "area".equalsIgnoreCase(tag.getName()))
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

		this.endTurnLink = new IndicatingAjaxLink<Void>("endTurnLink")
		{
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(final AjaxRequestTarget target)
			{
				final Player me = HomePage.this.session.getPlayer();
				final Long _gameId = HomePage.this.persistenceService
						.getPlayer(HomePage.this.session.getPlayer().getId()).getGame().getId();

				final ConsoleLogStrategy logger = AbstractConsoleLogStrategy.chooseStrategy(
						ConsoleLogType.END_OF_TURN, null, null, null, null, HomePage.this.session
								.getPlayer().getName(), null, null, null, Boolean.FALSE,
						HomePage.this.session.getGameId());
				final NotifierCometChannel ncc = new NotifierCometChannel(
						NotifierAction.END_OF_TURN_ACTION, null, me.getId(), me.getName(), me
								.getSide().getSideName(), null, null, null, "");
				final List<BigInteger> allPlayersInGame = HomePage.this.persistenceService
						.giveAllPlayersFromGame(_gameId);
				EventBusPostService.post(allPlayersInGame, ncc, new ConsoleLogCometChannel(logger));

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

		this.inResponseLink = new IndicatingAjaxLink<Void>("inResponseLink")
		{
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(final AjaxRequestTarget target)
			{
				final Game game = HomePage.this.persistenceService.getGame(HomePage.this.session
						.getGameId());

				final Player me = HomePage.this.session.getPlayer();
				final NotifierCometChannel ncc = new NotifierCometChannel(
						NotifierAction.IN_RESPONSE_ACTION, null, null, me.getName(), null, null,
						null, null, "");

				final List<BigInteger> allPlayersInGame = HomePage.this.persistenceService
						.giveAllPlayersFromGame(game.getId());
				EventBusPostService.post(allPlayersInGame, ncc);

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

		this.fineForMeLink = new IndicatingAjaxLink<Void>("fineForMeLink")
		{
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(final AjaxRequestTarget target)
			{
				final Game game = HomePage.this.persistenceService.getGame(HomePage.this.session
						.getGameId());

				final Player me = HomePage.this.session.getPlayer();
				final NotifierCometChannel ncc = new NotifierCometChannel(
						NotifierAction.FINE_FOR_ME_ACTION, null, null, me.getName(), null, null,
						null, null, "");

				final List<BigInteger> allPlayersInGame = HomePage.this.persistenceService
						.giveAllPlayersFromGame(game.getId());
				EventBusPostService.post(allPlayersInGame, ncc);
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

		this.untapAllLink = new IndicatingAjaxLink<Void>("untapAllLink")
		{
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(final AjaxRequestTarget target)
			{
				final Long _gameId = HomePage.this.session.getGameId();
				final List<BigInteger> allPlayersInGame = HomePage.this.persistenceService
						.giveAllPlayersFromGame(_gameId);

				final List<MagicCard> allCards = HomePage.this.persistenceService
						.getAllCardsAndTokensInBattlefieldForAGameAndAPlayer(_gameId,
								HomePage.this.session.getPlayer().getId(), HomePage.this.session
										.getPlayer().getDeck().getDeckId());
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
						ConsoleLogType.TAP_UNTAP, null, null, null, null, HomePage.this.session
								.getPlayer().getName(), null, null, null, Boolean.FALSE, _gameId);
				final UntapAllCometChannel uacc = new UntapAllCometChannel(_gameId,
						HomePage.this.session.getPlayer().getId(), HomePage.this.session
								.getPlayer().getDeck().getDeckId(), HomePage.this.session
								.getPlayer().getName(), allCards);
				EventBusPostService
						.post(allPlayersInGame, uacc, new ConsoleLogCometChannel(logger));
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

		this.untapAndDrawLink = new IndicatingAjaxLink<Void>("untapAndDrawLink")
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

	private void buildHandCards()
	{
		if (this.session.isHandHasBeenCreated().booleanValue())
		{
			this.hand = this.session.getFirstCardsInHand();
		}
		else
		{
			this.hand = this.createFirstCards();
		}
	}

	private void createPlayer()
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
			final String _name)
	{
		Player p = new Player();
		final Side side = new Side();
		side.setSideName(_side);
		p.setSide(side);
		p.setName(_name);
		p.setJsessionid(_jsessionid);
		p.setLifePoints(20L);

		Game game = new Game();
		game = this.persistenceService.createGameAndPlayer(game, p);
		p = game.getPlayers().iterator().next();
		p.setGame(game);

		this.session.setPlayerHasBeenCreated();

		this.deck = this.persistenceService.getDeckByDeckArchiveName("aggro-combo Red / Black");
		p.setDeck(this.deck);
		final List<MagicCard> mc = this.persistenceService.getAllCardsFromDeck(this.deck
				.getDeckArchive().getDeckName());

		for (final MagicCard card : mc)
		{
			card.setGameId(game.getId());
		}
		this.persistenceService.saveOrUpdateAllMagicCards(mc);

		this.deck.setCards(this.deck.shuffleLibrary(this.deck.getCards()));
		this.deck.setPlayerId(p.getId());

		p.setDeck(this.deck);
		this.persistenceService.updatePlayer(p);
		this.session.setGameId(game.getId());

		this.session.setPlayer(p);
		this.player = p;
	}

	private void generatePlayCardLink()
	{
		this.playCardLink = new WebMarkupContainer("playCardLink");
		this.playCardLink.setMarkupId("playCardLink0");
		this.playCardLink.setOutputMarkupId(true);

		this.add(this.playCardLink);
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

		final AjaxLink<Void> combatLink = new IndicatingAjaxLink<Void>("combatLink")
		{
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(final AjaxRequestTarget target)
			{
				HomePage.LOGGER.info("clicked on declare combat");
				HomePage.this.session.setCombatInProgress(!HomePage.this.session
						.isCombatInProgress().booleanValue());
				final Long _gameId = HomePage.this.session.getGameId();
				final List<BigInteger> allPlayersInGame = HomePage.this.persistenceService
						.giveAllPlayersFromGame(_gameId);

				final NotifierCometChannel ncc = new NotifierCometChannel(
						NotifierAction.COMBAT_IN_PROGRESS_ACTION, null, null, HomePage.this.session
								.getPlayer().getName(), "", "", "",
						HomePage.this.session.isCombatInProgress(), "");
				final ConsoleLogStrategy logger = AbstractConsoleLogStrategy.chooseStrategy(
						ConsoleLogType.COMBAT, null, null, HomePage.this.session
								.isCombatInProgress(), null, HomePage.this.session.getPlayer()
								.getName(), null, null, null, Boolean.FALSE, _gameId);

				EventBusPostService.post(allPlayersInGame, ncc, new ConsoleLogCometChannel(logger));
			}
		};
		combatLink.setMarkupId("combatLink");
		combatLink.setOutputMarkupId(true);

		combatPlaceholder.add(combatLink);

		this.add(combatPlaceholder);
	}

	private void generateDrawCardLink()
	{
		final AjaxLink<String> drawCardLink = new IndicatingAjaxLink<String>("drawCardLink")
		{
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(final AjaxRequestTarget target)
			{
				Long gameId = HomePage.this.session.getPlayer().getGame().getId();
				Long deckId = HomePage.this.session.getPlayer().getDeck().getDeckId();

				final List<MagicCard> cards = HomePage.this.persistenceService
						.getAllCardsInLibraryForDeckAndPlayer(gameId, HomePage.this.session
								.getPlayer().getId(), deckId);

				if ((cards != null) && (!cards.isEmpty()))
				{
					final MagicCard card = cards.get(0);

					final Deck _deck = HomePage.this.persistenceService.getDeck(deckId.longValue());
					_deck.getCards().remove(card);
					HomePage.this.persistenceService.saveOrUpdateDeck(_deck);

					card.setZone(CardZone.HAND);

					List<MagicCard> allCardsInHand = HomePage.this.persistenceService
							.getAllCardsInHandForAGameAndADeck(gameId, deckId);
					if (!allCardsInHand.isEmpty())
					{
						card.setZoneOrder(allCardsInHand.get(allCardsInHand.size() - 1)
								.getZoneOrder() + 1L);
					}

					HomePage.this.persistenceService.updateCard(card);

					final ArrayList<MagicCard> list = HomePage.this.session.getFirstCardsInHand();
					list.add(card);
					HomePage.this.session.setFirstCardsInHand(list);

					BattlefieldService.updateHand(target);

					final Player me = HomePage.this.session.getPlayer();
					final Long _gameId = HomePage.this.persistenceService
							.getPlayer(HomePage.this.session.getPlayer().getId()).getGame().getId();

					final Deck d = me.getDeck();
					final List<MagicCard> _hand = d
							.reorderMagicCards(HomePage.this.persistenceService
									.getAllCardsInHandForAGameAndADeck(_gameId, d.getDeckId()));
					HomePage.this.persistenceService.saveOrUpdateAllMagicCards(_hand);
					final List<MagicCard> library = d
							.reorderMagicCards(HomePage.this.persistenceService
									.getAllCardsInLibraryForDeckAndPlayer(_gameId, me.getId(),
											d.getDeckId()));
					HomePage.this.persistenceService.saveOrUpdateAllMagicCards(library);

					final ConsoleLogStrategy logger = AbstractConsoleLogStrategy.chooseStrategy(
							ConsoleLogType.DRAW_CARD, null, null, null, null, HomePage.this.session
									.getPlayer().getName(), null, null, null, null, _gameId);
					final NotifierCometChannel ncc = new NotifierCometChannel(
							NotifierAction.DRAW_CARD_ACTION, null, me.getId(), me.getName(), me
									.getSide().getSideName(), null, null, null, "");

					final List<BigInteger> allPlayersInGame = HomePage.this.persistenceService
							.giveAllPlayersFromGame(_gameId);
					EventBusPostService.post(allPlayersInGame, ncc, new ConsoleLogCometChannel(
							logger));
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

				if ("a".equalsIgnoreCase(tag.getName()) || "link".equalsIgnoreCase(tag.getName())
						|| "area".equalsIgnoreCase(tag.getName()))
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

				response.render(JavaScriptHeaderItem.forReference(new PackageResourceReference(
						HomePage.class, "script/jquery/jquery.atmosphere.js")));
				response.render(JavaScriptHeaderItem.forReference(new PackageResourceReference(
						HomePage.class, "script/jquery/jquery.wicketatmosphere.js")));
				response.render(JavaScriptHeaderItem.forReference(new PackageResourceReference(
						HomePage.class, "script/draggableHandle/jquery.ui.core.min-1.9.2.js")));
				response.render(JavaScriptHeaderItem.forReference(new PackageResourceReference(
						HomePage.class, "script/draggableHandle/jquery.ui.widget.min-1.9.2.js")));
				response.render(JavaScriptHeaderItem.forReference(new PackageResourceReference(
						HomePage.class, "script/draggableHandle/jquery.ui.mouse.min-1.9.2.js")));
				response.render(JavaScriptHeaderItem.forReference(new PackageResourceReference(
						HomePage.class, "script/draggableHandle/jquery.ui.touch-punch.js")));
				response.render(JavaScriptHeaderItem.forReference(new PackageResourceReference(
						HomePage.class, "script/draggableHandle/jquery.ui.droppable.min-1.9.2.js")));
				response.render(JavaScriptHeaderItem.forReference(new PackageResourceReference(
						HomePage.class, "script/draggableHandle/jquery.ui.draggable.min-1.9.2.js")));
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
						HomePage.class, "script/draggableHandle/jquery.hammer.min.js")));
				response.render(JavaScriptHeaderItem.forReference(new PackageResourceReference(
						HomePage.class, "script/jquery.jsPlumb-1.5.3-min.js")));
				response.render(JavaScriptHeaderItem.forReference(new PackageResourceReference(
						HomePage.class, "script/tour/bootstrap-tour-standalone.min.js")));
				response.render(JavaScriptHeaderItem.forReference(new PackageResourceReference(
						HomePage.class, "script/contextmenu/jquery.popmenu.min.js")));

				response.render(CssHeaderItem.forReference(new PackageResourceReference(
						HomePage.class, "stylesheet/jMenu.jquery.css")));
				response.render(CssHeaderItem.forReference(new PackageResourceReference(
						HomePage.class, "stylesheet/layout.css")));
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
						HomePage.class, "stylesheet/bootstrap-tour-standalone.min.css")));
				response.render(CssHeaderItem.forReference(new PackageResourceReference(
						HomePage.class, "stylesheet/myStyle.css")));
				response.render(CssHeaderItem.forReference(new PackageResourceReference(
						HomePage.class, "stylesheet/cards.css")));

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
		final Boolean isHandDisplayed = this.persistenceService.getPlayer(
				this.session.getPlayer().getId()).isHandDisplayed();
		galleryToUpdate = isHandDisplayed.booleanValue()
				? new HandComponent("gallery")
				: new WebMarkupContainer("gallery");

		galleryToUpdate.setOutputMarkupId(true);
		this.galleryParent.add(galleryToUpdate);
	}

	private void buildGraveyardMarkup()
	{
		final Component graveyardToUpdate;
		final Boolean isGraveyardDisplayed = this.persistenceService.getPlayer(
				this.session.getPlayer().getId()).isGraveyardDisplayed();
		graveyardToUpdate = ((isGraveyardDisplayed != null) && isGraveyardDisplayed.booleanValue())
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
		exileToUpdate = ((isExileDisplayed != null) && isExileDisplayed.booleanValue())
				? new ExileComponent("exile")
				: new WebMarkupContainer("exile");

		exileToUpdate.setOutputMarkupId(true);
		this.exileParent.add(exileToUpdate);
	}

	// TODO remove me
	private List<MagicCard> createFirstCards()
	{
		if (this.session.isPlayerCreated().booleanValue())
		{
			this.player = this.session.getPlayer();
			this.deck = this.persistenceService.getDeck(this.player.getDeck().getDeckId()
					.longValue());

			if ((this.deck == null) || (this.deck.getCards() == null)
					|| (this.deck.getCards().isEmpty()))
			{
				this.deck = this.persistenceService
						.getDeckByDeckArchiveName("aggro-combo Red / Black");
				this.player.setDeck(this.deck);
			}


			final ArrayList<MagicCard> _cards = new ArrayList<MagicCard>();

			for (final MagicCard mc : this.persistenceService.getAllCardsFromDeck(this.deck
					.getDeckArchive().getDeckName()))
			{
				mc.setGameId(this.session.getGameId());
				mc.setDeck(this.deck);
				mc.getDeck().setPlayerId(this.player.getId());
				_cards.add(mc);
			}
			this.player.getDeck().setCards(_cards);

			final ArrayList<MagicCard> cards = new ArrayList<MagicCard>();
			if (!this.session.isHandCardsHaveBeenBuilt().booleanValue())
			{
				this.deck.setCards(this.deck.shuffleLibrary(this.deck.getCards()));
			}

			for (int i = 0; i < 7; i++)
			{
				final MagicCard mc = this.deck.getCards().get(i);
				mc.setZone(CardZone.HAND);
				mc.setGameId(this.session.getPlayer().getGame().getId());
				cards.add(i, mc);
			}

			for (int i = 7; i < this.deck.getCards().size(); i++)
			{
				final MagicCard mc = this.deck.getCards().get(i);
				mc.setZone(CardZone.LIBRARY);
				mc.setGameId(this.session.getPlayer().getGame().getId());
				cards.add(i, mc);
			}

			this.persistenceService.updateAllMagicCards(cards);
			this.session.setFirstCardsInHand(cards);
			this.session.setHandHasBeenCreated();

			this.hand = cards;
			return cards;
		}
		this.createPlayer();
		this.deck = this.persistenceService.getDeck(this.player.getDeck().getDeckId().longValue());
		final ArrayList<MagicCard> cards = new ArrayList<MagicCard>();

		for (int i = 0; i < this.deck.getCards().size(); i++)
		{
			final MagicCard mc = this.deck.getCards().get(i);

			if (CardZone.HAND.equals(mc.getZone()))
			{
				cards.add(i, mc);
			}
		}

		this.session.setFirstCardsInHand(cards);
		this.session.setHandHasBeenCreated();
		this.hand = cards;

		return this.session.getPlayer().getDeck().getCards();

	}

	private ModalWindow generateAboutLink(final String id, final ModalWindow window)
	{
		window.setInitialWidth(450);
		window.setInitialHeight(700);
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
				target.prependJavaScript(BattlefieldService.HIDE_MENUS);
				target.appendJavaScript("Wicket.Window.unloadConfirmation = false;");
				window.show(target);
			}
		};

		aboutLink.setOutputMarkupId(true);
		window.setOutputMarkupId(true);
		this.add(aboutLink);
		return window;
	}

	private ModalWindow generateMulliganLink(final String id, final ModalWindow window)
	{
		window.setInitialWidth(500);
		window.setInitialHeight(150);
		window.setTitle("HatchetHarry - Mulligan");
		window.setContent(new MulliganModalWindow(window, window.getContentId()));
		window.setCssClassName(ModalWindow.CSS_CLASS_GRAY);
		window.setMaskType(ModalWindow.MaskType.SEMI_TRANSPARENT);
		this.add(window);

		final AjaxLink<Void> mulliganLink = new AjaxLink<Void>(id)
		{
			private static final long serialVersionUID = 8140325977385015896L;

			@Override
			public void onClick(final AjaxRequestTarget target)
			{
				target.prependJavaScript(BattlefieldService.HIDE_MENUS);
				target.appendJavaScript("Wicket.Window.unloadConfirmation = false;");
				window.show(target);
			}
		};

		mulliganLink.setOutputMarkupId(true);
		window.setOutputMarkupId(true);
		this.add(mulliganLink);
		return window;
	}

	private ModalWindow generateTeamInfoLink(final String id, final ModalWindow window)
	{
		window.setInitialWidth(475);
		window.setInitialHeight(750);
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
				target.prependJavaScript(BattlefieldService.HIDE_MENUS);
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
			final ModalWindow window)
	{
		window.setInitialWidth(475);
		window.setInitialHeight(550);
		window.setTitle("Create a game");

		window.setContent(new CreateGameModalWindow(window, window.getContentId(), _player, this));
		window.setCssClassName(ModalWindow.CSS_CLASS_GRAY);
		window.setMaskType(ModalWindow.MaskType.SEMI_TRANSPARENT);

		this.createGameLink = new AjaxLink<Void>(id)
		{
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(final AjaxRequestTarget _target)
			{
				_target.prependJavaScript(BattlefieldService.HIDE_MENUS);
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
		window.setInitialHeight(430);
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
				_target.prependJavaScript(BattlefieldService.HIDE_MENUS);
				_target.appendJavaScript("Wicket.Window.unloadConfirmation = false;");
				window.show(_target);
			}
		};

		this.joinGameLink.setOutputMarkupId(true).setMarkupId(id);
		window.setOutputMarkupId(true);

		this.add(this.joinGameLink);

		return window;
	}

	private ModalWindow generateJoinGameWithoutIdModalWindow(final String id, final Player _player,
			final ModalWindow window)
	{
		window.setInitialWidth(475);
		window.setInitialHeight(500);
		window.setTitle("Join a game without ID");

		window.setContent(new JoinGameWithoutIdModalWindow(window, window.getContentId(), _player,
				this.dataBoxParent, this));
		window.setCssClassName(ModalWindow.CSS_CLASS_GRAY);
		window.setMaskType(ModalWindow.MaskType.SEMI_TRANSPARENT);

		this.joinGameWithoutIdLink = new AjaxLink<Void>(id)
		{
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(final AjaxRequestTarget _target)
			{
				_target.prependJavaScript(BattlefieldService.HIDE_MENUS);
				_target.appendJavaScript("Wicket.Window.unloadConfirmation = false;");
				window.show(_target);
			}
		};

		this.joinGameWithoutIdLink.setOutputMarkupId(true).setMarkupId(id);
		window.setOutputMarkupId(true);

		this.add(this.joinGameWithoutIdLink);

		return window;
	}

	private void generateImportDeckLink(final String id)
	{
		final AjaxLink<Void> importDeckLink = new AjaxLink<Void>(id)
		{
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(final AjaxRequestTarget target)
			{
				target.prependJavaScript(BattlefieldService.HIDE_MENUS);
				target.appendJavaScript("jQuery('#importDeck').dialog('open');");
			}
		};

		importDeckLink.setOutputMarkupId(true);
		this.importDeckDialog.setOutputMarkupId(true);
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
				if (HomePage.this.session.getTopCardIndex() > 0L)
				{
					HomePage.this.session.setTopCardIndex(Long.valueOf(HomePage.this.session
							.getTopCardIndex() - 1L));
				}

			}
		});
		window.setInitialWidth(500);
		window.setInitialHeight(510);

		final List<MagicCard> allCardsInLibrary = this.persistenceService
				.getAllCardsInLibraryForDeckAndPlayer(this.session.getGameId(), this.session
						.getPlayer().getId(), this.session.getPlayer().getDeck().getDeckId());
		final MagicCard firstCard;

		if (allCardsInLibrary.isEmpty())
		{
			firstCard = null;
		}
		else
		{
			firstCard = allCardsInLibrary.get(this.session.getTopCardIndex().intValue());
		}

		window.setContent(new RevealTopLibraryCardModalWindow(window.getContentId(), window,
				firstCard));

		window.setCssClassName(ModalWindow.CSS_CLASS_GRAY);
		window.setMaskType(ModalWindow.MaskType.SEMI_TRANSPARENT);
		window.setOutputMarkupId(true);

		this.revealTopLibraryCardWindow = window;
		this.add(this.revealTopLibraryCardWindow);

		final AjaxLink<Void> revealTopLibraryCardLink = new AjaxLink<Void>(id)
		{
			private static final long serialVersionUID = 1L;

			@SuppressWarnings("boxing")
			@edu.umd.cs.findbugs.annotations.SuppressFBWarnings({ "PATH_TRAVERSAL_IN",
					"PATH_TRAVERSAL_IN" })
			@Override
			public void onClick(final AjaxRequestTarget target)
			{
				final List<MagicCard> _allCardsInLibrary = HomePage.this.persistenceService
						.getAllCardsInLibraryForDeckAndPlayer(HomePage.this.session.getGameId(),
								HomePage.this.session.getPlayer().getId(), HomePage.this.session
										.getPlayer().getDeck().getDeckId());
				if (_allCardsInLibrary.isEmpty())
				{
					return;
				}

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

				final Long _gameId = HomePage.this.persistenceService
						.getPlayer(HomePage.this.session.getPlayer().getId()).getGame().getId();
				final RevealTopLibraryCardCometChannel chan = new RevealTopLibraryCardCometChannel(
						HomePage.this.session.getPlayer().getName(), _firstCard,
						HomePage.this.session.getTopCardIndex());
				final ConsoleLogStrategy logger = AbstractConsoleLogStrategy.chooseStrategy(
						ConsoleLogType.REVEAL_TOP_CARD_OF_LIBRARY, null, null, null,
						_firstCard.getTitle(), HomePage.this.session.getPlayer().getName(), null,
						HomePage.this.session.getTopCardIndex() + 1L, null, Boolean.FALSE, _gameId);
				final List<BigInteger> allPlayersInGame = HomePage.this.persistenceService
						.giveAllPlayersFromGame(_gameId);

				EventBusPostService
						.post(allPlayersInGame, chan, new ConsoleLogCometChannel(logger));
			}
		};

		revealTopLibraryCardLink.setOutputMarkupId(true).setMarkupId(id);
		this.add(revealTopLibraryCardLink);
	}

	private void generateCreateTokenLink(final String id, final ModalWindow window)
	{
		window.setInitialWidth(500);
		window.setInitialHeight(510);

		final CreateTokenModalWindow createTokenModalWindow = new CreateTokenModalWindow(
				window.getContentId(), window);
		window.setContent(createTokenModalWindow);
		window.setCssClassName(ModalWindow.CSS_CLASS_GRAY);
		window.setMaskType(ModalWindow.MaskType.SEMI_TRANSPARENT);
		window.setTitle("Create a token");
		window.setOutputMarkupId(true);
		this.setCreateTokenModalWindow(window);
		this.add(window);

		final AjaxLink<Void> createTokenLink = new AjaxLink<Void>(id)
		{
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(final AjaxRequestTarget target)
			{
				target.prependJavaScript(BattlefieldService.HIDE_MENUS);
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
				target.prependJavaScript(BattlefieldService.HIDE_MENUS);

				final Long _gameId = HomePage.this.session.getGameId();
				final CountCardsCometChannel cccc = new CountCardsCometChannel(_gameId,
						HomePage.this.session.getPlayer().getName());

				final List<BigInteger> allPlayersInGame = HomePage.this.persistenceService
						.giveAllPlayersFromGame(_gameId);
				EventBusPostService.post(allPlayersInGame, cccc);
			}
		};

		countCardsLink.setOutputMarkupId(true);
		this.add(countCardsLink);
	}

	private void generateDiscardAtRandomLink(final String id)
	{
		final AjaxLink<Void> generateDiscardAtRandomLink = new AjaxLink<Void>(id)
		{
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(final AjaxRequestTarget target)
			{
				target.prependJavaScript(BattlefieldService.HIDE_MENUS);

				final Long _gameId = HomePage.this.session.getGameId();
				final List<BigInteger> allPlayersInGame = HomePage.this.persistenceService
						.giveAllPlayersFromGame(_gameId);

				final Player playerWhoDiscards = HomePage.this.session.getPlayer();
				final Long playerWhoDiscardsDeckId = playerWhoDiscards.getDeck().getDeckId();
				final int allCardsInHand = HomePage.this.persistenceService
						.getNumberOfCardsInACertainZoneForAGameAndADeck(CardZone.HAND, _gameId,
								playerWhoDiscardsDeckId).intValue();

				if (allCardsInHand == 0)
				{
					return;
				}

				@SuppressWarnings("boxing")
				final int randomCardIndex = (allCardsInHand != 1 ? ((Double)Math.floor(Math
						.random() * allCardsInHand)).intValue() : 0);
				final List<MagicCard> allCardsInHandForAGameAndAPlayer = HomePage.this.persistenceService
						.getAllCardsInHandForAGameAndADeck(_gameId, playerWhoDiscardsDeckId);
				final MagicCard chosenCard = allCardsInHandForAGameAndAPlayer
						.remove(randomCardIndex);

				chosenCard.setZone(CardZone.GRAVEYARD);
				HomePage.this.persistenceService.updateCardWithoutMerge(chosenCard);
				HomePage.this.persistenceService
						.updateAllMagicCards(allCardsInHandForAGameAndAPlayer);

				playerWhoDiscards.setHandDisplayed(Boolean.TRUE);
				playerWhoDiscards.setGraveyardDisplayed(Boolean.TRUE);

				if (allCardsInHandForAGameAndAPlayer.isEmpty())
				{
					playerWhoDiscards.getDeck().getCards().clear();
				}

				BattlefieldService.updateHand(target);
				BattlefieldService.updateGraveyard(target);

				final NotifierCometChannel ncc = new NotifierCometChannel(
						NotifierAction.DISCARD_AT_RANDOM, null, playerWhoDiscards.getId(),
						playerWhoDiscards.getName(), playerWhoDiscards.getSide().getSideName(),
						null, chosenCard.getTitle(), null, "");
				final ConsoleLogStrategy logger = AbstractConsoleLogStrategy.chooseStrategy(
						ConsoleLogType.DISCARD_AT_RANDOM, null, null, null, chosenCard.getTitle(),
						playerWhoDiscards.getName(), null, null, null, Boolean.FALSE, _gameId);

				EventBusPostService.post(allPlayersInGame, ncc, new ConsoleLogCometChannel(logger));
			}
		};

		generateDiscardAtRandomLink.setOutputMarkupId(true);
		this.add(generateDiscardAtRandomLink);
	}

	private void generateLoginLink(final String id, final ModalWindow window)
	{
		window.setInitialWidth(300);
		window.setInitialHeight(200);
		window.setTitle("HatchetHarry login");
		window.setContent(new LoginModalWindow(window.getContentId(), window));
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
				target.prependJavaScript(BattlefieldService.HIDE_MENUS);
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
				window.setContent(new UserPreferencesModalWindow(window.getContentId(), window));
				target.prependJavaScript(BattlefieldService.HIDE_MENUS);
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
				final Long _gameId = HomePage.this.session.getGameId();

				final ConsoleLogStrategy logger = AbstractConsoleLogStrategy.chooseStrategy(
						ConsoleLogType.INSERT_DIVISION, null, null, null, null,
						HomePage.this.session.getPlayer().getName(), null, null, null, null,
						_gameId);

				final List<BigInteger> allPlayersInGame = HomePage.this.persistenceService
						.giveAllPlayersFromGame(_gameId);
				EventBusPostService.post(allPlayersInGame, new ConsoleLogCometChannel(logger));
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
				final Long _gameId = HomePage.this.session.getGameId();
				final List<BigInteger> allPlayersInGame = HomePage.this.persistenceService
						.giveAllPlayersFromGame(_gameId);

				final ConsoleLogStrategy logger = AbstractConsoleLogStrategy.chooseStrategy(
						ConsoleLogType.SHUFFLE_LIBRARY, null, null, null, null,
						HomePage.this.session.getPlayer().getName(), null, null, null, null,
						_gameId);

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

				HomePage.this.persistenceService.saveOrUpdateAllMagicCards(allCardsInLibrary);

				EventBusPostService.post(allPlayersInGame, new ConsoleLogCometChannel(logger), ncc);
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

			case PUT_CARD_TO_GRAVEYARD_FROM_BATTLEFIELD_ACTION :
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
				if (Boolean.FALSE.equals(event.isCombatInProgress()))
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
				if ("".equals(event.getTargetPlayerName()))
				{
					target.appendJavaScript("jQuery.gritter.add({ title : '"
							+ event.getPlayerName()
							+ "', text : 'reveals his (her) hand', image : 'image/logoh2.gif', sticky : false, time : ''});");
				}
				else
				{
					target.appendJavaScript("jQuery.gritter.add({ title : '"
							+ event.getTargetPlayerName() + "', text : \" stops looking at "
							+ event.getPlayerName()
							+ "'s hand\", image : 'image/logoh2.gif', sticky : false, time : ''});");
				}
				break;

			case DISCARD_AT_RANDOM :
				target.appendJavaScript("jQuery.gritter.add({ title : '" + event.getPlayerName()
						+ "', text : 'discards a card from his (her) hand at random, and it is : "
						+ event.getCardName()
						+ "', image : 'image/logoh2.gif', sticky : false, time : ''});");
				break;

			case ASK_FOR_MULLIGAN :
				target.appendJavaScript("jQuery.gritter.add({ title : '"
						+ event.getPlayerName()
						+ "', text : 'asks for a mulligan. He (she) would like to draw "
						+ event.getGameId()
						+ " card(s). Do you agree?', image : 'image/logoh2.gif', sticky : false, time : ''});");
				break;

			case OK_FOR_MULLIGAN :
				target.appendJavaScript("jQuery.gritter.add({ title : '" + event.getPlayerName()
						+ "', text : 'agrees for mulligan. " + event.getTargetPlayerName()
						+ " can draw " + event.getGameId()
						+ " card(s).', image : 'image/logoh2.gif', sticky : false, time : ''});");
				break;

			case DONE_MULLIGAN :
				target.appendJavaScript("jQuery.gritter.add({ title : '" + event.getPlayerName()
						+ "', text : 'has done mulligan. He (she) has drawn " + event.getGameId()
						+ " card(s).', image : 'image/logoh2.gif', sticky : false, time : ''});");
				break;

			case OK_FOR_MULLIGAN_BUT_ONE_LESS :
				target.appendJavaScript("jQuery.gritter.add({ title : '" + event.getPlayerName()
						+ "', text : 'agrees for mulligan with one card less. "
						+ event.getTargetPlayerName() + " can draw " + event.getGameId()
						+ " card(s).', image : 'image/logoh2.gif', sticky : false, time : ''});");
				break;

			case REFUSE_MULLIGAN :
				target.appendJavaScript("jQuery.gritter.add({ title : '"
						+ event.getPlayerName()
						+ "', text : 'disagrees for "
						+ event.getTargetPlayerName()
						+ " to do mulligan.', image : 'image/logoh2.gif', sticky : false, time : ''});");
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
			buil.append("jQuery('#card" + mc.getUuid().replace("-", "_")
					+ "').parents('.cardContainer').removeClass('tapped'); ");
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
		final DataBox db = new DataBox("dataBox", event.getGameId().longValue());
		this.getDataBoxParent().addOrReplace(db);
		db.setOutputMarkupId(true);
		target.add(this.getDataBoxParent());
	}

	@Subscribe
	public void removeCardFromBattlefield(final AjaxRequestTarget target,
			final PutToGraveyardCometChannel event)
	{
		if (event.isShouldUpdateGraveyard())
		{
			BattlefieldService.updateGraveyard(target, event.getGameId(),
					event.getTargetPlayerId(), event.getDeckId());
		}
		BattlefieldService.updateCardsAndRestoreStateInBattlefield(target, this.persistenceService,
				event.getGameId(), event.getMagicCard(), false);
		target.appendJavaScript(BattlefieldService.REACTIVATE_BATTLEFIELD_JAVASCRIPT);
	}

	@Subscribe
	public void exileCardFromBattlefield(final AjaxRequestTarget target,
			final PutToExileFromBattlefieldCometChannel event)
	{
		if (event.isShouldUpdateExile())
		{
			BattlefieldService.updateExile(target, event.getGameId(), event.getTargetPlayerId(),
					event.getDeckId());
		}

		BattlefieldService.updateCardsAndRestoreStateInBattlefield(target, this.persistenceService,
				event.getGameId(), event.getMc(), false);
		target.appendJavaScript(BattlefieldService.REACTIVATE_BATTLEFIELD_JAVASCRIPT);
	}

	@Subscribe
	public void rotateCard(final AjaxRequestTarget target, final CardRotateCometChannel event)
	{
		final MagicCard mc = event.getMc();
		mc.setTapped(event.isTapped());

		final StringBuilder buil = new StringBuilder();

		buil.append("window.setTimeout(function() { jQuery('#card");
		buil.append(event.getCardUuid().replace("-", "_"));
		if (mc.isTapped())
		{
			buil.append("').parents('.cardContainer').addClass('tapped'); }, 500); ");
		}
		else
		{
			buil.append("').parents('.cardContainer').removeClass('tapped'); }, 500); ");
		}

		target.appendJavaScript(buil.toString());
	}

	@Subscribe
	public void putToHandFromBattlefield(final AjaxRequestTarget target,
			final PutToHandFromBattlefieldCometChannel event)
	{
		if (event.isShouldUpdateHand())
		{
			BattlefieldService.updateHand(target, event.getTargetPlayerId());
		}
		BattlefieldService.updateCardsAndRestoreStateInBattlefield(target, this.persistenceService,
				event.getGameId(), event.getMc(), false);
		target.appendJavaScript(BattlefieldService.REACTIVATE_BATTLEFIELD_JAVASCRIPT);
	}

	@Subscribe
	public void playCardFromHand(final AjaxRequestTarget target,
			final PlayCardFromHandCometChannel event)
	{
		final MagicCard mc = event.getMagicCard();

		BattlefieldService.updateCardsAndRestoreStateInBattlefield(target, this.persistenceService,
				event.getGameId(), mc, true);
		target.appendJavaScript(BattlefieldService.REACTIVATE_BATTLEFIELD_JAVASCRIPT);
	}

	@Subscribe
	public void playTopLibraryCard(final AjaxRequestTarget target,
			final PlayTopLibraryCardCometChannel event)
	{
		final MagicCard mc = event.getCard();

		BattlefieldService.updateCardsAndRestoreStateInBattlefield(target, this.persistenceService,
				event.getGameId(), mc, true);
	}

	@Subscribe
	public void putTokenOnBattlefield(final AjaxRequestTarget target,
			final PutTokenOnBattlefieldCometChannel event)
	{
		final MagicCard mc = event.getMagicCard();
		mc.setX(event.getSide().getX());
		mc.setY(event.getSide().getY());

		BattlefieldService.updateCardsAndRestoreStateInBattlefield(target, this.persistenceService,
				event.getGameId(), mc, true);
	}

	@Subscribe
	public void putTopLibraryCardToHand(final AjaxRequestTarget target,
			final PutTopLibraryCardToHandCometChannel event)
	{
		if (event.getPlayerId().longValue() == this.session.getPlayer().getId().longValue())
		{
			final Player p = this.persistenceService.getPlayer(event.getPlayerId());
			p.setHandDisplayed(Boolean.TRUE);
			this.persistenceService.mergePlayer(p);
			BattlefieldService.updateHand(target, event.getPlayerId());
		}
	}

	@Subscribe
	public void putTopLibraryCardToGraveyard(final AjaxRequestTarget target,
			final PutTopLibraryCardToGraveyardCometChannel event)
	{
		if (event.getPlayerId().longValue() == this.session.getPlayer().getId().longValue())
		{
			final Player p = this.persistenceService.getPlayer(event.getPlayerId());
			p.setGraveyardDisplayed(Boolean.TRUE);
			this.persistenceService.mergePlayer(p);
			BattlefieldService.updateGraveyard(target, event.getGameId(), event.getPlayerId(),
					event.getDeckId());
		}
	}

	@Subscribe
	public void playCardFromGraveyard(final AjaxRequestTarget target,
			final PlayCardFromGraveyardCometChannel event)
	{
		final MagicCard mc = event.getMagicCard();
		BattlefieldService.updateCardsAndRestoreStateInBattlefield(target, this.persistenceService,
				event.getGameId(), mc, true);
	}

	@Subscribe
	public void revealTopLibraryCard(final AjaxRequestTarget target,
			final RevealTopLibraryCardCometChannel event)
	{
		target.prependJavaScript(BattlefieldService.HIDE_MENUS);
		target.appendJavaScript("Wicket.Window.unloadConfirmation = false;");

		this.revealTopLibraryCardWindow.setTitle("This is the top card #" + (event.getIndex() + 1L)
				+ " of " + event.getPlayerName() + "'s library: ");
		this.revealTopLibraryCardWindow.setContent(new RevealTopLibraryCardModalWindow(
				this.revealTopLibraryCardWindow.getContentId(), this.revealTopLibraryCardWindow,
				event.getCard()));

		this.allOpenRevealTopLibraryCardWindows.add(this.revealTopLibraryCardWindow);
		this.revealTopLibraryCardWindow.show(target);
	}

	@Subscribe
	public void countCards(final AjaxRequestTarget target, final CountCardsCometChannel event)
	{
		target.prependJavaScript(BattlefieldService.HIDE_MENUS);
		target.appendJavaScript("Wicket.Window.unloadConfirmation = false;");
		this.countCardsWindow.setTitle(event.getRequestingPlayerName()
				+ " asks the number of cards by zone for each player of game #" + event.getGameId()
				+ ": ");
		this.countCardsWindow.setContent(new CountCardsModalWindow(this.countCardsWindow
				.getContentId(), event.getGameId()));
		this.countCardsWindow.show(target);
	}

	@Subscribe
	public void cardZoneChange(final AjaxRequestTarget target, final CardZoneMoveCometChannel event)
	{
		final MagicCard mc = event.getCard();
		final Deck d = event.getDeck();
		final boolean isCurrentPlayerSameThanTargetedPlayer = event.getTargetPlayerId().longValue() == HatchetHarrySession
				.get().getPlayer().getId().longValue();
		final boolean isCurrentPlayerSameThanRequestingPlayer = event.getRequestingPlayerId()
				.longValue() == this.session.getPlayer().getId().longValue();

		if (!event.getTargetZone().equals(CardZone.LIBRARY))
		{
			mc.setZone(event.getTargetZone());
			this.persistenceService.updateCard(mc);
		}
		else
		{
			return;
		}

		boolean hasAlreadyDisplayedGraveyard = false;
		boolean hasAlreadyDisplayedExile = false;

		// TODO: other cases
		switch (event.getTargetZone())
		{
			case BATTLEFIELD :
				mc.setX(event.getSide().getX());
				mc.setY(event.getSide().getY());
				this.persistenceService.updateCard(mc);
				BattlefieldService.updateCardsAndRestoreStateInBattlefield(target,
						this.persistenceService, event.getGameId(), mc, true);
				break;
			case HAND :
				if (isCurrentPlayerSameThanTargetedPlayer)
				{
					BattlefieldService.updateHand(target, event.getTargetPlayerId());
				}
				break;
			case GRAVEYARD :
				if (isCurrentPlayerSameThanTargetedPlayer)
				{
					BattlefieldService.updateGraveyard(target, event.getGameId(),
							event.getTargetPlayerId(), d.getDeckId());
					hasAlreadyDisplayedGraveyard = true;
				}
				break;
			case EXILE :
				if (isCurrentPlayerSameThanTargetedPlayer)
				{
					BattlefieldService.updateExile(target, event.getGameId(),
							event.getTargetPlayerId(), d.getDeckId());
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
				if (isCurrentPlayerSameThanTargetedPlayer)
				{
					BattlefieldService.updateHand(target, d.getDeckId());
				}
				if (event.isReveal() && isCurrentPlayerSameThanRequestingPlayer)
				{
					BattlefieldService.revealHand(target, event.getGameId(),
							event.getTargetPlayerId(), d.getDeckId());
				}
				break;
			case GRAVEYARD :
				if (isCurrentPlayerSameThanRequestingPlayer && !hasAlreadyDisplayedGraveyard)
				{
					BattlefieldService.updateGraveyard(target, event.getGameId(),
							event.getTargetPlayerId(), d.getDeckId());
				}
				break;
			case EXILE :
				if (isCurrentPlayerSameThanRequestingPlayer && !hasAlreadyDisplayedExile)
				{
					BattlefieldService.updateExile(target, event.getGameId(),
							event.getTargetPlayerId(), d.getDeckId());
				}
				break;
			// $CASES-OMITTED$
			default :
				throw new UnsupportedOperationException();
		}

		target.appendJavaScript("jQuery('#putToZoneIndicator" + event.getSourceZone().toString()
				+ "').css('display', 'none');");
		target.appendJavaScript(BattlefieldService.REACTIVATE_BATTLEFIELD_JAVASCRIPT);
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
		BattlefieldService.updateCardsAndRestoreStateInBattlefield(target, this.persistenceService,
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
	public void hideHand(final AjaxRequestTarget target, final StopRevealingHandCometChannel event)
	{
		BattlefieldService.hideHand(target);
	}

	@Subscribe
	public void revealHand(final AjaxRequestTarget target, final RevealHandCometChannel event)
	{
		BattlefieldService.revealHand(target, event.getGame(), event.getPlayer(), event.getDeck());
	}

	@Subscribe
	public void askMulligan(final AjaxRequestTarget target, final AskMulliganCometChannel event)
	{
		HomePage.LOGGER.info("askMulligan");
		this.askMulliganWindow.setTitle(event.getPlayer() + " asks for mulligan");
		this.askMulliganWindow
				.setContent(new AskMulliganModalWindow(this.askMulliganWindow,
						this.askMulliganWindow.getContentId(), event.getPlayer(), event
								.getNumberOfCards()));

		target.prependJavaScript(BattlefieldService.HIDE_MENUS);
		target.appendJavaScript("Wicket.Window.unloadConfirmation = false;");
		this.askMulliganWindow.show(target);
	}

	@Subscribe
	public void reorderCardsInBattlefield(final AjaxRequestTarget target,
			final ReorderCardCometChannel event)
	{
		HomePage.LOGGER.info("reorderCardsInBattlefield");

		final List<MagicCard> allCardsAndTokensInBattlefieldForAGameAndAPlayer = this.persistenceService
				.getAllCardsAndTokensInBattlefieldForAGameAndAPlayer(event.getGameId(),
						event.getPlayerId(), event.getDeckId());

		HomePage.LOGGER.info("requesting side: " + event.getPlayerSide() + ", this side: "
				+ this.session.getPlayer().getSide().getSideName());
		final WebMarkupContainer listViewForSide2 = this
				.generateCardListViewForSide2(allCardsAndTokensInBattlefieldForAGameAndAPlayer);
		target.add(listViewForSide2);
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

	// TODO: mutualize with generateCardPanels
	private final void restoreBattlefieldState()
	{
		// Sessions must be cleaned up between server restarts, as it's too much
		// difficult
		// to manage a state recovery
		final Player player1 = this.persistenceService.getPlayer(this.session.getPlayer().getId());
		final Boolean isHandDisplayed = player1.isHandDisplayed();
		final Component galleryToUpdate = isHandDisplayed.booleanValue() ? new HandComponent(
				"gallery") : new WebMarkupContainer("gallery");

		galleryToUpdate.setOutputMarkupId(true);
		this.galleryParent.addOrReplace(galleryToUpdate);

		LOGGER.info("this.session.getGameId(): " + this.session.getGameId());
		final List<MagicCard> allCardsInBattlefield = this.persistenceService
				.getAllCardsInBattlefieldForAGame(this.session.getGameId());
		LOGGER.info("allCardsInBattlefield.size(): " + allCardsInBattlefield.size());
		final List<MagicCard> allCardsInBattlefieldForPlayer1 = new ArrayList<MagicCard>();
		final List<MagicCard> allCardsInBattlefieldForPlayer2 = new ArrayList<MagicCard>();

		for (final MagicCard mc : allCardsInBattlefield)
		{
			if (mc.getDeck().getDeckId().longValue() == this.session.getPlayer().getDeck()
					.getDeckId().longValue())
			{
				allCardsInBattlefieldForPlayer1.add(mc);
			}
			else
			{
				allCardsInBattlefieldForPlayer2.add(mc);
			}
		}

		this.generateCardListViewForSide1(allCardsInBattlefieldForPlayer1);

		this.generateCardListViewForSide2(allCardsInBattlefieldForPlayer2);
		LOGGER.info("allCardsInBattlefieldForPlayer1.size(): "
				+ allCardsInBattlefieldForPlayer1.size());
		LOGGER.info("allCardsInBattlefieldForPlayer2.size(): "
				+ allCardsInBattlefieldForPlayer2.size());

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
		final StringBuilder js = new StringBuilder("window.setTimeout(function() { ");
		final List<MagicCard> allCards = this.persistenceService
				.getAllCardsInBattlefieldForAGame(this.session.getGameId());

		for (final MagicCard mc : allCards)
		{
			try
			{
				HomePage.LOGGER.info("%%% mc: " + mc);
				HomePage.LOGGER.info("%%% mc.getToken(): " + mc.getToken());

				js.append("var card = jQuery(\"#menutoggleButton" + mc.getUuid() + "\"); "
						+ "card.css(\"position\", \"absolute\"); " + "card.css(\"left\", \""
						+ mc.getX() + "px\");" + "card.css(\"top\", \"" + mc.getY() + "px\");\n");

				final String uuidValidForJs = mc.getUuid().replace("-", "_");
				if (mc.isTapped())
				{
					js.append("jQuery('#card" + uuidValidForJs
							+ "').parents('.cardContainer').addClass('tapped'); ");
				}

				if (((mc.getToken() == null) && mc.getCounters().isEmpty())
						|| ((mc.getToken() != null) && mc.getToken().getCounters().isEmpty()))
				{
					HomePage.LOGGER.info("### bullet id=" + uuidValidForJs + " hidden");
					js.append("jQuery('#bullet" + uuidValidForJs + "').hide(); ");
				}
				else
				{
					HomePage.LOGGER.info("### bullet id=" + uuidValidForJs + " shown");
					js.append("jQuery('#bullet" + uuidValidForJs + "').show(); ");
				}
			}
			catch (final IllegalArgumentException e)
			{
				HomePage.LOGGER.error("error parsing UUID of moved card", e);
			}
		}

		final List<SidePlaceholderPanel> allSides = this.session.getMySidePlaceholder();
		HomePage.LOGGER.info("size: " + allSides.size());

		js.append(" }, 300); ");
		response.render(JavaScriptHeaderItem.forScript(js.toString(), null));
	}

	@Required
	public void setPersistenceService(final PersistenceService _persistenceService)
	{
		this.persistenceService = _persistenceService;
	}

	@Required
	public void setDataGenerator(final DataGenerator _dataGenerator)
	{
		this.dataGenerator = _dataGenerator;
	}

	public WebMarkupContainer getFirstSidePlaceholderParent()
	{
		return this.firstSidePlaceholderParent;
	}

	public WebMarkupContainer getSecondSidePlaceholderParent()
	{
		return this.secondSidePlaceholderParent;
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

	public WebMarkupContainer getOpponentParentPlaceholder()
	{
		return this.opponentParentPlaceholder;
	}

	public WebMarkupContainer generateCardListViewForSide1(
			final List<MagicCard> _allMagicCardsInBattlefieldForSide1)
	{
		if (null == _allMagicCardsInBattlefieldForSide1)
		{
			this.allMagicCardsInBattlefieldForSide1 = new ArrayList<MagicCard>();
		}
		else
		{
			this.allMagicCardsInBattlefieldForSide1 = _allMagicCardsInBattlefieldForSide1;
		}

		Collections.sort(this.allMagicCardsInBattlefieldForSide1);
		final ListDataProvider<MagicCard> data = new ListDataProvider<MagicCard>(
				this.allMagicCardsInBattlefieldForSide1);

		this.allCardsInBattlefieldForSide1 = new QuickView<MagicCard>("magicCardsForSide1", data)
		{
			private static final long serialVersionUID = 1L;

			@Override
			protected void populate(final Item<MagicCard> item)
			{
				final MagicCard mc = item.getModelObject();
				final CardPanel cp = new CardPanel("cardPanel", new Model<PlayerAndCard>(
						new PlayerAndCard(HomePage.this.player, mc)));
				cp.setOutputMarkupId(true);
				item.add(cp);

				if (mc.isTapped())
				{
					item.add(new AttributeAppender("class", new Model<String>("tapped"), " "));
				}

				HomePage.this.getParentPlaceholder().add(new ReorderCardInBattlefieldBehavior());
			}
		};
		this.allCardsInBattlefieldForSide1.setOutputMarkupId(true);
		this.parentPlaceholder.addOrReplace(this.allCardsInBattlefieldForSide1);
		this.parentPlaceholder.add(new ReorderCardInBattlefieldBehavior());
		return this.parentPlaceholder;
	}

	WebMarkupContainer generateCardListViewForSide2(
			final List<MagicCard> _allMagicCardsInBattlefieldForSide2)
	{
		if (null == _allMagicCardsInBattlefieldForSide2)
		{
			this.allMagicCardsInBattlefieldForSide2 = new ArrayList<MagicCard>();
		}
		else
		{
			this.allMagicCardsInBattlefieldForSide2 = _allMagicCardsInBattlefieldForSide2;
		}

		Collections.sort(this.allMagicCardsInBattlefieldForSide2);
		final ListDataProvider<MagicCard> data = new ListDataProvider<MagicCard>(
				this.allMagicCardsInBattlefieldForSide2);

		this.allCardsInBattlefieldForSide2 = new QuickView<MagicCard>("magicCardsForSide2", data)
		{
			private static final long serialVersionUID = 1L;

			@Override
			protected void populate(final Item<MagicCard> item)
			{
				final MagicCard mc = item.getModelObject();

				if (HomePage.this.player.getGame() == null)
				{
					HomePage.this.player.setGame(HomePage.this.persistenceService
							.getGame(HomePage.this.getAllMagicCardsInBattlefieldForSide2().get(0)
									.getGameId()));
				}

				final CardPanel cp = new CardPanel("cardPanel", new Model<PlayerAndCard>(
						new PlayerAndCard(HomePage.this.player, mc)));
				cp.setOutputMarkupId(true);
				item.add(cp);

				if (mc.isTapped())
				{
					item.add(new AttributeAppender("class", new Model<String>("tapped"), " "));
				}
			}
		};
		this.allCardsInBattlefieldForSide2.setOutputMarkupId(true);
		this.opponentParentPlaceholder.addOrReplace(this.allCardsInBattlefieldForSide2);
		return this.opponentParentPlaceholder;
	}

	public final QuickView<MagicCard> getAllCardsInBattlefieldForSide1()
	{
		return this.allCardsInBattlefieldForSide1;
	}

	public final List<MagicCard> getAllMagicCardsInBattlefieldForSide1()
	{
		return this.allMagicCardsInBattlefieldForSide1;
	}

	public final QuickView<MagicCard> getAllCardsInBattlefieldForSide2()
	{
		return this.allCardsInBattlefieldForSide2;
	}

	public final List<MagicCard> getAllMagicCardsInBattlefieldForSide2()
	{
		return this.allMagicCardsInBattlefieldForSide2;
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
				catch (final NullPointerException e)
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

	WebMarkupContainer getDrawModeParent()
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

	public WebMarkupContainer getGameIdParent()
	{
		return this.gameIdParent;
	}

	public WebMarkupContainer getConferenceParent()
	{
		return this.conferenceParent;
	}

	void setCreateTokenModalWindow(final ModalWindow window)
	{
		this.createTokenWindow = window;
	}

	public ModalWindow getCreateTokenModalWindow()
	{
		return this.createTokenWindow;
	}
}
