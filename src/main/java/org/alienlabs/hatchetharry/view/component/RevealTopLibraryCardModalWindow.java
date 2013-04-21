package org.alienlabs.hatchetharry.view.component;

import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RevealTopLibraryCardModalWindow extends Panel
{
	private static final long serialVersionUID = 1L;

	static final Logger LOGGER = LoggerFactory.getLogger(RevealTopLibraryCardModalWindow.class);

	RequiredTextField<String> nameInput;

	public RevealTopLibraryCardModalWindow(final String id)
	{
		super(id);
		final ExternalImage topLibraryCard = new ExternalImage("topLibraryCard",
				"cards/topLibraryCard.jpg?" + Math.random());
		this.add(topLibraryCard);
	}

}
