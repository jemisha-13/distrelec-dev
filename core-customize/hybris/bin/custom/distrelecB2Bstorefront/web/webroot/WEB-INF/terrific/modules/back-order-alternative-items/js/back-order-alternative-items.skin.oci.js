(function ($) {

    /**
     * Module implementation.
     *
     * @namespace Tc.Module
     * @class Product-image-gallery
     * @extends Tc.Module
     */

    Tc.Module.BackOrderAlternativeItems.Oci = function (parent) {

        /**
         * Hook function to do all of your module stuff.
         *
         * @method on
         * @param {Function} callback function
         * @return void
         */
        this.on = function (callback) {

            var self = this;

             dataMethods = {
                items: [],
                cartArray: [],
                cartObject: {},
                parentProduct: {},
                parentProductArray: [],
                parentProductQty: [],
                download: [],
                language: [],
                qtyStep: [],
                qtyMin: []
            };

            $(document).on("click",".show-alt",function(e) {
                e.preventDefault();
                e.stopPropagation();
                e.stopImmediatePropagation();

                if (!window.location.origin) {
                    window.location.origin = window.location.protocol + "//" + window.location.hostname + (window.location.port ? ':' + window.location.port: '');
                }

                var baseUrl = window.location.origin,
                    productUrl = $(this).attr('data-url');

                entryNumber = $(this).attr('data-entry');       //this needs to be global to access other places
                ajaxUrl = baseUrl + productUrl;

                parentProductItem = $(this).parent().parent().siblings('.mod-back-order-item__alternative').children('.mod-back-order-alternative-items').children();

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
                    getAlternative(ajaxUrl);
                }

                $(this).addClass('activeVue');
            });

            function presetValue() {

                $.each($('.inputResponse'), function(i,v){

                    if($(v).val() < $(v).attr('data-qtyMin')) {
                        $(v).val($(v).attr('data-qtyMin'));
                    }

                });

            }

            function getAlternative(ajaxUrl) {

                function mapItems(response) {
                    $.each($(parentProductItem), function(i,v) {

                        if(response[i] !== undefined || null) {
                            $(v).children().children('.imageParent').children('img').attr('src',response[0].productImageUrl);
                            $(v).children().children('.imageParent').children('img').attr('alt',response[0].productImageAltText);
                            $(v).children().children('.itemContent').children().children('.productNameResponse').html(response[0].name);
                            $(v).children().children('.itemContent').children('.productInfo').children('.manufacturerInfo').children('.manufacturerResponse').html(' ' + response[0].distManufacturer.name);
                            $(v).children().children('.itemContent').children('.productInfo').children('.articleInfo').children('.articleNrResponse').html(' ' + response[0].code);
                            $(v).children().children('.itemContent').children('.productInfo').children('.articleInfo').children('.mpnResponse').html(' ' + response[0].typeName);
                            $(v).children().children('.itemContent').find('#itemCodePrice').html(' ' + response[0].price.formattedValue);
                            $(v).children().children('.itemContent').find('#itemCurrency').html(' ' + response[0].price.currency);
                            $(v).children().find('.inputResponse').eq(i).attr("data-qtyStep", response[i].orderQuantityStep);
                            $(v).children().find('.inputResponse').eq(i).attr('data-qtyMin', response[i].orderQuantityMinimum);
                            $(v).children().find('.stock-id').eq(i).addClass('stock-' + response[i].code);
                            $(v).children().find('.modalDownload').eq(i).attr('data-target', '#modal' + response[i].code);
                            $(v).children().find('.modalDownalodSibling').eq(i).attr('id', 'modal' + response[i].code);
                            $(v).children().find('.modalDownload').eq(i).addClass(response[i].code);
                            $(v).children().find('.modalDownload').eq(i).attr('id', response[i].code);
                            $(v).children().find('.stock-id').eq(i).html(response[i].code);
                            $(v).children().find('.addCartFN').attr('data-product-id', response[i].code);
                            $(v).children().find('.min-order-' + entryNumber).attr('data-min-qty', response[i].orderQuantityMinimum);

                            UpdateStockValue(response[i].code);

                            setTimeout(minOrderFunction, 100);


                        }
                    });
                }

                $.ajax({
                    url : ajaxUrl,
                    type : 'GET',
                    headers : {
                        'Accept' : 'application/json'
                    },
                    success : function(response) {
                        mapItems(response.carouselData);

                        presetValue();

                        dataMethods.items = response.carouselData;
                        $('#spinnerWrapper' + entryNumber).hide();
                    }
                });

                function UpdateStockValue(id) {
                    var  self = this
                        ,codeList = []
                        ,codes = $('.stock-' + id)
                        ,productNum = []
                    ;

                    $.each(codes, function (index, code) {
                        codeList.push(code.innerHTML);
                    });

                    productCodes = codeList;

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

                function minOrderFunction(){

                    var minOrder = $('*[class^="min-order-"] .min-order');

                    $.each(minOrder, function (index, minOrder) {

                        var minClass = $('.min-order-' + entryNumber);
                        var minQty =  minClass.attr('data-min-qty');
                        var qtyInnerHtml = minOrder.innerHTML;
                        var qtyReplace = qtyInnerHtml.replace("{0}", minQty);

                        minClass.find('.min-order').text(qtyReplace);

                    });

                }

                function mapItemsModal(response,self) {

                    var jQobj = $(self);

                    if (response.classifications.length >  0 ) {

                        for(var i=0; i < response.classifications.length; i++) {

                            for(var j=0; j < response.classifications[i].features.length; j++) {
                                $('.modal-content__classifications').append('<div class="classifications-item"><span class="title">'+response.classifications[i].features[j].name+'</span><span class="value">'+response.classifications[i].features[j].featureValues[0].value+'</span></div>');
                            }

                        }
                    }

                    if (response.name !== null && response.name !== undefined ) {
                        jQobj.next().find('.modalTitle').html(response.name);
                    }

                    if (response.code !== null && response.code !== undefined ) {
                        jQobj.next().find('.ArticleResponse').html(response.code);
                    }

                    if (response.typeName !== null && response.typeName !== undefined ) {
                        jQobj.next().find('.typeNameResponse').html(response.typeName);
                    }

                    if (response.distManufacturer.name !== null && response.distManufacturer.name !== undefined ) {
                        jQobj.next().find('.distManufacturerResponse').html(response.distManufacturer.name);
                    }

                    if (response.distManufacturer.image.brand_logo.url !== null && response.distManufacturer.image.brand_logo.url !== undefined ) {
                        jQobj.next().find('.imageResponseBrand').attr('src', response.distManufacturer.image.brand_logo.url);
                    }

                    if (response.distManufacturer.image.brand_logo.altText !== null && response.distManufacturer.image.brand_logo.altText !== undefined ) {
                        jQobj.next().find('.imageResponseBrand').attr('alt', response.distManufacturer.image.brand_logo.altText);
                    }

                    if (response.productImages[0].landscape_medium.url !== null && response.productImages[0].landscape_medium.url !== undefined ) {
                        jQobj.next().find('.imageResponse').attr('src', response.productImages[0].landscape_medium.url);
                    }

                    if (response.productImages[0].landscape_medium.altText !== null && response.productImages[0].landscape_medium.altText !== undefined ) {
                        jQobj.next().find('.imageResponse').attr('alt', response.productImages[0].landscape_medium.altText);
                    }

                    if (response.url !== null && response.url !== undefined ) {
                        jQobj.next().find('.downloadLink').attr('href', response.url);
                    }

                    if (response.productInformation.familyDescriptionBullets[0] !== null && response.productInformation.familyDescriptionBullets[0] !== undefined ) {
                        jQobj.next().find('.familyDescOne').removeClass('hidden');
                        jQobj.next().find('.familyDescOne').html(response.productInformation.familyDescriptionBullets[0]);
                    }

                    if (response.productInformation.familyDescriptionBullets[1] !== null && response.productInformation.familyDescriptionBullets[1] !== undefined ) {
                        jQobj.next().find('.familyDescTwo').removeClass('hidden');
                        jQobj.next().find('.familyDescTwo').html(response.productInformation.familyDescriptionBullets[1]);
                    }

                    if (response.productInformation.familyDescriptionBullets[2] !== null && response.productInformation.familyDescriptionBullets[2] !== undefined ) {
                        jQobj.next().find('.familyDescThree').removeClass('hidden');
                        jQobj.next().find('.familyDescThree').html(response.productInformation.familyDescriptionBullets[2]);
                    }

                    if (response.countryOfOrigin.name !== null && response.countryOfOrigin.name !== undefined ) {
                        jQobj.next().find('.countryOrigin').removeClass('hidden');
                        jQobj.next().find('.countryOrigin').html(' ' + response.countryOfOrigin.name + ' (' + response.countryOfOrigin.isocode + ')');
                    }

                    if (response.distManufacturer.name !== null && response.distManufacturer.name !== undefined ) {
                        jQobj.next().find('.manufacturer').removeClass('hidden');
                        jQobj.next().find('.manufacturer').html(' ' + response.distManufacturer.name);
                    }

                    if (response.grossWeight !== null && response.grossWeight !== undefined ) {
                        jQobj.next().find('.grossWeight').removeClass('hidden');
                        jQobj.next().find('.grossWeight').html(' ' + response.grossWeight );
                    }

                    if (response.grossWeightUnit !== null && response.grossWeightUnit !== undefined ) {
                        jQobj.next().find('.grossWeight').removeClass('hidden');
                        jQobj.next().find('.grossWeight').html( jQobj.next().find('.grossWeight').html()+ ' ' + response.grossWeightUnit);
                    }

                    if (response.dimensions !== null && response.dimensions !== undefined ) {
                        jQobj.next().find('.dimensions').removeClass('hidden');
                        jQobj.next().find('.dimensions').html(' ' + response.dimensions);
                    }

                    if (response.customsCode !== null && response.customsCode !== undefined ) {
                        jQobj.next().find('.customs').removeClass('hidden');
                        jQobj.next().find('.customs').html(' ' + response.customsCode);
                    }

                    if (response.unspsc5 !== null && response.unspsc5 !== undefined ) {
                        jQobj.next().find('.unspc').removeClass('hidden');
                        jQobj.next().find('.unspc').html(' ' + response.unspsc5);
                    }

                    if (response.rohs !== null && response.rohs !== undefined ) {
                        jQobj.next().find('.rohs').removeClass('hidden');
                        jQobj.next().find('.rohs').html(' ' + response.rohs);
                    }

                    if ( response.productInformation.paperCatalogPageNumber > 0 ) {
                        jQobj.next().find('.paperCatalogOne').html(' ' + response.productInformation.paperCatalogPageNumber);
                    }

                    if ( response.productInformation.paperCatalogPageNumber_16_17 > 0 ) {
                        jQobj.next().find('.paperCatalogOne').html(' ' + response.productInformation.paperCatalogPageNumber_16_17);
                    }

                }

                function mapItemsPdf(response, self) {
                    var jQobj = $(self);

                    if (response[0].downloads[0].name !== null && response[0].downloads[0].name !== undefined ) {
                        jQobj.next().find('.card-item__items--download-section').removeClass('hidden');
                        jQobj.next().find('.downloadName').html(response[0].downloads[0].name);
                    }

                    jQobj.next().find('.downloadMime').html('(' + response[0].downloads[0].mimeType);
                    jQobj.next().find('.downloadLanguage').html(' ' + response[0].downloads[0].languages[0].name + ')');
                }

                var countClicks = 0;

                $('.addCartFN').click(function(e){
                    e.preventDefault();
                    var productCode = $(this).attr('data-product-id');
                    var element = e.target;
                    var cartQty = '';
                    var htmlClass = $('html');

                    countClicks++;

                    if(htmlClass.hasClass('isie7') || htmlClass.hasClass('isie8') ){
                         var cartElement = e.target.previousSibling;
                         cartQty = cartElement.childNodes[3].value;
                    } else if (htmlClass.hasClass('ie9')) {
                         cartQty = e.target.previousElementSibling.childNodes[5].value;
                    } else {
                         cartQty = e.target.parentElement.previousElementSibling.childNodes[5].value;
                    }

                    dataMethods.cartObject = '{';
                    dataMethods.cartObject  += '"productCode":"' + productCode + '",';
                    dataMethods.cartObject  += '"quantity":' + cartQty;
                    dataMethods.cartObject += '}';
                    dataMethods.cartArray.push(dataMethods.cartObject);

                    if(htmlClass.hasClass('isie7') || htmlClass.hasClass('isie8') || htmlClass.hasClass('ie9') ){
                        var self = $(element).find('.cartIcon');

                        self.parent().addClass('ie-7');
                        self.removeClass('fa-cart-plus');
                        self.addClass('fa-check-circle');

                        setTimeout(function(){
                            self.parent().removeClass('ie-7');
                            self.removeClass('fa-check-circle');
                            self.addClass('fa-cart-plus');
                        }, 1500);
                    } else {
                        $(element).removeClass('fa-cart-plus');
                        $(element).addClass('fa-check-circle');

                        setTimeout(function(){
                            $(element).removeClass('fa-check-circle');
                            $(element).addClass('fa-cart-plus');
                        }, 1500);
                    }

                    if(countClicks !== dataMethods.cartArray.length) {
                        dataMethods.cartArray.pop();
                    }

                    var responseDynamic = $('#hiddenResponse');
                    responseDynamic.html(dataMethods.cartArray.join(", "));

                });

                $('.modalDownload').click(function(e){

                    var productId = $(this).attr('id');

                    $.ajax({
                        url : '/checkout/backorderDetails/getProductDetails/' + $(this).attr('id'),
                        type : 'GET',
                        headers : {
                            'Accept' : 'application/json'
                        },
                        success: function(response) {
                            mapItemsModal(JSON.parse(response),e.target);
                        }
                    });

                    $.ajax({
                       url: '/p/' + productId + '/downloads',
                       type : 'GET',
                       headers : {
                           'Accept' : 'application/json'
                       },
                       success: function(response) {
                           mapItemsPdf(response,e.target);
                       }
                    });

                });

                $('.qtyIncrement').click(function() {
                    var input0 = $(this).siblings('input')[0];
                    var htmlBodyClas = $('html');

                    if (htmlBodyClas.hasClass('isie7') || htmlBodyClas.hasClass('isie8')) {
                        input0.previousSibling.previousSibling.previousSibling.className += " hidden";
                    } else if (htmlBodyClas.hasClass('ie9')) {
                        input0.previousElementSibling.previousElementSibling.className += " hidden";
                    } else {
                        input0.previousElementSibling.previousElementSibling.classList.add('hidden');
                    }

                    if($(this).siblings('input').data('qtystep') === undefined) {
                        var currentValv2 = parseInt($(this).siblings('input').val(),10);
                        currentValv2 += 1;
                        $(this).siblings('input').val(currentValv2);
                    } else {
                        var currentVal = parseInt($(this).siblings('input').val(),10);
                        currentVal += $(this).siblings('input').data('qtystep');
                        $(this).siblings('input').val(currentVal);
                    }

                });

                $('.qtyDecrement').click(function() {
                    var input0 = $(this).siblings('input')[0];
                    var htmlBodyClass = $('html');

                    if($(this).siblings('input').attr('data-qtystep') === undefined) {
                        var currentValv2 = parseInt($(this).siblings('input').val(),10);
                        currentValv2 -= 1;
                        $(this).siblings('input').val(currentValv2);
                    } else {
                        var currentVal = parseInt($(this).siblings('input').val(),10);
                        currentVal -= $(this).siblings('input').attr('data-qtystep');
                        $(this).siblings('input').val(currentVal);
                    }

                    if( Number($(this).siblings('input').val()) < Number($(this).siblings('input').attr('data-qtymin')) ) {

                        $(this).siblings('input').val($(this).siblings('input').attr('data-qtymin'));

                        var input = $(this).siblings('input');
                        var pCode = input.closest('.card-item__items__content__input').next('.addCartFN');
                        var item = '';

                        if (htmlBodyClass.hasClass('isie7') || htmlBodyClass.hasClass('isie8')) {
                            item = input0.previousSibling.previousSibling.previousSibling.children[1];
                        } else {
                            item = input0.previousElementSibling.previousElementSibling.children[1];
                        }

                        var text = item.innerHTML.replace('{0}', pCode.attr('data-product-id')).replace('{1}', $(this).siblings('input').attr('data-qtymin'));

                        item.innerHTML = text;

                        if (htmlBodyClass.hasClass('isie7') || htmlBodyClass.hasClass('isie8')) {
                            input0.previousSibling.previousSibling.previousSibling.className -= " hidden";
                            input0.previousSibling.previousSibling.previousSibling.className += " numeric-popover";
                        } else if (htmlBodyClass.hasClass('ie9')){
                            input0.previousElementSibling.previousElementSibling.className -= " hidden";
                            input0.previousElementSibling.previousElementSibling.className += " numeric-popover";
                        } else {
                            input0.previousElementSibling.previousElementSibling.classList.remove('hidden');
                        }

                    }

                    if($(this).siblings('input').attr('data-qtystep') === '') {
                        $(this).val(1);
                    }

                });

                $('.inputResponse').blur(function(){
                    var currentVal = parseInt($(this).val(), 10);
                    var input0 = $(this)[0];
                    var htmlBodyClassInput = $('html');

                    if(currentVal < $(this).attr('data-qtymin')) {              // if moq is less than value pre set to moq
                        $(this).val($(this).attr('data-qtymin'));

                        var input = $(this);
                        var pCode = $(this).closest('.card-item__items__content__input').next('.addCartFN');
                        var item = '';

                        if (htmlBodyClassInput.hasClass('isie7') || htmlBodyClassInput.hasClass('isie8')) {
                            item = input0.previousSibling.previousSibling.previousSibling.children[1];
                        } else {
                            item = input0.previousElementSibling.previousElementSibling.children[1];
                        }

                        var text = item.innerHTML.replace('{0}', pCode.attr('data-product-id')).replace('{1}', $(this).attr('data-qtymin'));

                        item.innerHTML = text;

                        if (htmlBodyClassInput.hasClass('isie7') || htmlBodyClassInput.hasClass('isie8')) {
                            input0.previousSibling.previousSibling.previousSibling.className -= " hidden";
                            input0.previousSibling.previousSibling.previousSibling.className += " numeric-popover";
                        } else if (htmlBodyClassInput.hasClass('ie9')){
                            input0.previousElementSibling.previousElementSibling.className -= " hidden";
                            input0.previousElementSibling.previousElementSibling.className += " numeric-popover";
                        }  else {
                            input0.previousElementSibling.previousElementSibling.classList.remove('hidden');
                        }

                    } else {

                        if (htmlBodyClassInput.hasClass('isie7') || htmlBodyClassInput.hasClass('isie8')) {
                            input0.previousSibling.previousSibling.previousSibling.className += " hidden";
                        } else if(htmlBodyClassInput.hasClass('ie9')){
                            input0.previousElementSibling.previousElementSibling.className += " hidden";
                        } else {
                            input0.previousElementSibling.previousElementSibling.classList.add('hidden');
                        }

                    }

                    if($(this).val() === '') {
                        $(this).val($(this).attr('data-qtymin'));
                    }


                });

            }

            callback();

        };

    };

})(Tc.$);
