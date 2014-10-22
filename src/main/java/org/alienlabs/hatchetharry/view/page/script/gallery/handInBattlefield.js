window.setTimeout(function () {
	jQuery('#galleryParent').find('.cards').sortable({ placeholder: "ui-state-highlight"});

    function tooltips() {
    	if ((typeof dontZoom === 'undefined') || (!dontZoom)) {
    		$(this).parents('.cardContainer').toggleClass('details');
    	}
    }
    jQuery('.magicCard').unbind('click').click(tooltips);
}, 1000);