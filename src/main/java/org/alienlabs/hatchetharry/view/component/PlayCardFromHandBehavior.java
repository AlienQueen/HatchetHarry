package org.alienlabs.hatchetharry.view.component;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.alienlabs.hatchetharry.HatchetHarryApplication;
import org.alienlabs.hatchetharry.HatchetHarrySession;
import org.alienlabs.hatchetharry.model.MagicCard;
import org.alienlabs.hatchetharry.model.channel.PlayCardFromHandCometChannel;
import org.alienlabs.hatchetharry.service.PersistenceService;
import org.alienlabs.hatchetharry.view.page.HomePage;
import org.apache.wicket.Application;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
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

	private UUID uuidToLookFor;
	private final int currentCard;

	private CardPanel cp;
	private String side;

	public PlayCardFromHandBehavior(final WebMarkupContainer _cardParent,
			final UUID _uuidToLookFor, final int _currentCard, final String _side)
	{
		super();
		Injector.get().inject(this);
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

		PlayCardFromHandBehavior.LOGGER.info("URL: " + request.getQueryString());

		this.uuidToLookFor = UUID.fromString(request.getParameter("card"));
		PlayCardFromHandBehavior.LOGGER.info("uuidToLookFor: " + this.uuidToLookFor);

		HatchetHarrySession.get().setPlaceholderNumber(
				HatchetHarrySession.get().getPlaceholderNumber() + 1);

		final MagicCard card = this.persistenceService.getCardFromUuid(this.uuidToLookFor);

		PlayCardFromHandBehavior.LOGGER.info("Before remove: "
				+ HatchetHarrySession.get().getFirstCardsInHand().size());

		HatchetHarrySession.get().removeCardInHand(card);

		PlayCardFromHandBehavior.LOGGER.info("After remove: "
				+ HatchetHarrySession.get().getFirstCardsInHand().size());

		final HandComponent gallery = new HandComponent("gallery");
		gallery.setOutputMarkupId(true);

		this.cardParent.addOrReplace(gallery);
		target.add(this.cardParent);

		card.setX(100l + (HatchetHarrySession.get().getPlaceholderNumber() * 60));
		card.setY(100l + (HatchetHarrySession.get().getPlaceholderNumber() * 60));
		this.persistenceService.saveCard(card);

		final Integer id = (HatchetHarrySession.get().getPlaceholderNumber() + 1);
		HatchetHarrySession.get().setPlaceholderNumber(
				HatchetHarrySession.get().getPlaceholderNumber() + 1);

		this.cp = new CardPanel("cardPlaceholdera" + id, card.getSmallImageFilename(),
				card.getBigImageFilename(), card.getUuidObject());
		this.cp.setOutputMarkupId(true);
		HatchetHarrySession.get().addCardInBattleField(this.cp);
		PlayCardFromHandBehavior.LOGGER.info("uuid created " + card.getUuidObject().toString());

		final WebMarkupContainer parent = (WebMarkupContainer)target.getPage().get(
				"cardParent:playCardParentPlaceholdera"
						+ HatchetHarrySession.get().getPlaceholderNumber());
		parent.addOrReplace(this.cp);
		target.add(parent);

		// this.thumbParent.addOrReplace(this.cp);
		// target.add(this.thumbParent);

		final StringBuffer buf = new StringBuffer("var card = jQuery(\"#menutoggleButton"
				+ card.getUuid() + "\"); " + "card.css(\"position\", \"absolute\"); "
				+ "card.css(\"left\", \"" + card.getX() + "px\");" + "card.css(\"top\", \""
				+ card.getY() + "px\"); ");


		((HatchetHarryApplication)Application.get()).setPlayer(HatchetHarrySession.get()
				.getPlayer());
		buf.append("jQuery(document).ready(function() { var theInt = null; var $crosslink, $navthumb; var curclicked = 0; theInterval = function(cur) { if (typeof cur != 'undefined') curclicked = cur; $crosslink.removeClass('active-thumb'); $navthumb.eq(curclicked).parent().addClass('active-thumb'); jQuery('.stripNav ul li a').eq(curclicked).trigger('click'); $crosslink.removeClass('active-thumb'); $navthumb.eq(curclicked).parent().addClass('active-thumb'); jQuery('.stripNav ul li a').eq(curclicked).trigger('click'); curclicked++; if (6 == curclicked) curclicked = 0; }; jQuery('#main-photo-slider').codaSlider(); $navthumb = jQuery('.nav-thumb'); $crosslink = jQuery('.cross-link'); $navthumb.click(function() { var $this = jQuery(this); theInterval($this.parent().attr('href').slice(1) - 1); return false; }); theInterval(); });");
		target.appendJavaScript(buf.toString());

		final PlayCardFromHandCometChannel pcfhcc = new PlayCardFromHandCometChannel(
				this.uuidToLookFor, HatchetHarrySession.get().getPlayer().getName(), id);
		HatchetHarryApplication.get().getEventBus().post(pcfhcc);
	}

	@Subscribe
	// (filter = PlayCardFromHandFilter.class)
	public void playCardFromHand(final AjaxRequestTarget target,
			final PlayCardFromHandCometChannel event)
	{
		PlayCardFromHandBehavior.LOGGER.info("### card: " + event.getUuidToLookFor());

		final Integer id = event.getCardWicketId();
		HatchetHarrySession.get().setPlaceholderNumber(
				HatchetHarrySession.get().getPlaceholderNumber() + 1);

		final MagicCard card = this.persistenceService.getCardFromUuid(event.getUuidToLookFor());

		this.cp = new CardPanel("cardPlaceholdera" + id, card.getSmallImageFilename(),
				card.getBigImageFilename(), card.getUuidObject());
		this.cp.setOutputMarkupId(true);
		HatchetHarrySession.get().addCardInBattleField(this.cp);

		final WebMarkupContainer parent = (WebMarkupContainer)target.getPage().get(
				"cardParent:playCardParentPlaceholdera" + event.getCardWicketId());
		parent.addOrReplace(this.cp);
		target.add(parent);

		// this.thumbParent.addOrReplace(this.cp);
		// target.add(this.thumbParent);

		final StringBuffer buf = new StringBuffer();

		final String toId = HatchetHarrySession.get().getId();
		buf.append("var toId = \"" + toId + "\"; ");

		// TODO notify in another channel
		buf.append("jQuery.gritter.add({ title : '" + event.getPlayerName()
				+ "', text : \"has played \'" + card.getTitle()
				+ "\'!\", image : 'image/logoh2.gif', sticky : false, time : ''}); ");

		((HatchetHarryApplication)Application.get()).setPlayer(HatchetHarrySession.get()
				.getPlayer());

		buf.append("window.setTimeout(function() { ");

		final List<CardPanel> list = HatchetHarrySession.get().getAllCardsInBattleField();
		for (final CardPanel aCard : list)
		{
			try
			{
				final MagicCard mc = this.persistenceService.getCardFromUuid(aCard.getUuid());
				if (null != mc)
				{
					buf.append("var card = jQuery(\"#menutoggleButton" + aCard.getUuid() + "\"); "
							+ "card.css(\"position\", \"absolute\"); " + "card.css(\"left\", \""
							+ mc.getX() + "px\");" + "card.css(\"top\", \"" + mc.getY() + "px\"); ");
					PlayCardFromHandBehavior.LOGGER.info("moving card UUID=" + aCard.getUuid()
							+ " to posX=" + mc.getX() + ", posY=" + mc.getY());

					if (mc.isTapped())
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
			}
			catch (final IllegalArgumentException e)
			{
				PlayCardFromHandBehavior.LOGGER.error("error parsing UUID of moved card", e);
			}
		}

		buf.append(" }, 2000);");
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
