(function($) {
  /**
   * Module implementation.
   *
   * @namespace Tc.Module
   * @class Buying-section
   * @extends Tc.Module
   */
  Tc.Module.BuyingSection = Tc.Module.extend({
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

      this.$numeric = $(".numeric", $ctx);
      this.$btnCart = $(".btn-cart", $ctx);
      this.$isOCI = $('.mod-buying-section .btn-added-to-cart').hasClass('buying-section__OCI');

      this.$ipt = $(".ipt", $ctx);

      // subscribe to connector channel/s
      this.sandbox.subscribe("lightboxQuotation", this);
      this.sandbox.subscribe("toolsitemShopping", this);
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

      Tc.Utils.numericStepper(self.$numeric, {
        // Fire event to update quantity on toolsitem shopping
        warning: function(newValue) {
          self.quantityUpdate(self.$numeric.data("product-code"), newValue);
        },
        success: function(newValue) {
          self.quantityUpdate(self.$numeric.data("product-code"), newValue);
        }
      });

        function userPDPCheck() {

            if (
                (sessionStorage.getItem("userJourney") === "tempTrue" || sessionStorage.getItem("userJourney") === "null") &&
                digitalData.page.pageInfo.search.addtocart !== undefined && sessionStorage.getItem('userCartJourney') !== 'false'
            ) {
                digitalData.page.pageInfo.search.addtocart = true;
            } else {
                digitalData.page.pageInfo.search.addtocart = false;
            }
        }

        this.$btnCart.on("click", function() {
            sessionStorage.setItem('userCartJourney', 'false');
            userPDPCheck();
            self.addToCart();
        });

      var $btnBulkDiscount = $(".btn-bulk-discount", this.$ctx);

      $btnBulkDiscount
        .off("click.quotation")
        .on("click.quotation", function(ev) {
          ev.preventDefault();

          self.fire(
            "checkUserLoggedIn",
            {
              $btn: $btnBulkDiscount,
              quantity: self.$ipt.val()
            },
            ["lightboxQuotation"]
          );
        });

      $(".numeric .btn-wrapper").click(function() {
        var inputValue = self.$ipt.val(); // Quantity
        updatePrice(inputValue);
      });

      $(".numeric .ipt").keypress(function(e) {
        if (e.which === 13) {
          var inputValue = self.$ipt.val(); // Quantity
          updatePrice(inputValue);
        }
      });

      self.$numeric.focusout(function() {
        if (
          self.$numeric.hasClass("numeric-error") ||
          $(".btn-cart").hasClass("disabled")
        ) {
          $(".btn-cart").addClass("disabled");
        } else {
          $(".btn-cart").removeClass("disabled");
        }
      });

      // Logic in function is also re-used in "terrific/modules/product/js/product.skin.shopping.js"
      // If something needs to be changed, please update it there as well
      function updatePrice(inputValue) {
        var arr = []; // qty breaks
        var arrTwo = []; // prices
        var pricesWithVat = [];
        var arrPricePer = []; // prices per quantiy

        $(".mod-scaled-prices .body").each(function(index, elem) {
          var itemQty = $(this)
            .find(".nth1")
            .text()
            .replace("+", "")
            .replace("\u00a0", "")
            .replace(" ", "");
          var itemPrice = $(this)
            .find(".js-pricesWithoutVat")
            .text();
          var itemPricePer = $(this)
                .find(".nth2-price-per .price-per")
                .text();

          var itemPriceWithVat = $(this).find(".js-pricesWithVat").text();

          arr.push(itemQty);
          arrTwo.push(itemPrice);
          pricesWithVat.push(itemPriceWithVat);
          arrPricePer.push(itemPricePer);
        });

        arr.join(",");
        arrTwo.join(",");
        arrPricePer.join(",");
        pricesWithVat.join(",");

        var price = localStorage.getItem("currentPrice"); // Storing Price using local storage
        var pricePer = localStorage.getItem("currentPricePer"); // Storing Price using local storage
        var currentQuantity = parseInt(inputValue);

        $.each(arr, function(i) {
          var found = false;
          var breakPoint = parseInt(arr[i]);

          if (currentQuantity === breakPoint) {
            var GetPriceGreen = arrTwo[i].match(/[\d\.]+/)[0];

            if ( arrPricePer[i].length > 0) {
                var GetPricePer = arrPricePer[i].match(/[\d\.]+/)[0];
            }

            found = true;

            setTimeout(function() {
              $(".skin-scaled-prices-single .price.js-withoutVat .odometer-price").html(arrTwo[i]);
              $(".skin-scaled-prices-single .price.js-withVat .odometer-price").html(pricesWithVat[i]);

              $(".skin-scaled-prices-single .price-per--quantity").html(
                  '<span class="odometer-priceper green-price">' + arrPricePer[i] + "</span>"
              );
              localStorage.setItem("currentPrice", GetPriceGreen);
              localStorage.setItem("currentPricePer", GetPricePer);
            }, 3.0);
          } else if (currentQuantity < breakPoint) {
            var redClass = "",
                redClassPer = "";
            GetPriceRed = arrTwo[i - 1].match(/[\d\.]+/)[0];
            if ( arrPricePer[i - 1].length > 0) {
                GetPricePerRed = arrPricePer[i - 1].match(/[\d\.]+/)[0];

                if (pricePer !== GetPricePerRed) {
                    redClassPer = " red-priceper";
                }

                localStorage.setItem("currentPricePer", GetPricePerRed);
            }

            found = true; // If condition is fulfilled exit loop

            if (currentQuantity === breakPoint - 1 && price !== GetPriceRed) {
              redClass = " red-price";
            }

            localStorage.setItem("currentPrice", GetPriceRed);

            setTimeout(function() {
              $(".skin-scaled-prices-single .price.js-withoutVat .odometer-price").html(arrTwo[i - 1]);
              $(".skin-scaled-prices-single .price.js-withVat .odometer-price").html(pricesWithVat[i - 1]);

              $(".skin-scaled-prices-single .price-per--quantity").html(
                  '<span class="odometer-priceper '+redClassPer+'">' + arrPricePer[i-1] + "</span>"
              );
            }, 3.0);
          } else if (i === arr.length - 1 && currentQuantity > breakPoint) {
            localStorage.setItem("currentPrice", arrTwo[i].match(/[\d\.]+/)[0]);

            if ( arrPricePer[i].length > 0) {
                localStorage.setItem("currentPricePer", arrPricePer[i].match(/[\d\.]+/)[0]);
            }

            setTimeout(function() {
              $(".skin-scaled-prices-single .price.js-withoutVat .odometer-price").html(arrTwo[i]);
              $(".skin-scaled-prices-single .price.js-withVat .odometer-price").html(pricesWithVat[i]);

              $(".skin-scaled-prices-single .price-per--quantity").html(
                  '<span class="odometer-priceper green-priceper">' + arrPricePer[i] + "</span>"
              );
            }, 3.0);
          }

          if (found) {
            return false;
          }
        });
      }

      callback();
    },

    // Update Shopping list tools item so that the correct quantity would be added to shopping list

    quantityUpdate: function(productCode, newQuantity) {
      this.fire(
        "quantityUpdate",
        { productCode: productCode, newQuantity: newQuantity },
        ["toolsitemShopping"]
      );
    },

    /**
     * Hook function to trigger your events.
     *
     * @method after
     * @return void
     */
    after: function() {
      // Enable Cart Button after Modules on js was loaded
      // Additional logic for disabling/enabling cart button is in "modules/shipping-information/js/shipping-information.skin.pdp.js" -> "erpDisabledStatuses"
      if (this.$btnCart.attr("disabled") !== "disabled") {
        this.$btnCart.removeClass("disabled");
      }
    },

    /**
     *
     * @method addToCart
     *
     */
    addToCart: function() {
      if (!this.$numeric.hasClass("numeric-error")) {
        // we take the query param from the url to send it to the backend for FactFinder Tracking in the backend
        var url = Tc.Utils.splitUrl(document.URL);
        var posVal;
        var origPosVal;
        var origPageSizeVal;
        var queryParam;
        var pageType;
        var prodPriceVal = (digitalData.product[0].productInfo.unitPrice.length > 0 ? digitalData.product[0].productInfo.unitPrice[0].split(":").join("") : "");
      	 
      	  queryParam = $("#hidden-searchQuery").val();
          posVal = $("#hidden-pos").val();
          origPosVal = $("#hidden-origPosition").val();
          origPageSizeVal = $("#hidden-origPageSize").val();
          pageType = $("#hiddenPageType").val();
          filterApplied = $("#hidden-filterapplied").val();

        // hide possible warning popover
        this.$numeric.removeClass("numeric-warning");

        $(document).trigger("cart", {
          actionIdentifier: "orderDetailPageAddToCart",
          type: "add",
          productCodePost: this.$btnCart.data("product-code"),
          qty: this.$ipt.val(),
          trackQuery: queryParam,
          pos: posVal,
          origPos: origPosVal,
          origPageSize: origPageSizeVal,
          prodprice: prodPriceVal,
          pageType: pageType,
          filterApplied: filterApplied
        });

        var webshop_Logo = ($('.mod-logo').find('img').attr('src') !== "" ?  window.location.origin + $('.mod-logo').find  ('img').attr('src') : "");
        var product_Image = ($('.images-in-lightbox').attr('src') !== "" ?  window.location.origin + $('.images-in-lightbox').attr('src') : "");
        var product_Price = (digitalData.product[0].productInfo.unitPrice.length > 0 ? digitalData.product[0].productInfo.unitPrice[0].split(":").join("") : "");

        window._peq = window._peq || []; window._peq.push(["init"]);

        window._peq.push(["add-to-trigger",{
          "campaign_name": "PushEngage Cart Abandonment",
          "event_name": "add-to-cart",
          "title": {
            "productname": digitalData.product[0].productInfo.productName
          },
          "message": {
            "price": product_Price
          },
          "notification_url": {
            "notificationurl": window.location.origin + '/checkout'
          },
          "notification_image": {
            "imageurl": webshop_Logo
          },
          "big_image": {
            "bigimageurl": product_Image
          }
        }]);

        $(".mod-buying-section .btn-added-to-cart--checkout").removeClass(
          "show"
        );

        if (this.$isOCI) {
          $(".mod-buying-section .btn-added-to-cart").addClass("show");
          $(".mod-buying-section .btn-added-to-cart--added").addClass("show");
          $(".mod-buying-section .btn-added-to-cart--checkout").addClass(
              "show"
          );

          setTimeout(function() {
            $(".mod-buying-section .btn-added-to-cart--added").removeClass(
                "show"
            );
          }, 1000);
        }
      }
    }
  });
})(Tc.$);
