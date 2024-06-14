(function($) {

    Tc.Module.CheckoutBtnContinue = Tc.Module.extend({

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

            this.onDeliveryTypeChange = $.proxy(this, 'onDeliveryTypeChange');
            this.onShippingAddressChange = $.proxy(this, 'onShippingAddressChange');
            this.onPickupStoreChange = $.proxy(this, 'onPickupStoreChange');
            this.onContinueCheckoutClick = $.proxy(this, 'onContinueCheckoutClick');
            this.onAddOrderToExistingOpenOrderChange = $.proxy(this, 'onAddOrderToExistingOpenOrderChange');

            // subscribe to connector channel/s
            this.sandbox.subscribe('deliveryType', this);
            this.sandbox.subscribe('billingAddress', this);
            this.sandbox.subscribe('shippingAddress', this);
            this.sandbox.subscribe('pickupStore', this);
            this.sandbox.subscribe('openOrder', this);

            this.openOrderValidated = false;
            this.mobilePhoneWarnDisplayed = false;

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

            this.$$('.continue-checkout').on('submit', this.onContinueCheckoutClick);

            callback();
        },

        onDeliveryTypeChange: function(data){
            this.$$('.deliveryType').val(data.selectedDeliveryType);
        },

        //
        // User selected a different shipping address or one was preselected
        onShippingAddressChange: function(data){
            this.$$('.shippingAddressId').val(data.address.id);
            this.$$('.pickupLocationCode').val('');
        },

        //
        // User selected a different pickup warehouse or one was preselected
        onPickupStoreChange: function(data){
            this.$$('.pickupLocationCode').val(data.pickupLocationCode);
            this.$$('.shippingAddressId').val('');
        },

        //
        // User changes orderType (standard / open order)
        onProceedOrderAsOpenOrderChange: function(data){
            var isOpenOrder = '';
            if(data.isOpenOrder){
                isOpenOrder = data.isOpenOrder;
            }
            else{
                this.$$('.openOrderType').val('');
            }
            this.$$('.isOpenOrder').val(isOpenOrder);
        },

        //
        // User changes openOrderType (new / existing open order)
        onAddOrderToExistingOpenOrderChange: function(data){
            if(data.addedToExistingOpenOrder){
                this.$$('.openOrderType').val('existing');
            }
            else{
                this.$$('.openOrderType').val('new');
            }

        },

        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        //
        // User selected a different billing address, only used in Multiple Billing Addresses Context
        onBillingAddressChange: function(data){
            var deliveryType = this.$$('.deliveryType').val();
            this.$$('.billingAddressId').val(data.billingAddressId);

            // if the selected billing address is also a shipping address, we set the shipping address id
            if(deliveryType === '0' && data.selAddrIsBillingAndShipping){
                this.$$('.shippingAddressId').val(data.billingAddressId);
            }

        },

        //
        // User clicked continue in checkout
        onContinueCheckoutClick: function(ev){
            var deliveryType = this.$$('.deliveryType').val()
                ,billingAddressId = this.$$('.billingAddressId')
                ,shippingAddressId = this.$$('.shippingAddressId')
                ,pickupLocationCode = this.$$('.pickupLocationCode')
                ,isOpenOrder = this.$$('.isOpenOrder').val();

            if (deliveryType==='2' && (!$('.mobile-number-input').val() || $('.mobile-number-input').val()===$('.mobile-number-input').data('placeholder')) && !this.mobilePhoneWarnDisplayed) {
                ev.preventDefault();
                $('.mobile-number-input').addClass('error');
                $('.mobile-number-info i').removeClass('hidden');
                this.mobilePhoneWarnDisplayed=true;
            }
            else {
                return true;
            }

        },

        //
        // validation of the open order fields was successful, now we can submit the form for real
        onOpenOrderValidationCallback: function(data){
            if(data.hasValidationErrors){
                this.openOrderValidated = false;
            }
            else{
                this.openOrderValidated = true;
                this.$$('.continue-checkout').submit();
            }
        }
    });

})(Tc.$);
