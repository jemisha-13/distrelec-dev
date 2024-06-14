(function($) {

	/**
	 * This module implements the accordion content skin technical information on the product detail page
	 *
	 * @namespace Tc.Module
	 * @class DetailTabsContent
	 * @skin TechnicalInformation
	 * @extends Tc.Module
	 */
	Tc.Module.DetailTabsContent.TechnicalInformation = function (parent) {

		/**
		 * override the appropriate methods from the decorated module (ie. this.get = function()).
		 * the former/original method may be called via parent.<method>()
		 */
		this.on = function (callback) {
			// calling parent method
			parent.on(callback);
			var self = this;
			this.$btnSearch = this.$$('.btn-search');
			this.$btnInfo = this.$$('.btn-info');
			this.$btnShowMore = this.$$('.btn-show-more');
			this.$checkboxes = this.$$('input[type=checkbox]');
			this.$checkboxeslabels = this.$$('.feature-checkbox');
			this.blnAllowSimilarSearch = false;
			this.validationErrorEmpty = this.$$('#tmpl-similarsearch-error-empty').html();
			this.$searchsimilarDescription = this.$$('.searchsimilar-description');

			this.$btnInfo.on('mouseover', function(ev){
				ev.preventDefault();
				$('.searchsimilar-description').removeClass('hidden');
			});	
			
			this.$btnInfo.on('mouseout', function(ev){
				ev.preventDefault();
				$('.searchsimilar-description').addClass('hidden');
			});				
			
			self.$btnSearch.off('click').on('click', function (e) {

				// Validation
				var isValid = true;

				if(isValid) {
					self.blnAllowSimilarSearch = true;
					self.getSearchValues(); // search only allowed if isValid
				}
				return false;
			});			

			self.$btnShowMore.off('click').on('click', function (e) {
				e.preventDefault();
				self.showMore();
			});


			// checkboxes click handler: toggle similar search button
			self.$checkboxes.on('click', function() {
				self.updateAllowSimilarSearch();

				if (self.blnAllowSimilarSearch) {
					self.$btnSearch.removeClass('disabled');
				} else {
					self.$btnSearch.addClass('disabled');
				}
			});


			// init button state
			self.updateAllowSimilarSearch();
		};

		this.updateAllowSimilarSearch = function() {

			var self = this,
				checkedCount = 0;

			// loop checkbox group, count :checked
			$.each(self.$checkboxes, function() {
				if ($(this).is(':checked')) {
					checkedCount += 1;
				}
			});


			// set boolean for positive counts
			self.blnAllowSimilarSearch = checkedCount > 0;
			self.updateButtonState();
		};

		this.updateButtonState = function() {
			var self = this;
			if (self.blnAllowSimilarSearch) {
				self.$btnSearch.removeClass('disabled');
			}
			else {
				self.$btnSearch.addClass('disabled');
			}
		};

		this.getSearchValues = function () {
			var $ctx = this.$ctx,
				self = this,
				searchFor = [],
				url = Tc.Utils.splitUrl(self.$btnSearch.data('search-url'));

			// if there were no get params in the url before
			if (url.get === undefined) {
				url.get = {};
			}

			self.$checkboxes.each(function () {
				var $ipt = $(this),
					$td = $ipt.closest('td'),
					$featureValue = $td.next('td').find('.feature-value'),
					featureValueInner = $featureValue.html(),
					$featureUnit = $td.next('td').find('.feature-unit'),
					featureUnitInner = $featureUnit.html();

				// build get parameter
				if ($ipt.prop('checked')) {
					var key = 'filter_' + $ipt.attr('name'),
						val = '';

					if ($featureUnit.length > 0 && featureUnitInner !== '') {
						key += '~~' + featureUnitInner.replace(/ /g, '+').replace(/&nbsp;/g, '+').replace(/%/g,'%25');
					}

					if ($featureValue.length > 0 && featureValueInner !== '') {
						val = featureValueInner.replace(/%/g,'%25').replace(/\+/g, '%2B').replace(/ /g, '+').replace(/&nbsp;/g, '+').replace(/±/g,'%26plusmn%3B');
					}

					url.get[key] = val;
				}
			});

			location.href = Tc.Utils.joinUrl(url);
		};

		this.showMore = function () {
			var self = this,
				$ctx = this.$ctx;

			$ctx.find('.row-hidden').removeClass('row-hidden');
			self.$btnShowMore.parent('.row').css('display', 'none');
		};

		/**
		 * Hook function to trigger your events.
		 *
		 * @method after
		 * @return void
		 */
		this.after = function () {
			// calling parent method
			parent.after();
		};

	};

})(Tc.$);
