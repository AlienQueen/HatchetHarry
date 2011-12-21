package org.alienlabs.hatchetharry.view.component;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.alienlabs.hatchetharry.HatchetHarryApplication;
import org.alienlabs.hatchetharry.HatchetHarrySession;
import org.alienlabs.hatchetharry.model.MagicCard;
import org.alienlabs.hatchetharry.service.PersistenceService;
import org.alienlabs.hatchetharry.view.page.HomePage;
import org.apache.wicket.Application;
import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.injection.web.InjectorHolder;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.protocol.http.servlet.ServletWebRequest;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.template.PackagedTextTemplate;
import org.apache.wicket.util.template.TextTemplate;
import org.atmosphere.cpr.AtmosphereResourceEventListener;
import org.atmosphere.cpr.BroadcastFilter;
import org.atmosphere.cpr.Meteor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

@SuppressWarnings("serial")
public class PlayCardFromHandBehavior extends AbstractDefaultAjaxBehavior
{
	static final Logger logger = LoggerFactory.getLogger(PlayCardFromHandBehavior.class);

	@SpringBean
	private PersistenceService persistenceService;

	private final WebMarkupContainer thumbParent;
	private final WebMarkupContainer cardParent;

	private UUID uuidToLookFor;
	private int currentCard;

	private CardPanel cp;

	public PlayCardFromHandBehavior(final WebMarkupContainer _thumbParent,
			final WebMarkupContainer _cardParent, final UUID _uuidToLookFor, final int _currentCard)
	{
		super();
		InjectorHolder.getInjector().inject(this);
		this.thumbParent = _thumbParent;
		this.cardParent = _cardParent;
		this.uuidToLookFor = _uuidToLookFor;
		this.currentCard = _currentCard;
	}

	@Override
	protected void respond(final AjaxRequestTarget target)
	{
		PlayCardFromHandBehavior.logger.info("respond");

		final ServletWebRequest servletWebRequest = (ServletWebRequest)target.getPage()
				.getRequest();
		final HttpServletRequest request = servletWebRequest.getHttpServletRequest();
		final String jsessionid = request.getRequestedSessionId();
		try
		{
			this.uuidToLookFor = UUID.fromString(request.getParameter("card"));
		}
		catch (final IllegalArgumentException e)
		{
			PlayCardFromHandBehavior.logger.error("bad uuid: " + request.getParameter("card"), e);
		}
		final String stop = request.getParameter("stop");
		PlayCardFromHandBehavior.logger.info("url: " + request.getQueryString());

		int _indexOfClickedCard = -1;
		try
		{
			_indexOfClickedCard = Integer.parseInt(request.getParameter("indexOfClickedCard"));
		}
		catch (final NumberFormatException e)
		{
			PlayCardFromHandBehavior.logger.info("Error which should never happen!");
		}

		this.currentCard = _indexOfClickedCard;

		final MagicCard card = this.persistenceService.getCardFromUuid(this.uuidToLookFor);
		final List<MagicCard> all = HatchetHarrySession.get().getFirstCardsInHand();

		if (null != card)
		{
			PlayCardFromHandBehavior.logger.info("Removed? " + all.remove(card));
			HatchetHarrySession.get().setFirstCardsInHand(all);

			PlayCardFromHandBehavior.logger.info("card title: " + card.getTitle() + ", uuid: "
					+ card.getUuidObject() + ", filename: " + card.getBigImageFilename());

			if ("true".equals(stop))
			{
				PlayCardFromHandBehavior.logger.info("stopping round-trips");

				final String id = "cardPlaceholdera"
						+ (HatchetHarrySession.get().getPlaceholderNumber() + 1);
				HatchetHarrySession.get().setPlaceholderNumber(
						HatchetHarrySession.get().getPlaceholderNumber() + 1);

				this.cp = new CardPanel(id, card.getSmallImageFilename(),
						card.getBigImageFilename(), card.getUuidObject());
				this.cp.setOutputMarkupId(true);
				HatchetHarrySession.get().addCardInBattleField(this.cp);

				this.thumbParent.addOrReplace(this.cp);
				target.addComponent(this.thumbParent);

				target.appendJavascript("jQuery(document).ready(function() { "
						+ "jQuery.gritter.add({ title : '"
						+ ((HatchetHarryApplication)Application.get()).getPlayer().getSide()
						+ "', text : \"has played \'" + card.getTitle()
						+ "\'!\", image : 'image/logoh2.gif', sticky : false, time : ''}); });");

				final List<CardPanel> list = HatchetHarrySession.get().getAllCardsInBattleField();
				for (final CardPanel aCard : list)
				{
					target.appendJavascript("jQuery('#card" + aCard.getUuid()
							+ "').bubbletip(jQuery('#cardBubbleTip" + aCard.getUuid()
							+ "'), {deltaDirection : 'right'});");
					try
					{
						final MagicCard mc = this.persistenceService.getCardFromUuid(aCard
								.getUuid());
						if (null != mc)
						{
							target.appendJavascript("var card = jQuery(\"#menutoggleButton"
									+ aCard.getUuid() + "\"); "
									+ "card.css(\"position\", \"absolute\"); "
									+ "card.css(\"left\", \"" + mc.getX() + "px\");"
									+ "card.css(\"top\", \"" + mc.getY() + "px\");");
						}
					}
					catch (final IllegalArgumentException e)
					{
						PlayCardFromHandBehavior.logger
								.error("error parsing UUID of moved card", e);
					}
				}

				final List<CardPanel> toRemove = HatchetHarrySession.get().getAllCardsToRemove();
				if ((null != toRemove) && (toRemove.size() > 0))
				{
					for (final CardPanel c : toRemove)
					{
						target.appendJavascript("jQuery('#" + c.getMarkupId() + "').remove();");
					}
				}
			}
			else if ((null != this.uuidToLookFor) && (!"undefined".equals(this.uuidToLookFor)))
			{
				PlayCardFromHandBehavior.logger.info("card: " + this.uuidToLookFor);

				HatchetHarrySession.get().setPlaceholderNumber(
						HatchetHarrySession.get().getPlaceholderNumber() + 1);

				PlayCardFromHandBehavior.logger.info("continue!");

				final String message = jsessionid + "~~~" + this.uuidToLookFor + "~~~"
						+ (_indexOfClickedCard == 6 ? 0 : _indexOfClickedCard + 1);
				PlayCardFromHandBehavior.logger.info(message);

				final Meteor meteor = Meteor
						.build(request, new LinkedList<BroadcastFilter>(), null);
				meteor.addListener((AtmosphereResourceEventListener)target.getPage());
				meteor.broadcast(message);

				final HandComponent gallery = new HandComponent("gallery");
				gallery.setOutputMarkupId(true);

				this.cardParent.addOrReplace(gallery);
				target.addComponent(this.cardParent);

				((HatchetHarryApplication)Application.get()).setPlayer(HatchetHarrySession.get()
						.getPlayer());
				target.appendJavascript("jQuery(document).ready(function() { var theInt = null; var $crosslink, $navthumb; var curclicked = 0; theInterval = function(cur) { if (typeof cur != 'undefined') curclicked = cur; $crosslink.removeClass('active-thumb'); $navthumb.eq(curclicked).parent().addClass('active-thumb'); jQuery('.stripNav ul li a').eq(curclicked).trigger('click'); $crosslink.removeClass('active-thumb'); $navthumb.eq(curclicked).parent().addClass('active-thumb'); jQuery('.stripNav ul li a').eq(curclicked).trigger('click'); curclicked++; if (6 == curclicked) curclicked = 0; }; jQuery('#main-photo-slider').codaSlider(); $navthumb = jQuery('.nav-thumb'); $crosslink = jQuery('.cross-link'); $navthumb.click(function() { var $this = jQuery(this); theInterval($this.parent().attr('href').slice(1) - 1); return false; }); theInterval(); });");
			}
		}
	}

	@Override
	public void renderHead(final IHeaderResponse response)
	{
		super.renderHead(response);

		final HashMap<String, Object> variables = new HashMap<String, Object>();
		variables.put("url", this.getCallbackUrl());
		variables.put("uuid", this.uuidToLookFor.toString());
		variables.put("uuidValidForJs", this.uuidToLookFor.toString().replace("-", "_"));
		variables.put("next", (this.currentCard == 6 ? 0 : this.currentCard + 1));
		variables.put("clicked", this.currentCard);

		final TextTemplate template1 = new PackagedTextTemplate(HomePage.class,
				"script/playCard/playCard.js");
		template1.interpolate(variables);

		response.renderJavascript(template1.asString(), null);
	}

	@Required
	public void setPersistenceService(final PersistenceService _persistenceService)
	{
		this.persistenceService = _persistenceService;
	}
}
