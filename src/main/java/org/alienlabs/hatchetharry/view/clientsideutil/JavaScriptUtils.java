package org.alienlabs.hatchetharry.view.clientsideutil;

import java.util.List;

import org.alienlabs.hatchetharry.model.MagicCard;
import org.alienlabs.hatchetharry.service.PersistenceService;
import org.alienlabs.hatchetharry.view.component.GraveyardComponent;
import org.alienlabs.hatchetharry.view.component.HandComponent;
import org.alienlabs.hatchetharry.view.page.HomePage;
import org.apache.wicket.ajax.AjaxRequestTarget;

public final class JavaScriptUtils
{
	public static final String REACTIVATE_GRAVEYARD_COMPONENT_JAVASCRIPT = "var theIntGraveyard = null; var $crosslinkGraveyard, $navthumbGraveyard; var curclickedGraveyard = 0; theIntervalGraveyard = function(cur) { if (typeof cur != 'undefined') curclickedGraveyard = cur; $crosslinkGraveyard.removeClass('active-thumbGraveyard'); $navthumbGraveyard.eq(curclickedGraveyard).parent().addClass('active-thumbGraveyard'); jQuery('.stripNavGraveyard ul li a').eq(curclickedGraveyard).trigger('click'); $crosslinkGraveyard.removeClass('active-thumbGraveyard'); $navthumbGraveyard.eq(curclickedGraveyard).parent().addClass('active-thumbGraveyard'); jQuery('.stripNavGraveyard ul li a').eq(curclickedGraveyard).trigger('click'); curclickedGraveyard++; if (6 == curclickedGraveyard) curclickedGraveyard = 0; }; jQuery('#graveyard-main-photo-slider').codaSliderGraveyard(); $navthumbGraveyard = jQuery('.graveyard-nav-thumb'); $crosslinkGraveyard = jQuery('.graveyard-cross-link'); $navthumbGraveyard.click(function() { var $this = jQuery(this); theIntervalGraveyard($this.parent().attr('href').slice(1) - 1); return false; }); theIntervalGraveyard(); function updateGraveyardLabel(){ var graveyardCardName = jQuery('#graveyardGallery .active-thumbGraveyard .nav-thumb').attr('name'); if (graveyardCardName == undefined) { graveyardCardName = jQuery('#graveyardGallery .active-thumb .nav-thumb').attr('name'); } jQuery('#graveyardCardLabel').text(graveyardCardName); }; jQuery(function() { setTimeout(function() { updateGraveyardLabel(); jQuery('#graveyardGallery .cross-link .nav-thumb').click(updateGraveyardLabel); }, 500); });";
	public static final String REACTIVATE_HAND_COMPONENT_JAVASCRIPT = "var theInt = null; var $crosslink, $navthumb; var curclicked = 0; theInterval = function(cur) { if (typeof cur != 'undefined') curclicked = cur; $crosslink.removeClass('active-thumb'); $navthumb.eq(curclicked).parent().addClass('active-thumb'); jQuery('.stripNav ul li a').eq(curclicked).trigger('click'); $crosslink.removeClass('active-thumb'); $navthumb.eq(curclicked).parent().addClass('active-thumb'); jQuery('.stripNav ul li a').eq(curclicked).trigger('click'); curclicked++; if (6 == curclicked) curclicked = 0; }; jQuery('#main-photo-slider').codaSlider(); $navthumb = jQuery('.nav-thumb'); $crosslink = jQuery('.cross-link'); $navthumb.click(function() { var $this = jQuery(this); theInterval($this.parent().attr('href').slice(1) - 1); return false; }); theInterval(); function updateHandLabel(){ var handCardName = jQuery('#handGallery .active-thumbGraveyard .nav-thumb').attr('name'); if (handCardName == undefined) { handCardName = jQuery('#handGallery .active-thumb .nav-thumb').attr('name'); } jQuery('#cardLabel').text(handCardName); }; jQuery(function() { setTimeout(function() { updateHandLabel(); jQuery('#handGallery .cross-link .nav-thumb').click(updateHandLabel); }, 500); }); ";
	public static final String DEACTIVATE_END_OF_TURN_LINKS = "jQuery('#acceptEndTurnLink').attr('style', 'cursor: wait; color: black;'); jQuery('#endTurnActionLink').attr('style', 'cursor: wait; color: black;'); ";
	public static final String REACTIVATE_END_OF_TURN_LINKS = "jQuery('#acceptEndTurnLink').attr('style', 'cursor: pointer; color: white;'); jQuery('#endTurnActionLink').attr('style', 'cursor: pointer; color: white;'); ";
	public static final String HIDE_MENUS = "jQuery('.categories').hide();";
	public static final String HIDE_ALL_TOOLTIPS = "jQuery('.tooltip').attr('style', 'display: none;'); ";

	private JavaScriptUtils()
	{
	}

	public static void updateCardsAndRestoreStateInBattlefield(final AjaxRequestTarget target, final PersistenceService persistenceService, final Long gameId, final MagicCard mc, final boolean added)
	{
		final List<MagicCard> allCardsInBattlefield = ((HomePage)target.getPage()).getAllCardsInBattlefield().getModelObject();
		JavaScriptUtils.updateCardsInBattlefield(target, persistenceService, allCardsInBattlefield, mc, added);
		JavaScriptUtils.restoreStateOfCardsInBattlefield(target, allCardsInBattlefield);
	}

	public static void updateCardsInBattlefield(final AjaxRequestTarget target, final PersistenceService persistenceService, final List<MagicCard> allCardsInBattlefield, final MagicCard mc, final boolean added)
	{
		final HomePage homePage = (HomePage)target.getPage();
		final List<MagicCard> allCards = homePage.getAllCardsInBattlefield().getModelObject();

		if (null != mc)
		{
			if (added)
			{
				allCards.add(mc);
			}
			else
			{
				allCards.remove(mc);
			}

			for (int i = 0; i < allCards.size(); i++)
			{
				final MagicCard card = allCards.get(i);
				final MagicCard temp = persistenceService.getCardFromUuid(card.getUuidObject());
				card.setX(temp.getX());
				card.setY(temp.getY());
			}
			target.add(homePage.getParentPlaceholder());
		}
		else
		{
			homePage.getParentPlaceholder().addOrReplace(homePage.generateCardListView(allCardsInBattlefield));
			target.add(homePage.getParentPlaceholder());
		}
	}

	/*
	 * Three things to do: - card positions - card tapped / untapped state -
	 * activate tooltip again
	 */
	public static void restoreStateOfCardsInBattlefield(final AjaxRequestTarget target, final List<MagicCard> allCardsInBattlefield)
	{
		final StringBuffer buf = new StringBuffer();
		buf.append("var shouldMove = true; ");
		buf.append("window.setTimeout(function() { ");

		for (int i = 0; i < allCardsInBattlefield.size(); i++)
		{
			final MagicCard aCard = allCardsInBattlefield.get(i);

			final String uuidValidForJs = aCard.getUuid().replace("-", "_");

			if (aCard.isTapped())
			{
				buf.append("jQuery('#card" + uuidValidForJs + "').rotate(90); ");
			}
			else
			{
				buf.append("jQuery('#card" + uuidValidForJs + "').rotate(0); ");
			}

			buf.append("var card = jQuery('#cardHandle" + uuidValidForJs + "'); card.css('position', 'absolute'); card.css('left', '" + aCard.getX() + "px'); card.css('top', '" + aCard.getY() + "px'); ");

			buf.append("jQuery('#card" + uuidValidForJs
					+ "').click(function(e) { jQuery('#cardTooltip" + uuidValidForJs
					+ "').attr('style', 'display: block'); }); ");

			// For mobile
			buf.append("var hammertime" + uuidValidForJs + " = jQuery('#card" + uuidValidForJs
					+ "').hammer(); ");
			buf.append("hammertime" + uuidValidForJs + ".on('tap', function(ev) { ");
			buf.append("  jQuery('#cardTooltip" + uuidValidForJs
					+ "').attr('style', 'display: block'); ");
			buf.append("}); ");

			buf.append("jQuery('#tapHandleImage" + uuidValidForJs + "').unbind('click'); ");
			buf.append("var tapUrl" + uuidValidForJs + " = $('#tapHandleImage" + uuidValidForJs
					+ "').data('tapUrl'); ");
			buf.append("Wicket.Ajax.get({'u': tapUrl" + uuidValidForJs + " + '&uuid="
					+ aCard.getUuid() + "', 'e': 'click', 'c' : 'tapHandleImage" + uuidValidForJs
					+ "'}); ");

			buf.append("var dragUrl" + uuidValidForJs + " = jQuery('#handleImage" + uuidValidForJs
					+ "').data('dragUrl'); ");

			buf.append("jQuery('#cardHandle"
					+ uuidValidForJs
					+ "').draggable({ handle : '#handleImage"
					+ uuidValidForJs
					+ "', stop: function(event, ui) { "
					+ " if (!shouldMove) { shouldMove = true; return; } "
					+ "var card = jQuery('#' + event.target.id.replace('handleImage','cardHandle')); "
					+ "Wicket.Ajax.get({ 'u' : dragUrl" + uuidValidForJs
					+ " + '&posX=' + card.position().left + '&posY=' + card.position().top}); "
					+ "} }); ");
		}

		// Put to graveyard by drag & drop
		buf.append("jQuery('#putToGraveyard').droppable({ ");
		buf.append("accept: '");

		if (allCardsInBattlefield.size() >= 1)
		{
			final MagicCard aCard = allCardsInBattlefield.get(0);
			final String uuidValidForJs = aCard.getUuid().replace("-", "_");
			buf.append("#cardHandle" + uuidValidForJs);
		}

		for (int i = 1; i < allCardsInBattlefield.size(); i++)
		{
			final MagicCard aCard = allCardsInBattlefield.get(i);
			final String uuidValidForJs = aCard.getUuid().replace("-", "_");

			buf.append(", #cardHandle" + uuidValidForJs);
		}

		buf.append("', drop: function(event, ui) { ");
		buf.append("shouldMove = false; ");
		buf.append("jQuery('#' + ui.draggable.context.id).hide(); ");
		buf.append("Wicket.Ajax.get({ 'u' : ");
		buf.append("jQuery('#' + ui.draggable.context.id.replace('cardHandle','handleImage')).data('graveyardUrl') + '&uuid='+ ui.draggable.context.id.replace('cardHandle','') }); } }); ");

		// Put to hand by drag & drop
		buf.append("jQuery('#putToHand').droppable({ ");
		buf.append("accept: '");

		if (allCardsInBattlefield.size() >= 1)
		{
			final MagicCard aCard = allCardsInBattlefield.get(0);
			final String uuidValidForJs = aCard.getUuid().replace("-", "_");
			buf.append("#cardHandle" + uuidValidForJs);
		}

		for (int i = 1; i < allCardsInBattlefield.size(); i++)
		{
			final MagicCard aCard = allCardsInBattlefield.get(i);
			final String uuidValidForJs = aCard.getUuid().replace("-", "_");

			buf.append(", #cardHandle" + uuidValidForJs);
		}

		buf.append("', drop: function(event, ui) { ");
		buf.append("shouldMove = false; ");
		buf.append("jQuery('#' + ui.draggable.context.id).hide(); ");
		buf.append("Wicket.Ajax.get({ 'u' : ");
		buf.append("jQuery('#' + ui.draggable.context.id.replace('cardHandle','handleImage')).data('handUrl') + '&uuid='+ ui.draggable.context.id.replace('cardHandle','') }); } }); ");

		buf.append("}, 300); ");

		target.appendJavaScript(buf.toString());
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
}
