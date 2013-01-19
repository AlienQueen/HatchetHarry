package org.alienlabs.hatchetharry.view.component;

import java.math.BigInteger;
import java.util.ArrayList;
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
import org.alienlabs.hatchetharry.model.channel.PlayCardFromGraveyardCometChannel;
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

public class PlayCardFromGraveyardBehavior extends AbstractDefaultAjaxBehavior
{
	private static final long serialVersionUID = 1L;

	static final Logger LOGGER = LoggerFactory.getLogger(PlayCardFromGraveyardBehavior.class);

	@SpringBean
	private PersistenceService persistenceService;

	private final WebMarkupContainer cardParent;

	private UUID uuidToLookFor;

	private CardPanel cp;
	private String side;

	public PlayCardFromGraveyardBehavior(final WebMarkupContainer _cardParent, final String _side)
	{
		super();
		Injector.get().inject(this);
		this.cardParent = _cardParent;
		this.side = _side;
	}

	@Override
	protected void respond(final AjaxRequestTarget target)
	{
		PlayCardFromGraveyardBehavior.LOGGER.info("respond PlayCardFromGraveyardBehavior");

		target.appendJavaScript("jQuery(\"#box_menu_clone\").hide(); ");

		final ServletWebRequest servletWebRequest = (ServletWebRequest)target.getPage()
				.getRequest();
		final HttpServletRequest request = servletWebRequest.getContainerRequest();

		this.uuidToLookFor = UUID.fromString(request.getParameter("card"));
		PlayCardFromGraveyardBehavior.LOGGER.info("uuidToLookFor: " + this.uuidToLookFor);

		final Long gameId = HatchetHarrySession.get().getPlayer().getGames().iterator().next()
				.getId();
		final Game game = this.persistenceService.getGame(gameId);

		final Long placeholderId = game.getCurrentPlaceholderId() + 1;
		game.setCurrentPlaceholderId(placeholderId);
		this.persistenceService.updateGame(game);

		final MagicCard card = this.persistenceService.getCardFromUuid(this.uuidToLookFor);

		if (!CardZone.GRAVEYARD.equals(card.getZone()))
		{
			return;
		}

		card.setZone(CardZone.BATTLEFIELD);
		card.setCardPlaceholderId("cardPlaceholdera" + placeholderId);
		this.persistenceService.saveCard(card);
		HatchetHarrySession.get().removeCardFromGraveyard(card);

		// TODO remove this
		// this.cp = new CardPanel("cardPlaceholdera" + placeholderId,
		// card.getSmallImageFilename(),
		// card.getBigImageFilename(), card.getUuidObject());
		// this.cp.setOutputMarkupId(true);
		// this.cp.setMarkupId("cardPlaceholdera" + placeholderId);
		// HatchetHarrySession.get().addCardInBattleField(this.cp);

		// this.cardParent.addOrReplace(this.cp);
		// target.add(this.cardParent);

		// TODO: manage new graveyard
		// final HandComponent gallery = new HandComponent("gallery");
		// gallery.setOutputMarkupId(true);

		// this.galleryParent.addOrReplace(gallery);
		// target.add(this.galleryParent);

		card.setX(50l + (placeholderId * 10));
		card.setY(50l + (placeholderId * 25));
		// TODO card has been saved twice!
		this.persistenceService.saveCard(card);

		final StringBuffer buf = new StringBuffer();

		final Component graveyardToUpdate = new GraveyardComponent("graveyard");

		((HomePage)target.getPage()).getGraveyardParent().addOrReplace(graveyardToUpdate);
		target.add(((HomePage)target.getPage()).getGraveyardParent());
		// TODO externalize this string
		buf.append("var theIntGraveyard = null; var $crosslinkGraveyard, $navthumbGraveyard; var curclickedGraveyard = 0; theIntervalGraveyard = function(cur) { if (typeof cur != 'undefined') curclickedGraveyard = cur; $crosslinkGraveyard.removeClass('active-thumbGraveyard'); $navthumbGraveyard.eq(curclickedGraveyard).parent().addClass('active-thumbGraveyard'); jQuery('.stripNavGraveyard ul li a').eq(curclickedGraveyard).trigger('click'); $crosslinkGraveyard.removeClass('active-thumbGraveyard'); $navthumbGraveyard.eq(curclickedGraveyard).parent().addClass('active-thumbGraveyard'); jQuery('.stripNavGraveyard ul li a').eq(curclickedGraveyard).trigger('click'); curclickedGraveyard++; if (6 == curclickedGraveyard) curclickedGraveyard = 0; }; jQuery('#graveyard-main-photo-slider').codaSliderGraveyard(); $navthumbGraveyard = jQuery('.graveyard-nav-thumb'); $crosslinkGraveyard = jQuery('.graveyard-cross-link'); $navthumbGraveyard.click(function() { var $this = jQuery(this); theIntervalGraveyard($this.parent().attr('href').slice(1) - 1); return false; }); theIntervalGraveyard();");
		target.appendJavaScript(buf.toString());

		final PlayCardFromGraveyardCometChannel pcfgcc = new PlayCardFromGraveyardCometChannel(
				this.uuidToLookFor, HatchetHarrySession.get().getPlayer().getName(), gameId,
				placeholderId);

		final NotifierCometChannel ncc = new NotifierCometChannel(
				NotifierAction.PLAY_CARD_FROM_GRAVEYARD_ACTION, gameId, HatchetHarrySession.get()
						.getPlayer().getId(), HatchetHarrySession.get().getPlayer().getName(), "",
				"", card.getTitle(), null);

		final List<BigInteger> allPlayersInGame = this.persistenceService
				.giveAllPlayersFromGame(gameId);

		// post a message for all players in the game
		for (int i = 0; i < allPlayersInGame.size(); i++)
		{
			final Long player = allPlayersInGame.get(i).longValue();
			final String pageUuid = HatchetHarryApplication.getCometResources().get(player);
			PlayCardFromGraveyardBehavior.LOGGER.info("pageUuid: " + pageUuid);
			EventBus.get().post(pcfgcc, pageUuid);
			EventBus.get().post(ncc, pageUuid);
		}
	}

	@Subscribe
	public void playCardFromHand(final AjaxRequestTarget target,
			final PlayCardFromGraveyardCometChannel event)
	{
		PlayCardFromGraveyardBehavior.LOGGER.info("### card: " + event.getUuidToLookFor());

		final Long id = event.getCardPlaceholderId();

		final MagicCard card = this.persistenceService.getCardFromUuid(event.getUuidToLookFor());

		this.cp = new CardPanel("cardPlaceholdera" + id, card.getSmallImageFilename(),
				card.getBigImageFilename(), card.getUuidObject());
		this.cp.setOutputMarkupId(true);
		this.cp.setMarkupId("cardPlaceholdera" + event.getCardPlaceholderId());
		HatchetHarrySession.get().addCardInBattleField(this.cp);

		this.cardParent.addOrReplace(this.cp);
		target.add(this.cardParent);

		final StringBuffer buf = new StringBuffer();
		buf.append("window.setTimeout(function() { ");

		final List<MagicCard> allCardsInBattlefield = this.persistenceService
				.getAllCardsInBattleFieldForAGame(event.getGameId());
		for (final MagicCard aCard : allCardsInBattlefield)
		{
			buf.append("var card = jQuery(\"#menutoggleButton" + aCard.getUuid() + "\"); "
					+ "card.css(\"position\", \"absolute\"); " + "card.css(\"left\", \""
					+ aCard.getX() + "px\");" + "card.css(\"top\", \"" + aCard.getY() + "px\"); ");
			PlayCardFromGraveyardBehavior.LOGGER.info("moving card UUID=" + aCard.getUuid()
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

		final ArrayList<MagicCard> toRemove = HatchetHarrySession.get()
				.getAllCardsWhichHaveBeenInBattlefield();
		for (final MagicCard aCard : toRemove)
		{
			final MagicCard freshCard = this.persistenceService.getCardFromUuid(aCard
					.getUuidObject());
			PlayCardFromGraveyardBehavior.LOGGER.info("=== removing: "
					+ freshCard.getCardPlaceholderId());
			buf.append("jQuery('#" + freshCard.getCardPlaceholderId() + "').remove(); ");
		}

		buf.append("jQuery('#" + card.getCardPlaceholderId() + "').remove(); ");
		buf.append(" }, 3000); ");

		target.appendJavaScript(buf.toString());
	}

	@Override
	public void renderHead(final Component component, final IHeaderResponse response)
	{
		super.renderHead(component, response);

		final HashMap<String, Object> variables = new HashMap<String, Object>();
		variables.put("url", this.getCallbackUrl());
		variables.put("side", this.side);

		final TextTemplate template1 = new PackageTextTemplate(HomePage.class,
				"script/playCard/playCardFromGraveyard.js");
		template1.interpolate(variables);

		response.render(JavaScriptHeaderItem.forScript(template1.asString(),
				"playCardFromGraveyard"));
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
