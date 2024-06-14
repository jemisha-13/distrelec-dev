(function($) {

	/**
	 * This module implements the accordion content skin download on the product detail page
	 *
	 * @namespace Tc.Module
	 * @class DetailTabsContent
	 * @skin Download
	 * @extends Tc.Module
	 */
	Tc.Module.DetailTabsContent.Download = function (parent) {

		/**
		 * override the appropriate methods from the decorated module (ie. this.get = function()).
		 * the former/original method may be called via parent.<method>()
		 */
		this.on = function (callback, data) {
			// calling parent method
			parent.on(callback);
			
			var $ctx = this.$ctx,
				self = this;
			
			this.$popoverToggle = this.$$('.popover-toggle');
			this.$popoverLanguages = this.$$('.popover-languages');

			this.sandbox.subscribe('anchorLinks', this);
			
			this.$popoverToggle
				.popover({
					placement: 'bottom',
					content: this.$popoverLanguages.html(),
					html: true
				})
				.on('click', function(e) {
					e.preventDefault();
				});

			var productCode = $('.download-data').val();

            $('#cad-files').on('change', function (){
                var currentValue = $(this).find('option:selected').text();
                var formatValue = currentValue.replace(/\s/g, '');
                $('.download-btn').attr('data-file-name', formatValue);
            });

            $.ajax({
                url: productCode+'/downloads',
                type: 'get',
                success: function (data, textStatus, jqXHR) {

                    self.$$('.link-download-pdf').empty();

                    var downloadTemp = "<div class=\"download-element\">" +
                        "<div class=\"download-element__holder\">" +
                        "<a data-aainteraction=\"file download\" class=\"pdp-pdf-btn\" data-file-type=\"{{= it.mimeType }}\" data-file-name=\"{{= it.name }}\" href=\"{{= it.downloadUrl }}\" name=\"{{= it.name }}\">" +
                        "<i class=\"far fa-file-pdf\" aria-hidden=\"true\"></i>" +
                        "<p>\n" +
                        "{{= it.name }}<br />" +
                        "<span>(\n" +
                        "{{= it.mimeType }}\n" +
                        "{{? it.languages.length > 0 }}" +
                        "{{~it.languages :item}}" +
                        "<span class=\"lang\">" +
                        "{{=item.name}}" +
                        "</span>" +
                        "{{~}}" +
                        "{{?}})" +
                        "</span>" +
                        "</p>" +
                        "</a>" +
                        "</div>" +
                        "</div>";

                    var alternativeDownloadTemp = "<div class=\"download-element\">" +
                      "{{? it.alternativeDownloads.length > 0 }}" +
                      "<div class=\"show-more show-more-downloads\">" +
                      "<div class=\"show-more__holder\">" +
                      "<a href=\"/\" class=\"show-less-link popover-toggle \"><span><spring:message code=\"product.tabs.download.less.lang\" /></span><i class=\"fa fa-angle-up\" aria-hidden=\"true\"></i></a>" +
                      "<a href=\"/\" class=\"show-more-link popover-toggle\"><span><spring:message code=\"product.tabs.download.more.lang\" /></span><i class=\"fa fa-angle-down\" aria-hidden=\"true\"></i></a>" +
                      "</div>\n" +
                      "<div class=\"popover-languages hidden\">" +
                      "<ul>\n" +
                      "{{~it.alternativeDownloads :item}}" +
                      "<li>" +
                      "<a data-aainteraction=\"file download\" class=\"pdp-pdf-btn\" data-file-type=\"{{= item.mimeType }}\" data-file-name=\"{{= item.name }}\" href=\"{{=item.downloadUrl}}\" name=\"{{=item.name}}\">\n" +
                      "<i class=\"far fa-file-pdf\" aria-hidden=\"true\"></i>" +
                      "<p>" +
                      "{{=item.name }}<br />" +
                      "<span>(" +
                      "{{=item.mimeType }},&nbsp;&nbsp;" +
                      "{{=item.languages[0].name }}" +
                      ")</span>" +
                      "</p>" +
                      "</a>" +
                      "</li>" +
                      "{{~}}" +
                      "</ul>" +
                      "</div>" +
                      "</div>" +
                      "{{?}}" +
                      "</div>";

                    var templateFunction = doT.template(downloadTemp);
                    var alternativeTemplateFunction = doT.template(alternativeDownloadTemp);

                    $.each(data, function(index, item) {

                        var self2 = this;
                        self2.code = item.code;

                        $.each(item.downloads, function(index, item) {
                            var html = templateFunction( item );

                            if (self2.code === 'datasheets'){
                                $('.datasheets-container--data').removeClass('hidden');
                                self.$$('.datasheets-section', self.$ctx).append(html);
                            }
                            if (self2.code === 'manuals'){
                                $('.datasheets-container--manuals').removeClass('hidden');
                                self.$$('.manuals-section', self.$ctx).append(html);
                            }
                            if (self2.code === 'brochures'){
                                $('.datasheets-container--brochures').removeClass('hidden');
                                self.$$('.brochure-section', self.$ctx).append(html);
                            }
                            if (self2.code === 'certificates'){
                                $('.datasheets-container--certificates').removeClass('hidden');
                                self.$$('.certificate-section', self.$ctx).append(html);
                            }
                            if (self2.code === 'software'){
                                $('.datasheets-container--software').removeClass('hidden');
                                self.$$('.software-section', self.$ctx).append(html);
                            }
                            if (self2.code === 'templates'){
                                $('.datasheets-container--templates').removeClass('hidden');
                                self.$$('.template-section', self.$ctx).append(html);
                            }

                        });

                        var html = alternativeTemplateFunction( self2 );

                        if (self2.code === 'datasheets'){
                            $('.datasheets-container--data').removeClass('hidden');
                            self.$$('.datasheets-section', self.$ctx).append(html);
                        }
                        if (self2.code === 'manuals'){
                            $('.datasheets-container--manuals').removeClass('hidden');
                            self.$$('.manuals-section', self.$ctx).append(html);
                        }
                        if (self2.code === 'brochures'){
                            $('.datasheets-container--brochures').removeClass('hidden');
                            self.$$('.brochure-section', self.$ctx).append(html);
                        }
                        if (self2.code === 'certificates'){
                            $('.datasheets-container--certificates').removeClass('hidden');
                            self.$$('.certificate-section', self.$ctx).append(html);
                        }
                        if (self2.code === 'software'){
                            $('.datasheets-container--software').removeClass('hidden');
                            self.$$('.software-section', self.$ctx).append(html);
                        }
                        if (self2.code === 'templates'){
                            $('.datasheets-container--templates').removeClass('hidden');
                            self.$$('.template-section', self.$ctx).append(html);
                        }


                    });

                    $('.loading-downloads').addClass('hidden');

                    $('.popover-toggle').on('click', function(e) {
                        e.preventDefault();
                        $(e.currentTarget).parent().toggleClass('showless');
                        $(e.currentTarget).parent().next().toggleClass('hidden');
                    });

                    $( document ).trigger( "downloadsReady" );


                },
                error: function (jqXHR, textStatus, errorThrown) {
                }
            });

            setTimeout(function(){

                $('.pdp-pdf-btn').on('click', function (e){
                    e.preventDefault();

                    var windowReference = window.open();
                    windowReference.location = $(this).attr('href');
                    windowReference.target = '_blank';
                });

            }, 400);

			
			// document delegate click handler: close popover unless click target matches (#DISTRELEC-1804)
			$(document).on('click.accordiondownload', function(ev) {
				if (!$(self.$popoverToggle).is(ev.target) && $(self.$popoverToggle).has(ev.target).length === 0 && $('.popover').has(ev.target).length === 0) {
					$(self.$popoverToggle).popover('hide');

					return;
				}
			});

			callback();// ?
		};
		
		/**
		 * Hook function to trigger your events.
		 *
		 * @method after
		 * @return void
		 */
		this.after = function () {
			// calling parent method
			parent.after();
		};

	};

})(Tc.$);
