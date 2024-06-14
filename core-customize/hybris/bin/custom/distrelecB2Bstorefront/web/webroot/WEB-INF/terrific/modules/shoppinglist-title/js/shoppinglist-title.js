(function ($) {

	/**
	 * Module implementation.
	 *
	 * @namespace Tc.Module
	 * @class ShoppinglistTitle
	 * @extends Tc.Module
	 */
	Tc.Module.ShoppinglistTitle = Tc.Module.extend({

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
			this.sandbox.subscribe('shoppinglist', this);

			this.onShoppingListNameUpdate = $.proxy(this.onShoppingListNameUpdate, this);
		},

		/**
		 * Hook function to do all of your module stuff.
		 *
		 * @method on
		 * @param {Function} callback function
		 * @return void
		 */
		on: function (callback) {

			callback();
		},

		onShoppingListNameUpdate: function (data) {
			this.$$('.shopping-list-name').html(data.listName);
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
