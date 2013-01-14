package org.alienlabs.hatchetharry.view.component;

import java.util.LinkedList;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.alienlabs.hatchetharry.HatchetHarrySession;
import org.alienlabs.hatchetharry.view.page.HomePage;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.protocol.http.servlet.ServletWebRequest;
import org.atmosphere.cpr.BroadcastFilter;
import org.atmosphere.cpr.Meteor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SidePlaceholderMoveBehavior extends AbstractDefaultAjaxBehavior
{
	private static final long serialVersionUID = 1L;

	private static final Logger LOGGER = LoggerFactory.getLogger(SidePlaceholderMoveBehavior.class);

	private UUID uuid;
	private String jsessionid;
	private final WebMarkupContainer parent;
	private final String side;
	private final HomePage homePage;
	final WebMarkupContainer dataBoxParent;
	final private Long gameId;

	public SidePlaceholderMoveBehavior(final WebMarkupContainer _parent, final UUID _uuid,
			final String _jsessionid, final HomePage hp, final String _side,
			final WebMarkupContainer _dataBoxParent, final Long _gameId)
	{
		this.parent = _parent;
		this.uuid = _uuid;
		this.jsessionid = _jsessionid;
		this.homePage = hp;
		this.side = _side;
		this.dataBoxParent = _dataBoxParent;
		this.gameId = _gameId;
	}

	@Override
	protected void respond(final AjaxRequestTarget target)
	{
		SidePlaceholderMoveBehavior.LOGGER.info("## respond");
		final ServletWebRequest servletWebRequest = (ServletWebRequest)this.parent.getRequest();
		final HttpServletRequest request = servletWebRequest.getContainerRequest();
		this.jsessionid = request.getRequestedSessionId();
		final String _sideX = request.getParameter("posX");
		final String _sideY = request.getParameter("posY");
		final String stop = request.getParameter("stop");

		this.uuid = null;
		try
		{
			this.uuid = UUID.fromString(request.getParameter("uuid"));
		}
		catch (final Exception e)
		{
			SidePlaceholderMoveBehavior.LOGGER.error("error parsing UUID: " + e.getMessage());
			return;
		}
		final String _side = request.getParameter("side");

		if ("true".equals(stop))
		{
			final SidePlaceholderPanel spp = new SidePlaceholderPanel("secondSidePlaceholder",
					_side, this.homePage, this.uuid);
			spp.add(new SidePlaceholderMoveBehavior(spp, this.uuid, this.jsessionid, this.homePage,
					_side, this.homePage.getDataBoxParent(), this.gameId));
			spp.setOutputMarkupId(true);
			SidePlaceholderMoveBehavior.LOGGER.info("### gameId: " + this.gameId);

			final HatchetHarrySession session = HatchetHarrySession.get();
			session.putMySidePlaceholderInSesion(_side);


			this.homePage.getSecondSidePlaceholderParent().addOrReplace(spp);
			target.add(this.homePage.getSecondSidePlaceholderParent());

			SidePlaceholderMoveBehavior.LOGGER.info("### " + this.uuid);
			final int posX = ("infrared".equals(_side)) ? 300 : 900;

			target.appendJavaScript("jQuery(document).ready(function() { var card = jQuery('#sidePlaceholder"
					+ this.uuid
					+ "'); "
					+ "card.css('position', 'absolute'); "
					+ "card.css('left', '" + posX + "px'); " + "card.css('top', '500px'); });");

			spp.setPosX(posX);
			spp.setPosY(500);
			session.setMySidePlaceholder(spp);

			if (!this.jsessionid.equals(request.getParameter("requestingId")))
			{
				final DataBox dataBox = new DataBox("dataBox", this.gameId);
				dataBox.setOutputMarkupId(true);

				final WebMarkupContainer _parent = this.homePage.getDataBoxParent();
				_parent.addOrReplace(dataBox);
				target.add(_parent);
				SidePlaceholderMoveBehavior.LOGGER.info("# databox for game id=" + this.gameId);
			}
		}
		else if ((_sideX != null)
				&& (_sideY != null)
				&& (!this.jsessionid.equals(request.getParameter("requestingId")) && (!"true"
						.equals(stop))))
		{
			target.appendJavaScript("jQuery(document).ready(function() { var card = jQuery(\"#sidePlaceholder"
					+ this.uuid
					+ "\"); "
					+ "card.css(\"position\", \"absolute\"); "
					+ "card.css(\"left\", \""
					+ _sideX
					+ "px\"); "
					+ "card.css(\"top\", \""
					+ _sideY + "px\"); });");
			HatchetHarrySession.get().setMySidePosX(Integer.valueOf(_sideX));
			HatchetHarrySession.get().setMySidePosY(Integer.valueOf(_sideY));

			final Meteor meteor = Meteor.build(request, new LinkedList<BroadcastFilter>(), null);
			final String message = _side + "|||||" + this.jsessionid + "|||||" + this.uuid
					+ "|||||" + _sideX + "|||||" + _sideY;
			SidePlaceholderMoveBehavior.LOGGER.info("### message: " + message);
			meteor.broadcast(message);
		}
	}

	@Override
	public void renderHead(final Component component, final IHeaderResponse response)
	{
		super.renderHead(component, response);
		// TODO virer ces js
		// final StringBuffer js = new StringBuffer();
		//
		// final HashMap<String, Object> variables = new HashMap<String,
		// Object>();
		// variables.put("url", this.getCallbackUrl());
		// variables.put("uuid", this.uuid);
		// variables.put("uuidValidForJs", this.uuid.toString().replace("-",
		// "_"));
		// variables.put("jsessionid", this.jsessionid);
		// variables.put("side", this.side);
		//
		// final TextTemplate template1 = new
		// PackageTextTemplate(HomePage.class,
		// "script/draggableHandle/jquery.ui.draggable.sidePlaceholder.js");
		// template1.interpolate(variables);
		// js.append("\n" + template1.asString());
		//
		// final TextTemplate template2 = new
		// PackageTextTemplate(HomePage.class,
		// "script/draggableHandle/sidePlaceholderMove.js");
		// template2.interpolate(variables);
		// js.append("\n" + template2.asString());
		//
		// final TextTemplate template3 = new
		// PackageTextTemplate(HomePage.class,
		// "script/draggableHandle/initSidePlaceholderDrag.js");
		// template3.interpolate(variables);
		// js.append("\n" + template3.asString());
		//
		// response.render(JavaScriptHeaderItem.forScript(js.toString(),
		// "sidePlaceholderMove"));
	}

}
