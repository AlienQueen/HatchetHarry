jQuery(function() { 
	window.setTimeout(function () {
		jQuery("#cardRotate${uuidValidForJs}").data("tapUrl", "${url}");

    	jQuery('#cardRotate${uuidValidForJs}').unbind('click');
    	var tapUrl${uuidValidForJs} = jQuery('#cardRotate${uuidValidForJs}').data('tapUrl');
    	Wicket.Ajax.get({'u': tapUrl${uuidValidForJs}, 'e' : 'click', 'c' : 'cardRotate${uuidValidForJs}'});
	}, 2000);
});
