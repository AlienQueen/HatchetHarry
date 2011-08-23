var tapped${uuidValidForJs} = true;

jQuery(document).ready(
		function() {
			jQuery('#tapHandleImage${uuid}').click(
					function() {
						wicketAjaxGet('${url}&tapped=' + tapped${uuidValidForJs}
								+ '&uuid=${uuid}', function() {
						}, null, null);
					});

			function callbackCardRotate${uuidValidForJs}(response) {
				if (response.transport != 'polling'
						&& response.state != 'connected'
						&& response.state != 'closed') {
					if (response.status == 200) {

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
								if (tapped${uuidValidForJs}) {
									jQuery('#card${uuid}').rotate(90);
									tapped${uuidValidForJs} = false;
								} else {
									jQuery('#card${uuid}').rotate(0);
									tapped${uuidValidForJs} = true;
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