window.setTimeout(function() {
	jQuery("#tapHandleImage${uuid}").unbind("click");
	Wicket.Ajax.get({"u": "${url}&uuid=${uuid}", "e": "click", "c" : "tapHandleImage${uuid}"});
}, 2000);