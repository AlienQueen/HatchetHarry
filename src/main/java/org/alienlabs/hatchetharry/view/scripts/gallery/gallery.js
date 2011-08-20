var theInt = null;
var $crosslink, $navthumb;
var curclicked = 0;

theInterval = function(cur) {
	if (typeof cur != 'undefined')
		curclicked = cur;

	$crosslink.removeClass("active-thumb");
	$navthumb.eq(curclicked).parent().addClass("active-thumb");
	jQuery(".stripNav ul li a").eq(curclicked).trigger('click');

	$crosslink.removeClass("active-thumb");
	$navthumb.eq(curclicked).parent().addClass("active-thumb");
	jQuery(".stripNav ul li a").eq(curclicked).trigger('click');
	curclicked++;
	if (6 == curclicked)
		curclicked = 0;

};

jQuery(function() {

	jQuery("#main-photo-slider").codaSlider();

	$navthumb = jQuery(".nav-thumb");
	$crosslink = jQuery(".cross-link");

	$navthumb.click(function() {
		var $this = jQuery(this);
		theInterval($this.parent().attr('href').slice(1) - 1);
		return false;
	});

	theInterval();
});
