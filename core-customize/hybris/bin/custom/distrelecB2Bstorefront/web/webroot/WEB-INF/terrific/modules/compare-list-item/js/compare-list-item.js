(function ($) {

	/**
	* Module implementation.
	*
	* @namespace Tc.Module
	* @class Buying-section
	* @extends Tc.Module
	*/
	Tc.Module.CompareListItem = Tc.Module.extend({

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

			// connect
			this.sandbox.subscribe('metaHDCompare', this);
			this.sandbox.subscribe('lightboxStatus', this);
			this.sandbox.subscribe('compareGrid', this);
			this.sandbox.subscribe('toolItems', this);

			this.$btnCart = $('.btn-cart', $ctx);
			this.$btnCartSecond = $('.compare-cart', $ctx);
			this.$btnRemove = $('.b-remove', $ctx);
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
				$ctx = self.$ctx;

			this.sandbox.subscribe('comparelist', this);

			this.onRemoveItemFromComparelist = $.proxy(this.onRemoveItemFromComparelist, this);

			// most actions are covered by submodules
			// column hover
			$ctx.on('mouseenter', '.b-item', function() {
				$(this).addClass('hover');
			});
			$ctx.on('mouseleave', '.b-item', function() {
				$(this).removeClass('hover');
			});

			// add to cart button
			self.$btnCart.on('click.CompareListItem.Cart', $.proxy(this.addToCart, this));

			self.$btnCartSecond.on('click.CompareListItem.Cart', $.proxy(this.addToCart, this));

			callback();
		},

        addToCart: function() {

            var	 productCode = this.$$('.hidden-product-code').val();

            // Trigger Cart API to add to cart
            $(document).trigger('cart', {
                actionIdentifier: 'compareListAddToCart'
                ,type: 'add'
                ,productCodePost: productCode
                ,qty: 0 // backend magic: we send 0 and the backend automatically set it to the minimum quantity
            });


        },

		onRemoveItemFromComparelist: function(data){
			var _quantity = -1// default quantity for remove
				,_productCode = data.productCode
				,_redirect = "/compare"
				,mod = this
			;

			if (this.$$('.g-item').data('id') == _productCode && typeof _productCode !== 'undefined') {
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

						// remove item from grid
						mod.$ctx.remove();

						window.location.replace(_redirect);
					},
					error: function(jqXHR, textStatus, errorThrown) {
						// Ajax Error
					}
				});
			}
		},
		
		
		onRemoveAllItemsFromComparelist: function(data){
			var _quantity = -1// default quantity for remove
				,_productCode = data.productCode
				,_redirect = "/compare"
				,mod = this
			;

			$.ajax({
				url: '/compare/removeAll',
				type: 'post',
				data: {
				},
				dataType: 'json',
				success: function(response) {
					// trigger compareChange
					mod.fire('compareChange', {
						'compareProductsData': response.compareProductsData,
						'quantityChange': _quantity
					}, ['metaHDCompare']);

					// remove item from grid
					mod.$ctx.remove();
					
					$('.compare-list-size').text(0);

					window.location.replace(_redirect);
				},
				error: function(jqXHR, textStatus, errorThrown) {
				}
			});
			
		}		
		
		
		

	});
})(Tc.$);
