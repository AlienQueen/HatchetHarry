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
package org.alienlabs.hatchetharry.view;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.alienlabs.hatchetharry.model.MagicCard;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.CSSPackageResource;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.resources.CompressedResourceReference;
import org.apache.wicket.markup.html.resources.JavaScriptReference;
import org.atmosphere.cpr.AtmosphereResourceEvent;
import org.atmosphere.cpr.AtmosphereResourceEventListener;
import org.atmosphere.cpr.Meteor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.qos.mistletoe.wicket.TestReportPage;

import com.googlecode.wicketslides.SlidesPanel;

/**
 * Bootstrap class
 * 
 * @author Andrey Belyaev
 */
public class HomePage extends TestReportPage implements AtmosphereResourceEventListener
{

	private static final Logger logger = LoggerFactory.getLogger(HomePage.class);

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
		// Resources
		this.addHeadResources();

		// Welcome message
		this.add(new Label("message", "version 0.0.1 built on Sunday, 13th of February 2011"));

		// Hand
		this.buildHand();

		// Comet clock channel
		this.add(new ClockPanel("clockPanel"));

		// Comet chat channel
		this.add(new ChatPanel("chatPanel"));

		// Balduvian Horde
		final CardPanel baldu = new CardPanel("baldu");
		this.add(baldu);
	}

	protected void addHeadResources()
	{
		this.add(new JavaScriptReference("jQuery-1.4.4.js", HomePage.class,
				"scripts/jQuery-1.4.4.js"));
		this.add(new JavaScriptReference("qUnit.js", HomePage.class, "scripts/qunitTests/qUnit.js"));
		this.add(new JavaScriptReference("codeUnderTest.js", HomePage.class,
				"scripts/qunitTests/codeUnderTest.js"));
		this.add(new JavaScriptReference("HomePageTests.js", HomePage.class,
				"scripts/qunitTests/HomePageTests.js"));

		this.add(new JavaScriptReference("mootools.v1.11", HomePage.class,
				"scripts/mootools.v1.11.js"));
		this.add(new JavaScriptReference("jd.gallery", HomePage.class, "scripts/jd.gallery.js"));
		this.add(new JavaScriptReference("jd.gallery.set", HomePage.class,
				"scripts/jd.gallery.set.js"));
		this.add(new JavaScriptReference("jd.gallery.transitions", HomePage.class,
				"scripts/jd.gallery.transitions.js"));
		this.add(new JavaScriptReference("History", HomePage.class, "scripts/HistoryManager.js"));

		this.add(CSSPackageResource.getHeaderContribution(new CompressedResourceReference(
				HomePage.class, "stylesheets/menu.css")));
	}

	protected void buildHand()
	{
		final SlidesPanel.Builder builder = new SlidesPanel.Builder("gallery");

		final MagicCard hammerOfBogardan = new MagicCard("cards/HammerOfBogardan.jpg",
				"Hammer of Bogardan", "A red, reccurent nightmare");
		final MagicCard overrun = new MagicCard("cards/Overrun.jpg", "Overrun", "Chaaarge!");
		final MagicCard abeyance = new MagicCard("cards/Abeyance.jpg", "Abeyance",
				"A definitive show-stopper");
		final MagicCard tradewindRider = new MagicCard("cards/TradewindRider.jpg",
				"Tradewind Rider", "Don't let him pass you by");
		final MagicCard necropotence = new MagicCard("cards/Necropotence.jpg", "Necropotence",
				"Your darkest nightmare looks bright");
		final MagicCard cursedScroll = new MagicCard("cards/CursedScroll.jpg", "Cursed Scroll",
				"Close your mind to its magic, lest it pry it open in fear");

		builder.addImage(hammerOfBogardan, hammerOfBogardan);
		builder.addImage(overrun, overrun);
		builder.addImage(abeyance, abeyance);
		builder.addImage(tradewindRider, tradewindRider);
		builder.addImage(necropotence, necropotence);
		builder.addImage(cursedScroll, cursedScroll);

		this.add(builder.timed(true).delay(10000).fadeDuration(500).showArrow(true).size(226, 320)
				.showThumbs(true).thumbSize(23, 32).historyManager(true).build());
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
		final Locale originalLocale = this.getSession().getLocale();
		this.getSession().setLocale(Locale.ENGLISH);
		super.configureResponse();

		final String encoding = "text/html;charset=utf-8";

		this.getResponse().setContentType(encoding);
		this.getSession().setLocale(originalLocale);
	}

}
