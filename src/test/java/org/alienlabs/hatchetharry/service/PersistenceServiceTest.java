package org.alienlabs.hatchetharry.service;

import javax.sql.DataSource;

import org.alienlabs.hatchetharry.HatchetHarryApplication;
import org.alienlabs.hatchetharry.view.page.HomePage;
import org.apache.wicket.spring.injection.annot.SpringComponentInjector;
import org.apache.wicket.util.tester.WicketTester;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.Operations;
import com.ninja_squad.dbsetup.destination.DataSourceDestination;
import com.ninja_squad.dbsetup.operation.Operation;

public class PersistenceServiceTest
{
	public static final ClassPathXmlApplicationContext CLASS_PATH_XML_APPLICATION_CONTEXT = new ClassPathXmlApplicationContext(
			new String[] { "applicationContext.xml", "applicationContextTest.xml" });

	protected static transient WicketTester tester;
	protected static HatchetHarryApplication webApp;
	public static transient ApplicationContext context;
	protected static String pageDocument;

	public static final Operation INSERT_REFERENCE_DATA = Operations.sequenceOf(Operations
			.insertInto("Game").columns("currentPlaceholderId", "isAcceptEndOfTurnPending")
			.values(1L, null).values(2L, null).build());

	@BeforeClass
	public static void setUpBeforeClass()
	{
		PersistenceServiceTest.webApp = new HatchetHarryApplication()
		{
			private static final long serialVersionUID = 1L;


			@Override
			public void init()
			{
				PersistenceServiceTest.context = PersistenceServiceTest.CLASS_PATH_XML_APPLICATION_CONTEXT;
				this.getComponentInstantiationListeners().add(
						new SpringComponentInjector(this, PersistenceServiceTest.context, true));
				// We'll ask Emond to enable unit testing in EventBus
				// this.eventBus = new EventBusMock(this);
			}
		};

		PersistenceServiceTest.tester = new WicketTester(PersistenceServiceTest.webApp);

		// start and render the test page
		PersistenceServiceTest.tester.startPage(HomePage.class);

		// assert rendered page class
		PersistenceServiceTest.tester.assertRenderedPage(HomePage.class);

		PersistenceServiceTest.pageDocument = PersistenceServiceTest.tester.getLastResponse()
				.getDocument();
	}

	@Before
	public void before() throws Exception
	{
		// DB-Setup from NinjaSquad
		final Operation operation = Operations.sequenceOf(
				PersistenceServiceTest.INSERT_REFERENCE_DATA,
				Operations.insertInto("Game")
						.columns("currentPlaceholderId", "isAcceptEndOfTurnPending")
						.values(3L, null).values(4L, null).build());

		final DataSource ds = (DataSource)PersistenceServiceTest.context.getBean("dataSource");
		final DbSetup dbSetup = new DbSetup(new DataSourceDestination(ds), operation);
		dbSetup.launch();
	}

	@Test
	public void testBlah()
	{
		Assert.assertTrue(true);
	}

}
