(function ($) {

	/**
	* Module implementation.
	*
	* @namespace Tc.Module
	* @class Buying-section
	* @extends Tc.Module
	*/
	Tc.Module.CompareListAttributes = Tc.Module.extend({

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
			this.sandbox.subscribe('compareGrid', this);

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
