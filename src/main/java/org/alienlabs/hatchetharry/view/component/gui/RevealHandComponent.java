package org.alienlabs.hatchetharry.view.component.gui;

import java.io.IOException;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;

import org.alienlabs.hatchetharry.HatchetHarrySession;
import org.alienlabs.hatchetharry.model.CardZone;
import org.alienlabs.hatchetharry.model.MagicCard;
import org.alienlabs.hatchetharry.model.channel.ConsoleLogCometChannel;
import org.alienlabs.hatchetharry.model.channel.NotifierAction;
import org.alienlabs.hatchetharry.model.channel.NotifierCometChannel;
import org.alienlabs.hatchetharry.model.channel.StopRevealingHandCometChannel;
import org.alienlabs.hatchetharry.model.channel.consolelog.AbstractConsoleLogStrategy;
import org.alienlabs.hatchetharry.model.channel.consolelog.ConsoleLogStrategy;
import org.alienlabs.hatchetharry.model.channel.consolelog.ConsoleLogType;
import org.alienlabs.hatchetharry.service.PersistenceService;
import org.alienlabs.hatchetharry.view.clientsideutil.EventBusPostService;
import org.alienlabs.hatchetharry.view.component.zone.PutToZonePanel;
import org.alienlabs.hatchetharry.view.component.zone.ZoneMoveBehavior;
import org.alienlabs.hatchetharry.view.page.HomePage;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxLink;
import org.apache.wicket.injection.Injector;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
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
public class RevealHandComponent extends Panel
{
	private static final Logger LOGGER = LoggerFactory.getLogger(RevealHandComponent.class);
	private static final long serialVersionUID = 1L;
	private final WebMarkupContainer handCardsPlaceholder;
	private final ListView<MagicCard> allCards;
	private final WebMarkupContainer thumbsPlaceholder;
	private final List<MagicCard> allCardsInHand;
	@SpringBean
	private PersistenceService persistenceService;

	/**
	 * @param id
	 *            wicket:id
	 * @param ids
	 *            gameId, playerId, deckId
	 */
	public RevealHandComponent(final String id, final boolean isReveal, final Long... ids)
	{
		super(id);
		Injector.get().inject(this);

		final String markupId = "handGallery" + (ids.length != 0 ? ids[1].toString() : "");
		final WebMarkupContainer parent = new WebMarkupContainer("parent");
		parent.setMarkupId(markupId);
		parent.setOutputMarkupId(true);
		this.add(parent);

		final WebMarkupContainer content = new WebMarkupContainer("content");
		content.setOutputMarkupId(true);

		final ExternalImage handleImage = new ExternalImage("handleImage", "image/arrow.png");
		handleImage.setMarkupId("handle" + markupId);
		handleImage.setOutputMarkupId(true);
		content.add(handleImage);

		final ZoneMoveBehavior zmb = new ZoneMoveBehavior(this);
		this.add(zmb);

		final WebMarkupContainer slider = new WebMarkupContainer("slider");
		slider.setMarkupId("main-photo-slider" + (ids.length != 0 ? ids[1].toString() : ""));
		slider.setOutputMarkupId(true);

		final WebMarkupContainer page_wrap = new WebMarkupContainer("page-wrap");
		page_wrap.add(slider);
		content.add(page_wrap);

		if (isReveal)
		{
			final IndicatingAjaxLink<Void> closeHand = new IndicatingAjaxLink<Void>("closeHand")
			{
				private static final long serialVersionUID = 1L;

				@Override
				public void onClick(final AjaxRequestTarget target)
				{
					final String playerRevealing = RevealHandComponent.this.persistenceService
							.getPlayer(ids[1]).getName();
					final String playerStopping = HatchetHarrySession.get().getPlayer().getName();

					final ConsoleLogStrategy logger = AbstractConsoleLogStrategy.chooseStrategy(
							ConsoleLogType.REVEAL_HAND, null, null, false, null, playerRevealing,
							null, null, playerStopping, null, ids[0]);
					final NotifierCometChannel ncc = new NotifierCometChannel(
							NotifierAction.REVEAL_HAND, null, null, playerRevealing, null, null,
							null, null, playerStopping);
					final StopRevealingHandCometChannel rhcc = new StopRevealingHandCometChannel();
					final List<BigInteger> allPlayersInGame = RevealHandComponent.this.persistenceService
							.giveAllPlayersFromGame(ids[0]);

					EventBusPostService.post(allPlayersInGame, new ConsoleLogCometChannel(logger),
							ncc, rhcc);
				}

			};
			content.add(closeHand);
			content.setMarkupId("revealedContent");

			this.add(new Behavior()
			{
				private static final long serialVersionUID = 1L;

				@Override
				public void renderHead(final Component component, final IHeaderResponse response)
				{
					super.renderHead(component, response);

					final HashMap<String, Object> variables = new HashMap<String, Object>();
					variables.put("player", ids[1].toString());
					final TextTemplate template = new PackageTextTemplate(HomePage.class,
							"script/gallery/coda-slider.1.1.1.pack-for-hand-reveal.js");
					template.interpolate(variables);
					response.render(JavaScriptHeaderItem.forScript(template.asString(), null));

					try
					{
						template.close();
					}
					catch (final IOException e)
					{
						RevealHandComponent.LOGGER.error(
								"unable to close template in RevealHandComponent#renderHead()!", e);
					}
				}
			});
		}
		else
		{
			content.add(new WebMarkupContainer("closeHand").setVisible(false));
			content.setMarkupId("content");
		}

		this.handCardsPlaceholder = new WebMarkupContainer("handCardsPlaceholder");
		this.handCardsPlaceholder.setOutputMarkupId(true);

		this.allCardsInHand = this.persistenceService
				.getAllCardsInHandForAGameAndAPlayer((ids.length == 0 ? HatchetHarrySession.get()
						.getPlayer().getGame().getId() : ids[0]), (ids.length == 0
						? HatchetHarrySession.get().getPlayer().getId()
						: ids[1]), (ids.length == 0 ? HatchetHarrySession.get().getPlayer()
						.getDeck().getDeckId() : ids[2]));
		RevealHandComponent.LOGGER.info("### allCardsInHand: " + this.allCardsInHand.size());

		this.allCards = new ListView<MagicCard>("handCards", this.allCardsInHand)
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

				final ExternalImage handImagePlaceholder = new ExternalImage(
						"handImagePlaceholder", card.getBigImageFilename());
				handImagePlaceholder.setMarkupId("placeholder" + card.getUuid().replace("-", "_"));
				handImagePlaceholder.setOutputMarkupId(true);

				wrapper.add(handImagePlaceholder);
				item.add(wrapper);
			}
		};
		this.allCards.setOutputMarkupId(true);
		this.handCardsPlaceholder.setOutputMarkupId(true);

		this.handCardsPlaceholder.addOrReplace(this.allCards);
		slider.add(this.handCardsPlaceholder);

		this.thumbsPlaceholder = new WebMarkupContainer("thumbsPlaceholder");
		final ListView<MagicCard> thumbs = new ListView<MagicCard>("thumbs", this.allCardsInHand)
		{
			private static final long serialVersionUID = -1L;

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

				if (isReveal)
				{
					thumb.add(new AttributeModifier("class", "nav-thumb" + ids[1].toString()));
				}

				crossLink.add(thumb);
				crossLinkDiv.add(crossLink);
				item.add(crossLinkDiv);
			}
		};
		thumbs.setOutputMarkupId(true);
		this.thumbsPlaceholder.setOutputMarkupId(true);

		this.thumbsPlaceholder.addOrReplace(thumbs);
		page_wrap.add(this.thumbsPlaceholder);

		final PutToZonePanel putToZonePanel = new PutToZonePanel("putToZonePanel", CardZone.HAND,
				this.persistenceService.getPlayer((ids.length == 0 ? HatchetHarrySession.get()
						.getPlayer().getId() : ids[1])), isReveal);
		putToZonePanel.add(new AttributeModifier("style", isReveal
				? "position: absolute; top:25%; left: 0px;"
				: "position: absolute; top:21%; left: 13px;"));
		parent.add(putToZonePanel);
		parent.add(content);
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
