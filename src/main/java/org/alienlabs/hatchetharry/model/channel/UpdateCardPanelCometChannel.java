package org.alienlabs.hatchetharry.model.channel;

import java.util.UUID;


public class UpdateCardPanelCometChannel
{

	private final Long gameId;
	private final String requestingPlayerName;
	private final String targetPlayerName;
	private final String cardName;
	private final String counterName;
	private final Long targetNumberOfCounters;
	private final Long originalNumberOfCounters;
	private final NotifierAction action;
	private final UUID uuid;
	private final String bigImage;
	private final String ownerSide;

	public UpdateCardPanelCometChannel(final Long _gameId, final String _requestingPlayerName,
			final String _targetPlayerName, final String _cardName, final String _counterName,
			final Long _targetNumberOfCounters, final Long _originalNumberOfCounters,
			final NotifierAction _action, final UUID _uuid, final String _bigImage,
			final String _ownerSide)
	{
		this.gameId = _gameId;
		this.requestingPlayerName = _requestingPlayerName;
		this.targetPlayerName = _targetPlayerName;
		this.cardName = _cardName;
		this.counterName = _counterName;
		this.targetNumberOfCounters = _targetNumberOfCounters;
		this.originalNumberOfCounters = _originalNumberOfCounters;
		this.action = _action;
		this.uuid = _uuid;
		this.bigImage = _bigImage;
		this.ownerSide = _ownerSide;
	}

	public Long getGameId()
	{
		return this.gameId;
	}

	public String getRequestingPlayerName()
	{
		return this.requestingPlayerName;
	}

	public String getTargetPlayerName()
	{
		return this.targetPlayerName;
	}

	public String getCardName()
	{
		return this.cardName;
	}

	public String getCounterName()
	{
		return this.counterName;
	}

	public Long getTargetNumberOfCounters()
	{
		return this.targetNumberOfCounters;
	}

	public NotifierAction getAction()
	{
		return this.action;
	}

	public UUID getUuid()
	{
		return this.uuid;
	}

	public String getBigImage()
	{
		return this.bigImage;
	}

	public String getOwnerSide()
	{
		return this.ownerSide;
	}

	public Long getOriginalNumberOfCounters()
	{
		return this.originalNumberOfCounters;
	}

}
