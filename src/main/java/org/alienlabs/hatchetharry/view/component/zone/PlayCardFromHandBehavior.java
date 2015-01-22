package org.alienlabs.hatchetharry.view.component.zone;

import java.io.IOException;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.alienlabs.hatchetharry.HatchetHarrySession;
import org.alienlabs.hatchetharry.model.CardZone;
import org.alienlabs.hatchetharry.model.Deck;
import org.alienlabs.hatchetharry.model.Game;
import org.alienlabs.hatchetharry.model.MagicCard;
import org.alienlabs.hatchetharry.model.Player;
import org.alienlabs.hatchetharry.model.channel.ConsoleLogCometChannel;
import org.alienlabs.hatchetharry.model.channel.NotifierAction;
import org.alienlabs.hatchetharry.model.channel.NotifierCometChannel;
import org.alienlabs.hatchetharry.model.channel.PlayCardFromHandCometChannel;
import org.alienlabs.hatchetharry.model.consolelogstrategy.AbstractConsoleLogStrategy;
import org.alienlabs.hatchetharry.model.consolelogstrategy.ConsoleLogStrategy;
import org.alienlabs.hatchetharry.model.consolelogstrategy.ConsoleLogType;
import org.alienlabs.hatchetharry.service.PersistenceService;
import org.alienlabs.hatchetharry.view.clientsideutil.BattlefieldService;
import org.alienlabs.hatchetharry.view.clientsideutil.EventBusPostService;
import org.alienlabs.hatchetharry.view.page.HomePage;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.injection.Injector;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.template.PackageTextTemplate;
import org.apache.wicket.util.template.TextTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

public class PlayCardFromHandBehavior extends AbstractDefaultAjaxBehavior
{
	private static final Logger LOGGER = LoggerFactory.getLogger(PlayCardFromHandBehavior.class);
	private static final long serialVersionUID = 1L;
	@SpringBean
	private PersistenceService persistenceService;
	private final UUID uuidToLookFor;
	private ConsoleLogStrategy logger;

	public PlayCardFromHandBehavior(final UUID _uuidToLookFor)
	{
		super();
		Injector.get().inject(this);
		this.uuidToLookFor = _uuidToLookFor;
	}

	@Override
	protected void respond(final AjaxRequestTarget target)
	{
		final MagicCard card = this.persistenceService.getCardFromUuid(this.uuidToLookFor);

		if (null == card)
		{
			PlayCardFromHandBehavior.LOGGER.error("UUID " + this.uuidToLookFor
					+ " retrieved no MagicCard!");
			return;
		}

		if (!CardZone.HAND.equals(card.getZone()))
		{
			return;
		}

		final Long gameId = HatchetHarrySession.get().getPlayer().getGame().getId();

		final Game game = this.persistenceService.getGame(gameId);

		final Long currentPlaceholderId = Long
				.valueOf(game.getCurrentPlaceholderId().longValue() + 1);
		game.setCurrentPlaceholderId(currentPlaceholderId);

		this.persistenceService.updateGame(game);

		final Player p = HatchetHarrySession.get().getPlayer();
		final Deck d = p.getDeck();

		final List<MagicCard> hand = this.persistenceService.getAllCardsInHandForAGameAndADeck(
				gameId, d.getDeckId());
		PlayCardFromHandBehavior.LOGGER.info("remove? " + hand.remove(card));
		card.setZone(CardZone.BATTLEFIELD);
		d.reorderMagicCards(hand);
		this.persistenceService.saveOrUpdateAllMagicCards(hand);

		card.setOwnerSide(p.getSide().getSideName());
		card.setBattlefieldOrder(HatchetHarrySession.get().incrementLastBattlefieldOder());
		this.persistenceService.updateCard(card);

		final List<MagicCard> battlefield = this.persistenceService
				.getAllCardsInBattlefieldForAGameAndADeck(gameId, d.getDeckId());
		d.reorderMagicCards(battlefield);
		PlayCardFromHandBehavior.LOGGER.info("In battlefield: " + battlefield.size());
		this.persistenceService.saveOrUpdateAllMagicCards(battlefield);

		target.appendJavaScript("jQuery('#playCardIndicator').hide(); ");

		final PlayCardFromHandCometChannel pcfhcc = new PlayCardFromHandCometChannel(card,
				HatchetHarrySession.get().getPlayer().getName(), gameId);
		final NotifierCometChannel ncc = new NotifierCometChannel(
				NotifierAction.PLAY_CARD_FROM_HAND_ACTION, gameId, HatchetHarrySession.get()
				.getPlayer().getId(), HatchetHarrySession.get().getPlayer().getName(), "",
				card.getTitle(), null, "");
		this.logger = AbstractConsoleLogStrategy.chooseStrategy(
				ConsoleLogType.ZONE_MOVE, CardZone.HAND, CardZone.BATTLEFIELD, null,
				card.getTitle(), p.getName(), null, null, null, null, gameId);

		// post a message for all players in the game
		final List<BigInteger> allPlayersInGame = this.persistenceService
				.giveAllPlayersFromGame(gameId);
		EventBusPostService.post(allPlayersInGame, pcfhcc, ncc, new ConsoleLogCometChannel(this.logger));

		BattlefieldService.updateHand(target);
	}

	@Override
	public void renderHead(final Component component, final IHeaderResponse response)
	{
		super.renderHead(component, response);

		final HashMap<String, Object> variables = new HashMap<>();
		variables.put("url", this.getCallbackUrl());
		final String uuidAsString = this.uuidToLookFor.toString();
		variables.put("uuidValidForJs", uuidAsString.replace("-", "_"));

		final TextTemplate template = new PackageTextTemplate(HomePage.class,
				"script/playCard/playCard.js");
		template.interpolate(variables);

		response.render(JavaScriptHeaderItem.forScript(template.asString(), null));
		try
		{
			template.close();
		}
		catch (final IOException e)
		{
			PlayCardFromHandBehavior.LOGGER.error(
					"unable to close template in PlayCardFromHandBehavior#renderHead()!", e);
		}
	}

	@Required
	public void setPersistenceService(final PersistenceService _persistenceService)
	{
		this.persistenceService = _persistenceService;
	}

	public ConsoleLogStrategy getLogger()
	{
		return this.logger;
	}

}
