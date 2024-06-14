(function ($) {

	/**
	 * Module implementation.
	 *
	 * @namespace Tc.Module
	 * @class Mainnav
	 * @extends Tc.Module
	 */
	Tc.Module.MetadataContent = Tc.Module.extend({

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
		init: function ($ctx, sandbox, id) {
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
		on: function (callback) {

			$(document).ready(function() {
				var showCharMin = 460,
					showCharMax = 530,
					ellipsistext = "...",
					moretext = $('.content-text').data('showmore'),
					lesstext = $('.content-text').data('showless');
				
				$('.more').each(function() {
					var contentHtml = $(this).html(),
						contentText = $(this).text();
  
					if(contentText.length > showCharMax) {
						
						// Truncate text at last full stop or space between position showCharMin and showCharMax, if any. Otherwise at showCharMax. Make sure not to truncate a html tag.

						var textStart = contentText.substring(0, showCharMax),
							htmlStart = contentHtml.substring(0, showCharMax),
							i = textStart.lastIndexOf('.'),
							n = textStart.lastIndexOf(' '),
							insertionPoint = i < showCharMin ? (n < showCharMin ? showCharMax : n) : i,
							offset = 0;

						textStart = contentText.substring(0, insertionPoint);
						htmlStart = contentHtml.substring(0, insertionPoint);
						if (textStart !== htmlStart) { // Uh-u, html tags in text. Move insertion point forward the same number of positions as the size of the html tags.
							var strippedHtml = $(this).html(),
								j = strippedHtml.indexOf('<'),
								k,
								isA = false;
							while (j !== -1 && j < insertionPoint) { // Start of tag found before insertion point
								isA = (strippedHtml.charAt(j+1)==='a');
								k = strippedHtml.indexOf('>')+1;
								offset += k-j;
								strippedHtml = strippedHtml.substring(0,j) + strippedHtml.substring(k);
								j = strippedHtml.indexOf('<');
								if (isA && insertionPoint < strippedHtml.indexOf('a>')+2) {  // Insertion point in link, move forward and exit
									offset += strippedHtml.indexOf('a>') - insertionPoint + 2;
									j = -1;
								}
							}
							htmlStart = contentHtml.substr(0, insertionPoint + offset);
						}

						htmlEnd = contentHtml.substr(insertionPoint + offset);

						var html = htmlStart + '<em class="morecontent hidden">' + htmlEnd +	'&nbsp;&nbsp;<a href="#" class="morelink">' + lesstext + '</a></em>';
						html += '<em class="moreellipsis">' + ellipsistext + '&nbsp;&nbsp;<a href="#" class="morelink">' + moretext + '</a></em>';

						$(this).html(html);
					}

				});  

				$(".morelink").click(function(ev){
					ev.preventDefault();
					$(this).parent().toggleClass('hidden').siblings('.morecontent, .moreellipsis').toggleClass('hidden');
				});
			});
			
			callback();
		},
		
		
		after: function() {

		}
		
		
	});

})(Tc.$);
