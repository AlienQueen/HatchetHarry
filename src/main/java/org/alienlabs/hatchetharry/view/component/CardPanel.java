package org.alienlabs.hatchetharry.view.component;

import java.util.HashMap;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.alienlabs.hatchetharry.view.page.HomePage;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.Request;
import org.apache.wicket.request.resource.PackageResourceReference;
import org.apache.wicket.util.template.PackageTextTemplate;
import org.apache.wicket.util.template.TextTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CardPanel extends Panel
{
	private static final long serialVersionUID = 1L;

	private static final Logger LOGGER = LoggerFactory.getLogger(CardPanel.class);

	private final UUID uuid;

	public CardPanel(final String id, final String smallImage, final String bigImage,
			final UUID _uuid)
	{
		super(id);
		this.uuid = _uuid;

		this.setOutputMarkupId(true);

		this.add(new Behavior()
		{
			private static final long serialVersionUID = 1L;

			@Override
			public void renderHead(final Component component, final IHeaderResponse response)
			{
				response.render(CssHeaderItem.forReference(new PackageResourceReference(
						HomePage.class, "stylesheet/menu.css")));
				response.render(JavaScriptHeaderItem.forReference(new PackageResourceReference(
						HomePage.class, "script/contextmenu/jquery.contextMenu.js")));
				response.render(CssHeaderItem.forReference(new PackageResourceReference(
						HomePage.class, "script/contextmenu/jquery.contextMenu.css")));
			}

			@Override
			public boolean getStatelessHint(final Component component)
			{
				return false;
			}
		});

		final WebMarkupContainer menutoggleButton = new WebMarkupContainer("menutoggleButton");
		menutoggleButton.setOutputMarkupId(true);
		menutoggleButton.setMarkupId("menutoggleButton" + this.uuid.toString());

		final Form<String> form = new Form<String>("form");
		form.setOutputMarkupId(true);

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
		handleImage.setMarkupId("handleImage" + this.uuid.toString());
		handleImage.setOutputMarkupId(true);

		final Image tapHandleImage = new Image("tapHandleImage", new PackageResourceReference(
				"images/rightArrow.png"));
		tapHandleImage.setMarkupId("tapHandleImage" + this.uuid.toString());
		tapHandleImage.setOutputMarkupId(true);

		final TooltipPanel cardBubbleTip = new TooltipPanel("cardTooltip", bigImage);
		cardBubbleTip.setOutputMarkupId(true);
		cardBubbleTip.setMarkupId("cardTooltip" + this.uuid);
		cardBubbleTip.add(new AttributeModifier("style", "display:none;"));

		final Image cardImage = new Image("cardImage", new PackageResourceReference(HomePage.class,
				smallImage));
		cardImage.setOutputMarkupId(true);
		cardImage.setMarkupId("card" + this.uuid.toString());

		form.add(jsessionid, mouseX, mouseY, handleImage, cardImage, tapHandleImage, cardBubbleTip);
		menutoggleButton.add(form);
		this.add(menutoggleButton);
	}

	public HttpServletRequest getHttpServletRequest()
	{
		final Request servletWebRequest = this.getRequest();
		return (HttpServletRequest)servletWebRequest.getContainerRequest();
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
		response.render(JavaScriptHeaderItem.forScript(js.toString(), "easyTooltip.js"));

		final TextTemplate template2 = new PackageTextTemplate(HomePage.class,
				"script/tooltip/initTooltip.js");
		final HashMap<String, Object> variables = new HashMap<String, Object>();
		variables.put("uuid", this.uuid);
		template2.interpolate(variables);
		final StringBuffer js2 = new StringBuffer().append(template2.asString());
		response.render(JavaScriptHeaderItem.forScript(js2.toString(), "initTooltip.js" + this.uuid));
	}
}
