(function($) {

    /**
     * Comparelist Skin implementation for the module ShippingInformation.
     *
     * @namespace Tc.Module.Default
     * @class Basic
     * @extends Tc.Module
     * @constructor
     */
    Tc.Module.ShippingInformation.ComparelistNew = function (parent) {

        /**
         * override the appropriate methods from the decorated module (ie. this.get = function()).
         * the former/original method may be called via parent.<method>()
         */
        this.on = function (callback) {
            // calling parent method
            parent.on(callback);

            var self = this,
                $ctx = this.$ctx;


            // connect
            this.sandbox.subscribe('bundledAvailability', this);


            this.$row = $('.row', $ctx);
            this.$availableBar = $('.available-bar', $ctx);
            this.$btnInfo = $('.btn-info', $ctx);

            this.productCode = $('#product_code', $ctx).val();

            if ($('#tmpl-stock_level', $ctx).length) {
                this.tmplStockLevel = doT.template($('#tmpl-stock_level', $ctx).html());
            }

            this.tmplStockLevelPickupHeader = $('#tmpl-stock_level_pickup_header', $ctx).html();

            if ($('#tmpl-stock_level_pickup_row', $ctx).length) {
                this.tmplStockLevelPickupRow = doT.template($('#tmpl-stock_level_pickup_row', $ctx).html());
            }

            this.availTmplDots = $.availableBar.tmplDots;
            this.availTmplTime = $.availableBar.tmplTime;
            this.availState = $.availableBar.states;

            var $leadCloser = self.$$('.lead-closer');

            // close button
            $leadCloser.click(function(ev) {
                ev.preventDefault();

                var $flyout = $(ev.delegateTarget).closest('.leadTimeFlyout');

                $flyout.toggleClass('hidden');
            });

            var $moreAboutLeadLink = self.$$('.moreAboutLeadLink');

            // more bout lead link comparelist
            $moreAboutLeadLink.click(function(ev) {
                ev.preventDefault();

                //close others if they are open
                $('.leadTimeFlyout').addClass('hidden');

                var productCode = $(this).data('product-code');
                $('.leadTimeFlyout-'+productCode).toggleClass('hidden');

            });

            $(document).click(function(e) {

                var target = e.target; //target div recorded
                var id = $(e.target).attr('id');
                var className = $(e.target).attr('class');
                var productCode = $(e.target).data('product-code');

                if (className !== 'moreAboutLeadLink ellipsis' && className !== 'lead-closer' && className !== 'leadTimeFlyout' &&
                    id !== 'moreAboutLeadLink' && className !== 'inStockLong' && className !== 'furtherLong'){

                    $('.leadTimeFlyout').addClass('hidden');
                }

            });

        };

        //comparelist
        this.getPopover = function(item) {
            var $ctx = this.$ctx,
                self = this,
                stockLevelPickup = '';

            if (item.stockLevelPickup !== undefined) {
                stockLevelPickup += self.tmplStockLevelPickupHeader;
                stockLevelPickup += self.tmplStockLevelPickupRow(item.stockLevelPickup);
            }

            self.$btnInfo
                .popover({
                    placement: 'top',
                    content: self.tmplStockLevel(item) + stockLevelPickup,
                    html: true
                })
                .popover('toggle')
                .off('click')
                .on('click', function () {
                    $(this).popover('show');
                });

            self.$btnInfo
                .on('mouseenter', function () {
                    $(this).popover('show');
                });

            // Change popover position to be within its own compare list item
            var rightOffsetToModule = 55;

            this.$$('.popover').offset({
                left: this.$ctx.offset().left + this.$ctx.width() - this.$$('.popover').outerWidth() + rightOffsetToModule
            });
        };


        // sandbox listener: bundled availability call,
        // params: {data}
        this.onBundledAvailabilityChange = function(data) {
            var self = this,
                blnShowError = data.error,
                products = data.availabilityData.products,
                arrItem,
                item;

            if (Boolean(products)) {
                $.each(products, function(i, e) {
                    if (typeof e[self.productCode] !== 'undefined') {// try to match this modules product-code with one of the triggered
                        arrItem = $.map(e, function(availDetails, productCode) {
                            return availDetails;// returns an ARRAY with neccessary object
                        });
                    }
                });

                item = arrItem[0];// now we can reuse the handling as in getAvailable -> success

                if (!blnShowError) {
                    if (typeof item.backorderDeliveryDate !== 'undefined' && item.backorderDeliveryDate !== '') {
                        item.backorderQuantityFormated = ' (' + item.backorderDeliveryDate + ')';
                    } else {
                        item.backorderQuantityFormated = '';
                        item.backorderQuantity = '0';
                    }

                    self.$availableBar.html(self.availTmplDots(self.availState[item.statusCode]));
                    self.$availableBar.append(self.availTmplTime(item));
                    self.$row.removeClass('loading');
                    $(".divAvailability").removeClass('loading');
                    self.getPopover(item);
                }
                else {
                    self.$row.removeClass('loading');
                    $(".divAvailability").removeClass('loading');
                    self.$row.addClass('error');
                }
            }
        };
    };
})(Tc.$);
