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
import org.alienlabs.hatchetharry.model.channel.JoinGameCometChannel;
import org.alienlabs.hatchetharry.model.channel.JoinGameNotificationCometChannel;
import org.alienlabs.hatchetharry.service.PersistenceService;
import org.alienlabs.hatchetharry.view.page.HomePage;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.atmosphere.EventBus;
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

public class JoinGameModalWindow extends Panel
{
	private static final long serialVersionUID = -5432292812819537705L;

	@SpringBean
	PersistenceService persistenceService;

	static final Logger LOGGER = LoggerFactory.getLogger(JoinGameModalWindow.class);

	final DropDownChoice<Deck> decks;
	final RequiredTextField<Long> gameIdInput;
	final WebMarkupContainer dataBoxParent;

	Player player;
	HomePage hp;

	public JoinGameModalWindow(final ModalWindow _modal, final String id, final Player _player,
			final WebMarkupContainer _handCardsParent, final WebMarkupContainer _dataBoxParent,
			final HomePage _hp)
	{
		super(id);
		Injector.get().inject(this);

		this.player = _player;
		this.hp = _hp;
		this.dataBoxParent = _dataBoxParent;

		final Form<String> form = new Form<String>("form");

		final Label chooseDeck = new Label("chooseDeck", "Choose a deck: ");

		final ArrayList<Deck> allDecks = (ArrayList<Deck>)this.persistenceService.getAllDecks();
		final Model<ArrayList<Deck>> decksModel = new Model<ArrayList<Deck>>(allDecks);
		this.decks = new DropDownChoice<Deck>("decks", new Model<Deck>(), decksModel);
		this.decks.setRequired(true);

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
		sideInput.setRequired(true);

		final Label gameIdLabel = new Label("gameIdLabel",
				"Please provide the game id given by your opponent: ");
		final Model<Long> gameId = new Model<Long>(0l);
		this.gameIdInput = new RequiredTextField<Long>("gameIdInput", gameId);

		final AjaxButton submit = new AjaxButton("submit", form)
		{
			private static final long serialVersionUID = 561276328198198L;

			@Override
			protected void onSubmit(final AjaxRequestTarget target, final Form<?> _form)
			{

				final Long _id = Long.valueOf(JoinGameModalWindow.this.gameIdInput
						.getDefaultModelObjectAsString());
				Game game = JoinGameModalWindow.this.persistenceService.getGame(_id);
				if (null == game)
				{
					_modal.close(target);
					target.appendJavaScript("alert('The selected game (id= "
							+ JoinGameModalWindow.this.gameIdInput.getDefaultModelObjectAsString()
							+ ") does not exist!');");
					return;
				}

				final HatchetHarrySession session = HatchetHarrySession.get();

				JoinGameModalWindow.this.player = JoinGameModalWindow.this.persistenceService
						.getPlayer(session.getPlayer().getId());

				final Game gameToRemove = JoinGameModalWindow.this.persistenceService
						.getGame(session.getGameId());
				if (null != gameToRemove)
				{
					if (JoinGameModalWindow.this.player.getGames().remove(gameToRemove))
					{
						JoinGameModalWindow.this.persistenceService
								.saveOrUpdatePlayer(JoinGameModalWindow.this.player);
					}
					gameToRemove.getPlayers().remove(JoinGameModalWindow.this.player);
					JoinGameModalWindow.this.persistenceService.saveOrUpdateGame(gameToRemove);
				}

				session.setGameId(_id);
				JoinGameModalWindow.LOGGER.info("~~~ " + _id);

				final Deck deck = (Deck)JoinGameModalWindow.this.decks.getDefaultModelObject();
				final List<MagicCard> allCards = JoinGameModalWindow.this.persistenceService
						.getAllCardsFromDeck(deck.getId());
				deck.setCards(allCards);


				deck.setPlayerId(JoinGameModalWindow.this.player.getId());
				deck.shuffleLibrary();

				final ArrayList<MagicCard> firstCards = new ArrayList<MagicCard>();

				for (int i = 0; i < 7; i++)
				{
					firstCards.add(i, allCards.get(i));
					HatchetHarrySession.get().addCardIdInHand(i, i);
					deck.getCards().remove(allCards.get(i));
				}

				JoinGameModalWindow.this.persistenceService.saveDeck(deck);

				final List<CardPanel> cardToRemove = HatchetHarrySession.get()
						.getAllCardsInBattleField();
				final StringBuffer javaScript = new StringBuffer();

				if ((null != cardToRemove) && (cardToRemove.size() > 0))
				{
					for (final CardPanel cp : cardToRemove)
					{
						javaScript.append("jQuery('#" + cp.getMarkupId() + "').remove();");
						JoinGameModalWindow.LOGGER.info("cp.getMarkupId(): " + cp.getMarkupId());
						HatchetHarrySession.get().addCardInToRemoveList(cp);
					}
					JoinGameModalWindow.this.persistenceService.deleteAllCardsInBattleField();
					HatchetHarrySession.get().removeAllCardsFromBattleField();
				}

				session.setFirstCardsInHand(firstCards);
				session.setDeck(deck);

				JoinGameModalWindow.this.player.setSide(sideInput.getDefaultModelObjectAsString());
				JoinGameModalWindow.this.player.setName(nameInput.getDefaultModelObjectAsString());
				final Set<Game> newGames = new HashSet<Game>();
				newGames.add(game);
				JoinGameModalWindow.this.player.setGames(newGames);

				game = JoinGameModalWindow.this.persistenceService.getGame(game.getId());
				final Set<Player> players = game.getPlayers();
				players.add(JoinGameModalWindow.this.player);
				game.setPlayers(players);
				JoinGameModalWindow.this.persistenceService.saveOrUpdateGame(game);

				JoinGameModalWindow.this.persistenceService
						.updatePlayer(JoinGameModalWindow.this.player);

				final DataBox dataBox = new DataBox("dataBox",
						Long.valueOf(JoinGameModalWindow.this.gameIdInput
								.getDefaultModelObjectAsString()), JoinGameModalWindow.this.hp);
				final UpdateDataBoxBehavior behavior = new UpdateDataBoxBehavior(game.getId(),
						JoinGameModalWindow.this.hp, dataBox);
				dataBox.setOutputMarkupId(true);
				dataBox.add(behavior);
				JoinGameModalWindow.this.hp.getDataBoxParent().addOrReplace(dataBox);

				final HandComponent gallery = new HandComponent("gallery");
				_handCardsParent.addOrReplace(gallery);

				javaScript
						.append("jQuery('#joyRidePopup0').remove(); jQuery('[id^=\"menutoggleButton\"]').remove(); jQuery.gritter.add({title : \"You have requested to join a game\", text : \"You can start playing right now!\", image : 'image/logoh2.gif', sticky : false, time : ''}); "
								+ "var theInt = null; var $crosslink, $navthumb; var curclicked = 0; theInterval = function(cur) { if (typeof cur != 'undefined') curclicked = cur; $crosslink.removeClass('active-thumb'); $navthumb.eq(curclicked).parent().addClass('active-thumb'); jQuery('.stripNav ul li a').eq(curclicked).trigger('click'); $crosslink.removeClass('active-thumb'); $navthumb.eq(curclicked).parent().addClass('active-thumb'); jQuery('.stripNav ul li a').eq(curclicked).trigger('click'); curclicked++; if (6 == curclicked) curclicked = 0; }; jQuery('#main-photo-slider').codaSlider(); $navthumb = jQuery('.nav-thumb'); $crosslink = jQuery('.cross-link'); $navthumb.click(function() { var $this = jQuery(this); theInterval($this.parent().attr('href').slice(1) - 1); return false; }); theInterval(); ");

				JoinGameModalWindow.LOGGER.info("close!");

				final ServletWebRequest servletWebRequest = (ServletWebRequest)this.getPage()
						.getRequest();
				final HttpServletRequest request = servletWebRequest.getContainerRequest();
				final String jsessionid = request.getRequestedSessionId();

				final SidePlaceholderPanel spp = new SidePlaceholderPanel("secondSidePlaceholder",
						sideInput.getDefaultModelObjectAsString(), JoinGameModalWindow.this.hp,
						UUID.randomUUID());
				spp.add(new SidePlaceholderMoveBehavior(spp, spp.getUuid(), jsessionid,
						JoinGameModalWindow.this.hp, sideInput.getDefaultModelObjectAsString(),
						JoinGameModalWindow.this.hp.getDataBoxParent(), session.getGameId()));
				JoinGameModalWindow.LOGGER.info("gameId in JoinGameModalWindow: "
						+ session.getGameId());
				spp.setOutputMarkupId(true);

				session.putMySidePlaceholderInSesion(sideInput.getDefaultModelObjectAsString());

				JoinGameModalWindow.this.hp.getSecondSidePlaceholderParent().addOrReplace(spp);

				final int posX = ("infrared".equals(sideInput.getDefaultModelObjectAsString()))
						? 300
						: 900;

				javaScript
						.append("window.setTimeout(function() { var card = jQuery('#sidePlaceholder"
								+ spp.getUuid()
								+ "'); "
								+ "card.css('position', 'absolute'); "
								+ "card.css('left', '"
								+ posX
								+ "px'); "
								+ "card.css('top', '500px'); }, 2000);");

				final String opponentSide = ("infrared".equals(sideInput
						.getDefaultModelObjectAsString())) ? "ultraviolet" : "infrared";

				final SidePlaceholderPanel spp2 = new SidePlaceholderPanel("firstSidePlaceholder",
						opponentSide, JoinGameModalWindow.this.hp, UUID.randomUUID());
				spp2.add(new SidePlaceholderMoveBehavior(spp2, spp2.getUuid(), jsessionid,
						JoinGameModalWindow.this.hp, opponentSide, JoinGameModalWindow.this.hp
								.getDataBoxParent(), session.getGameId()));
				spp2.setOutputMarkupId(true);

				JoinGameModalWindow.this.hp.getFirstSidePlaceholderParent().addOrReplace(spp2);

				final int posX2 = (posX == 300) ? 900 : 300;
				javaScript
						.append("window.setTimeout(function() { var card = jQuery('#sidePlaceholder"
								+ spp2.getUuid()
								+ "'); "
								+ "card.css('position', 'absolute'); "
								+ "card.css('left', '"
								+ posX2
								+ "px'); "
								+ "card.css('top', '500px'); }, 2000); ");

				javaScript.append("window.gameId = " + session.getGameId() + "; ");

				JoinGameModalWindow.this.hp.getPlayCardBehavior().setSide(
						sideInput.getDefaultModelObjectAsString());
				HatchetHarrySession.get().getPlayer()
						.setSide(sideInput.getDefaultModelObjectAsString());
				HatchetHarrySession.get().getPlayer()
						.setName(nameInput.getDefaultModelObjectAsString());
				session.setMySidePosX(posX);
				session.setMySidePosY(500);

				final Side s = new Side();
				s.setGame(game);
				s.setSide(sideInput.getDefaultModelObjectAsString());
				s.setUuid(spp.getUuid().toString());
				s.setWicketId("secondSidePlaceholder");
				s.setX(Long.valueOf(posX));
				s.setY(Long.valueOf(500));
				JoinGameModalWindow.this.persistenceService.saveSide(s);

				spp.setPosX(posX);
				spp.setPosY(500);
				session.setMySidePlaceholder(spp);
				spp2.setPosX(posX2);
				spp2.setPosY(500);
				session.setMySidePlaceholder(spp2);

				session.setGameCreated();

				_modal.close(target);
				target.add(JoinGameModalWindow.this.hp.getDataBoxParent());
				target.add(_handCardsParent);
				target.add(JoinGameModalWindow.this.hp.getSecondSidePlaceholderParent());
				target.add(JoinGameModalWindow.this.hp.getFirstSidePlaceholderParent());
				target.appendJavaScript(javaScript.toString());

				final JoinGameCometChannel jgcc = new JoinGameCometChannel(
						sideInput.getDefaultModelObjectAsString(), jsessionid, spp.getUuid(),
						Long.valueOf(posX), 500l);
				EventBus.get().post(jgcc);

				final JoinGameNotificationCometChannel jgncc = new JoinGameNotificationCometChannel(
						HatchetHarrySession.get().getPlayer().getName(), jsessionid,
						HatchetHarrySession.get().getGameId());
				EventBus.get().post(jgncc);
			}

			@Override
			protected void onError(final AjaxRequestTarget target, final Form<?> _form)
			{
			}
		};
		submit.setOutputMarkupId(true);
		submit.setMarkupId("joinSubmit" + _player.getId());

		form.add(chooseDeck, this.decks, nameLabel, nameInput, sideLabel, sideInput, gameIdLabel,
				this.gameIdInput, submit);

		this.add(form);
	}

	@Required
	public void setPersistenceService(final PersistenceService _persistenceService)
	{
		this.persistenceService = _persistenceService;
	}

}
