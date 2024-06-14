(function ($) {

	/**
	 * Module implementation.
	 *
	 * @namespace Tc.Module
	 * @class Captcha
	 * @extends Tc.Module
	 */
	Tc.Module.PopularCategories = Tc.Module.extend({

		/**
		 * Initialize.
		 *
		 * @method init
		 * @return {void}
		 * @constructor
		 * @param {jQuery} $ctx the jquery context
		 * @param {Sandbox} sandbox the sandbox to get the resources from
		 * @param {Number} id the unique module id
		 */
		init: function ($ctx, sandbox, id) {
			// call base constructor
			this._super($ctx, sandbox, id);
		},

		/**
		 * Hook function to do all of your module stuff.
		 *
		 * @method on
		 * @param {Function} callback function
		 * @return void
		 */
		on: function (callback) {
			var carouselWidth = 1200;
			var carousel = null;
			var dynamicWidth = ($(window).outerWidth() >= 540 || $(window).outerWidth() === 0 ? 200 : 110);
			var carousel_Config = {
				slideWidth: dynamicWidth,
				minSlides: 1,
				maxSlides: 4,
				moveSlides: 1,
				slideMargin: 10,
				pause: 5000,
				pager: false,
				controls: false,
				auto: true,
				speed: 1000,
				infiniteLoop: true,
				useCSS: false
			};

			// device detection
			if( $(window).width() < 1200) {
				$('.img-responsive').addClass('img_defer');
			}

			if( $(window).width() < carouselWidth) {
				if(carousel === null) {
					carousel = $('.category-parent').bxSlider(carousel_Config);
				}
			}

			$(window).resize(function() {
				if( $(window).width() < carouselWidth) {
					if(carousel === null) {
						carousel = $('.category-parent').bxSlider(carousel_Config);
                        $('.category-parent > li').removeClass('sliderUnmount');
					}
					else {
						carousel.reloadSlider(); //reloading the slider if already instance present
					}
				} else {
					if(carousel){
						carousel.destroySlider();
						carousel = null;
                        $('.category-parent > li').addClass('sliderUnmount');
					}
				}
			});

			callback();
		}

	});

})(Tc.$);
