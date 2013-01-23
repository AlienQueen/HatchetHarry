package org.alienlabs.hatchetharry.view.clientsideutil;

import java.util.List;

import org.alienlabs.hatchetharry.model.MagicCard;
import org.alienlabs.hatchetharry.service.PersistenceService;
import org.alienlabs.hatchetharry.view.component.GraveyardComponent;
import org.alienlabs.hatchetharry.view.component.HandComponent;
import org.alienlabs.hatchetharry.view.page.HomePage;
import org.apache.wicket.ajax.AjaxRequestTarget;

public class JavaScriptUtils
{
	public static final String REACTIVATE_GRAVEYARD_JAVASCRIPT_COMPONENT = "var theIntGraveyard = null; var $crosslinkGraveyard, $navthumbGraveyard; var curclickedGraveyard = 0; theIntervalGraveyard = function(cur) { if (typeof cur != 'undefined') curclickedGraveyard = cur; $crosslinkGraveyard.removeClass('active-thumbGraveyard'); $navthumbGraveyard.eq(curclickedGraveyard).parent().addClass('active-thumbGraveyard'); jQuery('.stripNavGraveyard ul li a').eq(curclickedGraveyard).trigger('click'); $crosslinkGraveyard.removeClass('active-thumbGraveyard'); $navthumbGraveyard.eq(curclickedGraveyard).parent().addClass('active-thumbGraveyard'); jQuery('.stripNavGraveyard ul li a').eq(curclickedGraveyard).trigger('click'); curclickedGraveyard++; if (6 == curclickedGraveyard) curclickedGraveyard = 0; }; jQuery('#graveyard-main-photo-slider').codaSliderGraveyard(); $navthumbGraveyard = jQuery('.graveyard-nav-thumb'); $crosslinkGraveyard = jQuery('.graveyard-cross-link'); $navthumbGraveyard.click(function() { var $this = jQuery(this); theIntervalGraveyard($this.parent().attr('href').slice(1) - 1); return false; }); theIntervalGraveyard(); ";
	public static final String REACTIVATE_HAND_JAVASCRIPT_COMPONENT = "var theInt = null; var $crosslink, $navthumb; var curclicked = 0; theInterval = function(cur) { if (typeof cur != 'undefined') curclicked = cur; $crosslink.removeClass('active-thumb'); $navthumb.eq(curclicked).parent().addClass('active-thumb'); jQuery('.stripNav ul li a').eq(curclicked).trigger('click'); $crosslink.removeClass('active-thumb'); $navthumb.eq(curclicked).parent().addClass('active-thumb'); jQuery('.stripNav ul li a').eq(curclicked).trigger('click'); curclicked++; if (6 == curclicked) curclicked = 0; }; jQuery('#main-photo-slider').codaSlider(); $navthumb = jQuery('.nav-thumb'); $crosslink = jQuery('.cross-link'); $navthumb.click(function() { var $this = jQuery(this); theInterval($this.parent().attr('href').slice(1) - 1); return false; }); theInterval(); ";

	public static void updateCardsInBattlefield(final AjaxRequestTarget target)
	{
		final HomePage homePage = (HomePage)target.getPage();
		homePage.getParentPlaceholder().addOrReplace(homePage.generateCardListView());
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
		for (final MagicCard aCard : allCardsInBattlefield)
		{
			buf.append("var card = jQuery(\"#menutoggleButton" + aCard.getUuid() + "\"); "
					+ "card.css(\"position\", \"absolute\"); " + "card.css(\"left\", \""
					+ aCard.getX() + "px\");" + "card.css(\"top\", \"" + aCard.getY() + "px\"); ");

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

		buf.append("jQuery(\".myMenu\").buildMenu({" + "additionalData : \"pippo=1\","
				+ "menuWidth : 200," + "openOnRight : false,"
				+ "menuSelector : \".menuContainer\"," + "iconPath : \"/image/\","
				+ "hasImages : true," + "fadeInTime : 100," + "fadeOutTime : 300,"
				+ "adjustLeft : 0," + "minZindex : \"auto\"," + "adjustTop : 0," + "opacity : .95,"
				+ "shadow : true," + "shadowColor : \"#ccc\"," + "hoverIntent : 0,"
				+ "openOnClick : false," + "closeOnMouseOut : true," + "closeAfter : 1000,"
				+ "submenuHoverIntent : 200" + "}); ");
		buf.append(" }, 2000); ");

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
