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
import org.alienlabs.hatchetharry.model.channel.PutToHandFromBattlefieldCometChannel;
import org.alienlabs.hatchetharry.model.channel.consolelog.AbstractConsoleLogStrategy;
import org.alienlabs.hatchetharry.model.channel.consolelog.ConsoleLogStrategy;
import org.alienlabs.hatchetharry.model.channel.consolelog.ConsoleLogType;
import org.alienlabs.hatchetharry.service.PersistenceService;
import org.alienlabs.hatchetharry.view.clientsideutil.EventBusPostService;
import org.alienlabs.hatchetharry.view.clientsideutil.JavaScriptUtils;
import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.injection.Injector;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

@edu.umd.cs.findbugs.annotations.SuppressWarnings(value = { "SE_INNER_CLASS",
		"SIC_INNER_SHOULD_BE_STATIC_ANON" }, justification = "In Wicket, serializable inner classes are common. And as the parent Page is serialized as well, this is no concern. This is no bad practice in Wicket")
public class PutToHandFromBattlefieldBehavior extends AbstractDefaultAjaxBehavior
{
	private static final long serialVersionUID = 1L;

	private static final Logger LOGGER = LoggerFactory
		.getLogger(PutToHandFromBattlefieldBehavior.class);
	private final UUID uuid;

	@SpringBean
	private PersistenceService persistenceService;

	public PutToHandFromBattlefieldBehavior(final UUID _uuid)
	{
		Injector.get().inject(this);
		this.uuid = _uuid;
	}

	@Override
	protected void respond(final AjaxRequestTarget target)
	{
		PutToHandFromBattlefieldBehavior.LOGGER.info("respond");

		final String uniqueid = this.uuid.toString();
		MagicCard mc = null;

		try
		{
			mc = this.persistenceService.getCardFromUuid(UUID.fromString(uniqueid));
		}
		catch (final IllegalArgumentException e)
		{
			PutToHandFromBattlefieldBehavior.LOGGER.error("error parsing UUID of card", e);
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
		PutToHandFromBattlefieldBehavior.LOGGER.info("playerId in respond(): "
			+ session.getPlayer().getId());

		mc.setZone(CardZone.HAND);
		mc.setTapped(false);
		this.persistenceService.updateCard(mc);

		final boolean isHandDisplayed = this.persistenceService.getPlayer(
			session.getPlayer().getId()).isHandDisplayed();
		if (isHandDisplayed)
		{
			JavaScriptUtils.updateHand(target);
		}

		final Long gameId = session.getPlayer().getGame().getId();

		final Player p = this.persistenceService.getPlayer(session.getPlayer().getId());
		final Deck d = p.getDeck();
		final List<MagicCard> hand = d.reorderMagicCards(this.persistenceService
			.getAllCardsInHandForAGameAndAPlayer(gameId, p.getId(), d.getDeckId()));
		this.persistenceService.saveOrUpdateAllMagicCards(hand);
		final List<MagicCard> battlefield = d.reorderMagicCards(this.persistenceService
			.getAllCardsInBattlefieldForAGameAndAPlayer(gameId, p.getId(), d.getDeckId()));
		this.persistenceService.saveOrUpdateAllMagicCards(battlefield);

		final List<BigInteger> allPlayersInGame = PutToHandFromBattlefieldBehavior.this.persistenceService
			.giveAllPlayersFromGame(gameId);

		final ConsoleLogStrategy logger = AbstractConsoleLogStrategy.chooseStrategy(
			ConsoleLogType.ZONE_MOVE, CardZone.BATTLEFIELD, CardZone.HAND, null, mc.getTitle(),
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

			final PutToHandFromBattlefieldCometChannel pthfbcc = new PutToHandFromBattlefieldCometChannel(
				gameId, mc, session.getPlayer().getName(), targetPlayerName, targetPlayer.getId(),
				targetDeckId, (allPlayersInGame.get(i).longValue() == targetPlayer.getId()
					.longValue()));
			final NotifierCometChannel ncc = new NotifierCometChannel(
				NotifierAction.PUT_CARD_TO_HAND_FROM_BATTLEFIELD_ACTION, gameId, session
					.getPlayer().getId(), session.getPlayer().getName(), "", "", mc.getTitle(),
				null, targetPlayerName);

			if (allPlayersInGame.get(i).longValue() == targetPlayer.getId().longValue())
			{
				targetPlayer.setHandDisplayed(true);
				this.persistenceService.mergePlayer(targetPlayer);
			}

			EventBusPostService.post(playerToWhomToSend, pthfbcc, ncc, new ConsoleLogCometChannel(
				logger));
		}
	}

	@Required
	public void setPersistenceService(final PersistenceService _persistenceService)
	{
		this.persistenceService = _persistenceService;
	}

}
