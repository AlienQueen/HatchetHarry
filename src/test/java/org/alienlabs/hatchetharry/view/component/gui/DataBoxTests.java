package org.alienlabs.hatchetharry.view.component.gui;

import org.alienlabs.hatchetharry.serverSideTest.util.SpringContextLoaderBaseTests;
import org.alienlabs.hatchetharry.view.page.HomePage;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.behavior.AbstractAjaxBehavior;
import org.apache.wicket.extensions.ajax.markup.html.AjaxEditableLabel;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.FormComponent;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;

/**
 * We can not unit test with several players, nonetheless this test can be
 * viewed like a non-regression test. Moreover it allowed me to refactor the
 * component under test.
 *
 * Created by nostromo on 16/11/14.
 */
@ContextConfiguration(locations = { "classpath:applicationContext.xml",
		"classpath:applicationContextTest.xml" })
public class DataBoxTests extends SpringContextLoaderBaseTests
{

	@Test
	public void testDataBox() throws Exception
	{
		// Start a game and play a card
		super.startAGameAndPlayACard("ajaxSubmit");

		// Verify that the player Zala exists and has 20 life points
		super.tester.startPage(HomePage.class);
		super.tester.assertRenderedPage(HomePage.class);

		this.tester.assertComponent("dataBoxParent:dataBox", DataBox.class);
		this.tester.assertComponent("dataBoxParent:dataBox:parent:box:0:playerLabel", Label.class);
		final Label playerName = (Label)this.tester
				.getComponentFromLastRenderedPage("dataBoxParent:dataBox:parent:box:0:playerLabel");
		Assert.assertNotNull(playerName);
		Assert.assertEquals("Zala: ", playerName.getDefaultModelObjectAsString());

		this.tester.assertComponent(
				"dataBoxParent:dataBox:parent:box:0:playerLifePointsParent:playerLifePoints:label",
				Label.class);
		Label playerLifePoints = (Label)this.tester
				.getComponentFromLastRenderedPage("dataBoxParent:dataBox:parent:box:0:playerLifePointsParent:playerLifePoints:label");
		Assert.assertEquals("20", playerLifePoints.getDefaultModelObjectAsString());

		// Set life point total to 12
		this.tester.assertComponent(
				"dataBoxParent:dataBox:parent:box:0:playerLifePointsParent:playerLifePoints",
				AjaxEditableLabel.class);
		AjaxEditableLabel<String> ajaxLabel = (AjaxEditableLabel<String>)this.tester
				.getComponentFromLastRenderedPage("dataBoxParent:dataBox:parent:box:0:playerLifePointsParent:playerLifePoints");
		tester.executeAjaxEvent(
				"dataBoxParent:dataBox:parent:box:0:playerLifePointsParent:playerLifePoints:label",
				"onclick");

		// set new value to 12 and submit it
		FormComponent<String> editor = (FormComponent<String>)ajaxLabel.get("editor");
		tester.getRequest().setParameter(editor.getInputName(), "12");
		tester.getRequest().setParameter("save", "true");
		tester.executeBehavior((AbstractAjaxBehavior)editor.getBehaviorById(0));

		// Verify
		super.tester.startPage(HomePage.class);
		super.tester.assertRenderedPage(HomePage.class);
		tester.assertInvisible("dataBoxParent:dataBox:parent:box:0:playerLifePointsParent:playerLifePoints:editor");
		tester.assertVisible("dataBoxParent:dataBox:parent:box:0:playerLifePointsParent:playerLifePoints:label");
		tester.assertLabel(
				"dataBoxParent:dataBox:parent:box:0:playerLifePointsParent:playerLifePoints:label",
				"12");

		// Decrement life points
		this.tester.assertComponent("dataBoxParent:dataBox:parent:box:0:playerMinusLink",
				AjaxLink.class);
		this.tester.clickLink("dataBoxParent:dataBox:parent:box:0:playerMinusLink", true);

		// Verify
		super.tester.startPage(HomePage.class);
		super.tester.assertRenderedPage(HomePage.class);

		this.tester.assertComponent(
				"dataBoxParent:dataBox:parent:box:0:playerLifePointsParent:playerLifePoints:label",
				Label.class);
		playerLifePoints = (Label)this.tester
				.getComponentFromLastRenderedPage("dataBoxParent:dataBox:parent:box:0:playerLifePointsParent:playerLifePoints:label");
		Assert.assertEquals("11", playerLifePoints.getDefaultModelObjectAsString());

		// Increment life points
		this.tester.assertComponent("dataBoxParent:dataBox:parent:box:0:playerPlusLink",
				AjaxLink.class);
		this.tester.clickLink("dataBoxParent:dataBox:parent:box:0:playerPlusLink", true);

		// Verify
		super.tester.startPage(HomePage.class);
		super.tester.assertRenderedPage(HomePage.class);

		this.tester.assertComponent(
				"dataBoxParent:dataBox:parent:box:0:playerLifePointsParent:playerLifePoints:label",
				Label.class);
		playerLifePoints = (Label)this.tester
				.getComponentFromLastRenderedPage("dataBoxParent:dataBox:parent:box:0:playerLifePointsParent:playerLifePoints:label");
		Assert.assertEquals("12", playerLifePoints.getDefaultModelObjectAsString());


		// Set life point total to 23
		this.tester.assertComponent(
				"dataBoxParent:dataBox:parent:box:0:playerLifePointsParent:playerLifePoints",
				AjaxEditableLabel.class);
		ajaxLabel = (AjaxEditableLabel<String>)this.tester
				.getComponentFromLastRenderedPage("dataBoxParent:dataBox:parent:box:0:playerLifePointsParent:playerLifePoints");
		tester.executeAjaxEvent(
				"dataBoxParent:dataBox:parent:box:0:playerLifePointsParent:playerLifePoints:label",
				"onclick");

		// set new value to 23 and submit it
		editor = (FormComponent<String>)ajaxLabel.get("editor");
		tester.getRequest().setParameter(editor.getInputName(), "23");
		tester.getRequest().setParameter("save", "true");
		tester.executeBehavior((AbstractAjaxBehavior)editor.getBehaviorById(0));

		// Verify
		super.tester.startPage(HomePage.class);
		super.tester.assertRenderedPage(HomePage.class);
		tester.assertInvisible("dataBoxParent:dataBox:parent:box:0:playerLifePointsParent:playerLifePoints:editor");
		tester.assertVisible("dataBoxParent:dataBox:parent:box:0:playerLifePointsParent:playerLifePoints:label");
		tester.assertLabel(
				"dataBoxParent:dataBox:parent:box:0:playerLifePointsParent:playerLifePoints:label",
				"23");

		// Decrement life points
		this.tester.assertComponent("dataBoxParent:dataBox:parent:box:0:playerMinusLink",
				AjaxLink.class);
		this.tester.clickLink("dataBoxParent:dataBox:parent:box:0:playerMinusLink", true);

		// Verify
		super.tester.startPage(HomePage.class);
		super.tester.assertRenderedPage(HomePage.class);

		this.tester.assertComponent(
				"dataBoxParent:dataBox:parent:box:0:playerLifePointsParent:playerLifePoints:label",
				Label.class);
		playerLifePoints = (Label)this.tester
				.getComponentFromLastRenderedPage("dataBoxParent:dataBox:parent:box:0:playerLifePointsParent:playerLifePoints:label");
		Assert.assertEquals("22", playerLifePoints.getDefaultModelObjectAsString());
	}

}
