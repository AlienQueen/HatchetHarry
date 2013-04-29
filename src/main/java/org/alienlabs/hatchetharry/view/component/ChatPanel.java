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
package org.alienlabs.hatchetharry.view.component;

import java.math.BigInteger;
import java.util.List;

import org.alienlabs.hatchetharry.HatchetHarryApplication;
import org.alienlabs.hatchetharry.HatchetHarrySession;
import org.alienlabs.hatchetharry.model.channel.ChatCometChannel;
import org.alienlabs.hatchetharry.service.PersistenceService;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.atmosphere.Subscribe;
import org.apache.wicket.injection.Injector;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;


/**
 * Simple panel.
 * 
 * @author Andrey Belyaev
 */
public class ChatPanel extends Panel
{
	private static final long serialVersionUID = 1L;

	@SpringBean
	PersistenceService persistenceService;

	static final Logger LOGGER = LoggerFactory.getLogger(ChatPanel.class);

	public ChatPanel(final String id, final Long _playerId)
	{
		super(id);
		Injector.get().inject(this);

		final Form<String> form = new Form<String>("chatForm");

		final RequiredTextField<String> user = new RequiredTextField<String>("user",
				new Model<String>(""));
		user.setMarkupId("user" + _playerId);
		user.setOutputMarkupId(true);
		form.add(user);
		final RequiredTextField<String> message = new RequiredTextField<String>("message",
				new Model<String>(""));
		message.setOutputMarkupId(true).setMarkupId("message");
		form.add(message);

		final AjaxButton button = new AjaxButton("submit")
		{
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit(final AjaxRequestTarget target, final Form<?> _form)
			{
				ChatPanel.LOGGER.info("submit");

				final String _user = _form.get("user").getDefaultModelObjectAsString();
				final String _message = _form.get("message").getDefaultModelObjectAsString();
				ChatPanel.LOGGER.info("user: " + _user + ", message: " + _message);

				final String chatMessage = _user + " said: " + _message;

				final Long gameId = ChatPanel.this.persistenceService
						.getPlayer(HatchetHarrySession.get().getPlayer().getId()).getGame().getId();
				final List<BigInteger> allPlayersInGame = ChatPanel.this.persistenceService
						.giveAllPlayersFromGame(gameId);

				for (int i = 0; i < allPlayersInGame.size(); i++)
				{
					final Long playerToWhomToSend = allPlayersInGame.get(i).longValue();
					final String pageUuid = HatchetHarryApplication.getCometResources().get(
							playerToWhomToSend);

					final ChatCometChannel ccc = new ChatCometChannel(gameId, chatMessage);

					HatchetHarryApplication.get().getEventBus().post(ccc, pageUuid);
				}

			}

			@Override
			protected void onError(final AjaxRequestTarget target, final Form<?> _form)
			{
			}
		};
		form.add(button);
		this.add(form);
	}

	@Subscribe
	public void updateChat(final AjaxRequestTarget target, final ChatCometChannel event)
	{
		target.appendJavaScript("document.getElementById('message').value=''; ");
		target.appendJavaScript("var text = ['! ', '', '! ', '', '! ', '']; var index = 0; var showChatNotif = function() { if (index < 6) { window.setTimeout(function() { Notificon(text[index++], '/favicon.ico'); showChatNotif(); }, 500); } }; showChatNotif(); ");

		target.appendJavaScript("var chatPanel = document.getElementById('chat'); chatPanel.innerHTML = chatPanel.innerHTML + \"&#013;&#010;\" + \""
				+ event.getMessage()
				+ "\" + \"&#013;&#010;\"; chatPanel.scrollTop = chatPanel.scrollHeight; ");
	}

	@Required
	public void setPersistenceService(final PersistenceService _persistenceService)
	{
		this.persistenceService = _persistenceService;
	}

}
