package org.alienlabs.hatchetharry;

import org.apache.wicket.Request;
import org.apache.wicket.Session;
import org.apache.wicket.protocol.http.WebSession;

@SuppressWarnings("serial")
public class HatchetHarrySession extends WebSession
{

	private String cometUser;

	public HatchetHarrySession(final Request request)
	{
		super(request);
	}

	public synchronized String getCometUser()
	{
		return this.cometUser;
	}

	public synchronized void setCometUser(final String _cometUser)
	{
		this.cometUser = _cometUser;
		this.dirty();
	}

	public static HatchetHarrySession get()
	{
		return (HatchetHarrySession)Session.get();
	}
}
