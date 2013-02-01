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
								var menubaritem1 = jQuery('td.myMenu table.rootVoices tr td.rootVoice:first img:first');
								ok(
										("/image/logoh1.gif" === menubaritem1
												.attr('src')),
										"the first menubar option is the first image of a td class myMenu containing a table class rootVoices containing a tr which contains "
												+ "the first td class rootVoice should exist and point to '/image/logoh1.gif', and it appears to be: "
												+ menubaritem1.attr('src'));
								menubaritem1 = jQuery('div#box_menu table tr td div:first img:first');
								ok(
										(menubaritem1.attr('src') === "/image/logoh2.gif"),
										"the very first menubar entry is the first image contained in the div which id is box_menu and contains a table, a tr, a td and a div"
												+ ", should exist and point to '/image/logoh2.gif', and it appears to be: "
												+ menubaritem1.attr('src'));

								var menubaritem2 = jQuery('td.myMenu table.rootVoices tr td.rootVoice:nth-child(2)');
								ok(
										(menubaritem2.html() === "Game"),
										"the second menubar option is the second td of class rootVoice of the td of class myMenu which contains a table of class rootVoices and a tr."
												+ " It should exist and point to 'Game', and it appears to be: "
												+ menubaritem2.html());
							});
});
