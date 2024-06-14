(function ($) {

	/**
	 * Module implementation.
	 *
	 * @namespace Tc.Module
	 * @class ShoppinglistSelectAll
	 * @extends Tc.Module
	 */
	Tc.Module.ShoppinglistSelectAll = Tc.Module.extend({

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

			this.onSelectAllCheckboxChange = $.proxy(this.onSelectAllCheckboxChange, this);
			this.onSelectAllStateChange = $.proxy(this.onSelectAllStateChange, this);
			this.onToggleShoppingListToolsDisabledState = $.proxy(this.onToggleShoppingListToolsDisabledState, this);

			// subscribe to connector channel/s
			this.sandbox.subscribe('shoppinglistBulkAction', this);
		},

		/**
		 * Hook function to do all of your module stuff.
		 *
		 * @method on
		 * @param {Function} callback function
		 * @return void
		 */
		on: function (callback) {

			this.checkboxSelectAll = this.$$('#select-all');
			this.checkboxSelectAll.on('change', this.onSelectAllCheckboxChange);

			if(this.checkboxSelectAll.is(':checked')) {
				this.onAllCheckboxChecked();
			}

			callback();
		},

		onAllCheckboxChecked : function() {
			this.fire('selectAllCheckboxChange', { isSelected : true }, ['shoppinglistBulkAction']);
		},

		onSelectAllCheckboxChange: function(ev){
			var checkBoxChecked = $(ev.target).prop('checked');
			this.fire('selectAllCheckboxChange', { isSelected : checkBoxChecked }, ['shoppinglistBulkAction']);
		},

		onSelectAllStateChange: function(data){
			if(data.allProductsSelected){
				this.checkboxSelectAll.prop('checked', true);
			}
			else{
				this.checkboxSelectAll.prop('checked', false);
			}
		},

		onToggleShoppingListToolsDisabledState: function(data){
			var $label = this.$$('.select-all-label');
			if(data.disabled){
				this.checkboxSelectAll.attr("disabled","disabled");
				$label.attr("disabled","disabled");
			}
			else{
				this.checkboxSelectAll.removeAttr("disabled");
				$label.removeAttr("disabled");
			}
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
		}

	});

})(Tc.$);
