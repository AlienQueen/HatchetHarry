package org.alienlabs.hatchetharry.model.channel;

import org.alienlabs.hatchetharry.model.MagicCard;


public class RevealTopLibraryCardCometChannel {
	private final String playerName;
	private final MagicCard card;
	private final Long index;

	public RevealTopLibraryCardCometChannel(final String _playerName, final MagicCard _card,
											final Long _index) {
		this.playerName = _playerName;
		this.card = _card;
		this.index = _index;
	}

	public String getPlayerName() {
		return this.playerName;
	}

	public MagicCard getCard() {
		return this.card;
	}

	public Long getIndex() {
		return this.index;
	}

}
