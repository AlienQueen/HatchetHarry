jQuery(function() {
	jQuery('#tapHandleImage${uuid}').click(
			function() {
				Wicket.Ajax.get('${url}&uuid=${uuid}', function() {
				}, null, null);
			});
});