jQuery(document).ready(
		function() {

			// jQuery('[id^=\"createSubmit\"]').click(
			// wicketAjaxGet('${url}&title=1&text=1', function() {
			// }, null, null));
			//
			// jQuery('[id^=\"joinSubmit\"]').click(
			// wicketAjaxGet('${url}&title=2&text=2', function() {
			// }, null, null));

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
						var text = data.split("§§§")[1];
						var sessionId = data.split("§§§")[2];
						if (typeof text != "undefined") {
							// && (getCookie('JSESSIONID') != sessionId)) { //
							// We're
							// in
							// the
							// notifier
							// Meteor

							jQuery.gritter.add({
								// (string | mandatory) the heading of the
								// notification
								title : data.split("§§§")[0],
								// (string | mandatory) the text inside the
								// notification
								text : data.split("§§§")[1],
								// (string | optional) the image to display on
								// the left
								image : 'image/logoh2.gif',
								// (bool | optional) if you want it to fade out
								// on its own or
								// just sit
								// there
								sticky : false,
								// (int | optional) the time you want it to be
								// alive for before
								// fading
								// out
								time : ''
							});

							// jQuery('[id^=\"createSubmit\"]').click(
							// wicketAjaxGet(
							// '${url}&title=1&text=1&stop=true',
							// function() {
							// }, null, null));
							//
							// jQuery('[id^=\"joinSubmit\"]').click(
							// wicketAjaxGet(
							// '${url}&title=2&text=2&stop=true',
							// function() {
							// }, null, null));
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
