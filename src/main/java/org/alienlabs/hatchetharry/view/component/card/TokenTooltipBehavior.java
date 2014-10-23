package org.alienlabs.hatchetharry.view.component.card;

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

public class TokenTooltipBehavior extends AbstractDefaultAjaxBehavior
{
	private static final long serialVersionUID = 1L;

	private static final Logger LOGGER = LoggerFactory.getLogger(TokenTooltipBehavior.class);

	private final String uuid;

	public TokenTooltipBehavior(final String _uuid)
	{
		this.uuid = _uuid;
	}

	@Override
	public void renderHead(final Component component, final IHeaderResponse response)
	{
		super.renderHead(component, response);

		StringBuilder js = new StringBuilder();

		final HashMap<String, Object> variables = new HashMap<String, Object>();
		variables.put("url", this.getCallbackUrl());
		variables.put("uuidValidForJs", this.uuid.replace("-", "_"));

		final TextTemplate template1 = new PackageTextTemplate(HomePage.class,
				"script/draggableHandle/cardTooltip.js");
		template1.interpolate(variables);
		js = js.append("\n" + template1.asString());

		response.render(JavaScriptHeaderItem.forScript(js.toString(), null));
		try
		{
			template1.close();
		}
		catch (final IOException e)
		{
			TokenTooltipBehavior.LOGGER.error(
					"unable to close template1 in CardTooltipBehavior#renderHead()!", e);
		}
	}

	@Override
	protected void respond(final AjaxRequestTarget target)
	{
	}

}
