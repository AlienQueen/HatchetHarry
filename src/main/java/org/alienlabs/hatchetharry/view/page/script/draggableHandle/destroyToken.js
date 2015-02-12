window.setTimeout(function () {
    Wicket.Ajax.get({'u': '${destroyTokenUrl}', 'e' : 'click', 'c' : 'destroyToken${uuidValidForJs}'});
}, 1500);