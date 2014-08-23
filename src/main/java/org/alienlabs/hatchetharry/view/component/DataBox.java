package org.alienlabs.hatchetharry.view.component;

import java.math.BigInteger;
import java.util.List;

import org.alienlabs.hatchetharry.model.Player;
import org.alienlabs.hatchetharry.model.channel.ConsoleLogCometChannel;
import org.alienlabs.hatchetharry.model.channel.UpdateDataBoxCometChannel;
import org.alienlabs.hatchetharry.model.channel.consolelog.AbstractConsoleLogStrategy;
import org.alienlabs.hatchetharry.model.channel.consolelog.ConsoleLogStrategy;
import org.alienlabs.hatchetharry.model.channel.consolelog.ConsoleLogType;
import org.alienlabs.hatchetharry.service.PersistenceService;
import org.alienlabs.hatchetharry.view.clientsideutil.EventBusPostService;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.extensions.ajax.markup.html.AjaxEditableLabel;
import org.apache.wicket.injection.Injector;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

/**
 * @author nostromo
 */
@edu.umd.cs.findbugs.annotations.SuppressWarnings(value = "SE_INNER_CLASS", justification = "In Wicket, serializable inner classes are common. And as the parent Page is serialized as well, this is no concern. This is no bad practice in Wicket")
public class DataBox extends Panel
{
	static final Logger LOGGER = LoggerFactory.getLogger(DataBox.class);
	private static final long serialVersionUID = -9102861929848438800L;
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
				final Model<String> lifePoints = Model.of(Long.toString(player.getLifePoints()));
				final AjaxEditableLabel<String> playerLifePoints = new AjaxEditableLabel<String>(
					"playerLifePoints", lifePoints)
				{
					private static final long serialVersionUID = 1L;

					@Override
					@edu.umd.cs.findbugs.annotations.SuppressWarnings(value = "PRMC_POSSIBLY_REDUNDANT_METHOD_CALLS", justification = "There's no other way round")
					protected void onSubmit(final AjaxRequestTarget target)
					{
						super.onSubmit(target);
						DataBox.LOGGER.info(this.getDefaultModelObject().toString());

						player.setLifePoints(Long
							.parseLong(this.getDefaultModelObject().toString()));
						DataBox.this.persistenceService.updatePlayer(player);

						final List<BigInteger> allPlayersInGame = DataBox.this.persistenceService
							.giveAllPlayersFromGame(_gameId);
						final UpdateDataBoxCometChannel udbcc = new UpdateDataBoxCometChannel(
							_gameId);
						final ConsoleLogStrategy logger = AbstractConsoleLogStrategy
							.chooseStrategy(ConsoleLogType.LIFE_POINTS, null, null, null, null,
								player.getName(), null,
								Long.parseLong(this.getDefaultModelObject().toString()), null,
								true, _gameId);

						// post the DataBox update message to all players in the
						// game
						EventBusPostService.post(allPlayersInGame, udbcc,
							new ConsoleLogCometChannel(logger));
					}

				};
				playerLifePoints.setOutputMarkupId(true);
				playerLifePointsParent.add(playerLifePoints);
				item.add(playerLifePointsParent);

				final AjaxLink<Player> plus = new AjaxLink<Player>("playerPlusLink",
					Model.of(player))
				{
					private static final long serialVersionUID = 1L;

					@Override
					public void onClick(final AjaxRequestTarget target)
					{
						final Player playerToUpdate = DataBox.this.persistenceService
							.getPlayer(this.getModelObject().getId());
						playerToUpdate.setLifePoints(playerToUpdate.getLifePoints() + 1);
						DataBox.this.persistenceService.updatePlayer(playerToUpdate);

						final Long g = playerToUpdate.getGame().getId();
						final List<BigInteger> allPlayersInGame = DataBox.this.persistenceService
							.giveAllPlayersFromGame(g);
						final UpdateDataBoxCometChannel udbcc = new UpdateDataBoxCometChannel(g);

						final ConsoleLogStrategy logger = AbstractConsoleLogStrategy
							.chooseStrategy(ConsoleLogType.LIFE_POINTS, null, null, null, null,
								playerToUpdate.getName(), null, playerToUpdate.getLifePoints(),
								null, true, g);

						// post the DataBox update message to all players in the
						// game
						EventBusPostService.post(allPlayersInGame, udbcc,
							new ConsoleLogCometChannel(logger));
					}
				};

				final ExternalImage playerPlus = new ExternalImage("playerPlus",
					"image/plusLife.png");
				playerPlus.setOutputMarkupId(true);
				plus.add(playerPlus);
				item.add(plus);

				final AjaxLink<Player> minus = new AjaxLink<Player>("playerMinusLink",
					Model.of(player))
				{
					private static final long serialVersionUID = 1L;

					@Override
					public void onClick(final AjaxRequestTarget target)
					{
						final Player playerToUpdate = DataBox.this.persistenceService
							.getPlayer(this.getModelObject().getId());
						playerToUpdate.setLifePoints(playerToUpdate.getLifePoints() - 1);
						DataBox.this.persistenceService.updatePlayer(playerToUpdate);

						final Long g = playerToUpdate.getGame().getId();
						final List<BigInteger> allPlayersInGame = DataBox.this.persistenceService
							.giveAllPlayersFromGame(g);
						final UpdateDataBoxCometChannel udbcc = new UpdateDataBoxCometChannel(g);

						final ConsoleLogStrategy logger = AbstractConsoleLogStrategy
							.chooseStrategy(ConsoleLogType.LIFE_POINTS, null, null, null, null,
								playerToUpdate.getName(), null, playerToUpdate.getLifePoints(),
								null, true, g);

						// post the DataBox update message to all players in the
						// game
						EventBusPostService.post(allPlayersInGame, udbcc,
							new ConsoleLogCometChannel(logger));
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
