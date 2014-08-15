jQuery(function () {
    window.setTimeout(function () {
        var dragUrl$
        {
            uuidValidForJs
        }
        = jQuery('#handleImage${uuidValidForJs}').data('dragUrl');

        jQuery('#cardHandle${uuidValidForJs}').draggable({ handle: '#handleImage${uuidValidForJs}', helper: 'original', stop: function (event, ui) {
            if (!shouldMove) {
                shouldMove = true;
                return;
            }
            Wicket.Ajax.get({ 'u': dragUrl$
            {
                uuidValidForJs
            }
            +'&posX=' + (ui.offset.left) + '&posY=' + (ui.offset.top)
        });
    }
});

// The hand image is a drop target
jQuery('#putToHand').droppable({ accept: '.magicCard', drop: function (event, ui) {
    shouldMove = false;
    jQuery('#' + ui.draggable.context.id).hide();

    if (jQuery('#' + ui.draggable.context.id.replace('cardHandle', 'handleImage')).next().length != 0 && jQuery('#' + ui.draggable.context.id.replace('cardHandle', 'handleImage')).next().next().next().children(':first').attr('class') === 'token') {
        Wicket.Ajax.get({ 'u': jQuery('#' + ui.draggable.context.id.replace('cardHandle', 'handleImage')).data('destroyUrl') });
        return;
    }

    Wicket.Ajax.get({ 'u': jQuery('#' + ui.draggable.context.id.replace('cardHandle', 'handleImage')).data('handUrl') + '&uuid=' + ui.draggable.context.id.replace('cardHandle', '') });
}});

// The graveyard image is a drop target
jQuery('#putToGraveyard').droppable({ accept: '.magicCard', drop: function (event, ui) {
    shouldMove = false;
    jQuery('#' + ui.draggable.context.id).hide();

    if (jQuery('#' + ui.draggable.context.id.replace('cardHandle', 'handleImage')).next().length != 0 && jQuery('#' + ui.draggable.context.id.replace('cardHandle', 'handleImage')).next().next().next().children(':first').attr('class') === 'token') {
        Wicket.Ajax.get({ 'u': jQuery('#' + ui.draggable.context.id.replace('cardHandle', 'handleImage')).data('destroyUrl') });
        return;
    }

    Wicket.Ajax.get({ 'u': jQuery('#' + ui.draggable.context.id.replace('cardHandle', 'handleImage')).data('graveyardUrl') + '&uuid=' + ui.draggable.context.id.replace('cardHandle', '') });
}});

// The exile image is a drop target
jQuery('#putToExile').droppable({ accept: '.magicCard', drop: function (event, ui) {
    shouldMove = false;
    jQuery('#' + ui.draggable.context.id).hide();

    if (jQuery('#' + ui.draggable.context.id.replace('cardHandle', 'handleImage')).next().length != 0 && jQuery('#' + ui.draggable.context.id.replace('cardHandle', 'handleImage')).next().next().next().children(':first').attr('class') === 'token') {
        Wicket.Ajax.get({ 'u': jQuery('#' + ui.draggable.context.id.replace('cardHandle', 'handleImage')).data('destroyUrl') });
        return;
    }

    Wicket.Ajax.get({ 'u': jQuery('#' + ui.draggable.context.id.replace('cardHandle', 'handleImage')).data('exileUrl') + '&uuid=' + ui.draggable.context.id.replace('cardHandle', '') });
}});
},
2000
)
;
})
;
