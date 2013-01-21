package org.alienlabs.hatchetharry.view.clientsideutil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.alienlabs.hatchetharry.HatchetHarrySession;
import org.alienlabs.hatchetharry.model.CardZone;
import org.alienlabs.hatchetharry.model.MagicCard;
import org.alienlabs.hatchetharry.service.PersistenceService;
import org.alienlabs.hatchetharry.view.component.CardPanel;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;

public class JavaScriptUtils
{
	public static final String REACTIVATE_GRAVEYARD_JAVASCRIPT_COMPONENT = "var theIntGraveyard = null; var $crosslinkGraveyard, $navthumbGraveyard; var curclickedGraveyard = 0; theIntervalGraveyard = function(cur) { if (typeof cur != 'undefined') curclickedGraveyard = cur; $crosslinkGraveyard.removeClass('active-thumbGraveyard'); $navthumbGraveyard.eq(curclickedGraveyard).parent().addClass('active-thumbGraveyard'); jQuery('.stripNavGraveyard ul li a').eq(curclickedGraveyard).trigger('click'); $crosslinkGraveyard.removeClass('active-thumbGraveyard'); $navthumbGraveyard.eq(curclickedGraveyard).parent().addClass('active-thumbGraveyard'); jQuery('.stripNavGraveyard ul li a').eq(curclickedGraveyard).trigger('click'); curclickedGraveyard++; if (6 == curclickedGraveyard) curclickedGraveyard = 0; }; jQuery('#graveyard-main-photo-slider').codaSliderGraveyard(); $navthumbGraveyard = jQuery('.graveyard-nav-thumb'); $crosslinkGraveyard = jQuery('.graveyard-cross-link'); $navthumbGraveyard.click(function() { var $this = jQuery(this); theIntervalGraveyard($this.parent().attr('href').slice(1) - 1); return false; }); theIntervalGraveyard(); ";
	public static final String REACTIVATE_HAND_JAVASCRIPT_COMPONENT = "var theInt = null; var $crosslink, $navthumb; var curclicked = 0; theInterval = function(cur) { if (typeof cur != 'undefined') curclicked = cur; $crosslink.removeClass('active-thumb'); $navthumb.eq(curclicked).parent().addClass('active-thumb'); jQuery('.stripNav ul li a').eq(curclicked).trigger('click'); $crosslink.removeClass('active-thumb'); $navthumb.eq(curclicked).parent().addClass('active-thumb'); jQuery('.stripNav ul li a').eq(curclicked).trigger('click'); curclicked++; if (6 == curclicked) curclicked = 0; }; jQuery('#main-photo-slider').codaSlider(); $navthumb = jQuery('.nav-thumb'); $crosslink = jQuery('.cross-link'); $navthumb.click(function() { var $this = jQuery(this); theInterval($this.parent().attr('href').slice(1) - 1); return false; }); theInterval(); ";

	public static void putCardInGame(final AjaxRequestTarget target, final Long cardPlaceholderId,
			final PersistenceService persistenceService, final UUID uuidToLookFor,
			final WebMarkupContainer cardParent)
	{
		final Long id = cardPlaceholderId;
		final String placeholder = "cardPlaceholdera" + id;

		final MagicCard card = persistenceService.getCardFromUuid(uuidToLookFor);

		final CardPanel cp = new CardPanel(placeholder, card.getSmallImageFilename(),
				card.getBigImageFilename(), card.getUuidObject());
		cp.setOutputMarkupId(true);
		cp.setMarkupId(placeholder);
		HatchetHarrySession.get().addCardInBattleField(cp);

		cardParent.addOrReplace(cp);
		target.add(cardParent);
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

		buf.append(" }, 3000); ");

		target.appendJavaScript(buf.toString());
	}

	// When cards have been in graveyard their markup need to be removed from
	// battlefield
	public static void removeNonRelevantCardsFromBatlefield(final AjaxRequestTarget target,
			final PersistenceService persistenceService, final Long gameId)
	{
		final List<MagicCard> allCardsInGraveyard = persistenceService
				.getAllCardsInGraveyardForAGame(gameId);

		final StringBuffer buf = new StringBuffer();
		buf.append("window.setTimeout(function() { ");

		for (final MagicCard aCard : allCardsInGraveyard)
		{
			buf.append("jQuery('#menutoggleButton" + aCard.getUuid() + "').remove(); ");
		}

		final ArrayList<MagicCard> toRemove = HatchetHarrySession.get()
				.getAllCardsWhichHaveBeenInBattlefield();
		for (final MagicCard aCard : toRemove)
		{
			final MagicCard freshCard = persistenceService.getCardFromUuid(aCard.getUuidObject());

			Collections.sort(freshCard.getCardPlaceholderIds());
			for (final String _cardPlaceholderId : freshCard.getCardPlaceholderIds())
			{
				if (!CardZone.BATTLEFIELD.equals(freshCard.getZone())
						|| (freshCard.getCardPlaceholderIds().indexOf(_cardPlaceholderId) > 0))
				{
					buf.append("jQuery('#" + _cardPlaceholderId + "').children(0).remove(); ");
				}
			}
		}

		buf.append(" }, 3000); ");

		target.appendJavaScript(buf.toString());
	}

}
