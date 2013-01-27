package org.alienlabs.hatchetharry.view.component;

import org.alienlabs.hatchetharry.view.page.HomePage;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.panel.Panel;

/**
 * @author nostromo
 */
// TODO: delete this file
public class NotifierPanel extends Panel
{
	private static final long serialVersionUID = 7528525699722463229L;

	private final HomePage page;
	private final String title;
	private final String text;

	private final NotifierBehavior notif;

	final WebMarkupContainer dataBoxParent;
	final Long gameId;

	public NotifierPanel(final String id, final HomePage _page, final String _title,
			final String _text, final WebMarkupContainer _dataBoxParent, final Long _gameId)
	{
		super(id);
		this.setOutputMarkupId(true);

		this.page = _page;
		this.title = _title;
		this.text = _text;

		this.dataBoxParent = _dataBoxParent;
		this.gameId = _gameId;
		this.notif = new NotifierBehavior(this.page, this.title, this.text, this.dataBoxParent,
				this.gameId);
		this.add(this.notif);
	}

	public String getCallbackUrl()
	{
		return this.notif.getCallbackUrl().toString();
	}

}
