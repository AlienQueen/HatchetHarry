window.setTimeout(function () {
	Wicket.Ajax.get({'u': '${url}', 'e' : 'click', 'c' : 'putToGraveyard${uuidValidForJs}'});
}, 1500);