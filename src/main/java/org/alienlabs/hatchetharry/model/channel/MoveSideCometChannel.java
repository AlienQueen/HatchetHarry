package org.alienlabs.hatchetharry.model.channel;

import java.util.UUID;

public class MoveSideCometChannel {
	private final UUID uuid;
	private final String sideX;
	private final String sideY;

	public MoveSideCometChannel(final UUID _uuid, final String _sideX, final String _sideY) {
		this.uuid = _uuid;
		this.sideX = _sideX;
		this.sideY = _sideY;
	}

	public UUID getUuid() {
		return this.uuid;
	}

	public String getSideX() {
		return this.sideX;
	}

	public String getSideY() {
		return this.sideY;
	}


}
