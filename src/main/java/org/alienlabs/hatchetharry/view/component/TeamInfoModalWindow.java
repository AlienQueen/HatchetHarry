package org.alienlabs.hatchetharry.view.component;

import org.alienlabs.hatchetharry.view.page.HomePage;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.resource.PackageResourceReference;

public class TeamInfoModalWindow extends Panel
{
	private static final long serialVersionUID = -4905521543219537705L;

	public TeamInfoModalWindow(final String id, final ModalWindow _modal)
	{
		super(id);


		final Image img1 = new Image("img1", new PackageResourceReference(HomePage.class,
				"image/logo.png"));

		final Form<String> form = new Form<String>("form");

		final Label founder = new Label("founder", new Model<String>(
				"Founder: Zala \"AlienQueen\" Goupil (goupilpierre@gmail.com)"));
		final Label lead = new Label("lead", new Model<String>(
				"Lead developer: Zala \"AlienQueen\" Goupil (goupilpierre@gmail.com)"));
		final Label dev1 = new Label("dev1", new Model<String>(
				"Developer: Sandaly Diawara (sandaly.diawara@gmail.com)"));
		final Label gfx = new Label("gfx", new Model<String>(
				"GFX: Marie-Antoinette Navarro (mariea33@gmail.com)"));
		final Label tester1 = new Label("tester1", new Model<String>(
				"Tester: Jean Leherle (jean.ravnica@hotmail.fr)"));
		final Label tester2 = new Label("tester2", new Model<String>(
				"Tester: Benoît Bouchery (benoit.bouchery@laposte.net)"));

		final AjaxButton submit = new AjaxButton("submit", form)
		{
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit(final AjaxRequestTarget target, final Form<?> _form)
			{
				_modal.close(target);
			}

			@Override
			protected void onError(final AjaxRequestTarget target, final Form<?> _form)
			{
			}
		};

		form.add(founder, lead, dev1, gfx, tester1, tester2, submit);
		this.add(img1, form);
	}

}
