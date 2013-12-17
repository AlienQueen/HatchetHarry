// The dock
jQuery(function(){
	jQuery('#dock').jqDock();
});

// The toolbar, a jQuery plugin
jQuery(function() {

	jQuery('#floatingbar').css({
		height : "38px"
	});
});

jQuery(function() {
	jQuery(".gallery a[rel^='prettyPhoto']").prettyPhoto({});
});

// The drop-down menu for mobile
jQuery(function() {
	// Landscape mode menubar by default
	jQuery("#jMenu").show();
	jQuery('.dropdownmenu').hide();
	jQuery('.categories').hide();

	jQuery('.dropdownmenu').click(function(){ 
		jQuery('.categories').toggle();
	});
});

jQuery(function() {	
	// @see: initTooltip.js
	// TODO: re-activate Dnd + tap handle for baldu
	jQuery("#card249c4f0b_cad0_4606_b5ea_eaee8866a347").click(
	function(e) {
		jQuery("#cardTooltip249c4f0b_cad0_4606_b5ea_eaee8866a347").attr('style', 'display: block');
	});
});

//The website tour, a jQuery plugin
jQuery(function() {
	tl.pg.init({ 'pg_caption' : 'Site tour', 'auto_show_first': true});
});

// Display the name of the currently displayed card in the hand
function updateHandLabel(){
	var handCardName = jQuery('#handGallery .active-thumbGraveyard .nav-thumb').attr('name');
	if (handCardName == undefined) {
		handCardName = jQuery('#handGallery .active-thumb .nav-thumb').attr('name');
	}
	jQuery('#cardLabel').text(handCardName);
};

jQuery(function() {
	setTimeout(function() {
        // Freebox
        var userAgent = jQuery("#qunit-userAgent").html();
        if (userAgent.indexOf("FbxQmlTV") != -1) {
            jQuery("#floatingbar").css("top", "-10px");
            jQuery("#cssmenu").css("top", "28px");
            jQuery(".dropdownmenu").css("top", "28px");
        }

		updateHandLabel();
		jQuery('#handGallery .cross-link .nav-thumb').click(updateHandLabel);
		
		// We switch the menu to full bar or small drop-down depending upon the size of the screen
		jQuery(window).resize(function() {
		    var width = viewportSize.getWidth();
			var height = viewportSize.getHeight();

			if ((typeof(width) != "undefined") && (typeof(height) != "undefined") && (null != width) && (null != height) && (width<height)) {
				// Portrait
				jQuery("#cssmenu").hide();
				jQuery('.dropdownmenu').show();
			} else {
				// Landscape
				jQuery("#cssmenu").show();
				jQuery('.dropdownmenu').hide();
				jQuery('.categories').hide();
			}
		});
	}, 4000);
});

// The tooltips
jQuery(function() {
	jQuery('[title]').tipsy({gravity: 's'});
});

// For cards drag + hand, graveyard & exile drop
var shouldMove = true;


// Visio-conference
jQuery(function() {
	jQuery("#conference").dialog({ autoOpen: false, position: { my: "center", at: "center" } });
	
	jQuery("[data-id='conferenceOpener']").click(function() {
		jQuery("#conference").dialog("open");
	});
	
	jQuery("[data-id='conferenceOpenerResponsive']").click(function() {
		jQuery("#conference").dialog("open");
	});
});

// Hand, Graveyard & Exile
jQuery(function() {
	var theInt = null; var $crosslink, $navthumb; var curclicked = 0; theInterval = function(cur) { if (typeof cur != 'undefined') curclicked = cur; $crosslink.removeClass('active-thumb-Hand'); $navthumb.eq(curclicked).parent().addClass('active-thumb-Hand'); jQuery('.stripNav ul li a').eq(curclicked).trigger('click'); $crosslink.removeClass('active-thumb-Hand'); $navthumb.eq(curclicked).parent().addClass('active-thumb-Hand'); jQuery('.stripNav ul li a').eq(curclicked).trigger('click'); curclicked++; if (6 == curclicked) curclicked = 0; }; jQuery('#main-photo-slider').codaSlider(); $navthumb = jQuery('.nav-thumb'); $crosslink = jQuery('.cross-link'); $navthumb.click(function() { var $this = jQuery(this); theInterval($this.parent().attr('href').slice(1) - 1); return false; }); theInterval(); function updateHandLabel(){ var handCardName = jQuery('#handGallery .active-thumb-Hand .nav-thumb').attr('name'); if (handCardName == undefined) { handCardName = jQuery('#handGallery .active-thumb-Hand .nav-thumb').attr('name'); } jQuery('#cardLabel').text(handCardName); }; jQuery(function() { setTimeout(function() { updateHandLabel(); jQuery('#handGallery .cross-link .nav-thumb').click(updateHandLabel); }, 175); });
	var theIntGraveyard = null; var $crosslinkGraveyard, $navthumbGraveyard; var curclickedGraveyard = 0; theIntervalGraveyard = function(cur) { if (typeof cur != 'undefined') curclickedGraveyard = cur; $crosslinkGraveyard.removeClass('active-thumb-Graveyard'); $navthumbGraveyard.eq(curclickedGraveyard).parent().addClass('active-thumb-Graveyard'); jQuery('.stripNavGraveyard ul li a').eq(curclickedGraveyard).trigger('click'); $crosslinkGraveyard.removeClass('active-thumb-Graveyard'); $navthumbGraveyard.eq(curclickedGraveyard).parent().addClass('active-thumb-Graveyard'); jQuery('.stripNavGraveyard ul li a').eq(curclickedGraveyard).trigger('click'); curclickedGraveyard++; if (6 == curclickedGraveyard) curclickedGraveyard = 0; }; jQuery('#graveyard-main-photo-slider').codaSliderGraveyard(); $navthumbGraveyard = jQuery('.graveyard-nav-thumb'); $crosslinkGraveyard = jQuery('.graveyard-cross-link'); $navthumbGraveyard.click(function() { var $this = jQuery(this); theIntervalGraveyard($this.parent().attr('href').slice(1) - 1); return false; }); theIntervalGraveyard(); function updateGraveyardLabel(){ var graveyardCardName = jQuery('#graveyardGallery .active-thumb-Graveyard .nav-thumb').attr('name'); if (graveyardCardName == undefined) { graveyardCardName = jQuery('#graveyardGallery .active-thumb-Graveyard .nav-thumb').attr('name'); } jQuery('#graveyardCardLabel').text(graveyardCardName); }; jQuery(function() { setTimeout(function() { updateGraveyardLabel(); jQuery('#graveyardGallery .cross-link .nav-thumb').click(updateGraveyardLabel); }, 175); });
	var theIntExile = null; var $crosslinkExile, $navthumbExile; var curclickedExile = 0; theIntervalExile = function(cur) { if (typeof cur != 'undefined') curclickedExile = cur; $crosslinkExile.removeClass('active-thumb-Exile'); $navthumbExile.eq(curclickedExile).parent().addClass('active-thumb-Exile'); jQuery('.stripNavExile ul li a').eq(curclickedExile).trigger('click'); $crosslinkExile.removeClass('active-thumb-Exile'); $navthumbExile.eq(curclickedExile).parent().addClass('active-thumb-Exile'); jQuery('.stripNavExile ul li a').eq(curclickedExile).trigger('click'); curclickedExile++; if (6 == curclickedExile) curclickedExile = 0; }; jQuery('#exile-main-photo-slider').codaSliderExile(); $navthumbExile = jQuery('.exile-nav-thumb'); $crosslinkExile = jQuery('.exile-cross-link'); $navthumbExile.click(function() { var $this = jQuery(this); theIntervalExile($this.parent().attr('href').slice(1) - 1); return false; }); theIntervalExile(); function updateExileLabel(){ var exileCardName = jQuery('#exileGallery .active-thumb-Exile .nav-thumb').attr('name'); if (exileCardName == undefined) { exileCardName = jQuery('#exileGallery .active-thumb-Exile .nav-thumb').attr('name'); } jQuery('#exileCardLabel').text(exileCardName); }; jQuery(function() { setTimeout(function() { updateExileLabel(); jQuery('#exileGallery .cross-link .nav-thumb').click(updateExileLabel); }, 175); });
});

jQuery(function() {
	function getViewPortSize() {
	    if (typeof window.innerWidth != 'undefined')
	    {
	    	window.viewportwidth = window.innerWidth,
	    	window.viewportheight = window.innerHeight;
	    }
	}
	
	jQuery(window).resize(function () {
		getViewPortSize();
		var allCards = jQuery('.magicCard.ui-draggable');
		
		var allImages = allCards.find('.clickableCard');
		var firstCard = jQuery(allImages[0]);

		if (viewportwidth > 1024) {
	
			if (firstCard.attr('src').indexOf('_small') != -1) {
				for (var i = 0; i < allImages.length; i++) {
					var card = jQuery(allImages[i]);
					card.attr('src', card.attr('src').replace('_small', '_medium')); 
				}
			}
		} else {

			if  (firstCard.attr('src').indexOf('_medium') != -1) {	
				for (var i = 0; i < allImages.length; i++) {
					var card = jQuery(allImages[i]);
					card.attr('src', card.attr('src').replace('_medium', '_small')); 
				}
			}
		}
	});
});