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

import java.io.Serializable;
import java.util.Date;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class ClockChannel implements Serializable
{
	private static final long serialVersionUID = 1L;

	private static final Logger LOGGER = LoggerFactory.getLogger(ClockChannel.class);

	private static final ScheduledExecutorService executorService = Executors
			.newScheduledThreadPool(5);

	final Set<IClockListener> listeners = new CopyOnWriteArraySet<IClockListener>();

	private ClockMessage clock = new ClockMessage();

	void _notify(final IClockListener listener, final ClockMessage message)
	{
		try
		{
			ClockChannel.LOGGER.debug("_notify()");
			listener.onMessage(message);

		}
		catch (final Exception ex)
		{
			ClockChannel.LOGGER.error("Failed to notify clock listener " + listener, ex);
		}
	}

	public void addListener(final IClockListener listener)
	{
		synchronized (this.listeners)
		{
			this.listeners.add(listener);
		}
	}

	public Date getDate()
	{
		return new Date();
	}

	public void removeListener(final IClockListener listener)
	{
		synchronized (this.listeners)
		{
			this.listeners.remove(listener);
		}
	}

	/**
	 * sends the clock message asynchronously (in a background thread) to all
	 * listeners
	 */
	public void sendAsync()
	{
		ClockChannel.executorService.scheduleAtFixedRate(new Runnable()
		{
			@Override
			public void run()
			{
				for (final IClockListener listener : ClockChannel.this.listeners)
				{
					ClockChannel.this.setClock(new ClockMessage());
					ClockChannel.this._notify(listener, ClockChannel.this.getClock());
				}
			}
		}, 100, 2000, TimeUnit.MILLISECONDS);
	}

	public ClockMessage getClock()
	{
		return this.clock;
	}

	public void setClock(final ClockMessage _clock)
	{
		this.clock = _clock;
	}

}
