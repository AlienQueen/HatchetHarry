package org.alienlabs.hatchetharry.model.channel;

import org.alienlabs.hatchetharry.model.MagicCard;
import org.alienlabs.hatchetharry.view.component.CardPanel;


public class PutToHandFromBattlefieldCometChannel
{
	private final Long gameId;
	private final CardPanel card;
	private final MagicCard mc;
	private final Long playerId;

	public PutToHandFromBattlefieldCometChannel(final Long _gameId, final CardPanel _card,
			final MagicCard _mc, final Long _playerId)
	{
		this.gameId = _gameId;
		this.card = _card;
		this.mc = _mc;
		this.playerId = _playerId;

	}

	public Long getGameId()
	{
		return this.gameId;
	}

	public CardPanel getCardPanel()
	{
		return this.card;
	}

	public MagicCard getMagicCard()
	{
		return this.mc;
	}

	public Long getPlayerId()
	{
		return this.playerId;
	}

}
