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
package org.alienlabs.hatchetharry.view.component.chat;

import java.io.Serializable;
import java.util.Date;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class Message implements Serializable
{
	private static final long serialVersionUID = 1L;

	private final Date date = new Date();
	private final String chatroomName;
	private final String user;
	private final String message;

	Message(final String _user, final String _message, final String chatroom)
	{
		this.user = _user;
		this.message = _message;
		this.chatroomName = chatroom;
	}

	public String getChatroomName()
	{
		return this.chatroomName;
	}

	public Date getDate()
	{
		return (Date)this.date.clone();
	}

	public String getMessage()
	{
		return this.message;
	}

	public String getUser()
	{
		return this.user;
	}

	@Override
	public String toString()
	{
		return this.user + " said " + this.message;
	}
}