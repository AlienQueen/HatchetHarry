jQuery(document).ready(
		function() {

			function getCookie(c_name) {
				var i, x, y, ARRcookies = document.cookie.split(";");
				for (i = 0; i < ARRcookies.length; i++) {
					x = ARRcookies[i].substr(0, ARRcookies[i].indexOf("="));
					y = ARRcookies[i].substr(ARRcookies[i].indexOf("=") + 1);
					x = x.replace(/^\s+|\s+$/g, "");
					if (x == c_name) {
						return unescape(y);
					}
				}
			}

			function callbackNotifier(response) {
				if (response.transport != 'polling'
						&& response.state != 'connected'
						&& response.state != 'closed') {
					if (response.status == 200) {
						var data = response.responseBody;
						var sessionId = data.split(":::")[2];
						if ((typeof sessionId != "undefined")
								&& (getCookie('JSESSIONID') == sessionId)) {
							// We're
							// in
							// the
							// notifier
							// Meteor
							jQuery.gritter.add({
								title : data.split(":::")[0],
								text : data.split(":::")[1],
								image : 'image/logoh2.gif',
								sticky : false,
								time : ''
							});
						}
					}
				}
			}

			// You can set websocket, streaming or long-polling here.
			jQuery.atmosphere.subscribe(document
					.getElementById('notifierStart').href, callbackNotifier,
					jQuery.atmosphere.request = {
						transport : 'streaming'
					});
		});
