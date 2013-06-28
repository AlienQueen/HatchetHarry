// The dock
jQuery(function(){
	jQuery('#dock').jqDock();
});

// The menubar, a jQuery plugin
jQuery(function(){
	jQuery("#jMenu").jMenu({
		  openClick : false,
		  ulWidth : '250',
		  effects : {
			effectSpeedOpen : 150,
			effectSpeedClose : 150,
			effectTypeOpen : 'slide',
			effectTypeClose : 'hide',
			effectOpen : 'linear',
			effectClose : 'linear'
		  },
		  TimeBeforeOpening : 100,
		  TimeBeforeClosing : 3000,
		  animatedText : false,
		  paddingLeft: 1
		});
});

// The toolbar, a jQuery plugin
jQuery(function() {

	jQuery('#floatingbar').css({
		height : 38
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
	jQuery("#card249c4f0b_cad0_4606_b5ea_eaee8866a347").mouseover(
	function(e) {
		jQuery("#cardTooltip249c4f0b_cad0_4606_b5ea_eaee8866a347").attr('style', 'display: block');
	});
	jQuery("#cardTooltip249c4f0b_cad0_4606_b5ea_eaee8866a347").mouseover(
	function(e) {
		jQuery("#cardTooltip249c4f0b_cad0_4606_b5ea_eaee8866a347").attr('style', 'display: block');
	});
	jQuery("#cardTooltip249c4f0b_cad0_4606_b5ea_eaee8866a347").mouseout(
	function(e) {
		jQuery("#cardTooltip249c4f0b_cad0_4606_b5ea_eaee8866a347").attr('style', 'display: none');
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
		updateHandLabel();
		jQuery('#handGallery .cross-link .nav-thumb').click(updateHandLabel);
		
		// We switch the menu to full bar or small drop-down depending upon the size of the screen
		jQuery(window).resize(function() {
		    var width = viewportSize.getWidth();
			var height = viewportSize.getHeight();

			if ((typeof(width) != "undefined") && (typeof(height) != "undefined") && (null != width) && (null != height) && (width<height)) {
				// Portrait
				jQuery("#jMenu").hide();
				jQuery('.dropdownmenu').show();
			} else {
				// Landscape
				jQuery("#jMenu").show();
				jQuery('.dropdownmenu').hide();
				jQuery('.categories').hide();
			}
		});
	}, 12000);
});
