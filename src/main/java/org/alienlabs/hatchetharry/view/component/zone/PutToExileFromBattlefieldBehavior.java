package org.alienlabs.hatchetharry.view.component.zone;

import org.alienlabs.hatchetharry.HatchetHarrySession;
import org.alienlabs.hatchetharry.model.CardZone;
import org.alienlabs.hatchetharry.model.Deck;
import org.alienlabs.hatchetharry.model.MagicCard;
import org.alienlabs.hatchetharry.model.Player;
import org.alienlabs.hatchetharry.model.channel.ConsoleLogCometChannel;
import org.alienlabs.hatchetharry.model.channel.NotifierAction;
import org.alienlabs.hatchetharry.model.channel.NotifierCometChannel;
import org.alienlabs.hatchetharry.model.channel.PutToExileFromBattlefieldCometChannel;
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

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@edu.umd.cs.findbugs.annotations.SuppressFBWarnings(value = { "SE_INNER_CLASS",
		"SIC_INNER_SHOULD_BE_STATIC_ANON" }, justification = "In Wicket, serializable inner classes are common. And as the parent Page is serialized as well, this is no concern. This is no bad practice in Wicket")
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
		final Deck mydeck = this.persistenceService.getDeck(session.getPlayer().getDeck()
				.getDeckId().longValue());

		final List<MagicCard> exile = mydeck.reorderMagicCards(this.persistenceService
				.getAllCardsInExileForAGameAndAPlayer(gameId, p.getId(), mydeck.getDeckId()));
		this.persistenceService.saveOrUpdateAllMagicCards(exile);
		final List<MagicCard> battlefield = BattlefieldService.reorderCards(
				this.persistenceService.getAllCardsInBattlefieldForAGameAndADeck(gameId,
						mydeck.getDeckId()), mc.getBattlefieldOrder());
		this.persistenceService.saveOrUpdateAllMagicCards(battlefield);

		HatchetHarrySession.get().decrementLastBattlefieldOrder();

		final List<BigInteger> allPlayersInGame = PutToExileFromBattlefieldBehavior.this.persistenceService
				.giveAllPlayersFromGame(gameId);

		final ConsoleLogStrategy logger = AbstractConsoleLogStrategy.chooseStrategy(
				ConsoleLogType.ZONE_MOVE, CardZone.BATTLEFIELD, CardZone.EXILE, null,
				mc.getTitle(), HatchetHarrySession.get().getPlayer().getName(), null, null, null,
				null, gameId);

		for (int i = 0; i < allPlayersInGame.size(); i++)
		{
			final int index = i;
			final List<BigInteger> playerToWhomToSend = new ArrayList<>();
			playerToWhomToSend.add(allPlayersInGame.get(index));

			final Player targetPlayer = this.persistenceService.getPlayer(mc.getDeck()
					.getPlayerId());
			final String targetPlayerName = targetPlayer.getName();
			final Long targetDeckId = mc.getDeck().getDeckId();

			final PutToExileFromBattlefieldCometChannel ptefbcc = new PutToExileFromBattlefieldCometChannel(
					gameId, mc, session.getPlayer().getName(), targetPlayer.getId(), targetDeckId,
					(allPlayersInGame.get(i).longValue() == targetPlayer.getId().longValue()));
			final NotifierCometChannel ncc = new NotifierCometChannel(
					NotifierAction.PUT_CARD_TO_EXILE_FROM_BATTLEFIELD_ACTION, gameId, session
					.getPlayer().getId(), session.getPlayer().getName(), "", mc.getTitle(),
					null, targetPlayerName);
			EventBusPostService.post(playerToWhomToSend, ptefbcc, ncc, new ConsoleLogCometChannel(
					logger));

			if (allPlayersInGame.get(i).longValue() == targetPlayer.getId().longValue())
			{
				targetPlayer.setExileDisplayed(Boolean.TRUE);
				this.persistenceService.mergePlayer(targetPlayer);
			}
		}
	}

	@Override
	public void renderHead(final Component component, final IHeaderResponse response)
	{
		super.renderHead(component, response);

		final HashMap<String, Object> variables = new HashMap<>();
		variables.put("uuidValidForJs", this.uuid.toString().replaceAll("-", "_"));
		variables.put("url", this.getCallbackUrl());

		final TextTemplate template = new PackageTextTemplate(HomePage.class,
				"script/contextmenu/putToExileFromBattlefield.js");
		template.interpolate(variables);

		response.render(JavaScriptHeaderItem.forScript(template.asString(), null));
		try
		{
			template.close();
		}
		catch (final IOException e)
		{
			PutToExileFromBattlefieldBehavior.LOGGER.error(
					"unable to close template in PutToExileFromBattlefieldBehavior#renderHead()!",
					e);
		}
	}

	@Required
	public void setPersistenceService(final PersistenceService _persistenceService)
	{
		this.persistenceService = _persistenceService;
	}

}
