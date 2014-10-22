window.setTimeout(function () {
	Wicket.Ajax.get({'u': '${url}', 'e' : 'click', 'c' : 'putToExile${uuidValidForJs}'});
}, 1000);