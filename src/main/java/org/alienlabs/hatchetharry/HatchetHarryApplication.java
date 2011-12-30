package org.alienlabs.hatchetharry;

import org.alienlabs.hatchetharry.model.Player;
import org.alienlabs.hatchetharry.view.page.HomePage;
import org.apache.wicket.Request;
import org.apache.wicket.Response;
import org.apache.wicket.Session;
import org.apache.wicket.markup.html.resources.CompressedResourceReference;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.spring.injection.annot.SpringComponentInjector;

/**
 * Application object for your web application. If you want to run this
 * application without deploying, run the Start class.
 * 
 * 
 */
public class HatchetHarryApplication extends WebApplication
{
	private Player player;

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
	public Class<HomePage> getHomePage()
	{
		return HomePage.class;
	}

	@Override
	protected void init()
	{
		super.init();

		this.addComponentInstantiationListener(new SpringComponentInjector(this));

		this.mountSharedResource("favicon.ico", new CompressedResourceReference(HomePage.class,
				"image/favicon.ico").getSharedResourceKey());
		this.mountSharedResource("image/ajax-loader.gif", new CompressedResourceReference(
				HomePage.class, "image/ajax-loader.gif").getSharedResourceKey());

		this.mountSharedResource("image/HammerOfBogardan.jpg", new CompressedResourceReference(
				HomePage.class, "image/HammerOfBogardan.jpg").getSharedResourceKey());
		this.mountSharedResource("image/Overrun.jpg", new CompressedResourceReference(
				HomePage.class, "image/Overrun.jpg").getSharedResourceKey());
		this.mountSharedResource("image/Abeyance.jpg", new CompressedResourceReference(
				HomePage.class, "image/Abeyance.jpg").getSharedResourceKey());
		this.mountSharedResource("image/TradewindRider.jpg", new CompressedResourceReference(
				HomePage.class, "image/TradewindRider.jpg").getSharedResourceKey());
		this.mountSharedResource("image/Necropotence.jpg", new CompressedResourceReference(
				HomePage.class, "image/Necropotence.jpg").getSharedResourceKey());
		this.mountSharedResource("image/CursedScroll.jpg", new CompressedResourceReference(
				HomePage.class, "image/CursedScroll.jpg").getSharedResourceKey());
		this.mountSharedResource("image/HammerOfBogardanThumb.jpg",
				new CompressedResourceReference(HomePage.class, "image/HammerOfBogardanThumb.jpg")
						.getSharedResourceKey());
		this.mountSharedResource("image/OverrunThumb.jpg", new CompressedResourceReference(
				HomePage.class, "image/OverrunThumb.jpg").getSharedResourceKey());
		this.mountSharedResource("image/AbeyanceThumb.jpg", new CompressedResourceReference(
				HomePage.class, "image/AbeyanceThumb.jpg").getSharedResourceKey());
		this.mountSharedResource("image/TradewindRiderThumb.jpg", new CompressedResourceReference(
				HomePage.class, "image/TradewindRiderThumb.jpg").getSharedResourceKey());
		this.mountSharedResource("image/NecropotenceThumb.jpg", new CompressedResourceReference(
				HomePage.class, "image/NecropotenceThumb.jpg").getSharedResourceKey());
		this.mountSharedResource("image/CursedScrollThumb.jpg", new CompressedResourceReference(
				HomePage.class, "image/CursedScrollThumb.jpg").getSharedResourceKey());
		this.mountSharedResource("image/bg.png", new CompressedResourceReference(HomePage.class,
				"image/bg.png").getSharedResourceKey());
		this.mountSharedResource("image/icon-uparrowsmallwhite.png",
				new CompressedResourceReference(HomePage.class, "image/icon-uparrowsmallwhite.png")
						.getSharedResourceKey());
		this.mountSharedResource("image/transpBlack.png", new CompressedResourceReference(
				HomePage.class, "image/transpBlack.png").getSharedResourceKey());

		this.mountSharedResource("image/logoh2.gif", new CompressedResourceReference(
				HomePage.class, "image/logoh2.gif").getSharedResourceKey());
		this.mountSharedResource("image/library.gif", new CompressedResourceReference(
				HomePage.class, "image/library.gif").getSharedResourceKey());
		this.mountSharedResource("image/hand.gif", new CompressedResourceReference(HomePage.class,
				"image/hand.gif").getSharedResourceKey());
		this.mountSharedResource("image/eMail.png", new CompressedResourceReference(HomePage.class,
				"image/eMail.png").getSharedResourceKey());
		this.mountSharedResource("image/graveyard.gif", new CompressedResourceReference(
				HomePage.class, "image/graveyard.gif").getSharedResourceKey());
		this.mountSharedResource("image/Safari.png", new CompressedResourceReference(
				HomePage.class, "image/Safari.png").getSharedResourceKey());
		this.mountSharedResource("image/Terminal.png", new CompressedResourceReference(
				HomePage.class, "image/Terminal.png").getSharedResourceKey());
		this.mountSharedResource("image/gradient1.jpg", new CompressedResourceReference(
				HomePage.class, "image/gradient1.jpg").getSharedResourceKey());

		this.mountSharedResource("image/bgnd_sel_1.jpg", new CompressedResourceReference(
				HomePage.class, "image/bgnd_sel_1.jpg").getSharedResourceKey());
		this.mountSharedResource("image/bgnd_sel_2.jpg", new CompressedResourceReference(
				HomePage.class, "image/bgnd_sel_2.jpg").getSharedResourceKey());
		this.mountSharedResource("image/bgnd_sel_3.jpg", new CompressedResourceReference(
				HomePage.class, "image/bgnd_sel_3.jpg").getSharedResourceKey());
		this.mountSharedResource("image/bgnd_sel_4.png", new CompressedResourceReference(
				HomePage.class, "image/bgnd_sel_4.png").getSharedResourceKey());
		this.mountSharedResource("image/box_menu_over.png", new CompressedResourceReference(
				HomePage.class, "image/box_menu_over.png").getSharedResourceKey());
		this.mountSharedResource("image/box_top.png", new CompressedResourceReference(
				HomePage.class, "image/box_top.png").getSharedResourceKey());
		this.mountSharedResource("image/browser.png", new CompressedResourceReference(
				HomePage.class, "image/browser.png").getSharedResourceKey());
		this.mountSharedResource("image/DV_Tools.jpg", new CompressedResourceReference(
				HomePage.class, "image/DV_Tools.jpg").getSharedResourceKey());
		this.mountSharedResource("image/header_bgnd.jpg", new CompressedResourceReference(
				HomePage.class, "image/header_bgnd.jpg").getSharedResourceKey());
		this.mountSharedResource("image/menuArrow.gif", new CompressedResourceReference(
				HomePage.class, "image/menuArrow.gif").getSharedResourceKey());
		this.mountSharedResource("image/menuArrow_w.gif", new CompressedResourceReference(
				HomePage.class, "image/menuArrow_w.gif").getSharedResourceKey());
		this.mountSharedResource("image/24-tag-add.png", new CompressedResourceReference(
				HomePage.class, "image/24-tag-add.png").getSharedResourceKey());
		this.mountSharedResource("image/Applet.gif", new CompressedResourceReference(
				HomePage.class, "image/Applet.gif").getSharedResourceKey());
		this.mountSharedResource("image/bgColor.gif", new CompressedResourceReference(
				HomePage.class, "image/bgColor.gif").getSharedResourceKey());
		this.mountSharedResource("image/buttonfind.gif", new CompressedResourceReference(
				HomePage.class, "image/buttonfind.gif").getSharedResourceKey());
		this.mountSharedResource("image/iconDone.png", new CompressedResourceReference(
				HomePage.class, "image/iconDone.png").getSharedResourceKey());
		this.mountSharedResource("image/icon_13.png", new CompressedResourceReference(
				HomePage.class, "image/icon_13.png").getSharedResourceKey());
		this.mountSharedResource("image/icon_14.png", new CompressedResourceReference(
				HomePage.class, "image/icon_14.png").getSharedResourceKey());
		this.mountSharedResource("image/blank.gif", new CompressedResourceReference(HomePage.class,
				"image/blank.gif").getSharedResourceKey());
		this.mountSharedResource("image/battlefield.gif", new CompressedResourceReference(
				HomePage.class, "image/battlefield.gif").getSharedResourceKey());
		this.mountSharedResource("image/exiled.gif", new CompressedResourceReference(
				HomePage.class, "image/exiled.gif").getSharedResourceKey());
		this.mountSharedResource("image/close.gif", new CompressedResourceReference(HomePage.class,
				"image/close.gif").getSharedResourceKey());
		this.mountSharedResource("image/leftright.png", new CompressedResourceReference(
				HomePage.class, "image/leftright.png").getSharedResourceKey());
		this.mountSharedResource("image/topbottom.png", new CompressedResourceReference(
				HomePage.class, "image/topbottom.png").getSharedResourceKey());
		this.mountSharedResource("image/contact.png", new CompressedResourceReference(
				HomePage.class, "image/contact.png").getSharedResourceKey());
		this.mountSharedResource("image/googlebuzz.png", new CompressedResourceReference(
				HomePage.class, "image/googlebuzz.png").getSharedResourceKey());
		this.mountSharedResource("image/youtube.png", new CompressedResourceReference(
				HomePage.class, "image/youtube.png").getSharedResourceKey());
		this.mountSharedResource("image/playCard.png", new CompressedResourceReference(
				HomePage.class, "image/playCard.png").getSharedResourceKey());
		this.mountSharedResource("image/battle.png", new CompressedResourceReference(
				HomePage.class, "image/battle.png").getSharedResourceKey());
		this.mountSharedResource("image/tweet.png", new CompressedResourceReference(HomePage.class,
				"image/tweet.png").getSharedResourceKey());
		this.mountSharedResource("image/tui.png", new CompressedResourceReference(HomePage.class,
				"image/tui.png").getSharedResourceKey());
		this.mountSharedResource("image/ie-spacer.gif", new CompressedResourceReference(
				HomePage.class, "image/ie-spacer.gif").getSharedResourceKey());
		this.mountSharedResource("image/gritter.png", new CompressedResourceReference(
				HomePage.class, "image/gritter.png").getSharedResourceKey());
		this.mountSharedResource("stylesheet/jquery.gritter.css", new CompressedResourceReference(
				HomePage.class, "stylesheet/jquery.gritter.css").getSharedResourceKey());
		this.mountSharedResource("image/logobouclierrouge.png", new CompressedResourceReference(
				HomePage.class, "image/logobouclierrouge.png").getSharedResourceKey());
		this.mountSharedResource("image/logobouclierviolet.png", new CompressedResourceReference(
				HomePage.class, "image/logobouclierviolet.png").getSharedResourceKey());
	}

	@Override
	public Session newSession(final Request request, final Response response)
	{
		return new HatchetHarrySession(request);
	}

	public Player getPlayer()
	{
		return this.player;
	}

	public void setPlayer(final Player _player)
	{
		this.player = _player;
	}

}
