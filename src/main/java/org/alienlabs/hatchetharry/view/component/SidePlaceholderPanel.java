package org.alienlabs.hatchetharry.view.component;

import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.alienlabs.hatchetharry.view.page.HomePage;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.protocol.http.servlet.ServletWebRequest;
import org.apache.wicket.request.resource.PackageResourceReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SidePlaceholderPanel extends Panel
{
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = LoggerFactory.getLogger(SidePlaceholderPanel.class);

	private final UUID uuid;

	private long posX, posY;
	private final HomePage homePage;
	private final String side;

	public SidePlaceholderPanel(final String id, final String _side, final HomePage hp,
			final UUID _uuid)
	{
		super(id);
		this.setOutputMarkupId(true);

		this.homePage = hp;
		this.uuid = _uuid;
		this.side = _side;

		final WebMarkupContainer sidePlaceholder = new WebMarkupContainer("sidePlaceholder");
		sidePlaceholder.setOutputMarkupId(true);
		sidePlaceholder.setMarkupId("sidePlaceholder" + this.uuid.toString());

		final ServletWebRequest servletWebRequest = (ServletWebRequest)this.getRequest();
		final HttpServletRequest request = servletWebRequest.getContainerRequest();
		final String jsessionid = request.getRequestedSessionId();
		this.add(new SidePlaceholderMoveBehavior(this, this.uuid, jsessionid, this.homePage,
				this.side));

		final Form<String> form = new Form<String>("form");
		form.setOutputMarkupId(true);

		final TextField<String> jsessionidTextField = new TextField<String>("jsessionid",
				new Model<String>(this.getHttpServletRequest().getRequestedSessionId()));
		jsessionidTextField.setMarkupId("jsessionid" + this.uuid);
		jsessionidTextField.setOutputMarkupId(true);

		SidePlaceholderPanel.LOGGER.info("jsessionid: "
				+ this.getHttpServletRequest().getRequestedSessionId());
		SidePlaceholderPanel.LOGGER.info("uuid: " + this.uuid);
		final TextField<String> mouseX = new TextField<String>("mouseX", new Model<String>("0"));
		final TextField<String> mouseY = new TextField<String>("mouseY", new Model<String>("0"));
		mouseX.setMarkupId("mouseX" + this.uuid);
		mouseY.setMarkupId("mouseY" + this.uuid);
		mouseX.setOutputMarkupId(true);
		mouseY.setOutputMarkupId(true);

		final Image handleImage = new Image("handleImage", new PackageResourceReference(
				"images/arrow.png"));
		handleImage.setOutputMarkupId(true);
		handleImage.setMarkupId("handleImage" + this.uuid.toString());

		final String image = ("infrared".equals(this.side))
				? "image/logobouclierrouge.png"
				: "image/logobouclierviolet.png";

		final Image cardImage = new Image("sidePlaceholderImage", new PackageResourceReference(
				HomePage.class, image));
		cardImage.setOutputMarkupId(true);
		cardImage.setMarkupId("card" + this.uuid.toString());

		form.add(jsessionidTextField, mouseX, mouseY, handleImage, cardImage);
		sidePlaceholder.add(form);
		this.add(sidePlaceholder);
	}

	public HttpServletRequest getHttpServletRequest()
	{
		final ServletWebRequest servletWebRequest = (ServletWebRequest)this.getRequest();
		return servletWebRequest.getContainerRequest();
	}

	public UUID getUuid()
	{
		return this.uuid;
	}

	public long getPosX()
	{
		return this.posX;
	}

	public void setPosX(final long _posX)
	{
		this.posX = _posX;
	}

	public long getPosY()
	{
		return this.posY;
	}

	public void setPosY(final long _posY)
	{
		this.posY = _posY;
	}

	public String getSide()
	{
		return this.side;
	}

}
