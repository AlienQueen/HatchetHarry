package org.alienlabs.hatchetharry.view.component;

import java.util.UUID;

import org.alienlabs.hatchetharry.model.MagicCard;
import org.alienlabs.hatchetharry.service.PersistenceService;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

@edu.umd.cs.findbugs.annotations.SuppressWarnings(value = { "SE_INNER_CLASS",
		"SIC_INNER_SHOULD_BE_STATIC_ANON" }, justification = "In Wicket, serializable inner classes are common. And as the parent Page is serialized as well, this is no concern. This is no bad practice in Wicket")
public class MagicCardTooltipPanel extends Panel
{
	static final Logger LOGGER = LoggerFactory.getLogger(MagicCardTooltipPanel.class);
	private static final long serialVersionUID = 1L;
	final UUID uuid;
	final String bigImage;
	final String ownerSide;
	final MagicCard card;

	@SpringBean
	PersistenceService persistenceService;

	public MagicCardTooltipPanel(final String id, final UUID _uuid, final String _bigImage,
		final String _ownerSide, final MagicCard _card)
	{
		super(id);
		this.uuid = _uuid;
		this.bigImage = _bigImage;
		this.ownerSide = _ownerSide;
		this.card = _card;

		final CardTooltipBehavior ctb = new CardTooltipBehavior(this.uuid);
		this.add(ctb);

		final AjaxLink<Void> closeTooltip = new AjaxLink<Void>("closeTooltip")
		{
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(final AjaxRequestTarget target)
			{
				target.appendJavaScript("jQuery('.tooltip').hide(); ");
			}
		};

		final ExternalImage bubbleTipImg1 = new ExternalImage("bubbleTipImg1", this.bigImage);

		if ("infrared".equals(this.ownerSide))
		{
			bubbleTipImg1.add(new AttributeModifier("style", "border: 1px solid red;"));
		}
		else if ("ultraviolet".equals(this.ownerSide))
		{
			bubbleTipImg1.add(new AttributeModifier("style", "border: 1px solid purple;"));
		}
		else
		{
			bubbleTipImg1.add(new AttributeModifier("style", "border: 1px solid yellow;"));
		}

		final CounterTooltip counterPanel = new CounterTooltip("counterPanel", this.card, null);

		this.add(closeTooltip, bubbleTipImg1, counterPanel);
	}

	@Required
	public void setPersistenceService(final PersistenceService _persistenceService)
	{
		this.persistenceService = _persistenceService;
	}

}
