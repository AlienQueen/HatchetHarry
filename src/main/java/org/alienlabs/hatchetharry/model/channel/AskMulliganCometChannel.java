package org.alienlabs.hatchetharry.model.channel;

public class AskMulliganCometChannel {

	private final String player;
	private final Long numberOfCards;

	public AskMulliganCometChannel(final String _player, final Long _numberOfCards) {
		this.player = _player;
		this.numberOfCards = _numberOfCards;
	}

	public String getPlayer() {
		return this.player;
	}

	public Long getNumberOfCards() {
		return this.numberOfCards;
	}


}
