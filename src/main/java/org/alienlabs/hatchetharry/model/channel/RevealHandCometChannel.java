package org.alienlabs.hatchetharry.model.channel;


public class RevealHandCometChannel
{
	private final Long game;
	private final Long player;
	private final Long deck;

	public RevealHandCometChannel(final Long _game, final Long _player, final Long _deck)
	{
		this.game = _game;
		this.player = _player;
		this.deck = _deck;
	}

	public Long getGame()
	{
		return this.game;
	}

	public Long getPlayer()
	{
		return this.player;
	}

	public Long getDeck()
	{
		return this.deck;
	}

}
