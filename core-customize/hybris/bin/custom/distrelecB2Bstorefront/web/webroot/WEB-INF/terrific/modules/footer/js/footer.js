(function ($) {

	/**
	 * Footer implementation.
	 *
	 * @namespace Tc.Module
	 * @class Facets
	 * @extends Tc.Module
	 */
	Tc.Module.Footer = Tc.Module.extend({

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

            setTimeout(function(){
                $( "#___ratingbadge_0" ).remove();
            }, 5000);
		},

		/**
		 * Hook function to do all of your module stuff.
		 *
		 * @method on
		 * @param {Function} callback function
		 * @return void
		 */
		  on: function (callback) {
				var $leftBanner = $('.home-left-banner');
				var $footer = $('.mod-footer');

				if ($leftBanner.children('.img-ct').length > 0 && $footer.length > 0) {	// Make sure both banner and footer is present on page

					var footerOffset = $footer.offset().top;
					var bottomDiv = $leftBanner.offset().top + $leftBanner[0].offsetHeight;

					if (bottomDiv > footerOffset){
						$footer.css('margin-top', bottomDiv - footerOffset );
					}

				}

				var productContainer = $('.gu-70'),
					facetContainer = $('.mod-facets'),
					containerHeight;

				if (productContainer.height() > facetContainer.height() ) {
					containerHeight = productContainer.height() + 40;
				}
				else {
					containerHeight = facetContainer.height();
				}

				if ( productContainer.length > 0 && productContainer.height() > 1191) {
					$(productContainer).parent().css('height', containerHeight);
				}

				callback();

			  }
	});

})(Tc.$);