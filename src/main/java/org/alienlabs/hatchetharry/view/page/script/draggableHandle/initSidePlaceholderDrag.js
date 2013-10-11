window.setTimeout(function() {
	jQuery('#sidePlaceholder${uuidValidForJs}').draggable(
			{
				handle : '#handleImage${uuidValidForJs}',
				helper : 'original',
				stop : function(event, ui) {
						Wicket.Ajax.get({'u' : '${dragUrl}&posX=' + (ui.offset.left) + '&posY=' + (ui.offset.top) });
				}
			});
}, 250);
