(function($) {
  /**
   * Module implementation.
   *
   * @namespace Tc.Module
   * @class Mainnav
   * @extends Tc.Module
   */
  Tc.Module.Mainnav = Tc.Module.extend({
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

      this.flyoutTimeout;
      this.flyoutTimeoutDuration = 0;
      this.flyoutFadeInDuration = 0;

      this.flyoutLeaveTimeout;
      this.flyoutLeaveTimeoutDuration = 0;
      this.flyoutFadeOutDuration = 0;

      this.$l1 = $(".l1", $ctx);
      this.$e1 = $(".e1", $ctx);

      // Flyout can be disabled per Customer
      this.enableFlyout = this.$l1.data("enable-flyout");
    },

    /**
     * Hook function to do all of your module stuff.
     *
     * @method on
     * @param {Function} callback function
     * @return void
     */
    on: function(callback) {
      var buttonClass = $(".e1-button"),
        bodyClass = $("body");

      buttonClass.click(function() {
        $(".overlay-second").toggleClass("overlay-second--active");
        $(".li-mouseover.main_li").addClass("nav-hover");
        bodyClass.toggleClass("disable");
        self.resizeDropDown();
      });

      $(".overlay-second").click(function() {
        $(this).removeClass("overlay-second--active");
        bodyClass.removeClass("disable");
        $(".li-mouseover.main_li").addClass("nav-hover");
      });

      /* Special treatment for the first e1 */
      $("#js-products-dropdown").on({
        click: function(e) {
          $(".products-dropdown .mod-maincategorynav").toggleClass("display-block");

          //DISTRELEC-11522 prevent the overlapping of home-left-banner. Only visible in home page
          if ($(".skin-layout-nonavigation").length > 0) {
            $(".category-nav").toggleClass("category-nav--active");
            $(".home-left-banner").addClass("hidden");
          }
        }
      });

      if (this.enableFlyout) {
        var $ctx = this.$ctx,
          self = this,
          $body = $("body"),
          $l2 = $(".l2", $ctx),
          $arrow = $(".a1", $ctx).find("i");

        self.$l1.on({
          click: function(e) {
            e.stopPropagation();
            clearTimeout(self.flyoutLeaveTimeout);

            self.flyoutTimeout = setTimeout(function() {
              $body.addClass("l1-flyout");
            }, self.flyoutTimeoutDuration);
          }
        });

        $(document).click(function(e) {
          $(".l2").addClass("hidden");
          $(".e1").removeClass("hover");
          $(".a1").css("color", "#555");
        });

        self.flyoutBind();
      }

      callback();
    },
    resizeDropDown: function () {
      var level1menu = $(".level-1-wrapper"),
          windowHeight = $(window).outerHeight(true),
          prodNavHeight = $(".mod-maincategorynav").outerHeight(true),
          headerHeight = $(".mod-stickyheader").outerHeight(true),
          calculatedProdNaHeight = 0,
          prodNavAreaHeight = prodNavHeight + headerHeight;

        if (prodNavAreaHeight >= windowHeight) {
          calculatedProdNaHeight = windowHeight - headerHeight - 25;
          level1menu.height(calculatedProdNaHeight);
        }

    },

    flyoutBind: function() {
      var self = this,
        manufactureNavClickedTarget = false;

      /* First e1 treated differently */
      $("#js-products-dropdown").hoverIntent(function(e){
           $(".products-dropdown .mod-maincategorynav").addClass("display-block");

          if ($(".skin-layout-nonavigation").length > 0) {
            $(".category-nav").toggleClass("category-nav--active");
            $(".home-left-banner").addClass("hidden");
          }
        $('.flyout-settings').addClass('hidden');
        self.resizeDropDown();

      }, function() {
         $(".products-dropdown .mod-maincategorynav").removeClass("display-block");
      });

      $(this.$e1).hoverIntent(function(e) {

        $(this)
            .addClass("hover")
            .siblings(".e1")
            .removeClass("hover");

        $(this)
            .siblings()
            .find(".l2")
            .addClass("hidden");

        var menuL1Title = $(this)
            .find(".level1nodeTitle")
            .data("entitle");

        manufactureNavClickedTarget =
            $(e.target).hasClass("man-select") ||
            $(e.target).hasClass("man-nav");

        if (!manufactureNavClickedTarget) {
          $(this)
              .find(".l2")
              .toggleClass("hidden");
        }

        $('.flyout-settings').addClass('hidden');
      }, function(){
        $(this)
            .find(".l2")
            .addClass("hidden");
      });

      // Level 1 Elements Events
      self.$e1.on({
        click: function(e) {
          $(this)
            .addClass("hover")
            .siblings(".e1")
            .removeClass("hover");
          $(this)
            .siblings()
            .find(".l2")
            .addClass("hidden");

          var menuL1Title = $(this)
            .find(".level1nodeTitle")
            .data("entitle");


          manufactureNavClickedTarget =
            $(e.target).hasClass("man-select") ||
            $(e.target).hasClass("man-nav");
          if (!manufactureNavClickedTarget) {
            $(this)
              .find(".l2")
              .toggleClass("hidden");
          }

          $(".a1").css("color", "#555");
          $(this)
            .find(".a1")
            .css("color", "#009fb4");

          // needed in IE7 / IE Compatibility Mode to trigger flyout to appear, when hovering between l1 navigation elements
          if (Modernizr.isie7) {
            self.$$(".l2").css("visibility", "hidden");
            $(e.target)
              .closest(".e1")
              .find(".l2")
              .css("visibility", "visible");
          }

          // When "Main nav" has been opened, close the "Shop settings" popup (Only one dropdown open - DISTRELEC-24252)
          $('.flyout-settings').addClass('hidden');
        }
      });
    },

    after: function() {
      $(".l2:first").css("margin-top", "50px");
      $(".l2:last").css("float", "right");
      $(".l2:last").css("right", "0");
      $(".l2:last").css("margin-top", "24px");
      $(".l2:last .arrow-up").css("margin-left", "246px");

      $(".count_e1_2 .l2").css("margin-left", "0px");
    }
  });
})(Tc.$);
