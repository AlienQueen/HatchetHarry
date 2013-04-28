package org.alienlabs.hatchetharry.view.component;

import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.alienlabs.hatchetharry.model.MagicCard;
import org.alienlabs.hatchetharry.model.Player;
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

public class CardPanel extends Panel
{
	private static final long serialVersionUID = 1L;

	private static final Logger LOGGER = LoggerFactory.getLogger(CardPanel.class);

	@SpringBean
	PersistenceService persistenceService;

	final UUID uuid;

	private final PutToGraveyardFromBattlefieldBehavior putToGraveyardFromBattlefieldBehavior;
	private final PutToHandFromBattlefieldBehavior putToHandFromBattlefieldBehavior;

	Player owner;

	public CardPanel(final String id, final String smallImage, final String bigImage,
			final UUID _uuid)
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

		final MagicCard myCard = this.persistenceService.getCardFromUuid(this.uuid);

		final WebMarkupContainer cardHandle = new WebMarkupContainer("cardHandle");
		cardHandle.setOutputMarkupId(true);
		cardHandle.setMarkupId("cardHandle" + this.uuid.toString().replace("-", "_"));
		cardHandle.add(new AttributeModifier("style", "position: absolute; top: " + myCard.getY()
				+ "px; left: " + myCard.getX() + "px;"));

		final WebMarkupContainer menutoggleButton = new WebMarkupContainer("menutoggleButton");
		menutoggleButton.setOutputMarkupId(true);
		menutoggleButton.setMarkupId("menutoggleButton" + this.uuid.toString().replace("-", "_"));

		final Form<String> form = new Form<String>("form");
		form.setOutputMarkupId(true);

		this.putToGraveyardFromBattlefieldBehavior = new PutToGraveyardFromBattlefieldBehavior(
				this.uuid);
		menutoggleButton.add(this.putToGraveyardFromBattlefieldBehavior);

		this.putToHandFromBattlefieldBehavior = new PutToHandFromBattlefieldBehavior(this.uuid);
		menutoggleButton.add(this.putToHandFromBattlefieldBehavior);

		final CardMoveBehavior cardMoveBehavior = new CardMoveBehavior(this, this.uuid,
				this.putToGraveyardFromBattlefieldBehavior, this.putToHandFromBattlefieldBehavior);
		menutoggleButton.add(cardMoveBehavior);

		final CardRotateBehavior cardRotateBehavior = new CardRotateBehavior(this, this.uuid);
		menutoggleButton.add(cardRotateBehavior);

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
		handleImage.setMarkupId("handleImage" + this.uuid.toString().replace("-", "_"));
		handleImage.setOutputMarkupId(true);

		final Image tapHandleImage = new Image("tapHandleImage", new PackageResourceReference(
				"images/rightArrow.png"));
		tapHandleImage.setMarkupId("tapHandleImage" + this.uuid.toString().replace("-", "_"));
		tapHandleImage.setOutputMarkupId(true);

		final ExternalImage cardImage = new ExternalImage("cardImage", smallImage);
		cardImage.setOutputMarkupId(true);
		cardImage.setMarkupId("card" + this.uuid.toString().replace("-", "_"));


		final MagicCard mc = this.persistenceService.getCardFromUuid(this.uuid);

		if (null != mc)
		{
			this.owner = this.persistenceService.getPlayer(mc.getDeck().getPlayerId());

			if (null != this.owner)
			{
				if ("infrared".equals(this.owner.getSide()))
				{
					cardImage.add(new AttributeModifier("style", "border: 1px solid red;"));
				}
				else if ("ultraviolet".equals(this.owner.getSide()))
				{
					cardImage.add(new AttributeModifier("style", "border: 1px solid purple;"));
				}

				final TooltipPanel cardBubbleTip = new TooltipPanel("cardTooltip", cardHandle,
						this.uuid, bigImage, this.owner.getSide());
				cardBubbleTip.setOutputMarkupId(true);
				cardBubbleTip.setMarkupId("cardTooltip" + this.uuid.toString().replace("-", "_"));
				cardBubbleTip.add(new AttributeModifier("style", "display: none;"));

				form.add(cardBubbleTip);
			}
			else
			{
				cardImage.add(new AttributeModifier("style", "border: 1px solid yellow;"));
				final TooltipPanel cardBubbleTip = new TooltipPanel("cardTooltip", cardHandle,
						this.uuid, bigImage, "yellow");
				cardBubbleTip.setOutputMarkupId(true);
				cardBubbleTip.setMarkupId("cardTooltip" + this.uuid.toString().replace("-", "_"));
				cardBubbleTip.add(new AttributeModifier("style", "display: none;"));

				form.add(cardBubbleTip);
			}
		}

		final WebMarkupContainer contextMenu = new WebMarkupContainer("contextMenu");
		contextMenu.setOutputMarkupId(true);
		contextMenu.setMarkupId("contextMenu" + this.uuid.toString().replace("-", "_"));

		form.add(jsessionid, mouseX, mouseY, handleImage, cardImage, tapHandleImage, contextMenu);
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

	public PutToGraveyardFromBattlefieldBehavior getPutToGraveyardFromBattlefieldBehavior()
	{
		return this.putToGraveyardFromBattlefieldBehavior;
	}

	public PutToHandFromBattlefieldBehavior getPutToHandFromBattlefieldBehavior()
	{
		return this.putToHandFromBattlefieldBehavior;
	}

}
