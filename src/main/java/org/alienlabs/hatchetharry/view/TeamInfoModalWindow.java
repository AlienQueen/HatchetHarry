package org.alienlabs.hatchetharry.view;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.PackageResourceReference;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.model.Model;

@SuppressWarnings("serial")
public class TeamInfoModalWindow extends ModalWindow
{

	public TeamInfoModalWindow(final String id)
	{
		super(id);

		this.setInitialWidth(450);
		this.setInitialHeight(300);

		this.setTitle("HatchetHarry Team info");

		final Image img1 = new Image("img1", new PackageResourceReference(
				TeamInfoModalWindow.class, "images/logo.png"));

		final Form<String> form = new Form<String>("form");

		final Label founder = new Label("founder", new Model<String>(
				"Founder: Zala \"AlienQueen\" Goupil (goupilpierre@gmail.com"));
		final Label lead = new Label("lead", new Model<String>(
				"Lead developer: Zala \"AlienQueen\" Goupil (goupilpierre@gmail.com"));
		final Label tester1 = new Label("tester1", new Model<String>(
				"Tester: Jean Leherle (jean.ravnica@hotmail.fr)"));

		final AjaxButton submit = new AjaxButton("submit")
		{
			@Override
			protected void onSubmit(final AjaxRequestTarget target, final Form<?> _form)
			{
				ModalWindow.closeCurrent(target);
			}

		};
		submit.setOutputMarkupId(true);

		final Image img2 = new Image("img2", new PackageResourceReference(
				TeamInfoModalWindow.class, "images/logo.png"));

		this.add(img1);
		form.add(founder, lead, tester1, submit);
		submit.add(img2);
		this.add(form);

	}

}
