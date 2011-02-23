package org.alienlabs.hatchetharry.view;

import java.util.HashMap;
import java.util.LinkedList;

import javax.servlet.http.HttpServletRequest;

import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.protocol.http.servlet.ServletWebRequest;
import org.apache.wicket.util.template.PackagedTextTemplate;
import org.apache.wicket.util.template.TextTemplate;
import org.atmosphere.cpr.AtmosphereResourceEventListener;
import org.atmosphere.cpr.BroadcastFilter;
import org.atmosphere.cpr.Meteor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("serial")
public class CardMoveBehavior extends AbstractDefaultAjaxBehavior
{
	static final Logger logger = LoggerFactory.getLogger(CardMoveBehavior.class);
	private final CardPanel panel;

	public CardMoveBehavior(final CardPanel cp, final Form<String> _form)
	{
		this.panel = cp;
	}

	@Override
	protected void respond(final AjaxRequestTarget target)
	{
		CardMoveBehavior.logger.info("respond");
		final ServletWebRequest servletWebRequest = (ServletWebRequest)this.panel.getRequest();
		final HttpServletRequest request = servletWebRequest.getHttpServletRequest();

		final String _mouseX = request.getParameter("posX");
		final String _mouseY = request.getParameter("posY");
		final String message = request.getRequestedSessionId() + "$$$"
				+ (Integer.parseInt(_mouseX) - 16) + "$$$" + (Integer.parseInt(_mouseY) - 16);

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

		final TextTemplate template1 = new PackagedTextTemplate(CardPanel.class,
				"scripts/draggableHandle/jquery.ui.core.js");
		StringBuffer js = new StringBuffer().append(template1.asString());

		final TextTemplate template2 = new PackagedTextTemplate(CardPanel.class,
				"scripts/draggableHandle/jquery.ui.widget.js");
		js = js.append("\n" + template2.asString());

		final TextTemplate template3 = new PackagedTextTemplate(CardPanel.class,
				"scripts/draggableHandle/jquery.ui.mouse.js");
		js = js.append("\n" + template3.asString());

		final HashMap<String, Object> variables = new HashMap<String, Object>();
		variables.put("url", this.getCallbackUrl());
		final TextTemplate template4 = new PackagedTextTemplate(CardPanel.class,
				"scripts/draggableHandle/jquery.ui.draggable.js");
		template4.interpolate(variables);
		js = js.append("\n" + template4.asString());

		final TextTemplate template5 = new PackagedTextTemplate(CardPanel.class,
				"scripts/draggableHandle/cardMove.js");
		js = js.append("\n" + template5.asString());

		final TextTemplate template6 = new PackagedTextTemplate(CardPanel.class,
				"scripts/draggableHandle/initDrag.js");
		js = js.append("\n" + template6.asString());

		response.renderOnDomReadyJavascript(js.toString());
	}

}
