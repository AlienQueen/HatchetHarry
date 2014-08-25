package org.alienlabs.hatchetharry.model;

import java.io.Serializable;

import javax.persistence.*;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

@Entity
@Table(name = "Side", indexes = { @Index(columnList = "uuid") })
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Side implements Serializable
{
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long sideId;
    @Column(name="VERSION", length=20)
    private String version;
	@Column
	private String wicketId;
	@Column
	private String sideName;
	@Column(name = "uuid")
	private String uuid;
	@ManyToOne(fetch = FetchType.EAGER, targetEntity=Game.class)
    @Cascade(CascadeType.ALL)
	private Game game;
	@Column
	private Long x = 64l; // x coordinate
	@Column
	private Long y = 64l; // y coordinate

	public Long getSideId()
	{
		return this.sideId;
	}

	public void setSideId(final Long _sideId)
	{
		this.sideId = _sideId;
	}

	public String getWicketId()
	{
		return this.wicketId;
	}

	public void setWicketId(final String _wicketId)
	{
		this.wicketId = _wicketId;
	}

	public String getSideName()
	{
		return this.sideName;
	}

	public void setSideName(final String _side)
	{
		this.sideName = _side;
	}

	public String getUuid()
	{
		return this.uuid;
	}

	public void setUuid(final String _uuid)
	{
		this.uuid = _uuid;
	}

	public Game getGame()
	{
		return this.game;
	}

	public void setGame(final Game _game)
	{
		this.game = _game;
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

    public String getVersion() {
        return this.version;
    }

    public void setVersion(String _version) {
        this.version = _version;
    }

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = (prime * result) + ((this.game == null) ? 0 : this.game.hashCode());
		result = (prime * result) + ((this.sideId == null) ? 0 : this.sideId.hashCode());
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
		final Side other = (Side)obj;
		if (this.game == null)
		{
			if (other.game != null)
			{
				return false;
			}
		}
		else if (!this.game.equals(other.game))
		{
			return false;
		}
		if (this.sideId == null)
		{
			if (other.sideId != null)
			{
				return false;
			}
		}
		else if (!this.sideId.equals(other.sideId))
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
