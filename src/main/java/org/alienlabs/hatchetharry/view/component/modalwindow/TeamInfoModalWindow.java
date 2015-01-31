package org.alienlabs.hatchetharry.view.component.modalwindow;

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

@edu.umd.cs.findbugs.annotations.SuppressFBWarnings(value = { "SE_INNER_CLASS",
		"SIC_INNER_SHOULD_BE_STATIC_ANON" }, justification = "In Wicket, serializable inner classes are common. And as the parent Page is serialized as well, this is no concern. This is no bad practice in Wicket")
public class TeamInfoModalWindow extends Panel
{
	private static final long serialVersionUID = 1L;

	public TeamInfoModalWindow(final String id, final ModalWindow _modal)
	{
		super(id);


		final Image img1 = new Image("img1", new PackageResourceReference(HomePage.class,
				"image/logo.png"));

		final Form<String> form = new Form<>("form");

		final Label founder = new Label("founder", new Model<>(
				"Founder: Zala \"AlienQueen\" Goupil (goupilpierre@gmail.com)"));
		final Label lead = new Label("lead", new Model<>(
				"Lead developer: Zala \"AlienQueen\" Goupil (goupilpierre@gmail.com)"));
		final Label webdesign = new Label("webdesign", new Model<>(
				"Web design: Millicent Billette (millicent.billette@gmail.com)"));
		final Label gfx = new Label("gfx", new Model<>(
                "GFX: Marie-Antoinette Navarro (zalmareddp@gmail.com)"));
		final Label tester1 = new Label("tester1", new Model<>(
				"Tester: Jean Leherle (jean.ravnica@hotmail.fr)"));
		final Label tester2 = new Label("tester2", new Model<>(
				"Tester: Benoît Bouchery (benoit.bouchery@laposte.net)"));

		final AjaxButton submit = new AjaxButton("submit", form)
		{
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit(final AjaxRequestTarget target, final Form<?> _form)
			{
				_modal.close(target);
			}
		};

		form.add(founder, lead, webdesign, gfx, tester1, tester2, submit);
		this.add(img1, form);
	}

}
