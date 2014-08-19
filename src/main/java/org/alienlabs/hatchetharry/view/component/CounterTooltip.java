package org.alienlabs.hatchetharry.view.component;

import java.math.BigInteger;
import java.util.*;

import org.alienlabs.hatchetharry.HatchetHarryApplication;
import org.alienlabs.hatchetharry.HatchetHarrySession;
import org.alienlabs.hatchetharry.model.*;
import org.alienlabs.hatchetharry.model.channel.ConsoleLogCometChannel;
import org.alienlabs.hatchetharry.model.channel.NotifierAction;
import org.alienlabs.hatchetharry.model.channel.UpdateCardPanelCometChannel;
import org.alienlabs.hatchetharry.model.channel.UpdateTokenPanelCometChannel;
import org.alienlabs.hatchetharry.model.channel.consolelog.AbstractConsoleLogStrategy;
import org.alienlabs.hatchetharry.model.channel.consolelog.ConsoleLogStrategy;
import org.alienlabs.hatchetharry.model.channel.consolelog.ConsoleLogType;
import org.alienlabs.hatchetharry.service.PersistenceService;
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

public class CounterTooltip extends Panel
{
	static final Logger LOGGER = LoggerFactory.getLogger(CounterTooltip.class);
	private static final long serialVersionUID = 1L;
	final MagicCard card;
	final Token token;
	@SpringBean
	PersistenceService persistenceService;

	public CounterTooltip(final String id, final MagicCard _card, final Token _token)
	{
		super(id);
		this.card = _card;
		this.token = _token;


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
				final String _counterName = counterAddName.getDefaultModelObjectAsString();
				final Counter counter = new Counter();
				counter.setCounterName(_counterName);
				counter.setNumberOfCounters(1l);
				if (CounterTooltip.this.card == null)
				{
					counter.setToken(CounterTooltip.this.token);
				}
				else
				{
					counter.setCard(CounterTooltip.this.card);
				}
				CounterTooltip.this.persistenceService.saveOrUpdateCounter(counter);
				counterAddName.setModel(Model.of(""));

				final Set<Counter> counters;

				if (CounterTooltip.this.card == null)
				{
					counters = CounterTooltip.this.token.getCounters();
				}
				else
				{
					counters = CounterTooltip.this.card.getCounters();
				}

				counters.add(counter);

				if (CounterTooltip.this.card == null)
				{
					CounterTooltip.this.persistenceService.updateToken(CounterTooltip.this.token);
				}
				else
				{
					CounterTooltip.this.persistenceService.updateCard(CounterTooltip.this.card);
				}


				final Player targetPlayer;

				if (CounterTooltip.this.card == null)
				{
					targetPlayer = CounterTooltip.this.persistenceService
						.getPlayer(CounterTooltip.this.token.getPlayer().getId());
				}
				else
				{
					targetPlayer = CounterTooltip.this.persistenceService
						.getPlayer(CounterTooltip.this.card.getDeck().getPlayerId());
				}

				final Game game = CounterTooltip.this.persistenceService.getGame(targetPlayer
					.getGame().getId());
				final List<BigInteger> allPlayersInGame = CounterTooltip.this.persistenceService
					.giveAllPlayersFromGame(game.getId());

				final Object updateCometChannel;
				ConsoleLogStrategy logger;

				if (CounterTooltip.this.card == null)
				{
					updateCometChannel = new UpdateTokenPanelCometChannel(game.getId(),
						HatchetHarrySession.get().getPlayer().getName(), targetPlayer.getName(),
						CounterTooltip.this.token.getCreatureTypes(), _counterName,
						counter.getNumberOfCounters(), 0l, NotifierAction.ADD_COUNTER_ACTION,
						CounterTooltip.this.token, "", CounterTooltip.this.token.getPlayer()
							.getSide().getSideName());

					logger = AbstractConsoleLogStrategy.chooseStrategy(
						ConsoleLogType.COUNTER_ADD_REMOVE, null, null, null,
						CounterTooltip.this.token.getCreatureTypes(), HatchetHarrySession.get()
							.getPlayer().getName(), _counterName, counter.getNumberOfCounters(),
						targetPlayer.getName(), null, game.getId());
				}
				else
				{
					updateCometChannel = new UpdateCardPanelCometChannel(game.getId(),
						HatchetHarrySession.get().getPlayer().getName(), targetPlayer.getName(),
						CounterTooltip.this.card.getTitle(), _counterName,
						counter.getNumberOfCounters(), 0l, NotifierAction.ADD_COUNTER_ACTION,
						CounterTooltip.this.card, CounterTooltip.this.card.getBigImageFilename(),
						CounterTooltip.this.card.getOwnerSide());

					logger = AbstractConsoleLogStrategy.chooseStrategy(
						ConsoleLogType.COUNTER_ADD_REMOVE, null, null, null,
						CounterTooltip.this.card.getTitle(), HatchetHarrySession.get().getPlayer()
							.getName(), _counterName, counter.getNumberOfCounters(),
						targetPlayer.getName(), null, game.getId());

				}


				for (int i = 0; i < allPlayersInGame.size(); i++)
				{
					final Long p = allPlayersInGame.get(i).longValue();
					final String pageUuid = HatchetHarryApplication.getCometResources().get(p);
					CounterTooltip.LOGGER.info("pageUuid: " + pageUuid);
					HatchetHarryApplication.get().getEventBus().post(updateCometChannel, pageUuid);
					HatchetHarryApplication.get().getEventBus()
						.post(new ConsoleLogCometChannel(logger), pageUuid);
				}
			}

		};
		submit.setOutputMarkupId(true);
		form.add(submit);

		final List<Counter> allCounters;

		if (CounterTooltip.this.card == null)
		{
			allCounters = new ArrayList<Counter>(this.token.getCounters());
		}
		else
		{
			allCounters = new ArrayList<Counter>(this.card.getCounters());
		}

		Collections.sort(allCounters);

		final ListView<Counter> counters = new ListView<Counter>("counters", allCounters)
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
						CounterTooltip.this.persistenceService.updateCounter(counter);

						final Game game = CounterTooltip.this.persistenceService
							.getGame(HatchetHarrySession.get().getGameId());
						final List<BigInteger> allPlayersInGame = CounterTooltip.this.persistenceService
							.giveAllPlayersFromGame(game.getId());

						final Object counterTooltipCometChannel;
						ConsoleLogStrategy logger;

						if ((counter.getCard() != null) && (counter.getCard().getToken() == null))
						{
							final String targetPlayerName = CounterTooltip.this.persistenceService
								.getPlayer(CounterTooltip.this.card.getDeck().getPlayerId())
								.getName();

							counterTooltipCometChannel = new UpdateCardPanelCometChannel(
								game.getId(), HatchetHarrySession.get().getPlayer().getName(),
								targetPlayerName, CounterTooltip.this.card.getTitle(),
								counter.getCounterName(), counter.getNumberOfCounters(), 0l,
								NotifierAction.ADD_COUNTER_ACTION, CounterTooltip.this.card,
								CounterTooltip.this.card.getBigImageFilename(),
								CounterTooltip.this.card.getOwnerSide());

							logger = AbstractConsoleLogStrategy
								.chooseStrategy(ConsoleLogType.COUNTER_ADD_REMOVE, null, null,
                                        null, CounterTooltip.this.card.getTitle(), HatchetHarrySession
                                                .get().getPlayer().getName(), counter.getCounterName(),
                                        counter.getNumberOfCounters(), targetPlayerName, null,
                                        game.getId());
						}
						else
						{
							final String targetPlayerName = CounterTooltip.this.persistenceService
								.getPlayer(CounterTooltip.this.token.getPlayer().getId()).getName();

							counterTooltipCometChannel = new UpdateTokenPanelCometChannel(
								game.getId(), HatchetHarrySession.get().getPlayer().getName(),
								targetPlayerName, CounterTooltip.this.token.getCreatureTypes(),
								counter.getCounterName(), counter.getNumberOfCounters(), 0l,
								NotifierAction.ADD_COUNTER_ACTION, CounterTooltip.this.token, "",
								CounterTooltip.this.token.getPlayer().getSide().getSideName());

							logger = AbstractConsoleLogStrategy
								.chooseStrategy(ConsoleLogType.COUNTER_ADD_REMOVE, null, null,
                                        null, CounterTooltip.this.token.getCreatureTypes(),
                                        HatchetHarrySession.get().getPlayer().getName(),
                                        counter.getCounterName(), counter.getNumberOfCounters(),
                                        targetPlayerName, null, game.getId());
						}

						for (int i = 0; i < allPlayersInGame.size(); i++)
						{
							final Long p = allPlayersInGame.get(i).longValue();
							final String pageUuid = HatchetHarryApplication.getCometResources()
								.get(p);
							CounterTooltip.LOGGER.info("pageUuid: " + pageUuid);
							HatchetHarryApplication.get().getEventBus()
								.post(counterTooltipCometChannel, pageUuid);
							HatchetHarryApplication.get().getEventBus()
								.post(new ConsoleLogCometChannel(logger), pageUuid);
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
						NotifierAction action = NotifierAction.REMOVE_COUNTER_ACTION;
						String targetPlayerName = "";
						Object msg = "";

						final Game game = CounterTooltip.this.persistenceService
							.getGame(HatchetHarrySession.get().getGameId());
						final List<BigInteger> allPlayersInGame = CounterTooltip.this.persistenceService
							.giveAllPlayersFromGame(game.getId());

						ConsoleLogStrategy logger = null;

						if (counter.getNumberOfCounters().longValue() == 0)
						{

							if (CounterTooltip.this.card != null)
							{
								targetPlayerName = CounterTooltip.this.persistenceService
									.getPlayer(CounterTooltip.this.card.getDeck().getPlayerId())
									.getName();
								CounterTooltip.this.persistenceService.deleteCounter(counter,
									CounterTooltip.this.card, null);

								action = NotifierAction.CLEAR_COUNTER_ACTION;

								msg = new UpdateCardPanelCometChannel(
									game.getId(),
									HatchetHarrySession.get().getPlayer().getName(),
									CounterTooltip.this.persistenceService.getPlayer(
										CounterTooltip.this.card.getDeck().getPlayerId()).getName(),
									CounterTooltip.this.card.getTitle(), counter.getCounterName(),
									counter.getNumberOfCounters(), 0l, action,
									CounterTooltip.this.card, CounterTooltip.this.card
										.getBigImageFilename(), CounterTooltip.this.card
										.getOwnerSide());

								logger = AbstractConsoleLogStrategy.chooseStrategy(
									ConsoleLogType.COUNTER_ADD_REMOVE, null, null, null,
									CounterTooltip.this.card.getTitle(), HatchetHarrySession.get()
										.getPlayer().getName(), counter.getCounterName(), 0l,
									targetPlayerName, null, game.getId());
							}
							else
							{
								targetPlayerName = CounterTooltip.this.persistenceService
									.getPlayer(CounterTooltip.this.token.getPlayer().getId())
									.getName();

								CounterTooltip.this.token.getCounters().remove(counter);
								CounterTooltip.this.persistenceService
									.updateToken(CounterTooltip.this.token);
								final MagicCard card = CounterTooltip.this.persistenceService
									.getCardFromUuid(UUID.fromString(CounterTooltip.this.token
                                            .getUuid()));
								card.getCounters().remove(counter);
								CounterTooltip.this.persistenceService.updateCard(card);

								CounterTooltip.this.persistenceService.deleteCounter(counter, card,
									CounterTooltip.this.token);

								action = NotifierAction.CLEAR_COUNTER_ACTION;

								msg = new UpdateTokenPanelCometChannel(game.getId(),
									HatchetHarrySession.get().getPlayer().getName(),
									targetPlayerName, CounterTooltip.this.token.getCreatureTypes(),
									counter.getCounterName(), counter.getNumberOfCounters(), 0l,
									action, CounterTooltip.this.token, "",
									CounterTooltip.this.token.getPlayer().getSide().getSideName());

								logger = AbstractConsoleLogStrategy.chooseStrategy(
									ConsoleLogType.COUNTER_ADD_REMOVE, null, null, null,
									CounterTooltip.this.token.getCreatureTypes(),
									HatchetHarrySession.get().getPlayer().getName(),
									counter.getCounterName(), 0l, targetPlayerName, null,
									game.getId());
							}
						}
						else
						{
							if (CounterTooltip.this.card != null)
							{
								targetPlayerName = CounterTooltip.this.persistenceService
									.getPlayer(CounterTooltip.this.card.getDeck().getPlayerId())
									.getName();

								logger = AbstractConsoleLogStrategy.chooseStrategy(
									ConsoleLogType.COUNTER_ADD_REMOVE, null, null, null,
									CounterTooltip.this.card.getTitle(), HatchetHarrySession.get()
										.getPlayer().getName(), counter.getCounterName(),
									counter.getNumberOfCounters(), targetPlayerName, null,
									game.getId());
							}
							else
							{
								CounterTooltip.this.persistenceService.updateCounter(counter);
								action = NotifierAction.REMOVE_COUNTER_ACTION;


								logger = AbstractConsoleLogStrategy.chooseStrategy(
									ConsoleLogType.COUNTER_ADD_REMOVE, null, null, null,
									CounterTooltip.this.token.getCreatureTypes(),
									HatchetHarrySession.get().getPlayer().getName(),
									counter.getCounterName(), counter.getNumberOfCounters(),
									targetPlayerName, null, game.getId());
							}
						}


						final Object counterTooltipCometChannel;

						if ((counter.getCard() != null) && (counter.getCard().getToken() == null))
						{
							counterTooltipCometChannel = new UpdateCardPanelCometChannel(
								game.getId(), HatchetHarrySession.get().getPlayer().getName(),
								CounterTooltip.this.persistenceService.getPlayer(
									CounterTooltip.this.card.getDeck().getPlayerId()).getName(),
								CounterTooltip.this.card.getTitle(), counter.getCounterName(),
								counter.getNumberOfCounters(), 0l, action,
								CounterTooltip.this.card,
								CounterTooltip.this.card.getBigImageFilename(),
								CounterTooltip.this.card.getOwnerSide());

							logger = AbstractConsoleLogStrategy
								.chooseStrategy(ConsoleLogType.COUNTER_ADD_REMOVE, null, null,
                                        null, CounterTooltip.this.card.getTitle(), HatchetHarrySession
                                                .get().getPlayer().getName(), counter.getCounterName(),
                                        counter.getNumberOfCounters(), targetPlayerName, null,
                                        game.getId());
						}
						else if (action == NotifierAction.CLEAR_COUNTER_ACTION)
						{
							for (int i = 0; i < allPlayersInGame.size(); i++)
							{
								final Long p = allPlayersInGame.get(i).longValue();
								final String pageUuid = HatchetHarryApplication.getCometResources()
									.get(p);
								CounterTooltip.LOGGER.info("pageUuid: " + pageUuid);
								HatchetHarryApplication.get().getEventBus().post(msg, pageUuid);

								HatchetHarryApplication.get().getEventBus()
									.post(new ConsoleLogCometChannel(logger), pageUuid);
							}
							return;
						}
						else
						{
							targetPlayerName = CounterTooltip.this.persistenceService.getPlayer(
								CounterTooltip.this.token.getPlayer().getId()).getName();

							counterTooltipCometChannel = new UpdateTokenPanelCometChannel(
								game.getId(), HatchetHarrySession.get().getPlayer().getName(),
								targetPlayerName, CounterTooltip.this.token.getCreatureTypes(),
								counter.getCounterName(), counter.getNumberOfCounters(), 0l,
								action, CounterTooltip.this.token, "", CounterTooltip.this.token
									.getPlayer().getSide().getSideName());

							logger = AbstractConsoleLogStrategy
								.chooseStrategy(ConsoleLogType.COUNTER_ADD_REMOVE, null, null,
                                        null, CounterTooltip.this.token.getCreatureTypes(),
                                        HatchetHarrySession.get().getPlayer().getName(),
                                        counter.getCounterName(), counter.getNumberOfCounters(),
                                        targetPlayerName, null, game.getId());
						}

						for (int i = 0; i < allPlayersInGame.size(); i++)
						{
							final Long p = allPlayersInGame.get(i).longValue();
							final String pageUuid = HatchetHarryApplication.getCometResources()
								.get(p);
							CounterTooltip.LOGGER.info("pageUuid: " + pageUuid);
							HatchetHarryApplication.get().getEventBus()
								.post(counterTooltipCometChannel, pageUuid);

							HatchetHarryApplication.get().getEventBus()
								.post(new ConsoleLogCometChannel(logger), pageUuid);
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
						String targetPlayerName = "";
						Object msg = "";

						final Game game = CounterTooltip.this.persistenceService
							.getGame(HatchetHarrySession.get().getGameId());
						final List<BigInteger> allPlayersInGame = CounterTooltip.this.persistenceService
							.giveAllPlayersFromGame(game.getId());

						ConsoleLogStrategy logger = null;

						if (targetNumberOfCounters.longValue() == 0)
						{
							if (CounterTooltip.this.card != null)
							{
								CounterTooltip.this.persistenceService.deleteCounter(counter,
									CounterTooltip.this.card, null);

								action = NotifierAction.CLEAR_COUNTER_ACTION;

								msg = new UpdateCardPanelCometChannel(
									game.getId(),
									HatchetHarrySession.get().getPlayer().getName(),
									CounterTooltip.this.persistenceService.getPlayer(
										CounterTooltip.this.card.getDeck().getPlayerId()).getName(),
									CounterTooltip.this.card.getTitle(), counter.getCounterName(),
									counter.getNumberOfCounters(), 0l, action,
									CounterTooltip.this.card, CounterTooltip.this.card
										.getBigImageFilename(), CounterTooltip.this.card
										.getOwnerSide());

								targetPlayerName = CounterTooltip.this.persistenceService
									.getPlayer(CounterTooltip.this.card.getDeck().getPlayerId())
									.getName();

								logger = AbstractConsoleLogStrategy.chooseStrategy(
									ConsoleLogType.COUNTER_ADD_REMOVE, null, null, null,
									CounterTooltip.this.card.getTitle(), HatchetHarrySession.get()
										.getPlayer().getName(), counter.getCounterName(), 0l,
									targetPlayerName, null, game.getId());
							}
							else
							{
								targetPlayerName = CounterTooltip.this.persistenceService
									.getPlayer(CounterTooltip.this.token.getPlayer().getId())
									.getName();

								CounterTooltip.this.token.getCounters().remove(counter);
								CounterTooltip.this.persistenceService
									.updateToken(CounterTooltip.this.token);
								final MagicCard card = CounterTooltip.this.persistenceService
									.getCardFromUuid(UUID.fromString(CounterTooltip.this.token
                                            .getUuid()));
								card.getCounters().remove(counter);
								CounterTooltip.this.persistenceService.updateCard(card);

								CounterTooltip.this.persistenceService.deleteCounter(counter, null,
									CounterTooltip.this.token);

								action = NotifierAction.CLEAR_COUNTER_ACTION;

								msg = new UpdateTokenPanelCometChannel(game.getId(),
									HatchetHarrySession.get().getPlayer().getName(),
									targetPlayerName, CounterTooltip.this.token.getCreatureTypes(),
									counter.getCounterName(), counter.getNumberOfCounters(), 0l,
									action, CounterTooltip.this.token, "",
									CounterTooltip.this.token.getPlayer().getSide().getSideName());

								logger = AbstractConsoleLogStrategy.chooseStrategy(
									ConsoleLogType.COUNTER_ADD_REMOVE, null, null, null,
									CounterTooltip.this.token.getCreatureTypes(),
									HatchetHarrySession.get().getPlayer().getName(),
									counter.getCounterName(), 0l, targetPlayerName, null,
									game.getId());
							}
						}
						else
						{
							action = NotifierAction.SET_COUNTER_ACTION;
						}

						counter.setNumberOfCounters(targetNumberOfCounters);
						CounterTooltip.this.persistenceService.updateCounter(counter);

						final Object counterTooltipCometChannel;

						if ((counter.getCard() != null) && (counter.getCard().getToken() == null))
						{
							targetPlayerName = CounterTooltip.this.persistenceService.getPlayer(
								CounterTooltip.this.card.getDeck().getPlayerId()).getName();

							counterTooltipCometChannel = new UpdateCardPanelCometChannel(
								game.getId(), HatchetHarrySession.get().getPlayer().getName(),
								targetPlayerName, CounterTooltip.this.card.getTitle(),
								counter.getCounterName(), targetNumberOfCounters,
								originalNumberOfCounters, action, CounterTooltip.this.card,
								CounterTooltip.this.card.getBigImageFilename(),
								CounterTooltip.this.card.getOwnerSide());

							logger = AbstractConsoleLogStrategy
								.chooseStrategy(ConsoleLogType.COUNTER_ADD_REMOVE, null, null,
                                        null, CounterTooltip.this.card.getTitle(), HatchetHarrySession
                                                .get().getPlayer().getName(), counter.getCounterName(),
                                        counter.getNumberOfCounters(), targetPlayerName, null,
                                        game.getId());
						}
						else if (action == NotifierAction.CLEAR_COUNTER_ACTION)
						{
							for (int i = 0; i < allPlayersInGame.size(); i++)
							{
								final Long p = allPlayersInGame.get(i).longValue();
								final String pageUuid = HatchetHarryApplication.getCometResources()
									.get(p);
								CounterTooltip.LOGGER.info("pageUuid: " + pageUuid);
								HatchetHarryApplication.get().getEventBus().post(msg, pageUuid);

								HatchetHarryApplication.get().getEventBus()
									.post(new ConsoleLogCometChannel(logger), pageUuid);
							}
							return;
						}
						else
						{
							targetPlayerName = CounterTooltip.this.persistenceService.getPlayer(
								CounterTooltip.this.token.getPlayer().getId()).getName();

							counterTooltipCometChannel = new UpdateTokenPanelCometChannel(
								game.getId(), HatchetHarrySession.get().getPlayer().getName(),
								targetPlayerName, CounterTooltip.this.token.getCreatureTypes(),
								counter.getCounterName(), targetNumberOfCounters,
								originalNumberOfCounters, action, CounterTooltip.this.token, "",
								CounterTooltip.this.token.getPlayer().getSide().getSideName());


							logger = AbstractConsoleLogStrategy
								.chooseStrategy(ConsoleLogType.COUNTER_ADD_REMOVE, null, null,
                                        null, CounterTooltip.this.token.getCreatureTypes(),
                                        HatchetHarrySession.get().getPlayer().getName(),
                                        counter.getCounterName(), counter.getNumberOfCounters(),
                                        targetPlayerName, null, game.getId());
						}

						for (int i = 0; i < allPlayersInGame.size(); i++)
						{
							final Long p = allPlayersInGame.get(i).longValue();
							final String pageUuid = HatchetHarryApplication.getCometResources()
								.get(p);
							CounterTooltip.LOGGER.info("pageUuid: " + pageUuid);

							HatchetHarryApplication.get().getEventBus()
								.post(counterTooltipCometChannel, pageUuid);

							HatchetHarryApplication.get().getEventBus()
								.post(new ConsoleLogCometChannel(logger), pageUuid);
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

				if (counter.getNumberOfCounters() == 0)
				{
					item.setVisible(false);
				}
			}
		};

		this.add(form, counters);
	}

	@Required
	public void setPersistenceService(final PersistenceService _persistenceService)
	{
		this.persistenceService = _persistenceService;
	}

}
