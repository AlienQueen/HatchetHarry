package org.alienlabs.hatchetharry.view.component.gui;

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
	static final Logger LOGGER = LoggerFactory.getLogger(ConferencePanel.class);
	private static final long serialVersionUID = 1L;
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
			user.setPrivateIdentity("");
			user.setPassword("");
			user.setPlayer(null);
			user.setIdentity("");
			user.setFacebook(true);
			user.setUsername("");
			user.setRealm("");
		}
		else if (null != this.persistenceService.getUser(HatchetHarrySession.get().getUsername()))
		{
			user = this.persistenceService.getUser(HatchetHarrySession.get().getUsername());
			ConferencePanel.LOGGER.info("#user: " + user);

			if (null == user.getIdentity())
			{
				user.setLogin("");
				user.setPrivateIdentity("");
				user.setPassword("");
				user.setPlayer(null);
				user.setIdentity("");
				user.setFacebook(true);
				user.setUsername("");
				user.setRealm("");
			}
		}
		else
		{
			ConferencePanel.LOGGER.info("#3");
			user = new User();
			user.setLogin("");
			user.setPrivateIdentity("");
			user.setPassword("");
			user.setPlayer(null);
			user.setIdentity("");
			user.setFacebook(true);
			user.setUsername("");
			user.setRealm("");
		}

		final Model<String> loginModel = Model.of(user.getLogin());
		final RequiredTextField<String> login = new RequiredTextField<String>("login", loginModel);

		final Model<String> privateIdentityModel = Model.of(user.getPrivateIdentity());
		final RequiredTextField<String> privateIdentity = new RequiredTextField<String>(
				"privateIdentity", privateIdentityModel);

		final Model<String> identityModel = Model.of(user.getIdentity());
		final RequiredTextField<String> identity = new RequiredTextField<String>("identity",
				identityModel);

		final Model<String> passwordModel = Model.of(user.getPassword());
		final PasswordTextField password = new PasswordTextField("password", passwordModel);
		password.setResetPassword(false);

		final Model<String> realmModel = Model.of(user.getRealm());
		final RequiredTextField<String> realm = new RequiredTextField<String>("realm", realmModel);

		this.add(login, privateIdentity, identity, password, realm);
	}

	@Required
	public void setPersistenceService(final PersistenceService _persistenceService)
	{
		this.persistenceService = _persistenceService;
	}

}
