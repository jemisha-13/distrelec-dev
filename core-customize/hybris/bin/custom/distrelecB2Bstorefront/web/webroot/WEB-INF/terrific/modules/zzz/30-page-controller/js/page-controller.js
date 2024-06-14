(function($) {

	/**
	 * Module implementation.
	 *
	 * @namespace Tc.Module
	 * @class PageController
	 * @extends Tc.Module
	 */
    var localeCountry = "",
        localeLanguage = '';
	Tc.Module.PageController = Tc.Module.extend({

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


        importToolBackScreenMessage: function () {

            $('[class*="locale-"]').each(function() {
                var locale = $(this).attr('class').split('locale-').pop().split('-');
                localeCountry = locale[0];
                localeLanguage = locale[1];
            });

            switch (localeLanguage) {
                case 'en':
                    message = "If you use the browser back button, your import will error. Please click cancel on this window and use the back button at the bottom of the page instead.";
                    break;
                case 'cs':
                    message = "Pokud v prohlížeči použijete tlačítko Zpět, při importu dojde k chybě. Místo toho klikněte na tlačítko Storno v tomto okně a vraťte se pomocí tlačítka Zpět ve spodní části stránky.";
                    break;
                case 'da':
                    message = "Hvis du bruger browserens Tilbage-knap, vil importen slå fejl. Klik i stedet på Annuller i dette vindue, og tryk på Tilbage-knappen nederst på siden.";
                    break;
                case 'de':
                    message = "Wenn Sie die Browser-Zurück-Funktion verwenden, wird Ihr Import fehlschlagen. Wählen Sie daher in diesem Fenster bitte „Abbrechen“ aus und verwenden Sie stattdessen die Schaltfläche „Zurück“ unten auf dieser Seite.";
                    break;
                case 'et':
                    message = "Kui kasutate veebilehitseja tagasi liikumise nuppu, saate importimisel veateate. Klõpsake selles aknas tühistamise nuppu ja kasutage hoopis lehe allosas asuvat tagasi liikumise nuppu.";
                    break;
                case 'fi':
                    message = "Jos käytät Selaa taaksepäin -painiketta, tuonti aiheuttaa virheen. Napsauta tässä ikkunassa Peruuta-painiketta ja käytä sivun alalaidassa olevaa taaksepäin siirtymisen painiketta.";
                    break;
                case 'fr':
                    message = "Si vous utilisez le bouton de retour en arrière du navigateur, votre importation échouera. Cliquez sur Annuler dans cette fenêtre et utilisez plutôt le bouton de retour en arrière en bas de la page.";
                    break;
                case 'hu':
                    message = "Ha a böngésző vissza gombjára kattint, az importálás hibás lesz. Ehelyett kattintson ebben az ablakban a mégse elemre, majd az oldal alján a vissza gombra.";
                    break;
                case 'it':
                    message = "Se utilizzi il pulsante Indietro del tuo browser, la procedura di importazione verrà annullata. Fai clic su Annulla in questa finestra e utilizza il pulsante Indietro in fondo alla pagina.";
                    break;
                case 'lt':
                    message = "Jai naudosite naršyklės mygtuką „Atgal“, bus rodomas importavimo klaidos pranešimas. Vietoj to spustelėkite šiame lange mygtuką „Atšaukti“, tuomet paspauskite mygtuką „Atgal“, esantį puslapio ";
                    break;
                case 'lv':
                    message = "Ja tiek izmantota pārlūka poga Atpakaļ, importam rodas kļūda. Lūdzu, noklikšķiniet uz Atcelt šajā logā un tā vietā izmantojiet pogu Atpakaļ lapas apakšā.";
                    break;
                case 'nl':
                    message = "Als u de terugknop van uw browser gebruikt, mislukt het importeren. Klik op Annuleren in dit venster en gebruik de terugknop onderaan de pagina.";
                    break;
                case 'no':
                    message = "Hvis du bruker nettleserens Tilbake-knapp, vil importen slå feil. Klikk Avbryt i dette vinduet, og bruk Tilbake-knappen nederst på siden isteden.";
                    break;
                case 'pl':
                    message = "Użycie przycisku Wstecz przeglądarki spowoduje błąd importu. Kliknij przycisk Anuluj w tym oknie i zamiast tego użyj przycisku Wstecz na dole strony.";
                    break;
                case 'ro':
                    message = "Dacă utilizaţi butonul Înapoi din browser, importul va eşua. Clic pe Anulare din această fereastră şi utilizaţi butonul Înapoi din partea de jos a paginii.";
                    break;
                case 'sk':
                    message = "Ak použijete tlačidlo pre návrat v prehliadači, import bude vykazovať chybu. V tomto okne kliknite na možnosť zrušiť a namiesto toho použite tlačidlo pre návrat v dolnej časti strany.";
                    break;
                case 'sv':
                    message = "Om du använder bakåtknappen i webbläsaren kommer importen att bli fel. Klicka på Avbryt i fönstret och använd bakåtknappen längst ned på sidan istället.";
                    break;
                default:
                    message = "If you use the browser back button, your import will error. Please click cancel on this window and use the back button at the bottom of the page instead.";
            }
            return message;
        },

		init: function($ctx, sandbox, id) {
			// call base constructor
			this._super($ctx, sandbox, id);

			// Subscribe to channels
			this.sandbox.subscribe('windowHistoryEvents', this);

			this.onAddWindowPopstateEvent = $.proxy(this, 'onAddWindowPopstateEvent');
			this.metahdSearchFocus = $.proxy(this, 'metahdSearchFocus');

			this.initPopState = false;

			if (window.location.href.indexOf("bom-tool/review-file") >= 0) {
				window.history.pushState(null, "", window.location.href);

				if (window.history && window.history.pushState) {
					var str = this.importToolBackScreenMessage();

					$(window).on('popstate', function() {
						var cmt = confirm( str );
						if (cmt === true) {
							window.history.back();
						} else {
							window.history.pushState(null, "", window.location.href);
						}
					});
				}
			}
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

			this.bindButtonStates();

			$('.modal')
				.off('shown').on('shown', function(ev){
					$('body').addClass('no-scroll');
				})
				.off('hidden').on('hidden', function(ev){
					$('body').removeClass('no-scroll');
				});

			callback();
		},

		/**
		 * Hook function to trigger your events.
		 *
		 * @method after
		 * @return void
		 */
		after: function() {
			var self = this
				;

			// only add focues listener for none touch devices and on homepage
			if (!Modernizr.touch && $('body').hasClass('skin-layout-home')) {
			// Workaround to receive real focus on searchbar
				setTimeout(function () {
					self.metahdSearchFocus();
				}, 0);
			}

		},

		bindButtonStates: function() {
			var self = this
				;

			// Globally: Buttons
			$(document.body).on(
				{
					'click.obj-btn': function (event) {
						 // if buttons disabled
						if (false === self.isButtonEnabled($(this))) {
							event.preventDefault();
							return false;
						}
					}
				}
				,'.btn'
			);
		},

		/**
		 * Checkes if element is enabled
		 *
		 * Disabled if:
		 * - has class 'disabled'
		 * - or is disabled by dom
		 *
		 * @param $elem jQuery Object
		 * return {boolean} true if element is enabled
		 */
		isButtonEnabled: function($elem) {
			var c = true;

			if ($elem.hasClass('disabled')) {
				c = false;
			} else if ($elem.attr('disabled')) {
				c = false;
			}

			return c;
		},

		onAddWindowPopstateEvent: function() {
			var self = this;

			if (self.initPopState === false) {
				self.initPopState = true;
				
				// tag the page which was loaded on page load, by modifying state of history entry (isInitialPageLoad)
				//window.history.replaceState({isInitialPageLoad : true }, null, document.location);

				if (!$('html').hasClass('lt-ie10') && !Modernizr.iseproc) {
					window.addEventListener('popstate', function(event) {
						if(event.state !== null && event.state.isAjaxFacetHistoryEntry) {
							self.fire('facetsWindowsHistoryPopStateEvent', event, ['windowHistoryEvents']);
						} else if (event.state !== null && event.state.isAjaxPaginationHistoryEntry) {
							self.fire('paginationWindowsHistoryPopStateEvent', event, ['windowHistoryEvents']);
						} else if (event.state.isInitialPageLoad !== undefined) {
							location.href = document.location;
						}
					}, false);
				}
			}
		},
		
		
		/**
		 * Focus Search(bar) on Home
		 */
		metahdSearchFocus: function () {
			var $field = $("#metahd-search", this.$ctx)
				, $mod = $field.closest('.mod-metahd-search')
				;

			$field
				// Bind keypress (unbind after first keypress...)
				.on('keypress.home', function (event) {
					// Unbind keypress: We need only 1st keypress
					$field.unbind('keypress.home');

					// Reset Home Styling
					$mod.addClass('active');
				})

				// Focus to Field
				.focus()
			;
		}

});

})(Tc.$);
