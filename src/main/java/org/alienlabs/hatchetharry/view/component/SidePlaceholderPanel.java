package org.alienlabs.hatchetharry.view.component;

import java.util.UUID;

import org.alienlabs.hatchetharry.HatchetHarrySession;
import org.alienlabs.hatchetharry.model.Player;
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

	private final HomePage homePage;
	private final String side;

	private final Player player;

	public SidePlaceholderPanel(final String id, final String _side, final HomePage hp,
			final UUID _uuid, final Player _player)
	{
		super(id);
		this.setOutputMarkupId(true);

		this.homePage = hp;
		this.uuid = _uuid;
		this.side = _side;
		this.player = _player;

		this.add(new SidePlaceholderMoveBehavior(this, hp.getSideParent(), this.uuid,
				HatchetHarrySession.get().getGameId(), this.player));

		final WebMarkupContainer sidePlaceholder = new WebMarkupContainer("sidePlaceholder");
		sidePlaceholder.setOutputMarkupId(true);
		sidePlaceholder.setMarkupId("sidePlaceholder" + this.uuid.toString().replace("-", "_"));
		sidePlaceholder.add(new AttributeModifier("style",
				"position: absolute; top: 300px; left: 300px;"));
		sidePlaceholder.add(new AttributeModifier("class", "sidePlaceholder"));

		this.add(new SidePlaceholderMoveBehavior(this, this.homePage.getSideParent(), this.uuid,
				HatchetHarrySession.get().getGameId(), this.player));

		final Image handleImage = new Image("handleImage", new PackageResourceReference(
				"images/arrow.png"));
		handleImage.setOutputMarkupId(true);
		handleImage.setMarkupId("handleImage" + this.uuid.toString().replace("-", "_"));

		final String image = ("infrared".equals(this.side))
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
