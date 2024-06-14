(function ($) {

    /**
     * Module implementation.
     *
     * @namespace Tc.Module
     * @class Product-image-gallery
     * @extends Tc.Module
     */
    Tc.Module.BomNoAvailable = Tc.Module.extend({

        init: function($ctx, sandbox, id) {
            // call base constructor
            this._super($ctx, sandbox, id);

            // set module variables
            this.$modal = $('.modal', $ctx);

        },

        /**
         * Hook function to do all of your module stuff.
         *
         * @method on
         * @param {Function} callback function
         * @return void
         */
        on: function (callback) {
            var self = this
                ,$ctx = this.$ctx
                ,productCode = ''
                ,ajaxUrl = ''
                ,alternativesData = ''
                ,alternativesDataLength = 0
                ,codeList = []
                ,$btnShowHideAlternative = $(".bom-unavailable-product__show-alternative")
                ,$showText = $btnShowHideAlternative.data('show-text')
                ,$changeText = $btnShowHideAlternative.data('change-text')
                ,$hideText = $btnShowHideAlternative.data('hide-text')
                ,isOCI = $('.mod-layout').hasClass('isOCI-true'),
                $bomItemViewDetail =  this.$$('.bom-unavailable-product__view-detail'),
                $modalViewDetail = $('.bom-unavailable-product__modal', $ctx);

            $(".bom-unavailable-product__show-alternative").on('click', function(e){
                var $alternativeContent = $(this).parents('.bom-unavailable-product').find('.bom-unavailable-product__alternatives'),
                $alternativeSelected = $(this).parents('.bom-unavailable-product').find('.bom-unavailable-product__alternatives-selection'),
                $alternativeProdInfo = $(this).parents('.bom-unavailable-product').find(".bom-unavailable-product__info");

                $alternativeContent.toggleClass('hidden');

                if ( $alternativeContent.hasClass('hidden') ) {

                    if ( $alternativeProdInfo.hasClass('selected') ) {
                        $(this).html($changeText);
                        $(this).attr('data-aabutton', $(this).data('aabuttonchange'));
                    } else {
                        $(this).html($showText);
                        $(this).attr('data-aabutton', $(this).data('aabuttonshow'));
                    }

                    if ( $alternativeSelected.hasClass('selected') ) {
                        $alternativeSelected.removeClass('hidden');
                    }

                } else {

                    $(this).html($hideText);
                    $(this).attr('data-aabutton', $(this).data('aabuttonhide'));

                    if ( $alternativeSelected.hasClass('selected') ) {
                        $alternativeSelected.addClass('hidden');
                    }

                }

            });

            $('.bom-unavailable-product__alternatives-selection .ico-list-rm').click(function(){

                var $item = $(this).parents('.bom-unavailable-product__alternatives-selection'),
                    $btnShowHideAlternative = $(this).parents('.bom-unavailable-product').find('.bom-unavailable-product__show-alternative'),
                    $infoTitle = $(this).parents('.bom-unavailable-product').find('.bom-unavailable-product__info'),
                    $productcode = $(this).data('product-code'),
                    $infomsg = $(this).parents('.bom-unavailable-product').find('.skin-global-messages-component');
                    $currentSelectdItem = $('.bom-unavailable-product__item--'+$productcode),
                    $btnSelect = $currentSelectdItem.find('.bom-unavailable-product__alternatives-select');

                $infoTitle.removeClass('selected');
                $infomsg.removeClass('hidden');
                $currentSelectdItem.removeClass('selected');
                $btnSelect.removeClass('disabled').removeAttr('disabled');
                $item.removeClass('selected');
                $btnShowHideAlternative.html($showText);
                $btnShowHideAlternative.data('aabutton',$btnShowHideAlternative.data('aabuttonshow'));

            });

            $(".bom-unavailable-product__alternatives-select").on('click', function(e){
                var productcode = $(this).data('product-code'),
                    $alternativeSelected = $(this).parents('.bom-unavailable-product').find('.bom-unavailable-product__alternatives-selection'),
                    $infomsg = $(this).parents('.bom-unavailable-product').find('.skin-global-messages-component'),
                    $btnSaveAllProductToShoppingList = $('.mod-bom-toolbar .btn-add-shopping'),
                    $btnSaveAllProductToCartList = $('.mod-bom-toolbar .btn-add-cart'),
                    $showAlternative = $(this).parents('.bom-unavailable-product').find(".bom-unavailable-product__show-alternative"),
                    $alternativeProdInfo = $(this).parents('.bom-unavailable-product').find(".bom-unavailable-product__info"),
                    $alternativeProdItem = $(this).parents('.bom-unavailable-product').find(".bom-unavailable-product__item"),
                    $alternativeProdAlternatives = $(this).parents('.bom-unavailable-product').find(".bom-unavailable-product__alternatives"),
                    $alternativeProdItemCode = $(this).parents('.bom-unavailable-product').find(".bom-unavailable-product__item--" +productcode),
                    $allAlternativeSelect = $(this).parents('.bom-unavailable-product').find(".bom-unavailable-product__alternatives-select");

                $allAlternativeSelect.removeClass('disabled').removeAttr('disabled');
                $(this).addClass('disabled').attr('disabled','disabled');
                $alternativeProdItem.removeClass('selected');
                $alternativeProdItemCode.addClass('selected');
                $alternativeProdAlternatives.addClass('hidden');

                $alternativeProdInfo.addClass('selected');
                $showAlternative.html($changeText);
                $showAlternative.attr('data-aabutton', $showAlternative.data('aabuttonchange'));

                $alternativeSelected.removeClass('hidden');
                $('.bom-unavailable-product__alternatives-selection.selected .skin-product-bom').addClass('active');

                if ( $alternativeSelected.hasClass('selected') ) {
                    $alternativeSelected.removeClass('hidden');
                    $infomsg.addClass('hidden');
                    $btnSaveAllProductToShoppingList.removeAttr('disabled', 'disabled');
                    $btnSaveAllProductToCartList.removeAttr('disabled', 'disabled');
                }

                var $alternativeProductLength = $('.bom-unavailable-product__alternatives-selection.selected .skin-product-bom').length,
                    $mpnProductLength = $('.bom-mpn-product__alternatives-selection.selected .skin-product-bom').length,
                    $matchedProductLength = $('.productlist .skin-product-bom:not(.skin-product-not-buyable)').length,
                    $productLength = $alternativeProductLength + $mpnProductLength + $matchedProductLength,
                    $alternativeProductSelected = $('.bom-unavailable-product__alternatives-selection.selected .skin-product-bom.active').length,
                    $mpnProductSelected = $('.bom-mpn-product__alternatives-selection.selected .skin-product-bom.active').length,
                    $matchedProductSelected  = $('.productlist .skin-product-bom.active').length,
                    $selectedProductLength = $alternativeProductSelected + $mpnProductSelected + $matchedProductSelected,
                    $allProductSelection = $('.bom-product-controllbar__select-all input');

                if ( $productLength === $selectedProductLength && $selectedProductLength > 0 ) {
                    $allProductSelection.prop( "checked", true );
                    $allProductSelection.parent().addClass('active');
                } else {
                    $allProductSelection.prop( "checked", false );
                    $allProductSelection.parent().removeClass('active');
                }

            });

            $bomItemViewDetail.on("click", function(e) {
                e.preventDefault();

                var self = $(this),
                    $productcode = $(this).data('product-code'),
                    $btnViewDetail = $('#viewdetails-'+$productcode);

                if ( ! $btnViewDetail.hasClass('vue-added') ) {

                    var vueDetailVm = new Vue({
                        el: '#modal-'+$productcode,
                        data: {
                            items: [],
                            cartArray: [],
                            cartObject: {},
                            parentProduct: {},
                            parentProductArray: [],
                            parentProductQty: [],
                            download: [],
                            downloadManufacturer: [],
                            countryOrigin: [],
                            productDescription: [],
                            titleDescription: [],
                            pdfDownload: [],
                            pdfLanguages: [],
                            productImage: [],
                            productImageBrand: [],
                            seriesDescription: [],
                            familyDescription: [],
                            pdfDownloads: []
                        },

                        created: function () {
                            var self = this;

                            axios.get('/checkout/backorderDetails/getProductDetails/' + $productcode)
                                .then(function (response) {
                                    self.download = JSON.parse(response.data);
                                    self.downloadManufacturer = self.download.distManufacturer;
                                    self.countryOrigin = self.download.countryOfOrigin;
                                    self.productDescription = self.download.productInformation;

                                    self.productImage = self.download.productImages[0].landscape_medium;
                                    self.seriesDescription = self.download.productInformation.seriesDescriptionBullets;
                                    self.familyDescription = self.download.productInformation.familyDescription;

                                    if(self.titleDescription !== null) {
                                        self.titleDescription = self.download.productInformation.familyDescriptionBullets;
                                    }

                                    if(self.download.distManufacturer.image !== null) {
                                        self.productImageBrand = self.download.distManufacturer.image.brand_logo;
                                    }

                                });

                            $btnViewDetail.addClass('vue-added');

                        }

                    });

                }

                return true;
            });


            callback();
        }

    });

})(Tc.$);