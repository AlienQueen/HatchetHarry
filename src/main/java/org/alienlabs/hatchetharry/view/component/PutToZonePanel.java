package org.alienlabs.hatchetharry.view.component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.help.UnsupportedOperationException;

import org.alienlabs.hatchetharry.model.CardZone;
import org.alienlabs.hatchetharry.model.Player;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

public class PutToZonePanel extends Panel
{
	private static final long serialVersionUID = 1L;
	private final CardZone sourceZone;
	protected final DropDownChoice<CardZone> targetZoneInput;
	private final Player player;

	@SuppressWarnings("incomplete-switch")
	public PutToZonePanel(final String id, final CardZone _sourceZone, final Player _player)
	{
		super(id);
		this.sourceZone = _sourceZone;
		this.player = _player;

		final Form<String> form = new Form<String>("form");
		form.add(new AttributeModifier("class", new Model<String>("put-to-zone-for-"
				+ this.sourceZone)));

		final ArrayList<CardZone> allZones = new ArrayList<CardZone>(Arrays.asList(CardZone
				.values()));
		allZones.remove(_sourceZone);

		CardZone defaultZone = null;

		// TODO add more source zone one day
		switch (this.sourceZone)
		{
			case HAND :
				defaultZone = this.player.getDefaultTargetZoneForHand();
				break;
			case GRAVEYARD :
				defaultZone = this.player.getDefaultTargetZoneForGraveyard();
				break;
			case EXILE :
				defaultZone = this.player.getDefaultTargetZoneForExile();
				break;
			default :
				throw new UnsupportedOperationException();
		}

		final IModel<List<? extends CardZone>> zonesModel = Model.ofList(allZones);
		final Label targetZoneLabel = new Label("targetZoneLabel", "Put card to: ");
		this.targetZoneInput = new DropDownChoice<CardZone>("targetZoneInput",
				Model.of(defaultZone), zonesModel);
		this.targetZoneInput.setOutputMarkupId(true).setMarkupId(
				"putToZoneSelectFor" + this.sourceZone);

		final WebMarkupContainer submit = new WebMarkupContainer("submit");
		submit.setOutputMarkupId(true).setMarkupId("moveToZoneSubmit" + this.sourceZone);

		form.add(targetZoneLabel, this.targetZoneInput, submit);
		this.add(form);

		final PutToZoneBehavior ptzb = new PutToZoneBehavior(this.sourceZone);
		this.add(ptzb);
	}

}
