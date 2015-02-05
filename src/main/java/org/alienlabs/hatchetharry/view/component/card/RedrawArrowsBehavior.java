package org.alienlabs.hatchetharry.view.component.card;

import org.alienlabs.hatchetharry.model.Arrow;
import org.alienlabs.hatchetharry.service.PersistenceService;
import org.alienlabs.hatchetharry.view.page.HomePage;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.injection.Injector;
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
import java.util.List;

public class RedrawArrowsBehavior extends AbstractDefaultAjaxBehavior
{
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = LoggerFactory.getLogger(RedrawArrowsBehavior.class);
	private final Long gameId;
	@SpringBean private PersistenceService persistenceService;

	public RedrawArrowsBehavior(final Long _gameId)
	{
		this.gameId = _gameId;
		Injector.get().inject(this);
	}

	@Override protected void respond(final AjaxRequestTarget target)
	{
		// No need to respond to Ajax here
	}

	@Override public void renderHead(final Component component, final IHeaderResponse response)
	{
		super.renderHead(component, response);

		final HashMap<String, Object> variables = new HashMap<>();
		final List<Arrow> allArrows = this.persistenceService.loadAllArrowsForAGame(this.gameId);
		final StringBuilder content = new StringBuilder(
				"var redraw = function() { jQuery('._jsPlumb_connector').remove(); ");

		for (final Arrow arrow : allArrows)
		{
			content.append("jsPlumb.connect({ source: jQuery('#" + arrow.getSource()
					+ "').parent().parent().parent(), target: jQuery('#" + arrow.getTarget()
					+ "').parent().parent().parent(), connector:['Bezier', { curviness:70 }], overlays : [ ");
			content.append("					['Label', {location:0.7, id:'label', events:{ ");
			content.append("							} }]] }); ");
		}

		content.append("}; ");
		content.append("window.setTimeout(redraw, 1250); ");
		variables.put("content", content.toString());

		final TextTemplate template = new PackageTextTemplate(HomePage.class,
				"script/arrowDraw/redrawArrows.js");
		template.interpolate(variables);

		response.render(JavaScriptHeaderItem.forScript(template.asString(), "redrawArrows"));
		try
		{
			template.close();
		}
		catch (final IOException e)
		{
			RedrawArrowsBehavior.LOGGER
					.error("unable to close template in RedrawArrowsBehavior#renderHead()!", e);
		}
	}

	@Required public void setPersistenceService(final PersistenceService _persistenceService)
	{
		this.persistenceService = _persistenceService;
	}

}
