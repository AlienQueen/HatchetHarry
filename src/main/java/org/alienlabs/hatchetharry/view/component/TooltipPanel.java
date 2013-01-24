package org.alienlabs.hatchetharry.view.component;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.panel.Panel;

public class TooltipPanel extends Panel
{
	private static final long serialVersionUID = 1L;
	private final String ownerSide;

	public TooltipPanel(final String id, final String bigImage, final String _ownerSide)
	{
		super(id);
		this.ownerSide = _ownerSide;

		// TODO OK?
		final ExternalImage bubbleTipImg1 = new ExternalImage("bubbleTipImg1", bigImage);
		// final Image bubbleTipImg1 = new Image("bubbleTipImg1", new
		// PackageResourceReference(
		// HomePage.class, bigImage));

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
