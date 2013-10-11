window.setTimeout(function() {
	jQuery('#sidePlaceholder${uuidValidForJs}').draggable(
			{
				handle : '#handleImage${uuidValidForJs}',
				helper : 'original',
				stop : function() {
						Wicket.Ajax.get({'u' : '${dragUrl}&posX=' + (jQuery('#handleImage${uuidValidForJs}').offset().left) + '&posY=' + (jQuery('#handleImage${uuidValidForJs}').offset().top) });
				}
			});
}, 250);
