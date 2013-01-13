package org.alienlabs.hatchetharry.view.component;

import java.util.HashMap;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.alienlabs.hatchetharry.HatchetHarrySession;
import org.alienlabs.hatchetharry.model.MagicCard;
import org.alienlabs.hatchetharry.model.channel.CardRotateCometChannel;
import org.alienlabs.hatchetharry.service.PersistenceService;
import org.alienlabs.hatchetharry.view.page.HomePage;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.atmosphere.EventBus;
import org.apache.wicket.atmosphere.Subscribe;
import org.apache.wicket.injection.Injector;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.protocol.http.servlet.ServletWebRequest;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.template.PackageTextTemplate;
import org.apache.wicket.util.template.TextTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

public class CardRotateBehavior extends AbstractDefaultAjaxBehavior
{
	private static final long serialVersionUID = -9164073767944851883L;

	private static final Logger LOGGER = LoggerFactory.getLogger(CardRotateBehavior.class);
	private final CardPanel panel;
	private final UUID uuid;

	@SpringBean
	private PersistenceService persistenceService;

	public CardRotateBehavior(final CardPanel cp, final UUID _uuid)
	{
		this.panel = cp;
		this.uuid = _uuid;
		Injector.get().inject(this);
	}

	@Override
	protected void respond(final AjaxRequestTarget target)
	{
		CardRotateBehavior.LOGGER.info("respond");
		final ServletWebRequest servletWebRequest = (ServletWebRequest)this.panel.getRequest();
		final HttpServletRequest request = servletWebRequest.getContainerRequest();

		final String uuidToLookFor = request.getParameter("uuid");

		final MagicCard card = this.persistenceService.getCardFromUuid(UUID
				.fromString(uuidToLookFor));
		card.setTapped(!card.isTapped());
		this.persistenceService.saveCard(card);
		CardRotateBehavior.LOGGER.info("respond, gameId= " + HatchetHarrySession.get().getGameId());

		final CardRotateCometChannel crcc = new CardRotateCometChannel(HatchetHarrySession.get()
				.getGameId(), card.getUuid(), card.isTapped());
		EventBus.get().post(crcc);
	}

	@Subscribe
	public void rotateCard(final AjaxRequestTarget target, final CardRotateCometChannel event)
	{
		CardRotateBehavior.LOGGER.info("gameId from event= " + event.getGameId());
		CardRotateBehavior.LOGGER.info("gameId from session= "
				+ HatchetHarrySession.get().getGameId());

		final StringBuffer buf = new StringBuffer();

		final String toId = HatchetHarrySession.get().getId();
		buf.append("var toId = \"" + toId + "\"; ");

		if (event.isTapped())
		{
			buf.append("window.setTimeout(function() { jQuery('#card" + event.getCardUuid()
					+ "').rotate(90); window.setTimeout(function() {");
			buf.append("jQuery('#card" + event.getCardUuid()
					+ "').rotate(0); window.setTimeout(function() {");
			buf.append("jQuery('#card" + event.getCardUuid()
					+ "').rotate(90); }, 250); }, 250); }, 250);");
		}
		else
		{
			buf.append("window.setTimeout(function() {jQuery('#card" + event.getCardUuid()
					+ "').rotate(0); window.setTimeout(function() {");
			buf.append("jQuery('#card" + event.getCardUuid()
					+ "').rotate(90); window.setTimeout(function() {");
			buf.append("jQuery('#card" + event.getCardUuid()
					+ "').rotate(0); }, 250); }, 250); }, 250);");
		}

		target.appendJavaScript(buf.toString());
	}

	@Override
	public void renderHead(final Component component, final IHeaderResponse response)
	{
		super.renderHead(component, response);

		final HashMap<String, Object> variables = new HashMap<String, Object>();
		variables.put("url", this.getCallbackUrl());
		variables.put("uuid", this.uuid);

		final TextTemplate template = new PackageTextTemplate(HomePage.class,
				"script/rotate/cardRotate.js");
		template.interpolate(variables);

		response.render(JavaScriptHeaderItem.forScript(template.asString(), null));
	}

	@Required
	public void setPersistenceService(final PersistenceService _persistenceService)
	{
		this.persistenceService = _persistenceService;
	}

}
