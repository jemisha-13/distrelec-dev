(function($) {

	/**
	 * Campaign Skin Feedback implementation.
	 *
	 */
	Tc.Module.Campaign.Feedback = function (parent) {

		/**
		 * override the appropriate methods from the decorated module (ie. this.get = function()).
		 * the former/original method may be called via parent.<method>()
		 */
		this.on = function (callback) {

			this.sandbox.subscribe('campaign', this);
			this.backenddata = $('#backenddata');
			this.onFeedbackTextTopChange = $.proxy(this, 'onFeedbackTextTopChange');

			parent.on(callback);
		};

		this.onFeedbackTextTopChange = function () {
			this.$ctx.remove();
		};

        $("a[href='#feedbackForm']").click(function(e){
        	e.preventDefault();

        	var headerHeight = parseInt( $('#header .sticky-level-1').css('height') , 10);

            $('html,body').animate({
                     scrollTop: $('#feedbackFormContent').offset().top - (headerHeight + 50 )
            }, 1000);

        });

		if($('.js-empty-banner-hook')) {
			var emptyResultsHtml = sessionStorage.getItem('jsonBlob');
			var formattedHtml = JSON.parse(emptyResultsHtml);

			$('.js-empty-banner-hook').html(formattedHtml);
		}

		if($('.js-carousel-hook')) {
			var productCodes = sessionStorage.getItem('carousel');
			var carouselModule = '<mod:carousel-teaser';
			carouselModule += '\nlayout="product"';
			carouselModule += '\nskin="product skin-carousel-teaser-feedback';
			carouselModule += '\ncarouselData="${' + productCodes + '}"';
			carouselModule += '\ncomponentWidth="fullWidth"';
			carouselModule += '\ntitle=""';
			carouselModule += '\nautoplay="false"';
			carouselModule += '\nautoplayTimeout="0"';
			carouselModule += '\nautoplayDirection="left"';
			carouselModule += '\ndisplayPromotionText="false"';
			carouselModule += '\nmaxNumberToDisplay="0"';
			carouselModule += '\nwtTeaserTrackingId="${fn:toLowerCase(currentCountry.isoCode)}.faf-c-f"';
			carouselModule += '\n/>';

			$('.js-carousel-hook').html(carouselModule);
		}

	};

})(Tc.$);
