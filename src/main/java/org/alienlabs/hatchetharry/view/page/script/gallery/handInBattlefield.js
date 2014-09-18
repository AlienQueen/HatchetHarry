$('.cardContainer img').click(turn);
function turn(){
  $(this).parent().toggleClass('turned');
}
$('.cards').sortable({ placeholder: "ui-state-highlight" });
$('.cardContainer .tooltips').click(tooltips);

function tooltips(){
  $(this).parent().toggleClass('details');
  if($(this).parent().hasClass('details'))
    $('img',$(this).parent()).unbind('click').click(tooltips);
  else
    $('img',$(this).parent()).unbind('click').click(turn);
}