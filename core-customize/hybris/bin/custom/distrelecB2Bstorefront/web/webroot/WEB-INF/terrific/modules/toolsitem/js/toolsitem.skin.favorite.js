(function ($) {

	/**
	 * Favorite Skin implementation for the module Toolsitem.
	 *
	 * @author Remo Brunschwiler
	 * @namespace Tc.Module.Default
	 * @class Basic
	 * @extends Tc.Module
	 * @constructor
	 */
	Tc.Module.Toolsitem.Favorite = function (parent) {

		/**
		 * override the appropriate methods from the decorated module (ie. this.get = function()).
		 * the former/original method may be called via parent.<method>()
		 */
		this.on = function (callback) {
			// calling parent method
			parent.on(callback);

			// subscribe to connector channel/s
			this.sandbox.subscribe('lightboxLoginRequiredAddFavorite', this);
			this.sandbox.subscribe('favoritelist', this);

			this.$backendData = $('#backenddata');

			this.addOrRemoveProductToFavoriteList = $.proxy(this.addOrRemoveProductToFavoriteList, this);
			this.checkUserLoggedIn = $.proxy(this.checkUserLoggedIn, this);

			var $ctx = this.$ctx
				,$icoFav = $('.ico-fav', $ctx)
				,mod = this
			;

			$icoFav.on('click.Toolsitem.Favorite', function () {
				if(!$icoFav.hasClass('disabled')){
					var quantity = -1
						, productCode = $icoFav.data('product-code')
						, productCodesArray = []
					;

					// Item is ALREADY on the list and needs to be removed
					if ($(this).hasClass('active')) {
						mod.addOrRemoveProductToFavoriteList({ productCode: productCode}, "/shopping/favorite/remove", quantity);
						// Fire event for the favorite list, to visually remove the product from list
						mod.fire('removeProductFromFavoriteList', { productCode : productCode }, ['favoritelist']);
					}
					// item is not on the list yet
					else{
						quantity = 1;
						productCodesArray[0] = productCode;
						if(mod.checkUserLoggedIn()){
							mod.addOrRemoveProductToFavoriteList({ productCodes: productCodesArray}, "/shopping/favorite/add", quantity);
						}
					}
				}
			});
		};

		this.addOrRemoveProductToFavoriteList = function (data, path, quantity) {
			var  self = this
				,$ctx = this.$ctx
			;

			$.ajax({
				url: path,
				type: 'post',
				data: data,
				success: function (data, textStatus, jqXHR) {
					// Trigger Metahd
					$(document).trigger('listsChange', {
						type: 'favorite',
						quantity: quantity
					});

					$('.ico-fav', $ctx).toggleClass('active');
					
					$('.favorite-list-size').text(data.favoriteListCount);
				},
				error: function (jqXHR, textStatus, errorThrown) {
					// Do Nothing??
				}
			});
		};

		this.checkUserLoggedIn = function () {
			var self = this
				,$usersettings = $('.usersettings', self.$backendData)
				,$icoFav = $('.ico-fav', this.$ctx)
			;

			if ($usersettings.length > '0' && $usersettings.data('login').toString() === 'true') {
				return true;
			} else {
				var productCode = $icoFav.attr('data-product-code');
				if(productCode){
					var loginForm = $('.addFavoriteLoginForm');
					loginForm.attr('action', '/j_spring_security_check?addFavoriteProduct=' + productCode);
				}
				self.fire('openModal', ['lightboxLoginRequiredAddFavorite']);
				return false;
			}
		};

	};

})(Tc.$);
