package org.alienlabs.hatchetharry.view.component;

import java.util.List;

import org.alienlabs.hatchetharry.HatchetHarrySession;
import org.alienlabs.hatchetharry.model.MagicCard;
import org.alienlabs.hatchetharry.service.PersistenceService;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.springframework.beans.factory.annotation.Required;

public class HandComponent extends Panel
{
	private static final long serialVersionUID = 1L;

	@SpringBean
	private PersistenceService persistenceService;

	private final WebMarkupContainer handCardsPlaceholder;
	private final ListView<MagicCard> allCards;
	private final WebMarkupContainer thumbsPlaceholder;

	public HandComponent(final String id)
	{
		super(id);

		this.setOutputMarkupId(true);
		this.setMarkupId("handGallery");

		this.handCardsPlaceholder = new WebMarkupContainer("handCardsPlaceholder");
		this.handCardsPlaceholder.setOutputMarkupId(true);

		final List<MagicCard> allCardsInHand = this.persistenceService
				.getAllCardsInHandForAGameAndAPlayer(HatchetHarrySession.get().getPlayer()
						.getGame().getId(), HatchetHarrySession.get().getPlayer().getId(),
						HatchetHarrySession.get().getPlayer().getDeck().getDeckId());

		this.allCards = new ListView<MagicCard>("handCards", allCardsInHand)
		{
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(final ListItem<MagicCard> item)
			{
				HatchetHarrySession.get().addCardIdInHand(item.getIndex(), item.getIndex());
				final MagicCard card = item.getModelObject();

				final WebMarkupContainer wrapper = new WebMarkupContainer("wrapper");
				wrapper.setMarkupId("wrapper" + item.getIndex());
				wrapper.setOutputMarkupId(true);

				// TODO OK?
				final ExternalImage handImagePlaceholder = new ExternalImage(
						"handImagePlaceholder", card.getBigImageFilename());
				// final Image handImagePlaceholder = new
				// Image("handImagePlaceholder",
				// new PackageResourceReference(HomePage.class,
				// card.getBigImageFilename()));
				handImagePlaceholder.setMarkupId("placeholder" + card.getUuid().replace("-", "_"));
				handImagePlaceholder.setOutputMarkupId(true);

				final Label titlePlaceholder = new Label("titlePlaceholder", card.getTitle());
				titlePlaceholder.setMarkupId("placeholder" + card.getUuid().replace("-", "_")
						+ "_placeholder");
				titlePlaceholder.setOutputMarkupId(true);

				wrapper.add(handImagePlaceholder, titlePlaceholder);
				item.add(wrapper);

			}
		};
		this.handCardsPlaceholder.add(this.allCards);
		this.add(this.handCardsPlaceholder);

		this.thumbsPlaceholder = new WebMarkupContainer("thumbsPlaceholder");
		final ListView<MagicCard> thumbs = new ListView<MagicCard>("thumbs", allCardsInHand)
		{
			private static final long serialVersionUID = -787466183866875L;

			@Override
			protected void populateItem(final ListItem<MagicCard> item)
			{
				final MagicCard card = item.getModelObject();

				final WebMarkupContainer crossLinkDiv = new WebMarkupContainer("crossLinkDiv");
				crossLinkDiv.setMarkupId("cross-link-div" + item.getIndex());
				crossLinkDiv.setOutputMarkupId(true);

				final WebMarkupContainer crossLink = new WebMarkupContainer("crossLink");
				crossLink.add(new AttributeModifier("href", "#" + (item.getIndex() + 1)));
				crossLink.setMarkupId("cross-link" + item.getIndex());
				crossLink.setOutputMarkupId(true);

				// TODO OK?
				final ExternalImage thumb = new ExternalImage("thumbPlaceholder",
						card.getThumbnailFilename());
				// final Image thumb = new Image("thumbPlaceholder", new
				// PackageResourceReference(
				// HomePage.class, card.getThumbnailFilename()));
				thumb.setMarkupId("placeholder" + card.getUuid().replace("-", "_") + "_img");
				thumb.setOutputMarkupId(true);

				crossLink.add(thumb);
				crossLinkDiv.add(crossLink);
				item.add(crossLinkDiv);
			}
		};
		thumbs.setOutputMarkupId(true);
		this.thumbsPlaceholder.setOutputMarkupId(true);

		this.thumbsPlaceholder.add(thumbs);
		this.add(this.thumbsPlaceholder);

		HatchetHarrySession.get().setHandCardsHaveBeenBuilt(true);
	}

	@Required
	public void setPersistenceService(final PersistenceService _persistenceService)
	{
		this.persistenceService = _persistenceService;
	}

}
