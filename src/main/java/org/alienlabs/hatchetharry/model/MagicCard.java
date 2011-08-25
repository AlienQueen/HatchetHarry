package org.alienlabs.hatchetharry.model;

import java.io.Serializable;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.apache.wicket.model.Model;

import com.googlecode.wicketslides.SlideshowImage;

@Entity
public class MagicCard implements SlideshowImage, Serializable
{
	private static final long serialVersionUID = -5115712217304615521L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	@Column
	private String smallImageFilename = "";
	@Column
	private String bigImageFilename = "";
	@Column
	private String title = "";
	@Column
	private String description = "";
	@Column
	private String uuid;
	@Column
	private Long gameId;

	public MagicCard()
	{
	}

	public MagicCard(final String _smallImageFilename, final String _bigImageFilename,
			final String _title, final String _description)
	{
		this.smallImageFilename = _smallImageFilename;
		this.bigImageFilename = _bigImageFilename;
		this.title = _title;
		this.description = _description;
	}

	public Long getId()
	{
		return this.id;
	}

	public void setId(final Long _id)
	{
		this.id = _id;
	}

	@Override
	public String getLow()
	{
		return this.smallImageFilename;
	}

	@Override
	public String getHigh()
	{
		return this.smallImageFilename;
	}

	@Override
	public String getThumb()
	{
		return this.smallImageFilename;
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

	public String getSmallImageFilename()
	{
		return this.smallImageFilename;
	}


	public void setSmallImageFilename(final String _smallImagefilename)
	{
		this.smallImageFilename = _smallImagefilename;
	}

	@Override
	public Model<String> getModel()
	{
		return new Model<String>(this.smallImageFilename);
	}

	public String getUuid()
	{
		return this.uuid;
	}

	public void setUuid(final String _uuid)
	{
		this.uuid = _uuid;
	}


	public UUID getUuidObject()
	{
		return UUID.fromString(this.uuid);
	}

	public void setUuidObject(final UUID _uuid)
	{
		this.uuid = _uuid.toString();
	}

	public String getBigImageFilename()
	{
		return this.bigImageFilename;
	}

	public void setBigImageFilename(final String _bigImageFilename)
	{
		this.bigImageFilename = _bigImageFilename;
	}

	public Long getGameId()
	{
		return this.gameId;
	}

	public void setGameId(final Long _gameId)
	{
		this.gameId = _gameId;
	}

}
