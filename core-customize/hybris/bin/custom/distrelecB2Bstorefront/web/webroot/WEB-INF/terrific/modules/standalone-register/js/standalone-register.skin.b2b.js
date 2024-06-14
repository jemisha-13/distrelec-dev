(function($) {

    /**
     * Module implementation.
     *
     * @namespace Tc.Module
     * @class Product
     * @extends Tc.Module
     */

    Tc.Module.StandaloneRegister.B2b = function (parent) {

        /**
         * Hook function to do all of your module stuff.
         *
         * @method on
         * @param {Function} callback function
         * @return void
         */
        this.on = function (callback) {

            var $ctx = this.$ctx;
            var	errorSelf = this;

            this.validationErrorEmail = this.$$('#tmpl-bisnodereg-validation-error-email').html();
            this.validationErrorEmpty = this.$$('#tmpl-bisnodereg-validation-error-empty').html();

            axios.defaults.headers.common = {
                'X-Requested-With': 'XMLHttpRequest',
                'X-CSRF-TOKEN': document.querySelector('meta[name="_csrf"]').getAttribute('content')
            };

            function showValidVATInput(input) {
                input.siblings('.fa-times').addClass("hidden");
                input.siblings('.fa-check').removeClass("hidden");

                $('.grouped__prefilled').removeClass('error');
                $('.prefilled').removeClass('error');

                $('.grouped__prefilled').addClass('active');
                $('.prefilled').addClass('active');
            }

            function showInvalidVATInput(input) {
                input.parent().find('.fa-times').removeClass("hidden");
                input.siblings('.fa-check').addClass("hidden");

                $('.grouped__prefilled').removeClass('active');
                $('.prefilled').removeClass('active');

                $('.grouped__prefilled').addClass('error');
                $('.prefilled').addClass('error');
            }

            var setVatIdError = function (country) {
                var error = $("#vatId").siblings(".error-message")
                    .not(".error-message-required")
                    .find("span");

                var message = error.attr("data-"+country+"-error");
                error.text(message);
            };

            var setVatIdHint = function(country){
                var popup = $("#vatId").siblings(".inputPopup")
                    .find(".inputPopup__popup");
                var message = popup.attr("data-"+country+"-hint");
                popup.text(message);
            };

            var setVatIdPlaceholder = function(country){
                var input = $("#groupVatId");
                var placeholder = input.attr("data-"+country+"-placeholder");
                input.attr("placeholder", placeholder);
            };

            var countrySelector = $("#register\\.countryCode");
            var preselectedCountry = countrySelector.val();
            if(preselectedCountry){
                setVatIdHint(preselectedCountry);
                setVatIdError(preselectedCountry);
                setVatIdPlaceholder(preselectedCountry);
            }

            countrySelector.change(function(){
                var selectedCountry = $(this).val();
                var vatIdPrefix = selectedCountry;
                if(selectedCountry === "GR"){
                    vatIdPrefix = "EL";
                }
                setVatIdHint(selectedCountry);
                setVatIdError(selectedCountry);
                setVatIdPlaceholder(selectedCountry);
                $("#vatPreValue").text(vatIdPrefix);
            });

            var countryB2BSelector = $("#register\\.countryCodeB2B");
            countryB2BSelector.change(function(){
                var selectedCountry = $(this).val();
                var vatIdPrefix = null;
                setVatIdPlaceholder(selectedCountry);
                switch (selectedCountry){
                    case "CH": vatIdPrefix = "CHE";
                        break;
                    case "SM": vatIdPrefix = "SM";
                        break;
                    case "IT": vatIdPrefix = "IT";
                        break;
                }
                if(vatIdPrefix){
                    var vatPrefix = $("#vatPreValue");
                    vatPrefix.text(vatIdPrefix);
                    vatPrefix.removeAttr("data-ignore");
                }
                setVatIdHint(selectedCountry);
            });

            // parameter to handle user redirection
            if(window.location.href.indexOf('/registration/checkout') > -1) {
                document.getElementById('regType').value = "checkout";
                digitalData.page.pageInfo.registration.checkout="yes";
                digitalData.page.pageInfo.registration.source = 'checkout';
            } else {
                digitalData.page.pageInfo.registration.checkout="no";
            }

            // Set Existing Customer Value to Null
            digitalData.user[0].registration.existing='';
            populateUserInformation();

            // parameter to see where user registers from
            if(window.location.href.indexOf('registerFrom=') > -1) {
                var userRegisteredFrom = window.location.href.toString().split("registerFrom=").pop().toString();
                digitalData.page.pageInfo.registration.source = userRegisteredFrom;
            }

            if(localStorage.getItem('datalayerReg') === null) {
                digitalData.user[0].companyMatch = '';
            }

            //if there is an error open step 2 and assing values for hidden inputs
            if((document.querySelectorAll('.bd.error').length !== 0)) {
                document.querySelectorAll('.step-toggle')[0].classList.remove('hidden');

                if (document.getElementById('customerNum') !== null) {
                    if(document.querySelectorAll('.form-b2b__form__field--invoice a')[0] !== undefined) {
                        document.querySelectorAll('.form-b2b__form__field--invoice a')[0].classList.add('hidden');
                    }
                }

                if(localStorage.getItem('datalayerReg') !== null) {
                    digitalData.user[0] = JSON.parse(localStorage.getItem('datalayerReg'));
                }

                if(localStorage.getItem('datalayerPageInfo') !== null) {
                    digitalData.page.pageInfo.register = JSON.parse(localStorage.getItem('datalayerPageInfo'));
                }

                if(digitalData.page.pageInfo.countryCode === 'IT') {

                    if(document.querySelectorAll('.codiceDest')[0] !== undefined) {

                        if(document.getElementById('legalEmail').value.length > 0) {
                            document.querySelectorAll('label[for="italianVatNo"]')[0].click();
                        } else {
                            document.querySelectorAll('label[for="italianVatYes"]')[0].click();
                        }


                    }

                }

                if (document.getElementById('vatIdMessage').value !== '') {
                    var vatError = document.querySelectorAll('.compVat .error-message')[1];
                    var vatErrorText = document.querySelectorAll('.compVat .error-message span')[1];
                    var vatTick = document.querySelectorAll('.compVat .tickItem')[1];
                    var newVatError = document.getElementById('vatIdMessage').value;

                    vatError.classList.remove('hidden');
                    vatErrorText.innerHTML = newVatError;
                    vatTick.classList.add('fa-times');
                    vatTick.classList.remove('fa-check');
                }

                if ($('.form-b2b__companydetails.card-wrapper').hasClass('isBiz')) {
                    var countryErrorSelection = $(".form-b2b__companydetails.card-wrapper .countrySelectExportShop").val();

                    $.ajax({
                        url: '/_s/shopsettings-async',
                        type: 'get',
                        data: {
                            country: countryErrorSelection
                        },
                        success: function (data, textStatus, jqXHR) {
                        },
                        error: function (jqXHR, textStatus, errorThrown) {
                        }
                    });

                }

                var preOpenFields = function(storageName,element) {

                    var invoice = $('.form-b2b__form__field--invoice').find('a');
                    invoice.removeClass('hidden');

                    if(storageName === null || storageName === undefined) {
                        element.next().addClass('hidden');
                        element.next().val();
                        return;
                    }

                    element.next().removeClass('hidden');
                };

            }

            var count = 0,
                formWrapperInput = $('.form-b2b .form-b2b__form__field input'),
                formWrapperSelect = $('.form-b2b .form-b2b__form__field select'),
                customerNumInput = $('.form-b2b__companydetails__input input'),
                selectCountry = document.querySelectorAll('.countryPicker')[0],
                countryOption = null;

            if(document.querySelectorAll('.isBiz')[0] !== undefined) {
                var countryOptions = document.getElementById("register.countryCode");
                var selectedCountry = countryOptions.options[countryOptions.selectedIndex].value;

                if(selectedCountry === ''){
                    document.getElementById('bisnode').disabled = true;
                }

                else document.getElementById('bisnode').disabled = false;

                document.querySelectorAll('.countryPicker')[0].onchange = function () {

                    if(document.querySelectorAll('.isBiz')[0] !== undefined) {
                        document.getElementById('bisnode').disabled = false;
                        countryOption = countryOptions.options[countryOptions.selectedIndex].value;
                    }
                };
            }



            function addSelected() {
                var select = document.querySelectorAll('.countrySelectExportShop');
                for(var i = 0; i < select.length; i++) {

                    for(var j = 0; j < select[i].length; j++) {
                        if(select[i][j].value === digitalData.page.pageInfo.countryCode) {
                            select[i][j].selected = 'selected';
                        }
                    }

                }
            }

            if(digitalData.page.pageInfo.countryCode === 'CH' || digitalData.page.pageInfo.countryCode === 'LI') {
                addSelected();
            }
            if(digitalData.page.pageInfo.countryCode === 'IT' || digitalData.page.pageInfo.countryCode === 'SM' || digitalData.page.pageInfo.countryCode === 'VA') {
                addSelected();
            }

            $('input').keydown(function(e){
                if(e.keyCode === 13) {
                    e.preventDefault();
                }
            });

            $(document).on("click","#bisnode",function(e) {
                count++;

                if (count === 1) { //fix for searching multiple times it was making duplicate calls and creating vue instance more than once

                    e.stopPropagation();
                    e.stopImmediatePropagation();

                    var input = document.querySelector('#bisnode'),
                        bisCountryIso =  (document.querySelectorAll('.isBiz')[0] !== undefined ? countryOption : digitalData.page.pageInfo.countryCode),
                        bisUrl = window.location.origin + '/bisnode/companyinfo/typeahead?companyName=',
                        typeTimer;

                    var typeHandler = function () {
                        bisSearch = input.value;
                        buildResults.callService(bisSearch);
                    };

                    var buildResults = new Vue({
                        el: '#bisnoderesults',
                        data: {
                            items: [],
                            countryCode: digitalData.page.pageInfo.countryName,
                            duns: []
                        },
                        methods: {
                            callService: function (value) {

                                if (Array.isArray(digitalData.user) || digitalData.user.length) {
                                    digitalData.user[0].companyReg='new';
                                    digitalData.user[0].companyMatch='no match';
                                }

                                var self = this,
                                    bisResults = document.querySelectorAll('.bisnode__results')[0],
                                    valueEncoding = encodeURIComponent(value),
                                    bisnodeTimeout = input.getAttribute('data-bisnode-timeout'),
                                    responseSuccess = false;

                                if (document.querySelectorAll('.isBiz')[0] !== undefined) {
                                    bisCountryIso = document.querySelectorAll('.countryPicker')[0][selectCountry.selectedIndex].value;

                                    document.querySelectorAll('.countryPicker')[0].onchange = function () {
                                        document.getElementById('bisnode').disabled = false;
                                        bisCountryIso = this.options[selectCountry.selectedIndex].value;
                                    };
                                }

                                setTimeout(function () {
                                    if (!responseSuccess) {
                                        self.items = [];
                                    }
                                }, bisnodeTimeout);

                                axios.post(bisUrl + valueEncoding + '&countryCode=' + bisCountryIso)
                                    .then(function (response) {
                                        bisResults.classList.remove('d-none');
                                        bisResults.classList.remove('hidden');
                                        self.items = response.data;
                                        responseSuccess = true;

                                        if(document.querySelectorAll('.isBiz')[0] !== undefined) {
                                            var currentCountryOption = $("#register.countryCode");
                                            if (currentCountryOption.hasClass('error')) {
                                                ('$continueStep').disabled = true;
                                            } else {
                                                ('$continueStep').disabled = false;
                                            }
                                        }
                                    });
                            },

                            duplicateCompany: function (duns) {
                                bisCountryIso =  (document.querySelectorAll('.isBiz')[0] !== undefined ? countryOption : digitalData.page.pageInfo.countryCode);

                                var self = this,
                                    dunsCheck = window.location.origin + '/bisnode/isDunsPresent?duns=' + duns[0].innerHTML + '&countryCode=' + currentCountry(),
                                    getCompanyName = $('.bisnode__results__wrapper__item.animateActive').find('.primaryActive').html(),
                                    registeredInnerText = $('.infobox__text p'),
                                    bisResult = $('.bisnode__results__additional');

                                axios.post(dunsCheck)
                                    .then(function (response) {
                                        self.duns = response.data;
                                        if (Array.isArray(digitalData.user) || digitalData.user.length) {
                                            digitalData.user[0].companyMatch='bis company match';
                                        }
                                        if (self.duns.exist === true) { //if there is a duplicate company flag will return true
                                            if (Array.isArray(digitalData.user) || digitalData.user.length) {
                                                digitalData.user[0].companyReg = 'existing';
                                                digitalData.user[0].registration.existing='yes';
                                            }
                                            document.querySelectorAll('.step-toggle')[0].classList.add('hidden');
                                            document.querySelectorAll('.bisnode__results__duplicate')[0].classList.remove('hidden');
                                            bisResult.addClass('hidden');
                                        } else {
                                            digitalData.user[0].registration.existing='no';
                                        }
                                        if (self.duns.vat !== "") { //if vat number comes back give value to hidden input and hide vat field
                                            document.getElementById('vatId').value = self.duns.vat;
                                            document.querySelectorAll('.compVat')[0].classList.add('hidden');

                                            //US9: remove department field and remove additional changes title since no inputs are present
                                            if(!$('.bisnode__results__duplicate').is(":visible")) {
                                                $('#additional-changes-text').addClass("hidden");
                                            }
                                        }

                                    if (self.duns.organiationalNumber !== "" && document.getElementById('orgNumber') !== null) { //if org number comes back give value to hidden input
                                        document.getElementById('orgNumber').value = self.duns.organiationalNumber;
                                        document.querySelectorAll('.department')[0].classList.add('hidden');
                                    }

                                    });

                                //company name doesnt come back in response only present inside loop

                                registeredInnerText.html(registeredInnerText.html().replace('[0]', getCompanyName));

                            },

                            itemClick: function (index) {       // function for when bisnode returns a company and you select a company this handles the behavior in here

                                var resultItems = document.querySelectorAll('.bisnode__results__wrapper__item'),
                                    self = this.$refs["item-" + index][0],
                                    bisAdditional = document.querySelectorAll('.bisnode__results__additional')[0],
                                    dunsNumber = $(self).children('.dunsNum');
                                $('#groupVatId').val('');

                                for (var i = 0; i < resultItems.length; i++) {
                                    resultItems[i].classList.add('hidden');
                                }

                                self.classList.remove('hidden');
                                self.classList.add('active');
                                // DISTRELEC-26488: "companyNameInfoText" commented out for now
                                // $('#companyNameInfoText').addClass('hidden');
                                // $('#companyNameInfoText__duplicate').removeClass('hidden');
                                document.getElementById('bisnoderesults').classList.add('bisnode__results--shown');
                                document.getElementById('bisnoderesults').classList.add('bisnode__results--selected');

                                document.getElementById('bisnode').classList.add('hidden');
                                document.getElementById('showresults').classList.add('hidden');
                                document.getElementById('findCompany').classList.add('hidden');

                                digitalData.user[0].companyMatch='bis company match';
                                if (!self.classList.contains('animateActive') && document.querySelectorAll('.changeDetails.active').length === 0) {
                                    bisAdditional.classList.remove('hidden');
                                }

                                if (!self.classList.contains('animateActive')) {
                                    $(self).children('.tickItem').removeClass('hidden');
                                    $(self).children('.changeDetails').removeClass('hidden');
                                    scrollAnimation($('.form-b2b__companydetails'), 80, true);
                                    bisAdditional.classList.remove('hidden');

                                    if((document.querySelectorAll('.isBiz')[0] !== undefined)) {
                                        document.getElementById('register.countryCode').classList.add('disabled');
                                    }

                                }

                                self.classList.add('animateActive');
                                this.duplicateCompany(dunsNumber); //fire duplicate function on click of a company and pass duns number in to the function
                                document.getElementById('continueStep').classList.remove('hidden');
                                var companyInpCopy = document.querySelectorAll('.bisnode__results__wrapper__item.active > .address_section > .primaryActive')[0].innerHTML;
                                document.querySelectorAll('.companyInp')[0].value = companyInpCopy;
                                document.getElementById('duns').value = dunsNumber[0].innerHTML; //send duns number in form
                            },

                            customerlink: function (e) {
                                e.preventDefault();
                                document.querySelectorAll('.customernumber--helplink')[0].classList.remove('hidden');
                                document.querySelectorAll('.bisnode__results__duplicate .infobox')[0].classList.add('hidden');
                                var customerNumberInput = $('.bisnode__results__duplicate .customernumber');
                                customerNumberInput.children('.error-message').addClass('hidden');
                                // customerNumberInput.children('.tickItem').addClass('hidden');
                            },

                        findAccount: function (e) {
                            e.preventDefault();
                            var customernum = this.$refs.customernum.value;
                            var duns = document.getElementById('duns').value;
                            var custUrl = window.location.origin + '/bisnode/isCustomerPresent?customerId=' + customernum + '&duns=' +duns ;
                            var accountInput = document.getElementById('customerNum');
                            axios.post(custUrl)
                            .then(function (response) {

                                if(response.data.exist === true) {  //returns a flag if it exist it returns true
									scrollAnimation($('.form-b2b__companydetails__title:last'), 80, true);
									accountInput.classList.add('account_true');
									accountInput.classList.add('active');
                                    document.getElementById('customerNum').classList.add('pass');
									document.querySelectorAll('.step-toggle')[0].classList.remove('hidden');
									document.getElementById('existingCustomerb2b').value = true;
									document.getElementById('customerId').value = customernum;
									document.querySelectorAll('.findAccountButton')[0].classList.add('disabled');
                                    document.querySelectorAll('.form-b2b__form__field--invoice')[0].classList.add('hidden'); //hide invoice link for b2e existing
									genericPass(accountInput);
									step2.addClass('number--active');
									hasStep();
                                } else {
                                	 genericError(accountInput);
                                     return;
                                }

                                    });
                            },

                            stopSubmit: function(e) {
                                if(e.keyCode === 13) {
                                    e.preventDefault();
                                }
                            },

                            findCompany: function () {
                                document.getElementById('bisnode').classList.add('hidden');
                                document.getElementById('register.companyB2B').value = document.getElementById('bisnode').value;
                                document.getElementById('bisnoderesults').classList.add('hidden');
                                document.querySelectorAll("label[for='bisnode']")[0].classList.add('hidden');
                                document.querySelectorAll('.bisnode__results__existing')[0].classList.remove('hidden');
                                document.getElementById('radioSelectNoExisting').click();
                                document.querySelectorAll('.step-toggle')[0].classList.add('hidden');
                                document.getElementById('continueStep').classList.add('active');
                                document.querySelectorAll('.companyInp')[0].classList.remove('hidden');
                                document.querySelectorAll('.existingWrapper')[0].classList.add('hidden');
                                document.getElementById('continueStep').classList.remove('hidden');

                                if(document.getElementById('hiddenGroup') !== undefined && document.getElementById('hiddenGroup') !== null) {
                                    document.getElementById('hiddenGroup').classList.remove('hidden');
                                    document.getElementById('orgNumber').value = '';
                                }

                                // DISTRELEC-26488: "companyNameInfoText" commented out for now
                                // $('#companyNameInfoText').addClass('hidden');
                                document.getElementById('groupVatId').value = '';
                            },

                            resetItems: function(e) {
                                var instance = this.$refs['change' + e];
                                instance[0].classList.add('active');
                                document.getElementById('bisnode').value = '';
                                document.getElementById('bisnode').classList.remove('hidden');
                                document.getElementById('showresults').classList.remove('hidden');
                                document.getElementById('findCompany').classList.remove('hidden');
                                document.getElementById('bisnoderesults').classList.add('hidden');
                                document.querySelectorAll('.bisnode__results__additional')[0].classList.add('hidden');
                                document.querySelectorAll('.bisnode__results__duplicate')[0].classList.add('hidden');
                                document.querySelectorAll('.step-toggle')[0].classList.add('hidden');
                                document.getElementById('customerNum').classList.remove('pass');
                                document.getElementById('customerNum').value = "";
                                document.querySelectorAll('.findAccountButton')[0].classList.remove('disabled');
                                document.querySelectorAll('.customernumber .fa-check')[0].classList.add('hidden');
                                document.querySelectorAll('.bisnode__results')[0].classList.remove('bisnode__results--selected');

                                // DISTRELEC-26488: "companyNameInfoText" commented out for now
                                // $('#companyNameInfoText').removeClass('hidden');
                                // $('#companyNameInfoText__duplicate').addClass('hidden');

                                // var registeredTitle = $('.registeredTitle');
                                var registeredInnerText = $('.infobox__text p');
                                var getCompanyName = $('.bisnode__results__wrapper__item.animateActive').find('.primaryActive').html();

                                registeredInnerText.html(registeredInnerText.html().replace(getCompanyName, '[0]'));

                                if(document.querySelectorAll('.form-b2b__form__field--invoice')[0] !== undefined) {
                                    document.querySelectorAll('.form-b2b__form__field--invoice')[0].classList.remove('hidden');
                                }

                                if(document.querySelectorAll('.compVat')[0] !== undefined) {
                                    document.querySelectorAll('.compVat')[0].classList.remove('hidden');
                                    document.getElementById('vatId').value = "";
                                }

                                if((document.querySelectorAll('.isBiz')[0] !== undefined)) {
                                    document.getElementById('register.countryCode').classList.remove('disabled');
                                }

                                if(document.getElementById('orgNumber') !== null) {
                                    document.getElementById('orgNumber').value = '';
                                }
                                document.getElementById('duns').value = '';

                                if(document.getElementById('vatId') !== null && (document.querySelectorAll('.compVat')[0].classList.contains('isMandatory') === true)){
                                    document.getElementById('vatId').value = '';
                                }

                                this.items = [];
                            }

                        },

                        mounted: function() {
                            $('input').keydown(function(e){
                                if(e.keyCode === 13) {
                                    e.preventDefault();
                                }
                            });
                        }

                    });

                    var existingFlow = new Vue({
                        el: '#existingview',
                        methods: {

                            findAccount: function(e) {
                                e.preventDefault();
                                var getVal = document.getElementById('customerNumber'),
                                    companyName = document.querySelectorAll('.companyInp')[0],
                                    valueEncoding = encodeURIComponent(companyName.value);

                                document.querySelectorAll('.customerNumberError')[0].classList.add('hidden');

                                if(getVal.value.length === 0) {
                                    genericError(getVal);
                                } else {
                                    document.querySelectorAll('.customerNumberContainer .tickItem.fa-times')[0].classList.add('hidden');
                                    document.querySelectorAll('.customerNumberContainer .error-message')[0].classList.add('hidden');
                                }

                                if(companyName.value.length === 0) {
                                    genericError(companyName);
                                } else {
                                    document.querySelectorAll('.validCountCompany .form-group .fa-times')[0].classList.add('hidden');
                                    document.querySelectorAll('.validCountCompany .form-group .error-message')[0].classList.add('hidden');
                                }

                                if(getVal.value.length !== 0 && companyName.value.length !== 0) {

                                    var custUrl = window.location.origin + '/bisnode/isCustomerPresent?customerId=' + getVal.value + '&customerName=' + valueEncoding;

                                    axios.post(custUrl)
                                        .then(function (response) {

                                            if(response.data.exist === true) {  //returns a flag if it exist it returns true
                                                genericPass(getVal);
                                                genericPass(companyName);
                                                companyName.classList.add('pass');
                                                getVal.classList.add('pass');
                                                document.getElementById('customerId').value = getVal.value; // send customer number in form
                                                document.querySelectorAll('.companyInp')[0].value = companyName.value; // send company name in form
                                                document.getElementById('existingCustomerb2b').value = true;
                                                document.querySelectorAll('.existingBtn')[0].setAttribute('disabled', 'disabled');
                                                document.querySelectorAll('.existingBtn')[1].setAttribute('disabled', 'disabled');

                                                var node = document.querySelector('.form-b2b__form__field--invoice');
                                                if($(node).length > 0) {
                                                    $(node).addClass('hidden');
                                                }

                                                document.querySelectorAll('.findAccountCustomer')[0].classList.add('disabled');
                                                continueStepActions();
                                                step2.addClass('number--active');
                                                hasStep();
                                                scrollAnimation($('.form-b2b__companydetails__title:last'), 80, true);
                                                if (Array.isArray(digitalData.user) || digitalData.user.length) {

                                                    digitalData.user[0].companyReg = 'existing';
                                                    digitalData.user[0].registration.existing = 'yes';
                                                }
                                            } else {
                                                genericError(getVal);

                                                if (Array.isArray(digitalData.user) || digitalData.user.length) {
                                                    digitalData.user[0].companyReg = 'existing';
                                                    digitalData.user[0].registration.existing = 'no';
                                                }
                                            }
                                        });
                                }
                            },

                            reset: function(e) {
                                e.preventDefault();
                                document.querySelectorAll('.step-toggle')[0].classList.add('hidden');
                                document.querySelectorAll('.companyInp')[0].value = "";
                                document.getElementById('continueStep').classList.remove('active');
                                document.getElementById('existingview').classList.add('hidden');
                                document.querySelectorAll('.bisnode__results__additional')[0].classList.add('hidden');
                                document.getElementById('bisnode').classList.remove('hidden');
                                document.querySelectorAll('label[for="bisnode"]')[0].classList.remove('hidden');
                                document.querySelectorAll('.existingBtn')[0].removeAttribute('disabled');
                                document.querySelectorAll('.existingBtn')[1].removeAttribute('disabled');
                                document.getElementById('bisnode').value = "";
                                document.getElementById('customerNumber').classList.remove('pass');
                                document.getElementById('customerNumber').value = '';
                                document.getElementById('customerNumber').classList.remove('error');
                                document.querySelectorAll('.customerNumberContainer .error-message')[0].classList.add('hidden');
                                document.querySelectorAll('.customerNumberContainer .fa-times')[0].classList.add('hidden');

                                if(document.querySelectorAll('.form-b2b__form__field--invoice')[0] !== undefined) {
                                    document.querySelectorAll('.form-b2b__form__field--invoice')[0].classList.remove('hidden');
                                }

                                if((document.querySelectorAll('.isBiz')[0] !== undefined)) {
                                    document.getElementById('register.countryCode').classList.remove('disabled');
                                }

                                var items = document.querySelectorAll('.tickItem:not(.hidden)');
                                for(var i = 0; i < items.length; i++) {
                                    if($(items[i]).prev('.js-changeDetails').length <= 0) {
                                        items[i].classList.add('hidden');                                        
                                    }
                                }
                                // DISTRELEC-26488: "companyNameInfoText" commented out for now
                                // $('#companyNameInfoText').removeClass('hidden');
                            }

                        },

                        mounted: function() {
                            var customerNumber = $('#customerNumber');

                            document.getElementById('companynotfound').classList.remove('hidden');
                            document.querySelectorAll('.customerNumberContainer')[0].classList.remove('hidden');
                            document.querySelectorAll('.bisnode__results__additional')[0].classList.add('hidden');
                            step2.removeClass('number--active');
                            hasStep();
                        }

                    });

                    var bisResults = document.querySelectorAll('.bisnode__results')[0];

                    input.addEventListener('input', function (e) {

                        if(e.target.value.length >= 3 && e.target.value.length <= 30) {
                            clearTimeout(typeTimer);
                            typeTimer = setTimeout(typeHandler, 1500);
                        } else {
                            bisResults.classList.add('d-none');
                            bisResults.classList.add('hidden');

                            if(document.querySelectorAll('.isBiz')[0] !== undefined) {
                                var CurrentCountryOption = document.getElementById("register.countryCode");
                                document.getElementById("countryOption").innerHTML = CurrentCountryOption.options[CurrentCountryOption.selectedIndex].title;
                            }
                        }
                    });
                }

            });

            $('.changeDetails').click(function(e) {
                e.preventDefault();
                $('.form-b2b__companydetails.card-wrapper').removeClass('hidden');
            });

            $(document).on('click', '.form-b2b__form__field__custno .form-check-inline', function(e) {

                var fields = $('#companynotfound');
                var customerNumber = $('.customerNumberContainer');
                var bisAdd = $('.bisnode__results__additional');

                if($(e.target).closest('input').is('#radioSelectYesExisting')) {
                    $(e.target).closest('input').prop('checked', 'checked');
                    fields.removeClass('hidden');
                    customerNumber.removeClass('hidden');
                    document.querySelectorAll('.findAccountCustomer')[0].classList.remove('disabled');
                    bisAdd.addClass('hidden');
                    $('.step-toggle').addClass('hidden');
                    digitalData.user[0].registration.existing ='yes';
                } else {
                    $(e.target).closest('input').prop('checked', 'checked');
                    $(e.target).addClass('companyActive');
                    fields.removeClass('hidden');
                    customerNumber.addClass('hidden');
                    bisAdd.removeClass('hidden');
                    $('.validCountCompany').removeClass('hidden');
                    $('.bisnode__results__existing').addClass('pl-0');
                    $('#continueStep').removeClass('hidden');
                    digitalData.user[0].registration.existing='no';
                }

            });

            var codice = $('.codiceDest'),
                postCert = $('.postCert'),
                vatInp = $('.postCert > div > input, .codiceDest > div > input'),
                step2 = $('.standalone-register-holder__steps--b2b .standalone-register-holder__steps__items__item--2').children('.number');

            $('input[name="italianVat"]').click(function() {
                $(this).nextAll('.tick').removeClass('hidden');

                if($('#italianVatYes').is(':checked')) {
                    postCert.addClass('hidden');
                    postCert.removeClass('validCounts2');
                    codice.removeClass('hidden');
                    codice.addClass('validCounts2');
                } else {
                    codice.addClass('hidden');
                    codice.removeClass('validCounts2');
                    postCert.removeClass('hidden');
                    postCert.addClass('validCounts2');
                }

            });

            var currentCountry = function() {
                var countrySelect = $("#register\\.countryCode");
                if(countrySelect.length){
                    return countrySelect.val();
                }else{
                    var countryB2BSelect = $("#register\\.countryCodeB2B");
                    if(countryB2BSelect.length){
                        return countryB2BSelect.val();
                    }else {
                        return digitalData.page.pageInfo.countryCode;
                    }
                }
            };

            $('#continueStep').click(function(e) {
                var $clickedStep = $(this);

                $clickedStep.parent().find('.js-codiceEmail').trigger('input');
                $clickedStep.parent().find('.js-legalEmail').trigger('input');

                var hasValidationErrors= false;

                // DISTRELEC-26488: "companyNameInfoText" commented out for now
                // $('#companyNameInfoText').addClass('hidden');
                // $('#companyNameInfoText__companyNotFound').addClass('hidden');
                // $('#companyNameInfoText__companyResult').removeClass('hidden');

                if (hasValidationErrors === false ) {

                    e.preventDefault();
                    var vatPresent = document.querySelectorAll('.vat-container')[0];
                    var postCertInp = $('.postCert > div > input');
                    var codiceDestInp = $('.codiceDest > div > input');
                    var step2 = $('.standalone-register-holder__steps--b2b .standalone-register-holder__steps__items__item--2').children('.number');
                    var self = $(this);
                    var vatId = $('#groupVatId');

                    var isBizDisable = function() {

                        if(document.querySelectorAll('.isBiz')[0] !== undefined) {
                            document.getElementById('register.countryCode').classList.add('disabled');
                        }

                    };


                    var continueToNextStep = function() {
                        step2.addClass('number--active');
                        hasStep();
                        continueStepActions(self);
                        scrollAnimation($('.form-b2b__personaldetails'), 80, true);
                        isBizDisable();
                    };

                    var isVatMandatory =  function (){
                        return document.querySelectorAll('.compVat')[0].classList.contains('isMandatory') === true;
                    };

                    var validateOrgNumber = function (){
                        var orgNumber = $("#orgNumber");
                        if(orgNumber.length > 0){
                            var isMandatory = orgNumber.closest(".grouped").hasClass("isMandatory");
                            if(isMandatory){
                                var orgNumberValue = orgNumber.val().replace(/\s/g, "");
                                if(orgNumberValue.length>0){
                                    var orgValidationPattern = orgNumber.attr("data-validation-pattern");
                                    var regex = new RegExp(orgValidationPattern);

                                    if(regex.test(orgNumberValue)){
                                        genericPass(orgNumber);
                                        orgNumber.addClass("success");
                                        return true;
                                    }else{
                                        genericError(orgNumber);
                                        return false;
                                    }
                                }else{
                                    genericError(orgNumber);
                                    return false;
                                }
                            }else{
                                genericPass(orgNumber);
                                orgNumber.addClass("success");
                                return true;
                            }
                        }else{
                            genericPass(orgNumber);
                            orgNumber.addClass("success");
                            return true;
                        }
                    };

                    var vatValidationEnabledCountry = function (country) {
                        switch (country){
                            case "AT":
                            case "BE":
                            case "CZ":
                            case "DK":
                            case "EE":
                            case "FI":
                            case "FR":
                            case "DE":
                            case "HU":
                            case "IT":
                            case "LV":
                            case "LT":
                            case "NL":
                            case "PL":
                            case "RO":
                            case "SK":
                            case "BG":
                            case "HR":
                            case "CY":
                            case "GR":
                            case "IE":
                            case "LU":
                            case "MT":
                            case "PT":
                            case "SI":
                            case "ES":
                            case "XI":
                                return true;
                            default:
                                return false;
                        }
                    };

                    var validateVatIT = function() {
                        if (vatPresent !== undefined) {
                            var vatFlag = (vatValidation(codiceDestInp) || vatValidation(postCertInp));

                            if (vatFlag === false) {
                                if ($('.codiceDest').hasClass('active')) {
                                    genericError(codiceDestInp);
                                }
                                if ($('.postCert').hasClass('validCounts2')) {
                                    genericError(postCertInp);
                                }
                                return false;
                            } else {
                                if ($('.codiceDest').hasClass('active')) {
                                    genericPass(codiceDestInp);
                                }
                                if ($('.postCert').hasClass('validCounts2')) {
                                    genericPass(postCertInp);
                                }
                                return true;
                            }
                        }else{
                            return true;
                        }
                    };

                    var triggerVatValidationAnalytics = function(){
                        if('undefined' !== typeof Bootstrapper){
                            Bootstrapper.ensEvent.trigger("vat validation");
                        }else{
                            console.log("Validation event tracking skipped. Tracking is probably blocked in browser.");
                        }
                    };

                    var validateVat = function () {
                        var country = currentCountry();
                        var vat = vatId.val().replace(/\s/g, "");

                        digitalData.page.pageInfo.registration.validationService = (country === "CH" ? "CH" : "EU");

                        var internalValidateVat = function() {

                            if(country === "CH" || country === "GB"){
                                var chVatPattern = /^[0-9]{9}$/;
                                if(chVatPattern.test(vat)){
                                    digitalData.page.pageInfo.registration.validationResponse = "valid";
                                    triggerVatValidationAnalytics();
                                    genericGroupPass(vatId);
                                    if(validateOrgNumber() & validateVatIT()){
                                        continueToNextStep();
                                    }
                                } else{
                                    digitalData.page.pageInfo.registration.validationResponse = "invalid";
                                    triggerVatValidationAnalytics();
                                    validateOrgNumber();
                                    validateVatIT();
                                    var errorMessage = country === "GB" ? $('#vatIdError').data('gb-error') : '';
                                    $('#vatIdHint').addClass('hidden');
                                    genericGroupInputError(vatId, errorMessage);
                                }
                            } else {

                                // validateMinCharVatInput(vatId, 9, $('.grouped__prefilled'), $('.prefilled'));

                                if (vatValidationEnabledCountry(country)) {
                                    axios.post("/registration/validateVat", null, {
                                        params: {
                                            vatNumber: vatId.val().replace(/\s/g, ""),
                                            countryCode: country !== "GR" ? country : "EL"
                                        }
                                    }).then(function (response) {
                                        var success = response.data.success;
                                        if (success) {
                                            showValidVATInput(vatId);
                                            digitalData.page.pageInfo.registration.validationResponse = "valid";
                                            triggerVatValidationAnalytics();
                                            genericGroupPass(vatId);
                                            if(validateOrgNumber() & validateVatIT()) {
                                                continueToNextStep();
                                            }
                                        } else {
                                            showInvalidVATInput(vatId);
                                            digitalData.page.pageInfo.registration.validationResponse = "invalid";
                                            triggerVatValidationAnalytics();
                                            validateOrgNumber();
                                            validateVatIT();
                                            $('#vatIdHint').addClass('hidden');
                                            var errorMessage = response.data.errorMessage;

                                            genericGroupInputError(vatId, errorMessage);
                                        }
                                    }).catch(function (error) {
                                        showInvalidVATInput(vatId);
                                        console.error("VAT validation endpoint error: " + error);
                                        digitalData.page.pageInfo.registration.validationResponse = "error";
                                        triggerVatValidationAnalytics();
                                        if(validateOrgNumber() & validateVatIT()){
                                            genericGroupPass(vatId);
                                            continueToNextStep();
                                        }
                                    });
                                }
                            }
                        };

                        if(isVatMandatory()){
                            if(vat){
                                internalValidateVat();
                            }else{
                                var hiddenVat = $("#vatId");
                                if(hiddenVat.val()){
                                    if(validateOrgNumber() && validateVatIT()){
                                        continueToNextStep();
                                    }
                                }
                                else {
                                    digitalData.page.pageInfo.registration.validationResponse = "no input";
                                    triggerVatValidationAnalytics();
                                    genericGroupMandatoryError(vatId);
                                    validateOrgNumber();
                                    validateVatIT();
                                    genericGroupInputError(vatId);
                                }
                            }
                        }else{
                            if(vat){
                                internalValidateVat();
                            }else{
                                digitalData.page.pageInfo.registration.validationResponse = "no input";
                                triggerVatValidationAnalytics();
                                genericGroupPass(vatId);
                                if(validateOrgNumber() & validateVatIT()){
                                    continueToNextStep();
                                }
                            }
                        }
                    };

                    if(vatId.length > 0) {
                        validateVat();
                    } else {
                        if(validateOrgNumber()){
                            continueToNextStep();
                        }
                    }

                }
            });

            function continueStepActions(button) {
                $('.existingWrapper').removeClass('hidden');
                $('.existingWrapper > .bisnode__results__wrapper__item').removeClass('hidden');
                if(arguments.length === 1) {
                    button.addClass('hidden');
                }
                $('.companyInp').addClass('hidden');
                $('.searchTermCompany').html($('.companyInp').val());
            }

            function vatValidation(self) {

                //different validation depending on what type of input it is this is for italian vat field

                if(self.hasClass('codiceEmail') && self.val().length === 6 || self.val().length === 7) {
                    return true;
                }

                if(self.attr('type') === 'email') {
                    // For "legal" email, for valid email, domain of email should contain some specific words like "pec", "legal" etc.
                    // https://wiki.distrelec.com/pages/viewpage.action?pageId=28641672
                    var regex = /\S+@\S+\.\S+/,   //regex for email
                        emailSplit = self.val().toString().split("@").pop(),
                        emailCheck = emailSplit.indexOf('pec') !== -1 || emailSplit.indexOf('legal') !== -1 || emailSplit.indexOf('cert') !== -1 || emailSplit.indexOf('Sicurezzapostale') !== -1,
                        flag = regex.test(self.val()); //flag returns false if regex fails

                    if(flag === true && emailCheck === true) {
                        return true;
                    }

                    return false;

                }
                return false;
            }

            function hasStep() {       // function for if step 2 has been passed will show 3rd step

                if(step2.hasClass('number--active')) {
                    $('.step-toggle').removeClass('hidden');
                    $('.js-preferencesSection').removeClass('hidden');
                    $('.js-preferencesDescriptionSection').removeClass('hidden');
                } else {
                    $('.step-toggle').addClass('hidden');
                    $('.js-preferencesSection').addClass('hidden');
                    $('.js-preferencesDescriptionSection').addClass('hidden');
                }

            }

            function genericError(element) {
                $(element).addClass('error');
                $(element).siblings('.fa-check').addClass('hidden');
                $(element).parent().siblings('.fa-check').addClass('hidden');
                $(element).siblings('.fa-times').removeClass('hidden');
                $(element).parent().siblings('.fa-times').removeClass('hidden');
                $(element).siblings('.error-message').removeClass('hidden');
                $(element).parent().siblings('.error-message').removeClass('hidden');
            }

            function genericGroupHideErrors(element){
                var groupElement = $(element).parent();
                if(!element.value) {
                    groupElement.removeClass('error');
                    groupElement.siblings('.fa-times').addClass('hidden');
                }
                groupElement.siblings('.error-message-required').addClass('hidden');
            }

            function genericGroupMandatoryError(element){
                var groupElement = $(element).parent();
                groupElement.addClass('error');
                groupElement.siblings('.fa-check').addClass('hidden');
                groupElement.siblings('.fa-times').removeClass('hidden');
                groupElement.siblings('.error-message')
                    .not(".error-message-required").addClass('hidden');
                groupElement.siblings('.error-message-required').removeClass('hidden');
            }

            function genericGroupInputError(element, message) {
                var groupElement = $(element).parent();
                groupElement.addClass('error');
                groupElement.find('.tickItem').addClass('hidden');
                groupElement.find('.tickItemError').removeClass('hidden');
                groupElement.siblings('.error-message')
                    .not(".error-message-required").removeClass('hidden');
                groupElement.siblings('.error-message-required').addClass('hidden');

                if(message){
                    var errorMessage = groupElement.siblings(".error-message")
                        .not(".error-message-required")
                        .children("span");
                    errorMessage.text(message);
                }
            }

            function genericPass(element) {
                $(element).removeClass('error');
                $(element).siblings('.fa-check').removeClass('hidden');
                $(element).parent().siblings('.fa-check').removeClass('hidden');
                $(element).siblings('.fa-times').addClass('hidden');
                $(element).parent().siblings('.fa-times').addClass('hidden');
                $(element).siblings('.error-message').addClass('hidden');
                $(element).parent().siblings('.error-message').addClass('hidden');
            }

            function genericGroupPass(element) {
                var groupElement = $(element).parent();
                var elementCheckIcon = groupElement.siblings('.fa-check');
                var elementTimesIcon = groupElement.siblings('.fa-times');

                groupElement.removeClass('error');
                groupElement.siblings('.error-message').addClass('hidden');

                if (!elementTimesIcon.length) {
                    elementCheckIcon = groupElement.find('.fa-check');
                    elementTimesIcon = groupElement.find('.fa-times');
                }

                elementCheckIcon.removeClass('hidden');
                elementTimesIcon.addClass('hidden');
            }

            function enableScroll() {
                $('body').removeClass('prevent-scroll');
            }

            function disableScroll() {
                $('body').addClass('prevent-scroll');
            }

            function scrollAnimation(element, height, cancel) {
                if(cancel === true) {
                    disableScroll();
                    $('html, body').animate({
                        scrollTop: element.offset().top - height
                    }, 2000, function(){
                        enableScroll();
                    });
                }
            }

            vatInp.focusin(function(){
                $('.codicePopup').removeClass('hidden');
            });
            vatInp.focusout(function(){
                $('.codicePopup').addClass('hidden');
            });

            $('.grouped.validCounts2 > input').each(function(i,v){

                $(v).focusin(function(){
                    $(v).parent().children('.inputPopup').addClass('hidden');
                    $(v).siblings('.inputPopup').removeClass('hidden');
                });

                $(v).focusout(function() {

                    setTimeout(function(){
                        if($(v).val().length > 0) {
                            $(v).siblings('.ticks2').removeClass('hidden');
                        } else {
                            $(v).siblings('.ticks2').addClass('hidden');
                        }

                        $(v).siblings('.inputPopup').addClass('hidden');
                    }, 100);


                });

            });

            $('.form-b2b__form__field--invoice a').click(function(e) {
                e.preventDefault();
                $(this).toggleClass('active');
                $(this).next().toggleClass('hidden');
                if($(this).hasClass('active') && invoiceField.value.length > 0) {
                    invoiceField.classList.add('validate-email');
                } else {
                    invoiceField.classList.remove('validate-email');
                    invoiceField.classList.remove('error');
                    $('.form-b2b__form__field--invoice > .form-group > .field-msgs').addClass('hidden');
                }

            });

            function populateUserInformation() {

                var users = (null === digitalData.user || digitalData.user === undefined ) ? [] : JSON.parse(JSON.stringify(digitalData.user));
                var user;
                if ($('#inlineRadio2').is(':checked') || $('.form-check.mb-2 .error').length > 0) {
                    $('.terms-holder').addClass('active');

                    for( user in users){
                        if(null ===  users[user].registration || users[user].registration === undefined  ){
                            users[user].registration = {};
                        }
                        users[user].registration.type ='b2c';
                        users[user].registration.stage ='registration form';
                        users[user].registration.step ='1';
                        digitalData.page.pageInfo.channel='b2c';
                    }
                } else {
                    $('.terms-holder').removeClass('active');
                    $("#inlineRadio1").attr('checked', 'true');
                    for(user in users){
                        if(null ===  users[user].registration || users[user].registration === undefined  ){
                            users[user].registration = {};
                        }
                        users[user].registration.type ='b2b';
                        users[user].registration.stage ='registration form';
                        users[user].registration.step ='1';
                        digitalData.page.pageInfo.channel='b2b';
                    }
                }
                digitalData.user = users;

            }

            $('.form-check-input').click( function() {
                populateUserInformation();
            });

            if(document.querySelectorAll('.isBiz')[0] !== undefined) {
                $(".countrySelectExportShop").change(function(){
                    var countryCode = $(this).val();

                    $.ajax({
                        url: '/_s/shopsettings-async',
                        type: 'get',
                        data: {
                            country: countryCode
                        },
                        success: function (data, textStatus, jqXHR) {
                        },
                        error: function (jqXHR, textStatus, errorThrown) {
                        }
                    });
                });
            }else{
                $("#register\\.countryCodeB2B").change(function(){
                    var countryCode = $(this).val();

                    $.ajax({
                        url: '/_s/shopsettings-async',
                        type: 'get',
                        data: {
                            country: countryCode
                        },
                        success: function (data, textStatus, jqXHR) {
                        },
                        error: function (jqXHR, textStatus, errorThrown) {
                        }
                    });
                });
            }

            $('.form-b2b .btn-success').click(function(e) {
                e.preventDefault();
                $('.ajax-product-loader').removeClass('d-none');

                var hasValidationErrors = false;

                if(window.location.href.indexOf('/registration/checkout') > -1) {
                    document.getElementById('regType').value = "checkout";
                }

                // Add Step 2 Active
                var stepTwo = $('.standalone-register-holder__steps--b2b .standalone-register-holder__steps__items__item--3 .number');

                stepTwo.addClass('number--active');

                // If existing journey set org and vat number to non-mandatory

                if ((document.querySelectorAll('.compVat')[0] !== undefined && document.querySelectorAll('.compVat')[0].classList.contains('isMandatory') === true) && (digitalData.user[0].companyReg === 'existing')) {
                    $('.compVat').removeClass('isMandatory');
                }

                if ((document.querySelectorAll('.department')[0] !== undefined && document.querySelectorAll('.department')[0].classList.contains('isMandatory') === true) && (digitalData.user[0].companyReg === 'existing')) {
                    $('.department').removeClass('isMandatory');
                }

                // get the entire 2nd step div regardless of the flow

                var orgNumber = $('#orgNumber');
                var vatId = $('#groupVatId');

                if(document.getElementById('orgNumber') !== null) {
                    document.getElementById('orgNumber').value = document.getElementById('orgNumber').value.trim();

                    if (document.getElementById('orgNumber').value.length <= 0 && !$('.bisnode__results__additional').hasClass('hidden')) {
                        orgNumber.siblings('.errcustomerNumberor').removeClass('hidden');
                        orgNumber.siblings('.ticks2').addClass('hidden');
                        step2.removeClass('number--active');
                        $('.step-toggle').addClass('hidden');
                        scrollAnimation($('.form-b2b__personaldetails'), 80, false);

                        document.getElementById('continueStep').classList.remove('hidden');
                        hasValidationErrors = true;
                    }
                }

                // Check If Existing Customer

                if ($('.customerNumber').val() !== "") {
                    $('#existingCustomer').val('true');
                    digitalData.user[0].registration.existing='yes';
                }

                // Org Number replace '-'

                if ($('#orgNumber').val() !== undefined) {
                    if ($('#orgNumber').val() !== '') {
                        $('#orgNumber').val().replace(/-/, '');
                    }
                }

                if (document.querySelectorAll('.companyInp')[0] !== null) {
                    var htmldecode = document.querySelectorAll('.companyInp')[0];
                    var textArea = document.createElement('textarea');

                    textArea.innerHTML = htmldecode.value;
                    htmldecode.value = textArea.value;
                }

                if ($('#groupVatId').length > 0) {
                    $('.prefilled').removeClass('active');
                    $('.grouped__prefilled').removeClass('active');
                    $('.grouped__prefilled .fa-check').addClass('hidden');

                }

                if (hasValidationErrors) {
                    $('.ajax-product-loader').addClass('d-none');
                    var errorElement = $('.field.error'),
                        isBeGlobalErrorAvailable = $('.standalone-register-holder .be-global-error .error').length;

                    if ( errorElement.length > 0 ) {
                        $("html, body").animate({scrollTop: errorElement.offset().top - 120}, 500);
                    } else {
                        $("html, body").animate({scrollTop: 0}, 500);
                    }

                    if ( errorElement.length > 1 && isBeGlobalErrorAvailable < 1) {
                        $('.standalone-register-holder .fe-global-error').removeClass('hidden');
                    }

                } else {
                    var gid = $('.mod-captcha.form-b2b .g-recaptcha',self.$ctx).eq(0).data('gid');

                    if (typeof gid !== 'number') {
                        gid = grecaptcha.render($('.mod-captcha.form-b2b .g-recaptcha',self.$ctx)[0],{},true);
                        $('.mod-captcha.form-b2b .g-recaptcha',self.$ctx).eq(0).data({'gid':gid});
                    }

                    localStorage.setItem('datalayerReg', JSON.stringify(digitalData.user[0]));
                    localStorage.setItem('datalayerPageInfo', JSON.stringify(digitalData.page.pageInfo.registration));

                    var vatPrefix = $("#vatPreValue");
                    var groupVatId = $("#groupVatId");
                    var hiddenVatId = $("#vatId");
                    var groupVat = groupVatId.val();
                    var hiddenVat = hiddenVatId.val();

                    if(groupVatId.length > 0 && (!hiddenVat || !hiddenVat.endsWith(groupVat))) {
                        var groupVatIdValue = groupVatId.val().replace(/\s/g, "");

                        if (!vatPrefix.attr("data-ignore") && groupVatIdValue) {
                            hiddenVatId.val(vatPrefix.text() + groupVatIdValue);
                        } else {
                            var country = currentCountry();
                            if(currentCountry() === "LI"){
                                hiddenVatId.val(groupVatIdValue + " ");
                            }else{
                                hiddenVatId.val(groupVatIdValue);
                            }
                        }
                    }

                    $('#registerB2BForm').find('input').each(function(i,v){
                        $(v).val($(v).val().replace(/<[^>]*>/g,""));
                    });

                    grecaptcha.reset(gid);
                    grecaptcha.execute(gid);

                    // Since recaptcha is added dynamically in the DOM, we need to wait a little bit so we can target it
                    setTimeout(function () {
                        var captchaIframe;
                        // Captcha contains different title for every language so we need to identify which iframe is for recaptcha
                        $('iframe[title]').each(function () {
                            var $currentIframe = $(this);
                            var title = $currentIframe.attr('title');
                            // if "recaptcha" is substring of title we found correct iframe
                            if (title.toLowerCase().indexOf('recaptcha') !== -1) {
                                captchaIframe = $currentIframe;
                            }
                        });

                        // We need to manually add event on recaptcha overlay (it don't have built in event for this)
                        // Once user clicks on it, recaptcha will be closed and we are hiding loading overlay
                        captchaIframe.parent().siblings('div').on('click', function () {
                            // Hide loading overlay
                            $('.ajax-product-loader').addClass('d-none');
                        });
                    }, 1000);
                }

            });

            parent.on(callback);
        };
    };

})(Tc.$);
