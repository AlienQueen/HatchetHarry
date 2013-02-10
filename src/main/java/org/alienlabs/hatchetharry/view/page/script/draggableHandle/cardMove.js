// TODO in reality, cardMove.js configures the context menu: move it in its own Behavior

window.setTimeout(function() {
			// Show menu when #myDiv is clicked
			jQuery("#card${uuidValidForJs}").contextMenu(
					{
						menu : 'myMenu'
					},
					function(action, el, pos) {
						var uuid = jQuery(el).attr('id').replace('card', '');
						
						if (action === 'edit') {
							Wicket.Ajax.get({ 'u' : '${graveyardUrl}&uuid=' + uuid }); // TODO: uuid or uuidValidForJs???
						} else if (action === 'cut') {
							Wicket.Ajax.get({ 'u' : '${handUrl}&uuid=' + uuid }); // TODO: uuid or uuidValidForJs???
						}
//						alert('Action: ' + action + '\n\n' + 'Element ID: '
//								+ jQuery(el).attr('id') + '\n\n' + 'X: '
//								+ pos.x + '  Y: ' + pos.y
//								+ ' (relative to element)\n\n' + 'X: '
//								+ pos.docX + '  Y: ' + pos.docY
//								+ ' (relative to document)');
					}
			);
}, 2000);
