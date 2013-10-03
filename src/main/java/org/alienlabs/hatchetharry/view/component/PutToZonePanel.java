package org.alienlabs.hatchetharry.view.component;

import java.util.ArrayList;
import java.util.Arrays;

import org.alienlabs.hatchetharry.model.CardZone;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxButton;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;

public class PutToZonePanel extends Panel
{
	private static final long serialVersionUID = 1L;
	private final CardZone sourceZone;
	protected CardZone targetZone = CardZone.BATTLEFIELD;
	protected final DropDownChoice<CardZone> targetZoneInput;

	public PutToZonePanel(final String id, final CardZone _sourceZone)
	{
		super(id);
		this.sourceZone = _sourceZone;

		final Form<String> form = new Form<String>("form");
		form.add(new AttributeModifier("class", new Model<String>("put-to-zone-for-"
				+ this.sourceZone)));

		final ArrayList<CardZone> allZones = new ArrayList<CardZone>(Arrays.asList(CardZone
				.values()));
		allZones.remove(_sourceZone);

		final Model<ArrayList<CardZone>> zonesModel = new Model<ArrayList<CardZone>>(allZones);

		final Label targetZoneLabel = new Label("targetZoneLabel", "Put card to: ");
		this.targetZoneInput = new DropDownChoice<CardZone>("targetZoneInput", new Model<CardZone>(
				this.targetZone), zonesModel);

		final IndicatingAjaxButton submit = new IndicatingAjaxButton("submit", form)
		{
			private static final long serialVersionUID = 5612763286127668L;

			@Override
			protected void onSubmit(final AjaxRequestTarget target, final Form<?> _form)
			{
				PutToZonePanel.this.targetZone = PutToZonePanel.this.targetZoneInput
						.getModelObject();
			}
		};

		form.add(targetZoneLabel, this.targetZoneInput, submit);
		this.add(form);
	}

}
