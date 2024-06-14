(function($) {

	/**
	 * Module implementation.
	 *
	 * @namespace Tc.Module
	 * @class ProductlistControllbar
	 * @extends Tc.Module
	 */
	Tc.Module.ProductlistControllbar = Tc.Module.extend({

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

			// Subscribe to channels
			this.sandbox.subscribe('facetActions', this);

			this.onLoadProductsForFacetSearch = $.proxy(this, 'onLoadProductsForFacetSearch');
			this.onLoadProductsForFacetSearchCallback = $.proxy(this, 'onLoadProductsForFacetSearchCallback');
			
			if ($('.divScrollTop').width() > $('.wrapperScrollTop').width() + 10) {
				$('.skin-productlist-search-tabular').css('margin-top', '12px');
				$('.wrapperScrollTop').css('visibility', 'visible');
			}

			else {
				$('.skin-productlist-search-tabular').css('margin-top', '0');
				$('.wrapperScrollTop').css('visibility', 'hidden');
			}




		},

		/**
		 * Hook function to do all of your module stuff.
		 *
		 * @method on
		 * @param {Function} callback function
		 * @return void
		 */
		on: function(callback) {
			var self = this;
			
			$(window).resize(function() {
				
				if ($('.divScrollTable-s').length === 0) {
				
					if ($('.divScrollTop').width() > $('.wrapperScrollTop').width() + 10) {
						$('.skin-productlist-search-tabular').css('margin-top', '12px');
						$('.wrapperScrollTop').css('visibility', 'visible');
					}

					else {
						$('.skin-productlist-search-tabular').css('margin-top', '0');
						$('.wrapperScrollTop').css('visibility', 'hidden');
					}

				}

			});

			callback();
		},

		

		/**
		 * Hook function to trigger your events.
		 *
		 * @method after
		 * @return void
		 */
		after: function() {
	
		},

		onLoadProductsForFacetSearch: function(data){
			this.$$('.ajax-action-overlay').show();
		},

		onLoadProductsForFacetSearchCallback: function(data){
			var mod = this;
			this.$$('.ajax-action-overlay').hide();
		}

	});

})(Tc.$);
