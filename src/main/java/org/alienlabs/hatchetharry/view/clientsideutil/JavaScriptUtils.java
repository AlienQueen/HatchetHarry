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
	public static final String REACTIVATE_GRAVEYARD_JAVASCRIPT_COMPONENT = "var theIntGraveyard = null; var $crosslinkGraveyard, $navthumbGraveyard; var curclickedGraveyard = 0; theIntervalGraveyard = function(cur) { if (typeof cur != 'undefined') curclickedGraveyard = cur; $crosslinkGraveyard.removeClass('active-thumbGraveyard'); $navthumbGraveyard.eq(curclickedGraveyard).parent().addClass('active-thumbGraveyard'); jQuery('.stripNavGraveyard ul li a').eq(curclickedGraveyard).trigger('click'); $crosslinkGraveyard.removeClass('active-thumbGraveyard'); $navthumbGraveyard.eq(curclickedGraveyard).parent().addClass('active-thumbGraveyard'); jQuery('.stripNavGraveyard ul li a').eq(curclickedGraveyard).trigger('click'); curclickedGraveyard++; if (6 == curclickedGraveyard) curclickedGraveyard = 0; }; jQuery('#graveyard-main-photo-slider').codaSliderGraveyard(); $navthumbGraveyard = jQuery('.graveyard-nav-thumb'); $crosslinkGraveyard = jQuery('.graveyard-cross-link'); $navthumbGraveyard.click(function() { var $this = jQuery(this); theIntervalGraveyard($this.parent().attr('href').slice(1) - 1); return false; }); theIntervalGraveyard(); ";
	public static final String REACTIVATE_HAND_JAVASCRIPT_COMPONENT = "var theInt = null; var $crosslink, $navthumb; var curclicked = 0; theInterval = function(cur) { if (typeof cur != 'undefined') curclicked = cur; $crosslink.removeClass('active-thumb'); $navthumb.eq(curclicked).parent().addClass('active-thumb'); jQuery('.stripNav ul li a').eq(curclicked).trigger('click'); $crosslink.removeClass('active-thumb'); $navthumb.eq(curclicked).parent().addClass('active-thumb'); jQuery('.stripNav ul li a').eq(curclicked).trigger('click'); curclicked++; if (6 == curclicked) curclicked = 0; }; jQuery('#main-photo-slider').codaSlider(); $navthumb = jQuery('.nav-thumb'); $crosslink = jQuery('.cross-link'); $navthumb.click(function() { var $this = jQuery(this); theInterval($this.parent().attr('href').slice(1) - 1); return false; }); theInterval(); ";

	private JavaScriptUtils()
	{
	}

	public static void updateCardsInBattlefield(final AjaxRequestTarget target, final Long gameId)
	{
		final HomePage homePage = (HomePage)target.getPage();
		homePage.getParentPlaceholder().addOrReplace(homePage.generateCardListView(gameId));
		target.add(homePage.getParentPlaceholder());
	}

	/*
	 * Three things to do: - card positions - card tapped / untapped state -
	 * activate tooltip again
	 */
	public static void restoreStateOfCardsInBattlefield(final AjaxRequestTarget target,
			final PersistenceService persistenceService, final Long gameId)
	{
		final StringBuffer buf = new StringBuffer();
		buf.append("window.setTimeout(function() { ");

		final List<MagicCard> allCardsInBattlefield = persistenceService
				.getAllCardsInBattleFieldForAGame(gameId);

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

			buf.append("jQuery('#card" + uuidValidForJs
					+ "').easyTooltip({ useElement: 'cardTooltip" + uuidValidForJs + "'}); ");

			buf.append("jQuery('#tapHandleImage" + uuidValidForJs + "').unbind('click'); ");
			buf.append("var tapUrl = $('#tapHandleImage" + uuidValidForJs + "').data('tapUrl'); ");
			buf.append("Wicket.Ajax.get({'u': tapUrl + '&uuid=" + aCard.getUuid()
					+ "', 'e': 'click', 'c' : 'tapHandleImage" + uuidValidForJs + "'}); ");

			buf.append("var dragUrl = $('#handleImage" + uuidValidForJs + "').data('dragUrl'); ");
			buf.append("jQuery('#cardHandle"
					+ uuidValidForJs
					+ "').draggable({ handle : '#handleImage"
					+ uuidValidForJs
					+ "' , stop: function(event, ui) { "
					+ "var card = jQuery('#' + event.target.id.replace('handleImage','cardHandle')); "
					+ "Wicket.Ajax.get({ 'u' : dragUrl + '&posX=' + card.position().left + '&posY=' + card.position().top}); "
					+ "} }); ");
			buf.append("var graveyardUrl = jQuery('#handleImage" + uuidValidForJs
					+ "').data('graveyardUrl'); ");
			buf.append("var handUrl = jQuery('#handleImage" + uuidValidForJs
					+ "').data('handUrl'); ");
			buf.append("jQuery('#putToGraveyard').droppable({  drop: function(event, ui) { "
					+ "Wicket.Ajax.get({ 'u' : graveyardUrl + '&uuid=" + uuidValidForJs + "' }); "
					+ "return false; } }); ");
		}

		buf.append(" }, 3000); ");

		target.appendJavaScript(buf.toString());
	}

	public static void updateHand(final AjaxRequestTarget target)
	{
		((HomePage)target.getPage()).getGalleryParent().addOrReplace(new HandComponent("gallery"));
		target.add(((HomePage)target.getPage()).getGalleryParent());

		target.appendJavaScript(JavaScriptUtils.REACTIVATE_HAND_JAVASCRIPT_COMPONENT);
	}


	public static void updateGraveyard(final AjaxRequestTarget target)
	{
		((HomePage)target.getPage()).getGraveyardParent().addOrReplace(
				new GraveyardComponent("graveyard"));
		target.add(((HomePage)target.getPage()).getGraveyardParent());

		target.appendJavaScript(JavaScriptUtils.REACTIVATE_GRAVEYARD_JAVASCRIPT_COMPONENT);
	}
}
