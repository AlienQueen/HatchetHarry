package org.alienlabs.hatchetharry.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity
@Table(name = "Game")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Game implements Serializable
{
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long gameId;
	@Column(name = "VERSION", length = 20)
	private String version;
	@OneToMany(fetch = FetchType.EAGER, targetEntity = Player.class)
	@JoinTable(name = "Player_Game", joinColumns = @JoinColumn(name = "gameId"), inverseJoinColumns = @JoinColumn(name = "playerId"))
	private Set<Player> players = new HashSet<Player>();
	@Column
	private Long currentPlaceholderId = Long.valueOf(0L);
	@Column
	private Boolean isDrawMode = Boolean.FALSE;
	@Column
	private boolean pending = false;
	@Column
	private Integer desiredNumberOfPlayers = Integer.valueOf(0);
	@Column
	private Format desiredFormat;


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

	public Long getCurrentPlaceholderId()
	{
		return this.currentPlaceholderId;
	}

	public void setCurrentPlaceholderId(final Long _currentPlaceholderId)
	{
		this.currentPlaceholderId = _currentPlaceholderId;
	}

	public Boolean isDrawMode()
	{
		return this.isDrawMode;
	}

	public void setDrawMode(final Boolean drawMode)
	{
		this.isDrawMode = drawMode;
	}

	public String getVersion()
	{
		return this.version;
	}

	public void setVersion(final String _version)
	{
		this.version = _version;
	}

	public boolean isPending()
	{
		return this.pending;
	}

	public void setPending(final boolean _pending)
	{
		this.pending = _pending;
	}

	public Integer getDesiredNumberOfPlayers()
	{
		return this.desiredNumberOfPlayers;
	}

	public void setDesiredNumberOfPlayers(final Integer _desiredNumberOfPlayers)
	{
		this.desiredNumberOfPlayers = _desiredNumberOfPlayers;
	}

	public Format getDesiredFormat()
	{
		return this.desiredFormat;
	}

	public void setDesiredFormat(final Format _desiredFormat)
	{
		this.desiredFormat = _desiredFormat;
	}

	@Override
	public boolean equals(final Object o)
	{
		if (this == o)
		{
			return true;
		}
		if (!(o instanceof Game))
		{
			return false;
		}

		final Game game = (Game)o;

		if (this.gameId != null ? !this.gameId.equals(game.gameId) : game.gameId != null)
		{
			return false;
		}

		return true;
	}

	@Override
	public int hashCode()
	{
		return this.gameId != null ? this.gameId.hashCode() : 0;
	}

}
