 (function ($) {

    Tc.Module.MetahdSuggest = Tc.Module.extend({

        init: function ($ctx, sandbox, id) {
            // call base constructor
            this._super($ctx, sandbox, id);

            this.backenddata = $('#backenddata');
            this.sandbox.subscribe('metahdSearch', this);
            this.uiTpl = doT.template(this.$$('#template-suggest').html());

            var prodsTemp = this.$$('#template-suggest-row-product').html(),
                prodsTempWrapepr = '<a href="{{=it.uri}}?queryFromSuggest=true" class="suggest-row">'+prodsTemp+'</a>',
                catsTemp = this.$$('#template-suggest-row-category').html(),
                catsTempWrapepr = '<li><a href="{{=it.uri}}?queryFromSuggest=true" class="suggest-row">'+catsTemp+'</a></li>',
                mansTemp = this.$$('#template-suggest-row-manufacturer').html(),
                mansTempWrapepr = '<li><a href="{{=it.uri}}?queryFromSuggest=true" class="suggest-row">'+mansTemp+'</a></li>',
                searchTermTemp = this.$$('#template-suggest-row-searchTerm').html(),
                searchTermTempWrapepr = '<li><a href="/search?q={{=it.searchTermUrl}}&queryFromSuggest=true" class="suggest-row js-search-term">'+searchTermTemp+'</a></li>',
                termsTemp = this.$$('#template-suggest-row-terms').html(),
                termsTempWrapepr = '<li><a href="{{=it.uri}}?queryFromSuggest=true" >'+termsTemp+'</a></li>';

            this.rowTpl = {
                prods: doT.template(prodsTempWrapepr),
                cats: doT.template(catsTempWrapepr),
                mans: doT.template(mansTempWrapepr),
                searchTerm: doT.template(searchTermTempWrapepr),
                terms: doT.template(termsTempWrapepr)
            };
            this.$target = this.$$('#suggest-target');
            this.model.init(this);
            this.isFadeAllowed = true;
            
        },

        model: {
            init: function (mod) {
                this.mod = mod;
                this.dict = $.proxy(this.dict, this);
            }

            , query: null
            , data: null
            , sections: ['prods', 'cats', 'mans','searchTerm', 'terms'],

            dictionary: {

                productName: 'prods'
                , category: 'cats'
                , brand: 'mans'

                , prods: 'productName'
                , cats: 'category'
                , mans: 'brand'
                , search:'search'
                , terms: 'terms'
            
                , list: 'list'
                , searchTerm: 'searchTerm'

                , productCode: 'masterId'
                , productCodeErp: 'id'
                , productTitle: 'Title'
                , name: 'name'
                , manufacturer: 'Manufacturer'
                , typeName: 'TypeName'
                , salesUnit: 'SalesUnit'
                , categories: 'categories'
                , hits: 'count'
                , currencyIso: 'currencyIso'
                , price: 'price'
                , priceFormattedValue: 'formattedPriceValue'
                , minQuantity: 'ItemsMin'
                , step: 'ItemsStep'
                , uri: 'url'
                , imgUri: 'img_url'
                , superCategory: 'parentCategory'
                , l2: 'l2'

                , energyEfficiencyData: 'energyEfficiency'
                , energyEfficiencyClasses: 'Energyclasses_LOV'
                , energyTopText: 'calc_energylabel_top_text'
                , energyBottomText: 'calc_energylabel_bottom_text'
                , energyClassesBuiltInLed: 'Energyclasses_built-in_LED_LOV'
                , energyClassesIncludedBulb: 'energyclasses_included_bulb_LOV'
                , energyClassesFitting: 'Energyclasses_fitting_LOV'
                , energyConsumption: 'Leistung_W'

                , manufacturerUrl: 'ManufacturerUrl'
                , productURL: 'ProductURL'
                , imageURLs: 'AdditionalImageURLs'
                , prices: 'Price'
                , categoryExtensions: 'CategoryExtensions'
                , sourceField: 'sourceField'

            }

            , dict: function (localKey) {
                return this.dictionary[localKey];
            }

            ,isObjEmpty: function (obj) {
                for(var key in obj) {
                    if(obj.hasOwnProperty(key))
                        return false;
                }
                return true;
            }

            , populate: function (input) {
                var dict = this.dict;
                var suggestions = (input.result.suggestions) ? input.result.suggestions : input.result;
                var fusionSearch = (input.type === 'fusionResult') ? true : false;
                this.data = {
                    totalHits: 0,
                    prods: {list: []},
                    cats: {list: []},
                    mans: {list: []},
                    searchTerm: {list: []},
                    terms: {list: []}
                };
                
                // where fusion search is active loop over sections & entries
                if(fusionSearch === true) {
                    // split out response data into correct indexes
                    var responseData = {
                        prods: suggestions.response.docs,
                        cats: (suggestions.response.categorySuggestions) ? suggestions.response.categorySuggestions : 'No results',
                        mans: (suggestions.response.manufacturerSuggestions) ? suggestions.response.manufacturerSuggestions : 'No results',
                        searchTerm: (suggestions.response.term_suggestions) ? suggestions.response.term_suggestions : 'No results'
                    };
                    var currency = $('#backenddata .shopsettings').data('currency');

                    for(var key in responseData) {
                        // if a key is empty skip it
                        if(responseData[key] !== 'No results') {
                            for(var entry in responseData[key]) {
                                var row = this.fusionRowSourcetoRow(key, responseData[key][entry], currency);
                                
                                // where a valid row is returned, add it to the key's list
                                if(row) {
                                    this.data[key].list.push(row);
                                    this.data.totalHits++;
                                }
                            }
                        }
                    }
                }
                else {
                    $.each(suggestions, $.proxy(function (index, suggestion) {
                        if (dict(suggestion.type) !== undefined) {
                            var data = this.data;
                            var sectionName = dict(suggestion.type);
    
                            suggestion.attributes.name = suggestion.name;
    
                            if (sectionName === 'searchTerm') {
                                // For searchTerm object, we dont have deepLink from FF, we should add it as param to our search url later
                                suggestion.attributes.searchTerm = encodeURI(suggestion.attributes.name);
                            }
    
                            var row = this.rowSourceToRow(sectionName, suggestion.attributes, suggestion.searchParams);
    
                            if (row) {
                                this.data[sectionName].list.push(row);
                                data.totalHits++;
                            }
                        }
    
                    }, this));
                }

                window.dataLayer.push({
                    event: 'suggestedSearch',
                    searchTerm: $('#metahd-search')[0].value
                });
            },

            rowSourceToRow: function (sectionName, rowData, searchParams) {
                var dict = $.proxy(this.dict, this);
                var row = {};
                var self = this;

                row.name = rowData[dict('name')];
                row.uri = rowData[dict('uri')];
                row.imgUri = rowData[dict('imgUri')];

                if (sectionName == 'prods') {
                    row.name = rowData[dict('productTitle')];
                    row.productCode = rowData[dict('productCode')];
                    row.productCodeErp = rowData[dict('productCodeErp')];

                    if (row.productCodeErp.length === 8) {
                        row.productCodeErp = row.productCodeErp.substring(0, 3) + '-' + row.productCodeErp.substring(3, 5) + '-' + row.productCodeErp.substring(5, 8);
                    }

                    row.uri = rowData[dict('productURL')];
                    var images = JSON.parse(rowData[dict('imageURLs')]);
                    row.imgUri = images.landscape_small;
                    if ( self.isObjEmpty(images) ) {
                        var $countryisocode = $('body').data('isocode');
                        if ( $countryisocode == 'DK' || $countryisocode == 'FI' || $countryisocode == 'NO' || $countryisocode == 'SE' || $countryisocode == 'LT' || $countryisocode == 'LV' || $countryisocode == 'EE' || $countryisocode == 'NL' || $countryisocode == 'PL') {
                            row.imgUri = '/_ui/all/media/img/missing_landscape_small-elfa.png';
                        } else {
                            row.imgUri = '/_ui/all/media/img/missing_landscape_small.png';
                        }

                    }
                    row.typeName = rowData[dict('typeName')];
                    row.typeNameURIEncoded = encodeURI(rowData[dict('typeName')]);
                    row.salesUnit = rowData[dict('salesUnit')];
                    row.manufacturer = rowData[dict('manufacturer')];
                    row.manufacturerURIEncoded = encodeURI(rowData[dict('manufacturer')]);

                    row.energyEfficiencyData = [];

                    if (typeof rowData[dict('energyEfficiencyData')] != 'undefined') {
                        if (rowData[dict('energyEfficiencyData')].indexOf('{') === 0) {
                            var energyEfficiency = JSON.parse(rowData[dict('energyEfficiencyData')]);

                            row.energyEfficiencyData[0] = energyEfficiency[dict('energyEfficiencyClasses')] || '';
                            row.energyEfficiencyData[1] = energyEfficiency[dict('energyConsumption')] || '';
                            row.energyTopText = energyEfficiency[dict('energyTopText')] || '';
                            row.energyBottomText = energyEfficiency[dict('energyBottomText')] || '';
                            row.energyClassesBuiltInLed = energyEfficiency[dict('energyClassesBuiltInLed')] || '';
                            row.energyClassesIncludedBulb = energyEfficiency[dict('energyClassesIncludedBulb')] || '';
                            row.energyClassesFitting = energyEfficiency[dict('energyClassesFitting')] || '';
                            row.isLamp = (row.energyClassesFitting.length || row.energyClassesBuiltInLed.length);
                        } else {
                            row.energyEfficiencyData = rowData[dict('energyEfficiencyData')].split(';');

                            if (row.energyEfficiencyData.length === 2 && typeof row.energyEfficiencyData[1] === 'string') {
                                row.energyEfficiencyData[1] = row.energyEfficiencyData[1].substring(0, row.energyEfficiencyData[1].indexOf(' W'));
                            }

                        }

                    }

                    var prices = rowData[dict('prices')].split('|');
                    var priceData = ($('.shopsettings').data('channel-label') === 'B2C' ? prices[1] : prices[2]).split(';'); //Gross prices for B2C, net for B2B

                    row.currencyIso = priceData[0];
                    row.price = (priceData[2].split('='))[1];

                    var priceParts = row.price.split('.');

                    if (priceParts.length === 1) {
                        priceParts[1] = '00';
                    }

                    if (priceParts[0].length === 0) {
                        priceParts[0] = '0';
                    }

                    if (priceParts[1].length < 2) {
                        priceParts[1] += '00'.substring(priceParts[1].length);
                    }

                    row.price = priceParts.join('.');

                    if (priceParts[1].length > 4) {
                        row.price = parseFloat(row.price).toFixed(4);
                    }

                    row.minQuantity = (function (minQuantity) {
                        return $.isNumeric(minQuantity) ? minQuantity : 1;
                    })(rowData[dict('minQuantity')]);

                    row.step = (function (step) {
                        return $.isNumeric(step) ? step : 1;
                    })(rowData[dict('step')]);

                    row.categories = rowData[dict('categories')];

                } else if (sectionName == 'cats') {
                    var field = parseInt(rowData[dict('sourceField')].charAt(8));

                    if (!$.isNumeric(field) || field < 1) {
                        return false;
                    }

                    var extensions;

                    if (rowData[dict('categoryExtensions')] !== undefined) {
                        extensions = JSON.parse(rowData[dict('categoryExtensions')].replace(/, ?]/, ']')); // Note: the categoryExtensions field received from FactFinder is not well formated JSON, there's an extra comma at the end
                        row.uri = extensions[field - 1].url;
                        row.superCategory = rowData[dict('superCategory')];
                        row.l2 = rowData[dict('l2')];
                    }

                }
                
                else if (sectionName == 'searchTerm') {
                    row.searchTermUrl = rowData.searchTerm;
                }

                else if (sectionName == 'mans') {
                    row.uri = rowData[dict('manufacturerUrl')];
                }

                if (sectionName == 'cats' || sectionName == 'mans') {
                    row.hits = rowData[dict('hits')];
                }
                
                return row;
            },

            fusionRowSourcetoRow: function (sectionName, rowData, currency) {
                var row = {};
                var country = $('#backenddata .shopsettings').data('country');
                
                if(sectionName == 'prods') {
                    row.name = rowData.title;
                    row.uri = rowData.productUrl;
                    row.imgUri = rowData.imageURL;
    
                    row.productCode = rowData.productNumber;
                    var productCodeErp = rowData.productNumber.split('/');
                    row.productCodeErp = productCodeErp[productCodeErp.length - 1];
                    
                    if (row.productCodeErp.length === 8) {
                        row.productCodeErp = row.productCodeErp.substring(0, 3) + '-' + row.productCodeErp.substring(3, 5) + '-' + row.productCodeErp.substring(5, 8);
                    }
                    
                    // check if images defined, replace with default picture if not
                    if(!row.imgUri) {
                        var $countryisocode = $('body').data('isocode');
                        if ( $countryisocode == 'DK' || $countryisocode == 'FI' || $countryisocode == 'NO' || $countryisocode == 'SE' || $countryisocode == 'LT' || $countryisocode == 'LV' || $countryisocode == 'EE' || $countryisocode == 'NL' || $countryisocode == 'PL') {
                            row.imgUri = '/_ui/all/media/img/missing_landscape_small-elfa.png';
                        } else {
                            row.imgUri = '/_ui/all/media/img/missing_landscape_small.png';
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

                    if(row.energyEfficiencyData.length < 1) {

                        // check we didn't catch the data previously, just in case there is legacy code

                        if(typeof rowData.energyEffiency != 'undefined') {
                            var energyEfficiencyParsed = JSON.parse(rowData.energyEffiency);
                            for( var prop in energyEfficiencyParsed ){
                                row.energyEfficiencyData.push(energyEfficiencyParsed[prop]);
                            }
                        }

                    }

                    row.currencyIso = currency;
                    row.price = $('.shopsettings').data('channel-label') === 'B2C' ? rowData.singleMinPriceGross : rowData.singleMinPriceNet;
    
                    var priceParts = row.price.toString().split('.');
    
                    if (priceParts.length === 1) {
                        priceParts[1] = '00';
                    }
            
                    if (priceParts[0].length === 0) {
                        priceParts[0] = '0';
                    }
            
                    if (priceParts[1].length < 2) {
                        priceParts[1] += '00'.substring(priceParts[1].length);
                    }

                    row.price = priceParts.join('.');
                    if (priceParts[1].length > 4) {
                        row.price = parseFloat(row.price).toFixed(4);
                    }
                    if(country.toLowerCase() === 'se'){
                        row.price = row.price.replace('.', ',');
                    } 
                    
                    row.minQuantity = (function (minQuantity) {
                        return $.isNumeric(minQuantity) ? minQuantity : 1;
                    })(rowData.itemMin);
    
                    row.step = (function (step) {
                        return $.isNumeric(step) ? step : 1;
                    })(rowData.itemStep);
                }
    
                else if (sectionName == 'cats') {
                    row.name = rowData.name;
                    row.uri = rowData.url;
                }
    
                else if (sectionName == 'searchTerm') {
                    row.name = rowData.name;
                    row.searchTermUrl = rowData.url;
                }
    
                else if (sectionName == 'mans') {
                    row.name = rowData.name;
                    row.uri = rowData.url;
    
                    if(rowData.imageURL) {
                        row.imgUri = rowData.imageURL;
                    }
                }
                
                return row;
            },

        },

        on: function (callback) {
            var searchExperience = $('#backenddata .shopsettings').data('search-experience');
            
            $(document).on('search', $.proxy(function (ev, data) {

                if (data !== undefined) {
                    switch (data.type) {
                        case 'invalidTerm':
                            this.onInvalidTerm(data);
                            break;
                        case 'newResult':
                        // adding fusion result handled here, data passed through to onNewResult
                        case 'fusionResult':
                            this.onNewResult(data);
                            break;
                        case 'blur':
                            this.onBlurSearch(data);
                            break;

                        default:
                            throw('event type is not matched:' + data.type);
                    }

                }

            }, this));

            $(document).on('click', function(ev) {
                var target = $( ev.target );
                if(target.hasClass('search-suggestion') === true && searchExperience !== 'factfinder') {
                    var query = target.attr('title');
                    var fusionUrl = localStorage.getItem('fusion-query-url');
                    var pieces = fusionUrl.split('&');
                    var updatedUrl;

                    for(var i=0; i<pieces.length; i++) {
                        if(pieces[i].includes('q=')) {
                            var paramPieces = pieces[i].split('=');
                            paramPieces[1] = query;
                            pieces[i] = paramPieces.join('=');
                        }
                    }

                    updatedUrl = pieces.join('&');
                    localStorage.setItem('fusion-query', query);
                    localStorage.setItem('fusion-query-url', updatedUrl);
                }
            });

            callback();
        },


        after: function () {

        },

        onInvalidTerm: function (data) {
            this.hideParentScroll(false);
            this.clear();
        },

        onNewResult: function (input) {
            var model = this.model;

            model.populate(input);
            
            if (!model.data.totalHits) {
                this.clear();
                return;
            }

            this.renderContainer();
            this.isFadeAllowed = false;

            $.each(this.model.sections, $.proxy(function (index, sectionName) {
                this.renderRows(sectionName);
            }, this));

            this.bindRowClick();

            this.checkEmptyLeftSuggestions(this.model.sections);

            var $showAllLink = this.$$('.link-show-all');
            var searchUri = $showAllLink.data('search-uri');
            var searchTerm = this.model.data.query;

            $showAllLink.attr('href', searchUri + searchTerm);

            this.carts = this.sandbox.addModules(this.$$('.product-cart'));

            var selectedCatName = $(".metahd-select option:selected").text();
            $('.suggest-section .suggest-category-text').html(selectedCatName);

            this.hideParentScroll(true);

        },

        hideParentScroll: function (_isShow) {

            if ($(window).width() < 767) {
                if (_isShow) {
                    $('body,html').css('overflow','hidden');
                } else {
                    $('body,html').css('overflow','auto');
                }
            }

        },

        checkEmptyLeftSuggestions: function (sections) {
            var self = this;
            var emptySectionsCounter = 0;
            var noProductSuggestions = false;

            $.each(sections, function (index, sectionName) {
                var sectionData = self.model.data[sectionName].list;

                if (sectionData.length === 0 && sectionName !== 'prods') {
                    emptySectionsCounter++;
                }

                if (sectionData.length === 0 && sectionName == 'prods') {
                    noProductSuggestions = true;
                }

            });

            if (emptySectionsCounter === 4) {
                $('.overlay-suggest').addClass('active');

                this.$$('.left').addClass('hidden');
                this.$$('.container-suggest.row').css('max-width', '672px');
                this.$$('.right').css('border-left', '1px solid #cccccc');
                this.$$('.title-text').css('width', '380px');

                if ($(window).width() < 767) {

                    var windowHeight = $(window).height();
                    var magic = windowHeight - 138;

                    $('.mod-metahd-suggest .container-suggest').css('max-height', magic);

                }

            } else {
                $('.overlay-suggest').addClass('active');
            }

            if (noProductSuggestions) {
                this.$$('.right').addClass('hidden');
                this.$$('.content').css('width', '42%');
                this.$$('.left').css('width', '100%').css('display', 'table');
            }

        },


        onBlurSearch: function (data) {
            var self = this;

            self.clear();
            self.isFadeAllowed = true;
            this.hideParentScroll(false);
        },

        onEnergyLabelClick: function (ev) {
            var $ctx = this.$ctx;
            var self = this;

            ev.preventDefault();
            ev.stopPropagation();

            if ($('.container-suggest').height() > 500) {
                var _elem = $('.container-suggest .prods .js-results');

                _elem.css('overflow-y', 'auto');
                _elem.animate({scrollTop: _elem[0].scrollHeight}, 50);
            }

            $.each($('.suggest-row .energy-label-manufacturer'), function () {

                if ($(this).html() === 'undefined') {
                    $(this).html('');
                }

            });

            var $a = $(ev.target).closest('span');
            var $label = $a.parent().find('.energy-label-popover');
            var $lampLabel = $label.children('.lamp');

            if ($lampLabel.length) {
                var energyClassesLed = $a.data('energyClassesLed');
                var energyClassesBulb = $a.data('energyClassesBulb');
                var energyClassesIncludedBulb = $a.data('energyClassesIncludedbulb');
                var $textBottom = $label.find('.energy-label-text-bottom');
                var $textBottomClassText = $label.find('.energy-label-text-bottom .energy-class-text');
                var energyClasses = {'a++': 0, 'a+': 0, 'a': 0, 'b': 0, 'c': 0, 'd': 0, 'e': 0};
                var energyClassesColor = {'a++':'#108e2f','a+':'#50a328','a':'#bccb00','b':'#fae80b','c':'#f5ac00','d':'#e24f13','e':'#d8021b'};
                var $energyClassLable = "";
                var $energyLableCrossStr = "";
                var $energyLableCrossArr = [];
                var $energyLableCross = [];

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
                    $textBottom.html($textBottom.html().replace(/<energylabel>(.+)<\/energylabel>/, '<span class="ico-energy $1" title="$1"><i></i></span>'));
                }

                if (energyClassesLed.length) {

                    $.each(energyClassesLed.split(';'), function (i, val) {
                        valLower = val.toLowerCase();
                        energyClasses[valLower] = 1;
                    });

                    var ledValue = energyClassesLed.split(';');
                    var ledclass = (ledValue[0] + "--" + ledValue[ledValue.length - 1]).toLowerCase();
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

                    $.each(energyClassesBulb.split(';'), function (i, val) {
                        valLower = val.toLowerCase();
                        energyClasses[valLower] = 1;
                    });

                    var bulbValue = energyClassesBulb.split(';');
                    var bulbclass = (bulbValue[0] + "--" + bulbValue[bulbValue.length - 1]).toLowerCase();
                    var bulbTopValue = bulbValue[0].toLowerCase();

                    $lampLabel.append('<div class="energy-label-bulb bulbclass ' + bulbTopValue + ' ' + bulbclass + '">' +
                        '<div class="bracket-wrapper">\n' +
                        '  <span class="bracket"></span>\n' +
                        '  <span class="bulb-img"></span>\n' +
                        '</div></div>');

                }

                if (energyClassesLed.split(';').length < energyClassesBulb.split(';').length) {
                    $('.energy-label-bulb').addClass('max-left');
                }

                $.each(energyClasses, function (key, val) {

                    if (val === 0) {
                        $energyLableCross.push(key);
                    } else {
                        $energyLableCross.push('$');
                    }

                });

                $energyLableCrossStr = $energyLableCross.filter(function (item, pos, arr) {
                    return pos === 0 || item !== arr[pos - 1];
                });

                $energyLableCrossArr = $energyLableCrossStr.toString().split(',$,');

                $.each($energyLableCrossArr, function (key, val) {

                    val = val.toString().split(',').join('--').replace("--$", "").replace("$--", "");

                    var crossVal = val.split('--');
                    var crossFirstVal = crossVal[0];
                    var crossLastVal = crossVal[crossVal.length - 1];
                    var crossClass = crossFirstVal + '--' + crossLastVal;

                    if (crossClass.toString().indexOf('$') <= -1) {
                        $lampLabel.append('<div class="energy-label-cross crossclass ' + crossFirstVal + ' ' + crossClass + '">' +
                            '<div class="cross-icon-wrapper"><span class="cross-icon"></span></div>\n' +
                            '</div>');
                    }

                });

                $energyClassLable = $('.energy-label-text-bottom .energy-class-arrow-text').html();

                if ($energyClassLable !== '' && $energyClassLable !== undefined) {
                    $('.energy-label-text-bottom .energy-class-text').html($textBottomClassText.text().split(':')[0] + ':');

                    var colorCodeVar = $energyClassLable.toString().trim().toLowerCase();

                    $('.energy-label-text-bottom .status-arrow').css('fill', energyClassesColor[colorCodeVar]);

                } else {
                    $('.energy-class-arrow').css('display', 'none');

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

            $('.mod-layout').on('click.eel', function (ev) {
                ev.preventDefault();
                ev.stopPropagation();
                $('.energy-label-popover').addClass('hidden');
                $('.mod-layout').off('click.eel');
            });

        },

        carts: [],

        renderContainer: function () {
            var renderPhase = true;

            this.clear(renderPhase);

            if (this.isFadeAllowed) {
                this.$target
                    .hide().fadeIn('fast')
                    .html(this.uiTpl(this.model.data));
            }

            else {
                this.$target.html(this.uiTpl(this.model.data));
            }

        },


        renderRows: function (sectionName) {
            var sectionData = this.model.data[sectionName].list;
            var aRowHtml = $.map(sectionData, $.proxy(function (rowData, index) {
                    rowData.position = index + 1;

                    // Binding data to JS-template
                    return this.rowTpl[sectionName](rowData);

                }, this));

            if (sectionData.length === 0 && sectionName !== 'prods') {
                this.$$('.suggest-section.' + sectionName + '').addClass('hidden');
            }

            this.$$('.suggest-section.' + sectionName + ' .js-results')
                .empty()
                .html(aRowHtml.join(' \n'));

        },


        bindRowClick: function () {
            this.$$('.ico-energy').on('click', $.proxy(this.onEnergyLabelClick, this));
        },

        clear: function (renderPhase) {
            var self = this;
            var event = 'suggestClosed', toInterestedParties = 'metahdSearch';

            self.carts.length && self.sandbox.removeModules(self.carts);
            self.$target.empty();

            if (!renderPhase) {
                self.isFadeAllowed = true;
                self.fire(event, {}, [toInterestedParties]);
            }

        }

    });

})(Tc.$);
