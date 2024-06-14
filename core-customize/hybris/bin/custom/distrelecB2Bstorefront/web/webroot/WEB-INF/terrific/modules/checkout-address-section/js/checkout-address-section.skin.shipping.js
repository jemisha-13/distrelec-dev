(function($) {

	/**
	 * CheckoutAddressList Skin PickupStore implementation.
	 *
	 */
	Tc.Module.CheckoutAddressSection.Shipping = function (parent) {

		/**
		 * override the appropriate methods from the decorated module (ie. this.get = function()).
		 * the former/original method may be called via parent.<method>()
		 */
		this.on = function (callback) {

			parent.on(callback);

			this.onShippingAddressChangeClick = $.proxy(this, 'onShippingAddressChangeClick');
			this.onShippingAddressChange = $.proxy(this, 'onShippingAddressChange');
			this.onDeliveryTypeChange = $.proxy(this, 'onDeliveryTypeChange');

			// subscribe to connector channel/s
			this.sandbox.subscribe('deliveryType', this);
			this.sandbox.subscribe('shippingAddress', this);
			this.sandbox.subscribe('openOrder', this);

			this.$ctx.find('.address-list').on('click', '.btn-select', this.onShippingAddressChangeClick);

            if ($('.box-address__edit__list #different').is(':checked')) {
                $('.different-address-holder').removeClass('hidden');

                var selectedAddress = $('.selected-shipping-address .address-id').val();

                $('.skin-address-list-shipping .address-list .box-address-' + selectedAddress + ' input[type=radio]').prop('checked', 'checked');
            }

            if ($('.box-address__edit__list #pickup').is(':checked')) {
                $('.skin-checkout-address-section-pickup').removeClass('hidden');
			}

            $('.box-address__edit__list__item input').click(function() {
                var form =  $('.different-address-holder');
                var pickupForm = $('.skin-checkout-address-section-pickup');

                $(form).addClass('hidden');
                $(pickupForm).addClass('hidden');

                if ($('.box-address__edit__list #billing').is(':checked')) {
                    $(this).parent().find('label, input').attr('disabled', 'disabled');

                    var billingAddressId = $('.continue-checkout .billingAddressId').val();

                    $.ajax({
                        type: 'POST',
                        url: '/checkout/detail/billing',
                        data: {
                            "billingAddressId": billingAddressId
						},
                        success: function (data, textStatus, jqXHR) {
                           window.location = window.location.href;
                        },
                        error: function (jqXHR, textStatus, errorThrown) {
                        	console.log(errorThrown);
                        }
                	});

                }

                if ($('.box-address__edit__list #different').is(':checked')) {
                    $(form).removeClass('hidden');

                    if ($('.skin-address-list-shipping .address-list .box-address').length === 1) {
                        $('.skin-address-list-shipping .address-list .box-address input[type=radio]').trigger('click');
                    }

                }

                if ($('.box-address__edit__list #pickup').is(':checked')) {
                    $(this).parent().find('label, input').attr('disabled', 'disabled');
                    $(pickupForm).removeClass('hidden');

                    var warehouseCode = $('.skin-checkout-address-section-pickup .address-list .address-list__warehouse').val();

                    $.ajax({
                        type: 'POST',
                        url: '/checkout/detail/pickup',
                        data: {
                            "pickup": warehouseCode
						},
                        success: function (data, textStatus, jqXHR) {
                            window.location = window.location.href;
                        },
                        error: function (jqXHR, textStatus, errorThrown) {
                            console.log(errorThrown);
                        }
                    });

                }

            });

            $('.box-address__select').click(function() {
                $(this).parent().find('label, input').attr('disabled', 'disabled');

                var shippingAddressId = $(this).parents('.box-address').children('.address-id').val();

                $.ajax({
                    type: 'POST',
                    url: '/checkout/detail/shipping',
                    data: {
                        "shippingAddressId": shippingAddressId
                    },
                    success: function (data, textStatus, jqXHR) {
                        window.location = window.location.href;
                    },
                    error: function (jqXHR, textStatus, errorThrown) {
                        console.log(errorThrown);
                    }
                });

            });

		};

		//
		// Event is fired after user changed delivery type
		this.onDeliveryTypeChange = function(data){

			if(data.selectedDeliveryType === 1){
				// Slide down if delivery type 1 was selected
				this.$ctx.slideDown();

				// if there is at least one shipping address, which is not equal to billing it gets preselected
				if(this.$ctx.find('.selected-shipping-address').length > 0){
					var preselectedAddressId = this.$ctx.find('.mod-address .box-address').data('address-id');
					// Fire event to replace the previous address in dom and to update Continue Button Form with new id
					this.fire('shippingAddressChange', {
						deliveryType: data.selectedDeliveryType,
						updateSelectedAddress: false,
						address:{
							id: preselectedAddressId
						}
					}, ['shippingAddress']);

					this.shippingAddressChange(preselectedAddressId);
					this.showAddressMessage(false);
				} else {
					this.showAddressMessage(true);
				}
			}
			else{
				this.$ctx.slideUp();
				this.showAddressMessage(false);
			}
		};

		//
		// User selected a different shipping address
		this.onShippingAddressChangeClick = function (ev) {
			ev.preventDefault();
			var shippingAddressId = $(ev.target).data('address-id')
				,customerType = $(ev.target).data('customer-type')
				,newlySelectedAddress = $(ev.target).closest('.mod-address')
				;

			// Fire event to replace the previous address in dom and to update Continue Button Form with new id
			this.fire('shippingAddressChange', {
				deliveryType: '1',
				customerType: customerType,
				updateSelectedAddress: true,
				address:{
					id: shippingAddressId,
					companyName: newlySelectedAddress.find('.company-name').html(),
					companyName2: newlySelectedAddress.find('.company-name-2').html(),
					name: newlySelectedAddress.find('.name').html(),
					phone: newlySelectedAddress.find('.phone').html(),
					street: newlySelectedAddress.find('.street').html(),
					town: newlySelectedAddress.find('.town').html(),
					country: newlySelectedAddress.find('.country').html()
				}
			}, ['shippingAddress']);

			this.shippingAddressChange(shippingAddressId);
		};

		this.shippingAddressChange = function (shippingAddressId){
			$.ajax({
				url: '/checkout/address/shipping',
				type: 'POST',
				dataType: 'json',
				method: 'post',
				data: {
					"shippingAddressId": shippingAddressId
				},
				success: function (data, textStatus, jqXHR) {
				},
				error: function (jqXHR, textStatus, errorThrown) {
				}
			});
		};

		//
		// User changes openOrderType to existing open order, we hide all address sections
		this.onAddOrderToExistingOpenOrderChange = function(data){
			if(data.addedToExistingOpenOrder){
				this.$ctx.slideUp();
			}
		};
		
		// Show or hide "Please add Shipping Address" message		
		this.showAddressMessage = function(b) {
			$('.plz-add-address').toggleClass('hidden',!b);
			$('.btn-checkout').toggleClass('disabled',b);
		};

	};

})(Tc.$);
