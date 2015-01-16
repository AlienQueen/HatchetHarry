package org.alienlabs.hatchetharry.view.component.modalwindow;

import java.math.BigInteger;
import java.util.List;

import org.alienlabs.hatchetharry.HatchetHarrySession;
import org.alienlabs.hatchetharry.model.channel.ConsoleLogCometChannel;
import org.alienlabs.hatchetharry.model.channel.NotifierAction;
import org.alienlabs.hatchetharry.model.channel.NotifierCometChannel;
import org.alienlabs.hatchetharry.model.channel.consolelog.AbstractConsoleLogStrategy;
import org.alienlabs.hatchetharry.model.channel.consolelog.ConsoleLogStrategy;
import org.alienlabs.hatchetharry.model.channel.consolelog.ConsoleLogType;
import org.alienlabs.hatchetharry.service.PersistenceService;
import org.alienlabs.hatchetharry.view.clientsideutil.EventBusPostService;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

@edu.umd.cs.findbugs.annotations.SuppressFBWarnings(value = "SE_INNER_CLASS", justification = "In Wicket, serializable inner classes are common. And as the parent Page is serialized as well, this is no concern. This is no bad practice in Wicket")
public class AskMulliganModalWindow extends Panel
{
	private static final Logger LOGGER = LoggerFactory.getLogger(AskMulliganModalWindow.class);
	private static final long serialVersionUID = 1L;

	@SpringBean
	PersistenceService persistenceService;

	public AskMulliganModalWindow(final ModalWindow window, final String id, final String player,
			final Long numberOfCards)
	{
		super(id);

		final Form<String> form = new Form<String>("form");

		final Label mulliganLabel = new Label("mulliganLabel", player
				+ " asks for a mulligan. He (she) wants to draw " + numberOfCards
				+ " cards. Do you agree?");

		final AjaxButton agree = new AjaxButton("agree", form)
		{
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit(final AjaxRequestTarget target, final Form<?> _form)
			{
				AskMulliganModalWindow.LOGGER.info("agree");
				window.close(target);

				final ConsoleLogStrategy logger = AbstractConsoleLogStrategy.chooseStrategy(
						ConsoleLogType.OK_FOR_MULLIGAN, null, null, null, null, HatchetHarrySession
								.get().getPlayer().getName(), null, null, player, Boolean.FALSE,
						numberOfCards);
				final List<BigInteger> allPlayersInGame = AskMulliganModalWindow.this.persistenceService
						.giveAllPlayersFromGame(HatchetHarrySession.get().getGameId());
				final NotifierCometChannel ncc = new NotifierCometChannel(
						NotifierAction.OK_FOR_MULLIGAN, numberOfCards, null, HatchetHarrySession
								.get().getPlayer().getName(), null, null, null, null, player);
				EventBusPostService.post(allPlayersInGame, new ConsoleLogCometChannel(logger), ncc);
			}
		};

		final AjaxButton oneLess = new AjaxButton("oneLess", Model.of("OK for "
				+ (numberOfCards - 1L)), form)
		{
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit(final AjaxRequestTarget target, final Form<?> _form)
			{
				AskMulliganModalWindow.LOGGER.info("agree for one less");
				window.close(target);

				final ConsoleLogStrategy logger = AbstractConsoleLogStrategy.chooseStrategy(
						ConsoleLogType.OK_FOR_MULLIGAN_BUT_ONE_LESS, null, null, null, null,
						HatchetHarrySession.get().getPlayer().getName(), null, null, player,
						Boolean.FALSE, numberOfCards - 1L);
				final NotifierCometChannel ncc = new NotifierCometChannel(
						NotifierAction.OK_FOR_MULLIGAN_BUT_ONE_LESS, Long.valueOf(numberOfCards
								.longValue() - 1), null, HatchetHarrySession.get().getPlayer()
								.getName(), null, null, null, null, player);

				final List<BigInteger> allPlayersInGame = AskMulliganModalWindow.this.persistenceService
						.giveAllPlayersFromGame(HatchetHarrySession.get().getGameId());
				EventBusPostService.post(allPlayersInGame, new ConsoleLogCometChannel(logger), ncc);
			}
		};
		oneLess.setVisible(numberOfCards > 1L);

		final AjaxButton disagree = new AjaxButton("disagree", form)
		{
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit(final AjaxRequestTarget target, final Form<?> _form)
			{
				AskMulliganModalWindow.LOGGER.info("disagree");
				window.close(target);

				final ConsoleLogStrategy logger = AbstractConsoleLogStrategy.chooseStrategy(
						ConsoleLogType.REFUSE_MULLIGAN, null, null, null, null, HatchetHarrySession
								.get().getPlayer().getName(), null, null, player, Boolean.FALSE,
						numberOfCards - 1L);
				final NotifierCometChannel ncc = new NotifierCometChannel(
						NotifierAction.REFUSE_MULLIGAN, numberOfCards - 1L, null,
						HatchetHarrySession.get().getPlayer().getName(), null, null, null, null,
						player);

				final List<BigInteger> allPlayersInGame = AskMulliganModalWindow.this.persistenceService
						.giveAllPlayersFromGame(HatchetHarrySession.get().getGameId());
				EventBusPostService.post(allPlayersInGame, new ConsoleLogCometChannel(logger), ncc);
			}
		};

		form.add(mulliganLabel, agree, oneLess, disagree);
		this.add(form);
	}

	@Required
	public void setPersistenceService(final PersistenceService _persistenceService)
	{
		this.persistenceService = _persistenceService;
	}

}
