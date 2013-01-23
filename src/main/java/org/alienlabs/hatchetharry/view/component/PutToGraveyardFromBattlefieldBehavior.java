package org.alienlabs.hatchetharry.view.component;

import java.math.BigInteger;
import java.util.List;
import java.util.UUID;

import org.alienlabs.hatchetharry.HatchetHarryApplication;
import org.alienlabs.hatchetharry.HatchetHarrySession;
import org.alienlabs.hatchetharry.model.CardZone;
import org.alienlabs.hatchetharry.model.MagicCard;
import org.alienlabs.hatchetharry.model.channel.NotifierAction;
import org.alienlabs.hatchetharry.model.channel.NotifierCometChannel;
import org.alienlabs.hatchetharry.model.channel.PutToGraveyardCometChannel;
import org.alienlabs.hatchetharry.service.PersistenceService;
import org.alienlabs.hatchetharry.view.clientsideutil.JavaScriptUtils;
import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.atmosphere.EventBus;
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

		try
		{
			mc = this.persistenceService.getCardFromUuid(UUID.fromString(uniqueid));
		}
		catch (final IllegalArgumentException e)
		{
			PutToGraveyardFromBattlefieldBehavior.LOGGER.error("error parsing UUID of card", e);
		}

		if (null == mc)
		{
			return;
		}

		PutToGraveyardFromBattlefieldBehavior.LOGGER.info("playerId in respond(): "
				+ HatchetHarrySession.get().getPlayer().getId());

		mc.setZone(CardZone.GRAVEYARD);
		this.persistenceService.saveCard(mc);

		final Long gameId = HatchetHarrySession.get().getPlayer().getGames().iterator().next()
				.getId();
		final List<BigInteger> allPlayersInGame = PutToGraveyardFromBattlefieldBehavior.this.persistenceService
				.giveAllPlayersFromGame(gameId);

		final boolean isGraveyardDisplayed = HatchetHarrySession.get().isGraveyardDisplayed();
		if (isGraveyardDisplayed)
		{
			JavaScriptUtils.updateGraveyard(target);
		}

		for (int i = 0; i < allPlayersInGame.size(); i++)
		{
			final Long playerToWhomToSend = allPlayersInGame.get(i).longValue();
			final String _pageUuid = HatchetHarryApplication.getCometResources().get(
					playerToWhomToSend);

			final PutToGraveyardCometChannel _ptgcc = new PutToGraveyardCometChannel(gameId, mc);
			final NotifierCometChannel _ncc = new NotifierCometChannel(
					NotifierAction.PUT_CARD_TO_GRAVGEYARD_FROM_BATTLEFIELD, gameId,
					HatchetHarrySession.get().getPlayer().getId(), HatchetHarrySession.get()
							.getPlayer().getName(), "", "", mc.getTitle(), null);

			EventBus.get().post(_ptgcc, _pageUuid);
			EventBus.get().post(_ncc, _pageUuid);
		}
	}

	@Required
	public void setPersistenceService(final PersistenceService _persistenceService)
	{
		this.persistenceService = _persistenceService;
	}

}
