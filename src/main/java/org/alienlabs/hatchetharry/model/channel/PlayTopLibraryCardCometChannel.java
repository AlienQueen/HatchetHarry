package org.alienlabs.hatchetharry.model.channel;

import org.alienlabs.hatchetharry.model.MagicCard;
import org.alienlabs.hatchetharry.model.Side;

public class PlayTopLibraryCardCometChannel {
	private final Long gameId;
	private final MagicCard mc;
	private final Side side;

	public PlayTopLibraryCardCometChannel(final Long _gameId, final MagicCard _mc, final Side _side) {
		this.gameId = _gameId;
		this.mc = _mc;
		this.side = _side;
	}

	public Long getGameId() {
		return this.gameId;
	}

	public MagicCard getCard() {
		return this.mc;
	}

	public Side getSide() {
		return this.side;
	}

}
