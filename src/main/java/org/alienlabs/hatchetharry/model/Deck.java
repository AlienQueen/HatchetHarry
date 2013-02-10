/*
 * 
 * A Deck is what a player chooses to play. It is instantiated (i.e. duplicated in DB) each time a player chooses the underlying DeckArchive.
 * The MagicCard objects are duplicated too, since they represent the state of a particular card in the Deck, and they are instantiated using the
 * CollectibleCard object, which itself only represents the list of cards in a DeckArchive, without any game-related information.
 * 
 * @see: MagicCard 
 * @see: DeckArchive
 * @See: CollectibleCard
 */

package org.alienlabs.hatchetharry.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.persistence.Cacheable;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity
@Table(name = "Deck")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Deck implements Serializable
{
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "deckId")
	private Long deckId;
	@OneToOne(cascade = { CascadeType.MERGE })
	@JoinColumn(name = "Deck_DeckArchive")
	private DeckArchive deckArchive = new DeckArchive();
	@Column
	private Long playerId;
	@OneToMany(mappedBy = "deck", fetch = FetchType.EAGER, cascade = { CascadeType.ALL })
	private List<MagicCard> cards = new ArrayList<MagicCard>();

	public List<MagicCard> shuffleLibrary()
	{
		Collections.shuffle(this.cards);
		Collections.shuffle(this.cards);
		Collections.shuffle(this.cards);
		Collections.shuffle(this.cards);
		Collections.shuffle(this.cards);
		return this.cards;
	}

	public Long getPlayerId()
	{
		return this.playerId;
	}

	public void setPlayerId(final Long _playerId)
	{
		this.playerId = _playerId;
	}

	@Override
	public String toString()
	{
		return this.deckArchive.getDeckName();
	}

	public Long getDeckId()
	{
		return this.deckId;
	}

	public void setDeckId(final Long deckId)
	{
		this.deckId = deckId;
	}

	public DeckArchive getDeckArchive()
	{
		return this.deckArchive;
	}

	public void setDeckArchive(final DeckArchive deckArchive)
	{
		this.deckArchive = deckArchive;
	}

	public List<MagicCard> getCards()
	{
		return this.cards;
	}

	public void setCards(final List<MagicCard> _cards)
	{
		this.cards = _cards;
	}

}
