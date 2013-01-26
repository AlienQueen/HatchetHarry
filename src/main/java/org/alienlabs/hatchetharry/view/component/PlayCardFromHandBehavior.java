package org.alienlabs.hatchetharry.view.component;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.alienlabs.hatchetharry.HatchetHarryApplication;
import org.alienlabs.hatchetharry.HatchetHarrySession;
import org.alienlabs.hatchetharry.model.CardZone;
import org.alienlabs.hatchetharry.model.MagicCard;
import org.alienlabs.hatchetharry.model.channel.NotifierAction;
import org.alienlabs.hatchetharry.model.channel.NotifierCometChannel;
import org.alienlabs.hatchetharry.model.channel.PlayCardFromHandCometChannel;
import org.alienlabs.hatchetharry.service.PersistenceService;
import org.alienlabs.hatchetharry.view.clientsideutil.JavaScriptUtils;
import org.alienlabs.hatchetharry.view.page.HomePage;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.atmosphere.EventBus;
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

public class PlayCardFromHandBehavior extends AbstractDefaultAjaxBehavior
{
	private static final long serialVersionUID = 1L;

	static final Logger LOGGER = LoggerFactory.getLogger(PlayCardFromHandBehavior.class);

	@SpringBean
	private PersistenceService persistenceService;

	private UUID uuidToLookFor;
	private final int currentCard;

	private String side;

	public PlayCardFromHandBehavior(final UUID _uuidToLookFor, final int _currentCard,
			final String _side)
	{
		super();
		Injector.get().inject(this);
		this.uuidToLookFor = _uuidToLookFor;
		this.currentCard = _currentCard;
		this.side = _side;
	}

	@Override
	protected void respond(final AjaxRequestTarget target)
	{
		final ServletWebRequest servletWebRequest = (ServletWebRequest)target.getPage()
				.getRequest();
		final HttpServletRequest request = servletWebRequest.getContainerRequest();

		this.uuidToLookFor = UUID.fromString(request.getParameter("card"));
		final MagicCard card = this.persistenceService.getCardFromUuid(this.uuidToLookFor);

		final Long gameId = HatchetHarrySession.get().getPlayer().getGame().getId();

		if (!CardZone.HAND.equals(card.getZone()))
		{
			return;
		}

		card.setZone(CardZone.BATTLEFIELD);

		card.setX(50l + (card.getId() * 2));
		card.setY(50l + (card.getId() * 2));
		this.persistenceService.saveCard(card);

		JavaScriptUtils.updateHand(target);

		final PlayCardFromHandCometChannel pcfhcc = new PlayCardFromHandCometChannel(
				this.uuidToLookFor, HatchetHarrySession.get().getPlayer().getName(), gameId);

		final NotifierCometChannel ncc = new NotifierCometChannel(
				NotifierAction.PLAY_CARD_FROM_HAND_ACTION, gameId, HatchetHarrySession.get()
						.getPlayer().getId(), HatchetHarrySession.get().getPlayer().getName(), "",
				"", card.getTitle(), null);

		final List<BigInteger> allPlayersInGame = this.persistenceService
				.giveAllPlayersFromGame(gameId);

		// post a message for all players in the game
		for (int i = 0; i < allPlayersInGame.size(); i++)
		{
			final Long player = allPlayersInGame.get(i).longValue();
			final String pageUuid = HatchetHarryApplication.getCometResources().get(player);
			PlayCardFromHandBehavior.LOGGER.info("pageUuid: " + pageUuid);
			EventBus.get().post(pcfhcc, pageUuid);
			EventBus.get().post(ncc, pageUuid);
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
