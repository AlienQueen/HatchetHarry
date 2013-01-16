package org.alienlabs.hatchetharry.view.component;

import org.alienlabs.hatchetharry.view.page.HomePage;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.request.resource.PackageResourceReference;

public class TooltipPanel extends Panel
{
	private static final long serialVersionUID = 1L;
	private final String ownerSide;

	public TooltipPanel(final String id, final String bigImage, final String _ownerSide)
	{
		super(id);
		this.ownerSide = _ownerSide;

		final Image bubbleTipImg1 = new Image("bubbleTipImg1", new PackageResourceReference(
				HomePage.class, bigImage));

		if ("infrared".equals(this.ownerSide))
		{
			bubbleTipImg1.add(new AttributeModifier("style", "border: 1px solid red;"));
		}
		else if ("ultraviolet".equals(this.ownerSide))
		{
			bubbleTipImg1.add(new AttributeModifier("style", "border: 1px solid purple;"));
		}

		this.add(bubbleTipImg1);
	}

}
