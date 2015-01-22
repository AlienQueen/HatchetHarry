package org.alienlabs.hatchetharry.view.component.modalwindow;

import org.alienlabs.hatchetharry.view.page.HomePage;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.resource.PackageResourceReference;

@edu.umd.cs.findbugs.annotations.SuppressFBWarnings(value = { "SE_INNER_CLASS",
		"SIC_INNER_SHOULD_BE_STATIC_ANON" }, justification = "In Wicket, serializable inner classes are common. And as the parent Page is serialized as well, this is no concern. This is no bad practice in Wicket")
public class AboutModalWindow extends Panel
{
	private static final long serialVersionUID = 1L;

	public AboutModalWindow(final String id, final ModalWindow _modal)
	{
		super(id);

		final Image img1 = new Image("img1", new PackageResourceReference(HomePage.class,
				"image/logo.png"));

		final Form<String> form = new Form<>("form");

		final Label text1 = new Label("text1", new Model<>(
				"HatchetHarry is a Magic: the Gathering playing webapp."));

		final Label text2 = new Label(
				"text2",
				new Model<>(
						"It is free software licensed under the terms of the GNU Affero General Public License, version 3."));

		final Label text3 = new Label(
				"text3",
				new Model<>(
						"You are free to modify any part of its source code (client-side or server-side), as long as the changes keep this license."));

		final Label text4 = new Label("text4",
				new Model<>("(c) 2011-2014 Zala Pierre GOUPIL"));

		final ExternalLink link = new ExternalLink("link", "http://www.gnu.org/licenses/agpl.html");
		final ExternalLink whyLink = new ExternalLink("whyLink",
				"http://www.gnu.org/licenses/why-affero-gpl.html");

		final AjaxButton submit = new AjaxButton("submit", form)
		{
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit(final AjaxRequestTarget target, final Form<?> _form)
			{
				_modal.close(target);
			}
		};

		form.add(text1, text2, text3, text4, link, whyLink, submit);
		this.add(img1, form);
	}
}
