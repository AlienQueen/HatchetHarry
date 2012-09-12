package org.alienlabs.hatchetharry.model.channel;

public class CardMoveCometChannel {

	private final Long gameId;
	private final String mouseX;
	private final String mouseY;
	private final String uniqueid;

	public CardMoveCometChannel(Long _gameId, String _mouseX, String _mouseY, String _uniqueid) {
		this.gameId = _gameId;
		this.mouseX = _mouseX;
		this.mouseY = _mouseY;
		this.uniqueid = _uniqueid;
	}

	public Long getGameId() {
		return gameId;
	}

	public String getMouseX() {
		return mouseX;
	}

	public String getMouseY() {
		return mouseY;
	}

	public String getUniqueid() {
		return uniqueid;
	}

}
