jQuery(document)
		.ready(
				function() {

					module("hand");
					test(
							"a basic hand visibility test",
							function() {
								ok((jQuery('span#myGallery').length == 1),
										"a span with id myGallery should be present");
								var myGalleryContent = jQuery('span#myGallery div');
								ok((myGalleryContent.length > 0),
										"it should itself contain other elements, actually found: "
												+ myGalleryContent.length);
								var slideElements = jQuery('span#myGallery div.slideElement');
								ok((slideElements.length == 6),
										"these elements should contain the 6 images in the hand, actually found: "
												+ slideElements.length);
								var cssAttribute = jQuery(
										'span#myGallery div.slideElement:first')
										.css('background-image');
								ok(
										((cssAttribute.indexOf("url(") != -1) && (cssAttribute
												.indexOf("/cards/") != -1)),
										"the first of these images should have a source URL containing 'url(' and '/cards/', and it appears to be: "
												+ cssAttribute);
							});
					module("menubar");
					test(
							"a basic menubar visibility test",
							function() {
								var menubaritem1 = jQuery('div.bd ul.first-of-type li.yuimenubaritem a.yuimenubaritemlabel:first')[0];
								ok(
										(menubaritem1.href
												.indexOf("communication") != -1),
										"a link Object with class yuimenubaritemlabel whose parent is a li class yuimenubaritem"
												+ "whose parent is a ul class first-of-type whose parent is a div class bd"
												+ " should exist and point to './communication', and it appears to be: "
												+ menubaritem1.href);
								menubaritem1 = jQuery('div.bd ul.first-of-type li.yuimenubaritem a.yuimenubaritemlabel:first')[0];
								ok(
										(menubaritem1.innerHTML == "Documentation"),
										"a link Object with class yuimenubaritemlabel whose parent is a li class yuimenubaritem"
												+ "whose parent is a ul class first-of-type whose parent is a div class bd"
												+ " should exist and contain 'Documentation', and it appears to be: "
												+ menubaritem1.innerHTML);

								var menubaritem2 = jQuery('div.bd ul.first-of-type li.yuimenubaritem a.yuimenubaritemlabel')[1];
								ok(
										(menubaritem2.href
												.indexOf("http://shopping.yahoo.com") != -1),
										"a link Object with class yuimenubaritemlabel whose parent is a li class yuimenubaritem"
												+ "whose parent is a ul class first-of-type whose parent is a div class bd"
												+ " should exist and point to 'http://shopping.yahoo.com', and it appears to be: "
												+ menubaritem2.href);
								menubaritem2 = jQuery('div.bd ul.first-of-type li.yuimenubaritem a.yuimenubaritemlabel')[1];
								ok(
										(menubaritem2.innerHTML == "Game"),
										"a link Object with class yuimenubaritemlabel whose parent is a li class yuimenubaritem"
												+ "whose parent is a ul class first-of-type whose parent is a div class bd"
												+ " should exist and contain 'Game', and it appears to be: "
												+ menubaritem2.innerHTML);

								var allLinks = jQuery('div.bd ul.first-of-type li.yuimenubaritem a.yuimenubaritemlabel-hassubmenu:first');
								var found = false;

								found = allLinks
										.css('background-image')
										.indexOf(
												"menubaritem_submenuindicator.png") != -1;
								ok(
										found,
										"at least a link with the image 'menubaritem_submenuindicator.png' "
												+ "should be present in the page if the menubar is rendered, and it appears to be: "
												+ found);
							});
				});
