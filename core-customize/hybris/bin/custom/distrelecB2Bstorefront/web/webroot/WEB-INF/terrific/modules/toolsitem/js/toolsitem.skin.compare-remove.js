(function($) {

	/**
	 * Favorite-remove Skin implementation for the module Toolsitem.
	 *
	 * @author Remo Brunschwiler
	 * @namespace Tc.Module.Default
	 * @class Basic
	 * @extends Tc.Module
	 * @constructor
	 */
	Tc.Module.Toolsitem.CompareRemove = function (parent) {

		/**
		 * override the appropriate methods from the decorated module (ie. this.get = function()).
		 * the former/original method may be called via parent.<method>()
		 */
		this.on = function (callback) {
			// calling parent method
			parent.on(callback);

			// subscribe to connector channel/s
			this.sandbox.subscribe('comparelist', this);

			var mod = this;

            this.$ico = this.$$('.ico');

			mod.$ico.on('click.CompareListItem.Remove', function (ev) {
				ev.preventDefault();

				var productCode = mod.$ico.data('product-code')
					,redirectUrl = mod.$ico.data('redirect-url')
				;

				// fire event to remove the product visually from compare list
				mod.fire('removeItemFromComparelist', { productCode: productCode}, ['comparelist']);
            });
		};
	};

})(Tc.$);
