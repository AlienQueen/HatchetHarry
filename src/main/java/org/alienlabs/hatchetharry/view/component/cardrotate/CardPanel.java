package org.alienlabs.hatchetharry.view.component.cardrotate;

import java.util.HashMap;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.alienlabs.hatchetharry.view.component.CardMoveBehavior;
import org.alienlabs.hatchetharry.view.component.TooltipPanel;
import org.alienlabs.hatchetharry.view.page.CardMovePage;
import org.alienlabs.hatchetharry.view.page.CardRotatePage;
import org.alienlabs.hatchetharry.view.page.HomePage;
import org.apache.wicket.Component;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.protocol.http.servlet.ServletWebRequest;
import org.apache.wicket.request.resource.PackageResourceReference;
import org.apache.wicket.util.template.PackageTextTemplate;
import org.apache.wicket.util.template.TextTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CardPanel extends Panel
{
	private static final long serialVersionUID = 1L;

	private static final Logger LOGGER = LoggerFactory.getLogger(CardPanel.class);

	private final BookmarkablePageLink<CardMovePage> cardMovePage;
	private final BookmarkablePageLink<CardRotatePage> cardRotatePage;

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

		this.add(new Behavior()
		{
			private static final long serialVersionUID = 1L;

			@Override
			public void renderHead(final Component component, final IHeaderResponse response)
			{
				response.renderCSSReference(new PackageResourceReference(HomePage.class,
						"stylesheet/menu.css"));
				response.renderJavaScriptReference(new PackageResourceReference(HomePage.class,
						"script/contextmenu/jquery.contextMenu.js"));
				response.renderCSSReference(new PackageResourceReference(HomePage.class,
						"script/contextmenu/jquery.contextMenu.css"));
			}
		});

		final WebMarkupContainer menutoggleButton = new WebMarkupContainer("menutoggleButton");
		menutoggleButton.setOutputMarkupId(true);
		menutoggleButton.setMarkupId("menutoggleButton" + this.uuid.toString());

		final Form<String> form = new Form<String>("form");
		form.setOutputMarkupId(true);
		menutoggleButton.add(new CardMoveBehavior(this, this.uuid));
		menutoggleButton.add(new CardRotateBehavior(this, this.uuid));

		final TextField<String> jsessionid = new TextField<String>("jsessionid", new Model<String>(
				this.getHttpServletRequest().getRequestedSessionId()));
		jsessionid.setMarkupId("jsessionid" + this.uuid);
		jsessionid.setOutputMarkupId(true);

		CardPanel.LOGGER
				.info("jsessionid: " + this.getHttpServletRequest().getRequestedSessionId());
		CardPanel.LOGGER.info("uuid: " + this.uuid);
		final TextField<String> mouseX = new TextField<String>("mouseX", new Model<String>("0"));
		final TextField<String> mouseY = new TextField<String>("mouseY", new Model<String>("0"));
		mouseX.setMarkupId("mouseX" + this.uuid);
		mouseY.setMarkupId("mouseY" + this.uuid);
		mouseX.setOutputMarkupId(true);
		mouseY.setOutputMarkupId(true);

		final Image handleImage = new Image("handleImage", new PackageResourceReference(
				"images/arrow.png"));
		final Image tapHandleImage = new Image("tapHandleImage", new PackageResourceReference(
				"images/rightArrow.png"));
		handleImage.setOutputMarkupId(true);

		final TooltipPanel cardBubbleTip = new TooltipPanel("cardTooltip", bigImage);
		cardBubbleTip.setOutputMarkupId(true);
		cardBubbleTip.setMarkupId("cardTooltip" + this.uuid);
		cardBubbleTip.add(new SimpleAttributeModifier("style", "display:none;"));

		final Image cardImage = new Image("cardImage", new PackageResourceReference(HomePage.class,
				smallImage));
		cardImage.setOutputMarkupId(true);
		cardImage.setMarkupId("card" + this.uuid.toString());

		tapHandleImage.setMarkupId("tapHandleImage" + this.uuid.toString());
		tapHandleImage.setOutputMarkupId(true);

		form.add(jsessionid, mouseX, mouseY, handleImage, cardImage, tapHandleImage, cardBubbleTip);
		menutoggleButton.add(form);
		this.add(menutoggleButton);

		// Placeholders for CardPanel-adding with AjaxRequestTarget
		final WebMarkupContainer cardParent = new WebMarkupContainer("cardParent4");
		cardParent.setOutputMarkupId(true);
		final WebMarkupContainer cardPlaceholder = new WebMarkupContainer("cardPlaceholder4");
		cardPlaceholder.setOutputMarkupId(true);
		cardParent.add(cardPlaceholder);
		this.add(cardParent);
	}

	public HttpServletRequest getHttpServletRequest()
	{
		final ServletWebRequest servletWebRequest = (ServletWebRequest)this.getRequest();
		return servletWebRequest.getContainerRequest();
	}

	public UUID getUuid()
	{
		return this.uuid;
	}

	@Override
	public void renderHead(final IHeaderResponse response)
	{
		final TextTemplate template1 = new PackageTextTemplate(HomePage.class,
				"script/tooltip/easyTooltip.js");
		final StringBuffer js = new StringBuffer().append(template1.asString());
		response.renderJavaScript(js.toString(), "easyTooltip.js");

		final TextTemplate template2 = new PackageTextTemplate(HomePage.class,
				"script/tooltip/initTooltip.js");
		final HashMap<String, Object> variables = new HashMap<String, Object>();
		variables.put("uuid", this.uuid);
		template2.interpolate(variables);
		final StringBuffer js2 = new StringBuffer().append(template2.asString());
		response.renderJavaScript(js2.toString(), "initTooltip.js" + this.uuid);
	}
}
