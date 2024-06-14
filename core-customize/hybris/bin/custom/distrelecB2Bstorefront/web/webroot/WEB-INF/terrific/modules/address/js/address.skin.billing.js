(function($) {

	/**
	 * CheckoutAddressList Skin PickupStore implementation.
	 *
	 */
	Tc.Module.Address.Billing = function (parent) {

		/**
		 * override the appropriate methods from the decorated module (ie. this.get = function()).
		 * the former/original method may be called via parent.<method>()
		 */
		this.on = function (callback) {

            var $ctx = this.$ctx;
            var	self = this;

            this.onShippingAddressChange = $.proxy(this, 'onBillingAddressChangeCheckout');

            // subscribe to connector channel/s
            this.sandbox.subscribe('billingAddress', this);

            if($('span').hasClass('form_field_error')){

                $('.box-address__form').removeClass('hidden');
                $('.form_field_error input').val('');

            }


            $('.box-address .box-address__input input[type=radio]').click(function() {

                var billingAddressId = $(this).val();

                $.ajax({
                    url: '/checkout/detail/billing',
                    type: 'POST',
                    dataType: 'json',
                    method: 'post',
                    data: {
                        "billingAddressId": billingAddressId
                    },
                    success: function () {
                        window.location = window.location.href;
                    }

                });

            });


            $('.box-address__form button').click(function(e) {
                e.preventDefault();

                // Format Swedish, Czech and Slovak postal code before form submit (xxx xx)
                if (digitalData.page.pageInfo.countryCode === 'SE' || digitalData.page.pageInfo.countryCode === 'CZ' || digitalData.page.pageInfo.countryCode === 'SK') {
                    var postcode = $('.box-address__form #postalCode').val();

                    if (postcode.length === 5) {
                        $('.box-address__form #postalCode').val(postcode.substring(0, 3) + " " + postcode.substring(3));
                    }

                }

                $(this).closest('form').submit();

            });

			parent.on(callback);

		};

	};

})(Tc.$);
