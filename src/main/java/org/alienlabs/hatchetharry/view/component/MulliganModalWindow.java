package org.alienlabs.hatchetharry.view.component;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.alienlabs.hatchetharry.HatchetHarryApplication;
import org.alienlabs.hatchetharry.model.Player;
import org.alienlabs.hatchetharry.model.channel.AskMulliganCometChannel;
import org.alienlabs.hatchetharry.model.channel.ConsoleLogCometChannel;
import org.alienlabs.hatchetharry.model.channel.NotifierAction;
import org.alienlabs.hatchetharry.model.channel.NotifierCometChannel;
import org.alienlabs.hatchetharry.model.channel.consolelog.AbstractConsoleLogStrategy;
import org.alienlabs.hatchetharry.model.channel.consolelog.ConsoleLogStrategy;
import org.alienlabs.hatchetharry.model.channel.consolelog.ConsoleLogType;
import org.alienlabs.hatchetharry.service.PersistenceService;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
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

	@SpringBean
	PersistenceService persistenceService;

	public MulliganModalWindow(final String id, final Long gameId, final Player me)
	{
		super(id);

		final Form<String> form = new Form<String>("form");

		final ArrayList<String> mulligan = new ArrayList<String>() {{ add("1");  add("2"); add("3");  add("4"); add("5");  add("6"); add("7"); }};

		final Model<ArrayList<String>> mulliganModel = new Model<ArrayList<String>>(mulligan);
		final Label mulliganLabel = new Label("mulliganLabel", "Choose the number of cards you'd like to draw: ");
		final DropDownChoice<String> mulliganInput = new DropDownChoice<String>("mulliganInput", new Model<String>(), mulliganModel);
		mulliganInput.setOutputMarkupId(true);

		final AjaxButton submit = new AjaxButton("submit", form)
		{
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit(final AjaxRequestTarget target, final Form<?> _form)
			{
				LOGGER.info("gameId: " + gameId + ", playerId: "  + me.getId() + " ask to draw " + mulliganInput.getDefaultModelObjectAsString() + " cards");

				final ConsoleLogStrategy logger = AbstractConsoleLogStrategy.chooseStrategy(
						ConsoleLogType.ASK_FOR_MULLIGAN, null, null, null, null,
						me.getName(), null, null, null, false,
						Long.parseLong(mulliganInput.getDefaultModelObjectAsString()));
				final NotifierCometChannel ncc = new NotifierCometChannel(
						NotifierAction.ASK_FOR_MULLIGAN, Long.parseLong(mulliganInput.getDefaultModelObjectAsString()), null, me.getName(),
						null, null, null, null, "");

				final List<BigInteger> allPlayersInGame = MulliganModalWindow.this.persistenceService
						.giveAllPlayersFromGame(gameId);
				final List<BigInteger> allPlayersInGameExceptMe = MulliganModalWindow.this.persistenceService
						.giveAllPlayersFromGameExceptMe(gameId, me.getId());

				LOGGER.info("players: " + allPlayersInGameExceptMe.size());

				for (int i = 0; i < allPlayersInGameExceptMe.size(); i++)
				{
					final Long playerToWhomToSend = allPlayersInGameExceptMe.get(i).longValue();
					final String pageUuid = HatchetHarryApplication.getCometResources().get(
							playerToWhomToSend);
					HatchetHarryApplication.get().getEventBus().post(ncc, pageUuid);
					HatchetHarryApplication.get().getEventBus().post(new AskMulliganCometChannel(me.getName(), Long.parseLong(mulliganInput.getDefaultModelObjectAsString())), pageUuid);
				}

				for (int i = 0; i < allPlayersInGame.size(); i++)
				{
					final Long playerToWhomToSend = allPlayersInGame.get(i).longValue();
					final String pageUuid = HatchetHarryApplication.getCometResources().get(
							playerToWhomToSend);
					HatchetHarryApplication.get().getEventBus()
					.post(new ConsoleLogCometChannel(logger), pageUuid);
				}
			}
		};

		form.add(mulliganLabel, mulliganInput, submit);
		this.add(form);
	}

	@Required
	public void setPersistenceService(final PersistenceService _persistenceService)
	{
		this.persistenceService = _persistenceService;
	}

}