package org.alienlabs.hatchetharry.model;

import org.alienlabs.hatchetharry.view.page.HomePage;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.resource.PackageResourceReference;

@SuppressWarnings("serial")
public class HandCardImageLoader<T> extends ListItem<MagicCard>
{

	public HandCardImageLoader(final int index, final IModel<MagicCard> model)
	{
		super(index, model);
	}

	public void populate(final ListItem<MagicCard> item)
	{
		item.add(new Image("image", new PackageResourceReference(HomePage.class, "image"))
				.add(new AttributeModifier("src", true, new Model<String>(item.getModelObject()
						.getBigImageFilename()))));
		item.add(new Image("thumb", new PackageResourceReference(HomePage.class, "thumb"))
				.add(new AttributeModifier("src", true, new Model<String>(item.getModelObject()
						.getSmallImageFilename()))));
		item.add(new Label("title", item.getModelObject().getTitle()));
		item.add(new Label("description", item.getModelObject().getDescription()));
	}
}
