window.setTimeout(function () {
    if (typeof drawMode === 'undefined' || !drawMode) {
        var sortableInProgress = false;
        var battlefieldTurn = function (){
            if (sortableInProgress) {
                sortableInProgress = false;
                return;
            }
            $(this).prev().toggleClass('battlefieldTurned');
        }
        $('.battlefieldCards').sortable({ placeholder: "ui-state-highlight", start: function( event, ui ) {
            sortableInProgress = true;
        } });

        var battlefieldTooltips = function (){
            $(this).toggleClass('battlefieldDetails');
        }
        $('.clickableCard').unbind('click').click(battlefieldTooltips);
        $('.cardTooltips').unbind('click').click(battlefieldTurn);
    }
}, 1000);
