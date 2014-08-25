// The dock
jQuery(function () {
    jQuery('#dock').jqDock();
});

// The toolbar, a jQuery plugin
jQuery(function () {

    jQuery('#floatingbar').css({
        height: "38px"
    });
});

jQuery(function () {
    jQuery(".gallery a[rel^='prettyPhoto']").prettyPhoto({});
});

// The drop-down menu for mobile
jQuery(function () {
    // Landscape mode menubar by default
    jQuery("#jMenu").show();
    jQuery('.dropdownmenu').hide();
    jQuery('.categories').hide();

    jQuery('.dropdownmenu').click(function () {
        jQuery('.categories').toggle();
    });
});

jQuery(function () {
    // @see: initTooltip.js
    // TODO: re-activate Dnd + tap handle for baldu
    jQuery("#card249c4f0b_cad0_4606_b5ea_eaee8866a347").click(
        function (e) {
            jQuery("#cardTooltip249c4f0b_cad0_4606_b5ea_eaee8866a347").attr('style', 'display: block');
        });
});

jQuery(function () {
    setTimeout(function () {
        // Freebox
        var userAgent = jQuery("#qunit-userAgent").html();
        if (userAgent.indexOf("FbxQmlTV") != -1) {
            jQuery("#floatingbar").css("top", "-10px");
            jQuery("#cssmenu").css("top", "28px");
            jQuery(".dropdownmenu").css("top", "28px");
        }

        // We switch the menu to full bar or small drop-down depending upon the size of the screen
        jQuery(window).resize(function () {
            var width = viewportSize.getWidth();
            var height = viewportSize.getHeight();

            if ((typeof(width) != "undefined") && (typeof(height) != "undefined") && (null != width) && (null != height) && (width < height)) {
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
jQuery(function () {
    jQuery('[title]').tipsy({gravity: 's'});
});

// For cards drag + hand, graveyard & exile drop
var shouldMove = true;

// Hand, Graveyard & Exile
jQuery(function () {
    var theIntGraveyard = null;
    var $crosslinkGraveyard, $navthumbGraveyard;
    var curclickedGraveyard = 0;
    theIntervalGraveyard = function (cur) {
        if (typeof cur != 'undefined') curclickedGraveyard = cur;
        $crosslinkGraveyard.removeClass('active-thumb-Graveyard');
        $navthumbGraveyard.eq(curclickedGraveyard).parent().addClass('active-thumb-Graveyard');
        jQuery('.stripNavGraveyard ul li a').eq(curclickedGraveyard).trigger('click');
        $crosslinkGraveyard.removeClass('active-thumb-Graveyard');
        $navthumbGraveyard.eq(curclickedGraveyard).parent().addClass('active-thumb-Graveyard');
        jQuery('.stripNavGraveyard ul li a').eq(curclickedGraveyard).trigger('click');
        curclickedGraveyard++;
        if (6 == curclickedGraveyard) curclickedGraveyard = 0;
    };
    jQuery('#graveyard-main-photo-slider').codaSliderGraveyard();
    $navthumbGraveyard = jQuery('.graveyard-nav-thumb');
    $crosslinkGraveyard = jQuery('.graveyard-cross-link');
    $navthumbGraveyard.click(function () {
        var $this = jQuery(this);
        theIntervalGraveyard($this.parent().attr('href').slice(1) - 1);
        return false;
    });
    theIntervalGraveyard();
    var theIntExile = null;
    var $crosslinkExile, $navthumbExile;
    var curclickedExile = 0;
    theIntervalExile = function (cur) {
        if (typeof cur != 'undefined') curclickedExile = cur;
        $crosslinkExile.removeClass('active-thumb-Exile');
        $navthumbExile.eq(curclickedExile).parent().addClass('active-thumb-Exile');
        jQuery('.stripNavExile ul li a').eq(curclickedExile).trigger('click');
        $crosslinkExile.removeClass('active-thumb-Exile');
        $navthumbExile.eq(curclickedExile).parent().addClass('active-thumb-Exile');
        jQuery('.stripNavExile ul li a').eq(curclickedExile).trigger('click');
        curclickedExile++;
        if (6 == curclickedExile) curclickedExile = 0;
    };
    jQuery('#exile-main-photo-slider').codaSliderExile();
    $navthumbExile = jQuery('.exile-nav-thumb');
    $crosslinkExile = jQuery('.exile-cross-link');
    $navthumbExile.click(function () {
        var $this = jQuery(this);
        theIntervalExile($this.parent().attr('href').slice(1) - 1);
        return false;
    });
    theIntervalExile();
    var theInt = null;
    var $crosslink, $navthumb;
    var curclicked = 0;
    theInterval = function (cur) {
        if (typeof cur != 'undefined') curclicked = cur;
        $crosslink.removeClass('active-thumb-Hand');
        $navthumb.eq(curclicked).parent().addClass('active-thumb-Hand');
        jQuery('.stripNav ul li a').eq(curclicked).trigger('click');
        $crosslink.removeClass('active-thumb-Hand');
        $navthumb.eq(curclicked).parent().addClass('active-thumb-Hand');
        jQuery('.stripNav ul li a').eq(curclicked).trigger('click');
        curclicked++;
        if (6 == curclicked) curclicked = 0;
    };
    jQuery('#main-photo-slider').codaSlider();
    $navthumb = jQuery('.nav-thumb');
    $crosslink = jQuery('.cross-link');
    $navthumb.click(function () {
        var $this = jQuery(this);
        theInterval($this.parent().attr('href').slice(1) - 1);
        return false;
    });
    theInterval();
    jQuery('#content').show();
});

jQuery(function () {
    window.setTimeout(function () {
        function getViewPortSize() {
            if (typeof window.innerWidth != 'undefined') {
                window.viewportwidth = window.innerWidth,
                    window.viewportheight = window.innerHeight;
            }
        }

        getViewPortSize();
        if (viewportwidth <= 1024) {
            jQuery('#putToHand').attr('src', 'image/hand_small.jpg');
            jQuery('#putToGraveyard').attr('src', 'image/graveyard_small.jpg');
            jQuery('#putToExile').attr('src', 'image/exile_small.jpg');
        }

        jQuery(window).resize(function () {
            getViewPortSize();
            var allCards = jQuery('.magicCard.ui-draggable');

            var allImages = allCards.find('.clickableCard');
            var firstCard = jQuery(allImages[0]);

            if (viewportwidth > 1024) {
                if ((typeof firstCard != 'undefined') && (typeof firstCard.attr('src') != 'undefined') && (firstCard.attr('src').indexOf('_small') != -1)) {
                    for (var i = 0; i < allImages.length; i++) {
                        var card = jQuery(allImages[i]);
                        if ((typeof card != 'undefined') && (typeof card.attr('src') != 'undefined')) {
                            card.attr('src', card.attr('src').replace('_small', '_medium'));
                        }
                    }
                }
            } else {
                if ((typeof firstCard != 'undefined') && (typeof firstCard.attr('src') != 'undefined') && (firstCard.attr('src').indexOf('_medium') != -1)) {
                    for (var i = 0; i < allImages.length; i++) {
                        var card = jQuery(allImages[i]);
                        if ((typeof card != 'undefined') && (typeof card.attr('src') != 'undefined')) {
                            card.attr('src', card.attr('src').replace('_medium', '_small'));
                        }
                    }
                }
            }
        });
    }, 500);
});

//Visio-conference
jQuery(function () {
    window.setTimeout(function () {
        jQuery('#conference').dialog({ autoOpen: false, position: { my: 'center', at: 'center', of: window }, title: 'HatchetHarry video-conference' });
        jQuery('#importDeck').dialog({ autoOpen: false, position: { my: 'center', at: 'center', of: window }, title: 'HatchetHarry deck import' });
        jQuery('#closeImportDeck').click(function () {
            jQuery('#importDeck').dialog('close');
        });
    }, 250);
});

// Remove the ugly blue arrow due to the conference
jQuery(function () {
    window.setTimeout(function () {
        jQuery('#fvd-single-fennec-popup').hide();
    }, 500);
});

jQuery(function () {
    window.setTimeout(function () {
        jQuery('#playCardIndicator').hide();
    }, 250);
});

jQuery(function () {
    window.localStorage.setItem('org.doubango.expert.disable_video', "false");
    window.localStorage.setItem('org.doubango.expert.enable_rtcweb_breaker', "true");
    window.localStorage.setItem('org.doubango.expert.enable_media_caching', "true");
    window.localStorage.setItem('org.doubango.expert.disable_callbtn_options', "false");
});

//The website tour, a jQuery plugin
jQuery(function () {
    // Instance the tour
    var tour = new Tour({
        steps: [
            {
                element: "#tour1",
                title: "Drawing cards",
                placement: "top",
                trigger: 'manual',
                content: "When you've joined a game, you'll be able to draw cards, thanks to the button in this toolbar."
            },
            {
                element: "#tour2",
                title: "Untap all",
                placement: "top",
                trigger: 'manual',
                content: "You can untap all your permanents at once using the toolbar button."
            },
            {
                element: "#baldu",
                title: "An example card",
                placement: "right",
                trigger: 'manual',
                content: "This is a card. You can move it by drag and drop using the small, green handle on its side. If you do so, the opponent will see its move matched in her own browser. Additionaly, you can call a context menu by right-clicking on the card itself."
            },
            {
                element: "#tour3",
                title: "Play cards",
                placement: "top",
                trigger: 'manual',
                content: "You can play cards and the opponent will see them on the battlefield."
            },
            {
                element: "#cssmenu",
                title: "Menu bar",
                placement: "bottom",
                trigger: 'manual',
                content: "This is the menubar, where you'll find most of the options of HatchetHarry, logically grouped into menu entries."
            }
        ]});

    // Initialize the tour
    tour.init();

    jQuery('#launch_tour').click(function () {
        // Start the tour
        tour.start(true);
        tour.goTo(0);
        return false;
    });

});

jQuery(function () {
    window.setTimeout(function () {
        jQuery('.loader').hide();
        jQuery('body').removeClass("cloak");
    }, 3000);
});