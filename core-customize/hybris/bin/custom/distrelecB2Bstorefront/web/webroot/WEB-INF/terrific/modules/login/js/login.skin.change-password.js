(function($) {

	/**
	 * Dev Skin implementation for the module Layout.
	 *
	 * @author Dominic Modalek
	 * @namespace Tc.Module.Default
	 * @class Basic
	 * @extends Tc.Module
	 * @constructor
	 */
	Tc.Module.Login.ChangePassword = function (parent) {

		/**
		 * override the appropriate methods from the decorated module (ie. this.get = function()).
		 * the former/original method may be called via parent.<method>()
		 */
		this.on = function (callback) {
            // calling parent method
            parent.on(callback);
		};

		this.after = function () {
			// calling parent method
			parent.after();
		};


	};

})(Tc.$);
