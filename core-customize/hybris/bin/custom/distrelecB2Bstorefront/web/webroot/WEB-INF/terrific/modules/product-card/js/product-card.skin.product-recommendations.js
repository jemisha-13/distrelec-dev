(function($) {

    /**
     * Productlist Skin Favorite implementation for the module Productlist Order.
     *
     */
    Tc.Module.ProductCard.ProductRecommendations = function (parent) {

        /**
         * override the appropriate methods from the decorated module (ie. this.get = function()).
         * the former/original method may be called via parent.<method>()
         */
        this.on = function (callback) {

            // bind handlers to module context

            var  $carouselContainer = this.$ctx.find('.recommendations-holder')
                ,ffSearchChannel = $carouselContainer.data('ff-search-channel')
                ,ffSearchUrl = $carouselContainer.data('ff-search-url')
                ,productCodeErp = $carouselContainer.data('ff-producterp')
            ;

            var accessoriesVm = new Vue({
                el: '#app',
                data: {
                    items : []
                },

                created: function () {
                    var self = this;
                    axios.get(ffSearchUrl+'?maxResults=4&id='+ productCodeErp +'&channel='+ ffSearchChannel + '&format=json&do=getRecommendation')
                        .then(function (response) {
                            self.items = response.data.resultRecords;

                            if(self.items === undefined ) {
                                $('.skin-product-card-product-recommendations').addClass('hidden');
                            }

                        });

                }
            });

            parent.on(callback);
        };


    };

})(Tc.$);


