package org.alienlabs.hatchetharry.view;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.alienlabs.hatchetharry.HatchetHarrySession;
import org.alienlabs.hatchetharry.model.MagicCard;
import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.protocol.http.servlet.ServletWebRequest;
import org.apache.wicket.util.template.PackagedTextTemplate;
import org.apache.wicket.util.template.TextTemplate;
import org.atmosphere.cpr.AtmosphereResourceEventListener;
import org.atmosphere.cpr.BroadcastFilter;
import org.atmosphere.cpr.Meteor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("serial")
public class PlayCardFromHandBehavior extends AbstractDefaultAjaxBehavior
{
	static final Logger logger = LoggerFactory.getLogger(PlayCardFromHandBehavior.class);
	private final UUID uuid;
	private final WebMarkupContainer parent;

	public PlayCardFromHandBehavior(final UUID _uuid, final WebMarkupContainer _parent)
	{
		super();
		this.uuid = _uuid;
		this.parent = _parent;
	}

	@Override
	protected void respond(final AjaxRequestTarget target)
	{
		PlayCardFromHandBehavior.logger.info("respond");
		final ServletWebRequest servletWebRequest = (ServletWebRequest)target.getPage()
				.getRequest();
		final HttpServletRequest request = servletWebRequest.getHttpServletRequest();

		final UUID uuidToLookFor = UUID.fromString(request.getParameter("card"));


		final MagicCard card = (MagicCard)HatchetHarrySession.get().getAllCards()
				.get(uuidToLookFor);
		PlayCardFromHandBehavior.logger.info("size: "
				+ HatchetHarrySession.get().getAllCards().size());
		if (null != card)
		{
			PlayCardFromHandBehavior.logger.info("new card");
			final CardPanel cp = new CardPanel("cardPlaceholder", card.getSmallImageFilename(),
					card.getBigImageFilename(), uuidToLookFor);
			cp.setOutputMarkupId(true);
			this.parent.addOrReplace(cp);
			target.addComponent(this.parent);
		}
		else
		{
			PlayCardFromHandBehavior.logger.info("null!");
		}

		final String message = "PlayCardFromHand";

		final Meteor meteor = Meteor.build(request, new LinkedList<BroadcastFilter>(), null);
		PlayCardFromHandBehavior.logger.info(message);
		meteor.addListener((AtmosphereResourceEventListener)target.getPage());
		meteor.broadcast(message);

	}

	@Override
	public void renderHead(final IHeaderResponse response)
	{
		super.renderHead(response);

		final HashMap<String, Object> variables = new HashMap<String, Object>();
		variables.put("url", this.getCallbackUrl());
		variables.put("uuid", this.getUuid());
		final TextTemplate template = new PackagedTextTemplate(PlayCardFromHandBehavior.class,
				"scripts/playCard/playCard.js");
		template.interpolate(variables);

		response.renderOnDomReadyJavascript(template.asString());
	}

	protected UUID getUuid()
	{
		return this.uuid;
	}

}
