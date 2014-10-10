window.setTimeout(function() {
	if ($('.battlefieldCardsForSide1.cards').sortable()) {
		$('.battlefieldCardsForSide1.cards').sortable('destroy');
	}
	jQuery('.battlefieldCardsForSide1.cards').sortable(
			{ 	placeholder: "ui-state-highlight",
				stop: function(event, ui) {
			        index = ui.item.index();
			        var myId = ui.item.children(":first").children(":first").children(":first").attr('id');
			        var uuid=myId.slice(10, myId.length).replace(new RegExp("_", 'g'), "-");

			        dontZoom = true;
			        Wicket.Ajax.get({'u': '${url}&uuid=' + uuid + '&index=' + index});
			    }
			});
	var dontZoom = false;
}, 1000);