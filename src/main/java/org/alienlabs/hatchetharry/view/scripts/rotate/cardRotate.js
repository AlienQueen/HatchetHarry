var tapped = true;

jQuery(document).ready(
		function() {
			jQuery('#tapHandleImage${uuid}').click(
					function() {
						wicketAjaxGet('${url}&tapped=' + tapped
								+ '&uuid=${uuid}', function() {
						}, null, null);
					});

			function callbackCardRotate${uuidValidForJs}(response) {
				if (response.transport != 'polling'
						&& response.state != 'connected'
						&& response.state != 'closed') {
					if (response.status == 200) {
						// we
						// in
						// the
						// clock
						// meteor?

						if ((response.responseBody.indexOf("false") != -1)
								|| (response.responseBody.indexOf("true") != -1)) {
							// We're
							// in
							// the
							// card
							// rotate
							// Meteor
							var data = response.responseBody;
							if ((typeof data.split("___")[1] != 'undefined')
									&& (data.split("___")[1] == '${uuid}'))
								if (tapped) {
									jQuery('#card${uuid}').rotate(90);
									tapped = false;
								} else {
									jQuery('#card${uuid}').rotate(0);
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
					callbackCardRotate${uuidValidForJs}, jQuery.atmosphere.request = {
						transport : 'streaming'
					});
		});