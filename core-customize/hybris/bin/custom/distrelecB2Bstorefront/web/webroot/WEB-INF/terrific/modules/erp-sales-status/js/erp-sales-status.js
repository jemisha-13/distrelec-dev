(function($) {

	var codeList = []
        , codes = $('.hidden-product-code')
        , productCodes = []
        , productNum = []
        , $listItems = $(".mod-erp-sales-status")
        , $moreAboutLeadLink = $('.moreAboutLeadLink')
        , $leadCloser = $('.lead-closer');

    if (document.addEventListener) {
        document.addEventListener("DOMContentLoaded", initSalesStatus(), false);
    }
    else {
        document.attachEvent("onDOMContentLoaded", initSalesStatus);
    }

    function initSalesStatus() {
        $moreAboutLeadLink = $('.moreAboutLeadLink');
        $leadCloser = $('.lead-closer');

        // DISTRELEC-19930: improve cart speed, remove unnecessary requests
        if (!$("body").hasClass("skin-layout-cart")) {
            getErpSalesStatus();
        }
    }

    function getErpSalesStatus() {

        codes = $('.hidden-product-code');

        if ( codes.length > 0 ) {

            $.each(codes, function (index, code) {
                productNum.push(index);
                productCodes.push( $(code).val() );
            });

            $.ajax({
                url: '/availability',
                dataType: 'json',
                data: {
                    productCodes: productCodes.join(','),
                    detailInfo: true
                },
                contentType: 'application/json',
                success: function (data) {

                    var items = data.availabilityData.products,
                        item,
                        item2,
                        $listItem;

                    $.each(productCodes, function(i) {
                        var count = 0;
                        var found = false;
                        for (var item in items) {
                            if (items[count][this.toString()] !== undefined && !found) {
                                item2 = items[count][this.toString()];
                                found = true;
                            }

                            count++;
                        }

                        $listItem = $listItems.eq(productNum[i]);
                        var productCode = this.toString();
                        // self.getPopover(item2, $listItem, productCode);

                        setErpSalesStatus(item2, $listItem, productCode);

                    });
                }

            });

        }

    }

	function setErpSalesStatus(item, $listItem, productCode) {

        var countLines = 0
            , countryCode = $('.fsettingsContainer .fsettings .settings ').data('country-code')
            , swissStock = 0
            , cdcStock = 0
            , totalStock = 0
            , erpStatus = $('.loading').data('status-code')
            , $element = $('.erp-sales-status--'+productCode)
            , $statusCode = $element.data('status-code')
            , $isBomToolReviewPage = $('body').hasClass('skin-layout-bom-tool-review') ? true : false
            , $isNewSalesStatusEnabled = $('.isNewSalesStatusEnabled').val();

        if (item === undefined) {
            return true;
        }

        $.each(item.stockLevels, function (index, stockLevel) {

            switch(stockLevel.warehouseCode) {
                case '7374':
                    swissStock = stockLevel.available;
                    break;
                case '7371':
                    cdcStock = stockLevel.available;
                    break;
            }

            if(stockLevel.isWaldom && stockLevel.mview === "BTR") {

                if($('body').hasClass('skin-layout-bom-tool-review')) {
                    $listItem.closest('.bom-product__actions-right').find('.ipt').data('max-waldom-stock', stockLevel.available);
                    if(stockLevel.available === 0) $listItem.closest('.bom-product__actions-right').find('.ipt').val(0);
                } else if ($('body').hasClass('skin-layout-shopping-list')) {
                    $('[data-product-code="' + productCode + '"]').find('.ipt').data('max-waldom-stock', stockLevel.available ? stockLevel.available : 0);
                    if(stockLevel.available === 0) $('[data-product-code="' + productCode + '"]').find('.ipt').val(0);
                } else if ($('body').hasClass('skin-layout-compare')) {
                    if(stockLevel.available === 0) {
                        $('.compare-cart[data-product-code="' + productCode + '"]').addClass("disabled").addClass("disabled", true);
                        $('.btn-cart[data-product-code="' + productCode + '"]').addClass("disabled").attr("disabled", true);
                    }
                }

                if(stockLevel.available){
                    $element.find('.erp-sales-status__instock').removeClass('hidden');
                    $element.find('.erp-sales-status__currentlynotavailable').addClass('hidden');
                } else {
                    $element.find('.erp-sales-status__instock').addClass('hidden');
                    $element.find('.erp-sales-status__currentlynotavailable').removeClass('hidden');
                }
            }
            // In Stock
            if (item.stockLevelTotal > 0) {

                var $inStock = $element.find('.erp-sales-status__instock'),
                    $inStockText = $element.find('.erp-sales-status__instock .inStockText');
                $inStock.removeClass('hidden');

                if($inStockText.data('instock-text') !== undefined) {
                    var inStockText = $inStockText.data('instock-text').replace('{0}', stockLevel.available);
                    $inStock.html(inStockText);
                }

                if ( stockLevel.available > 0) {
                    $element.find('.erp-sales-status__instock').removeClass('hidden');
                    $element.find('.erp-sales-status__availabletoorder').addClass('hidden');
                    $element.find('.estimatedDeliveryDate').removeClass('hidden');
                }

                // long (flyout)
                var $inStockLong = $element.find('.leadTimeFlyout .inStockLong');
                if($inStockLong.data('instock-text') !== undefined) {
                    var inStockTextLong = $inStockLong.data('instock-text').replace('{0}', stockLevel.available);
                    $inStockLong.html(inStockTextLong);
                }
                countLines++;


                if ($isNewSalesStatusEnabled == 'true') {
                    var erpSuspendedStatuses = [50,51,52,53],
                        erpBlockedStatuses = [90,91];

                    if(erpSuspendedStatuses.indexOf($statusCode) !== -1) {

                        if ( stockLevel.available > 0 ) {
                            $element.find(".erp-sales-status__currently-unavailable").addClass("hidden");
                            $element.removeClass('status-error');
                        } else {
                            $element.find(".erp-sales-status--element").addClass("hidden");
                            $element.find(".erp-sales-status__currently-unavailable").removeClass("hidden");
                            $element.addClass('status-error');
                        }

                    }

                    if(erpBlockedStatuses.indexOf($statusCode) !== -1) {
                        $element.find(".erp-sales-status--element").addClass("hidden");
                        $element.find(".erp-sales-status__currently-unavailable").removeClass("hidden");
                        $element.addClass('status-error');
                    }

                    if ( $statusCode === 20 ) {
                        $element.find(".erp-sales-status--element").addClass("hidden");
                        $element.find(".erp-sales-status__20").removeClass("hidden");
                        $element.addClass('status-error');

                        if ($isBomToolReviewPage) {
                            $('.bom-unavailable-product__item--'+productCode).find('button').addClass('hidden');
                            $('.bom-mpn-product__item--'+productCode).find('button').addClass('hidden');
                        }

                    }

                    if ( $statusCode === 21 ) {
                        $element.find(".erp-sales-status--element").addClass("hidden");
                        $element.find(".erp-sales-status__21").removeClass("hidden");
                    }

                }

            } else if (item.stockLevelTotal === 0 && !stockLevel.isWaldom){
                $element.find('.erp-sales-status__instock').addClass('hidden');
                $element.find('.erp-sales-status__availabletoorder').removeClass('hidden');
            }

            if ( ($statusCode >= 40 && $statusCode <= 45) || ($statusCode === 10 || $statusCode === 60 || $statusCode === 90 || $statusCode === 91 || $statusCode === 0) ) {
                $element.find('.erp-sales-status__availabletoorder').addClass("hidden");
                $element.find('.erp-sales-status__40-45').removeClass("hidden");

                if ( item.stockLevelTotal > 0 ) {
                    $element.find(".erp-sales-status__40-45-instock").removeClass("hidden");
                } else {
                    $element.find(".erp-sales-status__40-45-outofstock").removeClass("hidden");

                    if ($isBomToolReviewPage) {
                        $('.bom-unavailable-product__item--'+productCode).find('button').addClass('hidden');
                        $('.bom-mpn-product__item--'+productCode).find('button').addClass('hidden');
                    }

                }

            }

            // further Stock

            if(stockLevel.available > 0){

                // short
                var $further = $element.find('.further-stock');
                $further.removeClass('hidden');

                if ($further.data('further-text') !== undefined){
                    var furtherText = $further.data('further-text').replace('{0}', stockLevel.available).replace('{1}', stockLevel.deliveryTime.split(' ')[0]);
                    $further.append("<div class='further-text'>"+furtherText+"</div>");
                }

                // long (flyout)
                var $furtherLong =$element.find('.leadTimeFlyout .furtherLong');

                if ($furtherLong.data('further-text') !== undefined){
                    var furtherLongText = $furtherLong.data('further-text').replace('{0}', stockLevel.available).replace('{1}', stockLevel.deliveryTime.split(' ')[0]);
                    $furtherLong.html(furtherLongText);
                    countLines++;
                }

            }



            // more stock available
            // Slow and External - This is More in {leadTime} weeks

            // more stock available in X weeks
            if (stockLevel.leadTime !== undefined && stockLevel.leadTime > 0 && $statusCode < 40) {
                var $moreStockAvailable = $element.find('.moreStockAvailable');
                if($moreStockAvailable.data('morestock-text') !== undefined) {
                    var moreStockAvailableText = $moreStockAvailable.data('morestock-text').replace('{0}', stockLevel.leadTime);
                    $moreStockAvailable.html(moreStockAvailableText);
                }
            }

            //More in [] week(s) --> More stock available in [ ] week(s) (In CH display this when any of the above conditions equal 0 instead) single
            if (countLines < 3 && stockLevel.leadTime !== undefined && stockLevel.leadTime > 0 && $statusCode < 40) {
                var $moreStockAvailablePDP = $element.find('.morestockavailable');
                $element.find('.erp-sales-status__morestockavailable').removeClass('hidden');
                if($moreStockAvailablePDP.data('morestockavailable-text') !== undefined) {
                    var moreStockAvailableTextPDP = $moreStockAvailablePDP.data('morestockavailable-text').replace('{0}', stockLevel.leadTime);
                    $moreStockAvailablePDP.html(moreStockAvailableTextPDP);
                }
            }


            // Pick up
            // For shops, display availability if
            // 1) there is an available quantity in _any_ warehouse, regardless of sales status, or
            // 2) sales status is < 40, regardless of available quantities
            if (item.stockLevelPickup !== undefined && item.stockLevelPickup.length > 0) {
                $.each(item.stockLevelPickup, function (index, stockLevelPickup) {
                    if (item.stockLevelTotal > 0 || $statusCode < 40) {
                        var $pickUp = $element.find('.erp-sales-status__pickup .pickupInStoreText');
                        $element.find('.erp-sales-status__pickup').removeClass('hidden');
                        if($pickUp.data('pickup-text') !== undefined) {
                            var pickupText = $pickUp.data('pickup-text').replace('{0}', stockLevelPickup.stockLevel);
                            $pickUp.html(pickupText);
                        }
                        countLines++;

                        // long (flyout)
                        var $pickUpLong = $element.find('.leadTimeFlyout .pickupLong');
                        if ($pickUpLong.data('pickup-long-text') !== undefined){
                            var pickpupLongText = $pickUpLong.data('pickup-long-text').replace('{0}', stockLevelPickup.stockLevel);
                            $pickUpLong.html(pickpupLongText);
                        }
                    }
                });

            }

        });

        // more bout lead link comparelist
        $moreAboutLeadLink.click(function(ev) {
            ev.preventDefault();

            //close others if they are open
            $('.leadTimeFlyout').addClass('hidden');

            var productCode = ev.delegateTarget.dataset.productCode;
            $('.leadTimeFlyout-'+productCode).toggleClass('hidden');

            return true;

        });

        $leadCloser.click(function(ev) {
            ev.preventDefault();
            var $flyout = $(ev.delegateTarget).closest('.leadTimeFlyout');
            $flyout.addClass('hidden');

            return true;
        });

    }

})(Tc.$);
