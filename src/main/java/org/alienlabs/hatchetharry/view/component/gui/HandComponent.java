package org.alienlabs.hatchetharry.view.component.gui;

import java.io.IOException;
import java.util.List;

import org.alienlabs.hatchetharry.HatchetHarrySession;
import org.alienlabs.hatchetharry.model.MagicCard;
import org.alienlabs.hatchetharry.service.PersistenceService;
import org.alienlabs.hatchetharry.view.component.zone.PlayCardFromHandBehavior;
import org.alienlabs.hatchetharry.view.page.HomePage;
import org.apache.wicket.Component;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.injection.Injector;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.template.PackageTextTemplate;
import org.apache.wicket.util.template.TextTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

@edu.umd.cs.findbugs.annotations.SuppressFBWarnings(value = { "SE_INNER_CLASS",
		"SIC_INNER_SHOULD_BE_STATIC_ANON" }, justification = "In Wicket, serializable inner classes are common. And as the parent Page is serialized as well, this is no concern. This is no bad practice in Wicket")
public class HandComponent extends Panel
{
	static final Logger LOGGER = LoggerFactory.getLogger(HandComponent.class);
	private static final long serialVersionUID = 1L;

	private final ListView<MagicCard> allCards;
	private final List<MagicCard> allCardsInHand;

	@SpringBean
	private PersistenceService persistenceService;

	/**
	 * @param id
	 *            wicket:id
	 * @param ids
	 *            gameId, playerId, deckId
	 */
	public HandComponent(final String id)
	{
		super(id);
		Injector.get().inject(this);

		this.add(new Behavior()
		{
			private static final long serialVersionUID = 1L;

			@Override
			public void renderHead(final Component component, final IHeaderResponse response)
			{
				super.renderHead(component, response);

				final TextTemplate sortableTemplate = new PackageTextTemplate(HomePage.class,
						"script/gallery/sortable.js");
				final TextTemplate handInBattlefieldTemplate = new PackageTextTemplate(
						HomePage.class, "script/gallery/handInBattlefield.js");

				response.render(JavaScriptHeaderItem.forScript(sortableTemplate.asString(), null));
				response.render(JavaScriptHeaderItem.forScript(
						handInBattlefieldTemplate.asString(), null));
				try
				{
					sortableTemplate.close();
				}
				catch (final IOException e)
				{
					HandComponent.LOGGER.error(
							"unable to close sortableTemplate in HandComponent#renderHead()!", e);
				}
				try
				{
					handInBattlefieldTemplate.close();
				}
				catch (final IOException e)
				{
					HandComponent.LOGGER
							.error("unable to close handInBattlefieldTemplate in HandComponent#renderHead()!",
									e);
				}
			}
		});

		Long gameId = HatchetHarrySession.get().getPlayer().getGame().getId();
		Long deckId = HatchetHarrySession.get().getPlayer().getDeck().getDeckId();

		this.allCardsInHand = this.persistenceService.getAllCardsInHandForAGameAndADeck(gameId,
				deckId);
		HandComponent.LOGGER.info("### allCardsInHand: " + this.allCardsInHand.size());

		this.allCards = new ListView<MagicCard>("handCards", this.allCardsInHand)
		{
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(final ListItem<MagicCard> item)
			{
				HatchetHarrySession.get().addCardIdInHand(item.getIndex(), item.getIndex());
				final MagicCard card = item.getModelObject();

				final ExternalImage handImagePlaceholder = new ExternalImage(
						"handImagePlaceholder", card.getBigImageFilename());
				final String uuid = card.getUuid().replace("-", "_");
				handImagePlaceholder.setMarkupId("placeholder" + uuid);
				handImagePlaceholder.setOutputMarkupId(true);
				final Label play = new Label("play");
				play.setOutputMarkupId(true);
				play.setMarkupId("play" + uuid);

				item.add(handImagePlaceholder, play);
				item.add(new PlayCardFromHandBehavior(card.getUuidObject()));
			}
		};
		this.allCards.setOutputMarkupId(true);
		this.add(this.allCards);

		HatchetHarrySession.get().setHandCardsHaveBeenBuilt(Boolean.TRUE);
	}

	@Required
	public void setPersistenceService(final PersistenceService _persistenceService)
	{
		this.persistenceService = _persistenceService;
	}

	public List<MagicCard> getAllCards()
	{
		return this.allCardsInHand;
	}

}
