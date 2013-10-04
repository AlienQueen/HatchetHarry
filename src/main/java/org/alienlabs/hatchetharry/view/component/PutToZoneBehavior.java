package org.alienlabs.hatchetharry.view.component;

import java.io.IOException;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.alienlabs.hatchetharry.HatchetHarryApplication;
import org.alienlabs.hatchetharry.HatchetHarrySession;
import org.alienlabs.hatchetharry.model.CardZone;
import org.alienlabs.hatchetharry.model.MagicCard;
import org.alienlabs.hatchetharry.model.Player;
import org.alienlabs.hatchetharry.model.channel.CardZoneMoveCometChannel;
import org.alienlabs.hatchetharry.model.channel.CardZoneMoveNotifier;
import org.alienlabs.hatchetharry.service.PersistenceService;
import org.alienlabs.hatchetharry.view.page.HomePage;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.injection.Injector;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.protocol.http.servlet.ServletWebRequest;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.template.PackageTextTemplate;
import org.apache.wicket.util.template.TextTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

public class PutToZoneBehavior extends AbstractDefaultAjaxBehavior
{
	private static final long serialVersionUID = 1L;
	static final Logger LOGGER = LoggerFactory.getLogger(PutToZoneBehavior.class);

	@SpringBean
	private PersistenceService persistenceService;

	private final CardZone sourceZone;
	private UUID uuidToLookFor;
	private CardZone targetZone;

	public PutToZoneBehavior(final CardZone _sourceZone)
	{
		super();
		Injector.get().inject(this);
		this.sourceZone = _sourceZone;
	}

	@SuppressWarnings("incomplete-switch")
	@Override
	protected void respond(final AjaxRequestTarget target)
	{
		final ServletWebRequest servletWebRequest = (ServletWebRequest)target.getPage()
				.getRequest();
		final HttpServletRequest request = servletWebRequest.getContainerRequest();

		try
		{
			this.uuidToLookFor = UUID.fromString(request.getParameter("card"));
		}
		catch (final IllegalArgumentException ex)
		{
			PutToZoneBehavior.LOGGER.error("No card with UUID= " + request.getParameter("card")
					+ " found!", ex);
		}

		final MagicCard card = this.persistenceService.getCardFromUuid(this.uuidToLookFor);

		if (null == card)
		{
			PutToZoneBehavior.LOGGER.error("UUID " + this.uuidToLookFor
					+ " retrieved no MagicCard!");
			return;
		}

		if (!this.sourceZone.equals(card.getZone()))
		{
			return;
		}

		try
		{
			this.targetZone = CardZone.valueOf(request.getParameter("targetZone").toUpperCase());
		}
		catch (final IllegalArgumentException ex)
		{
			PutToZoneBehavior.LOGGER.error(
					"wrong zone " + request.getParameter("targetZone") + "!", ex);
		}

		final Player ownerPlayer = this.persistenceService.getPlayer(card.getDeck().getPlayerId());

		// TODO add more source zone one day
		switch (this.sourceZone)
		{
			case HAND :
				ownerPlayer.setDefaultTargetZoneForHand(this.targetZone);
				break;
			case GRAVEYARD :
				ownerPlayer.setDefaultTargetZoneForGraveyard(this.targetZone);
				break;
			case EXILE :
				ownerPlayer.setDefaultTargetZoneForExile(this.targetZone);
				break;
			default :
				throw new UnsupportedOperationException();
		}
		this.persistenceService.updatePlayer(ownerPlayer);

		final String ownerPlayerName = ownerPlayer.getName();

		switch (this.targetZone)
		{
			case HAND :
				ownerPlayer.setHandDisplayed(true);
				this.persistenceService.updatePlayer(ownerPlayer);
				break;
			case GRAVEYARD :
				ownerPlayer.setGraveyardDisplayed(true);
				this.persistenceService.updatePlayer(ownerPlayer);
				break;
			case EXILE :
				ownerPlayer.setExileDisplayed(true);
				this.persistenceService.updatePlayer(ownerPlayer);
				break;
		}

		final CardZoneMoveCometChannel czmcc = new CardZoneMoveCometChannel(this.sourceZone,
				this.targetZone, card, card.getDeck().getPlayerId(), card.getGameId(), card
						.getDeck().getDeckId());

		final CardZoneMoveNotifier czmn = new CardZoneMoveNotifier(this.sourceZone,
				this.targetZone, card, HatchetHarrySession.get().getPlayer().getName(),
				ownerPlayerName);

		final List<BigInteger> allPlayersInGame = this.persistenceService
				.giveAllPlayersFromGame(HatchetHarrySession.get().getGameId());

		// post a message for all players in the game
		for (int i = 0; i < allPlayersInGame.size(); i++)
		{
			final Long player = allPlayersInGame.get(i).longValue();
			final String pageUuid = HatchetHarryApplication.getCometResources().get(player);
			PutToZoneBehavior.LOGGER.info("pageUuid: " + pageUuid);

			// For unit tests
			try
			{
				HatchetHarryApplication.get().getEventBus().post(czmcc, pageUuid);
				HatchetHarryApplication.get().getEventBus().post(czmn, pageUuid);
			}
			catch (final NullPointerException e)
			{
				// For tests only, so do nothing
			}
		}
	}

	@Override
	public void renderHead(final Component component, final IHeaderResponse response)
	{
		super.renderHead(component, response);

		final HashMap<String, Object> variables = new HashMap<String, Object>();
		variables.put("url", this.getCallbackUrl());
		variables.put("zone", this.sourceZone.toString());

		final TextTemplate template = new PackageTextTemplate(HomePage.class,
				"script/playCard/putToZone.js");
		template.interpolate(variables);

		response.render(JavaScriptHeaderItem.forScript(template.asString(), null));
		try
		{
			template.close();
		}
		catch (final IOException e)
		{
			PutToZoneBehavior.LOGGER.error(
					"unable to close template in PutToZoneBehavior#renderHead()!", e);
		}
	}

	@Required
	public void setPersistenceService(final PersistenceService _persistenceService)
	{
		this.persistenceService = _persistenceService;
	}

}
