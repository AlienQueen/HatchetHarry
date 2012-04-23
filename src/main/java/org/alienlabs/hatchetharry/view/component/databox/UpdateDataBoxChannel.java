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
package org.alienlabs.hatchetharry.view.component.databox;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class UpdateDataBoxChannel
{
	private static final Logger LOG = LoggerFactory.getLogger(UpdateDataBoxChannel.class);

	final Set<IUpdateDataBoxListener> listeners = new CopyOnWriteArraySet<IUpdateDataBoxListener>();

	private final String name;

	UpdateDataBoxChannel(final String _name)
	{
		this.name = _name;
	}

	void _notify(final IUpdateDataBoxListener listener,
			final UpdateDataBoxMessage updateDataBoxMessage)
	{
		try
		{
			listener.onMessage(updateDataBoxMessage);
		}
		catch (final Exception ex)
		{
			UpdateDataBoxChannel.LOG.error("Failed to notify chat listener " + listener, ex);
		}
	}

	public void addListener(final IUpdateDataBoxListener listener)
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

	public void removeListener(final IUpdateDataBoxListener listener)
	{
		synchronized (this.listeners)
		{
			this.listeners.remove(listener);
		}
	}

	/**
	 * sends the chat message asynchronously (in a background thread) to all
	 * listeners
	 */
	public void sendAsync(final String user, final String message)
	{
		final UpdateDataBoxMessage msg = new UpdateDataBoxMessage(user, message, this.name);

		for (final IUpdateDataBoxListener listener : UpdateDataBoxChannel.this.listeners)
		{
			UpdateDataBoxChannel.this._notify(listener, msg);
		}
	}
}
