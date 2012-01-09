package org.alienlabs.hatchetharry.serverSideTest;

import java.util.ArrayList;
import java.util.List;

import org.alienlabs.hatchetharry.HatchetHarryApplication;
import org.alienlabs.hatchetharry.view.component.CardPanel;
import org.alienlabs.hatchetharry.view.component.ChatPanel;
import org.alienlabs.hatchetharry.view.component.ClockPanel;
import org.alienlabs.hatchetharry.view.component.DataBox;
import org.alienlabs.hatchetharry.view.component.HandComponent;
import org.alienlabs.hatchetharry.view.page.HomePage;
import org.apache.wicket.Component.IVisitor;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.behavior.AbstractAjaxBehavior;
import org.apache.wicket.behavior.IBehavior;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.spring.injection.annot.SpringComponentInjector;
import org.apache.wicket.util.tester.TagTester;
import org.apache.wicket.util.tester.WicketTester;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Simple test using the WicketTester
 */
public class HomePageTest
{
	private static WicketTester tester;
	private static HatchetHarryApplication webApp;

	@Before
	public void setUp()
	{
		HomePageTest.webApp = new HatchetHarryApplication()
		{
			private static final long serialVersionUID = 1L;
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

		HomePageTest.tester = new WicketTester(HomePageTest.webApp);
		final ApplicationContext context = new ClassPathXmlApplicationContext(
				new String[] { "applicationContext.xml" });
		HomePageTest.tester.getApplication().addComponentInstantiationListener(
				new SpringComponentInjector(HomePageTest.tester.getApplication(), context, true));
	}

	@Test
	public void testRenderMyPage()
	{
		// start and render the test page
		HomePageTest.tester.startPage(HomePage.class);

		// assert rendered page class
		HomePageTest.tester.assertRenderedPage(HomePage.class);

		// assert rendered label component
		final Label message = (Label)HomePageTest.tester
				.getComponentFromLastRenderedPage("message");
		Assert.assertTrue(message.getDefaultModelObjectAsString().contains("version"));
		Assert.assertTrue(message.getDefaultModelObjectAsString().contains("release"));

	}

	@Test
	public void testRenderHand()
	{
		// start and render the test page
		HomePageTest.tester.startPage(HomePage.class);

		// assert rendered page class
		HomePageTest.tester.assertRenderedPage(HomePage.class);

		// assert hand is present
		HomePageTest.tester.assertComponent("handCardsPlaceholder:gallery", HandComponent.class);

		// assert hand content
		final HandComponent gallery = (HandComponent)HomePageTest.tester
				.getComponentFromLastRenderedPage("handCardsPlaceholder:gallery");

		final List<Image> img = new ArrayList<Image>();
		@SuppressWarnings("unchecked")
		final List<Image> images = (List<Image>)gallery.visitChildren(Image.class,
				new IVisitor<Image>()
				{
					@Override
					public List<Image> component(final Image component)
					{
						img.add(component);
						return img;
					}
				});

		// assert 1 image
		Assert.assertNotNull(images.get(0));
		Assert.assertEquals(1, images.size());

		// assert URL of a thumbnail
		final List<TagTester> tagTester = TagTester.createTagsByAttribute(HomePageTest.tester
				.getServletResponse().getDocument(), "class", "nav-thumb", false);
		Assert.assertNotNull(tagTester);
		Assert.assertNotNull(tagTester.get(0).getAttribute("src"));
		Assert.assertTrue(tagTester.get(0).getAttribute("src").contains(".jpg"));

		// assert number of thumbnails
		Assert.assertEquals(7, tagTester.size());
	}

	@Test
	public void testRenderClock()
	{
		// start and render the test page
		HomePageTest.tester.startPage(HomePage.class);

		// assert rendered page class
		HomePageTest.tester.assertRenderedPage(HomePage.class);

		// assert clock is present
		HomePageTest.tester.assertComponent("clockPanel", ClockPanel.class);

		// assert clock content
		final ClockPanel clock = (ClockPanel)HomePageTest.tester
				.getComponentFromLastRenderedPage("clockPanel");
		System.out.println("###" + clock.getTime().getObject());
		Assert.assertTrue(clock.getTime().getObject().contains("###"));
	}

	@Test
	public void testRenderMenuBar()
	{
		// start and render the test page
		HomePageTest.tester.startPage(HomePage.class);

		// assert rendered page class
		HomePageTest.tester.assertRenderedPage(HomePage.class);

		// Assert menubar
		List<TagTester> tagTester = TagTester.createTagsByAttribute(HomePageTest.tester
				.getServletResponse().getDocument(), "class", "rootVoices", false);
		Assert.assertNotNull(tagTester);
		Assert.assertEquals(1, tagTester.size());

		// Assert menu entries
		tagTester = TagTester.createTagsByAttribute(HomePageTest.tester.getServletResponse()
				.getDocument(), "class", "mbmenu", false);
		Assert.assertNotNull(tagTester);
		Assert.assertTrue(tagTester.size() > 1);

		// Assert 'the game wins' entry exists
		boolean containsText = false;
		for (final TagTester tt : tagTester)
		{
			if (((null != tt.getMarkup()) && tt.getMarkup().contains("The game wins")))
			{
				containsText = true;
				break;
			}
		}
		Assert.assertTrue(containsText);
	}

	@Test
	public void testRenderDataBox()
	{
		// start and render the test page
		HomePageTest.tester.startPage(HomePage.class);

		// assert rendered page class
		HomePageTest.tester.assertRenderedPage(HomePage.class);

		// assert DataBox is present
		HomePageTest.tester.assertComponent("dataBoxParent:dataBox", DataBox.class);

		// assert DataBox content
		HomePageTest.tester.assertLabel("dataBoxParent:dataBox:box:0:playerLifePoints",
				"20 life points");
	}

	@Test
	public void testRenderChat()
	{
		// start and render the test page
		HomePageTest.tester.startPage(HomePage.class);

		// assert rendered page class
		HomePageTest.tester.assertRenderedPage(HomePage.class);

		// assert chat is present
		HomePageTest.tester.assertComponent("chatPanel", ChatPanel.class);
		HomePageTest.tester.assertComponent("chatPanel:chatForm:user", RequiredTextField.class);
		HomePageTest.tester.assertComponent("chatPanel:chatForm:message", RequiredTextField.class);
	}

	@Test
	public void testRenderDock()
	{
		// start and render the test page
		HomePageTest.tester.startPage(HomePage.class);

		// assert rendered page class
		HomePageTest.tester.assertRenderedPage(HomePage.class);

		// Assert hand
		this.testDockElement("Hand");

		// Assert graveyard
		this.testDockElement("Graveyard");

		// Assert exiled
		this.testDockElement("Exiled");

		// Assert battlefield
		this.testDockElement("Battlefield");

		// Assert library
		this.testDockElement("Library");
	}

	// Assert dock element is present and contains a .gif
	private void testDockElement(final String name)
	{
		final List<TagTester> tagTester = TagTester.createTagsByAttribute(HomePageTest.tester
				.getServletResponse().getDocument(), "title", name, false);
		Assert.assertNotNull(tagTester);
		Assert.assertEquals(1, tagTester.size());

		final TagTester tt = tagTester.get(0);
		Assert.assertNotNull(tt);
		final TagTester tt2 = tt.getChild("alt", name);
		Assert.assertNotNull(tt2);
		Assert.assertFalse(tt.equals(tt2));
		Assert.assertNotNull(tt2.getAttribute("src"));
		Assert.assertTrue(tt2.getAttribute("src").contains(".gif"));
	}

	@Test
	public void testRenderModalWindows()
	{
		// start and render the test page
		HomePageTest.tester.startPage(HomePage.class);

		// assert rendered page class
		HomePageTest.tester.assertRenderedPage(HomePage.class);

		this.testModalWindow("aboutWindow", "aboutLink");
		this.testModalWindow("teamInfoWindow", "teamInfoLink");
		this.testModalWindow("createGameWindow", "createGameLink");
		this.testModalWindow("joinGameWindow", "joinGameLink");
	}

	public void testModalWindow(final String _window, final String linkToActivateWindow)
	{
		// assert about modal window is in the page
		HomePageTest.tester.assertComponent(_window, ModalWindow.class);
		final ModalWindow window = (ModalWindow)HomePageTest.tester
				.getComponentFromLastRenderedPage(_window);
		HomePageTest.tester.assertInvisible(window.getPageRelativePath() + ":"
				+ window.getContentId());

		@SuppressWarnings("unchecked")
		final AjaxLink<Void> aboutLink = (AjaxLink<Void>)HomePageTest.tester
				.getComponentFromLastRenderedPage(linkToActivateWindow);
		Assert.assertNotNull(aboutLink);
		final IBehavior b = aboutLink.getBehaviors().get(0);
		Assert.assertNotNull(b);
		HomePageTest.tester.executeBehavior((AbstractAjaxBehavior)b);
		HomePageTest.tester.assertVisible(window.getPageRelativePath() + ":"
				+ window.getContentId());
	}

	@Test
	public void testRenderBaldu()
	{
		// start and render the test page
		HomePageTest.tester.startPage(HomePage.class);

		// assert rendered page class
		HomePageTest.tester.assertRenderedPage(HomePage.class);

		// Test the baldu and its different children
		HomePageTest.tester.assertComponent("balduParent:baldu", CardPanel.class);
		final CardPanel baldu = (CardPanel)HomePageTest.tester
				.getComponentFromLastRenderedPage("balduParent:baldu");
		final Image tapHandleImage = (Image)baldu.get("menutoggleButton:form:tapHandleImage");
		Assert.assertNotNull(tapHandleImage);
		final Image handleImage = (Image)baldu.get("menutoggleButton:form:handleImage");
		Assert.assertNotNull(handleImage);
		final Image cardImage = (Image)baldu.get("menutoggleButton:form:cardImage");
		Assert.assertNotNull(cardImage);
	}

	@Test
	public void testRenderToolbar()
	{
		// start and render the test page
		HomePageTest.tester.startPage(HomePage.class);

		// assert rendered page class
		HomePageTest.tester.assertRenderedPage(HomePage.class);

		// Test the baldu and its different children
		HomePageTest.tester.assertComponent("drawCardLink", AjaxLink.class);
		HomePageTest.tester.assertComponent("playCardPlaceholder", WebMarkupContainer.class);
		HomePageTest.tester.assertComponent("playCardPlaceholder:playCardLink",
				WebMarkupContainer.class);
		HomePageTest.tester.assertComponent("endTurnPlaceholder", WebMarkupContainer.class);
		HomePageTest.tester.assertComponent("endTurnPlaceholder:endTurnLink", AjaxLink.class);
	}

}
