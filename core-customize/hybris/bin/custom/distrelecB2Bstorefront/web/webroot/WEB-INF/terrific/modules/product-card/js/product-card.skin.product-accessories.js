(function($) {

    /**
     * Productlist Skin Favorite implementation for the module Productlist Order.
     *
     */
    Tc.Module.ProductCard.ProductAccessories = function (parent) {

        /**
         * override the appropriate methods from the decorated module (ie. this.get = function()).
         * the former/original method may be called via parent.<method>()
         */
        this.on = function (callback) {

            // bind handlers to module context

            var  $carouselContainer = this.$ctx.find('.accessories-holder')
                ,productUrl = Tc.Utils.splitUrl(document.URL)
                ,ajaxUrlPostfix = $carouselContainer.data('ajax-url-postfix')
                ,actionUrl = $carouselContainer.data('action-url')
                ,ajaxUrl = actionUrl+ajaxUrlPostfix
            ;

            var replacementVm = new Vue({
                el: '#accessories',
                data: {
                    items : [],
                    itemsToShow: 8
                },

                methods: {

                    setStorage: function() {
                        sessionStorage.setItem('userJourney', 'false');
                    }

                },

                created: function () {
                    var self = this;
                    axios.get(ajaxUrl)
                        .then(function (response) {
                            self.items = response.data.products;

                            if(self.items.length === 0 ) {
                                $('.skin-product-card-product-accessories').addClass('hidden');
                            }

                            for(var i = 0; i < self.itemsToShow; i ++) {

                                try {
                                    if(self.items[i].productImageUrl.indexOf('amp;') !== -1) {
                                        self.items[i].productImageUrl = self.items[i].productImageUrl.replace(new RegExp('amp;', 'g'),'');
                                    }
                                }catch(e) {
                                    return e;
                                }

                            }

                        });

                }
            });

            parent.on(callback);
        };


    };

})(Tc.$);


