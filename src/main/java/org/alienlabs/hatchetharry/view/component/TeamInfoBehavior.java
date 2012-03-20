package org.alienlabs.hatchetharry.view.component;

import java.util.HashMap;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.util.template.PackageTextTemplate;
import org.apache.wicket.util.template.TextTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("serial")
public class TeamInfoBehavior extends AbstractDefaultAjaxBehavior
{
	static final Logger LOGGER = LoggerFactory.getLogger(TeamInfoBehavior.class);
	ModalWindow modal;

	public TeamInfoBehavior(final ModalWindow _modal)
	{
		this.modal = _modal;
	}

	@Override
	protected void respond(final AjaxRequestTarget target)
	{
		if (target != null)
		{
			TeamInfoBehavior.LOGGER.info("respond TeamInfoBehavior");
			this.modal.show(target);
		}
	}

	@Override
	public void renderHead(final Component component, final IHeaderResponse response)
	{
		super.renderHead(component, response);

		final HashMap<String, Object> variables = new HashMap<String, Object>();
		variables.put("url_for_team_info", this.getCallbackUrl());
		final TextTemplate template1 = new PackageTextTemplate(TeamInfoBehavior.class,
				"script/menubar/menubar.js");
		template1.interpolate(variables);
		final String js1 = template1.asString();
		response.renderJavaScript(js1, "menubar.js");
	}

}