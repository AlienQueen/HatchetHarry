package org.alienlabs.hatchetharry.model;

import java.io.Serializable;

import javax.persistence.*;
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
    @Column(name="VERSION", length=20)
    private String version;
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

	public void setId(final String _id)
	{
		this.id = _id;
	}

	public String getLang()
	{
		return this.lang;
	}

	public void setLang(final String _lang)
	{
		this.lang = _lang;
	}

	public String getName()
	{
		return this.name;
	}

	public void setName(final String _name)
	{
		this.name = _name;
	}

	public String getAltart()
	{
		return this.altart;
	}

	public void setAltart(final String _altart)
	{
		this.altart = _altart;
	}

	public String getCost()
	{
		return this.cost;
	}

	public void setCost(final String _cost)
	{
		this.cost = _cost;
	}

	public String getColor()
	{
		return this.color;
	}

	public void setColor(final String _color)
	{
		this.color = _color;
	}

	public String getType()
	{
		return this.type;
	}

	public void setType(final String _type)
	{
		this.type = _type;
	}

	public String getSet()
	{
		return this.expansionSet;
	}

	public void setSet(final String _set)
	{
		this.expansionSet = _set;
	}

	public String getRarity()
	{
		return this.rarity;
	}

	public void setRarity(final String _rarity)
	{
		this.rarity = _rarity;
	}

	public String getPower()
	{
		return this.power;
	}

	public void setPower(final String _power)
	{
		this.power = _power;
	}

	public String getToughness()
	{
		return this.toughness;
	}

	public void setToughness(final String _toughness)
	{
		this.toughness = _toughness;
	}

	public String getRules()
	{
		return this.rules;
	}

	public void setRules(final String _rules)
	{
		this.rules = _rules;
	}

	public String getPrintedname()
	{
		return this.printedname;
	}

	public void setPrintedname(final String _printedname)
	{
		this.printedname = _printedname;
	}

	public String getPrintedtype()
	{
		return this.printedtype;
	}

	public void setPrintedtype(final String _printedtype)
	{
		this.printedtype = _printedtype;
	}

	public String getPrintedrules()
	{
		return this.printedrules;
	}

	public void setPrintedrules(final String _printedrules)
	{
		this.printedrules = _printedrules;
	}

	public String getFlavor()
	{
		return this.flavor;
	}

	public void setFlavor(final String _flavor)
	{
		this.flavor = _flavor;
	}

	public String getWatermark()
	{
		return this.watermark;
	}

	public void setWatermark(final String _watermark)
	{
		this.watermark = _watermark;
	}

	public String getCardnum()
	{
		return this.cardnum;
	}

	public void setCardnum(final String _cardnum)
	{
		this.cardnum = _cardnum;
	}

	public String getArtist()
	{
		return this.artist;
	}

	public void setArtist(final String _artist)
	{
		this.artist = _artist;
	}

	public String getSets()
	{
		return this.sets;
	}

	public void setSets(final String _sets)
	{
		this.sets = _sets;
	}

	public String getRulings()
	{
		return this.rulings;
	}

	public void setRulings(final String _rulings)
	{
		this.rulings = _rulings;
	}

    public String getVersion() {
        return this.version;
    }

    public void setVersion(String _version) {
        this.version = _version;
    }

}
