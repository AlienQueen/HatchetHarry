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
	jQuery('#image${uuid}').remove();
	jQuery('#cross-link2').click();
	jQuery('#cross-link-div1').remove();
	wicketAjaxGet('${url}&card=${uuid}', function() {
	}, null, null);
});

function callbackPlayCard${uuidValidForJs}(response) {
	if (response.transport != 'polling'
			&& response.state != 'connected'
			&& response.state != 'closed') {
		if (response.status == 200) {
			var data = response.responseBody;
			var s = data.split("~~~")[1];
			if ((typeof s != "undefined")
					&& (typeof jQuery('#jsessionid${uuid}').val() == 'undefined')) {
				// We're in the play card Meteor
				jQuery('#image${uuid}').remove();
				jQuery('#cross-link2').click();
				jQuery('#cross-link-div1').remove();
				wicketAjaxGet('${url}&card=${uuid}&stop=true', function() { }, null, null);
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