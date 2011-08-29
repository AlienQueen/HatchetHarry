package org.alienlabs.hatchetharry.view.component;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.alienlabs.hatchetharry.HatchetHarrySession;
import org.alienlabs.hatchetharry.model.MagicCard;
import org.alienlabs.hatchetharry.service.PersistenceService;
import org.alienlabs.hatchetharry.view.page.HomePage;
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
	private final UUID uuid;
	private final WebMarkupContainer parent;

	@SpringBean
	private PersistenceService persistenceService;
	private final int indexOfClickedCard;
	private final int indexOfNextCard;

	public PlayCardFromHandBehavior(final UUID _uuid, final WebMarkupContainer _parent,
			final int _indexOfClickedCard, final int _indexOfNextCard)
	{
		super();
		InjectorHolder.getInjector().inject(this);
		this.uuid = _uuid;
		this.parent = _parent;
		this.indexOfClickedCard = _indexOfClickedCard;
		this.indexOfNextCard = _indexOfNextCard;
	}

	@Override
	protected void respond(final AjaxRequestTarget target)
	{
		PlayCardFromHandBehavior.logger.info("respond");

		final ServletWebRequest servletWebRequest = (ServletWebRequest)target.getPage()
				.getRequest();
		final HttpServletRequest request = servletWebRequest.getHttpServletRequest();
		final String jsessionid = request.getRequestedSessionId();
		final String uuidToLookFor = request.getParameter("card");
		final String stop = request.getParameter("stop");

		int _indexOfClickedCard = 0;
		try
		{
			_indexOfClickedCard = Integer.parseInt(request.getParameter("indexOfClickedCard"));
		}
		catch (final NumberFormatException e)
		{
			PlayCardFromHandBehavior.logger.info("Error which should never happen!");
		}
		HatchetHarrySession.get().addCardIdInHand(_indexOfClickedCard, _indexOfClickedCard);

		final MagicCard card = this.persistenceService.getCardFromUuid(UUID
				.fromString(uuidToLookFor));

		if (("true".equals(stop)) && (null != uuidToLookFor))
		{
			PlayCardFromHandBehavior.logger.info("stopping round-trips");

			final String id = "cardPlaceholder" + HatchetHarrySession.get().getPlayerLetter()
					+ HatchetHarrySession.get().getPlaceholderNumber();
			HatchetHarrySession.get().setPlaceholderNumber(
					HatchetHarrySession.get().getPlaceholderNumber() + 1);

			final CardPanel cp = new CardPanel(id, card.getSmallImageFilename(),
					card.getBigImageFilename(), UUID.fromString(uuidToLookFor));
			cp.setOutputMarkupId(true);

			this.parent.addOrReplace(cp);
			target.addComponent(this.parent);
		}
		else if ((null != card) && (null != uuidToLookFor) && (!"undefined".equals(uuidToLookFor)))
		{
			PlayCardFromHandBehavior.logger.info("card: " + uuidToLookFor);

			final String id = "cardPlaceholder" + HatchetHarrySession.get().getPlayerLetter()
					+ HatchetHarrySession.get().getPlaceholderNumber();
			HatchetHarrySession.get().setPlaceholderNumber(
					HatchetHarrySession.get().getPlaceholderNumber() + 1);

			final CardPanel cp = new CardPanel(id, card.getSmallImageFilename(),
					card.getBigImageFilename(), UUID.fromString(uuidToLookFor));
			cp.setOutputMarkupId(true);

			PlayCardFromHandBehavior.logger.info("continue!");

			final String message = jsessionid + "~~~" + this.uuid.toString() + "~~~"
					+ (this.indexOfClickedCard == 6 ? 0 : this.indexOfClickedCard + 1);
			PlayCardFromHandBehavior.logger.info(message);

			final Meteor meteor = Meteor.build(request, new LinkedList<BroadcastFilter>(), null);
			meteor.addListener((AtmosphereResourceEventListener)target.getPage());
			meteor.broadcast(message);

			this.parent.addOrReplace(cp);
			target.addComponent(this.parent);
		}
	}

	@Override
	public void renderHead(final IHeaderResponse response)
	{
		super.renderHead(response);

		final HashMap<String, Object> variables = new HashMap<String, Object>();
		variables.put("url", this.getCallbackUrl());
		variables.put("uuid", this.uuid.toString());
		variables.put("uuidValidForJs", this.uuid.toString().replace("-", "_"));
		variables.put("next", this.indexOfNextCard);
		variables.put("clicked", this.indexOfClickedCard);

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
