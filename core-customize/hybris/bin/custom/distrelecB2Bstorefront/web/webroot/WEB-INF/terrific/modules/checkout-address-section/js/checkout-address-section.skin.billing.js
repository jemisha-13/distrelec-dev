(function ($) {

	/**
	 * CheckoutAddressList Skin PickupStore implementation.
	 *
	 */
	Tc.Module.CheckoutAddressSection.Billing = function (parent) {

		/**
		 * override the appropriate methods from the decorated module (ie. this.get = function()).
		 * the former/original method may be called via parent.<method>()
		 */
		this.on = function (callback) {

			this.onDeliveryTypeChangeClick = $.proxy(this, 'onDeliveryTypeChangeClick');
			this.onAddOrderToExistingOpenOrderChange = $.proxy(this, 'onAddOrderToExistingOpenOrderChange');

			// subscribe to connector channel/s
			this.sandbox.subscribe('billingAddress', this);
			this.sandbox.subscribe('deliveryType', this);
			this.sandbox.subscribe('openOrder', this);
			
			this.$$('.box-typeselector').on('change', 'input[type=radio]', this.onDeliveryTypeChangeClick);
			
			// initial selection if user comes back to page or does a reload
			var _deliveryType = parseInt(this.$$('.box-typeselector :checked').val(), 10);
			this.fire('deliveryTypeChange',{ selectedDeliveryType: _deliveryType },['deliveryType']);

			parent.on(callback);
		};

		//
		// user actually changed delivery type by click
		this.onDeliveryTypeChangeClick = function (ev) {
			var self = this;
			var deliveryType;
			
			if (ev.target === undefined){
				deliveryType = ev;				
			}
			else{
				deliveryType = parseInt($(ev.target).val());
			}
				

			this.fire('deliveryTypeChange',{ selectedDeliveryType: deliveryType },['deliveryType']);

			// if selecting deliveryType 0, shipping address equals to billing address
			if (deliveryType === 0) {

				var billingAddressId = '';
				if(this.$ctx.find('.mod-address').length > 1){
					// find active billing address if there are multiple billing addresses
					billingAddressId = this.$ctx.find('.mod-address .box-address :checked').val();
				}
				else{
					billingAddressId = this.$ctx.find('.mod-address .box-address').data('address-id');
				}

				this.fire('shippingAddressChange', {
						deliveryType: 0,
						address:{
							id: billingAddressId
						}},
					['billingAddress']
				);

				// Reset shipping address id to billing address id (default)
				$.ajax({
					url: '/checkout/address/shipping',
					type: 'POST',
					dataType: 'json',
					method: 'post',
					data: {
						"shippingAddressId": billingAddressId
					},
					success: function (data, textStatus, jqXHR) {
						self.fire('pickupPlaceselected', { data: data }, ['deliveryType']);	
					},
					error: function (jqXHR, textStatus, errorThrown) {
					}
				});
			}
		};

		//
		// User changes open order type (new / existing), we show / hide the billing section
		this.onAddOrderToExistingOpenOrderChange = function (data) {
			if(data.addedToExistingOpenOrder){
				this.$ctx.slideUp();
			}
			else{
				this.$ctx.slideDown();

				var _deliveryType = parseInt(this.$$('.box-typeselector :checked').val(), 10);
				if(_deliveryType > 0){
					this.fire('deliveryTypeChange', {selectedDeliveryType: _deliveryType},['deliveryType']);
				}
			}
		};
		
		
		this.after = function (ev) {

		};		
		
		
	};

})(Tc.$);
