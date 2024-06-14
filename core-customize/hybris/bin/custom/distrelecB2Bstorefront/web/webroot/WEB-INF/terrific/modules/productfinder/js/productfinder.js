(function ($) {

	/**
	* Module implementation.
	*
	* @namespace Tc.Module
	* @class Productfinder
	* @extends Tc.Module
	*/
	Tc.Module.Productfinder = Tc.Module.extend({

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

			// validation helpers
			this.validationErrorCustomRange = this.$$('#tmpl-productfinder-validation-error-customrange').html();

			this.$grid = this.$$('.grid', $ctx);
			this.$total = this.$$('.b-total', $ctx);
			this.$loader = this.$$('.b-loader', $ctx);
			this.$btnGo = this.$$('.b-go', $ctx);
			this.data = $.parseJSON(productFinderData);// JSON data initially from backend as inline var. same object is reused in ajax post with updated values (parseJSON/toJSON)!
			this.$customRange = this.$grid.find('.customMin, .customMax');
		},


		/**
		* Hook function to do all of your module stuff.
		*
		* @method on
		* @param {Function} callback function
		* @return void
		*/
		on: function(callback) {
			var self = this;


			// initial result count
			self.updateTotalNumberOfProducts();


			// delegate module handler: click CHECKBOX, trigger filter via data attributes
			$('.b-finder', self.$grid).on('click.gridItem', function(e) {
				var $e = $(this),
					columnIndex = $e.data('column'),
					groupIndex = $e.data('group'),
					valueIndex = $e.data('value');

				self.toggleValueAndRefine(columnIndex, groupIndex, valueIndex);
			});


			// delegate module handler: click RADIO, trigger filter via data attributes
			$('.b-radio', self.$grid).on('click.gridRadio', function(e) {
				var $e = $(this),
					columnIndex = $e.data('column'),
					groupIndex = $e.data('group'),
					valueIndex = $e.data('value'),
					isChecked = $e.prop('checked');// basically this is always true

				self.toggleRadioValueAndRefine(columnIndex, groupIndex, valueIndex, isChecked);
			});


			// delegate module handler: click CUSTOM value radio, trigger filter + custom range via data attributes
			$('.b-custom', self.$grid).on('click.gridCustom', function(e) {
				var $e = $(this),
					columnIndex = $e.data('column'),
					groupIndex = $e.data('group'),
					valueIndex = $e.data('value'),
					isChecked = $e.prop('checked'),// basically this is always true
					isCustom = valueIndex === 'customValue' ? true : false,// has the item with custom inputs been selected
					$min = $e.parent().find('.customMin'),
					$max = $e.parent().find('.customMax');

				self.toggleCustomValueAndRefine(columnIndex, groupIndex, isCustom, $min.val(), $max.val());
			});


			// delegate module handler: click NOLIMIT radio, trigger filter via data attributes
			$('.b-nolimit', self.$grid).on('click.gridNolimit', function(e) {
				var $e = $(this),
					columnIndex = $e.data('column'),
					groupIndex = $e.data('group'),
					isChecked = $e.prop('checked');// basically this is always true

				self.toggleRadioValueAndRefine(columnIndex, groupIndex, null, isChecked);
			});


			// delegate module handler: keyup in custom range inputs triggers customValue selection
			self.$customRange.on('keyup.gridChange', function(e) {
				var $e = $(this),
					$target = $e.parent().siblings('.b-custom');

				$target.trigger('click.gridCustom');
			});


			// delegate module handler:
			//	click btnGo updates search url (https://jira.namics.com/browse/DISTRELEC-2538)
			//	click btnGo validates custom range input (https://jira.namics.com/browse/DISTRELEC-2539)
			// TODO if at some point later on there are multiple custom instances (not only for 'price'), refactoring is needed!
			self.$btnGo.off('click.refine').on('click.refine', function(ev) {
				// debug only
				//ev.preventDefault();


				// update go url manually (no json update/post), beware XSS
				var _url = Tc.Utils.splitUrl(self.$btnGo.attr('href')),// for url manipulation refer to 60-urlParser.js
					$min = self.$grid.find('.customMin').first(),
					_minTmp = $min.val(),
					_min = isFinite(parseInt(_minTmp, 10)) ? _minTmp : 0,
					$max = self.$grid.find('.customMax').first(),
					_maxTmp = $max.val(),
					_max = isFinite(parseInt(_maxTmp, 10)) ? _maxTmp : 0,
					blnCustomActive = $min.parent().siblings('.b-custom').is(':checked'),
					blnCustomValid = false,
					_newUrl;


				// update url for custom range only if custom radio is selected
				if (blnCustomActive) {
					// custom range valid?
					Tc.Utils.validate($('.validate-range', self.$grid), self.validationErrorCustomRange, 'triangle', function(error) {
						if (error) {
							ev.preventDefault();
						}
						else {
							blnCustomValid = true;
						}
					});

					// add custom min/max filter to url
					if (typeof _url.get === 'undefined') {
						_url.get = {};
					}
					_url.get.filter_Price = _min + '+-+' + _max;// e.g. &filter_Price=6133+-+30550
					_newUrl = Tc.Utils.joinUrl(_url);


					// if active and valid, update
					if (blnCustomValid) {
						self.updateGoUrl(_newUrl);
					}
				}
			});


			// delegate module handler: click reset btn
			$('.b-reset', this.$ctx).on('click.reset', function(e) {
				e.preventDefault();

				self.resetValues();
			});


			callback();
		},


		/* helper: update data obj (productFinderData) for toggled item (checkbox)
		*	params: columnIndex (String),
		*			groupIndex (String),
		*			valueIndex (String)
		*/
		toggleValueAndRefine: function(columnIndex, groupIndex, valueIndex) {
			var self = this;


			// switch standard value (switch 'checked' true|false with each click)
			self.data.columns[columnIndex].groups[groupIndex].values[valueIndex].checked = !self.data.columns[columnIndex].groups[groupIndex].values[valueIndex].checked;


			// now refine
			self.refineProductFinder();
		},


		/* helper: update data obj (productFinderData) for toggled item (radio)
		*	params: columnIndex (String),
		*			groupIndex (String),
		*			valueIndex (String),
		*			isChecked (boolean)
		*/
		toggleRadioValueAndRefine: function(columnIndex, groupIndex, valueIndex, isChecked) {
			var self = this,
				group;


			// switch radio (switch 'checked' true|false according state)
			// all other radios from same column and group must be unchecked
			group = self.data.columns[columnIndex].groups[groupIndex];

			for (var v = 0, lv = group.values.length; v < lv; v++) {
				if (v !== valueIndex) {
					self.data.columns[columnIndex].groups[groupIndex].values[v].checked = false;
				}
				else {
					self.data.columns[columnIndex].groups[groupIndex].values[v].checked = true;
				}
			}

			// do not forget customValue
			self.data.columns[columnIndex].groups[groupIndex].customValue.checked = false;


			// now refine
			self.refineProductFinder();
		},


		/* helper: update data obj (productFinderData) for toggled item (radio with custom value)
		*	params: columnIndex (String),
		*			groupIndex (String),
		*			valueIndex (String),
		*			isCustom (boolean),
		*			min (String),
		*			max (String)
		*/
		toggleCustomValueAndRefine: function(columnIndex, groupIndex, isCustom, min, max) {
			var self = this,
				minValue = typeof min !== 'undefined' ? min : 0,
				maxValue = typeof max !== 'undefined' ? max : 0,
				group;


			// set customValue min and max (if set yet)
			// all other radios from same column and group must be unchecked
			if (isCustom) {
				self.data.columns[columnIndex].groups[groupIndex].customValue.minValue = minValue;
				self.data.columns[columnIndex].groups[groupIndex].customValue.maxValue = maxValue;
			}
			else {
				group = self.data.columns[columnIndex].groups[groupIndex];

				for (var v = 0, lv = group.values.length; v < lv; v++) {
					self.data.columns[columnIndex].groups[groupIndex].values[v].checked = false;
				}


				// now refine
				self.refineProductFinder();
			}
		},


		// helper: post data obj (productFinderData) to endpoint, callback: use new data for grid update
		refineProductFinder: function() {
			var self = this;

			self.$loader.addClass('loading');// loading indicator, removed again in complete()

			$.ajax({
				url: '/productFinder',
				type: 'post',
				data: $.toJSON(self.data),
				contentType: 'application/json',
				dataType: 'json',
				success: function(response) {
					self.data = response;
					self.updateValues();
					self.updateGoUrl(response.searchUrl);
				},
				error: function(jqXHR, textStatus, errorThrown) {
				},
				complete: function() {
					self.$loader.removeClass('loading');
				}
			});
		},


		// helper: call updateValue for each item (checkbox) in data obj (productFinderData), update total result count
		updateValues: function() {
			var self = this;

			if (Boolean(self.data.columns)) {
				var cols = self.data.columns;// cache static part

				for (var c = 0, lc = cols.length; c < lc; c++) {
					var column = cols[c];

					for (var g = 0, lg = column.groups.length; g < lg; g++) {
						var group = column.groups[g];

						for (var v = 0, lv = group.values.length; v < lv; v++) {
							self.updateValue(c, g, v);
						}
					}
				}

				self.updateTotalNumberOfProducts();
			}
			else {
			}
		},


		/* helper: update element (checkbox AND label) states as provided
		*	params: columnIndex (String),
		*			groupIndex (String),
		*			valueIndex (String)
		*/
		updateValue: function(columnIndex, groupIndex, valueIndex) {
			var self = this,
				value = self.data.columns[columnIndex].groups[groupIndex].values[valueIndex],
				id = '#column' + columnIndex + '_group' + groupIndex + '_value' + valueIndex;

			$(id).prop('checked', value.checked);
			$(id).prop('disabled', value.disabled);


			// special treatment required for label attribute
			if (value.disabled) {
				$(id).next('label').attr('disabled', true);
			}
			else {
				$(id).next('label').removeAttr('disabled');
			}
		},


		// helper: reset all values (post data obj with all checked values nullified)
		resetValues: function() {
			var self = this;

			if (Boolean(self.data.columns)) {
				var cols = self.data.columns;// cache static part

				for (var c = 0, lc = cols.length; c < lc; c++) {
					var column = cols[c];

					for (var g = 0, lg = column.groups.length; g < lg; g++) {
						var group = column.groups[g];

						for (var v = 0, lv = group.values.length; v < lv; v++) {
							cols[c].groups[g].values[v].checked = null;
						}


						// reset custom value
						if (Boolean(cols[c].groups[g].customValue)) {
							if (typeof cols[c].groups[g].customValue !== 'undefined') {
								var $radio = $('#column' + c + '_group' + g + '_valuecustomValue');// DOM node

								cols[c].groups[g].customValue.checked = null;// data value
								$radio.prop('checked', false);// node state
								$radio.siblings('fieldset').find('input').val('');// min/max
							}
						}
					}
				}

				self.refineProductFinder();
			}
		},


		// helper: update total result count
		updateTotalNumberOfProducts: function() {
			var self = this,
				_txt = this.$total.data('text'),
				$counter = $('<span></span>');

			if (typeof self.data.totalNumberOfResults !== 'undefined') {
				self.$total.empty();
				$counter.text(self.data.totalNumberOfResults);
				self.$total.append($counter, _txt);
			}
		},


		// helper: update url to results page
		updateGoUrl: function(url) {
			var self = this;

			if (typeof url !== 'undefined') {
				self.$btnGo.attr('href', url);
			}
		}
	});
})(Tc.$);