(function ($) {

    /**
     * Module implementation.
     *
     * @namespace Tc.Module
     * @class Cart-list-item
     * @extends Tc.Module
     */
    Tc.Module.CartListItem = Tc.Module.extend({

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
        init: function ($ctx, sandbox, id) {
            // call base constructor
            this._super($ctx, sandbox, id);

            // subscribe to connector channel/s
            this.sandbox.subscribe('cart', this);
            this.sandbox.subscribe('lightboxStatus', this);

            this.removeItemFinally = $.proxy(this.removeItemFinally, this);
            this.productName = $ctx.find('.hidden-product-name').val();
            this.manufacturer = $ctx.find('.hidden-manufacturer').val();
            this.productCodeErp = $ctx.find('.hidden-product-code-erp').val();
            this.typeName = $ctx.find('.hidden-product-type-name').val();
            this.entryNumber = $ctx.find('.hidden-entry-number').val();
            this.productCost = $ctx.find('.hidden-product-price').val();
            this.position=$ctx.find('.hidden-position-number').val();


            this.$calibration = $('.calibration', $ctx);
            this.$calibrationCostPlus = $('.calibrationCostPlus', this.$calibration);
            this.$calibrationCostMinus = $('.calibrationCostMinus', this.$calibration);
            this.calibratedItemId = this.$calibration.data('calibrated-item-id');
            this.nonCalibratedItemId = this.$calibration.data('non-calibrated-item-id');
            this.isCalibrated = this.$calibration.data('is-calibrated');

            if (this.$calibration.length) {
                if (this.isCalibrated) {
                    this.priceDiff(this.nonCalibratedItemId, this.calibratedItemId);
                } else {
                    this.priceDiff(this.calibratedItemId, this.nonCalibratedItemId);
                }
            }

            this.$numeric = $('.numeric', $ctx);

            this.$iptAmount = this.$numeric.find('.ipt');
            this.currentAmount = this.$iptAmount.val(); // cache current amount to compare with new amount

            // array to store and cancel ongoing ajax calls
            this.jXhrPool = [];
            this.jXhrPoolId = 0;
        },

        /**
         * Hook function to do all of your module stuff.
         *
         * @method on
         * @param {Function} callback function
         * @return void
         */
        on: function (callback) {

            var $ctx = this.$ctx,
                self = this;

            Tc.Utils.numericStepper(self.$numeric, {
                change: function () {
                    if (!self.$numeric.hasClass("numeric-error") && !self.$numeric.hasClass("is-quote")) {
                        self.updateItem();
                    }
                }
            });

            self.initRemoveItem();
            $ctx.find('.ipt-reference').on('keyup', {entryNumber: self.entryNumber}, self.referenceChange);

            //
            // Cart change event is coming from the cart api
            if (self.position === '0') {  // Just add one event handler
                $(document).on('cartChange', $.proxy(function (ev, data) {
                    var self = this;
                    // only handle event if it is coming from update cart action
                    if(data.actionIdentifier === 'cartUpdate') {
                        if (data.type === 'update') {
                            self.fire('activateRecalculate', {}, ['cart']);
                        }
                    } else if(data.actionIdentifier === 'removeItemFromCart') {
                        if(data.type === 'remove') {
                            var cartIsEmpty = ($('.cart-list li').length === 0);
                            if (cartIsEmpty) {
                                window.location.href = '/cart';
                            }
                        }
                    }
                }, this));
            }

            $ctx.on('click', 'input:radio', function(e) {
                var $selectedRadioButton = $(e.target);
                if ($selectedRadioButton.val()==1) {
                    self.replaceItem(self.nonCalibratedItemId,self.calibratedItemId,true);
                } else {
                    self.replaceItem(self.calibratedItemId,self.nonCalibratedItemId,false);
                }
            });

            callback();
        },

        referenceChange: function (e) {
            var referenceBox = this;
            var entryNumber = e.data.entryNumber;
            var refNumber = $(referenceBox).val();
            $.ajax({
                url: '/cart/update/reference',
                dataType: 'json',
                method: 'post',
                data: {
                    entryNumber: entryNumber,
                    customerReference: refNumber

                },
                success: function (a, b, c) {

                },

                error: function (d, e, f) {

                }
            });
        },

        /**
         *
         * @method updateItem
         *
         */
        updateItem: function () {

            var self = this;

            // Trigger Cart API to add to cart
            $(document).trigger('cart', {
                actionIdentifier: 'cartUpdate',
                type: 'update',
                entryNumber: self.entryNumber,
                quantity: self.$numeric.find('.ipt').val()
            });
        },

        /**
         *
         * @method initRemoveItem
         *
         */
        initRemoveItem: function () {
            var self = this
                ,$ctx = this.$ctx
                ,$btnRemove = $('.btn-numeric-remove', $ctx)
                ,$btnRemoveQuote = $('.btn-numeric-remove-quote', $ctx)
            ;
                $btnRemove.on('click', function () {
                    window.dataLayer.push( {'ecommerce': null});  // Clear the previous ecommerce objects, if present
                    window.dataLayer.push({
                        event: 'removeFromCart',
                        ecommerce: {
                            currencyCode: window.digitalData.page.pageInfo.currency,
                            remove: {
                                products: [{
                                    id: $btnRemove.data('product-id')
                                }]
                            }
                        }
                    });
                    self.removeItemFinally();
                });
                $btnRemoveQuote.on('click', function(e){
                    e.preventDefault();
                    var quoteId = $(this).attr('data-quote-id');
                    self.removeQuoteFinally(quoteId);
                });
        },

        /**
         *
         * @method removeItemFinally
         *
         */
        removeItemFinally: function () {
            var self = this,
                $ctx = this.$ctx;

            $('.calibration-service').addClass('disabled').find('input').attr('disabled','disabled'); // Line enumeration gets screwed up when deleting an item; disabeling the possibility to switch to/from calibrated item on other rows

            $ctx.css({
                overflow: 'hidden'
            }).animate({
                height: 0,
                opacity: 0
            }, 500, function () {
                $ctx.remove();
                if (typeof self.entryNumber === 'undefined') {
                    if ($('.cart-list .mod-cart-list-item').length===0) {
                        $('.btn-add-to-cart').addClass('disabled');
                    }
                } else {
                    $(document).trigger('cart', {
                        actionIdentifier: 'removeItemFromCart',
                        type: 'remove',
                        entryNumber: self.entryNumber,
                        qty: -1
                    });
                }
            });
        },

        /**
         *
         * @method removeQuoteFinally
         *
         */
        removeQuoteFinally: function (quoteId) {
            var self = this,
                $quote = $('.quote-'+quoteId);

            $(document).trigger('cart', {
                actionIdentifier: 'removeItemFromCart',
                type: 'removeQuote',
                quoteId: quoteId,
                qty: -$quote.length + 1
            });

            $quote.css({
                overflow: 'hidden'
            }).animate({
                height: 0,
                opacity: 0
            }, 500, function () {
                $quote.remove();
            });
        },

        /**
         * Post terrific module load function - gets called when callbacks from on events finished.
         *
         * @method after
         */
        after: function () {

        },

        priceDiff: function(source, target) {
            var self = this,
                $ctx = this.$ctx;
            $.ajax({
                url: '/cart/price-diff',
                dataType: 'json',
                method: 'post',
                data: {
                    source: source,
                    target: target
                },
                success: function (data, textStatus, jqXHR) {
                    if (!data.error && data.priceDiff && data.priceDiff.amount) {
                        var absVal = data.priceDiff.amount.replace('-',''); // Can't use Math.abs(), decimal sign varies depending on country
                        var sign =  data.priceDiff.amount.indexOf('-') ? '+' : '-'; // Can't check for < 0, decimal sign varies depending on country
                        $('.calibration-cost-'+source, $ctx).html('('+sign+absVal+' '+data.priceDiff.currency+')');
                        $('.calibration-cost-'+target, $ctx).html('');
                    }
                },
                error: function (data, textStatus, jqXHR) {
                    console.error(data, textStatus, jqXHR);
                }
            });
        },

        replaceItem: function(source,target,toCal) {
            var self = this,
                $ctx = this.$ctx;
            $.ajax({
                url: '/cart/replace',
                dataType: 'json',
                method: 'post',
                data: {
                    source: source,
                    target: target,
                    entryNumber: self.entryNumber
                },
                success: function (data, textStatus, jqXHR) {
                    if (typeof data !== 'undefined') {
                        if (typeof data.errorData === 'undefined') {
                            var cart = data.cartData;
                            var thisProduct =  cart.products[self.position];
                            var formattedCode = thisProduct.codeErpRelevant.substr(0,3)+'-'+thisProduct.codeErpRelevant.substr(3,2)+'-'+thisProduct.codeErpRelevant.substr(5);
                            $ctx.find('.productlabel-wrap .cal').toggleClass('hidden', !toCal);
                            $ctx.find('.cell-info .cell-info-table .cell-info-cell:first-child .bd').text(formattedCode);
                            $ctx.find('.cell-info h3').text(thisProduct.name);
                            $ctx.find('.cell-list a, .cell-info a').attr('href', thisProduct.productUrl);
                            $ctx.find('.price-light').addClass('hidden');
                            $ctx.find('.price-box.left .price').html(thisProduct.priceLocal).hide().fadeIn(600);
                            $ctx.find('.price-box.right .price').html(thisProduct.totalPriceLocal).hide().fadeIn(600);
                            $('.calc-subtotal').html(cart.subTotalLocal).hide().fadeIn(600);
                            $('.calc-delivery').html(cart.deliveryCost).hide().fadeIn(600);
                            $('.calc-tax').html(cart.tax).hide().fadeIn(600);
                            $('.calc-total').html(cart.totalPriceLocal).hide().fadeIn(600);
                            $ctx.find('.hidden-product-code').val(thisProduct.codeErpRelevant);
                            $ctx.find('.hidden-product-code-erp').val(thisProduct.codeErpRelevant);
                            $ctx.find('.hidden-product-name').val(thisProduct.name);
                            $ctx.find('.hidden-product-price').val(thisProduct.price.replace(',','.'));
                            $ctx.find('.hidden-product-code-erp').val(thisProduct.codeErpRelevant);
                            self.priceDiff(source, target);
                        } else {
                            console.error('Failed to replace item. Error message: '+data.errorData.msg);
                        }
                    }
                },
                error: function (data, textStatus, jqXHR) {
                    console.error('Failed to replace item. Error message: '+textStatus);
                }
            });
        }

    });

})(Tc.$);
