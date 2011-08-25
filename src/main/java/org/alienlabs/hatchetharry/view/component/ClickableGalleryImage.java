package org.alienlabs.hatchetharry.view.component;

import java.util.UUID;

import org.alienlabs.hatchetharry.model.MagicCard;
import org.alienlabs.hatchetharry.service.PersistenceService;
import org.alienlabs.hatchetharry.view.page.HomePage;
import org.apache.wicket.ResourceReference;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

public class ClickableGalleryImage extends Panel
{
	@SpringBean
	private PersistenceService persistenceService;

	private final WebMarkupContainer cardParent;
	private final WebMarkupContainer cardPlaceholder;
	private final WebMarkupContainer parent;
	private final String bigImage;
	private final String name;
	private final String comment;
	private final Long cardToLookFor;

	private final int indexOfClickedCard;

	private final int indexOfNextCard;

	private static final Logger logger = LoggerFactory.getLogger(ClickableGalleryImage.class);

	private static final long serialVersionUID = 1091658635172607303L;

	public ClickableGalleryImage(final String id, final WebMarkupContainer _parent,
			final String _bigImage, final String _imageInHand, final String _smallImage,
			final String _name, final String _comment, final Long _cardToLookFor,
			final int _indexOfClickedCard, final int _indexOfNextCard)
	{
		super(id);
		this.parent = _parent;
		this.bigImage = _bigImage;
		this.name = _name;
		this.comment = _comment;
		this.cardToLookFor = _cardToLookFor;
		this.indexOfClickedCard = _indexOfClickedCard;
		this.indexOfNextCard = _indexOfNextCard;

		final MagicCard hammer = this.persistenceService.getNthCardOfGame(this.cardToLookFor);
		final UUID uuid;

		if (null == hammer)
		{
			uuid = UUID.randomUUID();
		}
		else
		{
			uuid = hammer.getUuidObject();
		}

		final WebMarkupContainer image = new WebMarkupContainer("image");
		image.setMarkupId("image" + uuid);
		image.setOutputMarkupId(true);
		this.add(image);

		final Image handImagePlaceholder = new Image("handImagePlaceholder", new ResourceReference(
				HomePage.class, this.bigImage));
		handImagePlaceholder.setMarkupId("placeholder" + uuid.toString().replace("-", "_"));
		handImagePlaceholder.setOutputMarkupId(true);
		image.add(handImagePlaceholder);

		final Label nameLabel = new Label("name", this.name);
		final Label commentLabel = new Label("comment", this.comment);
		image.add(nameLabel, commentLabel);

		this.cardParent = new WebMarkupContainer("cardParent");
		this.cardPlaceholder = new WebMarkupContainer("cardPlaceholder");
		this.cardParent.add(this.cardPlaceholder);
		this.cardParent.setOutputMarkupId(true);
		this.cardPlaceholder.setOutputMarkupId(true);
		image.add(this.cardParent);

		final PlayCardFromHandBehavior b = new PlayCardFromHandBehavior(uuid, this.parent,
				this.indexOfClickedCard, this.indexOfNextCard);
		handImagePlaceholder.add(b);

		final Image handImageLink = new Image("handImageLink", new ResourceReference(
				HomePage.class, "image/playCard.png"));
		handImageLink.setMarkupId("placeholder" + uuid.toString().replace("-", "_") + "_l");
		handImageLink.setOutputMarkupId(true);
		image.add(handImageLink);


		ClickableGalleryImage.logger.info("buildHand UUID: " + uuid);
	}

	@Required
	public void setPersistenceService(final PersistenceService _persistenceService)
	{
		this.persistenceService = _persistenceService;
	}

}
