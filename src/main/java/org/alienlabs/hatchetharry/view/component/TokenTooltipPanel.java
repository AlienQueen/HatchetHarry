package org.alienlabs.hatchetharry.view.component;

import java.util.UUID;

import org.alienlabs.hatchetharry.model.Token;
import org.alienlabs.hatchetharry.service.PersistenceService;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
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

	final WebMarkupContainer cardHandle;
	final UUID uuid;
	final String ownerSide;

	@SpringBean
	PersistenceService persistenceService;

	public TokenTooltipPanel(final String id, final WebMarkupContainer _cardHandle,
			final UUID _uuid, final String _ownerSide)
	{
		super(id);
		this.cardHandle = _cardHandle;
		this.uuid = _uuid;
		this.ownerSide = _ownerSide;

		final AjaxLink<Void> closeTooltip = new AjaxLink<Void>("closeTooltip")
		{
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(final AjaxRequestTarget target)
			{
				target.appendJavaScript("jQuery('.tooltip').attr('style', 'display: none;'); ");
			}
		};

		if ("infrared".equals(this.ownerSide))
		{
			this.add(new AttributeModifier("style", "border: 1px solid red;"));
		}
		else if ("ultraviolet".equals(this.ownerSide))
		{
			this.add(new AttributeModifier("style", "border: 1px solid purple;"));
		}
		else
		{
			this.add(new AttributeModifier("style", "border: 1px solid yellow;"));
		}
		this.add(closeTooltip);

		final Token myToken = this.persistenceService.getTokenFromUuid(this.uuid);

		this.add(new Label("tokenType", myToken.getType()).setOutputMarkupId(true));
		this.add(new Label("tokenPower", myToken.getPower()).setOutputMarkupId(true));
		this.add(new Label("tokenThoughness", myToken.getThoughness()).setOutputMarkupId(true));
		this.add(new Label("tokenColors", myToken.getColors()).setOutputMarkupId(true));
		this.add(new Label("tokenDescription", myToken.getDescription()).setOutputMarkupId(true));

	}

	@Required
	public void setPersistenceService(final PersistenceService _persistenceService)
	{
		this.persistenceService = _persistenceService;
	}

}
