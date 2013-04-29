package org.alienlabs.hatchetharry.model.channel;

import java.util.UUID;

import org.apache.wicket.markup.html.WebMarkupContainer;


public class UpdateCardPanelCometChannel
{

	private final Long gameId;
	private final String requestingPlayerName;
	private final String targetPlayerName;
	private final String cardName;
	private final String counterName;
	private final Long numberOfCounters;
	private final NotifierAction action;
	private final WebMarkupContainer cardHandle;
	private final UUID uuid;
	private final String bigImage;
	private final String ownerSide;

	public UpdateCardPanelCometChannel(final Long _gameId, final String _requestingPlayerName,
			final String _targetPlayerName, final String _cardName, final String _counterName,
			final Long _numberOfCounters, final NotifierAction _action,
			final WebMarkupContainer _cardHandle, final UUID _uuid, final String _bigImage,
			final String _ownerSide)
	{
		this.gameId = _gameId;
		this.requestingPlayerName = _requestingPlayerName;
		this.targetPlayerName = _targetPlayerName;
		this.cardName = _cardName;
		this.counterName = _counterName;
		this.numberOfCounters = _numberOfCounters;
		this.action = _action;
		this.cardHandle = _cardHandle;
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

	public Long getNumberOfCounters()
	{
		return this.numberOfCounters;
	}

	public NotifierAction getAction()
	{
		return this.action;
	}

	public WebMarkupContainer getCardHandle()
	{
		return this.cardHandle;
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

}
