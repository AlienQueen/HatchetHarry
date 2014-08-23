package org.alienlabs.hatchetharry.view.component;

import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.alienlabs.hatchetharry.model.Player;
import org.alienlabs.hatchetharry.model.Token;
import org.alienlabs.hatchetharry.service.PersistenceService;
import org.alienlabs.hatchetharry.view.page.HomePage;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.injection.Injector;
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
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

@edu.umd.cs.findbugs.annotations.SuppressWarnings(value = { "PRMC_POSSIBLY_REDUNDANT_METHOD_CALLS",
		"SE_INNER_CLASS", " SIC_INNER_SHOULD_BE_STATIC_ANON" }, justification = "There's no other way round")
public class TokenPanel extends Panel
{

	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = LoggerFactory.getLogger(TokenPanel.class);
	final UUID uuid;
	@SpringBean
	PersistenceService persistenceService;
	Player owner;

	public TokenPanel(final String id, final UUID _uuid)
	{
		super(id);
		Injector.get().inject(this);

		this.uuid = _uuid;

		this.setOutputMarkupId(true);

		this.add(new HeadProvider());

		final Token myToken = this.persistenceService.getTokenFromUuid(this.uuid);
		this.owner = this.persistenceService.getPlayer(myToken.getPlayer().getId());

		final WebMarkupContainer cardHandle = new WebMarkupContainer("tokenHandle");
		cardHandle.setOutputMarkupId(true);
		String uuidValidForJs = this.uuid.toString().replace("-", "_");
		cardHandle.setMarkupId("tokenHandle" + uuidValidForJs);
		cardHandle.add(new AttributeModifier("style", "position: absolute; top: "
			+ this.owner.getSide().getY() + "px; left: " + this.owner.getSide().getX() + "px;"));
		myToken.setX(this.owner.getSide().getX());
		myToken.setY(this.owner.getSide().getY());
		this.persistenceService.updateToken(myToken);

		final WebMarkupContainer menutoggleButton = new WebMarkupContainer("menutoggleButton");
		menutoggleButton.setOutputMarkupId(true);
		menutoggleButton.setMarkupId("menutoggleButton" + uuidValidForJs);

		final Form<String> form = new Form<String>("form");
		form.setOutputMarkupId(true);

		final TextField<String> jsessionid = new TextField<String>("jsessionid", new Model<String>(
			this.getHttpServletRequest().getRequestedSessionId()));
		jsessionid.setMarkupId("jsessionid" + this.uuid);
		jsessionid.setOutputMarkupId(true);

		TokenPanel.LOGGER.info("jsessionid: "
			+ this.getHttpServletRequest().getRequestedSessionId());
		TokenPanel.LOGGER.info("uuid: " + this.uuid);
		final TextField<String> mouseX = new TextField<String>("mouseX", new Model<String>("0"));
		final TextField<String> mouseY = new TextField<String>("mouseY", new Model<String>("0"));
		mouseX.setMarkupId("mouseX" + this.uuid);
		mouseY.setMarkupId("mouseY" + this.uuid);
		mouseX.setOutputMarkupId(true);
		mouseY.setOutputMarkupId(true);

		final Image handleImage = new Image("handleImage", new PackageResourceReference(
			"images/arrow.png"));
		handleImage.setMarkupId("handleImage" + uuidValidForJs);
		handleImage.setOutputMarkupId(true);

		final WebMarkupContainer bullet = new WebMarkupContainer("bullet");
		bullet.setOutputMarkupId(true).setMarkupId("bullet" + uuidValidForJs);

		final Image tapHandleImage = new Image("tapHandleImage", new PackageResourceReference(
			"images/rightArrow.png"));
		tapHandleImage.setMarkupId("tapHandleImage" + uuidValidForJs);
		tapHandleImage.setOutputMarkupId(true);

		final ExternalImage tokenImage = new ExternalImage("tokenImage", "image/backOfCard.jpg");
		tokenImage.setOutputMarkupId(true);
		tokenImage.setMarkupId("token" + uuidValidForJs);

		if ("infrared".equals(this.owner.getSide().getSideName()))
		{
			tokenImage.add(new AttributeModifier("style", "border: 1px solid red;"));
		}
		else if ("ultraviolet".equals(this.owner.getSide().getSideName()))
		{
			tokenImage.add(new AttributeModifier("style", "border: 1px solid purple;"));
		}

		final TokenTooltipPanel tokenBubbleTip = new TokenTooltipPanel("tokenTooltip", myToken);
		tokenBubbleTip.setOutputMarkupId(true);
		tokenBubbleTip.setMarkupId("tokenTooltip" + uuidValidForJs);
		tokenBubbleTip.add(new AttributeModifier("style", "display: none;"));

		form.add(jsessionid, mouseX, mouseY, handleImage, tokenImage, tapHandleImage,
			tokenBubbleTip);
		menutoggleButton.add(form);
		cardHandle.add(menutoggleButton);

		this.add(cardHandle);
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

	@Required
	public void setPersistenceService(final PersistenceService _persistenceService)
	{
		this.persistenceService = _persistenceService;
	}
}

class HeadProvider extends Behavior
{
	private static final long serialVersionUID = 1L;

	@Override
	public void renderHead(final Component component, final IHeaderResponse response)
	{
		super.renderHead(component, response);
		response.render(JavaScriptHeaderItem.forReference(new PackageResourceReference(
			HomePage.class, "script/contextmenu/jquery.contextMenu.js")));
		response.render(CssHeaderItem.forReference(new PackageResourceReference(HomePage.class,
			"script/contextmenu/jquery.contextMenu.css")));
	}
}
