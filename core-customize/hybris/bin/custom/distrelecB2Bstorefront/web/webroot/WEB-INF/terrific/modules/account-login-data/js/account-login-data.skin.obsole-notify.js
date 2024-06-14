(function($) {

	Tc.Module.AccountLoginData.ObsoleNotify = function (parent) {

		this.on = function (callback) {

			var self = this;
            self.obsoleCategories = [],
            self.categoryOptionAllEle = $('#obsolescenceNotifications_AllOption'),
            self.categoryOptions = $('.obsolescence-notifications__category-option'),
            self.categoryOptionIpt = $('.obsolescence-notifications__category-option input'),
            self.obsoleHeader = $('.obsolescence-notifications__email-checkbox-header'),
            self.successMsg = $('.skin-global-messages-component.success');
            self.errorMsg = $('.skin-global-messages-component.error');

			// Validate Elements
			self.$ctx.on('click', '.btn-change', function(e) {
			    e.preventDefault();
                self.successMsg.addClass('hidden');

				// Validation
				var isValid = true;

                self.setAllObsoleCategories();
                
                obsoleCategories = JSON.stringify({ "obsoleCategories": self.obsoleCategories });
                
                $.ajax({
                    url: '/my-account/changeObsolPreference',
                    type: 'POST',
                    dataType: 'json',
                    contentType: "application/json; charset=utf-8",
                    method: 'post',
                    data: obsoleCategories,
                    success: function (data, textStatus, jqXHR) {
                        var isOptedForObsolVal = data[0].optedForObsol,
                            isObsolCalSelectedVal = false,
                            obsolCatSelectedValLen = 0;

                        for ( var i=0; i < data.length; i++) {

                            if ( data[i].obsolCategorySelected === true ) {
                                isObsolCalSelectedVal = true;
                                obsolCatSelectedValLen++;
                            }

                        }

                        if ( obsolCatSelectedValLen === data.length ) {
                            self.categoryOptionAllEle.prop('checked',true);
                            self.categoryOptionAllEle.attr('value','true');

                            self.categoryOptionIpt.prop('checked',false);
                            self.categoryOptionIpt.attr('value','false');
                        }

                        self.successMsg.removeClass('hidden');
                        setTimeout(function(){ self.successMsg.addClass('hidden'); }, 3000);

                    },
                    error: function (jqXHR, textStatus, errorThrown) {
                    }
                });

            });

            self.$ctx.on('click', '.obsolescence-notifications__email-checkbox-header', function(e) {
                e.preventDefault();

                var isClass = $('.obsolescence-notifications__email-checkbox-content').hasClass('hidden');

                $('.obsolescence-notifications__email-checkbox .arrows').addClass('hidden');

                if (isClass) {
                    $('.obsolescence-notifications__email-checkbox-content').removeClass('hidden');
                    $('.obsolescence-notifications__email-checkbox .fa-chevron-up').removeClass('hidden');

                } else {
                    $('.obsolescence-notifications__email-checkbox-content').addClass('hidden');
                    $('.obsolescence-notifications__email-checkbox .fa-chevron-down').removeClass('hidden');
                }

            });

            self.$ctx.on('click', '#obsolescenceNotifications_AllOption' , function(e) {
                self.categoryOptionIpt.prop('checked',false);
                self.categoryOptionIpt.attr('value','false');
            });

            self.$ctx.on('click', '.obsolescence-notifications__category-option input' , function(e) {
                var _elem = $(this),
                    _elemVal = self.checkObsoleValue(_elem);

                self.categoryOptionAllEle.prop('checked',false);
                self.categoryOptionAllEle.attr('value','false');
            });

            // calling parent method
	        parent.on(callback);
		};

        this.checkObsoleValue = function (_elem) {
            var _obsValue = '';

            if( _elem.prop("checked") === true ){
                _obsValue = true;
            }
            else if( _elem.prop("checked") === false ){
                _obsValue = false;
            }

            return _obsValue;
        };

        this.setObsoleValue = function (_elem,_value) {
            _elem.prop('checked',_value);
            _elem.attr('checked',_value);
            _elem.attr('value',_value);

            if (_value) {
                _elem.attr('checked', 'checked');
            } else {
                _elem.removeAttr('checked');
            }

        };

        this.checkObsoleCategories = function () {
            var self = this;

            $('.obsolescence-notifications__category-option').each(function (index, value) {
                var _elem = $(this).find('input');
                    codeId = _elem.attr('id'),
                    obsoleCategory = '',
                    obsValue = '';

                $(this).attr('data-index',index);
                obsValue = self.checkObsoleValue( _elem );
                obsoleCategory = {code:codeId, obsolCategorySelected:obsValue};
                self.obsoleCategories[index] = obsoleCategory;
            });

        };

        this.setAllObsoleCategories = function () {
            var self = this,
                isOptedForObsolVal = '',
                obsolCatSelectedValLen = 0,
                categoryOptionAllVal = false,
                obsolCatLen = self.categoryOptions.length;

            $('.obsolescence-notifications__category-option').each(function (index, value) {
                var _elem = $(this).find('input');
                obsValue = self.checkObsoleValue( _elem );

                if ( obsValue === true ) {
                    obsolCatSelectedValLen++;
                }

                if ( obsolCatLen === obsolCatSelectedValLen) {
                    categoryOptionAllVal = true;
                }

            });

            if ( self.categoryOptionAllEle.prop("checked") ) {
                categoryOptionAllVal = true;
            } else {
                categoryOptionAllVal = false;
            }

            $('.obsolescence-notifications__category-option').each(function (index, value) {
                var _elem = $(this).find('input');
                codeId = _elem.attr('id'),
                obsValue = self.checkObsoleValue( _elem ),
                obsoleCategory = '',
                obsolCatLen = self.categoryOptions.length;

                if ( obsValue === true ) {
                    isOptedForObsolVal = true;
                    obsolCatSelectedValLen++;
                }

                if ( self.categoryOptionAllEle.prop("checked") ) {
                    isOptedForObsolVal = true;
                }

                obsoleCategory = {code: codeId, obsolCategorySelected: obsValue, optedForObsol: isOptedForObsolVal, allCatSelected: categoryOptionAllVal};
                self.obsoleCategories[index] = obsoleCategory;
            });

        };

    };

})(Tc.$);
