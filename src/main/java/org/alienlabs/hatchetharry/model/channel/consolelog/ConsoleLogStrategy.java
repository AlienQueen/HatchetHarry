package org.alienlabs.hatchetharry.model.channel.consolelog;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class ConsoleLogStrategy
{
	private static final Logger LOGGER = LoggerFactory.getLogger(ConsoleLogStrategy.class);

	protected final Date date = new Date();

	public abstract void logToConsole(AjaxRequestTarget target);

	protected void logMessage(final AjaxRequestTarget target, final String message,
			final Boolean clearConsole)
	{
		if ((null != clearConsole) && clearConsole)
		{
			target.appendJavaScript("var consolePanel = document.getElementById('console'); consolePanel.innerHTML = ''; ");
		}

		final String newDate = new SimpleDateFormat("HH:mm:ss").format(this.date);

		target.appendJavaScript("var consolePanel = document.getElementById('console'); consolePanel.innerHTML = consolePanel.innerHTML + \"&#013;&#010;\" + \""
				+ newDate
				+ ": "
				+ message
				+ "\" + \"&#013;&#010;\"; consolePanel.scrollTop = consolePanel.scrollHeight; document.activeElement.blur(); ");

		ConsoleLogStrategy.LOGGER.info(newDate + ": " + message);
	}

}
