$(function() {
    $('.cards').sortable({ placeholder: "ui-state-highlight"});

    function tooltips() {
        $(this).toggleClass('details');
    }
    $('.magicCard').unbind('click').click(tooltips);
});