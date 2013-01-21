window.setTimeout(function() {
			// Show menu when #myDiv is clicked
			jQuery("#card${uuid}").contextMenu(
					{
						menu : 'myMenu'
					},
					function(action, el, pos) {
						var uuid = jQuery(el).attr('id').replace('card', '');
						
						if (action === 'edit') {
							Wicket.Ajax.get({ 'u' : '${graveyardUrl}&uuid=' + uuid });
						} else if (action === 'cut') {
							Wicket.Ajax.get({ 'u' : '${handUrl}&uuid=' + uuid });
						}
//						alert('Action: ' + action + '\n\n' + 'Element ID: '
//								+ jQuery(el).attr('id') + '\n\n' + 'X: '
//								+ pos.x + '  Y: ' + pos.y
//								+ ' (relative to element)\n\n' + 'X: '
//								+ pos.docX + '  Y: ' + pos.docY
//								+ ' (relative to document)');
					}
			);
}, 5000);
