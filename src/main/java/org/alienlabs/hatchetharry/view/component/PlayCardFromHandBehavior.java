package org.alienlabs.hatchetharry.view.component;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

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
		final MagicCard card = this.persistenceService.getCardFromUuid(this.getUuid());

		if (null != card)
		{
			PlayCardFromHandBehavior.logger.info("card!");
			final CardPanel cp = new CardPanel("cardPlaceholder", card.getSmallImageFilename(),
					card.getBigImageFilename(), this.getUuid());
			cp.setOutputMarkupId(true);
			this.parent.addOrReplace(cp);
			target.addComponent(this.parent);
		}
		else
		{
			PlayCardFromHandBehavior.logger.info("null!");
		}

		final String message = jsessionid + "~~~" + this.getUuid();
		PlayCardFromHandBehavior.logger.info(message);

		final String stop = request.getParameter("stop");
		if (!"true".equals(stop))
		{
			final Meteor meteor = Meteor.build(request, new LinkedList<BroadcastFilter>(), null);
			meteor.addListener((AtmosphereResourceEventListener)target.getPage());
			meteor.broadcast(message);
		}
	}

	@Override
	public void renderHead(final IHeaderResponse response)
	{
		super.renderHead(response);

		final HashMap<String, Object> variables = new HashMap<String, Object>();
		variables.put("url", this.getCallbackUrl());
		variables.put("uuid", this.getUuid());
		variables.put("uuidValidForJs", this.uuid.toString().replace("-", "_"));
		variables.put("next", this.indexOfNextCard);
		variables.put("clicked", this.indexOfClickedCard);

		final TextTemplate template = new PackagedTextTemplate(HomePage.class,
				"script/playCard/playCard.js");
		template.interpolate(variables);

		response.renderJavascript(template.asString(), null);
	}

	protected UUID getUuid()
	{
		return this.uuid;
	}

	@Required
	public void setPersistenceService(final PersistenceService _persistenceService)
	{
		this.persistenceService = _persistenceService;
	}
}
