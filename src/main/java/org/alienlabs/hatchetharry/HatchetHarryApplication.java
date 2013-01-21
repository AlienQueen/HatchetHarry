package org.alienlabs.hatchetharry;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.alienlabs.hatchetharry.view.page.HomePage;
import org.apache.wicket.Application;
import org.apache.wicket.Page;
import org.apache.wicket.RuntimeConfigurationType;
import org.apache.wicket.Session;
import org.apache.wicket.atmosphere.EventBus;
import org.apache.wicket.atmosphere.ResourceRegistrationListener;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.request.Request;
import org.apache.wicket.request.Response;
import org.apache.wicket.request.resource.PackageResourceReference;
import org.apache.wicket.spring.injection.annot.SpringComponentInjector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Application object for your web application. If you want to run this
 * application without deploying, run the Start class.
 * 
 * 
 */
public class HatchetHarryApplication extends WebApplication
		implements
			Serializable,
			ResourceRegistrationListener
{
	private static final long serialVersionUID = 1L;
	private boolean mistletoeTest = false;
	EventBus eventBus;

	// Map of playerId and Atmosphere UUID
	private static Map<Long, String> cometResources = new HashMap<Long, String>();

	static final Logger LOGGER = LoggerFactory.getLogger(HomePage.class);

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

	public static HatchetHarryApplication get()
	{
		return (HatchetHarryApplication)Application.get();
	}

	@Override
	protected void init()
	{
		super.init();
		final ApplicationContext applicationContext = new ClassPathXmlApplicationContext(
				new String[] { "applicationContext.xml" });
		this.getComponentInstantiationListeners().add(
				new SpringComponentInjector(this, applicationContext));
		// this.getComponentPostOnBeforeRenderListeners().add(new
		// WicketDebugListener());

		this.eventBus = new EventBus(this);
		this.eventBus.addRegistrationListener(this);

		final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
		final Runnable beeper = new Runnable()
		{
			@Override
			public void run()
			{
				try
				{
					HatchetHarryApplication.this.eventBus.post(new Date());
				}
				catch (final Exception e)
				{
					HatchetHarryApplication.LOGGER.error("error in clock Comet channel!", e);
				}
			}
		};
		scheduler.scheduleWithFixedDelay(beeper, 2, 2, TimeUnit.SECONDS);

		// this.mount(new MountedMapperWithoutPageComponentInfo("/",
		// HomePage.class));

		this.mountResource("favicon.ico", new PackageResourceReference(HomePage.class,
				"image/favicon.ico"));
		this.mountResource("image/ajax-loader.gif", new PackageResourceReference(HomePage.class,
				"image/ajax-loader.gif"));
		this.mountResource("image/fond4.jpg", new PackageResourceReference(HomePage.class,
				"image/fond4.jpg"));

		this.mountResource("image/HammerOfBogardan.jpg", new PackageResourceReference(
				HomePage.class, "image/HammerOfBogardan.jpg"));
		this.mountResource("image/Overrun.jpg", new PackageResourceReference(HomePage.class,
				"image/Overrun.jpg"));
		this.mountResource("image/Abeyance.jpg", new PackageResourceReference(HomePage.class,
				"image/Abeyance.jpg"));
		this.mountResource("image/TradewindRider.jpg", new PackageResourceReference(HomePage.class,
				"image/TradewindRider.jpg"));
		this.mountResource("image/Necropotence.jpg", new PackageResourceReference(HomePage.class,
				"image/Necropotence.jpg"));
		this.mountResource("image/CursedScroll.jpg", new PackageResourceReference(HomePage.class,
				"image/CursedScroll.jpg"));
		this.mountResource("image/HammerOfBogardanThumb.jpg", new PackageResourceReference(
				HomePage.class, "image/HammerOfBogardanThumb.jpg"));
		this.mountResource("image/OverrunThumb.jpg", new PackageResourceReference(HomePage.class,
				"image/OverrunThumb.jpg"));
		this.mountResource("image/AbeyanceThumb.jpg", new PackageResourceReference(HomePage.class,
				"image/AbeyanceThumb.jpg"));
		this.mountResource("image/TradewindRiderThumb.jpg", new PackageResourceReference(
				HomePage.class, "image/TradewindRiderThumb.jpg"));
		this.mountResource("image/NecropotenceThumb.jpg", new PackageResourceReference(
				HomePage.class, "image/NecropotenceThumb.jpg"));
		this.mountResource("image/CursedScrollThumb.jpg", new PackageResourceReference(
				HomePage.class, "image/CursedScrollThumb.jpg"));
		this.mountResource("image/bg.png", new PackageResourceReference(HomePage.class,
				"image/bg.png"));
		this.mountResource("image/tombe1.png", new PackageResourceReference(HomePage.class,
				"image/tombe1.png"));
		this.mountResource("image/icon-uparrowsmallwhite.png", new PackageResourceReference(
				HomePage.class, "image/icon-uparrowsmallwhite.png"));
		this.mountResource("image/transpBlack.png", new PackageResourceReference(HomePage.class,
				"image/transpBlack.png"));

		this.mountResource("image/logoh2.gif", new PackageResourceReference(HomePage.class,
				"image/logoh2.gif"));
		this.mountResource("image/logoh1.gif", new PackageResourceReference(HomePage.class,
				"image/logoh1.gif"));
		this.mountResource("image/browse.png", new PackageResourceReference(HomePage.class,
				"image/browse.png"));
		this.mountResource("image/library.gif", new PackageResourceReference(HomePage.class,
				"image/library.gif"));
		this.mountResource("image/hand.gif", new PackageResourceReference(HomePage.class,
				"image/hand.gif"));
		this.mountResource("image/eMail.png", new PackageResourceReference(HomePage.class,
				"image/eMail.png"));
		this.mountResource("image/graveyard.gif", new PackageResourceReference(HomePage.class,
				"image/graveyard.gif"));
		this.mountResource("image/Safari.png", new PackageResourceReference(HomePage.class,
				"image/Safari.png"));
		this.mountResource("image/Terminal.png", new PackageResourceReference(HomePage.class,
				"image/Terminal.png"));

		this.mountResource("image/bgnd_sel_1.jpg", new PackageResourceReference(HomePage.class,
				"image/bgnd_sel_1.jpg"));
		this.mountResource("image/bgnd_sel_2.jpg", new PackageResourceReference(HomePage.class,
				"image/bgnd_sel_2.jpg"));
		this.mountResource("image/bgnd_sel_3.jpg", new PackageResourceReference(HomePage.class,
				"image/bgnd_sel_3.jpg"));
		this.mountResource("image/bgnd_sel_4.png", new PackageResourceReference(HomePage.class,
				"image/bgnd_sel_4.png"));
		this.mountResource("image/box_menu_over.png", new PackageResourceReference(HomePage.class,
				"image/box_menu_over.png"));
		this.mountResource("image/box_top.png", new PackageResourceReference(HomePage.class,
				"image/box_top.png"));
		this.mountResource("image/browser.png", new PackageResourceReference(HomePage.class,
				"image/browser.png"));
		this.mountResource("image/DV_Tools.jpg", new PackageResourceReference(HomePage.class,
				"image/DV_Tools.jpg"));
		this.mountResource("image/header_bgnd.jpg", new PackageResourceReference(HomePage.class,
				"image/header_bgnd.jpg"));
		this.mountResource("image/menuArrow.gif", new PackageResourceReference(HomePage.class,
				"image/menuArrow.gif"));
		this.mountResource("image/menuArrow_w.gif", new PackageResourceReference(HomePage.class,
				"image/menuArrow_w.gif"));
		this.mountResource("image/24-tag-add.png", new PackageResourceReference(HomePage.class,
				"image/24-tag-add.png"));
		this.mountResource("image/Applet.gif", new PackageResourceReference(HomePage.class,
				"image/Applet.gif"));
		this.mountResource("image/bgColor.gif", new PackageResourceReference(HomePage.class,
				"image/bgColor.gif"));
		this.mountResource("image/buttonfind.gif", new PackageResourceReference(HomePage.class,
				"image/buttonfind.gif"));
		this.mountResource("image/iconDone.png", new PackageResourceReference(HomePage.class,
				"image/iconDone.png"));
		this.mountResource("image/icon_13.png", new PackageResourceReference(HomePage.class,
				"image/icon_13.png"));
		this.mountResource("image/icon_14.png", new PackageResourceReference(HomePage.class,
				"image/icon_14.png"));
		this.mountResource("image/blank.gif", new PackageResourceReference(HomePage.class,
				"image/blank.gif"));
		this.mountResource("image/battlefield.gif", new PackageResourceReference(HomePage.class,
				"image/battlefield.gif"));
		this.mountResource("image/exiled.gif", new PackageResourceReference(HomePage.class,
				"image/exiled.gif"));
		this.mountResource("image/close.gif", new PackageResourceReference(HomePage.class,
				"image/close.gif"));
		this.mountResource("image/leftright.png", new PackageResourceReference(HomePage.class,
				"image/leftright.png"));
		this.mountResource("image/topbottom.png", new PackageResourceReference(HomePage.class,
				"image/topbottom.png"));
		this.mountResource("image/contact.png", new PackageResourceReference(HomePage.class,
				"image/contact.png"));
		this.mountResource("image/googlebuzz.png", new PackageResourceReference(HomePage.class,
				"image/googlebuzz.png"));
		this.mountResource("image/youtube.png", new PackageResourceReference(HomePage.class,
				"image/youtube.png"));
		this.mountResource("image/playCard.png", new PackageResourceReference(HomePage.class,
				"image/playCard.png"));
		this.mountResource("image/battle.png", new PackageResourceReference(HomePage.class,
				"image/battle.png"));
		this.mountResource("image/tweet.png", new PackageResourceReference(HomePage.class,
				"image/tweet.png"));
		this.mountResource("image/tui.png", new PackageResourceReference(HomePage.class,
				"image/tui.png"));
		this.mountResource("image/ie-spacer.gif", new PackageResourceReference(HomePage.class,
				"image/ie-spacer.gif"));
		this.mountResource("image/gritter.png", new PackageResourceReference(HomePage.class,
				"image/gritter.png"));
		this.mountResource("stylesheet/jquery.gritter.css", new PackageResourceReference(
				HomePage.class, "stylesheet/jquery.gritter.css"));
		this.mountResource("image/logobouclierrouge.png", new PackageResourceReference(
				HomePage.class, "image/logobouclierrouge.png"));
		this.mountResource("image/logobouclierviolet.png", new PackageResourceReference(
				HomePage.class, "image/logobouclierviolet.png"));

		this.mountResource("image/edit.png", new PackageResourceReference(HomePage.class,
				"image/edit.png"));
		this.mountResource("image/cut.png", new PackageResourceReference(HomePage.class,
				"image/cut.png"));
		this.mountResource("image/copy.png", new PackageResourceReference(HomePage.class,
				"image/copy.png"));
		this.mountResource("image/paste.png", new PackageResourceReference(HomePage.class,
				"image/paste.png"));
		this.mountResource("image/delete.png", new PackageResourceReference(HomePage.class,
				"image/delete.png"));
		this.mountResource("image/quit.png", new PackageResourceReference(HomePage.class,
				"image/quit.png"));

		this.getJavaScriptLibrarySettings().setJQueryReference(
				new PackageResourceReference(HomePage.class, "script/google-analytics.js"));
		this.getJavaScriptLibrarySettings().setWicketEventReference(
				new PackageResourceReference(HomePage.class, "blah.js"));
		this.getJavaScriptLibrarySettings().setWicketAjaxReference(
				new PackageResourceReference(HomePage.class, "blah.js"));
	}

	@Override
	public RuntimeConfigurationType getConfigurationType()
	{

		return RuntimeConfigurationType.DEVELOPMENT;
	}

	@Override
	public Session newSession(final Request request, final Response response)
	{
		return new HatchetHarrySession(request);
	}

	public boolean isMistletoeTest()
	{
		return this.mistletoeTest;
	}

	public void setMistletoeTest(final boolean _mistletoeTest)
	{
		this.mistletoeTest = _mistletoeTest;
	}

	public static Map<Long, String> getCometResources()
	{
		return HatchetHarryApplication.cometResources;
	}

	@Override
	public void resourceRegistered(final String uuid, final Page page)
	{
		final Long playerId = HatchetHarrySession.get().getPlayer().getId();
		HatchetHarryApplication.LOGGER.info("uuid added: " + uuid + ", for playerId: " + playerId);
		HatchetHarryApplication.cometResources.put(playerId, uuid);
	}

	@Override
	public void resourceUnregistered(final String uuid)
	{
		final Long playerId = HatchetHarrySession.get().getPlayer().getId();
		HatchetHarryApplication.LOGGER
				.info("uuid removed: " + uuid + ", for playerId: " + playerId);
		HatchetHarryApplication.cometResources.remove(playerId);
	}

}
