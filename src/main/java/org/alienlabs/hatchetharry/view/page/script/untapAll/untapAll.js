jQuery(document)
		.ready(
				function() {

					function getCookie(c_name) {
						var i, x, y, ARRcookies = document.cookie.split(";");
						for (i = 0; i < ARRcookies.length; i++) {
							x = ARRcookies[i].substr(0, ARRcookies[i]
									.indexOf("="));
							y = ARRcookies[i]
									.substr(ARRcookies[i].indexOf("=") + 1);
							x = x.replace(/^\s+|\s+$/g, "");
							if (x == c_name) {
								return unescape(y);
							}
						}
					}

					function callbackUntapAll(response) {
						if (response.transport != 'polling'
								&& response.state != 'connected'
								&& response.state != 'closed') {
							if (response.status == 200) {

								var data = response.responseBody;

								if (typeof data.split("_____")[1] != "undefined") {
									// We're in the untap all Meteor

									var cards = "";
									for ( var i = 1; i < data.split("___").length; i++) {
										cards = cards + "_____"
												+ data.split("_____")[i];
									}
									var playerId = data.split("&playerId=")[1];
									wicketAjaxGet('${url}&playerId=' + playerId
											+ '&cards=' + cards, function() {
									}, null, null);
								}
							}
							;
						}
						;
					}
					;

					// You can set websocket, streaming or long-polling here.
					jQuery.atmosphere.subscribe(document
							.getElementById('untapAll').href, callbackUntapAll,
							jQuery.atmosphere.request = {
								transport : 'streaming'
							});

				});