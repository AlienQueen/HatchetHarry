package org.alienlabs.hatchetharry;

import java.util.ArrayList;

import org.alienlabs.hatchetharry.model.MagicCard;
import org.apache.wicket.Request;
import org.apache.wicket.Session;
import org.apache.wicket.protocol.http.WebSession;

public class HatchetHarrySession extends WebSession
{
	private static final long serialVersionUID = 4565051252275468687L;
	private String cometUser;

	private static final String MY_GAME = "MY_GAME";
	private static final String FIRST_CARDS_IN_HAND = "FIRST_CARDS_IN_HAND";
	private static String HAND_CARDS_HAVE_BEEN_BUILT = "HAND_CARDS_HAVE_BEEN_BUILT";

	public HatchetHarrySession(final Request request)
	{
		super(request);
		this.setAttribute(HatchetHarrySession.HAND_CARDS_HAVE_BEEN_BUILT, false);
	}

	public static HatchetHarrySession get()
	{
		return (HatchetHarrySession)Session.get();
	}

	public synchronized String getCometUser()
	{
		return this.cometUser;
	}

	public synchronized void setCometUser(final String _cometUser)
	{
		this.cometUser = _cometUser;
		this.dirty();
	}

	public synchronized Long getGameId()
	{
		return (Long)this.getAttribute(HatchetHarrySession.MY_GAME);
	}

	public synchronized void setGameId(final Long _gameId)
	{
		this.setAttribute(HatchetHarrySession.MY_GAME, _gameId);
	}

	public synchronized boolean getHandCardsHaveBeenBuilt()
	{
		return (Boolean)this.getAttribute(HatchetHarrySession.HAND_CARDS_HAVE_BEEN_BUILT);
	}

	public synchronized void setHandCardsHaveBeenBuilt(final Boolean handCardsHaveBeenBuilt)
	{
		this.setAttribute(HatchetHarrySession.HAND_CARDS_HAVE_BEEN_BUILT, handCardsHaveBeenBuilt);
	}

	@SuppressWarnings("unchecked")
	public synchronized ArrayList<MagicCard> getFirstCardsInHand()
	{
		return (ArrayList<MagicCard>)this.getAttribute(HatchetHarrySession.FIRST_CARDS_IN_HAND);
	}

	public synchronized void setFirstCardsInHand(final ArrayList<MagicCard> cards)
	{
		this.setAttribute(HatchetHarrySession.FIRST_CARDS_IN_HAND, cards);
	}
}
