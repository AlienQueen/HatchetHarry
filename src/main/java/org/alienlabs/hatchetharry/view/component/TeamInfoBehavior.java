package org.alienlabs.hatchetharry.view.component;

import java.util.HashMap;

import org.alienlabs.hatchetharry.view.page.HomePage;
import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.util.template.PackagedTextTemplate;
import org.apache.wicket.util.template.TextTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("serial")
public class TeamInfoBehavior extends AbstractDefaultAjaxBehavior
{
	static final Logger logger = LoggerFactory.getLogger(TeamInfoBehavior.class);
	ModalWindow modal;

	public TeamInfoBehavior(final HomePage _hp, final ModalWindow _modal)
	{
		this.modal = _modal;
	}

	@Override
	protected void respond(final AjaxRequestTarget target)
	{
		if (target != null)
		{
			TeamInfoBehavior.logger.info("respond TeamInfoBehavior");
			this.modal.show(target);
		}
	}

	@Override
	public void renderHead(final IHeaderResponse response)
	{
		super.renderHead(response);

		final HashMap<String, Object> variables = new HashMap<String, Object>();
		variables.put("url_for_team_info", this.getCallbackUrl());
		final TextTemplate template1 = new PackagedTextTemplate(TeamInfoBehavior.class,
				"script/menubar/menubar.js");
		template1.interpolate(variables);
		final String js1 = template1.asString();
		response.renderJavascript(js1, "menubar.js");

		final TextTemplate template2 = new PackagedTextTemplate(TeamInfoBehavior.class,
				"script/qunitTests/HomePageTests.js");
		final String js2 = template2.asString();
		response.renderJavascript(js2, "codeUnderTest.js");

		final TextTemplate template3 = new PackagedTextTemplate(TeamInfoBehavior.class,
				"script/qunitTests/HomePageTests.js");
		final String js3 = template3.asString();
		response.renderJavascript(js3, "HomePageTests.js");
	}

}