package org.alienlabs.hatchetharry.view.component.modalwindow;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.panel.Panel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wicketstuff.facebook.plugins.LoginButton;

@edu.umd.cs.findbugs.annotations.SuppressWarnings(value = { "SE_INNER_CLASS",
		"SIC_INNER_SHOULD_BE_STATIC_ANON" }, justification = "In Wicket, serializable inner classes are common. And as the parent Page is serialized as well, this is no concern. This is no bad practice in Wicket")
public class LoginModalWindow extends Panel
{
	static final Logger LOGGER = LoggerFactory.getLogger(LoginModalWindow.class);
	private static final long serialVersionUID = 1L;

	public LoginModalWindow(final String id, final ModalWindow window)
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
