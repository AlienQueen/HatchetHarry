package org.alienlabs.hatchetharry.view.component;

import org.alienlabs.hatchetharry.service.PersistenceService;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

public class ConferencePanel extends Panel
{
	private static final long serialVersionUID = 1L;
	static final Logger LOGGER = LoggerFactory.getLogger(ConferencePanel.class);

	@SpringBean
	PersistenceService persistenceService;

	public ConferencePanel(final String id)
	{
		super(id);
	}

	@Required
	public void setPersistenceService(final PersistenceService _persistenceService)
	{
		this.persistenceService = _persistenceService;
	}

}
