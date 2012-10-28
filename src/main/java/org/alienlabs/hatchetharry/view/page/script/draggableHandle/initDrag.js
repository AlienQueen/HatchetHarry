window.setTimeout(function() {
	jQuery("#menutoggleButton${uuid}").draggable({
		handle : "#handleImage${uuid}"
	});
	
	jQuery("#handleImage${uuid}").data("url","${url}");
	
	console.log("draggable handle initialized for component #handleImage${uuid}");
}, 2000);