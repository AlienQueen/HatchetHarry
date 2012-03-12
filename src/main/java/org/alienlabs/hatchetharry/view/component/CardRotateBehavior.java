package org.alienlabs.hatchetharry.view.component;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.UUID;

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

public class CardRotateBehavior extends AbstractDefaultAjaxBehavior
{
	private static final long serialVersionUID = -9164073767944851883L;

	private static final Logger LOGGER = LoggerFactory.getLogger(CardRotateBehavior.class);
	private final CardPanel panel;
	private final UUID uuid;

	@SpringBean
	private transient PersistenceService persistenceService;

	public CardRotateBehavior(final CardPanel cp, final UUID _uuid)
	{
		this.panel = cp;
		this.uuid = _uuid;
		InjectorHolder.getInjector().inject(this);
	}

	@Override
	protected void respond(final AjaxRequestTarget target)
	{
		CardRotateBehavior.LOGGER.info("respond");
		final ServletWebRequest servletWebRequest = (ServletWebRequest)this.panel.getRequest();
		final HttpServletRequest request = servletWebRequest.getHttpServletRequest();

		final String uuidToLookFor = request.getParameter("uuid");

		final MagicCard card = this.persistenceService.getCardFromUuid(UUID
				.fromString(uuidToLookFor));
		card.setTapped(!card.isTapped());
		this.persistenceService.saveCard(card);

		final String message = request.getRequestedSessionId() + "&tapped=" + card.isTapped()
				+ "___" + uuidToLookFor;

		final Meteor meteor = Meteor.build(request, new LinkedList<BroadcastFilter>(), null);
		CardRotateBehavior.LOGGER.info("meteor: " + meteor);
		CardRotateBehavior.LOGGER.info(message);
		meteor.addListener((AtmosphereResourceEventListener)this.panel.getPage());
		meteor.broadcast(message);
	}

	@Override
	public void renderHead(final IHeaderResponse response)
	{
		super.renderHead(response);

		final HashMap<String, Object> variables = new HashMap<String, Object>();
		variables.put("url", this.getCallbackUrl());
		variables.put("uuid", this.uuid);
		variables.put("uuidValidForJs", this.uuid.toString().replace("-", "_"));

		final TextTemplate template = new PackagedTextTemplate(HomePage.class,
				"script/rotate/cardRotate.js");
		template.interpolate(variables);

		response.renderOnDomReadyJavascript(template.asString());
	}

	@Required
	public void setPersistenceService(final PersistenceService _persistenceService)
	{
		this.persistenceService = _persistenceService;
	}

}
