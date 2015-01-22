package org.alienlabs.hatchetharry.model.consolelogstrategy;

import java.io.Serializable;
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

@edu.umd.cs.findbugs.annotations.SuppressFBWarnings(value = "DE_MIGHT_IGNORE", justification = "We ignore the exception since it's a duplicate key from DB due to the fact that the same console log is persisted for each player in the game. But we only want it once in DB.")
@SuppressWarnings("PMD.EmptyCatchBlock")
public abstract class ConsoleLogStrategy implements Serializable
{
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = LoggerFactory.getLogger(ConsoleLogStrategy.class);

	private final Date date = new Date();
	private String message;

	@SpringBean
	private transient PersistenceService persistenceService;

	ConsoleLogStrategy()
	{
		Injector.get().inject(this);
	}

	public abstract void logToConsole(AjaxRequestTarget target);

	void logMessage(final AjaxRequestTarget target, final String _message,
			final Boolean clearConsole, final Long gameId, final String... cardNames)
	{
		if ((null != clearConsole) && clearConsole.booleanValue())
		{
			target.appendJavaScript("var consolePanel = document.getElementById('console'); consolePanel.innerHTML = ''; ");
			this.persistenceService.deleteAllMessagesForAGame(gameId);
			return;
		}

		final String newDate = new SimpleDateFormat("HH:mm:ss").format(this.date);
		String __message = _message;

		for (String card : cardNames)
		{
			String _card = card.replaceAll("'", "&apos;");
			__message = __message.replaceFirst("&&&",
					"<a class='consoleCard' style='color: white;' href='#' title='<img src=\\\\\"cards/"
							+ _card + ".jpg\\\\\"></img>'>" + _card + "</a>");
		}

		target.appendJavaScript("var consolePanel = document.getElementById('console'); consolePanel.innerHTML = consolePanel.innerHTML + \"<br/>\" + \""
				+ newDate
				+ ": "
				+ __message
				+ "\" + \"<br/>\"; consolePanel.scrollTop = consolePanel.scrollHeight; document.activeElement.blur();  "
				+ "window.setTimeout(function() { jQuery('.consoleCard[title]').tipsy({html: true, gravity: 'n'});  "
				+ "jQuery('.consoleCard[original-title]').tipsy({html: true, gravity: 'n'});  "
				+ "jQuery('.consoleCard').click(function() { "
				+ "return false;  "
				+ "});"
				+ "}, 500); ");

		final ConsoleLogMessage msg = new ConsoleLogMessage();
		msg.setMessage(newDate + ": " + __message);
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

		ConsoleLogStrategy.LOGGER.info(newDate + ": " + __message);
		this.message = __message;
	}

	@Required
	public void setPersistenceService(final PersistenceService _persistenceService)
	{
		this.persistenceService = _persistenceService;
	}

	String getMessage()
	{
		return this.message;
	}

}
