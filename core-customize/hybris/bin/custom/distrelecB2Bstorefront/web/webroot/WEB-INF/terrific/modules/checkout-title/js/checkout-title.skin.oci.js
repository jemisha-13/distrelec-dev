(function($) {

	/**
	 *
	 * @namespace Tc.Module
	 * @class CheckoutTitle
	 * @skin oci
	 * @extends Tc.Module
	 */
	Tc.Module.CheckoutTitle.Oci = function (parent) {
	
		/**
		* override the appropriate methods from the decorated module (ie. this.get = function()).
		* the former/original method may be called via parent.<method>()
		*/
		
		this.on = function (callback) {

		    parent.on(callback);

		};
	};

})(Tc.$);