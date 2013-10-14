package org.alienlabs.hatchetharry.view.component;

import java.io.IOException;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.alienlabs.hatchetharry.HatchetHarryApplication;
import org.alienlabs.hatchetharry.HatchetHarrySession;
import org.alienlabs.hatchetharry.model.CardZone;
import org.alienlabs.hatchetharry.model.Deck;
import org.alienlabs.hatchetharry.model.Game;
import org.alienlabs.hatchetharry.model.MagicCard;
import org.alienlabs.hatchetharry.model.Player;
import org.alienlabs.hatchetharry.model.channel.NotifierAction;
import org.alienlabs.hatchetharry.model.channel.NotifierCometChannel;
import org.alienlabs.hatchetharry.model.channel.PlayCardFromGraveyardCometChannel;
import org.alienlabs.hatchetharry.service.PersistenceService;
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

public class PlayCardFromGraveyardBehavior extends AbstractDefaultAjaxBehavior
{
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = LoggerFactory
			.getLogger(PlayCardFromGraveyardBehavior.class);

	@SpringBean
	private PersistenceService persistenceService;

	private UUID uuidToLookFor;
	private String side;

	public PlayCardFromGraveyardBehavior(final String _side)
	{
		super();
		Injector.get().inject(this);
		this.side = _side;
	}

	@Override
	protected void respond(final AjaxRequestTarget target)
	{
		target.appendJavaScript(JavaScriptUtils.HIDE_MENUS);

		final ServletWebRequest servletWebRequest = (ServletWebRequest)target.getPage()
				.getRequest();
		final HttpServletRequest request = servletWebRequest.getContainerRequest();

		this.uuidToLookFor = UUID.fromString(request.getParameter("card"));

		final MagicCard card = this.persistenceService.getCardFromUuid(this.uuidToLookFor);

		if (!CardZone.GRAVEYARD.equals(card.getZone()))
		{
			return;
		}
		card.setZone(CardZone.BATTLEFIELD);

		final Long gameId = HatchetHarrySession.get().getPlayer().getGame().getId();
		final Game game = this.persistenceService.getGame(gameId);
		final Long currentPlaceholderId = game.getCurrentPlaceholderId() + 1;
		game.setCurrentPlaceholderId(currentPlaceholderId);
		this.persistenceService.updateGame(game);

		card.setX(300l + (currentPlaceholderId * 16));
		card.setY(300l + (currentPlaceholderId * 16));
		this.persistenceService.updateCard(card);

		JavaScriptUtils.updateGraveyard(target);

		final Player p = this.persistenceService.getPlayer(HatchetHarrySession.get().getPlayer()
				.getId());
		final Deck d = p.getDeck();
		final List<MagicCard> graveyard = d.reorderMagicCards(this.persistenceService
				.getAllCardsInGraveyardForAGameAndAPlayer(gameId, p.getId(), d.getDeckId()));
		this.persistenceService.updateAllMagicCards(graveyard);
		final List<MagicCard> battlefield = d.reorderMagicCards(this.persistenceService
				.getAllCardsInBattlefieldForAGameAndAPlayer(gameId, p.getId(), d.getDeckId()));
		this.persistenceService.updateAllMagicCards(battlefield);

		final PlayCardFromGraveyardCometChannel pcfgcc = new PlayCardFromGraveyardCometChannel(
				card, HatchetHarrySession.get().getPlayer().getName(), gameId, p.getSide());

		final NotifierCometChannel ncc = new NotifierCometChannel(
				NotifierAction.PLAY_CARD_FROM_GRAVEYARD_ACTION, gameId, HatchetHarrySession.get()
						.getPlayer().getId(), HatchetHarrySession.get().getPlayer().getName(), "",
				"", card.getTitle(), null, "");

		final List<BigInteger> allPlayersInGame = this.persistenceService
				.giveAllPlayersFromGame(gameId);

		// post a message for all players in the game
		for (int i = 0; i < allPlayersInGame.size(); i++)
		{
			final Long player = allPlayersInGame.get(i).longValue();
			final String pageUuid = HatchetHarryApplication.getCometResources().get(player);

			// For unit tests
			try
			{
				HatchetHarryApplication.get().getEventBus().post(pcfgcc, pageUuid);
				HatchetHarryApplication.get().getEventBus().post(ncc, pageUuid);
			}
			catch (final NullPointerException e)
			{
				// Nothing to do in unit tests
			}
		}
	}

	@Override
	public void renderHead(final Component component, final IHeaderResponse response)
	{
		super.renderHead(component, response);

		final HashMap<String, Object> variables = new HashMap<String, Object>();
		variables.put("url", this.getCallbackUrl());
		variables.put("side", this.side);

		final TextTemplate template = new PackageTextTemplate(HomePage.class,
				"script/playCard/playCardFromGraveyard.js");
		template.interpolate(variables);

		response.render(JavaScriptHeaderItem.forScript(template.asString(), "playCardFromGraveyard"));
		try
		{
			template.close();
		}
		catch (final IOException e)
		{
			PlayCardFromGraveyardBehavior.LOGGER.error(
					"unable to close template in PlayCardFromGraveyardBehavior#renderHead()!", e);
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
