package org.alienlabs.hatchetharry.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

@Entity
@Table(name = "Game")
public class Game implements Serializable
{
	private static final long serialVersionUID = 5336828396327485268L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long gameId;
	@ManyToMany
	@JoinTable(name = "Player_Game", joinColumns = @JoinColumn(name = "playerId"), inverseJoinColumns = @JoinColumn(name = "gameId"))
	private List<Player> players = new ArrayList<Player>();

	public Long getId()
	{
		return this.gameId;
	}

	public void setId(final Long _id)
	{
		this.gameId = _id;
	}

	public List<Player> getPlayers()
	{
		return this.players;
	}

	public void setPlayers(final List<Player> _players)
	{
		this.players = _players;
	}

}
