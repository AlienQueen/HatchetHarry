package org.alienlabs.hatchetharry.view.clientsideutil;

import org.alienlabs.hatchetharry.model.Counter;
import org.alienlabs.hatchetharry.model.MagicCard;
import org.alienlabs.hatchetharry.service.PersistenceService;
import org.alienlabs.hatchetharry.view.component.ExileComponent;
import org.alienlabs.hatchetharry.view.component.GraveyardComponent;
import org.alienlabs.hatchetharry.view.component.HandComponent;
import org.alienlabs.hatchetharry.view.page.HomePage;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aplombee.QuickView;

public class JavaScriptUtils
{
	public static final String REACTIVATE_GRAVEYARD_COMPONENT_JAVASCRIPT = "var theIntGraveyard = null; var $crosslinkGraveyard, $navthumbGraveyard; var curclickedGraveyard = 0; theIntervalGraveyard = function(cur) { if (typeof cur != 'undefined') curclickedGraveyard = cur; $crosslinkGraveyard.removeClass('active-thumb-Graveyard'); $navthumbGraveyard.eq(curclickedGraveyard).parent().addClass('active-thumb-Graveyard'); jQuery('.stripNavGraveyard ul li a').eq(curclickedGraveyard).trigger('click'); $crosslinkGraveyard.removeClass('active-thumb-Graveyard'); $navthumbGraveyard.eq(curclickedGraveyard).parent().addClass('active-thumb-Graveyard'); jQuery('.stripNavGraveyard ul li a').eq(curclickedGraveyard).trigger('click'); curclickedGraveyard++; if (6 == curclickedGraveyard) curclickedGraveyard = 0; }; jQuery('#graveyard-main-photo-slider').codaSliderGraveyard(); $navthumbGraveyard = jQuery('.graveyard-nav-thumb'); $crosslinkGraveyard = jQuery('.graveyard-cross-link'); $navthumbGraveyard.click(function() { var $this = jQuery(this); theIntervalGraveyard($this.parent().attr('href').slice(1) - 1); return false; }); theIntervalGraveyard(); function updateGraveyardLabel(){ var graveyardCardName = jQuery('#graveyardGallery .active-thumb-Graveyard .nav-thumb').attr('name'); if (graveyardCardName == undefined) { graveyardCardName = jQuery('#graveyardGallery .active-thumb-Graveyard .nav-thumb').attr('name'); } jQuery('#graveyardCardLabel').text(graveyardCardName); }; jQuery(function() { setTimeout(function() { updateGraveyardLabel(); jQuery('#graveyardGallery .cross-link .nav-thumb').click(updateGraveyardLabel); }, 175); });";
	public static final String REACTIVATE_EXILE_COMPONENT_JAVASCRIPT = "var theIntExile = null; var $crosslinkExile, $navthumbExile; var curclickedExile = 0; theIntervalExile = function(cur) { if (typeof cur != 'undefined') curclickedExile = cur; $crosslinkExile.removeClass('active-thumb-Exile'); $navthumbExile.eq(curclickedExile).parent().addClass('active-thumb-Exile'); jQuery('.stripNavExile ul li a').eq(curclickedExile).trigger('click'); $crosslinkExile.removeClass('active-thumb-Exile'); $navthumbExile.eq(curclickedExile).parent().addClass('active-thumb-Exile'); jQuery('.stripNavExile ul li a').eq(curclickedExile).trigger('click'); curclickedExile++; if (6 == curclickedExile) curclickedExile = 0; }; jQuery('#exile-main-photo-slider').codaSliderExile(); $navthumbExile = jQuery('.exile-nav-thumb'); $crosslinkExile = jQuery('.exile-cross-link'); $navthumbExile.click(function() { var $this = jQuery(this); theIntervalExile($this.parent().attr('href').slice(1) - 1); return false; }); theIntervalExile(); function updateExileLabel(){ var exileCardName = jQuery('#exileGallery .active-thumb-Exile .nav-thumb').attr('name'); if (exileCardName == undefined) { exileCardName = jQuery('#exileGallery .active-thumb-Exile .nav-thumb').attr('name'); } jQuery('#exileCardLabel').text(exileCardName); }; jQuery(function() { setTimeout(function() { updateExileLabel(); jQuery('#exileGallery .cross-link .nav-thumb').click(updateExileLabel); }, 175); });";
	public static final String REACTIVATE_HAND_COMPONENT_JAVASCRIPT = "var theInt = null; var $crosslink, $navthumb; var curclicked = 0; theInterval = function(cur) { if (typeof cur != 'undefined') curclicked = cur; $crosslink.removeClass('active-thumb-Hand'); $navthumb.eq(curclicked).parent().addClass('active-thumb-Hand'); jQuery('.stripNav ul li a').eq(curclicked).trigger('click'); $crosslink.removeClass('active-thumb-Hand'); $navthumb.eq(curclicked).parent().addClass('active-thumb-Hand'); jQuery('.stripNav ul li a').eq(curclicked).trigger('click'); curclicked++; if (6 == curclicked) curclicked = 0; }; jQuery('#main-photo-slider').codaSlider(); $navthumb = jQuery('.nav-thumb'); $crosslink = jQuery('.cross-link'); $navthumb.click(function() { var $this = jQuery(this); theInterval($this.parent().attr('href').slice(1) - 1); return false; }); theInterval(); function updateHandLabel(){ var handCardName = jQuery('#handGallery .active-thumb-Hand .nav-thumb').attr('name'); if (handCardName == undefined) { handCardName = jQuery('#handGallery .active-thumb-Hand .nav-thumb').attr('name'); } jQuery('#cardLabel').text(handCardName); }; jQuery(function() { setTimeout(function() { updateHandLabel(); jQuery('#handGallery .cross-link .nav-thumb').click(updateHandLabel); }, 175); }); ";
	public static final String DEACTIVATE_END_OF_TURN_LINKS = "jQuery('#acceptEndTurnLink').attr('style', 'cursor: wait; color: black;'); jQuery('#endTurnActionLink').attr('style', 'cursor: wait; color: black;'); ";
	public static final String REACTIVATE_END_OF_TURN_LINKS = "jQuery('#acceptEndTurnLink').attr('style', 'cursor: pointer; color: white;'); jQuery('#endTurnActionLink').attr('style', 'cursor: pointer; color: white;'); ";
	public static final String HIDE_MENUS = "if (jQuery('#cssmenu').is(':visible')) { jQuery('#cssmenu').hide(); jQuery('#cssmenu').show(); } else  { jQuery('.dropdownmenu').hide(); jQuery('.categories').hide(); jQuery('.dropdownmenu').show(); } ";
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
		JavaScriptUtils.restoreStateOfCardsInBattlefield(target, persistenceService, mc, added);
	}

	public static void updateCardsInBattlefield(final AjaxRequestTarget target,
			final PersistenceService persistenceService, final MagicCard mc, final boolean added)
	{
		final HomePage homePage = (HomePage)target.getPage();

		final QuickView<MagicCard> magicCardList = homePage.getAllCardsInBattlefield();
		final QuickView<MagicCard> tooltipList = homePage.getAllTooltips();

		if (null != mc)
		{
			if (added)
			{
				homePage.getAllMagicCardsInBattlefield().add(mc);
				// just enough to create and add a new item in the end
				magicCardList.addNewItems(mc);

				homePage.getAllTooltipsInBattlefield().add(mc);
				// just enough to create and add a new item in the end
				tooltipList.addNewItems(mc);
			}
			else
			{
				for (int i = 0; i < magicCardList.size(); i++)
				{
					final MagicCard targetCard = homePage.getAllCardsInBattlefield().getItem(i)
							.getModelObject();

					for (final Counter count : mc.getCounters())
					{
						count.setNumberOfCounters(0l);
						persistenceService.updateCounter(count);
						JavaScriptUtils.LOGGER.info("clear");
					}

					if (mc.equals(targetCard))
					{
						homePage.getAllMagicCardsInBattlefield().remove(mc);
						magicCardList.remove(homePage.getAllCardsInBattlefield().getItem(i));
						JavaScriptUtils.LOGGER.info("remove card: " + mc.getTitle());
						break;
					}
				}

				for (int i = 0; i < tooltipList.size(); i++)
				{
					final MagicCard targetCard = homePage.getAllTooltips().getItem(i)
							.getModelObject();
					if (mc.equals(targetCard))
					{
						homePage.getAllTooltipsInBattlefield().remove(mc);
						tooltipList.remove(homePage.getAllTooltips().getItem(i));
						JavaScriptUtils.LOGGER.info("remove tooltip: " + mc.getTitle());
						break;
					}
				}
			}
		}
	}

	/*
	 * Three things to do: - card positions - card tapped / untapped state -
	 * activate tooltip again
	 */
	public static void restoreStateOfCardsInBattlefield(final AjaxRequestTarget target,
			final PersistenceService persistenceService, final MagicCard mc, final boolean added)
	{
		if ((added) && (null != mc))
		{
			final StringBuilder buil = new StringBuilder();
			buil.append("window.setTimeout(function() { ");

			final String uuidValidForJs = mc.getUuid().replace("-", "_");

			buil.append("if (typeof drawMode === 'undefined' || drawMode === false) { ");
			buil.append("jQuery('#card" + uuidValidForJs
					+ "').click(function(e) {  jQuery('#cardTooltip" + uuidValidForJs
					+ "').attr('style', 'display: block; position: absolute; left: "
					+ (mc.getX() + 127) + "px; top: " + (mc.getY() + 56)
					+ "px; z-index: 50;'); jQuery('#cardTooltip" + uuidValidForJs
					+ " > span').attr('style', 'display: block;'); }); ");

			// For mobile
			buil.append("var hammertime" + uuidValidForJs + " = jQuery('#card" + uuidValidForJs
					+ "').hammer(); ");
			buil.append("hammertime" + uuidValidForJs + ".on('tap', function(ev) { ");
			buil.append("jQuery('#cardTooltip" + uuidValidForJs
					+ "').attr('style', 'display: block; position: absolute; left: "
					+ (mc.getX() + 127) + "px; top: " + (mc.getY() + 56)
					+ "px; z-index: 50;'); jQuery('#cardTooltip" + uuidValidForJs
					+ " > span').attr('style', 'display: block;'); }); ");

			buil.append("jQuery('#cardTooltip" + uuidValidForJs + "').hide(); ");
			buil.append(" } else { ");
			buil.append("jQuery('.clickableCard').unbind('click'); jQuery('._jsPlumb_connector').remove(); jQuery('._jsPlumb_overlay').remove(); jQuery('._jsPlumb_endpoint').remove(); "
					+ "for (var index = 0; index < arrows.length; index++) { "
					+ "var e0 = jsPlumb.addEndpoint(arrows[index]['source']); "
					+ "var e1 = jsPlumb.addEndpoint(arrows[index]['target']); "
					+ "jsPlumb.connect({ source:e0, target:e1, connector:['Bezier', { curviness:70 }], overlays : [ "
					+ "					['Label', {location:0.7, id:'label', events:{ } }], ['Arrow', { "
					+ "						cssClass:'l1arrow',  location:0.5, width:20,length:20 }]]}); } ");

			buil.append("var plumbSource, plumbTarget; "
					+ "jQuery('.clickableCard').click(function (event) { "
					+ "if (cardAlreadySelected) { "
					+ "	cardAlreadySelected = false; "
					+ "	plumbTarget = jQuery('#' + event.target.id).parent().parent().parent().parent().attr('id'); "
					+ " Wicket.Ajax.get({ 'u' : jQuery('#' + plumbTarget).data('arrowDrawUrl') + '&source=' + plumbSource + '&target=' + plumbTarget}); "
					+ "} else { "
					+ "	cardAlreadySelected = true; "
					+ "	plumbSource = jQuery('#' + event.target.id).parent().parent().parent().parent().attr('id'); "
					+ "}}); };");

			buil.append("jQuery('#tapHandleImage" + uuidValidForJs + "').unbind('click'); ");
			buil.append("var tapUrl" + uuidValidForJs + " = jQuery('#tapHandleImage"
					+ uuidValidForJs + "').data('tapUrl'); ");
			buil.append("Wicket.Ajax.get({'u': tapUrl" + uuidValidForJs + " + '&uuid="
					+ mc.getUuid() + "', 'e': 'click', 'c' : 'tapHandleImage" + uuidValidForJs
					+ "'}); ");

			buil.append("var dragUrl" + uuidValidForJs + " = jQuery('#handleImage" + uuidValidForJs
					+ "').data('dragUrl'); ");

			buil.append("jQuery('#cardHandle"
					+ uuidValidForJs
					+ "').draggable({ handle : '#handleImage"
					+ uuidValidForJs
					+ "', helper : 'original'"
					+ ", stop: function(event, ui) { "
					+ " if (!shouldMove) { shouldMove = true; return; } "
					+ " var card = jQuery('#' + event.target.id.replace('handleImage','cardHandle')); "
					+ "Wicket.Ajax.get({ 'u' : dragUrl" + uuidValidForJs
					+ " + '&posX=' + (ui.offset.left) + '&posY=' + (ui.offset.top)}); " + "} }); ");

			// The hand image is a drop target
			buil.append("jQuery('#putToHand').droppable({ accept: '.magicCard', drop: function(event, ui) { "
					+ "shouldMove = false; "
					+ "if (jQuery('#' + ui.draggable.context.id.replace('cardHandle','handleImage')).next().length != 0  && jQuery('#' + ui.draggable.context.id.replace('cardHandle','handleImage')).next().next().next().children(':first').attr('class') === 'token') { "
					+ "Wicket.Ajax.get({ 'u' : jQuery('#' + ui.draggable.context.id.replace('cardHandle','handleImage')).data('destroyUrl') });"
					+ " return; } "
					+ "jQuery('#' + ui.draggable.context.id).hide(); "
					+ "Wicket.Ajax.get({ 'u' : jQuery('#' + ui.draggable.context.id.replace('cardHandle','handleImage')).data('handUrl') + '&uuid='+ ui.draggable.context.id.replace('cardHandle','') }); "
					+ "}}); ");

			// The graveyard image is a drop target
			buil.append("jQuery('#putToGraveyard').droppable({ accept: '.magicCard', drop: function(event, ui) { "
					+ "shouldMove = false; "
					+ "if (jQuery('#' + ui.draggable.context.id.replace('cardHandle','handleImage')).next().length != 0  && jQuery('#' + ui.draggable.context.id.replace('cardHandle','handleImage')).next().next().next().children(':first').attr('class') === 'token') { "
					+ "Wicket.Ajax.get({ 'u' : jQuery('#' + ui.draggable.context.id.replace('cardHandle','handleImage')).data('destroyUrl') });"
					+ " return; } "
					+ "jQuery('#' + ui.draggable.context.id).hide(); "
					+ "Wicket.Ajax.get({ 'u' : jQuery('#' + ui.draggable.context.id.replace('cardHandle','handleImage')).data('graveyardUrl') + '&uuid='+ ui.draggable.context.id.replace('cardHandle','') });"
					+ "}}); ");

			// The exile image is a drop target
			buil.append("jQuery('#putToExile').droppable({ accept: '.magicCard', drop: function(event, ui) { "
					+ "shouldMove = false; "
					+ "if (jQuery('#' + ui.draggable.context.id.replace('cardHandle','handleImage')).next().length != 0  && jQuery('#' + ui.draggable.context.id.replace('cardHandle','handleImage')).next().next().next().children(':first').attr('class') === 'token') { "
					+ "Wicket.Ajax.get({ 'u' : jQuery('#' + ui.draggable.context.id.replace('cardHandle','handleImage')).data('destroyUrl') });"
					+ " return; } "
					+ "jQuery('#' + ui.draggable.context.id).hide(); "
					+ "Wicket.Ajax.get({ 'u' : jQuery('#' + ui.draggable.context.id.replace('cardHandle','handleImage')).data('exileUrl') + '&uuid='+ ui.draggable.context.id.replace('cardHandle','') });"
					+ "}}); ");

			buil.append("}, 175); ");

			target.appendJavaScript(buil.toString());
		}
	}

	public static void updateHand(final AjaxRequestTarget target)
	{
		((HomePage)target.getPage()).getGalleryParent().addOrReplace(new HandComponent("gallery"));
		target.add(((HomePage)target.getPage()).getGalleryParent());

		target.appendJavaScript(JavaScriptUtils.REACTIVATE_HAND_COMPONENT_JAVASCRIPT);
	}

	public static void updateHand(final AjaxRequestTarget target, final Long gameId,
			final Long playerId, final Long deckId)
	{
		((HomePage)target.getPage()).getGalleryParent().addOrReplace(
				new HandComponent("gallery", gameId, playerId, deckId));
		target.add(((HomePage)target.getPage()).getGalleryParent());

		target.appendJavaScript(JavaScriptUtils.REACTIVATE_HAND_COMPONENT_JAVASCRIPT);
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
}
