package org.alienlabs.hatchetharry.view.component;

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

public class AboutModalWindow extends Panel
{
	private static final long serialVersionUID = -5432292812819537705L;

	public AboutModalWindow(final String id, final ModalWindow _modal)
	{
		super(id);

		final Image img1 = new Image("img1", new PackageResourceReference(HomePage.class,
				"image/logo.png"));

		final Form<String> form = new Form<String>("form");

		final Label text1 = new Label("text1", new Model<String>(
				"HatchetHarry is a Magic: the Gathering playing webapp."));

		final Label text2 = new Label(
				"text2",
				new Model<String>(
						"It is free software licensed under the terms of the GNU Affero General Public License, version 3."));

		final Label text3 = new Label(
				"text3",
				new Model<String>(
						"You are free to modify any part of its source code (client-side or server-side), provided that the changes keep this license."));

		final Label text4 = new Label("text4",
				new Model<String>("(c) 2011-2013 Zala Pierre GOUPIL"));

		final ExternalLink link = new ExternalLink("link", "http://www.gnu.org/licenses/agpl.html");
		final ExternalLink whyLink = new ExternalLink("whyLink",
				"http://www.gnu.org/licenses/why-affero-gpl.html");

		final AjaxButton submit = new AjaxButton("submit", form)
		{
			private static final long serialVersionUID = 5612763286127668L;

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

		form.add(text1, text2, text3, text4, link, whyLink, submit);
		this.add(img1, form);
	}
}
