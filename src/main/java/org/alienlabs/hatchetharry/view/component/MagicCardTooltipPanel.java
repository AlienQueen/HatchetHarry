package org.alienlabs.hatchetharry.view.component;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
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
import org.alienlabs.hatchetharry.model.channel.UpdateCardPanelCometChannel;
import org.alienlabs.hatchetharry.service.PersistenceService;
import org.alienlabs.hatchetharry.view.clientsideutil.JavaScriptUtils;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxButton;
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

public class MagicCardTooltipPanel extends Panel
{
	private static final long serialVersionUID = 1L;
	static final Logger LOGGER = LoggerFactory.getLogger(MagicCardTooltipPanel.class);

	final UUID uuid;
	final String bigImage;
	final String ownerSide;
	final MagicCard card;

	@SpringBean
	PersistenceService persistenceService;

	public MagicCardTooltipPanel(final String id, final UUID _uuid, final String _bigImage,
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
				JavaScriptUtils.updateCardsAndRestoreStateInBattlefield(target,
						MagicCardTooltipPanel.this.persistenceService,
						HatchetHarrySession.get().getGameId(), null, false);
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
		form.add(counterAddName);

		final IndicatingAjaxButton submit = new IndicatingAjaxButton("submit", form)
		{
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit(final AjaxRequestTarget target, final Form<?> _form)
			{
				final MagicCard myCard = MagicCardTooltipPanel.this.persistenceService
						.getCardFromUuid(MagicCardTooltipPanel.this.uuid);

				final String _counterName = _form.get("counterAddName")
						.getDefaultModelObjectAsString();
				final Counter counter = new Counter();
				counter.setCounterName(_counterName);
				counter.setNumberOfCounters(1l);
				counter.setCard(myCard);
				MagicCardTooltipPanel.this.persistenceService.saveCounter(counter);

				final Set<Counter> counters = myCard.getCounters();
				counters.add(counter);
				MagicCardTooltipPanel.this.persistenceService.updateCard(myCard);

				final Player targetPlayer = MagicCardTooltipPanel.this.persistenceService.getPlayer(myCard
						.getDeck().getPlayerId());
				final Game game = MagicCardTooltipPanel.this.persistenceService.getGame(targetPlayer
						.getGame().getId());
				final List<BigInteger> allPlayersInGame = MagicCardTooltipPanel.this.persistenceService
						.giveAllPlayersFromGame(game.getId());

				final UpdateCardPanelCometChannel ucpcc = new UpdateCardPanelCometChannel(
						game.getId(), HatchetHarrySession.get().getPlayer().getName(),
						targetPlayer.getName(), myCard.getTitle(), _counterName,
						counter.getNumberOfCounters(), 0l, NotifierAction.ADD_COUNTER,
						MagicCardTooltipPanel.this.uuid, MagicCardTooltipPanel.this.bigImage,
						MagicCardTooltipPanel.this.ownerSide);

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
		form.add(submit);

		final List<Counter> cardCounters = new ArrayList<Counter>(this.card.getCounters());
		Collections.sort(cardCounters);

		final ListView<Counter> counters = new ListView<Counter>("counters", cardCounters)
		{
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(final ListItem<Counter> item)
			{
				final Counter counter = item.getModelObject();

				final AjaxLink<Void> addCounterLink = new AjaxLink<Void>("addCounterLink")
				{
					private static final long serialVersionUID = 1L;

					@Override
					public void onClick(final AjaxRequestTarget target)
					{
						counter.setNumberOfCounters(counter.getNumberOfCounters() + 1);
						try
						{
							MagicCardTooltipPanel.this.persistenceService.saveOrUpdateCounter(counter);
						}
						catch (final Exception ex)
						{
							MagicCardTooltipPanel.LOGGER.error(
									"error occured while trying to add a counter: ", ex);
							return;
						}

						final Player targetPlayer = MagicCardTooltipPanel.this.persistenceService
								.getPlayer(MagicCardTooltipPanel.this.card.getDeck().getPlayerId());
						final Game game = MagicCardTooltipPanel.this.persistenceService.getGame(targetPlayer
								.getGame().getId());
						final List<BigInteger> allPlayersInGame = MagicCardTooltipPanel.this.persistenceService
								.giveAllPlayersFromGame(game.getId());

						final UpdateCardPanelCometChannel ucpcc = new UpdateCardPanelCometChannel(
								game.getId(), HatchetHarrySession.get().getPlayer().getName(),
								targetPlayer.getName(), MagicCardTooltipPanel.this.card.getTitle(),
								counter.getCounterName(), counter.getNumberOfCounters(), 0l,
								NotifierAction.ADD_COUNTER, MagicCardTooltipPanel.this.uuid,
								MagicCardTooltipPanel.this.bigImage, MagicCardTooltipPanel.this.ownerSide);

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
							MagicCardTooltipPanel.this.persistenceService.deleteCounter(counter,
									MagicCardTooltipPanel.this.card);
							action = NotifierAction.CLEAR_COUNTER;
						}
						else
						{
							try
							{
								MagicCardTooltipPanel.this.persistenceService.saveOrUpdateCounter(counter);
								action = NotifierAction.REMOVE_COUNTER;
							}
							catch (final Exception ex)
							{
								MagicCardTooltipPanel.LOGGER.error(
										"error occured while trying to remove a counter: ", ex);
								return;
							}
						}

						final Player targetPlayer = MagicCardTooltipPanel.this.persistenceService
								.getPlayer(MagicCardTooltipPanel.this.card.getDeck().getPlayerId());
						final Game game = MagicCardTooltipPanel.this.persistenceService.getGame(targetPlayer
								.getGame().getId());
						final List<BigInteger> allPlayersInGame = MagicCardTooltipPanel.this.persistenceService
								.giveAllPlayersFromGame(game.getId());
						final UpdateCardPanelCometChannel ucpcc = new UpdateCardPanelCometChannel(
								game.getId(), HatchetHarrySession.get().getPlayer().getName(),
								targetPlayer.getName(), MagicCardTooltipPanel.this.card.getTitle(),
								counter.getCounterName(), counter.getNumberOfCounters(), 0l,
								action, MagicCardTooltipPanel.this.uuid, MagicCardTooltipPanel.this.bigImage,
								MagicCardTooltipPanel.this.ownerSide);

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

				final Form<String> setCounterForm = new Form<String>("setCounterForm");
				final TextField<Long> setCounterButton = new TextField<Long>("setCounterButton",
						Model.of(counter.getNumberOfCounters()));
				setCounterForm.add(setCounterButton);

				final IndicatingAjaxButton setCounterSubmit = new IndicatingAjaxButton(
						"setCounterSubmit", setCounterForm)
				{
					private static final long serialVersionUID = 1L;

					@Override
					protected void onSubmit(final AjaxRequestTarget target, final Form<?> _form)
					{
						NotifierAction action;
						final Long originalNumberOfCounters = counter.getNumberOfCounters();
						final Long targetNumberOfCounters = Long.parseLong((String)_form.get(
								"setCounterButton").getDefaultModelObject());

						if (targetNumberOfCounters.longValue() == 0)
						{
							MagicCardTooltipPanel.this.persistenceService.deleteCounter(counter,
									MagicCardTooltipPanel.this.card);
							action = NotifierAction.CLEAR_COUNTER;
						}
						else
						{
							counter.setNumberOfCounters(targetNumberOfCounters);
							MagicCardTooltipPanel.this.persistenceService.saveOrUpdateCounter(counter);
							action = NotifierAction.SET_COUNTER;
						}

						final Player targetPlayer = MagicCardTooltipPanel.this.persistenceService
								.getPlayer(MagicCardTooltipPanel.this.card.getDeck().getPlayerId());
						final Game game = MagicCardTooltipPanel.this.persistenceService.getGame(targetPlayer
								.getGame().getId());
						final List<BigInteger> allPlayersInGame = MagicCardTooltipPanel.this.persistenceService
								.giveAllPlayersFromGame(game.getId());
						final UpdateCardPanelCometChannel ucpcc = new UpdateCardPanelCometChannel(
								game.getId(), HatchetHarrySession.get().getPlayer().getName(),
								targetPlayer.getName(), MagicCardTooltipPanel.this.card.getTitle(),
								counter.getCounterName(), targetNumberOfCounters,
								originalNumberOfCounters, action, MagicCardTooltipPanel.this.uuid,
								MagicCardTooltipPanel.this.bigImage, MagicCardTooltipPanel.this.ownerSide);

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

				setCounterForm.add(new Label("counterName", counter.getCounterName())
						.setOutputMarkupId(true));
				setCounterForm.add(new Label("numberOfCounters", counter.getNumberOfCounters())
						.setOutputMarkupId(true));

				setCounterForm.add(setCounterSubmit, addCounterLink, removeCounterLink);

				setCounterForm.setOutputMarkupPlaceholderTag(false);
				item.add(setCounterForm);
			}
		};

		this.add(closeTooltip, bubbleTipImg1, form, counters);
	}

	@Required
	public void setPersistenceService(final PersistenceService _persistenceService)
	{
		this.persistenceService = _persistenceService;
	}

}
