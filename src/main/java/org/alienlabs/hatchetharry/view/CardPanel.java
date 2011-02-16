package org.alienlabs.hatchetharry.view;

import org.apache.wicket.ResourceReference;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.panel.Panel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("serial")
public class CardPanel extends Panel
{
	static final Logger logger = LoggerFactory.getLogger(CardPanel.class);

	public CardPanel(final String id)
	{
		super(id);

		final Image menutoggleImage = new Image("menutoggleImage", new ResourceReference(
				"handCards/BalduvianHorde_small.jpg"));
		this.add(menutoggleImage);
	}
}
