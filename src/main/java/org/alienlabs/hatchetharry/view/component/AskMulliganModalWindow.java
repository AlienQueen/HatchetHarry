package org.alienlabs.hatchetharry.view.component;

import org.alienlabs.hatchetharry.service.PersistenceService;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

public class AskMulliganModalWindow extends Panel
{
	static final Logger LOGGER = LoggerFactory.getLogger(AskMulliganModalWindow.class);
	private static final long serialVersionUID = 1L;

	@SpringBean
	PersistenceService persistenceService;

	public AskMulliganModalWindow(final String id, final String player, final Long numberOfCards)
	{
		super(id);

		final Form<String> form = new Form<String>("form");

		final Label mulliganLabel = new Label("mulliganLabel", player + " asks for a mulligan. He (she) wants to draw " + numberOfCards + " cards. Do you agree?");

		final AjaxButton submit = new AjaxButton("agree", form)
		{
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit(final AjaxRequestTarget target, final Form<?> _form)
			{
				LOGGER.info("agree");

				//				final ConsoleLogStrategy logger = AbstractConsoleLogStrategy.chooseStrategy(
				//						ConsoleLogType.ASK_FOR_MULLIGAN, null, null, null, null,
				//						me.getName(), null, null, null, false,
				//						Long.parseLong(mulliganInput.getDefaultModelObjectAsString()));
				//				final NotifierCometChannel ncc = new NotifierCometChannel(
				//						NotifierAction.ASK_FOR_MULLIGAN, Long.parseLong(mulliganInput.getDefaultModelObjectAsString()), null, me.getName(),
				//						null, null, null, null, "");
				//
				//				final List<BigInteger> allPlayersInGame = AskMulliganModalWindow.this.persistenceService
				//						.giveAllPlayersFromGame(gameId);
				//				final List<BigInteger> allPlayersInGameExceptMe = AskMulliganModalWindow.this.persistenceService
				//						.giveAllPlayersFromGameExceptMe(gameId, me.getId());
				//
				//				LOGGER.info("players: " + allPlayersInGameExceptMe.size());
				//
				//				for (int i = 0; i < allPlayersInGameExceptMe.size(); i++)
				//				{
				//					final Long playerToWhomToSend = allPlayersInGameExceptMe.get(i).longValue();
				//					final String pageUuid = HatchetHarryApplication.getCometResources().get(
				//							playerToWhomToSend);
				//					HatchetHarryApplication.get().getEventBus().post(ncc, pageUuid);
				//				}
				//
				//				for (int i = 0; i < allPlayersInGame.size(); i++)
				//				{
				//					final Long playerToWhomToSend = allPlayersInGame.get(i).longValue();
				//					final String pageUuid = HatchetHarryApplication.getCometResources().get(
				//							playerToWhomToSend);
				//					HatchetHarryApplication.get().getEventBus()
				//					.post(new ConsoleLogCometChannel(logger), pageUuid);
				//				}
			}
		};

		form.add(mulliganLabel, submit);
		this.add(form);
	}

	@Required
	public void setPersistenceService(final PersistenceService _persistenceService)
	{
		this.persistenceService = _persistenceService;
	}

}