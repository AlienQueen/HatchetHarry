package org.alienlabs.hatchetharry.view.component;

import java.io.IOException;
import java.util.HashMap;

import org.alienlabs.hatchetharry.view.page.HomePage;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.util.template.PackageTextTemplate;
import org.apache.wicket.util.template.TextTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ZoneMoveBehavior extends AbstractDefaultAjaxBehavior
{
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = LoggerFactory.getLogger(ZoneMoveBehavior.class);
	private final Component zone;

	public ZoneMoveBehavior(final Component _zone)
	{
		this.zone = _zone;
	}

	@Override
	protected void respond(final AjaxRequestTarget target)
	{
	}

	@Override
	public void renderHead(final Component component, final IHeaderResponse response)
	{
		super.renderHead(component, response);

		final HashMap<String, Object> variables = new HashMap<String, Object>();
		variables.put("component", this.zone.getMarkupId());
		variables.put("handle", "handle" + this.zone.getMarkupId());

		final TextTemplate template = new PackageTextTemplate(HomePage.class,
				"script/draggableHandle/zoneMove.js");
		template.interpolate(variables);

		response.render(JavaScriptHeaderItem.forScript(template.asString(), "zoneMoveBehavior"));
		try
		{
			template.close();
		}
		catch (final IOException e)
		{
			ZoneMoveBehavior.LOGGER.error(
					"unable to close template in ZoneMoveBehavior#renderHead()!", e);
		}
	}

}
