// The dock
jQuery(function(){
	jQuery('#dock').jqDock();
});

// The toolbar, a jQuery plugin
jQuery(function() {

	jQuery('#floatingbar').css({
		height : "38px"
	});
});

jQuery(function() {
	jQuery(".gallery a[rel^='prettyPhoto']").prettyPhoto({});
});

// The drop-down menu for mobile
jQuery(function() {
	// Landscape mode menubar by default
	jQuery("#jMenu").show();
	jQuery('.dropdownmenu').hide();
	jQuery('.categories').hide();

	jQuery('.dropdownmenu').click(function(){ 
		jQuery('.categories').toggle();
	});
});

jQuery(function() {	
	// @see: initTooltip.js
	// TODO: re-activate Dnd + tap handle for baldu
	jQuery("#card249c4f0b_cad0_4606_b5ea_eaee8866a347").click(
	function(e) {
		jQuery("#cardTooltip249c4f0b_cad0_4606_b5ea_eaee8866a347").attr('style', 'display: block');
	});
});

//The website tour, a jQuery plugin
jQuery(function() {
	tl.pg.init({ 'pg_caption' : 'Site tour', 'auto_show_first': true});
});

// Display the name of the currently displayed card in the hand
function updateHandLabel(){
	var handCardName = jQuery('#handGallery .active-thumbGraveyard .nav-thumb').attr('name');
	if (handCardName == undefined) {
		handCardName = jQuery('#handGallery .active-thumb .nav-thumb').attr('name');
	}
	jQuery('#cardLabel').text(handCardName);
};

jQuery(function() {
	setTimeout(function() {
        // Freebox
        var userAgent = jQuery("#qunit-userAgent").html();
        if (userAgent.indexOf("FbxQmlTV") != -1) {
            jQuery("#floatingbar").css("top", "-10px");
            jQuery("#cssmenu").css("top", "28px");
            jQuery(".dropdownmenu").css("top", "28px");
        }

		updateHandLabel();
		jQuery('#handGallery .cross-link .nav-thumb').click(updateHandLabel);
		
		// We switch the menu to full bar or small drop-down depending upon the size of the screen
		jQuery(window).resize(function() {
		    var width = viewportSize.getWidth();
			var height = viewportSize.getHeight();

			if ((typeof(width) != "undefined") && (typeof(height) != "undefined") && (null != width) && (null != height) && (width<height)) {
				// Portrait
				jQuery("#cssmenu").hide();
				jQuery('.dropdownmenu').show();
			} else {
				// Landscape
				jQuery("#cssmenu").show();
				jQuery('.dropdownmenu').hide();
				jQuery('.categories').hide();
			}
		});
	}, 4000);
});

// The tooltips
jQuery(function() {
	jQuery('[title]').tipsy({gravity: 's'});
});

// For cards drag + hand, graveyard & exile drop
var shouldMove = true;


// Visio-conference
jQuery(function() {
	jQuery("#conference").dialog({ autoOpen: false, position: { my: "center", at: "center" } });
	
	jQuery("[data-id='conferenceOpener']").click(function() {
		jQuery("#conference").dialog("open");
	});
	
	jQuery("[data-id='conferenceOpenerResponsive']").click(function() {
		jQuery("#conference").dialog("open");
	});
});