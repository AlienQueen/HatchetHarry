package org.alienlabs.hatchetharry.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

@Entity
@Table(name = "Counter")
public class Counter implements Serializable
{
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long counterId;
	@Column
	private String counterName;
	@Column
	private Long numberOfCounters = 0l;

	@ManyToOne(fetch = FetchType.EAGER)
	@Cascade({ CascadeType.ALL })
	@JoinColumn(name = "card")
	private MagicCard card;

	@ManyToOne(fetch = FetchType.EAGER)
	@Cascade({ CascadeType.ALL })
	@JoinColumn(name = "token")
	private Token token;

	public String getCounterName()
	{
		return this.counterName;
	}

	public void setCounterName(final String _counterName)
	{
		this.counterName = _counterName;
	}

	public Long getNumberOfCounters()
	{
		return this.numberOfCounters;
	}

	public void setNumberOfCounters(final Long _numberOfCounters)
	{
		this.numberOfCounters = _numberOfCounters;
	}

	public MagicCard getCard()
	{
		return this.card;
	}

	public void setCard(final MagicCard _card)
	{
		this.card = _card;
	}

	public Long getId()
	{
		return this.counterId;
	}

	public void setId(final Long _id)
	{
		this.counterId = _id;
	}

	public Token getToken()
	{
		return this.token;
	}

	public void setToken(final Token _token)
	{
		this.token = _token;
	}

}
