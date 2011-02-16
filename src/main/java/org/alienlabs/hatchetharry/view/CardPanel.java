package org.alienlabs.hatchetharry.view;

import org.apache.wicket.ResourceReference;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("serial")
public class CardPanel extends Panel
{
	static final Logger logger = LoggerFactory.getLogger(CardPanel.class);

	public CardPanel(final String id)
	{
		super(id);

		final Button menutoggleButton = new Button("menutoggleButton", new Model<String>(""));
		final Image img = new Image("menutoggleImage", new ResourceReference(
				"cards/BalduvianHorde_small.jpg"));
		menutoggleButton.add(img);
		menutoggleButton.setOutputMarkupId(true);
		menutoggleButton.setMarkupId("contextMenu");
		this.add(menutoggleButton);
	}
}
