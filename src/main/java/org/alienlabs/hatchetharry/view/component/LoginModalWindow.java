package org.alienlabs.hatchetharry.view.component;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.basic.MultiLineLabel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wicketstuff.facebook.FacebookPermission;
import org.wicketstuff.facebook.FacebookSdk;
import org.wicketstuff.facebook.behaviors.AuthStatusChangeEventBehavior;
import org.wicketstuff.facebook.plugins.LoginButton;

public class LoginModalWindow extends Panel
{
	private static final long serialVersionUID = 1L;

	static final Logger LOGGER = LoggerFactory.getLogger(LoginModalWindow.class);

	public LoginModalWindow(final String id, final Long gameId)
	{
		super(id);
		this.add(new FacebookSdk("fb-root", "1398596203720626"));
		this.add(new LoginButton("loginButton", FacebookPermission.user_events));

		final Model<String> responseModel = new Model<String>();
		final MultiLineLabel responseLabel = new MultiLineLabel("response", responseModel);
		responseLabel.setOutputMarkupId(true);
		this.add(responseLabel);

		this.add(new AuthStatusChangeEventBehavior()
		{
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSessionEvent(final AjaxRequestTarget target, final String status,
					final String userId, final String signedRequest, final String expiresIn,
					final String accessToken)
			{
				final StringBuilder sb = new StringBuilder();
				sb.append("status: ").append(status).append('\n');
				sb.append("signedRequest: ").append(signedRequest).append('\n');
				sb.append("expiresIn: ").append(expiresIn).append('\n');
				sb.append("accessToken: ").append(accessToken).append('\n');

				LoginModalWindow.LOGGER.info(sb.toString());
				responseModel.setObject(sb.toString());
				target.add(responseLabel);
			}
		});
	}

}