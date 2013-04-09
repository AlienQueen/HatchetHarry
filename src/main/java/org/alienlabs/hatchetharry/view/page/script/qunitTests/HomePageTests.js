jQuery(function() {

					module("hand"); 
					test(
							"a basic hand visibility test",
							function() {
								ok((jQuery('span#handGallery').length == 1),
										"a span with id handGallery should be present and be unique");
								ok((jQuery('span#gallery').length == 1),
										"a span with id gallery should be present and be unique");
								var myGalleryContent = jQuery('span#gallery div');
								ok((myGalleryContent.length > 0),
										"it should itself contain other elements, actually found: "
												+ myGalleryContent.length);
								var slideElements = jQuery('span#gallery a.cross-link');
								ok((slideElements.length == 7),
										"these elements should contain the 7 images in the hand, actually found: "
												+ slideElements.length);
								var attribute = jQuery(
										'span#gallery a.cross-link:first img')
										.attr('src');
								ok(
										((attribute.indexOf("cards") != -1) && (attribute
												.indexOf(".jpg") != -1)),
										"the first of these images should have a source URL containing 'cards' and '.jpg', and it appears to be: "
												+ attribute);
							});
					module("menubar");
					test(
							"a basic menubar visibility test",
							function() {
								var menubaritem1 = jQuery('ul.jMenu a.fNiv img:first');
								ok(
										("/image/logoh1.gif" === menubaritem1
												.attr('src')),
										"the first menubar option is the first image of a <a> class fNiv contained in a <ul> of class jMenu, it should exist and point to '/image/logoh1.gif', and it appears to be: "
												+ menubaritem1.attr('src'));
								menubaritem1 = jQuery('ul.jMenu li ul li a:first');
								ok(
										(menubaritem1.text() === "About HatchetHarry"),
										"the very first menubar entry is the <a> contained in a <li>, a <ul>, a <li> in a <ul> of class jMenu"
												+ ", it should exist and contain 'About HatchetHarry', and it appears to be: "
												+ menubaritem1.text());

								var menubaritem2 = jQuery('ul.jMenu li.fNiv a:first');
								ok(
										(menubaritem2.text() === "Game"),
										"the second menubar option is the first <a> contained in a <li> class fNiv in a <ul> class jMenu."
												+ " It should exist and point to 'Game', and it appears to be: "
												+ menubaritem2.text());
							});
});