window.setTimeout(function () {
	Wicket.Ajax.get({'u': '${url}', 'e' : 'click', 'c' : 'putToHand${uuidValidForJs}'});
}, 1500);