jQuery(document).ready(function() {

	jQuery("#placeholder${uuidValidForJs}").hover(function(){
		jQuery("#placeholder${uuidValidForJs}_l").css("margin", "150px 0px 0px -120px");
	}, function() {
		jQuery("#placeholder${uuidValidForJs}_l").css("margin", "-1000px 0px 0px -1000px");
	});

	jQuery("#placeholder${uuidValidForJs}_l").hover(function(){
		jQuery("#placeholder${uuidValidForJs}_l").css("margin", "150px 0px 0px -120px");
	}, function() {
			jQuery("#placeholder${uuidValidForJs}_l").css("margin", "150px 0px 0px -120px");
	});
 
	jQuery("#placeholder${uuidValidForJs}_l").click(function() {
		wicketAjaxGet('${url}&card=${uuid}', function() {
		}, null, null);
	});
	
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
	
	function callbackPlayCard${uuidValidForJs}(response) {
		if (response.transport != 'polling'
				&& response.state != 'connected'
				&& response.state != 'closed') {
			if (response.status == 200) {
				var data = response.responseBody;
				var s = data.split("~~~")[1];
				var sessionId = data.split("~~~")[0];
				if ((typeof s != "undefined")
						&& (getCookie('JSESSIONID') == sessionId)
						&& (s == '${uuid}')) {
					// We're in the play card Meteor
					jQuery('#image${uuid}').remove();
					jQuery('#cross-link${next}').click();
					jQuery('#cross-link-div${clicked}').remove();
				} else if ((typeof s != "undefined")
						&& (getCookie('JSESSIONID') != sessionId)) 
				{
							wicketAjaxGet('${url}&card=' + s + '&stop=true', function() { }, null, null);
				}
			};
		};
	};
	
	// You can set websocket, streaming or long-polling here.
	jQuery.atmosphere.subscribe(
			document.getElementById('playCard').href, callbackPlayCard${uuidValidForJs},
			jQuery.atmosphere.request = {
				transport : 'streaming'
			}
	);
	
});