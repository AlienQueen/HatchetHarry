package org.alienlabs.hatchetharry.model;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

@Entity
@Table(name = "Player")
public class Player implements Serializable
{
	private static final long serialVersionUID = 7963755937946852379L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long playerId;
	@ManyToMany(mappedBy = "players")
	private List<Game> game;
	@Column
	private String side;
	@Column
	private String name;
	@Column
	private String jsessionid;
	@Column
	private Long lifePoints;

	public Long getId()
	{
		return this.playerId;
	}

	public void setId(final Long _id)
	{
		this.playerId = _id;
	}

	public String getSide()
	{
		return this.side;
	}

	public void setSide(final String _side)
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

	public List<Game> getGame()
	{
		return this.game;
	}

	public void setGame(final List<Game> _game)
	{
		this.game = _game;
	}
}
