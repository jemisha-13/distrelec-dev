(function ($) {

    /**
     * Product Skin Bom implementation for the module Productlist Order.
     *
     */
    Tc.Module.MetahdItem.CartCheckout = function (parent) {


        this.on = function (callback) {

            this.$popover = null;
            this.popoverTimer = null;
            this.popoverHideDelay = 4000;

            this.onCartChange = $.proxy(this.onCartChange, this);
            this.hidePopover = $.proxy(this.hidePopover, this);

            this.tmplCartIsEmpty = doT.template($('#template-metahd-item-cart-is-empty', this.$ctx).html());
            this.tmplProductItem = doT.template($('#template-metahd-item-cart-product-item', this.$ctx).html());

            $(document).on('cartDataAvailable', $.proxy(this.updateMiniCart, this));

            parent.on(callback);
        };

        this.updateMiniCart = function () {
            $('.minicart__items__count span').html(window._cart.totalItems);
            $('.minicart__price__currency').html(window._cart.currency);
            $('.minicart__price__figure').html(window._cart.totalPrice);
        };

    };

})(Tc.$);
