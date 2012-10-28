
jQuery(document).ready(function() {
	
 jQuery("#placeholder${uuidValidForJs}").hover(function(){
	 jQuery("#linkOnCard").css("margin", "150px 0px 0px -120px");
 }, function() {
	 jQuery("#linkOnCard").css("margin", "-1000px 0px 0px -1000px");
 });

 jQuery("#linkOnCard").hover(function(){
	 jQuery("#linkOnCard").css("margin", "150px 0px 0px -120px");
 }, function() {
	 jQuery("#linkOnCard").css("margin", "150px 0px 0px -120px");
 });
	
	jQuery("#playCardLink${clicked}").click(function () {
		var img = jQuery(".active-thumb img");
		var id = img.attr('id');
		var withoutPlaceholder = id.split('placeholder')[1];
		var withoutImg = withoutPlaceholder.split('_img')[0];
		var uuid = withoutImg.replace(/_/g,"-");
		
		var a = jQuery(".active-thumb");
		var aId = a.attr('id');
		var currentCard = aId.split("cross-link")[1];
		
		Wicket.Ajax.get({'u': '${url}&card=' + uuid + '&indexOfClickedCard=' + currentCard + '&side=${side}'});
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
				var id = data.split("~~~")[1];
				var sessionId = data.split("~~~")[0];
				
				if (data.split("~~~").length > 0) {
					if ((typeof id != "undefined") 
							&& (getCookie('JSESSIONID') == sessionId)) 
							{
							// We're in the play card Meteor
							var a = jQuery(".active-thumb");
							var aId = a.attr('id');
							var currentCard = aId.split("cross-link")[1];
							var nextCard = (currentCard == 6 ? 0 : (currentCard + 1));
							
							jQuery('#cross-link-div' + currentCard).remove();
							jQuery('#placeholder' + currentCard).remove();
							jQuery('#cross-link' + nextCard).addClass("active-thumb");
							
							var posY;
							if (-1 == data.indexOf("#####")) {
								posY = data.split("~~~")[5];
							} else {
								posY = data.split("~~~")[5].split("1#####")[0];
							}
							Wicket.Ajax.get('${url}&card=' + id + '&stop=true&indexOfClickedCard=' + data.split("~~~")[2]
							+ '&side=' + data.split("~~~")[3] + '&posX=' + data.split("~~~")[4] + '&posY=' + posY, function() { }, null, null);
						} else if ((typeof id != "undefined")
								&& (getCookie('JSESSIONID') != sessionId)) 
						{
							var posY;
							if (-1 == data.indexOf("#####")) {
								posY = data.split("~~~")[5];
							} else {
								posY = data.split("~~~")[5].split("1#####")[0];
							}
							Wicket.Ajax.get('${url}&card=' + id + '&stop=true&indexOfClickedCard=' + data.split("~~~")[2]
							+ '&side=' + data.split("~~~")[3] + '&posX=' + data.split("~~~")[4] + '&posY=' + posY, function() { }, null, null);
						};
					};
			};
		};
	};
	
	// You can set websocket, streaming or long-polling here.
	// TODO: use wicket-atmosphere
//	jQuery.atmosphere.subscribe(
//			document.getElementById('playCard').href, callbackPlayCard${uuidValidForJs},
//			jQuery.atmosphere.request = {
//				transport : 'streaming'
//			}
//	);
	
});