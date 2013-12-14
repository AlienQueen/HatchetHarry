jQuery(function() { 
	window.setTimeout(function() {
		jQuery("#handleImage${uuidValidForJs}").data("dragUrl","${url}");
		jQuery("#handleImage${uuidValidForJs}").data("handUrl","${handUrl}");
		jQuery("#handleImage${uuidValidForJs}").data("graveyardUrl","${graveyardUrl}");
		jQuery("#handleImage${uuidValidForJs}").data("exileUrl","${exileUrl}");
	}, 150);
});