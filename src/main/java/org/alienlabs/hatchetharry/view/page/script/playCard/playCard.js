var canHideLink = true;

showLink = function() {
	jQuery("#${uuid}_l").css("visibility", "visible");
};

hideLink = function() {
	if (canHideLink) {
		jQuery("#${uuid}_l").css("visibility", "hidden");
	}
};

dontHideLink = function() {
	jQuery("#${uuid}_l").css("visibility", "visible");
	canHideLink = false;
	jQuery("#${uuid}_l").css("visibility", "visible");
};

canHideLink = function() {
	canHideLink = true;
};

jQuery("#${uuid}_l").mouseenter(dontHideLink).mouseleave(canHideLink);
jQuery("#${uuid}").mouseenter(showLink).mouseleave(hideLink);

jQuery("#${uuid}_l").click(function() {
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
// wicketAjaxGet('${url}&card=${uuid}&stop=true' , function() { }, null, null);
			} else if ((typeof s != "undefined")
					&& (getCookie('JSESSIONID') != sessionId)) 
			{
// jQuery('#image${uuid}').remove();
// jQuery('#cross-link${next}').click();
// jQuery('#cross-link-div${clicked}').remove();
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