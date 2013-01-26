package org.alienlabs.hatchetharry.view.component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.alienlabs.hatchetharry.HatchetHarrySession;
import org.alienlabs.hatchetharry.model.CardZone;
import org.alienlabs.hatchetharry.model.CollectibleCard;
import org.alienlabs.hatchetharry.model.Deck;
import org.alienlabs.hatchetharry.model.Game;
import org.alienlabs.hatchetharry.model.MagicCard;
import org.alienlabs.hatchetharry.model.Player;
import org.alienlabs.hatchetharry.model.Side;
import org.alienlabs.hatchetharry.service.PersistenceService;
import org.alienlabs.hatchetharry.view.clientsideutil.JavaScriptUtils;
import org.alienlabs.hatchetharry.view.page.HomePage;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.injection.Injector;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.protocol.http.servlet.ServletWebRequest;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

public class CreateGameModalWindow extends Panel
{
	private static final long serialVersionUID = -5432292812819537705L;

	@SpringBean
	PersistenceService persistenceService;

	static final Logger LOGGER = LoggerFactory.getLogger(CreateGameModalWindow.class);

	final DropDownChoice<Deck> decks;
	final Player player;
	final Game game;
	final WebMarkupContainer sidePlaceholderParent;

	final HomePage homePage;

	public CreateGameModalWindow(final ModalWindow _modal, final String id, final Player _player,
			final WebMarkupContainer _sidePlaceholderParent, final HomePage hp)
	{
		super(id);
		Injector.get().inject(this);

		this.player = _player;
		this.sidePlaceholderParent = _sidePlaceholderParent;
		this.homePage = hp;

		final Form<String> form = new Form<String>("form");

		final Label chooseDeck = new Label("chooseDeck", "Choose a deck: ");

		final ArrayList<Deck> allDecks = (ArrayList<Deck>)this.persistenceService.getAllDecks();
		final Model<ArrayList<Deck>> decksModel = new Model<ArrayList<Deck>>(allDecks);
		this.decks = new DropDownChoice<Deck>("decks", new Model<Deck>(), decksModel);

		this.game = this.player.getGame();
		final Label gameId = new Label("gameId", "The id of this game is: " + this.game.getId()
				+ ". You'll have to provide it to your opponent(s).");
		HatchetHarrySession.get().setGameId(this.game.getId());

		final Label nameLabel = new Label("nameLabel", "Choose a name: ");
		final Model<String> nameModel = new Model<String>("");
		final RequiredTextField<String> nameInput = new RequiredTextField<String>("name", nameModel);

		final ArrayList<String> allSides = new ArrayList<String>();
		allSides.add("infrared");
		allSides.add("ultraviolet");
		final Model<ArrayList<String>> sidesModel = new Model<ArrayList<String>>(allSides);
		final Label sideLabel = new Label("sideLabel", "Choose your side: ");
		final DropDownChoice<String> sideInput = new DropDownChoice<String>("sideInput",
				new Model<String>(), sidesModel);

		final AjaxButton submit = new AjaxButton("submit", form)
		{
			private static final long serialVersionUID = 5612763286127668L;

			@Override
			protected void onSubmit(final AjaxRequestTarget target, final Form<?> _form)
			{
				_modal.close(target);

				final Game g = CreateGameModalWindow.this.persistenceService
						.getGame(CreateGameModalWindow.this.game.getId());

				CreateGameModalWindow.this.player.setGame(g);

				CreateGameModalWindow.this.player
						.setSide(sideInput.getDefaultModelObjectAsString());
				CreateGameModalWindow.this.player
						.setName(nameInput.getDefaultModelObjectAsString());

				CreateGameModalWindow.this.persistenceService.updateGame(g);

				Deck deck = (Deck)CreateGameModalWindow.this.decks.getDefaultModelObject();
				final List<MagicCard> cards = new ArrayList<MagicCard>();

				final List<String> allTitles = CreateGameModalWindow.this.persistenceService
						.findCollectibleCardTitlesFromDeckArchive(deck.getDeckArchive()
								.getDeckArchiveId());
				for (final String title : allTitles)
				{
					final CollectibleCard cc = CreateGameModalWindow.this.persistenceService
							.findCollectibleCardByName(title);

					final MagicCard card = new MagicCard("cards/" + cc.getTitle() + "_small.jpg",
							"cards/" + cc.getTitle() + ".jpg", "cards/" + cc.getTitle()
									+ "Thumb.jpg", cc.getTitle(), "");
					card.setGameId(g.getId());
					card.setDeck(deck);
					card.setUuidObject(UUID.randomUUID());
					card.setZone(CardZone.LIBRARY);

					CreateGameModalWindow.this.persistenceService.saveOrUpdateCard(card);
					cards.add(card);
				}

				deck.setCards(cards);
				deck.setPlayerId(HatchetHarrySession.get().getPlayer().getId());
				deck = CreateGameModalWindow.this.persistenceService.saveOrUpdateDeck(deck);

				CreateGameModalWindow.this.player.setDeck(deck);
				CreateGameModalWindow.this.persistenceService
						.updatePlayer(CreateGameModalWindow.this.player);
				HatchetHarrySession.get().setPlayer(CreateGameModalWindow.this.player);

				deck.shuffleLibrary();

				final ArrayList<MagicCard> firstCards = new ArrayList<MagicCard>();

				for (int i = 0; i < 7; i++)
				{
					final MagicCard aCard = cards.get(i);
					aCard.setZone(CardZone.HAND);
					CreateGameModalWindow.this.persistenceService.saveOrUpdateCard(aCard);
					firstCards.add(i, aCard);
					HatchetHarrySession.get().addCardIdInHand(i, aCard.getId()); // TODO
																					// remove
																					// this
					deck.getCards().remove(aCard);
				}

				CreateGameModalWindow.this.persistenceService.saveDeck(deck);

				final List<CardPanel> toRemove = HatchetHarrySession.get()
						.getAllCardsInBattleField();
				if ((null != toRemove) && (toRemove.size() > 0))
				{
					for (final CardPanel cp : toRemove)
					{
						target.appendJavaScript("jQuery('#" + cp.getMarkupId() + "').remove();");
						CreateGameModalWindow.LOGGER.info("cp.getMarkupId(): " + cp.getMarkupId());
						HatchetHarrySession.get().addCardInToRemoveList(cp);
					}
					CreateGameModalWindow.this.persistenceService.deleteAllCardsInBattleField();
					HatchetHarrySession.get().removeAllCardsFromBattleField();
				}

				HatchetHarrySession.get().setFirstCardsInHand(firstCards);

				if (HatchetHarrySession.get().isHandDisplayed())
				{
					JavaScriptUtils.updateHand(target);
				}

				final StringBuffer buf = new StringBuffer(
						"jQuery('#joyRidePopup0').remove(); jQuery('[id^=\"menutoggleButton\"]').remove(); jQuery.gritter.add({title : \"You've created a game\", text : \"As soon as a player is connected, you'll be able to play.\", image : 'image/logoh2.gif', sticky : false, time : ''}); ");
				// TODO remove gameId management since we now have Comet
				// channels
				buf.append("window.gameId = " + CreateGameModalWindow.this.game.getId() + "; ");

				CreateGameModalWindow.LOGGER.info("close!");

				final UUID uuid = UUID.randomUUID();
				final SidePlaceholderPanel spp = new SidePlaceholderPanel("firstSidePlaceholder",
						sideInput.getDefaultModelObjectAsString(), hp, uuid);
				final HatchetHarrySession session = HatchetHarrySession.get();
				session.putMySidePlaceholderInSesion(sideInput.getDefaultModelObjectAsString());

				final ServletWebRequest servletWebRequest = (ServletWebRequest)this.getPage()
						.getRequest();
				final HttpServletRequest request = servletWebRequest.getContainerRequest();
				final String jsessionid = request.getRequestedSessionId();

				final SidePlaceholderMoveBehavior spmb = new SidePlaceholderMoveBehavior(spp,
						spp.getUuid(), jsessionid, CreateGameModalWindow.this.homePage,
						CreateGameModalWindow.this.homePage.getDataBoxParent(),
						CreateGameModalWindow.this.game.getId());
				spp.add(spmb);
				spp.setOutputMarkupId(true);

				CreateGameModalWindow.this.sidePlaceholderParent.addOrReplace(spp);
				target.add(CreateGameModalWindow.this.sidePlaceholderParent);

				final int posX = ("infrared".equals(sideInput.getDefaultModelObjectAsString()))
						? 300
						: 900;

				buf.append("window.setTimeout(function() { var card = jQuery(\"#sidePlaceholder"
						+ spp.getUuid() + "\"); " + "card.css(\"position\", \"absolute\"); "
						+ "card.css(\"left\", \"" + posX + "px\"); "
						+ "card.css(\"top\", \"500px\");" + "jQuery(\"#" + spp.getMarkupId()
						+ "\").draggable({ handle : \"#handleImage" + uuid + "\" });"
						+ "jQuery(\"#handleImage" + uuid + "\").data(\"url\",\""
						+ spmb.getCallbackUrl() + "\");" + " }, 3000); ");

				target.appendJavaScript(buf.toString());

				CreateGameModalWindow.this.homePage.getPlayCardBehavior().setSide(
						sideInput.getDefaultModelObjectAsString());

				session.getPlayer().setSide(sideInput.getDefaultModelObjectAsString());
				session.getPlayer().setName(nameInput.getDefaultModelObjectAsString());
				session.setMySidePosX(posX);
				session.setMySidePosY(500);

				spp.setPosX(posX);
				spp.setPosY(500);
				session.setMySidePlaceholder(spp);

				final Side s = new Side();
				s.setGame(CreateGameModalWindow.this.persistenceService.getGame(session.getGameId()));
				s.setSide(sideInput.getDefaultModelObjectAsString());
				s.setUuid(spp.getUuid().toString());
				s.setWicketId("firstSidePlaceholder");
				s.setX(Long.valueOf(posX));
				s.setY(Long.valueOf(500));
				CreateGameModalWindow.this.persistenceService.saveSide(s);

				final DataBox db = new DataBox("dataBox", Long.valueOf(g.getId()));
				CreateGameModalWindow.this.homePage.getDataBoxParent().addOrReplace(db);
				db.setOutputMarkupId(true);
				target.add(CreateGameModalWindow.this.homePage.getDataBoxParent());

				session.setGameCreated();
				session.resetCardsInGraveyard();

				if (session.isGraveyardDisplayed())
				{
					JavaScriptUtils.updateGraveyard(target);
				}
			}

			@Override
			protected void onError(final AjaxRequestTarget target, final Form<?> _form)
			{
				CreateGameModalWindow.LOGGER.error("ERROR");
			}
		};
		submit.setOutputMarkupId(true);
		submit.setMarkupId("createSubmit" + _player.getId());

		form.add(chooseDeck, this.decks, gameId, nameLabel, nameInput, sideLabel, sideInput, submit);

		this.add(form);
	}

	@Required
	public void setPersistenceService(final PersistenceService _persistenceService)
	{
		this.persistenceService = _persistenceService;
	}

}
