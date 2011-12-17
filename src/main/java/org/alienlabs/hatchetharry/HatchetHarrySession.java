package org.alienlabs.hatchetharry;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.alienlabs.hatchetharry.model.Deck;
import org.alienlabs.hatchetharry.model.MagicCard;
import org.alienlabs.hatchetharry.model.Player;
import org.alienlabs.hatchetharry.view.component.CardPanel;
import org.alienlabs.hatchetharry.view.component.DataBox;
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
	private static String ALL_CARDS_IN_HAND = "ALL_CARDS_IN_HAND";
	private static String PLAYER_HAS_BEEN_CREATED = "PLAYER_HAS_BEEN_CREATED";
	private static String PLAYER = "PLAYER";
	private static String HAND_HAS_BEEN_CREATED = "HAND_HAS_BEEN_CREATED";
	private static String INDEX_OF_CURRENT_CARD = "INDEX_OF_CURRENT_CARD";
	private static String INDEX_NEXT_PLAYER = "INDEX_NEXT_PLAYER";
	private static String PLAYER_LETTER = "PLAYER_LETTER";
	private static String PLACEHOLDER_NUMBER = "PLACEHOLDER_NUMBER";
	private static String GAME_CREATED = "GAME_CREATED";
	private static String CARDS_IN_BATTLEFIELD = "CARDS_IN_BATTLEFIELD";
	private static String DATA_BOX = "DATA_BOX";
	private static String DECK = "DECK";

	public HatchetHarrySession(final Request request)
	{
		super(request);
		this.setAttribute(HatchetHarrySession.HAND_CARDS_HAVE_BEEN_BUILT, false);
		this.setAttribute(HatchetHarrySession.PLAYER_HAS_BEEN_CREATED, false);
		this.setAttribute(HatchetHarrySession.HAND_HAS_BEEN_CREATED, false);
		this.setAttribute(HatchetHarrySession.INDEX_NEXT_PLAYER, 1l);
		this.setAttribute(HatchetHarrySession.GAME_CREATED, false);
		this.setAttribute(HatchetHarrySession.CARDS_IN_BATTLEFIELD, new ArrayList<CardPanel>());
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

	public Long getGameId()
	{
		return (Long)this.getAttribute(HatchetHarrySession.MY_GAME);
	}

	public void setGameId(final Long _gameId)
	{
		this.setAttribute(HatchetHarrySession.MY_GAME, _gameId);
	}

	public boolean getHandCardsHaveBeenBuilt()
	{
		return (Boolean)this.getAttribute(HatchetHarrySession.HAND_CARDS_HAVE_BEEN_BUILT);
	}

	public void setHandCardsHaveBeenBuilt(final Boolean handCardsHaveBeenBuilt)
	{
		this.setAttribute(HatchetHarrySession.HAND_CARDS_HAVE_BEEN_BUILT, handCardsHaveBeenBuilt);
	}

	@SuppressWarnings("unchecked")
	public List<MagicCard> getFirstCardsInHand()
	{
		return (List<MagicCard>)this.getAttribute(HatchetHarrySession.FIRST_CARDS_IN_HAND);
	}

	public void setFirstCardsInHand(final List<MagicCard> cards)
	{
		this.setAttribute(HatchetHarrySession.FIRST_CARDS_IN_HAND, cards);
	}

	@SuppressWarnings("unchecked")
	public int getFirstCardIdInHand()
	{
		return ((List<Integer>)this.getAttribute(HatchetHarrySession.ALL_CARDS_IN_HAND)).get(0);
	}

	@SuppressWarnings("unchecked")
	public void addCardIdInHand(final int index, final int id)
	{
		if (this.getAttribute(HatchetHarrySession.ALL_CARDS_IN_HAND) == null)
		{
			this.setAttribute(HatchetHarrySession.ALL_CARDS_IN_HAND, new ArrayList<Integer>());
		}
		((List<Integer>)this.getAttribute(HatchetHarrySession.ALL_CARDS_IN_HAND)).add(index, id);
	}

	@SuppressWarnings("unchecked")
	public void removeCardIdInHand(final MagicCard c)
	{
		((List<Integer>)this.getAttribute(HatchetHarrySession.ALL_CARDS_IN_HAND)).remove(c.getId());
	}

	public boolean setPlayerHasBeenCreated()
	{
		this.setAttribute(HatchetHarrySession.PLAYER_HAS_BEEN_CREATED, true);
		return true;
	}

	public boolean isPlayerCreated()
	{
		return (Boolean)this.getAttribute(HatchetHarrySession.PLAYER_HAS_BEEN_CREATED);
	}

	public boolean getHandHasBeenCreated()
	{
		return (Boolean)this.getAttribute(HatchetHarrySession.HAND_HAS_BEEN_CREATED);
	}

	public void setHandHasBeenCreated()
	{
		this.setAttribute(HatchetHarrySession.HAND_HAS_BEEN_CREATED, true);
	}

	public Player getPlayer()
	{
		return (Player)this.getAttribute(HatchetHarrySession.PLAYER);
	}

	public void setPlayer(final Player _player)
	{
		this.setPlayerHasBeenCreated();
		this.setAttribute(HatchetHarrySession.PLAYER, _player);
	}

	public int getIndexOfCurrentCard()
	{
		return (Integer)this.getAttribute(HatchetHarrySession.INDEX_OF_CURRENT_CARD);
	}

	public void setIndexOfCurrentCard(final int card)
	{
		this.setAttribute(HatchetHarrySession.INDEX_OF_CURRENT_CARD, card);
	}

	public void setPlayerLetter(final String _string)
	{
		this.setAttribute(HatchetHarrySession.PLAYER_LETTER, _string);
	}

	public String getPlayerLetter()
	{
		return (String)this.getAttribute(HatchetHarrySession.PLAYER_LETTER);
	}

	public void setPlaceholderNumber(final int _index)
	{
		this.setAttribute(HatchetHarrySession.PLACEHOLDER_NUMBER, _index);
	}

	public int getPlaceholderNumber()
	{
		return (Integer)this.getAttribute(HatchetHarrySession.PLACEHOLDER_NUMBER);
	}

	public void setDataBox(final DataBox _dataBox)
	{
		this.setAttribute(HatchetHarrySession.DATA_BOX, _dataBox);
	}

	public DataBox getDataBox()
	{
		return (DataBox)this.getAttribute(HatchetHarrySession.DATA_BOX);
	}

	public void setDeck(final Deck _deck)
	{
		this.setAttribute(HatchetHarrySession.DECK, _deck);
	}

	public Deck getDeck()
	{
		return (Deck)this.getAttribute(HatchetHarrySession.DECK);
	}

	public void setGameCreated()
	{
		this.setAttribute(HatchetHarrySession.GAME_CREATED, true);
	}

	public boolean isGameCreated()
	{
		return (Boolean)this.getAttribute(HatchetHarrySession.GAME_CREATED);
	}

	public void addCardInBattleField(final CardPanel cp)
	{
		@SuppressWarnings("unchecked")
		final ArrayList<CardPanel> cards = (ArrayList<CardPanel>)this
				.getAttribute(HatchetHarrySession.CARDS_IN_BATTLEFIELD);
		cards.add(cp);
		this.setAttribute(HatchetHarrySession.CARDS_IN_BATTLEFIELD, cards);
	}


	public ArrayList<CardPanel> getAllCardsInBattleField()
	{
		@SuppressWarnings("unchecked")
		final ArrayList<CardPanel> cards = (ArrayList<CardPanel>)this
				.getAttribute(HatchetHarrySession.CARDS_IN_BATTLEFIELD);
		return cards;
	}

	public CardPanel removeACardFromBattleField()
	{
		@SuppressWarnings("unchecked")
		final ArrayList<CardPanel> cards = (ArrayList<CardPanel>)this
				.getAttribute(HatchetHarrySession.CARDS_IN_BATTLEFIELD);

		final Iterator<CardPanel> it = cards.iterator();
		final boolean next = it.hasNext();
		boolean success;

		if (next)
		{
			final CardPanel cp = it.next();
			success = cards.remove(cp);
			if (success)
			{
				this.setAttribute(HatchetHarrySession.CARDS_IN_BATTLEFIELD, cards);
				return cp;
			}
		}
		return null;
	}

}
