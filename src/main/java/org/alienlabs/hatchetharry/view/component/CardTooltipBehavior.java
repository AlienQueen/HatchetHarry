package org.alienlabs.hatchetharry.view.component;

import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

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

public class CardTooltipBehavior extends AbstractDefaultAjaxBehavior
{
	private static final long serialVersionUID = 1L;

	private static final Logger LOGGER = LoggerFactory.getLogger(CardTooltipBehavior.class);

	private final UUID uuid;

	public CardTooltipBehavior(final UUID _uuid)
	{
		this.uuid = _uuid;
	}

	@Override
	public void renderHead(final Component component, final IHeaderResponse response)
	{
		super.renderHead(component, response);

		final HashMap<String, String> variables = new HashMap<String, String>();
		variables.put("url", this.getCallbackUrl().toString());
		String uuidValidForJs = this.uuid.toString().replace("-", "_");
		variables.put("uuidValidForJs", uuidValidForJs);

		final TextTemplate template = new PackageTextTemplate(HomePage.class,
			"script/draggableHandle/cardTooltip.js");
		template.interpolate(variables);

		response.render(JavaScriptHeaderItem.forScript(template.asString(), "cardTooltipScript"
			+ uuidValidForJs));
		try
		{
			template.close();
		}
		catch (final IOException e)
		{
			CardTooltipBehavior.LOGGER.error(
				"unable to close template in CardTooltipBehavior#renderHead()!", e);
		}
	}

	@Override
	protected void respond(final AjaxRequestTarget target)
	{
	}

}
