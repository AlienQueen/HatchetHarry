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
	final UUID uuid;
	private final PutToHandFromBattlefieldBehavior putToHandFromBattlefieldBehavior;
	private final PutToGraveyardFromBattlefieldBehavior putToGraveyardFromBattlefieldBehavior;
	private final PutToExileFromBattlefieldBehavior putToExileFromBattlefieldBehavior;
	private final DestroyTokenBehavior destroyTokenBehavior;
	@SpringBean
	PersistenceService persistenceService;
	private Player owner;

	public CardPanel(final String id, final String smallImage, final UUID _uuid, final Player _owner)
	{
		super(id);
		Injector.get().inject(this);

		this.uuid = _uuid;
		this.owner = _owner;

		this.setOutputMarkupId(true);

		this.add(new Behavior()
		{
			private static final long serialVersionUID = 1L;

			@Override
			public void renderHead(final Component component, final IHeaderResponse response)
			{
				super.renderHead(component, response);
				response.render(JavaScriptHeaderItem.forReference(new PackageResourceReference(
					HomePage.class, "script/contextmenu/jquery.contextMenu.js")));
				response.render(CssHeaderItem.forReference(new PackageResourceReference(
					HomePage.class, "script/contextmenu/jquery.contextMenu.css")));
			}
		});

		final MagicCard myCard = this.persistenceService.getCardFromUuid(this.uuid);

		final WebMarkupContainer cardHandle = new WebMarkupContainer("cardHandle");
		cardHandle.setOutputMarkupId(true);
		String uuidValidForJs = this.uuid.toString().replace("-", "_");
		cardHandle.setMarkupId("cardHandle" + uuidValidForJs);
		cardHandle.add(new AttributeModifier("style", "position: absolute; top: "
			+ this.owner.getSide().getY() + "px; left: " + this.owner.getSide().getX()
			+ "px;  z-index: 1;"));
		cardHandle.add(new AttributeModifier("name", myCard.getTitle()));

        if ("baldu".equals(id))
		{
			cardHandle.add(new AttributeModifier("class", "baldu"));
		}
		else
		{
			cardHandle.add(new AttributeModifier("class", "magicCard"));
		}

		final WebMarkupContainer menutoggleButton = new WebMarkupContainer("menutoggleButton");
		menutoggleButton.setOutputMarkupId(true);
		menutoggleButton.setMarkupId("menutoggleButton" + uuidValidForJs);

		final Form<String> form = new Form<String>("form");
		form.setOutputMarkupId(true);

		this.putToHandFromBattlefieldBehavior = new PutToHandFromBattlefieldBehavior(this.uuid);
		menutoggleButton.add(this.putToHandFromBattlefieldBehavior);

		this.putToGraveyardFromBattlefieldBehavior = new PutToGraveyardFromBattlefieldBehavior(
			this.uuid);
		menutoggleButton.add(this.putToGraveyardFromBattlefieldBehavior);

		this.putToExileFromBattlefieldBehavior = new PutToExileFromBattlefieldBehavior(this.uuid);
		menutoggleButton.add(this.putToExileFromBattlefieldBehavior);

		this.destroyTokenBehavior = new DestroyTokenBehavior(this.uuid);
		menutoggleButton.add(this.destroyTokenBehavior);

		final CardMoveBehavior cardMoveBehavior = new CardMoveBehavior(this, this.uuid,
			this.putToGraveyardFromBattlefieldBehavior, this.putToHandFromBattlefieldBehavior,
			this.putToExileFromBattlefieldBehavior, this.destroyTokenBehavior, myCard.getX()
				.longValue() == -1l ? this.owner.getSide().getX().longValue() : myCard.getX()
				.longValue(), myCard.getY().longValue() == -1l ? this.owner.getSide().getY()
				.longValue() : myCard.getY().longValue());
		menutoggleButton.add(cardMoveBehavior);

		final CardRotateBehavior cardRotateBehavior = new CardRotateBehavior(this, this.uuid,
			myCard.isTapped());

		final DrawModeBehavior drawModeBehavior = new DrawModeBehavior(this.uuid, myCard,
			this.owner);
		menutoggleButton.add(cardRotateBehavior, drawModeBehavior);

		final ArrowDrawBehavior arrowDrawBehavior = new ArrowDrawBehavior("cardHandle"
			+ uuidValidForJs);
		menutoggleButton.add(arrowDrawBehavior);

		final String requestedSessionId = this.getHttpServletRequest().getRequestedSessionId();
		final TextField<String> jsessionid = new TextField<String>("jsessionid", new Model<String>(
			requestedSessionId));
		jsessionid.setMarkupId("jsessionid" + this.uuid);
		jsessionid.setOutputMarkupId(true);

		CardPanel.LOGGER.info("jsessionid: " + requestedSessionId);
		CardPanel.LOGGER.info("uuid: " + this.uuid);
		final TextField<String> mouseX = new TextField<String>("mouseX", new Model<String>("0"));
		final TextField<String> mouseY = new TextField<String>("mouseY", new Model<String>("0"));
		mouseX.setMarkupId("mouseX" + this.uuid);
		mouseY.setMarkupId("mouseY" + this.uuid);
		mouseX.setOutputMarkupId(true);
		mouseY.setOutputMarkupId(true);

		final ExternalImage handleImage = new ExternalImage("handleImage", "image/arrow.png");
		handleImage.setMarkupId("handleImage" + uuidValidForJs);
		handleImage.setOutputMarkupId(true);

		final ExternalImage tapHandleImage = new ExternalImage("tapHandleImage",
			"image/rightArrow.png");
		tapHandleImage.setMarkupId("tapHandleImage" + uuidValidForJs);
		tapHandleImage.setOutputMarkupId(true);

		final WebMarkupContainer bullet = new WebMarkupContainer("bullet");
		bullet.setOutputMarkupId(true).setMarkupId("bullet" + uuidValidForJs);

		final ExternalImage cardImage = new ExternalImage("cardImage", smallImage);
		cardImage.setOutputMarkupId(true);
		cardImage.setMarkupId("card" + uuidValidForJs);
		cardImage.add(new AttributeModifier("class", "clickableCard"));

		this.owner = this.persistenceService.getPlayer(myCard.getDeck().getPlayerId());
		if (null != this.owner)
		{
			if ("infrared".equals(this.owner.getSide().getSideName()))
			{
				cardImage.add(new AttributeModifier("style", "border: 1px solid red;"));
				handleImage.add(new AttributeModifier("style", "border: 1px red dotted;"));
			}
			else if ("ultraviolet".equals(this.owner.getSide().getSideName()))
			{
				cardImage.add(new AttributeModifier("style", "border: 1px solid purple;"));
				handleImage.add(new AttributeModifier("style", "border: 1px purple dotted;"));
			}
		}
		else
		{
			cardImage.add(new AttributeModifier("style", "border: 1px solid yellow;"));
			handleImage.add(new AttributeModifier("style", "border: 1px yellow dotted;"));
		}

		final WebMarkupContainer contextMenu = new WebMarkupContainer("contextMenu");
		contextMenu.setOutputMarkupId(true);
		contextMenu.setMarkupId("contextMenu" + uuidValidForJs);

		final WebMarkupContainer card = new WebMarkupContainer("card");
		final WebMarkupContainer token = new WebMarkupContainer("token");
		contextMenu.add(card, token);

		if (null == myCard.getToken())
		{
			token.setVisible(false);
		}
		else
		{
			card.setVisible(false);
		}

		form.add(jsessionid, mouseX, mouseY, handleImage, bullet, cardImage, tapHandleImage,
			contextMenu);
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
