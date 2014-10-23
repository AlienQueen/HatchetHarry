package org.alienlabs.hatchetharry.view.component.card;

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

@edu.umd.cs.findbugs.annotations.SuppressWarnings(value = { "SE_INNER_CLASS",
		"SIC_INNER_SHOULD_BE_STATIC_ANON" }, justification = "In Wicket, serializable inner classes are common. And as the parent Page is serialized as well, this is no concern. This is no bad practice in Wicket")
public class TokenTooltipPanel extends Panel
{
	static final Logger LOGGER = LoggerFactory.getLogger(TokenTooltipPanel.class);
	private static final long serialVersionUID = 1L;
	@SpringBean
	PersistenceService persistenceService;

	public TokenTooltipPanel(final String id, final Token token)
	{
		super(id);
		final String ownerSide = this.persistenceService.getPlayer(token.getPlayer().getId())
			.getSide().getSideName();

		final TokenTooltipBehavior ttb = new TokenTooltipBehavior(token.getUuid());
		this.add(ttb);

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
		this.add(new Label("toughness", token.getToughness()).setOutputMarkupId(true));
		this.add(new Label("colors", token.getColors()).setOutputMarkupId(true));
		this.add(new Label("capabilities", token.getCapabilities()).setOutputMarkupId(true));
		this.add(new Label("creatureTypes", token.getCreatureTypes()).setOutputMarkupId(true));
		this.add(new Label("description", token.getDescription()).setOutputMarkupId(true));

		final CounterTooltip counterPanel = new CounterTooltip("counterPanel", null, token);
		this.add(counterPanel);
	}

	@Required
	public void setPersistenceService(final PersistenceService _persistenceService)
	{
		this.persistenceService = _persistenceService;
	}

}
