(function ($) {

	/**
	 * Module implementation.
	 *
	 * @namespace Tc.Module
	 * @class CarouselTeaserItem
	 * @extends Tc.Module
	 */
	Tc.Module.CarouselTeaserItem = Tc.Module.extend({

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

			this.triggerCart = $.proxy(this, 'triggerCart');

		},

		/**
		 * Hook function to do all of your module stuff.
		 *
		 * @method on
		 * @param {Function} callback function
		 * @return void
		 */
		on: function (callback) {
			
			var self = this,
				$ctx = this.$ctx;

			var $itemLink = $('.item-link', this.$ctx);

			this.productCode = this.$ctx.find('input[type="hidden"][class="hidden-product-code-erp"]').val();

			this.$$('a.btn.btn-buy').on('click', this.triggerCart);
			

			callback();  
		},
		
		

		/////////////////////////////////////////////////////////// 

		/**
		 * Hook function to do all of your module stuff. carousel-teaser-item
		 *
		 * @method on
		 * @param {Function} callback function
		 * @return void
		 */
		triggerCart: function(e){
			e.preventDefault();

            var $clickedLink = $(e.delegateTarget)
                ,$product = $clickedLink.closest('.mod-carousel-teaser-item');
            var productCode = $product.find('.hidden-product-code').val();

			// Trigger Cart API to add to cart
			$(document).trigger('cart', {
				actionIdentifier: 'carpetAddToCart',
				type: 'add',
				productCodePost: productCode,
				qty: 0 // backend magic: we send 0 and the backend automatically set it to the minimum quantity
			});
			
			if($clickedLink.hasClass('reloadPage') && window.location.pathname === '/cart'){
				setTimeout(function(){
					location.reload();
				}, 1000);
			}
			
		}

	});

})(Tc.$);
