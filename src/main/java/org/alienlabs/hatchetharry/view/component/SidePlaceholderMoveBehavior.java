package org.alienlabs.hatchetharry.view.component;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.alienlabs.hatchetharry.HatchetHarrySession;
import org.alienlabs.hatchetharry.view.page.HomePage;
import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.injection.web.InjectorHolder;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.protocol.http.servlet.ServletWebRequest;
import org.apache.wicket.util.template.PackagedTextTemplate;
import org.apache.wicket.util.template.TextTemplate;
import org.atmosphere.cpr.BroadcastFilter;
import org.atmosphere.cpr.Meteor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SidePlaceholderMoveBehavior extends AbstractDefaultAjaxBehavior
{
	private static final long serialVersionUID = 1L;

	private static final Logger logger = LoggerFactory.getLogger(SidePlaceholderMoveBehavior.class);
	private final UUID uuid;
	private String jsessionid;
	private final WebMarkupContainer parent;

	private UUID toShow;
	private final HomePage homePage;

	public SidePlaceholderMoveBehavior(final WebMarkupContainer _parent, final UUID _uuid,
			final String _jsessionid, final HomePage hp)
	{
		this.parent = _parent;
		this.uuid = _uuid;
		this.jsessionid = _jsessionid;
		this.homePage = hp;

		InjectorHolder.getInjector().inject(this);
	}

	@Override
	protected void respond(final AjaxRequestTarget target)
	{
		SidePlaceholderMoveBehavior.logger.info("## respond");
		final ServletWebRequest servletWebRequest = (ServletWebRequest)this.parent.getRequest();
		final HttpServletRequest request = servletWebRequest.getHttpServletRequest();
		this.jsessionid = request.getRequestedSessionId();
		final String _sideX = request.getParameter("posX");
		final String _sideY = request.getParameter("posY");
		this.toShow = UUID.fromString(request.getParameter("uuid"));
		final String _side = request.getParameter("side");
		final String _uuid = request.getParameter("uuid");

		// if (!this.jsessionid.equals(request.getParameter("requestingId")))
		// {
		// if (HatchetHarrySession.get().isMySidePlaceholderInSesion(
		// ((HatchetHarrySession.get()).getPlayer().getSide())))
		// {
		// target.appendJavascript("jQuery(document).ready(function() { var card = jQuery(\"#sidePlaceholder"
		// + this.toShow
		// + "\"); "
		// + "card.css(\"position\", \"absolute\"); "
		// + "card.css(\"left\", \""
		// + _sideX
		// + "px\"); "
		// + "card.css(\"top\", \""
		// + _sideY + "px\"); });");
		// }
		// else
		// {
		// if
		// (!((HatchetHarrySession.get()).isMySidePlaceholderInSesion("infrared")))
		// {

		if ((_sideX == null) || (_sideY == null))
		{
			final SidePlaceholderPanel spp = new SidePlaceholderPanel("secondSidePlaceholder",
					_side, this.homePage, UUID.randomUUID());
			spp.add(new SidePlaceholderMoveBehavior(this.parent, UUID.fromString(_uuid),
					this.jsessionid, this.homePage));
			spp.setOutputMarkupId(true);

			final HatchetHarrySession h = ((HatchetHarrySession.get()));
			h.putMySidePlaceholderInSesion(_side);


			this.homePage.getSecondSidePlaceholderParent().addOrReplace(spp);
			target.addComponent(this.homePage.getSecondSidePlaceholderParent());

			target.appendJavascript("jQuery(document).ready(function() { var card = jQuery(\"#sidePlaceholder"
					+ this.toShow
					+ "\"); "
					+ "card.css(\"position\", \"absolute\"); "
					+ "card.css(\"left\", \""
					+ ((_sideX != null) ? _sideX : 300)
					+ "px\"); "
					+ "card.css(\"top\", \"" + ((_sideY != null) ? _sideY : 500) + "px\"); });");
			// }
			// }
		}
		else
		{
			target.appendJavascript("jQuery(document).ready(function() { var card = jQuery(\"#sidePlaceholder"
					+ this.toShow
					+ "\"); "
					+ "card.css(\"position\", \"absolute\"); "
					+ "card.css(\"left\", \""
					+ _sideX
					+ "px\"); "
					+ "card.css(\"top\", \""
					+ _sideY + "px\"); });");

			final Meteor meteor = Meteor.build(request, new LinkedList<BroadcastFilter>(), null);
			// meteor.addListener((AtmosphereResourceEventListener)target.getPage());
			meteor.broadcast(_side + "|||||" + this.jsessionid + "|||||" + this.uuid + "|||||"
					+ _sideX + "|||||" + _sideY);
		}
	}

	@Override
	public void renderHead(final IHeaderResponse response)
	{
		super.renderHead(response);

		StringBuffer js = new StringBuffer();

		final HashMap<String, Object> variables = new HashMap<String, Object>();
		variables.put("url", this.getCallbackUrl());
		variables.put("uuid", this.uuid);
		variables.put("uuidValidForJs", this.uuid.toString().replace("-", "_"));
		variables.put("jsessionid", this.jsessionid);

		final TextTemplate template4 = new PackagedTextTemplate(HomePage.class,
				"script/draggableHandle/jquery.ui.draggable.sidePlaceholder.js");
		template4.interpolate(variables);
		js = js.append("\n" + template4.asString());

		final TextTemplate template5 = new PackagedTextTemplate(HomePage.class,
				"script/draggableHandle/sidePlaceholderMove.js");
		template5.interpolate(variables);
		js = js.append("\n" + template5.asString());

		final TextTemplate template6 = new PackagedTextTemplate(HomePage.class,
				"script/draggableHandle/initSidePlaceholderDrag.js");
		template6.interpolate(variables);
		js = js.append("\n" + template6.asString());

		response.renderOnDomReadyJavascript(js.toString());
	}

}
