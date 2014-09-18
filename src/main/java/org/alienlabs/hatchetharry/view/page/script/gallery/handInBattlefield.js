$(function() {
    sortableInProgress = false;
    $('.cardContainer img').click(turn);
    function turn(){
      if (sortableInProgress) {
          sortableInProgress = false;
          return;
      }
        $(this).parent().toggleClass('turned');
    }
    $('.cards').sortable({ placeholder: "ui-state-highlight", start: function( event, ui ) {
        sortableInProgress = true;
    } });
    $('.cardContainer .tooltips').click(tooltips);

    function tooltips(){
      $(this).parent().toggleClass('details');
      if($(this).parent().hasClass('details'))
        $('img',$(this).parent()).unbind('click').click(tooltips);
      else
        $('img',$(this).parent()).unbind('click').click(turn);
    }
});