package org.alienlabs.hatchetharry.view.component;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.alienlabs.hatchetharry.HatchetHarryApplication;
import org.alienlabs.hatchetharry.HatchetHarrySession;
import org.alienlabs.hatchetharry.model.MagicCard;
import org.alienlabs.hatchetharry.model.channel.CardMoveCometChannel;
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

public class CardMoveBehavior extends AbstractDefaultAjaxBehavior
{
	private static final long serialVersionUID = 1L;

	private static final Logger LOGGER = LoggerFactory.getLogger(CardMoveBehavior.class);
	private final CardPanel panel;
	private final UUID uuid;

	@SpringBean
	private PersistenceService persistenceService;

	public CardMoveBehavior(final CardPanel cp, final UUID _uuid)
	{
		this.panel = cp;
		this.uuid = _uuid;
		Injector.get().inject(this);
	}

	@Override
	protected void respond(final AjaxRequestTarget target)
	{
		CardMoveBehavior.LOGGER.info("respond");
		final ServletWebRequest servletWebRequest = (ServletWebRequest)this.panel.getRequest();
		final HttpServletRequest request = servletWebRequest.getContainerRequest();

		final String _mouseX = request.getParameter("posX");
		final String _mouseY = request.getParameter("posY");
		final String uniqueid = this.uuid.toString();
		CardMoveBehavior.LOGGER.info("uuid: " + uniqueid);

		final Long gameId = HatchetHarrySession.get().getGameId();
		final Long playerId = HatchetHarrySession.get().getPlayer().getId();

		try
		{
			final MagicCard mc = this.persistenceService.getCardFromUuid(UUID.fromString(uniqueid));
			if (null != mc)
			{
				mc.setX(Long.parseLong(_mouseX));
				mc.setY(Long.parseLong(_mouseY));
				this.persistenceService.saveCard(mc);
			}
		}
		catch (final IllegalArgumentException e)
		{
			CardMoveBehavior.LOGGER.error("error parsing UUID of moved card", e);
		}

		CardMoveBehavior.LOGGER.info("playerId in respond(): "
				+ HatchetHarrySession.get().getPlayer().getId());

		final List<BigInteger> allPlayersInGame = CardMoveBehavior.this.persistenceService
				.giveAllPlayersFromGame(gameId);

		for (int i = 0; i < allPlayersInGame.size(); i++)
		{
			final Long playerToWhomToSend = allPlayersInGame.get(i).longValue();
			final String pageUuid = HatchetHarryApplication.getCometResources().get(
					playerToWhomToSend);
			final CardMoveCometChannel cardMoveCometChannel = new CardMoveCometChannel(gameId,
					_mouseX, _mouseY, uniqueid, playerId);

			EventBus.get().post(cardMoveCometChannel, pageUuid);
		}
	}

	@Subscribe
	public void moveCard(final AjaxRequestTarget target, final CardMoveCometChannel event)
	{
		CardMoveBehavior.LOGGER.info("playerId in moveCard(): "
				+ HatchetHarrySession.get().getPlayer().getId());
		CardMoveBehavior.LOGGER.info("event playerId in moveCard(): " + event.getPlayerId());

		CardMoveBehavior.LOGGER.info("gameId in moveCard(): "
				+ HatchetHarrySession.get().getGameId());
		CardMoveBehavior.LOGGER.info("event gameId in moveCard(): " + event.getGameId());
		CardMoveBehavior.LOGGER
				.info("### "
						+ ((HatchetHarrySession.get().getGameId().longValue() == event.getGameId()
								.longValue()) && (HatchetHarrySession.get().getPlayer().getId()
								.longValue() != event.getPlayerId().longValue())));
		CardMoveBehavior.LOGGER.info("event.getUniqueid(): " + event.getUniqueid());

		// if ((HatchetHarrySession.get().getGameId().longValue() ==
		// event.getGameId().longValue())
		// && (HatchetHarrySession.get().getPlayer().getId().longValue() !=
		// event
		// .getPlayerId().longValue()))
		// {
		target.appendJavaScript("var card = jQuery('#menutoggleButton" + event.getUniqueid()
				+ "');" + "card.css('position', 'absolute');" + "card.css('left', '"
				+ event.getMouseX() + "');" + "card.css('top', '" + event.getMouseY() + "');");
		// }
	}

	@Override
	public void renderHead(final Component component, final IHeaderResponse response)
	{
		super.renderHead(component, response);

		final TextTemplate template1 = new PackageTextTemplate(HomePage.class,
				"script/draggableHandle/jquery.ui.core.js");
		StringBuffer js = new StringBuffer().append(template1.asString());

		final TextTemplate template2 = new PackageTextTemplate(HomePage.class,
				"script/draggableHandle/jquery.ui.widget.js");
		js = js.append("\n" + template2.asString());

		final TextTemplate template3 = new PackageTextTemplate(HomePage.class,
				"script/draggableHandle/jquery.ui.mouse.js");
		js = js.append("\n" + template3.asString());

		final TextTemplate template7 = new PackageTextTemplate(HomePage.class,
				"script/draggableHandle/jquery.ui.touch-punch.js");
		js = js.append("\n" + template7.asString());

		final HashMap<String, Object> variables = new HashMap<String, Object>();
		variables.put("url", this.getCallbackUrl());
		variables.put("uuid", this.uuid);
		variables.put("uuidValidForJs", this.uuid.toString().replace("-", "_"));

		final TextTemplate template4 = new PackageTextTemplate(HomePage.class,
				"script/draggableHandle/jquery.ui.draggable.js");
		template4.interpolate(variables);
		js = js.append("\n" + template4.asString());

		final TextTemplate template5 = new PackageTextTemplate(HomePage.class,
				"script/draggableHandle/cardMove.js");
		template5.interpolate(variables);
		js = js.append("\n" + template5.asString());

		final TextTemplate template6 = new PackageTextTemplate(HomePage.class,
				"script/draggableHandle/initDrag.js");
		template6.interpolate(variables);
		js = js.append("\n" + template6.asString());

		response.render(JavaScriptHeaderItem.forScript(js.toString(), null));
	}

	@Required
	public void setPersistenceService(final PersistenceService _persistenceService)
	{
		this.persistenceService = _persistenceService;
	}

}
