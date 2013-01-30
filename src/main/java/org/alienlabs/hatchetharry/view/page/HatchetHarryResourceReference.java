package org.alienlabs.hatchetharry.view.page;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.ajax.WicketAjaxJQueryResourceReference;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.HeaderItem;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.request.resource.CssResourceReference;
import org.apache.wicket.request.resource.JavaScriptResourceReference;
import org.apache.wicket.request.resource.PackageResourceReference;

public class HatchetHarryResourceReference extends JavaScriptResourceReference
{
	private static final long serialVersionUID = 1L;

	public HatchetHarryResourceReference()
	{
		super(WicketAjaxJQueryResourceReference.class, "res/js/wicket-ajax-jquery.js");
	}

	@Override
	public Iterable<? extends HeaderItem> getDependencies()
	{
		final List<HeaderItem> dependencies = new ArrayList<HeaderItem>();

		dependencies.add(JavaScriptHeaderItem.forReference(new PackageResourceReference(
				HomePage.class, "script/google-analytics.js")));
		dependencies.add(JavaScriptHeaderItem.forReference(new PackageResourceReference(
				HomePage.class, "script/jquery/jquery-ui-1.8.18.core.mouse.widget.js")));
		dependencies.add(JavaScriptHeaderItem.forReference(new PackageResourceReference(
				HomePage.class, "script/tour/jquery.easing.1.3.js")));
		dependencies.add(JavaScriptHeaderItem.forReference(new PackageResourceReference(
				HomePage.class, "script/tour/jquery.cookie.js")));
		dependencies.add(JavaScriptHeaderItem.forReference(new PackageResourceReference(
				HomePage.class, "script/tour/modernizr.mq.js")));
		dependencies.add(JavaScriptHeaderItem.forReference(new PackageResourceReference(
				HomePage.class, "script/tour/jquery.joyride-1.0.5.js")));
		dependencies.add(JavaScriptHeaderItem.forReference(new PackageResourceReference(
				HomePage.class, "script/menubar/jquery.metadata.js")));
		dependencies.add(JavaScriptHeaderItem.forReference(new PackageResourceReference(
				HomePage.class, "script/menubar/jquery.hoverIntent.js")));
		dependencies.add(JavaScriptHeaderItem.forReference(new PackageResourceReference(
				HomePage.class, "script/menubar/mbMenu.js")));
		dependencies.add(JavaScriptHeaderItem.forReference(new PackageResourceReference(
				HomePage.class, "script/menubar/yahoo-dom-event.js")));
		dependencies.add(JavaScriptHeaderItem.forReference(new PackageResourceReference(
				HomePage.class, "script/menubar/animation.js")));
		dependencies.add(JavaScriptHeaderItem.forReference(new PackageResourceReference(
				HomePage.class, "script/menubar/utilities.js")));
		dependencies.add(JavaScriptHeaderItem.forReference(new PackageResourceReference(
				HomePage.class, "script/menubar/container_core.js")));
		dependencies.add(JavaScriptHeaderItem.forReference(new PackageResourceReference(
				HomePage.class, "script/menubar/menu.js")));
		dependencies.add(JavaScriptHeaderItem.forReference(new PackageResourceReference(
				HomePage.class, "script/menubar/element.js")));
		dependencies.add(JavaScriptHeaderItem.forReference(new PackageResourceReference(
				HomePage.class, "script/dock/dock.js")));
		dependencies.add(JavaScriptHeaderItem.forReference(new PackageResourceReference(
				HomePage.class, "script/qunitTests/qUnit.js")));
		dependencies.add(JavaScriptHeaderItem.forReference(new PackageResourceReference(
				HomePage.class, "script/qunitTests/codeUnderTest.js")));
		dependencies.add(JavaScriptHeaderItem.forReference(new PackageResourceReference(
				HomePage.class, "script/qunitTests/HomePageTests.js")));
		dependencies.add(JavaScriptHeaderItem.forReference(new PackageResourceReference(
				HomePage.class, "script/gallery/jquery-easing-compatibility.1.2.pack.js")));
		dependencies.add(JavaScriptHeaderItem.forReference(new PackageResourceReference(
				HomePage.class, "script/gallery/coda-slider.1.1.1.pack.js")));
		dependencies.add(JavaScriptHeaderItem.forReference(new PackageResourceReference(
				HomePage.class, "script/gallery/coda-sliderGraveyard.1.1.1.pack.js")));
		dependencies.add(JavaScriptHeaderItem.forReference(new PackageResourceReference(
				HomePage.class, "script/gallery/gallery.js")));
		dependencies.add(JavaScriptHeaderItem.forReference(new PackageResourceReference(
				HomePage.class, "script/gallery/graveyard.js")));
		dependencies.add(JavaScriptHeaderItem.forReference(new PackageResourceReference(
				HomePage.class, "script/rotate/jQueryRotate.2.1.js")));
		dependencies.add(JavaScriptHeaderItem.forReference(new PackageResourceReference(
				HomePage.class, "script/draggableHandle/jquery.ui.draggable.sidePlaceholder.js")));

		dependencies.add(CssHeaderItem.forReference(new CssResourceReference(HomePage.class,
				"stylesheet/menu.css")));
		dependencies.add(CssHeaderItem.forReference(new CssResourceReference(HomePage.class,
				"stylesheet/layout.css")));
		dependencies.add(CssHeaderItem.forReference(new CssResourceReference(HomePage.class,
				"stylesheet/menu_black.css")));
		dependencies.add(CssHeaderItem.forReference(new CssResourceReference(HomePage.class,
				"stylesheet/jquery.jquerytour.css")));
		dependencies.add(CssHeaderItem.forReference(new CssResourceReference(HomePage.class,
				"stylesheet/galleryStyle.css")));
		dependencies.add(CssHeaderItem.forReference(new CssResourceReference(HomePage.class,
				"stylesheet/graveyardStyle.css")));
		dependencies.add(CssHeaderItem.forReference(new CssResourceReference(HomePage.class,
				"stylesheet/jquery.gritter.css")));
		dependencies.add(CssHeaderItem.forReference(new CssResourceReference(HomePage.class,
				"stylesheet/fixed4all.css")));
		dependencies.add(CssHeaderItem.forReference(new CssResourceReference(HomePage.class,
				"stylesheet/fixed4ie.css")));
		dependencies.add(CssHeaderItem.forReference(new CssResourceReference(HomePage.class,
				"stylesheet/prettyPhoto.css")));
		dependencies.add(CssHeaderItem.forReference(new CssResourceReference(HomePage.class,
				"stylesheet/toolbarStyle.css")));
		dependencies.add(CssHeaderItem.forReference(new CssResourceReference(HomePage.class,
				"stylesheet/tipsy.css")));
		dependencies.add(CssHeaderItem.forReference(new CssResourceReference(HomePage.class,
				"script/dock/dock.css")));
		dependencies.add(CssHeaderItem.forReference(new CssResourceReference(HomePage.class,
				"stylesheet/joyride-1.0.5.css")));
		dependencies.add(CssHeaderItem.forReference(new CssResourceReference(HomePage.class,
				"stylesheet/demo-style.css")));
		dependencies.add(CssHeaderItem.forReference(new CssResourceReference(HomePage.class,
				"stylesheet/mobile.css")));

		dependencies.add(JavaScriptHeaderItem.forReference(new PackageResourceReference(
				HomePage.class, "script/toolbar/jquery.prettyPhoto.js")));
		dependencies.add(JavaScriptHeaderItem.forReference(new PackageResourceReference(
				HomePage.class, "script/toolbar/jquery.tipsy.js")));
		dependencies.add(JavaScriptHeaderItem.forReference(new PackageResourceReference(
				HomePage.class, "script/notifier/jquery.gritter.min.js")));

		return dependencies;
	}
}
