package org.alienlabs.hatchetharry.view.component.gui;

import org.alienlabs.hatchetharry.model.Game;
import org.alienlabs.hatchetharry.model.Player;
import org.alienlabs.hatchetharry.service.PersistenceService;
import org.alienlabs.hatchetharry.view.page.HomePage;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.template.PackageTextTemplate;
import org.apache.wicket.util.template.TextTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import java.io.IOException;
import java.util.HashMap;

public class DrawModeBehavior extends AbstractDefaultAjaxBehavior
{
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = LoggerFactory.getLogger(DrawModeBehavior.class);
	@SpringBean private PersistenceService persistenceService;

	private final Player player;

	public DrawModeBehavior(final Player _player)
	{
		this.player = _player;
	}

	@Override public void renderHead(final Component component, final IHeaderResponse response)
	{
		super.renderHead(component, response);

		StringBuilder js = new StringBuilder();
		final HashMap<String, Object> variables = new HashMap<>();

		// TODO: aGame can be null
		final Game aGame = this.player.getGame();
		final Game game = this.persistenceService.getGame(aGame.getId());
		final Boolean drawMode = game == null ?
				Boolean.FALSE :
				(game.isDrawMode() == null ? Boolean.FALSE : game.isDrawMode());
		variables.put("drawMode", drawMode);

		final TextTemplate template1 = new PackageTextTemplate(HomePage.class,
				"script/draggableHandle/drawMode.js");
		template1.interpolate(variables);
		js = js.append("\n" + template1.asString());

		response.render(JavaScriptHeaderItem.forScript(js.toString(), null));
		try
		{
			template1.close();
		}
		catch (final IOException e)
		{
			DrawModeBehavior.LOGGER
					.error("unable to close template1 in CardTooltipBehavior#renderHead()!", e);
		}
	}

	@Override protected void respond(final AjaxRequestTarget target)
	{
		// The Ajax behavior is in HomePage.java, we just use need the callback URL
	}

	@Required public void setPersistenceService(final PersistenceService _persistenceService)
	{
		this.persistenceService = _persistenceService;
	}

}
