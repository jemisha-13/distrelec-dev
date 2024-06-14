(function ($) {

	/**
	 * CheckoutAddressList Skin PickupStore implementation.
	 *
	 */
	Tc.Module.CheckoutAddressSection.BillingMultiple = function (parent) {

		/**
		 * override the appropriate methods from the decorated module (ie. this.get = function()).
		 * the former/original method may be called via parent.<method>()
		 */
		this.on = function (callback) {

			this.onBillingAddressChangeClick = $.proxy(this, 'onBillingAddressChangeClick');

			// subscribe to connector channel/s
			this.sandbox.subscribe('billingAddress', this);
			this.sandbox.subscribe('deliveryType', this);
			this.sandbox.subscribe('openOrder', this);
			this.sandbox.subscribe('shippingAddress', this);

			this.$$('.address-list').on('click', ':radio', this.onBillingAddressChangeClick);

            $('.box-address__edit__list__item input').click(function() {
                $('.different-address-holder').removeClass('hidden');
			});

            $('.default-link').click(function(e) {
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

			parent.on(callback);

		};

		//
		// User selected a different billing address
		this.onBillingAddressChangeClick = function (ev) {
			var billingAddressId = $(ev.target).val();
			var $boxTypeSelector = this.$$('.box-typeselector');
			var deliveryType = $boxTypeSelector.val();
			var selAddrIsBillingAndShipping = $(ev.target).closest('.box-address').data('is-billing-and-shipping');

			if(selAddrIsBillingAndShipping === false){
				// hide option "ship to same address" because selected address is not also a shipping address
				$boxTypeSelector.find('.delivery-type-0').addClass('hidden');

				// select deliveryType 1 instead
				$boxTypeSelector.find('.delivery-type-1 input[type=radio]').prop("checked", true);
				this.fire('deliveryTypeChange',{ selectedDeliveryType: 1 },['deliveryType']);
			}
			else{
				$boxTypeSelector.find('.delivery-type-0').removeClass('hidden');
			}

			// show shipping type radio buttons, which are initially hidden
			$boxTypeSelector.slideDown();

			// Update Continue Button Form with new id
			this.fire('billingAddressChange', { deliveryType: deliveryType, billingAddressId: billingAddressId, selAddrIsBillingAndShipping: selAddrIsBillingAndShipping}, ['billingAddress']);

			$.ajax({
				url: '/checkout/address/billing',
				type: 'POST',
				dataType: 'json',
				method: 'post',
				data: {
					"billingAddressId": billingAddressId
				},
				success: function (data, textStatus, jqXHR) {
				},
				error: function (jqXHR, textStatus, errorThrown) {
				}
			});
		};
	};

})(Tc.$);
