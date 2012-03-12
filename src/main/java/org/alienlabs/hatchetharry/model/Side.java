package org.alienlabs.hatchetharry.model;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "Side")
public class Side implements Serializable
{
	private static final long serialVersionUID = -1703518536709468323L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long sideId;
	@Column
	private String wicketId;
	@Column
	private String side;
	@Column
	private String uuid;
	@ManyToOne(cascade = { CascadeType.MERGE })
	@JoinColumn(name = "side_game")
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

	public String getSide()
	{
		return this.side;
	}

	public void setSide(final String _side)
	{
		this.side = _side;
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
