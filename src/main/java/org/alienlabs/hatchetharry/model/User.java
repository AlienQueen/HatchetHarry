package org.alienlabs.hatchetharry.model;

import java.io.Serializable;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

@Entity
@Table(name = "User")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class User implements Serializable
{
	private static final long serialVersionUID = 1L;

	@Id
	private String username;
	@Column(name = "VERSION", length = 20)
	private String version;
	@Column
	private String login;
	@Column
	private String privateIdentity;
	@ManyToOne(fetch = FetchType.EAGER, targetEntity = Player.class)
	@Cascade(CascadeType.ALL)
	private Player player;
	@Column
	private String identity;
	@Column
	private String password; // TODO passwords in plain-text
	@Column
	private Boolean isFacebook = Boolean.FALSE;
	@Column
	private String realm;

	public User()
	{
	}

	public String getUsername()
	{
		return this.username;
	}

	public void setUsername(final String _username)
	{
		this.username = _username;
	}

	public String getLogin()
	{
		return this.login;
	}

	public void setLogin(final String _login)
	{
		this.login = _login;
	}

	public Player getPlayer()
	{
		return this.player;
	}

	public void setPlayer(final Player _player)
	{
		this.player = _player;
	}

	public String getIdentity()
	{
		return this.identity;
	}

	public void setIdentity(final String _identity)
	{
		this.identity = _identity;
	}

	public String getPassword()
	{
		return this.password;
	}

	public void setPassword(final String _password)
	{
		this.password = _password;
	}

	public Boolean isFacebook()
	{
		return this.isFacebook;
	}

	public void setFacebook(final Boolean facebook)
	{
		this.isFacebook = facebook;
	}

	public String getRealm()
	{
		return this.realm;
	}

	public void setRealm(final String _realm)
	{
		this.realm = _realm;
	}

	public String getPrivateIdentity()
	{
		return this.privateIdentity;
	}

	public void setPrivateIdentity(final String _privateIdentity)
	{
		this.privateIdentity = _privateIdentity;
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
