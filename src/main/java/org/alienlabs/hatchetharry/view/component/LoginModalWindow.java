package org.alienlabs.hatchetharry.view.component;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.panel.Panel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wicketstuff.facebook.plugins.LoginButton;

public class LoginModalWindow extends Panel
{
	private static final long serialVersionUID = 1L;

	static final Logger LOGGER = LoggerFactory.getLogger(LoginModalWindow.class);

	public LoginModalWindow(final String id, final Long gameId, final ModalWindow window)
	{
		super(id);

		final LoginButton button = new LoginButton("loginButton");
		button.setShowFaces(true);

		final AjaxLink<Void> close = new AjaxLink<Void>("close")
		{
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(final AjaxRequestTarget target)
			{
				target.appendJavaScript("authenticateUserWithFacebook();");
				window.close(target);
			}

		};

		this.add(button, close);
	}
}