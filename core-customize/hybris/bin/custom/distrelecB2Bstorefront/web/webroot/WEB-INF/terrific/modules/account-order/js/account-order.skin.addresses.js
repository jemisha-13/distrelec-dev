(function ($) {

	/**
	 * Addresses Order Skin Favorite implementation for the module AccountOrder.
	 *
	 */
	Tc.Module.AccountOrder.Addresses = function (parent) {

		/**
		 * override the appropriate methods from the decorated module (ie. this.get = function()).
		 * the former/original method may be called via parent.<method>()
		 */
		this.on = function (callback) {
			parent.on(callback);

		};

	};

})(Tc.$);
