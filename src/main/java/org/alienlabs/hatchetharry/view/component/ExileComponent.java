package org.alienlabs.hatchetharry.view.component;

import java.util.List;

import org.alienlabs.hatchetharry.HatchetHarrySession;
import org.alienlabs.hatchetharry.model.CardZone;
import org.alienlabs.hatchetharry.model.MagicCard;
import org.alienlabs.hatchetharry.service.PersistenceService;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.springframework.beans.factory.annotation.Required;

public class ExileComponent extends Panel
{
	private static final long serialVersionUID = 1L;

	@SpringBean
	private PersistenceService persistenceService;

	private final WebMarkupContainer exileCardsPlaceholder;
	private final ListView<MagicCard> allCards;
	private final WebMarkupContainer thumbsPlaceholder;

	/**
	 * 
	 * @param id
	 *            wicket:id
	 * @param ids
	 *            gameId, playerId, deckId
	 */
	public ExileComponent(final String id, final Long... ids)
	{
		super(id);

		this.setOutputMarkupId(true);
		this.setMarkupId("exileGallery");

		this.exileCardsPlaceholder = new WebMarkupContainer("exileCardsPlaceholder");
		this.exileCardsPlaceholder.setOutputMarkupId(true);

		final List<MagicCard> allCardsInExile = this.persistenceService
				.getAllCardsInExileForAGameAndAPlayer((ids.length == 0 ? HatchetHarrySession.get()
						.getPlayer().getGame().getId() : ids[0]), (ids.length == 0
						? HatchetHarrySession.get().getPlayer().getId()
						: ids[1]), (ids.length == 0 ? HatchetHarrySession.get().getPlayer()
						.getDeck().getDeckId() : ids[2]));

		this.allCards = new ListView<MagicCard>("exileCards", allCardsInExile)
		{
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(final ListItem<MagicCard> item)
			{
				final MagicCard card = item.getModelObject();

				final WebMarkupContainer wrapper = new WebMarkupContainer("wrapper");
				wrapper.setMarkupId("wrapper" + item.getIndex());
				wrapper.setOutputMarkupId(true);

				final ExternalImage handImagePlaceholder = new ExternalImage(
						"exileImagePlaceholder", card.getBigImageFilename());
				handImagePlaceholder.setMarkupId("placeholder" + card.getUuid().replace("-", "_"));
				handImagePlaceholder.setOutputMarkupId(true);

				wrapper.add(handImagePlaceholder);
				item.add(wrapper);

			}
		};
		this.exileCardsPlaceholder.add(this.allCards);
		this.add(this.exileCardsPlaceholder);

		this.thumbsPlaceholder = new WebMarkupContainer("thumbsPlaceholder");
		final ListView<MagicCard> thumbs = new ListView<MagicCard>("thumbs", allCardsInExile)
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

				final ExternalImage thumb = new ExternalImage("thumbPlaceholder",
						card.getThumbnailFilename());
				thumb.setMarkupId("placeholder" + card.getUuid().replace("-", "_") + "_img");
				thumb.setOutputMarkupId(true);
				thumb.add(new AttributeModifier("name", card.getTitle()));

				crossLink.add(thumb);
				crossLinkDiv.add(crossLink);
				item.add(crossLinkDiv);
			}
		};
		thumbs.setOutputMarkupId(true);
		this.thumbsPlaceholder.setOutputMarkupId(true);

		final PutToZonePanel putToZonePanel = new PutToZonePanel("putToZonePanel", CardZone.EXILE,
				this.persistenceService.getPlayer((ids.length == 0 ? HatchetHarrySession.get()
						.getPlayer().getId() : ids[1])));
		putToZonePanel.add(new AttributeModifier("style",
				"position:absolute; top: 74%; left:545px;"));
		this.add(putToZonePanel);

		this.thumbsPlaceholder.add(thumbs);
		this.add(this.thumbsPlaceholder);
	}

	@Required
	public void setPersistenceService(final PersistenceService _persistenceService)
	{
		this.persistenceService = _persistenceService;
	}

}
