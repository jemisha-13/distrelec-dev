!function(t){"use strict";var a=function(a){this.element=t(a)};a.prototype={constructor:a,show:function(){var a,e,n,i=this.element,s=i.closest("ul:not(.dropdown-menu)"),o=i.attr("data-target");o||(o=(o=i.attr("href"))&&o.replace(/.*(?=#[^\s]*$)/,"")),i.parent("li").hasClass("active")||(a=s.find(".active:last a")[0],n=t.Event("show",{relatedTarget:a}),i.trigger(n),n.isDefaultPrevented()||(e=t(o),this.activate(i.parent("li"),s),this.activate(e,e.parent(),function(){i.trigger({type:"shown",relatedTarget:a})})))},activate:function(a,e,n){var i=e.find("> .active"),s=n&&t.support.transition&&i.hasClass("fade");function o(){i.removeClass("active").find("> .dropdown-menu > .active").removeClass("active"),a.addClass("active"),s?(a[0].offsetWidth,a.addClass("in")):a.removeClass("fade"),a.parent(".dropdown-menu")&&a.closest("li.dropdown").addClass("active"),n&&n()}s?i.one(t.support.transition.end,o):o(),i.removeClass("in")}};var e=t.fn.tab;t.fn.tab=function(e){return this.each(function(){var n=t(this),i=n.data("tab");i||n.data("tab",i=new a(this)),"string"==typeof e&&i[e]()})},t.fn.tab.Constructor=a,t.fn.tab.noConflict=function(){return t.fn.tab=e,this},t(document).on("click.tab.data-api",'[data-toggle="tab"], [data-toggle="pill"]',function(a){a.preventDefault(),t(this).tab("show")})}(window.jQuery);