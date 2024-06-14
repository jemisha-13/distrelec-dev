(function ($) {

    /**
     * Module implementation.
     *
     * @namespace Tc.Module
     * @class Product-image-gallery
     * @extends Tc.Module
     */
    Tc.Module.CarouselTeaser = Tc.Module.extend({

        /**
         * Initialize.
         *
         * @method init
         * @return {void}
         * @constructor
         * @param {jQuery} $ctx the jquery context
         * @param {Sandbox} sandbox the sandbox to get the resources from
         * @param {Number} id the unique module id
         */
        init: function ($ctx, sandbox, id) {
            // call base constructor
            this._super($ctx, sandbox, id);

            // bind handlers to module context
            this.initializeModule = $.proxy(this.initializeModule, this);
            this.createCarousel = $.proxy(this.createCarousel, this);
            this.onCarouselNavClickAfter = $.proxy(this.onCarouselNavClickAfter, this);

            this.productCodes = [];// placeholder for all product-codes in list

        },

        /**
         * Hook function to do all of your module stuff.
         *
         * @method on
         * @param {Function} callback function
         * @return void
         */
        on: function (callback) {

            // subscribe to connector channel/s
            this.sandbox.subscribe('toolItems', this);

            this.initializeModule();

            var self = this;
            $('.mod-carousel-teaser-item .btn-buy').click(function(){
                self.addToCartEventTracking();
            });

            callback();
        },

        // Method is getting overriden in the module skin product-lazy-load
        initializeModule: function () {

            var self = this;

            // Lazy Load caroufredsel
            Modernizr.load([{
                load: '/_ui/all/cache/dyn-jquery-caroufredsel.min.js',
                complete: function () {
                    self.createCarousel();
                    self.getAvailabilityLazy();
                }
            }]);

        },

        createCarousel: function () {
            var mod = this
                , $carouselTeaser = this.$$('.carousel-teaser')
                , teaserTimeoutInSeconds = $carouselTeaser.data('timeout')
                , teaserAutoplay = $carouselTeaser.data('autoplay')
                , playDirection = $carouselTeaser.data('direction')
                , visibleItemsCount = $carouselTeaser.data('items-visible')
            ;

            $('.carousel-nav-product-box-vertical').removeClass('hidden');

            $carouselTeaser.carouFredSel({
                width: "100%", // Width needs to be set so that items are center aligned
                align: "left",
                circular: true,
                direction: playDirection,
                infinite: false,
                items: {
                    visible: visibleItemsCount
                },
                auto: {
                    play: teaserAutoplay,
                    items: 1,
                    timeoutDuration: teaserTimeoutInSeconds * 1000,
                    delay: teaserTimeoutInSeconds * 1000,
                    onAfter: function () {
                    } // override onAfter defined at prev/next. somehow the event gets triggered from there even if it shouldn't
                },
                prev: {
                    button: function () {
                        return mod.$$(".btn-prev");
                    },
                    items: 1,
                    onAfter: this.onCarouselNavClickAfter
                },
                next: {
                    button: function () {
                        return mod.$$(".btn-next");
                    },
                    items: 1,
                    onAfter: this.onCarouselNavClickAfter
                },
                onCreate: function (data) {
                    data.items.visible = data.items;

                    var $carouselTeaserProductBox = $('.skin-carousel-teaser-product-box-vertical');
                    var $carouselTeaserProductBoxVerticalItems = $('.skin-carousel-teaser-item-product-vertical-box');
                    var lengthCarouselTeaserProductBoxItemsVertical = $carouselTeaserProductBoxVerticalItems.length;

                    if ($carouselTeaserProductBox !== undefined) {

                        if (lengthCarouselTeaserProductBoxItemsVertical === 1) {
                            $carouselTeaserProductBox.height(200);
                        }

                        if (lengthCarouselTeaserProductBoxItemsVertical === 2) {
                            $carouselTeaserProductBox.height(350);
                        }

                        if (lengthCarouselTeaserProductBoxItemsVertical === 3) {
                            $carouselTeaserProductBox.find('.caroufredsel_wrapper').height(450);
                        }

                    }

                }

            });

        },

        addToCartEventTracking: function () {
            sessionStorage.setItem('userCartJourney', 'false');

            if ((sessionStorage.getItem("userJourney") === "tempTrue" || sessionStorage.getItem("userJourney") === "null") &&
                digitalData.page.pageInfo.search.addtocart !== undefined && sessionStorage.getItem('userCartJourney') !== 'false') {
                digitalData.page.pageInfo.search.addtocart = true;
            } else {
                digitalData.page.pageInfo.search.addtocart = false;
            }

        },

        onCarouselNavClickAfter: function (data) {
            this.$$('.carousel-teaser').trigger('configuration', ['auto', false, false]); // third param prevents scrolling DISTRELEC-3461
        },

        requestProductToggleStates: function () {
            var allCarouselElements = this.$$('.mod-carousel-teaser-item')
                , productCodesArray = []
                , mod = this
            ;

            if (allCarouselElements.length !== 0) {

                $.each(allCarouselElements, function (index, carouselElement) {
                    if ($(carouselElement).data('product-id') !== '') {
                        productCodesArray[index] = $(carouselElement).data('product-id');
                    }
                });

                $.ajax({
                    url: '/checkToggles',
                    type: 'post',
                    data: {
                        productCodes: productCodesArray
                    },
                    success: function (data, textStatus, jqXHR) {
                        mod.fire('updateToolItemStates', {products: data.products}, ['toolItems']);
                    },
                    error: function (jqXHR, textStatus, errorThrown) {
                    }
                });

            }

        },

        getAvailabilityLazy: function (data) {
            var maxTime = 4000; // 2 seconds
            var time = 0;

            var interval = setInterval(function () {
                if ($('.teaserItemProductLazy').is(':visible')) {
                    // visible, do something
                    clearInterval(interval);

                    var codeList = [];
                    var codes = $('*[class^="stock-"]');
                    var productNum = [];

                    $.each(codes, function (index, code) {
                        codeList.push(code.dataset.productcode);
                    });

                    productCodes = codeList;

                    // Perform availability request carousel
                    $.ajax({
                        url: '/availability',
                        dataType: 'json',
                        data: {
                            productCodes: codeList.join(','),
                            detailInfo: false
                        },
                        contentType: 'application/json',
                        success: function (data) {
                            var items = data.availabilityData.products,
                                item,
                                $product;

                            $.each(productCodes, function (i) {

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
                            });
                        }
                    });


                } else {
                    if (time > maxTime) {
                        // still hidden, after 4 seconds, stop checking
                        clearInterval(interval);
                        return;
                    }

                    // not visible yet, do something
                    time += 100;
                }
            }, 500);
        }

    });

})(Tc.$);
