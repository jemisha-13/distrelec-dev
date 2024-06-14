(function ($) {

	/**
	 * Module implementation.
	 *
	 * @namespace Tc.Module
	 * @class CarpetNewItem
	 * @extends Tc.Module
	 */
	Tc.Module.CarpetNewItem = Tc.Module.extend({

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

			// subscribe to connector channel/s
			this.sandbox.subscribe('lightboxQuotation', this);
			this.sandbox.subscribe('lightboxLoginRequired', this);
			this.sandbox.subscribe('lightboxVideo', this);

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

			this.$$('a.btn.btn-buy').on('click', this.triggerCart);

			callback();
		},

		///////////////////////////////////////////////////////////

		/**
		 * Hook function to do all of your module stuff.
		 *
		 * @method on
		 * @param {Function} callback function
		 * @return void
		 */
        triggerCart: function(e){
            e.preventDefault();

            var $clickedLink = $(e.delegateTarget)
                ,$product = $clickedLink.closest('.mod-carpet-new-item');

            var productCode = $product.find('.hidden-product-code').val();

            // Trigger Cart API to add to cart
            $(document).trigger('cart', {
                actionIdentifier: 'carpetAddToCart',
                type: 'add',
                productCodePost: productCode,
                qty: 0 // backend magic: we send 0 and the backend automatically set it to the minimum quantity
            });
        },

		///////////////////////////////////////////////////////////

		
		/**
		 *
		 * @method itemLinkClicked
		 *
		 */
		 itemLinkClicked: function (e) {
			
			var youtubeId = e.currentTarget.attributes['data-youtubeid'].nodeValue;
			
			if (youtubeId !== ''){
			 
				//blocks the href link
				e.preventDefault();
				
				var self = this;
				
				self.fire('showLightboxVideo', {
					youtubeId: youtubeId
				}, ['lightboxVideo']);
			
			}

		}

	});

})(Tc.$);
