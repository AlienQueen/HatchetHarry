package org.alienlabs.hatchetharry.view;

import java.util.LinkedList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.wicket.ResourceReference;
import org.apache.wicket.Session;
import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.markup.html.CSSPackageResource;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.html.resources.CompressedResourceReference;
import org.apache.wicket.markup.html.resources.JavaScriptReference;
import org.apache.wicket.model.Model;
import org.apache.wicket.protocol.http.servlet.ServletWebRequest;
import org.atmosphere.cpr.BroadcastFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("serial")
public class CardPanel extends Panel
{
	static final Logger logger = LoggerFactory.getLogger(CardPanel.class);

	/**
	 * List of {@link BroadcastFilter}
	 */
	final List<BroadcastFilter> list;

	final BookmarkablePageLink<CardMovePage> cardMovePage;

	public CardPanel(final String id)
	{
		super(id);

		Session.findOrCreate();

		this.cardMovePage = new BookmarkablePageLink<CardMovePage>("cardMove", CardMovePage.class);
		this.add(this.cardMovePage);

		this.add(new JavaScriptReference("jQuery.bubbletip-1.0.6.js", HomePage.class,
				"scripts/bubbletip/jQuery.bubbletip-1.0.6.js"));
		this.add(CSSPackageResource.getHeaderContribution(new CompressedResourceReference(
				HomePage.class, "scripts/bubbletip/bubbletip.css")));

		final WebMarkupContainer menutoggleButton = new WebMarkupContainer("menutoggleButton");
		final Image menutoggleImage = new Image("menutoggleImage", new ResourceReference(
				"cards/BalduvianHorde_small.jpg"));
		menutoggleButton.setOutputMarkupId(true);
		menutoggleButton.setMarkupId("contextMenu");


		final Image bubbleTipImg1 = new Image("bubbleTipImg1", new ResourceReference(
				"cards/BalduvianHorde.jpg"));
		final Label bubbleTipText1 = new Label("bubbleTipText1", new Model<String>(
				"<b><br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
						+ "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
						+ "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
						+ "5/5<br/><br/>"
						+ "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;When Balduvian Horde comes<br/>"
						+ "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;into play,sacrifice it<br/>"
						+ "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;unless you discard a card<br/>"
						+ "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;at random from your hand</b>"));
		bubbleTipText1.add(new SimpleAttributeModifier("style", "color: white;"));
		bubbleTipText1.setOutputMarkupPlaceholderTag(true).setEscapeModelStrings(false);

		this.add(new JavaScriptReference("jquery.contextMenu.js", HomePage.class,
				"scripts/contextmenu/jquery.contextMenu.js"));
		this.add(CSSPackageResource.getHeaderContribution(new CompressedResourceReference(
				HomePage.class, "scripts/contextmenu/jquery.contextMenu.css")));

		this.list = new LinkedList<BroadcastFilter>();

		final Form<String> form = new Form<String>("form");
		menutoggleButton.add(new CardMoveBehavior(this, form));

		final TextField<String> jsessionid = new TextField<String>("jsessionid", new Model<String>(
				((ServletWebRequest)this.getRequest()).getHttpServletRequest()
						.getRequestedSessionId()));
		CardPanel.logger.info("jsessionid: "
				+ ((ServletWebRequest)this.getRequest()).getHttpServletRequest()
						.getRequestedSessionId());
		final TextField<String> mouseX = new TextField<String>("mouseX", new Model<String>("0"));
		final TextField<String> mouseY = new TextField<String>("mouseY", new Model<String>("0"));

		final Image handleImage = new Image("handleImage",
				new ResourceReference("images/arrow.png"));

		form.add(jsessionid, mouseX, mouseY, handleImage, menutoggleImage, bubbleTipImg1,
				bubbleTipText1);
		menutoggleButton.add(form);
		this.add(menutoggleButton);
	}

	public HttpServletRequest getHttpServletRequest()
	{
		final ServletWebRequest servletWebRequest = (ServletWebRequest)this.getRequest();
		final HttpServletRequest request = servletWebRequest.getHttpServletRequest();
		return request;
	}

}
