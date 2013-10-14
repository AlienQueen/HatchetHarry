package org.alienlabs.hatchetharry.view.component;

import java.math.BigInteger;
import java.util.List;

import org.alienlabs.hatchetharry.HatchetHarryApplication;
import org.alienlabs.hatchetharry.HatchetHarrySession;
import org.alienlabs.hatchetharry.model.CardZone;
import org.alienlabs.hatchetharry.model.Deck;
import org.alienlabs.hatchetharry.model.Game;
import org.alienlabs.hatchetharry.model.MagicCard;
import org.alienlabs.hatchetharry.model.Player;
import org.alienlabs.hatchetharry.model.channel.NotifierAction;
import org.alienlabs.hatchetharry.model.channel.NotifierCometChannel;
import org.alienlabs.hatchetharry.model.channel.PlayTopLibraryCardCometChannel;
import org.alienlabs.hatchetharry.model.channel.PutTopLibraryCardToGraveyardCometChannel;
import org.alienlabs.hatchetharry.model.channel.PutTopLibraryCardToHandCometChannel;
import org.alienlabs.hatchetharry.service.PersistenceService;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxButton;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

public class RevealTopLibraryCardModalWindow extends Panel
{
	private static final long serialVersionUID = 1L;

	static final Logger LOGGER = LoggerFactory.getLogger(RevealTopLibraryCardModalWindow.class);

	final ModalWindow modal;
	final MagicCard card;

	@SpringBean
	PersistenceService persistenceService;

	public RevealTopLibraryCardModalWindow(final String id, final ModalWindow _modal,
			final MagicCard _card)
	{
		super(id);
		this.modal = _modal;
		this.card = _card;

		final ExternalImage topLibraryCard = new ExternalImage("topLibraryCard",
				"cards/topLibraryCard.jpg?" + Math.random());

		if (null != this.card)
		{
			topLibraryCard.add(new AttributeModifier("name", this.card.getTitle()));
		}

		topLibraryCard.setOutputMarkupId(true).setMarkupId("topLibraryCard");
		this.add(topLibraryCard);


		final Form<String> form = new Form<String>("form");

		final IndicatingAjaxButton doNothing = new IndicatingAjaxButton("doNothing", form)
		{
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit(final AjaxRequestTarget target, final Form<?> _form)
			{
				RevealTopLibraryCardModalWindow.this.modal.close(target);
			}
		};
		doNothing.setOutputMarkupId(true).setMarkupId("doNothing");

		final IndicatingAjaxButton putToBattlefield = new IndicatingAjaxButton(
				"putToBattlefieldFromModalWindow", form)
		{
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit(final AjaxRequestTarget target, final Form<?> _form)
			{
				final Game game = RevealTopLibraryCardModalWindow.this.persistenceService
						.getGame(RevealTopLibraryCardModalWindow.this.card.getGameId());
				final Long currentPlaceholderId = game.getCurrentPlaceholderId() + 1;
				game.setCurrentPlaceholderId(currentPlaceholderId);
				RevealTopLibraryCardModalWindow.this.persistenceService.updateGame(game);

				final Player p = RevealTopLibraryCardModalWindow.this.persistenceService
						.getPlayer(RevealTopLibraryCardModalWindow.this.card.getDeck()
								.getPlayerId());
				final Long gameId = game.getId();

				final PlayTopLibraryCardCometChannel ptlccc = new PlayTopLibraryCardCometChannel(
						gameId, RevealTopLibraryCardModalWindow.this.card, p.getSide());

				final NotifierCometChannel ncc = new NotifierCometChannel(
						NotifierAction.PLAY_TOP_LIBRARY_CARD, gameId, p.getId(),
						HatchetHarrySession.get().getPlayer().getName(), "", "",
						RevealTopLibraryCardModalWindow.this.card.getTitle(), null, p.getName());

				final List<BigInteger> allPlayersInGame = RevealTopLibraryCardModalWindow.this.persistenceService
						.giveAllPlayersFromGame(gameId);

				// post a message for all players in the game
				for (int i = 0; i < allPlayersInGame.size(); i++)
				{
					final Long player = allPlayersInGame.get(i).longValue();
					final String pageUuid = HatchetHarryApplication.getCometResources().get(player);
					PlayCardFromHandBehavior.LOGGER.info("pageUuid: " + pageUuid);

					HatchetHarryApplication.get().getEventBus().post(ptlccc, pageUuid);
					HatchetHarryApplication.get().getEventBus().post(ncc, pageUuid);
				}

				RevealTopLibraryCardModalWindow.this.modal.close(target);
			}
		};
		putToBattlefield.setOutputMarkupId(true).setMarkupId("putToBattlefieldFromModalWindow");

		final IndicatingAjaxButton putToHand = new IndicatingAjaxButton("putToHandFromModalWindow",
				form)
		{
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit(final AjaxRequestTarget target, final Form<?> _form)
			{
				final Game game = RevealTopLibraryCardModalWindow.this.persistenceService
						.getGame(RevealTopLibraryCardModalWindow.this.card.getGameId());
				RevealTopLibraryCardModalWindow.this.card.setZone(CardZone.HAND);
				RevealTopLibraryCardModalWindow.this.persistenceService
						.updateCard(RevealTopLibraryCardModalWindow.this.card);

				final Player p = RevealTopLibraryCardModalWindow.this.persistenceService
						.getPlayer(RevealTopLibraryCardModalWindow.this.card.getDeck()
								.getPlayerId());
				final Deck d = p.getDeck();
				final Long gameId = game.getId();
				final List<MagicCard> hand = d
						.reorderMagicCards(RevealTopLibraryCardModalWindow.this.persistenceService
								.getAllCardsInHandForAGameAndAPlayer(gameId, p.getId(),
										d.getDeckId()));
				RevealTopLibraryCardModalWindow.this.persistenceService.updateAllMagicCards(hand);

				final PutTopLibraryCardToHandCometChannel ptlccc = new PutTopLibraryCardToHandCometChannel(
						gameId, p.getId(), d.getDeckId());

				final NotifierCometChannel ncc = new NotifierCometChannel(
						NotifierAction.PUT_TOP_LIBRARY_CARD_TO_HAND, gameId, p.getId(),
						HatchetHarrySession.get().getPlayer().getName(), "", "",
						RevealTopLibraryCardModalWindow.this.card.getTitle(), null, p.getName());

				final List<BigInteger> allPlayersInGame = RevealTopLibraryCardModalWindow.this.persistenceService
						.giveAllPlayersFromGame(gameId);

				// post a message for all players in the game
				for (int i = 0; i < allPlayersInGame.size(); i++)
				{
					final Long player = allPlayersInGame.get(i).longValue();
					final String pageUuid = HatchetHarryApplication.getCometResources().get(player);
					PlayCardFromHandBehavior.LOGGER.info("pageUuid: " + pageUuid);

					HatchetHarryApplication.get().getEventBus().post(ptlccc, pageUuid);
					HatchetHarryApplication.get().getEventBus().post(ncc, pageUuid);
				}

				RevealTopLibraryCardModalWindow.this.modal.close(target);
			}
		};
		putToHand.setOutputMarkupId(true).setMarkupId("putToHandFromModalWindow");

		final IndicatingAjaxButton putToGraveyard = new IndicatingAjaxButton(
				"putToGraveyardFromModalWindow", form)
		{
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit(final AjaxRequestTarget target, final Form<?> _form)
			{
				final Game game = RevealTopLibraryCardModalWindow.this.persistenceService
						.getGame(RevealTopLibraryCardModalWindow.this.card.getGameId());
				RevealTopLibraryCardModalWindow.this.card.setZone(CardZone.GRAVEYARD);
				RevealTopLibraryCardModalWindow.this.persistenceService
						.updateCard(RevealTopLibraryCardModalWindow.this.card);

				final Player p = RevealTopLibraryCardModalWindow.this.persistenceService
						.getPlayer(RevealTopLibraryCardModalWindow.this.card.getDeck()
								.getPlayerId());
				final Deck d = p.getDeck();
				final Long gameId = game.getId();
				final List<MagicCard> hand = d
						.reorderMagicCards(RevealTopLibraryCardModalWindow.this.persistenceService
								.getAllCardsInGraveyardForAGameAndAPlayer(gameId, p.getId(),
										d.getDeckId()));
				RevealTopLibraryCardModalWindow.this.persistenceService.updateAllMagicCards(hand);

				final PutTopLibraryCardToGraveyardCometChannel chan = new PutTopLibraryCardToGraveyardCometChannel(
						gameId, p.getId(), d.getDeckId());

				final NotifierCometChannel ncc = new NotifierCometChannel(
						NotifierAction.PUT_TOP_LIBRARY_CARD_TO_GRAVEYARD, gameId, p.getId(),
						HatchetHarrySession.get().getPlayer().getName(), "", "",
						RevealTopLibraryCardModalWindow.this.card.getTitle(), null, p.getName());

				final List<BigInteger> allPlayersInGame = RevealTopLibraryCardModalWindow.this.persistenceService
						.giveAllPlayersFromGame(gameId);

				// post a message for all players in the game
				for (int i = 0; i < allPlayersInGame.size(); i++)
				{
					final Long player = allPlayersInGame.get(i).longValue();
					final String pageUuid = HatchetHarryApplication.getCometResources().get(player);
					PlayCardFromHandBehavior.LOGGER.info("pageUuid: " + pageUuid);

					HatchetHarryApplication.get().getEventBus().post(chan, pageUuid);
					HatchetHarryApplication.get().getEventBus().post(ncc, pageUuid);
				}

				RevealTopLibraryCardModalWindow.this.modal.close(target);
			}
		};
		putToGraveyard.setOutputMarkupId(true).setMarkupId("putToGraveyardFromModalWindow");

		form.add(doNothing, putToBattlefield, putToHand, putToGraveyard);
		this.add(form);
	}

	@Required
	public void setPersistenceService(final PersistenceService _persistenceService)
	{
		this.persistenceService = _persistenceService;
	}
}
