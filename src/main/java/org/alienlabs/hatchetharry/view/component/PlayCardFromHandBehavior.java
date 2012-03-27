package org.alienlabs.hatchetharry.view.component;

import java.util.ArrayList;
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
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.injection.Injector;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.protocol.http.servlet.ServletWebRequest;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.template.PackageTextTemplate;
import org.apache.wicket.util.template.TextTemplate;
import org.atmosphere.cpr.AtmosphereResourceEventListener;
import org.atmosphere.cpr.BroadcastFilter;
import org.atmosphere.cpr.Meteor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

public class PlayCardFromHandBehavior extends AbstractDefaultAjaxBehavior
{
	private static final long serialVersionUID = 1L;

	static final Logger LOGGER = LoggerFactory.getLogger(PlayCardFromHandBehavior.class);

	@SpringBean
	private PersistenceService persistenceService;

	private final WebMarkupContainer thumbParent;
	private final WebMarkupContainer cardParent;

	private UUID uuidToLookFor;
	private int currentCard;

	private CardPanel cp;
	private String side;

	public PlayCardFromHandBehavior(final WebMarkupContainer _thumbParent,
			final WebMarkupContainer _cardParent, final UUID _uuidToLookFor,
			final int _currentCard, final String _side)
	{
		super();
		Injector.get().inject(this);
		this.thumbParent = _thumbParent;
		this.cardParent = _cardParent;
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
		final String jsessionid = request.getRequestedSessionId();

		PlayCardFromHandBehavior.LOGGER.info("URL: " + request.getQueryString());

		long posX = 0;
		long posY = 0;
		if ((null != request.getParameter("posX")) && (!"".equals(request.getParameter("posX")))
				&& (!"null".equals(request.getParameter("posX"))))
		{
			posX = Long.parseLong(request.getParameter("posX"));
			PlayCardFromHandBehavior.LOGGER.info("posX: " + posX);
		}
		if ((null != request.getParameter("posY")) && (!"".equals(request.getParameter("posY")))
				&& (!"null".equals(request.getParameter("posY"))))
		{
			posY = Long.parseLong(request.getParameter("posY"));
			PlayCardFromHandBehavior.LOGGER.info("posY: " + posY);
		}

		try
		{
			this.uuidToLookFor = UUID.fromString(request.getParameter("card"));
		}
		catch (final IllegalArgumentException e)
		{
			PlayCardFromHandBehavior.LOGGER.error("bad uuid: " + request.getParameter("card"), e);
		}
		final String stop = request.getParameter("stop");
		PlayCardFromHandBehavior.LOGGER.info("url: " + request.getQueryString());

		int _indexOfClickedCard = -1;
		try
		{
			_indexOfClickedCard = Integer.parseInt(request.getParameter("indexOfClickedCard"));
		}
		catch (final NumberFormatException e)
		{
			PlayCardFromHandBehavior.LOGGER.error("Error which should never happen!");
		}

		this.currentCard = _indexOfClickedCard;

		final MagicCard card = this.persistenceService.getCardFromUuid(this.uuidToLookFor);
		final ArrayList<MagicCard> all = HatchetHarrySession.get().getFirstCardsInHand();

		if (null != card)
		{
			PlayCardFromHandBehavior.LOGGER.info("Removed? " + all.remove(card));
			HatchetHarrySession.get().setFirstCardsInHand(all);

			PlayCardFromHandBehavior.LOGGER.info("card title: " + card.getTitle() + ", uuid: "
					+ card.getUuidObject() + ", filename: " + card.getBigImageFilename());

			if ("true".equals(stop))
			{
				PlayCardFromHandBehavior.LOGGER.info("stopping round-trips");

				final String id = "cardPlaceholdera"
						+ (HatchetHarrySession.get().getPlaceholderNumber() + 1);
				HatchetHarrySession.get().setPlaceholderNumber(
						HatchetHarrySession.get().getPlaceholderNumber() + 1);

				this.cp = new CardPanel(id, card.getSmallImageFilename(),
						card.getBigImageFilename(), card.getUuidObject());
				this.cp.setOutputMarkupId(true);
				HatchetHarrySession.get().addCardInBattleField(this.cp);

				this.thumbParent.addOrReplace(this.cp);
				target.add(this.thumbParent);

				target.appendJavaScript("jQuery(document).ready(function() { "
						+ "jQuery.gritter.add({ title : '" + request.getParameter("side")
						+ "', text : \"has played \'" + card.getTitle()
						+ "\'!\", image : 'image/logoh2.gif', sticky : false, time : ''}); });");

				final List<CardPanel> list = HatchetHarrySession.get().getAllCardsInBattleField();
				for (final CardPanel aCard : list)
				{
					try
					{
						final MagicCard mc = this.persistenceService.getCardFromUuid(aCard
								.getUuid());
						if (null != mc)
						{
							target.appendJavaScript("var card = jQuery(\"#menutoggleButton"
									+ aCard.getUuid() + "\"); "
									+ "card.css(\"position\", \"absolute\"); "
									+ "card.css(\"left\", \"" + mc.getX() + "px\");"
									+ "card.css(\"top\", \"" + mc.getY() + "px\");");

							if (mc.isTapped())
							{
								target.appendJavaScript("jQuery('#card" + mc.getUuid()
										+ "').rotate(90);");
							}
							else
							{
								target.appendJavaScript("jQuery('#card" + mc + "').rotate(0);");
							}

							target.appendJavaScript("jQuery(\"#card" + aCard.getUuid()
									+ "\").easyTooltip({" + "useElement: \"cardTooltip"
									+ aCard.getUuid() + "\"});");
						}
					}
					catch (final IllegalArgumentException e)
					{
						PlayCardFromHandBehavior.LOGGER
								.error("error parsing UUID of moved card", e);
					}
				}

				final List<CardPanel> toRemove = HatchetHarrySession.get().getAllCardsToRemove();
				if ((null != toRemove) && (toRemove.size() > 0))
				{
					for (final CardPanel c : toRemove)
					{
						target.appendJavaScript("jQuery('#" + c.getMarkupId() + "').remove();");
					}
				}

				target.appendJavaScript("jQuery(document).ready(function() { var card = jQuery(\"#menutoggleButton"
						+ this.cp.getUuid()
						+ "\"); "
						+ "card.css(\"position\", \"absolute\"); "
						+ "card.css(\"left\", \""
						+ posX
						+ "px\"); "
						+ "card.css(\"top\", \""
						+ posY + "px\"); });");
				card.setX(posX);
				card.setY(posY);
				this.persistenceService.saveCard(card);
			}
			else if ((null != this.uuidToLookFor) && (!"".equals(this.uuidToLookFor.toString())))
			{
				PlayCardFromHandBehavior.LOGGER.info("card: " + this.uuidToLookFor);

				HatchetHarrySession.get().setPlaceholderNumber(
						HatchetHarrySession.get().getPlaceholderNumber() + 1);

				PlayCardFromHandBehavior.LOGGER.info("continue!");

				final String message = jsessionid + "~~~" + this.uuidToLookFor + "~~~"
						+ (_indexOfClickedCard == 6 ? 0 : _indexOfClickedCard + 1) + "~~~"
						+ this.side + "~~~" + HatchetHarrySession.get().getMySidePosX() + "~~~"
						+ HatchetHarrySession.get().getMySidePosY();
				PlayCardFromHandBehavior.LOGGER.info(message);

				final HandComponent gallery = new HandComponent("gallery");
				gallery.setOutputMarkupId(true);

				this.cardParent.addOrReplace(gallery);
				target.add(this.cardParent);

				((HatchetHarryApplication)Application.get()).setPlayer(HatchetHarrySession.get()
						.getPlayer());
				target.appendJavaScript("jQuery(document).ready(function() { var theInt = null; var $crosslink, $navthumb; var curclicked = 0; theInterval = function(cur) { if (typeof cur != 'undefined') curclicked = cur; $crosslink.removeClass('active-thumb'); $navthumb.eq(curclicked).parent().addClass('active-thumb'); jQuery('.stripNav ul li a').eq(curclicked).trigger('click'); $crosslink.removeClass('active-thumb'); $navthumb.eq(curclicked).parent().addClass('active-thumb'); jQuery('.stripNav ul li a').eq(curclicked).trigger('click'); curclicked++; if (6 == curclicked) curclicked = 0; }; jQuery('#main-photo-slider').codaSlider(); $navthumb = jQuery('.nav-thumb'); $crosslink = jQuery('.cross-link'); $navthumb.click(function() { var $this = jQuery(this); theInterval($this.parent().attr('href').slice(1) - 1); return false; }); theInterval(); });");

				final Meteor meteor = Meteor
						.build(request, new LinkedList<BroadcastFilter>(), null);
				meteor.addListener((AtmosphereResourceEventListener)target.getPage());
				meteor.broadcast(message);
			}
		}
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

		response.renderJavaScript(template1.asString(), null);
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
