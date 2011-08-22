package org.alienlabs.hatchetharry.view.component;

import org.alienlabs.hatchetharry.view.page.HomePage;
import org.apache.wicket.markup.html.panel.Panel;

@SuppressWarnings("serial")
public class YuiMenuBar extends Panel
{

	public YuiMenuBar(final String id, final HomePage hp)
	{
		super(id);
		final TeamInfoModalWindow modal = new TeamInfoModalWindow("modal");
		this.add(modal);
		this.add(new TeamInfoBehavior(hp, modal));
	}

}
