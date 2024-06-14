(function($) {

	/**
	 * Module implementation.
	 *
	 * @namespace Tc.Module
	 * @class Servicenav
	 * @extends Tc.Module
	 */
	Tc.Module.Servicenav = Tc.Module.extend({

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
		init: function($ctx, sandbox, id) {
			// call base constructor
			this._super($ctx, sandbox, id);
			
			// Data Arrays
			this.allSiteSettings = [];
			this.currentShopSettings = [];
			this.channels	= {};
			this.countries 	= {};
			this.languages 	= {};			
			
			// State flags
			this.areTemplatesReady = false;
			this.areDataReady = false;

			//Drop down
			this.serviceNav = $('.fsettingsContainer', $ctx);

		},

		
		
		/**
		 * Hook function to do all of your module stuff.
		 *
		 * @method on
		 * @param {Function} callback function
		 * @return void
		 */
		on: function(callback) {

			var self = this,
				$ctx = this.$ctx,
				$fsettings = $('.fsettings'),
				shopSettings = $('#backenddata .shopsettings').data(),
				onKeyPress = $.proxy('onKeyPress');

			// If statement needed to not through an error on skin checkout pages because of missing template markup
			if(!$ctx.hasClass('skin-servicenav-checkout')){
				this.uiTpl = doT.template( this.$$('#template-servicenav-settings').html() );
			}

			$ctx.on('click', '.btn-save', function(e) {
				$('#select-country').removeAttr('disabled');
				if($('#select-country').val() === 'otherCountries'){
					window.location.href = 'http://www.distrelec.com/global/';
					return false;
				}
				
				return true;
			});

			this.serviceNav.hoverIntent(function(e){
				if($(e.target).parent().attr('class') === 'flyout-close') {
					$('.flyout-settings').addClass('js-hidden').addClass('hidden');
					$('.shopsetings-popup').addClass('transparent');
				} else {
					$('.shopsetings-popup').removeClass('transparent');
					$('.flyout-settings').removeClass('hidden').removeClass('js-hidden');	
				}
			});


            function removeServiceNav(){

                $('.flyout-settings').addClass('hidden').addClass('js-hidden');
                $(document).off('keydown.settings');

            }

			$ctx.on('click', '.btn-cancel', function(e) {
                removeServiceNav();
			});

            $ctx.on('click', '.flyout-close', function(e) {
                removeServiceNav();
            });

            $('html').on('click', 'body', function(e){

                if ($('.flyout-settings').has(e.target).length <= 0){
					removeServiceNav();
                }

			});


            $ctx.on('click', '#select-country', function(e) {
                $('.ft-holder').toggleClass('show');
            });

            self.initSiteSettings();

            $fsettings.on('click touchstart', function(ev){
				ev.preventDefault();
				ev.stopPropagation();
                self.initSiteSettings();
			});

            $ctx.on('click', '#select-languageSelectBoxItOptions .selectboxit-option', function(ev) {
            	var selectClass = $('#select-languageSelectBoxItOptions .selectboxit-option');

                selectClass.removeClass('selectboxit-selected');
                $(this).addClass('selectboxit-selected');

            });

            $ctx.on('click', '#select-channelSelectBoxItOptions .selectboxit-option', function(ev) {
            	var selectClass = $('#select-channelSelectBoxItOptions .selectboxit-option');

                selectClass.removeClass('selectboxit-selected');
                $(this).addClass('selectboxit-selected');

            });

            var ua = navigator.userAgent;
            var is_ie = ua.indexOf("MSIE ") > -1 || ua.indexOf("Trident/") > -1;

			if (is_ie) {
				self.checkSelectBoxIt();
			}

			callback();
		},

		after: function() {
			var $settings = $('#backenddata .shopsettings');

			if ($settings.length) {
				this.initData($settings.data());
			}

		},			

        checkSelectBoxIt : function(){
            var self = this,
            	isSelectBoxLen = $('#select-country> option').length;

            if ( isSelectBoxLen >= 1 ) {
                self.bindEvents();
            } else {
				setTimeout(function(){
					self.checkSelectBoxIt();
				}, 1000);
            }

        },

        initSiteSettings : function(){
            var self = this;

            if(!self.areDataReady){
                self.loadData();
            } else {

                if ($('.shopsetings-popup').hasClass('transparent')){
                    $('.shopsetings-popup').removeClass('transparent');
                    $('.flyout-settings').removeClass('hidden');
                } else {
                    $('.flyout-settings').toggleClass('hidden');
                }

            }

            if ($('.flyout-settings').hasClass('hidden')){
                $(document).off('keydown.settings');
            } else {
                $(document).on('keydown.settings', self.onKeyPress);
            }

        },
		
		loadData : function() {
			var self = this;
			$.ajax({
				url: '/_s/allsitesettings',
				type: 'get',
				cache: true,
				dataType: 'json',
				success: function (data) {
					self.initSettingsData(data);	
				}
			});
		},

		initSettingsData : function(data){
			var self = this;

			self.allSiteSettings = data;
			var $settings = $('#backenddata .shopsettings');
			if ($settings.length) {
				self.initData($settings.data());
				
				if (self.areTemplatesReady === false) {
					self.initTemplates();
				}
				
				self.populateData();
			}

		},		
		
		initData: function(data) {
			this.currentShopSettings = data;
			this.channels = this.getDataForKey('channels', 'country', this.currentShopSettings.country);
			this.countries = this.getDataForKey('country', '','');
			this.languages = this.getDataForKey('languages', '','');
		},

		initTemplates: function() {
			this.uiTpl = doT.template(this.$$('#template-lightboxshopsettings').html());
			this.uiTplOption = doT.template(this.$$('#template-lightboxshopsettings-option').html());

			$('.formSettingsContainer').html(this.uiTpl(this.currentShopSettings));

			this.$modalWindow = this.$$('.modal');
			this.$selectChannel		= this.$$('#select-channel');
			this.$selectCountry		= this.$$('#select-country');
			this.$selectLanguage	= this.$$('#select-language');

			this.bindEvents();

			this.areTemplatesReady = true;
			this.btnLabelDefault = this.$btnConfirm.data('label-default');
			this.btnLabelRedirect = this.$btnConfirm.data('label-redirect');
			this.areDataReady = true;
		},	

		populateData: function() {
            var self = this;

			this.populateSelect(this.$selectChannel, this.channels, 'channel');
			this.populateSelect(this.$selectCountry, this.countries, 'country');
			this.setAsSelected(this.$selectChannel, this.currentShopSettings.channel);
			this.setAsSelected(this.$selectCountry, this.currentShopSettings.country);
			this.populateSelect(this.$selectLanguage, this.filterSelect('languages'), 'language');
			this.setAsSelected(this.$selectLanguage, this.currentShopSettings.language);
			self.setFlagsForContries();

			if (this.currentShopSettings.country === 'AD') {
				self.$selectCountry.val('EX');
				self.$selectCountry.trigger( "change" );
                $('.fsettings .current-country-name').html('International Shop');
			}

		},

        setFlagsForContries: function() {
            var self = this;
            var isFlagPresent = $( "#select-countrySelectBoxItContainer .icon-country" ).hasClass( "flag" );

            if (!isFlagPresent) {
                var currentSelectedFlag =  $( "#select-countrySelectBoxIt .selectboxit-text" ).data('val');

                $( "#select-countrySelectBoxIt .selectboxit-default-icon").addClass('flag flag--'+currentSelectedFlag);
                $( "#select-countrySelectBoxItOptions .selectboxit-option" ).each(function( index ) {
                    var currentFlag = $( this ).data('val');
                    $(this).find('.icon-country').addClass('flag flag--'+currentFlag);
                });

				setTimeout(function(){
					self.setFlagsForContries();
				}, 10);

            }

        },

		bindEvents: function() {
			var self = this;
			var $body = $('body');

			setTimeout(function () {
				if(!Modernizr.isie7) {
					Modernizr.load([{
						load: '/_ui/all/cache/dyn-jquery-selectboxit.min.js',
						complete: function () {
							self.$$('.selectpicker').selectBoxIt({
								autoWidth : false
							});
						}
					}]);
				}
			}, 300);

			$body.addClass('modal-open'); // used to prevent scrolling while modal is open			

			self.$selectCountry.on('change', function() {
				self.populateSelect(self.$selectLanguage, self.filterSelect('languages'), 'language');
				self.populateSelect(self.$selectChannel, self.filterSelect('channels'), 'channel');

				if ( self.currentShopSettings.country === 'AD') {
					return;
				}

				if(!Modernizr.touch && !Modernizr.isie7 && ! Modernizr.iseproc) {
					self.$selectLanguage.data("selectBox-selectBoxIt").refresh();
					self.$selectChannel.data("selectBox-selectBoxIt").refresh();
				} else {
                    self.$selectLanguage.data("selectBox-selectBoxIt").refresh();
                    self.$selectChannel.data("selectBox-selectBoxIt").refresh();
				}

			});

			self.$btnConfirm = $('.btn-save', self.$ctx);

			// add event to save button
			$('.js-toggle-hook').on('click',function (e) {
				e.preventDefault();
				
				// check country code, if not on international shop submit the form to update site settings
				if($('#backenddata .shopsettings').data('country') === 'EX') {
					// gather required data for call to endpoint
					var requestData = { 
						country: $('#select-country').val(),
						channel: $('#select-channel').val(),
						language: $('#select-language').val()
					};

					// make request to endpoint with required information, on success refresh page
					$.ajax({
						url: '/_s/shopsettings-toggle',
						type: 'post',
						data: requestData,
						success: function(data, textStatus, jqXHR) {
							window.location.reload();
						}
					});
				}
				else {
					$('#command').submit();
				}
			});
		},		

		getDataForKey:  function(theKey, filterKey, propertyKey) {
			var self = this;
			var output = {};
			var key;
			var keyLabel;

			for (var i = 0; i < self.allSiteSettings.length; i++) {

				if(filterKey === '' || self.allSiteSettings[i][filterKey][propertyKey] !== undefined ){

					for (var j = 0; j < self._keys(self.allSiteSettings[i][theKey]).length; j++) {
						key = self._keys(self.allSiteSettings[i][theKey])[j];
						keyLabel = self.allSiteSettings[i][theKey][key];

						if(output[key] === undefined) {
							output[key] = keyLabel;
						}
					}
				}
			}

			return output;
		},

		populateSelect: function($theSelect, theDataArray, name) {
			var self = this;
			var optionsMarkup = [];

			for (var key in theDataArray) {
				optionsMarkup.push(self.uiTplOption({ 'name' : name, 'key' : key, 'keyLabel' : theDataArray[key]}));

				// [DISTRELEC-22905]
				// For export shop, UK - Mainland and Northern Ireland shouldn't contain "Private" (B2C) option for channel
				if (self.currentShopSettings.shop === 'distrelec_EX' && name === 'channel' && key === 'B2C') {
					// Value which user selected in select field
					var selectedValue = $(self.$selectCountry[0]).val();
					// If: user selects country value from dropdown
					// Else: value is selected on page load
					var countryValue = selectedValue ? selectedValue : self.currentShopSettings.country;

					// If: UK - Mainland or Northern Ireland, remove last item from array (B2C)
					if (countryValue === 'GB' || countryValue === 'XI') {
						optionsMarkup.pop();
					}
				}
			}

			$theSelect.html(optionsMarkup);

			if (optionsMarkup.length === 0) {
				$theSelect.addClass('selectboxit-disabled');
				self.$btnConfirm.val(self.btnLabelRedirect);
			} else {
				$theSelect.removeClass('selectboxit-disabled');
				self.$btnConfirm.val(self.btnLabelDefault);
			}

			self.setAsSelected(self.$selectChannel, self.currentShopSettings.channel);
			self.setAsSelected(self.$selectLanguage, self.currentShopSettings.language);
		},

		filterSelect: function(name) {
			var self = this;
			var filteredOptions = {};
			var selectedCountryKey = self.$selectCountry.val();

			for (var i = 0; i < self.allSiteSettings.length; i++) {
				var thisCountryKey = self._keys(self.allSiteSettings[i].country);

				if (thisCountryKey == selectedCountryKey) {
					for (var j = 0; j < self._keys(self.allSiteSettings[i][name]).length; j++) {
						var key = self._keys(self.allSiteSettings[i][name])[j],
							keyLabel = self.allSiteSettings[i][name][key];

						if (filteredOptions[key] === undefined) {
							filteredOptions[key] = keyLabel;
						}
					}
				}
			}

			return filteredOptions;
		},

		setAsSelected: function($theSelect, theKey) {
			$theSelect.val(theKey);
		},

		hasCountryChanged: function() {
			return this.currentShopSettings.country !== this.$selectCountry.val();
		},

		_has: function(obj, key) {
			return Object.prototype.hasOwnProperty.call(obj, key);
		},

		_keys : Object.keys || function(obj) {
			var self = this;
			if (obj !== Object(obj)) throw new TypeError('Invalid object');
			var keys = [];
			for (var key in obj) if (self._has(obj, key)) keys[keys.length] = key;
			return keys;
		},
		
		onKeyPress: function(ev) {

			if (ev.keyCode === 27) {
				$('.flyout-settings').addClass('hidden');
				$(document).off('keydown.settings');
			}

		}

	});

})(Tc.$);
