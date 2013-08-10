package org.alienlabs.hatchetharry.view.component;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.alienlabs.hatchetharry.HatchetHarryApplication;
import org.alienlabs.hatchetharry.HatchetHarrySession;
import org.alienlabs.hatchetharry.model.Counter;
import org.alienlabs.hatchetharry.model.Game;
import org.alienlabs.hatchetharry.model.MagicCard;
import org.alienlabs.hatchetharry.model.Player;
import org.alienlabs.hatchetharry.model.channel.NotifierAction;
import org.alienlabs.hatchetharry.model.channel.ReactivateTooltipsCometChannel;
import org.alienlabs.hatchetharry.model.channel.UpdateCardPanelCometChannel;
import org.alienlabs.hatchetharry.service.PersistenceService;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

public class TooltipPanel extends Panel
{
	private static final long serialVersionUID = 1L;
	static final Logger LOGGER = LoggerFactory.getLogger(TooltipPanel.class);

	final UUID uuid;
	final String bigImage;
	final String ownerSide;
	final MagicCard card;

	@SpringBean
	PersistenceService persistenceService;

	public TooltipPanel(final String id, final UUID _uuid, final String _bigImage,
			final String _ownerSide, final MagicCard _card)
	{
		super(id);
		this.uuid = _uuid;
		this.bigImage = _bigImage;
		this.ownerSide = _ownerSide;
		this.card = _card;

		final AjaxLink<Void> closeTooltip = new AjaxLink<Void>("closeTooltip")
		{
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(final AjaxRequestTarget target)
			{
				target.appendJavaScript("jQuery('.tooltip').hide(); ");

				final Long gameId = HatchetHarrySession.get().getGameId();
				final ReactivateTooltipsCometChannel rtcc = new ReactivateTooltipsCometChannel(
						gameId);
				final List<BigInteger> allPlayersInGame = TooltipPanel.this.persistenceService
						.giveAllPlayersFromGame(gameId);

				// post a message for all players in the game
				for (int i = 0; i < allPlayersInGame.size(); i++)
				{
					final Long player = allPlayersInGame.get(i).longValue();
					final String pageUuid = HatchetHarryApplication.getCometResources().get(player);

					HatchetHarryApplication.get().getEventBus().post(rtcc, pageUuid);
				}
			}
		};

		final ExternalImage bubbleTipImg1 = new ExternalImage("bubbleTipImg1", this.bigImage);

		if ("infrared".equals(this.ownerSide))
		{
			bubbleTipImg1.add(new AttributeModifier("style", "border: 1px solid red;"));
		}
		else if ("ultraviolet".equals(this.ownerSide))
		{
			bubbleTipImg1.add(new AttributeModifier("style", "border: 1px solid purple;"));
		}
		else
		{
			bubbleTipImg1.add(new AttributeModifier("style", "border: 1px solid yellow;"));
		}

		final Form<String> form = new Form<String>("form");
		final TextField<String> counterAddName = new TextField<String>("counterAddName",
				new Model<String>(""));
		counterAddName.setOutputMarkupId(true);

		final AjaxSubmitLink submit = new AjaxSubmitLink("submit")
		{
			private static final long serialVersionUID = 1L;

			@Override
			public void onSubmit(final AjaxRequestTarget target, final Form<?> _form)
			{
				final MagicCard myCard = TooltipPanel.this.persistenceService
						.getCardFromUuid(TooltipPanel.this.uuid);

				final String _counterName = counterAddName.getDefaultModelObjectAsString();
				final Counter counter = new Counter();
				counter.setCounterName(_counterName);
				counter.setNumberOfCounters(1l);
				counter.setCard(myCard);

				final Set<Counter> counters = myCard.getCounters();
				counters.add(counter);
				TooltipPanel.this.persistenceService.updateCard(myCard);

				final Player targetPlayer = TooltipPanel.this.persistenceService.getPlayer(myCard
						.getDeck().getPlayerId());
				final Game game = TooltipPanel.this.persistenceService.getGame(targetPlayer
						.getGame().getId());
				final List<BigInteger> allPlayersInGame = TooltipPanel.this.persistenceService
						.giveAllPlayersFromGame(game.getId());

				final UpdateCardPanelCometChannel ucpcc = new UpdateCardPanelCometChannel(
						game.getId(), HatchetHarrySession.get().getPlayer().getName(),
						targetPlayer.getName(), myCard.getTitle(), counter.getCounterName(),
						counter.getNumberOfCounters(), NotifierAction.ADD_COUNTER,
						TooltipPanel.this.uuid, TooltipPanel.this.bigImage,
						TooltipPanel.this.ownerSide);

				for (int i = 0; i < allPlayersInGame.size(); i++)
				{
					final Long p = allPlayersInGame.get(i).longValue();
					final String pageUuid = HatchetHarryApplication.getCometResources().get(p);
					PlayCardFromHandBehavior.LOGGER.info("pageUuid: " + pageUuid);
					HatchetHarryApplication.get().getEventBus().post(ucpcc, pageUuid);
				}
			}

		};
		submit.setOutputMarkupId(true);

		final List<Counter> cardCounters = new ArrayList<Counter>(this.card.getCounters());
		final ListView<Counter> counters = new ListView<Counter>("counters", cardCounters)
		{
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(final ListItem<Counter> item)
			{
				final Counter counter = item.getModelObject();
				item.add(new Label("counterName", counter.getCounterName()).setOutputMarkupId(true));
				item.add(new Label("numberOfCounters", counter.getNumberOfCounters())
						.setOutputMarkupId(true));

				final AjaxLink<Void> addCounterLink = new AjaxLink<Void>("addCounterLink")
				{
					private static final long serialVersionUID = 1L;

					@Override
					public void onClick(final AjaxRequestTarget target)
					{
						counter.setNumberOfCounters(counter.getNumberOfCounters() + 1);
						try
						{
							TooltipPanel.this.persistenceService.saveOrUpdateCounter(counter);
						}
						catch (final Exception ex)
						{
							TooltipPanel.LOGGER.error(
									"error occured while trying to add a counter: ", ex);
							return;
						}

						final Player targetPlayer = TooltipPanel.this.persistenceService
								.getPlayer(TooltipPanel.this.card.getDeck().getPlayerId());
						final Game game = TooltipPanel.this.persistenceService.getGame(targetPlayer
								.getGame().getId());
						final List<BigInteger> allPlayersInGame = TooltipPanel.this.persistenceService
								.giveAllPlayersFromGame(game.getId());

						final UpdateCardPanelCometChannel ucpcc = new UpdateCardPanelCometChannel(
								game.getId(), HatchetHarrySession.get().getPlayer().getName(),
								targetPlayer.getName(), TooltipPanel.this.card.getTitle(),
								counter.getCounterName(), counter.getNumberOfCounters(),
								NotifierAction.ADD_COUNTER, TooltipPanel.this.uuid,
								TooltipPanel.this.bigImage, TooltipPanel.this.ownerSide);

						for (int i = 0; i < allPlayersInGame.size(); i++)
						{
							final Long p = allPlayersInGame.get(i).longValue();
							final String pageUuid = HatchetHarryApplication.getCometResources()
									.get(p);
							PlayCardFromHandBehavior.LOGGER.info("pageUuid: " + pageUuid);
							HatchetHarryApplication.get().getEventBus().post(ucpcc, pageUuid);
						}
					}
				};
				addCounterLink.setOutputMarkupId(true);

				final ExternalImage counterPlus = new ExternalImage("counterPlus",
						"image/plusLife.png");
				counterPlus.setOutputMarkupId(true);
				addCounterLink.add(counterPlus);
				final AjaxLink<Void> removeCounterLink = new AjaxLink<Void>("removeCounterLink")
				{
					private static final long serialVersionUID = 1L;

					@Override
					public void onClick(final AjaxRequestTarget target)
					{
						counter.setNumberOfCounters(counter.getNumberOfCounters() - 1);
						NotifierAction action;

						if (counter.getNumberOfCounters().longValue() == 0)
						{
							TooltipPanel.this.persistenceService.deleteCounter(counter,
									TooltipPanel.this.card);
							action = NotifierAction.CLEAR_COUNTER;
						}
						else
						{
							try
							{
								TooltipPanel.this.persistenceService.saveOrUpdateCounter(counter);
								action = NotifierAction.REMOVE_COUNTER;
							}
							catch (final Exception ex)
							{
								TooltipPanel.LOGGER.error(
										"error occured while trying to remove a counter: ", ex);
								return;
							}
						}

						final Player targetPlayer = TooltipPanel.this.persistenceService
								.getPlayer(TooltipPanel.this.card.getDeck().getPlayerId());
						final Game game = TooltipPanel.this.persistenceService.getGame(targetPlayer
								.getGame().getId());
						final List<BigInteger> allPlayersInGame = TooltipPanel.this.persistenceService
								.giveAllPlayersFromGame(game.getId());
						final UpdateCardPanelCometChannel ucpcc = new UpdateCardPanelCometChannel(
								game.getId(), HatchetHarrySession.get().getPlayer().getName(),
								targetPlayer.getName(), TooltipPanel.this.card.getTitle(),
								counter.getCounterName(), counter.getNumberOfCounters(), action,
								TooltipPanel.this.uuid, TooltipPanel.this.bigImage,
								TooltipPanel.this.ownerSide);

						for (int i = 0; i < allPlayersInGame.size(); i++)
						{
							final Long p = allPlayersInGame.get(i).longValue();
							final String pageUuid = HatchetHarryApplication.getCometResources()
									.get(p);
							PlayCardFromHandBehavior.LOGGER.info("pageUuid: " + pageUuid);
							HatchetHarryApplication.get().getEventBus().post(ucpcc, pageUuid);
						}
					}
				};
				removeCounterLink.setOutputMarkupId(true);

				final ExternalImage counterMinus = new ExternalImage("counterMinus",
						"image/minusLife.png");
				counterMinus.setOutputMarkupId(true);
				removeCounterLink.add(counterMinus);

				item.add(addCounterLink, removeCounterLink);
			}
		};

		form.add(counterAddName, submit);
		this.add(closeTooltip, bubbleTipImg1, form, counters);
	}

	@Required
	public void setPersistenceService(final PersistenceService _persistenceService)
	{
		this.persistenceService = _persistenceService;
	}

}
