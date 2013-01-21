package org.alienlabs.hatchetharry.view.component;

import java.math.BigInteger;
import java.util.ArrayList;
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
import org.alienlabs.hatchetharry.view.page.HomePage;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.atmosphere.EventBus;
import org.apache.wicket.atmosphere.Subscribe;
import org.apache.wicket.injection.Injector;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

public class PutToGraveyardFromBattlefieldBehavior extends AbstractDefaultAjaxBehavior
{
	private static final long serialVersionUID = 1L;

	private static final Logger LOGGER = LoggerFactory.getLogger(PutToGraveyardFromBattlefieldBehavior.class);
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
			PutToGraveyardFromBattlefieldBehavior.LOGGER.error("error parsing UUID of moved card", e);
		}

		if (null == mc)
		{
			return;
		}

		PutToGraveyardFromBattlefieldBehavior.LOGGER.info("playerId in respond(): "
				+ HatchetHarrySession.get().getPlayer().getId());

		mc.setZone(CardZone.GRAVEYARD);
		this.persistenceService.saveCard(mc);

		final List<CardPanel> allCardsInBattlefield = HatchetHarrySession.get()
				.getAllCardsInBattleField();

		for (final CardPanel cp : allCardsInBattlefield)
		{
			if (mc.getUuid().equals(cp.getUuid().toString()))
			{
				if (allCardsInBattlefield.remove(cp))
				{
					PutToGraveyardFromBattlefieldBehavior.LOGGER.info("|||");
					HatchetHarrySession.get().setAllCardsInBattleField(allCardsInBattlefield);

					final Long _gameId = PutToGraveyardFromBattlefieldBehavior.this.persistenceService
							.getPlayer(HatchetHarrySession.get().getPlayer().getId()).getGames()
							.iterator().next().getId();
					final List<BigInteger> allPlayersInGame = PutToGraveyardFromBattlefieldBehavior.this.persistenceService
							.giveAllPlayersFromGame(_gameId);

					HatchetHarrySession.get().addCardInGraveyard(mc);

					final boolean isGraveyardDisplayed = HatchetHarrySession.get()
							.isGraveyardDisplayed();
					if (isGraveyardDisplayed)
					{
						final Component graveyardToUpdate = new GraveyardComponent("graveyard");

						((HomePage)target.getPage()).getGraveyardParent().addOrReplace(
								graveyardToUpdate);
						target.add(((HomePage)target.getPage()).getGraveyardParent());

						target.appendJavaScript(JavaScriptUtils.REACTIVATE_GRAVEYARD_JAVASCRIPT_COMPONENT);
					}

					for (int i = 0; i < allPlayersInGame.size(); i++)
					{
						final Long playerToWhomToSend = allPlayersInGame.get(i).longValue();
						final String pageUuid = HatchetHarryApplication.getCometResources().get(
								playerToWhomToSend);
						final PutToGraveyardCometChannel ptgcc = new PutToGraveyardCometChannel(
								_gameId, cp, mc);
						final NotifierCometChannel ncc = new NotifierCometChannel(
								NotifierAction.PUT_CARD_TO_GRAVGEYARD_FROM_BATTLEFIELD, _gameId,
								HatchetHarrySession.get().getPlayer().getId(), HatchetHarrySession
										.get().getPlayer().getName(), "", "", mc.getTitle(), null);
						// TODO: display a notification for card put to
						// graveyard
						EventBus.get().post(ptgcc, pageUuid);
						EventBus.get().post(ncc, pageUuid);
					}

					return;
				}
				PutToGraveyardFromBattlefieldBehavior.LOGGER.info("could not remove card uuid= " + mc.getUuid());
				return;
			}
			PutToGraveyardFromBattlefieldBehavior.LOGGER.info("could not find CardPanel uuid= " + mc.getUuid()
					+ " in allCardsInBattlefield from session");
		}
	}

	@Subscribe
	public void removeCardFromBattlefield(final AjaxRequestTarget target,
			final PutToGraveyardCometChannel event)
	{
		final ArrayList<MagicCard> toRemove = HatchetHarrySession.get()
				.getAllCardsWhichHaveBeenInBattlefield();
		toRemove.add(event.getMagicCard());
		HatchetHarrySession.get().setAllCardsWhichHaveBeenInBattlefield(toRemove);

		target.appendJavaScript("window.setTimeout(function() { jQuery('#"
				+ event.getCardPanel().getMarkupId() + "').children(0).remove(); }, 3000); ");

		JavaScriptUtils.removeNonRelevantCardsFromBatlefield(target, this.persistenceService,
				event.getGameId());
	}

	@Required
	public void setPersistenceService(final PersistenceService _persistenceService)
	{
		this.persistenceService = _persistenceService;
	}

}
