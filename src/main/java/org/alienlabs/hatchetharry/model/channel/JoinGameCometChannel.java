package org.alienlabs.hatchetharry.model.channel;

import java.util.UUID;

public class JoinGameCometChannel
{

	private final String side;
	private final String jsessionid;
	private final UUID uuid;
	private final Long posX;
	private final Long posY;

	public JoinGameCometChannel(final String _side, final String _jsessionid, final UUID _uuid,
			final Long _posX, final Long _posY)
	{
		this.side = _side;
		this.jsessionid = _jsessionid;
		this.uuid = _uuid;
		this.posX = _posX;
		this.posY = _posY;
	}

	public String getSide()
	{
		return this.side;
	}

	public String getJsessionid()
	{
		return this.jsessionid;
	}

	public UUID getUuid()
	{
		return this.uuid;
	}

	public Long getPosX()
	{
		return this.posX;
	}

	public Long getPosY()
	{
		return this.posY;
	}

}
