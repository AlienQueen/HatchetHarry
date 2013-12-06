jQuery(function() { 
	window.setTimeout(function() {
		jQuery('._jsPlumb_connector').remove(); jQuery('._jsPlumb_overlay').remove(); jQuery('._jsPlumb_endpoint').remove();

		if (typeof arrows == 'undefined') {
			arrows = new Array();
			cardAlreadySelected = false;
		}
		if (!${drawMode}) {
			jQuery('.clickableCard').unbind('click'); jQuery('.clickableCard').unbind('tap'); 
		} else {
			for (var index = 0; index < arrows.length; index++) {
				var e0 = jsPlumb.addEndpoint(arrows[index]['source']);
				var e1 = jsPlumb.addEndpoint(arrows[index]['target']);

				jsPlumb.connect({ source:e0, target:e1, connector:['Bezier', { curviness:70 }], overlays : [
				                                                                                            ['Label', {location:0.7, id:'label', events:{ } }], ['Arrow', {
				                                                                                            	cssClass:'l1arrow',  location:0.5, width:40,length:40 }]]}); }

			var plumbSource, plumbTarget;
			jQuery('.clickableCard').click(function (event) {
				if (cardAlreadySelected) {
					cardAlreadySelected = false;
					plumbTarget = jQuery('#' + event.target.id).parent().parent().parent().parent().attr('id');

					Wicket.Ajax.get({ 'u' : jQuery('#' + plumbTarget).data('arrowDrawUrl') + '&source=' + plumbSource + '&target=' + plumbTarget});
				} else {
					cardAlreadySelected = true;
					plumbSource = jQuery('#' + event.target.id).parent().parent().parent().parent().attr('id');
				}});
		}
	} , 125);
});