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

import java.util.LinkedList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.alienlabs.hatchetharry.view.page.ChatPage;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.protocol.http.servlet.ServletWebRequest;
import org.atmosphere.cpr.BroadcastFilter;
import org.atmosphere.cpr.Meteor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Simple panel.
 * 
 * @author Andrey Belyaev
 */
@SuppressWarnings("serial")
public class ChatPanel extends Panel
{
	static final Logger logger = LoggerFactory.getLogger(ChatPanel.class);

	final List<BroadcastFilter> list;

	public ChatPanel(final String id, final Long _playerId)
	{
		super(id);

		this.add(new BookmarkablePageLink<ChatPage>("chatStart", ChatPage.class));

		this.list = new LinkedList<BroadcastFilter>();

		final Form<String> form = new Form<String>("chatForm");
		final RequiredTextField<String> user = new RequiredTextField<String>("user",
				new Model<String>(""));
		user.setMarkupId("user" + _playerId);
		user.setOutputMarkupId(true);
		form.add(user);
		final RequiredTextField<String> message = new RequiredTextField<String>("message",
				new Model<String>(""));
		form.add(message);
		final AjaxButton button = new AjaxButton("submit")
		{
			@Override
			protected void onSubmit(final AjaxRequestTarget target, final Form<?> _form)
			{
				ChatPanel.logger.info("submit");
				final ServletWebRequest servletWebRequest = (ServletWebRequest)this.getRequest();
				final HttpServletRequest request = servletWebRequest.getHttpServletRequest();
				final String jsessionid = request.getRequestedSessionId();

				final Meteor meteor = Meteor.build(request, ChatPanel.this.list, null);
				ChatPanel.logger.info("meteor: " + meteor);

				final String _user = _form.get("user").getDefaultModelObjectAsString();
				final String _message = _form.get("message").getDefaultModelObjectAsString();
				ChatPanel.logger.info("user: " + _user + ", message: " + _message);
				meteor.broadcast(jsessionid + "###" + _user + " said: " + _message);
			}
		};
		form.add(button);
		this.add(form);
	}
}