/*
 * Initialize and render the MenuBar when its elements are ready to be scripted.
 */
function teamInfoCallback(p_sType, p_aArgs, p_oValue) {
	wicketAjaxGet('${url_for_team_info}', function() {
	}, null, null);
}

jQuery(document)
		.ready(
				function() {
					YAHOO.util.Event
							.onContentReady(
									"menubar",
									function() {

										/*
										 * Instantiate a MenuBar: The first
										 * argument passed to the constructor is
										 * the id for the Menu element to be
										 * created, the second is an object
										 * literal of configuration properties.
										 */

										/*
										 * Define an array of object literals,
										 * each containing the data necessary to
										 * create a submenu.
										 */

										var aSubmenuData = [
												{
													text : "<em id=\"logo\">HatchetHarry</em>",
													submenu : {
														id : "logo",
														itemdata : [
																[ {
																	text : "About HatchetHarry",
																	url : "#"
																} ],
																[ {
																	text : "HatchetHarry Team Info",
																	url : "${url_for_team_info}"
																} ],
																[ {
																	text : "Preferences",
																	url : "#"
																} ] ]
													}
												},
												{
													text : "Documentation",
													submenu : {
														id : "Documentation",
														itemdata : [
																[ {
																	text : "Official Magic(tm) rules",
																	url : "http://360.yahoo.com"
																} ],
																[ {
																	text : "HatchetHarry documentation",
																	url : "http://alerts.yahoo.com"
																} ],
																[ {
																	text : "Browse cards database",
																	url : "http://avatars.yahoo.com"
																} ],
																[ {
																	text : "New & old rules",
																	submenu : {
																		id : "newrules",
																		itemdata : [
																				{
																					text : "Layers",
																					url : "http://mail.yahoo.com"
																				},
																				{
																					text : "Mana pool",
																					url : "http://addressbook.yahoo.com"
																				},
																				{
																					text : "Stack",
																					url : "http://calendar.yahoo.com"
																				},
																				{
																					text : "The game wins",
																					url : "http://notepad.yahoo.com"
																				},
																				{
																					text : "Infinite manas, life, tokens and combo",
																					url : "http://notepad.yahoo.com"
																				} ]
																	}
																} ] ]
													}
												},
												{
													text : "Game",
													submenu : {
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
													}
												} ];

										var oMenuBar = new YAHOO.widget.MenuBar(
												"menubar", {
													autosubmenudisplay : true,
													hidedelay : 750,
													lazyload : true,
													itemdata : aSubmenuData
												});

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