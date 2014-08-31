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

        /* jQuery(window).resize(function () {
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
        });*/
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
                element: "#baldu",
                title: "The card",
                placement: "right",
                trigger: 'manual',
                content: "One more thing about this card: using the small green arrow on its other side, you can tap and untap it."
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
            },
            {
                element: "#page-wrap",
                title: "Your hand",
                placement: "right",
                trigger: 'manual',
                content: "This is your hand. You can browse the cards in it and play one of them."
            },
            {
                element: "#tour_10",
                title: "The dock",
                placement: "top",
                trigger: 'manual',
                content: "This is the dock. You can show the different zones of Magic using its icons."
            },
            {
                element: "#tour_10",
                title: "Dock button",
                placement: "top",
                trigger: 'manual',
                content: "Hide or display your hand using this button."
            },
            {
                element: "#tour_11",
                title: "Dock button",
                placement: "top",
                trigger: 'manual',
                content: "Browse through your graveyard using this button."
            },
            {
                element: "#tour_12",
                title: "Dock button",
                placement: "top",
                trigger: 'manual',
                content: "Browse through the exiled cards using this button."
            },
            {
                element: "#tour_13",
                title: "Hide all",
                placement: "top",
                trigger: 'manual',
                content: "Hide every zone of the game except the battlefield using this button. Only the permanents remain, so that you can have a vista of the game. If you click here again, the previously displayed zones will be restored, at the same place."
            },
            {
                element: "#tour_14",
                title: "The library",
                placement: "top",
                trigger: 'manual',
                content: "Browse through your library using this button. Your opponent will be notified of this action. Beware: you're supposed to play fair! Don't forget the rules of the game."
            },
            {
                element: "#chat",
                title: "The chat",
                placement: "bottom",
                trigger: 'manual',
                content: "This is a chat. Its behavior is standard and you DON'T need to refresh your browser in order to receive new messages."
            },
            {
                element: "#dataBoxParent",
                title: "The Databox",
                placement: "bottom",
                trigger: 'manual',
                content: "This is what we call the Databox: it gives the life point totals of all players of the game. If you click on one of these numbers, you can set it up directly. And you can use the 'plus' and 'minus' buttons."
            },
            {
                element: "#tour_1",
                title: "You're done!",
                placement: "bottom",
                trigger: 'manual',
                content: "Have fun!"
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

var uncloak = function () {
    jQuery('.loader').hide();
    jQuery('body').removeClass("cloak");
};

var resizeCards = function() {
    jQuery('.clickableCards').each(function(index, value) {
        jQuery(this).css('float: left; margin-left: 2rem; width: ' + Math.min(25, 100 / cards.length) + '%; max-width: 226px; height: auto;');
    });
}