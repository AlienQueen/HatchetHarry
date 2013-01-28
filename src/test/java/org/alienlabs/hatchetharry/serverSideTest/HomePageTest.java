package org.alienlabs.hatchetharry.serverSideTest;

import java.util.List;

import org.alienlabs.hatchetharry.HatchetHarryApplication;
import org.alienlabs.hatchetharry.view.component.CardPanel;
import org.alienlabs.hatchetharry.view.component.ChatPanel;
import org.alienlabs.hatchetharry.view.component.ClockPanel;
import org.alienlabs.hatchetharry.view.component.DataBox;
import org.alienlabs.hatchetharry.view.component.HandComponent;
import org.alienlabs.hatchetharry.view.page.HomePage;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.behavior.AbstractAjaxBehavior;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.spring.injection.annot.SpringComponentInjector;
import org.apache.wicket.util.tester.TagTester;
import org.apache.wicket.util.tester.WicketTester;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Simple test using the WicketTester
 */
public class HomePageTest
{
	static final ClassPathXmlApplicationContext CLASS_PATH_XML_APPLICATION_CONTEXT = new ClassPathXmlApplicationContext(
			new String[] { "applicationContext.xml" });
	protected static transient WicketTester tester;
	protected static HatchetHarryApplication webApp;
	protected static transient ApplicationContext context;

	@BeforeClass
	public static void setUpBeforeClass()
	{
		HomePageTest.webApp = new HatchetHarryApplication()
		{
			private static final long serialVersionUID = 1L;


			@Override
			public void init()
			{
				HomePageTest.context = HomePageTest.CLASS_PATH_XML_APPLICATION_CONTEXT;
				this.getComponentInstantiationListeners().add(
						new SpringComponentInjector(this, HomePageTest.context, true));
			}
		};
		HomePageTest.tester = new WicketTester(HomePageTest.webApp);
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
				.getComponentFromLastRenderedPage("message1");
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
		HomePageTest.tester.assertComponent("galleryParent:gallery", HandComponent.class);

		// assert URL of a thumbnail
		final List<TagTester> tagTester = TagTester.createTagsByAttribute(HomePageTest.tester
				.getLastResponse().getDocument(), "class", "nav-thumb", false);
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
				.getLastResponse().getDocument(), "class", "myMenu", false);
		Assert.assertNotNull(tagTester);
		Assert.assertEquals(1, tagTester.size());

		// Assert menu entries
		tagTester = TagTester.createTagsByAttribute(HomePageTest.tester.getLastResponse()
				.getDocument(), "class", "mbmenu", false);
		Assert.assertNotNull(tagTester);
		Assert.assertEquals(3, tagTester.size());

		// Assert 'Import a deck' entry exists
		boolean containsText = false;
		for (final TagTester tt : tagTester)
		{
			if (((null != tt.getMarkup()) && tt.getMarkup().contains("Import a deck")))
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
		HomePageTest.tester.assertLabel(
				"dataBoxParent:dataBox:parent:box:0:playerLifePointsParent:playerLifePoints",
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
		HomePageTest.testDockElement("Hand");

		// Assert graveyard
		HomePageTest.testDockElement("Graveyard");

		// Assert exiled
		HomePageTest.testDockElement("Exiled");

		// Assert battlefield
		HomePageTest.testDockElement("Battlefield");

		// Assert library
		HomePageTest.testDockElement("Library");

		// The first click must hide the hand
		HomePageTest.tester.assertComponent("handLink", AjaxLink.class);
		HomePageTest.tester.clickLink("handLink", true);
		WebMarkupContainer handParent = (WebMarkupContainer)HomePageTest.tester
				.getComponentFromLastRenderedPage("galleryParent");
		HomePageTest.tester.assertComponentOnAjaxResponse(handParent);

		final Component gallery = HomePageTest.tester
				.getComponentFromLastRenderedPage("galleryParent:gallery");
		Assert.assertNotNull(gallery);
		Assert.assertFalse(gallery instanceof HandComponent);
		HomePageTest.tester.assertComponent("galleryParent:gallery", WebMarkupContainer.class);

		// The second click must show the hand
		HomePageTest.tester.clickLink("handLink", true);
		handParent = (WebMarkupContainer)HomePageTest.tester
				.getComponentFromLastRenderedPage("galleryParent");
		HomePageTest.tester.assertComponentOnAjaxResponse(handParent);

		HomePageTest.tester.assertComponent("galleryParent:gallery", HandComponent.class);
		final HandComponent hand = (HandComponent)HomePageTest.tester
				.getComponentFromLastRenderedPage("galleryParent:gallery");
		Assert.assertNotNull(hand);
	}

	// Assert dock element is present and contains a .gif
	private static void testDockElement(final String name)
	{
		final List<TagTester> tagTester = TagTester.createTagsByAttribute(HomePageTest.tester
				.getLastResponse().getDocument(), "title", name, false);
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

		HomePageTest.testModalWindow("aboutWindow", "aboutLink");
		HomePageTest.testModalWindow("teamInfoWindow", "teamInfoLink");
		HomePageTest.testModalWindowWithParent("generateCreateGameLinkParent", "createGameWindow",
				"createGameLinkParent", "createGameLink");
		HomePageTest.testModalWindowWithParent("generateJoinGameLinkParent", "joinGameWindow",
				"joinGameLinkParent", "joinGameLink");
	}

	private static void testModalWindow(final String _window, final String linkToActivateWindow)
	{
		// assert about & team info modal window are in the page
		HomePageTest.tester.assertComponent(_window, ModalWindow.class);
		final ModalWindow window = (ModalWindow)HomePageTest.tester
				.getComponentFromLastRenderedPage(_window);
		HomePageTest.tester.assertInvisible(window.getPageRelativePath() + ":"
				+ window.getContentId());

		final AjaxLink<Void> aboutLink = (AjaxLink<Void>)HomePageTest.tester
				.getComponentFromLastRenderedPage(linkToActivateWindow);
		Assert.assertNotNull(aboutLink);
		final Behavior b = aboutLink.getBehaviors().get(0);
		Assert.assertNotNull(b);
		HomePageTest.tester.executeBehavior((AbstractAjaxBehavior)b);
		HomePageTest.tester.assertVisible(window.getPageRelativePath() + ":"
				+ window.getContentId());
	}


	private static void testModalWindowWithParent(final String windowParent, final String _window,
			final String parentOfLinkToActivateWindow, final String linkToActivateWindow)
	{
		// assert about game windows are in the page
		HomePageTest.tester.assertComponent(windowParent + ":" + _window, ModalWindow.class);
		final ModalWindow window = (ModalWindow)HomePageTest.tester
				.getComponentFromLastRenderedPage(windowParent + ":" + _window);
		HomePageTest.tester.assertInvisible(window.getPageRelativePath() + ":"
				+ window.getContentId());

		final AjaxLink<Void> aboutLink = (AjaxLink<Void>)HomePageTest.tester
				.getComponentFromLastRenderedPage(parentOfLinkToActivateWindow + ":"
						+ linkToActivateWindow);
		Assert.assertNotNull(aboutLink);
		final Behavior b = aboutLink.getBehaviors().get(0);
		Assert.assertNotNull(b);
		HomePageTest.tester.executeBehavior((AbstractAjaxBehavior)b);
		HomePageTest.tester.assertVisible(window.getPageRelativePath() + ":"
				+ window.getContentId());
	}

	@Test
	@Ignore("change in CardPanels implementation")
	// TODO activate me again
	public void testRenderBaldu()
	{
		// start and render the test page
		HomePageTest.tester.startPage(HomePage.class);

		// assert rendered page class
		HomePageTest.tester.assertRenderedPage(HomePage.class);

		// Test the baldu and its different children
		HomePageTest.tester.assertComponent("parentPlaceholder:handCards:0:cardPanel",
				CardPanel.class);
		final CardPanel baldu = (CardPanel)HomePageTest.tester
				.getComponentFromLastRenderedPage("parentPlaceholder:handCards:0:cardPanel");
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
