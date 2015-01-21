window.setTimeout(function () {
	jQuery('#galleryParent').find('.cards').sortable({ placeholder: "ui-state-highlight"});

	jQuery('.gallery .magicCard').unbind('click').click(function() {
		$(this).parents('.cardContainer').toggleClass('details');
	});
}, 1000);