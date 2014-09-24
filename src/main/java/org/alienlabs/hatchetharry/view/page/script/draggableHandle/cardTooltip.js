window.setTimeout(function () {
    if (typeof drawMode === 'undefined' || !drawMode) {
        $('.battlefieldCards').sortable({ placeholder: "ui-state-highlight" });

        var battlefieldTooltips = function (){
            $(this).toggleClass('battlefieldDetails');
        }
        $('.clickableCard').unbind('click').click(battlefieldTooltips);
    }
}, 1000);
