package org.alienlabs.hatchetharry.view.component.card;

import java.io.IOException;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.alienlabs.hatchetharry.HatchetHarrySession;
import org.alienlabs.hatchetharry.model.MagicCard;
import org.alienlabs.hatchetharry.model.channel.CardMoveCometChannel;
import org.alienlabs.hatchetharry.service.PersistenceService;
import org.alienlabs.hatchetharry.view.clientsideutil.EventBusPostService;
import org.alienlabs.hatchetharry.view.component.zone.PutToExileFromBattlefieldBehavior;
import org.alienlabs.hatchetharry.view.component.zone.PutToGraveyardFromBattlefieldBehavior;
import org.alienlabs.hatchetharry.view.component.zone.PutToHandFromBattlefieldBehavior;
import org.alienlabs.hatchetharry.view.page.HomePage;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.injection.Injector;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.model.Model;
import org.apache.wicket.protocol.http.servlet.ServletWebRequest;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.template.PackageTextTemplate;
import org.apache.wicket.util.template.TextTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

public class CardMoveBehavior extends AbstractDefaultAjaxBehavior
{
	private static final long serialVersionUID = 1L;

	private static final Logger LOGGER = LoggerFactory.getLogger(CardMoveBehavior.class);
	private final Model<CardPanel> panel;

	private final String uuidValidForJs;
	private final PutToHandFromBattlefieldBehavior putToHandFromBattlefieldBehavior;
	private final PutToGraveyardFromBattlefieldBehavior putToGraveyardFromBattlefieldBehavior;
	private final PutToExileFromBattlefieldBehavior putToExileFromBattlefieldBehavior;
	private final DestroyTokenBehavior destroyTokenBehavior;

	@SpringBean
	private PersistenceService persistenceService;

	private CardMoveBehavior(final Model<CardPanel> cp,
			final PutToGraveyardFromBattlefieldBehavior _putToGraveyardBehavior,
			final PutToHandFromBattlefieldBehavior _putToHandFromBattlefieldBehavior,
			final PutToExileFromBattlefieldBehavior _putToExileFromBattlefieldBehavior,
			final DestroyTokenBehavior _destroyTokenBehavior)
	{
		Injector.get().inject(this);

		this.panel = cp;
		this.uuidValidForJs = this.panel.getObject().getPlayerAndCard().getCard().getUuidObject()
				.toString().replaceAll("-", "_");

		this.putToGraveyardFromBattlefieldBehavior = _putToGraveyardBehavior;
		this.putToHandFromBattlefieldBehavior = _putToHandFromBattlefieldBehavior;
		this.putToExileFromBattlefieldBehavior = _putToExileFromBattlefieldBehavior;
		this.destroyTokenBehavior = _destroyTokenBehavior;
	}

	@Override
	protected void respond(final AjaxRequestTarget target)
	{
		CardMoveBehavior.LOGGER.info("respond");
		final ServletWebRequest servletWebRequest = (ServletWebRequest)this.panel.getObject()
				.getRequest();
		final HttpServletRequest request = servletWebRequest.getContainerRequest();

		request.getParameter("posX");
		request.getParameter("posY");
		CardMoveBehavior.LOGGER.info("uuid: " + this.uuidValidForJs);

		final MagicCard mc;
		final Long gameId;

		try
		{
			mc = this.persistenceService.getCardFromUuid(this.panel.getObject().getPlayerAndCard()
					.getCard().getUuidObject());
			if (null == mc)
			{
				return;
			}

			gameId = mc.getGameId();
			CardMoveBehavior.LOGGER.info("uuid: " + this.uuidValidForJs);
			this.persistenceService.updateCard(mc);
		}
		catch (final IllegalArgumentException e)
		{
			CardMoveBehavior.LOGGER.error("error parsing UUID of moved card", e);
			return;
		}

		final Long playerId = HatchetHarrySession.get().getPlayer().getId();

		CardMoveBehavior.LOGGER.info("playerId in respond(): "
				+ HatchetHarrySession.get().getPlayer().getId());

		final List<BigInteger> allPlayersInGame = CardMoveBehavior.this.persistenceService
				.giveAllPlayersFromGame(gameId);
		final CardMoveCometChannel cardMoveCometChannel = new CardMoveCometChannel(gameId, mc,
				this.uuidValidForJs, playerId);
		EventBusPostService.post(allPlayersInGame, cardMoveCometChannel);
	}

	@Override
	public void renderHead(final Component component, final IHeaderResponse response)
	{
		super.renderHead(component, response);

		StringBuilder js = new StringBuilder();

		final HashMap<String, Object> variables = new HashMap<>();
		variables.put("url", this.getCallbackUrl());
		variables.put("uuidValidForJs", this.uuidValidForJs);
		variables.put("handUrl", this.putToHandFromBattlefieldBehavior.getCallbackUrl());
		variables.put("graveyardUrl", this.putToGraveyardFromBattlefieldBehavior.getCallbackUrl());
		variables.put("exileUrl", this.putToExileFromBattlefieldBehavior.getCallbackUrl());
		variables.put("destroyUrl", this.destroyTokenBehavior.getCallbackUrl());

		// TODO in reality, cardMove.js configures the context menu: move it in
		// its own Behavior
		final TextTemplate template1 = new PackageTextTemplate(HomePage.class,
				"script/draggableHandle/cardMove.js");
		template1.interpolate(variables);
		js = js.append("\n" + template1.asString());

		final TextTemplate template2 = new PackageTextTemplate(HomePage.class,
				"script/draggableHandle/initDrag.js");
		template2.interpolate(variables);
		js = js.append("\n" + template2.asString());

		final TextTemplate template3 = new PackageTextTemplate(HomePage.class,
				"script/draggableHandle/dragCard.js");
		template3.interpolate(variables);
		js = js.append("\n" + template3.asString());

		response.render(JavaScriptHeaderItem.forScript(js.toString(), null));
		try
		{
			template1.close();
		}
		catch (final IOException e)
		{
			CardMoveBehavior.LOGGER.error(
					"unable to close template1 in CardMoveBehavior#renderHead()!", e);
		}
		try
		{
			template2.close();
		}
		catch (final IOException e)
		{
			CardMoveBehavior.LOGGER.error(
					"unable to close template2 in CardMoveBehavior#renderHead()!", e);
		}
		try
		{
			template3.close();
		}
		catch (final IOException e)
		{
			CardMoveBehavior.LOGGER.error(
					"unable to close template3 in CardMoveBehavior#renderHead()!", e);
		}
	}

	@Required
	public void setPersistenceService(final PersistenceService _persistenceService)
	{
		this.persistenceService = _persistenceService;
	}

	public String getUuidValidForJs()
	{
		return this.uuidValidForJs;
	}

}
