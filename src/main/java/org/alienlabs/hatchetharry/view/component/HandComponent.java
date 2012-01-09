package org.alienlabs.hatchetharry.view.component;

import org.alienlabs.hatchetharry.HatchetHarrySession;
import org.alienlabs.hatchetharry.model.MagicCard;
import org.alienlabs.hatchetharry.view.page.HomePage;
import org.alienlabs.hatchetharry.view.page.PlayCardPage;
import org.apache.wicket.ResourceReference;
import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;

public class HandComponent extends Panel
{
	private static final long serialVersionUID = -271477124972641648L;

	private final BookmarkablePageLink<PlayCardPage> playCardPage;
	private final WebMarkupContainer handCardsPlaceholder;
	private final ListView<MagicCard> allCards;
	private final WebMarkupContainer thumbsPlaceholder;

	public HandComponent(final String id)
	{
		super(id);

		this.setOutputMarkupId(true);
		this.setMarkupId("handGallery");
		this.playCardPage = new BookmarkablePageLink<PlayCardPage>("playCardPage",
				PlayCardPage.class);
		this.add(this.playCardPage);

		this.handCardsPlaceholder = new WebMarkupContainer("handCardsPlaceholder");
		this.handCardsPlaceholder.setOutputMarkupId(true);
		this.allCards = new ListView<MagicCard>("handCards", HatchetHarrySession.get()
				.getFirstCardsInHand())
		{
			private static final long serialVersionUID = -7874661839855866875L;

			@Override
			protected void populateItem(final ListItem<MagicCard> item)
			{
				HatchetHarrySession.get(this.getRequest()).addCardIdInHand(item.getIndex(),
						item.getIndex());
				final MagicCard card = item.getModelObject();

				final WebMarkupContainer wrapper = new WebMarkupContainer("wrapper");
				wrapper.setMarkupId("wrapper" + item.getIndex());
				wrapper.setOutputMarkupId(true);

				final Image handImagePlaceholder = new Image("handImagePlaceholder",
						new ResourceReference(HomePage.class, card.getBigImageFilename()));
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
		final ListView<MagicCard> thumbs = new ListView<MagicCard>("thumbs", HatchetHarrySession
				.get().getFirstCardsInHand())
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
				crossLink.add(new SimpleAttributeModifier("href", "#" + (item.getIndex() + 1)));
				crossLink.setMarkupId("cross-link" + item.getIndex());
				crossLink.setOutputMarkupId(true);

				final Image thumb = new Image("thumbPlaceholder", new ResourceReference(
						HomePage.class, card.getThumbnailFilename()));
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

}
