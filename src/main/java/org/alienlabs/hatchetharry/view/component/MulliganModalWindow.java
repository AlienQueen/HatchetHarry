package org.alienlabs.hatchetharry.view.component;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.alienlabs.hatchetharry.HatchetHarrySession;
import org.alienlabs.hatchetharry.model.CardZone;
import org.alienlabs.hatchetharry.model.MagicCard;
import org.alienlabs.hatchetharry.model.Player;
import org.alienlabs.hatchetharry.model.channel.AskMulliganCometChannel;
import org.alienlabs.hatchetharry.model.channel.ConsoleLogCometChannel;
import org.alienlabs.hatchetharry.model.channel.NotifierAction;
import org.alienlabs.hatchetharry.model.channel.NotifierCometChannel;
import org.alienlabs.hatchetharry.model.channel.consolelog.AbstractConsoleLogStrategy;
import org.alienlabs.hatchetharry.model.channel.consolelog.ConsoleLogStrategy;
import org.alienlabs.hatchetharry.model.channel.consolelog.ConsoleLogType;
import org.alienlabs.hatchetharry.service.PersistenceService;
import org.alienlabs.hatchetharry.view.clientsideutil.EventBusPostService;
import org.alienlabs.hatchetharry.view.clientsideutil.JavaScriptUtils;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

public class MulliganModalWindow extends Panel
{
	static final Logger LOGGER = LoggerFactory.getLogger(MulliganModalWindow.class);
	private static final long serialVersionUID = 1L;
	private final DropDownChoice<String> mulliganInput;
	@SpringBean
	PersistenceService persistenceService;

	public MulliganModalWindow(final ModalWindow window, final String id)
	{
		super(id);
		final Form<String> form = new Form<String>("form");

		@SuppressWarnings("serial")
		final ArrayList<String> mulligan = new ArrayList<String>()
		{
			{
				this.add("1");
				this.add("2");
				this.add("3");
				this.add("4");
				this.add("5");
				this.add("6");
				this.add("7");
			}
		};

		final Model<ArrayList<String>> mulliganModel = new Model<ArrayList<String>>(mulligan);
		final Label mulliganLabel = new Label("mulliganLabel",
			"Choose the number of cards you'd like to draw: ");
		this.mulliganInput = new DropDownChoice<String>("mulliganInput", new Model<String>(),
			mulliganModel);
		this.mulliganInput.setOutputMarkupId(true);

		final AjaxButton ask = new AjaxButton("ask", form)
		{
			private static final long serialVersionUID = 1L;

			@Override
			@edu.umd.cs.findbugs.annotations.SuppressWarnings(value = "DLS_DEAD_LOCAL_STORE", justification = "False positive on NotifierCometChannel instantiation")
			protected void onSubmit(final AjaxRequestTarget target, final Form<?> _form)
			{
				MulliganModalWindow.LOGGER.info("gameId: " + MulliganModalWindow.this.getGameId()
					+ ", playerId: " + MulliganModalWindow.this.getPlayer().getId()
					+ " asks to draw "
					+ MulliganModalWindow.this.mulliganInput.getDefaultModelObjectAsString()
					+ " cards");

				final ConsoleLogStrategy logger = AbstractConsoleLogStrategy.chooseStrategy(
					ConsoleLogType.ASK_FOR_MULLIGAN, null, null, null, null,
					MulliganModalWindow.this.getPlayer().getName(), null, null, null, false, Long
						.parseLong(MulliganModalWindow.this.mulliganInput
							.getDefaultModelObjectAsString()));
				final NotifierCometChannel ncc = new NotifierCometChannel(
					NotifierAction.ASK_FOR_MULLIGAN,
					Long.parseLong(MulliganModalWindow.this.mulliganInput
						.getDefaultModelObjectAsString()), null, MulliganModalWindow.this
						.getPlayer().getName(), null, null, null, null, "");

				final List<BigInteger> allPlayersInGame = MulliganModalWindow.this.persistenceService
					.giveAllPlayersFromGame(MulliganModalWindow.this.getGameId());
				final List<BigInteger> allPlayersInGameExceptMe = MulliganModalWindow.this.persistenceService
					.giveAllPlayersFromGameExceptMe(MulliganModalWindow.this.getGameId(),
						MulliganModalWindow.this.getPlayer().getId());

				MulliganModalWindow.LOGGER.info("players: " + allPlayersInGameExceptMe.size());
				EventBusPostService.post(
					allPlayersInGameExceptMe,
					ncc,
					new AskMulliganCometChannel(MulliganModalWindow.this.getPlayer().getName(),
						Long.parseLong(MulliganModalWindow.this.mulliganInput
							.getDefaultModelObjectAsString())));
				EventBusPostService.post(allPlayersInGame, new ConsoleLogCometChannel(logger));
			}
		};

		final AjaxButton draw = new AjaxButton("draw", form)
		{
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit(final AjaxRequestTarget target, final Form<?> _form)
			{
				MulliganModalWindow.this.drawCards(MulliganModalWindow.this.getGameId(),
					MulliganModalWindow.this.getPlayer(), Long
						.parseLong(MulliganModalWindow.this.mulliganInput
							.getDefaultModelObjectAsString()), target);
			}
		};

		final AjaxButton drawOneLess = new AjaxButton("drawOneLess", form)
		{
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit(final AjaxRequestTarget target, final Form<?> _form)
			{
				MulliganModalWindow.this.drawCards(MulliganModalWindow.this.getGameId(),
					MulliganModalWindow.this.getPlayer(), Long
						.parseLong(MulliganModalWindow.this.mulliganInput
							.getDefaultModelObjectAsString()) - 1, target);
			}
		};

		final AjaxButton quit = new AjaxButton("quit", form)
		{
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit(final AjaxRequestTarget target, final Form<?> _form)
			{
				window.close(target);
			}
		};

		form.add(mulliganLabel, this.mulliganInput, ask, draw, drawOneLess, quit);
		this.add(form);
	}

	@Required
	public void setPersistenceService(final PersistenceService _persistenceService)
	{
		this.persistenceService = _persistenceService;
	}

	private void drawCards(final Long gameId, final Player me, final Long numberOfCards,
		final AjaxRequestTarget target)
	{
		final List<MagicCard> hand = HatchetHarrySession.get().getFirstCardsInHand();
		for (final MagicCard card : hand)
		{
			card.setZone(CardZone.LIBRARY);
		}

		final ArrayList<MagicCard> newHand = new ArrayList<MagicCard>();
		HatchetHarrySession.get().setFirstCardsInHand(newHand);

		final List<MagicCard> deck = HatchetHarrySession.get().getPlayer().getDeck().getCards();
		Collections.shuffle(deck);
		Collections.shuffle(deck);
		Collections.shuffle(deck);

		for (int i = 0; i < numberOfCards; i++)
		{
			deck.get(i).setZone(CardZone.HAND);
			newHand.add(deck.get(i));
		}
		MulliganModalWindow.this.persistenceService.saveOrUpdateAllMagicCards(deck);
		HatchetHarrySession.get().setFirstCardsInHand(newHand);

		JavaScriptUtils.updateHand(target);

		final ConsoleLogStrategy logger = AbstractConsoleLogStrategy.chooseStrategy(
			ConsoleLogType.DONE_MULLIGAN, null, null, null, null, me.getName(), null, null, null,
			false, numberOfCards);
		final NotifierCometChannel ncc = new NotifierCometChannel(NotifierAction.DONE_MULLIGAN,
			numberOfCards, null, me.getName(), null, null, null, null, "");
		final List<BigInteger> allPlayersInGame = MulliganModalWindow.this.persistenceService
			.giveAllPlayersFromGame(gameId);
		MulliganModalWindow.LOGGER.info("players: " + allPlayersInGame.size());
		EventBusPostService.post(allPlayersInGame, ncc, new ConsoleLogCometChannel(logger));
	}

	Long getGameId()
	{
		return HatchetHarrySession.get().getGameId();
	}

	Player getPlayer()
	{
		return HatchetHarrySession.get().getPlayer();
	}

}
