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
	Tc.Module.Toolsitem.FavoriteRemove = function (parent) {

		/**
		 * override the appropriate methods from the decorated module (ie. this.get = function()).
		 * the former/original method may be called via parent.<method>()
		 */
		this.on = function (callback) {
			// calling parent method
			parent.on(callback);

			// subscribe to connector channel/s
			this.sandbox.subscribe('favoritelist', this);

			var mod = this;

            this.$ico = this.$$('.ico');

			mod.$ico.on('click.Toolsitem.Favorite.remove', function () {
				var productCode = mod.$ico.data('product-code');

				$.ajax({
					url: '/shopping/favorite/remove',
					type: 'post',
					data: {
						productCode: productCode
					},
					success: function () {
						$(document).trigger('listsChange', {
							type: 'favorite',
							quantity: -1
						});

						mod.fire('removeProductFromFavoriteList', { productCode : productCode }, ['favoritelist']);
						
						$('.favorite-list-size').text(parseInt($('.favorite-list-size').text()) - 1);
					},
					error: function (jqXHR, textStatus, errorThrown) {
					}
				});
            });
		};
	};

})(Tc.$);
