package org.alienlabs.hatchetharry.view.component.gui;

import org.alienlabs.hatchetharry.HatchetHarrySession;
import org.alienlabs.hatchetharry.model.MagicCard;
import org.alienlabs.hatchetharry.model.channel.ReorderCardCometChannel;
import org.alienlabs.hatchetharry.service.PersistenceService;
import org.alienlabs.hatchetharry.view.clientsideutil.EventBusPostService;
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

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class ReorderCardInBattlefieldBehavior extends AbstractDefaultAjaxBehavior
{
	private static final long serialVersionUID = 1L;

	private static final Logger LOGGER = LoggerFactory
			.getLogger(ReorderCardInBattlefieldBehavior.class);

	@SpringBean
	private PersistenceService persistenceService;

	public ReorderCardInBattlefieldBehavior()
	{
		Injector.get().inject(this);
	}

	@Override
	protected void respond(final AjaxRequestTarget target)
	{
		ReorderCardInBattlefieldBehavior.LOGGER.info("respond");

		final ServletWebRequest servletWebRequest = (ServletWebRequest)target.getPage()
				.getRequest();
		final HttpServletRequest request = servletWebRequest.getContainerRequest();

		final String uuidAsString = request.getParameter("uuid");
		ReorderCardInBattlefieldBehavior.LOGGER.info("uuid: " + uuidAsString);

		final String newIndexAsString = request.getParameter("index");
		ReorderCardInBattlefieldBehavior.LOGGER.info("newIndex: " + newIndexAsString);

		final UUID uuid = UUID.fromString(uuidAsString);
		MagicCard card = this.persistenceService.getCardFromUuid(uuid);
		final HatchetHarrySession session = HatchetHarrySession.get();
		final List<MagicCard> allCardsInBattlefieldForPlayer = this.persistenceService
				.getAllCardsAndTokensInBattlefieldForAGameAndAPlayer(session.getGameId(), session
						.getPlayer().getId(), session.getPlayer().getDeck().getDeckId());
		LOGGER.info("allCardsInBattlefieldForPlayer.size(): "
				+ allCardsInBattlefieldForPlayer.size());

		final Integer newIndex = Integer.parseInt(newIndexAsString) > allCardsInBattlefieldForPlayer
				.size() - 1 ? Integer.valueOf(allCardsInBattlefieldForPlayer.size() - 1) : Integer
				.valueOf(Integer.parseInt(newIndexAsString));
		final Integer oldIndex = card.getBattlefieldOrder().intValue() > allCardsInBattlefieldForPlayer
				.size() - 1 ? Integer.valueOf(allCardsInBattlefieldForPlayer.size() - 1) : card
				.getBattlefieldOrder();


		int startIndex, endIndex;
		Collections.sort(allCardsInBattlefieldForPlayer);

		if (newIndex.intValue() < oldIndex.intValue())
		{
			startIndex = newIndex.intValue() < 0 ? 0 : newIndex.intValue();
			endIndex = oldIndex.intValue();

			card = allCardsInBattlefieldForPlayer.get(oldIndex.intValue());

			for (int index = endIndex - 1; index >= startIndex; index--)
			{
				final MagicCard mc = allCardsInBattlefieldForPlayer.get(index);
				mc.setBattlefieldOrder(Integer.valueOf(index + 1));
			}
			card.setBattlefieldOrder(newIndex);
		}
		else if (oldIndex.intValue() < newIndex.intValue())
		{
			startIndex = oldIndex.intValue() < 0 ? 0 : oldIndex.intValue();
			endIndex = newIndex.intValue();

			card = allCardsInBattlefieldForPlayer.remove(startIndex);

			for (int index = startIndex; index < endIndex; index++)
			{
				final MagicCard mc = allCardsInBattlefieldForPlayer.get(index);
				mc.setBattlefieldOrder(Integer.valueOf(index));
			}
			card.setBattlefieldOrder(newIndex);
			allCardsInBattlefieldForPlayer.add(newIndex.intValue(), card);
		}

		Collections.sort(allCardsInBattlefieldForPlayer);

		for (int i = 0; i < allCardsInBattlefieldForPlayer.size(); i++)
		{
			ReorderCardInBattlefieldBehavior.LOGGER.info("index: " + i + ", card: "
					+ allCardsInBattlefieldForPlayer.get(i).getTitle() + ", order: "
					+ allCardsInBattlefieldForPlayer.get(i).getBattlefieldOrder());
		}
		this.persistenceService.updateAllMagicCards(allCardsInBattlefieldForPlayer);

		final List<BigInteger> giveAllPlayersFromGame = this.persistenceService
				.giveAllPlayersFromGame(session.getGameId());
		final ReorderCardCometChannel reorder = new ReorderCardCometChannel(session.getGameId(),
				session.getPlayer().getId(), session.getPlayer().getDeck().getDeckId(), session
						.getPlayer().getSide().getSideName());
		EventBusPostService.post(giveAllPlayersFromGame, reorder);
	}

	@Override
	public void renderHead(final Component component, final IHeaderResponse response)
	{
		super.renderHead(component, response);

		final HashMap<String, Object> variables = new HashMap<>();
		variables.put("url", this.getCallbackUrl());

		final TextTemplate template = new PackageTextTemplate(HomePage.class,
				"script/playCard/reorderCard.js");
		template.interpolate(variables);

		response.render(JavaScriptHeaderItem.forScript(template.asString(), null));
		try
		{
			template.close();
		}
		catch (final IOException e)
		{
			ReorderCardInBattlefieldBehavior.LOGGER
					.error("unable to close template in ReorderCardInBattlefieldBehavior#renderHead()!",
							e);
		}
	}

	@Required
	public void setPersistenceService(final PersistenceService _persistenceService)
	{
		this.persistenceService = _persistenceService;
	}

}
