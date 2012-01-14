package org.alienlabs.hatchetharry.view.component;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.alienlabs.hatchetharry.HatchetHarrySession;
import org.alienlabs.hatchetharry.model.Deck;
import org.alienlabs.hatchetharry.model.Game;
import org.alienlabs.hatchetharry.model.MagicCard;
import org.alienlabs.hatchetharry.model.Player;
import org.alienlabs.hatchetharry.service.PersistenceService;
import org.alienlabs.hatchetharry.view.page.HomePage;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.injection.web.InjectorHolder;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.protocol.http.servlet.ServletWebRequest;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.atmosphere.cpr.BroadcastFilter;
import org.atmosphere.cpr.Meteor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

public class JoinGameModalWindow extends Panel
{
	private static final long serialVersionUID = -5432292812819537705L;

	@SpringBean
	transient PersistenceService persistenceService;

	static final Logger logger = LoggerFactory.getLogger(JoinGameModalWindow.class);

	final DropDownChoice<Deck> decks;
	final RequiredTextField<Long> gameIdInput;

	Player player;
	HomePage hp;

	public JoinGameModalWindow(final ModalWindow _modal, final String id, final Player _player,
			final WebMarkupContainer _handCardsParent, final CharSequence _url,
			final WebMarkupContainer _dataBoxParent, final HomePage _hp,
			final WebMarkupContainer sidePlaceholderParent)
	{
		super(id);
		InjectorHolder.getInjector().inject(this);

		this.player = _player;
		this.hp = _hp;

		final Form<String> form = new Form<String>("form");

		final Label chooseDeck = new Label("chooseDeck", "Choose a deck: ");

		final ArrayList<Deck> allDecks = (ArrayList<Deck>)this.persistenceService.getAllDecks();
		final Model<ArrayList<Deck>> decksModel = new Model<ArrayList<Deck>>(allDecks);
		this.decks = new DropDownChoice<Deck>("decks", new Model<Deck>(), decksModel);
		this.decks.setRequired(true);

		final Label nameLabel = new Label("nameLabel", "Choose a name: ");
		final Model<String> inputName = new Model<String>("");
		final RequiredTextField<String> name = new RequiredTextField<String>("name", inputName);

		final ArrayList<String> allSides = new ArrayList<String>();
		allSides.add("infrared");
		allSides.add("ultraviolet");
		final Model<ArrayList<String>> sidesModel = new Model<ArrayList<String>>(allSides);
		final Label sideLabel = new Label("sideLabel", "Choose your side: ");
		final DropDownChoice<String> sideInput = new DropDownChoice<String>("sideInput",
				new Model<String>(), sidesModel);
		sideInput.setRequired(true);

		this.player.getGame().get(0);
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
				final Deck deck = (Deck)JoinGameModalWindow.this.decks.getDefaultModelObject();
				final List<MagicCard> allCards = JoinGameModalWindow.this.persistenceService
						.getAllCardsFromDeck(deck.getId());
				deck.setCards(allCards);
				deck.shuffleLibrary();

				final List<MagicCard> firstCards = new ArrayList<MagicCard>();

				for (int i = 0; i < 7; i++)
				{
					firstCards.add(i, allCards.get(i));
					HatchetHarrySession.get().addCardIdInHand(i, i);
					deck.getCards().remove(allCards.get(i));
				}

				final List<CardPanel> toRemove = HatchetHarrySession.get()
						.getAllCardsInBattleField();
				if ((null != toRemove) && (toRemove.size() > 0))
				{
					for (final CardPanel cp : toRemove)
					{
						target.appendJavascript("jQuery('#" + cp.getMarkupId() + "').remove();");
						JoinGameModalWindow.logger.info("cp.getMarkupId(): " + cp.getMarkupId());
						HatchetHarrySession.get().addCardInToRemoveList(cp);
					}
					JoinGameModalWindow.this.persistenceService.deleteAllCardsInBattleField();
					HatchetHarrySession.get().removeAllCardsFromBattleField();
				}

				HatchetHarrySession.get().setFirstCardsInHand(firstCards);
				HatchetHarrySession.get().setDeck(deck);

				final Game g = JoinGameModalWindow.this.persistenceService.getGame(Long
						.valueOf(JoinGameModalWindow.this.gameIdInput
								.getDefaultModelObjectAsString()));
				final List<Player> players = g.getPlayers();
				players.add(JoinGameModalWindow.this.player);
				final List<Game> games = new ArrayList<Game>();
				games.add(g);
				JoinGameModalWindow.this.player.setGame(games);
				JoinGameModalWindow.this.persistenceService.updateGame(g);
				JoinGameModalWindow.this.persistenceService
						.updatePlayer(JoinGameModalWindow.this.player);

				final UpdateDataBoxBehavior behavior = new UpdateDataBoxBehavior(_dataBoxParent,
						g.getId(), JoinGameModalWindow.this.hp);
				final DataBox dataBox = new DataBox("dataBox",
						Long.valueOf(JoinGameModalWindow.this.gameIdInput
								.getDefaultModelObjectAsString()), _dataBoxParent,
						JoinGameModalWindow.this.hp);
				HatchetHarrySession.get().setDataBox(dataBox);
				dataBox.add(behavior);
				_dataBoxParent.addOrReplace(dataBox);
				target.addComponent(_dataBoxParent);

				final HandComponent gallery = new HandComponent("gallery");
				_handCardsParent.addOrReplace(gallery);
				target.addComponent(_handCardsParent);

				_modal.close(target);

				target.appendJavascript("jQuery('#tourcontrols').remove(); jQuery('[id^=\"menutoggleButton\"]').remove(); jQuery.gritter.add({title : \"You have requested to join a game\", text : \"You can start playing right now!\", image : 'image/logoh2.gif', sticky : false, time : ''}); var theInt = null; var $crosslink, $navthumb; var curclicked = 0; theInterval = function(cur) { if (typeof cur != 'undefined') curclicked = cur; $crosslink.removeClass('active-thumb'); $navthumb.eq(curclicked).parent().addClass('active-thumb'); jQuery('.stripNav ul li a').eq(curclicked).trigger('click'); $crosslink.removeClass('active-thumb'); $navthumb.eq(curclicked).parent().addClass('active-thumb'); jQuery('.stripNav ul li a').eq(curclicked).trigger('click'); curclicked++; if (6 == curclicked) curclicked = 0; }; jQuery('#main-photo-slider').codaSlider(); $navthumb = jQuery('.nav-thumb'); $crosslink = jQuery('.cross-link'); $navthumb.click(function() { var $this = jQuery(this); theInterval($this.parent().attr('href').slice(1) - 1); return false; }); theInterval(); wicketAjaxGet('"
						+ _url
						+ "&text=2&title=2', function() { }, null, null); wicketAjaxGet('"
						+ behavior.getUrl()
						+ "&jsessionid="
						+ this.getParent().getPage().getSession().getId()
						+ "', function() { }, null, null);");

				JoinGameModalWindow.logger.info("close!");

				final ServletWebRequest servletWebRequest = (ServletWebRequest)this.getPage()
						.getRequest();
				final HttpServletRequest request = servletWebRequest.getHttpServletRequest();
				final String jsessionid = request.getRequestedSessionId();

				final SidePlaceholderPanel spp = new SidePlaceholderPanel("secondSidePlaceholder",
						sideInput.getDefaultModelObjectAsString(), JoinGameModalWindow.this.hp,
						UUID.randomUUID());
				spp.setOutputMarkupId(true);
				spp.add(new SidePlaceholderMoveBehavior(sidePlaceholderParent, spp.getUuid(),
						jsessionid, JoinGameModalWindow.this.hp, sideInput
								.getDefaultModelObjectAsString()));

				final HatchetHarrySession h = ((HatchetHarrySession.get()));
				h.putMySidePlaceholderInSesion(sideInput.getDefaultModelObjectAsString());

				sidePlaceholderParent.addOrReplace(spp);
				target.addComponent(sidePlaceholderParent);

				final int posX = ("infrared".equals(sideInput.getDefaultModelObjectAsString()))
						? 300
						: 900;

				target.appendJavascript("jQuery(document).ready(function() { var card = jQuery('#sidePlaceholder"
						+ spp.getUuid()
						+ "'); "
						+ "card.css('position', 'absolute'); "
						+ "card.css('left', '" + posX + "px'); " + "card.css('top', '500px'); });");

				final String opponentSide = ("infrared".equals(sideInput
						.getDefaultModelObjectAsString())) ? "ultraviolet" : "infrared";

				final SidePlaceholderPanel spp2 = new SidePlaceholderPanel("firstSidePlaceholder",
						opponentSide, JoinGameModalWindow.this.hp, UUID.randomUUID());
				spp2.setOutputMarkupId(true);
				spp2.add(new SidePlaceholderMoveBehavior(JoinGameModalWindow.this.hp
						.getSecondSidePlaceholderParent(), spp2.getUuid(), jsessionid,
						JoinGameModalWindow.this.hp, opponentSide));

				JoinGameModalWindow.this.hp.getFirstSidePlaceholderParent().addOrReplace(spp2);
				target.addComponent(JoinGameModalWindow.this.hp.getFirstSidePlaceholderParent());

				final int posX2 = (posX == 300) ? 900 : 300;
				target.appendJavascript("jQuery(document).ready(function() { var card = jQuery('#sidePlaceholder"
						+ spp2.getUuid()
						+ "'); "
						+ "card.css('position', 'absolute'); "
						+ "card.css('left', '" + posX2 + "px'); " + "card.css('top', '500px'); });");

				final Meteor meteor = Meteor
						.build(request, new LinkedList<BroadcastFilter>(), null);
				final String message = sideInput.getDefaultModelObjectAsString() + "|||||"
						+ jsessionid + "|||||" + spp.getUuid();
				JoinGameModalWindow.logger.info("# message: " + message);
				meteor.broadcast(message);

				JoinGameModalWindow.this.hp.getPlayCardBehavior().setSide(
						sideInput.getDefaultModelObjectAsString());
				HatchetHarrySession.get().getPlayer()
						.setSide(sideInput.getDefaultModelObjectAsString());
				HatchetHarrySession.get().setMySidePosX(posX);
				HatchetHarrySession.get().setMySidePosY(500);
			}
		};
		submit.setOutputMarkupId(true);
		submit.setMarkupId("joinSubmit" + _player.getId());

		form.add(chooseDeck, this.decks, nameLabel, name, sideLabel, sideInput, gameIdLabel,
				this.gameIdInput, submit);

		this.add(form);
	}

	@Required
	public void setPersistenceService(final PersistenceService _persistenceService)
	{
		this.persistenceService = _persistenceService;
	}

}
