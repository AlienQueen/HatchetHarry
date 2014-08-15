jQuery(function () {
    window.setTimeout(function () {
        jQuery("#handleImage${uuidValidForJs}").data("destroyUrl", "${destroyTokenUrl}");
    }, 150);
});