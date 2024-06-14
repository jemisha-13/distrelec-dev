(function($) {

	/**
	 * Compare Skin implementation for the module Toolsitem.
	 *
	 * @namespace Tc.Module.Default
	 * @class Basic
	 * @extends Tc.Module
	 * @constructor
	 */
	Tc.Module.Toolsitem.CompareBottom = function (parent) {
		/**
		 * override the appropriate methods from the decorated module (ie. this.get = function()).
		 * the former/original method may be called via parent.<method>()
		 */
		this.on = function (callback) {
			// calling parent method
			parent.on(callback);


			// connect
			this.sandbox.subscribe('comparelist', this);

			var mod = this,
				$icoCompare = this.$$('.ico-compare');


			// compare button
			$icoCompare.off('click.Toolsitem.Compare').on('click.Toolsitem.Compare', function(ev) {
				ev.preventDefault();

				var quantity = -1,
					_productCode = $icoCompare.data('product-code');

				// Item is ALREADY on the list and needs to be removed
				if ($(this).hasClass('active')) {
					// visually
					mod.$$('.ico-compare').removeClass('active');

					// fire event to remove the product from compare list
					mod.fire('removeItemFromComparelist', { productCode: _productCode}, ['comparelist']);
				}
			});
		};
	};

})(Tc.$);
