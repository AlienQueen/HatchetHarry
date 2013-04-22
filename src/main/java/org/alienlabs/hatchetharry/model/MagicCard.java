/*
 * A MagicCard is a card in a Deck currently played, i.e. it has game state-related information.
 * It is instantiated (i.e. duplicated in DB) via the CollectibleCard object which represent the list
 * of cards in the Deck but without any game state information.
 * 
 * @see: Deck 
 * @see: DeckArchive
 * @See: CollectibleCard
 */

package org.alienlabs.hatchetharry.model;

import java.io.Serializable;
import java.util.UUID;

import javax.persistence.Cacheable;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.apache.wicket.model.Model;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity
@Table(name = "MagicCard")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class MagicCard implements SlideshowImage, Serializable
{
	private static final long serialVersionUID = -5115712217304615521L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "magicCardId")
	private Long id;
	@Column
	private String smallImageFilename = "";
	@Column
	private String bigImageFilename = "";
	@Column
	private String thumbnailFilename = "";
	@Column
	private String title = "";
	@Column
	private String description = "";
	@Column
	private String uuid;
	@Column
	private Long gameId;
	@ManyToOne(cascade = { CascadeType.ALL }, fetch = FetchType.EAGER)
	@JoinColumn(name = "card_deck")
	private Deck deck;
	@Column
	private Long x = 64l; // x coordinate
	@Column
	private Long y = 64l; // y coordinate
	@Column
	private boolean tapped = false;
	@Column
	@Enumerated(value = EnumType.STRING)
	private CardZone zone;
	@Column
	private Long zoneOrder = 0l;

	public MagicCard()
	{
	}

	public MagicCard(final String _smallImageFilename, final String _bigImageFilename,
			final String _thumbnailFilename, final String _title, final String _description)
	{
		this.smallImageFilename = _smallImageFilename;
		this.bigImageFilename = _bigImageFilename;
		this.thumbnailFilename = _thumbnailFilename;
		this.title = _title;
		this.description = _description;
	}

	public Long getId()
	{
		return this.id;
	}

	public void setId(final Long _id)
	{
		this.id = _id;
	}

	@Override
	public String getLow()
	{
		return this.smallImageFilename;
	}

	@Override
	public String getHigh()
	{
		return this.smallImageFilename;
	}

	@Override
	public String getThumb()
	{
		return this.smallImageFilename;
	}

	@Override
	public String getTitle()
	{
		return this.title;
	}

	@Override
	public String getDescription()
	{
		return this.description;
	}

	public String getSmallImageFilename()
	{
		return this.smallImageFilename;
	}


	public void setSmallImageFilename(final String _smallImagefilename)
	{
		this.smallImageFilename = _smallImagefilename;
	}

	public Model<String> getModel()
	{
		return new Model<String>(this.smallImageFilename);
	}

	public String getUuid()
	{
		return this.uuid;
	}

	public void setUuid(final String _uuid)
	{
		this.uuid = _uuid;
	}


	public UUID getUuidObject()
	{
		return UUID.fromString(this.uuid);
	}

	public void setUuidObject(final UUID _uuid)
	{
		this.uuid = _uuid.toString();
	}

	public String getBigImageFilename()
	{
		return this.bigImageFilename;
	}

	public void setBigImageFilename(final String _bigImageFilename)
	{
		this.bigImageFilename = _bigImageFilename;
	}

	public Long getGameId()
	{
		return this.gameId;
	}

	public void setGameId(final Long _gameId)
	{
		this.gameId = _gameId;
	}

	public Deck getDeck()
	{
		return this.deck;
	}

	public void setDeck(final Deck _deck)
	{
		this.deck = _deck;
	}

	public String getThumbnailFilename()
	{
		return this.thumbnailFilename;
	}

	public void setThumbnailFilename(final String _thumbnailFilename)
	{
		this.thumbnailFilename = _thumbnailFilename;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = (prime * result) + ((this.id == null) ? 0 : this.id.hashCode());
		result = (prime * result) + ((this.uuid == null) ? 0 : this.uuid.hashCode());
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
		final MagicCard other = (MagicCard)obj;
		if (this.id == null)
		{
			if (other.id != null)
			{
				return false;
			}
		}
		else if (!this.id.equals(other.id))
		{
			return false;
		}
		if (this.uuid == null)
		{
			if (other.uuid != null)
			{
				return false;
			}
		}
		else if (!this.uuid.equals(other.uuid))
		{
			return false;
		}
		return true;
	}

	public Long getX()
	{
		return this.x;
	}

	public void setX(final Long _x)
	{
		this.x = _x;
	}

	public Long getY()
	{
		return this.y;
	}

	public void setY(final Long _y)
	{
		this.y = _y;
	}

	public boolean isTapped()
	{
		return this.tapped;
	}

	public void setTapped(final boolean _tapped)
	{
		this.tapped = _tapped;
	}

	public CardZone getZone()
	{
		return this.zone;
	}

	public void setZone(final CardZone _zone)
	{
		this.zone = _zone;
	}

	public Long getZoneOrder()
	{
		return this.zoneOrder;
	}

	public void setZoneOrder(final Long _zoneOrder)
	{
		this.zoneOrder = _zoneOrder;
	}

}
