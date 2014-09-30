window.setTimeout(function () {
    if (typeof drawMode === 'undefined' || !drawMode) {
        $('.battlefieldCards').sortable({ placeholder: "ui-state-highlight" });
    }
}, 1000);
