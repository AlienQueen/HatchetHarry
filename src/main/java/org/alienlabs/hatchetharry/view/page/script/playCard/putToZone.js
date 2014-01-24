window.setTimeout(function() {
	
	jQuery('#moveToZoneSubmit${zone}${Player}').click(function() {
		var img = jQuery(".active-thumb-${reveal}${zone} img");
		var id = img.attr('id');
		var withoutPlaceholder = id.split('placeholder')[1];
		var withoutImg = withoutPlaceholder.split('_img')[0];
		var uuid = withoutImg.replace(/_/g,"-");
		
		var targetZone = jQuery("#putToZoneSelectFor${zone}${Player}[name='targetZoneLabel:targetZoneGroup:targetZoneInput'] option:selected").text();
		console.log(targetZone);
			
		Wicket.Ajax.get({'u': '${url}&card=' + uuid + '&targetZone=' + targetZone});
	});
}, 250);