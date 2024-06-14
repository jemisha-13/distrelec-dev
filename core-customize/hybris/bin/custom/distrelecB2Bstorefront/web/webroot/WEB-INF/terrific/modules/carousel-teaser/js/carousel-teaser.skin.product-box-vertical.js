(function($) {

	/**
	 * Productlist Skin Favorite implementation for the module Productlist Order.
	 *
	 */
	Tc.Module.CarouselTeaser.ProductBoxVertical = function (parent) {

		/**
		 * override the appropriate methods from the decorated module (ie. this.get = function()).
		 * the former/original method may be called via parent.<method>()
		 */
		this.on = function (callback) {
			this.requestProductToggleStates = $.proxy(this, 'requestProductToggleStates');
			parent.on(callback);
		};
		
		
		this.after = function () {
			
			if ($('.replacementProductsCartPage').length > 0){
				var replacementsCounter =  $('.replacementProductsCartPage').find('.skin-carousel-teaser-item-product-vertical-box').length;
				
				if (replacementsCounter === 2){
					$('.replacementProductsCartPage').height(350);
				}
				
			}

		};

	};

})(Tc.$);

