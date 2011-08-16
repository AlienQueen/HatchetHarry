var tapped = true;

jQuery(document).ready(
		function() {
			jQuery('#tapHandleImage').click(function() {
				wicketAjaxGet('${url}&tapped=' + tapped, function() {
				}, null, null);
			});

			function callbackCardRotate(response) {
				if (response.transport != 'polling'
						&& response.state != 'connected'
						&& response.state != 'closed') {
					if (response.status == 200) {
						// we
						// in
						// the
						// clock
						// meteor?

						if ((response.responseBody == "false")
								|| (response.responseBody == "true")) {
							// We're
							// in
							// the
							// card
							// rotate
							// Meteor
							if (tapped) {
								jQuery('#card').rotate(90);
								tapped = false;
							} else {
								jQuery('#card').rotate(0);
								tapped = true;
							}
						}
						jQuery.atmosphere.log('info',
								[ "response.responseBody: "
										+ response.responseBody ]);
					}
				}
			}
			// You can set websocket, streaming or long-polling here.
			jQuery.atmosphere.subscribe(
					document.getElementById('cardRotate').href,
					callbackCardRotate, jQuery.atmosphere.request = {
						transport : 'streaming'
					});
		});