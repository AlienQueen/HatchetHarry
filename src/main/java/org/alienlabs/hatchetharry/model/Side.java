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
	private static final long serialVersionUID = -1703518536709468323L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long sideId;
	@Column
	private String wicketId;
	@Column
	private String sideName;
	@Column(name = "uuid")
	private String uuid;
	@ManyToOne(fetch = FetchType.EAGER)
	@Cascade({ CascadeType.SAVE_UPDATE })
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
}
