(function($) {

	/**
	 * Productlist Order Skin Shopping implementation for the module Productlist Order.
	 *
	 */
	Tc.Module.ProductlistOrder.PlpMobile = function (parent) {

		/**
		 * override the appropriate methods from the decorated module (ie. this.get = function()).
		 * the former/original method may be called via parent.<method>()
		 */
		this.on = function (callback) {

			parent.on(callback);

			// subscribe to connector channel/s
			this.sandbox.subscribe('shoppinglistBulkAction', this);

			$('.orderlist-select-holder__header__close').click(function(){
				$('body').removeClass('mobile-category-filter');
				$('.skin-productlist-order-plp-mobile').removeClass('mobile');
			});

			$('.orderlist-select-holder__content span').click(function(){
				$('.orderlist-select-holder__content span').removeClass('selected');
				$(this).addClass('selected');
			});

			$('.submit-btn').click(function(){

                var selectedValue = $('.orderlist-select-holder__content span.selected').attr('data-order-value').split(':'),
                    urlObj = Tc.Utils.splitUrl(document.URL);

                // Prepare "sort" get param
                if (urlObj.get === undefined) {
                    urlObj.get = {sort: ""};
                } else if (urlObj.get.q === undefined) {
                    urlObj.get.sort = "";
                }

                // Add "sort" and "page" get parameter
                urlObj.get.sort = selectedValue[0] + ':' + selectedValue[1];
                urlObj.get.page = 1; // on sort event, user should be directed to first result-page

                location.href = Tc.Utils.joinUrl(urlObj);

			});

		};


	};

})(Tc.$);
