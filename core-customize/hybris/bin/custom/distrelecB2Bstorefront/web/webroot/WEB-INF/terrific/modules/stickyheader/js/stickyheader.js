(function($) {
  /**
   * Module implementation.
   *
   * @namespace Tc.Module
   * @class Logo
   * @extends Tc.Module
   */
  Tc.Module.Stickyheader = Tc.Module.extend({
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
    },

    /**
     * Hook function to do all of your module stuff.
     *
     * @method on
     * @param {Function} callback function
     * @return void
     */
    on: function(callback) {
      var mod = this;

      callback();
    },

    /**
     * Hook function to trigger your events.
     *
     * @method after
     * @return void
     */
    after: function() {
      var self = this;
      var prevScrollpos = window.pageYOffset;

      $(document).ready(function() {
        if ($("body").hasClass("skin-layout-home")) {
          $(window).scrollTop(0);
        }
      });

      if (!Tc.Utils.isEditedInSmartedit()) {
        $(document).scroll(function() {
          var top = $(window).scrollTop();
          var topNeg = -$(window).scrollTop() + 80;
          var navToggle = $("#js-products-dropdown");
          var mainCategoryNav = $(".mod-maincategorynav ");
          var currentScrollPos = window.pageYOffset;
          var stickyLevel1 = $(".sticky-level-1");

          $(".sticky-level-2").css("top", topNeg);

          if (top >= 41) {
            $(navToggle).addClass("fixed");
            stickyLevel1.addClass("sticky-level-1--stuck");
          } else {
            $(navToggle).removeClass("fixed");
            stickyLevel1.removeClass("sticky-level-1--stuck");
          }

          if ($(navToggle).hasClass("fixed")) {
            mainCategoryNav.addClass("mod-maincategorynav--sticky");
          } else {
            mainCategoryNav.removeClass("mod-maincategorynav--sticky");
          }

          prevScrollpos = currentScrollPos;
        });
      }
    }
  });
})(Tc.$);
