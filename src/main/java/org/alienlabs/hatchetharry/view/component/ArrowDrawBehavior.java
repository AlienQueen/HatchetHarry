package org.alienlabs.hatchetharry.view.component;

import java.io.IOException;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.alienlabs.hatchetharry.HatchetHarryApplication;
import org.alienlabs.hatchetharry.HatchetHarrySession;
import org.alienlabs.hatchetharry.model.channel.ArrowDrawCometChannel;
import org.alienlabs.hatchetharry.service.PersistenceService;
import org.alienlabs.hatchetharry.view.page.HomePage;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.injection.Injector;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.protocol.http.servlet.ServletWebRequest;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.template.PackageTextTemplate;
import org.apache.wicket.util.template.TextTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

public class ArrowDrawBehavior extends AbstractDefaultAjaxBehavior
{
	private static final long serialVersionUID = 1L;

	private static final Logger LOGGER = LoggerFactory.getLogger(ArrowDrawBehavior.class);

	@SpringBean
	private PersistenceService persistenceService;

	private final String markupId;

	public ArrowDrawBehavior(final String _markupId)
	{
		this.markupId = _markupId;
		Injector.get().inject(this);
	}

	@Override
	protected void respond(final AjaxRequestTarget target)
	{
		ArrowDrawBehavior.LOGGER.info("respond");

		final ServletWebRequest servletWebRequest = (ServletWebRequest)target.getPage()
				.getRequest();
		final HttpServletRequest request = servletWebRequest.getContainerRequest();

		final String arrowDrawSource = request.getParameter("source");
		final String arrowDrawTarget = request.getParameter("target");

		ArrowDrawBehavior.LOGGER.info("respond, gameId= " + HatchetHarrySession.get().getGameId());

		final Long gameId = HatchetHarrySession.get().getGameId();
		final List<BigInteger> allPlayersInGame = ArrowDrawBehavior.this.persistenceService
				.giveAllPlayersFromGame(gameId);

		for (int i = 0; i < allPlayersInGame.size(); i++)
		{
			final Long playerToWhomToSend = allPlayersInGame.get(i).longValue();
			final String pageUuid = HatchetHarryApplication.getCometResources().get(
					playerToWhomToSend);
			final ArrowDrawCometChannel cacc = new ArrowDrawCometChannel(arrowDrawSource,
					arrowDrawTarget);

			// For unit tests only, we'll ask a solution to Emond
			try
			{
				HatchetHarryApplication.get().getEventBus().post(cacc, pageUuid);
			}
			catch (final NullPointerException e)
			{
				// Nothing to do in unit tests
			}
		}
	}

	@Override
	public void renderHead(final Component component, final IHeaderResponse response)
	{
		super.renderHead(component, response);

		final HashMap<String, Object> variables = new HashMap<String, Object>();
		variables.put("arrowDrawUrl", this.getCallbackUrl());
		variables.put("uuidValidForJs", this.markupId);

		final TextTemplate template = new PackageTextTemplate(HomePage.class,
				"script/arrowDraw/arrowDraw.js");
		template.interpolate(variables);

		response.render(JavaScriptHeaderItem.forScript(template.asString(), null));
		try
		{
			template.close();
		}
		catch (final IOException e)
		{
			ArrowDrawBehavior.LOGGER.error(
					"unable to close template in CardRotateBehavior#renderHead()!", e);
		}
	}

	@Required
	public void setPersistenceService(final PersistenceService _persistenceService)
	{
		this.persistenceService = _persistenceService;
	}

}
