package org.alienlabs.hatchetharry.view.component;

import org.alienlabs.hatchetharry.view.page.HomePage;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.request.resource.PackageResourceReference;

public class TooltipPanel extends Panel
{
	private static final long serialVersionUID = 1L;

	public TooltipPanel(final String id, final String bigImage)
	{
		super(id);

		final Image bubbleTipImg1 = new Image("bubbleTipImg1", new PackageResourceReference(
				HomePage.class, bigImage));
		this.add(bubbleTipImg1);
	}

}
