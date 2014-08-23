jQuery(function () {
    window.setTimeout(function () {
        jQuery("#tapHandleImage${uuidValidForJs}").data("tapUrl", "${url}");

        jQuery('#tapHandleImage${uuidValidForJs}').unbind('click');
        var tapUrl${uuidValidForJs} = jQuery('#tapHandleImage${uuidValidForJs}').data('tapUrl');
        Wicket.Ajax.get({'u': tapUrl${uuidValidForJs} +'&uuid=${uuid}', 'e' : 'click', 'c' : 'tapHandleImage${uuidValidForJs}'});

    if (${tapped}) {
        jQuery('#card${uuidValidForJs}').rotate(90);
    }
}, 125);
})
;
