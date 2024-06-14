(function($) {

    /**
     * User Management Skin implementation for the module ListFilter.
     *
     * @author Céline Müller
     * @namespace Tc.Module.ListFilter
     * @class Basic
     * @extends Tc.Module
     * @constructor
     */
    Tc.Module.AccountListFilter.UserManagement = function (parent) {

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
						self.$$('#filter_state').selectBoxIt({
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
