jQuery(document).ready(
		function() {

			function callbackNotifier(response) {
				if (response.transport != 'polling'
						&& response.state != 'connected'
						&& response.state != 'closed') {
					if (response.status == 200) {
						var data = response.responseBody;
						var sessionId = data.split(":::")[2];
						if (typeof sessionId != "undefined") {
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
