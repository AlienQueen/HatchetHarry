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
import org.alienlabs.hatchetharry.model.channel.PutToHandFromBattlefieldCometChannel;
import org.alienlabs.hatchetharry.service.PersistenceService;
import org.alienlabs.hatchetharry.view.clientsideutil.JavaScriptUtils;
import org.alienlabs.hatchetharry.view.page.HomePage;
import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.atmosphere.EventBus;
import org.apache.wicket.injection.Injector;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

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

		PutToHandFromBattlefieldBehavior.LOGGER.info("playerId in respond(): "
				+ HatchetHarrySession.get().getPlayer().getId());

		mc.setZone(CardZone.HAND);
		this.persistenceService.saveCard(mc);

		final boolean isHandDisplayed = HatchetHarrySession.get().isHandDisplayed();
		if (isHandDisplayed)
		{
			final HandComponent handToUpdate = new HandComponent("gallery");
			handToUpdate.setOutputMarkupId(true);
			((HomePage)target.getPage()).getGalleryParent().addOrReplace(handToUpdate);
			target.add(((HomePage)target.getPage()).getGalleryParent());

			target.appendJavaScript(JavaScriptUtils.REACTIVATE_HAND_JAVASCRIPT_COMPONENT);
		}

		final Long gameId = PutToHandFromBattlefieldBehavior.this.persistenceService
				.getPlayer(HatchetHarrySession.get().getPlayer().getId()).getGames().iterator()
				.next().getId();
		final List<BigInteger> allPlayersInGame = PutToHandFromBattlefieldBehavior.this.persistenceService
				.giveAllPlayersFromGame(gameId);

		for (int i = 0; i < allPlayersInGame.size(); i++)
		{
			final Long playerToWhomToSend = allPlayersInGame.get(i).longValue();
			final String pageUuid = HatchetHarryApplication.getCometResources().get(
					playerToWhomToSend);

			final PutToHandFromBattlefieldCometChannel pthfbcc = new PutToHandFromBattlefieldCometChannel(
					gameId);
			final NotifierCometChannel ncc = new NotifierCometChannel(
					NotifierAction.PUT_CARD_TO_HAND_FROM_BATTLEFIELD, gameId, HatchetHarrySession
							.get().getPlayer().getId(), HatchetHarrySession.get().getPlayer()
							.getName(), "", "", mc.getTitle(), null);

			EventBus.get().post(pthfbcc, pageUuid);
			EventBus.get().post(ncc, pageUuid);
		}

		// return;
		// }
		// PutToHandFromBattlefieldBehavior.LOGGER.info("could not remove card uuid= "
		// + mc.getUuid());
		// return;
		// }
		// PutToHandFromBattlefieldBehavior.LOGGER.info("could not find CardPanel uuid= "
		// + mc.getUuid() + " in allCardsInBattlefield from session");
		// }
	}

	@Required
	public void setPersistenceService(final PersistenceService _persistenceService)
	{
		this.persistenceService = _persistenceService;
	}

}
