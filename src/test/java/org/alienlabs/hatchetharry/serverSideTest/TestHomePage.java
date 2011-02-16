package org.alienlabs.hatchetharry.serverSideTest;

import java.util.ArrayList;
import java.util.List;

import org.alienlabs.hatchetharry.HatchetHarryApplication;
import org.alienlabs.hatchetharry.view.CardPanel;
import org.alienlabs.hatchetharry.view.HomePage;
import org.apache.wicket.Component.IVisitor;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.spring.injection.annot.SpringComponentInjector;
import org.apache.wicket.util.tester.TagTester;
import org.apache.wicket.util.tester.WicketTester;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.googlecode.wicketslides.SlidesPanel;

/**
 * Simple test using the WicketTester
 */
public class TestHomePage
{

	private static WicketTester tester;
	private static HatchetHarryApplication webApp;
	List<Image> img;

	@BeforeClass
	public static void setUp()
	{
		TestHomePage.webApp = new HatchetHarryApplication()
		{
			// note in this case the application context is in the default
			// package
			ApplicationContext context = new ClassPathXmlApplicationContext(
					new String[] { "applicationContext.xml" });

			@Override
			public void init()
			{
				this.addComponentInstantiationListener(new SpringComponentInjector(this,
						this.context, true));
			}
		};
		// WicketTester app=new WicketTester();
		TestHomePage.tester = new WicketTester(TestHomePage.webApp);
	}


	@Test
	public void testRenderMyPage()
	{
		// start and render the test page
		TestHomePage.tester.startPage(HomePage.class);

		// assert rendered page class
		TestHomePage.tester.assertRenderedPage(HomePage.class);

		// assert rendered label component
		TestHomePage.tester.assertComponent("message", Label.class);

		final Label message = (Label)TestHomePage.tester
				.getComponentFromLastRenderedPage("message");
		Assert.assertTrue(message.getDefaultModelObjectAsString().startsWith(
				"version 0.0.1 built on "));

		// assert hand is present
		TestHomePage.tester.assertComponent("gallery", SlidesPanel.class);

		final SlidesPanel gallery = (SlidesPanel)TestHomePage.tester
				.getComponentFromLastRenderedPage("gallery");

		this.img = new ArrayList<Image>();
		@SuppressWarnings("unchecked")
		final List<Image> images = (List<Image>)gallery.visitChildren(Image.class,
				new IVisitor<Image>()
				{
					@Override
					public List<Image> component(final Image component)
					{
						TestHomePage.this.img.add(component);
						return TestHomePage.this.img;
					}
				});

		// assert 1 images
		Assert.assertNotNull(images.get(0));
		Assert.assertEquals(1, images.size());

		// assert URL of a thumbnail
		TagTester tagTester = TagTester.createTagByAttribute(TestHomePage.tester
				.getServletResponse().getDocument(), "class", "thumbnail");
		Assert.assertNotNull(tagTester);
		Assert.assertNotNull(tagTester.getAttribute("src"));
		Assert.assertTrue(tagTester.getAttribute("src").contains("HammerOfBogardan.jpg"));

		// assert URL of an image
		tagTester = TagTester.createTagByAttribute(TestHomePage.tester.getServletResponse()
				.getDocument(), "class", "full");
		Assert.assertNotNull(tagTester);
		Assert.assertNotNull(tagTester.getAttribute("src"));
		Assert.assertTrue(tagTester.getAttribute("src").contains("HammerOfBogardan.jpg"));
	}

	@Test
	public void testRenderComponents()
	{
		// start and render the test page
		TestHomePage.tester.startPage(HomePage.class);

		// assert rendered page class
		TestHomePage.tester.assertRenderedPage(HomePage.class);

		// assert rendered label component
		TestHomePage.tester.assertComponent("baldu", CardPanel.class);
	}
}