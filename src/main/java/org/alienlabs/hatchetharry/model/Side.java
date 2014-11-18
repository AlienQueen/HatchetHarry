package org.alienlabs.hatchetharry.model;

import java.io.Serializable;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

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
	@Column(name = "VERSION", length = 20)
	private String version;
	@Column
	private String wicketId;
	@Column
	private String sideName;
	@Column(name = "uuid")
	private String uuid;
	@ManyToOne(fetch = FetchType.EAGER, targetEntity = Game.class)
	@Cascade(CascadeType.ALL)
	private Game game;
	@Column
	private Long x = Long.valueOf(64l); // x coordinate
	@Column
	private Long y = Long.valueOf(64l); // y coordinate

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
		if (!(o instanceof Side))
		{
			return false;
		}

		final Side side = (Side)o;

		if (this.sideId != null ? !this.sideId.equals(side.sideId) : side.sideId != null)
		{
			return false;
		}

		return true;
	}

	@Override
	public int hashCode()
	{
		return this.sideId != null ? this.sideId.hashCode() : 0;
	}

}
