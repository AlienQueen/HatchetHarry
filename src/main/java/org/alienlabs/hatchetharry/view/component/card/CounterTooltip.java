package org.alienlabs.hatchetharry.view.component.card;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.alienlabs.hatchetharry.HatchetHarrySession;
import org.alienlabs.hatchetharry.model.Counter;
import org.alienlabs.hatchetharry.model.Game;
import org.alienlabs.hatchetharry.model.MagicCard;
import org.alienlabs.hatchetharry.model.Player;
import org.alienlabs.hatchetharry.model.Token;
import org.alienlabs.hatchetharry.model.channel.ConsoleLogCometChannel;
import org.alienlabs.hatchetharry.model.channel.NotifierAction;
import org.alienlabs.hatchetharry.model.consolelogstrategy.AbstractConsoleLogStrategy;
import org.alienlabs.hatchetharry.model.consolelogstrategy.ConsoleLogStrategy;
import org.alienlabs.hatchetharry.model.consolelogstrategy.ConsoleLogType;
import org.alienlabs.hatchetharry.service.PersistenceService;
import org.alienlabs.hatchetharry.view.clientsideutil.EventBusPostService;
import org.alienlabs.hatchetharry.view.component.gui.ExternalImage;
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
import org.springframework.beans.factory.annotation.Required;

@edu.umd.cs.findbugs.annotations.SuppressFBWarnings(value = "SE_INNER_CLASS", justification = "In Wicket, serializable inner classes are common. And as the parent Page is serialized as well, this is no concern. This is no bad practice in Wicket")
public class CounterTooltip extends Panel
{
	private static final long serialVersionUID = 1L;
	final MagicCard card;
	Token token;

	@SpringBean
	PersistenceService persistenceService;

	public CounterTooltip(final String id, final MagicCard _card, final Token _token)
	{
		super(id);
		this.card = _card;
		this.token = _token;

		final Form<String> form = new Form<>("form");
		final TextField<String> counterAddName = new TextField<>("counterAddName",
				new Model<>(""));
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
				counter.setNumberOfCounters(1L);
				final Set<Counter> counters;
				final Player targetPlayer;
				final ConsoleLogStrategy logger;
				final Game game;

				// We are working with a token
				if ((CounterTooltip.this.card == null)
						|| ("token".equals(CounterTooltip.this.card.getTitle())))
				{
					counter.setToken(CounterTooltip.this.token);
					counters = CounterTooltip.this.token.getCounters();
					counters.add(counter);
					CounterTooltip.this.persistenceService.updateToken(CounterTooltip.this.token);

					targetPlayer = CounterTooltip.this.persistenceService
							.getPlayer(CounterTooltip.this.token.getPlayer().getId());
					game = CounterTooltip.this.persistenceService.getGame(targetPlayer.getGame()
							.getId());

					logger = AbstractConsoleLogStrategy.chooseStrategy(
							ConsoleLogType.COUNTER_ADD_REMOVE, null, null, null,
							CounterTooltip.this.token.getCreatureTypes(), HatchetHarrySession.get()
							.getPlayer().getName(), _counterName,
							counter.getNumberOfCounters(), targetPlayer.getName(), null,
							game.getId());
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

					logger = AbstractConsoleLogStrategy.chooseStrategy(
							ConsoleLogType.COUNTER_ADD_REMOVE, null, null, null,
							CounterTooltip.this.card.getTitle(), HatchetHarrySession.get()
							.getPlayer().getName(), _counterName,
							counter.getNumberOfCounters(), targetPlayer.getName(), null,
							game.getId());
				}

				counterAddName.setModel(Model.of(""));

				final List<BigInteger> allPlayersInGame = CounterTooltip.this.persistenceService
						.giveAllPlayersFromGame(game.getId());
				EventBusPostService.post(allPlayersInGame, new ConsoleLogCometChannel(logger));
			}
		};
		submit.setOutputMarkupId(true);
		form.add(submit);

		final List<Counter> allCounters;

		if ((CounterTooltip.this.card == null)
				|| "token".equals(CounterTooltip.this.card.getTitle()))
		{
			this.token = this.persistenceService.getTokenFromUuid(this.token.getUuid());
			allCounters = new ArrayList<>(this.token.getCounters());
		}
		else
		{
			allCounters = new ArrayList<>(this.card.getCounters());
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
						counter.setNumberOfCounters(counter.getNumberOfCounters() + 1L);
						CounterTooltip.this.persistenceService.updateCounter(counter);

						final Game game = CounterTooltip.this.persistenceService
								.getGame(HatchetHarrySession.get().getGameId());
						final List<BigInteger> allPlayersInGame = CounterTooltip.this.persistenceService
								.giveAllPlayersFromGame(game.getId());

						ConsoleLogStrategy logger;

						if ((counter.getCard() != null) && (counter.getCard().getToken() == null))
						{
							final String targetPlayerName = CounterTooltip.this.persistenceService
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
							final String targetPlayerName = CounterTooltip.this.persistenceService
									.getPlayer(CounterTooltip.this.token.getPlayer().getId())
									.getName();

							logger = AbstractConsoleLogStrategy.chooseStrategy(
									ConsoleLogType.COUNTER_ADD_REMOVE, null, null, null,
									CounterTooltip.this.token.getCreatureTypes(),
									HatchetHarrySession.get().getPlayer().getName(),
									counter.getCounterName(), counter.getNumberOfCounters(),
									targetPlayerName, null, game.getId());
						}

						EventBusPostService.post(allPlayersInGame, new ConsoleLogCometChannel(
								logger));
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
								counter.setNumberOfCounters(counter.getNumberOfCounters() - 1L);
								CounterTooltip.this.persistenceService.updateCounter(counter);

								final String targetPlayerName = "";

								final Game game = CounterTooltip.this.persistenceService
										.getGame(HatchetHarrySession.get().getGameId());
								final List<BigInteger> allPlayersInGame = CounterTooltip.this.persistenceService
										.giveAllPlayersFromGame(game.getId());

								CounterTooltip.this.removeOrChangeCounters(targetPlayerName, game,
										allPlayersInGame, counter);
							}
								};
								removeCounterLink.setOutputMarkupId(true);

								final ExternalImage counterMinus = new ExternalImage("counterMinus",
										"image/minusLife.png");
								counterMinus.setOutputMarkupId(true);
								removeCounterLink.add(counterMinus);

								final Form<String> setCounterForm = new Form<>("setCounterForm");
								final TextField<Long> setCounterButton = new TextField<>("setCounterButton",
										Model.of(counter.getNumberOfCounters()));
								setCounterForm.add(setCounterButton);

								final IndicatingAjaxButton setCounterSubmit = new IndicatingAjaxButton(
										"setCounterSubmit", setCounterForm)
								{
									private static final long serialVersionUID = 1L;

									@Override
									protected void onSubmit(final AjaxRequestTarget target, final Form<?> _form)
									{
										final Long targetNumberOfCounters = Long.valueOf(Long.parseLong(_form.get(
												"setCounterButton").getDefaultModelObjectAsString()));
										counter.setNumberOfCounters(targetNumberOfCounters);
										final String targetPlayerName = "";

										final Game game = CounterTooltip.this.persistenceService
												.getGame(HatchetHarrySession.get().getGameId());
										final List<BigInteger> allPlayersInGame = CounterTooltip.this.persistenceService
												.giveAllPlayersFromGame(game.getId());
										CounterTooltip.this.removeOrChangeCounters(targetPlayerName, game,
												allPlayersInGame, counter);
									}
								};

								setCounterForm.add(new Label("counterName", counter.getCounterName())
								.setOutputMarkupId(true));
								setCounterForm.add(new Label("numberOfCounters", counter.getNumberOfCounters())
								.setOutputMarkupId(true));

								setCounterForm.add(setCounterSubmit, addCounterLink, removeCounterLink);

								setCounterForm.setOutputMarkupPlaceholderTag(false);
								item.add(setCounterForm);

								if (counter.getNumberOfCounters() == 0L)
								{
									item.setVisible(false);
								}
			}
				};
				this.add(form, counters);
	}

	void removeOrChangeCounters(final String _targetPlayerName, final Game game,
			final List<BigInteger> allPlayersInGame, final Counter counter)
	{
		ConsoleLogStrategy logger;
		final RemoveCounter removeCounter = new RemoveCounter(_targetPlayerName, game, counter)
		.removeCounterIfNeeded();
		final NotifierAction action = removeCounter.getAction();
		final String targetPlayerName = removeCounter.getTargetPlayerName();
		logger = removeCounter.getLogger();

		this.changeNumberOfCounters(targetPlayerName, game, allPlayersInGame, logger, action,
				counter);
	}

	private void changeNumberOfCounters(final String _targetPlayerName, final Game game,
			final List<BigInteger> allPlayersInGame, final ConsoleLogStrategy _logger,
			final NotifierAction action, final Counter counter)
	{
		ConsoleLogStrategy logger = null;

		if ((counter.getCard() != null) && (counter.getCard().getToken() == null))
		{
			logger = AbstractConsoleLogStrategy.chooseStrategy(ConsoleLogType.COUNTER_ADD_REMOVE,
					null, null, null, this.card.getTitle(), HatchetHarrySession.get().getPlayer()
					.getName(), counter.getCounterName(), counter.getNumberOfCounters(),
					_targetPlayerName, null, game.getId());
		}
		else if (action == NotifierAction.CLEAR_COUNTER_ACTION)
		{
			this.persistenceService.updateCounter(counter);
			EventBusPostService.post(allPlayersInGame, new ConsoleLogCometChannel(_logger));
			return;
		}
		else
		{
			final String targetPlayerName = this.persistenceService.getPlayer(
					this.token.getPlayer().getId()).getName();

			logger = AbstractConsoleLogStrategy.chooseStrategy(ConsoleLogType.COUNTER_ADD_REMOVE,
					null, null, null, this.token.getCreatureTypes(), HatchetHarrySession.get()
					.getPlayer().getName(), counter.getCounterName(),
					counter.getNumberOfCounters(), targetPlayerName, null, game.getId());
		}

		this.persistenceService.updateCounter(counter);
		EventBusPostService.post(allPlayersInGame, new ConsoleLogCometChannel(logger));
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
		private final Game game;
		private final Counter counter;
		private ConsoleLogStrategy logger;

		public RemoveCounter(final String _targetPlayerName, final Game _game,
				final Counter _counter)
		{
			this.targetPlayerName = _targetPlayerName;
			this.game = _game;
			this.counter = _counter;
		}

		public NotifierAction getAction()
		{
			return this.action;
		}

		public String getTargetPlayerName()
		{
			return this.targetPlayerName;
		}

		public ConsoleLogStrategy getLogger()
		{
			return this.logger;
		}

		public RemoveCounter removeCounterIfNeeded()
		{
			this.targetPlayerName = CounterTooltip.this.persistenceService.getPlayer(
					CounterTooltip.this.card.getDeck().getPlayerId()).getName();

			if (this.counter.getNumberOfCounters() == 0L)
			{

				this.action = NotifierAction.CLEAR_COUNTER_ACTION;
				CounterTooltip.this.persistenceService.deleteCounter(this.counter,
						CounterTooltip.this.card, null);

				this.logger = AbstractConsoleLogStrategy.chooseStrategy(
						ConsoleLogType.COUNTER_ADD_REMOVE, null, null, null,
						CounterTooltip.this.card.getTitle(), HatchetHarrySession.get().getPlayer()
						.getName(), this.counter.getCounterName(), 0L,
						this.targetPlayerName, null, this.game.getId());
			}
			else
			{

				this.action = NotifierAction.REMOVE_COUNTER_ACTION;
				CounterTooltip.this.persistenceService.updateCounter(this.counter);

				this.logger = AbstractConsoleLogStrategy.chooseStrategy(
						ConsoleLogType.COUNTER_ADD_REMOVE, null, null, null,
						CounterTooltip.this.card.getTitle(), HatchetHarrySession.get().getPlayer()
						.getName(), this.counter.getCounterName(),
						this.counter.getNumberOfCounters(), this.targetPlayerName, null,
						this.game.getId());
			}
			return this;
		}
	}
}
