(function($) {

	/**
	 * Productlist Skin Favorite implementation for the module Productlist Order.
	 *
	 */
	Tc.Module.CarouselTeaser.Campaign = function (parent) {

		/**
		 * override the appropriate methods from the decorated module (ie. this.get = function()).
		 * the former/original method may be called via parent.<method>()
		 */
		this.on = function (callback) {
			this.sandbox.subscribe('campaign', this);

			// bind handlers to module context
			this.requestProductToggleStates = $.proxy(this, 'requestProductToggleStates');
			this.initializeModule = $.proxy(this, 'initializeModule');
			this.onCarouselChange = $.proxy(this, 'onCarouselChange');
			this.onHideCarousel = $.proxy(this, 'onHideCarousel');

			parent.on(callback);
		};

		parent.initializeModule = function () {
		};

		this.onCarouselChange = function (data) {
			var mod = this
				,$carouselContainer = this.$ctx.find('.carousel-teaser-container')
				,maxItemsInCarousel = $carouselContainer.data('max-items-in-carousel')
				,maxItemsVisibleForComponentWidth = $carouselContainer.data('max-items-visible')
				,carouselTeaserItemTemplate = doT.template(this.$$('#tmpl-carousel-teaser-item').html())
				;

			if(data.factFinderCampaign.length > 0){
				var numberOfItemsFromFactFinder = data.factFinderCampaign.length
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

				$.each(data.factFinderCampaign, function(index, product) {
					if(index < numberOfItemsFromFactFinder){

						product.position = index;

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
						$('.skin-carousel-teaser-feedback').addClass('hidden'); // hide carousel which is initialized on page load
						parent.createCarousel();
					}

				}]);

			}

		};

		this.onHideCarousel = function (data) {
			var mod = this;

			mod.$ctx.addClass('loading');
			mod.$ctx.hide();
		};

	};

})(Tc.$);

