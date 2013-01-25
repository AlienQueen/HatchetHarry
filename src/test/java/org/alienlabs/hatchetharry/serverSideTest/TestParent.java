package org.alienlabs.hatchetharry.serverSideTest;

import org.alienlabs.hatchetharry.HatchetHarryApplication;
import org.apache.wicket.spring.injection.annot.SpringComponentInjector;
import org.apache.wicket.util.tester.WicketTester;
import org.junit.BeforeClass;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class TestParent
{
	protected static transient WicketTester tester;
	protected static HatchetHarryApplication webApp;
	protected static transient ApplicationContext context;

	@BeforeClass
	public static void setUpBeforeClass()
	{
		TestParent.webApp = new HatchetHarryApplication()
		{
			private static final long serialVersionUID = 1L;


			@Override
			public void init()
			{
				TestParent.context = new ClassPathXmlApplicationContext(
						new String[] { "applicationContext.xml" });
				this.getComponentInstantiationListeners().add(
						new SpringComponentInjector(this, TestParent.context, true));
			}
		};
		TestParent.tester = new WicketTester(TestParent.webApp);
	}
}
