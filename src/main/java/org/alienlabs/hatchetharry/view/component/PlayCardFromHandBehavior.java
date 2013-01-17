package org.alienlabs.hatchetharry.view.component;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.alienlabs.hatchetharry.HatchetHarryApplication;
import org.alienlabs.hatchetharry.HatchetHarrySession;
import org.alienlabs.hatchetharry.model.CardZone;
import org.alienlabs.hatchetharry.model.Game;
import org.alienlabs.hatchetharry.model.MagicCard;
import org.alienlabs.hatchetharry.model.channel.NotifierAction;
import org.alienlabs.hatchetharry.model.channel.NotifierCometChannel;
import org.alienlabs.hatchetharry.model.channel.PlayCardFromHandCometChannel;
import org.alienlabs.hatchetharry.service.PersistenceService;
import org.alienlabs.hatchetharry.view.page.HomePage;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.atmosphere.EventBus;
import org.apache.wicket.atmosphere.Subscribe;
import org.apache.wicket.injection.Injector;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.protocol.http.servlet.ServletWebRequest;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.template.PackageTextTemplate;
import org.apache.wicket.util.template.TextTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

public class PlayCardFromHandBehavior extends AbstractDefaultAjaxBehavior
{
	private static final long serialVersionUID = 1L;

	static final Logger LOGGER = LoggerFactory.getLogger(PlayCardFromHandBehavior.class);

	@SpringBean
	private PersistenceService persistenceService;

	private final WebMarkupContainer cardParent;
	private final WebMarkupContainer galleryParent;

	private UUID uuidToLookFor;
	private final int currentCard;

	private CardPanel cp;
	private String side;

	public PlayCardFromHandBehavior(final WebMarkupContainer _cardParent,
			final WebMarkupContainer _galleryParent, final UUID _uuidToLookFor,
			final int _currentCard, final String _side)
	{
		super();
		Injector.get().inject(this);
		this.cardParent = _cardParent;
		this.galleryParent = _galleryParent;
		this.uuidToLookFor = _uuidToLookFor;
		this.currentCard = _currentCard;
		this.side = _side;
	}

	@Override
	protected void respond(final AjaxRequestTarget target)
	{
		PlayCardFromHandBehavior.LOGGER.info("respond");

		final ServletWebRequest servletWebRequest = (ServletWebRequest)target.getPage()
				.getRequest();
		final HttpServletRequest request = servletWebRequest.getContainerRequest();

		PlayCardFromHandBehavior.LOGGER.info("URL: " + request.getQueryString());

		this.uuidToLookFor = UUID.fromString(request.getParameter("card"));
		PlayCardFromHandBehavior.LOGGER.info("uuidToLookFor: " + this.uuidToLookFor);

		final Long gameId = HatchetHarrySession.get().getPlayer().getGames().iterator().next()
				.getId();
		final Game game = this.persistenceService.getGame(gameId);

		final Long placeholderId = game.getCurrentPlaceholderId() + 1;
		game.setCurrentPlaceholderId(placeholderId);
		this.persistenceService.updateGame(game);

		final MagicCard card = this.persistenceService.getCardFromUuid(this.uuidToLookFor);
		card.setZone(CardZone.BATTLEFIELD);
		card.setCardPlaceholderId("cardPlaceholdera" + placeholderId);
		this.persistenceService.saveCard(card);

		this.cp = new CardPanel(this.cardParent, "cardPlaceholdera" + placeholderId,
				card.getSmallImageFilename(), card.getBigImageFilename(), card.getUuidObject());
		this.cp.setOutputMarkupId(true);
		this.cp.setMarkupId("cardPlaceholdera" + placeholderId);
		HatchetHarrySession.get().addCardInBattleField(this.cp);

		this.cardParent.addOrReplace(this.cp);
		target.add(this.cardParent);

		PlayCardFromHandBehavior.LOGGER.info("Before remove: "
				+ HatchetHarrySession.get().getFirstCardsInHand().size());
		HatchetHarrySession.get().removeCardInHand(card);
		PlayCardFromHandBehavior.LOGGER.info("After remove: "
				+ HatchetHarrySession.get().getFirstCardsInHand().size());

		final HandComponent gallery = new HandComponent("gallery");
		gallery.setOutputMarkupId(true);

		this.galleryParent.addOrReplace(gallery);
		target.add(this.galleryParent);

		card.setX(50l + (placeholderId * 10));
		card.setY(50l + (placeholderId * 25));
		this.persistenceService.saveCard(card);

		final StringBuffer buf = new StringBuffer();

		buf.append("jQuery(document).ready(function() { var theInt = null; var $crosslink, $navthumb; var curclicked = 0; theInterval = function(cur) { if (typeof cur != 'undefined') curclicked = cur; $crosslink.removeClass('active-thumb'); $navthumb.eq(curclicked).parent().addClass('active-thumb'); jQuery('.stripNav ul li a').eq(curclicked).trigger('click'); $crosslink.removeClass('active-thumb'); $navthumb.eq(curclicked).parent().addClass('active-thumb'); jQuery('.stripNav ul li a').eq(curclicked).trigger('click'); curclicked++; if (6 == curclicked) curclicked = 0; }; jQuery('#main-photo-slider').codaSlider(); $navthumb = jQuery('.nav-thumb'); $crosslink = jQuery('.cross-link'); $navthumb.click(function() { var $this = jQuery(this); theInterval($this.parent().attr('href').slice(1) - 1); return false; }); theInterval(); });");
		target.appendJavaScript(buf.toString());

		final PlayCardFromHandCometChannel pcfhcc = new PlayCardFromHandCometChannel(
				this.uuidToLookFor, HatchetHarrySession.get().getPlayer().getName(), gameId,
				placeholderId);

		final NotifierCometChannel ncc = new NotifierCometChannel(NotifierAction.PLAY_CARD_ACTION,
				gameId, HatchetHarrySession.get().getPlayer().getId(), HatchetHarrySession.get()
						.getPlayer().getName(), "", "", card.getTitle(), null);

		final List<BigInteger> allPlayersInGame = this.persistenceService
				.giveAllPlayersFromGame(gameId);

		// post a message for all players in the game, except me
		for (int i = 0; i < allPlayersInGame.size(); i++)
		{
			final Long player = allPlayersInGame.get(i).longValue();
			final String pageUuid = HatchetHarryApplication.getCometResources().get(player);
			PlayCardFromHandBehavior.LOGGER.info("pageUuid: " + pageUuid);
			EventBus.get().post(pcfhcc, pageUuid);
			EventBus.get().post(ncc, pageUuid);
		}

		// post a message for me
		final String me = HatchetHarryApplication.getCometResources().get(
				HatchetHarrySession.get().getPlayer().getId());
		PlayCardFromHandBehavior.LOGGER.info("pageUuid for myself: " + me);
		EventBus.get().post(pcfhcc, me);
		EventBus.get().post(ncc, me);
	}

	@Subscribe
	// contextAwareFilter = FilterPlayerAndGamePredicate.class)
	public void playCardFromHand(final AjaxRequestTarget target,
			final PlayCardFromHandCometChannel event)
	{
		PlayCardFromHandBehavior.LOGGER.info("### card: " + event.getUuidToLookFor());

		final Long id = event.getCardPlaceholderId();

		final MagicCard card = this.persistenceService.getCardFromUuid(event.getUuidToLookFor());

		this.cp = new CardPanel(this.cardParent, "cardPlaceholdera" + id,
				card.getSmallImageFilename(), card.getBigImageFilename(), card.getUuidObject());
		this.cp.setOutputMarkupId(true);
		this.cp.setMarkupId("cardPlaceholdera" + event.getCardPlaceholderId());
		HatchetHarrySession.get().addCardInBattleField(this.cp);

		this.cardParent.addOrReplace(this.cp);
		target.add(this.cardParent);

		final StringBuffer buf = new StringBuffer();
		buf.append("window.setTimeout(function() {  ");

		final List<MagicCard> allCardsInBattlefield = this.persistenceService
				.getAllCardsInBattleFieldForAGame(event.getGameId());
		for (final MagicCard aCard : allCardsInBattlefield)
		{
			buf.append("var card = jQuery(\"#menutoggleButton" + aCard.getUuid() + "\"); "
					+ "card.css(\"position\", \"absolute\"); " + "card.css(\"left\", \""
					+ aCard.getX() + "px\");" + "card.css(\"top\", \"" + aCard.getY() + "px\"); ");
			PlayCardFromHandBehavior.LOGGER.info("moving card UUID=" + aCard.getUuid()
					+ " to posX=" + aCard.getX() + ", posY=" + aCard.getY());

			if (aCard.isTapped())
			{
				buf.append("jQuery('#card" + aCard.getUuid() + "').rotate(90); ");
			}
			else
			{
				buf.append("jQuery('#card" + aCard.getUuid() + "').rotate(0); ");
			}

			buf.append("jQuery(\"#card" + aCard.getUuid() + "\").easyTooltip({"
					+ "useElement: \"cardTooltip" + aCard.getUuid() + "\"}); ");
		}

		final List<MagicCard> allCardsInGraveyard = this.persistenceService
				.getAllCardsInGraveyardForAGame(event.getGameId());
		for (final MagicCard aCard : allCardsInGraveyard)
		{
			buf.append("jQuery('#menutoggleButton" + aCard.getUuid() + "').remove(); ");
		}

		buf.append(" }, 3000); ");
		target.appendJavaScript(buf.toString());
	}

	@Override
	public void renderHead(final Component component, final IHeaderResponse response)
	{
		super.renderHead(component, response);

		final HashMap<String, Object> variables = new HashMap<String, Object>();
		variables.put("url", this.getCallbackUrl());
		variables.put("uuid", this.uuidToLookFor.toString());
		variables.put("uuidValidForJs", this.uuidToLookFor.toString().replace("-", "_"));
		variables.put("next", (this.currentCard == 6 ? 0 : this.currentCard + 1));
		variables.put("clicked", this.currentCard);
		variables.put("side", this.side);

		final TextTemplate template1 = new PackageTextTemplate(HomePage.class,
				"script/playCard/playCard.js");
		template1.interpolate(variables);

		PlayCardFromHandBehavior.LOGGER.info("### clicked: " + this.currentCard);
		response.render(JavaScriptHeaderItem.forScript(template1.asString(), "playCardFromHand"));
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
