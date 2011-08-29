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
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.alienlabs.hatchetharry.HatchetHarrySession;
import org.alienlabs.hatchetharry.model.Deck;
import org.alienlabs.hatchetharry.model.MagicCard;
import org.alienlabs.hatchetharry.model.Player;
import org.alienlabs.hatchetharry.service.PersistenceService;
import org.alienlabs.hatchetharry.view.component.AboutModalWindow;
import org.alienlabs.hatchetharry.view.component.CardPanel;
import org.alienlabs.hatchetharry.view.component.ChatPanel;
import org.alienlabs.hatchetharry.view.component.ClockPanel;
import org.alienlabs.hatchetharry.view.component.PlayCardFromHandBehavior;
import org.alienlabs.hatchetharry.view.component.TeamInfoModalWindow;
import org.apache.wicket.ResourceReference;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxFallbackLink;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.CSSPackageResource;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.resources.CompressedResourceReference;
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

	static final Logger logger = LoggerFactory.getLogger(HomePage.class);
	protected WebMarkupContainer cardParent;
	private Long gameId;

	@SpringBean
	PersistenceService persistenceService;
	ModalWindow teamInfoWindow;
	ModalWindow aboutWindow;

	Player player;
	Deck deck;
	BookmarkablePageLink<PlayCardPage> playCardPage;
	List<MagicCard> hand;
	private WebMarkupContainer parentPlaceholder;
	AjaxFallbackLink<MagicCard> playCardLink;

	public HomePage()
	{
		// InjectorHolder.getInjector().inject(this);

		this.setOutputMarkupId(true);

		this.createPlayer();

		// Resources
		this.addHeadResources();

		// Placeholders for CardPanel-adding with AjaxRequestTarget
		this.createCardPanelPlaceholders();

		// Welcome message
		this.add(new Label("message", "version 0.0.3 built on Sunday, 28th of August 2011"));

		// Comet clock channel
		this.add(new ClockPanel("clockPanel"));

		// Comet chat channel
		this.add(new ChatPanel("chatPanel"));

		// Hand
		if (HatchetHarrySession.get().getHandHasBeenCreated())
		{
			this.hand = HatchetHarrySession.get().getFirstCardsInHand();
		}
		else
		{
			this.hand = this.createFirstCards();
		}

		this.buildHand();
		this.generateAboutLink();
		this.generateTemInfoLink();
		this.generatePlayCardLink(this.hand.get(0));
		this.generatePlayCardsBehaviorsForAllOpponents();
	}

	@SuppressWarnings("unchecked")
	private void generatePlayCardsBehaviorsForAnOpponent(final Long opponentId)
	{
		final List<MagicCard> list = new ArrayList<MagicCard>();
		list.addAll((List<MagicCard>)this.persistenceService.getCardsByDeckId(opponentId));

		final ListView<MagicCard> allCards = new ListView<MagicCard>("playCardParent" + opponentId,
				list)
		{
			private static final long serialVersionUID = 12981489148949L;

			@Override
			protected void populateItem(final ListItem<MagicCard> item)
			{
				final MagicCard card = (MagicCard)item.getDefaultModelObject();
				final WebMarkupContainer cardPlaceholder = new WebMarkupContainer("cardPlaceholder"
						+ opponentId);
				final PlayCardFromHandBehavior behavior = new PlayCardFromHandBehavior(
						card.getUuidObject(), HomePage.this.cardParent, item.getIndex(),
						(item.getIndex() == 6 ? 0 : item.getIndex() + 1));
				cardPlaceholder.add(behavior);
				item.add(cardPlaceholder);
			}
		};
		HomePage.this.cardParent.add(allCards);
	}

	private void generatePlayCardsBehaviorsForAllOpponents()
	{
		this.generatePlayCardsBehaviorsForAnOpponent(1l);
		this.generatePlayCardsBehaviorsForAnOpponent(2l);
	}

	private void generatePlayCardLink(final MagicCard mc)
	{
		final WebMarkupContainer playCardPlaceholder = new WebMarkupContainer("playCardPlaceholder");
		playCardPlaceholder.setMarkupId("playCardPlaceholder0");
		playCardPlaceholder.setOutputMarkupId(true);
		HomePage.logger.info("Generating link");

		this.playCardLink = new AjaxFallbackLink<MagicCard>("playCardLink")
		{
			private static final long serialVersionUID = 6590465665519989765L;
			private CardPanel cp;

			@Override
			public void onClick(final AjaxRequestTarget target)
			{
				final ServletWebRequest servletWebRequest = (ServletWebRequest)target.getPage()
						.getRequest();
				final HttpServletRequest request = servletWebRequest.getHttpServletRequest();
				final String jsessionid = request.getRequestedSessionId();
				final String uuidToLookFor = mc.getUuid();

				HatchetHarrySession.get().addCardIdInHand(0, 0);

				final MagicCard card = HomePage.this.persistenceService.getCardFromUuid(UUID
						.fromString(uuidToLookFor));

				if (null != card)
				{
					HomePage.logger.info("card!");
					this.cp = new CardPanel("cardPlaceholder3", card.getSmallImageFilename(),
							card.getBigImageFilename(), UUID.fromString(uuidToLookFor));
					this.cp.setOutputMarkupId(true);

					this.cp.add(new PlayCardFromHandBehavior(UUID.fromString(uuidToLookFor),
							HomePage.this.cardParent, 0, 1));
				}
				else
				{
					HomePage.logger.info("null!");
				}


				final String message = jsessionid + "~~~" + uuidToLookFor + "~~~" + 1;
				HomePage.logger.info(message);

				final String stop = request.getParameter("stop");
				if (!"true".equals(stop))
				{
					HomePage.logger.info("continue!");
					final Meteor meteor = Meteor.build(request, new LinkedList<BroadcastFilter>(),
							null);
					meteor.addListener((AtmosphereResourceEventListener)target.getPage());
					meteor.broadcast(message);
				}
				else
				{
					HomePage.logger.info("stop!");
				}

				HomePage.this.cardParent.addOrReplace(this.cp);
				target.addComponent(HomePage.this.cardParent);
				target.appendJavascript("click" + uuidToLookFor.replace("-", "_") + "("
						+ UUID.fromString(uuidToLookFor) + ")");
			}

		};

		this.playCardLink.setMarkupId("playCardLink0");
		this.playCardLink.setOutputMarkupId(true);
		playCardPlaceholder.add(this.playCardLink);

		this.add(playCardPlaceholder);
	}

	@SuppressWarnings("unchecked")
	protected void createPlayer()
	{
		final ServletWebRequest servletWebRequest = (ServletWebRequest)this.getRequest();
		final HttpServletRequest request = servletWebRequest.getHttpServletRequest();
		final String jsessionid = request.getRequestedSessionId();

		if (!HatchetHarrySession.get().isPlayerCreated())
		{
			this.player = new Player();
			this.player.setLifePoints(20l);
			this.player.setSide("infrared");
			this.player.setName("infrared");
			this.player.setJsessionid(jsessionid);
			this.player.setGameId(1l);
			this.player.setId(this.persistenceService.savePlayer(this.player));
			HatchetHarrySession.get().setPlayerHasBeenCreated();
			HatchetHarrySession.get().setPlayer(this.player);

			this.deck = this.persistenceService.getDeck(this.player.getId());
			this.deck.setCards((List<MagicCard>)this.persistenceService
					.getAllCardsFromDeck(this.player.getId()));
			this.deck.setCards(this.deck.shuffleLibrary());
			this.deck.setPlayerId(this.player.getId());
			this.deck.setId(this.player.getId());
			this.deck = this.persistenceService.saveDeck(this.deck);
		}
		else
		{
			this.player = HatchetHarrySession.get().getPlayer();
			this.player.setLifePoints(20l);
			this.player.setSide("ultraviolet");
			this.player.setName("ultraviolet");
			this.player.setJsessionid(jsessionid);
			this.player.setGameId(1l);
			this.persistenceService.saveOrUpdatePlayer(this.player);
			HatchetHarrySession.get().setPlayerHasBeenCreated();
			HatchetHarrySession.get().setPlayer(this.player);

			// this.deck = new Deck();
			this.deck = this.persistenceService.getDeck(this.player.getId());
			this.deck.setCards((List<MagicCard>)this.persistenceService
					.getAllCardsFromDeck(this.player.getId()));
			this.deck.setCards(this.deck.shuffleLibrary());
			this.deck.setPlayerId(this.player.getId());
			this.deck = this.persistenceService.saveDeck(this.deck);
		}
	}

	protected void createCardPanelPlaceholders()
	{
		this.cardParent = new WebMarkupContainer("cardParent");
		this.cardParent.setOutputMarkupId(true);
		final WebMarkupContainer cardPlaceholder = new WebMarkupContainer("cardPlaceholder");
		final WebMarkupContainer cardPlaceholder3 = new WebMarkupContainer("cardPlaceholder3");
		this.setOutputMarkupId(true);
		this.add(this.cardParent);
		this.cardParent.add(cardPlaceholder3);
		this.setParentPlaceholder(this.cardParent);
		this.add(cardPlaceholder);
	}

	private void setParentPlaceholder(final WebMarkupContainer _cardParent)
	{
		this.parentPlaceholder = _cardParent;
	}

	WebMarkupContainer getParentPlaceholder()
	{
		return this.parentPlaceholder;
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

	protected void buildHand()
	{

		this.playCardPage = new BookmarkablePageLink<PlayCardPage>("playCardPage",
				PlayCardPage.class);
		this.add(this.playCardPage);

		final ListView<MagicCard> cards = new ListView<MagicCard>("handCards", HatchetHarrySession
				.get().getFirstCardsInHand())
		{
			private static final long serialVersionUID = -7874661839855866875L;

			@Override
			protected void populateItem(final ListItem<MagicCard> item)
			{
				HatchetHarrySession.get().addCardIdInHand(item.getIndex(), item.getIndex());
				final MagicCard card = item.getModelObject();

				final WebMarkupContainer wrapper = new WebMarkupContainer("wrapper");
				wrapper.setMarkupId("wrapper" + item.getIndex());
				wrapper.setOutputMarkupId(true);

				final Image handImagePlaceholder = new Image("handImagePlaceholder",
						new ResourceReference(HomePage.class, card.getBigImageFilename()));
				handImagePlaceholder.setMarkupId("placeholder" + card.getUuid().replace("-", "_"));
				handImagePlaceholder.setOutputMarkupId(true);

				final PlayCardFromHandBehavior b = new PlayCardFromHandBehavior(
						card.getUuidObject(), HomePage.this.cardParent, item.getIndex(),
						(item.getIndex() == 6 ? 0 : item.getIndex() + 1));
				handImagePlaceholder.add(b);

				final Label titlePlaceholder = new Label("titlePlaceholder", card.getTitle());
				titlePlaceholder.setMarkupId("placeholder" + card.getUuid().replace("-", "_")
						+ "_placeholder");
				titlePlaceholder.setOutputMarkupId(true);


				HomePage.logger.info("###### cardTitle: " + card.getTitle() + ", uuid: "
						+ card.getUuid());

				wrapper.add(handImagePlaceholder, titlePlaceholder);
				item.add(wrapper);

			}
		};
		// cards.setRenderBodyOnly(false);
		this.add(cards);

		final ListView<MagicCard> thumbs = new ListView<MagicCard>("thumbs", HatchetHarrySession
				.get().getFirstCardsInHand())
		{
			private static final long serialVersionUID = -787466183866875L;

			@Override
			protected void populateItem(final ListItem<MagicCard> item)
			{
				HomePage.logger.info("###### item.getIndex(): " + item.getIndex());
				final MagicCard card = item.getModelObject();

				final WebMarkupContainer crossLinkDiv = new WebMarkupContainer("crossLinkDiv");
				crossLinkDiv.setMarkupId("cross-link-div" + item.getIndex());
				crossLinkDiv.setOutputMarkupId(true);

				final WebMarkupContainer crossLink = new WebMarkupContainer("crossLink");
				crossLink.setMarkupId("cross-link" + item.getIndex());
				crossLink.setOutputMarkupId(true);

				final Image thumb = new Image("thumbPlaceholder", new ResourceReference(
						HomePage.class, card.getThumbnailFilename()));
				thumb.setMarkupId("placeholder" + card.getUuid().replace("-", "_") + "_img");
				thumb.setOutputMarkupId(true);

				HomePage.logger.info("###### thumbTitle: " + card.getThumb() + ", uuid: "
						+ card.getUuid());

				crossLink.add(thumb);
				crossLinkDiv.add(crossLink);
				item.add(crossLinkDiv);
			}
		};

		this.add(thumbs);
		HatchetHarrySession.get().setHandCardsHaveBeenBuilt(true);
	}

	@SuppressWarnings("unchecked")
	protected List<MagicCard> createFirstCards()
	{
		this.player = HatchetHarrySession.get().getPlayer();
		this.deck = this.persistenceService.getDeck(this.player.getId());
		this.deck.setCards((List<MagicCard>)this.persistenceService.getAllCardsFromDeck(this.deck
				.getId()));
		if (!HatchetHarrySession.get().getHandCardsHaveBeenBuilt())
		{
			this.deck.shuffleLibrary();
		}
		final List<MagicCard> cards = new ArrayList<MagicCard>();

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

	protected void generateTemInfoLink()
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

	public Long getGameId()
	{
		return this.gameId;
	}

	public void setGameId(final Long _gameId)
	{
		this.gameId = _gameId;
	}

}
