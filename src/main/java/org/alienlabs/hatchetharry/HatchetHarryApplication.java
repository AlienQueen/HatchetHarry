package org.alienlabs.hatchetharry;

import org.alienlabs.hatchetharry.view.HomePage;
import org.apache.wicket.Request;
import org.apache.wicket.ResourceReference;
import org.apache.wicket.Response;
import org.apache.wicket.Session;
import org.apache.wicket.protocol.http.WebApplication;

import ch.qos.mistletoe.wicket.TestReportPage;

import com.ttdev.wicketpagetest.MockableSpringBeanInjector;

/**
 * Application object for your web application. If you want to run this
 * application without deploying, run the Start class.
 * 
 * 
 */
public class HatchetHarryApplication extends WebApplication
{

	/**
	 * Constructor
	 */
	public HatchetHarryApplication()
	{
	}

	/**
	 * @see org.apache.wicket.Application#getHomePage()
	 */
	@Override
	public Class<? extends TestReportPage> getHomePage()
	{
		return HomePage.class;
	}

	@Override
	protected void init()
	{
		super.init();

		MockableSpringBeanInjector.installInjector(this);
		this.mountSharedResource("images/logo32_32.png", new ResourceReference(HomePage.class,
				"images/logo32_32.png").getSharedResourceKey());
		this.mountSharedResource("images/library.gif", new ResourceReference(HomePage.class,
				"images/library.gif").getSharedResourceKey());
		this.mountSharedResource("images/hand.gif", new ResourceReference(HomePage.class,
				"images/hand.gif").getSharedResourceKey());
		this.mountSharedResource("images/eMail.png", new ResourceReference(HomePage.class,
				"images/eMail.png").getSharedResourceKey());
		this.mountSharedResource("images/graveyard.gif", new ResourceReference(HomePage.class,
				"images/graveyard.gif").getSharedResourceKey());
		this.mountSharedResource("images/Safari.png", new ResourceReference(HomePage.class,
				"images/Safari.png").getSharedResourceKey());
		this.mountSharedResource("images/Terminal.png", new ResourceReference(HomePage.class,
				"images/Terminal.png").getSharedResourceKey());
		this.mountSharedResource("images/gradient1.jpg", new ResourceReference(HomePage.class,
				"images/gradient1.jpg").getSharedResourceKey());

		this.mountSharedResource("images/bgnd_sel_1.jpg", new ResourceReference(HomePage.class,
				"images/bgnd_sel_1.jpg").getSharedResourceKey());
		this.mountSharedResource("images/bgnd_sel_2.jpg", new ResourceReference(HomePage.class,
				"images/bgnd_sel_2.jpg").getSharedResourceKey());
		this.mountSharedResource("images/bgnd_sel_3.jpg", new ResourceReference(HomePage.class,
				"images/bgnd_sel_3.jpg").getSharedResourceKey());
		this.mountSharedResource("images/bgnd_sel_4.png", new ResourceReference(HomePage.class,
				"images/bgnd_sel_4.png").getSharedResourceKey());
		this.mountSharedResource("images/box_menu_over.png", new ResourceReference(HomePage.class,
				"images/box_menu_over.png").getSharedResourceKey());
		this.mountSharedResource("images/box_top.png", new ResourceReference(HomePage.class,
				"images/box_top.png").getSharedResourceKey());
		this.mountSharedResource("images/browser.png", new ResourceReference(HomePage.class,
				"images/browser.png").getSharedResourceKey());
		this.mountSharedResource("images/DV_Tools.jpg", new ResourceReference(HomePage.class,
				"images/DV_Tools.jpg").getSharedResourceKey());
		this.mountSharedResource("images/header_bgnd.jpg", new ResourceReference(HomePage.class,
				"images/header_bgnd.jpg").getSharedResourceKey());
		this.mountSharedResource("images/menuArrow.gif", new ResourceReference(HomePage.class,
				"images/menuArrow.gif").getSharedResourceKey());
		this.mountSharedResource("images/menuArrow_w.gif", new ResourceReference(HomePage.class,
				"images/menuArrow_w.gif").getSharedResourceKey());
		this.mountSharedResource("images/24-tag-add.png", new ResourceReference(HomePage.class,
				"images/24-tag-add.png").getSharedResourceKey());
		this.mountSharedResource("images/Applet.gif", new ResourceReference(HomePage.class,
				"images/Applet.gif").getSharedResourceKey());
		this.mountSharedResource("images/bgColor.gif", new ResourceReference(HomePage.class,
				"images/bgColor.gif").getSharedResourceKey());
		this.mountSharedResource("images/buttonfind.gif", new ResourceReference(HomePage.class,
				"images/buttonfind.gif").getSharedResourceKey());
		this.mountSharedResource("images/iconDone.png", new ResourceReference(HomePage.class,
				"images/iconDone.png").getSharedResourceKey());
		this.mountSharedResource("images/icon_13.png", new ResourceReference(HomePage.class,
				"images/icon_13.png").getSharedResourceKey());
		this.mountSharedResource("images/icon_14.png", new ResourceReference(HomePage.class,
				"images/icon_14.png").getSharedResourceKey());
		this.mountSharedResource("images/blank.gif", new ResourceReference(HomePage.class,
				"images/blank.gif").getSharedResourceKey());
		this.mountSharedResource("images/battlefield.gif", new ResourceReference(HomePage.class,
				"images/battlefield.gif").getSharedResourceKey());
		this.mountSharedResource("images/exiled.gif", new ResourceReference(HomePage.class,
				"images/exiled.gif").getSharedResourceKey());
	}

	@Override
	public Session newSession(final Request request, final Response response)
	{
		return new HatchetHarrySession(request);
	}

}
