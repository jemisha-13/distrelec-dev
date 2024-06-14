(function ($) {

	/**
	 * Module implementation.
	 *
	 * @namespace Tc.Module
	 * @class ShoppinglistBulkAction
	 * @extends Tc.Module
	 */
	Tc.Module.ShoppinglistBulkAction = Tc.Module.extend({

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

			this.bindChangeEventListener = $.proxy(this.bindChangeEventListener, this);
			this.onSelectAllCheckboxChange = $.proxy(this.onSelectAllCheckboxChange, this);
			this.onAddProductsToExistingShoppingListSuccess = $.proxy(this.onAddProductsToExistingShoppingListSuccess, this);
			this.onAddedNewListAndProductsSuccess = $.proxy(this.onAddedNewListAndProductsSuccess, this);
			this.trackBulkAction = $.proxy(this.trackBulkAction, this);

			// subscribe to connector channel/s
			this.sandbox.subscribe('shoppinglistBulkAction', this);
			this.sandbox.subscribe('lightboxShoppinglist', this);
		},

		/**
		 * Hook function to do all of your module stuff.
		 *
		 * @method on
		 * @param {Function} callback function
		 * @return void
		 */
		on: function (callback) {
			var  self = this
				,$selectCustom
				,$pretext
			;

			this.$select = this.$$('#select-shoppinglist-bulk-action');

			// Lazy Load SelectBoxIt Dropdown
			if(!Modernizr.touch && ! Modernizr.isie7 && ! Modernizr.iseproc) {
				Modernizr.load([{
					load: '/_ui/all/cache/dyn-jquery-selectboxit.min.js',
					complete: function () {
						self.$select.selectBoxIt({
							autoWidth : false,
							defaultText: self.$select.data('pretext')
						});
					}
				}]);
			}

			this.$select.on('change', this.bindChangeEventListener);

			callback();
		},

		/**
		 * Hook function to trigger your events.
		 *
		 * @method after
		 * @return void
		 */
		after: function () {
			// Do stuff here or remove after method
			//...
		},

		onSelectAllCheckboxChange: function(data){
			if(data.isSelected){
				this.$ctx.addClass('active');
			}
			else{
				this.$ctx.removeClass('active');
			}
		},

		onSingleProductsSelectedStateChange: function(data){
			if(data.noProductsSelected){
				this.$ctx.removeClass('active');
			}
			else{
				this.$ctx.addClass('active');
			}
		},

		// If products were really added to existing shopping list(s), uncheck selectAllCheckbox and Products checkbox
		onAddProductsToExistingShoppingListSuccess: function(data){
			this.resetProductSelectionStates();
		},
		// If products were really added to new shopping list, uncheck selectAllCheckbox and Products checkbox
		onAddedNewListAndProductsSuccess: function(data){
			this.resetProductSelectionStates();
		},

		bindChangeEventListener: function(ev){
			var selectedOption = $(ev.target).find(":selected")
				,selectedValue = selectedOption.val()
				,self = this
			;

			if(selectedValue != 'bulkActionDefault'){
				if(selectedValue == "bulkActionFavorite"){
					this.fire('addSelectedProductsToFavoriteList', ['shoppinglistBulkAction']);
					// Uncheck Products immediately
					this.resetProductSelectionStates();
				}
				else if(selectedValue == "bulkActionCompare"){
					this.fire('addSelectedProductsToCompareList', ['shoppinglistBulkAction']);
					// Uncheck Products immediately
					this.resetProductSelectionStates();
				}
				else if(selectedValue == "bulkActionShopping"){
					this.fire('addSelectedProductsToShoppingList', ['shoppinglistBulkAction']);
				}
				else if(selectedValue == "bulkActionCart"){
					this.fire('addSelectedProductsToCart', ['shoppinglistBulkAction']);
					// Uncheck Products immediately
					this.resetProductSelectionStates();
				}
				else if(selectedValue == "bulkActionRemove"){
					this.fire('removeSelectedProductsFromList', ['shoppinglistBulkAction']);
					// Uncheck Products immediately
					this.resetProductSelectionStates();
				}
			}
		},

		resetProductSelectionStates: function(){
			// Reset the dropdown to the default element and state
			this.$select.find('option').removeAttr('selected');
			this.$select.find('option:first-child').attr('selected','selected');
			// only use plugin method, if plugin was initialized
			if(!Modernizr.touch && ! Modernizr.isie7 && ! Modernizr.iseproc) {
				this.$select.data("selectBox-selectBoxIt").refresh();
			}
			// trigger selectAllCheckboxChange to get products unselected
			this.fire('selectAllCheckboxChange', { isSelected : false }, ['shoppinglistBulkAction']);
			// trigger selectAllStateChange to uncheck selectAll Checkbox
			this.fire('selectAllStateChange', { allProductsSelected : false }, ['shoppinglistBulkAction']);
		}
	});

})(Tc.$);
