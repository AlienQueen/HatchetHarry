package org.alienlabs.hatchetharry.serverSideTest;

import java.util.List;

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
import org.apache.wicket.util.tester.TagTester;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Simple test using the WicketTester
 */
public class HomePageTest extends TestParent
{
	@Test
	public void testRenderMyPage()
	{
		// start and render the test page
		TestParent.tester.startPage(HomePage.class);

		// assert rendered page class
		TestParent.tester.assertRenderedPage(HomePage.class);

		// assert rendered label component
		final Label message = (Label)TestParent.tester.getComponentFromLastRenderedPage("message1");
		Assert.assertTrue(message.getDefaultModelObjectAsString().contains("version"));
		Assert.assertTrue(message.getDefaultModelObjectAsString().contains("release"));

	}

	@Test
	public void testRenderHand()
	{
		// start and render the test page
		TestParent.tester.startPage(HomePage.class);

		// assert rendered page class
		TestParent.tester.assertRenderedPage(HomePage.class);

		// assert hand is present
		TestParent.tester.assertComponent("galleryParent:gallery", HandComponent.class);

		// assert URL of a thumbnail
		final List<TagTester> tagTester = TagTester.createTagsByAttribute(TestParent.tester
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
		TestParent.tester.startPage(HomePage.class);

		// assert rendered page class
		TestParent.tester.assertRenderedPage(HomePage.class);

		// assert clock is present
		TestParent.tester.assertComponent("clockPanel", ClockPanel.class);

		// assert clock content
		final ClockPanel clock = (ClockPanel)TestParent.tester
				.getComponentFromLastRenderedPage("clockPanel");
		Assert.assertTrue(clock.getTime().getObject().contains("###"));
	}

	@Test
	public void testRenderMenuBar()
	{
		// start and render the test page
		TestParent.tester.startPage(HomePage.class);

		// assert rendered page class
		TestParent.tester.assertRenderedPage(HomePage.class);

		// Assert menubar
		List<TagTester> tagTester = TagTester.createTagsByAttribute(TestParent.tester
				.getLastResponse().getDocument(), "class", "rootVoices", false);
		Assert.assertNotNull(tagTester);
		Assert.assertEquals(1, tagTester.size());

		// Assert menu entries
		tagTester = TagTester.createTagsByAttribute(TestParent.tester.getLastResponse()
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
		TestParent.tester.startPage(HomePage.class);

		// assert rendered page class
		TestParent.tester.assertRenderedPage(HomePage.class);

		// assert DataBox is present
		TestParent.tester.assertComponent("dataBoxParent:dataBox", DataBox.class);

		// assert DataBox content
		TestParent.tester.assertLabel(
				"dataBoxParent:dataBox:parent:box:0:playerLifePointsParent:playerLifePoints",
				"20 life points");
	}

	@Test
	public void testRenderChat()
	{
		// start and render the test page
		TestParent.tester.startPage(HomePage.class);

		// assert rendered page class
		TestParent.tester.assertRenderedPage(HomePage.class);

		// assert chat is present
		TestParent.tester.assertComponent("chatPanel", ChatPanel.class);
		TestParent.tester.assertComponent("chatPanel:chatForm:user", RequiredTextField.class);
		TestParent.tester.assertComponent("chatPanel:chatForm:message", RequiredTextField.class);
	}

	@Test
	public void testRenderDock()
	{
		// start and render the test page
		TestParent.tester.startPage(HomePage.class);

		// assert rendered page class
		TestParent.tester.assertRenderedPage(HomePage.class);

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
		TestParent.tester.assertComponent("handLink", AjaxLink.class);
		TestParent.tester.clickLink("handLink", true);
		WebMarkupContainer handParent = (WebMarkupContainer)TestParent.tester
				.getComponentFromLastRenderedPage("galleryParent");
		TestParent.tester.assertComponentOnAjaxResponse(handParent);

		final Component gallery = TestParent.tester
				.getComponentFromLastRenderedPage("galleryParent:gallery");
		Assert.assertNotNull(gallery);
		Assert.assertFalse(gallery instanceof HandComponent);
		TestParent.tester.assertComponent("galleryParent:gallery", WebMarkupContainer.class);

		// The second click must show the hand
		TestParent.tester.clickLink("handLink", true);
		handParent = (WebMarkupContainer)TestParent.tester
				.getComponentFromLastRenderedPage("galleryParent");
		TestParent.tester.assertComponentOnAjaxResponse(handParent);

		TestParent.tester.assertComponent("galleryParent:gallery", HandComponent.class);
		final HandComponent hand = (HandComponent)TestParent.tester
				.getComponentFromLastRenderedPage("galleryParent:gallery");
		Assert.assertNotNull(hand);
	}

	// Assert dock element is present and contains a .gif
	private static void testDockElement(final String name)
	{
		final List<TagTester> tagTester = TagTester.createTagsByAttribute(TestParent.tester
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
		TestParent.tester.startPage(HomePage.class);

		// assert rendered page class
		TestParent.tester.assertRenderedPage(HomePage.class);

		this.testModalWindow("aboutWindow", "aboutLink");
		this.testModalWindow("teamInfoWindow", "teamInfoLink");
		this.testModalWindow("createGameWindow", "createGameLink");
		this.testModalWindow("joinGameWindow", "joinGameLink");
	}

	public void testModalWindow(final String _window, final String linkToActivateWindow)
	{
		// assert about modal window is in the page
		TestParent.tester.assertComponent(_window, ModalWindow.class);
		final ModalWindow window = (ModalWindow)TestParent.tester
				.getComponentFromLastRenderedPage(_window);
		TestParent.tester.assertInvisible(window.getPageRelativePath() + ":"
				+ window.getContentId());

		final AjaxLink<Void> aboutLink = (AjaxLink<Void>)TestParent.tester
				.getComponentFromLastRenderedPage(linkToActivateWindow);
		Assert.assertNotNull(aboutLink);
		final Behavior b = aboutLink.getBehaviors().get(0);
		Assert.assertNotNull(b);
		TestParent.tester.executeBehavior((AbstractAjaxBehavior)b);
		TestParent.tester.assertVisible(window.getPageRelativePath() + ":" + window.getContentId());
	}

	@Test
	@Ignore("change in CardPanels implementation")
	// TODO activate me again
	public void testRenderBaldu()
	{
		// start and render the test page
		TestParent.tester.startPage(HomePage.class);

		// assert rendered page class
		TestParent.tester.assertRenderedPage(HomePage.class);

		// Test the baldu and its different children
		TestParent.tester.assertComponent("parentPlaceholder:handCards:0:cardPanel",
				CardPanel.class);
		final CardPanel baldu = (CardPanel)TestParent.tester
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
		TestParent.tester.startPage(HomePage.class);

		// assert rendered page class
		TestParent.tester.assertRenderedPage(HomePage.class);

		// Test the baldu and its different children
		TestParent.tester.assertComponent("drawCardLink", AjaxLink.class);
		TestParent.tester.assertComponent("playCardPlaceholder", WebMarkupContainer.class);
		TestParent.tester.assertComponent("playCardPlaceholder:playCardLink",
				WebMarkupContainer.class);
		TestParent.tester.assertComponent("endTurnPlaceholder", WebMarkupContainer.class);
		TestParent.tester.assertComponent("endTurnPlaceholder:endTurnLink", AjaxLink.class);
	}

}
