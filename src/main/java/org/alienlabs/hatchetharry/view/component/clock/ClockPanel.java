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
package org.alienlabs.hatchetharry.view.component.clock;

import java.util.Date;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wicketstuff.push.AbstractPushEventHandler;
import org.wicketstuff.push.IPushEventContext;
import org.wicketstuff.push.IPushNode;
import org.wicketstuff.push.IPushNodeDisconnectedListener;
import org.wicketstuff.push.IPushService;
import org.wicketstuff.push.IPushServiceRef;

/**
 * Examples of chat using {@link IPushService}.
 * <p>
 * This example is abstract because it doesn't define which push service
 * implementation it uses.
 * <p>
 * The whole example doesn't depend on which implementation is used, and show
 * easy it is to switch between implementations.
 * 
 * @author Vincent Demay
 * @author Xavier Hanin
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class ClockPanel extends Panel
{
	private static final long serialVersionUID = 1L;
	ClockChannel clockChannel;
	static final Logger LOGGER = LoggerFactory.getLogger(ClockPanel.class);

	public ClockPanel(final String id, final IPushServiceRef<?> pushServiceRef)
	{
		super(id);
		this.setOutputMarkupId(true);

		final String clockChannelName = "#clock";
		this.clockChannel = ServiceLocator.getClockService().getClockChannel(clockChannelName);

		final Label clockField = new Label("clock", "clock");
		clockField.setOutputMarkupId(true);
		this.add(clockField);

		/*
		 * install push node
		 */
		final IPushNode<ClockMessage> pushNode = pushServiceRef.get().installNode(this,
				new AbstractPushEventHandler<ClockMessage>()
				{
					private static final long serialVersionUID = 1L;

					@Override
					public void onEvent(final AjaxRequestTarget target,
							final ClockMessage _message, final IPushNode<ClockMessage> node,
							final IPushEventContext<ClockMessage> ctx)
					{
						ClockPanel.LOGGER.debug("onEvent()");
						this.replaceHTML(target, clockField, ClockPanel.this._renderMessage());
					}
				});

		this.clockChannel.addListener(new IClockListener()
		{
			private static final long serialVersionUID = 1L;

			@Override
			public void onMessage(final ClockMessage msg)
			{
				if (pushServiceRef.get().isConnected(pushNode))
				{
					ClockPanel.LOGGER.debug("onMessage()");
					pushServiceRef.get().publish(pushNode, msg);
				}
				else
				{
					ClockPanel.LOGGER.debug("onMessage(): removeListener()");
					ClockPanel.this.clockChannel.removeListener(this);
					ClockPanel.this.clockChannel.addListener(this);
					pushServiceRef.get().publish(pushNode, msg);
				}
			}
		});

		/*
		 * install disconnect listener
		 */
		pushServiceRef.get().addNodeDisconnectedListener(new IPushNodeDisconnectedListener()
		{
			@Override
			public void onDisconnect(final IPushNode<?> node)
			{
				if (node.equals(pushNode))
				{
					pushServiceRef.get().removeNodeDisconnectedListener(this);
				}
			}
		});

		this.clockChannel.sendAsync();
	}

	String _renderMessage()
	{
		final String date = new Date().toString();

		return date;
	}

	public ClockMessage getClock()
	{
		return this.clockChannel.getClock();
	}
}
