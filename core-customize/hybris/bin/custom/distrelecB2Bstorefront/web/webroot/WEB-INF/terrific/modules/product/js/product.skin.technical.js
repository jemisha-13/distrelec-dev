(function($) {

    /**
     * Product Skin Technical implementation for the module Productlist Order.
     *
     */
    Tc.Module.Product.Technical = function (parent) {

        this.on = function (callback) {

            var $ctx = this.$ctx,
                self = this;

            this.productCodes = [];// placeholder for all product-codes in list

            //Add to cart buttons
            this.$btnCart = this.$ctx.find('.btn-cart');
            self.$btnCart.off('click').on('click', function (ev) {
                ev.preventDefault();
                ev.stopPropagation();
                self.onAddToCartClick(ev);
            });

            // availability Templates
            this.tmplStockLevel = doT.template($('#tmpl-stock_level', $ctx).html());
            this.tmplStockLevelPickupHeader = $('#tmpl-stock_level_pickup_header', $ctx).html();
            this.tmplStockLevelPickupRow = doT.template($('#tmpl-stock_level_pickup_row', $ctx).html());

            // Load the Availability of every product in the tabular table
            self.getAvailable(0);

            parent.on(callback);
        };

        this.onAddToCartClick = function (ev) {
            ev.preventDefault();

            this.$btnCart.addClass('active');
            var productCode = this.$btnCart.data('product-code');

            var $target = $(ev.target)
                , $product = $target.closest('.main__actions')
                , queryParam = ""
            ;

            $target.closest('.btn-cart').addClass('item-added');

            this.triggerCart($product, queryParam);

        };

        this.triggerCart = function ($product, queryParam) {

            var productCode = $product.find('.hidden-product-code').val().replace(/\D/g,'');
            var productPrice = $product.find('.hidden-price').val();
            var productQty = $product.find('.mod-numeric-stepper').find('.ipt').val();
            var productValue = parseFloat(productPrice * productQty).toFixed(2);
            var currency = digitalData.page.pageInfo.currency;
            var dyQuantity = parseInt(productQty);
            var origPosition= $product.find('.hidden-origPosition').val();
            var query='';
            var searchQuery= $product.find('.hidden-searchQuery').val();
            
            if(searchQuery === ""){
             query = $("#hidden-searchQuery").val();
            } 
            
            if(typeof searchQuery != 'undefined' && (searchQuery === "" || searchQuery === "*")){
            	query=$product.find('.hidden-categoryCodePath').val();
            }else {
            	query=searchQuery;
            }
            var pos= $product.find('.hidden-pos').val();
            var origPageSize= $product.find('.hidden-origPageSize').val();
            var productFamily= $product.find('.hidden-productFamily').val();
            var productCampaign= $product.find('.hidden-productCampaign').val();
            if (productFamily === undefined || productFamily === null){
            	productFamily=false;
            }
            // Trigger Cart API to add to cart
            $(document).trigger('cart', {
                actionIdentifier: 'toolsitemAddToCart',
                type: 'add',
                productCodePost: productCode,
                qty: productQty, // backend magic: we send 0 and the backend automatically set it to the minimum quantity
                origPos: origPosition,
                pos: pos,
                origPageSize: origPageSize,
                prodprice: productPrice,
                queryParam: query,
                isProductFamily: productFamily,
                productCampaign: productCampaign
            });
        };


        this.getAvailable = function(start){
            var self = this,
                $ctx = this.$ctx,
                $listItems = $ctx.find('.mod-shipping-information'),
                $hiddenCode = $ctx.find('.hidden-product-code'),
                productCodes = this.productCodes,
                productCodesQuantities = [],
                productNum = [],
                i,
                len = $hiddenCode.length;

            // Gather product data for each cart list item
            for (i = start; i < len; i += 1) {
                productNum.push(i);
                this.productCodes.push( $ctx.find('.hidden-product-code')[i].value );
            }

            // Perform availability request product list tabular
            $.ajax({
                url: '/availability',
                dataType: 'json',
                data: {
                    productCodes: this.productCodes.join(','),
                    detailInfo: true
                },
                contentType: 'application/json',
                success: function (data) {
                    var items = data.availabilityData.products,
                        item,
                        item2,
                        $listItem;


                    $.each(productCodes, function (i) {
                        var count = 0;
                        var found = false;
                        for (var item in items) {
                            if (items[count][this.toString()] !== undefined && !found){
                                item2 = items[count][this.toString()];
                                found = true;
                            }
                            count++;
                        }

                        $listItem = $listItems.eq(productNum[i]);
                        var productCode = this.toString();

                        if ( item2 !== undefined) {
                            self.getPopover(item2, $listItem, productCode);
                        }

                    });

                    $ctx.find('.loading').removeClass('loading');
                }
            });


        };

        /**
         *
         * @getPopover product list search tabular
         *
         * @param item
         * @param $listItem
         */
        this.getPopover = function(item, $listItem, productCode){

            var self = this,
                stockLevelPickup = '',
                countLines = 0,
                $infoStock = $('.info-stock-'+productCode),
                statusCode = parseInt($('.leadTimeFlyout-'+productCode).data('status-code'));

            if (isNaN(statusCode)) {
                statusCode = 0;
            }

            $.each(item.stockLevels, function (index, stockLevel) {

                // In Stock

                    if (stockLevel.available > 0) {
                        // short
                        var $inStock = $infoStock.find('.inStockText');
                        $infoStock.find('.instock').removeClass('hidden');
                        if ($inStock.data('instock-text') !== undefined) {
                            var inStockText = $inStock.data('instock-text').replace('{0}', stockLevel.available);
                            $inStock.html(inStockText);
                        }

                        // long (flyout)
                        var $inStockLong = $('.leadTimeFlyout-' + productCode).find('.inStockLong');
                        if ($inStockLong.data('instock-text') !== undefined) {
                            var inStockTextLong = $inStockLong.data('instock-text').replace('{0}', stockLevel.available);
                            $inStockLong.html(inStockTextLong);
                        }

                        countLines++;
                    }

                // Further additional product list (Only in CH and only for warehouseCode = 7371) tabular
                var warehouseCdcCode = $('.leadTimeFlyout').data('warehouse-cdc');

                // further Stock
                if (stockLevel.available > 0) {

                    // short
                    var $further = $('.info-stock-'+productCode).find('.further');
                    $('.info-stock-'+productCode).find('.further').removeClass('hidden');

                    if ($further.data('further-text') !== undefined) {
                        var furtherText = $further.data('further-text').replace('{0}', stockLevel.available).replace('{1}', stockLevel.deliveryTime.split(' ')[0]);
                        $further.find('.further-text--fast').html(furtherText);
                    }

                    // long (flyout)
                    var $furtherLong = $('.leadTimeFlyout-' + productCode).find('.furtherLong');

                    if ($furtherLong.data('further-text') !== undefined) {
                        var furtherLongText = $furtherLong.data('further-text').replace('{0}', stockLevel.available).replace('{1}', stockLevel.deliveryTime.split(' ')[0]);
                        $furtherLong.html(furtherLongText);
                        countLines++;
                    }
                }



                // more stock available
                // more stock available in X weeks - tabular
                if (stockLevel.leadTime !== undefined && stockLevel.leadTime > 0 && statusCode < 40) {
                    var $moreStockAvailable = $('.leadTimeFlyout-' + productCode).find('.moreStockAvailable');

                    if ($moreStockAvailable.data('morestock-text') !== undefined) {
                        var moreStockAvailableText = $moreStockAvailable.data('morestock-text').replace('{0}', stockLevel.leadTime);
                        $moreStockAvailable.html(moreStockAvailableText);
                    }

                }

                //More in [] week(s) --> More stock available in [ ] week(s) (In CH display this when any of the above conditions equal 0 instead) tabular
                if (countLines < 3 && stockLevel.leadTime !== undefined && stockLevel.leadTime > 0 && statusCode < 40) {
                    var $moreStockAvailablePDP = $infoStock.find('.moreStockAvailableText');
                    $('.info-stock').find('.moreStockAvailable').removeClass('hidden');

                    if ($moreStockAvailablePDP.data('morestockavailable-text') !== undefined) {
                        var moreStockAvailableTextPDP = $moreStockAvailablePDP.data('morestockavailable-text').replace('{0}', stockLevel.leadTime);
                        $moreStockAvailablePDP.html(moreStockAvailableTextPDP);
                    }


                }
            });

            // Pick up
            // For shops, display availability if
            // 1) there is an available quantity in _any_ warehouse, regardless of sales status, or
            // 2) sales status is < 40, regardless of available quantities
            if (item.stockLevelPickup !== undefined && item.stockLevelPickup.length > 0) {
                $.each(item.stockLevelPickup, function (index, stockLevelPickup) {
                    if (item.stockLevelTotal > 0 || statusCode < 40) {
                        var $pickUp = $infoStock.find('.pickupInStoreText');
                        $infoStock.find('.pickup').removeClass('hidden');
                        if($pickUp.data('pickup-text') !== undefined) {
                            var pickupText = $pickUp.data('pickup-text').replace('{0}', stockLevelPickup.stockLevel);
                            $pickUp.html(pickupText);
                        }

                        countLines++;

                        // long (flyout) tabular
                        var $pickUpLong = $('.leadTimeFlyout-'+productCode).find('.pickupLong');
                        if ($pickUpLong.data('pickup-long-text') !== undefined){
                            var pickpupLongText = $pickUpLong.data('pickup-long-text').replace('{0}', stockLevelPickup.stockLevel);
                            $pickUpLong.html(pickpupLongText);
                        }
                    }
                });
            }



        };

    };

})(Tc.$);
