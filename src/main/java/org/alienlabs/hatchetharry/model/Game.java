package org.alienlabs.hatchetharry.model;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "Game")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Game implements Serializable {
	private static final long serialVersionUID = 5336828396327485268L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long gameId;
	@OneToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "Player_Game", joinColumns = @JoinColumn(name = "gameId"), inverseJoinColumns = @JoinColumn(name = "playerId"))
	private Set<Player> players = new HashSet<Player>();
	@Column
	private Long currentPlaceholderId = 0L;
	@Column
	private Boolean isDrawMode = false;


	public Long getId() {
		return this.gameId;
	}

	public void setId(final Long _id) {
		this.gameId = _id;
	}

	public Set<Player> getPlayers() {
		return this.players;
	}

	public void setPlayers(final Set<Player> _players) {
		this.players = _players;
	}

	public Long getCurrentPlaceholderId() {
		return this.currentPlaceholderId;
	}

	public void setCurrentPlaceholderId(final Long _currentPlaceholderId) {
		this.currentPlaceholderId = _currentPlaceholderId;
	}

	public Boolean isDrawMode() {
		return this.isDrawMode;
	}

	public void setDrawMode(final Boolean drawMode) {
		this.isDrawMode = drawMode;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = (prime * result) + ((this.players == null) ? 0 : this.players.hashCode());
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (this.getClass() != obj.getClass()) {
			return false;
		}
		final Game other = (Game) obj;
		if (this.players == null) {
			if (other.players != null) {
				return false;
			}
		} else if (!this.players.equals(other.players)) {
			return false;
		}
		return true;
	}

}
