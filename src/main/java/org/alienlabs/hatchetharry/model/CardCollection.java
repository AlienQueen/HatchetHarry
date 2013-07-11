package org.alienlabs.hatchetharry.model;

import java.io.Serializable;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity
@XmlRootElement(name = "CardCollection")
@XmlAccessorType(XmlAccessType.FIELD)
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class CardCollection implements Serializable
{
	private static final long serialVersionUID = 1L;

	@Id
	private String id;

	@Column
	private String lang, name, altart, cost, color, type;
	@Column(length = 1024)
	@XmlAttribute(name = "set")
	private String expansionSet;
	@Column
	private String rarity, power, toughness;

	@Column(length = 1024)
	private String rules;

	private String printedname, printedtype, printedrules, flavor, watermark, cardnum, artist;
	@Column(length = 6000)
	private String sets;
	@Column
	private String rulings;

	public String getId()
	{
		return this.id;
	}

	public String getLang()
	{
		return this.lang;
	}

	public String getName()
	{
		return this.name;
	}

	public String getAltart()
	{
		return this.altart;
	}

	public String getCost()
	{
		return this.cost;
	}

	public String getColor()
	{
		return this.color;
	}

	public String getType()
	{
		return this.type;
	}

	public String getSet()
	{
		return this.expansionSet;
	}

	public String getRarity()
	{
		return this.rarity;
	}

	public String getPower()
	{
		return this.power;
	}

	public String getToughness()
	{
		return this.toughness;
	}

	public String getRules()
	{
		return this.rules;
	}

	public String getPrintedname()
	{
		return this.printedname;
	}

	public String getPrintedtype()
	{
		return this.printedtype;
	}

	public String getPrintedrules()
	{
		return this.printedrules;
	}

	public String getFlavor()
	{
		return this.flavor;
	}

	public String getWatermark()
	{
		return this.watermark;
	}

	public String getCardnum()
	{
		return this.cardnum;
	}

	public String getArtist()
	{
		return this.artist;
	}

	public String getSets()
	{
		return this.sets;
	}

	public String getRulings()
	{
		return this.rulings;
	}

	public void setId(final String _id)
	{
		this.id = _id;
	}

	public void setLang(final String _lang)
	{
		this.lang = _lang;
	}

	public void setName(final String _name)
	{
		this.name = _name;
	}

	public void setAltart(final String _altart)
	{
		this.altart = _altart;
	}

	public void setCost(final String _cost)
	{
		this.cost = _cost;
	}

	public void setColor(final String _color)
	{
		this.color = _color;
	}

	public void setType(final String _type)
	{
		this.type = _type;
	}

	public void setSet(final String _set)
	{
		this.expansionSet = _set;
	}

	public void setRarity(final String _rarity)
	{
		this.rarity = _rarity;
	}

	public void setPower(final String _power)
	{
		this.power = _power;
	}

	public void setToughness(final String _toughness)
	{
		this.toughness = _toughness;
	}

	public void setRules(final String _rules)
	{
		this.rules = _rules;
	}

	public void setPrintedname(final String _printedname)
	{
		this.printedname = _printedname;
	}

	public void setPrintedtype(final String _printedtype)
	{
		this.printedtype = _printedtype;
	}

	public void setPrintedrules(final String _printedrules)
	{
		this.printedrules = _printedrules;
	}

	public void setFlavor(final String _flavor)
	{
		this.flavor = _flavor;
	}

	public void setWatermark(final String _watermark)
	{
		this.watermark = _watermark;
	}

	public void setCardnum(final String _cardnum)
	{
		this.cardnum = _cardnum;
	}

	public void setArtist(final String _artist)
	{
		this.artist = _artist;
	}

	public void setSets(final String _sets)
	{
		this.sets = _sets;
	}

	public void setRulings(final String _rulings)
	{
		this.rulings = _rulings;
	}
}