jQuery(function() {
				Wicket.Ajax.get({"u": "${url}&uuid=${uuid}", "e": "click", "c" : "tapHandleImage${uuid}"}, function() {
				});
});