/*******************************************************************************
 * jquery.mb.components Copyright (c) 2001-2010. Matteo Bicocchi (Pupunzi); Open
 * lab srl, Firenze - Italy email: info@pupunzi.com site: http://pupunzi.com
 * 
 * Licences: MIT, GPL http://www.opensource.org/licenses/mit-license.php
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/

/*
 * Name:jquery.mb.menu Version: 2.8.5rc5
 * 
 * added: boxMenu menu modality by: Sven Dowideit
 * http://trunk.fosiki.com/Sandbox/WebSubMenu
 */
// to get the element that is fireing a contextMenu event you have
// $.mbMenu.lastContextMenuEl that returns an object.
(function($) {
	jQuery.mbMenu = {
		name : "mbMenu",
		author : "Matteo Bicocchi",
		version : "2.8.5rc5",
		actualMenuOpener : false,
		options : {
			template : "yourMenuVoiceTemplate",// the url that returns the menu
			// voices via ajax. the data
			// passed in the request is the
			// "menu" attribute value as
			// "menuId"
			additionalData : "",
			menuSelector : ".menuContainer",
			menuWidth : 400,
			openOnRight : false,
			containment : "window",
			iconPath : "ico/",
			hasImages : true,
			fadeInTime : 100,
			fadeOutTime : 200,
			menuTop : 0,
			menuLeft : 0,
			submenuTop : 0,
			submenuLeft : 4,
			opacity : 1,
			openOnClick : true,
			closeOnMouseOut : false,
			closeAfter : 500,
			minZindex : "auto", // or number
			hoverIntent : 0, // if you use jquery.hoverIntent.js set this to
			// time in milliseconds; 0= false;
			submenuHoverIntent : 200, // if you use jquery.hoverIntent.js set
			// this to time in milliseconds; 0=
			// false;
			onContextualMenu : function() {
			} // it pass 'o' (the menu you clicked on) and 'e' (the event)
		},
		buildMenu : function(options) {
			return this
					.each(function() {
						var thisMenu = this;
						thisMenu.id = !this.id ? "menu_"
								+ Math.floor(Math.random() * 1000) : this.id;
						this.options = {};
						jQuery.extend(this.options, jQuery.mbMenu.options);
						jQuery.extend(this.options, options);

						jQuery(".mbmenu").hide();
						thisMenu.clicked = false;
						thisMenu.rootMenu = false;
						thisMenu.actualOpenedMenu = false;
						thisMenu.menuvoice = false;
						var root = jQuery(this);
						var openOnClick = this.options.openOnClick;
						var closeOnMouseOut = this.options.closeOnMouseOut;

						// build roots
						jQuery(root)
								.each(
										function() {

											/*
											 * using metadata plugin you can add
											 * attribute writing them inside the
											 * class attr with a JSON sintax for
											 * ex: class="rootVoice
											 * {menu:'menu_2'}"
											 */
											if (jQuery.metadata) {
												jQuery.metadata
														.setType("class");
												thisMenu.menuvoice = jQuery(
														this)
														.find(".rootVoice");
												jQuery(thisMenu.menuvoice)
														.each(
																function() {
																	if (jQuery(
																			this)
																			.metadata().menu)
																		jQuery(
																				this)
																				.attr(
																						"menu",
																						jQuery(
																								this)
																								.metadata().menu);
																	if (jQuery(
																			this)
																			.metadata().disabled)
																		jQuery(
																				this)
																				.attr(
																						"isDisable",
																						jQuery(
																								this)
																								.metadata().disabled);
																});
											}

											thisMenu.menuvoice = jQuery(this)
													.find("[menu]")
													.add(
															jQuery(this)
																	.filter(
																			"[menu]"));
											thisMenu.menuvoice.filter(
													"[isDisable]").addClass(
													"disabled");

											jQuery(thisMenu.menuvoice).css(
													"white-space", "nowrap");

											if (openOnClick) {
												jQuery(thisMenu.menuvoice)
														.bind(
																"click",
																function() {
																	jQuery(
																			document)
																			.unbind(
																					"click.closeMbMenu");
																	if (!jQuery(
																			this)
																			.attr(
																					"isOpen")) {
																		jQuery(
																				this)
																				.buildMbMenu(
																						thisMenu,
																						jQuery(
																								this)
																								.attr(
																										"menu"));
																		jQuery(
																				this)
																				.attr(
																						"isOpen",
																						"true");
																	} else {
																		jQuery(
																				this)
																				.removeMbMenu(
																						thisMenu,
																						true);
																		jQuery(
																				this)
																				.addClass(
																						"selected");
																	}

																	// empty
																	if (jQuery(
																			this)
																			.attr(
																					"menu") == "empty") {
																		if (thisMenu.actualOpenedMenu) {
																			jQuery(
																					"[isOpen]")
																					.removeAttr(
																							"isOpen");
																		}
																		jQuery(
																				this)
																				.removeMbMenu(
																						thisMenu);
																	}
																	jQuery(
																			document)
																			.unbind(
																					"click.closeMbMenu");
																});
											}
											var mouseOver = jQuery.browser.msie ? "mouseenter"
													: "mouseover";
											var mouseOut = jQuery.browser.msie ? "mouseleave"
													: "mouseout";

											jQuery(thisMenu.menuvoice)
													.mb_hover(
															this.options.hoverIntent,
															function() {
																if (!jQuery(
																		this)
																		.attr(
																				"isOpen"))
																	jQuery(
																			"[isOpen]")
																			.removeAttr(
																					"isOpen");
																if (closeOnMouseOut)
																	clearTimeout(jQuery.mbMenu.deleteOnMouseOut);
																if (!openOnClick)
																	jQuery(
																			thisMenu)
																			.find(
																					".selected")
																			.removeClass(
																					"selected");
																if (thisMenu.actualOpenedMenu) {
																	jQuery(
																			thisMenu.actualOpenedMenu)
																			.removeClass(
																					"selected");
																}
																jQuery(this)
																		.addClass(
																				"selected");
																if ((thisMenu.clicked || !openOnClick)
																		&& !jQuery(
																				this)
																				.attr(
																						"isOpen")) {
																	jQuery(this)
																			.removeMbMenu(
																					thisMenu);
																	jQuery(this)
																			.buildMbMenu(
																					thisMenu,
																					jQuery(
																							this)
																							.attr(
																									"menu"));
																	if (jQuery(
																			this)
																			.attr(
																					"menu") == "empty") {
																		jQuery(
																				this)
																				.removeMbMenu(
																						thisMenu);
																	}
																	jQuery(this)
																			.attr(
																					"isOpen",
																					"true");
																}
															},
															function() {
																if (closeOnMouseOut)
																	jQuery.mbMenu.deleteOnMouseOut = setTimeout(
																			function() {
																				jQuery(
																						this)
																						.removeMbMenu(
																								thisMenu,
																								true);
																				jQuery(
																						document)
																						.unbind(
																								"click.closeMbMenu");
																			},
																			jQuery(root)[0].options.closeAfter);

																if (jQuery(this)
																		.attr(
																				"menu") == "empty") {
																	jQuery(this)
																			.removeClass(
																					"selected");
																}
																if (!thisMenu.clicked)
																	jQuery(this)
																			.removeClass(
																					"selected");
																jQuery(document)
																		.one(
																				"click.closeMbMenu",
																				function() {
																					jQuery(
																							"[isOpen]")
																							.removeAttr(
																									"isOpen");
																					jQuery(
																							this)
																							.removeClass(
																									"selected");
																					jQuery(
																							this)
																							.removeMbMenu(
																									thisMenu,
																									true);
																					thisMenu.rootMenu = false;
																					thisMenu.clicked = false;
																				});
															});
										});
					});
		},
		buildContextualMenu : function(options) {
			return this
					.each(function() {
						var thisMenu = this;
						thisMenu.options = {};
						jQuery.extend(thisMenu.options, jQuery.mbMenu.options);
						jQuery.extend(thisMenu.options, options);
						jQuery(".mbmenu").hide();
						thisMenu.clicked = false;
						thisMenu.rootMenu = false;
						thisMenu.actualOpenedMenu = false;
						thisMenu.menuvoice = false;

						/*
						 * using metadata plugin you can add attribut writing
						 * them inside the class attr with a JSON sintax for ex:
						 * class="rootVoice {menu:'menu_2'}"
						 */
						var cMenuEls;
						if (jQuery.metadata) {
							jQuery.metadata.setType("class");
							cMenuEls = jQuery(this).find(".cmVoice");
							jQuery(cMenuEls)
									.each(
											function() {
												if (jQuery(this).metadata().cMenu)
													jQuery(this)
															.attr(
																	"cMenu",
																	jQuery(this)
																			.metadata().cMenu);
											});
						}
						cMenuEls = jQuery(this).find("[cMenu]").add(
								jQuery(this).filter("[cMenu]"));

						jQuery(cMenuEls)
								.each(
										function() {
											jQuery(this).css({
												"-webkit-user-select" : "none",
												"-moz-user-select" : "none"
											});
											var cm = this;
											cm.id = !cm.id ? "menu_"
													+ Math
															.floor(Math
																	.random() * 100)
													: cm.id;
											jQuery(cm).css({
												cursor : "default"
											});
											jQuery(cm)
													.bind(
															"contextmenu",
															"mousedown",
															function(event) {
																event
																		.preventDefault();
																event
																		.stopPropagation();
																event.cancelBubble = true;

																jQuery.mbMenu.lastContextMenuEl = cm;

																if (jQuery.mbMenu.options.actualMenuOpener) {
																	jQuery(
																			thisMenu)
																			.removeMbMenu(
																					jQuery.mbMenu.options.actualMenuOpener);
																}
																/*
																 * add custom
																 * behavior to
																 * contextMenuEvent
																 * passing the
																 * el and the
																 * event you can
																 * for example
																 * store to
																 * global var
																 * the obj that
																 * is fireing
																 * the event
																 * mbActualContextualMenuObj=cm;
																 * 
																 * you can for
																 * example
																 * create a
																 * function that
																 * manipulate
																 * the voices of
																 * the menu you
																 * are opening
																 * according to
																 * a certain
																 * condition...
																 */

																thisMenu.options
																		.onContextualMenu(
																				this,
																				event);

																jQuery(this)
																		.buildMbMenu(
																				thisMenu,
																				jQuery(
																						this)
																						.attr(
																								"cMenu"),
																				"cm",
																				event);
																jQuery(this)
																		.attr(
																				"isOpen",
																				"true");

															});
										});
					});
		}
	};
	jQuery.fn
			.extend({
				buildMbMenu : function(op, m, type, e) {
					var msie6 = jQuery.browser.msie
							&& jQuery.browser.version == "6.0";
					var mouseOver = jQuery.browser.msie ? "mouseenter"
							: "mouseover";
					var mouseOut = jQuery.browser.msie ? "mouseleave"
							: "mouseout";
					if (e) {
						this.mouseX = jQuery(this).getMouseX(e);
						this.mouseY = jQuery(this).getMouseY(e);
					}

					if (jQuery.mbMenu.options.actualMenuOpener
							&& jQuery.mbMenu.options.actualMenuOpener != op)
						jQuery(op).removeMbMenu(
								jQuery.mbMenu.options.actualMenuOpener);
					jQuery.mbMenu.options.actualMenuOpener = op;
					if (!type || type == "cm") {
						if (op.rootMenu) {
							jQuery(op.rootMenu).removeMbMenu(op);
							jQuery(op.actualOpenedMenu).removeAttr("isOpen");
							jQuery("[isOpen]").removeAttr("isOpen");
						}
						op.clicked = true;
						op.actualOpenedMenu = this;
						jQuery(op.actualOpenedMenu).attr("isOpen", "true");
						jQuery(op.actualOpenedMenu).addClass("selected");
					}

					// empty
					if (jQuery(this).attr("menu") == "empty") {
						return;
					}

					var opener = this;
					var where = (!type || type == "cm") ? jQuery(document.body)
							: jQuery(this).parent().parent();

					var menuClass = op.options.menuSelector.replace(".", "");

					if (op.rootMenu)
						menuClass += " submenuContainer";
					if (!op.rootMenu && jQuery(opener).attr("isDisable"))
						menuClass += " disabled";

					where.append("<div class='menuDiv'><div class='"
							+ menuClass + " '></div></div>");
					this.menu = where.find(".menuDiv");
					jQuery(this.menu).css({
						width : 0,
						height : 0
					});
					if (op.options.minZindex != "auto") {
						jQuery(this.menu).css({
							zIndex : op.options.minZindex++
						});
					} else {
						jQuery(this.menu).mb_bringToFront();
					}
					this.menuContainer = jQuery(this.menu).find(
							op.options.menuSelector);

					jQuery(this.menuContainer).bind(mouseOver, function() {
						jQuery(opener).addClass("selected");
					});
					jQuery(this.menuContainer).css({
						position : "absolute",
						opacity : op.options.opacity
					});

					jQuery(this.menuContainer).attr("id", "mb_" + m).hide();

					// LITERAL MENU SUGGESTED BY SvenDowideit
					var isBoxmenu = jQuery("#" + m).hasClass("boxMenu");

					if (isBoxmenu) {
						this.voices = jQuery("#" + m).clone(true);
						this.voices.css({
							display : "block"
						});
						this.voices.attr("id", m + "_clone");
					} else {
						// TODO this will break <a rel=text> - if there are
						// nested a's
						this.voices = jQuery("#" + m).find("a").clone(true);
					}

					/*
					 * using metadata plugin you can add attribut writing them
					 * inside the class attr with a JSON sintax for ex:
					 * class="rootVoice {menu:'menu_2'}"
					 */
					if (jQuery.metadata) {
						jQuery.metadata.setType("class");
						jQuery(this.voices)
								.each(
										function() {
											if (jQuery(this).metadata().disabled)
												jQuery(this)
														.attr(
																"isdisable",
																jQuery(this)
																		.metadata().disabled);
											if (jQuery(this).metadata().img)
												jQuery(this)
														.attr(
																"img",
																jQuery(this)
																		.metadata().img);
											if (jQuery(this).metadata().menu)
												jQuery(this)
														.attr(
																"menu",
																jQuery(this)
																		.metadata().menu);
											if (jQuery(this).metadata().action)
												jQuery(this)
														.attr(
																"action",
																jQuery(this)
																		.metadata().action);
										});
					}

					// build each voices of the menu
					jQuery(this.voices)
							.each(
									function(i) {

										var voice = this;
										var imgPlace = "";

										var isText = jQuery(voice).attr("rel") == "text";
										var isTitle = jQuery(voice).attr("rel") == "title";
										var isDisabled = jQuery(voice).is(
												"[isdisable]");
										if (!op.rootMenu
												&& jQuery(opener).attr(
														"isDisable"))
											isDisabled = true;

										var isSeparator = jQuery(voice).attr(
												"rel") == "separator";

										// boxMenu SUGGESTED by Sven Dowideit
										if (op.options.hasImages && !isText
												&& !isBoxmenu) {

											var imgPath = jQuery(voice).attr(
													"img") ? jQuery(voice)
													.attr("img") : "";
											imgPath = (imgPath.length > 3 && imgPath
													.indexOf(".") > -1) ? "<img class='imgLine' src='"
													+ op.options.iconPath
													+ imgPath + "'>"
													: imgPath;
											imgPlace = "<td class='img'>"
													+ imgPath + "</td>";
										}

										var line = "<table id='"
												+ m
												+ "_"
												+ i
												+ "' class='line"
												+ (isTitle ? " title" : "")
												+ "' cellspacing='0' cellpadding='0' border='0' style='width:100%;' width='100%'><tr>"
												+ imgPlace
												+ "<td class='voice' nowrap></td></tr></table>";

										if (isSeparator)
											line = "<p class='separator' style='width:100%;'></p>";

										if (isText)
											line = "<div style='width:100%; display:table' class='line' id='"
													+ m
													+ "_"
													+ i
													+ "'><div class='voice'></div></div>";

										// boxMenu SUGGESTED by Sven Dowideit
										if (isBoxmenu)
											line = "<div style='width:100%; display:inline' class='' id='"
													+ m
													+ "_"
													+ i
													+ "'><div class='voice'></div></div>";

										jQuery(opener.menuContainer).append(
												line);

										var menuLine = jQuery(
												opener.menuContainer).find(
												"#" + m + "_" + i);
										var menuVoice = menuLine.find(".voice");
										if (!isSeparator) {
											menuVoice.append(this);
											if (jQuery(this).attr("menu")
													&& !isDisabled) {
												menuLine
														.find(".voice a")
														.wrap(
																"<div class='menuArrow'></div>");
												menuLine
														.find(".menuArrow")
														.addClass(
																"subMenuOpener");
												menuLine.css({
													cursor : "default"
												});
												this.isOpener = true;
											}
											if (isText) {
												menuVoice.addClass("textBox");
												if (jQuery.browser.msie)
													menuVoice
															.css({
																maxWidth : op.options.menuWidth
															});
												this.isOpener = true;
											}
											if (isDisabled) {
												menuLine.addClass("disabled")
														.css({
															cursor : "default"
														});
											}

											if (!(isText || isTitle
													|| isDisabled || isBoxmenu)) {
												menuLine.css({
													cursor : "pointer"
												});

												menuLine
														.bind(
																"mouseover",
																function() {
																	clearTimeout(jQuery.mbMenu.deleteOnMouseOut);
																	jQuery(this)
																			.addClass(
																					"selected");
																});

												menuLine
														.bind(
																"mouseout",
																function() {
																	jQuery(this)
																			.removeClass(
																					"selected");
																});

												menuLine
														.mb_hover(
																op.options.submenuHoverIntent,
																function(event) {
																	if (opener.menuContainer.actualSubmenu
																			&& !jQuery(
																					voice)
																					.attr(
																							"menu")) {
																		jQuery(
																				opener.menu)
																				.find(
																						".menuDiv")
																				.remove();
																		jQuery(
																				opener.menuContainer.actualSubmenu)
																				.removeClass(
																						"selected");
																		opener.menuContainer.actualSubmenu = false;
																	}
																	if (jQuery(
																			voice)
																			.attr(
																					"menu")) {
																		if (opener.menuContainer.actualSubmenu
																				&& opener.menuContainer.actualSubmenu != this) {
																			jQuery(
																					opener.menu)
																					.find(
																							".menuDiv")
																					.remove();
																			jQuery(
																					opener.menuContainer.actualSubmenu)
																					.removeClass(
																							"selected");
																			opener.menuContainer.actualSubmenu = false;
																		}
																		if (!jQuery(
																				voice)
																				.attr(
																						"action"))
																			jQuery(
																					opener.menuContainer)
																					.find(
																							"#"
																									+ m
																									+ "_"
																									+ i)
																					.css(
																							"cursor",
																							"default");
																		if (!opener.menuContainer.actualSubmenu
																				|| opener.menuContainer.actualSubmenu != this) {
																			jQuery(
																					opener.menu)
																					.find(
																							".menuDiv")
																					.remove();

																			opener.menuContainer.actualSubmenu = false;
																			jQuery(
																					this)
																					.buildMbMenu(
																							op,
																							jQuery(
																									voice)
																									.attr(
																											"menu"),
																							"sm",
																							event);
																			opener.menuContainer.actualSubmenu = this;
																		}
																		jQuery(
																				this)
																				.attr(
																						"isOpen",
																						"true");
																		return false;
																	}
																}, function() {
																});
											}
											if (isDisabled || isTitle || isText
													|| isBoxmenu) {
												jQuery(this).removeAttr("href");
												menuLine
														.bind(
																mouseOver,
																function() {
																	if (closeOnMouseOut)
																		clearTimeout(jQuery.mbMenu.deleteOnMouseOut);
																	if (opener.menuContainer.actualSubmenu) {
																		jQuery(
																				opener.menu)
																				.find(
																						".menuDiv")
																				.remove();
																		opener.menuContainer.actualSubmenu = false;
																	}
																}).css(
																"cursor",
																"default");
											}
											menuLine
													.bind(
															"click",
															function() {
																if ((jQuery(
																		voice)
																		.attr(
																				"action") || jQuery(
																		voice)
																		.attr(
																				"href"))
																		&& !isDisabled
																		&& !isBoxmenu
																		&& !isText) {
																	var target = jQuery(
																			voice)
																			.attr(
																					"target") ? jQuery(
																			voice)
																			.attr(
																					"target")
																			: "_self";
																	if (jQuery(
																			voice)
																			.attr(
																					"href")
																			&& jQuery(
																					voice)
																					.attr(
																							"href")
																					.indexOf(
																							"javascript:") > -1) {
																		jQuery(
																				voice)
																				.attr(
																						"action",
																						jQuery(
																								voice)
																								.attr(
																										"href")
																								.replace(
																										"javascript:",
																										""));
																	}
																	var link = jQuery(
																			voice)
																			.attr(
																					"action") ? jQuery(
																			voice)
																			.attr(
																					"action")
																			: "window.open('"
																					+ jQuery(
																							voice)
																							.attr(
																									"href")
																					+ "', '"
																					+ target
																					+ "')";
																	jQuery(
																			voice)
																			.removeAttr(
																					"href");
																	eval(link);
																	jQuery(this)
																			.removeMbMenu(
																					op,
																					true);
																} else {
																	jQuery(
																			document)
																			.unbind(
																					"click.closeMbMenu");
																}
															});
										}
									});

					// Close on Mouseout

					var closeOnMouseOut = jQuery(op)[0].options.closeOnMouseOut;
					if (closeOnMouseOut) {
						jQuery(opener.menuContainer)
								.bind(
										"mouseenter",
										function() {
											clearTimeout(jQuery.mbMenu.deleteOnMouseOut);
										});
						jQuery(opener.menuContainer)
								.bind(
										"mouseleave",
										function() {
											var menuToRemove = jQuery.mbMenu.options.actualMenuOpener;
											jQuery.mbMenu.deleteOnMouseOut = setTimeout(
													function() {
														jQuery(this)
																.removeMbMenu(
																		menuToRemove,
																		true);
														jQuery(document)
																.unbind(
																		"click.closeMbMenu");
													},
													jQuery(op)[0].options.closeAfter);
										});
					}

					// positioning opened
					var t = 0, l = 0;
					jQuery(this.menuContainer).css({
						minWidth : op.options.menuWidth
					});
					if (jQuery.browser.msie)
						jQuery(this.menuContainer).css("width",
								jQuery(this.menuContainer).width() + 2);

					switch (type) {
					case "sm":
						t = jQuery(this).position().top + op.options.submenuTop;

						l = jQuery(this).position().left + jQuery(this).width()
								- op.options.submenuLeft;
						break;
					case "cm":
						t = this.mouseY - 5;
						l = this.mouseX - 5;
						break;
					default:
						if (op.options.openOnRight) {
							t = jQuery(this).offset().top
									- (jQuery.browser.msie ? 2 : 0)
									+ op.options.menuTop;
							l = jQuery(this).offset().left
									+ jQuery(this).outerWidth()
									- op.options.menuLeft
									- (jQuery.browser.msie ? 2 : 0);
						} else {
							t = jQuery(this).offset().top
									+ jQuery(this).outerHeight()
									- (!jQuery.browser.mozilla ? 2 : 0)
									+ op.options.menuTop;
							l = jQuery(this).offset().left
									+ op.options.menuLeft;
						}
						break;
					}

					jQuery(this.menu).css({
						position : "absolute",
						top : t,
						left : l
					});

					if (!type || type == "cm")
						op.rootMenu = this.menu;
					jQuery(this.menuContainer).bind(mouseOut, function() {
						jQuery(document).one("click.closeMbMenu", function() {
							jQuery(document).removeMbMenu(op, true);
						});
					});

					if (op.options.fadeInTime > 0)
						jQuery(this.menuContainer)
								.fadeIn(op.options.fadeInTime);
					else
						jQuery(this.menuContainer).show();

					var wh = (op.options.containment == "window") ? jQuery(
							window).height() : jQuery(
							"#" + op.options.containment).offset().top
							+ jQuery("#" + op.options.containment)
									.outerHeight();
					var ww = (op.options.containment == "window") ? jQuery(
							window).width() : jQuery(
							"#" + op.options.containment).offset().left
							+ jQuery("#" + op.options.containment).outerWidth();

					var mh = jQuery(this.menuContainer).outerHeight();
					var mw = jQuery(this.menuContainer).outerWidth();

					var actualX = jQuery(where.find(".menuDiv:first")).offset().left
							- jQuery(window).scrollLeft();
					var actualY = jQuery(where.find(".menuDiv:first")).offset().top
							- jQuery(window).scrollTop();
					switch (type) {
					case "sm":
						if ((actualX + mw) >= ww && mw < ww) {
							l -= ((op.options.menuWidth * 2) - (op.options.submenuLeft * 2));
						}
						break;
					case "cm":
						if ((actualX + (op.options.menuWidth * 1.5)) >= ww
								&& mw < ww) {
							l -= ((op.options.menuWidth) - (op.options.submenuLeft));
						}
						break;
					default:
						if ((actualX + mw) >= ww && mw < ww) {
							l -= (jQuery(this.menuContainer).offset().left + mw)
									- ww + 18;
						}
						break;
					}
					if ((actualY + mh) >= wh - 10 && mh < wh) {
						t -= ((actualY + mh) - wh) + 10;
					}

					jQuery(this.menu).css({
						top : t,
						left : l
					});
				},

				removeMbMenu : function(op, fade) {
					if (!op)
						op = jQuery.mbMenu.options.actualMenuOpener;
					if (!op)
						return;
					if (op.rootMenu) {
						jQuery(op.actualOpenedMenu).removeAttr("isOpen")
								.removeClass("selected");
						jQuery("[isOpen]").removeAttr("isOpen");
						jQuery(op.rootMenu).css({
							width : 1,
							height : 1
						});
						if (fade)
							jQuery(op.rootMenu).fadeOut(op.options.fadeOutTime,
									function() {
										jQuery(this).remove();
									});
						else
							jQuery(op.rootMenu).remove();
						op.rootMenu = false;
						op.clicked = false;
					}
				},

				// mouse Position
				getMouseX : function(e) {
					var mouseX;
					if (jQuery.browser.msie)
						mouseX = e.clientX
								+ document.documentElement.scrollLeft;
					else
						mouseX = e.pageX;
					if (mouseX < 0)
						mouseX = 0;
					return mouseX;
				},
				getMouseY : function(e) {
					var mouseY;
					if (jQuery.browser.msie)
						mouseY = e.clientY + document.documentElement.scrollTop;
					else
						mouseY = e.pageY;
					if (mouseY < 0)
						mouseY = 0;
					return mouseY;
				},
				// get max z-inedex of the page
				mb_bringToFront : function() {
					var zi = 10;
					jQuery('*')
							.each(
									function() {
										if (jQuery(this).css("position") == "absolute"
												|| jQuery(this).css("position") == "fixed") {
											var cur = parseInt(jQuery(this)
													.css('zIndex'));
											zi = cur > zi ? parseInt(jQuery(
													this).css('zIndex')) : zi;
										}
									});

					jQuery(this).css('zIndex', zi += 10);
				},
				mb_hover : function(hoverIntent, fn1, fn2) {
					if (hoverIntent == 0)
						jQuery(this).hover(fn1, fn2);
					else
						jQuery(this).hoverIntent({
							sensitivity : 30,
							interval : hoverIntent,
							timeout : 0,
							over : fn1,
							out : fn2
						});
				}
			});
	jQuery.fn.buildMenu = jQuery.mbMenu.buildMenu;
	jQuery.fn.buildContextualMenu = jQuery.mbMenu.buildContextualMenu;
})(jQuery);