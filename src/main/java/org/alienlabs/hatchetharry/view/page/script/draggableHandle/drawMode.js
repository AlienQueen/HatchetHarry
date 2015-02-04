jQuery(function () {
    window.setTimeout(function () {

        jQuery('._jsPlumb_connector').remove();
        cardAlreadySelected = false;

        if (${drawMode}) {
            var plumbSource, plumbTarget;
            jQuery('.clickableCard').click(function (event) {
                if (cardAlreadySelected) {
                    cardAlreadySelected = false;
                    plumbTarget = jQuery('#' + event.target.id).parent().parent().parent().parent().attr('id');

                    Wicket.Ajax.get({'u': jQuery('#' + plumbTarget).data('arrowDrawUrl') + '&source=' + plumbSource + '&target=' + plumbTarget});
                } else {
                    cardAlreadySelected = true;
                    plumbSource = jQuery('#' + event.target.id).parent().parent().parent().parent().attr('id');
                }
            });
        }
    }, 750);
});