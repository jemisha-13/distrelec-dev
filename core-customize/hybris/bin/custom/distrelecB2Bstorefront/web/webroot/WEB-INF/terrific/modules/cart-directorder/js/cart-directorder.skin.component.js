(function ($) {
    /**
     * Module implementation.
     *
     * @namespace Tc.Module
     * @class Cart-directorder
     * @extends Tc.Module
     */

    Tc.Module.CartDirectorder.Component = function (parent) {

        this.on = function (callback) {
            // calling parent method
            parent.on(callback);

            // subscribe to connector channel/s
            this.sandbox.subscribe('lightboxShoppinglist', this);
            this.sandbox.subscribe('shoppinglist', this);
            this.sandbox.subscribe('toolsitemShopping', this);

            var self = this,
                $icoList = this.$$('.ico-list');

            $icoList.on('click', function (e) {
                e.preventDefault();

                if(!$icoList.hasClass('disabled')){
                    self.fire('checkUserLoggedIn',{}, ['lightboxShoppinglist']);
                }
            });
        };

    };

})(Tc.$);
