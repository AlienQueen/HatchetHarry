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
import org.alienlabs.hatchetharry.model.channel.CardRotateCometChannel;
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
		final MagicCard card;
		try
		{
			card = this.persistenceService.getCardFromUuid(UUID.fromString(uuidToLookFor));
		}
		catch (final NullPointerException e)
		{
			CardRotateBehavior.LOGGER.error("Error parsing UUID " + uuidToLookFor
					+ " in CardRotateBehavior", e);
			return;
		}

		final HomePage homePage = (HomePage)target.getPage();
		final List<MagicCard> allCards = homePage.getAllCardsInBattlefield().getModelObject();
		final int index = allCards.indexOf(card);
		allCards.remove(card);

		card.setTapped(!card.isTapped());
		this.persistenceService.updateCard(card);
		allCards.add(index, card);

		CardRotateBehavior.LOGGER.info("respond, gameId= " + HatchetHarrySession.get().getGameId());

		final Long gameId = card.getGameId();
		final List<BigInteger> allPlayersInGame = CardRotateBehavior.this.persistenceService
				.giveAllPlayersFromGame(gameId);

		for (int i = 0; i < allPlayersInGame.size(); i++)
		{
			final Long playerToWhomToSend = allPlayersInGame.get(i).longValue();
			final String pageUuid = HatchetHarryApplication.getCometResources().get(
					playerToWhomToSend);
			final CardRotateCometChannel crcc = new CardRotateCometChannel(gameId, card,
					card.getUuid(), card.isTapped());
			HatchetHarryApplication.get().getEventBus().post(crcc, pageUuid);
		}
	}

	@Override
	public void renderHead(final Component component, final IHeaderResponse response)
	{
		super.renderHead(component, response);

		final HashMap<String, Object> variables = new HashMap<String, Object>();
		variables.put("url", this.getCallbackUrl());
		variables.put("uuidValidForJs", this.uuid.toString().replace("-", "_"));

		final TextTemplate template = new PackageTextTemplate(HomePage.class,
				"script/rotate/cardRotate.js");
		template.interpolate(variables);

		response.render(JavaScriptHeaderItem.forScript(template.asString(), null));
		try
		{
			template.close();
		}
		catch (final IOException e)
		{
			CardRotateBehavior.LOGGER.error(
					"unable to close template in CardRotateBehavior#renderHead()!", e);
		}
	}

	@Required
	public void setPersistenceService(final PersistenceService _persistenceService)
	{
		this.persistenceService = _persistenceService;
	}

}
