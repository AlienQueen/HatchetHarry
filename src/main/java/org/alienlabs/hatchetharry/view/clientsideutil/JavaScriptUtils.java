package org.alienlabs.hatchetharry.view.clientsideutil;

import java.util.Collections;
import java.util.List;

import org.alienlabs.hatchetharry.HatchetHarrySession;
import org.alienlabs.hatchetharry.model.Arrow;
import org.alienlabs.hatchetharry.model.Counter;
import org.alienlabs.hatchetharry.model.MagicCard;
import org.alienlabs.hatchetharry.service.PersistenceService;
import org.alienlabs.hatchetharry.view.component.gui.ExileComponent;
import org.alienlabs.hatchetharry.view.component.gui.GraveyardComponent;
import org.alienlabs.hatchetharry.view.component.gui.HandComponent;
import org.alienlabs.hatchetharry.view.page.HomePage;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aplombee.QuickView;

public class JavaScriptUtils
{
	public static final String REACTIVATE_GRAVEYARD_COMPONENT_JAVASCRIPT = "var theIntGraveyard = null; var $crosslinkGraveyard, $navthumbGraveyard; var curclickedGraveyard = 0; theIntervalGraveyard = function(cur) { if (typeof cur != 'undefined') curclickedGraveyard = cur; $crosslinkGraveyard.removeClass('active-thumb-Graveyard'); $navthumbGraveyard.eq(curclickedGraveyard).parent().addClass('active-thumb-Graveyard'); jQuery('.stripNavGraveyard ul li a').eq(curclickedGraveyard).trigger('click'); $crosslinkGraveyard.removeClass('active-thumb-Graveyard'); $navthumbGraveyard.eq(curclickedGraveyard).parent().addClass('active-thumb-Graveyard'); jQuery('.stripNavGraveyard ul li a').eq(curclickedGraveyard).trigger('click'); curclickedGraveyard++; if (6 == curclickedGraveyard) curclickedGraveyard = 0; }; jQuery('#graveyard-main-photo-slider').codaSliderGraveyard(); $navthumbGraveyard = jQuery('.graveyard-nav-thumb'); $crosslinkGraveyard = jQuery('.graveyard-cross-link'); $navthumbGraveyard.click(function() { var $this = jQuery(this); theIntervalGraveyard($this.parent().attr('href').slice(1) - 1); return false; }); theIntervalGraveyard(); ";
	public static final String REACTIVATE_EXILE_COMPONENT_JAVASCRIPT = "var theIntExile = null; var $crosslinkExile, $navthumbExile; var curclickedExile = 0; theIntervalExile = function(cur) { if (typeof cur != 'undefined') curclickedExile = cur; $crosslinkExile.removeClass('active-thumb-Exile'); $navthumbExile.eq(curclickedExile).parent().addClass('active-thumb-Exile'); jQuery('.stripNavExile ul li a').eq(curclickedExile).trigger('click'); $crosslinkExile.removeClass('active-thumb-Exile'); $navthumbExile.eq(curclickedExile).parent().addClass('active-thumb-Exile'); jQuery('.stripNavExile ul li a').eq(curclickedExile).trigger('click'); curclickedExile++; if (6 == curclickedExile) curclickedExile = 0; }; jQuery('#exile-main-photo-slider').codaSliderExile(); $navthumbExile = jQuery('.exile-nav-thumb'); $crosslinkExile = jQuery('.exile-cross-link'); $navthumbExile.click(function() { var $this = jQuery(this); theIntervalExile($this.parent().attr('href').slice(1) - 1); return false; }); theIntervalExile(); function updateExileLabel(){ var exileCardName = jQuery('#exileGallery .active-thumb-Exile .nav-thumb').attr('name'); if (exileCardName == undefined) { exileCardName = jQuery('#exileGallery .active-thumb-Exile .nav-thumb').attr('name'); } jQuery('#exileCardLabel').text(exileCardName); }; jQuery(function() { setTimeout(function() { updateExileLabel(); jQuery('#exileGallery .cross-link .nav-thumb').click(updateExileLabel); }, 175); }); ";
	public static final String REACTIVATE_BATTLEFIELD_JAVASCRIPT = "window.setTimeout(function() { jQuery('#galleryParent').find('.cards').sortable({ placeholder: \"ui-state-highlight\"}); function tooltips() { if ((typeof dontZoom === 'undefined') || (!dontZoom)) { $(this).parents('.cardContainer').toggleClass('details'); } } jQuery('.magicCard').unbind('click').click(tooltips); }, 2000); ";
	public static final String REVEAL_HAND_COMPONENT_JAVASCRIPT = "var theIntPlayer = null; var $crosslinkPlayer, $navthumbPlayer; var curclickedPlayer = 0; theIntervalPlayer = function(cur) { if (typeof cur != 'undefined') curclickedPlayer = cur; $crosslinkPlayer.removeClass('active-thumb-RevealHand'); $navthumbPlayer.eq(curclickedPlayer).parent().addClass('active-thumb-RevealHand'); jQuery('.stripNavPlayer ul li a').eq(curclickedPlayer).trigger('click'); $crosslinkPlayer.removeClass('active-thumb-RevealHand'); $navthumbPlayer.eq(curclickedPlayer).parent().addClass('active-thumb-RevealHand'); jQuery('.stripNavPlayer ul li a').eq(curclickedPlayer).trigger('click'); curclickedPlayer++; if (6 == curclickedPlayer) curclickedPlayer = 0; }; jQuery('#main-photo-sliderPlayer').codaSliderPlayer(); $navthumbPlayer = jQuery('.nav-thumbPlayer'); $crosslinkPlayer = jQuery('.cross-link'); $navthumbPlayer.click(function() { var $thisPlayer = jQuery(this); theIntervalPlayer($thisPlayer.parent().attr('href').slice(1) - 1); return false; }); theIntervalPlayer(); jQuery(function() { setTimeout(function() { jQuery('#stripNavPlayer0').hide(); jQuery('#galleryRevealParent').children().first().attr('style', ''); jQuery('#handGalleryPlayer').attr('style', 'width: 262px; margin: 25px auto; position: absolute; top: 1%; left: 70%; min-height: 420px;'); jQuery('#handlehandGalleryPlayer').attr('style', 'position: relative; top: 110px; left: 0%; z-index: 10; cursor: move;'); jQuery('#handGalleryPlayer').draggable({ handle : '#handlehandGalleryPlayer', helper : 'original'}); jQuery('#revealedContent').show(); }, 500); }); ";
	public static final String HIDE_MENUS = "if (jQuery('#cssmenu').is(':visible')) { jQuery('#cssmenu').hide(); jQuery('#cssmenu').show(); } else { jQuery('.dropdownmenu').hide(); jQuery('.categories').hide(); jQuery('.dropdownmenu').show(); } ";
	public static final String HIDE_ALL_TOOLTIPS = "jQuery('.tooltip').attr('style', 'display: none;'); jQuery('.cardTooltip').attr('style', 'display: none;'); ";

	private static final Logger LOGGER = LoggerFactory.getLogger(JavaScriptUtils.class);

	private JavaScriptUtils()
	{
	}

	public static void updateCardsAndRestoreStateInBattlefield(final AjaxRequestTarget target,
			final PersistenceService persistenceService, final Long gameId, final MagicCard mc,
			final boolean added)
	{
		JavaScriptUtils.updateCardsInBattlefield(target, persistenceService, mc, added);
		JavaScriptUtils.restoreStateOfCardsInBattlefield(target, persistenceService, mc, added,
				gameId);
	}

	public static void updateCardsInBattlefield(final AjaxRequestTarget target,
			final PersistenceService persistenceService, final MagicCard mc, final boolean added)
	{
		final HomePage homePage = (HomePage)target.getPage();

		final QuickView<MagicCard> magicCardListForSide1 = homePage
				.getAllCardsInBattlefieldForSide1();
		final QuickView<MagicCard> magicCardListForSide2 = homePage
				.getAllCardsInBattlefieldForSide2();

		if (null != mc)
		{
			if (added)
			{
				if (mc.getOwnerSide().equals(
						persistenceService.getPlayer(HatchetHarrySession.get().getPlayer().getId())
								.getSide().getSideName()))
				{
					homePage.getAllMagicCardsInBattlefieldForSide1().add(mc);
					// just enough to create and add a new item in the end
					magicCardListForSide1.addNewItems(mc);
				}
				else
				{
					homePage.getAllMagicCardsInBattlefieldForSide2().add(mc);
					// just enough to create and add a new item in the end
					magicCardListForSide2.addNewItems(mc);
				}
			}
			else
			{
				for (int i = 0; i < magicCardListForSide1.size(); i++)
				{
					final MagicCard targetCard = homePage.getAllCardsInBattlefieldForSide1()
							.getItem(i).getModelObject();

					for (final Counter count : mc.getCounters())
					{
						count.setNumberOfCounters(0l);
						persistenceService.updateCounter(count);
						JavaScriptUtils.LOGGER.info("clear");
					}

					if (mc.equals(targetCard))
					{
						homePage.getAllMagicCardsInBattlefieldForSide1().remove(mc);
						magicCardListForSide1.remove(homePage.getAllCardsInBattlefieldForSide1()
								.getItem(i));
						JavaScriptUtils.LOGGER.info("remove card: " + mc.getTitle());
						break;
					}
				}

				for (int i = 0; i < magicCardListForSide2.size(); i++)
				{
					final MagicCard targetCard = homePage.getAllCardsInBattlefieldForSide2()
							.getItem(i).getModelObject();

					for (final Counter count : mc.getCounters())
					{
						count.setNumberOfCounters(0l);
						persistenceService.updateCounter(count);
						JavaScriptUtils.LOGGER.info("clear");
					}

					if (mc.equals(targetCard))
					{
						homePage.getAllMagicCardsInBattlefieldForSide2().remove(mc);
						magicCardListForSide2.remove(homePage.getAllCardsInBattlefieldForSide2()
								.getItem(i));
						JavaScriptUtils.LOGGER.info("remove card: " + mc.getTitle());
						break;
					}
				}
			}
		}
	}

	public static void restoreStateOfCardsInBattlefield(final AjaxRequestTarget target,
			final PersistenceService persistenceService, final MagicCard mc, final boolean added,
			final Long gameId)
	{
		final Boolean drawMode = persistenceService.getGame(gameId).isDrawMode();
		final StringBuilder buil = new StringBuilder();

		if (!added && (null != mc) && (drawMode != null) && drawMode.booleanValue())
		{
			buil.append("window.setTimeout(function() { ");

			final String uuidValidForJs = mc.getUuid().replace("-", "_");
			final String cardToRemove = "cardHandle" + uuidValidForJs;

			buil.append("jQuery('._jsPlumb_connector').remove(); jQuery('._jsPlumb_overlay').remove(); jQuery('._jsPlumb_endpoint').remove(); ");

			final List<Arrow> allArrows = persistenceService.loadAllArrowsForAGame(gameId);
			for (final Arrow arrow : allArrows)
			{
				JavaScriptUtils.LOGGER.info("source: " + arrow.getSource() + ", target: "
						+ arrow.getTarget() + ", uuidValidForJs: " + uuidValidForJs);

				if (arrow.getSource().equals(cardToRemove)
						|| arrow.getTarget().equals(cardToRemove))
				{
					buil.append("for (var index = 0; index < arrows.length; index++) { ");
					buil.append("if (arrows[index]['source'].id === '" + cardToRemove
							+ "' || arrows[index]['target'].id === '" + cardToRemove + "') { ");
					buil.append("arrows.splice( jQuery.inArray(index, arrows), 1 ); ");
					buil.append("index--; ");
					buil.append("} } ");
					persistenceService.deleteArrow(arrow);
				}
				else
				{
					buil.append("var e0 = jsPlumb.addEndpoint(");
					buil.append(arrow.getSource());
					buil.append(" ); ");
					buil.append("var e1 = jsPlumb.addEndpoint(");
					buil.append(arrow.getTarget());
					buil.append("); ");
					buil.append(" arrows.push({ 'source' : ");
					buil.append(arrow.getSource());
					buil.append(", 'target' : ");
					buil.append(arrow.getTarget());
					buil.append(" }); ");
					buil.append("	jsPlumb.connect({ source:e0, target:e1, connector:['Bezier', { curviness:70 }], overlays : [ ");
					buil.append("					['Label', {location:0.7, id:'label', events:{ ");
					buil.append("							} }], ['Arrow', { ");
					buil.append("						cssClass:'l1arrow',  location:0.5, width:40,length:40 }]] }); ");
				}
			}

			buil.append("}, 175); ");
		}

		final List<MagicCard> allCards = persistenceService
				.getAllCardsInBattleFieldForAGame(gameId);
		buil.append("window.setTimeout(function() { ");
		JavaScriptUtils.LOGGER.info("###  allCards.size(): " + allCards.size());

		for (final MagicCard magicCard : allCards)
		{
			JavaScriptUtils.LOGGER.info("### "
					+ (magicCard.getToken() != null
							? magicCard.getToken().getCounters().size()
							: magicCard.getCounters().size()) + " counters, uuid = "
					+ magicCard.getUuid().replace("-", "_"));
			if (((magicCard.getToken() != null) && magicCard.getToken().getCounters().isEmpty())
					|| (((magicCard.getToken() == null) && magicCard.getCounters().isEmpty())))
			{
				buil.append("jQuery('#bullet" + magicCard.getUuid().replace("-", "_")
						+ "').hide(); ");
			}
			else
			{
				buil.append("jQuery('#bullet" + magicCard.getUuid().replace("-", "_")
						+ "').show(); ");
			}
		}

		buil.append("}, 175); ");

		target.appendJavaScript(buil.toString());
	}

	public static void updateHand(final AjaxRequestTarget target)
	{
		((HomePage)target.getPage()).getGalleryParent().addOrReplace(
				new HandComponent("gallery", false));
		target.add(((HomePage)target.getPage()).getGalleryParent());

		target.appendJavaScript(JavaScriptUtils.REACTIVATE_BATTLEFIELD_JAVASCRIPT);
	}

	public static void updateHand(final AjaxRequestTarget target, final Long gameId,
			final Long playerId, final Long deckId)
	{
		((HomePage)target.getPage()).getGalleryParent().addOrReplace(
				new HandComponent("gallery", false, gameId, playerId, deckId));
		target.add(((HomePage)target.getPage()).getGalleryParent());

		target.appendJavaScript(JavaScriptUtils.REACTIVATE_BATTLEFIELD_JAVASCRIPT.replaceAll(
				"Player", playerId.toString()));
	}

	public static void revealHand(final AjaxRequestTarget target, final Long gameId,
			final Long playerId, final Long deckId)
	{
		((HomePage)target.getPage()).getGalleryRevealParent().addOrReplace(
				new HandComponent("galleryReveal", true, gameId, playerId, deckId));
		target.add(((HomePage)target.getPage()).getGalleryRevealParent());

		target.appendJavaScript(JavaScriptUtils.REVEAL_HAND_COMPONENT_JAVASCRIPT.replaceAll(
				"Player", playerId.toString()));
	}

	public static void hideHand(final AjaxRequestTarget target)
	{
		((HomePage)target.getPage()).getGalleryRevealParent().addOrReplace(
				new WebMarkupContainer("galleryReveal"));
		target.add(((HomePage)target.getPage()).getGalleryRevealParent());
	}

	public static void updateGraveyard(final AjaxRequestTarget target)
	{
		((HomePage)target.getPage()).getGraveyardParent().addOrReplace(
				new GraveyardComponent("graveyard"));
		target.add(((HomePage)target.getPage()).getGraveyardParent());

		target.appendJavaScript(JavaScriptUtils.REACTIVATE_GRAVEYARD_COMPONENT_JAVASCRIPT);
	}

	public static void updateGraveyard(final AjaxRequestTarget target, final Long gameId,
			final Long playerId, final Long deckId)
	{
		((HomePage)target.getPage()).getGraveyardParent().addOrReplace(
				new GraveyardComponent("graveyard", gameId, playerId, deckId));
		target.add(((HomePage)target.getPage()).getGraveyardParent());

		target.appendJavaScript(JavaScriptUtils.REACTIVATE_GRAVEYARD_COMPONENT_JAVASCRIPT);
	}

	public static void updateExile(final AjaxRequestTarget target, final Long gameId,
			final Long playerId, final Long deckId)
	{
		((HomePage)target.getPage()).getExileParent().addOrReplace(
				new ExileComponent("exile", gameId, playerId, deckId));
		target.add(((HomePage)target.getPage()).getExileParent());

		target.appendJavaScript(JavaScriptUtils.REACTIVATE_EXILE_COMPONENT_JAVASCRIPT);
	}

	public static List<MagicCard> reorderCards(
			final List<MagicCard> allCardsInBattlefieldForPlayer, final Integer removedCardIndex)
	{
		Collections.sort(allCardsInBattlefieldForPlayer);

		for (int index = removedCardIndex; index < allCardsInBattlefieldForPlayer.size(); index++)
		{
			final MagicCard magicCard = allCardsInBattlefieldForPlayer.get(index);
			magicCard.setBattlefieldOrder(index);

			LOGGER.info("&&& mc: " + magicCard.getTitle() + ", order: "
					+ magicCard.getBattlefieldOrder().intValue());
		}

		return allCardsInBattlefieldForPlayer;
	}
}
