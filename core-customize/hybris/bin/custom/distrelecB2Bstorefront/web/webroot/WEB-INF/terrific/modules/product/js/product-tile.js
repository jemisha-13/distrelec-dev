(function ($) {

    setTimeout(function(){
        checkInStock();
    }, 1000);

    function checkInStock() {

        if ($('.product__tile').length > 0) {

            var productStockVm = new Vue({
                el: '.product__tile',
                data: {
                    availabilityProducts: [],
                    codeList: [],
                    codes: $('*[id*="stock--"]'),
                    productCodes: []
                },
                created: function () {
                    var self = this;

                    $.each(self.codes, function (index, code) {
                        self.codeList.push(code.dataset.productcode);
                    });

                    self.productCodes = self.codeList;
                    
                    axios
                        .get('/availability', {
                            params: {
                                productCodes: self.codeList.join(','),
                                detailInfo: false
                            }
                        })
                        .then(function (response) {
                            self.availabilityProducts = response.data.availabilityData.products;

                            for (var i = 0; i < self.availabilityProducts.length; i++) {
                                var _ele = 'stock--' + [self.productCodes[i]] ;
                                document.getElementById('' + _ele).innerHTML = self.availabilityProducts[i][self.productCodes[i]].stockLevelTotal;
                            }

                        });

                }

            });

        }
    }

})(Tc.$);
