(function($) {

	/**
	 * Productlist Skin Favorite implementation for the module Productlist Order.
	 *
	 */
	Tc.Module.CarouselTeaser.ProductLazyLoadAccessories = function (parent) {

		/**
		 * override the appropriate methods from the decorated module (ie. this.get = function()).
		 * the former/original method may be called via parent.<method>()
		 */
		this.on = function (callback) {

			// bind handlers to module context
			this.requestProductToggleStates = $.proxy(this, 'requestProductToggleStates');
			this.initializeModule = $.proxy(this, 'initializeModule');
			
			parent.on(callback);
		};
		
		
		parent.isElementVisible = function (elementToBeChecked) {
		    var TopView = $(window).scrollTop();
		    var BotView = TopView + $(window).height();
		    var TopElement = $(elementToBeChecked).offset().top;
		    var BotElement = TopElement + $(elementToBeChecked).height();
		    return ((BotElement <= BotView) && (TopElement >= TopView));		
		};
		
		
		//initializeModule accesories
		parent.initializeModule = function () {
			var mod = this
				,$carouselContainer = this.$ctx.find('.carousel-teaser-container')
				,ajaxUrlPostfix = $carouselContainer.data('ajax-url')
				,maxItemsInCarousel = $carouselContainer.data('max-items-in-carousel')
				,maxItemsVisibleForComponentWidth = $carouselContainer.data('max-items-visible')
				,carouselTeaserItemTemplate = doT.template(this.$$('#tmpl-carousel-teaser-item').html())
				,productUrl = Tc.Utils.splitUrl(document.URL)
			;		
			
			// get base url from product url, because get parameters give a 500 error
			productUrl = productUrl.base;
			
			$.ajax({
				url: productUrl+ajaxUrlPostfix, // /de/.../p/15413281/accessories, '/_ui/all/data/carousel-teaser-product.json',
				type: 'GET',
				dataType: 'json',
				success: function (data, textStatus, jqXHR) {
					if(data.products.length > 0){
						var numberOfItemsFromFactFinder = data.products.length
							,itemsVisible = maxItemsVisibleForComponentWidth
						;

						// If there are less products coming from factfinder, than can be displayed, itemsVisible gets adjusted to the smaller number
						// also Nav Buttons are not visible and header has more space
						if(numberOfItemsFromFactFinder <= maxItemsVisibleForComponentWidth){
							itemsVisible = numberOfItemsFromFactFinder;
							mod.$ctx.find('.carousel-header').addClass('no-nav-buttons');
							mod.$ctx.find('.carousel-nav').addClass('hidden');
						}
						mod.$ctx.find('.carousel-teaser').data('items-visible', itemsVisible);

						// component property maxItemsToDisplay (property from cms) can override the number of items coming from factfinder
						if(maxItemsInCarousel > 0){
							numberOfItemsFromFactFinder = maxItemsInCarousel;
						}
						$.each(data.products, function(index, product) {
							if(index < numberOfItemsFromFactFinder){

								product.price.formattedValue = Tc.Utils.formatPrice(product.price.formattedValue, $('.shopsettings').data('country'));

								var $newTeaserItem = $(carouselTeaserItemTemplate(product));

								// markup needs to be wrapped with a div to be recognized as a module by the sandbox function
								var $module = $newTeaserItem.wrap('<div></div>').parent();
								mod.sandbox.addModules($module);

								$carouselContainer.find('.carousel-teaser').append($newTeaserItem);

								$newTeaserItem.data('position', index + 1);
							}
						});

						mod.requestProductToggleStates();

						// Lazy Load caroufredsel
						Modernizr.load([{
							load: '/_ui/all/cache/dyn-jquery-caroufredsel.min.js',
							complete: function () {

								mod.$ctx.fadeIn(function(){
									mod.$ctx.removeClass('loading');
								});

								// finally create carousel
								parent.createCarousel();
							}
						}]);
					}
					else{
						$('.cart-page-product-box-component').removeClass('hidden');
					}
				},
				error: function (jqXHR, textStatus, errorThrown) {
					// Do Nothing??
				}
			});			
			

		};

	};

})(Tc.$);


