(function($) {

    /**
     * Productlist Skin Favorite implementation for the module Productlist Order.
     *
     */
    Tc.Module.ProductCard.ProductAlternatives = function () {

        /**
         * override the appropriate methods from the decorated module (ie. this.get = function()).
         * the former/original method may be called via parent.<method>()
         */
        this.on = function (callback) {


            // bind handlers to module context

            var  $alternativesSection = $('.skin-product-card-product-alternatives'),
                 $alternativeSectionCarousel = $('.skin-product-card-product-carousel')
                ,$alternativesHolder = this.$ctx.find('.alternatives-holder')
                ,productCode = $alternativesHolder.data('product-code')
                ,$alternativesJsTemplate = $alternativesHolder.find('.alternatives-js-template')
                ,$alternativesContent = $alternativesHolder.find('.alternatives-holder__content')
                ,ajaxUrl = '/p/getAlterntaives/'+productCode
                ,alternativesData = ''
                ,alternativesDataLength = 0
                ,codeList = []
                ,isOCI = $('.mod-layout').hasClass('isOCI-true')
            ;

            if (!isOCI) {

                Vue.component('product-alternatives',{
                    template : '#product-alternatives-template',
                    props: ['index','itemcode','itempimalternatecategory','itemurl','itemproductimagealttext','itempromotiontext','itemproductimageurl','itemname','itempricecurrency','itempriceformattedvalue']
                });

                axios.get(ajaxUrl)
                    .then(function (response) {
                        alternativesData = response.data.products;
                        alternativesDataLength = alternativesData.length;

                        $('.product-alternatives-vm__list').css('opacity','0');

                        if ( alternativesDataLength > 0) {

                            $.each(alternativesData, function(order, alternative) {
                                codeList.push(alternative.code);
                            });

                            $alternativesSection.removeClass('hidden');
                            $alternativeSectionCarousel.removeClass('hidden');

                            var elements = document.getElementsByClassName('newClass');
                            for(var i = 0; i < elements.length; i++) {

                                new Vue({
                                    el: elements[i],
                                    data: {
                                        alternativesData: alternativesData
                                    }
                                });

                            }

                            var element = document.getElementsByClassName('product-alt')[0];

                            new Vue({
                                el: element,
                                data: {
                                    alternativesData: alternativesData,
                                    itemsToShow: 8
                                }
                            });



                            setTimeout(function(){

                                if ( $(window).width() < 500 ) {

                                    if ( alternativesDataLength > 1) {
                                        $('.product-alternatives-list-carousel .alternatives-holder__all-alternatives').removeClass('hidden');
                                    }


                                } else {

                                    if ( alternativesDataLength > 2) {
                                        $('.product-alternatives-list-carousel .alternatives-holder__all-alternatives').removeClass('hidden');
                                    }

                                }


                                if ( alternativesDataLength > 1) {

                                    $('.product-alternatives-list-carousel .product-alternatives-vm__list').not('.slick-initialized').slick({
                                        slidesToShow: 2,
                                        slidesToScroll: 1,
                                        centerMode: false,
                                        focusOnSelect: true,
                                        autoplay: true,
                                        prevArrow: '<span class="slick-prev"> <i class="fa fa-angle-left"></i> </span>',
                                        nextArrow: '<span class="slick-next"> <i class="fa fa-angle-right"></i> </span>',
                                        responsive: [
                                            {
                                                breakpoint: 480,
                                                settings: {
                                                    slidesToShow: 1
                                                }
                                            }
                                        ]
                                    });

                                    $('.skin-product-card-product-carousel .product-alternatives-vm__list').not('.slick-initialized').slick({
                                        slidesToShow: 2,
                                        slidesToScroll: 1,
                                        centerMode: false,
                                        focusOnSelect: true,
                                        autoplay: true,
                                        prevArrow: '<span class="slick-prev"> <i class="fa fa-angle-left"></i> </span>',
                                        nextArrow: '<span class="slick-next"> <i class="fa fa-angle-right"></i> </span>',
                                        responsive: [
                                            {
                                                breakpoint: 480,
                                                settings: {
                                                    slidesToShow: 1
                                                }
                                            }
                                        ]
                                    });

                                }

                                $('.product-alternatives-vm__list').css('opacity','1');

                            }, 1000);

                        }

                    });

            }

            setTimeout(function(){
                $('.card-item-anchor').click(function(){
                    sessionStorage.setItem('userJourney', 'false');
                });
            }, 3000);


            callback();
        };

    };

})(Tc.$);


