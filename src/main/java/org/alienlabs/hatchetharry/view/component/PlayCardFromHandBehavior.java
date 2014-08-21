package org.alienlabs.hatchetharry.view.component;

import java.io.IOException;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.alienlabs.hatchetharry.HatchetHarrySession;
import org.alienlabs.hatchetharry.model.CardZone;
import org.alienlabs.hatchetharry.model.Deck;
import org.alienlabs.hatchetharry.model.Game;
import org.alienlabs.hatchetharry.model.MagicCard;
import org.alienlabs.hatchetharry.model.Player;
import org.alienlabs.hatchetharry.model.Side;
import org.alienlabs.hatchetharry.model.channel.ConsoleLogCometChannel;
import org.alienlabs.hatchetharry.model.channel.NotifierAction;
import org.alienlabs.hatchetharry.model.channel.NotifierCometChannel;
import org.alienlabs.hatchetharry.model.channel.PlayCardFromHandCometChannel;
import org.alienlabs.hatchetharry.model.channel.consolelog.AbstractConsoleLogStrategy;
import org.alienlabs.hatchetharry.model.channel.consolelog.ConsoleLogStrategy;
import org.alienlabs.hatchetharry.model.channel.consolelog.ConsoleLogType;
import org.alienlabs.hatchetharry.service.PersistenceService;
import org.alienlabs.hatchetharry.view.clientsideutil.EventBusPostService;
import org.alienlabs.hatchetharry.view.clientsideutil.JavaScriptUtils;
import org.alienlabs.hatchetharry.view.page.HomePage;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.injection.Injector;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.protocol.http.servlet.ServletWebRequest;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.template.PackageTextTemplate;
import org.apache.wicket.util.template.TextTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

public class PlayCardFromHandBehavior extends AbstractDefaultAjaxBehavior
{
	static final Logger LOGGER = LoggerFactory.getLogger(PlayCardFromHandBehavior.class);
	private static final long serialVersionUID = 1L;
	private final int currentCard;
	@SpringBean
	private PersistenceService persistenceService;
	private UUID uuidToLookFor;
	private String side;

	public PlayCardFromHandBehavior(final UUID _uuidToLookFor, final int _currentCard,
		final String _side)
	{
		super();
		Injector.get().inject(this);
		this.uuidToLookFor = _uuidToLookFor;
		this.currentCard = _currentCard;
		this.side = _side;
	}

	@Override
	protected void respond(final AjaxRequestTarget target)
	{
		final ServletWebRequest servletWebRequest = (ServletWebRequest)target.getPage()
			.getRequest();
		final HttpServletRequest request = servletWebRequest.getContainerRequest();

		try
		{
			this.uuidToLookFor = UUID.fromString(request.getParameter("card"));
		}
		catch (final IllegalArgumentException ex)
		{
			PlayCardFromHandBehavior.LOGGER.error(
				"No card with UUID= " + request.getParameter("card") + " found!", ex);
		}

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

		final Long currentPlaceholderId = game.getCurrentPlaceholderId() + 1;
		game.setCurrentPlaceholderId(currentPlaceholderId);

		this.persistenceService.updateGame(game);

		final Player p = this.persistenceService.getPlayer(HatchetHarrySession.get().getPlayer()
			.getId());
		final Deck d = p.getDeck();

		card.setZone(CardZone.BATTLEFIELD);

		final Player owner = this.persistenceService.getPlayer(card.getDeck().getPlayerId());
		final Side _side = owner.getSide();
		card.setX(_side.getX());
		card.setY(_side.getY());
		card.setDeck(d);
		this.persistenceService.saveOrUpdateCardAndDeck(card);

		final List<MagicCard> hand = d.reorderMagicCards(this.persistenceService
			.getAllCardsInHandForAGameAndAPlayer(gameId, p.getId(), d.getDeckId()));
		this.persistenceService.saveOrUpdateAllMagicCards(hand);
		final List<MagicCard> battlefield = d.reorderMagicCards(this.persistenceService
			.getAllCardsInBattlefieldForAGameAndAPlayer(gameId, p.getId(), d.getDeckId()));
		this.persistenceService.saveOrUpdateAllMagicCards(battlefield);

		JavaScriptUtils.updateHand(target);
		target.appendJavaScript("jQuery('#playCardIndicator').hide(); ");

		final PlayCardFromHandCometChannel pcfhcc = new PlayCardFromHandCometChannel(card,
			HatchetHarrySession.get().getPlayer().getName(), gameId, _side);
		final NotifierCometChannel ncc = new NotifierCometChannel(
			NotifierAction.PLAY_CARD_FROM_HAND_ACTION, gameId, HatchetHarrySession.get()
				.getPlayer().getId(), HatchetHarrySession.get().getPlayer().getName(), "", "",
			card.getTitle(), null, "");
		final ConsoleLogStrategy logger = AbstractConsoleLogStrategy.chooseStrategy(
			ConsoleLogType.ZONE_MOVE, CardZone.HAND, CardZone.BATTLEFIELD, null, card.getTitle(),
			owner.getName(), null, null, null, null, gameId);

		// post a message for all players in the game
		final List<BigInteger> allPlayersInGame = this.persistenceService
			.giveAllPlayersFromGame(gameId);
		EventBusPostService.post(allPlayersInGame, pcfhcc, ncc, new ConsoleLogCometChannel(logger));
	}

	@Override
	public void renderHead(final Component component, final IHeaderResponse response)
	{
		super.renderHead(component, response);

		final HashMap<String, Object> variables = new HashMap<String, Object>();
		variables.put("url", this.getCallbackUrl());
		String uuidAsString = this.uuidToLookFor.toString();
		variables.put("uuid", uuidAsString);
		variables.put("uuidValidForJs", uuidAsString.replace("-", "_"));
		variables.put("next", (this.currentCard == 6 ? 0 : this.currentCard + 1));
		variables.put("side", this.side);

		final TextTemplate template = new PackageTextTemplate(HomePage.class,
			"script/playCard/playCard.js");
		template.interpolate(variables);

		PlayCardFromHandBehavior.LOGGER.info("### clicked: " + this.currentCard);
		response.render(JavaScriptHeaderItem.forScript(template.asString(), "playCardFromHand"));
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

	public String getSide()
	{
		return this.side;
	}

	public void setSide(final String _side)
	{
		this.side = _side;
	}

}
