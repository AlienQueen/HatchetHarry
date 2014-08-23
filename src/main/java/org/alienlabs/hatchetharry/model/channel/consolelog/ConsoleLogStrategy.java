package org.alienlabs.hatchetharry.model.channel.consolelog;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.alienlabs.hatchetharry.model.ConsoleLogMessage;
import org.alienlabs.hatchetharry.service.PersistenceService;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.injection.Injector;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

public abstract class ConsoleLogStrategy
{
	private static final Logger LOGGER = LoggerFactory.getLogger(ConsoleLogStrategy.class);

	protected final Date date = new Date();
	@SpringBean
	PersistenceService persistenceService;

	public ConsoleLogStrategy()
	{
		Injector.get().inject(this);
	}

	public abstract void logToConsole(AjaxRequestTarget target);

	@edu.umd.cs.findbugs.annotations.SuppressWarnings(value = "DE_MIGHT_IGNORE", justification = "We ignore the exception since it's a duplicate key from DB due to the fact that the same console log is persisted for each player in the game. But we only want it once in DB.")
	protected void logMessage(final AjaxRequestTarget target, final String message,
		final Boolean clearConsole, final Long gameId)
	{
		if ((null != clearConsole) && clearConsole)
		{
			target
				.appendJavaScript("var consolePanel = document.getElementById('console'); consolePanel.innerHTML = ''; ");
			this.persistenceService.deleteAllMessagesForAGame(gameId);
			return;
		}

		final String newDate = new SimpleDateFormat("HH:mm:ss").format(this.date);
		final ConsoleLogMessage msg = new ConsoleLogMessage();
		msg.setMessage(newDate + ": " + message);
		msg.setGameId(gameId);

		try
		{
			if ((msg.getGameId() != null) && (msg.getMessage() != null))
			{
				this.persistenceService.saveMessageWithoutDuplicate(msg);
			}
		}
		catch (final Exception e)
		{
			// Expected
		}

		target
			.appendJavaScript("var consolePanel = document.getElementById('console'); consolePanel.innerHTML = consolePanel.innerHTML + \"&#013;&#010;\" + \""
				+ newDate
				+ ": "
				+ message
				+ "\" + \"&#013;&#010;\"; consolePanel.scrollTop = consolePanel.scrollHeight; document.activeElement.blur(); ");

		ConsoleLogStrategy.LOGGER.info(newDate + ": " + message);
	}

	@Required
	public void setPersistenceService(final PersistenceService _persistenceService)
	{
		this.persistenceService = _persistenceService;
	}

}
