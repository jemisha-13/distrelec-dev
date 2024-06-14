(function($) {

	/**
	 * Skin implementation for the module account-login-data.
	 *
	 * @author Céline Müller
	 * @namespace Tc.Module.LoginData
	 * @class Basic
	 * @extends Tc.Module
	 * @constructor
	 */
	Tc.Module.AccountLoginData.ChangeName = function (parent) {

		/**
		 * override the appropriate methods from the decorated module (ie. this.get = function()).
		 * the former/original method may be called via parent.<method>()
		 */
		this.on = function (callback) {

			var self = this;

			// Lazy Load SelectBoxIt Dropdown
			if(!Modernizr.touch && !Modernizr.isie7) {
				Modernizr.load([{
					load: '/_ui/all/cache/dyn-jquery-selectboxit.min.js',
					complete: function () {
						 self.$$('#select-title,#select-department,#select-function').selectBoxIt({
							autoWidth : false
						});
					}
				}]);
			}

            // calling parent method
            parent.on(callback);
		};

	};

})(Tc.$);
