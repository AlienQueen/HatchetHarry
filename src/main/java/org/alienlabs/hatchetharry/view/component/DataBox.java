package org.alienlabs.hatchetharry.view.component;

import java.util.List;

import org.alienlabs.hatchetharry.model.Player;
import org.alienlabs.hatchetharry.service.PersistenceService;
import org.alienlabs.hatchetharry.view.page.HomePage;
import org.alienlabs.hatchetharry.view.page.UpdateDataBoxPage;
import org.apache.wicket.ResourceReference;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.injection.web.InjectorHolder;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.spring.injection.annot.SpringBean;
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

	public DataBox(final String id, final long _gameId, final WebMarkupContainer _dataBoxParent,
			final HomePage _hp)
	{
		super(id);
		InjectorHolder.getInjector().inject(this);

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

				final Label playerLifePoints = new Label("playerLifePoints", Long.toString(player
						.getLifePoints()) + " life points");
				playerLifePoints.setOutputMarkupId(true);
				item.add(playerLifePoints);

				final AjaxLink<Void> plus = new AjaxLink<Void>("playerPlusLink")
				{
					private static final long serialVersionUID = -2987998457733835993L;

					@Override
					public void onClick(final AjaxRequestTarget target)
					{
						player.setLifePoints(player.getLifePoints() + 1);
						DataBox.this.persistenceService.saveOrUpdatePlayer(player);

						final UpdateDataBoxBehavior behavior = new UpdateDataBoxBehavior(
								_dataBoxParent, _gameId, _hp);
						_dataBoxParent.add(behavior);

						_dataBoxParent.addOrReplace(DataBox.this);
						target.addComponent(_dataBoxParent);

						target.appendJavascript("wicketAjaxGet('" + behavior.getUrl()
								+ "&jsessionid=" + this.getParent().getPage().getSession().getId()
								+ "', function() { }, null, null);");
					}
				};
				final Image playerPlus = new Image("playerPlus", new ResourceReference(
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
						player.setLifePoints(player.getLifePoints() - 1);
						DataBox.this.persistenceService.saveOrUpdatePlayer(player);

						final UpdateDataBoxBehavior behavior = new UpdateDataBoxBehavior(
								_dataBoxParent, _gameId, _hp);
						DataBox.this.add(behavior);

						_dataBoxParent.addOrReplace(DataBox.this);
						target.addComponent(_dataBoxParent);

						target.appendJavascript("wicketAjaxGet('" + behavior.getUrl()
								+ "&jsessionid=" + this.getParent().getPage().getSession().getId()
								+ "', function() { }, null, null);");
					}
				};
				final Image playerMinus = new Image("playerMinus", new ResourceReference(
						HomePage.class, "image/minusLife.png"));
				playerMinus.setOutputMarkupId(true);
				minus.add(playerMinus);
				item.add(minus);
			}
		};

		box.setOutputMarkupId(true);
		this.add(box);
	}

	@Required
	public void setPersistenceService(final PersistenceService _persistenceService)
	{
		this.persistenceService = _persistenceService;
	}

}
