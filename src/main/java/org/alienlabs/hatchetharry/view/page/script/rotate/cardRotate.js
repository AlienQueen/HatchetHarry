jQuery(document).ready(function() {
	jQuery('#tapHandleImage${uuid}').click(function() {
		wicketAjaxGet('${url}&uuid=${uuid}', function() {
		}, null, null);
	});

	// function callbackCardRotate${uuidValidForJs}(response) {
	// if (response.transport != 'polling'
	// && response.state != 'connected'
	// && response.state != 'closed') {
	// if (response.status == 200) {
	//
	// if ((response.responseBody.indexOf("false") != -1)
	// || (response.responseBody.indexOf("true") != -1)) {
	// // We're
	// // in
	// // the
	// // card
	// // rotate
	// // Meteor
	// var data = response.responseBody;
	// if ((typeof data.split("___")[1] != 'undefined')
	// && (data.split("___")[1] == '${uuid}'))
	// if (response.responseBody.indexOf("true") != -1) {
	// jQuery('#card${uuid}').rotate(90);
	// } else {
	// jQuery('#card${uuid}').rotate(0);
	// }
	// }
	// jQuery.atmosphere.log('info',
	// [ "response.responseBody: "
	// + response.responseBody ]);
	// }
	// }
	// }
	// You can set websocket, streaming or long-polling here.
	// jQuery.atmosphere.subscribe(
	// document.getElementById('cardRotate').href,
	// callbackCardRotate${uuidValidForJs}, jQuery.atmosphere.request = {
	// transport : 'streaming'
	// });

	var request = {
		url : '${url}',
		logLevel : 'debug',
		transport : 'websocket',
		fallbackTransport : 'long-polling'
	};

	request.onMessage = function(response) {
		if (response.status == 200) {
			var data = response.responseBody;
			if (data.length > 0) {
				if (response.responseBody.indexOf("true") != -1) {
					jQuery('#card${uuid}').rotate(90);
				} else {
					jQuery('#card${uuid}').rotate(0);
				}
			}
		}
	}
	subSocket = socket.subscribe(request);
});