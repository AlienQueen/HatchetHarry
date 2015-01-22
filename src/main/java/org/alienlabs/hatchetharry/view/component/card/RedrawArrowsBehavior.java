package org.alienlabs.hatchetharry.view.component.card;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import org.alienlabs.hatchetharry.HatchetHarrySession;
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

public class RedrawArrowsBehavior extends AbstractDefaultAjaxBehavior
{
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = LoggerFactory.getLogger(RedrawArrowsBehavior.class);
	private final Long gameId;
	@SpringBean
	private PersistenceService persistenceService;

	public RedrawArrowsBehavior(final Long _gameId)
	{
		this.gameId = _gameId;
		Injector.get().inject(this);
	}

	@Override
	protected void respond(final AjaxRequestTarget target)
	{
		// TODO have this work again
	}

	@Override
	public void renderHead(final Component component, final IHeaderResponse response)
	{
		super.renderHead(component, response);

		final HashMap<String, Object> variables = new HashMap<>();
		variables.put("arrowDrawUrl", this.getCallbackUrl());

		final List<Arrow> allArrows = this.persistenceService.loadAllArrowsForAGame(this.gameId);
		final StringBuilder content = new StringBuilder(
				"var redraw = function() { arrow = new Array(); ");

		final Boolean drawMode = this.persistenceService.getGame(
				HatchetHarrySession.get().getGameId()).isDrawMode();
		content.append("drawMode = " + drawMode.booleanValue() + "; ");
		RedrawArrowsBehavior.LOGGER.info("drawMode: " + drawMode.booleanValue());

		if (drawMode.booleanValue())
		{
			content.append("jQuery('._jsPlumb_connector').remove(); jQuery('._jsPlumb_overlay').remove(); jQuery('._jsPlumb_endpoint').remove(); ");

			for (final Arrow arrow : allArrows)
			{
				content.append("var e0 = jsPlumb.addEndpoint(");
				content.append("jQuery('#" + arrow.getSource() + "').parent().parent().parent() ");
				content.append(" ); ");
				content.append("var e1 = jsPlumb.addEndpoint(");
				content.append("jQuery('#" + arrow.getTarget() + "').parent().parent().parent() ");
				content.append("); ");
				content.append(" arrows.push({ 'source' : ");
				content.append("jQuery('#" + arrow.getSource() + "').parent().parent().parent() ");
				content.append(", 'target' : ");
				content.append("jQuery('#" + arrow.getTarget() + "').parent().parent().parent() ");
				content.append(" }); ");
				content.append("	jsPlumb.connect({ source:e0, target:e1, connector:['Bezier', { curviness:70 }], overlays : [ ");
				content.append("					['Label', {location:0.7, id:'label', events:{ ");
				content.append("							} }]] }); ");
			}
		}
		content.append("}; ");
		content.append("window.setTimeout(redraw, 500); ");
		variables.put("content", content.toString());

		final TextTemplate template = new PackageTextTemplate(HomePage.class,
				"script/arrowDraw/redrawArrows.js");
		template.interpolate(variables);

		response.render(JavaScriptHeaderItem.forScript(template.asString(), null));
		try
		{
			template.close();
		}
		catch (final IOException e)
		{
			RedrawArrowsBehavior.LOGGER.error(
					"unable to close template in RedrawArrowsBehavior#renderHead()!", e);
		}
	}

	@Required
	public void setPersistenceService(final PersistenceService _persistenceService)
	{
		this.persistenceService = _persistenceService;
	}

}
