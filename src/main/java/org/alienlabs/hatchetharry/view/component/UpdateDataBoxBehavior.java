package org.alienlabs.hatchetharry.view.component;

import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

import org.alienlabs.hatchetharry.HatchetHarrySession;
import org.alienlabs.hatchetharry.model.Player;
import org.alienlabs.hatchetharry.service.PersistenceService;
import org.alienlabs.hatchetharry.view.page.HomePage;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.injection.Injector;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.protocol.http.servlet.ServletWebRequest;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.template.PackageTextTemplate;
import org.apache.wicket.util.template.TextTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

public class UpdateDataBoxBehavior extends AbstractDefaultAjaxBehavior
{
	private static final long serialVersionUID = -1192901847359306923L;

	static final Logger LOGGER = LoggerFactory.getLogger(UpdateDataBoxBehavior.class);

	final Long gameId;

	private final HomePage hp;

	private final DataBox parent;
	@SpringBean
	private PersistenceService persistenceService;

	public UpdateDataBoxBehavior(final Long _gameId, final HomePage _hp, final DataBox _parent)
	{
		Injector.get().inject(this);
		this.gameId = _gameId;
		this.hp = _hp;
		this.parent = _parent;
	}

	@Override
	protected void respond(final AjaxRequestTarget target)
	{
		UpdateDataBoxBehavior.LOGGER.info("respond");
		final ServletWebRequest servletWebRequest = (ServletWebRequest)target.getPage()
				.getRequest();
		final HttpServletRequest request = servletWebRequest.getContainerRequest();
		final String jsessionid = request.getParameter("jsessionid");
		final String displayJoinMessage = request.getParameter("displayJoinMessage");
		Long playerId;
		try
		{
			playerId = Long.parseLong(request.getParameter("playerId"));
		}
		catch (final NumberFormatException e)
		{
			playerId = 0l;
		}

		final HatchetHarrySession session = HatchetHarrySession.get();

		if (this.hp.getSession().getId().equals(jsessionid) && ("true".equals(displayJoinMessage)))
		{
			UpdateDataBoxBehavior.LOGGER.info("notify with jsessionid="
					+ this.hp.getSession().getId());

			target.appendJavaScript("Wicket.Ajax.get('" + this.hp.notifierPanel.getCallbackUrl()
					+ "&title=A player joined in!&text=Ready to play?&jsessionid=" + jsessionid
					+ "', function() { }, null, null);");
		}

		if (playerId != 0)
		{
			final WebMarkupContainer playerLifePointsParent = this.parent
					.retrievePlayerLifePointsParentForPlayer(playerId);
			final Player player = this.persistenceService.getPlayer(playerId);
			playerLifePointsParent.addOrReplace(new Label("playerLifePoints", Long.toString(player
					.getLifePoints()) + " life points"));
			target.add(playerLifePointsParent);
			UpdateDataBoxBehavior.LOGGER.info("just updating the life points");
		}

		UpdateDataBoxBehavior.LOGGER.info("UpdateDataBoxBehavior with gameId="
				+ session.getGameId());
	}

	@Override
	public void renderHead(final Component component, final IHeaderResponse response)
	{
		super.renderHead(component, response);

		final HashMap<String, Object> variables = new HashMap<String, Object>();
		variables.put("url", this.getCallbackUrl());
		variables.put("jsessionid", this.getComponent().getPage().getSession().getId());

		final TextTemplate template = new PackageTextTemplate(HomePage.class,
				"script/databox/updateDataBox.js");
		template.interpolate(variables);

		response.render(JavaScriptHeaderItem.forScript(template.asString(), "updateDataBox"));
	}

	public String getUrl()
	{
		return this.getCallbackUrl().toString();
	}

	@Required
	public void setPersistenceService(final PersistenceService _persistenceService)
	{
		this.persistenceService = _persistenceService;
	}

}
