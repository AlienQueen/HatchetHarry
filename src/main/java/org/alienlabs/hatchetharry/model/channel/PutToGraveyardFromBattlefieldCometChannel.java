package org.alienlabs.hatchetharry.model.channel;

import org.alienlabs.hatchetharry.model.MagicCard;


public class PutToGraveyardFromBattlefieldCometChannel extends PutToZoneFromBattlefieldParentCometChannel
{
	public PutToGraveyardFromBattlefieldCometChannel(final Long _gameId, final MagicCard _mc,
													 final String _requestingPlayerName, final Long _targetPlayerId, final Long _deckId, final boolean _shouldUpdateZone)
	{
		super(_gameId, _mc, _requestingPlayerName, _targetPlayerId, _deckId,
				_shouldUpdateZone);

	}

}
