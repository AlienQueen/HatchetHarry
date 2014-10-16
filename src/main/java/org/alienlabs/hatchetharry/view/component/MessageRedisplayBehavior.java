package org.alienlabs.hatchetharry.view.component;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import org.alienlabs.hatchetharry.model.ChatMessage;
import org.alienlabs.hatchetharry.model.ConsoleLogMessage;
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

public class MessageRedisplayBehavior extends AbstractDefaultAjaxBehavior
{
	private static final long serialVersionUID = 1L;

	private static final Logger LOGGER = LoggerFactory.getLogger(MessageRedisplayBehavior.class);
	private final Long gameId;
	@SpringBean
	private PersistenceService persistenceService;

	public MessageRedisplayBehavior(final Long _gameId)
	{
		this.gameId = _gameId;
		Injector.get().inject(this);
	}

	@Override
	protected void respond(final AjaxRequestTarget target)
	{
	}

	@Override
	public void renderHead(final Component component, final IHeaderResponse response)
	{
		super.renderHead(component, response);

		final HashMap<String, Object> variables = new HashMap<String, Object>();
		final StringBuilder buil = new StringBuilder();

		final List<ConsoleLogMessage> allConsoleLogMessages = this.persistenceService
				.loadAllConsoleLogMessagesForAGame(this.gameId);
		for (final ConsoleLogMessage msg : allConsoleLogMessages)
		{
			buil.append("var consolePanel = document.getElementById('console'); consolePanel.innerHTML = consolePanel.innerHTML + \"&#013;&#010;\" + \"");
			buil.append(msg.getMessage());
			buil.append("\" + \"&#013;&#010;\"; ");
		}

		final List<ChatMessage> allChatMessages = this.persistenceService
				.loadAllChatMessagesForAGame(this.gameId);
		for (final ChatMessage msg : allChatMessages)
		{
			buil.append("var chatPanel = document.getElementById('chat'); chatPanel.innerHTML = chatPanel.innerHTML + \"&#013;&#010;\" + \"");
			buil.append(msg.getMessage());
			buil.append("\" + \"&#013;&#010;\"; ");
		}

		variables.put("content", buil.toString());

		final TextTemplate template = new PackageTextTemplate(HomePage.class,
				"script/messageRedisplay.js");
		template.interpolate(variables);

		response.render(JavaScriptHeaderItem.forScript(template.asString(), null));
		try
		{
			template.close();
		}
		catch (final IOException e)
		{
			MessageRedisplayBehavior.LOGGER.error(
					"unable to close template in MessageRedisplayBehavior#renderHead()!", e);
		}
	}

	@Required
	public void setPersistenceService(final PersistenceService _persistenceService)
	{
		this.persistenceService = _persistenceService;
	}

}
