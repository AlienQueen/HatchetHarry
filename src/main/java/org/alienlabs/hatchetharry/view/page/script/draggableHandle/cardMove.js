// TODO in reality, cardMove.js configures the context menu: move it in its own Behavior

window.setTimeout(function () {
    // Show menu when #myDiv is clicked
    jQuery("#card${uuidValidForJs}").contextMenu(
        {
            menu: 'contextMenu${uuidValidForJs}'
        },
        function (action, el, pos) {
            var uuid = jQuery(el).attr('id').replace('card', '');

            if (action === 'graveyard') {
                jQuery('#' + jQuery(el).attr('id').replace('card', 'cardHandle')).hide();
                Wicket.Ajax.get({ 'u': '${graveyardUrl}&uuid=' + uuid });
            } else if (action === 'hand') {
                jQuery('#' + jQuery(el).attr('id').replace('card', 'cardHandle')).hide();
                Wicket.Ajax.get({ 'u': '${handUrl}&uuid=' + uuid });
            } else if (action === 'exile') {
                jQuery('#' + jQuery(el).attr('id').replace('card', 'cardHandle')).hide();
                var url = jQuery('#handleImage' + uuid).data('exileUrl');
                Wicket.Ajax.get({ 'u': url + '&uuid=' + uuid });
            } else if (action === 'move') {
                var body = document.getElementById('body');
                body.style.cursor = 'move';
                jQuery("body").click(function (event) {
                    var url = jQuery('#handleImage' + uuid).data('dragUrl');
                    body.style.cursor = "default";
                    Wicket.Ajax.get({ 'u': url + '&uuid=' + uuid + '&posX=' + event.pageX + '&posY=' + event.pageY});
                    jQuery("body").unbind('click');
                });
            } else if (action === 'destroy') {
                jQuery('#' + jQuery(el).attr('id').replace('card', 'cardHandle')).hide();
                Wicket.Ajax.get({ 'u': '${destroyUrl}&uuid=' + uuid });
            }
        }
    );
    jQuery('#cardHandle${uuidValidForJs}').attr('style', 'display: block; position: absolute; left: ${posX}px; top: ${posY}px; z-index: 1;');

    function getViewPortSize() {
        if (typeof window.innerWidth != 'undefined') {
            window.viewportwidth = window.innerWidth,
                window.viewportheight = window.innerHeight;
        }
    }

    getViewPortSize();
    var card = jQuery('#card${uuidValidForJs}');
    if (viewportwidth > 1024) {
        if (typeof card != 'undefined' && typeof card.attr('src') != 'undefined' && card.attr('src').indexOf('_small') != -1) {
            card.attr('src', card.attr('src').replace('_small', '_medium'));
        }
    } else {
        if (typeof card != 'undefined' && typeof card.attr('src') != 'undefined' && card.attr('src').indexOf('_medium') != -1) {
            card.attr('src', card.attr('src').replace('_medium', '_small'));
        }
    }
}, 500);
