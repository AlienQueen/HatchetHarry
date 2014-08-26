package org.alienlabs.hatchetharry.model;

import java.io.Serializable;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity
@Table(name = "Counter", indexes = { @Index(columnList = "card"), @Index(columnList = "token") })
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Counter implements Serializable, Comparable<Counter>
{
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long counterId;
    @Column(name="VERSION", length=20)
    private String version;
	@Column
	private String counterName;
	@Column
	private Long numberOfCounters = 0l;

	@ManyToOne(fetch = FetchType.EAGER, targetEntity=MagicCard.class)
	@JoinColumn(name = "card")
	private MagicCard card;

	@ManyToOne(fetch = FetchType.EAGER, targetEntity=Token.class)
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

    public String getVersion() {
        return this.version;
    }

    public void setVersion(String _version) {
        this.version = _version;
    }

	/**
	 * This class defines a compareTo(...) method and doesn't inherit its equals() method from
	 * java.lang.Object. Generally, the value of compareTo should return zero if and only if equals
	 * returns true. If this is violated, weird and unpredictable failures will occur in classes
	 * such as PriorityQueue. In Java 5 the PriorityQueue.remove method uses the compareTo method,
	 * while in Java 6 it uses the equals method. From the JavaDoc for the compareTo method in the
	 * Comparable interface: It is strongly recommended, but not strictly required that
	 * (x.compareTo(y)==0) == (x.equals(y)). Generally speaking, any class that implements the
	 * Comparable interface and violates this condition should clearly indicate this fact. The
	 * recommended language is
	 * "Note: this class has a natural ordering that is inconsistent with equals."
	 */
	@Override
	public int compareTo(final Counter c)
	{
		if (this.equals(c))
		{
			return 0;
		}

		if (this.getCounterName().equals(c.getCounterName()))
		{
			return -1;
		}
		return this.getCounterName().compareTo(c.getCounterName());
	}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Counter)) return false;

        Counter counter = (Counter) o;

        if (counterId != null ? !counterId.equals(counter.counterId) : counter.counterId != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return counterId != null ? counterId.hashCode() : 0;
    }
}
