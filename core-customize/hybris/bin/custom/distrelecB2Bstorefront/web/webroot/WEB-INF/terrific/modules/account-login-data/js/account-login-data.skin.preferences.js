(function($) {

    Tc.Module.AccountLoginData.Preferences = function (parent) {

        this.on = function (callback) {

            var successMsg = $('.skin-global-messages-component.success');
            var errorMsg = $('.skin-global-messages-component.error');
            var self = this;
            var initialCheckboxState;
            var emailCheckbox = $('.js-emailCheckbox');
            var emailCheckboxCheckedState = emailCheckbox.prop('checked');

            self.$ctx.on('click', '.js-submitConsentForm', function(e) {
                e.preventDefault();

                var obsCheckboxes = $('.js-consentForm .js-obs');
                var consentCheckboxes = $('.js-consentForm .js-checkbox');

                var allObsCheckboxesChecked = obsCheckboxes.not(':checked').length === 0;
                var preferenceStatusCheckboxes = {};
                preferenceStatusCheckboxes.obsoleCategories = [];

                consentCheckboxes.not('.js-obs').each(function(i,element) {
                    var checkbox = $(element);
                    var inputName = checkbox.attr('name');

                    preferenceStatusCheckboxes[inputName] = checkbox.prop('checked');
                });

                obsCheckboxes.each(function(i,element) {
                    var checkbox = $(element);
                    var inputName = checkbox.attr('name');
                    var obsObj = {
                        "code": inputName,
                        "optedForObsol": true, // Always set to true, I dont know why
                        "obsolCategorySelected": checkbox.prop('checked'),
                        "allCatSelected": allObsCheckboxesChecked
                    };

                    preferenceStatusCheckboxes.obsoleCategories.push(obsObj);
                });

                $.ajax({
                    url: '/my-account/preference-center',
                    type: 'POST',
                    dataType: 'json',
                    contentType: "application/json; charset=utf-8",
                    method: 'post',
                    data: JSON.stringify(preferenceStatusCheckboxes),
                    success: function () {
                        $("html, body").animate({scrollTop: 0}, 500);
                        digitalData.page.pageInfo.prefRemoved = [];
                        digitalData.page.pageInfo.prefAdded = [];
                        successMsg.removeClass('hidden');
                        setTimeout(function(){ successMsg.addClass('hidden'); }, 3000);
                        saveCheckboxStates();

                        var consentSection = $('.js-consentSection');
                        if(consentSection.data('show-doubleoptin-message') === true && emailCheckboxCheckedState === false && emailCheckbox.prop('checked')) {
                            $('.js-doubleOptinReminderModal').modal({'backdrop': 'static'});
                        } else if (consentSection.data('show-doubleoptin-message') && emailCheckboxCheckedState === true && !emailCheckbox.prop('checked')){
                            emailCheckboxCheckedState = false;
                        }
                    },
                    error: function () {
                        $("html, body").animate({scrollTop: 0}, 500);
                        resetForm();
                        errorMsg.removeClass('hidden');
                        setTimeout(function(){ errorMsg.addClass('hidden'); }, 3000);
                    }
                });

            });

            if(!$('.js-emailConsents').data('disable-interaction')) {
                self.$ctx.on('change','.js-parentCheckboxItem .js-checkbox', function(e) {
                    var checkbox = $(e.currentTarget);


                    // Select all or unselect all children checkboxes based on the parent
                    checkbox.siblings('.js-childrenCheckboxes').find('.js-checkbox').prop('checked', checkbox.is(':checked'));

                    // Change value of all children under 2nd lvl based on level 2 value
                    var levelTwoNotEmpty = false;
                    $('.js-levelTwo .js-checkbox').each(function(index, element){
                        if($(element).prop('checked')) {
                            levelTwoNotEmpty = true;
                            return false;
                        }
                    });

                    // Change value of all children under 1st lvl based on level 1 value
                    $('.js-levelTwo').closest('.js-parentCheckboxItem').find('> .js-checkbox').prop('checked', levelTwoNotEmpty);

                    var levelOneNotEmpty = false;
                    $('.js-levelOne .js-checkbox').each(function(index, element){
                        if($(element).prop('checked')) {
                            levelOneNotEmpty = true;
                            return false;
                        }
                    });

                    $('.js-levelOne').closest('.js-parentCheckboxItem').find('> .js-checkbox').prop('checked', levelOneNotEmpty);
                });
            }



            self.$ctx.on('change','.js-consentForm .js-checkbox', function(e) {
                digitalData.page.pageInfo.prefRemoved = [];
                digitalData.page.pageInfo.prefAdded = [];
                $.each(initialCheckboxState, function( index, value ) {
                    var element = value.ref;
                    var parentNames = '';
                    var name = element.attr('name');

                    if((value.checked !== element.prop('checked')) || value.changed) {
                        value.changed = true;
                        $(element.parents('.js-parentCheckboxItem').get().reverse()).each(function(index, parent) {
                            if (element.get(0) === $(parent).find('> .js-parentCheckbox').get(0)) return true;
                            parentNames = parentNames + $(parent).find('> .js-parentCheckbox').attr('name') + ':';
                        });

                        if(value.ref.prop('checked')) {
                            digitalData.page.pageInfo.prefAdded.push(parentNames + name);
                        } else {
                            digitalData.page.pageInfo.prefRemoved.push(parentNames + name);
                        }
                    }

                });


            });


            self.$ctx.on('click','.js-cancelConsentForm', function(e) {
                resetForm();
            });

            function resetForm() {
                $.each(initialCheckboxState, function( index, value ) {
                    value.ref.prop('checked', value.checked);
                });
            }

            function saveCheckboxStates() {
                initialCheckboxState = [];
                $('.js-consentForm .js-checkbox').each(function(i,element) {
                    var elementState = {};
                    elementState.ref = $(element);
                    elementState.checked = $(element).prop('checked');
                    initialCheckboxState.push(elementState);
                });
            }

            $('.js-newsletterConsent').trigger('change');

            digitalData.page.pageInfo.prefRemoved = [];
            digitalData.page.pageInfo.prefAdded = [];

            saveCheckboxStates();

            // calling parent method
            parent.on(callback);
        };

    };

})(Tc.$);
