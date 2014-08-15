package org.alienlabs.hatchetharry.model.channel;

import org.alienlabs.hatchetharry.model.MagicCard;


public class PutToGraveyardCometChannel {
	private final Long gameId;
	private final MagicCard mc;
	private final String requestingPlayerName;
	private final String targetPlayerName;
	private final Long targetPlayerId;
	private final Long deckId;
	private final boolean shouldUpdateGraveyard;

	public PutToGraveyardCometChannel(final Long _gameId, final MagicCard _mc,
									  final String _requestingPlayerName, final String _targetPlayerName,
									  final Long _targetPlayerId, final Long _deckId, final boolean _shouldUpdateGraveyard) {
		this.gameId = _gameId;
		this.mc = _mc;
		this.requestingPlayerName = _requestingPlayerName;
		this.targetPlayerName = _targetPlayerName;
		this.targetPlayerId = _targetPlayerId;
		this.deckId = _deckId;
		this.shouldUpdateGraveyard = _shouldUpdateGraveyard;

	}

	public Long getGameId() {
		return this.gameId;
	}

	public MagicCard getMagicCard() {
		return this.mc;
	}

	public String getRequestingPlayerName() {
		return this.requestingPlayerName;
	}

	public String getTargetPlayerName() {
		return this.targetPlayerName;
	}

	public Long getTargetPlayerId() {
		return this.targetPlayerId;
	}

	public Long getDeckId() {
		return this.deckId;
	}

	public boolean isShouldUpdateGraveyard() {
		return this.shouldUpdateGraveyard;
	}

}
