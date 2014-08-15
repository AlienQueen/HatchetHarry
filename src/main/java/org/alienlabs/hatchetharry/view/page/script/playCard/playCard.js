window.setTimeout(function () {
    jQuery("#playCardLink0").click(function () {
        if (document.getElementById('content').nodeName === 'DIV') {
            return false;
        }

        var img = jQuery(".active-thumb-Hand img");
        var id = img.attr('id');
        var withoutPlaceholder = id.split('placeholder')[1];
        var withoutImg = withoutPlaceholder.split('_img')[0];
        var uuid = withoutImg.replace(/_/g, "-");

        var a = jQuery(".active-thumb-Hand");
        var aId = a.attr('id');
        var currentCard = aId.split("cross-link")[1];

        jQuery('#playCardIndicator').show();
        Wicket.Ajax.get({'u': '${url}&card=' + uuid + '&indexOfClickedCard=' + currentCard + '&side=${side}'});
    });
}, 1500);
