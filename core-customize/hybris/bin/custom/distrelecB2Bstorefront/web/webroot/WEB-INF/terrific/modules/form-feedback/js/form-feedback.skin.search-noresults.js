(function ($) {
  Tc.Module.FormFeedback.Noresults = function (parent) {
    this.on = function (callback) {
      $("#tellUsMore, #email, #manufacturerType").on("input", function () {
        var count = this.selectionStart,
          regex = /[<>'"=;()]/,
          value = $(this).val();
        if (regex.test(value)) {
          $(this).val(value.replace(regex, ""));
          count--;
        }
        this.setSelectionRange(count, count);
      });

      $("#tellUsMore").on("input", updateCount);

      function updateCount() {
        var cs = $(this).val().length;
        $("#characters").text(cs);

        $(".charCount").toggleClass(
          "charCountLimit",
          $("#tellUsMore").val().length > 250
        );
      }

      parent.on(callback);
    };
    this.after = function () {};
  };
})(Tc.$);
