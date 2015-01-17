package org.alienlabs.hatchetharry.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import java.io.Serializable;

/**
 * Created by nostromo on 17/01/15.
 */
@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class ParentMessage implements Serializable
{
	private static final long serialVersionUID = 1L;

	@Id
    @GeneratedValue(strategy = GenerationType.TABLE)
	protected Long id;
	@Column(name = "VERSION", length = 20)
	protected String version;
	@Column(name = "gameId")
	protected Long gameId;
	@Column(name = "message")
	protected String message;

	public Long getId()
	{
		return this.id;
	}

	public void setId(final Long _id)
	{
		this.id = _id;
	}

	public Long getGameId()
	{
		return this.gameId;
	}

	public void setGameId(final Long _gameId)
	{
		this.gameId = _gameId;
	}

	public String getMessage()
	{
		return this.message;
	}

	public void setMessage(final String _message)
	{
		this.message = _message;
	}

	public String getVersion()
	{
		return this.version;
	}

	public void setVersion(final String _version)
	{
		this.version = _version;
	}

}
