package org.alienlabs.hatchetharry;

import org.alienlabs.hatchetharry.view.page.HomePage;
import org.apache.wicket.Request;
import org.apache.wicket.ResourceReference;
import org.apache.wicket.Response;
import org.apache.wicket.Session;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.spring.injection.annot.SpringComponentInjector;

import ch.qos.mistletoe.wicket.TestReportPage;

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

		this.addComponentInstantiationListener(new SpringComponentInjector(this));

		this.mountSharedResource("favicon.ico",
				new ResourceReference("image/favicon.ico").getSharedResourceKey());
		this.mountSharedResource("image/ajax-loader.gif", new ResourceReference(
				"image/ajax-loader.gif").getSharedResourceKey());

		this.mountSharedResource("image/HammerOfBogardan.jpg", new ResourceReference(
				HomePage.class, "image/HammerOfBogardan.jpg").getSharedResourceKey());
		this.mountSharedResource("image/Overrun.jpg", new ResourceReference(HomePage.class,
				"image/Overrun.jpg").getSharedResourceKey());
		this.mountSharedResource("image/Abeyance.jpg", new ResourceReference(HomePage.class,
				"image/Abeyance.jpg").getSharedResourceKey());
		this.mountSharedResource("image/TradewindRider.jpg", new ResourceReference(HomePage.class,
				"image/TradewindRider.jpg").getSharedResourceKey());
		this.mountSharedResource("image/Necropotence.jpg", new ResourceReference(HomePage.class,
				"image/Necropotence.jpg").getSharedResourceKey());
		this.mountSharedResource("image/CursedScroll.jpg", new ResourceReference(HomePage.class,
				"image/CursedScroll.jpg").getSharedResourceKey());
		this.mountSharedResource("image/HammerOfBogardanThumb.jpg", new ResourceReference(
				HomePage.class, "image/HammerOfBogardanThumb.jpg").getSharedResourceKey());
		this.mountSharedResource("image/OverrunThumb.jpg", new ResourceReference(HomePage.class,
				"image/OverrunThumb.jpg").getSharedResourceKey());
		this.mountSharedResource("image/AbeyanceThumb.jpg", new ResourceReference(HomePage.class,
				"image/AbeyanceThumb.jpg").getSharedResourceKey());
		this.mountSharedResource("image/TradewindRiderThumb.jpg", new ResourceReference(
				HomePage.class, "image/TradewindRiderThumb.jpg").getSharedResourceKey());
		this.mountSharedResource("image/NecropotenceThumb.jpg", new ResourceReference(
				HomePage.class, "image/NecropotenceThumb.jpg").getSharedResourceKey());
		this.mountSharedResource("image/CursedScrollThumb.jpg", new ResourceReference(
				HomePage.class, "image/CursedScrollThumb.jpg").getSharedResourceKey());
		this.mountSharedResource("image/bg.png", new ResourceReference(HomePage.class,
				"image/bg.png").getSharedResourceKey());
		this.mountSharedResource("image/icon-uparrowsmallwhite.png", new ResourceReference(
				HomePage.class, "image/icon-uparrowsmallwhite.png").getSharedResourceKey());
		this.mountSharedResource("image/transpBlack.png", new ResourceReference(HomePage.class,
				"image/transpBlack.png").getSharedResourceKey());

		this.mountSharedResource("image/logoh2.gif", new ResourceReference(HomePage.class,
				"image/logoh2.gif").getSharedResourceKey());
		this.mountSharedResource("image/library.gif", new ResourceReference(HomePage.class,
				"image/library.gif").getSharedResourceKey());
		this.mountSharedResource("image/hand.gif", new ResourceReference(HomePage.class,
				"image/hand.gif").getSharedResourceKey());
		this.mountSharedResource("image/eMail.png", new ResourceReference(HomePage.class,
				"image/eMail.png").getSharedResourceKey());
		this.mountSharedResource("image/graveyard.gif", new ResourceReference(HomePage.class,
				"image/graveyard.gif").getSharedResourceKey());
		this.mountSharedResource("image/Safari.png", new ResourceReference(HomePage.class,
				"image/Safari.png").getSharedResourceKey());
		this.mountSharedResource("image/Terminal.png", new ResourceReference(HomePage.class,
				"image/Terminal.png").getSharedResourceKey());
		this.mountSharedResource("image/gradient1.jpg", new ResourceReference(HomePage.class,
				"image/gradient1.jpg").getSharedResourceKey());

		this.mountSharedResource("image/bgnd_sel_1.jpg", new ResourceReference(HomePage.class,
				"image/bgnd_sel_1.jpg").getSharedResourceKey());
		this.mountSharedResource("image/bgnd_sel_2.jpg", new ResourceReference(HomePage.class,
				"image/bgnd_sel_2.jpg").getSharedResourceKey());
		this.mountSharedResource("image/bgnd_sel_3.jpg", new ResourceReference(HomePage.class,
				"image/bgnd_sel_3.jpg").getSharedResourceKey());
		this.mountSharedResource("image/bgnd_sel_4.png", new ResourceReference(HomePage.class,
				"image/bgnd_sel_4.png").getSharedResourceKey());
		this.mountSharedResource("image/box_menu_over.png", new ResourceReference(HomePage.class,
				"image/box_menu_over.png").getSharedResourceKey());
		this.mountSharedResource("image/box_top.png", new ResourceReference(HomePage.class,
				"image/box_top.png").getSharedResourceKey());
		this.mountSharedResource("image/browser.png", new ResourceReference(HomePage.class,
				"image/browser.png").getSharedResourceKey());
		this.mountSharedResource("image/DV_Tools.jpg", new ResourceReference(HomePage.class,
				"image/DV_Tools.jpg").getSharedResourceKey());
		this.mountSharedResource("image/header_bgnd.jpg", new ResourceReference(HomePage.class,
				"image/header_bgnd.jpg").getSharedResourceKey());
		this.mountSharedResource("image/menuArrow.gif", new ResourceReference(HomePage.class,
				"image/menuArrow.gif").getSharedResourceKey());
		this.mountSharedResource("image/menuArrow_w.gif", new ResourceReference(HomePage.class,
				"image/menuArrow_w.gif").getSharedResourceKey());
		this.mountSharedResource("image/24-tag-add.png", new ResourceReference(HomePage.class,
				"image/24-tag-add.png").getSharedResourceKey());
		this.mountSharedResource("image/Applet.gif", new ResourceReference(HomePage.class,
				"image/Applet.gif").getSharedResourceKey());
		this.mountSharedResource("image/bgColor.gif", new ResourceReference(HomePage.class,
				"image/bgColor.gif").getSharedResourceKey());
		this.mountSharedResource("image/buttonfind.gif", new ResourceReference(HomePage.class,
				"image/buttonfind.gif").getSharedResourceKey());
		this.mountSharedResource("image/iconDone.png", new ResourceReference(HomePage.class,
				"image/iconDone.png").getSharedResourceKey());
		this.mountSharedResource("image/icon_13.png", new ResourceReference(HomePage.class,
				"image/icon_13.png").getSharedResourceKey());
		this.mountSharedResource("image/icon_14.png", new ResourceReference(HomePage.class,
				"image/icon_14.png").getSharedResourceKey());
		this.mountSharedResource("image/blank.gif", new ResourceReference(HomePage.class,
				"image/blank.gif").getSharedResourceKey());
		this.mountSharedResource("image/battlefield.gif", new ResourceReference(HomePage.class,
				"image/battlefield.gif").getSharedResourceKey());
		this.mountSharedResource("image/exiled.gif", new ResourceReference(HomePage.class,
				"image/exiled.gif").getSharedResourceKey());
		this.mountSharedResource("image/close.gif", new ResourceReference(HomePage.class,
				"image/close.gif").getSharedResourceKey());
		this.mountSharedResource("image/leftright.png", new ResourceReference(HomePage.class,
				"image/leftright.png").getSharedResourceKey());
		this.mountSharedResource("image/topbottom.png", new ResourceReference(HomePage.class,
				"image/topbottom.png").getSharedResourceKey());
		this.mountSharedResource("image/contact.png", new ResourceReference(HomePage.class,
				"image/contact.png").getSharedResourceKey());
		this.mountSharedResource("image/googlebuzz.png", new ResourceReference(HomePage.class,
				"image/googlebuzz.png").getSharedResourceKey());
		this.mountSharedResource("image/youtube.png", new ResourceReference(HomePage.class,
				"image/youtube.png").getSharedResourceKey());
		this.mountSharedResource("image/playCard.png", new ResourceReference(HomePage.class,
				"image/playCard.png").getSharedResourceKey());
		this.mountSharedResource("image/battle.png", new ResourceReference(HomePage.class,
				"image/battle.png").getSharedResourceKey());
		this.mountSharedResource("image/tweet.png", new ResourceReference(HomePage.class,
				"image/tweet.png").getSharedResourceKey());
		this.mountSharedResource("image/tui.png", new ResourceReference(HomePage.class,
				"image/tui.png").getSharedResourceKey());
	}

	@Override
	public Session newSession(final Request request, final Response response)
	{
		return new HatchetHarrySession(request);
	}

}
