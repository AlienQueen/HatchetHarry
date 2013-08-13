
jQuery(document).ready(function() {
	
 jQuery("#placeholder${uuidValidForJs}").hover(function(){
	 jQuery("#linkOnCard").css("margin", "150px 0px 0px -120px");
 }, function() {
	 jQuery("#linkOnCard").css("margin", "-1000px 0px 0px -1000px");
 });

 jQuery("#linkOnCard").hover(function(){
	 jQuery("#linkOnCard").css("margin", "150px 0px 0px -120px");
 }, function() {
	 jQuery("#linkOnCard").css("margin", "150px 0px 0px -120px");
 });
	
	jQuery("#playCardLink${clicked}").click(function () {
		if (document.getElementById('content').nodeName === 'DIV') {
			return false;
		}
		
		// TODO: maybe active-thumb should be replaced with active-thumb-hand
		var img = jQuery(".active-thumb img");
		var id = img.attr('id');
		var withoutPlaceholder = id.split('placeholder')[1];
		var withoutImg = withoutPlaceholder.split('_img')[0];
		var uuid = withoutImg.replace(/_/g,"-");
		
		// TODO: maybe active-thumb should be replaced with active-thumb-hand
		var a = jQuery(".active-thumb");
		var aId = a.attr('id');
		var currentCard = aId.split("cross-link")[1];
		
		Wicket.Ajax.get({'u': '${url}&card=' + uuid + '&indexOfClickedCard=' + currentCard + '&side=${side}'});
	});
});