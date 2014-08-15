package org.alienlabs.hatchetharry.view.component;

import java.io.IOException;
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

public class CardMoveBehavior extends AbstractDefaultAjaxBehavior {
	private static final long serialVersionUID = 1L;

	private static final Logger LOGGER = LoggerFactory.getLogger(CardMoveBehavior.class);
	private final CardPanel panel;

	private final UUID uuid;
	private String uuidValidForJs;

	private final PutToHandFromBattlefieldBehavior putToHandFromBattlefieldBehavior;
	private final PutToGraveyardFromBattlefieldBehavior putToGraveyardFromBattlefieldBehavior;
	private final PutToExileFromBattlefieldBehavior putToExileFromBattlefieldBehavior;
	private final DestroyTokenBehavior destroyTokenBehavior;

	@SpringBean
	private PersistenceService persistenceService;

	private long posX;
	private long posY;


	public CardMoveBehavior(final CardPanel cp, final UUID _uuid,
							final PutToGraveyardFromBattlefieldBehavior _putToGraveyardBehavior,
							final PutToHandFromBattlefieldBehavior _putToHandFromBattlefieldBehavior,
							final PutToExileFromBattlefieldBehavior _putToExileFromBattlefieldBehavior,
							final DestroyTokenBehavior _destroyTokenBehavior, final long _posX, final long _posY) {
		Injector.get().inject(this);

		this.panel = cp;
		this.uuid = _uuid;
		this.putToGraveyardFromBattlefieldBehavior = _putToGraveyardBehavior;
		this.putToHandFromBattlefieldBehavior = _putToHandFromBattlefieldBehavior;
		this.putToExileFromBattlefieldBehavior = _putToExileFromBattlefieldBehavior;
		this.destroyTokenBehavior = _destroyTokenBehavior;

		this.posX = _posX;
		this.posY = _posY;
	}

	@Override
	protected void respond(final AjaxRequestTarget target) {
		CardMoveBehavior.LOGGER.info("respond");
		final ServletWebRequest servletWebRequest = (ServletWebRequest) this.panel.getRequest();
		final HttpServletRequest request = servletWebRequest.getContainerRequest();

		final String _mouseX = request.getParameter("posX");
		final String _mouseY = request.getParameter("posY");
		final String uniqueid = this.uuid.toString();
		CardMoveBehavior.LOGGER.info("uuid: " + uniqueid);

		try {
			final String roundedX = _mouseX.substring(0,
															 (_mouseX.contains(".")) ? _mouseX.indexOf(".") : _mouseX.length());
			final String roundedY = _mouseY.substring(0,
															 (_mouseY.contains(".")) ? _mouseY.indexOf(".") : _mouseY.length());
			final long _x = Long.parseLong(roundedX);
			final long _y = Long.parseLong(roundedY);
			this.posX = _x <= -300 ? -300 : _x;
			this.posY = _y <= -150 ? -150 : _y;
		} catch (final NumberFormatException e) {
			CardMoveBehavior.LOGGER.error("error parsing coordinates of moved card", e);
			return;
		}

		final MagicCard mc;
		final Long gameId;

		try {
			mc = this.persistenceService.getCardFromUuid(UUID.fromString(uniqueid));
			if (null == mc) {
				return;
			}

			gameId = mc.getGameId();
			mc.setX(this.posX);
			mc.setY(this.posY);
			CardMoveBehavior.LOGGER.info("uuid: " + uniqueid + ", posX: " + this.posX + ", posY: "
												 + this.posY);
			this.persistenceService.updateCard(mc);
		} catch (final IllegalArgumentException e) {
			CardMoveBehavior.LOGGER.error("error parsing UUID of moved card", e);
			return;
		}

		final Long playerId = HatchetHarrySession.get().getPlayer().getId();

		CardMoveBehavior.LOGGER.info("playerId in respond(): "
											 + HatchetHarrySession.get().getPlayer().getId());

		final List<BigInteger> allPlayersInGame = CardMoveBehavior.this.persistenceService
														  .giveAllPlayersFromGame(gameId);

		for (int i = 0; i < allPlayersInGame.size(); i++) {
			final Long playerToWhomToSend = allPlayersInGame.get(i).longValue();
			final String pageUuid = HatchetHarryApplication.getCometResources().get(
																						   playerToWhomToSend);
			final CardMoveCometChannel cardMoveCometChannel = new CardMoveCometChannel(gameId, mc,
																							  Long.toString(this.posX), Long.toString(this.posY), uniqueid, playerId);

			// For unit tets
			try {
				HatchetHarryApplication.get().getEventBus().post(cardMoveCometChannel, pageUuid);
			} catch (final NullPointerException e) {
				// Nothing to do in unit tests
			}
		}
	}

	@Override
	public void renderHead(final Component component, final IHeaderResponse response) {
		super.renderHead(component, response);

		StringBuilder js = new StringBuilder();

		final HashMap<String, Object> variables = new HashMap<String, Object>();
		variables.put("url", this.getCallbackUrl());
		variables.put("uuidValidForJs", this.uuid.toString().replace("-", "_"));
		variables.put("handUrl", this.putToHandFromBattlefieldBehavior.getCallbackUrl());
		variables.put("graveyardUrl", this.putToGraveyardFromBattlefieldBehavior.getCallbackUrl());
		variables.put("exileUrl", this.putToExileFromBattlefieldBehavior.getCallbackUrl());
		variables.put("destroyUrl", this.destroyTokenBehavior.getCallbackUrl());
		variables.put("posX", this.posX);
		variables.put("posY", this.posY);

		// TODO in reality, cardMove.js configures the context menu: move it in
		// its own Behavior
		final TextTemplate template1 = new PackageTextTemplate(HomePage.class,
																	  "script/draggableHandle/cardMove.js");
		template1.interpolate(variables);
		js = js.append("\n" + template1.asString());

		final TextTemplate template2 = new PackageTextTemplate(HomePage.class,
																	  "script/draggableHandle/initDrag.js");
		template2.interpolate(variables);
		js = js.append("\n" + template2.asString());

		final TextTemplate template3 = new PackageTextTemplate(HomePage.class,
																	  "script/draggableHandle/dragCard.js");
		template3.interpolate(variables);
		js = js.append("\n" + template3.asString());

		response.render(JavaScriptHeaderItem.forScript(js.toString(), null));
		try {
			template1.close();
		} catch (final IOException e) {
			CardMoveBehavior.LOGGER.error(
												 "unable to close template1 in CardMoveBehavior#renderHead()!", e);
		}
		try {
			template2.close();
		} catch (final IOException e) {
			CardMoveBehavior.LOGGER.error(
												 "unable to close template2 in CardMoveBehavior#renderHead()!", e);
		}
		try {
			template3.close();
		} catch (final IOException e) {
			CardMoveBehavior.LOGGER.error(
												 "unable to close template3 in CardMoveBehavior#renderHead()!", e);
		}
	}

	@Required
	public void setPersistenceService(final PersistenceService _persistenceService) {
		this.persistenceService = _persistenceService;
	}

	public UUID getUuid() {
		return this.uuid;
	}

	public String getUuidValidForJs() {
		return this.uuidValidForJs;
	}

}
