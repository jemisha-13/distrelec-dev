(function($) {
  /**
   * Module implementation.
   *
   * @namespace Tc.Module
   * @class Addresses
   * @extends Tc.Module
   */
  Tc.Module.AccountListFilter = Tc.Module.extend({
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
      var $ctx = this.$ctx,
        self = this;

      this.$inputs = this.$$("input[type=text],select.field");
      this.$btnReset = this.$$(".btn-reset");
      this.$btnSearch = this.$$(".btn-search");
      this.$firstVisit = false;

      // Lazy Load SelectBoxIt Dropdown
      if (!Modernizr.touch && !Modernizr.isie7) {
        Modernizr.load([
          {
            load: "/_ui/all/cache/dyn-jquery-selectboxit.min.js",
            complete: function() {
              self.$$("select.field").selectBoxIt({
                autoWidth: false
              });
            }
          }
        ]);
      }

      // Lazy Load Datepicker and its Region
      Modernizr.load([
        {
          load: [
            "/_ui/all/cache/dyn-jquery-ui-datepicker.min.js",
            "/_ui/all/cache/dyn-jquery-ui-datepicker-i18n.min.js"
          ],
          complete: function() {
              self.initDatePicker();
          }
        }
      ]);


      $("#filter_invoiceto", $ctx).blur(function() {
        var valDateTo = $("#filter_invoiceto").val();
        var valDateFrom = $("#filter_invoicefrom").val();

        if (valDateTo >= 1 && valDateTo <= valDateFrom) {
          $("#filter_invoicefrom").val(valDateTo - 1);
        }
      });

      $(".date-fields input").focus(function() {

        var optRegionalLanguage = $(".shopsettings").data("language");

        if (optRegionalLanguage == "en") {
            optRegionalLanguage = "en-GB";
        }

        $.datepicker.setDefaults( $.datepicker.regional[optRegionalLanguage] );

        if ( $('.ui-datepicker').length === 0 ) {
            self.initDatePicker();
        }

      });

      this.$btnReset.on("click", function() {
        self.$inputs.each(function(index) {
          if ($(this).is("input")) {
            $(this).removeAttr("value");
          }
          if ($(this).hasClass("hasDatepicker")) {
            // Reset maxDate on dateFrom
            $("#filter_datefrom").datepicker("option", "maxDate", "");
          }
          if ($(this).is("select")) {
            $(this)
              .find("option")
              .removeAttr("selected");
            $(this)
              .find("option:first-child")
              .attr("selected", "selected");
            var $select = $(this).data("selectBox-selectBoxIt");

            // only refresh, if plugin was initialized
            if (!Modernizr.touch && !Modernizr.isie7) {
              $select.refresh();
            }
          }
        });
      });

      function convertDate(dateField, dateFormat) {
        if (
          dateField &&
          dateField.length &&
          dateField.datepicker &&
          dateFormat
        ) {
          var date = dateField.datepicker("getDate");
          var val = $.datepicker.formatDate(dateFormat, date);
          dateField.val(val);
        }
      }

      this.$btnSearch.on("click", function(ev) {
        var $theField;
        var $invoiceHistory = $("#invoiceHistoryForm");
        var conversionFormat = $(".date-fields").data("conversion-format");
        if ($("#orderHistoryForm").length) {
          ev.preventDefault();
          // remove "-" from article number in Order History.
          $theField = $("#orderHistoryForm").find("#filter_product_name");
          if ($theField.length) {
            $theField.val($theField.val().replace(/-/g, ""));
          }
          $("#orderHistoryForm").submit();
        } else if ($("#quotationHistoryForm").length) {
          ev.preventDefault();
          // remove "-" from article number in Quotation history.
          $theField = $("#quotationHistoryForm").find("#filter_articleNumber");
          if ($theField.length) {
            $theField.val($theField.val().replace(/-/g, ""));
          }
          $("#quotationHistoryForm").submit();
        } else if (
          $invoiceHistory.length &&
          conversionFormat &&
          conversionFormat.length
        ) {
          ev.preventDefault();
          conversionFormat = conversionFormat
            .replace(/MM/, "mm")
            .replace(/yyyy/, "yy");
          convertDate(
            $invoiceHistory.find("#filter_datefrom"),
            conversionFormat
          );
          convertDate($invoiceHistory.find("#filter_dateto"), conversionFormat);
          convertDate(
            $invoiceHistory.find("#filter_duedatefrom"),
            conversionFormat
          );
          convertDate(
            $invoiceHistory.find("#filter_duedateto"),
            conversionFormat
          );
          convertDate(
            $invoiceHistory.find("#filter_expiry_datefrom"),
            conversionFormat
          );
          convertDate(
            $invoiceHistory.find("#filter_expiry_dateto"),
            conversionFormat
          );
          $invoiceHistory.submit();
        }
      });

      $("#filter_ordernr,#filter_invoicenr,#filter_ordernan").keypress(function(
        e
      ) {
        if (e.which === 32) {
          return false;
        }
      });

      $("#filter_ordernr,#filter_invoicenr,#filter_ordernan").on(
        "paste",
        function(e) {
          var self = this;
          var clipboardData =
            e.clipboardData ||
            e.originalEvent.clipboardData ||
            window.clipboardData;
          var pastedData = clipboardData.getData("text");

          setTimeout(function() {
            $(self).val(pastedData.replace(/\D/g, ""));
          }, 1);
        }
      );

      callback();
    },

    parseDate: function(dateVal,dateReDot,dateReSlash){
      // function parseDate(dateVal) {
        if (dateVal && dateReDot.test(dateVal)) {
            return new Date($.datepicker.parseDate("dd.mm.yy", dateVal));
        } else if (dateVal && dateReSlash.test(dateVal)) {
            return new Date($.datepicker.parseDate("dd/mm/yy", dateVal));
        } else {
            return dateVal;
        }
    },

    initDatePicker: function(){
      var self = this,
          $backenddata = $("#backenddata"),
          optRegional = $(".shopsettings", $backenddata).data("language"),
          optDateFormat = $(".date-fields", self.$ctx).data("format"),
          $datepickerFrom = $("#filter_datefrom", self.$ctx),
          $datepickerTo = $("#filter_dateto", self.$ctx),
          $duedatepickerFrom = $("#filter_duedatefrom", self.$ctx),
          $duedatepickerTo = $("#filter_duedateto", self.$ctx),
          $datepickerExpiryFrom = $("#filter_expiry_datefrom", self.$ctx),
          $datepickerExpiryTo = $("#filter_expiry_dateto", self.$ctx);

        if (!self.$firstVisit) {

          optDateFormat = optDateFormat
              .replace(/MM/, "mm")
              .replace(/yyyy/, "yy");

          $datepickerFrom.datepicker({
              onSelect: function(selected) {
                  // set dateTo min date with the selected toDate
                  $("#filter_dateto", self.$ctx).datepicker(
                      "option",
                      "minDate",
                      selected
                  );
              }
          });

          $duedatepickerFrom.datepicker({
              onSelect: function(selected) {
                  // set dateTo min date with the selected toDate
                  $("#filter_duedateto", self.$ctx).datepicker(
                      "option",
                      "minDate",
                      selected
                  );
              }
          });

          $datepickerTo.datepicker({
              onSelect: function(selected) {
                  // set dateFrom max date with the selected toDate
                  $("#filter_datefrom", self.$ctx).datepicker(
                      "option",
                      "maxDate",
                      selected
                  );
              }
          });

          $duedatepickerTo.datepicker({
              onSelect: function(selected) {
                  // set dateFrom max date with the selected toDate
                  $("#filter_duedatefrom", self.$ctx).datepicker(
                      "option",
                      "maxDate",
                      selected
                  );
              }
          });

          $datepickerExpiryFrom.datepicker({
              onSelect: function(selected) {
                  // set dateTo min date with the selected toDate
                  $("#filter_expiry_dateto", self.$ctx).datepicker(
                      "option",
                      "minDate",
                      selected
                  );
              }
          });

          $datepickerExpiryTo.datepicker({
              onSelect: function(selected) {
                  // set dateFrom max date with the selected toDate
                  $("#filter_expiry_datefrom", self.$ctx).datepicker(
                      "option",
                      "maxDate",
                      selected
                  );
              }
          });

          if (optRegional == "en") {
              optRegional = "en-GB";
          }

          var dateReDot = new RegExp("\\d?\\d\\.\\d?\\d\\.\\d{4}");
          var dateReSlash = new RegExp("\\d?\\d\\/\\d?\\d\\/\\d{4}");

          // We need to save the value to set it again
          var fromDate = $datepickerFrom.val();
          $datepickerFrom.datepicker(
              "option",
              $.datepicker.regional[optRegional]
          );
          $datepickerFrom.datepicker("option", "dateFormat", optDateFormat);
          $datepickerFrom.datepicker("setDate", self.parseDate(fromDate,dateReDot,dateReSlash));

          var fromDueDate = $duedatepickerFrom.val();
          $duedatepickerFrom.datepicker(
              "option",
              $.datepicker.regional[optRegional]
          );
          $duedatepickerFrom.datepicker(
              "option",
              "dateFormat",
              optDateFormat
          );
          $duedatepickerFrom.datepicker(
              "setDate",
              self.parseDate(fromDueDate,dateReDot,dateReSlash)
          );

          var expiryFromDate = $datepickerExpiryFrom.val();
          $datepickerExpiryFrom.datepicker(
              "option",
              $.datepicker.regional[optRegional]
          );
          $datepickerExpiryFrom.datepicker(
              "option",
              "dateFormat",
              optDateFormat
          );
          $datepickerExpiryFrom.datepicker(
              "setDate",
              self.parseDate(expiryFromDate,dateReDot,dateReSlash)
          );

          // We need to save the value to set it again
          var toDate = $datepickerTo.val();
          $datepickerTo.datepicker(
              "option",
              $.datepicker.regional[optRegional]
          );
          $datepickerTo.datepicker("option", "dateFormat", optDateFormat);
          $datepickerTo.datepicker("setDate", self.parseDate(toDate,dateReDot,dateReSlash));

          var toDueDate = $duedatepickerTo.val();
          $duedatepickerTo.datepicker(
              "option",
              $.datepicker.regional[optRegional]
          );
          $duedatepickerTo.datepicker("option", "dateFormat", optDateFormat);
          $duedatepickerTo.datepicker(
              "setDate",
              self.parseDate(toDueDate,dateReDot,dateReSlash)
          );

          var expiryToDate = $datepickerExpiryTo.val();
          $datepickerExpiryTo.datepicker(
              "option",
              $.datepicker.regional[optRegional]
          );
          $datepickerExpiryTo.datepicker(
              "option",
              "dateFormat",
              optDateFormat
          );
          $datepickerExpiryTo.datepicker(
              "setDate",
              self.parseDate(expiryToDate,dateReDot,dateReSlash)
          );

          self.$firstVisit = true;
        }

    },

    /**
     * Hook function to trigger your events.
     *
     * @method after
     * @return void
     */
    after: function() {
      // Do stuff here or remove after method
      //...
      var $label_filter_reference = $(".label_filter_reference");
      var $label_filter_div = $(".label_filter_div");

      if ($label_filter_reference.text().length > 25) {
        $label_filter_div.addClass("twoLines");
      } else {
        $label_filter_div.addClass("oneLine");
      }
    }
  });
})(Tc.$);
