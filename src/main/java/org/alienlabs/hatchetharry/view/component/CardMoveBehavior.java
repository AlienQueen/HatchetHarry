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

@SuppressWarnings("serial")
public class CardMoveBehavior extends AbstractDefaultAjaxBehavior
{
	private static final long serialVersionUID = 1L;

	private static final Logger logger = LoggerFactory.getLogger(CardMoveBehavior.class);
	private final CardPanel panel;
	private final UUID uuid;

	@SpringBean
	private transient PersistenceService persistenceService;

	public CardMoveBehavior(final CardPanel cp, final UUID _uuid)
	{
		this.panel = cp;
		this.uuid = _uuid;
		InjectorHolder.getInjector().inject(this);
	}

	@Override
	protected void respond(final AjaxRequestTarget target)
	{
		CardMoveBehavior.logger.info("respond");
		final ServletWebRequest servletWebRequest = (ServletWebRequest)this.panel.getRequest();
		final HttpServletRequest request = servletWebRequest.getHttpServletRequest();

		final String _mouseX = request.getParameter("posX");
		final String _mouseY = request.getParameter("posY");
		final String uniqueid = request.getParameter("uuid");
		final String message = request.getRequestedSessionId() + "&&&"
				+ (Integer.parseInt(_mouseX) - 16) + "&&&" + (Integer.parseInt(_mouseY) - 16)
				+ "&&&" + uniqueid;

		try
		{
			final MagicCard mc = this.persistenceService.getCardFromUuid(UUID.fromString(uniqueid));
			if (null != mc)
			{
				mc.setX(Long.parseLong(_mouseX) - 16);
				mc.setY(Long.parseLong(_mouseY) - 16);
				this.persistenceService.saveCard(mc);
			}
		}
		catch (final IllegalArgumentException e)
		{
			CardMoveBehavior.logger.error("error parsing UUID of moved card", e);
		}

		final Meteor meteor = Meteor.build(request, new LinkedList<BroadcastFilter>(), null);
		CardMoveBehavior.logger.info("meteor: " + meteor);
		CardMoveBehavior.logger.info(message);
		meteor.addListener((AtmosphereResourceEventListener)this.panel.getPage());
		meteor.broadcast(message);
	}

	@Override
	public void renderHead(final IHeaderResponse response)
	{
		super.renderHead(response);

		final TextTemplate template1 = new PackagedTextTemplate(HomePage.class,
				"script/draggableHandle/jquery.ui.core.js");
		StringBuffer js = new StringBuffer().append(template1.asString());

		final TextTemplate template2 = new PackagedTextTemplate(HomePage.class,
				"script/draggableHandle/jquery.ui.widget.js");
		js = js.append("\n" + template2.asString());

		final TextTemplate template3 = new PackagedTextTemplate(HomePage.class,
				"script/draggableHandle/jquery.ui.mouse.js");
		js = js.append("\n" + template3.asString());

		final HashMap<String, Object> variables = new HashMap<String, Object>();
		variables.put("url", this.getCallbackUrl());
		variables.put("uuid", this.uuid);
		variables.put("uuidValidForJs", this.uuid.toString().replace("-", "_"));

		final TextTemplate template4 = new PackagedTextTemplate(HomePage.class,
				"script/draggableHandle/jquery.ui.draggable.js");
		template4.interpolate(variables);
		js = js.append("\n" + template4.asString());

		final TextTemplate template5 = new PackagedTextTemplate(HomePage.class,
				"script/draggableHandle/cardMove.js");
		template5.interpolate(variables);
		js = js.append("\n" + template5.asString());

		final TextTemplate template6 = new PackagedTextTemplate(HomePage.class,
				"script/draggableHandle/initDrag.js");
		template6.interpolate(variables);
		js = js.append("\n" + template6.asString());

		response.renderOnDomReadyJavascript(js.toString());
	}

	@Required
	public void setPersistenceService(final PersistenceService _persistenceService)
	{
		this.persistenceService = _persistenceService;
	}

}
