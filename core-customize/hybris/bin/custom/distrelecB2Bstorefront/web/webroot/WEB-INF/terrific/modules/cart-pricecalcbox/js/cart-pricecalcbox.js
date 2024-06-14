(function($) {

	/**
	 * Module implementation.
	 *
	 * @namespace Tc.Module
	 * @class Cart-pricecalcbox
	 * @extends Tc.Module
	 */
	Tc.Module.CartPricecalcbox = Tc.Module.extend({

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

            // subscribe to connector channel/s
            this.sandbox.subscribe('cart', this);
			this.sandbox.subscribe('lightboxYesNo', this);
			this.sandbox.subscribe('deliveryType', this);

			this.openOrderBaseUrl = "/checkout/openorder";
			this.openOrderSetDateUrl = "/setCloseDate?closeDate=";
			this.openOrderSetDateUrlMyAccount = "/changeOpenOrderInERP?code=";

			// init elements
			this.$termsBox = this.$$('#terms-and-conditions');
			this.$checkoutBtn = this.$$('.btn-checkout');
			this.$checkoutBtnFinal = this.$$('.btn-checkout-final');
			this.$approvalBtn = $('.mod-checkout-order-budget-approval-bar .btn-submit');
			this.$agreeCheckbox = this.$$('input#agree');
			this.$reevoCheckbox = this.$$('.js-reevo-checkbox');
			this.actionIdentifier = '/checkout/review/continueCheckout';

			this.orderReferenceAlreadyNoticed = false;
			this.reevoConsent = false;
		},

		/**
		 * Hook function to do all of your module stuff.
		 *
		 * @method on
		 * @param {Function} callback function
		 * @return void
		 */
		on: function(callback) {
			var $ctx = this.$ctx,
				self = this;

			this.$$('.btn-open-order-close').on('click', function(ev) {
				ev.preventDefault();

				var $link = $(ev.target).closest('a')
				, lightboxTitle = $link.data('lightbox-title')
				, lightboxMessage = $link.data('lightbox-message')
				, lightboxBtnDeny = $link.data('lightbox-btn-deny')
				, lightboxShowBtnConfirm = $link.data('lightbox-show-confirm-button')
				, lightboxBtnConf = $link.data('lightbox-btn-conf')
				, orderCode = $link.data('order-id')
				, actionUrl = $link.data('action-url')
				, source = $link.data('source')
				;

				this.lightboxTitle = lightboxTitle;

				var closeButton = $(ev.target)
				,openOrderCloseDateToday = closeButton.data('close-date-today')
				;

				self.fire(
					'yesNoAction',
					{
						actionIdentifier: actionUrl,
						attribute: {openOrderCloseDateToday: openOrderCloseDateToday,
									orderCode: orderCode,
									source : source},
						lightboxTitle: lightboxTitle,
						lightboxMessage: lightboxMessage,
						lightboxShowBtnConfirm: lightboxShowBtnConfirm,
						lightboxBtnDeny: lightboxBtnDeny,
						lightboxBtnConf: lightboxBtnConf
					},
					['lightboxYesNo']
				);

			});


			this.$checkoutBtnFinal.on('click', function(ev) {

				var $verifyAddressCheckbox = $('.verify-address-checkbox');

				var $verifyCostCenterValue = $('#costcenter');
				var $verifyOrderReferenceValue = $('#projectnumber');

				if ($('.mod-verify-address').length > 0){
					if(!$verifyAddressCheckbox.is(":checked")){
						ev.preventDefault();
						$('.field-required').css('visibility', 'visible');
						$('html, body').animate({
					        scrollTop: $('.field-required').offset().top - 400
					    }, 200);
					}
					else{
						$('.field-required').css('visibility', 'hidden');
					}
				}


				if ($('.mod-checkout-order-summary-cost-center-box').length > 0){
					if ( ( $verifyCostCenterValue.val() === ''  || $verifyOrderReferenceValue.val() === '' ) &&  !this.orderReferenceAlreadyNoticed ){
						ev.preventDefault();
						this.orderReferenceAlreadyNoticed = true;
						$('.reference-required').css('display', 'block');

						if ($('.field-required').css('display') != 'block'){
							$('html, body').animate({
						        scrollTop: $('.reference-required').offset().top - 400
						    }, 200);
						}

					}
				}

			});

			if ($('.catalog-quantity-error').length) {
				$('.btn-checkout').on('click', function (ev) {
					ev.preventDefault();
					$('.catalog-quantity-error').addClass('hi-viz');
				});
			}

			this.$approvalBtn.on('click', function(ev) {
				var $verifyCostCenterValue = $('#costcenter');
				var $verifyOrderReferenceValue = $('#projectnumber');
				if ($('.mod-checkout-order-summary-cost-center-box').length > 0){
					if ( ( $verifyCostCenterValue.val() === ''  || $verifyOrderReferenceValue.val() === '' ) &&  !this.orderReferenceAlreadyNoticed ){
						ev.preventDefault();
						this.orderReferenceAlreadyNoticed = true;
						$('.reference-required').css('visibility', 'visible');
						if ($('.field-required').css('visibility') != 'visible'){
							$('html, body').animate({
						        scrollTop: $('.reference-required').offset().top - 400
						    }, 200);
						}
					}
				}

			});

            // DISTRELEC-25977 - added reevo-checkbox monitoring to set reevoConsent.
			this.$reevoCheckbox.on('click', function() {
			    self.reevoConsent = this.checked;

			    // send request to reevoeligible endpoint to set the reevoEligible status
                $.ajax({
                    url: '/cart/reevooeligible',
                    type: 'post',
                    data: {
                        reevooEligible: self.reevoConsent
                    },
                    method: 'post',
                    success: function (data, textStatus, jqXHR) {
                        return data;
                    },
                    error: function (jqXHR, textStatus, errorThrown) {
                        return errorThrown;
                    }
                });
			});

			callback();
		},

		/**
		 * Hook function to trigger your events.
		 *
		 * @method after
		 * @return void
		 */
		after: function() {
		},


        /**
         *
         * @method onWriteCalcBoxData
         *
         * fire method to set data recalculate data
         *
         * @param data
         */
        onWriteCalcBoxData: function (data) {
            var self = this,
                $ctx = this.$ctx;
            $ctx.find('.calc-subtotal').html(data.data.subtotal);
            $ctx.find('.calc-delivery').html(data.data.delivery);
            $ctx.find('.calc-payment').html(data.data.payment);
            $ctx.find('.calc-tax').html(data.data.tax);
            $ctx.find('.calc-total').html(data.data.totalPrice);
        },


        /**
        *
        * @method onPickupPlaceselected
        *
        * fire method to set new delivery costs and taxes
        *
        * @param data
        */
        onPickupPlaceselected: function (data) {
        	var cartData = data.data;
        	$('.calc-delivery').fadeOut(function() {
        		$(this).html(cartData.deliveryCost.price).fadeIn();
          	});

        	$('.calc-tax').fadeOut(function() {
      		  	$(this).html(cartData.totalTax.price).fadeIn();
        	});

        	$('.calc-total').fadeOut(function() {
        		$(this).html(cartData.totalPrice.price).fadeIn();
        	});

        	$('.updated-delivery-mode-name').fadeOut(function() {
      		  	$(this).html(cartData.deliveryMode.name).fadeIn();
        	});
        },

		/**
		 *
		 * @method onDialogConfirm
		 *
		 * if user has selected continue in yes no lightbox
		 *
		 * @param data
		 */
		onDialogConfirm: function (data) {
			var url;
			if (data.attribute.source == "checkout"){
				url = "/checkout/openorder" + "/setCloseDate?closeDate=" + data.attribute.openOrderCloseDateToday + "&code=" + data.attribute.orderCode;

				$.ajax({
					url: url,
					type: 'POST',
					dataType: 'json',
					method: 'post',
					success: function (data, textStatus, jqXHR) {
						var reviewForm = $('.reviewForm');
						reviewForm.submit();
					},
					error: function (jqXHR, textStatus, errorThrown) {
						alert(textStatus);
					}
				});

			}else if (data.attribute.source == "myaccount"){
				url = "/checkout/openorder" + "/changeOpenOrderInERP?closeDate=" + data.attribute.openOrderCloseDateToday + "&code=" + data.attribute.orderCode;

				$.ajax({
					url: url,
					type: 'POST',
					dataType: 'json',
					method: 'post',
					success: function (data, textStatus, jqXHR) {
						location.reload();
					},
					error: function (jqXHR, textStatus, errorThrown) {
						alert(textStatus);
					}
				});

			}

		},

		/**
		 *
		 * @method onDialogCancel
		 *
		 * if user has selected NO in yes no lightbox
		 *
		 * @param data
		 */
		onDialogCancel: function (data) {
		}

	});

})(Tc.$);
