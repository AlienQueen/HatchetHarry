package org.alienlabs.hatchetharry.view.component;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;

import org.alienlabs.hatchetharry.HatchetHarryApplication;
import org.alienlabs.hatchetharry.HatchetHarrySession;
import org.alienlabs.hatchetharry.model.Player;
import org.alienlabs.hatchetharry.model.channel.NotifierAction;
import org.alienlabs.hatchetharry.model.channel.NotifierCometChannel;
import org.alienlabs.hatchetharry.service.PersistenceService;
import org.alienlabs.hatchetharry.view.page.HomePage;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.template.PackageTextTemplate;
import org.apache.wicket.util.template.TextTemplate;
import org.springframework.beans.factory.annotation.Required;

public class AcceptEndTurnBehavior extends AbstractDefaultAjaxBehavior
{
	private static final long serialVersionUID = 1L;

	@SpringBean
	private PersistenceService persistenceService;

	@Override
	public void respond(final AjaxRequestTarget target)
	{
		final Player me = HatchetHarrySession.get().getPlayer();
		final Long gameId = this.persistenceService
				.getPlayer(HatchetHarrySession.get().getPlayer().getId()).getGame().getId();
		final List<BigInteger> allPlayersInGame = this.persistenceService
				.giveAllPlayersFromGame(gameId);

		for (int i = 0; i < allPlayersInGame.size(); i++)
		{
			final Long playerToWhomToSend = allPlayersInGame.get(i).longValue();
			final String pageUuid = HatchetHarryApplication.getCometResources().get(
					playerToWhomToSend);
			final NotifierCometChannel ncc = new NotifierCometChannel(
					NotifierAction.ACCEPT_END_OF_TURN_ACTION, null, null, me.getName(), null, null,
					null, null, "");

			HatchetHarryApplication.get().getEventBus().post(ncc, pageUuid);
		}
	}

	@Override
	public void renderHead(final Component component, final IHeaderResponse response)
	{
		super.renderHead(component, response);

		final TextTemplate template = new PackageTextTemplate(HomePage.class,
				"script/acceptEndTurn.js");

		final HashMap<String, Object> variables = new HashMap<String, Object>();
		variables.put("url", this.getCallbackUrl());
		template.interpolate(variables);

		final StringBuffer js = new StringBuffer().append(template.asString());
		response.render(JavaScriptHeaderItem.forScript(js.toString(), null));
	}

	@Required
	public void setPersistenceService(final PersistenceService _persistenceService)
	{
		this.persistenceService = _persistenceService;
	}

}
