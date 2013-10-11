jQuery(document).ready(
		function() {

			function getCookie(c_name)
			{
				var i,x,y,ARRcookies=document.cookie.split(";");
				for (i=0;i<ARRcookies.length;i++)
				{
					x=ARRcookies[i].substr(0,ARRcookies[i].indexOf("="));
					y=ARRcookies[i].substr(ARRcookies[i].indexOf("=")+1);
					x=x.replace(/^\s+|\s+$/g,"");
					if (x==c_name)
					{
						return unescape(y);
					}
				}
			}

			function callbackSidePlaceholderMove${uuidValidForJs}(response) {
				if (response.transport != 'polling'
					&& response.state != 'connected'
						&& response.state != 'closed') {
					if (response.status == 200) {
						var data = response.responseBody;

						if ((typeof data.split("|||||")[1] != "undefined")
								&& (getCookie('JSESSIONID') != data.split("|||||")[1])
								&& (data.split("|||||").length == 3)) {
							var req = "${url}&id=${jsessionid}&requestingId="
								+ data.split("|||||")[1]
							+ "&side=" + data.split("|||||")[0]
							+ "&uuid=" + data.split("|||||")[2]
							+ "&stop=true";
							Wicket.Ajax.get(req, function() { }, null, null);
						}

						if ((typeof data.split("|||||")[1] != "undefined")
								&& (getCookie('JSESSIONID') != data.split("|||||")[1])
								&& (data.split("|||||").length == 5)
								&& (typeof data.split("|||||")[3] != 'undefined')
								&& (data.split("|||||")[2] == '${uuid}')) {
							// We're in the card move Meteor
							var card = jQuery("#sidePlaceholder${uuid}");
							card.css("position", "absolute");
							card.css("left", data.split("|||||")[3]);
							card.css("top", data.split("|||||")[4]);
							// }
						}
					}
				}
			}
			// You can set websocket, streaming or long-polling here.
			// TODO: use wicket-atmosphere
//			jQuery.atmosphere.subscribe(
//					document.getElementById("sidePlaceholderMove").href, callbackSidePlaceholderMove${uuidValidForJs},
//					jQuery.atmosphere.request = {
//							transport : 'streaming'
//			});
		});