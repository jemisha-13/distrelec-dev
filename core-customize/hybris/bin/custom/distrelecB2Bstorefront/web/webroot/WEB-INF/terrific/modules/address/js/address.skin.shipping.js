(function($) {

	/**
	 * CheckoutAddressList Skin PickupStore implementation.
	 *
	 */
	Tc.Module.Address.Shipping = function (parent) {

		/**
		 * override the appropriate methods from the decorated module (ie. this.get = function()).
		 * the former/original method may be called via parent.<method>()
		 */
		this.on = function (callback) {

			parent.on(callback);

			this.onShippingAddressChange = $.proxy(this, 'onShippingAddressChange');

			// subscribe to connector channel/s
			this.sandbox.subscribe('shippingAddress', this);


			$('.box-address__remove__links .default-link').click(function(e) {
				e.preventDefault();

				var setDefaultUrl = $(this).data('action');

                $.ajax({
                    type: 'POST',
                    url: setDefaultUrl
                })

				.done(function(data) {
					window.location.reload();
				})

				.fail(function(data) {
					console.log(data);
				});

			});

            $('.box-address__remove__links .remove-link').click(function(e) {
                e.preventDefault();

                var addressId = $(this).parents('.box-address').children('.address-id').val();

                $.ajax({
                    type: 'GET',
                    url: '/checkout/detail/remove-address/' + addressId,
                    success: function () {
                        $(this).parents('li').remove();
                        window.location = window.location.href;
                    },
                    error: function (errorThrown) {
                        console.log(errorThrown);
                    }
                });

            });


            $(".mod-lightbox-checkout-shipping #postalCode, .mod-lightbox-checkout-shipping-edit #postalCode").blur(function() {
                var ele = $(this);
                checkPostcode(ele);

            });

            $('.mod-lightbox-checkout-shipping #postalCode, .mod-lightbox-checkout-shipping-edit #postalCode').on("keydown", function(e) {
                var ele = $(this);

                if (e.keyCode == 13) {
                    var inputs = $(this).closest('form').find(':focusable');
                    inputs.eq(inputs.index(this) + 1).focus();

                    return false;
                }

            });

            function checkPostcode(_ele) {

                if (digitalData.page.pageInfo.countryCode === 'SE' || digitalData.page.pageInfo.countryCode === 'CZ' || digitalData.page.pageInfo.countryCode === 'SK' ) {
                    var postcode = _ele.val();

                    if (postcode.length === 5) {
                        _ele.val(postcode.substring(0, 3) + " " + postcode.substring(3));
                    }

                }

            }

		};

	};

})(Tc.$);
