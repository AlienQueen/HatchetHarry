package org.alienlabs.hatchetharry.view.component;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;

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
import org.alienlabs.hatchetharry.view.clientsideutil.EventBusPostService;
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

@edu.umd.cs.findbugs.annotations.SuppressWarnings(value = "SE_INNER_CLASS", justification = "In Wicket, serializable inner classes are common. And as the parent Page is serialized as well, this is no concern. This is no bad practice in Wicket")
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
				final Set<Counter> counters;
				final Player targetPlayer;
				final Object updateCometChannel;
				final ConsoleLogStrategy logger;
				final Game game;

				// We are working with a token
				if (CounterTooltip.this.card == null)
				{
					counter.setToken(CounterTooltip.this.token);
					counters = CounterTooltip.this.token.getCounters();
					counters.add(counter);
					CounterTooltip.this.persistenceService.saveOrUpdateCounter(counter);
					CounterTooltip.this.persistenceService.updateToken(CounterTooltip.this.token);
					targetPlayer = CounterTooltip.this.persistenceService
						.getPlayer(CounterTooltip.this.token.getPlayer().getId());
					game = CounterTooltip.this.persistenceService.getGame(targetPlayer.getGame()
						.getId());

					updateCometChannel = new UpdateTokenPanelCometChannel(game.getId(),
						HatchetHarrySession.get().getPlayer().getName(), targetPlayer.getName(),
						CounterTooltip.this.token.getCreatureTypes(), _counterName,
						counter.getNumberOfCounters(), 0l, NotifierAction.ADD_COUNTER_ACTION,
						CounterTooltip.this.token, CounterTooltip.this.token.getPlayer().getSide()
							.getSideName());

					logger = AbstractConsoleLogStrategy.chooseStrategy(
						ConsoleLogType.COUNTER_ADD_REMOVE, null, null, null,
						CounterTooltip.this.token.getCreatureTypes(), HatchetHarrySession.get()
							.getPlayer().getName(), _counterName, counter.getNumberOfCounters(),
						targetPlayer.getName(), null, game.getId());
				}
				// We are working with a card
				else
				{
					counter.setCard(CounterTooltip.this.card);
					counters = CounterTooltip.this.card.getCounters();
					counters.add(counter);
					CounterTooltip.this.persistenceService.updateCard(CounterTooltip.this.card);

					targetPlayer = CounterTooltip.this.persistenceService
						.getPlayer(CounterTooltip.this.card.getDeck().getPlayerId());
					game = CounterTooltip.this.persistenceService.getGame(targetPlayer.getGame()
						.getId());

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

				counterAddName.setModel(Model.of(""));
				final List<BigInteger> allPlayersInGame = CounterTooltip.this.persistenceService
					.giveAllPlayersFromGame(game.getId());
				EventBusPostService.post(allPlayersInGame, updateCometChannel,
					new ConsoleLogCometChannel(logger));
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
								NotifierAction.ADD_COUNTER_ACTION, CounterTooltip.this.token,
								CounterTooltip.this.token.getPlayer().getSide().getSideName());

							logger = AbstractConsoleLogStrategy
								.chooseStrategy(ConsoleLogType.COUNTER_ADD_REMOVE, null, null,
									null, CounterTooltip.this.token.getCreatureTypes(),
									HatchetHarrySession.get().getPlayer().getName(),
									counter.getCounterName(), counter.getNumberOfCounters(),
									targetPlayerName, null, game.getId());
						}

						EventBusPostService.post(allPlayersInGame, counterTooltipCometChannel,
							new ConsoleLogCometChannel(logger));
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
						final String targetPlayerName = "";
						final Object msg = "";

						final Game game = CounterTooltip.this.persistenceService
							.getGame(HatchetHarrySession.get().getGameId());
						final List<BigInteger> allPlayersInGame = CounterTooltip.this.persistenceService
							.giveAllPlayersFromGame(game.getId());
						final ConsoleLogStrategy logger = null;

						CounterTooltip.this.removeOrChangeCounters(0l,
							counter.getNumberOfCounters(), targetPlayerName, msg, game,
							allPlayersInGame, counter);
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
						final Long originalNumberOfCounters = counter.getNumberOfCounters();
						final Long targetNumberOfCounters = Long.parseLong((String)_form.get(
							"setCounterButton").getDefaultModelObject());
						counter.setNumberOfCounters(targetNumberOfCounters);
						final String targetPlayerName = "";
						final Object msg = "";

						final Game game = CounterTooltip.this.persistenceService
							.getGame(HatchetHarrySession.get().getGameId());
						final List<BigInteger> allPlayersInGame = CounterTooltip.this.persistenceService
							.giveAllPlayersFromGame(game.getId());
						final ConsoleLogStrategy logger = null;

						CounterTooltip.this.removeOrChangeCounters(originalNumberOfCounters,
							targetNumberOfCounters, targetPlayerName, msg, game, allPlayersInGame,
							counter);
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

	void removeOrChangeCounters(final Long originalNumberOfCounters,
		final Long targetNumberOfCounters, String targetPlayerName, Object msg, final Game game,
		final List<BigInteger> allPlayersInGame, final Counter counter)
	{
		ConsoleLogStrategy logger;
		final RemoveCounter removeCounter = new RemoveCounter(targetPlayerName, msg, game, counter)
			.removeCounterIfNeeded();
		final NotifierAction action = removeCounter.getAction();
		targetPlayerName = removeCounter.getTargetPlayerName();
		logger = removeCounter.getLogger();
		msg = removeCounter.getMsg();

		this.changeNumberOfCounters(originalNumberOfCounters, targetNumberOfCounters,
			targetPlayerName, msg, game, allPlayersInGame, logger, action, counter);
	}

	private void changeNumberOfCounters(final Long originalNumberOfCounters,
		final Long targetNumberOfCounters, String targetPlayerName, final Object msg,
		final Game game, final List<BigInteger> allPlayersInGame, ConsoleLogStrategy logger,
		final NotifierAction action, final Counter counter)
	{
		final Object counterTooltipCometChannel;

		if ((counter.getCard() != null) && (counter.getCard().getToken() == null))
		{
			counterTooltipCometChannel = new UpdateCardPanelCometChannel(game.getId(),
				HatchetHarrySession.get().getPlayer().getName(), this.persistenceService.getPlayer(
					this.card.getDeck().getPlayerId()).getName(), this.card.getTitle(),
				counter.getCounterName(), targetNumberOfCounters, originalNumberOfCounters, action,
				this.card, this.card.getBigImageFilename(), this.card.getOwnerSide());

			logger = AbstractConsoleLogStrategy.chooseStrategy(ConsoleLogType.COUNTER_ADD_REMOVE,
				null, null, null, this.card.getTitle(), HatchetHarrySession.get().getPlayer()
					.getName(), counter.getCounterName(), counter.getNumberOfCounters(),
				targetPlayerName, null, game.getId());
		}
		else if (action == NotifierAction.CLEAR_COUNTER_ACTION)
		{
			EventBusPostService.post(allPlayersInGame, msg, new ConsoleLogCometChannel(logger));
			return;
		}
		else
		{
			targetPlayerName = this.persistenceService.getPlayer(this.token.getPlayer().getId())
				.getName();

			counterTooltipCometChannel = new UpdateTokenPanelCometChannel(game.getId(),
				HatchetHarrySession.get().getPlayer().getName(), targetPlayerName,
				this.token.getCreatureTypes(), counter.getCounterName(), targetNumberOfCounters,
				originalNumberOfCounters, action, this.token, this.token.getPlayer().getSide()
					.getSideName());


			logger = AbstractConsoleLogStrategy.chooseStrategy(ConsoleLogType.COUNTER_ADD_REMOVE,
				null, null, null, this.token.getCreatureTypes(), HatchetHarrySession.get()
					.getPlayer().getName(), counter.getCounterName(),
				counter.getNumberOfCounters(), targetPlayerName, null, game.getId());
		}

		EventBusPostService.post(allPlayersInGame, counterTooltipCometChannel,
			new ConsoleLogCometChannel(logger));
	}

	@Required
	public void setPersistenceService(final PersistenceService _persistenceService)
	{
		this.persistenceService = _persistenceService;
	}

	class RemoveCounter
	{
		private NotifierAction action;
		private String targetPlayerName;
		private Object msg;
		private final Game game;
		private final Counter counter;
		private ConsoleLogStrategy logger;

		public RemoveCounter(final String targetPlayerName, final Object msg, final Game game,
			final Counter counter)
		{
			this.targetPlayerName = targetPlayerName;
			this.msg = msg;
			this.game = game;
			this.counter = counter;
		}

		public NotifierAction getAction()
		{
			return this.action;
		}

		public String getTargetPlayerName()
		{
			return this.targetPlayerName;
		}

		public Object getMsg()
		{
			return this.msg;
		}

		public ConsoleLogStrategy getLogger()
		{
			return this.logger;
		}

		public RemoveCounter removeCounterIfNeeded()
		{
			if (this.counter.getNumberOfCounters().longValue() == 0)
			{
				this.action = NotifierAction.CLEAR_COUNTER_ACTION;
				if (CounterTooltip.this.card != null)
				{
					final Deck d = CounterTooltip.this.card.getDeck();
					final String counterName = this.counter.getCounterName();
					final Long targetNumberOfCounters = this.counter.getNumberOfCounters();

                    this.targetPlayerName = CounterTooltip.this.persistenceService.getPlayer(
						CounterTooltip.this.card.getDeck().getPlayerId()).getName();

					CounterTooltip.this.persistenceService.deleteCounter(this.counter,
						CounterTooltip.this.card, null);

					this.msg = new UpdateCardPanelCometChannel(
						this.game.getId(),
						HatchetHarrySession.get().getPlayer().getName(),
						CounterTooltip.this.persistenceService.getPlayer(d.getPlayerId()).getName(),
						CounterTooltip.this.card.getTitle(), counterName, targetNumberOfCounters,
						0l, this.action, CounterTooltip.this.card, CounterTooltip.this.card
							.getBigImageFilename(), CounterTooltip.this.card.getOwnerSide());

					this.logger = AbstractConsoleLogStrategy.chooseStrategy(
						ConsoleLogType.COUNTER_ADD_REMOVE, null, null, null,
						CounterTooltip.this.card.getTitle(), HatchetHarrySession.get().getPlayer()
							.getName(), this.counter.getCounterName(), 0l, this.targetPlayerName,
						null, this.game.getId());
				}
				else
				{
					this.targetPlayerName = CounterTooltip.this.persistenceService.getPlayer(
						CounterTooltip.this.token.getPlayer().getId()).getName();

					CounterTooltip.this.token.getCounters().remove(this.counter);
					CounterTooltip.this.persistenceService.updateToken(CounterTooltip.this.token);
					final MagicCard card = CounterTooltip.this.persistenceService
						.getCardFromUuid(UUID.fromString(CounterTooltip.this.token.getUuid()));
					card.getCounters().remove(this.counter);
					CounterTooltip.this.persistenceService.updateCard(card);

					CounterTooltip.this.persistenceService.deleteCounter(this.counter, card,
						CounterTooltip.this.token);

					this.msg = new UpdateTokenPanelCometChannel(this.game.getId(),
						HatchetHarrySession.get().getPlayer().getName(), this.targetPlayerName,
						CounterTooltip.this.token.getCreatureTypes(),
						this.counter.getCounterName(), this.counter.getNumberOfCounters(), 0l,
						this.action, CounterTooltip.this.token, CounterTooltip.this.token
							.getPlayer().getSide().getSideName());

					this.logger = AbstractConsoleLogStrategy.chooseStrategy(
						ConsoleLogType.COUNTER_ADD_REMOVE, null, null, null,
						CounterTooltip.this.token.getCreatureTypes(), HatchetHarrySession.get()
							.getPlayer().getName(), this.counter.getCounterName(), 0l,
						this.targetPlayerName, null, this.game.getId());
				}
			}
			else
			{
				this.action = NotifierAction.REMOVE_COUNTER_ACTION;
				if (CounterTooltip.this.card != null)
				{
					this.targetPlayerName = CounterTooltip.this.persistenceService.getPlayer(
						CounterTooltip.this.card.getDeck().getPlayerId()).getName();

					this.logger = AbstractConsoleLogStrategy.chooseStrategy(
						ConsoleLogType.COUNTER_ADD_REMOVE, null, null, null,
						CounterTooltip.this.card.getTitle(), HatchetHarrySession.get().getPlayer()
							.getName(), this.counter.getCounterName(),
						this.counter.getNumberOfCounters(), this.targetPlayerName, null,
						this.game.getId());
				}
				else
				{
					CounterTooltip.this.persistenceService.updateCounter(this.counter);

					this.logger = AbstractConsoleLogStrategy.chooseStrategy(
						ConsoleLogType.COUNTER_ADD_REMOVE, null, null, null,
						CounterTooltip.this.token.getCreatureTypes(), HatchetHarrySession.get()
							.getPlayer().getName(), this.counter.getCounterName(),
						this.counter.getNumberOfCounters(), this.targetPlayerName, null,
						this.game.getId());
				}
			}
			return this;
		}
	}
}
