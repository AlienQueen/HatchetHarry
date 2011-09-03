package org.alienlabs.hatchetharry.view.component;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.panel.Panel;

/**
 * @author nostromo
 */
public class NotifierPanel extends Panel
{
	private static final long serialVersionUID = 7528525699722463229L;

	private final WebPage page;
	private final String title;
	private final String text;

	private final NotifierBehavior notif;

	public NotifierPanel(final String id, final WebPage _page, final String _title,
			final String _text)
	{
		super(id);
		this.setOutputMarkupId(true);

		this.page = _page;
		this.title = _title;
		this.text = _text;

		this.notif = new NotifierBehavior(this.page, this.title, this.text);
		this.add(this.notif);
	}

	public String getCallbackUrl()
	{
		return this.notif.getCallbackUrl().toString();
	}

}
