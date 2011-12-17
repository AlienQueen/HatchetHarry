package org.alienlabs.hatchetharry.view.component;

import java.util.HashMap;
import java.util.LinkedList;

import javax.servlet.http.HttpServletRequest;

import org.alienlabs.hatchetharry.view.page.HomePage;
import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.protocol.http.servlet.ServletWebRequest;
import org.apache.wicket.util.template.PackagedTextTemplate;
import org.apache.wicket.util.template.TextTemplate;
import org.atmosphere.cpr.BroadcastFilter;
import org.atmosphere.cpr.Meteor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UpdateDataBoxBehavior extends AbstractDefaultAjaxBehavior
{
	private static final long serialVersionUID = -1192901847359306923L;

	static final Logger logger = LoggerFactory.getLogger(UpdateDataBoxBehavior.class);

	final WebMarkupContainer dataBoxParent;

	final Long gameId;

	private final HomePage hp;

	public UpdateDataBoxBehavior(final WebMarkupContainer _dataBoxParent, final Long _gameId,
			final HomePage _hp)
	{
		this.dataBoxParent = _dataBoxParent;
		this.gameId = _gameId;
		this.hp = _hp;
	}

	@Override
	protected void respond(final AjaxRequestTarget target)
	{
		UpdateDataBoxBehavior.logger.info("respond");
		final ServletWebRequest servletWebRequest = (ServletWebRequest)target.getPage()
				.getRequest();
		final HttpServletRequest request = servletWebRequest.getHttpServletRequest();
		final String stop = request.getParameter("stop");
		final String jsessionid = request.getParameter("jsessionid");
		request.getParameter("notify");

		final DataBox dataBox = new DataBox("dataBox", this.gameId, this.dataBoxParent, this.hp);
		// HatchetHarrySession.get().getDataBox().remove(this);
		// this.detach(HatchetHarrySession.get().getDataBox());
		// dataBox.add(this);
		// HatchetHarrySession.get().setDataBox(dataBox);

		this.dataBoxParent.addOrReplace(dataBox);
		target.addComponent(this.dataBoxParent);

		if (this.hp.getSession().getId().equals(jsessionid))
		{
			UpdateDataBoxBehavior.logger.info("notify with jsessionid="
					+ this.hp.getSession().getId());
			target.appendJavascript("wicketAjaxGet('" + this.hp.notifierPanel.getCallbackUrl()
					+ "&title=A player joined in!&text=Ready to play?&jsessionid=" + jsessionid
					+ "', function() { }, null, null);");
		}

		if (!"true".equals(stop))
		{

			final String message = "+++++" + request.getRequestedSessionId();

			final Meteor meteor = Meteor.build(request, new LinkedList<BroadcastFilter>(), null);
			UpdateDataBoxBehavior.logger.info("meteor: " + meteor);
			UpdateDataBoxBehavior.logger.info(message);
			// meteor.addListener((AtmosphereResourceEventListener)target.getPage());
			// meteor.broadcast(message);
		}
	}

	@Override
	public void renderHead(final IHeaderResponse response)
	{
		super.renderHead(response);

		final HashMap<String, Object> variables = new HashMap<String, Object>();
		variables.put("url", this.getCallbackUrl());
		variables.put("jsessionid", this.getComponent().getPage().getSession().getId());

		final TextTemplate template = new PackagedTextTemplate(HomePage.class,
				"script/databox/updateDataBox.js");
		template.interpolate(variables);

		response.renderOnDomReadyJavascript(template.asString());
	}

	public String getUrl()
	{
		return this.getCallbackUrl().toString();
	}

}
