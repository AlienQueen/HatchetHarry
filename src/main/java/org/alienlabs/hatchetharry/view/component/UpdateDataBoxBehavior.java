package org.alienlabs.hatchetharry.view.component;

import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

import org.alienlabs.hatchetharry.HatchetHarrySession;
import org.alienlabs.hatchetharry.view.page.HomePage;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.protocol.http.servlet.ServletWebRequest;
import org.apache.wicket.util.template.PackageTextTemplate;
import org.apache.wicket.util.template.TextTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UpdateDataBoxBehavior extends AbstractDefaultAjaxBehavior
{
	private static final long serialVersionUID = -1192901847359306923L;

	static final Logger LOGGER = LoggerFactory.getLogger(UpdateDataBoxBehavior.class);

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
		UpdateDataBoxBehavior.LOGGER.info("respond");
		final ServletWebRequest servletWebRequest = (ServletWebRequest)target.getPage()
				.getRequest();
		final HttpServletRequest request = servletWebRequest.getContainerRequest();
		final String jsessionid = request.getParameter("jsessionid");
		final String displayJoinMessage = request.getParameter("displayJoinMessage");

		if (this.hp.getSession().getId().equals(jsessionid) && ("true".equals(displayJoinMessage)))
		{
			UpdateDataBoxBehavior.LOGGER.info("notify with jsessionid="
					+ this.hp.getSession().getId());
			target.appendJavaScript("wicketAjaxGet('" + this.hp.notifierPanel.getCallbackUrl()
					+ "&title=A player joined in!&text=Ready to play?&jsessionid=" + jsessionid
					+ "', function() { }, null, null);");
		}

		final DataBox dataBox = new DataBox("dataBox", this.gameId, this.hp);
		dataBox.setOutputMarkupId(true);
		this.dataBoxParent.setOutputMarkupId(true);
		dataBox.add(new UpdateDataBoxBehavior(this.dataBoxParent, this.gameId, this.hp));
		this.dataBoxParent.addOrReplace(dataBox);
		target.addComponent(this.dataBoxParent, "dataBoxParent"
				+ HatchetHarrySession.get().getPlayerLetter());
	}

	@Override
	public void renderHead(final Component component, final IHeaderResponse response)
	{
		super.renderHead(component, response);

		final HashMap<String, Object> variables = new HashMap<String, Object>();
		variables.put("url", this.getCallbackUrl());
		variables.put("jsessionid", this.getComponent().getPage().getSession().getId());

		final TextTemplate template = new PackageTextTemplate(HomePage.class,
				"script/databox/updateDataBox.js");
		template.interpolate(variables);

		response.renderOnDomReadyJavaScript(template.asString());
	}

	public String getUrl()
	{
		return this.getCallbackUrl().toString();
	}

}
