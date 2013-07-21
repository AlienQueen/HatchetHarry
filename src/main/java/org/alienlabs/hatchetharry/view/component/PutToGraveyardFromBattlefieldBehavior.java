package org.alienlabs.hatchetharry.view.component;

import java.math.BigInteger;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.alienlabs.hatchetharry.HatchetHarryApplication;
import org.alienlabs.hatchetharry.HatchetHarrySession;
import org.alienlabs.hatchetharry.model.CardZone;
import org.alienlabs.hatchetharry.model.Deck;
import org.alienlabs.hatchetharry.model.MagicCard;
import org.alienlabs.hatchetharry.model.Player;
import org.alienlabs.hatchetharry.model.channel.NotifierAction;
import org.alienlabs.hatchetharry.model.channel.NotifierCometChannel;
import org.alienlabs.hatchetharry.model.channel.PutToGraveyardCometChannel;
import org.alienlabs.hatchetharry.service.PersistenceService;
import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.injection.Injector;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

public class PutToGraveyardFromBattlefieldBehavior extends AbstractDefaultAjaxBehavior
{
	private static final long serialVersionUID = 1L;

	private static final Logger LOGGER = LoggerFactory
			.getLogger(PutToGraveyardFromBattlefieldBehavior.class);
	private final UUID uuid;

	@SpringBean
	private PersistenceService persistenceService;

	public PutToGraveyardFromBattlefieldBehavior(final UUID _uuid)
	{
		Injector.get().inject(this);
		this.uuid = _uuid;
	}

	@Override
	protected void respond(final AjaxRequestTarget target)
	{
		PutToGraveyardFromBattlefieldBehavior.LOGGER.info("respond");

		final String uniqueid = this.uuid.toString();
		MagicCard mc = null;

		Date beginDate = new Date();
		try
		{
			mc = this.persistenceService.getCardFromUuid(UUID.fromString(uniqueid));
		}
		catch (final IllegalArgumentException e)
		{
			PutToGraveyardFromBattlefieldBehavior.LOGGER.error("error parsing UUID of card", e);
		}
		Date endDate = new Date();
		PutToGraveyardFromBattlefieldBehavior.LOGGER.info("getCardFromUuid: "
				+ Long.toString(endDate.getTime() - beginDate.getTime()) + "msec");

		if (null == mc)
		{
			return;
		}

		final HatchetHarrySession session = HatchetHarrySession.get();
		PutToGraveyardFromBattlefieldBehavior.LOGGER.info("playerId in respond(): "
				+ session.getPlayer().getId());

		mc.setZone(CardZone.GRAVEYARD);
		mc.setTapped(false);
		beginDate = new Date();
		this.persistenceService.updateCard(mc);
		endDate = new Date();
		PutToGraveyardFromBattlefieldBehavior.LOGGER.info("updateCard: "
				+ Long.toString(endDate.getTime() - beginDate.getTime()) + "msec");


		final Long gameId = session.getPlayer().getGame().getId();

		beginDate = new Date();
		final Player p = this.persistenceService.getPlayer(session.getPlayer().getId());
		endDate = new Date();
		PutToGraveyardFromBattlefieldBehavior.LOGGER.info("getPlayer: "
				+ Long.toString(endDate.getTime() - beginDate.getTime()) + "msec");

		final Deck d = p.getDeck();
		beginDate = new Date();
		final List<MagicCard> graveyard = d.reorderMagicCards(this.persistenceService
				.getAllCardsInGraveyardForAGameAndAPlayer(gameId, p.getId(), d.getDeckId()));
		endDate = new Date();
		PutToGraveyardFromBattlefieldBehavior.LOGGER.info("reorderMagicCards graveyard: "
				+ Long.toString(endDate.getTime() - beginDate.getTime()) + "msec");

		beginDate = new Date();
		this.persistenceService.updateAllMagicCards(graveyard);
		endDate = new Date();
		PutToGraveyardFromBattlefieldBehavior.LOGGER.info("updateAllMagicCards graveyard: "
				+ Long.toString(endDate.getTime() - beginDate.getTime()) + "msec");

		beginDate = new Date();
		final List<MagicCard> battlefield = d.reorderMagicCards(this.persistenceService
				.getAllCardsInBattlefieldForAGameAndAPlayer(gameId, p.getId(), d.getDeckId()));
		endDate = new Date();
		PutToGraveyardFromBattlefieldBehavior.LOGGER.info("reorderMagicCards battlefield: "
				+ Long.toString(endDate.getTime() - beginDate.getTime()) + "msec");

		beginDate = new Date();
		this.persistenceService.updateAllMagicCards(battlefield);
		endDate = new Date();
		PutToGraveyardFromBattlefieldBehavior.LOGGER.info("reorderMagicCards battlefield: "
				+ Long.toString(endDate.getTime() - beginDate.getTime()) + "msec");

		beginDate = new Date();
		final List<BigInteger> allPlayersInGame = PutToGraveyardFromBattlefieldBehavior.this.persistenceService
				.giveAllPlayersFromGame(gameId);
		endDate = new Date();
		PutToGraveyardFromBattlefieldBehavior.LOGGER.info("giveAllPlayersFromGame: "
				+ Long.toString(endDate.getTime() - beginDate.getTime()) + "msec");

		for (int i = 0; i < allPlayersInGame.size(); i++)
		{
			final Long playerToWhomToSend = allPlayersInGame.get(i).longValue();

			final String _pageUuid = HatchetHarryApplication.getCometResources().get(
					playerToWhomToSend);

			beginDate = new Date();
			final Player targetPlayer = this.persistenceService.getPlayer(mc.getDeck()
					.getPlayerId());
			endDate = new Date();
			PutToGraveyardFromBattlefieldBehavior.LOGGER.info("getPlayer: "
					+ Long.toString(endDate.getTime() - beginDate.getTime()) + "msec");

			final String targetPlayerName = targetPlayer.getName();
			final Long targetDeckId = mc.getDeck().getDeckId();

			if (allPlayersInGame.get(i).longValue() == targetPlayer.getId().longValue())
			{
				targetPlayer.setGraveyardDisplayed(true);
				beginDate = new Date();
				this.persistenceService.updatePlayer(targetPlayer);
				endDate = new Date();
				PutToGraveyardFromBattlefieldBehavior.LOGGER.info("updatePlayer: "
						+ Long.toString(endDate.getTime() - beginDate.getTime()) + "msec");
			}

			beginDate = new Date();
			final PutToGraveyardCometChannel _ptgcc = new PutToGraveyardCometChannel(gameId, mc,
					session.getPlayer().getName(), targetPlayerName, targetPlayer.getId(),
					targetDeckId, (allPlayersInGame.get(i).longValue() == targetPlayer.getId()
							.longValue()));
			final NotifierCometChannel _ncc = new NotifierCometChannel(
					NotifierAction.PUT_CARD_TO_GRAVGEYARD_FROM_BATTLEFIELD_ACTION, gameId, session
							.getPlayer().getId(), session.getPlayer().getName(), "", "",
					mc.getTitle(), null, targetPlayerName);
			endDate = new Date();
			PutToGraveyardFromBattlefieldBehavior.LOGGER.info("build comet channel: "
					+ Long.toString(endDate.getTime() - beginDate.getTime()) + "msec");

			beginDate = new Date();
			HatchetHarryApplication.get().getEventBus().post(_ptgcc, _pageUuid);
			HatchetHarryApplication.get().getEventBus().post(_ncc, _pageUuid);
			endDate = new Date();
			PutToGraveyardFromBattlefieldBehavior.LOGGER.info("post: "
					+ Long.toString(endDate.getTime() - beginDate.getTime()) + "msec");
		}
	}

	@Required
	public void setPersistenceService(final PersistenceService _persistenceService)
	{
		this.persistenceService = _persistenceService;
	}

}
