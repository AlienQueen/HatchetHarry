package org.alienlabs.hatchetharry.model.channel;


public class UpdateDataBoxCometChannel {
	private final Long gameId;

	public UpdateDataBoxCometChannel(final Long _gameId) {
		this.gameId = _gameId;
	}

	public Long getGameId() {
		return this.gameId;
	}

}
