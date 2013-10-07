package org.alienlabs.hatchetharry.view.component;

import org.alienlabs.hatchetharry.model.Token;
import org.alienlabs.hatchetharry.service.PersistenceService;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

public class TokenTooltipPanel extends Panel
{
	private static final long serialVersionUID = 1L;
	static final Logger LOGGER = LoggerFactory.getLogger(TokenTooltipPanel.class);

	@SpringBean
	PersistenceService persistenceService;

	public TokenTooltipPanel(final String id, final Token token)
	{
		super(id);
		final String ownerSide = this.persistenceService.getPlayer(token.getPlayer().getId())
				.getSide();

		final AjaxLink<Void> closeTooltip = new AjaxLink<Void>("closeTooltip")
		{
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(final AjaxRequestTarget target)
			{
				target.appendJavaScript("jQuery('.cardTooltip').attr('style', 'display: none;'); ");
			}
		};

		if ("infrared".equals(ownerSide))
		{
			this.add(new AttributeModifier("style", "border: 1px solid red;"));
		}
		else if ("ultraviolet".equals(ownerSide))
		{
			this.add(new AttributeModifier("style", "border: 1px solid purple;"));
		}
		else
		{
			this.add(new AttributeModifier("style", "border: 1px solid yellow;"));
		}
		this.add(closeTooltip);

		this.add(new Label("type", token.getType()).setOutputMarkupId(true));
		this.add(new Label("power", token.getPower()).setOutputMarkupId(true));
		this.add(new Label("thoughness", token.getThoughness()).setOutputMarkupId(true));
		this.add(new Label("colors", token.getColors()).setOutputMarkupId(true));
		this.add(new Label("capabilities", token.getCapabilities()).setOutputMarkupId(true));
		this.add(new Label("creatureTypes", token.getCreatureTypes()).setOutputMarkupId(true));
		this.add(new Label("description", token.getDescription()).setOutputMarkupId(true));

	}

	@Required
	public void setPersistenceService(final PersistenceService _persistenceService)
	{
		this.persistenceService = _persistenceService;
	}

}
