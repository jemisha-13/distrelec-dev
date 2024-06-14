(function($) {

    /**
     * Productlist Skin Favorite implementation for the module Productlist Order.
     *
     */
    Tc.Module.ProductCard.ProductRecommendationsCart = function (parent) {

        /**
         * override the appropriate methods from the decorated module (ie. this.get = function()).
         * the former/original method may be called via parent.<method>()
         */
            this.currentLanguage = $('#backenddata .shopsettings').data('language');

            this.on = function (callback) {
            // bind handlers to module context            
            var that = this;
            var  $carouselContainer = this.$ctx.find('.recommendations-holder'),
                 ffSearchChannel = $carouselContainer.data('ff-search-channel'),
                 ffSearchUrl = $carouselContainer.data('ff-search-url'),
                 ids = [],
                 country = $('#backenddata .shopsettings').data('country'),
                 channel = $('#backenddata .shopsettings').data('channel-label'),
                 searchExperience = $('#backenddata .shopsettings').data('search-experience');

            var dataLayer = (typeof digitalData !== 'undefined' && digitalData) ?
                ((typeof digitalData.cart !== 'undefined' && digitalData.cart && typeof digitalData.cart.item !== 'undefined' && digitalData.cart.item) ? digitalData.cart.item
                    : ((typeof digitalData.product !== 'undefined' && digitalData.product) ? digitalData.product : []))
                : [];

            $.each(dataLayer, function (index, product) {
                ids.push(product.productInfo.productID);
            });
            
            var productIDs = [];
            var urlFinal;
            if (searchExperience === 'factfinder') {
                $.each(ids, function(i, data){
                    productIDs.push('id=' + data + '&');
                });
    
                var productString;
    
                if(productIDs.length === 0 || productIDs === undefined) {
                     productString =  'id=15519041';
                } else {
                     productString =  productIDs.join("").toString();
                }
    
                urlFinal = ffSearchUrl+'?do=getRecommendation&channel='+ ffSearchChannel+'&format=json&maxResults=8&'+productString;
            }

            var accessoriesVm = new Vue({
                el: '#app',
                data: { items : [] },

                created: function () {
                    var self = this;
                    if((searchExperience !== 'factfinder')) {
                        $('.skin-product-card-product-recommendations-cart').addClass('hidden');
                    }
                    else {
                        axios.get(urlFinal).then(function (response) {
                            self.items = response.data.resultRecords;
                            
                            if(self.items === undefined || self.items.length === 0 ) {
                                $('.skin-product-card-product-recommendations-cart').addClass('hidden');
                            }
                            createCartRecommendationCarousel();
                        });
                    }

                },
                methods: {
                    addCart: function(e) {
                        e.preventDefault();

                        var productCode = e.target.getAttribute('data-product-id');
                        var element = e.target;

                        // Trigger Cart API to add to cart
                        $(document).trigger('cart', {
                            actionIdentifier: 'carpetAddToCart',
                            type: 'add',
                            productCodePost: productCode,
                            qty: 0 // backend magic: we send 0 and the backend automatically set it to the minimum quantity
                        });

                        if(element.classList.contains('reloadPage') && window.location.pathname === '/cart'){
                            setTimeout(function(){
                                location.reload();
                            }, 1000);
                        }

                    },
                    transformDataArray: function(items){
                      var transformedArray = [];          
                      items.forEach(function(element,index) {
                        transformedArray.push(
                          {
                            id: element.productNumber,
                            record: {
                              ProductNumber: element.productNumber,
                              ProductNumberElfa: element.productNumber,
                              Manufacturer: element.manufacturer,
                              energyEfficiencyData: element.energyEfficiency,
                              erpCode: element.productNumber,
                              itemPositionOneBased: null,
                              Title: element.title,
                              ItemsMin: element.itemMin,
                              ItemsStep: element.itemStep,
                              originalPackSize:'',
                              productImageASltText: element.title,
                              ImageURL: element.imageURL,
                              promoLabelCompensateClass: null,
                              promotiontext: null,
                              salesUnit:'',
                              showCarouselItemHead:'',
                              TypeName: element.typeName,
                              ProductURL: element.productUrl
                            }
                          });
                        var price = $('.shopsettings').data('channel-label') === 'B2C' ? element.singleMinPriceGross : element.singleMinPriceNet;
                        var priceParts = price.toString().split('.');
          
                        if (priceParts.length === 1) {
                            priceParts[1] = '00';
                        }
                        if (priceParts[0].length === 0) {
                            priceParts[0] = '0';
                        }
                        if (priceParts[1].length < 2) {
                            priceParts[1] += '00'.substring(priceParts[1].length);
                        }
                        transformedArray[index].record.singleMinPrice = priceParts.join('.');
                        
                        if (priceParts[1].length > 4) {
                            transformedArray[index].record.singleMinPrice = parseFloat(transformedArray[index].record.singleMinPrice).toFixed(4);
                        }
                        if(country.toLowerCase() === 'se'){
                            transformedArray[index].record.singleMinPrice = transformedArray[index].record.singleMinPrice.replace('.',',');
                          } 
                      });
                      return transformedArray;
                    }
                }
            });

            $( window ).resize(function() {
                createCartRecommendationCarousel();
            });

            function createCartRecommendationCarousel() {

                setTimeout(function(){
                    // Lazy Load caroufredsel
                    if ($(window).width() > 768 ) {

                        var liMaxHeight = -1,
                            node,
                            $cartTitle = $(".cart-page-recommendations .card-item__title");

                        $cartTitle.each(function(index) {
                            if ($(this).outerHeight() > liMaxHeight) {
                                liMaxHeight = $(this).outerHeight();
                                node = index;
                            }
                        });

                        $cartTitle.height(liMaxHeight);

                        Modernizr.load([{
                            load: '/_ui/all/cache/dyn-jquery-caroufredsel.min.js',
                            complete: function () {

                                $('.recommendations-holder ul').carouFredSel({
                                    responsive: true,
                                    width: '100%',
                                    scroll : {
                                        items         : 1,
                                        pauseOnHover  : true
                                    },
                                    items: {
                                        visible: {
                                            min: 4
                                        }
                                    },
                                    prev: '#recommendations-holder-prev',
                                    next: '#recommendations-holder-next',
                                    onCreate : function () {
                                        $(".recommendations-holder-nav").removeClass('hidden');
                                    }
                                });
                            }
                        }]);
                    }
                }, 1000);
            }
            parent.on(callback);
        };
    };
})(Tc.$);


