(function($) {

	/**
	 * Bom-Remove Skin implementation for the module Toolsitem.
	 *
	 * @author Céline Müller
	 * @namespace Tc.Module.Toolsitem
	 * @class Basic
	 * @extends Tc.Module
	 * @constructor
	 */
	Tc.Module.Toolsitem.BomRemove = function (parent) {

		/**
		 * override the appropriate methods from the decorated module (ie. this.get = function()).
		 * the former/original method may be called via parent.<method>()
		 */
		this.on = function (callback) {
			// calling parent method
			parent.on(callback);

			var mod = this;

            this.$ico = this.$$('.ico');

			mod.$ico.on('click.Toolsitem.Bom.remove', function (ev) {
				var productCode = mod.$ico.data('product-code');
				$(ev.target).closest('.list__item').slideUp(400, function() {
					$(this).remove();
					if ( $('.skin-productlist-bom .productlist .list li').length === 0 ) {
						$('.mod-bom-toolbar ._right').addClass('hidden');
					}
				});
            });
		};
	};

})(Tc.$);
