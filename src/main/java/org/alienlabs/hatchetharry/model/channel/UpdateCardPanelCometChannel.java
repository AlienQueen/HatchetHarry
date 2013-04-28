package org.alienlabs.hatchetharry.model.channel;


public class UpdateCardPanelCometChannel
{

	private final Long gameId;
	private final String requestingPlayerName;
	private final String targetPlayerName;
	private final String cardName;
	private final String counterName;
	private final Long numberOfCounters;
	private final NotifierAction action;

	public UpdateCardPanelCometChannel(final Long _gameId, final String _requestingPlayerName,
			final String _targetPlayerName, final String _cardName, final String _counterName,
			final Long _numberOfCounters, final NotifierAction _action)
	{
		this.gameId = _gameId;
		this.requestingPlayerName = _requestingPlayerName;
		this.targetPlayerName = _targetPlayerName;
		this.cardName = _cardName;
		this.counterName = _counterName;
		this.numberOfCounters = _numberOfCounters;
		this.action = _action;
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

	public Long getNumberOfCounters()
	{
		return this.numberOfCounters;
	}

	public NotifierAction getAction()
	{
		return this.action;
	}

}
