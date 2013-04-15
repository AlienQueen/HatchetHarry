package org.alienlabs.hatchetharry.serverSideTest.util;

import org.alienlabs.hatchetharry.HatchetHarryApplication;
import org.alienlabs.hatchetharry.service.PersistenceService;
import org.alienlabs.hatchetharry.view.page.HomePage;
import org.apache.wicket.spring.injection.annot.SpringComponentInjector;
import org.apache.wicket.util.tester.WicketTester;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class SpringContextLoaderBaseTest
{
	static final ClassPathXmlApplicationContext CLASS_PATH_XML_APPLICATION_CONTEXT = new ClassPathXmlApplicationContext(
			new String[] { "applicationContext.xml" });
	protected static transient WicketTester tester;
	protected static HatchetHarryApplication webApp;
	protected static transient ApplicationContext context;
	protected static String pageDocument;

	@BeforeClass
	public static void setUpBeforeClass()
	{
		SpringContextLoaderBaseTest.webApp = new HatchetHarryApplication()
		{
			private static final long serialVersionUID = 1L;


			@Override
			public void init()
			{
				SpringContextLoaderBaseTest.context = SpringContextLoaderBaseTest.CLASS_PATH_XML_APPLICATION_CONTEXT;
				this.getComponentInstantiationListeners()
						.add(new SpringComponentInjector(this, SpringContextLoaderBaseTest.context,
								true));
				this.eventBus = new EventBusMock(this);

				SpringContextLoaderBaseTest.context.getBean(PersistenceService.class).resetDb();
			}
		};

		SpringContextLoaderBaseTest.tester = new WicketTester(SpringContextLoaderBaseTest.webApp);

		// start and render the test page
		SpringContextLoaderBaseTest.tester.startPage(HomePage.class);

		// assert rendered page class
		SpringContextLoaderBaseTest.tester.assertRenderedPage(HomePage.class);

		SpringContextLoaderBaseTest.pageDocument = SpringContextLoaderBaseTest.tester
				.getLastResponse().getDocument();
	}

	@AfterClass
	public static void tearDownAfterClass()
	{
		SpringContextLoaderBaseTest.context.getBean(PersistenceService.class).resetDb();
	}

}
