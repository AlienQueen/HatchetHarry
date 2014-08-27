package org.alienlabs.hatchetharry.view.component;

import org.alienlabs.hatchetharry.HatchetHarrySession;
import org.alienlabs.hatchetharry.model.User;
import org.alienlabs.hatchetharry.service.PersistenceService;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxButton;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

@edu.umd.cs.findbugs.annotations.SuppressWarnings(value = "SE_INNER_CLASS", justification = "In Wicket, serializable inner classes are common. And as the parent Page is serialized as well, this is no concern. This is no bad practice in Wicket")
public class UserPreferencesModalWindow extends Panel
{
	static final Logger LOGGER = LoggerFactory.getLogger(UserPreferencesModalWindow.class);
	private static final long serialVersionUID = 1L;
	@SpringBean
	PersistenceService persistenceService;

	public UserPreferencesModalWindow(final String id, final ModalWindow window)
	{
		super(id);

		final Form<String> form = new Form<String>("form");

		final User user;

		if ((null == HatchetHarrySession.get().getUsername())
			|| (null == this.persistenceService.getUser(HatchetHarrySession.get().getUsername())))
		{
			user = new User();
			user.setLogin("");
			user.setPrivateIdentity("");
			user.setPassword("");
			user.setPlayer(HatchetHarrySession.get().getPlayer());
			user.setIdentity("");
			user.setFacebook(true);
			user.setUsername("");
			user.setRealm("");
		}
		else
		{
			user = this.persistenceService.getUser(HatchetHarrySession.get().getUsername());
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

		final Model<String> realmModel = Model.of(user.getRealm());
		final RequiredTextField<String> realm = new RequiredTextField<String>("realm", realmModel);

		final IndicatingAjaxButton submit = new IndicatingAjaxButton("submit", form)
		{
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit(final AjaxRequestTarget target, final Form<?> _form)
			{
				if (!"test".equals(loginModel.getObject()))
				{
					user.setLogin(loginModel.getObject());
					user.setPrivateIdentity(privateIdentityModel.getObject());
					user.setIdentity(identityModel.getObject());
					user.setFacebook(true);
					user.setUsername(HatchetHarrySession.get().getUsername());
					user.setPassword(passwordModel.getObject());
					user.setPlayer(HatchetHarrySession.get().getPlayer());
					user.setRealm(realmModel.getObject());

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

		form.add(login, privateIdentity, identity, password, realm, submit);
		this.add(form);
	}

	@Required
	public void setPersistenceService(final PersistenceService _persistenceService)
	{
		this.persistenceService = _persistenceService;
	}

}
