(function ($) {

    /**
     * Favorite Skin implementation for the module Toolsitem.
     *
     * @author Remo Brunschwiler
     * @namespace Tc.Module.Default
     * @class Basic
     * @extends Tc.Module
     * @constructor
     */
    Tc.Module.Toolsitem.FavoriteHomepage = function (parent) {

        /**
         * override the appropriate methods from the decorated module (ie. this.get = function()).
         * the former/original method may be called via parent.<method>()
         */
        this.on = function (callback) {
            // calling parent method
            parent.on(callback);

            // subscribe to connector channel/s
            this.sandbox.subscribe('LightboxLoginFavorites', this);
            this.sandbox.subscribe('favoritelist', this);

            this.$backendData = $('#backenddata');

            this.addOrRemoveProductToFavoriteList = $.proxy(this.addOrRemoveProductToFavoriteList, this);
            this.checkUserLoggedIn = $.proxy(this.checkUserLoggedIn, this);

            var $ctx = this.$ctx
                ,$icoFav = $('.ico', $ctx)
                ,mod = this
            ;

            $icoFav.on('click.Toolsitem.Favorite.homepage', function (e) {
                e.preventDefault();
                if(!$icoFav.hasClass('disabled')){

                    var targetUrl =  window.location.origin + '/shopping/favorite';

                    if(mod.checkUserLoggedIn()){
                        window.location.href = targetUrl;
                    }

                }
            });
        };


        this.checkUserLoggedIn = function () {
            var self = this
                ,$usersettings = $('.usersettings', self.$backendData)
            ;

            if ($usersettings.length > '0' && $usersettings.data('login').toString() === 'true') {
                return true;
            } else {
                self.fire('openModal', ['LightboxLoginFavorites']);
                return false;
            }
        };

    };


})(Tc.$);
