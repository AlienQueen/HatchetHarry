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

import java.io.Serializable;
import java.util.UUID;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class JoinGameMessage implements Serializable
{
	private static final long serialVersionUID = 1L;

	private final String side;
	private final String jsessionid;
	private final UUID sideUuid;

	JoinGameMessage(final String _side, final String _jsessionid, final UUID _sideUuid)
	{
		this.side = _side;
		this.jsessionid = _jsessionid;
		this.sideUuid = _sideUuid;
	}

	public String getMessage()
	{
		return this.side + "|||||" + this.jsessionid + "|||||" + this.sideUuid.toString();
	}

	@Override
	public String toString()
	{
		return this.getMessage();
	}
}