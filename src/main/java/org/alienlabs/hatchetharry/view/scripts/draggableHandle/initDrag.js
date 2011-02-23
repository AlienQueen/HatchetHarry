jQuery(function() {
	jQuery("#contextMenu").draggable({
		handle : "img"
	});
	jQuery("div img").disableSelection();
});