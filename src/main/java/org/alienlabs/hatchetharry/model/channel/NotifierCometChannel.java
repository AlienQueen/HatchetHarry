package org.alienlabs.hatchetharry.model.channel;

public class NotifierCometChannel
{

	private final NotifierAction action;
	private final Long gameId;
	private final Long playerId;
	private final String playerName;
	private final String side;
	private final String jsessionid;
	private final String cardName;
	private final Boolean combatInProgress;
	private final String targetPlayerName;

	public NotifierCometChannel(final NotifierAction _action, final Long _gameId,
		final Long _playerId, final String _playerName, final String _side,
		final String _jsessionid, final String _cardName, final Boolean _combatInProgress,
		final String _targetPlayerName)
	{
		this.action = _action;
		this.gameId = _gameId;
		this.playerId = _playerId;
		this.playerName = _playerName;
		this.side = _side;
		this.jsessionid = _jsessionid;
		this.cardName = _cardName;
		this.combatInProgress = _combatInProgress;
		this.targetPlayerName = _targetPlayerName;
	}

	public NotifierAction getAction()
	{
		return this.action;
	}

	public Long getGameId()
	{
		return this.gameId;
	}

	public Long getPlayerId()
	{
		return this.playerId;
	}

	public String getPlayerName()
	{
		return this.playerName;
	}

	public String getSide()
	{
		return this.side;
	}

	public String getJsessionid()
	{
		return this.jsessionid;
	}

	public String getCardName()
	{
		return this.cardName;
	}

	public Boolean isCombatInProgress()
	{
		return this.combatInProgress;
	}

	public String getTargetPlayerName()
	{
		return this.targetPlayerName;
	}

}
