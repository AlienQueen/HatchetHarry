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
			
			function callbackDataBox${jsessionid}(response) {
				if (response.transport != 'polling'
					&& response.state != 'connected'
					&& response.state != 'closed') {
					if (response.status == 200) {
						var data = response.responseBody;
						var s = data.split("+++++")[1];
						
						if ((typeof s != "undefined")
								&& (getCookie('JSESSIONID') != s)) {
							// We're in the DataBox Meteor
							wicketAjaxGet('${url}&stop=true&notify=true&jsessionid=' + getCookie('JSESSIONID'), function() { }, null, null);
						}
						
						var t = data.split("§§§")[1];
						var u = data.split("###")[1];
						
						if ((typeof t != "undefined") && (typeof u == "undefined")) {
							jQuery.gritter.add({
								title : data.split("§§§")[0],
								text : data.split("§§§")[1],
								image : 'image/logoh2.gif',
								sticky : false,
								time : ''
							});
						}
						
						var v = data.split("%%%")[1];
						
						if (typeof v != "undefined") {
							wicketAjaxGet('${url}', function() { }, null, null);
						}
					}
				}
			}
			
			// You can set websocket, streaming or long-polling here.
			jQuery.atmosphere.subscribe(
				document.getElementById('updateDataBox').href, callbackDataBox${jsessionid},
				jQuery.atmosphere.request = {
					transport : 'streaming'
			});
});