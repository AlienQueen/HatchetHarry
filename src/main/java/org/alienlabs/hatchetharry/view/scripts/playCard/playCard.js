var canHideLink = true;

showLink = function() {
	jQuery("#${uuid}_l").css("visibility", "visible");
};

hideLink = function() {
	if (canHideLink) {
		jQuery("#${uuid}_l").css("visibility", "hidden");
	}
};

dontHideLink = function() {
	jQuery("#${uuid}_l").css("visibility", "visible");
	canHideLink = false;
	jQuery("#${uuid}_l").css("visibility", "visible");
};

canHideLink = function() {
	canHideLink = true;
};

jQuery("#${uuid}_l").mouseenter(dontHideLink).mouseleave(canHideLink);
jQuery("#${uuid}").mouseenter(showLink).mouseleave(hideLink);