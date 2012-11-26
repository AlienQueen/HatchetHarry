package org.alienlabs.hatchetharry.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

@Entity
@Table(name = "Game")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Game implements Serializable
{
	private static final long serialVersionUID = 5336828396327485268L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long gameId;
	@ManyToMany(fetch = FetchType.EAGER)
	@Cascade({ CascadeType.ALL })
	@JoinTable(name = "Player_Game", joinColumns = @JoinColumn(name = "gameId"), inverseJoinColumns = @JoinColumn(name = "playerId"))
	private Set<Player> players = new HashSet<Player>();
	@OneToMany(mappedBy = "game")
	private List<Side> sides = new ArrayList<Side>();
	@Column(nullable = true)
	private String firstPlayerPageCometUuid;
	@Column(nullable = true)
	private String secondPlayerPageCometUuid;


	public Long getId()
	{
		return this.gameId;
	}

	public void setId(final Long _id)
	{
		this.gameId = _id;
	}

	public Set<Player> getPlayers()
	{
		return this.players;
	}

	public void setPlayers(final Set<Player> _players)
	{
		this.players = _players;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = (prime * result) + ((this.gameId == null) ? 0 : this.gameId.hashCode());
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
		final Game other = (Game)obj;
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
		return true;
	}

	public List<Side> getSides()
	{
		return this.sides;
	}

	public void setSides(final List<Side> _sides)
	{
		this.sides = _sides;
	}

	public String getFirstPlayerPageCometUuid()
	{
		return this.firstPlayerPageCometUuid;
	}

	public void setFirstPlayerPageCometUuid(final String _firstPlayerPageCometUuid)
	{
		this.firstPlayerPageCometUuid = _firstPlayerPageCometUuid;
	}

	public String getSecondPlayerPageCometUuid()
	{
		return this.secondPlayerPageCometUuid;
	}

	public void setSecondPlayerPageCometUuid(final String _secondPlayerPageCometUuid)
	{
		this.secondPlayerPageCometUuid = _secondPlayerPageCometUuid;
	}

}
