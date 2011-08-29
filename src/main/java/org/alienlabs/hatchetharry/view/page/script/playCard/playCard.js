
jQuery(document).ready(function() {

// jQuery("#page-wrap").hover(function(){
// jQuery("#placeholder${uuidValidForJs}_img").css("margin", "150px 0px 0px
// -120px");
// }, function() {
// jQuery("#placeholder${uuidValidForJs}_img").css("margin", "-1000px 0px 0px
// -1000px");
// });
//
// jQuery("#placeholder${uuidValidForJs}_img").hover(function(){
// jQuery("#placeholder${uuidValidForJs}_img").css("margin", "150px 0px 0px
// -120px");
// }, function() {
// jQuery("#placeholder${uuidValidForJs}_img").css("margin", "150px 0px 0px
// -120px");
// });
 
// jQuery("#cross-link${clicked}").click(function() {
// wicketAjaxGet('${url}&card=${uuid}', function() {
// }, null, null);
// });
	
	jQuery("#playCardLink${clicked}").click(function () {
		wicketAjaxGet('${url}&card=${uuid}&indexOfClickedCard=${clicked}' , function() {
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
				var uuid = data.split("~~~")[1];
				var sessionId = data.split("~~~")[0];
				
				if (data.split("~~~").length > 0) {
					if ((typeof uuid != "undefined") 
							&& (getCookie('JSESSIONID') == sessionId))
							{
							// We're in the play card Meteor
							jQuery('#cross-link${next}').click();
							jQuery('#cross-link-div${clicked}').remove();
							jQuery('#placeholder${uuidValidForJs}').remove();
		
						} else if ((typeof uuid != "undefined")
								&& (getCookie('JSESSIONID') != sessionId)) 
						{
									wicketAjaxGet('${url}&card=' + uuid + '&stop=true&indexOfClickedCard=' + data.split("~~~")[2], function() { }, null, null);
						};
					};
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