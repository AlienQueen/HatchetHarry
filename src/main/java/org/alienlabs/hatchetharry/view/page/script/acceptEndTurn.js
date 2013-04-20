jQuery(function() { 
	window.setTimeout(function() {
		Wicket.Ajax.get({'u': '${url}', 'e': 'click', 'c' : 'acceptEndTurnLink'});
	}, 1000);
});