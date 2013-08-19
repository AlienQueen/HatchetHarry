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
							jQuery('#' + jQuery(el).attr('id').replace('card', 'cardHandle')).hide();
							Wicket.Ajax.get({ 'u' : '${graveyardUrl}&uuid=' + uuid });
						} else if (action === 'cut') {
							jQuery('#' + jQuery(el).attr('id').replace('card', 'cardHandle')).hide();
							Wicket.Ajax.get({ 'u' : '${handUrl}&uuid=' + uuid });
						} else if (action === 'copy') {
                            var body = document.getElementById('body');
                            body.style.cursor='move';
                            jQuery("body").click(function(event) {
                                var url = jQuery('#handleImage' + uuid).data('dragUrl');
                                body.style.cursor="default";
                                Wicket.Ajax.get({ 'u' : url + '&uuid=' + uuid + '&posX=' + event.pageX + '&posY=' + event.pageY});
                                jQuery("body").unbind('click');
                            });
                        }
					}
			);
}, 175);
