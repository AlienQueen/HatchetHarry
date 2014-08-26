package org.alienlabs.hatchetharry.model.channel;

import org.alienlabs.hatchetharry.model.Token;


public class UpdateTokenPanelCometChannel
{

	private final Long gameId;
	private final String requestingPlayerName;
	private final String targetPlayerName;
	private final String tokenName;
	private final String counterName;
	private final Long targetNumberOfCounters;
	private final Long originalNumberOfCounters;
	private final NotifierAction action;
	private final Token token;
	private final String ownerSide;

	public UpdateTokenPanelCometChannel(final Long _gameId, final String _requestingPlayerName,
		final String _targetPlayerName, final String _tokenName, final String _counterName,
        final Long _targetNumberOfCounters, final Long _originalNumberOfCounters,
		final NotifierAction _action, final Token _token, final String _ownerSide)
	{
		this.gameId = _gameId;
		this.requestingPlayerName = _requestingPlayerName;
		this.targetPlayerName = _targetPlayerName;
		this.tokenName = _tokenName;
		this.counterName = _counterName;
		this.targetNumberOfCounters = _targetNumberOfCounters;
		this.originalNumberOfCounters = _originalNumberOfCounters;
		this.action = _action;
		this.token = _token;
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
		return this.tokenName;
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

	public Token getToken()
	{
		return this.token;
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
