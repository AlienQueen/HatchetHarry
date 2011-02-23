jQuery(document).ready(
		function() {
			function callbackCardMove(response) {
				// Websocket events.
				jQuery.atmosphere.log('info', [ "response.state: "
						+ response.state ]);
				jQuery.atmosphere.log('info', [ "response.transport: "
						+ response.transport ]);
				if (response.transport != 'polling'
						&& response.state != 'connected'
						&& response.state != 'closed') {
					jQuery.atmosphere.log('info', [ "response.responseBody: "
							+ response.responseBody ]);
					if (response.status == 200) {
						var data = response.responseBody;
						if ((typeof (data.split("$$$")[1]) != "undefined")
								&& (jQuery('#jsessionid').val() != data
										.split("$$$")[0])) { // We're
							// in
							// the
							// card
							// move
							// Meteor
							// if ((data.split("$$$")[0].toString()) !=
							// (jQuery("jsessionid")
							// .val())) {
							var card = jQuery("#menutoggleButton");
							card.css("position", "absolute");
							card.css("left", data.split("$$$")[1]);
							card.css("top", data.split("$$$")[2]);
							// }
						}
					}
				}
			}
			// You can set websocket, streaming or long-polling here.
			jQuery.atmosphere.subscribe(
					document.getElementById('cardMove').href, callbackCardMove,
					jQuery.atmosphere.request = {
						transport : 'streaming'
					});
		});
