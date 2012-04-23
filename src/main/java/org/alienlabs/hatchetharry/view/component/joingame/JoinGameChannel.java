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

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArraySet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class JoinGameChannel
{
	private static final Logger LOGGER = LoggerFactory.getLogger(JoinGameChannel.class);

	final Set<IJoinGameListener> listeners = new CopyOnWriteArraySet<IJoinGameListener>();

	private final String name;

	JoinGameChannel(final String _name)
	{
		this.name = _name;
	}

	void _notify(final IJoinGameListener listener, final JoinGameMessage joinGameMessage)
	{
		try
		{
			JoinGameChannel.LOGGER.info("_notify()");
			listener.onMessage(joinGameMessage);
		}
		catch (final Exception ex)
		{
			JoinGameChannel.LOGGER.error("Failed to notify join game listener " + listener, ex);
		}
	}

	public void addListener(final IJoinGameListener listener)
	{
		synchronized (this.listeners)
		{
			this.listeners.add(listener);
		}
	}

	public String getName()
	{
		return this.name;
	}

	public void removeListener(final IJoinGameListener listener)
	{
		synchronized (this.listeners)
		{
			this.listeners.remove(listener);
		}
	}

	/**
	 * sends the join game message asynchronously (in a background thread) to
	 * all listeners
	 */
	public void sendAsync(final String _side, final String _jsessionid, final UUID _sideUuid)
	{
		final JoinGameMessage msg = new JoinGameMessage(_side, _jsessionid, _sideUuid);
		for (final IJoinGameListener listener : JoinGameChannel.this.listeners)
		{
			JoinGameChannel.this._notify(listener, msg);
		}
	}
}
