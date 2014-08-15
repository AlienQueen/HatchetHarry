package org.alienlabs.hatchetharry.model.channel;

import org.alienlabs.hatchetharry.model.MagicCard;
import org.alienlabs.hatchetharry.model.Side;

/**
 * @author nostromo
 */
public class PlayCardFromHandCometChannel {
	private final String playerName;
	private final Long gameId;
	private final MagicCard mc;
	private final Side side;

	public PlayCardFromHandCometChannel(final MagicCard _mc, final String _playerName,
										final Long _gameId, final Side _side) {
		this.mc = _mc;
		this.playerName = _playerName;
		this.gameId = _gameId;
		this.side = _side;
	}

	public MagicCard getMagicCard() {
		return this.mc;
	}

	public String getPlayerName() {
		return this.playerName;
	}

	public Long getGameId() {
		return this.gameId;
	}

	public Side getSide() {
		return this.side;
	}

}
