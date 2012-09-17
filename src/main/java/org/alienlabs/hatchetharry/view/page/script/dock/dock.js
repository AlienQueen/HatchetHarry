function distance(x0, y0, x1, y1) {
		var xDiff = x1-x0;
		var yDiff = y1-y0;
	
		return Math.sqrt(xDiff*xDiff + yDiff*yDiff);
	}

	jQuery(document).ready(function() {
		var proximity = 180;
		var iconSmall = 48, iconLarge = 128; //css also needs changing to compensate with size
		var iconDiff = (iconLarge - iconSmall);
		var mouseX, mouseY;
		var dock = jQuery("#dock");
		var animating = false, redrawReady = false;
	
		jQuery(document.body).removeClass("no_js");
	
		//below are methods for maintaining a constant 60fps redraw for the dock without flushing
		jQuery(document).bind("mousemove", function(e) {
			if (dock.is(":visible")) {
				mouseX = e.pageX;
				mouseY = e.pageY;
		
				redrawReady = true;
				registerConstantCheck();
			}
		});
	
		function registerConstantCheck() {
			if (!animating) {
				animating = true;
			
				window.setTimeout(callCheck, 15);
			}
		}
	
		function callCheck() {
			sizeDockIcons();
		
			animating = false;
	
			if (redrawReady) {
				redrawReady = false;
				registerConstantCheck();
			}
		}
	
		//do the maths and resize each icon
		function sizeDockIcons() {
			dock.find("li").each(function() {
				//find the distance from the center of each icon
				var centerX = jQuery(this).offset().left + (jQuery(this).outerWidth()/2.0);
				var centerY = jQuery(this).offset().top + (jQuery(this).outerHeight()/2.0);
			
				var dist = distance(centerX, centerY, mouseX, mouseY);
			
				//determine the new sizes of the icons from the mouse distance from their centres
				var newSize =  (1 - Math.min(1, Math.max(0, dist/proximity))) * iconDiff + iconSmall;
				jQuery(this).find("a").css({width: newSize});
			});
		}
	}); 
