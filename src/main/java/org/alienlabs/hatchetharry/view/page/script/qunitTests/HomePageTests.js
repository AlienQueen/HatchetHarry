var before, after;

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
								var slideElements = jQuery('span#gallery span.cross-link');
								ok((slideElements.length == 7),
										"these elements should contain the 7 images in the hand, actually found: "
												+ slideElements.length);
								var attribute = jQuery(
										'span#gallery span.cross-link:first img')
										.attr('src');
								ok(
										((attribute.indexOf("cards") != -1) && (attribute
												.indexOf(".jpg") != -1)),
										"the first of these images should have a source URL containing 'cards' and '.jpg', and it appears to be: "
												+ attribute);
							});
					module("clock"); 
					asyncTest( 
							"test the clock",
							function() {
								expect(1);
								before = jQuery("#clock").text();
								
								window.setTimeout(function() {
									after = jQuery("#clock").text();
									ok((before !== after), "the clock should tick");
									start();
								}, 10000);
							});
});