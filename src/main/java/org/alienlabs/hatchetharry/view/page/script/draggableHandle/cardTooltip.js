window.setTimeout(function() {
	if (typeof drawMode == 'undefined' || !drawMode) {
		jQuery('.clickableCard').click(function(e) { 
			jQuery('#cardTooltip${uuidValidForJs}').attr('style', 'display: block; position: absolute; left: ${posX}px; top: ' + (${posY} + 56) + 'px; z-index: 50;');
			jQuery('#cardTooltip${uuidValidForJs} > span').attr('style', 'display: block;'); });

		// For mobile
		var hammertime${uuidValidForJs} = jQuery('.clickableCard').hammer();
		hammertime${uuidValidForJs}.on('tap', function(ev) {
			jQuery('#cardTooltip${uuidValidForJs}').attr('style', 'display: block; position: absolute; left: '
					+ (${posX} + 127) + 'px; top: ' + (${posY} + 56)
					+ 'px; z-index: 50;');
			jQuery('#cardTooltip${uuidValidForJs} > span').attr('style', 'display: block;');
		});

		jQuery('#cardTooltip${uuidValidForJs}').hide(); 
	}
}, 500);
