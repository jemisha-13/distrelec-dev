(function ($) {

	// - Dev Snippet -
	// 	 Trigger "lists change" via Console:
	//   $(document).trigger('listsChange', { type: 'favorite', addedQuantity: '3010006', quantity: 1});

	/**
	 * This module implements the list functionality.
	 *
	 * @namespace Tc.Module
	 * @class MetahdLists
	 * @extends Tc.Module
	 */
	Tc.Module.MetahdItem = Tc.Module.extend({


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

			callback();
		}

	});

})(Tc.$);
