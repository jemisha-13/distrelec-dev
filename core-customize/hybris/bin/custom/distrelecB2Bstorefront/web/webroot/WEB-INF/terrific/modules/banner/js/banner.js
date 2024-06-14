(function ($) {
    /**
	 * Module implementation.
	 *
	 * @namespace Tc.Module
	 * @extends Tc.Module
	 */
	Tc.Module.Banner = Tc.Module.extend({

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

            this.searchExperience = $('#backenddata .shopsettings').data('search-experience');
            this.fusionEnabled = $('.input-search').data('fusion-active');

            if((typeof this.fusionEnabled !== undefined && this.fusionEnabled !== false) && this.searchExperience !== 'factfinder') {
                this.populateBanner();
            }

		},

        /**
		 * Hook function to do all of your module stuff.
		 *
		 * @method on
		 * @param {Function} callback function
		 * @return void
		 */
		on: function(callback) {
			callback();
		},

        /**
         * Function to build banner html with values saved in session storage
         * 
         * @method populateBanner
         * @return void
         */
        populateBanner: function() {
            var bannerData = JSON.parse(sessionStorage.getItem('banner'));

            if(bannerData) {
                // zone attribute is returned as a string from fusion api so needs to be split to get the attributes into an array
                var bannerContent = bannerData.zone.split(', ');
                var formattedBannerContent = { header: bannerContent[0].split(': ')[0], description: bannerContent[1].split(': ')[0]};
                var bannerHtml = '<div class="card__item__img">';

                bannerHtml += '\n<img data-src=' + bannerData.url + 'alt="' + formattedBannerContent.header + '" width="380" height="89"/>';
                bannerHtml += '\n</div>';
                bannerHtml += '\n<div class="card__item__info">';
                bannerHtml += '\n<h2 class="card__item__title">' + formattedBannerContent.header + '</h2>';
                bannerHtml += '\n<p class="card__item__description">' + formattedBannerContent.description + '</p>';
                bannerHtml += '\n</div>';

                $('.js-banner-hook').html(bannerHtml);
            }
        }

    });
})(Tc.$);