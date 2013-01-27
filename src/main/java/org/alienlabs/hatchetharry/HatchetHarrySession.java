package org.alienlabs.hatchetharry;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.alienlabs.hatchetharry.model.MagicCard;
import org.alienlabs.hatchetharry.model.Player;
import org.alienlabs.hatchetharry.view.component.CardPanel;
import org.alienlabs.hatchetharry.view.component.SidePlaceholderPanel;
import org.apache.wicket.Application;
import org.apache.wicket.Component;
import org.apache.wicket.Session;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.protocol.http.WebSession;
import org.apache.wicket.request.Request;

public class HatchetHarrySession extends WebSession
{
	private static final long serialVersionUID = 4565051252275468687L;
	private String cometUser; // TODO: do something, Gromit

	private static final String MY_GAME = "MY_GAME";
	private static final String FIRST_CARDS_IN_HAND = "FIRST_CARDS_IN_HAND";
	private static final String CARDS_IN_GRAVEYARD = "CARDS_IN_GRAVEYARD";
	private static final String HAND_CARDS_HAVE_BEEN_BUILT = "HAND_CARDS_HAVE_BEEN_BUILT";
	private static final String ALL_CARDS_IN_HAND = "ALL_CARDS_IN_HAND";
	private static final String PLAYER_HAS_BEEN_CREATED = "PLAYER_HAS_BEEN_CREATED";
	private static final String PLAYER = "PLAYER";
	private static final String HAND_HAS_BEEN_CREATED = "HAND_HAS_BEEN_CREATED";
	private static final String INDEX_OF_CURRENT_CARD = "INDEX_OF_CURRENT_CARD";
	private static final String INDEX_NEXT_PLAYER = "INDEX_NEXT_PLAYER";
	private static final String GAME_CREATED = "GAME_CREATED";
	private static final String CARDS_IN_BATTLEFIELD = "CARDS_IN_BATTLEFIELD";
	private static final String DATA_BOX = "DATA_BOX";
	private static final String DATA_BOX_PARENT = "DATA_BOX_PARENT";
	private static final String TO_REMOVE = "TO_REMOVE";
	private static final String MY_SIDE_PLACEHOLDER = "MY_SIDE_PLACEHOLDER";
	private static final String MY_SIDE_PANELS = "MY_SIDE_PANELS";
	private static final String MY_SIDE_POS_X = "MY_SIDE_POS_X";
	private static final String MY_SIDE_POS_Y = "MY_SIDE_POS_Y";
	private static final String FIST_SIDE_MOVE_CALLBACK_URL = "FIST_SIDE_MOVE_CALLBACK_URL";
	private static final String SECOND_SIDE_MOVE_CALLBACK_URL = "SECOND_SIDE_MOVE_CALLBACK_URL";
	private static final String IS_HAND_DISPLAYED = "IS_HAND_DISPLAYED";
	private static final String IS_GRAVEYARD_DISPLAYED = "IS_GRAVEYARD_DISPLAYED";
	private static final String IS_COMBAT_IN_PROGRESS = "IS_COMBAT_IN_PROGRESS";
	private static final String COMET_UUID = "COMET_UUID";
	private static final String ALL_CARDS_WHICH_HAVE_BEEN_TO_GRAVEYARD = "ALL_CARDS_WHICH_HAVE_BEEN_TO_GRAVEYARD";
	private static final String ALL_CARD_PANELS_IN_BATTLEFIELD = "ALL_CARD_PANELS_IN_BATTLEFIELD";

	public HatchetHarrySession(final Request request)
	{
		super(request);
		this.setAttribute(HatchetHarrySession.HAND_CARDS_HAVE_BEEN_BUILT, false);
		this.setAttribute(HatchetHarrySession.PLAYER_HAS_BEEN_CREATED, false);
		this.setAttribute(HatchetHarrySession.HAND_HAS_BEEN_CREATED, false);
		this.setAttribute(HatchetHarrySession.INDEX_NEXT_PLAYER, 1l);
		this.setAttribute(HatchetHarrySession.GAME_CREATED, false);
		this.setAttribute(HatchetHarrySession.CARDS_IN_BATTLEFIELD, new ArrayList<CardPanel>());
		this.setAttribute(HatchetHarrySession.MY_SIDE_PANELS, new ArrayList<SidePlaceholderPanel>());
		this.setAttribute(HatchetHarrySession.MY_SIDE_PLACEHOLDER, new ArrayList<String>());
		this.setAttribute(HatchetHarrySession.IS_HAND_DISPLAYED, true);
		this.setAttribute(HatchetHarrySession.IS_GRAVEYARD_DISPLAYED, false);
		this.setAttribute(HatchetHarrySession.MY_GAME, 0L);
		this.setAttribute(HatchetHarrySession.IS_COMBAT_IN_PROGRESS, false);
		this.setAttribute(HatchetHarrySession.CARDS_IN_GRAVEYARD, new ArrayList<MagicCard>());
		this.setAttribute(HatchetHarrySession.ALL_CARDS_WHICH_HAVE_BEEN_TO_GRAVEYARD,
				new ArrayList<MagicCard>());
		this.setAttribute(HatchetHarrySession.ALL_CARD_PANELS_IN_BATTLEFIELD,
				new ArrayList<MagicCard>());
	}

	public static HatchetHarrySession get(final Request request)
	{
		if (((HatchetHarryApplication)Application.get()).isMistletoeTest())
		{
			return new HatchetHarrySession(request);
		}
		return (HatchetHarrySession)Session.get();
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

	public boolean isHandCardsHaveBeenBuilt()
	{
		return (Boolean)this.getAttribute(HatchetHarrySession.HAND_CARDS_HAVE_BEEN_BUILT);
	}

	public void setHandCardsHaveBeenBuilt(final Boolean handCardsHaveBeenBuilt)
	{
		this.setAttribute(HatchetHarrySession.HAND_CARDS_HAVE_BEEN_BUILT, handCardsHaveBeenBuilt);
	}

	public final ArrayList<MagicCard> getFirstCardsInHand()
	{
		return (ArrayList<MagicCard>)this.getAttribute(HatchetHarrySession.FIRST_CARDS_IN_HAND);
	}

	public void setFirstCardsInHand(final ArrayList<MagicCard> cards)
	{
		this.setAttribute(HatchetHarrySession.FIRST_CARDS_IN_HAND, cards);
	}

	public ArrayList<MagicCard> getCardsInGraveyard()
	{
		return (ArrayList<MagicCard>)this.getAttribute(HatchetHarrySession.CARDS_IN_GRAVEYARD);
	}

	public void addCardInGraveyard(final MagicCard card)
	{
		final List<MagicCard> cards = this.getCardsInGraveyard();
		cards.add(card);
		this.setAttribute(HatchetHarrySession.CARDS_IN_GRAVEYARD, (ArrayList<MagicCard>)cards);
	}

	public void removeCardFromGraveyard(final MagicCard card)
	{
		final List<MagicCard> cards = this.getCardsInGraveyard();
		cards.remove(card);
		this.setAttribute(HatchetHarrySession.CARDS_IN_GRAVEYARD, (ArrayList<MagicCard>)cards);
	}

	public void resetCardsInGraveyard()
	{
		this.setAttribute(HatchetHarrySession.CARDS_IN_GRAVEYARD, new ArrayList<MagicCard>());
	}

	public long getFirstCardIdInHand()
	{
		return ((List<Long>)this.getAttribute(HatchetHarrySession.ALL_CARDS_IN_HAND)).get(0);
	}

	// TODO remove this
	public void addCardIdInHand(final int index, final long id)
	{
		if (this.getAttribute(HatchetHarrySession.ALL_CARDS_IN_HAND) == null)
		{
			this.setAttribute(HatchetHarrySession.ALL_CARDS_IN_HAND, new ArrayList<Long>());
		}
		((List<Long>)this.getAttribute(HatchetHarrySession.ALL_CARDS_IN_HAND)).add(index, id);
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

	public final boolean isHandHasBeenCreated()
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

	public void setDataBox(final Component _dataBox)
	{
		this.setAttribute(HatchetHarrySession.DATA_BOX, _dataBox);
	}

	public Component getDataBox()
	{
		return (Component)this.getAttribute(HatchetHarrySession.DATA_BOX);
	}

	public void setDataBoxParent(final WebMarkupContainer _dataBoxParent)
	{
		this.setAttribute(HatchetHarrySession.DATA_BOX_PARENT, _dataBoxParent);
	}

	public WebMarkupContainer getDataBoxParent()
	{
		return (WebMarkupContainer)this.getAttribute(HatchetHarrySession.DATA_BOX_PARENT);
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
		final ArrayList<CardPanel> cards = (ArrayList<CardPanel>)this
				.getAttribute(HatchetHarrySession.CARDS_IN_BATTLEFIELD);
		cards.add(cp);
		this.setAttribute(HatchetHarrySession.CARDS_IN_BATTLEFIELD, cards);
	}

	public void addCardInToRemoveList(final CardPanel cp)
	{
		ArrayList<CardPanel> cards;

		if (this.getAttribute(HatchetHarrySession.TO_REMOVE) == null)
		{
			cards = new ArrayList<CardPanel>();
		}
		else
		{
			cards = (ArrayList<CardPanel>)this.getAttribute(HatchetHarrySession.TO_REMOVE);
		}
		cards.add(cp);
		this.setAttribute(HatchetHarrySession.TO_REMOVE, cards);
	}

	public List<CardPanel> getAllCardsToRemove()
	{
		final List<CardPanel> cards = (ArrayList<CardPanel>)this
				.getAttribute(HatchetHarrySession.TO_REMOVE);
		return cards;
	}

	public void removeAllCardsFromBattleField()
	{
		this.setAttribute(HatchetHarrySession.CARDS_IN_BATTLEFIELD, new ArrayList<CardPanel>());
	}

	public ArrayList<CardPanel> getAllCardsInBattleField()
	{
		final ArrayList<CardPanel> cards = (ArrayList<CardPanel>)this
				.getAttribute(HatchetHarrySession.CARDS_IN_BATTLEFIELD);
		return cards;
	}

	public void setAllCardsInBattleField(final ArrayList<CardPanel> cards)
	{
		this.setAttribute(HatchetHarrySession.CARDS_IN_BATTLEFIELD, cards);
	}

	public CardPanel removeACardFromBattleField()
	{
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

	public List<SidePlaceholderPanel> getMySidePlaceholder()
	{
		return (List<SidePlaceholderPanel>)this.getAttribute(HatchetHarrySession.MY_SIDE_PANELS);
	}

	public void setMySidePlaceholder(final SidePlaceholderPanel _mySidePlaceholder)
	{
		final ArrayList<SidePlaceholderPanel> l = (ArrayList<SidePlaceholderPanel>)this
				.getAttribute(HatchetHarrySession.MY_SIDE_PANELS);
		l.add(_mySidePlaceholder);
		this.setAttribute(HatchetHarrySession.MY_SIDE_PANELS, l);
	}

	public boolean isMySidePlaceholderInSesion(final String side)
	{
		return ((List<String>)this.getAttribute(HatchetHarrySession.MY_SIDE_PLACEHOLDER))
				.contains(side);
	}

	public void putMySidePlaceholderInSesion(final String side)
	{
		final ArrayList<String> l = (ArrayList<String>)this
				.getAttribute(HatchetHarrySession.MY_SIDE_PLACEHOLDER);
		l.add(side);
		this.setAttribute(HatchetHarrySession.MY_SIDE_PLACEHOLDER, l);
	}

	public Integer getMySidePosX()
	{
		return (Integer)this.getAttribute(HatchetHarrySession.MY_SIDE_POS_X);
	}

	public void setMySidePosX(final int _mySidePosX)
	{
		this.setAttribute(HatchetHarrySession.MY_SIDE_POS_X, _mySidePosX);
	}

	public Integer getMySidePosY()
	{
		return (Integer)this.getAttribute(HatchetHarrySession.MY_SIDE_POS_Y);
	}

	public void setMySidePosY(final int _mySidePosY)
	{
		this.setAttribute(HatchetHarrySession.MY_SIDE_POS_Y, _mySidePosY);
	}

	public String getSecondMySidePosY()
	{
		return (String)this.getAttribute(HatchetHarrySession.SECOND_SIDE_MOVE_CALLBACK_URL);
	}

	public String getFirstSideMoveCallbackUrl()
	{
		return (String)this.getAttribute(HatchetHarrySession.FIST_SIDE_MOVE_CALLBACK_URL);
	}

	public final void setFirstSideMoveCallbackUrl(final String callbackUrl)
	{
		this.setAttribute(HatchetHarrySession.FIST_SIDE_MOVE_CALLBACK_URL, callbackUrl);
	}

	public String getSecondSideMoveCallbackUrl()
	{
		return (String)this.getAttribute(HatchetHarrySession.SECOND_SIDE_MOVE_CALLBACK_URL);
	}

	public void setSecondSideMoveCallbackUrl(final String callbackUrl)
	{
		this.setAttribute(HatchetHarrySession.SECOND_SIDE_MOVE_CALLBACK_URL, callbackUrl);
	}

	public boolean isHandDisplayed()
	{
		return (Boolean)this.getAttribute(HatchetHarrySession.IS_HAND_DISPLAYED);
	}

	public void setHandDisplayed(final boolean isDisplayed)
	{
		this.setAttribute(HatchetHarrySession.IS_HAND_DISPLAYED, isDisplayed);
	}

	public boolean isGraveyardDisplayed()
	{
		return (Boolean)this.getAttribute(HatchetHarrySession.IS_GRAVEYARD_DISPLAYED);
	}

	public void setGraveyardDisplayed(final boolean isDisplayed)
	{
		this.setAttribute(HatchetHarrySession.IS_GRAVEYARD_DISPLAYED, isDisplayed);
	}

	public boolean isCombatInProgress()
	{
		return (Boolean)this.getAttribute(HatchetHarrySession.IS_COMBAT_IN_PROGRESS);
	}

	public void setCombatInProgress(final boolean combatInProgress)
	{
		this.setAttribute(HatchetHarrySession.IS_COMBAT_IN_PROGRESS, combatInProgress);
	}

	public String getCometUuid()
	{
		return (String)this.getAttribute(HatchetHarrySession.COMET_UUID);
	}

	public void setCometUuid(final String _cometUuid)
	{
		this.setAttribute(HatchetHarrySession.COMET_UUID, _cometUuid);
	}

	public ArrayList<MagicCard> getAllCardsWhichHaveBeenInBattlefield()
	{
		return (ArrayList<MagicCard>)this
				.getAttribute(HatchetHarrySession.ALL_CARDS_WHICH_HAVE_BEEN_TO_GRAVEYARD);
	}

	public void setAllCardsWhichHaveBeenInBattlefield(final ArrayList<MagicCard> list)
	{
		this.setAttribute(HatchetHarrySession.ALL_CARDS_WHICH_HAVE_BEEN_TO_GRAVEYARD, list);
	}

	public ArrayList<MagicCard> getAllMagicCardsInBattleField()
	{
		return (ArrayList<MagicCard>)this
				.getAttribute(HatchetHarrySession.ALL_CARD_PANELS_IN_BATTLEFIELD);
	}

	public void setAllMagicCardsInBattleField(final ArrayList<MagicCard> list)
	{
		this.setAttribute(HatchetHarrySession.ALL_CARD_PANELS_IN_BATTLEFIELD, list);
	}

}
