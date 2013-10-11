package org.alienlabs.hatchetharry.view.component;

import java.math.BigInteger;
import java.util.List;
import java.util.UUID;

import org.alienlabs.hatchetharry.HatchetHarryApplication;
import org.alienlabs.hatchetharry.HatchetHarrySession;
import org.alienlabs.hatchetharry.model.Deck;
import org.alienlabs.hatchetharry.model.MagicCard;
import org.alienlabs.hatchetharry.model.Player;
import org.alienlabs.hatchetharry.model.channel.DestroyTokenCometChannel;
import org.alienlabs.hatchetharry.model.channel.NotifierAction;
import org.alienlabs.hatchetharry.model.channel.NotifierCometChannel;
import org.alienlabs.hatchetharry.service.PersistenceService;
import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.injection.Injector;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

public class DestroyTokenBehavior extends AbstractDefaultAjaxBehavior
{
	private static final long serialVersionUID = 1L;

	private static final Logger LOGGER = LoggerFactory.getLogger(DestroyTokenBehavior.class);
	private final UUID uuid;

	@SpringBean
	private PersistenceService persistenceService;

	public DestroyTokenBehavior(final UUID _uuid)
	{
		Injector.get().inject(this);
		this.uuid = _uuid;
	}

	@Override
	protected void respond(final AjaxRequestTarget target)
	{
		DestroyTokenBehavior.LOGGER.info("respond");

		final String uniqueid = this.uuid.toString();
		MagicCard mc = null;

		try
		{
			mc = this.persistenceService.getCardFromUuid(UUID.fromString(uniqueid));
		}
		catch (final IllegalArgumentException e)
		{
			DestroyTokenBehavior.LOGGER.error("error parsing UUID of card", e);
		}

		if (null == mc)
		{
			return;
		}

		final HatchetHarrySession session = HatchetHarrySession.get();
		DestroyTokenBehavior.LOGGER.info("playerId in respond(): " + session.getPlayer().getId());
		DestroyTokenBehavior.LOGGER.info("mc.getTitle(): " + mc.getTitle());

		final String tokenName = mc.getToken().getCreatureTypes();
		final Player targetPlayer = this.persistenceService.getPlayer(mc.getDeck().getPlayerId());

		this.persistenceService.deleteCardAndToken(mc);

		final Long gameId = session.getPlayer().getGame().getId();
		final Player p = this.persistenceService.getPlayer(session.getPlayer().getId());
		final Deck d = p.getDeck();

		// TODO: reorder?
		final List<MagicCard> battlefield = this.persistenceService
				.getAllCardsInBattlefieldForAGameAndAPlayer(gameId, p.getId(), d.getDeckId());

		this.persistenceService.updateAllMagicCards(battlefield);

		final List<BigInteger> allPlayersInGame = DestroyTokenBehavior.this.persistenceService
				.giveAllPlayersFromGame(gameId);

		for (int i = 0; i < allPlayersInGame.size(); i++)
		{
			final Long playerToWhomToSend = allPlayersInGame.get(i).longValue();

			final String _pageUuid = HatchetHarryApplication.getCometResources().get(
					playerToWhomToSend);

			final DestroyTokenCometChannel dtcc = new DestroyTokenCometChannel(mc, gameId);
			final NotifierCometChannel _ncc = new NotifierCometChannel(
					NotifierAction.DESTROY_TOKEN, gameId, session.getPlayer().getId(), session
							.getPlayer().getName(), "", "", tokenName, null, targetPlayer.getName());

			// For unit tests
			try
			{
				HatchetHarryApplication.get().getEventBus().post(dtcc, _pageUuid);
				HatchetHarryApplication.get().getEventBus().post(_ncc, _pageUuid);
			}
			catch (final NullPointerException e)
			{
				// Nothing to do in unit tests
			}
		}
	}

	@Required
	public void setPersistenceService(final PersistenceService _persistenceService)
	{
		this.persistenceService = _persistenceService;
	}

}