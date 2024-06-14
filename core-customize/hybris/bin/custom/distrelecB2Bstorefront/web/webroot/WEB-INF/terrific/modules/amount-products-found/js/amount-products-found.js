(function($) {

	/**
	 * Module implementation.
	 *
	 * @namespace Tc.Module
	 * @class Amount-products-found
	 * @extends Tc.Module
	 */
	Tc.Module.AmountProductsFound = Tc.Module.extend({

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
		init: function($ctx, sandbox, id) {
			// call base constructor
			this._super($ctx, sandbox, id);

			this.sandbox.subscribe('facetActions', this);

			this.onLoadProductsForFacetSearchCallback = $.proxy(this, 'onLoadProductsForFacetSearchCallback');
		},

		/**
		 * Hook function to do all of your module stuff.
		 *
		 * @method on
		 * @param {Function} callback function
		 * @return void
		 */
		on: function(callback) {
			var $ctx = this.$ctx,
				self = this;

			$('.mod-amount-products-found .view-all a').click(function(e) {
				e.preventDefault();

                $('html, body').animate({
                    scrollTop: $("#select-productlist-orderSelectBoxItContainer").offset().top - 50
                }, 2000);
            });

			callback();
		},

		onLoadProductsForFacetSearchCallback: function(data){
			if(data.status){
				this.$$('.count .nr').html(data.productsCount);
				var text="";

				if(data.productsCount === 1){
					text= this.$$('.count').data('text-singular');
				}
				else{
					text= this.$$('.count').data('text-plural');
				}
				this.$$('.count .text').html(text);

				if( data.productsCountCatalogPlus > 0 ){
					this.$$('.service-plus').removeClass('hidden');
					this.$$('.service-plus .nr-service-plus').html(data.productsCountCatalogPlus);
				}
				else{
					this.$$('.service-plus').addClass('hidden');
				}
			}
		},


		/**
		 * Hook function to trigger your events.
		 *
		 * @method after
		 * @return void
		 */
		after: function() {
			// Do stuff here or remove after method
			//...
			if ( window.location.href.indexOf("filter_CuratedProducts") > -1 ) {
				$('.mod-amount-products-found .nr').css('display','none');
				$('.mod-amount-products-found .text').css('display','none');
				$('.mod-amount-products-found .view-all a').css('margin','0');
			}
		}

	});

})(Tc.$);
