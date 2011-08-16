jQuery(document).ready(
		function() {
			function callbackCardMove(response) {
				if (response.transport != 'polling'
						&& response.state != 'connected'
						&& response.state != 'closed') {
					if (response.status == 200) {
						var data = response.responseBody;
						var s = data.split("$$$")[1];
						if ((typeof s != "undefined")
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
