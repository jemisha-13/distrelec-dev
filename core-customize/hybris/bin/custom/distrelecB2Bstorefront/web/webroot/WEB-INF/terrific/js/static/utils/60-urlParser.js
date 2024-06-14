/*
 *
 * split and join url in its parts to change for example get parameter
 *
 * */

Tc.Utils.splitUrl = function(url) {
  //remove the parametrs from the category search url which are not required.

  var urlSpl = url.split("?"),
    urlGet,
    urlObj = {};

  urlObj.base = urlSpl[0];

    if (urlSpl.length > 1) {
      urlObj.get = {};
      var regex = /[A-Z]&[A-Z\bC\+\+(?!\w)]\+?/g;
      if (urlSpl[1].match(regex) !== null) {
        var replacer = urlSpl[1].match(regex)[0];
        var replacerFix = replacer.replace("&", "%26+");
        urlGet = urlSpl[1].replace(replacer, replacerFix);

        urlGet = urlSpl[1].replace(/(?:\+\&\+)/, "+%26+").split("&");

      } else {
        urlGet = urlSpl[1].replace(/(?:\+\&\+)/, "+%26+").split("&");
      }

    // iterate over each get param
    $.each(urlGet, function() {
      var part = this.toString().split("=");

      // Save a single key = value
      if (urlObj.get[part[0]] == undefined) {
        urlObj.get[part[0]] = part[1];

        // There is already a key with this name
        // use case: fact finder filter with multiple manufacturers
      } else {
        // Change value to array
        if (typeof urlObj.get[part[0]] !== "object") {
          var existingValue = urlObj.get[part[0]];
          urlObj.get[part[0]] = [];
          urlObj.get[part[0]].push(existingValue);
        }

        // Add new value to array
        urlObj.get[part[0]].push(part[1]);
      }
    });
  }

  return urlObj;
};

Tc.Utils.joinUrl = function(urlObj) {
  var url = urlObj.base,
    key,
    _get = [];

  if (urlObj.get !== undefined) {
    for (key in urlObj.get) {
      var value = urlObj.get[key];
      if (value !== undefined) {
        if (typeof value !== "object") {
          // Single Key = value pari
          _get.push(key + "=" + value);
        } else {
          // Key with multiple values
          $.each(value, function(index, element) {
            _get.push(key + "=" + element);
          });
        }
      } else {
        _get.push(key);
      }
    }

    url += "?" + _get.join("&");
  }

  return url;
};

$(".mat-button").click(function() {
  var url = $(this).attr("data-url");

  if (url) {
    window.location.href = url;
  }
});
