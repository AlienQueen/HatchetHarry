package org.alienlabs.hatchetharry;

import java.util.HashMap;
import java.util.UUID;

import org.alienlabs.hatchetharry.model.MagicCard;
import org.apache.wicket.Request;
import org.apache.wicket.Session;
import org.apache.wicket.protocol.http.WebSession;

public class HatchetHarrySession extends WebSession
{
	private static final long serialVersionUID = 4565051252275468687L;
	private String cometUser;

	private static final String ALL_CARDS = "allCArds";
	private static final String MY_GAME = "MY_GAME";

	public HatchetHarrySession(final Request request)
	{
		super(request);
		this.setAttribute(HatchetHarrySession.ALL_CARDS, new HashMap<UUID, MagicCard>(0));
	}

	public static HatchetHarrySession get()
	{
		return (HatchetHarrySession)Session.get();
	}

	public String getCometUser()
	{
		return this.cometUser;
	}

	public void setCometUser(final String _cometUser)
	{
		this.cometUser = _cometUser;
		this.dirty();
	}

	@SuppressWarnings("unchecked")
	public HashMap<?, ?> getAllCards()
	{
		return (HashMap<UUID, MagicCard>)this.getAttribute(HatchetHarrySession.ALL_CARDS);
	}

	public synchronized void setAllCards(final HashMap<UUID, MagicCard> _allCards)
	{
		this.setAttribute(HatchetHarrySession.ALL_CARDS, _allCards);
	}

	public Long getGameId()
	{
		return (Long)this.getAttribute(HatchetHarrySession.MY_GAME);
	}

	public synchronized void setGameId(final Long _gameId)
	{
		this.setAttribute(HatchetHarrySession.MY_GAME, _gameId);
	}
}
