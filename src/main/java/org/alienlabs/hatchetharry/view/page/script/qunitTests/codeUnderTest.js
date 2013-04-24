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
		height : 0
	}).animate({
		height : '38'
	}, 'slow');
	jQuery('.toolbarLink').tipsy({
		gravity : 's'
	});

});

jQuery(function() {
	jQuery(".gallery a[rel^='prettyPhoto']").prettyPhoto({});
});

// The drop-down menu for mobile
jQuery(function() {
	var height = jQuery(window).height();
	var width = jQuery(window).width();

	if (width>height) {
		// Landscape
		jQuery("#jMenu").show();
		jQuery('.dropdownmenu').hide();
		jQuery('.categories').hide();
	} else {
		// Portrait
		jQuery("#jMenu").hide();
		jQuery('.dropdownmenu').show();
	}
	jQuery('.dropdownmenu').click(function(){ 
		jQuery('.categories').toggle();
	});
});

// We switch the menu to full bar or small drop-down depending upon the size of the screen
jQuery(window).resize(function() {
	var height = jQuery(window).height();
	var width = jQuery(window).width();
	
	if (width>height) {
		// Landscape
		jQuery("#jMenu").show();
		jQuery('.dropdownmenu').hide();
		jQuery('.categories').hide();
	} else {
		// Portrait
		jQuery("#jMenu").hide();
		jQuery('.dropdownmenu').show();
	}
});

jQuery(function() {	
	// @see: initTooltip.js
	// TODO: re-activate Dnd + tap handle for baldu
	jQuery("#card249c4f0b_cad0_4606_b5ea_eaee8866a347").easyTooltip({
		useElement: "cardTooltip249c4f0b_cad0_4606_b5ea_eaee8866a347"				   
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
	}, 2000);
});