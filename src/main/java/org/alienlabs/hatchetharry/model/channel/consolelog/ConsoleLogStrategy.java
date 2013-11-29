package org.alienlabs.hatchetharry.model.channel.consolelog;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.wicket.ajax.AjaxRequestTarget;

public abstract class ConsoleLogStrategy
{
	protected final Date date = new Date();

	public abstract void logToConsole(AjaxRequestTarget target);

	protected void logMessage(final AjaxRequestTarget target, final String message,
			final Boolean clearConsole)
	{
		if ((null != clearConsole) && clearConsole)
		{
			target.appendJavaScript("var consolePanel = document.getElementById('console'); consolePanel.innerHTML = ''; ");
		}

		target.appendJavaScript("var consolePanel = document.getElementById('console'); consolePanel.innerHTML = consolePanel.innerHTML + \"&#013;&#010;\" + \""
				+ new SimpleDateFormat("HH:mm:ss").format(this.date)
				+ ": "
				+ message
				+ "\" + \"&#013;&#010;\"; consolePanel.scrollTop = consolePanel.scrollHeight; document.activeElement.blur(); ");
	}

}
