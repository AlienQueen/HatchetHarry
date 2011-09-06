package org.alienlabs.hatchetharry.view.component;

import java.util.HashMap;
import java.util.LinkedList;

import javax.servlet.http.HttpServletRequest;

import org.alienlabs.hatchetharry.view.page.HomePage;
import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.protocol.http.servlet.ServletWebRequest;
import org.apache.wicket.util.template.PackagedTextTemplate;
import org.apache.wicket.util.template.TextTemplate;
import org.atmosphere.cpr.AtmosphereResourceEventListener;
import org.atmosphere.cpr.BroadcastFilter;
import org.atmosphere.cpr.Meteor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NotifierBehavior extends AbstractDefaultAjaxBehavior
{
	private static final long serialVersionUID = -1301311949498369085L;

	static final Logger logger = LoggerFactory.getLogger(NotifierBehavior.class);
	private final WebPage page;
	private final String title;
	private final String text;

	public NotifierBehavior(final WebPage _page, final String _title, final String _text)
	{
		this.page = _page;
		this.title = _title;
		this.text = _text;
	}

	@Override
	protected void respond(final AjaxRequestTarget target)
	{
		final ServletWebRequest servletWebRequest = (ServletWebRequest)this.page.getRequest();
		final HttpServletRequest request = servletWebRequest.getHttpServletRequest();

		NotifierBehavior.logger.info("respond to: " + request.getQueryString());

		final String _title = request.getParameter("title");
		final String _text = request.getParameter("text");
		final String _show = request.getParameter("show");
		final boolean show = ("true".equals(_show));

		final String jsessionid = request.getParameter("jsessionid");

		final boolean isSameSessionId = (request.getRequestedSessionId().equals(jsessionid));

		if (isSameSessionId)
		{
			final String message = _title + ":::" + _text + ":::" + request.getRequestedSessionId();
			final Meteor meteor = Meteor.build(request, new LinkedList<BroadcastFilter>(), null);
			NotifierBehavior.logger.info("meteor: " + meteor);
			NotifierBehavior.logger.info(message);
			meteor.addListener((AtmosphereResourceEventListener)this.page);
			meteor.broadcast(message);
		}
		else if (show)
		{
			final String message = _title + ":::" + _text + ":::" + request.getRequestedSessionId()
					+ ":::show";
			final Meteor meteor = Meteor.build(request, new LinkedList<BroadcastFilter>(), null);
			NotifierBehavior.logger.info("meteor: " + meteor);
			NotifierBehavior.logger.info(message);
			meteor.addListener((AtmosphereResourceEventListener)this.page);
			meteor.broadcast(message);
		}
	}

	@Override
	public void renderHead(final IHeaderResponse response)
	{
		super.renderHead(response);
		final int before = this.getCallbackUrl().toString().indexOf("&jsessionid=");
		final int after = this.getCallbackUrl().toString().indexOf("&random=");
		boolean isSameSessionId = false;
		NotifierBehavior.logger.info("render head?");

		if ((before != -1) && (after != -1))
		{
			isSameSessionId = (this.page.getSession().getId().equals(this.getCallbackUrl()
					.subSequence(before, after)));
		}

		if (!isSameSessionId)
		{
			final String url = this.getCallbackUrl().toString();

			final HashMap<String, Object> variables = new HashMap<String, Object>();
			variables.put("url", url);
			variables.put("title", this.title);
			variables.put("text", this.text);

			final TextTemplate template = new PackagedTextTemplate(HomePage.class,
					"script/notifier/notifier.js");
			template.interpolate(variables);

			response.renderOnDomReadyJavascript(template.asString());
			NotifierBehavior.logger.info("yes!");
		}
		else
		{
			NotifierBehavior.logger.info("No!");
		}
	}

}
