(function($) {
	/**
	 * Make the compare pop up sticky when scrolling the list of products
	 * Ans back to be fixed when reaching the end of the list or top filters
	 */
	Tc.Module.Toolsitem.ComparePopupPlp = function(parent) {
		this.on = function(callback) {
			// connect
			this.sandbox.subscribe('plp-content', this);

			var plpFilters = $('.plp-content__filters');
			var newsletterFooter = $('.mod-newslettersubscribe');

			// Identify if this is a non-oci page
			if (plpFilters.length && newsletterFooter.length) {

				var $actionBar = plpFilters.offset().top;
				var $relatedPages = newsletterFooter.offset().top - ($('.mod-related-pages').length ? 1360 : 1135);

				var $window = $(window);

				$window.scroll(function () {

					var currentScroll = $window.scrollTop();

					if (currentScroll > $actionBar && currentScroll < $relatedPages) {
						$('.skin-toolsitem-compare-popup-plp').css({
							position: 'fixed',
						});
					} else {
						$('.skin-toolsitem-compare-popup-plp').css({
							position: 'absolute'
						});
					}

				});
			}

			parent.on(callback);
		};
	};
})(Tc.$);
