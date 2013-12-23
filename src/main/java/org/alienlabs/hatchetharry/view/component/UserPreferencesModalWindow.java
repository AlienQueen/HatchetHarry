package org.alienlabs.hatchetharry.view.component;

import org.alienlabs.hatchetharry.HatchetHarrySession;
import org.alienlabs.hatchetharry.model.User;
import org.alienlabs.hatchetharry.service.PersistenceService;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxButton;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

public class UserPreferencesModalWindow extends Panel
{
	private static final long serialVersionUID = 1L;

	static final Logger LOGGER = LoggerFactory.getLogger(UserPreferencesModalWindow.class);

	@SpringBean
	PersistenceService persistenceService;

	public UserPreferencesModalWindow(final String id, final Long gameId, final ModalWindow window)
	{
		super(id);

		final Form<String> form = new Form<String>("form");

		final User user;

		if (null == HatchetHarrySession.get().getUsername())
		{
			user = new User();
			user.setLogin("test");
			user.setPassword("test");
			user.setPlayer(HatchetHarrySession.get().getPlayer());
			user.setIdentity("test");
			user.setIsFacebook(true);
			user.setUsername("test");
		}
		else if (null == this.persistenceService.getUser(HatchetHarrySession.get().getUsername()))
		{
			user = new User();
			user.setLogin(HatchetHarrySession.get().getUsername());
			user.setPassword("");
			user.setPlayer(HatchetHarrySession.get().getPlayer());
			user.setIdentity("");
			user.setIsFacebook(true);
			user.setUsername("");
		}
		else
		{
			user = this.persistenceService.getUser(HatchetHarrySession.get().getUsername());
		}

		final Label login = new Label("login", user.getLogin());

		final Model<String> usernameModel = Model.of(user.getUsername());
		final RequiredTextField<String> username = new RequiredTextField<String>("username",
				usernameModel);

		final Model<String> identityModel = Model.of(user.getIdentity());
		final RequiredTextField<String> identity = new RequiredTextField<String>("identity",
				identityModel);

		final Model<String> passwordModel = Model.of(user.getPassword());
		final PasswordTextField password = new PasswordTextField("password", passwordModel);

		final IndicatingAjaxButton submit = new IndicatingAjaxButton("submit", form)
		{
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit(final AjaxRequestTarget target, final Form<?> _form)
			{
				UserPreferencesModalWindow.LOGGER.info("submit");

				if (!"test".equals(usernameModel.getObject()))
				{
					user.setIdentity(identityModel.getObject());
					user.setIsFacebook(true);
					user.setLogin(HatchetHarrySession.get().getUsername());
					user.setPassword(passwordModel.getObject());
					user.setUsername(usernameModel.getObject());
					user.setPlayer(HatchetHarrySession.get().getPlayer());

					UserPreferencesModalWindow.this.persistenceService.saveOrUpdateUser(user);
				}

				window.close(target);
			}

			@Override
			protected void onError(final AjaxRequestTarget target, final Form<?> _form)
			{
				UserPreferencesModalWindow.LOGGER.error("ERROR");
			}
		};

		form.add(login, username, identity, password, submit);
		this.add(form);
	}

	@Required
	public void setPersistenceService(final PersistenceService _persistenceService)
	{
		this.persistenceService = _persistenceService;
	}

}