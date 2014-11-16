package org.alienlabs.hatchetharry.view.component.gui;

import java.util.UUID;

import org.alienlabs.hatchetharry.model.Player;
import org.alienlabs.hatchetharry.model.Side;
import org.alienlabs.hatchetharry.view.page.HomePage;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.request.resource.PackageResourceReference;

public class SidePlaceholderPanel extends Panel
{
	private static final long serialVersionUID = 1L;

	private final UUID uuid;
	private final String side;

	public SidePlaceholderPanel(final String id, final String _side, final HomePage hp,
			final UUID _uuid, final Player player)
	{
		super(id);
		this.setOutputMarkupId(true);

		this.uuid = _uuid;
		this.side = _side;

		final Side mySide = player.getSide();


		this.add(new SidePlaceholderMoveBehavior(this, hp.getSideParent(), this.uuid, player));

		final WebMarkupContainer sidePlaceholder = new WebMarkupContainer("sidePlaceholder");
		sidePlaceholder.setOutputMarkupId(true);
		final String uuidValidForJs = this.uuid.toString().replace("-", "_");
		sidePlaceholder.setMarkupId("sidePlaceholder" + uuidValidForJs);
		sidePlaceholder.add(new AttributeModifier("style", "position: absolute; top: "
				+ mySide.getY() + "px; left: " + mySide.getX() + "px;"));
		sidePlaceholder.add(new AttributeModifier("class", "sidePlaceholder"));

		this.add(new SidePlaceholderMoveBehavior(this, hp.getSideParent(), this.uuid, player));

		final ExternalImage handleImage = new ExternalImage("handleImage", "image/arrow.png");
		handleImage.setOutputMarkupId(true);
		handleImage.setMarkupId("handleImage" + uuidValidForJs);

		final String image = "infrared".equals(this.side)
				? "image/logobouclierrouge.png"
				: "image/logobouclierviolet.png";

		final Image cardImage = new Image("sidePlaceholderImage", new PackageResourceReference(
				HomePage.class, image));
		cardImage.setOutputMarkupId(true);
		cardImage.setMarkupId("side" + this.uuid.toString());

		sidePlaceholder.add(handleImage, cardImage);
		this.add(sidePlaceholder);
	}

	public UUID getUuid()
	{
		return this.uuid;
	}

	public String getSide()
	{
		return this.side;
	}

}
