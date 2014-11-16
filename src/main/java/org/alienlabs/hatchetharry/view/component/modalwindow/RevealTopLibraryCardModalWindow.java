package org.alienlabs.hatchetharry.view.component.modalwindow;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.List;
import java.util.ResourceBundle;

import org.alienlabs.hatchetharry.HatchetHarryApplication;
import org.alienlabs.hatchetharry.HatchetHarrySession;
import org.alienlabs.hatchetharry.model.CardZone;
import org.alienlabs.hatchetharry.model.Deck;
import org.alienlabs.hatchetharry.model.Game;
import org.alienlabs.hatchetharry.model.MagicCard;
import org.alienlabs.hatchetharry.model.Player;
import org.alienlabs.hatchetharry.model.channel.ConsoleLogCometChannel;
import org.alienlabs.hatchetharry.model.channel.NotifierAction;
import org.alienlabs.hatchetharry.model.channel.NotifierCometChannel;
import org.alienlabs.hatchetharry.model.channel.PlayTopLibraryCardCometChannel;
import org.alienlabs.hatchetharry.model.channel.PutTopLibraryCardToGraveyardCometChannel;
import org.alienlabs.hatchetharry.model.channel.PutTopLibraryCardToHandCometChannel;
import org.alienlabs.hatchetharry.model.channel.RevealTopLibraryCardCometChannel;
import org.alienlabs.hatchetharry.model.channel.consolelog.AbstractConsoleLogStrategy;
import org.alienlabs.hatchetharry.model.channel.consolelog.ConsoleLogStrategy;
import org.alienlabs.hatchetharry.model.channel.consolelog.ConsoleLogType;
import org.alienlabs.hatchetharry.service.PersistenceService;
import org.alienlabs.hatchetharry.view.clientsideutil.EventBusPostService;
import org.alienlabs.hatchetharry.view.component.gui.ExternalImage;
import org.alienlabs.hatchetharry.view.page.HomePage;
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

import com.google.common.io.Files;

@edu.umd.cs.findbugs.annotations.SuppressFBWarnings(value = "SE_INNER_CLASS", justification = "In Wicket, serializable inner classes are common. And as the parent Page is serialized as well, this is no concern. This is no bad practice in Wicket")
public class RevealTopLibraryCardModalWindow extends Panel
{
	static final Logger LOGGER = LoggerFactory.getLogger(RevealTopLibraryCardModalWindow.class);
	private static final long serialVersionUID = 1L;
	final ModalWindow modal;
	final MagicCard card;

	@SpringBean
	PersistenceService persistenceService;

	public RevealTopLibraryCardModalWindow(final String id, final ModalWindow _modal,
			final MagicCard _card) throws NoSuchAlgorithmException
	{
		super(id);
		this.modal = _modal;
		this.card = _card;

		final ExternalImage topLibraryCard = new ExternalImage("topLibraryCard",
				"cards/topLibraryCard.jpg?" + SecureRandom.getInstanceStrong().nextLong());

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
				if (HatchetHarrySession.get().getTopCardIndex().longValue() > 0l)
				{
					HatchetHarrySession.get().setTopCardIndex(
							HatchetHarrySession.get().getTopCardIndex().longValue() - 1l);
				}
				RevealTopLibraryCardModalWindow.this.modal.close(target);
			}
		};
		doNothing.setOutputMarkupId(true).setMarkupId("doNothing");

		final IndicatingAjaxButton next = new IndicatingAjaxButton("next", form)
		{
			private static final long serialVersionUID = 1L;

			@edu.umd.cs.findbugs.annotations.SuppressFBWarnings({ "PATH_TRAVERSAL_IN",
					"PATH_TRAVERSAL_IN" })
			@Override
			protected void onSubmit(final AjaxRequestTarget target, final Form<?> _form)
			{
				final HatchetHarrySession session = HatchetHarrySession.get();
				final List<MagicCard> allCardsInLibrary = RevealTopLibraryCardModalWindow.this.persistenceService
						.getAllCardsInLibraryForDeckAndPlayer(session.getGameId(), session
								.getPlayer().getId(), session.getPlayer().getDeck().getDeckId());

				if ((null == allCardsInLibrary) || (allCardsInLibrary.isEmpty()))
				{
					return;
				}

				session.setTopCardIndex(session.getTopCardIndex() + 1);
				final MagicCard firstCard = allCardsInLibrary.get(session.getTopCardIndex()
						.intValue());
				final String topCardName = firstCard.getBigImageFilename();

				final String cardPath = ResourceBundle.getBundle(
						HatchetHarryApplication.class.getCanonicalName()).getString(
						"SharedResourceFolder");
				final String cardPathAndName = cardPath.replace("/cards", "") + topCardName;
				final File from = new File(cardPathAndName);
				final File to = new File(cardPath + "topLibraryCard.jpg");

				try
				{
					Files.copy(from, to);
				}
				catch (final IOException e)
				{
					RevealTopLibraryCardModalWindow.LOGGER.error("could not copy from: "
							+ cardPathAndName + " to: " + cardPath + "topLibraryCard.jpg", e);
				}

				final Long gameId = RevealTopLibraryCardModalWindow.this.persistenceService
						.getPlayer(session.getPlayer().getId()).getGame().getId();
				final List<BigInteger> allPlayersInGame = RevealTopLibraryCardModalWindow.this.persistenceService
						.giveAllPlayersFromGame(gameId);
				final RevealTopLibraryCardCometChannel chan = new RevealTopLibraryCardCometChannel(
						session.getPlayer().getName(), firstCard, session.getTopCardIndex());
				final ConsoleLogStrategy logger = AbstractConsoleLogStrategy.chooseStrategy(
						ConsoleLogType.REVEAL_TOP_CARD_OF_LIBRARY, null, null, null,
						firstCard.getTitle(), session.getPlayer().getName(), null,
						session.getTopCardIndex() + 1l, null, false, session.getGameId());

				EventBusPostService
						.post(allPlayersInGame, chan, new ConsoleLogCometChannel(logger));
			}
		};
		next.setOutputMarkupId(true).setMarkupId("next");

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

				RevealTopLibraryCardModalWindow.this.card.setZone(CardZone.BATTLEFIELD);
				RevealTopLibraryCardModalWindow.this.card.setX(p.getSide().getX());
				RevealTopLibraryCardModalWindow.this.card.setY(p.getSide().getY());
				RevealTopLibraryCardModalWindow.this.persistenceService
						.updateCard(RevealTopLibraryCardModalWindow.this.card);

				final PlayTopLibraryCardCometChannel ptlccc = new PlayTopLibraryCardCometChannel(
						gameId, RevealTopLibraryCardModalWindow.this.card, p.getSide());

				final NotifierCometChannel ncc = new NotifierCometChannel(
						NotifierAction.PLAY_TOP_LIBRARY_CARD_ACTION, gameId, p.getId(),
						HatchetHarrySession.get().getPlayer().getName(), "", "",
						RevealTopLibraryCardModalWindow.this.card.getTitle(), null, p.getName());

				final List<BigInteger> allPlayersInGame = RevealTopLibraryCardModalWindow.this.persistenceService
						.giveAllPlayersFromGame(gameId);

				// post a message for all players in the game
				EventBusPostService.post(allPlayersInGame, ptlccc, ncc);

				for (final ModalWindow mw : ((HomePage)target.getPage())
						.getAllOpenRevealTopLibraryCardWindows())
				{
					mw.close(target);
				}
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
				final Long gameId = game.getId();

				final List<MagicCard> hand = RevealTopLibraryCardModalWindow.this.card.getDeck()
						.reorderMagicCards(
								RevealTopLibraryCardModalWindow.this.persistenceService
										.getAllCardsInHandForAGameAndAPlayer(gameId, p.getId(),
												RevealTopLibraryCardModalWindow.this.card.getDeck()
														.getDeckId()));
				RevealTopLibraryCardModalWindow.this.persistenceService
						.saveOrUpdateAllMagicCards(hand);

				final PutTopLibraryCardToHandCometChannel ptlccc = new PutTopLibraryCardToHandCometChannel(
						gameId, p.getId(), RevealTopLibraryCardModalWindow.this.card.getDeck()
								.getDeckId());

				final NotifierCometChannel ncc = new NotifierCometChannel(
						NotifierAction.PUT_TOP_LIBRARY_CARD_TO_HAND_ACTION, gameId, p.getId(),
						HatchetHarrySession.get().getPlayer().getName(), "", "",
						RevealTopLibraryCardModalWindow.this.card.getTitle(), null, p.getName());

				final List<BigInteger> allPlayersInGame = RevealTopLibraryCardModalWindow.this.persistenceService
						.giveAllPlayersFromGame(gameId);

				// post a message for all players in the game
				EventBusPostService.post(allPlayersInGame, ptlccc, ncc);

				for (final ModalWindow mw : ((HomePage)target.getPage())
						.getAllOpenRevealTopLibraryCardWindows())
				{
					mw.close(target);
				}
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
				final Deck d = RevealTopLibraryCardModalWindow.this.card.getDeck();
				final Long gameId = game.getId();
				final List<MagicCard> hand = d
						.reorderMagicCards(RevealTopLibraryCardModalWindow.this.persistenceService
								.getAllCardsInGraveyardForAGameAndAPlayer(gameId, p.getId(),
										d.getDeckId()));
				RevealTopLibraryCardModalWindow.this.persistenceService
						.saveOrUpdateAllMagicCards(hand);

				final PutTopLibraryCardToGraveyardCometChannel chan = new PutTopLibraryCardToGraveyardCometChannel(
						gameId, p.getId(), d.getDeckId());
				final NotifierCometChannel ncc = new NotifierCometChannel(
						NotifierAction.PUT_TOP_LIBRARY_CARD_TO_GRAVEYARD_ACTION, gameId, p.getId(),
						HatchetHarrySession.get().getPlayer().getName(), "", "",
						RevealTopLibraryCardModalWindow.this.card.getTitle(), null, p.getName());
				final List<BigInteger> allPlayersInGame = RevealTopLibraryCardModalWindow.this.persistenceService
						.giveAllPlayersFromGame(gameId);

				// post a message for all players in the game
				EventBusPostService.post(allPlayersInGame, chan, ncc);

				for (final ModalWindow mw : ((HomePage)target.getPage())
						.getAllOpenRevealTopLibraryCardWindows())
				{
					mw.close(target);
				}
			}
		};
		putToGraveyard.setOutputMarkupId(true).setMarkupId("putToGraveyardFromModalWindow");

		form.add(doNothing, next, putToBattlefield, putToHand, putToGraveyard);
		this.add(form);
	}

	@Required
	public void setPersistenceService(final PersistenceService _persistenceService)
	{
		this.persistenceService = _persistenceService;
	}

}
