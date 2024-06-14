(function ($) {

	/**
	 * Module implementation.
	 *
	 * @namespace Tc.Module
	 * @class Mainnav
	 * @extends Tc.Module
	 */
	Tc.Module.OrderOverviewBox = Tc.Module.extend({

		/**
		 * Initialize.
		 *
		 * @method init
		 * @return {void}
		 * @constructor
		 * @param {jQuery} $ctx the jquery context
		 * @param {Sandbox} sandbox the sandbox to get the resources from
		 * @param {Number} id the unique module id
		 */
		init: function ($ctx, sandbox, id) {
			// call base constructor
			this._super($ctx, sandbox, id);

		},

		/**
		 * Hook function to do all of your module stuff.
		 *
		 * @method on
		 * @param {Function} callback function
		 * @return void
		 */
		on: function (callback) {
			
			var self = this;

			// subscribe to connector channel/s
			this.sandbox.subscribe('myaccount', this);
			this.sandbox.subscribe('cartlistBulkAction', this);
			
			// bind click event to Add to cart buttons
			this.onClick = $.proxy(this, 'onClick');
			this.$$('.btn-add-to-cart').on('click', this.onClick);

			callback();
		},

		onClick: function (ev) {
			ev.preventDefault();
			var allProducts = $('.cart-list .mod-cart-list-item'),
				productsJson = '[{',
				quotationId = $('.cart-list').data('quote-id'),
				errorText = $('.cart-list').data('error-text'),
				loadingState = $('.skin-loading-state-loading-state');

			if (!isNaN(quotationId) && allProducts.length) {
				loadingState.removeClass('hidden');
				allProducts.each(function( index, product ) {
					var  $product = $(product)
						,productCode = $product.find('.hidden-product-code').val()
						,productQty = $product.find('.ipt').val() || parseInt($product.find('.qty').text()) 
						,itemNumber = $product.find('.hidden-item-number').val()
					;

					if(index > 0){
						productsJson += '},{';
					}

					// build cartAPI productCode & quantity json
					productsJson += '"productCode":"' + productCode + '",';
					productsJson += '"quantity":' + productQty + ',';
					productsJson += '"itemNumber":"' + itemNumber + '",';
					productsJson += '"reference":""';
				});

				productsJson += '}]';

				$.ajax({
					url: '/cart/addquotation',
					type: 'POST',
					data: {quotationId: quotationId, productsJson: productsJson},
					dataType: 'json',
					success: function (data, textStatus, jqXHR) {
						if (typeof data.status!=='undefined' && data.status==='ok') {
							window.location.href = '/cart';
						} else {
							if (typeof data.errorMsg!=='undefined' && data.errorMsg.length) {
								errorText = data.errorMsg;
							}
							loadingState.addClass('hidden');
							alert(errorText);
						}
					},
					error: function (jqXHR, textStatus, errorThrown) {
						loadingState.addClass('hidden');
						alert(errorText);
					}
				});
			}
		}
	
	});

})(Tc.$);
