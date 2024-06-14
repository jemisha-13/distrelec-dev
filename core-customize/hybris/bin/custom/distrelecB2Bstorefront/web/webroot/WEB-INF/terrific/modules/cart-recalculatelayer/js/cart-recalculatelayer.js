(function($) {
  /**
   * Module implementation.
   *
   * @namespace Tc.Module
   * @class Cart-recalculatelayer
   * @extends Tc.Module
   */
  Tc.Module.CartRecalculatelayer = Tc.Module.extend({
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

      // subscribe to connector channel/s
      this.sandbox.subscribe("cart", this);
      this.sandbox.subscribe("lightboxStatus", this);

      this.onCartChange = $.proxy(this, "onCartChange");

      // jQuery Var's
      this.$btnRecalculate = $(".btn-recalculate", $ctx);
    },

    /**
     * Hook function to do all of your module stuff.
     *
     * @method on
     * @param {Function} callback function
     * @return void
     */
    on: function(callback) {
      $(document).on("cartChange", this.onCartChange);

      callback();
    },

    /**
     *
     * @method onActivateRecalculate
     *
     * fire method for enabling recalculate layer
     *
     */
    onActivateRecalculate: function() {
      var $ctx = this.$ctx,
        self = this,
        init = function() {
          self.$btnRecalculate.off("click").on("click", function() {
            self.recalculate();
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

      $(".mod-checkout-proceed .btn-checkout").addClass("disabled");

      self.sticky();
    },

    /**
     *
     * @method deactivateRecalculate
     *
     */
    deactivateRecalculate: function() {
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
          self.$btnRecalculate.removeClass("btn-recalculate-loading");
        }
      );
    },

    /**
     *
     * @method recalculate
     *
     * send productData from the method gatherProductData to the server
     * result should be a object containing recalculated prices
     *
     */
    recalculate: function() {
      var $ctx = this.$ctx,
        self = this;

      if (!self.$btnRecalculate.hasClass("btn-recalculate-loading")) {
        self.$btnRecalculate.addClass("btn-recalculate-loading");

        $.ajax({
          url: "/cart/dangerousProducts",
          type: "get",
          dataType: "json",
          success: function(data) {
            if (data.dangerousProducts === "") {
              $(".dangerous-goods-warning").addClass("hidden");
            } else {
              $(".dangerous-goods-warning").removeClass("hidden");
              $(".list-of-products").html(
                self.htmlDecode(data.dangerousProducts)
              );
            }

            sessionStorage.setItem("userCartJourney", "false");

            location.reload();
          },
          error: function(jqXHR, textStatus, errorThrown) {}
        });
      }
    },

    htmlDecode: function(input) {
      var e = document.createElement("div");
      e.innerHTML = input;
      return e.childNodes.length === 0 ? "" : e.childNodes[0].nodeValue;
    },

    /**
     *
     * @method onCartChange
     *
     * event is coming from cart api
     *
     * @param data
     */
    onCartChange: function(ev, data) {
      var self = this;

      if (data.actionIdentifier === "recalculateLayer") {
        self.recalculateResultSuccess(data.ajaxSuccessData);
      }
    },

    /**
     *
     * @method recalculateResultSuccess
     *
     * fire the result object to the priceCalcBox module an disable the layer
     *
     * @param data
     */
    recalculateResultSuccess: function(data) {
      var self = this,
        $ctx = this.$ctx;

      this.deactivateRecalculate();
      this.fire("writeCalcBoxData", { data: data.cartData }, ["cart"]);
      this.fire("writeCalcBoxDataItem", { data: data.cartData.products }, [
        "cart"
      ]);
    },

    /**
     *
     * @method sticky
     *
     * use the waypoint plugin
     *
     */
    sticky: function() {
      var self = this,
        $ctx = this.$ctx,
        $overlay = $(".btn-recalculate-wrap", $ctx),
        vHeight = $.waypoints("viewportHeight"),
        oOffset = $overlay.offset(),
        btnOffset = self.$btnRecalculate.offset();

      if (btnOffset.top < oOffset.top) {
        self.$btnRecalculate.addClass("stickToTop");
      }

      if (oOffset.top > vHeight) {
        self.$btnRecalculate.addClass("stickToTop");
      }

      // Waypoint for the Top of the Overlay
      $overlay.waypoint(
        function(direction) {
          if (direction === "up") {
            self.$btnRecalculate
              .addClass("stickToTop")
              .removeClass("stickToBottom");
          } else {
            self.$btnRecalculate.removeClass("stickToTop");
          }
        },
        {
          offset: function() {
            var sightCorrection = 60; // Sight Correction is a trial and error value to make fine adjustment
            return vHeight / 2 - sightCorrection;
          }
        }
      );

      // Waypoint for the Bottom of the Overlay
      $overlay.waypoint(
        function(direction) {
          if (direction === "down") {
            self.$btnRecalculate
              .addClass("stickToBottom")
              .removeClass("stickToTop");
          } else {
            self.$btnRecalculate.removeClass("stickToBottom");
          }
        },
        {
          offset: function() {
            var sightCorrection = 84; // Sight Correction is a trial and error value to make fine adjustment
            return (
              vHeight / 2 -
              $(this).height() +
              self.$btnRecalculate.height() +
              sightCorrection
            );
          }
        }
      );

      self.$btnRecalculate.css("visibility", "visible");
    }
  });
})(Tc.$);
