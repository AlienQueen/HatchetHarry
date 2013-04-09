package org.alienlabs.hatchetharry.serverSideTest;

import java.util.List;

import org.alienlabs.hatchetharry.HatchetHarryApplication;
import org.alienlabs.hatchetharry.service.PersistenceService;
import org.alienlabs.hatchetharry.view.component.HandComponent;
import org.alienlabs.hatchetharry.view.page.HomePage;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.spring.injection.annot.SpringComponentInjector;
import org.apache.wicket.util.tester.TagTester;
import org.apache.wicket.util.tester.WicketTester;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Simple test using the WicketTester
 */
public class NonRegressionTest
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
		NonRegressionTest.webApp = new HatchetHarryApplication()
		{
			private static final long serialVersionUID = 1L;


			@Override
			public void init()
			{
				NonRegressionTest.context = NonRegressionTest.CLASS_PATH_XML_APPLICATION_CONTEXT;
				this.getComponentInstantiationListeners().add(
						new SpringComponentInjector(this, NonRegressionTest.context, true));
				NonRegressionTest.context.getBean(PersistenceService.class).resetDb();
			}
		};

		NonRegressionTest.tester = new WicketTester(NonRegressionTest.webApp);

		// start and render the test page
		NonRegressionTest.tester.startPage(HomePage.class);

		// assert rendered page class
		NonRegressionTest.tester.assertRenderedPage(HomePage.class);

		NonRegressionTest.pageDocument = NonRegressionTest.tester.getLastResponse().getDocument();
	}

	@AfterClass
	public static void tearDownAfterClass()
	{
		NonRegressionTest.context.getBean(PersistenceService.class).resetDb();
	}

	@Test
	public void testDrawingACardShouldRaiseTheNumberOfCardsInHandToHeight()
	{
		// Init
		// assert hand is present
		NonRegressionTest.tester.assertComponent("galleryParent:gallery", HandComponent.class);

		// assert URL of a thumbnail
		List<TagTester> tagTester = TagTester.createTagsByAttribute(NonRegressionTest.pageDocument,
				"class", "nav-thumb", false);
		Assert.assertNotNull(tagTester);

		// assert number of thumbnails
		Assert.assertEquals(7, tagTester.size());

		Assert.assertNotNull(tagTester.get(0).getAttribute("src"));
		Assert.assertTrue(tagTester.get(0).getAttribute("src").contains(".jpg"));

		// Run
		NonRegressionTest.tester.assertComponent("drawCardLink", AjaxLink.class);
		NonRegressionTest.tester.clickLink("drawCardLink", true);

		// Verify
		NonRegressionTest.pageDocument = NonRegressionTest.tester.getLastResponse().getDocument();
		final String header = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><ajax-response><component id=\"galleryParent\" ><![CDATA[<span id=\"galleryParent\">";
		final int toRemoveHeader = NonRegressionTest.pageDocument.indexOf(header);
		NonRegressionTest.pageDocument = NonRegressionTest.pageDocument.substring(toRemoveHeader
				+ header.length());
		final int toRemoveFooter = NonRegressionTest.pageDocument.indexOf("]]></component>");
		NonRegressionTest.pageDocument = NonRegressionTest.pageDocument
				.substring(0, toRemoveFooter);

		tagTester = TagTester.createTagsByAttribute(NonRegressionTest.pageDocument, "class",
				"nav-thumb", false);
		Assert.assertNotNull(tagTester);

		// assert number of thumbnails
		Assert.assertEquals(8, tagTester.size());

		Assert.assertNotNull(tagTester.get(3).getAttribute("src"));
		Assert.assertTrue(tagTester.get(3).getAttribute("src").contains(".jpg"));
	}

}
