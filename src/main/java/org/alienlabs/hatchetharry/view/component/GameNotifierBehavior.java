package org.alienlabs.hatchetharry.view.component;

import java.util.LinkedList;

import javax.servlet.http.HttpServletRequest;

import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.protocol.http.servlet.ServletWebRequest;
import org.atmosphere.cpr.BroadcastFilter;
import org.atmosphere.cpr.Meteor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("serial")
public class GameNotifierBehavior extends AbstractDefaultAjaxBehavior
{
	static final Logger logger = LoggerFactory.getLogger(GameNotifierBehavior.class);
	private final WebPage page;

	public GameNotifierBehavior(final WebPage _p)
	{
		this.page = _p;
	}

	@Override
	protected void respond(final AjaxRequestTarget target)
	{
		final ServletWebRequest servletWebRequest = (ServletWebRequest)this.page.getRequest();
		final HttpServletRequest request = servletWebRequest.getHttpServletRequest();

		GameNotifierBehavior.logger.info("respond to: " + request.getQueryString());

		final String title = request.getParameter("title");
		final String text = request.getParameter("text");
		final String stop = request.getParameter("stop");

		if ((!"true".equals(stop)) && (null != text))
		{
			final String message = ("1".equals(title)
					? "You've created a game"
					: "You have requested to join a game")
					+ "§§§"
					+ ("1".equals(text)
							? "As soon as a player is connected, you'll be able to play."
							: "You can start right now!") + "§§§" + request.getRequestedSessionId();
			final Meteor meteor = Meteor.build(request, new LinkedList<BroadcastFilter>(), null);
			GameNotifierBehavior.logger.info("meteor: " + meteor);
			GameNotifierBehavior.logger.info(message);
			// meteor.addListener((AtmosphereResourceEventListener)this.page);
			meteor.broadcast(message);
		}
	}

	@Override
	public void renderHead(final IHeaderResponse response)
	{
		super.renderHead(response);
	}

}
