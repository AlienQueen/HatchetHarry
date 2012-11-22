window.setTimeout(function() {
	jQuery("#tapHandleImage${uuid}").unbind("click");
	jQuery("#tapHandleImage${uuid}").click(function () { Wicket.Ajax.get({"u": "${url}&uuid=${uuid}"}); });
//	Wicket.Ajax.get({"u": "${url}&uuid=${uuid}", "e": "click", "c" : "tapHandleImage${uuid}"});
}, 2000);