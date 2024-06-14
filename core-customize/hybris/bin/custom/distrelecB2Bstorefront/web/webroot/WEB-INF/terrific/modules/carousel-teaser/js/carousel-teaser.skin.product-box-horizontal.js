(function($) {

	/**
	 * Productlist Skin Favorite implementation for the module Productlist Order.
	 *
	 */
	Tc.Module.CarouselTeaser.ProductBoxHorizontal = function (parent) {

		/**
		 * override the appropriate methods from the decorated module (ie. this.get = function()).
		 * the former/original method may be called via parent.<method>()
		 */
		this.on = function (callback) {
			this.requestProductToggleStates = $.proxy(this, 'requestProductToggleStates');
			parent.on(callback);
		};
		
		this.after = function (callback) {
			
		};


	};

})(Tc.$);

