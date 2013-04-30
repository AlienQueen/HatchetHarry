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

public class TokenPanel extends Panel
{
	private static final long serialVersionUID = 1L;

	private static final Logger LOGGER = LoggerFactory.getLogger(TokenPanel.class);

	@SpringBean
	PersistenceService persistenceService;

	final UUID uuid;

	// private final PutToGraveyardFromBattlefieldBehavior
	// putToGraveyardFromBattlefieldBehavior;

	Player owner;

	public TokenPanel(final String id, final UUID _uuid)
	{
		super(id);
		Injector.get().inject(this);

		this.uuid = _uuid;

		this.setOutputMarkupId(true);

		this.add(new Behavior()
		{
			private static final long serialVersionUID = 1L;

			@Override
			public void renderHead(final Component component, final IHeaderResponse response)
			{
				super.renderHead(component, response);
				response.render(CssHeaderItem.forReference(new PackageResourceReference(
						HomePage.class, "stylesheet/menu.css")));
				response.render(JavaScriptHeaderItem.forReference(new PackageResourceReference(
						HomePage.class, "script/contextmenu/jquery.contextMenu.js")));
				response.render(CssHeaderItem.forReference(new PackageResourceReference(
						HomePage.class, "script/contextmenu/jquery.contextMenu.css")));
			}
		});

		final Token myToken = this.persistenceService.getTokenFromUuid(this.uuid);

		final WebMarkupContainer cardHandle = new WebMarkupContainer("tokenHandle");
		cardHandle.setOutputMarkupId(true);
		cardHandle.setMarkupId("tokenHandle" + this.uuid.toString().replace("-", "_"));
		cardHandle.add(new AttributeModifier("style", "position: absolute; top: " + myToken.getY()
				+ "px; left: " + myToken.getX() + "px;"));

		final WebMarkupContainer menutoggleButton = new WebMarkupContainer("menutoggleButton");
		menutoggleButton.setOutputMarkupId(true);
		menutoggleButton.setMarkupId("menutoggleButton" + this.uuid.toString().replace("-", "_"));

		final Form<String> form = new Form<String>("form");
		form.setOutputMarkupId(true);

		// this.putToGraveyardFromBattlefieldBehavior = new
		// PutToGraveyardFromBattlefieldBehavior(
		// this.uuid);
		// menutoggleButton.add(this.putToGraveyardFromBattlefieldBehavior);

		// this.putToHandFromBattlefieldBehavior = new
		// PutToHandFromBattlefieldBehavior(this.uuid);
		// menutoggleButton.add(this.putToHandFromBattlefieldBehavior);

		// final CardMoveBehavior cardMoveBehavior = new CardMoveBehavior(this,
		// this.uuid,
		// this.putToGraveyardFromBattlefieldBehavior,
		// this.putToHandFromBattlefieldBehavior);
		// menutoggleButton.add(cardMoveBehavior);

		// final CardRotateBehavior cardRotateBehavior = new
		// CardRotateBehavior(this, this.uuid);
		// menutoggleButton.add(cardRotateBehavior);

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
		handleImage.setMarkupId("handleImage" + this.uuid.toString().replace("-", "_"));
		handleImage.setOutputMarkupId(true);

		final Image tapHandleImage = new Image("tapHandleImage", new PackageResourceReference(
				"images/rightArrow.png"));
		tapHandleImage.setMarkupId("tapHandleImage" + this.uuid.toString().replace("-", "_"));
		tapHandleImage.setOutputMarkupId(true);

		final ExternalImage tokenImage = new ExternalImage("tokenImage", "image/backOfCard.jpg");
		tokenImage.setOutputMarkupId(true);
		tokenImage.setMarkupId("token" + this.uuid.toString().replace("-", "_"));

		this.owner = this.persistenceService.getPlayer(myToken.getPlayer().getId());

		if (null != this.owner)
		{
			if ("infrared".equals(this.owner.getSide()))
			{
				tokenImage.add(new AttributeModifier("style", "border: 1px solid red;"));
			}
			else if ("ultraviolet".equals(this.owner.getSide()))
			{
				tokenImage.add(new AttributeModifier("style", "border: 1px solid purple;"));
			}

			final TokenTooltipPanel tokenBubbleTip = new TokenTooltipPanel("tokenTooltip",
					cardHandle, this.uuid, this.owner.getSide());
			tokenBubbleTip.setOutputMarkupId(true);
			tokenBubbleTip.setMarkupId("tokenTooltip" + this.uuid.toString().replace("-", "_"));
			tokenBubbleTip.add(new AttributeModifier("style", "display: none;"));

			form.add(tokenBubbleTip);
		}
		else
		{
			tokenImage.add(new AttributeModifier("style", "border: 1px solid yellow;"));
			final TokenTooltipPanel tokenBubbleTip = new TokenTooltipPanel("tokenTooltip",
					cardHandle, this.uuid, "yellow");
			tokenBubbleTip.setOutputMarkupId(true);
			tokenBubbleTip.setMarkupId("tokenTooltip" + this.uuid.toString().replace("-", "_"));
			tokenBubbleTip.add(new AttributeModifier("style", "display: none;"));

			form.add(tokenBubbleTip);
		}

		final WebMarkupContainer contextMenu = new WebMarkupContainer("contextMenu");
		contextMenu.setOutputMarkupId(true);
		contextMenu.setMarkupId("contextMenu" + this.uuid.toString().replace("-", "_"));

		form.add(jsessionid, mouseX, mouseY, handleImage, tokenImage, tapHandleImage, contextMenu);
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

	// public PutToGraveyardFromBattlefieldBehavior
	// getPutToGraveyardFromBattlefieldBehavior()
	// {
	// return this.putToGraveyardFromBattlefieldBehavior;
	// }
	//
	// public PutToHandFromBattlefieldBehavior
	// getPutToHandFromBattlefieldBehavior()
	// {
	// return this.putToHandFromBattlefieldBehavior;
	// }

}
