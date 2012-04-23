/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.alienlabs.hatchetharry.view.component.joingame;

import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.alienlabs.hatchetharry.HatchetHarrySession;
import org.alienlabs.hatchetharry.view.component.SidePlaceholderMoveBehavior;
import org.alienlabs.hatchetharry.view.component.SidePlaceholderPanel;
import org.alienlabs.hatchetharry.view.component.UpdateDataBoxBehavior;
import org.alienlabs.hatchetharry.view.component.databox.DataBox;
import org.alienlabs.hatchetharry.view.page.HomePage;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.protocol.http.servlet.ServletWebRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wicketstuff.push.AbstractPushEventHandler;
import org.wicketstuff.push.IPushChannel;
import org.wicketstuff.push.IPushEventContext;
import org.wicketstuff.push.IPushNode;
import org.wicketstuff.push.IPushNodeDisconnectedListener;
import org.wicketstuff.push.cometd.CometdPushService;

public class JoinGamePanel extends Panel
{
	private static final long serialVersionUID = 1L;
	static final Logger LOGGER = LoggerFactory.getLogger(JoinGamePanel.class);

	final UUID uuid;
	final String jsessionid;
	final WebMarkupContainer parent;
	final String side;
	final HomePage homePage;
	final WebMarkupContainer dataBoxParent;
	final Long gameId;
	JoinGameChannel joinGameChannel;

	public JoinGamePanel(final String id, final CometdPushService pushServiceRef,
			final WebMarkupContainer _parent, final UUID _uuid, final String _jsessionid,
			final HomePage hp, final String _side, final WebMarkupContainer _dataBoxParent,
			final Long _gameId)
	{
		super(id);

		this.parent = _parent;
		this.uuid = _uuid;
		this.jsessionid = _jsessionid;
		this.homePage = hp;
		this.side = _side;
		this.dataBoxParent = _dataBoxParent;
		this.gameId = _gameId;

		final String joinGameChannelName = "joinGame";
		this.joinGameChannel = JoinGameServiceLocator.getJoinGameService().getJoinGameChannel(
				joinGameChannelName);

		final IPushChannel<JoinGameMessage> channel = pushServiceRef
				.createChannel(joinGameChannelName);

		/*
		 * install push node
		 */
		final IPushNode<JoinGameMessage> pushNode = pushServiceRef.installNode(this,
				new AbstractPushEventHandler<JoinGameMessage>()
				{
					private static final long serialVersionUID = 1L;

					@Override
					public void onEvent(final AjaxRequestTarget target,
							final JoinGameMessage _message, final IPushNode<JoinGameMessage> node,
							final IPushEventContext<JoinGameMessage> ctx)
					{
						final SidePlaceholderPanel spp = new SidePlaceholderPanel(
								"secondSidePlaceholder", JoinGamePanel.this.side,
								JoinGamePanel.this.homePage, JoinGamePanel.this.uuid);
						spp.add(new SidePlaceholderMoveBehavior(spp, JoinGamePanel.this.uuid,
								JoinGamePanel.this.jsessionid, JoinGamePanel.this.homePage, _side,
								JoinGamePanel.this.homePage.getDataBoxParent(),
								JoinGamePanel.this.gameId));
						spp.setOutputMarkupId(true);
						JoinGamePanel.LOGGER.info("### gameId: " + JoinGamePanel.this.gameId);

						final HatchetHarrySession session = HatchetHarrySession.get();
						session.putMySidePlaceholderInSesion(_side);


						JoinGamePanel.this.homePage.getSecondSidePlaceholderParent().addOrReplace(
								spp);
						target.add(JoinGamePanel.this.homePage.getSecondSidePlaceholderParent());

						JoinGamePanel.LOGGER.info("### " + JoinGamePanel.this.uuid);
						final int posX = ("infrared".equals(_side)) ? 300 : 900;

						target.appendJavaScript("jQuery(document).ready(function() { var card = jQuery('#sidePlaceholder"
								+ JoinGamePanel.this.uuid
								+ "'); "
								+ "card.css('position', 'absolute'); "
								+ "card.css('left', '"
								+ posX + "px'); " + "card.css('top', '500px'); });");

						spp.setPosX(posX);
						spp.setPosY(500);
						session.setMySidePlaceholder(spp);

						final ServletWebRequest servletWebRequest = (ServletWebRequest)JoinGamePanel.this.parent
								.getRequest();
						final HttpServletRequest request = servletWebRequest.getContainerRequest();

						if (!JoinGamePanel.this.jsessionid.equals(request
								.getParameter("requestingId")))
						{
							final DataBox dataBox = new DataBox("dataBox",
									JoinGamePanel.this.gameId, JoinGamePanel.this.homePage);
							final UpdateDataBoxBehavior behavior = new UpdateDataBoxBehavior(
									JoinGamePanel.this.gameId, JoinGamePanel.this.homePage, dataBox);
							dataBox.setOutputMarkupId(true);
							dataBox.add(behavior);

							final WebMarkupContainer newDataBoxParent = JoinGamePanel.this.homePage
									.getDataBoxParent();
							newDataBoxParent.addOrReplace(dataBox);
							target.add(newDataBoxParent);
							JoinGamePanel.LOGGER.info("# databox for game id="
									+ JoinGamePanel.this.gameId);
						}

						// this.appendHTML(target, chatHistoryField,
						// JoinGamePanel.this._renderMessage(_message));
					}
				}, joinGameChannelName);

		/*
		 * connect to chat room
		 */
		this.joinGameChannel.addListener(new IJoinGameListener()
		{
			private static final long serialVersionUID = 1L;

			@Override
			public void onMessage(final JoinGameMessage msg)
			{
				if (pushServiceRef.isConnected(pushNode))
				{
					JoinGamePanel.LOGGER.info("### onMessage()");
					pushServiceRef.publish(pushNode, msg);
				}
				else
				{
					JoinGamePanel.LOGGER.info("onMessage(): removeListener()");
					JoinGamePanel.this.joinGameChannel.removeListener(this);
					JoinGamePanel.this.joinGameChannel.addListener(this);
					pushServiceRef.publish(pushNode, msg);
				}
			}
		});

		/*
		 * install disconnect listener
		 */
		pushServiceRef.addNodeDisconnectedListener(new IPushNodeDisconnectedListener()
		{
			@Override
			public void onDisconnect(final IPushNode<?> node)
			{
				if (node.equals(pushNode))
				{
					pushServiceRef.removeNodeDisconnectedListener(this);
				}
			}
		});

		pushServiceRef.connectToChannel(pushNode, channel);
	}
}
