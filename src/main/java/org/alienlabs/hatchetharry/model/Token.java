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
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity
@Table(name = "Token")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Token implements Serializable
{
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "tokenId")
	private Long id;
	@Column
	private String type;
	@Column
	private Long power;
	@Column
	private Long thoughness;
	@Column
	private String colors;
	@Column
	private String description;
	@Column
	private String uuid;
	@Column
	private Long gameId;
	@Column
	private Long x = 64l; // x coordinate
	@Column
	private Long y = 64l; // y coordinate
	@Column
	private boolean tapped;
	@OneToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "Card_Counter", joinColumns = @JoinColumn(name = "uuid"), inverseJoinColumns = @JoinColumn(name = "counterId"))
	private Set<Counter> counters = new HashSet<Counter>();
	@OneToOne(cascade = { CascadeType.MERGE })
	@JoinColumn(name = "Player_Token")
	private Player player = new Player();

	public Token()
	{
	}

	public Token(final String _type, final Long _power, final Long _thoughness,
			final String _colors, final String _description, final String _uuid, final Long _gameId)
	{
		this.type = _type;
		this.power = _power;
		this.thoughness = _thoughness;
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

	public Long getPower()
	{
		return this.power;
	}

	public void setPower(final Long _power)
	{
		this.power = _power;
	}

	public Long getThoughness()
	{
		return this.thoughness;
	}

	public void setThoughness(final Long _thoughness)
	{
		this.thoughness = _thoughness;
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

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = (prime * result) + ((this.gameId == null) ? 0 : this.gameId.hashCode());
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
		final Token other = (Token)obj;
		if (this.gameId == null)
		{
			if (other.gameId != null)
			{
				return false;
			}
		}
		else if (!this.gameId.equals(other.gameId))
		{
			return false;
		}
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
}
