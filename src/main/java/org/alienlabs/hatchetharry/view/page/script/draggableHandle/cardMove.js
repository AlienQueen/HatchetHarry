jQuery(document).ready(
		function() {

				jQuery('#card${uuid}').bubbletip(jQuery('#cardBubbleTip${uuid}'), {
					deltaDirection : 'right'
				});

			// Show menu when #myDiv is clicked
			jQuery("#card${uuid}").contextMenu(
					{
						menu : 'myMenu'
					},
					function(action, el, pos) {
						jQuery('#cardBubbleTip${uuid}').removeBubbletip();
						alert('Action: ' + action + '\n\n' + 'Element ID: '
								+ jQuery(el).attr('id') + '\n\n' + 'X: '
								+ pos.x + '  Y: ' + pos.y
								+ ' (relative to element)\n\n' + 'X: '
								+ pos.docX + '  Y: ' + pos.docY
								+ ' (relative to document)');
						jQuery(document).ready(
								function() {
									jQuery('#card${uuid}').bubbletip(
											jQuery('#cardBubbleTip${uuid}'), {
												deltaDirection : 'right'
											});
								});
					});

			function callbackCardMove${uuidValidForJs}(response) {
				if (response.transport != 'polling'
						&& response.state != 'connected'
						&& response.state != 'closed') {
					if (response.status == 200) {
						var data = response.responseBody;
						var s = data.split("&&&")[1];
						if ((typeof s != "undefined")
								&& (jQuery('#jsessionid${uuid}').val() != data
										.split("&&&")[0])
								&& (typeof data.split("&&&")[3] != 'undefined')
								&& (data.split("&&&")[3] == '${uuid}')) {
							// We're in the card move Meteor
							var card = jQuery("#menutoggleButton${uuid}");
							card.css("position", "absolute");
							card.css("left", data.split("&&&")[1]);
							card.css("top", data.split("&&&")[2]);
							// }
						}
					}
				}
			}
			// You can set websocket, streaming or long-polling here.
			jQuery.atmosphere.subscribe(
					document.getElementById('cardMove').href, callbackCardMove${uuidValidForJs},
					jQuery.atmosphere.request = {
						transport : 'streaming'
					});
		});
