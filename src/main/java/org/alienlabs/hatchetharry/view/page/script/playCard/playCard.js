window.setTimeout(function() {
    jQuery('#play${uuidValidForJs}').click(function (event) {
        event.stopPropagation();
        jQuery('#playCardIndicator').show();
        Wicket.Ajax.get({'u': '${url}'});
    });
}, 1000);