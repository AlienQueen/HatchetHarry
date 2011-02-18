package org.alienlabs.hatchetharry.view;

import org.apache.wicket.ResourceReference;
import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.markup.html.CSSPackageResource;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.html.resources.CompressedResourceReference;
import org.apache.wicket.markup.html.resources.JavaScriptReference;
import org.apache.wicket.model.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("serial")
public class CardPanel extends Panel
{
	static final Logger logger = LoggerFactory.getLogger(CardPanel.class);

	public CardPanel(final String id)
	{
		super(id);

		this.add(new JavaScriptReference("jQuery.bubbletip-1.0.6.js", HomePage.class,
				"scripts/bubbletip/jQuery.bubbletip-1.0.6.js"));
		this.add(CSSPackageResource.getHeaderContribution(new CompressedResourceReference(
				HomePage.class, "scripts/bubbletip/bubbletip.css")));

		final WebMarkupContainer menutoggleButton = new WebMarkupContainer("menutoggleButton");
		final Image img = new Image("menutoggleImage", new ResourceReference(
				"cards/BalduvianHorde_small.jpg"));
		menutoggleButton.add(img);
		menutoggleButton.setOutputMarkupId(true);
		menutoggleButton.setMarkupId("contextMenu");
		this.add(menutoggleButton);

		final Image bubbleTipImg1 = new Image("bubbleTipImg1", new ResourceReference(
				"cards/BalduvianHorde.jpg"));
		final Label bubbleTipText1 = new Label("bubbleTipText1", new Model<String>(
				"<b><br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
						+ "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
						+ "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
						+ "5/5<br/><br/>"
						+ "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;When Balduvian Horde comes<br/>"
						+ "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;into play,sacrifice it<br/>"
						+ "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;unless you discard a card<br/>"
						+ "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;at random from your hand</b>"));
		bubbleTipText1.add(new SimpleAttributeModifier("style", "color=white;"));
		bubbleTipText1.setOutputMarkupPlaceholderTag(true).setEscapeModelStrings(false);
		menutoggleButton.add(bubbleTipImg1, bubbleTipText1);

		this.add(new JavaScriptReference("jquery.contextMenu.js", HomePage.class,
				"scripts/contextmenu/jquery.contextMenu.js"));
		this.add(CSSPackageResource.getHeaderContribution(new CompressedResourceReference(
				HomePage.class, "scripts/contextmenu/jquery.contextMenu.css")));

		this.add(new JavaScriptReference("jquery.ui.core.js", HomePage.class,
				"scripts/draggableHandle/jquery.ui.core.js"));
		this.add(new JavaScriptReference("jquery.ui.widget.js", HomePage.class,
				"scripts/draggableHandle/jquery.ui.widget.js"));
		this.add(new JavaScriptReference("jquery.ui.mouse.js", HomePage.class,
				"scripts/draggableHandle/jquery.ui.mouse.js"));
		this.add(new JavaScriptReference("jquery.ui.draggable.js", HomePage.class,
				"scripts/draggableHandle/jquery.ui.draggable.js"));

		final Image handle = new Image("handleImage", new ResourceReference("images/arrow.png"));
		menutoggleButton.add(handle);
	}
}
