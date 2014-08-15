package org.alienlabs.hatchetharry.view.component;

import org.alienlabs.hatchetharry.HatchetHarrySession;
import org.alienlabs.hatchetharry.model.User;
import org.alienlabs.hatchetharry.service.PersistenceService;
import org.alienlabs.hatchetharry.view.page.HomePage;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.protocol.http.servlet.ServletWebRequest;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.template.PackageTextTemplate;
import org.apache.wicket.util.template.TextTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.HashMap;

public class FacebookLoginBehavior extends AbstractDefaultAjaxBehavior {
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = LoggerFactory.getLogger(FacebookLoginBehavior.class);

	@SpringBean
	PersistenceService persistenceService;

	@Override
	protected void respond(final AjaxRequestTarget target) {
		final ServletWebRequest servletWebRequest = (ServletWebRequest) target.getPage()
																				.getRequest();
		final HttpServletRequest request = servletWebRequest.getContainerRequest();

		FacebookLoginBehavior.LOGGER.info("respond to: " + request.getQueryString());

		final String username = request.getParameter("username");
		if ((null != username) && !"undefined".equals(username)) {
			final Label usernameLabel = new Label("username", "Logged in as " + username);
			usernameLabel.setOutputMarkupId(true);
			HatchetHarrySession.get().setUsername(username);
			HatchetHarrySession.get().setLoggedIn(true);

			final WebMarkupContainer usernameParent = ((HomePage) target.getPage())
															  .getUsernameParent();
			usernameParent.addOrReplace(usernameLabel);
			target.add(usernameParent);

			final User inDb = this.persistenceService.getUser(username);
			if (null == inDb) {
				final User user = new User();
				user.setUsername(username);
				user.setFacebook(true);
				user.setPlayer(HatchetHarrySession.get().getPlayer());
				this.persistenceService.saveOrUpdateUser(user);
			}
		}
	}

	@Override
	public void renderHead(final Component component, final IHeaderResponse response) {
		super.renderHead(component, response);

		final HashMap<String, Object> variables = new HashMap<String, Object>();
		variables.put("url", this.getCallbackUrl());

		final TextTemplate template = new PackageTextTemplate(HomePage.class,
																	 "script/login/facebook.js");
		template.interpolate(variables);

		response.render(JavaScriptHeaderItem.forScript(template.asString(), "facebook"));
		try {
			template.close();
		} catch (final IOException e) {
			FacebookLoginBehavior.LOGGER.error(
													  "unable to close template in FacebookLoginBehavior#renderHead()!", e);
		}
	}

	@Required
	public void setPersistenceService(final PersistenceService _persistenceService) {
		this.persistenceService = _persistenceService;
	}

}
