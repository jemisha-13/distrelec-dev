
#
# Frontend Architecture Overview
#


How To Documentations
--------------------------------------------------------

See Frontend Wiki Page: https://wiki.namics.com/display/distrelint/500+Frontend


General
--------------------------------------------------------

* box-sizing: border-box is default for all elements
* .no-js class is handled via Modernizer
* Sprites are generated with Grunt Glue Task


Terrific: Where to find What
--------------------------------------------------------

* Modules:
  web/webroot/WEB-INF/terrific/modules

* Modules Tag:
  web/webroot/WEB-INF/tags/terrific/modules

* Layouts:
  web/webroot/WEB-INF/views/desktop/pages/terrific

* Layout Templates:
  web/webroot/WEB-INF/tags/terrific/views

* Elements (CSS):
  web/webroot/WEB-INF/terrific/css/static/elements

* CSS Libraries Static:
  web/webroot/WEB-INF/terrific/css/static/libraries

* JS Static - minified through Terrific
  web/webroot/WEB-INF/terrific/js/static/libraries
  web/webroot/WEB-INF/terrific/js/static/helper <-- Your project wide helper functions
  web/webroot/WEB-INF/terrific/js/static/plugins
  web/webroot/WEB-INF/terrific/js/static/plugins-src  <-- Our own Plugins or customized Plugins
  web/webroot/WEB-INF/terrific/js/static/connectors

* JS Libraries Static - not minified through Terrific
  web/webroot/_ui/all/js

* Media/Images:
  web/webroot/_ui/all/media

* Generated Files - Grunt:
  http://distrelec.local:9001/_ui/all/cache/base.css
  --> web/webroot/_ui/all/cache/base.css
  http://distrelec.local:9001/_ui/all/cache/base.js
  --> web/webroot/_ui/all/cache/base.js


Grid
--------------------------------------------------------

960px grid system
12 Columns ~ Margin left: 0px
Based on the simple grid system - http://www.webnicer.com/ by Jacek Ciolek


Fonts
--------------------------------------------------------

Helvetica, Arial
