package org.alienlabs.hatchetharry.view.component;

import java.util.ArrayList;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

public class CreateGameModalWindow extends Panel
{
	private static final long serialVersionUID = -5432292812819537705L;

	@SpringBean
	transient PersistenceService persistenceService;

	static final Logger logger = LoggerFactory.getLogger(CreateGameModalWindow.class);

	final DropDownChoice<Deck> decks;
	final Player player;
	final Game game;
	final WebMarkupContainer sidePlaceholderParent;

	final HomePage homePage;

	public CreateGameModalWindow(final ModalWindow _modal, final String id, final Player _player,
			final WebMarkupContainer _handCardsParent, final CharSequence _url,
			final WebMarkupContainer _sidePlaceholderParent, final HomePage hp)
	{
		super(id);
		InjectorHolder.getInjector().inject(this);

		this.player = _player;
		this.sidePlaceholderParent = _sidePlaceholderParent;
		this.homePage = hp;

		final Form<String> form = new Form<String>("form");

		final Label chooseDeck = new Label("chooseDeck", "Choose a deck: ");

		final ArrayList<Deck> allDecks = (ArrayList<Deck>)this.persistenceService.getAllDecks();
		final Model<ArrayList<Deck>> decksModel = new Model<ArrayList<Deck>>(allDecks);
		this.decks = new DropDownChoice<Deck>("decks", new Model<Deck>(), decksModel);

		this.game = this.player.getGame().get(0);
		final Label gameId = new Label("gameId", "The id of this game is: " + this.game.getId()
				+ ". You'll have to provide it to your opponent(s).");

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

		final AjaxButton submit = new AjaxButton("submit", form)
		{
			private static final long serialVersionUID = 5612763286127668L;

			@Override
			protected void onSubmit(final AjaxRequestTarget target, final Form<?> _form)
			{
				final Game g = CreateGameModalWindow.this.persistenceService
						.getGame(CreateGameModalWindow.this.game.getId());
				final List<Game> games = new ArrayList<Game>();
				games.add(g);
				CreateGameModalWindow.this.player.setGame(games);
				CreateGameModalWindow.this.persistenceService
						.updatePlayer(CreateGameModalWindow.this.player);

				final Deck deck = (Deck)CreateGameModalWindow.this.decks.getDefaultModelObject();
				final List<MagicCard> allCards = CreateGameModalWindow.this.persistenceService
						.getAllCardsFromDeck(deck.getId());
				deck.setCards(allCards);
				deck.shuffleLibrary();

				final List<MagicCard> firstCards = new ArrayList<MagicCard>();

				for (int i = 0; i < 7; i++)
				{
					firstCards.add(i, allCards.get(i));
					HatchetHarrySession.get().addCardIdInHand(i, allCards.get(i).getId());
				}

				final List<CardPanel> toRemove = HatchetHarrySession.get()
						.getAllCardsInBattleField();
				if ((null != toRemove) && (toRemove.size() > 0))
				{
					for (final CardPanel cp : toRemove)
					{
						target.appendJavascript("jQuery('#" + cp.getMarkupId() + "').remove();");
						CreateGameModalWindow.logger.info("cp.getMarkupId(): " + cp.getMarkupId());
						HatchetHarrySession.get().addCardInToRemoveList(cp);
					}
					CreateGameModalWindow.this.persistenceService.deleteAllCardsInBattleField();
					HatchetHarrySession.get().removeAllCardsFromBattleField();
				}

				HatchetHarrySession.get().setFirstCardsInHand(firstCards);
				HatchetHarrySession.get().setDeck(deck);

				final HandComponent gallery = new HandComponent("gallery");
				_handCardsParent.addOrReplace(gallery);
				target.addComponent(_handCardsParent);

				_modal.close(target);

				target.appendJavascript("jQuery('#tourcontrols').remove(); jQuery('[id^=\"menutoggleButton\"]').remove(); jQuery.gritter.add({title : \"You've created a game\", text : \"As soon as a player is connected, you'll be able to play.\", image : 'image/logoh2.gif', sticky : false, time : ''}); var theInt = null; var $crosslink, $navthumb; var curclicked = 0; theInterval = function(cur) { if (typeof cur != 'undefined') curclicked = cur; $crosslink.removeClass('active-thumb'); $navthumb.eq(curclicked).parent().addClass('active-thumb'); jQuery('.stripNav ul li a').eq(curclicked).trigger('click'); $crosslink.removeClass('active-thumb'); $navthumb.eq(curclicked).parent().addClass('active-thumb'); jQuery('.stripNav ul li a').eq(curclicked).trigger('click'); curclicked++; if (6 == curclicked) curclicked = 0; }; jQuery('#main-photo-slider').codaSlider(); $navthumb = jQuery('.nav-thumb'); $crosslink = jQuery('.cross-link'); $navthumb.click(function() { var $this = jQuery(this); theInterval($this.parent().attr('href').slice(1) - 1); return false; }); theInterval(); wicketAjaxGet('"
						+ _url + "&text=1&title=1', function() { }, null, null);");

				CreateGameModalWindow.logger.info("close!");

				final SidePlaceholderPanel spp = new SidePlaceholderPanel("firstSidePlaceholder",
						sideInput.getDefaultModelObjectAsString(), hp, UUID.randomUUID());
				final HatchetHarrySession h = ((HatchetHarrySession.get()));
				h.putMySidePlaceholderInSesion(sideInput.getDefaultModelObjectAsString());

				final ServletWebRequest servletWebRequest = (ServletWebRequest)this.getPage()
						.getRequest();
				final HttpServletRequest request = servletWebRequest.getHttpServletRequest();
				final String jsessionid = request.getRequestedSessionId();
				spp.add(new SidePlaceholderMoveBehavior(
						CreateGameModalWindow.this.sidePlaceholderParent, spp.getUuid(),
						jsessionid, CreateGameModalWindow.this.homePage, sideInput
								.getDefaultModelObjectAsString()));
				spp.setOutputMarkupId(true);

				CreateGameModalWindow.this.sidePlaceholderParent.addOrReplace(spp);
				target.addComponent(CreateGameModalWindow.this.sidePlaceholderParent);

				final int posX = ("infrared".equals(sideInput.getDefaultModelObjectAsString()))
						? 300
						: 900;

				target.appendJavascript("jQuery(document).ready(function() { var card = jQuery(\"#sidePlaceholder"
						+ spp.getUuid()
						+ "\"); "
						+ "card.css(\"position\", \"absolute\"); "
						+ "card.css(\"left\", \""
						+ posX
						+ "px\"); "
						+ "card.css(\"top\", \"500px\"); });");

				CreateGameModalWindow.this.homePage.getPlayCardBehavior().setSide(
						sideInput.getDefaultModelObjectAsString());

				HatchetHarrySession.get().getPlayer()
						.setSide(sideInput.getDefaultModelObjectAsString());
				HatchetHarrySession.get().setMySidePosX(posX);
				HatchetHarrySession.get().setMySidePosY(500);
			}
		};
		submit.setOutputMarkupId(true);
		submit.setMarkupId("createSubmit" + _player.getId());

		form.add(chooseDeck, this.decks, gameId, nameLabel, name, sideLabel, sideInput, submit);

		this.add(form);
	}

	@Required
	public void setPersistenceService(final PersistenceService _persistenceService)
	{
		this.persistenceService = _persistenceService;
	}

}
