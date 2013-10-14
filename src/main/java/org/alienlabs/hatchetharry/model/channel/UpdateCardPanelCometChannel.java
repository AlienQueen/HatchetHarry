package org.alienlabs.hatchetharry.model.channel;

import org.alienlabs.hatchetharry.model.MagicCard;


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
	private final MagicCard mc;
	private final String bigImage;
	private final String ownerSide;

	public UpdateCardPanelCometChannel(final Long _gameId, final String _requestingPlayerName,
			final String _targetPlayerName, final String _cardName, final String _counterName,
			final Long _targetNumberOfCounters, final Long _originalNumberOfCounters,
			final NotifierAction _action, final MagicCard _mc, final String _bigImage,
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
		this.mc = _mc;
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

	public MagicCard getMagicCard()
	{
		return this.mc;
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
