(function($) {
  /**
   * Productlist Skin Favorite implementation for the module Productlist Order.
   *
   */
  Tc.Module.ProductCard.ProductAlsobought = function(parent) {
    /**
     * override the appropriate methods from the decorated module (ie. this.get = function()).
     * the former/original method may be called via parent.<method>()
     */
    this.currentLanguage = $('#backenddata .shopsettings').data('language');

    this.on = function(callback) {
      // bind handlers to module context
      var that = this,
          $carouselContainer = this.$ctx.find(".recommendations-holder"),
          productUrl = Tc.Utils.splitUrl(document.URL),
          ajaxUrlPostfix = $carouselContainer.data("ajax-url"),
          ajaxUrl = productUrl.base + ajaxUrlPostfix,
          country = $('#backenddata .shopsettings').data('country'),
          productCodeErp = $carouselContainer.data('ff-producterp'),
          searchExperience = $('#backenddata .shopsettings').data('search-experience');

      $(
        ".skin-product-card-product-alsobought .col-12 .card-item-anchor"
      ).click(function() {
        sessionStorage.setItem("userJourney", "false");
      });

      var accessoriesVm = new Vue({
        el: "#appfirst",
        data: {
          items: [],
          itemsToShow: 8
        },

        methods: {
          setStorage: function() {
            sessionStorage.setItem('userJourney', 'false');
          },

          decode: function(value) {
            if (!value) {
              return value;
            }

            // Fix for "decodeURIComponent" if it receives "%" character, without this JS will break and items won't be shown
            return he.decode(decodeURIComponent(value.replace(/%(?![0-9][0-9a-fA-F]+)/g, '%25')));
          },
          transformDataArray: function(items){
            var transformedArray = [],
                currency = $('#backenddata .shopsettings').data('currency');

            
            items.forEach(function(element,index) {
              transformedArray.push({
                activePromotionLabels: null,
                categories: null,
                code: element.productNumber,
                distManufacturer: element.manufacturer,
                energyEfficiencyData: element.energyEfficiency,
                erpCode: element.productNumber,
                itemPositionOneBased: null,
                name: element.title,
                orderQuantityMinimum: element.itemMin,
                orderQuantityStep: element.itemStep,
                originalPackSize:'',
                price:{
                  currency: currency,
                  formattedValue: '',
                },
                productImageAltText: element.title,
                productImageUrl: element.imageURL,
                promoLabelCompensateClass: null,
                promotiontext: null,
                salesUnit:'',
                showCarouselItemHead:'',
                typeName: element.typeName,
                url: element.productUrl
              });

              var price = $('.shopsettings').data('channel-label') === 'B2C' ? element.singleMinPriceGross : element.singleMinPriceNet,
                  priceParts = price.toString().split('.');

              if (priceParts.length === 1) {
                  priceParts[1] = '00';
              }
              if (priceParts[0].length === 0) {
                  priceParts[0] = '0';
              }
              if (priceParts[1].length < 2) {
                  priceParts[1] += '00'.substring(priceParts[1].length);
              }
              transformedArray[index].price.formattedValue = priceParts.join('.');
              if (priceParts[1].length > 4) {
                transformedArray[index].price.formattedValue = parseFloat(transformedArray[index].price.formattedValue).toFixed(4);
              }
              
              if(country.toLowerCase() === 'se'){
                transformedArray[index].price.formattedValue = transformedArray[index].price.formattedValue.replace('.', ',');
              } 
  
            });
            return transformedArray;
          }
        },

        created: function() {
          var self = this;
          if(searchExperience !== 'factfinder') {
            $(".skin-product-card-product-alsobought").addClass("hidden");
          }else{
            axios.get(ajaxUrl).then(function(response) {
              self.items = response.data.carouselData;
              if (self.items === undefined) {
                $(".skin-product-card-product-alsobought").addClass("hidden");
              }
            });
          }
        },
      });

      parent.on(callback);
    };
  };
})(Tc.$);
