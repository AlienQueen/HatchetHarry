package org.alienlabs.hatchetharry.view.component;

import java.math.BigInteger;
import java.util.List;

import org.alienlabs.hatchetharry.HatchetHarryApplication;
import org.alienlabs.hatchetharry.model.Player;
import org.alienlabs.hatchetharry.model.channel.UpdateDataBoxCometChannel;
import org.alienlabs.hatchetharry.service.PersistenceService;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.injection.Injector;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

/**
 * @author nostromo
 */
public class DataBox extends Panel
{
	private static final long serialVersionUID = -9102861929848438800L;
	static final Logger LOGGER = LoggerFactory.getLogger(DataBox.class);

	@SpringBean
	PersistenceService persistenceService;


	public DataBox(final String id, final long _gameId)
	{
		super(id);
		Injector.get().inject(this);
		this.setOutputMarkupId(true);

		final List<Player> players = this.persistenceService.getAllPlayersOfGame(_gameId);
		final ListView<Player> box = new ListView<Player>("box", players)
		{
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(final ListItem<Player> item)
			{
				final Player player = item.getModelObject();

				final Label playerLabel = new Label("playerLabel", player.getName() + ": ");
				playerLabel.setOutputMarkupId(true);
				item.add(playerLabel);

				final WebMarkupContainer playerLifePointsParent = new WebMarkupContainer(
						"playerLifePointsParent");
				playerLifePointsParent.setOutputMarkupId(true);
				playerLifePointsParent.setMarkupId("playerLifePointsParent" + player.getId());
				final Label playerLifePoints = new Label("playerLifePoints", Long.toString(player
						.getLifePoints()) + " life points");
				playerLifePoints.setOutputMarkupId(true);
				playerLifePointsParent.add(playerLifePoints);
				item.add(playerLifePointsParent);

				final AjaxLink<Void> plus = new AjaxLink<Void>("playerPlusLink")
				{
					private static final long serialVersionUID = -2987998457733835993L;

					@Override
					public void onClick(final AjaxRequestTarget target)
					{
						final Player playerToUpdate = DataBox.this.persistenceService
								.getPlayer(player.getId());
						playerToUpdate.setLifePoints(playerToUpdate.getLifePoints() + 1);
						DataBox.this.persistenceService.updatePlayer(playerToUpdate);

						final Long g = playerToUpdate.getGame().getId();
						final List<BigInteger> allPlayersInGame = DataBox.this.persistenceService
								.giveAllPlayersFromGame(g);
						final UpdateDataBoxCometChannel udbcc = new UpdateDataBoxCometChannel(g);

						// post the DataBox update message to all players in the
						// game
						for (int i = 0; i < allPlayersInGame.size(); i++)
						{
							final Long p = allPlayersInGame.get(i).longValue();
							final String pageUuid = HatchetHarryApplication.getCometResources()
									.get(p);
							PlayCardFromHandBehavior.LOGGER.info("pageUuid: " + pageUuid);
							HatchetHarryApplication.get().getEventBus().post(udbcc, pageUuid);
						}
					}
				};

				final ExternalImage playerPlus = new ExternalImage("playerPlus",
						"image/plusLife.png");
				playerPlus.setOutputMarkupId(true);
				plus.add(playerPlus);
				item.add(plus);

				final AjaxLink<Void> minus = new AjaxLink<Void>("playerMinusLink")
				{
					private static final long serialVersionUID = -2987999764313835993L;

					@Override
					public void onClick(final AjaxRequestTarget target)
					{
						final Player playerToUpdate = DataBox.this.persistenceService
								.getPlayer(player.getId());
						playerToUpdate.setLifePoints(playerToUpdate.getLifePoints() - 1);
						DataBox.this.persistenceService.updatePlayer(playerToUpdate);

						final Long g = playerToUpdate.getGame().getId();
						final List<BigInteger> allPlayersInGame = DataBox.this.persistenceService
								.giveAllPlayersFromGame(g);
						final UpdateDataBoxCometChannel udbcc = new UpdateDataBoxCometChannel(g);

						// post the DataBox update message to all players in the
						// game
						for (int i = 0; i < allPlayersInGame.size(); i++)
						{
							final Long p = allPlayersInGame.get(i).longValue();
							final String pageUuid = HatchetHarryApplication.getCometResources()
									.get(p);
							PlayCardFromHandBehavior.LOGGER.info("pageUuid: " + pageUuid);
							HatchetHarryApplication.get().getEventBus().post(udbcc, pageUuid);
						}
					}
				};

				final ExternalImage playerMinus = new ExternalImage("playerMinus",
						"image/minusLife.png");
				playerMinus.setOutputMarkupId(true);
				minus.add(playerMinus);
				item.add(minus);
			}
		};
		box.setOutputMarkupId(true);

		final WebMarkupContainer parent = new WebMarkupContainer("parent");
		parent.setOutputMarkupId(true);
		parent.add(box);
		this.add(parent);
	}

	@Required
	public void setPersistenceService(final PersistenceService _persistenceService)
	{
		this.persistenceService = _persistenceService;
	}

}
