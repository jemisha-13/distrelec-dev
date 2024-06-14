(function($) {

	/**
	 * EmptyCart Skin implementation for the module Toolsitem.
	 *
	 * @author Petra Weiss
	 * @namespace Tc.Module.Toolsitem
	 * @class Basic
	 * @extends Tc.Toolsitem
	 * @constructor
	 */
	Tc.Module.Toolsitem.EmptyCart = function (parent) {

		this.on = function (callback) {

			this.sandbox.subscribe('cartlistRemoveAction', this);
			this.onClickIcoEmptyCart = $.proxy(this, 'onClickIcoEmptyCart');
			this.$ctx.on('click', '.ico-list', this.onClickIcoEmptyCart);

			var self = this,
				$ctx = this.$ctx,
				$ico = $('.ico-list', $ctx);

			$ico.on('click', function () {
				self.fire('emptyCart', {}, ['cartlistRemoveAction']);
			});

			// calling parent method
			parent.on(callback);
		};
	};

})(Tc.$);
