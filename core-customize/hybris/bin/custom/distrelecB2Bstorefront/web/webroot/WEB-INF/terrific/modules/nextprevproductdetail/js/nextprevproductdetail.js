(function($) {

	/**
	 * Module implementation.
	 *
	 * @namespace Tc.Module
	 * @class Nextprevproductdetail
	 * @extends Tc.Module
	 */
	Tc.Module.Nextprevproductdetail = Tc.Module.extend({

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

			// Do stuff here
			//...
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


			self.$previous = self.$$('.arrow_prev');
			self.$next = self.$$('.arrow_next');
			

			self.$previous.on('click', function(ev) {
				location.href=self.$$('.btn-prev').data('href');
			});		
			
			// deselect previous selected mandatory column
			self.$next.on('click', function () {
				location.href=self.$$('.btn-next').data('href');
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
			// Do stuff here or remove after method
			//...
		}

	});

})(Tc.$);