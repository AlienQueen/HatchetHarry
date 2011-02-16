$(document).ready(function() {
	var myGallery = new gallery($('myGallery'), {
		timed : true,
		fadeDuration : 1000,
		delay : 10000,
		useHistoryManager : true,
		thumbWidth : 23,
		thumbHeight : 32,
		showInfopane : true,
		embedLinks : true,
		thumbCloseCarousel : true,
		showCarouselLabel : true,
		textShowCarousel : 'Hand',
		showArrows : true,
		showCarousel : true
	});
	HistoryManager.start();
	myGallery.showCarousel();
});

/*
 * Initialize and render the MenuBar when its elements are ready to be scripted.
 */
$(document)
		.ready(
				function() {
					YAHOO.util.Event
							.onContentReady(
									"productsandservices",
									function() {

										/*
										 * Instantiate a MenuBar: The first
										 * argument passed to the constructor is
										 * the id for the Menu element to be
										 * created, the second is an object
										 * literal of configuration properties.
										 */

										var oMenuBar = new YAHOO.widget.MenuBar(
												"productsandservices", {
													autosubmenudisplay : true,
													hidedelay : 750,
													lazyload : true
												});

										/*
										 * Define an array of object literals,
										 * each containing the data necessary to
										 * create a submenu.
										 */

										var aSubmenuData = [
												{
													id : "Documentation",
													itemdata : [
															{
																text : "Official Magic(tm) rules",
																url : "http://360.yahoo.com"
															},
															{
																text : "HatchetHarry documentation",
																url : "http://alerts.yahoo.com"
															},
															{
																text : "Browse cards database",
																url : "http://avatars.yahoo.com"
															} ]
												},
												{
													id : "Game",
													itemdata : [
															{
																text : "Your life",
																submenu : {
																	id : "life",
																	itemdata : [
																			{
																				text : "Gain 1 life",
																				url : "http://mail.yahoo.com"
																			},
																			{
																				text : "Loose 1 life",
																				url : "http://addressbook.yahoo.com"
																			},
																			{
																				text : "Gain an infinity of life",
																				url : "http://calendar.yahoo.com"
																			},
																			{
																				text : "Give up the game",
																				url : "http://notepad.yahoo.com"
																			} ]
																}

															},
															{
																text : "Mana",
																submenu : {
																	id : "mana",
																	itemdata : [
																			{
																				text : "Add 1 coloured mana to pool",
																				url : "http://mail.yahoo.com"
																			},
																			{
																				text : "Remove 1 coloured mana from pool",
																				url : "http://addressbook.yahoo.com"
																			},
																			{
																				text : "Add 1 colorless mana to pool",
																				url : "http://calendar.yahoo.com"
																			},
																			{
																				text : "Remove 1 colorless mana from pool",
																				url : "http://notepad.yahoo.com"
																			},
																			{
																				text : "flush mana pool",
																				url : "http://calendar.yahoo.com"
																			} ]
																}

															},
															{
																text : "Cards",
																submenu : {
																	id : "cards",
																	itemdata : [
																			{
																				text : "Put top library card to graveyard",
																				url : "http://mail.yahoo.com"
																			},
																			{
																				text : "Discard a card at random",
																				url : "http://addressbook.yahoo.com"
																			},
																			{
																				text : "Put top graveyard card to top of library",
																				url : "http://calendar.yahoo.com"
																			},
																			{
																				text : "Remove top graveyard card from the game",
																				url : "http://notepad.yahoo.com"
																			} ]
																}

															} ]
												},

												{
													id : "entertainment",
													itemdata : [
															{
																text : "Fantasy Sports",
																url : "http://fantasysports.yahoo.com"
															},
															{
																text : "Games",
																url : "http://games.yahoo.com"
															},
															{
																text : "Kids",
																url : "http://www.yahooligans.com"
															},
															{
																text : "Music",
																url : "http://music.yahoo.com"
															},
															{
																text : "Movies",
																url : "http://movies.yahoo.com"
															},
															{
																text : "Radio",
																url : "http://music.yahoo.com/launchcast"
															},
															{
																text : "Travel",
																url : "http://travel.yahoo.com"
															},
															{
																text : "TV",
																url : "http://tv.yahoo.com"
															} ]
												},

												{
													id : "information",
													itemdata : [
															{
																text : "Downloads",
																url : "http://downloads.yahoo.com"
															},
															{
																text : "Finance",
																url : "http://finance.yahoo.com"
															},
															{
																text : "Health",
																url : "http://health.yahoo.com"
															},
															{
																text : "Local",
																url : "http://local.yahoo.com"
															},
															{
																text : "Maps & Directions",
																url : "http://maps.yahoo.com"
															},
															{
																text : "My Yahoo!",
																url : "http://my.yahoo.com"
															},
															{
																text : "News",
																url : "http://news.yahoo.com"
															},
															{
																text : "Search",
																url : "http://search.yahoo.com"
															},
															{
																text : "Small Business",
																url : "http://smallbusiness.yahoo.com"
															},
															{
																text : "Weather",
																url : "http://weather.yahoo.com"
															} ]
												} ];

										var ua = YAHOO.env.ua, oAnim; // Animation
										// instance

										/*
										 * "beforeshow" event handler for each
										 * submenu of the MenuBar instance, used
										 * to setup certain style properties
										 * before the menu is animated.
										 */

										function onSubmenuBeforeShow(p_sType,
												p_sArgs) {

											var oBody, oElement, oShadow, oUL;

											if (this.parent) {

												oElement = this.element;

												/*
												 * Get a reference to the Menu's
												 * shadow element and set its
												 * "height" property to "0px" to
												 * syncronize it with the height
												 * of the Menu instance.
												 */

												oShadow = oElement.lastChild;
												oShadow.style.height = "0px";

												/*
												 * Stop the Animation instance
												 * if it is currently animating
												 * a Menu.
												 */

												if (oAnim && oAnim.isAnimated()) {

													oAnim.stop();
													oAnim = null;

												}

												/*
												 * Set the body element's
												 * "overflow" property to
												 * "hidden" to clip the display
												 * of its negatively positioned
												 * <ul> element.
												 */

												oBody = this.body;

												// Check if the menu is a
												// submenu of a submenu.

												if (this.parent
														&& !(this.parent instanceof YAHOO.widget.MenuBarItem)) {

													/*
													 * There is a bug in
													 * gecko-based browsers and
													 * Opera where an element
													 * whose "position" property
													 * is set to "absolute" and
													 * "overflow" property is
													 * set to "hidden" will not
													 * render at the correct
													 * width when its
													 * offsetParent's "position"
													 * property is also set to
													 * "absolute." It is
													 * possible to work around
													 * this bug by specifying a
													 * value for the width
													 * property in addition to
													 * overflow.
													 */

													if (ua.gecko || ua.opera) {

														oBody.style.width = oBody.clientWidth
																+ "px";

													}

													/*
													 * Set a width on the
													 * submenu to prevent its
													 * width from growing when
													 * the animation is
													 * complete.
													 */

													if (ua.ie == 7) {

														oElement.style.width = oElement.clientWidth
																+ "px";

													}

												}

												oBody.style.overflow = "hidden";

												/*
												 * Set the <ul> element's
												 * "marginTop" property to a
												 * negative value so that the
												 * Menu's height collapses.
												 */

												oUL = oBody
														.getElementsByTagName("ul")[0];

												oUL.style.marginTop = ("-"
														+ oUL.offsetHeight + "px");

											}

										}

										/*
										 * "tween" event handler for the Anim
										 * instance, used to syncronize the size
										 * and position of the Menu instance's
										 * shadow and iframe shim (if it exists)
										 * with its changing height.
										 */

										function onTween(p_sType, p_aArgs,
												p_oShadow) {

											if (this.cfg.getProperty("iframe")) {

												this.syncIframe();

											}

											if (p_oShadow) {

												p_oShadow.style.height = this.element.offsetHeight
														+ "px";

											}

										}

										/*
										 * "complete" event handler for the Anim
										 * instance, used to remove style
										 * properties that were animated so that
										 * the Menu instance can be displayed at
										 * its final height.
										 */

										function onAnimationComplete(p_sType,
												p_aArgs, p_oShadow) {

											var oBody = this.body, oUL = oBody
													.getElementsByTagName("ul")[0];

											if (p_oShadow) {

												p_oShadow.style.height = this.element.offsetHeight
														+ "px";

											}

											oUL.style.marginTop = "";
											oBody.style.overflow = "";

											// Check if the menu is a submenu of
											// a submenu.

											if (this.parent
													&& !(this.parent instanceof YAHOO.widget.MenuBarItem)) {

												// Clear widths set by the
												// "beforeshow" event handler

												if (ua.gecko || ua.opera) {

													oBody.style.width = "";

												}

												if (ua.ie == 7) {

													this.element.style.width = "";

												}

											}

										}

										/*
										 * "show" event handler for each submenu
										 * of the MenuBar instance - used to
										 * kick off the animation of the <ul>
										 * element.
										 */

										function onSubmenuShow(p_sType, p_sArgs) {

											var oElement, oShadow, oUL;

											if (this.parent) {

												oElement = this.element;
												oShadow = oElement.lastChild;
												oUL = this.body
														.getElementsByTagName("ul")[0];

												/*
												 * Animate the <ul> element's
												 * "marginTop" style property to
												 * a value of 0.
												 */

												oAnim = new YAHOO.util.Anim(
														oUL,
														{
															marginTop : {
																to : 0
															}
														},
														.5,
														YAHOO.util.Easing.easeOut);

												oAnim.onStart
														.subscribe(function() {

															oShadow.style.height = "100%";

														});

												oAnim.animate();

												/*
												 * Subscribe to the Anim
												 * instance's "tween" event for
												 * IE to syncronize the size and
												 * position of a submenu's
												 * shadow and iframe shim (if it
												 * exists) with its changing
												 * height.
												 */

												if (YAHOO.env.ua.ie) {

													oShadow.style.height = oElement.offsetHeight
															+ "px";

													/*
													 * Subscribe to the Anim
													 * instance's "tween" event,
													 * passing a reference
													 * Menu's shadow element and
													 * making the scope of the
													 * event listener the Menu
													 * instance.
													 */

													oAnim.onTween.subscribe(
															onTween, oShadow,
															this);

												}

												/*
												 * Subscribe to the Anim
												 * instance's "complete" event,
												 * passing a reference Menu's
												 * shadow element and making the
												 * scope of the event listener
												 * the Menu instance.
												 */

												oAnim.onComplete.subscribe(
														onAnimationComplete,
														oShadow, this);

											}

										}

										/*
										 * Subscribe to the "beforerender"
										 * event, adding a submenu to each of
										 * the items in the MenuBar instance.
										 */

										oMenuBar
												.subscribe(
														"beforeRender",
														function() {

															var nSubmenus = aSubmenuData.length, i;

															if (this.getRoot() == this) {

																for (i = 0; i < nSubmenus; i++) {
																	this
																			.getItem(i).cfg
																			.setProperty(
																					"submenu",
																					aSubmenuData[i]);
																}

															}

														});

										/*
										 * Subscribe to the "beforeShow" and
										 * "show" events for each submenu of the
										 * MenuBar instance.
										 */

										oMenuBar.subscribe("beforeShow",
												onSubmenuBeforeShow);
										oMenuBar.subscribe("show",
												onSubmenuShow);

										/*
										 * Call the "render" method with no
										 * arguments since the markup for this
										 * MenuBar instance is already exists in
										 * the page.
										 */
										oMenuBar.render();
									});
				});

/*
 * Initialize the ContextMenu instances when the the elements that trigger their
 * display are ready to be scripted.
 */

$(document).ready(
		function() {
			YAHOO.util.Event.onContentReady("menutoggleButton", function() {

				// Maintain a reference to the "clones" <ul>

				var oClones = this;

				// Clone the first ewe so that we can create more later

				var oLI = oClones.getElementsByTagName("li")[0];
				var oEweTemplate = oLI.cloneNode(true);

				// Renames an "ewe"

				function editEweName(p_oLI) {

					var oCite = p_oLI.lastChild;

					if (oCite.nodeType != 1) {

						oCite = oCite.previousSibling;

					}

					var oTextNode = oCite.firstChild;

					var sName = window.prompt("Enter a new name for ",
							oTextNode.nodeValue);

					if (sName && sName.length > 0) {

						oTextNode.nodeValue = sName;

					}

				}

				// Clones an "ewe"

				function cloneEwe(p_oLI, p_oMenu) {

					var oClone = p_oLI.cloneNode(true);

					p_oLI.parentNode.appendChild(oClone);

					p_oMenu.cfg.setProperty("trigger", oClones.childNodes);

				}

				// Deletes an "ewe"

				function deleteEwe(p_oLI) {

					var oUL = p_oLI.parentNode;

					oUL.removeChild(p_oLI);

				}

				// "click" event handler for each item in the ewe context menu

				function onEweContextMenuClick(p_sType, p_aArgs) {

					/*
					 * The second item in the arguments array (p_aArgs) passed
					 * back to the "click" event handler is the MenuItem
					 * instance that was the target of the "click" event.
					 */

					var oItem = p_aArgs[1], // The MenuItem that was clicked
					oTarget = this.contextEventTarget, oLI;

					if (oItem) {

						oLI = oTarget.nodeName.toUpperCase() == "LI" ? oTarget
								: YAHOO.util.Dom.getAncestorByTagName(oTarget,
										"LI");

						switch (oItem.index) {

						case 0: // Edit name

							editEweName(oLI);

							break;

						case 1: // Clone

							cloneEwe(oLI, this);

							break;

						case 2: // Delete

							deleteEwe(oLI);

							break;

						}

					}

				}

				/*
				 * Array of text labels for the MenuItem instances to be added
				 * to the ContextMenu instanc.
				 */

				var aMenuItems = [ "Edit Name", "Clone", "Delete" ];

				/*
				 * Instantiate a ContextMenu: The first argument passed to the
				 * constructor is the id for the Menu element to be created, the
				 * second is an object literal of configuration properties.
				 */

				var oEweContextMenu = new YAHOO.widget.ContextMenu(
						"ewecontextmenu", {
							trigger : oClones.childNodes,
							itemdata : aMenuItems,
							lazyload : true
						});

				// "render" event handler for the ewe context menu

				function onContextMenuRender(p_sType, p_aArgs) {

					// Add a "click" event handler to the ewe context menu

					this.subscribe("click", onEweContextMenuClick);

				}

				// Add a "render" event handler to the ewe context menu

				oEweContextMenu.subscribe("render", onContextMenuRender);

				// Deletes an ewe from the field

				function deleteEwes() {

					oEweContextMenu.cfg.setProperty("target", null);

					oClones.innerHTML = "";

					function onHide(p_sType, p_aArgs, p_oItem) {

						p_oItem.cfg.setProperty("disabled", true);

						p_oItem.parent.unsubscribe("hide", onHide, p_oItem);

					}

					this.parent.subscribe("hide", onHide, this);

				}

				// Creates a new ewe and appends it to the field

				function createNewEwe() {

					var oLI = oEweTemplate.cloneNode(true);

					oClones.appendChild(oLI);

					this.parent.getItem(1).cfg.setProperty("disabled", false);

					oEweContextMenu.cfg.setProperty("trigger",
							oClones.childNodes);

				}

				// Sets the color of the grass in the field

				function setFieldColor(p_sType, p_aArgs, p_sColor) {

					var oCheckedItem = this.parent.checkedItem;

					if (oCheckedItem != this) {

						YAHOO.util.Dom.setStyle("clones", "backgroundColor",
								p_sColor);

						this.cfg.setProperty("checked", true);

						oCheckedItem.cfg.setProperty("checked", false);

						this.parent.checkedItem = this;

					}

				}

				// "render" event handler for the field context menu

				function onFieldMenuRender(p_sType, p_aArgs) {

					if (this.parent) { // submenu

						this.checkedItem = this.getItem(0);

					}

				}

				/*
				 * Array of object literals - each containing configuration
				 * properties for the items for the context menu.
				 */

				var oFieldContextMenuItemData = [

				{
					text : "Field color",
					submenu : {
						id : "fieldcolors",
						itemdata : [ {
							text : "Light Green",
							onclick : {
								fn : setFieldColor,
								obj : "#99cc66"
							},
							checked : true
						}, {
							text : "Medium Green",
							onclick : {
								fn : setFieldColor,
								obj : "#669933"
							}
						}, {
							text : "Dark Green",
							onclick : {
								fn : setFieldColor,
								obj : "#336600"
							}
						} ]
					}
				}, {
					text : "Delete all",
					onclick : {
						fn : deleteEwes
					}
				}, {
					text : "New Ewe",
					onclick : {
						fn : createNewEwe
					}
				}

				];

				/*
				 * Instantiate a ContextMenu: The first argument passed to the
				 * constructor is the id for the Menu element to be created, the
				 * second is an object literal of configuration properties.
				 */

				var oFieldContextMenu = new YAHOO.widget.ContextMenu(
						"fieldcontextmenu", {
							trigger : "menutoggleImage",
							itemdata : oFieldContextMenuItemData,
							lazyload : true
						});

				// Add a "render" event handler to the field context menu

				oFieldContextMenu.subscribe("render", onFieldMenuRender);

			});
		});