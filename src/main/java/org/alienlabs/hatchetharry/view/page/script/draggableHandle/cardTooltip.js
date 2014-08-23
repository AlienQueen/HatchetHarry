window.setTimeout(function () {
    if (typeof drawMode === 'undefined' || !drawMode) {

        jQuery('#card${uuidValidForJs}').unbind('click');
        jQuery('#card${uuidValidForJs}').unbind('tap');

        jQuery('#card${uuidValidForJs}').click(function (e) {
            jQuery('#cardTooltip${uuidValidForJs}').children(0).children(0).show();
            jQuery('#cardTooltip${uuidValidForJs}').attr('style', 'display: block; position: absolute; left: ' + (jQuery('#card${uuidValidForJs}').offset().left + 20) + 'px; top: ' + (jQuery('#card${uuidValidForJs}').offset().top + 20) + 'px; z-index: 50;');
        });

        // For mobile
        var hammertime$
        {
            uuidValidForJs
        }
        = jQuery('#card${uuidValidForJs}').hammer();
        hammertime$
        {
            uuidValidForJs
        }
    .
        on('tap', function (ev) {
            jQuery('#cardTooltip${uuidValidForJs}').attr('style', 'display: block; position: absolute; left: '
                + (jQuery('#card${uuidValidForJs}').offset().left + 20) + 'px; top: ' + (jQuery('#card${uuidValidForJs}').offset().top + 20)
                + 'px; z-index: 50;');
            jQuery('#cardTooltip${uuidValidForJs}').children(0).children(0).show();
        });
    }
}, 1000);
