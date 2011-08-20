package org.alienlabs.hatchetharry.view;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.protocol.http.servlet.ServletWebRequest;
import org.apache.wicket.util.template.PackagedTextTemplate;
import org.apache.wicket.util.template.TextTemplate;
import org.atmosphere.cpr.AtmosphereResourceEventListener;
import org.atmosphere.cpr.BroadcastFilter;
import org.atmosphere.cpr.Meteor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("serial")
public class PlayCardFromHandBehavior extends AbstractDefaultAjaxBehavior
{
	static final Logger logger = LoggerFactory.getLogger(PlayCardFromHandBehavior.class);
	private final Panel panel = null;
	private final UUID uuid;

	public PlayCardFromHandBehavior(final UUID _uuid)
	{
		super();
		this.uuid = _uuid;
	}

	@Override
	protected void respond(final AjaxRequestTarget target)
	{
		PlayCardFromHandBehavior.logger.info("respond");
		final ServletWebRequest servletWebRequest = (ServletWebRequest)this.panel.getRequest();
		final HttpServletRequest request = servletWebRequest.getHttpServletRequest();

		final String tapped = request.getParameter("tapped");
		final String message = request.getRequestedSessionId() + "&tapped=" + tapped;

		final Meteor meteor = Meteor.build(request, new LinkedList<BroadcastFilter>(), null);
		PlayCardFromHandBehavior.logger.info("meteor: " + meteor);
		PlayCardFromHandBehavior.logger.info(message);
		meteor.addListener((AtmosphereResourceEventListener)this.panel.getPage());
		meteor.broadcast(message);
	}

	@Override
	public void renderHead(final IHeaderResponse response)
	{
		super.renderHead(response);

		final HashMap<String, Object> variables = new HashMap<String, Object>();
		variables.put("url", this.getCallbackUrl());
		variables.put("uuid", this.getUuid());
		final TextTemplate template = new PackagedTextTemplate(PlayCardFromHandBehavior.class,
				"scripts/playCard/playCard.js");
		template.interpolate(variables);

		response.renderOnDomReadyJavascript(template.asString());
	}

	public UUID getUuid()
	{
		return this.uuid;
	}

}
