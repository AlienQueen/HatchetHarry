package org.alienlabs.hatchetharry.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class CardCollection implements Serializable
{
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public final String id;

	@Column
	public final String lang, name, altart, cost, color, type, set, rarity, power, toughness,
			rules, printedname, printedtype, printedrules, flavor, watermark, cardnum, artist,
			sets, rulings;

	public CardCollection(final String _id, final String _lang, final String _name,
			final String _altart, final String _cost, final String _color, final String _type,
			final String _set, final String _rarity, final String _power, final String _toughness,
			final String _rules, final String _printedname, final String _printedtype,
			final String _printedrules, final String _flavor, final String _watermark,
			final String _cardnum, final String _artist, final String _sets, final String _rulings)
	{
		this.id = _id;
		this.lang = _lang;
		this.name = _name;
		this.altart = _altart;
		this.cost = _cost;
		this.color = _color;
		this.type = _type;
		this.set = _set;
		this.rarity = _rarity;
		this.power = _power;
		this.toughness = _toughness;
		this.rules = _rules;
		this.printedname = _printedname;
		this.printedtype = _printedtype;
		this.printedrules = _printedrules;
		this.flavor = _flavor;
		this.watermark = _watermark;
		this.cardnum = _cardnum;
		this.artist = _artist;
		this.sets = _sets;
		this.rulings = _rulings;
	}
}