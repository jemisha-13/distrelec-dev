(function ($) {

    /**
     * Module implementation.
     *
     * @namespace Tc.Module
     * @class Cart-directorder
     * @extends Tc.Module
     */
    Tc.Module.CartDirectorder = Tc.Module.extend({

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

            this.onCartChange = $.proxy(this,'onCartChange');

            // subscribe to connector channel/s
            this.sandbox.subscribe('cart', this);
            this.sandbox.subscribe('lightboxStatus', this);

            $(document).on('cartChange', this.onCartChange);

            // jQuery Var's

            this.$numeric = $('.quickorder__numeric', $ctx);
            this.$btnAdd = $('.btn-add-product', $ctx);
            this.$btnAddField = $('.btn-add-field', $ctx);
            this.$quickOrderFieldWrapper = $('.quickorder__fieldWrapper', $ctx);
            this.$quickOrderField = $('.quickorder--field', $ctx);
            this.$ipt = $('#directOrder', $ctx);
            this.$ipt_val = [];
            this.$prods = $('.direct-prods', $ctx);
            this.$inputsForQuickResponse = $('input[type=text]', $ctx);
            this.$searchInput = this.$$('#directOrder');


            // var's
            this.tmpl = doT.template( this.$$('#tmpl-cart-directorder-typeahead').html() );
            this.isComponent = $ctx.hasClass('skin-cart-directorder-component');
            this.typeahead_uri = !(this.$searchInput.data('typeahead-uri')) ? '/cart/directOrder/search' : this.$searchInput.data('typeahead-uri');
            this.typeahead_channel = this.$searchInput.data('typeahead-channel');

            // array to store and cancel ongoing ajax calls
            this.jXhrPool = [];
            this.jXhrPoolId = 0;

            // array to store and cancel ongoing ajax calls
            this.currentTypeahead = 'directOrder';
            this.quickOrder_jXhrPool = [];
            this.quickOrder_jXhrPoolId = 0;

            // store if fusion search is enabled
            this.currentLanguage = $('#backenddata .shopsettings').data('language');

            if($('.isLogged').length > 0) {
                this.addNewField(2);
            } else {
                this.addNewField(3);
            }

        },

        /**
         * Hook function to do all of your module stuff.
         *
         * @method on
         * @param {Function} callback function
         * @return void
         */
        on: function (callback) {

            var $ctx = this.$ctx,
                self = this;

            Tc.Utils.numericStepper(self.$numeric);

            // + -
            Tc.Utils.numericStepper(self.$numeric, {
                error: function(value, minErrorMsg) {// disable send if quantity below quotation threshold
                    self.$btnAdd.attr('disabled','disabled');
                },
                warning: function(value, minErrorMsg) {// enable send if its only a warning, value has been autocorrected
                    self.$btnAdd.removeAttr('disabled');
                },
                success: function(value) {// reset disable
                    self.$btnAdd.removeAttr('disabled');
                }
            });

            self.clearQuantityFields();

            self.$btnAdd.off('click').on('click', function (e) {
                var inputValue = $('#directOrder').val();
                e.preventDefault();
                e.stopPropagation();

                $('.btn-add-product').attr( "data-product-id", inputValue );

                if ( self.isComponent ) {
                    self.checkAllQty();
                } else {
                    self.sendData();
                }

            });

            self.$btnAddField.off('click').on('click', function (e) {
                e.preventDefault();
                e.stopPropagation();
                self.addNewField();
            });

            $('.mod-cart-directorder .numeric .ipt').click( function(){

                $(this).val(''); // Empty input field on click

            });

            $(document).mouseup(function(e)
            {
                var container = $(".mod-cart-directorder"),
                    $numeric = $('.quickorder__numeric'),
                    $popoverAll = $('.quickorder__numeric .numeric-popover');

                // if the target of the click isn't the container nor a descendant of the container
                if (!container.is(e.target) && container.has(e.target).length === 0)
                {
                    $('.direct-prods').hide();
                }

                if (!$numeric.is(e.target) && $numeric.has(e.target).length === 0 && !self.$btnAdd.is(e.target) && self.$btnAdd.has(e.target).length === 0)
                {
                    if ( $('.skin-cart-directorder-component').length ) {
                        $popoverAll.addClass('hidden');
                    }
                }

            });

            // general enter key event to add product to cart
            // only execute if there was no interaction with the suggestion layer
            //
            self.$inputsForQuickResponse.on('keyup.enterAddToCart', function (e) {
                var $hovered = self.$prods.find('.direct-prod.hover');
                if($hovered.length === 0){
                    if (e.keyCode == 13) {
                        e.preventDefault();
                        e.stopPropagation();
                        self.$btnAdd.trigger('click');
                    }
                }
            });


            var searchExperience = $('#backenddata .shopsettings').data('search-experience');
            var onInvalidTerm = self.handleInvalidTerm;
            var typeAheadOptions = {
                source: function (query) {
                    var currentXhrId = ++self.jXhrPoolId,
                        fusionBaseUri = $('#backenddata .shopsettings').data('search-fusionurl'),
                        fusionSuffix = $('#backenddata .shopsettings').data('search-collection-suffix');
                        fusionApiKey = $('#backenddata .shopsettings').data('search-fusionapikey');
                        fusionUri = fusionBaseUri + '/typeahead' + (fusionSuffix ? '_' + fusionSuffix : ''),
                        country = $('#backenddata .shopsettings').data('country'),
                        channel = $('#backenddata .shopsettings').data('channel-label');


                    if(searchExperience !== 'factfinder') {
                        var fusionUrl = fusionUri + '?country=' + country.toLowerCase() + '&language=' + self.currentLanguage.toLowerCase() + '&channel=' + channel.toLowerCase() + '&q=' + query.replace(/\-/g, '');
                        return $.ajax({
                            url: fusionUrl,
                            type: 'GET',
                            headers: {
                                'X-Api-Key': fusionApiKey
                            },
                            beforeSend: function(jqXHR) {
                                $.each(self.jXhrPool, function(index, jqXHR) {
                                    if(jqXHR !== null && jqXHR.readyState !== 4) {
                                        jqXHR.abort();
                                    }
                                });
                                self.jXhrPool.push(jqXHR);
                            },
                            success: function (data, textStatus, jqXHR) {
                                if(currentXhrId == self.jXhrPoolId) {
                                    self.jXhrPool.pop();
                                    self.typeAheadListFusion(data.response);
                                }
                            },
                            error: function (jqXHR, textStatus, errorThrown) {
                                return errorThrown;
                            }
                        });
                    }else{
                        return $.ajax({
                            url: self.typeahead_uri,
                            type: 'get',
                            data: {
                                query: query.replace(/\-/g, ''),
                                channel: self.typeahead_channel,
                                format: 'json'
                            },
                            beforeSend: function( jqXHR ) {
                                // Stop all previous ongoing ajax calls
                                $.each(self.jXhrPool, function(index, jqXHR) {
                                    if (jqXHR !== null && jqXHR.readyState !== 4) {
                                        jqXHR.abort();
                                    }
                                });
                                self.jXhrPool.push(jqXHR);
                            },
                            success: function (data) {
                                // double check if it is really the latest xhrCall which was successful
                                if(currentXhrId == self.jXhrPoolId){
                                    self.jXhrPool.pop();
                                    self.typeAheadList(data);
                                }
                            },
                            error: function (jqXHR, textStatus, errorThrown) {
                            }
                        });
                    }
                },
                minLength: 3,
                items: 5,
                onInvalidTerm: onInvalidTerm,
                moduleReference: self,
                menu: this.$prods // Dropdown Menu is needed to be passed in as an option to work correctly
            };

            self.$ipt.typeahead(typeAheadOptions);

            callback();
        },

        handleInvalidTerm: function () {
            var self = this.options.moduleReference;

            if (self.isComponent) {
                $('.direct-prods').hide();
            } else {
                self.$prods.hide();
            }
        },



        /**
         *
         * @method gatherDataSingle
         *
         * @returns {{code: *, bulk: *}}
         */
        gatherDataSingle: function () {
            var self = this,
                $ctx = this.$ctx;

            return {
                productCodePost: $ctx.find($('[name^="directOrder"]')).val().trim().replace(/\-/g, ''),
                qty: $ctx.find('.numeric').find('.ipt').val().trim()
            };
        },

        /**
         *
         * @method gatherData
         *
         * @returns {{code: *, bulk: *}}
         */
        gatherDataMultiple: function () {
            var self = this,
                $ctx = this.$ctx,
                cartAPIproductsJson,
                anyError = false;


            var $allProducts = $('[name^="directOrder"]').filter(function() { return this.value !== ""; });

            if ($allProducts.size() > 0){

                $('#directOrder0').removeClass('border--red');
                $('.field--qty0').removeClass('border--red');

                cartAPIproductsJson = '[{';

                $allProducts.each(function (index, product) {

                    var $product = $(product);
                    if (index > 0) {
                        cartAPIproductsJson += '},{';
                    }

                    var productCode = $product.val().trim().replace(/\-/g, '');
                    var position = $product[0].id.substr($product[0].id.length - 1);
                    var quantity = $('.field--qty'+position).val().trim();

                    $('.field--qty'+position).removeClass('border--red');
                    if (!$.isNumeric(quantity)){
                        $('.field--qty'+position).addClass('border--red');
                        anyError=true;
                    }

                    cartAPIproductsJson += '"productCode":"' + productCode + '",';
                    cartAPIproductsJson += '"quantity":' + quantity + ',';
                    cartAPIproductsJson += '"product":null,';
                    cartAPIproductsJson += '"reference":""';

                });

                cartAPIproductsJson += '}]';
            }
            else{
                $('#directOrder0').addClass('border--red');
                $('.field--qty0').addClass('border--red');
                anyError = true;
            }

            if (!anyError){
                return cartAPIproductsJson;
            }

        },

        /**
         *
         * @method prepareForNextDirectOrder
         *
         */
        prepareForNextDirectOrder: function () {
            var self = this,
                $currIptEle,
                $currProdsEle;

            // Unbind Keydown Handler which is used to reset Numeric Field Values

            if (self.isComponent) {
                $currIptEle = $('#'+self.getCurrentTypeahead());
                $currProdsEle = $('.direct-prods');
            } else {
                $currIptEle = self.$ipt;
                $currProdsEle = self.$prods;
            }

            $currIptEle.val('');
            $currIptEle.unbind('keydown');
            $currProdsEle.hide();

        },

        /**
         *
         * @method resetNumericStepperToDefault
         *
         */
        resetNumericStepperToDefault: function () {
            var self = this;

            // Reset data attributes to default values, because they might have been adapted for the last added product
            self.$numeric.data('min', self.$numeric.data('min-default'));
            self.$numeric.data('step', self.$numeric.data('step-default'));
            self.$numeric.find('.ipt').val(self.$numeric.data('min-default'));
            self.$numeric.data('min-error', self.$numeric.data('min-error-default'));
            self.$numeric.data('step-error', self.$numeric.data('step-error-default'));

            // Hide error / warning popover
            self.$numeric.removeClass('numeric-error');
            self.$numeric.removeClass('numeric-warning');
        },


        /**
         *
         * @method sendData
         *
         */
        sendData: function () {
            var self = this,
                data,
                actionIdentifier = 'directOrder',
                typeOfDirectOrder;

            if ( self.isComponent ) {
                //Home direct order: Multiple values
                data = self.gatherDataMultiple();
                typeOfDirectOrder = 'multiple';
            } else {
                //normal direct order: single value
                data = self.gatherDataSingle();
                typeOfDirectOrder = 'simple';
            }

            if (data !== undefined) {

                var productCode = data.productCodePost;

                if (data.qty > 0) {
                    quantity = data.qty;
                } else {
                    quantity = 1;
                }

                if (self.isComponent) {
                    actionIdentifier += 'Component';
                }

                if (typeOfDirectOrder === 'multiple'){
                    // Trigger Cart API to add to cart
                    $(document).trigger('cart', {
                        actionIdentifier: actionIdentifier,
                        type: 'addBulk',
                        productsJson: data,
                        qty: data.qty,
                        isComponent: self.isComponent
                    });
                }  else {
                    // Trigger Cart API to add to cart
                    $(document).trigger('cart', {
                        actionIdentifier: actionIdentifier,
                        type: 'add',
                        productCodePost: data.productCodePost,
                        qty: data.qty,
                        isComponent: self.isComponent
                    });

                    if (data.qty > 0) {
                        quantity = data.qty;
                    } else {
                        quantity = 1;
                    }
                }
            }
        },

        //
        // Cart change event is coming from the cart api
        onCartChange: function(ev, data) {
            var self = this;
            if(data.actionIdentifier == "directOrder"){
                switch(data.type) {
                    case 'directOrderQuantityError':
                        self.cartChangeQuantityError(ev, data);
                        break;
                    case 'directOrderProductIdentifierError':
                        self.cartChangeProductIdentifierError(ev, data);
                        break;
                    case 'update':
                        self.cartChangeUpdate(ev, data);
                        break;
                    case 'add':
                        self.cartChangeAdd(ev, data);
                        break;
                }
            }
        },

        /**
         *
         * @method cartChangeQuantityError
         *
         * @param {object} ev
         * @param {object} data
         * @returns void
         */
        cartChangeQuantityError: function (ev, data) {
            var self = this,
                $currIptEle;

            self.$numeric.data('min', data.ajaxData.errorData.minQuantity);
            self.$numeric.data('min-error', data.ajaxData.errorData.minQuantityErrorMessage);
            self.$numeric.data('step', data.ajaxData.errorData.stepQuantity);
            self.$numeric.data('step-error', data.ajaxData.errorData.stepQuantityError);

            // Trigger stepper change to show error Message and suggest correct value
            Tc.Utils.numericStepper(self.$numeric, 'error');


            if (self.isComponent) {
                $currIptEle = $('#'+self.getCurrentTypeahead());
            } else {
                $currIptEle = self.$ipt;
            }

            // Attach listener to the product field to reset numeric field as soon as value changes
            // Do not reset if enter or tab key was pressed
            $currIptEle.on('keydown', function (ev) {
                if (ev.keyCode !== 13 && ev.keyCode !== 9) {
                    self.resetNumericStepperToDefault();
                    $currIptEle.unbind('keydown');
                }
            });
        },

        /**
         *
         * @method cartChangeQuantityError
         *
         * @param {object} ev
         * @param {object} data
         * @returns void
         */
        cartChangeProductIdentifierError: function (ev, data) {
            var self = this,
                $currIptEle;

            if (self.isComponent) {
                $currIptEle = $('#'+self.getCurrentTypeahead());
            } else {
                $currIptEle = self.$ipt;
            }

            Tc.Utils.showPopover($currIptEle, data.ajaxData.errorData.unknownProductIdentifierErrorMessage);
            $currIptEle.addClass('error');

            var popoverTimer = setTimeout(function(){
                Tc.Utils.hidePopover($currIptEle);
                $currIptEle.removeClass('error');
                clearTimeout(popoverTimer);
            }, 4000);
        },

        /**
         *
         * @method cartChangeQuantityError
         *
         * @param {object} ev
         * @param {object} data
         * @returns void
         */
        cartChangeUpdate: function (ev, data) {
            var self = this;

            self.fire('updateCartListItem', {ajaxSuccessData: data.ajaxSuccessData}, ['cart']);

            self.prepareForNextDirectOrder();
            self.resetNumericStepperToDefault();
        },


        /*
         * clear fields on module load
         */
        clearQuantityFields: function () {
            var self = this;
            var $qtyFields = $('[name^="quantityField"]').filter(function() { return this.value !== ""; });
            $qtyFields.val('');
        },

        /**
         *
         * @method cartChangeQuantityError
         *
         * @param {object} ev
         * @param {object} data
         * @returns void
         */
        cartChangeAdd: function (ev, data) {
            var self = this;

            // Cart Was empty before and it needs a page reload
            if (data.ajaxSuccessData.cartData.products.length === 1 && !self.isComponent) {
                window.location.href = "/cart";
            } else {
                self.fire('newCartListItem', {ajaxSuccessData: data.ajaxSuccessData}, ['cart']);
                self.prepareForNextDirectOrder();
                self.resetNumericStepperToDefault();
            }
        },

        /**
         *
         * @method prepareModel
         *
         * @param backendSuggestions
         */
        prepareModel: function (backendSuggestions) {
            var self = this;
            var model = [];
            var $countryisocode = $('body').data('isocode'),
                $missingThumbnailImg;

            if ( $countryisocode == 'DK' || $countryisocode == 'FI' || $countryisocode == 'NO' || $countryisocode == 'SE' || $countryisocode == 'LT' || $countryisocode == 'LV' || $countryisocode == 'EE' || $countryisocode == 'NL' || $countryisocode == 'PL') {
                $missingThumbnailImg = '/_ui/all/media/img/missing_portrait_small-elfa.png';
            } else {
                $missingThumbnailImg = '/_ui/all/media/img/missing_portrait_small.png';
            }

            for (var i = 0; i < backendSuggestions.length; i++) {
                var modelItem = {};
                if (backendSuggestions[i].code !== undefined)	{ // old schema
                    modelItem.name = backendSuggestions[i].name;
                    modelItem.categories = backendSuggestions[i].categories;
                    modelItem.code = backendSuggestions[i].code;
                    modelItem.codeErpRelevant = backendSuggestions[i].codeErpRelevant;
                    modelItem.unit = backendSuggestions[i].orderQuantityMin;
                    modelItem.currency = backendSuggestions[i].currencyIso;
                    modelItem.formattedPrice = backendSuggestions[i].formattedPriceValue;
                    modelItem.thumbnail = {};
                    modelItem.thumbnail.url = backendSuggestions[i].imageUrl === "" ? $missingThumbnailImg : backendSuggestions[i].imageUrl;
                    modelItem.thumbnail.alt = backendSuggestions[i].name; // description not available from FactFinder 12.09.2013
                    model.push(modelItem);
                } else if (backendSuggestions[i].type === 'productName') { // new schema
                    modelItem.name = backendSuggestions[i].attributes.Title;
                    modelItem.code = backendSuggestions[i].attributes.masterId;
                    modelItem.codeErpRelevant = backendSuggestions[i].attributes.id;
                    modelItem.unit = backendSuggestions[i].attributes.ItemsMin;

                    modelItem.ItemsMin = backendSuggestions[i].attributes.ItemsMin;
                    modelItem.ItemsStep = backendSuggestions[i].attributes.ItemsStep;

                    // render price as string with 2–4 decimals
                    var prices =  backendSuggestions[i].attributes.Price.split('|');
                    var priceData = ($('.shopsettings').data('channel-label') === 'B2C' ? prices[1] : prices[2]).split(';'); // Gross prices for B2C, net for B2B
                    modelItem.currency = priceData[0];
                    modelItem.price =  (priceData[2].split('='))[1];
                    var priceParts = modelItem.price.split('.');
                    if (priceParts.length===1) priceParts[1] = '00';
                    if (priceParts[0].length===0) priceParts[0]='0';
                    if (priceParts[1].length<2) priceParts[1] += '00'.substring(priceParts[1].length);
                    modelItem.formattedPrice = priceParts.join('.');
                    if (priceParts[1].length>4) modelItem.formattedPrice = parseFloat(modelItem.priceFormattedValue).toFixed(4);

                    modelItem.formattedPrice = Tc.Utils.formatPrice(modelItem.formattedPrice,$('.shopsettings').data('country'));

                    var images = JSON.parse( backendSuggestions[i].attributes.AdditionalImageURLs);
                    modelItem.thumbnail = {};
                    modelItem.thumbnail.url = !(images.landscape_small) ? $missingThumbnailImg : images.landscape_small.replace(/landscape_small/g, 'portrait_small');
                    modelItem.thumbnail.alt = modelItem.name;


                    modelItem.manufacturer = backendSuggestions[i].attributes.Manufacturer;
                    modelItem.typeName = backendSuggestions[i].attributes.TypeName;
                    modelItem.productCode = backendSuggestions[i].attributes.articleNr;

                    modelItem.energyEfficiencyData = [];

                    if (typeof backendSuggestions[i].attributes.energyEfficiency != 'undefined') {
                        if ( backendSuggestions[i].attributes.energyEfficiency.indexOf('{') === 0) {

                            // New data schema
                            // "energyEfficiency":"{\u0022Energyclasses_LOV\u0022:\u0022A++\u0022,\u0022Leistung_W\u0022:\u00226\u0022}"

                            var energyEfficiency = JSON.parse(backendSuggestions[i].attributes.energyEfficiency);
                            modelItem.energyEfficiencyData[0] = energyEfficiency.Energyclasses_LOV || '';
                            modelItem.energyEfficiencyData[1] = energyEfficiency.Leistung_W || '';
                            modelItem.energyTopText =	energyEfficiency.calc_energylabel_top_text || '';
                            modelItem.energyBottomText = energyEfficiency.calc_energylabel_bottom_text || '';
                            modelItem.energyClassesBuiltInLed = energyEfficiency['Energyclasses_built-in_LED_LOV'] || '';
                            modelItem.energyClassesIncludedBulb = energyEfficiency.energyclasses_included_bulb_LOV || '';
                            modelItem.energyClassesFitting = energyEfficiency.Energyclasses_fitting_LOV || '';
                            modelItem.isLamp = (modelItem.energyClassesFitting.length || modelItem.energyClassesBuiltInLed.length);
                        } else {

                            // Old data schema
                            // "energyEfficiency":"A;13 W"

                            modelItem.energyEfficiencyData = backendSuggestions[i].attributes.energyEfficiency.split(';');
                            if (modelItem.energyEfficiencyData.length === 2 && typeof modelItem.energyEfficiencyData[1] === 'string') {
                                modelItem.energyEfficiencyData[1] = modelItem.energyEfficiencyData[1].substring(0,modelItem.energyEfficiencyData[1].indexOf(' W'));
                            }
                        }
                    }

                    model.push(modelItem);
                }
            }
            return model;
        },

        /**
         *
         * @method typeAheadListFusion
         *
         * @param input
         */
        typeAheadListFusion: function (input) {
            this.model = [];
            var self = this,
                $currProdsEle,
                currency = $('#backenddata .shopsettings').data('currency');

            for(var entry in input.docs) {
                var row = self.fusionRowSourcetoRow(input.docs[entry], currency);
                if(row) {
                    this.model.push(row);
                }
            }

            if ( self.isComponent ) {
                $currProdsEle = $('.direct-prods--'+self.getCurrentTypeahead() );
            } else {
                $currProdsEle = self.$prods;
            }
            $currProdsEle.html(self.tmpl(this.model)).show();
            self.typeAheadHandler();
        },

        /**
         *
         * @method fusionRowSourcetoRow
         *
         * @param rowData
         * @param currency
         */
        fusionRowSourcetoRow: function (rowData, currency) {
            var row = {};
            var self = this;
            var country = $('#backenddata .shopsettings').data('country');
            row.name = rowData.title;
            row.uri = rowData.productUrl;
            row.unit = rowData.itemMin;
            row.code = rowData.productNumber;

            var productCodeErp = rowData.productNumber.split('/');
            row.codeErpRelevant = productCodeErp[productCodeErp.length - 1];
            
            if (row.codeErpRelevant.length === 8) {
                row.codeErpRelevant = row.codeErpRelevant.substring(0, 3) + '-' + row.codeErpRelevant.substring(3, 5) + '-' + row.codeErpRelevant.substring(5, 8);
            }
            row.thumbnail = {};
            row.thumbnail.url = rowData.imageURL;
            // check if images defined, replace with default picture if not
            if(!row.thumbnail.url) {
                var $countryisocode = $('body').data('isocode');
                if ( $countryisocode == 'DK' || $countryisocode == 'FI' || $countryisocode == 'NO' || $countryisocode == 'SE' || $countryisocode == 'LT' || $countryisocode == 'LV' || $countryisocode == 'EE' || $countryisocode == 'NL' || $countryisocode == 'PL') {
                    row.thumbnail.url = '/_ui/all/media/img/missing_landscape_small-elfa.png';
                } else {
                    row.thumbnail.url = '/_ui/all/media/img/missing_landscape_small.png';
                }
            }

            row.typeName = rowData.typeName;
            row.manufacturer = rowData.manufacturer;

            row.energyEfficiencyData = [];
            // energy efficieny data checks
            if(typeof rowData.energyEffiencyLabels_en_string_s != 'undefined') {
                if(rowData.energyEffiencyLabels_en_string_s.indexOf('{') === 0) {
                    var energyEfficiency = JSON.parse(rowData.energyEffiencyLabels_en_string_s);

                    row.energyEfficiencyData[0] = energyEfficiency.energyEfficiencyClasses || '';
                    row.energyEfficiencyData[1] = energyEfficiency.Leistung_W || '';
                    row.energyTopText = energyEfficiency.calc_energylabel_top_text || '';
                    row.energyBottomText = energyEfficiency.calc_energylabel_bottom_text || '';
                    row.energyClassesBuiltInLed = energyEfficiency.energyClassesBuiltInLed || '';
                    row.energyClassesIncludedBulb = energyEfficiency.energyclasses_included_bulb_LOV || '';
                    row.energyClassesFitting = energyEfficiency.Energyclasses_fitting_LOV || '';
                    row.isLamp = (row.energyClassesFitting.length || row.energyClassesBuiltInLed.length);
                }
                else {
                    row.energyEfficiencyData = rowData.energyEffiencyLabels_en_string_s.split(';');

                    if(row.energyEfficiencyData.length === 2 && typeof row.energyEfficiencyData[1] === 'string') {
                        row.energyEfficiencyData[1] = row.energyEfficiencyData[1].substring(0, row.energyEfficiencyData[1]. indexOf(' W'));
                    }
                }
            }
            row.currency = currency;
            row.formattedPrice = $('.shopsettings').data('channel-label') === 'B2C' ? rowData.singleMinPriceGross : rowData.singleMinPriceNet;

            var priceParts = row.formattedPrice.toString().split('.');

            if (priceParts.length === 1) {
                priceParts[1] = '00';
            }
    
            if (priceParts[0].length === 0) {
                priceParts[0] = '0';
            }
    
            if (priceParts[1].length < 2) {
                priceParts[1] += '00'.substring(priceParts[1].length);
            }

            row.formattedPrice = priceParts.join('.');
            if (priceParts[1].length > 4) {
                row.formattedPrice = parseFloat(row.formattedPrice).toFixed(4);
            }
            
            if(country.toLowerCase() === 'se'){
                row.formattedPrice = row.formattedPrice.replace('.', ',');
            } 

            row.ItemsMin = (function (minQuantity) {
                return $.isNumeric(minQuantity) ? minQuantity : 1;
            })(rowData.itemMin);

            row.ItemsStep = (function (step) {
                return $.isNumeric(step) ? step : 1;
            })(rowData.itemStep);
            
            return row;
        },

        /**
         *
         * @method typeAheadList
         *
         * @param data
         */
        typeAheadList: function (data) {

            var  self = this
                ,backendTemplateBridge
                ,backendSuggestions = data.searchResult === undefined ? data.suggestions :  data.searchResult.suggestions // new / old scheme
                ,$currProdsEle;

            backendTemplateBridge = self.prepareModel(backendSuggestions);

            if ( self.isComponent ) {
                $currProdsEle = $('.direct-prods--'+self.getCurrentTypeahead() );
            } else {
                $currProdsEle = self.$prods;
            }

            $currProdsEle.html(self.tmpl(backendTemplateBridge)).show();

            self.typeAheadHandler();
        },

        getCurrentTypeahead: function (e) {
            var  currentTypeahead = $('.quickorder--component').attr('id').split('$')[1];
            return currentTypeahead;
        },

        typeAheadHandlerHover: function ($item) {
            $item.addClass('hover').siblings().removeClass('hover');
        },

        typeAheadHandlerPrev: function ($hovered) {
            var self = this,
                $prev = $hovered.prev(),
                $last ,
                $currProdsEle;

            if ( self.isComponent ) {
                $currProdsEle = $('.direct-prods--'+self.getCurrentTypeahead() );
            } else {
                $currProdsEle = self.$prods;
            }

            $last = $currProdsEle.find('.direct-prod').last();

            if ($hovered.length > 0) {
                if ($prev.length > 0) {
                    self.typeAheadHandlerHover($prev);
                } else {
                    self.typeAheadHandlerHover($last);
                }
            } else {
                self.typeAheadHandlerHover($last);
            }
        },

        typeAheadHandlerNext: function ($hovered) {
            var self = this,
                $next = $hovered.next(),
                $currProdsEle,
                $first;

            if ( self.isComponent ) {
                $currProdsEle = $('.direct-prods--'+self.getCurrentTypeahead() );
            } else {
                $currProdsEle = self.$prods;
            }

            $first = $currProdsEle.find('.direct-prod').eq(0);

            if ($hovered.length > 0) {
                if ($next.length > 0) {
                    self.typeAheadHandlerHover($next);
                } else {
                    self.typeAheadHandlerHover($first);
                }
            } else {
                self.typeAheadHandlerHover($first);
            }
        },

        typeAheadHandlerSelect: function ($hovered) {

            var self = this,
                $currIptEle,
                $currProdsEle;

            var artNumSelected = $hovered.data('code');
            var artNumSelectedErpRelevant = $hovered.data('code-erp-relevant');



            if ( self.isComponent ) {
                $currIptEle = $('#'+self.getCurrentTypeahead());
                $currProdsEle = $('.direct-prods--'+self.getCurrentTypeahead());
            } else {
                $currIptEle = self.$ipt;
                $currProdsEle = self.$prods;
            }

            var suggestionBoxEntry = $currIptEle.val();
            var suggestionBoxSelection = artNumSelectedErpRelevant === "" ? (artNumSelected === "" ? suggestionBoxEntry : artNumSelected) : artNumSelectedErpRelevant;

            $currIptEle.val(suggestionBoxSelection);
            $currProdsEle.hide().html('');

            self.typeAheadHandlerUnbind();

        },

        /**
         *
         * @method typeAheadHandler
         *
         */
        typeAheadHandler: function () {

            var self = this,
                hover = function ($item) {
                    $item.addClass('hover').siblings().removeClass('hover');
                },
                itemNext = function ($hovered) {
                    var $next = $hovered.next(),
                        $first = self.$prods.find('.direct-prod').eq(0);

                    if ($hovered.length > 0) {
                        if ($next.length > 0) {
                            hover($next);
                        } else {
                            hover($first);
                        }
                    } else {
                        hover($first);
                    }
                },
                itemPrev = function ($hovered) {
                    var $prev = $hovered.prev(),
                        $last = self.$prods.find('.direct-prod').last();

                    if ($hovered.length > 0) {
                        if ($prev.length > 0) {
                            hover($prev);
                        } else {
                            hover($last);
                        }
                    } else {
                        hover($last);
                    }
                },
                itemSelect = function ($hovered) {

                    var artNumSelected = $hovered.data('code');
                    var artNumSelectedErpRelevant = $hovered.data('code-erp-relevant');
                    var suggestionBoxEntry = self.$ipt.val();
                    var suggestionBoxSelection = artNumSelectedErpRelevant === "" ? (artNumSelected === "" ? suggestionBoxEntry : artNumSelected) : artNumSelectedErpRelevant;

                    var $currIptEle,
                        $currQtyEle,
                        $currProdsEle,
                        $currQtyMin,
                        $currQtyStep,
                        $currQtyMinError,
                        $currQtyStepError,
                        $currQtyErpCode;


                    $currQtyMin = $hovered.data('min');
                    $currQtyStep = $hovered.data('step');
                    $currQtyErpCode = $hovered.data('code-erp-relevant');


                    if ( self.isComponent ) {
                        $currIptEle = $('#'+self.getCurrentTypeahead());
                        $currProdsEle = $('.direct-prods--'+self.getCurrentTypeahead());
                        $currQtyEle = $('#qty-'+self.getCurrentTypeahead());
                        $currQtyEle.find('.quickorder__quantity').removeClass('disabled').removeAttr('disabled');
                    } else {

                        $currQtyEle = $('.cart-quickorder__numeric');
                        $currIptEle = self.$ipt;
                        $currProdsEle = self.$prods;
                        $currQtyEle.find('.ipt').val($currQtyMin);
                    }
                    
                    $currQtyMinError = $currQtyEle.data('min-error').replace('1', $currQtyMin);
                    $currQtyStepError = $currQtyEle.data('step-error').replace('1', $currQtyStep);

                    $currQtyEle.attr('data-product-code', $currQtyErpCode );
                    $currQtyEle.attr('data-min', $currQtyMin );
                    $currQtyEle.attr('data-step', $currQtyStep );
                    $currQtyEle.attr('data-min-error', $currQtyMinError );
                    $currQtyEle.attr('data-step-error', $currQtyStepError );


                    Tc.Utils.numericStepper(self.$numeric, 'error');

                    $currIptEle.val(suggestionBoxSelection);
                    $currProdsEle.hide().html('');

                    self.typeAheadHandlerUnbind();

                },
                onEnergyLabelClick = function (ev) {
                    var $ctx = this.$ctx,
                        self = this;

                    ev.preventDefault();
                    ev.stopPropagation();

                    if ( $('.container-suggest').height() > 500 ) {
                        var _elem = $('.container-suggest .prods .js-results');
                        _elem.css('overflow-y','auto');
                        _elem.animate({ scrollTop: _elem[0].scrollHeight }, 50);
                    }

                    var $a = $(ev.target).closest('span'),
                        $label = $a.parent().find('.energy-label-popover'),
                        $lampLabel = $label.children('.lamp');

                    if ($lampLabel.length) {
                        var energyClassesLed = $a.data('energyClassesLed'),
                            energyClassesBulb = $a.data('energyClassesBulb'),
                            energyClassesIncludedBulb = $a.data('energyClassesIncludedbulb'),
                            $textBottom = $label.find('.energy-label-text-bottom'),
                            $textBottomClassText = $label.find('.energy-label-text-bottom .energy-class-text'),
                            energyClasses = {'a++':0,'a+':0,'a':0,'b':0,'c':0,'d':0,'e':0},	// energyClasses is an array of allowed values. During the population of the label, any class with a LED or Bulb icon sets the value of this array to 1.
                            energyClassesColor = {'a++':'#108e2f','a+':'#50a328','a':'#bccb00','b':'#fae80b','c':'#f5ac00','d':'#e24f13','e':'#d8021b'},
                            $energyClassLable = "",
                            $energyLableCrossStr = "",
                            $energyLableCrossArr = [],
                            $energyLableCross = [];

                        if ( energyClassesLed.length > 0 && energyClassesBulb .length > 0 ) {
                            $('.eel-top-text--ledbulb').removeClass('hidden');
                            $('.eel-bottom-text--ledbulb').removeClass('hidden');
                        } else if ( energyClassesLed.length > 0 ) {
                            $('.eel-top-text--led').removeClass('hidden');
                            $('.eel-bottom-text--led').removeClass('hidden');
                        } else if ( energyClassesBulb.length > 0 ) {
                            $('.eel-top-text--bulb').removeClass('hidden');
                            $('.eel-bottom-text--bulb').removeClass('hidden');
                        }

                        if ( energyClassesIncludedBulb.length === 0 && energyClassesLed .length > 0 ) {
                            $('.eel-bottom-text--lednotincludedbulb').removeClass('hidden');
                        } else if ( energyClassesIncludedBulb.length > 0 ) {
                            $('.eel-bottom-text--includedbulb').removeClass('hidden');
                            $('.eel-bottom-text--arrow').removeClass('hidden');
                        } else {
                            $('.eel-bottom-text--lednotincludedbulb').addClass('hidden');
                            $('.eel-bottom-text--includedbulb').addClass('hidden');
                            $('.eel-bottom-text--arrow').addClass('hidden');
                        }

                        if ($textBottom.length && $textBottom.html().length) {
                            $textBottom.html($textBottom.html().replace(/<energylabel>(.+)<\/energylabel>/,'<span class="ico-energy $1" title="$1"><i></i></span>'));
                        }

                        if (energyClassesLed.length) {
                            $.each(energyClassesLed.split(';'), function(i, val) {
                                valLower = val.toLowerCase();
                                energyClasses[valLower]=1;
                            });

                            var ledValue = energyClassesLed.split(';');
                            var ledclass = (ledValue[0] + "--"  + ledValue[ledValue.length - 1]).toLowerCase();
                            var ledTopValue = ledValue[0].toLowerCase();
                            $lampLabel.append('<div class="energy-label-led ledclass ' + ledTopValue + ' ' + ledclass + '">' +
                                '<div class="bracket-wrapper">\n' +
                                '  	<span class="bracket"></span>\n' +
                                ' 	<span class="bracket-wrapper-text">\n' +
                                '  		<span>L</span>\n' +
                                '  		<span>E</span>\n' +
                                '  		<span>D</span>\n' +
                                ' 	</span>\n' +
                                '</div></div>');

                        }

                        if (energyClassesBulb.length) {
                            $.each(energyClassesBulb.split(';'), function(i, val) {
                                valLower = val.toLowerCase();
                                energyClasses[valLower]=1;
                            });

                            var bulbValue = energyClassesBulb.split(';');
                            var bulbclass = (bulbValue[0] + "--"  + bulbValue[bulbValue.length - 1]).toLowerCase();
                            var bulbTopValue = bulbValue[0].toLowerCase();

                            $lampLabel.append('<div class="energy-label-bulb bulbclass ' + bulbTopValue + ' ' + bulbclass + '">' +
                                '<div class="bracket-wrapper">\n' +
                                '  <span class="bracket"></span>\n' +
                                '  <span class="bulb-img"></span>\n' +
                                '</div></div>');
                        }

                        if ( energyClassesLed.split(';').length < energyClassesBulb.split(';').length ) {
                            $('.energy-label-bulb').addClass('max-left');
                        }

                        $.each(energyClasses, function(key, val) { // Check for energy classes not populated with LED or Bulb icons, add Cross icon to those

                            if (val===0) {
                                $energyLableCross.push(key);
                            } else {
                                $energyLableCross.push('$');
                            }

                        });

                        $energyLableCrossStr = $energyLableCross.filter(function(item, pos, arr){
                            return pos === 0 || item !== arr[pos-1];
                        });

                        $energyLableCrossArr = $energyLableCrossStr.toString().split(',$,');

                        $.each($energyLableCrossArr, function(key, val) {

                            val =  val.toString().split(',').join('--').replace("--$", "").replace("$--", "");

                            var crossVal = val.split('--');
                            var crossFirstVal = crossVal[0];
                            var crossLastVal = crossVal[crossVal.length-1];
                            var crossClass = "";

                            crossClass = crossFirstVal + '--' + crossLastVal;

                            if ( crossClass.toString().indexOf('$') <= -1 ) {
                                $lampLabel.append('<div class="energy-label-cross crossclass ' + crossFirstVal + ' ' + crossClass + '">' +
                                    '<div class="cross-icon-wrapper"><span class="cross-icon"></span></div>\n' +
                                    '</div>');
                            }

                        });

                        $energyClassLable = $('.energy-label-text-bottom .energy-class-arrow-text').html();

                        if ( $energyClassLable !== '' && $energyClassLable !== undefined ) {
                            var colorCodeVar = $energyClassLable.toString().trim().toLowerCase();
                            $('.energy-label-text-bottom .status-arrow').css( 'fill', energyClassesColor[colorCodeVar]);
                        } else {
                            $('.energy-class-arrow').css( 'display', 'none');

                            var lednotincludedbulbVar = $lampLabel.find('.eel-bottom-text--lednotincludedbulb').hasClass('hidden');

                            if ( !lednotincludedbulbVar ) {
                                $textBottom.addClass('border');
                            } else {
                                $textBottom.removeClass('border');
                            }
                        }
                    }

                    if ( $label.hasClass('hidden') ) {
                        $('.energy-label-popover').addClass('hidden');
                        $label.removeClass('hidden');
                    } else {
                        $label.addClass('hidden');
                    }

                    $('.mod-layout').on('click.eel', function(ev) {
                        ev.preventDefault();
                        ev.stopPropagation();
                        $('.energy-label-popover').addClass('hidden');
                        $('.mod-layout').off('click.eel');
                    });
                };

            self.typeAheadHandlerUnbind();

            $('.direct-prods')
                .on('mouseenter.typeAheadList', '.direct-prod', function () {
                    hover($(this));
                })
                .on('mouseleave.typeAheadList', '.direct-prod', function () {
                    $(this).removeClass('hover');
                })
                .on('click.typeAheadList', '.direct-prod', function () {
                    itemSelect($(this));
                })
                .on('click.typeAheadList', '.mod-energy-efficiency-label', function (e) {
                    onEnergyLabelClick(e);
                });


            self.$ipt.off('keyup.typeAheadList').on('keyup.typeAheadList', function (e) {

                var $hovered = self.$prods.find('.direct-prod.hover'),
                    code = e.keyCode;
                if (code === 40) {
                    e.preventDefault();
                    itemNext($hovered);
                } else if (code === 38) {
                    e.preventDefault();
                    itemPrev($hovered);
                } else if (code === 13) {
                    // only execute if user was interacting with suggestion layer
                    if($hovered.length > 0){
                        e.preventDefault();
                        itemSelect($hovered);
                    }
                }
            });


        },


        /**
         *
         * @method typeAheadHandlerUnbind
         *
         */
        typeAheadHandlerUnbind: function () {
            var self = this;

            $('.direct-prods')
                .off('mouseenter.typeAheadList')
                .off('mouseleave.typeAheadList')
                .off('click.typeAheadList');

            if ( self.isComponent ) {
                // for ( var i=0; i< 10; i++) {
                //     self.$ipt_val[i].off('keyup.typeAheadList');
                // }
            } else {
                self.$ipt.off('keyup.typeAheadList');
            }

        },

        /**
         *  @method addNewField
         *  for adding new field using passed parameter as field number
         */
        addNewField: function (_num) {
            var self = this;

            if ( _num !== undefined ) {

                for (var i=0; i < _num; i++) {
                    self.createNewField(i);
                }

            } else {

                var fieldsLength = $('.quickorder__fieldWrapper .quickorder--field').length;
                self.createNewField(fieldsLength);

            }

        },


        /**
         *  @method createNewField
         *  for creating new field using passed parameter as field number
         */
        createNewField: function (_fieldNum) {
            var self = this;

            if ( _fieldNum <= 9) {

                var item = self.$quickOrderField.clone();
                item.find('.quickorder__aticle-number').attr('id','directOrder'+_fieldNum);
                item.find('.quickorder__aticle-number').attr('name','directOrder'+_fieldNum);
                item.find('.quickorder__quantity').attr('id','directOrder'+_fieldNum+'-qty-ipt');
                item.find('.quickorder__quantity').attr('name','quantityField'+_fieldNum);
                item.find('.quickorder__quantity').addClass('field--qty'+_fieldNum).attr('disabled','disabled');
                item.find('.direct-prods').addClass('direct-prods--directOrder'+_fieldNum);
                item.find('.quickorder__numeric').attr('id','qty-directOrder'+_fieldNum);

                item.appendTo( self.$quickOrderFieldWrapper);

                if (_fieldNum === 9) {
                    $('.btn-add-field').addClass('hidden');
                }

                $('#qty-directOrder'+_fieldNum).off('keyup').on('keyup', function (e) {
                    self.checkEnteredQty(_fieldNum);
                });

                $('#directOrder'+_fieldNum).off('keyup.typeAheadList').on('keyup.typeAheadList', function (e) {

                    var _inputId = $(e.target).attr('id');
                    $('.quickorder--component').attr('id','currentTypeahea$'+_inputId);

                    $('#'+_inputId +'-qty-ipt').val('').attr('disabled','disabled');

                    var $hovered,
                        $currProdsEle,
                        code = e.keyCode;

                    if ( self.isComponent ) {
                        $currProdsEle = $('.direct-prods--'+self.getCurrentTypeahead() );
                    } else {
                        $currProdsEle = self.$prods;
                    }

                    $hovered = $currProdsEle.find('.direct-prod.hover');

                    if (code === 40) {
                        e.preventDefault();
                        self.typeAheadHandlerNext($hovered);
                    } else if (code === 38) {
                        e.preventDefault();
                        self.typeAheadHandlerPrev($hovered);
                    } else if (code === 13) {
                        // only execute if user was interacting with suggestion layer
                        if($hovered.length > 0){
                            e.preventDefault();
                            self.typeAheadHandlerSelect($hovered);
                        }
                    }
                });

                // 'typeAheadOptions' for dynamically added input field
                var typeAheadOptions = {
                    source: function (query) {
                        var currentXhrId = ++self.jXhrPoolId,
                        fusionBaseUri = $('#backenddata .shopsettings').data('search-fusionurl'),
                        fusionSuffix = $('#backenddata .shopsettings').data('search-collection-suffix');
                        fusionApiKey = $('#backenddata .shopsettings').data('search-fusionapikey');
                        fusionUri = fusionBaseUri + '/typeahead' + (fusionSuffix ? '_' + fusionSuffix : ''),
                        country = $('#backenddata .shopsettings').data('country'),
                        channel = $('#backenddata .shopsettings').data('channel-label'),
                        searchExperience = $('#backenddata .shopsettings').data('search-experience');

                        if(searchExperience !== 'factfinder') {
                            var fusionUrl = fusionUri + '?country=' + country.toLowerCase() + '&language=' + self.currentLanguage.toLowerCase() + '&channel=' + channel.toLowerCase() + '&q=' + query.replace(/\-/g, '');
                            return $.ajax({
                                url: fusionUrl,
                                type: 'GET',
                                headers: {
                                    'X-Api-Key': fusionApiKey
                                },
                                beforeSend: function(jqXHR) {
                                    $.each(self.jXhrPool, function(index, jqXHR) {
                                        if(jqXHR !== null && jqXHR.readyState !== 4) {
                                            jqXHR.abort();
                                        }
                                    });
                                    self.jXhrPool.push(jqXHR);
                                },
                                success: function (data, textStatus, jqXHR) {
                                    if(currentXhrId == self.jXhrPoolId) {
                                        self.jXhrPool.pop();
                                        self.typeAheadListFusion(data.response);
                                    }
                                },
                                error: function (jqXHR, textStatus, errorThrown) {
                                    return errorThrown;
                                }
                            });

                        }else{
                            return $.ajax({
                                url: self.typeahead_uri,
                                type: 'get',
                                data: {
                                    query: query.replace(/\-/g, ''),
                                    channel: self.typeahead_channel,
                                    format: 'json'
                                },
                                beforeSend: function( jqXHR ) {
                                    // Stop all previous ongoing ajax calls
                                    $.each(self.jXhrPool, function(index, jqXHR) {
                                        if (jqXHR !== null && jqXHR.readyState !== 4) {
                                            jqXHR.abort();
                                        }
                                    });
                                    self.jXhrPool.push(jqXHR);
                                },
                                success: function (data) {
                                    // double check if it is really the latest xhrCall which was successful
                                    if(currentXhrId == self.jXhrPoolId){
                                        self.jXhrPool.pop();
                                        self.typeAheadList(data);
                                    }
                                },
                                error: function (jqXHR, textStatus, errorThrown) {
                                }
                            });
                        }
                    },
                    minLength: 3,
                    items: 5,
                    onInvalidTerm: self.onInvalidTerm,
                    moduleReference: self,
                    menu: this.$prods // Dropdown Menu is needed to be passed in as an option to work correctly
                };

                self.$ipt_val[_fieldNum] = $('#directOrder'+_fieldNum);
                self.$ipt_val[_fieldNum].typeahead(typeAheadOptions);

            }

        },

        checkAllQty: function () {
            var self = this,
                checkAllQtyError = false;

            $allItems = $('.quickorder__fieldWrapper .quickorder--field');

            $allItems.each(function (index, item) {
                var _item = $(item).find('.quickorder__numeric');
                self.checkEnteredQty(index);

                if ( _item.hasClass('numeric-error') ) {
                    checkAllQtyError = true;
                }

            });

            if (!checkAllQtyError) {
                self.sendData();
            }

        },

        checkEnteredQty: function (_fieldNum) {
            var self = this;

            var _item = $('#qty-directOrder'+_fieldNum);

            if ( $('#directOrder'+_fieldNum).val() !== '' ) {

                var moqUnit = Number( _item.data('min') ),
                    moqStep = Number( _item.data('step') ),
                    qtyValue = Number( _item.find('input').val() );

                self.checkQtyValidation(_item[0].id,moqUnit,moqStep,qtyValue);

            }

        },

        checkQtyValidation: function (_qtyIpt,_moqUnit,_moqStep,_qtyValue) {

            var self = this;

            var qtyOrderField = $('#'+_qtyIpt ),
                qtyIptField = $('#'+_qtyIpt + ' .quickorder__quantity'),
                modulo = _qtyValue % _moqUnit,
                $popover = $('#' + qtyIptField[0].id + ' ' + '+ .numeric-popover');

            qtyOrderField.removeClass('numeric-error');
            $popover.addClass('hidden');

            if ( _qtyValue <= 0) {
                self.qtyValidationPopup(qtyOrderField,'min-error');
                qtyOrderField.addClass('numeric-error');
                $popover.removeClass('hidden');
            } else {
                if ( _moqStep > 1) {
                    if ( modulo !== 0 ) {
                        self.qtyValidationPopup(qtyOrderField,'step-error');
                        qtyOrderField.addClass('numeric-error');
                        $popover.removeClass('hidden');
                    }

                }

                if ( _qtyValue < _moqUnit && _qtyValue !== 0) {
                    self.qtyValidationPopup(qtyOrderField,'min-error');
                    qtyOrderField.addClass('numeric-error');
                    $popover.removeClass('hidden');
                }

            }

            qtyIptField.removeClass('border--red');
        },

        qtyValidationPopup: function (_qtyOrderField,_mode) {

            var minErrorMsg = _qtyOrderField.attr('data-min-error'),
                stepErrorMsg = _qtyOrderField.attr('data-step-error'),
                $popover = _qtyOrderField.find('.numeric-popover'),
                $popoverAll = $('.quickorder__numeric .numeric-popover');

            switch(_mode) {
                case 'min-error':
                    $popover.find('.popover-content').html(minErrorMsg);
                    break;
                case 'step-error':
                    $popover.find('.popover-content').html(stepErrorMsg);
                    break;
                default:
            }

            $popover.removeClass('hidden');

        },

    });

})(Tc.$);
