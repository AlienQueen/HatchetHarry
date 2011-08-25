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
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.alienlabs.hatchetharry.HatchetHarrySession;
import org.alienlabs.hatchetharry.model.MagicCard;
import org.alienlabs.hatchetharry.service.PersistenceService;
import org.alienlabs.hatchetharry.view.component.CardPanel;
import org.alienlabs.hatchetharry.view.component.ChatPanel;
import org.alienlabs.hatchetharry.view.component.ClickableGalleryImage;
import org.alienlabs.hatchetharry.view.component.ClockPanel;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.CSSPackageResource;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.resources.CompressedResourceReference;
import org.apache.wicket.markup.html.resources.JavaScriptReference;
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

	private static final Logger logger = LoggerFactory.getLogger(HomePage.class);
	private final WebMarkupContainer cardPlaceholder;
	protected final WebMarkupContainer cardParent;
	private Long gameId;

	@SpringBean
	private PersistenceService persistenceService;
	private boolean handCardHaveBeenBuilt;

	public HomePage()
	{
		this(new PageParameters());
	}

	/**
	 * Constructor that is invoked when page is invoked without a session.
	 * 
	 * @param parameters
	 *            Page parameters
	 */
	public HomePage(final PageParameters parameters)
	{
		// InjectorHolder.getInjector().inject(this);

		this.setOutputMarkupId(true);


		// Placeholders for CardPanel-adding with AjaxRequestTarget
		this.cardParent = new WebMarkupContainer("cardParent");
		this.cardParent.setOutputMarkupId(true);
		this.add(this.cardParent);
		this.cardPlaceholder = new WebMarkupContainer("cardPlaceholder");
		this.cardPlaceholder.setOutputMarkupId(true);
		this.cardParent.add(this.cardPlaceholder);

		// Resources
		this.addHeadResources();

		// Welcome message
		this.add(new Label("message", "version 0.0.3 built on Thursday, 25nd of August 2011"));

		// Comet clock channel
		this.add(new ClockPanel("clockPanel"));

		// Balduvian Horde
		final UUID balduUuid;
		MagicCard firstCardOfGame = this.persistenceService.getFirstCardOfGame();
		if (firstCardOfGame != null)
		{
			balduUuid = firstCardOfGame.getUuidObject();
			HomePage.logger.info("retrieving from db, with uuid=" + balduUuid);
		}
		else
		{
			balduUuid = UUID.randomUUID();
			firstCardOfGame = new MagicCard();
			firstCardOfGame.setBigImageFilename("image/BalduvianHorde.jpg");
			firstCardOfGame.setSmallImageFilename("image/BalduvianHorde_small.jpg");
			firstCardOfGame.setGameId(1l);
			firstCardOfGame.setUuidObject(balduUuid);
			HomePage.logger.info("new baldu");
			this.persistenceService.saveCard(firstCardOfGame);
		}
		this.setGameId(firstCardOfGame.getGameId());

		final CardPanel baldu = new CardPanel("baldu", firstCardOfGame.getSmallImageFilename(),
				firstCardOfGame.getBigImageFilename(), balduUuid);
		this.add(baldu);

		HomePage.logger.info("HP UUID: " + balduUuid);

		// Comet chat channel
		this.add(new ChatPanel("chatPanel"));

		// Hand
		this.buildHand();
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
		List<MagicCard> allCardsFromHand;

		if (HatchetHarrySession.get().getHandCardsHaveBeenBuilt())
		{
			// allCardsFromHand =
			// HatchetHarrySession.get().getFirstCardsInHand();
			allCardsFromHand = this.persistenceService.getFirstHand();
		}
		else
		{
			allCardsFromHand = this.createFirstCards();
		}

		final ListView<MagicCard> cards = new ListView<MagicCard>("handCards", allCardsFromHand)
		{
			private static final long serialVersionUID = -7874661839855866875L;

			@Override
			protected void populateItem(final ListItem<MagicCard> item)
			{
				final MagicCard card = item.getModelObject();
				item.add(new ClickableGalleryImage("handImagePlaceholder",
						HomePage.this.cardParent, card.getBigImageFilename(), card
								.getBigImageFilename(), card.getSmallImageFilename(), card
								.getTitle(), card.getDescription(), card.getId(),
						item.getIndex() + 1, item.getIndex() == 5 ? 0 : item.getIndex() + 2));
			}
		};
		this.add(cards);
		HatchetHarrySession.get().setHandCardsHaveBeenBuilt(true);
	}

	protected List<MagicCard> createFirstCards()
	{
		final ArrayList<MagicCard> cards = new ArrayList<MagicCard>();
		MagicCard c1 = new MagicCard("image/HammerOfBogardan_small.jpg",
				"image/HammerOfBogardan.jpg", "Hammer of Bogardan", "A red, reccurent nightmare");
		c1.setGameId(1l);
		c1 = this.persistenceService.saveCardByGeneratingItsUuid(c1);
		cards.add(c1);

		MagicCard c2 = new MagicCard("image/Overrun_small.jpg", "image/Overrun.jpg", "Overrun",
				"Chaaarge!");
		c2.setGameId(1l);
		c2 = this.persistenceService.saveCardByGeneratingItsUuid(c2);
		cards.add(c2);

		MagicCard c3 = new MagicCard("image/Abeyance_small.jpg", "image/Abeyance.jpg", "Abeyance",
				"A good show-stopper");
		c3.setGameId(1l);
		c3 = this.persistenceService.saveCardByGeneratingItsUuid(c3);
		cards.add(c3);

		MagicCard c4 = new MagicCard("image/TradewindRider_small.jpg", "image/TradewindRider.jpg",
				"Tradewind Rider", "Don't let it pass you by");
		c4.setGameId(1l);
		c4 = this.persistenceService.saveCardByGeneratingItsUuid(c4);
		cards.add(c4);

		MagicCard c5 = new MagicCard("image/Necropotence_small.jpg", "image/Necropotence.jpg",
				"Necropotence", "Your darkest nightmare looks bright");
		c5.setGameId(1l);
		c5 = this.persistenceService.saveCardByGeneratingItsUuid(c5);
		cards.add(c5);

		MagicCard c6 = new MagicCard("image/CursedScroll_small.jpg", "image/CursedScroll.jpg",
				"Cursed Scroll", "Close your mind to its magic, lest it pry it open in fear");
		c6.setGameId(1l);
		c6 = this.persistenceService.saveCardByGeneratingItsUuid(c6);
		cards.add(c6);

		HatchetHarrySession.get().setFirstCardsInHand(cards);
		return cards;
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
