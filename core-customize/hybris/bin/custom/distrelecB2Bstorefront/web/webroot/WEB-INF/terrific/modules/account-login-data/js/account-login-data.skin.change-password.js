(function($) {

	/**
	 * ChangePassword Skin implementation for the module account-login-data.
	 *
	 * @author Céline Müller
	 * @namespace Tc.Module.Default
	 * @class Basic
	 * @extends Tc.Module
	 * @constructor
	 */
	Tc.Module.AccountLoginData.ChangePassword = function (parent) {

		/**
		 * override the appropriate methods from the decorated module (ie. this.get = function()).
		 * the former/original method may be called via parent.<method>()
		 */
		this.on = function (callback) {

            // calling parent method
            parent.on(callback);

		};


	};

})(Tc.$);
