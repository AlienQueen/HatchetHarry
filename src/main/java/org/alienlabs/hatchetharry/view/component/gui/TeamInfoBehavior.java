package org.alienlabs.hatchetharry.view.component.gui;

import java.io.IOException;
import java.util.HashMap;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.util.template.PackageTextTemplate;
import org.apache.wicket.util.template.TextTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TeamInfoBehavior extends AbstractDefaultAjaxBehavior
{
	private static final long serialVersionUID = 1L;

	private static final Logger LOGGER = LoggerFactory.getLogger(TeamInfoBehavior.class);
	private final ModalWindow modal;

	private TeamInfoBehavior(final ModalWindow _modal)
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

		final HashMap<String, Object> variables = new HashMap<>();
		variables.put("url_for_team_info", this.getCallbackUrl());
		final TextTemplate template = new PackageTextTemplate(TeamInfoBehavior.class,
				"script/menubar/menubar.js");
		template.interpolate(variables);
		final String js1 = template.asString();
		response.render(JavaScriptHeaderItem.forScript(js1, null));
		try
		{
			template.close();
		}
		catch (final IOException e)
		{
			TeamInfoBehavior.LOGGER.error(
					"unable to close template in TeamInfoBehavior#renderHead()!", e);
		}
	}

}
