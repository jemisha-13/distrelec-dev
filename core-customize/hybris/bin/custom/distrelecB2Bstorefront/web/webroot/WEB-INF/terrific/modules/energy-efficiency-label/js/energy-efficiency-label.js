(function($) {

	/**
	 * Module implementation.
	 *
	 * @namespace Tc.Module
	 * @class Energy Efficiency Label
	 * @extends Tc.Module
	 */
	Tc.Module.EnergyEfficiencyLabel = Tc.Module.extend({

		init: function($ctx, sandbox, id) {
			// call base constructor
			this._super($ctx, sandbox, id);
			this.alreadyClicked = false;
		},
		on: function (callback) {
			// calling parent method

			var self = this,
				$ctx = this.$ctx;

			this.$energyLabelItem = $('.energy-label',$ctx);
			this.$energyLabelItem.on('click', 'span', self.onEnergyLabelClick);
			
			callback();

		},

		onEnergyLabelClick: function (ev) {
			var $ctx = this.$ctx,
				self = this;
			 
			ev.preventDefault();
			ev.stopPropagation();
			
			var $a = $(ev.target).closest('span'),
				$label = $a.parent().find('.energy-label-popover'),
				productCode = $a.data('productCode'),
				ffUrl = $a.data('ffUrl'),
				ffChannel = $a.data('ffChannel')
			;

			if (self.alreadyClicked) {
				$label.toggleClass('hidden');
				$('.mod-layout').on('click.eel', function(ev) {
					ev.preventDefault();
					ev.stopPropagation();
					$('.energy-label-popover').addClass('hidden');
					$('.mod-layout').off('click.eel');
				});						
			}

			else {

				$lampLabel = $label.children('.lamp');

				if ($lampLabel.length) {
					var energyClassesLed = $a.data('energyClassesLed'),
						energyClassesBulb = $a.data('energyClassesBulb'),
                        energyClassesIncludedBulb = $a.data('energyClassesIncludedbulb'),
						$textBottom = $label.find('.energy-label-text-bottom'),
                        $textBottomClassText = $label.find('.energy-label-text-bottom .energy-class-text'),
                        $textBottomClassArrow = $label.find('.energy-label-text-bottom .status-arrow'),
						energyClasses = {'a++':0,'a+':0,'a':0,'b':0,'c':0,'d':0,'e':0},	// energyClasses is an array of allowed values. During the population of the label, any class with a LED or Bulb icon sets the value of this array to 1.
                        energyClassesColor = {'a++':'#108e2f','a+':'#50a328','a':'#bccb00','b':'#fae80b','c':'#f5ac00','d':'#e24f13','e':'#d8021b'},
						$energyClassLable = "",
                        $energyLableCrossStr = "",
                        $energyLableCrossArr = [],
                    	$energyLableCross = [],
                        $arrowText = $label.find('.eel-bottom-text--arrow'),
                        $includedBulb = $label.find('.eel-bottom-text--includedbulb'),
                        $ledNotIncludedBulb = $label.find('.eel-bottom-text--lednotincludedbulb');

                    if ( energyClassesIncludedBulb.length === 0 && energyClassesLed.length > 0 ) {
                        $ledNotIncludedBulb.removeClass('hidden');
                    } else if ( energyClassesIncludedBulb.length > 0 ) {
                        $includedBulb.removeClass('hidden');
                        $arrowText.removeClass('hidden');
                    } else {
                        $ledNotIncludedBulb.addClass('hidden');
                        $includedBulb.addClass('hidden');
                        $arrowText.addClass('hidden');
                    }


					if ($textBottom.length && $textBottom.html().length) {
						$textBottom.html($textBottom.html().replace(/<energylabel>(.+)<\/energylabel>/,'<span class="ico-energy $1" title="$1"><i></i></span>'));
					}

					if (energyClassesLed.length) {
						$.each(energyClassesLed.split(';'), function(i, val) {
							valLower = val.toLowerCase().replace('=','');
							energyClasses[valLower]=1;
						});

						var ledValue = energyClassesLed.split(';');
                        var ledTopValue = ledValue[0].toLowerCase().replace('=','');
                        var ledLastValue = (ledValue[ledValue.length - 1]).toLowerCase().replace('=','');
						var ledclass = ledTopValue + "--"  + ledLastValue;


                        $lampLabel.append('<div class="energy-label-led ledclass ' + ledTopValue + ' ' + ledclass + '">' +
							'<div class="bracket-wrapper">\n' +
                            '  	<span class="bracket"></span>\n' +
                            ' 	<span class="bracket-wrapper-text">\n' +
                            '  		<span>L</span>\n' +
                            '  		<span>E</span>\n' +
                            '  		<span>D</span>\n' +
                            ' 	</span>\n' +
                            '</div></div>');

					}

					if (energyClassesBulb.length) {
						$.each(energyClassesBulb.split(';'), function(i, val) {
							valLower = val.toLowerCase().replace('=','');
							energyClasses[valLower]=1;
						});

                        var bulbValue = energyClassesBulb.split(';');
                        var bulbTopValue = bulbValue[0].toLowerCase().replace('=','');
                        var bulbLastValue = (bulbValue[bulbValue.length - 1]).toLowerCase().replace('=','');
                        var bulbclass = bulbTopValue + "--"  +bulbLastValue;


                        $lampLabel.append('<div class="energy-label-bulb bulbclass ' + bulbTopValue + ' ' + bulbclass + '">' +
                            '<div class="bracket-wrapper">\n' +
                            '  <span class="bracket"></span>\n' +
                            '  <span class="bulb-img"></span>\n' +
                            '</div></div>');
					}

                    if ( energyClassesLed.split(';').length < energyClassesBulb.split(';').length ) {
                        $('.energy-label-bulb').addClass('max-left');
					}

                    $.each(energyClasses, function(key, val) { // Check for energy classes not populated with LED or Bulb icons, add Cross icon to those

                        if (val===0) {
                            $energyLableCross.push(key);
                        } else {
                            $energyLableCross.push('$');
                        }

                    });

                    $energyLableCrossStr = $energyLableCross.filter(function(item, pos, arr){
                        return pos === 0 || item !== arr[pos-1];
                    });

                    $energyLableCrossArr = $energyLableCrossStr.toString().split(',$,');

                    $.each($energyLableCrossArr, function(key, val) {

                        val =  val.toString().split(',').join('--').replace("--$", "").replace("$--", "").replace('=','');

                        var crossVal = val.split('--');
                        var crossFirstVal = crossVal[0];
                        var crossLastVal = crossVal[crossVal.length-1];
                        var crossClass = "";

                        crossClass = crossFirstVal + '--' + crossLastVal;

                        if ( crossClass.toString().indexOf('$') <= -1 ) {
                            $lampLabel.append('<div class="energy-label-cross crossclass ' + crossFirstVal + ' ' + crossClass + '">' +
                                '<div class="cross-icon-wrapper"><span class="cross-icon"></span></div>\n' +
                                '</div>');
                        }

                    });

                    $energyClassLable = $('.energy-label-text-bottom .energy-class-arrow-text').html();

                    if ( $energyClassLable !== '' && $energyClassLable !== undefined ) {
                        var colorCodeVar = $energyClassLable.toString().trim().toLowerCase();
                        $('.energy-label-text-bottom .status-arrow').css( 'fill', energyClassesColor[colorCodeVar]);
                    } else {
                        $('.energy-class-arrow').css( 'display', 'none');

                        if ($textBottomClassText.text().trim() !== '' && $textBottomClassText.text().trim().length > 0) {
                            $textBottom.addClass('border');
                        } else {
							$textBottom.removeClass('border');
						}
                    }

                }
				
				$arrow = $label.find('.energy-label-efficiency').find('i');
				$arrow.html($arrow.text().replace(/\+/g, '<sup>+</sup>'));

                if ( $label.hasClass('hidden') ) {
                    $('.energy-label-popover').addClass('hidden');
                    $label.removeClass('hidden');
                } else {
                    $label.addClass('hidden');
                }

				$('.mod-layout').on('click.eel', function(ev) {
					ev.preventDefault();
					ev.stopPropagation();
					$('.energy-label-popover').addClass('hidden');
					$('.mod-layout').off('click.eel');
				});			
				
				// self.alreadyClicked = true;

			}

		}
	});
})(Tc.$);
