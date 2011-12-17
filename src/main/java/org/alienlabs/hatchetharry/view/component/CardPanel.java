package org.alienlabs.hatchetharry.view.component;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.alienlabs.hatchetharry.view.page.CardMovePage;
import org.alienlabs.hatchetharry.view.page.CardRotatePage;
import org.alienlabs.hatchetharry.view.page.HomePage;
import org.apache.wicket.ResourceReference;
import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.markup.html.CSSPackageResource;
import org.apache.wicket.markup.html.WebMarkupContainer;
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

	BookmarkablePageLink<CardMovePage> cardMovePage = null;
	BookmarkablePageLink<CardRotatePage> cardRotatePage = null;

	private final WebMarkupContainer cardParent;
	private final WebMarkupContainer cardPlaceholder;

	private final UUID uuid;

	public CardPanel(final String id, final String smallImage, final String bigImage,
			final UUID _uuid)
	{
		super(id);
		this.uuid = _uuid;

		this.setOutputMarkupId(true);

		this.cardMovePage = new BookmarkablePageLink<CardMovePage>("cardMove", CardMovePage.class);
		this.cardRotatePage = new BookmarkablePageLink<CardRotatePage>("cardRotate",
				CardRotatePage.class);

		this.add(this.cardMovePage, this.cardRotatePage);

		this.add(new JavaScriptReference("jQuery.bubbletip-1.0.6.js", HomePage.class,
				"script/bubbletip/jQuery.bubbletip-1.0.6.js"));
		this.add(CSSPackageResource.getHeaderContribution(new CompressedResourceReference(
				HomePage.class, "script/bubbletip/bubbletip.css")));
		this.add(CSSPackageResource.getHeaderContribution(new CompressedResourceReference(
				HomePage.class, "stylesheet/menu.css")));

		this.add(new JavaScriptReference("jquery.contextMenu.js", HomePage.class,
				"script/contextmenu/jquery.contextMenu.js"));
		this.add(CSSPackageResource.getHeaderContribution(new CompressedResourceReference(
				HomePage.class, "script/contextmenu/jquery.contextMenu.css")));

		final WebMarkupContainer menutoggleButton = new WebMarkupContainer("menutoggleButton");
		menutoggleButton.setOutputMarkupId(true);
		menutoggleButton.setMarkupId("menutoggleButton" + this.uuid.toString());


		this.list = new LinkedList<BroadcastFilter>();

		final Form<String> form = new Form<String>("form");
		form.setOutputMarkupId(true);
		menutoggleButton.add(new CardMoveBehavior(this, form, this.uuid));
		menutoggleButton.add(new CardRotateBehavior(this, form, this.uuid));

		final TextField<String> jsessionid = new TextField<String>("jsessionid", new Model<String>(
				(this.getHttpServletRequest().getRequestedSessionId())));
		jsessionid.setMarkupId("jsessionid" + this.uuid);
		jsessionid.setOutputMarkupId(true);

		CardPanel.logger
				.info("jsessionid: " + this.getHttpServletRequest().getRequestedSessionId());
		CardPanel.logger.info("uuid: " + this.uuid);
		final TextField<String> mouseX = new TextField<String>("mouseX", new Model<String>("0"));
		final TextField<String> mouseY = new TextField<String>("mouseY", new Model<String>("0"));
		mouseX.setMarkupId("mouseX" + this.uuid);
		mouseY.setMarkupId("mouseY" + this.uuid);
		mouseX.setOutputMarkupId(true);
		mouseY.setOutputMarkupId(true);

		final Image handleImage = new Image("handleImage",
				new ResourceReference("images/arrow.png"));
		final Image tapHandleImage = new Image("tapHandleImage", new ResourceReference(
				"images/rightArrow.png"));
		handleImage.setOutputMarkupId(true);

		final TooltipPanel cardBubbleTip = new TooltipPanel("cardBubbleTip", bigImage);
		cardBubbleTip.setOutputMarkupId(true);
		cardBubbleTip.setMarkupId("cardBubbleTip" + this.uuid);
		cardBubbleTip.add(new SimpleAttributeModifier("style", "display:none;"));

		final Image cardImage = new Image("cardImage", new ResourceReference(HomePage.class,
				smallImage));
		cardImage.setOutputMarkupId(true);
		cardImage.setMarkupId("card" + this.uuid.toString());

		tapHandleImage.setMarkupId("tapHandleImage" + this.uuid.toString());
		tapHandleImage.setOutputMarkupId(true);

		form.add(jsessionid, mouseX, mouseY, handleImage, cardImage, tapHandleImage, cardBubbleTip);
		menutoggleButton.add(form);
		this.add(menutoggleButton);

		// Placeholders for CardPanel-adding with AjaxRequestTarget
		this.cardParent = new WebMarkupContainer("cardParent4");
		this.cardParent.setOutputMarkupId(true);
		this.cardPlaceholder = new WebMarkupContainer("cardPlaceholder4");
		this.cardPlaceholder.setOutputMarkupId(true);
		this.cardParent.add(this.cardPlaceholder);
		this.add(this.cardParent);
	}

	public HttpServletRequest getHttpServletRequest()
	{
		final ServletWebRequest servletWebRequest = (ServletWebRequest)this.getRequest();
		final HttpServletRequest request = servletWebRequest.getHttpServletRequest();
		return request;
	}

	public UUID getUuid()
	{
		return this.uuid;
	}

}
