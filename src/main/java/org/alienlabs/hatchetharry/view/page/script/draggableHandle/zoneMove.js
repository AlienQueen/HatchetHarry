jQuery(function() { 
	window.setTimeout(function() {
		jQuery('#${component}').draggable({ handle : '#${handle}', helper : 'original'}); 
		jQuery('#${component}').attr('style', 'position: absolute; top: 28%; left: 0%;');
		jQuery('#${handle}').attr('style', 'position: relative; top: 40px; left: 0%; z-index: 10; cursor: move;');
	}, 800);
});