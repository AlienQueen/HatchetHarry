package org.alienlabs.hatchetharry.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.apache.wicket.model.Model;

import com.googlecode.wicketslides.SlideshowImage;

@Entity
public class MagicCard implements SlideshowImage, Serializable
{

	@Id
	@GeneratedValue
	private long id;
	@Column
	private String filename = "";
	@Column
	private String title = "";
	@Column
	private String description = "";

	public MagicCard()
	{
	}

	public MagicCard(final String _filename, final String _title, final String _description)
	{
		this.filename = _filename;
		this.title = _title;
		this.description = _description;
	}

	public long getId()
	{
		return this.id;
	}

	public void setId(final long _id)
	{
		this.id = _id;
	}

	@Override
	public String getLow()
	{
		return this.filename;
	}

	@Override
	public String getHigh()
	{
		return this.filename;
	}

	@Override
	public String getThumb()
	{
		return this.filename;
	}

	@Override
	public String getTitle()
	{
		return this.title;
	}

	@Override
	public String getDescription()
	{
		return this.description;
	}

	public String getFilename()
	{
		return this.filename;
	}

	@Override
	public Model<String> getModel()
	{
		return new Model<String>(this.filename);
	}

}
