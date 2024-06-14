(function($) {

	/**
	 * Module implementation.
	 *
	 * @namespace Tc.Module
	 * @class Servicenav
	 * @extends Tc.Module
	 */
	Tc.Module.Seo = Tc.Module.extend({

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
		},

		/**
		 * Hook function to do all of your module stuff.
		 *
		 * @method on
		 * @param {Function} callback function
		 * @return void
		 */
		on: function(callback) {

			var self = this,
				$ctx = this.$ctx;

			$ctx.on('click', '.categoryseo__show-more', function(e) {
				var $currentEle = $(this).parents('.categoryseo').find('.categoryseo__content'),
					$seoDescription = $(this).parents('.categoryseo').find('.categoryseo__description'),
					$chevronDown = $('.fa-chevron-down'),
					$chevronUp = $('.fa-chevron-up');


				if ( $currentEle.hasClass('hidden') ) {
                    $currentEle.removeClass('hidden');
					$seoDescription.removeClass('lessText');
					$chevronDown.addClass('hidden');
					$chevronUp.removeClass('hidden');
				} else {
                    $currentEle.addClass('hidden');
					$seoDescription.addClass('lessText');
					$chevronDown.removeClass('hidden');
					$chevronUp.addClass('hidden');
				}
			});
			$('.page-title__back-navigation').click(function(e){
				e.preventDefault();
				window.history.back();
			});

			callback();
		},

		after: function() {


		}


	});

})(Tc.$);
