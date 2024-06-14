
#
# List of all used CSS / JS Libraries and Plugins
#

!! Add every Library, Plugin etc. you use to this list


JS Libraries, Polyfills and Helpers
--------------------------------------------------------


* **jQuery - 1.9.1**
  Info:
  File: via Google CDN //ajax.googleapis.com/ajax/libs/jquery/1.9.1/jquery.min.js

* **TerrificJS - 2.0.1**
  Allows you to modularize your jQuery code by solely relying on CSS naming conventions.
  Info : https://github.com/brunschgi/terrificjs/
  File : web/webroot/WEB-INF/terrific/js/static/libraries/10-terrific-2.0.1.min.js
  Incl.: Terrific Parser

* **doT.js - 1.0.0**
  Javascript template engine for nodejs and browsers.
  Info: https://github.com/olado/doT
  File: web/webroot/WEB-INF/terrific/js/static/libraries/20-doT-1.0.0.min.js
  Incl.: Terrific Parser

* **HTML5Shiv - 3.7.4**
  Way to enable use of HTML5 sectioning elements in legacy Internet Explorer.
  Info : https://github.com/aFarkas/html5shiv/
         https://code.google.com/p/html5shiv/source/browse/trunk/html5.js
  File : web/webroot/_ui/all/js/html5shiv-3.6.2pre.js
  Incl.: html>head><!--[if lte IE 9]>...<![endif]-->

* **jQuery UI**
  Info : http://jqueryui.com
  Build: 1.10.3
  File:  30-jquery-ui-1.10.3.custom.js
  Incl.: jquery.ui.core.js, jquery.ui.widget.js, jquery.ui.mouse.js

* **Modernizer**
  Library that detects HTML5 and CSS3 features in the user’s browser.
  Info : http://modernizr.com/
  Build: http://modernizr.com/download/#-touch-cssclasses-teststyles-prefixes-load
  File : web/webroot/WEB-INF/terrific/js/static-head/modernizr-custom.js
  Incl.: html>head>
  Options:
  - Misc: Touch Events
  - Extra: Modernizr.load / yepnope
  - Extra: Add CSS Classes



jQuery Plugins
--------------------------------------------------------

* **jQuery Waypoints - v2.0.2**
  makes it easy to execute a function whenever you scroll to an element.
  Info: [http://imakewebthings.com/jquery-waypoints/]
  File: web/webroot/WEB-INF/terrific/js/static/plugins/020-waypoints-2.0.2.min.js

* **jQuery Waypoints: Sticky Extension - v2.0.2**
  used to make elements "stick" to the top of the page when the user scrolls past
  Info: [http://imakewebthings.com/jquery-waypoints/shortcuts/sticky-elements/]
  File: web/webroot/WEB-INF/terrific/js/static/plugins/021-waypoints-sticky-2.0.2.min.js

* **Twitter Bootstrap: Modals - v2.3.1**
  used to make lightboxes
  Info: [http://twitter.github.io/bootstrap/javascript.html#modals]
  File: web/webroot/WEB-INF/terrific/js/static/plugins/bootstrap-modal.js

* **Twitter Bootstrap: Tabs - v2.3.1**
  used to make tabbed content
  Info: [http://twitter.github.io/bootstrap/javascript.html#tabs]
  File: web/webroot/WEB-INF/terrific/js/static/plugins/bootstrap-tabs.js
  Dependencies: /web/webroot/WEB-INF/terrific/css/static/plugins/00-bootstrap-common.css
  CSS is stripped to essentials and customized

* **Twitter Bootstrap: Popover - v2.3.1**
  used to make rich tooltips
  Info: [http://twitter.github.io/bootstrap/javascript.html#popover]
  File: web/webroot/WEB-INF/terrific/js/static/plugins/bootstrap-modal.js
  Dependencies: /web/webroot/WEB-INF/terrific/css/static/plugins/00-bootstrap-common.css

* **Twitter Bootstrap: Typeahead - v2.3.1**
  used to implment autosuggest behavior for the global search input
  Info: [http://twitter.github.io/bootstrap/javascript.html#typeahead]
  File: web/webroot/WEB-INF/terrific/js/static/plugins/bootstrap-typeahead-custom.js
  The plugin itself is modified for enhanced functionality

* **jQuery selectBoxIt**
  used to make stylable select boxes
  Info: [https://github.com/silviomoreto/bootstrap-select]
  File: web/webroot/WEB-INF/terrific/js/static/plugins/jquery.selectBoxIt.js
  Dependencies: jQuery UI: Core.Widget

* **jQuery++ Cookie**
  used to set and get cookies
  Info: http://jquerypp.com/#cookie
  File: web/webroot/WEB-INF/terrific

* **jquery.carouFredSel-6.2.1**
  Used for all Carousels for Teasers and Product Gallery
  Info: https://github.com/gilbitron/carouFredSel
  File: /web/webroot/WEB-INF/terrific/js/static/plugins/jquery.carouFredSel-6.2.1.js

* **jquery.editable-1.3.3**
  Used for Inline editing shopping list names
  Info: http://web.archive.org/web/20120707085904/http://www.arashkarimzadeh.com/jquery/7-editable-jquery-plugin.html
  File: /web/webroot/WEB-INF/terrific/js/static/plugins/jquery.editable-1.3.3-custom.js


CSS Libraries and Helpers
--------------------------------------------------------

* **Normalize.css - 2.1.0**
  makes browsers render all elements more consistently and in line with modern standards.
  Info : http://necolas.github.com/normalize.css/
  File : web/webroot/WEB-INF/terrific/css/static/libraries/normalize-2.1.0.css
  Incl.: Terrific Parser
