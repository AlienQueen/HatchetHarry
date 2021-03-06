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
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.apache.wicket.model.Model;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity
@Table(name = "MagicCard", indexes = { @Index(columnList = "uuid"), @Index(columnList = "gameId"),
		@Index(columnList = "card_deck") })
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class MagicCard implements SlideshowImage, Serializable, Comparable<MagicCard>
{
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "magicCardId")
	private Long id;
	@Column(name = "VERSION", length = 20)
	private String version;
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
	@ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.EAGER, targetEntity = Deck.class)
	@JoinColumn(name = "card_deck")
	private Deck deck;
	@Column
	private Long x = -1L; // x coordinate
	@Column
	private Long y = -1L; // y coordinate
	@Column
	private boolean tapped;
	@Column
	@Enumerated(value = EnumType.STRING)
	private CardZone zone;
	@Column
	private Long zoneOrder = 0L;
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "card", targetEntity = Counter.class, orphanRemoval = true)
	private Set<Counter> counters = new HashSet<>();
	@Column
	private String ownerSide;
	@OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
	private Token token;
	@Column
	private Integer battlefieldOrder;

	public MagicCard()
	{
	}

	public MagicCard(final String _smallImageFilename, final String _bigImageFilename,
			final String _thumbnailFilename, final String _title, final String _description,
			final String _ownerSide, final Token _token, final Integer _battlefieldOrder)
	{
		this.smallImageFilename = _smallImageFilename;
		this.bigImageFilename = _bigImageFilename;
		this.thumbnailFilename = _thumbnailFilename;
		this.title = _title;
		this.description = _description;
		this.ownerSide = _ownerSide;
		this.token = _token;
		this.battlefieldOrder = _battlefieldOrder;
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
		return new Model<>(this.smallImageFilename);
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

	public Set<Counter> getCounters()
	{
		return this.counters;
	}

	public void setCounters(final Set<Counter> _counters)
	{
		this.counters = _counters;
	}

	public String getOwnerSide()
	{
		return this.ownerSide;
	}

	public void setOwnerSide(final String _ownerSide)
	{
		this.ownerSide = _ownerSide;
	}

	public Token getToken()
	{
		return this.token;
	}

	public void setToken(final Token _token)
	{
		this.token = _token;
	}

	public String getVersion()
	{
		return this.version;
	}

	public void setVersion(final String _version)
	{
		this.version = _version;
	}

	public Integer getBattlefieldOrder()
	{
		return this.battlefieldOrder;
	}

	public void setBattlefieldOrder(final Integer _battlefieldOrder)
	{
		this.battlefieldOrder = _battlefieldOrder;
	}

	@Override
	public int compareTo(final MagicCard o)
	{
		return this.battlefieldOrder.compareTo(o.getBattlefieldOrder());
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
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
}
