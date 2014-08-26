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
	private static final long serialVersionUID = 5336828396327485268L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long gameId;
    @Column(name="VERSION", length=20)
    private String version;
    @OneToMany(fetch = FetchType.EAGER, targetEntity=Player.class)
	@JoinTable(name = "Player_Game", joinColumns = @JoinColumn(name = "gameId"), inverseJoinColumns = @JoinColumn(name = "playerId"))
	private Set<Player> players = new HashSet<Player>();
	@Column
	private Long currentPlaceholderId = 0L;
	@Column
	private Boolean isDrawMode = false;


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

    public String getVersion() {
        return this.version;
    }

    public void setVersion(String _version) {
        this.version = _version;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Game)) return false;

        Game game = (Game) o;

        if (gameId != null ? !gameId.equals(game.gameId) : game.gameId != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return gameId != null ? gameId.hashCode() : 0;
    }
}
