(function($) {

	/**
	 * Cartbulk Skin implementation for the module Toolsitem.
	 *
	 * @author Céline Müller
	 * @namespace Tc.Module.Toolsitem
	 * @class Basic
	 * @extends Tc.Toolsitem
	 * @constructor
	 */
	Tc.Module.Toolsitem.CartBulk = function (parent) {

		this.on = function (callback) {

			// subscribe to connector channel/s
			this.sandbox.subscribe('cartlistBulkAction', this);

			// module vars
			this.$ico = $('.ico-cart', this.$ctx);

			// event handler & context
			this.onClick = $.proxy(this, 'onClick');
			this.$ctx.on('click', this.$ico, this.onClick);

			// calling parent method
			parent.on(callback);
		};

		this.onClick = function(e) {

			e.preventDefault();

            this.$ico.addClass('active');

            var  $target = $(e.target)
                ,$product = $target.closest('.mod-product')
            ;

            this.fire('addAllProductsToCart', {}, ['cartlistBulkAction']);

        };

	};

})(Tc.$);
