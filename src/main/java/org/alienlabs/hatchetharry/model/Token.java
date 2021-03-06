package org.alienlabs.hatchetharry.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

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
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity
@Table(name = "Token", indexes = { @Index(columnList = "uuid"), @Index(columnList = "Player_Token") })
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Token implements Serializable
{
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "tokenId")
	private Long id;
	@Column(name = "VERSION", length = 20)
	private String version;
	@Column
	private String type;
	@Column
	private String power;
	@Column
	private String toughness;
	@Column
	private String colors;
	@Column
	private String description;
	@Column
	private String uuid;
	@Column
	private Long gameId;
	@Column
	private Long x = -1L; // x coordinate
	@Column
	private Long y = -1L; // y coordinate
	@Column
	private boolean tapped;
	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, targetEntity = Counter.class, orphanRemoval = true)
	@JoinTable(name = "Card_Counter", joinColumns = @JoinColumn(name = "uuid"), inverseJoinColumns = @JoinColumn(name = "counterId"))
	private Set<Counter> counters = new HashSet<>();
	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "Player_Token")
	private Player player = new Player();
	@Column
	private String capabilities;
	@Column
	private String creatureTypes;

	public Token()
	{
	}

	public Token(final String _type, final String _power, final String _toughness,
			final String _colors, final String _description, final String _uuid, final Long _gameId)
	{
		this.type = _type;
		this.power = _power;
		this.toughness = _toughness;
		this.colors = _colors;
		this.description = _description;
		this.uuid = _uuid;
		this.gameId = _gameId;
	}

	public Long getId()
	{
		return this.id;
	}

	public void setId(final Long _id)
	{
		this.id = _id;
	}

	public String getType()
	{
		return this.type;
	}

	public void setType(final String _type)
	{
		this.type = _type;
	}

	public String getPower()
	{
		return this.power;
	}

	public void setPower(final String _power)
	{
		this.power = _power;
	}

	public String getToughness()
	{
		return this.toughness;
	}

	public void setToughness(final String _toughness)
	{
		this.toughness = _toughness;
	}

	public String getColors()
	{
		return this.colors;
	}

	public void setColors(final String _colors)
	{
		this.colors = _colors;
	}

	public String getDescription()
	{
		return this.description;
	}

	public void setDescription(final String _description)
	{
		this.description = _description;
	}

	public String getUuid()
	{
		return this.uuid;
	}

	public void setUuid(final String _uuid)
	{
		this.uuid = _uuid;
	}

	public Long getGameId()
	{
		return this.gameId;
	}

	public void setGameId(final Long _gameId)
	{
		this.gameId = _gameId;
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

	public Set<Counter> getCounters()
	{
		return this.counters;
	}

	public void setCounters(final Set<Counter> _counters)
	{
		this.counters = _counters;
	}

	public Player getPlayer()
	{
		return this.player;
	}

	public void setPlayer(final Player _player)
	{
		this.player = _player;
	}


	public String getCapabilities()
	{
		return this.capabilities;
	}

	public void setCapabilities(final String _capabilities)
	{
		this.capabilities = _capabilities;
	}

	public String getCreatureTypes()
	{
		return this.creatureTypes;
	}

	public void setCreatureTypes(final String _creatureTypes)

	{
		this.creatureTypes = _creatureTypes;
	}

	public String getVersion()
	{
		return this.version;
	}

	public void setVersion(final String _version)
	{
		this.version = _version;
	}

	@Override
	public boolean equals(final Object o)
	{
		if (this == o)
		{
			return true;
		}
		if (!(o instanceof Token))
		{
			return false;
		}

		final Token token = (Token)o;

		if (this.id != null ? !this.id.equals(token.id) : token.id != null)
		{
			return false;
		}

		return true;
	}

	@Override
	public int hashCode()
	{
		return this.id != null ? this.id.hashCode() : 0;
	}

}
