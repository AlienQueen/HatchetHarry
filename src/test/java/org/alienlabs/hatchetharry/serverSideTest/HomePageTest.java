package org.alienlabs.hatchetharry.serverSideTest;

import java.util.List;

import org.alienlabs.hatchetharry.HatchetHarryApplication;
import org.alienlabs.hatchetharry.view.component.ChatPanel;
import org.alienlabs.hatchetharry.view.component.ClockPanel;
import org.alienlabs.hatchetharry.view.component.DataBox;
import org.alienlabs.hatchetharry.view.component.HandComponent;
import org.alienlabs.hatchetharry.view.component.cardrotate.CardPanel;
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
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Simple test using the WicketTester
 */
public class HomePageTest
{
	private transient WicketTester tester;

	@Before
	public void setUp()
	{
		final HatchetHarryApplication webApp = new HatchetHarryApplication()
		{
			private static final long serialVersionUID = 1L;
			// note in this case the application context is in the default
			// package
			transient ApplicationContext context = new ClassPathXmlApplicationContext(
					new String[] { "applicationContext.xml" });

			@Override
			public void init()
			{
				this.getComponentInstantiationListeners().add(
						new SpringComponentInjector(this, this.context, true));
			}
		};

		this.tester = new WicketTester(webApp);
		final ApplicationContext context = new ClassPathXmlApplicationContext(
				new String[] { "applicationContext.xml" });
		this.tester.getApplication().getComponentInstantiationListeners()
				.add(new SpringComponentInjector(this.tester.getApplication(), context, true));
	}

	@Test
	public void testRenderMyPage()
	{
		// start and render the test page
		this.tester.startPage(HomePage.class);

		// assert rendered page class
		this.tester.assertRenderedPage(HomePage.class);

		// assert rendered label component
		final Label message = (Label)this.tester.getComponentFromLastRenderedPage("message");
		Assert.assertTrue(message.getDefaultModelObjectAsString().contains("version"));
		Assert.assertTrue(message.getDefaultModelObjectAsString().contains("release"));

	}

	@Test
	public void testRenderHand()
	{
		// start and render the test page
		this.tester.startPage(HomePage.class);

		// assert rendered page class
		this.tester.assertRenderedPage(HomePage.class);

		// assert hand is present
		this.tester.assertComponent("handCardsPlaceholder:gallery", HandComponent.class);

		this.tester.getComponentFromLastRenderedPage("handCardsPlaceholder:gallery");

		// assert URL of a thumbnail
		final List<TagTester> tagTester = TagTester.createTagsByAttribute(this.tester
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
		this.tester.startPage(HomePage.class);

		// assert rendered page class
		this.tester.assertRenderedPage(HomePage.class);

		// assert clock is present
		this.tester.assertComponent("clockPanel", ClockPanel.class);

		// assert clock content
		final ClockPanel clock = (ClockPanel)this.tester
				.getComponentFromLastRenderedPage("clockPanel");
		Assert.assertTrue(clock.getTime().getObject().contains("###"));
	}

	@Test
	public void testRenderMenuBar()
	{
		// start and render the test page
		this.tester.startPage(HomePage.class);

		// assert rendered page class
		this.tester.assertRenderedPage(HomePage.class);

		// Assert menubar
		List<TagTester> tagTester = TagTester.createTagsByAttribute(this.tester.getLastResponse()
				.getDocument(), "class", "rootVoices", false);
		Assert.assertNotNull(tagTester);
		Assert.assertEquals(1, tagTester.size());

		// Assert menu entries
		tagTester = TagTester.createTagsByAttribute(this.tester.getLastResponse().getDocument(),
				"class", "mbmenu", false);
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
		this.tester.startPage(HomePage.class);

		// assert rendered page class
		this.tester.assertRenderedPage(HomePage.class);

		// assert DataBox is present
		this.tester.assertComponent("dataBoxParent:dataBox", DataBox.class);

		// assert DataBox content
		this.tester.assertLabel(
				"dataBoxParent:dataBox:parent:box:0:playerLifePointsParent:playerLifePoints",
				"20 life points");
	}

	@Test
	public void testRenderChat()
	{
		// start and render the test page
		this.tester.startPage(HomePage.class);

		// assert rendered page class
		this.tester.assertRenderedPage(HomePage.class);

		// assert chat is present
		this.tester.assertComponent("chatPanel", ChatPanel.class);
		this.tester.assertComponent("chatPanel:chatForm:user", RequiredTextField.class);
		this.tester.assertComponent("chatPanel:chatForm:message", RequiredTextField.class);
	}

	@Test
	public void testRenderDock()
	{
		// start and render the test page
		this.tester.startPage(HomePage.class);

		// assert rendered page class
		this.tester.assertRenderedPage(HomePage.class);

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

		// The first click must hide the hand
		this.tester.assertComponent("handLink", AjaxLink.class);
		this.tester.clickLink("handLink", true);
		WebMarkupContainer handParent = (WebMarkupContainer)this.tester
				.getComponentFromLastRenderedPage("handCardsPlaceholder");
		this.tester.assertComponentOnAjaxResponse(handParent);

		final Component gallery = this.tester
				.getComponentFromLastRenderedPage("handCardsPlaceholder:gallery");
		Assert.assertNotNull(gallery);
		Assert.assertFalse(gallery instanceof HandComponent);
		this.tester.assertComponent("handCardsPlaceholder:gallery", WebMarkupContainer.class);

		// The second click must show the hand
		this.tester.clickLink("handLink", true);
		handParent = (WebMarkupContainer)this.tester
				.getComponentFromLastRenderedPage("handCardsPlaceholder");
		this.tester.assertComponentOnAjaxResponse(handParent);

		this.tester.assertComponent("handCardsPlaceholder:gallery", HandComponent.class);
		final HandComponent hand = (HandComponent)this.tester
				.getComponentFromLastRenderedPage("handCardsPlaceholder:gallery");
		Assert.assertNotNull(hand);
	}

	// Assert dock element is present and contains a .gif
	private void testDockElement(final String name)
	{
		final List<TagTester> tagTester = TagTester.createTagsByAttribute(this.tester
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
		this.tester.startPage(HomePage.class);

		// assert rendered page class
		this.tester.assertRenderedPage(HomePage.class);

		this.testModalWindow("aboutWindow", "aboutLink");
		this.testModalWindow("teamInfoWindow", "teamInfoLink");
		this.testModalWindow("createGameWindow", "createGameLink");
		this.testModalWindow("joinGameWindow", "joinGameLink");
	}

	public void testModalWindow(final String _window, final String linkToActivateWindow)
	{
		// assert about modal window is in the page
		this.tester.assertComponent(_window, ModalWindow.class);
		final ModalWindow window = (ModalWindow)this.tester
				.getComponentFromLastRenderedPage(_window);
		this.tester.assertInvisible(window.getPageRelativePath() + ":" + window.getContentId());

		@SuppressWarnings("unchecked")
		final AjaxLink<Void> aboutLink = (AjaxLink<Void>)this.tester
				.getComponentFromLastRenderedPage(linkToActivateWindow);
		Assert.assertNotNull(aboutLink);
		final Behavior b = aboutLink.getBehaviors().get(0);
		Assert.assertNotNull(b);
		this.tester.executeBehavior((AbstractAjaxBehavior)b);
		this.tester.assertVisible(window.getPageRelativePath() + ":" + window.getContentId());
	}

	@Test
	public void testRenderBaldu()
	{
		// start and render the test page
		this.tester.startPage(HomePage.class);

		// assert rendered page class
		this.tester.assertRenderedPage(HomePage.class);

		// Test the baldu and its different children
		this.tester.assertComponent("balduParent:baldu", CardPanel.class);
		final CardPanel baldu = (CardPanel)this.tester
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
		this.tester.startPage(HomePage.class);

		// assert rendered page class
		this.tester.assertRenderedPage(HomePage.class);

		// Test the baldu and its different children
		this.tester.assertComponent("drawCardLink", AjaxLink.class);
		this.tester.assertComponent("playCardPlaceholder", WebMarkupContainer.class);
		this.tester.assertComponent("playCardPlaceholder:playCardLink", WebMarkupContainer.class);
		this.tester.assertComponent("endTurnPlaceholder", WebMarkupContainer.class);
		this.tester.assertComponent("endTurnPlaceholder:endTurnLink", AjaxLink.class);
	}

}
