package org.alienlabs.hatchetharry.view.component;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.alienlabs.hatchetharry.model.Player;
import org.alienlabs.hatchetharry.service.PersistenceService;
import org.alienlabs.hatchetharry.view.page.HomePage;
import org.alienlabs.hatchetharry.view.page.UpdateDataBoxPage;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.injection.Injector;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.protocol.http.servlet.ServletWebRequest;
import org.apache.wicket.request.resource.PackageResourceReference;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.atmosphere.cpr.BroadcastFilter;
import org.atmosphere.cpr.Meteor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

/**
 * @author nostromo
 */
public class DataBox extends Panel
{
	private static final long serialVersionUID = -9102861929848438800L;

	@SpringBean
	PersistenceService persistenceService;

	private final BookmarkablePageLink<UpdateDataBoxPage> updateDataBox;
	final HomePage hp;

	static final Logger LOGGER = LoggerFactory.getLogger(DataBox.class);

	static final Map<Long, WebMarkupContainer> allPlayerLifePointsParents = new HashMap<Long, WebMarkupContainer>();

	public DataBox(final String id, final long _gameId, final HomePage _hp)
	{
		super(id);
		Injector.get().inject(this);
		this.hp = _hp;
		this.setOutputMarkupId(true);

		this.updateDataBox = new BookmarkablePageLink<UpdateDataBoxPage>("updateDataBox",
				UpdateDataBoxPage.class);
		this.add(this.updateDataBox);

		final List<Player> players = this.persistenceService.getAllPlayersOfGame(_gameId);

		final ListView<Player> box = new ListView<Player>("box", players)
		{
			private static final long serialVersionUID = -9108376429848438800L;

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

				DataBox.allPlayerLifePointsParents.put(player.getId(), playerLifePointsParent);

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

						DataBox.this.writeUpdateDataBoxCometMessage(playerToUpdate.getId());
					}
				};
				final Image playerPlus = new Image("playerPlus", new PackageResourceReference(
						HomePage.class, "image/plusLife.png"));
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

						DataBox.this.writeUpdateDataBoxCometMessage(player.getId());
					}
				};
				final Image playerMinus = new Image("playerMinus", new PackageResourceReference(
						HomePage.class, "image/minusLife.png"));
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

	void writeUpdateDataBoxCometMessage(final Long playerId)
	{
		final ServletWebRequest servletWebRequest = (ServletWebRequest)this.hp.getRequest();
		final HttpServletRequest request = servletWebRequest.getContainerRequest();

		final String message = "%%%" + request.getRequestedSessionId() + "%%%" + playerId;
		final Meteor meteor = Meteor.build(request, new LinkedList<BroadcastFilter>(), null);
		DataBox.LOGGER.info("meteor: " + meteor);
		DataBox.LOGGER.info(message);

		meteor.addListener(this.hp);
		meteor.broadcast(message);
	}

	public WebMarkupContainer retrievePlayerLifePointsParentForPlayer(final Long playerId)
	{
		return DataBox.allPlayerLifePointsParents.get(playerId);
	}
}
