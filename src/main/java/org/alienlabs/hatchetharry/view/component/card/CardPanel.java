package org.alienlabs.hatchetharry.view.component.card;

import org.alienlabs.hatchetharry.HatchetHarrySession;
import org.alienlabs.hatchetharry.model.MagicCard;
import org.alienlabs.hatchetharry.model.PlayerAndCard;
import org.alienlabs.hatchetharry.service.PersistenceService;
import org.alienlabs.hatchetharry.view.component.gui.DrawModeBehavior;
import org.alienlabs.hatchetharry.view.component.gui.ExternalImage;
import org.alienlabs.hatchetharry.view.component.zone.PutToExileFromBattlefieldBehavior;
import org.alienlabs.hatchetharry.view.component.zone.PutToGraveyardFromBattlefieldBehavior;
import org.alienlabs.hatchetharry.view.component.zone.PutToHandFromBattlefieldBehavior;
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
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.resource.PackageResourceReference;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

@edu.umd.cs.findbugs.annotations.SuppressFBWarnings(value = { "SE_INNER_CLASS",
		"SIC_INNER_SHOULD_BE_STATIC_ANON" }, justification = "In Wicket, serializable inner classes are common. And as the parent Page is serialized as well, this is no concern. This is no bad practice in Wicket")
public class CardPanel extends Panel
{
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = LoggerFactory.getLogger(CardPanel.class);

	private final PutToHandFromBattlefieldBehavior putToHandFromBattlefieldBehavior;
	private final PutToGraveyardFromBattlefieldBehavior putToGraveyardFromBattlefieldBehavior;
	private final PutToExileFromBattlefieldBehavior putToExileFromBattlefieldBehavior;
	private final DestroyTokenBehavior destroyTokenBehavior;

	@SpringBean
	private PersistenceService persistenceService;
	private final IModel<PlayerAndCard> playerAndCard;

	public CardPanel(final String id, final IModel<PlayerAndCard> _playerAndCard)
	{
		super(id, _playerAndCard);
		this.playerAndCard = _playerAndCard;

		Injector.get().inject(this);
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

		final MagicCard myCard = this.persistenceService.getCardFromUuid(this.playerAndCard
				.getObject().getCard().getUuidObject());

		final WebMarkupContainer cardHandle = new WebMarkupContainer("cardHandle");
		cardHandle.setOutputMarkupId(true);
		final String uuidValidForJs = this.playerAndCard.getObject().getCard().getUuidObject()
				.toString().replace("-", "_");
		cardHandle.setMarkupId("cardHandle" + uuidValidForJs);
		cardHandle.add(new AttributeModifier("name", myCard.getTitle()));

		final WebMarkupContainer menutoggleButton = new WebMarkupContainer("menutoggleButton");
		menutoggleButton.setOutputMarkupId(true);
		menutoggleButton.setMarkupId("menutoggleButton" + uuidValidForJs);

		final Form<String> form = new Form<>("form");
		form.setOutputMarkupId(true);

		this.putToHandFromBattlefieldBehavior = new PutToHandFromBattlefieldBehavior(
				this.playerAndCard.getObject().getCard().getUuidObject());
		menutoggleButton.add(this.putToHandFromBattlefieldBehavior);

		this.putToGraveyardFromBattlefieldBehavior = new PutToGraveyardFromBattlefieldBehavior(
				this.playerAndCard.getObject().getCard().getUuidObject());
		menutoggleButton.add(this.putToGraveyardFromBattlefieldBehavior);

		this.putToExileFromBattlefieldBehavior = new PutToExileFromBattlefieldBehavior(
				this.playerAndCard.getObject().getCard().getUuidObject());
		menutoggleButton.add(this.putToExileFromBattlefieldBehavior);

		if (this.playerAndCard.getObject().getCard().getToken() != null)
		{
			this.destroyTokenBehavior = new DestroyTokenBehavior(this.playerAndCard.getObject()
					.getCard().getUuidObject());
			menutoggleButton.add(this.destroyTokenBehavior);
		}
		else
		{
			this.destroyTokenBehavior = null;
		}

		final CardRotateBehavior cardRotateBehavior = new CardRotateBehavior(this.playerAndCard
				.getObject().getCard().getUuidObject());

		final DrawModeBehavior drawModeBehavior = new DrawModeBehavior(this.playerAndCard
				.getObject().getPlayer());
		menutoggleButton.add(cardRotateBehavior, drawModeBehavior);

		final ArrowDrawBehavior arrowDrawBehavior = new ArrowDrawBehavior("cardHandle"
				+ uuidValidForJs);
		menutoggleButton.add(arrowDrawBehavior);

		CardPanel.LOGGER.info("uuid: " + this.playerAndCard.getObject().getCard().getUuidObject());

		final WebMarkupContainer bullet = new WebMarkupContainer("bullet");
		bullet.setOutputMarkupId(true).setMarkupId("bullet" + uuidValidForJs);

		final ExternalImage cardImage = new ExternalImage("cardImage", this.playerAndCard
				.getObject().getCard().getBigImageFilename());
		cardImage.setOutputMarkupId(true);

		final ExternalImage cardRotate = new ExternalImage("cardRotate", "/image/rightArrow.png");
		cardRotate.setOutputMarkupId(true);
		cardRotate.setMarkupId("cardRotate" + uuidValidForJs);

		cardImage.setMarkupId("card" + uuidValidForJs);

		if (null != this.playerAndCard.getObject().getPlayer())
		{
			if ("infrared".equals(this.playerAndCard.getObject().getPlayer().getSide()
					.getSideName()))
			{
				cardImage.add(new AttributeModifier("style", "border: 1px solid red;"));
			}
			else if ("ultraviolet".equals(this.playerAndCard.getObject().getPlayer().getSide()
					.getSideName()))
			{
				cardImage.add(new AttributeModifier("style", "border: 1px solid purple;"));
			}
		}
		else
		{
			cardImage.add(new AttributeModifier("style", "border: 1px solid yellow;"));
		}

		final CardInBattlefieldContextMenu contextMenu = new CardInBattlefieldContextMenu(
				"contextMenu", new Model<>(myCard));

		form.add(bullet, cardImage, cardRotate, contextMenu);
		menutoggleButton.add(form);

		final WebMarkupContainer side = new WebMarkupContainer("side");
		if (this.playerAndCard.getObject().getPlayer().getSide().getSideName()
				.equals(HatchetHarrySession.get().getPlayer().getSide().getSideName()))
		{
			side.add(new AttributeModifier("class", "battlefieldCardsForSide1"));
		}
		else
		{
			side.add(new AttributeModifier("class", "battlefieldCardsForSide2"));
		}

		side.add(menutoggleButton);
		cardHandle.add(side);
		this.add(cardHandle);

		final CardTooltipBehavior ctb = new CardTooltipBehavior();
		this.add(ctb);

		if (HatchetHarrySession.get().isDisplayTooltips().booleanValue())
		{
			this.add(new MagicCardTooltipPanel("tooltip", myCard.getUuidObject(), myCard
					.getBigImageFilename(), myCard.getOwnerSide(), myCard));
		}
		else
		{
			this.add(new WebMarkupContainer("tooltip"));
		}
	}

	public PlayerAndCard getPlayerAndCard()
	{
		return this.playerAndCard.getObject();
	}

	public PutToGraveyardFromBattlefieldBehavior getPutToGraveyardFromBattlefieldBehavior()
	{
		return this.putToGraveyardFromBattlefieldBehavior;
	}

	public PutToHandFromBattlefieldBehavior getPutToHandFromBattlefieldBehavior()
	{
		return this.putToHandFromBattlefieldBehavior;
	}

	public PutToExileFromBattlefieldBehavior getPutToExileFromBattlefieldBehavior()
	{
		return this.putToExileFromBattlefieldBehavior;
	}

	@Required
	public void setPersistenceService(final PersistenceService _persistenceService)
	{
		this.persistenceService = _persistenceService;
	}

}
