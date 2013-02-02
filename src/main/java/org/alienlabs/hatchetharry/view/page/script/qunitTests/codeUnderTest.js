// The menubar, a jQuery plugin
jQuery(function(){
	jQuery("#jMenu").jMenu({
		  openClick : false,
		  ulWidth : 'auto',
		  effects : {
			effectSpeedOpen : 150,
			effectSpeedClose : 150,
			effectTypeOpen : 'slide',
			effectTypeClose : 'hide',
			effectOpen : 'linear',
			effectClose : 'linear'
		  },
		  TimeBeforeOpening : 100,
		  TimeBeforeClosing : 11,
		  animatedText : false,
		  paddingLeft: 1
		});
});
//jQuery(function() {
//
//	jQuery('.myMenu').buildMenu({
//		additionalData : 'pippo=1',
//		menuWidth : 200,
//		openOnRight : false,
//		menuSelector : '.menuContainer',
//		iconPath : '/image/',
//		hasImages : true,
//		fadeInTime : 100,
//		fadeOutTime : 3000,
//		adjustLeft : 0,
//		minZindex : 'auto',
//		adjustTop : 0,
//		opacity : .95,
//		shadow : true,
//		shadowColor : '#ccc',
//		hoverIntent : 0,
//		openOnClick : false,
//		closeOnMouseOut : true,
//		closeAfter : 5000,
//		submenuHoverIntent : 200
//	});
//});

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

//The website tour, a jQuery plugin
jQuery(window).load(function() {
	jQuery(this).joyride();
});