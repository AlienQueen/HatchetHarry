window.setTimeout(function() {
				Wicket.Ajax.get({"u": "${url}&uuid=${uuid}", "e": "click", "c" : "tapHandleImage${uuid}"});
}, 2000);