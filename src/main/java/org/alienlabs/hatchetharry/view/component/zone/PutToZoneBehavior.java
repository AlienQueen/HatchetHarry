package org.alienlabs.hatchetharry.view.component.zone;

import java.io.IOException;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.alienlabs.hatchetharry.HatchetHarrySession;
import org.alienlabs.hatchetharry.model.CardZone;
import org.alienlabs.hatchetharry.model.MagicCard;
import org.alienlabs.hatchetharry.model.Player;
import org.alienlabs.hatchetharry.model.channel.CardZoneMoveCometChannel;
import org.alienlabs.hatchetharry.model.channel.CardZoneMoveNotifier;
import org.alienlabs.hatchetharry.model.channel.ConsoleLogCometChannel;
import org.alienlabs.hatchetharry.model.channel.consolelog.AbstractConsoleLogStrategy;
import org.alienlabs.hatchetharry.model.channel.consolelog.ConsoleLogStrategy;
import org.alienlabs.hatchetharry.model.channel.consolelog.ConsoleLogType;
import org.alienlabs.hatchetharry.service.PersistenceService;
import org.alienlabs.hatchetharry.view.clientsideutil.EventBusPostService;
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
	static final Logger LOGGER = LoggerFactory.getLogger(PutToZoneBehavior.class);
	private static final long serialVersionUID = 1L;
	private final CardZone sourceZone;
	private final Player player;
	private final boolean isReveal;
	@SpringBean
	private PersistenceService persistenceService;
	private UUID uuidToLookFor;
	private CardZone targetZone;

	public PutToZoneBehavior(final CardZone _sourceZone, final Player _player,
			final boolean _isReveal)
	{
		super();
		Injector.get().inject(this);

		this.sourceZone = _sourceZone;
		this.player = _player;
		this.isReveal = _isReveal;
	}

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
			this.targetZone = CardZone.valueOf(request.getParameter("targetZone").toUpperCase(
					Locale.ENGLISH));
		}
		catch (final IllegalArgumentException ex)
		{
			PutToZoneBehavior.LOGGER.error(
					"wrong zone " + request.getParameter("targetZone") + "!", ex);
			return;
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
			// $CASES-OMITTED$
			default :
				throw new UnsupportedOperationException();
		}
		this.persistenceService.mergePlayer(ownerPlayer);

		final String ownerPlayerName = ownerPlayer.getName();

		switch (this.targetZone)
		{
			case HAND :
				ownerPlayer.setHandDisplayed(true);
				this.persistenceService.mergePlayer(ownerPlayer);
				break;
			case GRAVEYARD :
				ownerPlayer.setGraveyardDisplayed(true);
				this.persistenceService.mergePlayer(ownerPlayer);
				break;
			case EXILE :
				ownerPlayer.setExileDisplayed(true);
				this.persistenceService.mergePlayer(ownerPlayer);
				break;
			case BATTLEFIELD :
				break;
			case LIBRARY :
				break;
			default :
				throw new UnsupportedOperationException();
		}

		final CardZoneMoveCometChannel czmcc = new CardZoneMoveCometChannel(this.sourceZone,
				this.targetZone, card, HatchetHarrySession.get().getPlayer().getId(), card
						.getDeck().getPlayerId(), card.getGameId(), card.getDeck(),
				ownerPlayer.getSide(), this.isReveal);

		final CardZoneMoveNotifier czmn = new CardZoneMoveNotifier(this.sourceZone,
				this.targetZone, card, HatchetHarrySession.get().getPlayer().getName(),
				ownerPlayerName);

		final List<BigInteger> allPlayersInGame = this.persistenceService
				.giveAllPlayersFromGame(HatchetHarrySession.get().getGameId());

		final ConsoleLogStrategy logger = AbstractConsoleLogStrategy.chooseStrategy(
				ConsoleLogType.ZONE_MOVE, this.sourceZone, this.targetZone, null, card.getTitle(),
				HatchetHarrySession.get().getPlayer().getName(), null, null, null, null,
				HatchetHarrySession.get().getGameId());

		// post a message for all players in the game
		EventBusPostService.post(allPlayersInGame, czmcc, czmn, new ConsoleLogCometChannel(logger));
	}

	@Override
	public void renderHead(final Component component, final IHeaderResponse response)
	{
		super.renderHead(component, response);

		final HashMap<String, Object> variables = new HashMap<String, Object>();
		variables.put("url", this.getCallbackUrl());
		variables.put("zone", this.sourceZone.toString());

		if (this.isReveal)
		{
			variables.put("Player", this.player.getId().toString());
			variables.put("reveal", "Reveal");
		}
		else
		{
			variables.put("Player", "");
			variables.put("reveal", "");
		}

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
