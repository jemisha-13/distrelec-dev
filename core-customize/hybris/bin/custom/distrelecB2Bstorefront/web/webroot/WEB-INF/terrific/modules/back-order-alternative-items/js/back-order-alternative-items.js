(function ($) {

    /**
     * Module implementation.
     *
     * @namespace Tc.Module
     * @class Product-image-gallery
     * @extends Tc.Module
     */
    Tc.Module.BackOrderAlternativeItems = Tc.Module.extend({

        /**
         * Hook function to do all of your module stuff.
         *
         * @method on
         * @param {Function} callback function
         * @return void
         */
        on: function (callback) {
            var self = this;

            $(".show-alt").on('click', function(e){
                e.preventDefault();
                e.stopPropagation();
                e.stopImmediatePropagation();


                var baseUrl = window.location.origin,
                    productUrl = $(this).attr('data-bourl');

                entryNumber = $(this).attr('data-entry');       //this needs to be global to access other places
                ajaxUrl = baseUrl + productUrl;
                vueID = '#app' + entryNumber;

                $('.mod-back-order-item__alternative--' + entryNumber).toggleClass('mod-back-order-item__alternative--active');

                $(this).toggleClass('active');
                if($(this).hasClass('active')) {
                    $(this).children('.btn-show-text').addClass('hidden');
                    $(this).children('.btn-hide-text').removeClass('hidden');
                    $(this).attr('data-aabutton', 'open');
                }else {
                    $(this).children('.btn-hide-text').addClass('hidden');
                    $(this).children('.btn-show-text').removeClass('hidden');
                    $(this).attr('data-aabutton', 'close');
                }

                if(!$(this).hasClass('activeVue')) {
                    self.getAlternative(ajaxUrl,vueID);
                }

                $(this).addClass('activeVue');

            });

            callback();
        },

        getAlternative: function(ajaxUrl, vueID){

            function UpdateStockValue(){
                var  self = this
                    ,codeList = []
                    ,codes = $('*[class^="stock-"] .stock-id')
                    ,productNum = []
                ;

                $.each(codes, function (index, code) {
                    codeList.push(code.innerHTML);
                });

                productCodes = codeList;
                var listCodes = codeList.join(',');

                axios.get('/availability?productCodes=' + listCodes + '&detailInfo=false')
                    .then(function (response){
                        var items = response.data.availabilityData.products;
                        var count = 0;
                        // Iteration over each product code for alternative items
                        $.each(productCodes,  function (){
                            // Declaration of current product code
                            var productCode = this.toString();
                            // Removal of all hyphens (-) within the product code
                            productCodeFormatted = productCode.replaceAll('-','');
                            // Declaration for each product by product code
                            product = items[count][productCodeFormatted];
                            // Declaration for the stockLevelTotal and converted to a string value
                            stockLevelTotal=product.stockLevelTotal.toString();

                            // Appending the stockLevelTotal onto the product
                            $('.stock-' + productCode).find('#rightColumn2').text(stockLevelTotal);

                            count++;
                        });
                    });
            } setTimeout(UpdateStockValue, 2000);

            var accessoriesVm = new Vue({
                el: vueID,
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

                    axios.get(ajaxUrl)
                        .then(function (response) {
                            self.items = response.data.carouselData;

                            $.each(self.items, function (index, item) {

                                var erpCode = self.items[index].code;

                                if (erpCode.length == 8) {
                                    erpCode = erpCode.slice(0,3) + '-' + erpCode.slice(3,5) + '-' + erpCode.slice(5,8);
                                    self.items[index].code = erpCode;
                                }

                            });
                            
                            $('#spinnerWrapper' + entryNumber).hide();

                        });

                },

                methods: {
                    addCart: function(e) {
                        e.preventDefault();
                        var element =  (e.srcElement.tagName === "I" ? e.target : e.target.querySelector('i'));
                        var productCode = element.getAttribute('data-product-id');
                        var cartQty = element.parentElement.previousElementSibling.childNodes[4].value;

                        this.cartObject = '{';
                        this.cartObject  += '"productCode":"' + productCode + '",';
                        this.cartObject  += '"quantity":' + cartQty;
                        this.cartObject += '}';
                        this.cartArray.push(this.cartObject);

                        $(element).removeClass('fa-cart-plus');
                        $(element).addClass('fa-check-circle');

                        setTimeout(function(){
                            $(element).removeClass('fa-check-circle');
                            $(element).addClass('fa-cart-plus');
                        }, 1500);

                        var responseDynamic = $('#hiddenResponse' + element.getAttribute('data-cart-count'));
                        responseDynamic.html(this.cartArray.join(", "));

                    },


                    preSetValue: function() {

                        setTimeout(function(){

                            $('.item-input').each(function(i,v){

                                if($(v).val() < $(v).attr('data-min-qty')) {
                                    $(v).val($(v).attr('data-min-qty'));
                                }

                            });

                        }, 1500);

                    },

                    modalDownload: function(e) {
                        var productCode = e.target.className;
                        var self = this;

                        axios.get('/checkout/backorderDetails/getProductDetails/' + productCode)
                            .then(function (response) {
                               self.download = JSON.parse(response.data);
                               self.downloadManufacturer = self.download.distManufacturer;
                               self.countryOrigin = self.download.countryOfOrigin;
                               self.productDescription = self.download.productInformation;
                               self.titleDescription = self.download.productInformation.familyDescriptionBullets;
                               self.productImage = self.download.productImages[0].landscape_medium;
                               self.seriesDescription = self.download.productInformation.seriesDescriptionBullets;
                               self.familyDescription = self.download.productInformation.familyDescription;

                               if(self.download.distManufacturer.image !== null) {
                                 self.productImageBrand = self.download.distManufacturer.image.brand_logo;
                               }

                            });

                        axios.get('/p/' + productCode + '/downloads')
                            .then(function (response) {
                                self.pdfDownload = response.data;
                            });

                    },

                    increment: function(response) {
                        this.$forceUpdate();

                        var codeForRef = response.code;
                        var currentQty = parseInt(this.$refs[codeForRef][0].value, 10);

                        currentQty += response.orderQuantityStep;

                        this.$refs[codeForRef][0].value = currentQty;
                        this.$refs[codeForRef][0].previousElementSibling.previousElementSibling.classList.add('hidden');
                    },

                    decrement: function(response) {
                        var codeForRef = response.code;
                        var currentQty = parseInt(this.$refs[codeForRef][0].value, 10);

                        currentQty -= response.orderQuantityStep;

                        this.$refs[codeForRef][0].value = currentQty;

                        if(currentQty < response.orderQuantityMinimum) {
                            var item = this.$refs[codeForRef][0].previousElementSibling.previousElementSibling.children[1];
                            var text = item.innerHTML.replace('{0}', codeForRef).replace('{1}', response.orderQuantityMinimum);

                            item.innerHTML = text;

                            this.$refs[codeForRef][0].previousElementSibling.previousElementSibling.classList.remove('hidden');
                            this.$refs[codeForRef][0].value = response.orderQuantityMinimum;
                        }

                    },

                    qtyBlur: function(response) {

                        var codeForRef = response.code;
                        var qtyVal =  parseInt(this.$refs[codeForRef][0].value, 10);

                        if(qtyVal < response.orderQuantityMinimum) {
                            this.$refs[codeForRef][0].value = response.orderQuantityMinimum;

                            var item = this.$refs[codeForRef][0].previousElementSibling.previousElementSibling.children[1];
                            var text = item.innerHTML.replace('{0}', codeForRef).replace('{1}', response.orderQuantityMinimum);

                            item.innerHTML = text;

                            this.$refs[codeForRef][0].previousElementSibling.previousElementSibling.classList.remove('hidden');

                        } else {
                            this.$refs[codeForRef][0].previousElementSibling.previousElementSibling.classList.add('hidden');
                        }

                        if(this.$refs[codeForRef][0].value === '') {
                            this.$refs[codeForRef][0].value = response.orderQuantityMinimum;
                        }

                    }
                },

                mounted: function() {
                    this.preSetValue();
                }

            });
        }

    });

})(Tc.$);