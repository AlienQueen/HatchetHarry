package org.alienlabs.hatchetharry.view.component.card;

import java.util.UUID;

import org.alienlabs.hatchetharry.model.MagicCard;
import org.alienlabs.hatchetharry.view.component.gui.ExternalImage;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.panel.Panel;

@edu.umd.cs.findbugs.annotations.SuppressFBWarnings(value = { "SE_INNER_CLASS",
"SIC_INNER_SHOULD_BE_STATIC_ANON" }, justification = "In Wicket, serializable inner classes are common. And as the parent Page is serialized as well, this is no concern. This is no bad practice in Wicket")
public class MagicCardTooltipPanel extends Panel
{
	private static final long serialVersionUID = 1L;
	private final String bigImage;
	private final String ownerSide;
	private final MagicCard card;

	// TODO remove _uuid
	public MagicCardTooltipPanel(final String id, final UUID _uuid, final String _bigImage,
			final String _ownerSide, final MagicCard _card)
	{
		super(id);
		this.bigImage = _bigImage;
		this.ownerSide = _ownerSide;
		this.card = _card;

		final CardTooltipBehavior ctb = new CardTooltipBehavior();
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

				final CounterTooltip counterPanel = new CounterTooltip("counterPanel", this.card,
						this.card.getToken());

				this.add(closeTooltip, bubbleTipImg1, counterPanel);
	}

}
