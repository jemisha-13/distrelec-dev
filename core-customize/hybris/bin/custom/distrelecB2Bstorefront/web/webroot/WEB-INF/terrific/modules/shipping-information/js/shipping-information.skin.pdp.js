(function($) {

    /**
     * Single Skin implementation for the module ShippingInformation.
     *
     * @namespace Tc.Module.Default
     * @class Basic
     * @extends Tc.Module
     * @constructor
     */
    Tc.Module.ShippingInformation.Pdp = function(parent) {

        /**
         * override the appropriate methods from the decorated module (ie.
         * this.get = function()). the former/original method may be called via
         * parent.<method>()
         */
        this.on = function(callback) {
            parent.on(callback);

            var self = this, $ctx = this.$ctx;

            this.$row = $('.row', $ctx);
            this.productCode = $('#product_code', $ctx).val();

            var $leadCloser = self.$$('.lead-closer');
            var $moreAboutLeadLink = self.$$('.moreAboutLeadLink');

            $leadCloser.click(function(e) {
                e.preventDefault();

                var $flyout = $(ev.delegateTarget).closest('.leadTimeFlyout');

                $flyout.toggleClass('hidden');
                $('.lead-arrow-down').toggleClass('hidden');
            });

            $moreAboutLeadLink.click(function(e) {
                e.preventDefault();

                $('.leadTimeFlyout').toggleClass('hidden');
                $('.lead-arrow-down').toggleClass('hidden');
                $('.leadTimeFlyout').css('margin-top', '-' + $('.leadTimeFlyout').height() - 5 + 'px');
            });

            $(document).click(
                function(e) {
                    var className = $(e.target).attr('class');

                    if (className !== 'moreAboutLeadLink' &&
                        className !== 'lead-closer' &&
                        className !== 'leadTimeFlyout' &&
                        className !== 'moreAboutLeadLink' &&
                        className !== 'inStockLong' &&
                        className !== 'furtherLong'
                    ){
                        $('.leadTimeFlyout').addClass('hidden');
                        $('.lead-arrow-down').addClass('hidden');
                    }
                });

            self.getAvailable();

        };

        this.setInfoBoxColor = function (_color) {

            var loadingDiv = $('.loading');

            switch(_color) {
                case 'red':
                    loadingDiv.removeClass('loading--inStock');
                    loadingDiv.addClass('loading--notStock');
                    break;
                case 'green':
                    loadingDiv.removeClass('loading--notStock');
                    loadingDiv.addClass('loading--inStock');
                    break;
            }

        };

        this.setInfoBoxErp = function(erpStatus) {

            var erpStatusHasCross = [53,50,52];

            if(erpStatusHasCross.indexOf(erpStatus) !== -1) {
                $('.icon--inStock').addClass('hidden');
                $('.icon--notStock').removeClass('hidden');
            }

        };

        this.getAvailable = function () {
            var $ctx = this.$ctx,
                self = this;

            $.ajax({
                url: '/availability',
                dataType: 'json',
                data: {
                    productCodes: self.productCode + ";" + $("input:text.ipt").val(),
                    detailInfo: true
                },
                contentType: 'application/json',
                success: function (data) {

                    if (data.availabilityData.products.length) {
                        var item = data.availabilityData.products[0][self.productCode],
                            countLines = 0,
                            statusCode =  parseInt($('.leadTimeFlyout').data('status-code')),
                            countryCode = $('.fsettingsContainer .fsettings .settings ').data('country-code'),
                            swissStock = 0,
                            cdcStock = 0,
                            totalStock = 0,
                            erpStatus = $('.loading').data('status-code');

                        if (isNaN(statusCode)) {
                            statusCode = 0;
                        }

                        if (item !== undefined) {

                            $.each(item.stockLevels, function (index, stockLevel) {

                                switch(stockLevel.warehouseCode) {
                                    case '7374':
                                        swissStock = stockLevel.available;
                                        break;
                                    case '7371':
                                        cdcStock = stockLevel.available;
                                        break;
                                }
                                var $deliveryTime = $('.loading__normal').find('.deliveryTime').find('.deliveryTimeText');

                                var stockLevelPickupItem = item.stockLevelPickup !== undefined && item.stockLevelPickup.length > 0 ? item.stockLevelPickup[0] : false;
                                var isPickupStock = stockLevelPickupItem.warehouseCode === stockLevel.warehouseCode;
                                var stockLevelTotalIsPositive = item.stockLevelTotal > 0;
                                var stockLevelIsPositive = stockLevel.available > 0;
                                var isStockZero = stockLevel.stockLevelTotal === 0;
                                var stockLevelIsWaldom = stockLevel.isWaldom && stockLevel.mview === "BTR";
                                var isSwissStorefront = digitalData.page.pageInfo.countryCode === "CH";
                                var isFrenchStorefront = digitalData.page.pageInfo.countryCode === "FR";

                                if(stockLevel.mview === "BTO" || stockLevel.mview === "DIR"){
                                    $(".notify-link").hide();
                                }

                                // To show available to order for empty stock BTO products
                                if((stockLevel.mview === "BTO" || stockLevel.mview === "DIR")  && !stockLevelTotalIsPositive){
                                    var availableOrder = $('.loading__normal--comingsoon .outofstock .sales-status--no-longer-available').data('available-order');
                                    var btoIcon = $('.loading__normal--comingsoon .fa-times-circle');
                                    var deliveryTextBto = $('.outofstock .deliveryTime');
                                    
                                    btoIcon.removeClass('fa-times-circle').addClass('fa-check-circle');
                                    deliveryTextBto.removeClass('hidden');

                                    $('.outofstock .sales-status').html(availableOrder);

                                    $('.stock-notification').addClass('hidden');

                                }

                                if (stockLevelIsWaldom) {

                                    $('.mod-buying-section .ipt').data('max-waldom-stock', stockLevel.available);

                                    if (stockLevelTotalIsPositive) {

                                        var $inStockWaldom = $('.loading__normal').find('.instock').find('.inStockText');

                                        if ($inStockWaldom.data("instock-text") !== undefined) {
                                            var inStockTextWaldom = $inStockWaldom.data('instock-text').replace('{0}', stockLevel.available);
                                            $inStockWaldom.html(inStockTextWaldom);
                                        }

                                        if ($deliveryTime.data('delivery-text') !== undefined) {
                                            var $waldomDeliveryTimeText = $deliveryTime.data('delivery-text').replace('{0}', stockLevel.replenishmentDeliveryTime);
                                            $deliveryTime.html($waldomDeliveryTimeText);
                                        }
                                    }


                                } else {

                                    if (stockLevelTotalIsPositive && stockLevelIsPositive) {
                                       
                                        // Append Stock To Headline
                                        var $inStock = $('.loading__normal').find('.instock').find('.inStockText');
                                        var inStockText;
                                        var warehouses = item.stockLevels;
                                        if($inStock.data('instock-text') !== undefined &&
                                          (isPickupStock || !stockLevelPickupItem) && stockLevel.mview != "BTO" && stockLevel.mview != "DIR") {
                                            inStockText = $inStock.data('instock-text').replace('{0}', stockLevel.available);
                                            $inStock.html(inStockText);
                                            
                                        } else if(stockLevel.mview == "BTO"){
                                            
                                            inStockText = $inStock.data('instock-bto').replace('{0}', stockLevel.available).replace('{1}', stockLevel.replenishmentDeliveryTime2.split(' ')[0]);
                                            $inStock.html(inStockText);
                                            
                                        }else if(stockLevel.mview == "DIR"){
                                            inStockText = $inStock.data('instock-bto').replace('{0}', stockLevel.available).replace('{1}', stockLevel.replenishmentDeliveryTime.split(' ')[0]);
                                            $inStock.html(inStockText);

                                        }else if(warehouses[0].available > 0 && warehouses[1].available === 0){
                                            $('.deliveryTime').addClass('hidden');

                                            inStockText = $inStock.data('available-order');
                                            $inStock.html(inStockText);
                                        }

                                        var $deliveryTimeText1;
                                        var $deliveryTimeText2;
                                        if($deliveryTime.data('next-day-delivery-text') !== undefined &&
                                          (isPickupStock || !stockLevelPickupItem) && stockLevel.mview != "BTO" && stockLevel.mview != "DIR") {
                                              if(!isFrenchStorefront){
                                                $deliveryTimeText1= $deliveryTime.data('next-day-delivery-text');
                                                $deliveryTime.html($deliveryTimeText1);

                                              } else {
                                                $deliveryTimeText2 = $deliveryTime.data('delivery-text').replace('{0}', stockLevel.replenishmentDeliveryTime);
                                                $deliveryTime.html($deliveryTimeText2);
                                              }
                                        } else if (stockLevel.mview === "BTO" || stockLevel.mview === "DIR"){
                                            
                                                $deliveryTimeText2 = $deliveryTime.data('delivery-text-bto');
                                                $deliveryTime.html($deliveryTimeText2);
                                              
                                        }
                                        else {
                                            
                                            $deliveryTimeText2 = $deliveryTime.data('delivery-text-inconfirmation');
                                            $deliveryTime.html($deliveryTimeText2);

                                        }

                                        countLines++;

                                        $('.estimatedDeliveryDate').removeClass('hidden');

                                    } else if(isStockZero && isSwissStorefront) {
                                        var $inStockZero = $('.loading__normal').find('.instock');
                                        var $stockZero = $inStockZero.find('.inStockText');
                                        var $deliveryText = $('.loading__normal').find('.deliveryTime');
                                        $deliveryText.parent().addClass('no-heading');
                                        $deliveryText.find('.deliveryTimeText').addClass('hidden');

                                        if($stockZero.data('instock-text') !== undefined) {
                                            var $stockZeroText = $stockZero.data('instock-text').replace('{0}', item.stockLevelTotal);
                                            $stockZero.html($stockZeroText);
                                        }

                                        $inStockZero.parent().addClass('no-heading');
                                        $stockZero.addClass('hidden');
                                    }

                                }

                                if (stockLevelPickupItem) {

                                    $.each(item.stockLevelPickup, function (index, stockPickup) { 
                                        var $further = $('.info-stock').find('.further');
                                        
                                        if (stockLevelIsPositive && stockPickup.warehouseCode !== stockLevel.warehouseCode && stockLevel.mview !== "BTO") {
                                            
                                            $further.removeClass('hidden');

                                            if ($further.data('further-text-additional') !== undefined && !stockLevelIsWaldom && isSwissStorefront && stockLevel.mview !== "BTO") {
                                                var furtherText = $further.data('further-text-waldom').replace('{0}', stockLevel.available).replace('{1}', stockLevel.replenishmentDeliveryTime.split(' ')[0]);
                                                var furtherText2= $further.data('further-text-additional').replace('{0}', stockLevel.available).replace('{1}', item.stockLevels[1].replenishmentDeliveryTime2.split(' ')[0]);

                                                $('.further').append("<div class='further-text'>" + furtherText + "</div>");

                                                if(item.stockLevels[1].replenishmentDeliveryTime2 !== "0"){
                                                    $('.further').append("<div class='further-text'>" + furtherText2 + "</div>");
                                                }
                                            }

                                        } else if (statusCode <= 40){
                                            
                                            // For products without a stock value in the 7371 warehouse
                                            $further.removeClass('hidden');

                                            var furtherTextNoStock= $further.data('further-text-additional').replace('{0}', stockLevel.available).replace('{1}', item.stockLevels[1].replenishmentDeliveryTime2.split(' ')[0]);

                                            if(stockPickup.warehouseCode !== stockLevel.warehouseCode && item.stockLevels[1].replenishmentDeliveryTime2 !== "0" && stockLevel.mview !== "BTO"){
                                                $('.further').append("<div class='further-text'>" + furtherTextNoStock + "</div>");
                                            }
                                        }
                                    });
                                } else {

                                    var $further = $('.info-stock').find('.further');
                                    $further.removeClass('hidden');

                                    if ($further.data('further-text') !== undefined && !stockLevelIsWaldom && isSwissStorefront) {
                                        var furtherTextSwiss = $further.data('further-text').replace('{0}', item.stockLevelTotal).replace('{1}', stockLevel.replenishmentDeliveryTime.split(' ')[0]);

                                        if(stockLevel.replenishmentDeliveryTime2 !== "0"){
                                            $('.further').append("<div class='further-text'>" + furtherTextSwiss + "</div>");
                                        }
                                    } else {
                                        var $deliveryTimeText3;
                                        if(stockLevel.replenishmentDeliveryTime2 !== undefined && stockLevel.replenishmentDeliveryTime2 !== "0" && stockLevel.mview !== "BTO"){
   
                                            var furtherStockText = $further.data('further-text-additional').replace('{1}', stockLevel.replenishmentDeliveryTime2.split(' ')[0]);

                                            if(statusCode !== 40 && statusCode !== 41 && statusCode !== 50 && statusCode !== 53 && statusCode !== 90){
                                                $('.further').append("<div class='further-text'>" + furtherStockText + "</div>");
                                            }

                                            $deliveryTimeText3 = $deliveryTime.data('next-day-delivery-text');
                                            $deliveryTime.html($deliveryTimeText3);

                                        } else if (stockLevel.mview === "BTO") {
                                            $deliveryTimeText3 = $deliveryTime.data('delivery-text-bto');
                                            $deliveryTime.html($deliveryTimeText3);
                                        }

                                    }

                                }


                                if (stockLevel.leadTime !== undefined && stockLevel.leadTime > 0 && statusCode < 40) {
                                    var $moreStockAvailable = $('.leadTimeFlyout').find('.moreStockAvailable');

                                    if($moreStockAvailable.data('morestock-text') !== undefined) {
                                        var moreStockAvailableText = $moreStockAvailable.data('morestock-text').replace('{0}', stockLevel.leadTime);

                                        $moreStockAvailable.html(moreStockAvailableText);
                                    }
                                }

                                if (countLines < 3 && stockLevel.leadTime !== undefined && stockLevel.leadTime > 0 && statusCode < 40) {
                                    var $moreStockAvailablePDP = $('.info-stock').find('.moreStockAvailableText');

                                    $moreStockAvailablePDP.find('.moreStockAvailable').removeClass('hidden');

                                    if($moreStockAvailablePDP.data('morestockavailable-text') !== undefined) {
                                        var moreStockAvailableTextPDP = $moreStockAvailablePDP.data('morestockavailable-text').replace('{0}', stockLevel.leadTime);

                                        $moreStockAvailablePDP.html(moreStockAvailableTextPDP);
                                    }
                                }

                                if (stockLevelPickupItem) {

                                    $.each(item.stockLevelPickup, function (index, stockLevelPickup) {
                                        
                                        if ((stockLevelTotalIsPositive || statusCode < 40) && stockLevel.mview !== "BTO") {
                                            
                                            var infoStore = $('.info-stock');
                                            var $pickUp = infoStore.find('.pickup').find('.pickupInStoreText');

                                            infoStore.find('.pickup').removeClass('hidden');

                                            if($pickUp.data('pickup-text') !== undefined) {
                                                var pickupText = $pickUp.data('pickup-text').replace('{0}', stockLevelPickup.stockLevel);
                                                
                                                $pickUp.html(pickupText);

                                                if (stockLevelPickup.stockLevel === 0) {
                                                    $pickUp.addClass('hidden');
                                                }
                                            }

                                            countLines++;

                                            var $pickUpLong = $('.leadTimeFlyout').find('.pickupLong');

                                            if ($pickUpLong.data('pickup-long-text') !== undefined) {
                                                var pickpupLongText = $pickUpLong.data('pickup-long-text').replace('{0}', stockLevelPickup.stockLevel);

                                                $pickUpLong.html(pickpupLongText);

                                                if (stockLevelPickup.stockLevel === 0) {
                                                    $pickUpLong.addClass('hidden');
                                                }
                                            }
                                        }
                                    });

                                }


                                if(stockLevelIsPositive) {
                                    var $moreAvailable = $('.info-stock').find('.moreStockAvailable');

                                    if(!stockLevelPickupItem){
                                            $('.pickup').addClass('hidden');
                                    }

                                    if ($moreAvailable.data('morestockavailable-text') !== undefined){
                                        $moreAvailable.removeClass('hidden');
                                        var $moreAvailableText = $moreAvailable.data('morestockavailable-text').replace('{0}', stockLevel.available).replace('{1}', stockLevel.replenishmentDeliveryTime.split(' ')[0]);

                                        $moreAvailable.html($moreAvailableText);
                                    }

                                }

                            });
                        }

                        if (erpStatus === 30 || erpStatus === 31 || (erpStatus === 40 && item.stockLevelTotal > 0) || (erpStatus === 41 && item.stockLevelTotal > 0)) {
                            // Match the ERP statuses which mean in stock & visible on the web

                            self.setInfoBoxColor('green');
                            $('.loading__normal.loading__normal--instock').removeClass('hidden');

                        } else if ((erpStatus === 40 && item.stockLevelTotal < 1) || (erpStatus === 41 && item.stockLevelTotal < 1 || erpStatus === 60 || erpStatus === 61 || erpStatus === 62 || erpStatus === 90 || erpStatus === 91 || erpStatus === 53 || erpStatus === 50 || erpStatus === 52 || erpStatus === 51)) {
                            // Match the ERP statuses which mean out of stock & visible on the web

                            self.setInfoBoxColor('red');
                            self.setInfoBoxErp(erpStatus);
                            $('.loading__normal.loading__normal--outofstock').removeClass('hidden');

                        } else if (erpStatus === 20 || erpStatus === 21) {
                            // Match the ERP statuses which mean coming to stock soon & visible on the web with stock notification

                            self.setInfoBoxColor('green');
                            $('.loading__normal.loading__normal--comingsoon').removeClass('hidden');
                            $('.leadtime-holder').addClass('hidden');

                        }

                        if((item.stockLevelTotal === 0 || item.stockLevelTotal === undefined) && (erpStatus === 30 || erpStatus === 31)) {

                            $.each(item.stockLevels, function (index, stockLevel) {
                                if(stockLevel.isWaldom && stockLevel.mview === "BTR"){

                                    $('.loading__normal.loading__normal--instock').addClass('hidden');
                                    $('.table-icon .icon--notStock').removeClass('hidden');
                                    $('.loading__normal.loading__normal--outofstock').removeClass('hidden');
                                    $('.mod-buying-section .numeric').attr('disabled','disabled').addClass('disabled-btn');
                                    var $unavailableStock = $('.loading__normal.loading__normal--outofstock').find('.sales-status.sales-status--no-longer-available');
                                    $unavailableStock[0].innerText = $unavailableStock.data('waldom-unavailable');

                                    $('.loading').addClass('loading--amber');
                                } else {
                                    $('.loading__normal.loading__normal--instock').addClass('hidden');
                                    $('.loading__normal.loading__normal--comingsoon').removeClass('hidden');
                                    $('.loading__normal.loading__normal--comingsoon .outofstock').removeClass('hidden');
                                }
                            });
                        }

                        var erpDisabledStatuses = [20,40,41,42,43,44,45,50,51,52,53,60,61,62,90,91,99];
                        var erpStatusesHideStock = [20,21,60,61,62];

                        if(erpDisabledStatuses.indexOf(erpStatus) !== -1) {
                            $('.btn-cart').attr('disabled','disabled').addClass('disabled');

                            var erpStatusesEnableATC = [40,41,42,43,44,45,60,61,62,90,91,99];

                            if(erpStatusesEnableATC.indexOf(erpStatus) !== -1 && item.stockLevelTotal > 0) {
                                    $('.btn-cart').removeAttr('disabled').removeClass('disabled');
                                }

                        } else {
                            $.each(item.stockLevels, function (index, stockLevel) {
                                if(stockLevel.isWaldom && stockLevel.mview === "BTR" && stockLevel.available === 0){
                                    $('.btn-cart').attr('disabled','disabled').addClass('disabled');
                                } else {
                                    $('.btn-cart').removeAttr('disabled').removeClass('disabled');
                                }
                            });
                        }

                        if(erpStatusesHideStock.indexOf(erpStatus) !== -1) {
                            $('.tableinfo.info-stock').addClass('hidden');
                        }

                        // DISTRELEC-25041: disabled Add to Cart button on distrelec.fr if status 20,21 or stock is 0
                        var d4DisabledStatuses = [20, 21];
                        if(digitalData.page.pageInfo.shop === 'distrelec france' && d4DisabledStatuses.indexOf(erpStatus) !== -1) {
                            $('.mod-buying-section .btn-cart').attr('disabled','disabled').addClass('disabled');
                            $('.mod-buying-section .numeric').addClass('disabled-btn');
                        }
                        if(digitalData.page.pageInfo.shop === 'distrelec france' && (item.stockLevelTotal === undefined || item.stockLevelTotal === 0)) {
                            $('.mod-buying-section .btn-cart').attr('disabled','disabled').addClass('disabled');
                            $('.mod-buying-section .numeric').addClass('disabled-btn');
                        }

                        var itemCategoryGrps = ['BANS','BANC'],
                            itemCategoryGrp = $('.loading').data('item-catgroup');


                        if(itemCategoryGrps.indexOf(itemCategoryGrp) !== -1 && erpStatus !== 30 && erpStatus !== 31 ) {

                            if ( item.stockLevelTotal === 0) {
                                $('.loading').addClass('loading--amber');
                                $('.item-category-message').removeClass('hidden');
                            }

                        }

                        $productCategoryGrp = $('.loading').data('item-catgroup');
                        digitalData.product[0].productInfo.shipType = $productCategoryGrp;

                        var isItemCategoryGrpAvailable = $('.loading').hasClass('loading--amber');
                        if(digitalData.page.pageInfo.shop === 'distrelec france' && isItemCategoryGrpAvailable ) {
                            $('.mod-buying-section .btn-cart').removeAttr('disabled').removeClass('disabled');
                        }

                        //Adding FE Flag to see if there is stock or not and then base logic off codes

                        var inStock = (item.stockLevelTotal > 0);

                        if(inStock === true) {

                            switch(erpStatus) {
                                case 40:
                                case 41:
                                case 42:
                                case 43:
                                case 44:
                                case 45:
                                    $('.btn-cart').attr('disabled', false);
                                    $('.btn-cart').removeClass('disabled');
                                    break;
                                case 50:
                                case 51:
                                case 52:
                                case 53:
                                    self.setInfoBoxColor('green');
                                    $('.mod-product-status-box').addClass('hidden');
                                    $('.btn-cart').attr('disabled', false);
                                    $('.btn-cart').removeClass('disabled');
                                    $('.loading__normal.loading__normal--instock').removeClass('hidden');
                                    $('.loading__normal.loading__normal--outofstock').addClass('hidden');
                                    $('.table-icon .icon--inStock').removeClass('hidden');
                                    break;
                                case 60:
                                case 61:
                                case 62:
                                case 90:
                                case 91:
                                    $('.table-icon .icon--notStock').removeClass('hidden');
                                    break;
                            }

                        } else {

                            switch(erpStatus) {
                                case 51:
                                    self.setInfoBoxColor('red');
                                    $('.btn-cart').attr('disabled', true);
                                    $('.btn-cart').addClass('disabled');
                                    $('.table-icon .icon--notStock').removeClass('hidden');
                                    break;
                                case 40:
                                case 41:
                                    if(item.stockLevelTotal === 0) {
                                        $('.table-icon .icon--notStock').removeClass('hidden');
                                    }
                                   break;
                                case 60:
                                case 61:
                                case 62:
                                case 90:
                                case 91:
                                    $('.table-icon .icon--notStock').removeClass('hidden');
                                    break;
                            }

                        }

                    }
                },

                error: function() {
                    self.$row.removeClass('loading');
                    self.$row.addClass('error');
                }
            });
        };

    };
})(Tc.$);

