package org.alienlabs.hatchetharry.view.component;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.alienlabs.hatchetharry.HatchetHarrySession;
import org.alienlabs.hatchetharry.model.CardZone;
import org.alienlabs.hatchetharry.model.Deck;
import org.alienlabs.hatchetharry.model.MagicCard;
import org.alienlabs.hatchetharry.model.Player;
import org.alienlabs.hatchetharry.model.channel.ConsoleLogCometChannel;
import org.alienlabs.hatchetharry.model.channel.NotifierAction;
import org.alienlabs.hatchetharry.model.channel.NotifierCometChannel;
import org.alienlabs.hatchetharry.model.channel.PutToExileFromBattlefieldCometChannel;
import org.alienlabs.hatchetharry.model.channel.consolelog.AbstractConsoleLogStrategy;
import org.alienlabs.hatchetharry.model.channel.consolelog.ConsoleLogStrategy;
import org.alienlabs.hatchetharry.model.channel.consolelog.ConsoleLogType;
import org.alienlabs.hatchetharry.service.PersistenceService;
import org.alienlabs.hatchetharry.view.clientsideutil.EventBusPostService;
import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.injection.Injector;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

public class PutToExileFromBattlefieldBehavior extends AbstractDefaultAjaxBehavior
{
	private static final long serialVersionUID = 1L;

	private static final Logger LOGGER = LoggerFactory
		.getLogger(PutToExileFromBattlefieldBehavior.class);
	private final UUID uuid;

	@SpringBean
	private PersistenceService persistenceService;

	public PutToExileFromBattlefieldBehavior(final UUID _uuid)
	{
		Injector.get().inject(this);
		this.uuid = _uuid;
	}

	@Override
	protected void respond(final AjaxRequestTarget target)
	{
		PutToExileFromBattlefieldBehavior.LOGGER.info("respond");

		final String uniqueid = this.uuid.toString();
		MagicCard mc = null;

		try
		{
			mc = this.persistenceService.getCardFromUuid(UUID.fromString(uniqueid));
		}
		catch (final IllegalArgumentException e)
		{
			PutToExileFromBattlefieldBehavior.LOGGER.error("error parsing UUID of card", e);
		}

		if (null == mc)
		{
			return;
		}

		if (!CardZone.BATTLEFIELD.equals(mc.getZone()))
		{
			return;
		}

		final HatchetHarrySession session = HatchetHarrySession.get();
		PutToExileFromBattlefieldBehavior.LOGGER.info("playerId in respond(): "
			+ session.getPlayer().getId());

		mc.setZone(CardZone.EXILE);
		mc.setTapped(false);
		this.persistenceService.updateCard(mc);

		final Long gameId = session.getPlayer().getGame().getId();
		final Player p = this.persistenceService.getPlayer(session.getPlayer().getId());
		final Deck d = p.getDeck();
		final List<MagicCard> exile = d.reorderMagicCards(this.persistenceService
			.getAllCardsInExileForAGameAndAPlayer(gameId, p.getId(), d.getDeckId()));
		this.persistenceService.saveOrUpdateAllMagicCards(exile);
		final List<MagicCard> battlefield = d.reorderMagicCards(this.persistenceService
			.getAllCardsInBattlefieldForAGameAndAPlayer(gameId, p.getId(), d.getDeckId()));
		this.persistenceService.saveOrUpdateAllMagicCards(battlefield);

		final List<BigInteger> allPlayersInGame = PutToExileFromBattlefieldBehavior.this.persistenceService
			.giveAllPlayersFromGame(gameId);

		final ConsoleLogStrategy logger = AbstractConsoleLogStrategy.chooseStrategy(
			ConsoleLogType.ZONE_MOVE, CardZone.BATTLEFIELD, CardZone.EXILE, null, mc.getTitle(),
			HatchetHarrySession.get().getPlayer().getName(), null, null, null, null, gameId);

		for (int i = 0; i < allPlayersInGame.size(); i++)
		{
			final int index = i;
			final List<BigInteger> playerToWhomToSend = new ArrayList<BigInteger>()
			{
				{
					this.add(allPlayersInGame.get(index));
				}
			};

			final Player targetPlayer = this.persistenceService.getPlayer(mc.getDeck()
				.getPlayerId());
			final String targetPlayerName = targetPlayer.getName();
			final Long targetDeckId = mc.getDeck().getDeckId();

			final PutToExileFromBattlefieldCometChannel ptefbcc = new PutToExileFromBattlefieldCometChannel(
				gameId, mc, session.getPlayer().getName(), targetPlayerName, targetPlayer.getId(),
				targetDeckId, (allPlayersInGame.get(i).longValue() == targetPlayer.getId()
					.longValue()));
			final NotifierCometChannel ncc = new NotifierCometChannel(
				NotifierAction.PUT_CARD_TO_EXILE_FROM_BATTLEFIELD_ACTION, gameId, session
					.getPlayer().getId(), session.getPlayer().getName(), "", "", mc.getTitle(),
				null, targetPlayerName);
			EventBusPostService.post(playerToWhomToSend, ptefbcc, ncc, new ConsoleLogCometChannel(
				logger));

			if (allPlayersInGame.get(i).longValue() == targetPlayer.getId().longValue())
			{
				targetPlayer.setExileDisplayed(true);
				this.persistenceService.mergePlayer(targetPlayer);
			}
		}
	}

	@Required
	public void setPersistenceService(final PersistenceService _persistenceService)
	{
		this.persistenceService = _persistenceService;
	}

}
