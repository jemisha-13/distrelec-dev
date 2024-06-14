(function($) {

	/**
	 * This module implements the accordion content skin technical information on the product detail page
	 *
	 * @namespace Tc.Module
	 * @class DetailAccordionContent
	 * @skin TechnicalInformationCompare
	 * @extends Tc.Module
	 */
	Tc.Module.DetailAccordionContent.TechnicalInformationCompare = function (parent) {

		/**
		 * override the appropriate methods from the decorated module (ie. this.get = function()).
		 * the former/original method may be called via parent.<method>()
		 */
		this.on = function (callback) {
			// calling parent method
			var self = this;
			this.$btnShowMore = this.$$('.btn-show-more');

			// connect
			this.sandbox.subscribe('compareGrid', this);

            self.$btnShowMore.on('click', function (e) {
                e.preventDefault();
                self.showMore();
            });

            var possibleCommonAttributes = $(".possibleCommonAttributes")[0].getElementsByTagName("li").length;
            var possibleOtherAttributes = $(".possibleOtherAttributes")[0].getElementsByTagName("li").length;

			self.iterateOverProductDetails(possibleOtherAttributes, "possibleOtherAttributes__", "otherAttributes_product__");
			self.iterateOverProductDetails(possibleCommonAttributes, "possibleCommonAttributes__", "commonAttributes_product__");

			parent.on(callback);
		};

		this.iterateOverProductDetails = function(productAttributeLength, featureClassName, productClassName) {
			for (var i = 0; i < productAttributeLength; i++) {
				this.forEachProductAttribute(featureClassName, productClassName, i);
			}
		};

		// Make height of product rows equal
		this.forEachProductAttribute = function(featureClassName, productClassName, i) {
			var myArray = [];
			var queryProductAttributes = $( "." + productClassName + i );
			var queryFeatureAttributes = $( "." + featureClassName + i );

			queryProductAttributes.each(function() {
				myArray.push($( this )[0].getBoundingClientRect().height);
			});
			var maxValueInArray = Math.max.apply(Math, myArray);

			queryProductAttributes.each(function() {
				$( this )[0].style.height = maxValueInArray + 'px';
			});
			queryFeatureAttributes.each(function() {
				($( this )[0].style.height = maxValueInArray + 'px');
			});
		};

        this.showMore = function() {
            var self = this,
                $ctx = this.$ctx;
            $ctx.find('.row-hidden').removeClass('row-hidden');
            self.$btnShowMore.parent('.row').css('display', 'none');
	        // Update Element Heights
	        self.fire('showMoreTechnicalAttributes', {  }, ['compareGrid']);
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
