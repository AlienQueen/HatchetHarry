package org.alienlabs.hatchetharry.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "Deck")
public class Deck implements Serializable
{
	private static final long serialVersionUID = 5336828396327485268L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "deckId")
	private Long deckId;
	@Column
	private Long playerId;
	@OneToMany(mappedBy = "deck")
	private List<MagicCard> cards = new ArrayList<MagicCard>();
	@Column
	private String deckName;

	public List<MagicCard> shuffleLibrary()
	{
		Collections.shuffle(this.cards);
		Collections.shuffle(this.cards);
		Collections.shuffle(this.cards);
		Collections.shuffle(this.cards);
		Collections.shuffle(this.cards);
		return this.cards;
	}

	public Long getId()
	{
		return this.deckId;
	}

	public void setId(final Long _id)
	{
		this.deckId = _id;
	}

	public Long getPlayerId()
	{
		return this.playerId;
	}

	public void setPlayerId(final Long _playerId)
	{
		this.playerId = _playerId;
	}

	public List<MagicCard> getCards()
	{
		return this.cards;
	}

	public void setCards(final List<MagicCard> _cards)
	{
		this.cards = _cards;
	}

	public String getDeckName()
	{
		return this.deckName;
	}

	public void setDeckName(final String _deckName)
	{
		this.deckName = _deckName;
	}

	@Override
	public String toString()
	{
		return this.getDeckName();
	}

}