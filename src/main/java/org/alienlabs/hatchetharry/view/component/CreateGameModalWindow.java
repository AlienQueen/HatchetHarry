package org.alienlabs.hatchetharry.view.component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.alienlabs.hatchetharry.HatchetHarrySession;
import org.alienlabs.hatchetharry.model.Deck;
import org.alienlabs.hatchetharry.model.Game;
import org.alienlabs.hatchetharry.model.MagicCard;
import org.alienlabs.hatchetharry.model.Player;
import org.alienlabs.hatchetharry.model.Side;
import org.alienlabs.hatchetharry.service.PersistenceService;
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
			final WebMarkupContainer _handCardsParent,
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

		this.game = this.player.getGames().iterator().next();
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
				final Set<Game> games = new HashSet<Game>();
				games.add(g);
				g.setPlaceholderId(1);
				CreateGameModalWindow.this.persistenceService.saveOrUpdateGame(g);
				HatchetHarrySession.get().setGameId(g.getId());

				CreateGameModalWindow.this.player.setGames(games);
				CreateGameModalWindow.LOGGER.info("### "
						+ sideInput.getDefaultModelObjectAsString());
				CreateGameModalWindow.this.player
						.setSide(sideInput.getDefaultModelObjectAsString());
				CreateGameModalWindow.this.player
						.setName(nameInput.getDefaultModelObjectAsString());
				CreateGameModalWindow.this.persistenceService
						.updatePlayer(CreateGameModalWindow.this.player);

				final Deck deck = (Deck)CreateGameModalWindow.this.decks.getDefaultModelObject();
				final List<MagicCard> allCards = CreateGameModalWindow.this.persistenceService
						.getAllCardsFromDeck(deck.getId());
				deck.setCards(allCards);
				deck.setPlayerId(HatchetHarrySession.get().getPlayer().getId());
				deck.shuffleLibrary();

				final ArrayList<MagicCard> firstCards = new ArrayList<MagicCard>();

				for (int i = 0; i < 7; i++)
				{
					firstCards.add(i, allCards.get(i));
					HatchetHarrySession.get().addCardIdInHand(i, allCards.get(i).getId());
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
				HatchetHarrySession.get().setDeck(deck);

				final HandComponent gallery = new HandComponent("gallery");
				_handCardsParent.addOrReplace(gallery);
				target.add(_handCardsParent);

				target.appendJavaScript("jQuery('#joyRidePopup0').remove(); jQuery('[id^=\"menutoggleButton\"]').remove(); jQuery.gritter.add({title : \"You've created a game\", text : \"As soon as a player is connected, you'll be able to play.\", image : 'image/logoh2.gif', sticky : false, time : ''}); var theInt = null; var $crosslink, $navthumb; var curclicked = 0; theInterval = function(cur) { if (typeof cur != 'undefined') curclicked = cur; $crosslink.removeClass('active-thumb'); $navthumb.eq(curclicked).parent().addClass('active-thumb'); jQuery('.stripNav ul li a').eq(curclicked).trigger('click'); $crosslink.removeClass('active-thumb'); $navthumb.eq(curclicked).parent().addClass('active-thumb'); jQuery('.stripNav ul li a').eq(curclicked).trigger('click'); curclicked++; if (6 == curclicked) curclicked = 0; }; jQuery('#main-photo-slider').codaSlider(); $navthumb = jQuery('.nav-thumb'); $crosslink = jQuery('.cross-link'); $navthumb.click(function() { var $this = jQuery(this); theInterval($this.parent().attr('href').slice(1) - 1); return false; }); theInterval(); ");

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
						sideInput.getDefaultModelObjectAsString(),
						CreateGameModalWindow.this.homePage.getDataBoxParent(),
						CreateGameModalWindow.this.game.getId());
				spp.add(spmb);
				spp.setOutputMarkupId(true);

				CreateGameModalWindow.this.sidePlaceholderParent.addOrReplace(spp);
				target.add(CreateGameModalWindow.this.sidePlaceholderParent);

				final int posX = ("infrared".equals(sideInput.getDefaultModelObjectAsString()))
						? 300
						: 900;

				target.appendJavaScript("window.setTimeout(function() { var card = jQuery(\"#sidePlaceholder"
						+ spp.getUuid()
						+ "\"); "
						+ "card.css(\"position\", \"absolute\"); "
						+ "card.css(\"left\", \""
						+ posX
						+ "px\"); "
						+ "card.css(\"top\", \"500px\");"
						+ "jQuery(\"#"
						+ spp.getMarkupId()
						+ "\").draggable({ handle : \"#handleImage"
						+ uuid
						+ "\" });"
						+ "jQuery(\"#handleImage"
						+ uuid
						+ "\").data(\"url\",\""
						+ spmb.getCallbackUrl() + "\");" + " }, 2000);");

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

				session.setGameCreated();
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
