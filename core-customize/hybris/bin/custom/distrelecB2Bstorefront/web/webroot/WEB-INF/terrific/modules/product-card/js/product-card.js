(function ($) {

    /**
     * Module implementation.
     *
     * @namespace Tc.Module
     * @class Product-image-gallery
     * @extends Tc.Module
     */
    Tc.Module.ProductCard = Tc.Module.extend({

        /**
         * Hook function to do all of your module stuff.
         *
         * @method on
         * @param {Function} callback function
         * @return void
         */
        on: function (callback) {

            function UpdateStockValue(){
                var  self = this
                    ,codeList = []
                    ,codes = $('*[class^="stock-"] .stock-id')
                    ,productNum = []
                    ,isOCI = $('.mod-layout').hasClass('isOCI-true')

                ;

                $.each(codes, function (index, code) {
                    codeList.push(code.innerHTML);
                });

                productCodes = codeList;
                var listCodes = codeList.join(',');

                // DISTRELEC-19930: improve cart speed, remove unnecessary requests
                if(!$("body").hasClass("skin-layout-cart")) {
                    if (!isOCI) {

                        axios.get('/availability?productCodes=' + listCodes + '&detailInfo=false')
                            .then(function (response){
                                var items = response.data.availabilityData.products;

                                $.each(productCodes, function (){

                                    var count = 0;
                                    var found = false;
                                    for (var item in items) {
                                        if (items[count][this.toString()] !== undefined && !found) {
                                            stockLevel = items[count][this.toString()];
                                            found = true;
                                        }
                                        count++;
                                    }

                                var productCode = this.toString();
                                $('.stock-' + productCode).find('#rightColumn2').text(stockLevel.stockLevelTotal);
                                $('.stock-' + productCode).parents('.card-item-anchor').attr('data-stock', stockLevel.stockLevelTotal);

                                });


                            });

                    } else {

                        $.ajax({
                            url : '/availability',
                            type : 'GET',
                            data: {
                                productCodes: codeList.join(','),
                                detailInfo: false
                            },
                            headers : {
                                'Accept' : 'application/json'
                            },
                            success: function(data) {
                                var items = data.availabilityData.products;

                                $.each(productCodes, function (i){

                                    var count = 0;
                                    var found = false;
                                    for (var item in items) {
                                        if (items[count][this.toString()] !== undefined && !found) {
                                            stockLevel = items[count][this.toString()];
                                            found = true;
                                        }
                                        count++;
                                    }

                                    var productCode = this.toString();
                                    $('.stock-' + productCode).next().children('#rightColumn2').text(stockLevel.stockLevelTotal);

                                });

                            }
                        });

                    }
                }



            } setTimeout(UpdateStockValue, 1000);

            $('.toggle-container__buttton').on('click', function (e) {
                e.preventDefault();
                $(this).parents('.toggle-container').find('.toggle-container__content').removeClass('hidden');
                $(this).addClass('hidden');
                return true;
            });

            callback();
        }

    });

})(Tc.$);