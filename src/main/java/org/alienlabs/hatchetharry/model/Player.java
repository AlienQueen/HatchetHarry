package org.alienlabs.hatchetharry.model;

import java.io.Serializable;

import javax.persistence.Cacheable;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity
@Table(name = "Player", indexes = { @Index(columnList = "Player_Side"), @Index(columnList = "deck") })
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Player implements Serializable
{
	private static final long serialVersionUID = 7963755937946852379L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long playerId;
	@ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	private Game game = new Game();
	@OneToOne(cascade = { CascadeType.ALL })
	@JoinColumn(name = "Player_Side")
	private Side side = new Side();
	@Column
	private String name;
	@Column
	private String jsessionid;
	@Column
	private Long lifePoints;
	@ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinColumn(name = "deck")
	private Deck deck;
	@Column
	private Boolean isHandDisplayed = true;
	@Column
	private Boolean isGraveyardDisplayed;
	@Column
	private Boolean isExileDisplayed;
	@Column
	private CardZone defaultTargetZoneForHand = CardZone.BATTLEFIELD;
	@Column
	private CardZone defaultTargetZoneForGraveyard = CardZone.BATTLEFIELD;
	@Column
	private CardZone defaultTargetZoneForExile = CardZone.BATTLEFIELD;
	@Column
	private String sideUuid;

	public Long getId()
	{
		return this.playerId;
	}

	public void setId(final Long _id)
	{
		this.playerId = _id;
	}

	public Side getSide()
	{
		return this.side;
	}

	public void setSide(final Side _side)
	{
		this.side = _side;
	}

	public String getName()
	{
		return this.name;
	}

	public void setName(final String _name)
	{
		this.name = _name;
	}

	public String getJsessionid()
	{
		return this.jsessionid;
	}

	public void setJsessionid(final String _jsessionid)
	{
		this.jsessionid = _jsessionid;
	}

	public Long getLifePoints()
	{
		return this.lifePoints;
	}

	public void setLifePoints(final Long _lifePoints)
	{
		this.lifePoints = _lifePoints;
	}

	public Game getGame()
	{
		return this.game;
	}

	public void setGame(final Game _game)
	{
		this.game = _game;
	}

	public Deck getDeck()
	{
		return this.deck;
	}

	public void setDeck(final Deck _deck)
	{
		this.deck = _deck;
	}

	public Boolean isHandDisplayed()
	{
		return this.isHandDisplayed;
	}

	public void setHandDisplayed(final boolean _isHandDisplayed)
	{
		this.isHandDisplayed = _isHandDisplayed;
	}

	public Boolean isGraveyardDisplayed()
	{
		return this.isGraveyardDisplayed;
	}

	public void setGraveyardDisplayed(final boolean _isGraveyardDisplayed)
	{
		this.isGraveyardDisplayed = _isGraveyardDisplayed;
	}

	public Boolean isExileDisplayed()
	{
		return this.isExileDisplayed;
	}

	public void setExileDisplayed(final boolean _isExileDisplayed)
	{
		this.isExileDisplayed = _isExileDisplayed;
	}

	public CardZone getDefaultTargetZoneForHand()
	{
		return this.defaultTargetZoneForHand;
	}

	public void setDefaultTargetZoneForHand(final CardZone _defaultTargetZoneForHand)
	{
		this.defaultTargetZoneForHand = _defaultTargetZoneForHand;
	}

	public CardZone getDefaultTargetZoneForGraveyard()
	{
		return this.defaultTargetZoneForGraveyard;
	}

	public void setDefaultTargetZoneForGraveyard(final CardZone _defaultTargetZoneForGraveyard)
	{
		this.defaultTargetZoneForGraveyard = _defaultTargetZoneForGraveyard;
	}

	public CardZone getDefaultTargetZoneForExile()
	{
		return this.defaultTargetZoneForExile;
	}

	public void setDefaultTargetZoneForExile(final CardZone _defaultTargetZoneForExile)
	{
		this.defaultTargetZoneForExile = _defaultTargetZoneForExile;
	}

	public String getSideUuid()
	{
		return this.sideUuid;
	}

	public void setSideUuid(final String _sideUuid)
	{
		this.sideUuid = _sideUuid;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = (prime * result) + ((this.jsessionid == null) ? 0 : this.jsessionid.hashCode());
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
		final Player other = (Player)obj;
		if (this.jsessionid == null)
		{
			if (other.jsessionid != null)
			{
				return false;
			}
		}
		else if (!this.jsessionid.equals(other.jsessionid))
		{
			return false;
		}
		return true;
	}

}
