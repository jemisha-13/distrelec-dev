(function($) {

	/**
	 * Productlist Skin DetailPage implementation for the module Productlist.
	 * All products are loaded on page load. Availability and toggle states are requested for all products and
	 * show more only makes the hidden products visible
	 *
	 */
	Tc.Module.Productlist.DetailPage = function (parent) {

		this.on = function (callback) {

			this.onShowMoreButtonClick = $.proxy(this, 'onShowMoreButtonClick');
			this.$$('.btn-show-more').on('click', this.onShowMoreButtonClick);

			parent.on(callback);

		};

		this.onShowMoreButtonClick = function (ev) {
			var mod = this;
			ev.preventDefault();

			var showMoreButton = $(ev.target).closest('a')
				,templateTechnical = doT.template(this.$$('#tmpl-product-list-item-technical').html())
				,newProducts = []
				,productCodesArray = []
				,actionURL = showMoreButton.data('action-url') + showMoreButton.data('ajax-url-postfix')
				,pageSize = showMoreButton.data('page-size')
				,offset = this.$$('.mod-product:not(.skin-product-template)').length
				;

			$.ajax({
				type: 'GET',
				url: actionURL,
				dataType: 'json',
				data: {
					offset: offset,
					size: pageSize
				},
				success: function (data) {

					$.each(data.products, function(index, item) {

                        // Add URI Encoded Values for Webtrekk 7.5 (dot Template)
                        item.nameURIEncoded = encodeURI(item.name);
                        item.manufacturerURIEncoded = encodeURI(item.manufacturer);
                        item.typeNameURIEncoded = encodeURI(item.typeName);
                        item.position = index;
                        item.volumePricesMap = item.volumePrices;

                        var $newProduct = $(templateTechnical(item)).css("display","none");
                        var $module = $newProduct.wrap('<div></div>').parent();

                        mod.sandbox.addModules($module);

                        mod.$$('.productlist .list', mod.$ctx).append($newProduct);
						$newProduct.fadeIn();
						newProducts[index] = $newProduct;
						productCodesArray[index] = $newProduct.find('.hidden-product-code').val();
					});

					// Make ajax calls for availability and toggle states
					mod.requestProductAvailabilityStates(productCodesArray, newProducts);
					mod.requestProductToggleStates (productCodesArray);

					if(data.products.length === pageSize){
						$('.row-show-more').removeClass('loading');
						$('.row-show-more').slideDown();
					}
					else{
						$('.row-show-more').slideUp();
					}
					
					$('.loading-similars').addClass('hidden');
				}
			});
		};
	};

})(Tc.$);
