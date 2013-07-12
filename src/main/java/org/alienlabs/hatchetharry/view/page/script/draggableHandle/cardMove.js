// TODO in reality, cardMove.js configures the context menu: move it in its own Behavior

window.setTimeout(function() {
			// Show menu when #myDiv is clicked
			jQuery("#card${uuidValidForJs}").contextMenu(
					{
						menu : 'contextMenu${uuidValidForJs}'
					},
					function(action, el, pos) {
						var uuid = jQuery(el).attr('id').replace('card', '');
						
						if (action === 'edit') {
							Wicket.Ajax.get({ 'u' : '${graveyardUrl}&uuid=' + uuid });
						} else if (action === 'cut') {
							Wicket.Ajax.get({ 'u' : '${handUrl}&uuid=' + uuid });
						}
					}
			);
}, 75);
