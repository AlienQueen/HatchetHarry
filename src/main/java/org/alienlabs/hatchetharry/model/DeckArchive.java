/*
 * A DeckArchive is the name of a Deck, linked to CollectibleCard which both represent the deck and its cards list.
 * The DeckArchive name must be unique in DB and if a Player uses it as deck for his game, it is not duplicated, hence the suffix "Archive".
 * 
 * @see: CollectibleCard
 * @See: Deck
 * @see: MagicCard 
 */

package org.alienlabs.hatchetharry.model;

import java.io.Serializable;

import javax.persistence.*;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity
@Table(name = "DeckArchive")//, indexes = { @Index(columnList = "deckName") })
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class DeckArchive implements Serializable
{
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "deckArchiveId")
	private Long deckArchiveId;
    @Column(name="VERSION", length=20)
    private String version;
	@Column
	private String deckName;

	public Long getDeckArchiveId()
	{
		return this.deckArchiveId;
	}

	public void setDeckArchiveId(final Long _deckArchiveId)
	{
		this.deckArchiveId = _deckArchiveId;
	}

	public String getDeckName()
	{
		return this.deckName;
	}

	public void setDeckName(final String _deckName)
	{
		this.deckName = _deckName;
	}

    public String getVersion() {
        return this.version;
    }

    public void setVersion(String _version) {
        this.version = _version;
    }

	@Override
	public String toString()
	{
		return this.deckName;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = (prime * result) + ((this.deckName == null) ? 0 : this.deckName.hashCode());
		return result;
	}

	@Override
	public boolean equals(final Object obj)
	{
		if (this == obj)
		{
			return true;
		}
		if (obj == null)
		{
			return false;
		}
		if (this.getClass() != obj.getClass())
		{
			return false;
		}
		final DeckArchive other = (DeckArchive)obj;
		if (this.deckName == null)
		{
			if (other.deckName != null)
			{
				return false;
			}
		}
		else if (!this.deckName.equals(other.deckName))
		{
			return false;
		}
		return true;
	}

}
