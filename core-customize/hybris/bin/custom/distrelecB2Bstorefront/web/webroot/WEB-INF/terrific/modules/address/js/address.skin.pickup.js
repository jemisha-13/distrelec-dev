(function($) {

	/**
	 * CheckoutAddressList Skin PickupStore implementation.
	 *
	 */
	Tc.Module.Address.Pickup = function (parent) {

		/**
		 * override the appropriate methods from the decorated module (ie. this.get = function()).
		 * the former/original method may be called via parent.<method>()
		 */
		this.on = function (callback) {

			parent.on(callback);

			var pickupSection = $('.box-address__edit__list__item:nth-of-type(3) input[type=radio]');

			if ( pickupSection.is(':checked') ) {
                $('.skin-checkout-address-section-pickup').removeClass('hidden');
			}

		};
	};

})(Tc.$);
