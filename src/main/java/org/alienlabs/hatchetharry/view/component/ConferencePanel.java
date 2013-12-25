package org.alienlabs.hatchetharry.view.component;

import org.alienlabs.hatchetharry.HatchetHarrySession;
import org.alienlabs.hatchetharry.model.User;
import org.alienlabs.hatchetharry.service.PersistenceService;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
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

		final User user;

		if ((null == HatchetHarrySession.get().getUsername())
				|| (null == this.persistenceService
						.getUser(HatchetHarrySession.get().getUsername())))
		{
			ConferencePanel.LOGGER.info("#1");
			user = new User();
			user.setLogin("");
			user.setPassword("");
			user.setPlayer(null);
			user.setIdentity("");
			user.setFacebook(true);
			user.setUsername("");
		}
		else if (null != this.persistenceService.getUser(HatchetHarrySession.get().getUsername()))
		{
			user = this.persistenceService.getUser(HatchetHarrySession.get().getUsername());
			ConferencePanel.LOGGER.info("#user: " + user);
		}
		else
		{
			ConferencePanel.LOGGER.info("#3");
			user = new User();
			user.setLogin("");
			user.setPassword("");
			user.setPlayer(null);
			user.setIdentity("");
			user.setFacebook(true);
			user.setUsername("");
		}

		final Model<String> identityModel = Model.of(user.getIdentity());
		final RequiredTextField<String> identity = new RequiredTextField<String>("identity",
				identityModel);

		final Model<String> passwordModel = Model.of(user.getPassword());
		final PasswordTextField password = new PasswordTextField("password", passwordModel);

		this.add(identity, password);
	}

	@Required
	public void setPersistenceService(final PersistenceService _persistenceService)
	{
		this.persistenceService = _persistenceService;
	}

}
