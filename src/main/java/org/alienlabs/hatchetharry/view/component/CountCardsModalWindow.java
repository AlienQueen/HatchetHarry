package org.alienlabs.hatchetharry.view.component;

import org.alienlabs.hatchetharry.model.CardZone;
import org.alienlabs.hatchetharry.model.Player;
import org.alienlabs.hatchetharry.service.PersistenceService;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import java.util.List;

public class CountCardsModalWindow extends Panel {
	private static final long serialVersionUID = 1L;

	static final Logger LOGGER = LoggerFactory.getLogger(CountCardsModalWindow.class);

	@SpringBean
	PersistenceService persistenceService;

	public CountCardsModalWindow(final String id, final Long gameId) {
		super(id);

		final List<Player> allPlayersOfGame = this.persistenceService.getAllPlayersOfGame(gameId);
		final ListView<Player> list = new ListView<Player>("players", allPlayersOfGame) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(final ListItem<Player> item) {
				final Player p = item.getModelObject();
				item.add(new Label("playerName", p.getName()));
				item.add(new Label("hand", CountCardsModalWindow.this.persistenceService
												   .getNumberOfCardsInACertainZoneForAGameAndADeck(CardZone.HAND, gameId, p
																																  .getDeck().getDeckId())));
				item.add(new Label("library", CountCardsModalWindow.this.persistenceService
													  .getNumberOfCardsInACertainZoneForAGameAndADeck(CardZone.LIBRARY, gameId, p
																																		.getDeck().getDeckId())));
				item.add(new Label("graveyard", CountCardsModalWindow.this.persistenceService
														.getNumberOfCardsInACertainZoneForAGameAndADeck(CardZone.GRAVEYARD, gameId,
																											   p.getDeck().getDeckId())));
				item.add(new Label("exile", CountCardsModalWindow.this.persistenceService
													.getNumberOfCardsInACertainZoneForAGameAndADeck(CardZone.EXILE, gameId, p
																																	.getDeck().getDeckId())));
				item.add(new Label("battlefield", CountCardsModalWindow.this.persistenceService
														  .getNumberOfCardsInACertainZoneForAGameAndADeck(CardZone.BATTLEFIELD,
																												 gameId, p.getDeck().getDeckId())));
				item.add(new Label("total", p.getDeck().getCards().size()));
			}
		};
		this.add(list);
	}

	@Required
	public void setPersistenceService(final PersistenceService _persistenceService) {
		this.persistenceService = _persistenceService;
	}

}