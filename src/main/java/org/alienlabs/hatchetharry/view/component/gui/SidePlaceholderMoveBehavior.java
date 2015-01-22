package org.alienlabs.hatchetharry.view.component.gui;

import java.io.IOException;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.alienlabs.hatchetharry.HatchetHarrySession;
import org.alienlabs.hatchetharry.model.Player;
import org.alienlabs.hatchetharry.model.Side;
import org.alienlabs.hatchetharry.model.channel.MoveSideCometChannel;
import org.alienlabs.hatchetharry.service.PersistenceService;
import org.alienlabs.hatchetharry.view.clientsideutil.EventBusPostService;
import org.alienlabs.hatchetharry.view.page.HomePage;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.injection.Injector;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.protocol.http.servlet.ServletWebRequest;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.template.PackageTextTemplate;
import org.apache.wicket.util.template.TextTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

public class SidePlaceholderMoveBehavior extends AbstractDefaultAjaxBehavior
{
	private static final long serialVersionUID = 1L;

	private static final Logger LOGGER = LoggerFactory.getLogger(SidePlaceholderMoveBehavior.class);

	private final SidePlaceholderPanel panel;
	private final WebMarkupContainer parent;
	private final UUID uuid;
	private final Player player;
	@SpringBean
	private PersistenceService persistenceService;

	public SidePlaceholderMoveBehavior(final SidePlaceholderPanel _panel,
			final WebMarkupContainer _parent, final UUID _uuid, final Player _player)
	{
		super();
		Injector.get().inject(this);

		this.panel = _panel;
		this.parent = _parent;
		this.uuid = _uuid;
		this.player = _player;
	}

	@Override
	protected void respond(final AjaxRequestTarget target)
	{
		SidePlaceholderMoveBehavior.LOGGER.info("## respond");
		final ServletWebRequest servletWebRequest = (ServletWebRequest)this.parent.getRequest();
		final HttpServletRequest request = servletWebRequest.getContainerRequest();

		final String _sideX = request.getParameter("posX");
		final String _sideY = request.getParameter("posY");
		int sideX;
		final int sideY;

		try
		{
			sideX = Math.round(Float.parseFloat(_sideX));
			sideY = Math.round(Float.parseFloat(_sideY));
		}
		catch (final Exception e)
		{
			SidePlaceholderMoveBehavior.LOGGER.info("could not parse position " + _sideX + " or "
					+ _sideY);
			return;
		}

		HatchetHarrySession.get().setMySidePlaceholder(this.panel);

		final Side side = this.player.getSide();
		side.setX(Long.valueOf(sideX));
		side.setY(Long.valueOf(sideY));
		this.persistenceService.updateSide(side);

		final List<BigInteger> allPlayersInGame = SidePlaceholderMoveBehavior.this.persistenceService
				.giveAllPlayersFromGame(HatchetHarrySession.get().getGameId());
		final MoveSideCometChannel mscc = new MoveSideCometChannel(this.uuid,
				Integer.toString(sideX), Integer.toString(sideY));
		EventBusPostService.post(allPlayersInGame, mscc);
		SidePlaceholderMoveBehavior.LOGGER.info("done");
	}

	@Override
	public void renderHead(final Component component, final IHeaderResponse response)
	{
		super.renderHead(component, response);

		StringBuilder js = new StringBuilder();

		final HashMap<String, Object> variables = new HashMap<>();
		variables.put("dragUrl", this.getCallbackUrl());
		variables.put("uuidValidForJs", this.uuid.toString().replace("-", "_"));

		final TextTemplate template = new PackageTextTemplate(HomePage.class,
				"script/draggableHandle/initSidePlaceholderDrag.js");
		template.interpolate(variables);
		js = js.append(template.asString());

		response.render(JavaScriptHeaderItem.forScript(js.toString(), null));
		try
		{
			template.close();
		}
		catch (final IOException e)
		{
			SidePlaceholderMoveBehavior.LOGGER.error(
					"unable to close template1 in SidePlaceholderMoveBehavior#renderHead()!", e);
		}
	}

	@Required
	public void setPersistenceService(final PersistenceService _persistenceService)
	{
		this.persistenceService = _persistenceService;
	}

}
