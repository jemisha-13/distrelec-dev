(function($) {

	/**
	 * Module implementation.
	 *
	 * @namespace Tc.Module
	 * @class Address
	 * @extends Tc.Module
	 */
	Tc.Module.Address = Tc.Module.extend({

		/**
		 * Initialize.
		 *
		 * @method init
		 * @return {void}
		 * @constructor
		 * @param {jQuery} $ctx the jquery context
		 * @param {Sandbox} sandbox the sandbox to get the resources from
		 * @param {Number} id the unique module id
		 */
		init: function($ctx, sandbox, id) {
			// call base constructor
			this._super($ctx, sandbox, id);

		},

		/**
		 * Hook function to do all of your module stuff.
		 *
		 * @method on
		 * @param {Function} callback function
		 * @return void
		 */
		on: function(callback) {

			callback();
			
			// subscribe to channel(s)
			this.sandbox.subscribe('lightboxYesNo', this);
			this.sandbox.subscribe('billingAddress', this);
			this.sandbox.subscribe('shippingAddress', this);


			var self= this;
			
			this.$changeBillingAddressButton = this.$$('.change-billing-address-button');
			this.$changeBillingAddressButton.click( function(ev) {
				ev.preventDefault();
				self.loadMoreAddresses(ev, 'billing');
			});			

			this.$changeShippingAddressButton = this.$$('.change-shipping-address-button');
			this.$changeShippingAddressButton.click( function(ev) {
				ev.preventDefault();
				self.loadMoreAddresses(ev, 'shipping');
			});	
			
			
			this.loadClickListeners();
			
			
			// Button set as default 
			this.$btnSetAsDefault = this.$$('.btn-set-default-checkout');
			
			this.$btnSetAsDefault.click( function(ev) {
				
				ev.preventDefault();
				
				var $link = $(ev.target).closest('a')
					, lightboxTitle = $link.data('lightbox-title')
					, lightboxMessage = $link.data('lightbox-message')
					, lightboxBtnDeny = $link.data('lightbox-btn-deny')
					, lightboxShowBtnConfirm = $link.data('lightbox-show-confirm-button')
					, lightboxBtnConf = $link.data('lightbox-btn-conf')
					, lightboxAddressId = $link.data('address-id')
					, isShipping = $link.data('is-shipping')
					, isBilling = $link.data('is-billing')
					, addressType = $link.data('address-type')
				;
				
				
				if (addressType === 'shipping'){
					isShipping = true;
					isBilling = false;
				}
				else{
					isShipping = false;
					isBilling = true;					
				}				
				
				self.fire(
					'yesNoAction',
					{
						actionIdentifier: self.actionIdentifier,
						attribute: lightboxAddressId + ',' + isBilling + ',' + isShipping ,
						addressId: lightboxAddressId,
						isShipping: isShipping,
						isBilling: isBilling,
						addressType: addressType,
						lightboxTitle: lightboxTitle,
						lightboxMessage: lightboxMessage,
						lightboxBtnDeny: lightboxBtnDeny,
						lightboxShowBtnConfirm: lightboxShowBtnConfirm,
						lightboxBtnConf: lightboxBtnConf
					},
					['lightboxYesNo']
				);
				return false;
			});

			if ($('.skin-checkout-address-section-billing .box-address__form__field .form_field_error:not(.js-is-FE)').length > 0) {
                $('.box-address__form').removeClass('hidden');
                $('.form .mod-global-messages').removeClass('hidden');
                $('.address__edit__link').addClass('hidden');
			}

			$('.address__edit__link').click(function(e) {
				e.preventDefault();

                $(this).parent().next('form').removeClass('hidden');
				$('.form .mod-global-messages').removeClass('hidden');
				$(this).addClass('hidden');
			});

			$('.skin-checkout-address-section-billing .box-address__form__submit__cancel').click(function(e) {
				e.preventDefault();

				$('.box-address__form').addClass('hidden');
                $('.form .mod-global-messages').addClass('hidden');
                $('.address__edit__link').removeClass('hidden');
			});

			var $billingForm = self.$$('.js-billing-form');

			if ($billingForm.length) {
				var $phoneField = $('.js-libphonenumber', $billingForm);
				var $countryField = $('.js-libphonenumber-isocode-select', $billingForm);

				$phoneField.on('focusout', function () {
					var $selfPhoneField = $(this);

					Tc.Utils.validateGoogleLibphonenumber($selfPhoneField);
				});

				$countryField.on('change', function () {
					$phoneField.each(function () {
						var $currentPhoneField = $(this);

						Tc.Utils.validateGoogleLibphonenumber($currentPhoneField);
					});
				});
			}
		},
		
		
		loadMoreAddresses: function(ev, type){
			var self = this;
			
			if (type === 'billing'){
				$.ajax({
					url: '/checkout/address/load-addresses?type=billing',
					type: 'GET',
					dataType: 'html',
					success: function(data, textStatus, jqXHR) {
						$('.load-more-billing-addresses-container').html(data);
						$('.load-more-billing-addresses-container').stop().slideToggle();

						self.loadClickListeners();
						 
					},
					error: function(jqXHR, textStatus, errorThrown) {
					}
				});				
			}
			else{
				$.ajax({
					url: '/checkout/address/load-addresses?type=shipping',
					type: 'GET',
					dataType: 'html',
					success: function(data, textStatus, jqXHR) {
						$('.load-more-shipping-addresses-container').html(data);
						$('.load-more-shipping-addresses-container').stop().slideToggle();
						
						self.loadClickListeners();
					},
					error: function(jqXHR, textStatus, errorThrown) {
					}
				});					
			}
		},
		
		
		// Sets the click listener to the new buttons created after the ajax call
		loadClickListeners: function(ev){
			
			var self = this;
			
			
			/* SET DEFAULT BUTTONS */ 
			this.$btnSetAsDefault = this.$$('.btn-set-default-checkout');
			
			this.$btnSetAsDefault.click( function(ev) {
				
				ev.preventDefault();
				
				var $link = $(ev.target).closest('a')
					, lightboxTitle = $link.data('lightbox-title')
					, lightboxMessage = $link.data('lightbox-message')
					, lightboxBtnDeny = $link.data('lightbox-btn-deny')
					, lightboxShowBtnConfirm = $link.data('lightbox-show-confirm-button')
					, lightboxBtnConf = $link.data('lightbox-btn-conf')
					, lightboxAddressId = $link.data('address-id')
					, isShipping = $link.data('is-shipping')
					, isBilling = $link.data('is-billing')
					, addressType = $link.data('address-type')
				;
				self.fire(
					'yesNoAction',
					{
						actionIdentifier: self.actionIdentifier,
						attribute: lightboxAddressId + ',' + isBilling + ',' + isShipping + ',' + addressType,
						addressId: lightboxAddressId,
						isShipping: isShipping,
						isBilling: isBilling,
						addressType : addressType,
						lightboxTitle: lightboxTitle,
						lightboxMessage: lightboxMessage,
						lightboxBtnDeny: lightboxBtnDeny,
						lightboxShowBtnConfirm: lightboxShowBtnConfirm,
						lightboxBtnConf: lightboxBtnConf
					},
					['lightboxYesNo']
				);
				return false;
			});	
			
			
			/* SELECT ADDRESS BUTTONS */
			this.$selectShippingAddressButton = this.$$('.select-shipping-address-button');
			this.$selectShippingAddressButton.click( function(ev) {
				ev.preventDefault();
				self.selectAddress(ev, 'shipping');
			});			
			
			this.$selectBillingAddressButton = this.$$('.select-billing-address-button');
			this.$selectBillingAddressButton.click( function(ev) {
				ev.preventDefault();
				self.selectAddress(ev, 'billing');
			});					
			
			
		},
		
		

		
		
		selectAddress: function(ev, type){
			var self = this;
			
			ev.preventDefault();
			var addressId = $(ev.target).data('address-id')
				,customerType = $(ev.target).data('customer-type')
				,newlySelectedAddress = $(ev.target).closest('.mod-address')
				;
			
			
			if (type === 'billing'){
				// Fire event to replace the previous address in dom and to update Continue Button Form with new id
				
				
				this.fire('billingAddressChangeCheckout', {
					deliveryType: '1',
					customerType: customerType,
					updateSelectedAddress: true,
					address:{
						id: addressId,
						companyName: newlySelectedAddress.find('.company-name').html(),
						companyName2: newlySelectedAddress.find('.company-name-2').html(),
						name: newlySelectedAddress.find('.name').html(),
						phone: newlySelectedAddress.find('.phone').html(),
						street: newlySelectedAddress.find('.street').html(),
						town: newlySelectedAddress.find('.town').html(),
						country: newlySelectedAddress.find('.country').html()
					}
				}, ['billingAddress']);				
				
				
				$.ajax({
					url: '/checkout/address/billing',
					type: 'POST',
					dataType: 'json',
					method: 'post',
					data: {
						"billingAddressId": addressId
					},
					success: function (data, textStatus, jqXHR) {
					},
					error: function (jqXHR, textStatus, errorThrown) {
					}
				});				
			}
			else{

				// Fire event to replace the previous address in dom and to update Continue Button Form with new id
				this.fire('shippingAddressChange', {
					deliveryType: '1',
					customerType: customerType,
					updateSelectedAddress: true,
					address:{
						id: addressId,
						companyName: newlySelectedAddress.find('.company-name').html(),
						companyName2: newlySelectedAddress.find('.company-name-2').html(),
						additionalAddress: newlySelectedAddress.find('.additional-address').html(),
						name: newlySelectedAddress.find('.name').html(),
						phone: newlySelectedAddress.find('.phone').html(),
						street: newlySelectedAddress.find('.street').html(),
						town: newlySelectedAddress.find('.town').html(),
						country: newlySelectedAddress.find('.country').html()
					}
				}, ['shippingAddress']);				
				
				
				
				
				$.ajax({
					url: '/checkout/detail/shipping',
					type: 'POST',
					dataType: 'json',
					method: 'post',
					data: {
						"shippingAddressId": addressId
					},
					success: function (data, textStatus, jqXHR) {
					},
					error: function (jqXHR, textStatus, errorThrown) {
					}
				});				
			}
		},
		

		onDialogConfirm: function(ev) {
			var self = this;
			
			var splitted = ev.attribute.split(',');
			var addressId = splitted[0];
			var isBilling = splitted[1];
			var isShipping = splitted[2];
			var addressType = splitted[3];
			
			var typeOfAddressEvent; 
			var typeOfAddressId;
			
			var newlySelectedAddress;
			
			var $splitted = splitted;
			
			//DISTRELEC-11395
			if (addressType === 'shipping'){
				isShipping = true;
				isBilling = false;
			}
			else{
				isShipping = false;
				isBilling = true;					
			}
			
			
			$.ajax({
				url: '/my-account/set-default-address?addressCode=' + addressId + '&billing=' + isBilling + '&shipping=' + isShipping,
				type: 'POST',
				success: function(data, textStatus, jqXHR) {
					
					// if billing & shipping
					if ($splitted[1] === 'true' && $splitted[2] === 'true'){
						
						if (splitted[3] === 'shipping'){
							$('.setAsDefaultShipping').removeClass('hidden');
							$('.setAsDefaultShipping-'+$splitted[0]).addClass('hidden');
							
							typeOfAddressEvent = 'ShippingAddressChange';
							typeOfAddressId = 'shippingAddress';
							
							newlySelectedAddress = $('.setAsDefaultShipping-'+splitted[0]).closest('.mod-address');
						
						}
						else{
							$('.setAsDefaultBilling').removeClass('hidden');
							$('.setAsDefaultBilling-'+$splitted[0]).addClass('hidden');							

							typeOfAddressEvent = 'billingAddressChangeCheckout';
							typeOfAddressId = 'billingAddress';
							
							newlySelectedAddress = $('.setAsDefaultBilling-'+splitted[0]).closest('.mod-address');
						}

						
					}
					//if billing
					else if ($splitted[1] === 'true' && $splitted[2] === 'false'){
						$('.setAsDefaultBilling').removeClass('hidden');
						$('.setAsDefaultBilling-'+$splitted[0]).addClass('hidden');
						
						typeOfAddressEvent = 'billingAddressChangeCheckout';
						typeOfAddressId = 'billingAddress';	
						
						newlySelectedAddress = $('.setAsDefaultBilling-'+splitted[0]).closest('.mod-address');
						
					}
					//if shipping
					else if ($splitted[2] === 'true' && $splitted[1] === 'false'){
						$('.setAsDefaultShipping').removeClass('hidden');
						$('.setAsDefaultShipping-'+$splitted[0]).addClass('hidden');
						
						typeOfAddressEvent = 'ShippingAddressChange';
						typeOfAddressId = 'shippingAddress';
						
						newlySelectedAddress = $('.setAsDefaultShipping-'+splitted[0]).closest('.mod-address');
					}
					
					
					
					
					
					// 
					self.fire(typeOfAddressEvent, {
						deliveryType: '1',
						customerType: 'b2b',
						updateSelectedAddress: true,
						address:{
							id: addressId,
							companyName: newlySelectedAddress.find('.company-name').html(),
							companyName2: newlySelectedAddress.find('.company-name-2').html(),
							name: newlySelectedAddress.find('.name').html(),
							phone: newlySelectedAddress.find('.phone').html(),
							street: newlySelectedAddress.find('.street').html(),
							town: newlySelectedAddress.find('.town').html(),
							country: newlySelectedAddress.find('.country').html()
						}
					}, [typeOfAddressId]);		


					
				},
				error: function(jqXHR, textStatus, errorThrown) {
				}
			});				
			
		},		
		

		/**
		 * Hook function to trigger your events.
		 *
		 * @method after
		 * @return void
		 */
		after: function() {
			
		}

	});

})(Tc.$);
