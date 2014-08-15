package org.alienlabs.hatchetharry.model.channel;

import org.alienlabs.hatchetharry.model.MagicCard;

public class DestroyTokenCometChannel {
	private final MagicCard card;
	private final Long gameId;

	public DestroyTokenCometChannel(final MagicCard _card, final Long _gameId) {
		this.card = _card;
		this.gameId = _gameId;
	}

	public MagicCard getCard() {
		return this.card;
	}

	public Long getGameId() {
		return this.gameId;
	}

}
