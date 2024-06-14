(function($) {

	/**
	 * CheckoutAddressList Skin PickupStore implementation.
	 *
	 */
	Tc.Module.CheckoutAddressSection.Pickup = function (parent) {

		/**
		 * override the appropriate methods from the decorated module (ie. this.get = function()).
		 * the former/original method may be called via parent.<method>()
		 */
		this.on = function (callback) {

			parent.on(callback);

			this.onDeliveryTypeChange = $.proxy(this, 'onDeliveryTypeChange');
			this.onPickupStoreChangeClick = $.proxy(this, 'onPickupStoreChangeClick');
			this.onMobilePhoneChangeClick = $.proxy(this, 'onMobilePhoneChangeClick');

			// subscribe to connector channel/s
			this.sandbox.subscribe('deliveryType', this);
			this.sandbox.subscribe('pickupStore', this);
			this.sandbox.subscribe('openOrder', this);

			if(this.$ctx.find('.mod-address').length > 1) {
				this.$$('.address-list').on('change', ':radio', this.onPickupStoreChangeClick);
			}
			
			this.$$('.mobile-number-save').on('click', this.onMobilePhoneChangeClick);
		};

		// Event is fired after user changed delivery type
		this.onDeliveryTypeChange = function(data){

			if (data.selectedDeliveryType === 2) {

				if (this.$ctx.find('.mod-address').length === 1) {
					var warehouseCode = this.$ctx.find('.mod-address .box-address').data('warehouse-code');
					this.pickupStoreChange(warehouseCode);
				}

				if ($("input[name='pickup']").is(':checked')) {
					var radioChecked = $('input[name=pickup]:checked').val();
					this.pickupStoreChange(radioChecked); 					
				}

			} else {
				this.$ctx.slideUp();
			}

		};

		// User selected a pickup location
		this.onPickupStoreChangeClick = function (ev) {
			var warehouseCode = $(ev.target).val();
			this.pickupStoreChange(warehouseCode);
		};

		this.pickupStoreChange = function(warehouseCode){
			
			var self = this;
			
			// Update Continue Button Form with new id
			this.fire('pickupStoreChange', { deliveryType: '2', pickupLocationCode: warehouseCode }, ['pickupStore']);

			$.ajax({
				url: '/checkout/address/pickup',
				type: 'POST',
				dataType: 'json',
				method: 'post',
				data: {
					"pickup": warehouseCode
				},
				success: function (data, textStatus, jqXHR) {
					self.fire('pickupPlaceselected', { data: data }, ['deliveryType']);
				},
				error: function (jqXHR, textStatus, errorThrown) {
					console.log(errorThrown);
				}
			});
		};

		// User changes openOrderType to existing open order, we hide all address sections
		this.onAddOrderToExistingOpenOrderChange = function(data){
			if (data.addedToExistingOpenOrder) {
				this.$ctx.slideUp();
			}
		};

		// User changed mobile number
        this.onMobilePhoneChangeClick = function (ev) {
            ev.preventDefault();

            $('.mobile-number__info--ok').addClass('hidden');
            $('.mobile-number__info--warning').addClass('hidden');
            $('.mobile-number__info--error').addClass('hidden');

            var error = false;
            var mobileNumber = this.$$('.mobile-number__input').val();

            if (!/^[+()\d-]+$/.test(mobileNumber)) {
                error = true;
                $('.mobile-number__input').addClass('error');
                $('.mobile-number__info--warning').removeClass('hidden');
            }

            if (mobileNumber.length < 9) {
                error = true;
                $('.mobile-number__input').addClass('error');
                $('.mobile-number__info--warning').removeClass('hidden');
            }

            if (error === false) {
                $('.mobile-number__input').removeClass('error');
                $('.mobile-number__info--warning').addClass('hidden');
                this.mobilePhoneChange(mobileNumber);
            }

        };


        this.mobilePhoneChange = function(mobileNumber) {
			var self = this;
			$.ajax({
				url: '/checkout/detail/updateContact',
				type: 'POST',
				dataType: 'json',
				method: 'post',
				data: {
					'mobileNumber': mobileNumber
				},
				success: function (data) {
					if (data.action.status === 'true') {
						self.$$('.mobile-number__info--ok').removeClass('hidden');
					} else {
						self.$$('.mobile-number__info--error').removeClass('hidden');
					}
				},
				error: function (jqXHR, textStatus, errorThrown) {
					console.log(errorThrown);
					self.$$('.mobile-number__info--error').removeClass('hidden');
				}
			});
		};
		
	};

})(Tc.$);
