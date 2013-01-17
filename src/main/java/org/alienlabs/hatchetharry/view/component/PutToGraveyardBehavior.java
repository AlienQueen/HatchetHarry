package org.alienlabs.hatchetharry.view.component;

import java.math.BigInteger;
import java.util.List;
import java.util.UUID;

import org.alienlabs.hatchetharry.HatchetHarryApplication;
import org.alienlabs.hatchetharry.HatchetHarrySession;
import org.alienlabs.hatchetharry.model.CardZone;
import org.alienlabs.hatchetharry.model.MagicCard;
import org.alienlabs.hatchetharry.model.channel.PutToGraveyardCometChannel;
import org.alienlabs.hatchetharry.service.PersistenceService;
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

public class PutToGraveyardBehavior extends AbstractDefaultAjaxBehavior
{
	private static final long serialVersionUID = 1L;

	private static final Logger LOGGER = LoggerFactory.getLogger(PutToGraveyardBehavior.class);
	private final UUID uuid;

	@SpringBean
	private PersistenceService persistenceService;

	public PutToGraveyardBehavior(final UUID _uuid)
	{
		Injector.get().inject(this);
		this.uuid = _uuid;
	}

	@Override
	protected void respond(final AjaxRequestTarget target)
	{
		PutToGraveyardBehavior.LOGGER.info("respond");

		final String uniqueid = this.uuid.toString();
		MagicCard mc = null;

		try
		{
			mc = this.persistenceService.getCardFromUuid(UUID.fromString(uniqueid));
		}
		catch (final IllegalArgumentException e)
		{
			PutToGraveyardBehavior.LOGGER.error("error parsing UUID of moved card", e);
		}

		if (null == mc)
		{
			return;
		}

		PutToGraveyardBehavior.LOGGER.info("playerId in respond(): "
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
					PutToGraveyardBehavior.LOGGER.info("|||");
					HatchetHarrySession.get().setAllCardsInBattleField(allCardsInBattlefield);

					final Long _gameId = PutToGraveyardBehavior.this.persistenceService
							.getPlayer(HatchetHarrySession.get().getPlayer().getId()).getGames()
							.iterator().next().getId();
					final List<BigInteger> allPlayersInGame = PutToGraveyardBehavior.this.persistenceService
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

						target.appendJavaScript("var theIntGraveyard = null; var $crosslinkGraveyard, $navthumbGraveyard; var curclickedGraveyard = 0; theIntervalGraveyard = function(cur) { if (typeof cur != 'undefined') curclickedGraveyard = cur; $crosslinkGraveyard.removeClass('active-thumbGraveyard'); $navthumbGraveyard.eq(curclickedGraveyard).parent().addClass('active-thumbGraveyard'); jQuery('.stripNavGraveyard ul li a').eq(curclickedGraveyard).trigger('click'); $crosslinkGraveyard.removeClass('active-thumbGraveyard'); $navthumbGraveyard.eq(curclickedGraveyard).parent().addClass('active-thumbGraveyard'); jQuery('.stripNavGraveyard ul li a').eq(curclickedGraveyard).trigger('click'); curclickedGraveyard++; if (6 == curclickedGraveyard) curclickedGraveyard = 0; }; jQuery('#graveyard-main-photo-slider').codaSliderGraveyard(); $navthumbGraveyard = jQuery('.graveyard-nav-thumb'); $crosslinkGraveyard = jQuery('.graveyard-cross-link'); $navthumbGraveyard.click(function() { var $this = jQuery(this); theIntervalGraveyard($this.parent().attr('href').slice(1) - 1); return false; }); theIntervalGraveyard();");
					}

					for (int i = 0; i < allPlayersInGame.size(); i++)
					{
						final Long playerToWhomToSend = allPlayersInGame.get(i).longValue();
						final String pageUuid = HatchetHarryApplication.getCometResources().get(
								playerToWhomToSend);
						final PutToGraveyardCometChannel ptgcc = new PutToGraveyardCometChannel(
								_gameId, cp);

						EventBus.get().post(ptgcc, pageUuid);
					}

					return;
				}
				PutToGraveyardBehavior.LOGGER.info("could not remove card uuid= " + mc.getUuid());
				return;
			}
			PutToGraveyardBehavior.LOGGER.info("could not find CardPanel uuid= " + mc.getUuid()
					+ " in allCardsInBattlefield from session");
		}
	}

	@Subscribe
	public void removeCardFromBattlefield(final AjaxRequestTarget target,
			final PutToGraveyardCometChannel event)
	{
		target.appendJavaScript("jQuery('#" + event.getCard().getMarkupId() + "').remove();");
	}

	@Required
	public void setPersistenceService(final PersistenceService _persistenceService)
	{
		this.persistenceService = _persistenceService;
	}

}
