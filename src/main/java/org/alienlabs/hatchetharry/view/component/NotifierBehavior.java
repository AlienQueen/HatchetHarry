package org.alienlabs.hatchetharry.view.component;

import java.util.HashMap;
import java.util.LinkedList;

import javax.servlet.http.HttpServletRequest;

import org.alienlabs.hatchetharry.view.page.HomePage;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.protocol.http.servlet.ServletWebRequest;
import org.apache.wicket.util.template.PackageTextTemplate;
import org.apache.wicket.util.template.TextTemplate;
import org.atmosphere.cpr.BroadcastFilter;
import org.atmosphere.cpr.Meteor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NotifierBehavior extends AbstractDefaultAjaxBehavior
{
	private static final long serialVersionUID = -1301311949498369085L;

	static final Logger LOGGER = LoggerFactory.getLogger(NotifierBehavior.class);
	private final HomePage page;
	private final String title;
	private final String text;
	final WebMarkupContainer dataBoxParent;
	final Long gameId;

	public NotifierBehavior(final HomePage _page, final String _title, final String _text,
			final WebMarkupContainer _dataBoxParent, final Long _gameId)
	{
		this.page = _page;
		this.title = _title;
		this.text = _text;
		this.dataBoxParent = _dataBoxParent;
		this.gameId = _gameId;
	}

	@Override
	protected void respond(final AjaxRequestTarget target)
	{
		final ServletWebRequest servletWebRequest = (ServletWebRequest)this.page.getRequest();
		final HttpServletRequest request = servletWebRequest.getContainerRequest();

		NotifierBehavior.LOGGER.info("respond to: " + request.getQueryString());

		final String _title = request.getParameter("title");
		final String _text = request.getParameter("text");
		final String _show = request.getParameter("show");
		request.getParameter("updateDataBox");
		final boolean show = ("true".equals(_show));

		final String jsessionid = request.getParameter("jsessionid");

		final boolean isSameSessionId = (request.getRequestedSessionId().equals(jsessionid));

		if (isSameSessionId)
		{
			final String message = _title + ":::" + _text + ":::" + request.getRequestedSessionId();
			final Meteor meteor = Meteor.build(request, new LinkedList<BroadcastFilter>(), null);
			NotifierBehavior.LOGGER.info("meteor: " + meteor);
			NotifierBehavior.LOGGER.info(message);
			NotifierBehavior.LOGGER.info("there?");

			// meteor.addListener(this.page);
			// meteor.broadcast(message);
		}
		else if (show)
		{
			final String message = _title + ":::" + _text + ":::" + request.getRequestedSessionId()
					+ ":::show";
			final Meteor meteor = Meteor.build(request, new LinkedList<BroadcastFilter>(), null);
			NotifierBehavior.LOGGER.info("meteor: " + meteor);
			NotifierBehavior.LOGGER.info(message);
			// meteor.addListener(this.page);
			// meteor.broadcast(message);
		}
	}

	@Override
	public void renderHead(final Component component, final IHeaderResponse response)
	{
		super.renderHead(component, response);
		final int before = this.getCallbackUrl().toString().indexOf("&jsessionid=");
		final int after = this.getCallbackUrl().toString().indexOf("&random=");
		boolean isSameSessionId = false;
		NotifierBehavior.LOGGER.info("render head?");

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

			final TextTemplate template = new PackageTextTemplate(HomePage.class,
					"script/notifier/notifier.js");
			template.interpolate(variables);

			response.render(JavaScriptHeaderItem.forScript(template.asString(), "notifierBehavior"));
			NotifierBehavior.LOGGER.info("yes!");
		}
		else
		{
			NotifierBehavior.LOGGER.info("No!");
		}
	}

}
