(function($) {

	/**
	 * Compare Skin implementation for the module Toolsitem.
	 *
	 * @namespace Tc.Module.Default
	 * @class Basic
	 * @extends Tc.Module
	 * @constructor
	 */
	Tc.Module.Toolsitem.Compare = function (parent) {
		/**
		 * override the appropriate methods from the decorated module (ie. this.get = function()).
		 * the former/original method may be called via parent.<method>()
		 */
		this.on = function (callback) {
			// calling parent method
			parent.on(callback);

			this.addProductToCompareList = $.proxy(this.addProductToCompareList, this);
			this.removeProductFromCompareList = $.proxy(this.removeProductFromCompareList, this);
			this.trackAddToCompareListAction = $.proxy(this.trackAddToCompareListAction, this);
			digitalData.product.compareList = [];

			// connect
			this.sandbox.subscribe('metaHDCompare', this);
			this.sandbox.subscribe('lightboxStatus', this);

			var mod = this,
				$icoCompare = this.$$('.ico-compare');


			// compare button
			$icoCompare.off('click.Toolsitem.Compare').on('click.Toolsitem.Compare', function(ev) {
				var quantity = -1,
					productCode = $icoCompare.data('product-code'),
					productCodesArray = [productCode];

				// Item is ALREADY on the list and needs to be removed
				if ($(this).hasClass('active')) {
					// visually
					mod.$$('.ico-compare').removeClass('active');
					if(digitalData.product.compareList.indexOf(productCode.toString()) !== -1) {
						digitalData.product.compareList.splice(digitalData.product.compareList.indexOf(productCode.toString()), 1);
					}
					// effectively
					mod.removeProductFromCompareList({ productCodes: productCodesArray }, "/compare/remove");
				}
				// item needs to be added to list
				else{
					window.dataLayer.push({
						event: 'compareProducts',
						compareProduct: productCode.toString()
					});
					quantity = 1;
					mod.addProductToCompareList({ productCodes: productCodesArray}, "/compare/add", quantity);
				}

				mod.chekCompareSelection();
			});

            mod.chekCompareSelection();
		};

		this.chekCompareSelection = function () {

            var compareSelectedOldLen = $('.compare-list-size').text();

            setTimeout(function(){

                var compareSelectedNewLen = $('.compare-list-size').text();

                if ( Number(compareSelectedNewLen) > 1 ) {
                    $('.skin-toolsitem-compare-popup-plp').addClass('active');
                } else {
                    $('.skin-toolsitem-compare-popup-plp').removeClass('active');
                }

			}, 2500);

        };

		// remove from compare helper
		this.removeProductFromCompareList = function(data){
			var _quantity = -1,
				_productCode = data.productCodes[0],
				mod = this,
				$icoCompare = mod.$$('.ico-compare');

			if ($icoCompare.data('product-code') == _productCode && typeof _productCode !== 'undefined') {
				$.ajax({
					url: '/compare/remove',
					type: 'post',
					data: {
						productCode: _productCode
					},
					dataType: 'json',
					success: function(response) {
						// trigger compareChange
						mod.fire('compareChange', {
							'compareProductsData': response.compareProductsData,
							'quantityChange': _quantity
						}, ['metaHDCompare']);
						
						$('.compare-list-size').text(parseInt($('.compare-list-size').text()) - 1);
                        $('.plp-compare__compare-count').html( $('.compare-list-size').text() );
                        $('.plp-compare__item-added').removeClass('showhide');
					},
					error: function(jqXHR, textStatus, errorThrown) {
						// Ajax Error
					}
				});
			}
		};


		// add to compare helper
		this.addProductToCompareList = function (data, path, quantity) {
			var mod = this;

            mod.$$('.ico-compare').addClass('active');
			digitalData.product.compareList.push(mod.$$('.ico-compare').attr('data-product-code'));

			$.ajax({
				url: path,
				type: 'post',
				data: data,
				success: function (data, textStatus, jqXHR) {
					if(quantity > 0){

						$('.compare-list-size').text(parseInt($('.compare-list-size').text()) + 1);
                        $('.plp-compare__compare-count').html( $('.compare-list-size').text() );
                        $('.plp-compare__item-added').addClass('showhide');
                        $('.skin-toolsitem-compare-popup-plp').addClass('active');

                        setTimeout(function(){
                            $('.plp-compare__item-added').removeClass('showhide');
                        }, 2000);
					}

					// trigger compareChange
					mod.fire('compareChange', { 'compareProductsData': data.compareProductsData, 'quantityChange': quantity }, ['metaHDCompare']);

				},
				error: function (jqXHR, textStatus, errorThrown) {
                    mod.$$('.ico-compare').removeClass('active');
				}
			});
		};

	};

})(Tc.$);
