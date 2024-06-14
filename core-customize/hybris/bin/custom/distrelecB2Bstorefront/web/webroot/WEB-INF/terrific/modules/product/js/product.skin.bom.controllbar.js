(function($) {

    /**
     * Product Skin Bom implementation for the module Productlist Order.
     *
     */
    Tc.Module.Product.BomControllbar = function (parent) {

        this.on = function (callback) {

            var self = this;

            var $bomItemCheckbox =  $('.bom-product__checkbox'),
                $bomToggleView = $('.bom-product-controllbar__onoffswitch-checkbox'),
                $allProductSelectCheckbox = $('.bom-product-controllbar__select-all input');

            $bomItemCheckbox.click(function(ev){
                ev.preventDefault();
                $(this).parents('.skin-product-bom').toggleClass('active');

                self.checkProductSelection(ev,false);

                return false;
            });

            self.checkProductSelection(false, false);

            $bomToggleView.on('click', function(ev){

                var $target = $(ev.target);

                if($target.prop("checked") === true){
                    self.setBomProductListDetailView(true);
                    if (typeof(Storage) !== "undefined") {
                        localStorage.setItem("BomProductListDetailView", "true");
                    }
                    digitalData.page.pageInfo.viewType = "detailed";

                } else {
                    self.setBomProductListDetailView(false);
                    if (typeof(Storage) !== "undefined") {
                        localStorage.setItem("BomProductListDetailView", "false");
                    }
                    digitalData.page.pageInfo.viewType = "compact";
                }

            });

            $allProductSelectCheckbox.on('click', function(ev){
              var $target = $(ev.target),
                  $alternativeProduct = $('.bom-unavailable-product__alternatives-selection.selected .skin-product-bom'),
                  $mpnProduct = $(".bom-mpn-product__alternatives-selection.selected .skin-product-bom"),
                  $matchedProduct = $('.productlist .skin-product-bom');

                if($target.prop("checked") === true){
                    $target.parent().addClass('active');
                    $alternativeProduct.addClass('active');
                    $mpnProduct.addClass("active");
                    $matchedProduct.addClass('active');
                    setTimeout(function() {
                        self.checkProductSelection(ev,true);
                    }, 100);
                } else {
                    $target.parent().removeClass('active');
                    $alternativeProduct.removeClass('active');
                    $mpnProduct.removeClass("active");
                    $matchedProduct.removeClass('active');
                    setTimeout(function() {
                        self.checkProductSelection(ev,false);
                    }, 100);
                }

                return true;
            });

            if (typeof(Storage) !== "undefined") {
                var bomProductListDetailView = localStorage.getItem("BomProductListDetailView");

                if ( bomProductListDetailView == 'true') {
                    self.setBomProductListDetailView(true);
                } else {
                    self.setBomProductListDetailView(false);
                }

            }

            parent.on(callback);
        };

        this.setBomProductListDetailView = function (_bomProductListView) {

            if(_bomProductListView){
                $('.bom-product__detail-view').addClass('showdetailview');
                $('.bom-product-controllbar__onoffswitch-switch').addClass('showdetailview');
                digitalData.page.pageInfo.viewType="detailed view";
            } else {
                $('.bom-product__detail-view').removeClass('showdetailview');
                $('.bom-product-controllbar__onoffswitch-switch').removeClass('showdetailview');
                digitalData.page.pageInfo.viewType="compact view";
            }

        };

        this.checkProductSelection = function (ev,_allProductSelectCheckbox) {
            if (!!ev) {
                ev.preventDefault();
            }

            var $alternativeProductLength = $('.bom-unavailable-product__alternatives-selection.selected .skin-product-bom').length,
                $mpnProductLength = $('.bom-mpn-product__alternatives-selection.selected .skin-product-bom').length,
                $matchedProductLength = $('.productlist .skin-product-bom:not(.skin-product-not-buyable)').length,
                $productLength = $alternativeProductLength + $mpnProductLength + $matchedProductLength,
                $alternativeProductSelected = $('.bom-unavailable-product__alternatives-selection.selected .skin-product-bom.active').length,
                $mpnProductSelected = $('.bom-mpn-product__alternatives-selection.selected .skin-product-bom.active').length,
                $matchedProductSelected  = $('.productlist .skin-product-bom.active').length,
                $selectedProductLength = $alternativeProductSelected + $mpnProductSelected + $matchedProductSelected,
                $btnSaveAllProductToShoppingList = $('.mod-bom-toolbar .btn-add-shopping'),
                $btnSaveAllProductToCartList = $('.mod-bom-toolbar .btn-add-cart'),
                $allProductSelection = $('.bom-product-controllbar__select-all input'),
                $allBomProducts = $('.skin-product-bom'),
                $selectedBomProducts = $('.skin-product-bom.active'),
                isQuantiryError = false,
                $errorContainer= $('.skin-global-messages-component.bom'),
                $errorContent= $('.skin-global-messages-component.bom .messages-component__label'),
                $errorMessage = '';

            $.each($selectedBomProducts, function (index, item) {

               if ( $(item).find('.numeric').hasClass('numeric-error') ) {
                   isQuantiryError = true;
                   $(item).addClass('error');

                   var message = $(item).find('.popover-content').html();
                   $errorMessage = $errorMessage + message +'</br>';

               } else {
                    $(item).removeClass('error');
               }

            });

            if (isQuantiryError) {
                $($errorContent).html( $errorMessage );
                $errorContainer.removeClass('hidden');
            } else {
                $errorContainer.addClass('hidden');
            }

            if ( !_allProductSelectCheckbox ) {

                if ( $productLength === $selectedProductLength && $selectedProductLength > 0 ) {
                    $allProductSelection.prop( "checked", true );
                    $allProductSelection.parent().addClass('active');
                } else {
                    $allProductSelection.prop( "checked", false );
                    $allProductSelection.parent().removeClass('active');
                }

            }

            if ( $selectedProductLength > 0 && !isQuantiryError) {
                $btnSaveAllProductToShoppingList.removeAttr('disabled', 'disabled');
                $btnSaveAllProductToCartList.removeAttr('disabled', 'disabled');
            } else {
                $btnSaveAllProductToShoppingList.attr('disabled', 'disabled');
                $btnSaveAllProductToCartList.attr('disabled', 'disabled');
            }

            return true;

        };

    };

})(Tc.$);

