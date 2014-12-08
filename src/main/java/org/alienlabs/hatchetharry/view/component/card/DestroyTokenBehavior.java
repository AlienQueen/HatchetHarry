package org.alienlabs.hatchetharry.view.component.card;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.alienlabs.hatchetharry.HatchetHarrySession;
import org.alienlabs.hatchetharry.model.Deck;
import org.alienlabs.hatchetharry.model.MagicCard;
import org.alienlabs.hatchetharry.model.Player;
import org.alienlabs.hatchetharry.model.channel.ConsoleLogCometChannel;
import org.alienlabs.hatchetharry.model.channel.DestroyTokenCometChannel;
import org.alienlabs.hatchetharry.model.channel.NotifierAction;
import org.alienlabs.hatchetharry.model.channel.NotifierCometChannel;
import org.alienlabs.hatchetharry.model.channel.consolelog.AbstractConsoleLogStrategy;
import org.alienlabs.hatchetharry.model.channel.consolelog.ConsoleLogStrategy;
import org.alienlabs.hatchetharry.model.channel.consolelog.ConsoleLogType;
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

@edu.umd.cs.findbugs.annotations.SuppressFBWarnings(value = { "SE_INNER_CLASS",
		"SIC_INNER_SHOULD_BE_STATIC_ANON" }, justification = "In Wicket, serializable inner classes are common. And as the parent Page is serialized as well, this is no concern. This is no bad practice in Wicket")
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
		if (mc.getBattlefieldOrder().intValue() >= (session.getLastBattlefieldOrder().intValue() - 1))
		{
			session.setLastBattlefieldOrder(Integer.valueOf(session.getLastBattlefieldOrder()
					.intValue() - 1));
		}

		DestroyTokenBehavior.LOGGER.info("playerId in respond(): " + session.getPlayer().getId());
		DestroyTokenBehavior.LOGGER.info("mc.getTitle(): " + mc.getTitle());

		final String tokenName = mc.getToken().getCreatureTypes();
		final Player targetPlayer = this.persistenceService.getPlayer(mc.getDeck().getPlayerId());

		final Long gameId = session.getPlayer().getGame().getId();
		final Player p = this.persistenceService.getPlayer(session.getPlayer().getId());
		final Deck d = p.getDeck();
		List<MagicCard> allCards = this.persistenceService
				.getAllCardsInBattlefieldForAGameAndADeck(gameId, d.getDeckId());
		allCards = BattlefieldService.reorderCards(allCards, Integer.valueOf(allCards.indexOf(mc)));
		allCards.remove(mc);
		this.persistenceService.deleteCardAndToken(mc);

		final List<BigInteger> allPlayersInGame = DestroyTokenBehavior.this.persistenceService
				.giveAllPlayersFromGame(gameId);

		for (int i = 0; i < allPlayersInGame.size(); i++)
		{
			final int index = i;
			final List<BigInteger> playerToWhomToSend = new ArrayList<BigInteger>();
			playerToWhomToSend.add(allPlayersInGame.get(index));

			final DestroyTokenCometChannel dtcc = new DestroyTokenCometChannel(mc, gameId);
			final NotifierCometChannel _ncc = new NotifierCometChannel(
					NotifierAction.DESTROY_TOKEN_ACTION, gameId, session.getPlayer().getId(),
					session.getPlayer().getName(), "", "", tokenName, null, targetPlayer.getName());
			final ConsoleLogStrategy logger = AbstractConsoleLogStrategy.chooseStrategy(
					ConsoleLogType.TOKEN_CREATION_DESTRUCTION, null, null, Boolean.FALSE, null,
					session.getPlayer().getName(), tokenName, null, null, Boolean.FALSE, gameId);

			EventBusPostService.post(playerToWhomToSend, dtcc, _ncc, new ConsoleLogCometChannel(
					logger));
		}
	}

	@Override
	public void renderHead(final Component component, final IHeaderResponse response)
	{
		super.renderHead(component, response);

		final HashMap<String, Object> variables = new HashMap<String, Object>();
		variables.put("uuidValidForJs", this.uuid.toString().replace("-", "_"));
		variables.put("destroyTokenUrl", this.getCallbackUrl());

		final TextTemplate template = new PackageTextTemplate(HomePage.class,
				"script/draggableHandle/destroyToken.js");
		template.interpolate(variables);

		response.render(JavaScriptHeaderItem.forScript(template.asString(), null));
		try
		{
			template.close();
		}
		catch (final IOException e)
		{
			DestroyTokenBehavior.LOGGER.error(
					"unable to close template1 in DestroyTokenBehavior#renderHead()!", e);
		}
	}

	@Required
	public void setPersistenceService(final PersistenceService _persistenceService)
	{
		this.persistenceService = _persistenceService;
	}

}
