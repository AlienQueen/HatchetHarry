package org.alienlabs.hatchetharry.model.channel;

import org.alienlabs.hatchetharry.model.CardZone;
import org.alienlabs.hatchetharry.model.MagicCard;

public class CardZoneMoveNotifier {
	private final CardZone sourceZone, targetZone;
	private final MagicCard card;
	private final String requestingPlayer, ownerPlayer;

	public CardZoneMoveNotifier(final CardZone _sourceZone, final CardZone _targetZone,
								final MagicCard _card, final String _requestingPlayer, final String _ownerPlayer) {
		super();
		this.sourceZone = _sourceZone;
		this.targetZone = _targetZone;
		this.card = _card;
		this.requestingPlayer = _requestingPlayer;
		this.ownerPlayer = _ownerPlayer;
	}

	public CardZone getSourceZone() {
		return this.sourceZone;
	}

	public CardZone getTargetZone() {
		return this.targetZone;
	}

	public MagicCard getCard() {
		return this.card;
	}

	public String getRequestingPlayer() {
		return this.requestingPlayer;
	}

	public String getOwnerPlayer() {
		return this.ownerPlayer;
	}

}
