window.setTimeout(function () {
	jQuery('#galleryParent').find('.cards').sortable({ placeholder: "ui-state-highlight"});

	jQuery('.gallery .magicCard').unbind('click').click(function() {
		if ($(this).hasClass('details')) {
			$(this).css('z-index', '');
		} else {
			$(this).css('z-index', ++zIndex);
		};
		$(this).parents('.cardContainer').toggleClass('details');
	});
}, 1000);