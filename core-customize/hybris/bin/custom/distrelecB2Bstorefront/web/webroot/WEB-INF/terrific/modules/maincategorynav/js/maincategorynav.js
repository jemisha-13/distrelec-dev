(function ($) {

	/**
	 * Module implementation.
	 *
	 * @namespace Tc.Module
	 * @class Mainnav
	 * @extends Tc.Module
	 */
	Tc.Module.Maincategorynav = Tc.Module.extend({

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

			var $ctx = this.$ctx,
				self = this;

			
			// hack for IE8. Otherwise it shows weird checkboxes
			$ctx.find("input[type='checkbox']").css('display', 'none');
			
			$('a.no-click').on("click", function (e) {
		        e.preventDefault();
		    });

			$('.nav-hover .level2-toggle').click(function(){
                $('.nav-hover .level2-toggle').removeClass('active');
                $('.nav-hover .content-l1').removeClass('active');
				$(this).toggleClass('active');
				$(this).parents('.level_1').next('.content-l1').toggleClass('active');
			});

			self.positionL2categories();
			
			
			callback();
		},
		
		
		
		
		/**
		 * Hook function to do all of your module stuff.
		 *
		 */ 		
		positionL2categories: function(){
			
		},
		
		
		
		/**
		 * Hook function to trigger your events.
		 *
		 * @method after
		 * @return void
		 */
		after: function() {
		}		
		
		
		
	});

})(Tc.$);
