package org.alienlabs.hatchetharry.view.page;

import org.alienlabs.hatchetharry.HatchetHarryApplication;
import org.alienlabs.hatchetharry.HatchetHarrySession;
import org.apache.wicket.markup.html.WebPage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EndGamePage extends WebPage
{
	private static final long serialVersionUID = 1L;
	static final Logger LOGGER = LoggerFactory.getLogger(EndGamePage.class);

	public EndGamePage()
	{
		EndGamePage.LOGGER.info("ending game #" + HatchetHarrySession.get().getGameId());
		HatchetHarryApplication.getCometResources().remove(
				HatchetHarrySession.get().getPlayer().getId());
		HatchetHarrySession.get().invalidate();
	}
}
