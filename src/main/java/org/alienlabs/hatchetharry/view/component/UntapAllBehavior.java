package org.alienlabs.hatchetharry.view.component;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.alienlabs.hatchetharry.model.MagicCard;
import org.alienlabs.hatchetharry.service.PersistenceService;
import org.alienlabs.hatchetharry.view.page.HomePage;
import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.injection.web.InjectorHolder;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.protocol.http.servlet.ServletWebRequest;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.template.PackagedTextTemplate;
import org.apache.wicket.util.template.TextTemplate;
import org.atmosphere.cpr.AtmosphereResourceEventListener;
import org.atmosphere.cpr.BroadcastFilter;
import org.atmosphere.cpr.Meteor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

public class UntapAllBehavior extends AbstractDefaultAjaxBehavior
{
	private static final long serialVersionUID = 1L;
	static final Logger logger = LoggerFactory.getLogger(UntapAllBehavior.class);

	@SpringBean
	private transient PersistenceService persistenceService;

	public UntapAllBehavior()
	{
		InjectorHolder.getInjector().inject(this);
	}

	@Override
	protected void respond(final AjaxRequestTarget target)
	{
		UntapAllBehavior.logger.info("respond");

		final ServletWebRequest servletWebRequest = (ServletWebRequest)target.getPage()
				.getRequest();
		final HttpServletRequest request = servletWebRequest.getHttpServletRequest();
		final String jsessionid = request.getRequestedSessionId();
		final Long playerId = Long.parseLong(request.getParameter("playerId"));

		if ((null == request.getParameter("cards"))
				|| (jsessionid.equals(request.getParameter("sessionid"))))
		{
			UntapAllBehavior.logger.info("respond first if");
			final StringBuffer message = new StringBuffer(jsessionid);

			final List<MagicCard> allCardsInBattlefieldOnMySide = this.persistenceService
					.getAllCardsInBattleFieldForAPlayer(playerId);

			for (final MagicCard mc : allCardsInBattlefieldOnMySide)
			{
				message.append("_____").append(mc.getUuid().toString());
				mc.setTapped(false);
				this.persistenceService.saveCard(mc);
			}
			message.append("&playerId=").append(playerId.toString());

			final Meteor meteor = Meteor.build(request, new LinkedList<BroadcastFilter>(), null);
			meteor.addListener((AtmosphereResourceEventListener)target.getPage());
			meteor.broadcast(message);
			UntapAllBehavior.logger.info("message: " + message);
		}
		else
		{
			UntapAllBehavior.logger.info("respond else");
			final String allCards = request.getParameter("cards");
			final String[] cards = allCards.split("_____");
			UntapAllBehavior.logger.info("cards.length: " + cards.length);
			for (final String cardUuid : cards)
			{
				target.appendJavascript("jQuery('#card" + cardUuid + "').rotate(0);");
			}
		}
	}

	@Override
	public void renderHead(final IHeaderResponse response)
	{
		super.renderHead(response);

		final HashMap<String, Object> variables = new HashMap<String, Object>();
		variables.put("url", this.getCallbackUrl());

		final TextTemplate template1 = new PackagedTextTemplate(HomePage.class,
				"script/untapAll/untapAll.js");
		template1.interpolate(variables);

		response.renderJavascript(template1.asString(), null);
	}

	@Required
	public void setPersistenceService(final PersistenceService _persistenceService)
	{
		this.persistenceService = _persistenceService;
	}

}
