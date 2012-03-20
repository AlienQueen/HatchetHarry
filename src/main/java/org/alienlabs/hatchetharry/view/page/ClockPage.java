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

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.alienlabs.hatchetharry.HatchetHarrySession;
import org.apache.wicket.Session;
import org.apache.wicket.markup.html.WebPage;
import org.atmosphere.cpr.AtmosphereResourceEvent;
import org.atmosphere.cpr.AtmosphereResourceEventListener;
import org.atmosphere.cpr.Meteor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Suspend the response using the {@link Meteor} API.
 * 
 * @author Andrey Belyaev
 * @author Jeanfrancois Arcand
 */
public class ClockPage extends WebPage implements AtmosphereResourceEventListener
{

	static final Logger LOGGER = LoggerFactory.getLogger(ClockPage.class);
	static final Map<String, Callable<String>> connectedJSessionIds = new HashMap<String, Callable<String>>();

	// TODO is this necessary??
	private final AtomicBoolean scheduleStarted = new AtomicBoolean(false);

	public ClockPage()
	{
		final HttpServletRequest req = (HttpServletRequest)this.getRequest().getContainerRequest();

		// Grap a Meteor
		final Meteor meteor = Meteor.build(req);
		final String jsessionid = req.getRequestedSessionId();

		// Start scheduling update.
		if ((!this.scheduleStarted.getAndSet(true))
				&& !(ClockPage.connectedJSessionIds.containsKey(jsessionid)))
		{
			final Callable<String> callable = new Callable<String>()
			{
				@Override
				public String call()
				{
					final String s = "1#####" + new Date().toString();
					ClockPage.LOGGER.debug(s);
					return s;
				}
			};
			ClockPage.connectedJSessionIds.put(jsessionid, callable);
			((HatchetHarrySession)Session.get()).setCometUser(jsessionid);
			meteor.schedule(callable, 5); // 5 seconds
		}

		// Add us to the listener list.
		meteor.addListener(this);

		final String transport = req.getHeader("X-Atmosphere-Transport");

		// Suspend the connection. Could be long-polling, streaming or
		// websocket.
		meteor.suspend(-1, !((transport != null) && transport.equalsIgnoreCase("long-polling")));
	}

	@Override
	public void onBroadcast(
			final AtmosphereResourceEvent<HttpServletRequest, HttpServletResponse> event)
	{
		event.getResource().getRequest().getRequestedSessionId();
		// if (!jsessionid.equals(((String)event.getMessage()).split("###")[0]))
		// {
		// If we are using long-polling, resume the connection as soon as we
		// get an event.
		final String transport = event.getResource().getRequest()
				.getHeader("X-Atmosphere-Transport");
		if ((transport != null) && transport.equalsIgnoreCase("long-polling"))
		{
			final Meteor meteor = Meteor.lookup(event.getResource().getRequest());
			meteor.removeListener(this);
			meteor.resume();
		}
		// }
	}

	@Override
	public void onSuspend(
			final AtmosphereResourceEvent<HttpServletRequest, HttpServletResponse> event)
	{
		final String transport = event.getResource().getRequest()
				.getHeader("X-Atmosphere-Transport");
		final HttpServletRequest req = event.getResource().getRequest();
		ClockPage.LOGGER.info("Suspending the %s response from ip {}:{}",
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
		ClockPage.LOGGER.info("Resuming the {} response from ip {}:{}",
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
		ClockPage.LOGGER.info("{} connection dropped from ip {}:{}",
				new Object[] { transport == null ? "websocket" : transport, req.getRemoteAddr(),
						req.getRemotePort() });
	}

	@Override
	public void onThrowable(
			final AtmosphereResourceEvent<HttpServletRequest, HttpServletResponse> event)
	{
		ClockPage.LOGGER.info("onThrowable()", event.throwable());
	}

}
