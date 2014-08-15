jQuery(document).ready(function () {

    jQuery("#playCardFromGraveyardLinkDesktop, #playCardFromGraveyardLinkResponsive").click(function () {
        if (document.getElementById('graveyard-content') == null || document.getElementById('graveyard-content').nodeName === 'DIV') {
            return false;
        }

        var img = jQuery(".active-thumb-Graveyard img");
        if (img.length == 0) {
            return false;
        }
        var id = img.attr('id');
        var withoutPlaceholder = id.split('placeholder')[1];
        var withoutImg = withoutPlaceholder.split('_img')[0];
        var uuid = withoutImg.replace(/_/g, "-");

        var a = jQuery(".active-thumb-Graveyard");
        var aId = a.attr('id');
        var currentCard = aId.split("graveyard-cross-link")[1];

        Wicket.Ajax.get({'u': '${url}&card=' + uuid + '&indexOfClickedCard=' + currentCard + '&side=${side}'});
    });
});