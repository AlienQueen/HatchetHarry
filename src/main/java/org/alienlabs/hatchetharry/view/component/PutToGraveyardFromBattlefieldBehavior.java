package org.alienlabs.hatchetharry.view.component;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
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
import org.alienlabs.hatchetharry.model.channel.PutToGraveyardCometChannel;
import org.alienlabs.hatchetharry.model.channel.consolelog.AbstractConsoleLogStrategy;
import org.alienlabs.hatchetharry.model.channel.consolelog.ConsoleLogStrategy;
import org.alienlabs.hatchetharry.model.channel.consolelog.ConsoleLogType;
import org.alienlabs.hatchetharry.service.PersistenceService;
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

@edu.umd.cs.findbugs.annotations.SuppressWarnings(value = { "SE_INNER_CLASS",
		"SIC_INNER_SHOULD_BE_STATIC_ANON" }, justification = "In Wicket, serializable inner classes are common. And as the parent Page is serialized as well, this is no concern. This is no bad practice in Wicket")
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

		final HatchetHarrySession session = HatchetHarrySession.get();
		PutToGraveyardFromBattlefieldBehavior.LOGGER.info("playerId in respond(): "
				+ session.getPlayer().getId());
		PutToGraveyardFromBattlefieldBehavior.LOGGER.info("mc.getTitle(): " + mc.getTitle());

		mc.setZone(CardZone.GRAVEYARD);
		mc.setTapped(false);
		this.persistenceService.updateCard(mc);

		final Long gameId = session.getPlayer().getGame().getId();

		final Player p = this.persistenceService.getPlayer(session.getPlayer().getId());

		final Deck d = p.getDeck();
		// TODO: reorder?
		final List<MagicCard> graveyard = this.persistenceService
				.getAllCardsInGraveyardForAGameAndAPlayer(gameId, p.getId(), d.getDeckId());

		this.persistenceService.saveOrUpdateAllMagicCards(graveyard);

		// TODO: reorder?
		final List<MagicCard> battlefield = this.persistenceService
				.getAllCardsInBattlefieldForAGameAndAPlayer(gameId, p.getId(), d.getDeckId());

		this.persistenceService.saveOrUpdateAllMagicCards(battlefield);

		final List<BigInteger> allPlayersInGame = PutToGraveyardFromBattlefieldBehavior.this.persistenceService
				.giveAllPlayersFromGame(gameId);

		final ConsoleLogStrategy logger = AbstractConsoleLogStrategy.chooseStrategy(
				ConsoleLogType.ZONE_MOVE, CardZone.BATTLEFIELD, CardZone.GRAVEYARD, null,
				mc.getTitle(), HatchetHarrySession.get().getPlayer().getName(), null, null, null,
				null, gameId);

		for (int i = 0; i < allPlayersInGame.size(); i++)
		{
			final int index = i;
			final List<BigInteger> playerToWhomToSend = new ArrayList<BigInteger>()
			{
				/**
				 *
				 */
				private static final long serialVersionUID = 1L;

				{
					this.add(allPlayersInGame.get(index));
				}
			};

			final Player targetPlayer = this.persistenceService.getPlayer(mc.getDeck()
					.getPlayerId());
			final String targetPlayerName = targetPlayer.getName();
			final Long targetDeckId = mc.getDeck().getDeckId();

			if (allPlayersInGame.get(i).longValue() == targetPlayer.getId().longValue())
			{
				targetPlayer.setGraveyardDisplayed(true);
				this.persistenceService.mergePlayer(targetPlayer);
			}

			final PutToGraveyardCometChannel _ptgcc = new PutToGraveyardCometChannel(gameId, mc,
					session.getPlayer().getName(), targetPlayerName, targetPlayer.getId(),
					targetDeckId, (allPlayersInGame.get(i).longValue() == targetPlayer.getId()
							.longValue()));
			final NotifierCometChannel _ncc = new NotifierCometChannel(
					NotifierAction.PUT_CARD_TO_GRAVGEYARD_FROM_BATTLEFIELD_ACTION, gameId, session
							.getPlayer().getId(), session.getPlayer().getName(), "", "",
					mc.getTitle(), null, targetPlayerName);

			EventBusPostService.post(playerToWhomToSend, _ptgcc, _ncc, new ConsoleLogCometChannel(
					logger));
		}
	}

	@Override
	public void renderHead(final Component component, final IHeaderResponse response)
	{
		super.renderHead(component, response);

		final HashMap<String, Object> variables = new HashMap<String, Object>();
		variables.put("uuidValidForJs", this.uuid.toString().replaceAll("-", "_"));
		variables.put("url", this.getCallbackUrl());

		final TextTemplate template = new PackageTextTemplate(HomePage.class,
				"script/contextmenu/putToGraveyardFromBattlefield.js");
		template.interpolate(variables);

		response.render(JavaScriptHeaderItem.forScript(template.asString(), null));
		try
		{
			template.close();
		}
		catch (final IOException e)
		{
			PutToGraveyardFromBattlefieldBehavior.LOGGER
					.error("unable to close template in PutToGraveyardFromBattlefieldBehavior#renderHead()!",
							e);
		}
	}

	@Required
	public void setPersistenceService(final PersistenceService _persistenceService)
	{
		this.persistenceService = _persistenceService;
	}

}
