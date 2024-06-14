(function($) {

	/**
	 * Productlist Skin Favorite implementation for the module Productlist Order.
	 *
	 */
	Tc.Module.CarouselTeaser.Product = function (parent) {

		/**
		 * override the appropriate methods from the decorated module (ie. this.get = function()).
		 * the former/original method may be called via parent.<method>()
		 */
		this.on = function (callback) {

			// bind handlers to module context
			this.requestProductToggleStates = $.proxy(this.requestProductToggleStates, this);

			parent.requestProductToggleStates();

			parent.on(callback);
		};

	};

})(Tc.$);

