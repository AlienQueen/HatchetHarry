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