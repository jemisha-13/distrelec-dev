(function($) {
  /**
   * CheckoutAddressList Skin PickupStore implementation.
   *
   */
  Tc.Module.CartRecalculatelayer.Shopping = function(parent) {
    this.sandbox.subscribe("cart", this);

    /**
     * override the appropriate methods from the decorated module (ie. this.get = function()).
     * the former/original method may be called via parent.<method>()
     */
    this.on = function(callback) {
      var $ctx = this.$ctx;

      // jQuery Var's
      this.$btnRecalculate = $(".btn-recalculate-shopping", $ctx);
      this.$wrapprerBdRecalculate = $(
        ".skin-cart-recalculatelayer-shopping .bd-cart-recalculate-layer-shopping",
        $ctx
      );
      this.$wrapprerBackRecalculate = $(
        ".skin-cart-recalculatelayer-shopping .back-cart-recalculate-layer-shopping",
        $ctx
      );

      parent.on(callback);
    };

    /**
     *
     * @method onActivateRecalculate
     *
     * fire method for enabling recalculate layer
     *
     */
    this.onActivateRecalculate = function(data) {
      var $ctx = this.$ctx,
        self = this,
        init = function() {
          self.$btnRecalculate.off("click").on("click", function() {
            self.recalculateShoppingList(data);
          });
        };

      if ($ctx.css("display") === "none") {
        $ctx
          .show()
          .css({
            opacity: 0
          })
          .animate(
            {
              opacity: 1
            },
            500,
            init
          );
      }

      self.sticky();
      self.adjustHeight();
    };

    /**
     *
     * @method recalculate
     *
     * send productData from the method gatherProductData to the server
     * result should be a object containing recalculated prices
     *
     */
    this.recalculateShoppingList = function(shoppingListId) {
      var $ctx = this.$ctx,
        self = this;

      if (!self.$btnRecalculate.hasClass("btn-recalculate-loading-shopping")) {
        self.$btnRecalculate.addClass("btn-recalculate-loading-shopping");

        $.ajax({
          url: "/shopping/" + shoppingListId + "/calculate",
          type: "GET",
          dataType: "json",
          method: "get",
          success: function(data, textStatus, jqXHR) {
            self.recalculateShoppingResultSuccess(data);
            localStorage.setItem("userJourney", "false");
            if (digitalData.page.pageInfo.search !== undefined) {
              digitalData.page.pageInfo.search.addtocart = false;
            }
          },
          error: function(jqXHR, textStatus, errorThrown) {}
        });
      }
    };

    this.onCartChange = function(ev, data) {
      var self = this;

      if (data.actionIdentifier === "recalculateLayer") {
        self.recalculateShoppingResultSuccess(data.ajaxSuccessData);
      }
    };

    this.recalculateShoppingResultSuccess = function(data) {
      var self = this;
      var $ctx = this.$ctx;
      var subTotalPrice = data.subTotal.replace("&#039;", "'");
      var totalTax = data.totalTax.replace("&#039;", "'");
      var totalPrice = data.price.replace("&#039;", "'");

      $('.js-subTotalPrice').html(subTotalPrice);
      $('.js-totalTax').html(totalTax);
      $('.js-totalPrice').html(totalPrice);

      $(".calc-total").text(
        data.currency + " " + totalPrice
      );

      this.deactivateRecalculate();
    };

    /**
     *
     * @method deactivateRecalculate
     *
     */
    this.deactivateRecalculate = function() {
      var self = this,
        $ctx = this.$ctx;

      self.$btnRecalculate.off("click");

      $ctx.animate(
        {
          opacity: 0
        },
        500,
        function() {
          $ctx.hide();
          self.$btnRecalculate.removeClass("btn-recalculate-loading-shopping");
        }
      );
    };

    this.adjustHeight = function() {
      var self = this;
      var eTop = $(".skin-cart-pricecalcbox-shopping").css("height");

      self.$wrapprerBdRecalculate.css("height", eTop + "px");
      self.$wrapprerBackRecalculate.css("height", eTop + "px");
    };
  };
})(Tc.$);
