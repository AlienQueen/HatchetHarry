// The context menu, which appears when clicking on a card (a YUI widget)
$(document).ready(
		function() {
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
				 * The second item in the arguments array (p_aArgs) passed back
				 * to the "click" event handler is the MenuItem instance that
				 * was the target of the "click" event.
				 */

				var oItem = p_aArgs[1], // The MenuItem that was clicked
				oTarget = this.contextEventTarget, oLI;

				if (oItem) {

					oLI = oTarget.nodeName.toUpperCase() == "LI" ? oTarget
							: YAHOO.util.Dom
									.getAncestorByTagName(oTarget, "LI");

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
			 * Array of text labels for the MenuItem instances to be added to
			 * the ContextMenu instanc.
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

				oEweContextMenu.cfg.setProperty("trigger", oClones.childNodes);

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

// The dock: a jQuery plugin
$(document).ready(function() {
	jQuery('#dock').jqDock({
		align : 'middle'
	});
});

// The menubar, a jQuery plugin
$(document).ready(function() {

	jQuery(".myMenu").buildMenu({
		additionalData : "pippo=1",
		menuWidth : 200,
		openOnRight : false,
		menuSelector : ".menuContainer",
		iconPath : "/image/",
		hasImages : true,
		fadeInTime : 100,
		fadeOutTime : 300,
		adjustLeft : 0,
		minZindex : "auto",
		adjustTop : 0,
		opacity : .95,
		shadow : true,
		shadowColor : "#ccc",
		hoverIntent : 0,
		openOnClick : false,
		closeOnMouseOut : false,
		closeAfter : 1000,
		submenuHoverIntent : 200
	});
});

// The website tour, a jQuery plugin
$(document)
		.ready(
				function() {
					// if (!(jQuery.Storage.get("tour") == "true")) {
					var config = {
						mainTitle : "First time here?",
						saveCookie : true,
						steps : [
								{
									"name" : "tour_1",
									"bgcolor" : "#444444",
									"color" : "white",
									"position" : "TL",
									"text" : "This is the menubar, where you'll find most of the options of HatchetHarry, logically grouped into menu entries.",
									"time" : 10000
								},
								{
									"name" : "tour_2",
									"bgcolor" : "#444444",
									"color" : "white",
									"text" : "This is a card. You can move it by drag and drop using the small, green handle. If you do so, the opponent will see its move matched in its own browser. Additionaly, you can call a context menu by right-clicking of the card itself.",
									"position" : "TL",
									"time" : 15000
								},
								{
									"name" : "tour_3",
									"bgcolor" : "#444444",
									"color" : "white",
									"text" : "This is the reference clock. It displays the hour on the server, so it's the same hour for every player.",
									"position" : "BL",
									"time" : 10000
								},
								{
									"name" : "tour_4",
									"bgcolor" : "#444444",
									"color" : "white",
									"text" : "This is your hand. You can browse the cards in it and play one of them.",
									"position" : "TL",
									"time" : 10000
								},
								{
									"name" : "tour_5",
									"bgcolor" : "#444444",
									"color" : "white",
									"text" : "This is the dock. You can show the different zones of Magic using its icons.",
									"position" : "BL",
									"time" : 10000
								},
								{
									"name" : "tour_6",
									"bgcolor" : "#444444",
									"color" : "white",
									"text" : "Hide or display your hand using this button.",
									"position" : "L",
									"time" : 10000
								},
								{
									"name" : "tour_7",
									"bgcolor" : "#444444",
									"color" : "white",
									"text" : "Browse through your graveyard using this button. Your opponent will be notified of this action.",
									"position" : "L",
									"time" : 10000
								},
								{
									"name" : "tour_8",
									"bgcolor" : "#444444",
									"color" : "white",
									"text" : "Browse through the exilded cards using this button. Your opponent will be notified of this action.",
									"position" : "L",
									"time" : 10000
								},
								{
									"name" : "tour_9",
									"bgcolor" : "#444444",
									"color" : "white",
									"text" : "Hide every zone of the game except the battlefield using this button. Only the permanents remain, so that you can have a vista of the game. If you click here again, the previously displayed zones will be restored, at the same places.",
									"position" : "L",
									"time" : 15000
								},
								{
									"name" : "tour_10",
									"bgcolor" : "#444444",
									"color" : "white",
									"text" : "Browse through your library using this button. Your opponent will be notified of this action. Beware: you're supposed to play fair! Don't forget the rules of the game.",
									"position" : "L",
									"time" : 10000
								},
								{
									"name" : "tour_11",
									"bgcolor" : "#444444",
									"color" : "white",
									"text" : "This is a chat. Its behavior is standard and you DON'T need to refresh your browser in order to receive new messages.",
									"position" : "TL",
									"time" : 10000
								} ]
					};

					jQuery.tour.start(config);
					// }
				});

// The toolbar, a jQuery plugin
$(function() {

	jQuery('#floatingbar').css({
		height : 0
	}).animate({
		height : '38'
	}, 'slow');
	jQuery('.toolbarLink').tipsy({
		gravity : 's'
	});

});

$(document).ready(function() {
	jQuery(".gallery a[rel^='prettyPhoto']").prettyPhoto({});
});