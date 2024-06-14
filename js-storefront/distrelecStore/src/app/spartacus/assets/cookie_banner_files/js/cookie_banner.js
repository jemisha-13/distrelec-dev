//Stop serverComponent firing the TMS portion of Ensighten
Bootstrapper.getServerComponent = function () {};

var googleConsentOptions = {};
//googleConsentOptions.id = document.querySelector('html').dataset.gtmId; //examples: AW-123456, DC-123456, G-123456-7, GTM-ABCDEF, G-ABCDEF123
googleConsentOptions.id = 'UA-57933327-6';
googleConsentOptions.advertising_storage_category = 'Marketing';
googleConsentOptions.analytics_storage_category = 'Statistics'; //name of Privacy category to tie to Google's analytics storage
googleConsentOptions.enhanced_storage_category = 'Preferences';

/******* DO NOT EDIT BELOW *******/
var dataLayer = googleConsentOptions.dataLayer || 'dataLayer';
window[dataLayer] = window[dataLayer] || [];
var gtag = function () {
  window[dataLayer].push(arguments);
};

/******** START OF BOOTSTRAPPER FUNCTIONS & EVENT LISTNENERS *********/

Bootstrapper.privacy = Bootstrapper.privacy || {};

// DISTRELEC-28933 - Cookie reconsent version addition - START

Bootstrapper.privacy.version = '1.0';
Bootstrapper.privacy.onAfterInit = function () {
  var cookie_name = 'version',
    existing_version = Bootstrapper.gateway.getCookie(cookie_name);
  if (existing_version && Bootstrapper.privacy.version !== existing_version) {
    var privacy_cookies = Bootstrapper.gateway.getCookieTypes().concat('BANNER_VIEWED', 'MODAL_VIEWED'),
      cookie_domain = window.gateway.consentCookies.getCookieDomain();
    for (var i = 0; i < privacy_cookies.length; i++) {
      // remove cookies
      document.cookie =
        Bootstrapper.ensightenOptions.client.toUpperCase() +
        '_ENSIGHTEN_PRIVACY_' +
        privacy_cookies[i].replace(/ /g, '_') +
        '="";expires=' +
        new Date(0).toUTCString() +
        ';path=/;domain=' +
        cookie_domain;
    }
    Bootstrapper.gateway.setCookie(cookie_name, Bootstrapper.privacy.version);
    Bootstrapper.bindDOMParsed(function () {
      //Bootstrapper.gateway.openBanner();
    });
  } else if (!existing_version) {
    Bootstrapper.gateway.setCookie(cookie_name, Bootstrapper.privacy.version);
  }
};

// DISTRELEC-28933 - Cookie reconsent version addition - END

var onBeforeInit_old = Bootstrapper.privacy.onBeforeInit,
  onAfterInit_old = Bootstrapper.privacy.onAfterInit;

Bootstrapper.privacy.onBeforeInit = function (ensClientConfig) {
  if (onBeforeInit_old) {
    onBeforeInit_old.apply(this, arguments);
  }

  var onConsent_old = ensClientConfig.onConsent;
  ensClientConfig.onConsent = function (key, value) {
    if (onConsent_old) {
      onConsent_old.apply(this, arguments);
    }

    var storageValue = value === '1' ? 'granted' : 'denied';
    /* START CUSTOM LOGIC */
    if (key === googleConsentOptions.analytics_storage_category) {
      var googleBlocked = isGoogleBlocked();
      var googleAllowed = isGoogleAllowed();

      if ((value === '1' && googleBlocked === false) || googleAllowed) {
        storageValue = 'granted';
      } else {
        storageValue = 'denied';
      }
    }
    /* END CUSTOM LOGIC */
    switch (key) {
      case googleConsentOptions.advertising_storage_category:
        gtag('consent', 'update', {
          ad_storage: storageValue,
        });
        break;
      case googleConsentOptions.analytics_storage_category:
        gtag('consent', 'update', {
          analytics_storage: storageValue,
        });
        break;
    }
  };
};

Bootstrapper.privacy.services = {
  settings: {
    always_allowed: {
      EN: 'Always active',
      CS: 'Vždy aktivní',
      DK: 'Altid aktiv',
      DE: 'Immer aktiv',
      EE: 'Alati aktiivne',
      FI: 'Aina käytössä',
      FR: 'Toujours actifs',
      HU: 'Mindig aktív',
      IT: 'Sempre attivi',
      LT: 'Visada aktyvūs',
      LV: 'Vienmēr ieslēgtas',
      NL: 'Altijd actief',
      NO: 'Alltid aktiv',
      PL: 'Zawsze aktywne',
      RO: 'Activ întotdeauna',
      SK: 'Vždy aktívne',
      SV: 'Alltid aktiv',
      default: 'Always active',
    },
    expand_services: [
      {
        EN: 'Show list of essential cookies',
        CS: 'Zobrazit seznam základních souborů cookie',
        DK: 'Vis liste over nødvendige cookies',
        DE: 'Unbedingt erforderliche Cookies anzeigen',
        EE: 'Kuva oluliste küpsiste loend',
        FI: 'Näytä välttämättömien evästeiden luettelo',
        FR: 'Afficher la liste des cookies essentiels',
        HU: 'Az alapvető cookie-k listájának megjelenítése',
        IT: "Mostrare l'elenco dei cookie essenziali",
        LT: 'Rodyti būtinųjų slapukų sąrašą',
        LV: 'Parādīt noteikti nepieciešamo sīkdatņu sarakstu',
        NL: 'Lijst met essentiële cookies weergeven',
        NO: 'Vis liste over grunnleggende informasjonskapsler',
        PL: 'Pokaż listę niezbędnych plików cookie',
        RO: 'Afişează lista cu modulele cookie esenţiale',
        SK: 'Zobraziť zoznam nevyhnutných súborov cookie',
        SV: 'Visa lista över nödvändiga cookies',
        default: 'Show list of essential cookies',
      },
      {
        EN: 'Show list of enhanced functionality cookies',
        CS: 'Zobrazit seznam souborů cookie s rozšířenými funkcemi',
        DK: 'Vis liste over cookies, der giver forbedret funktionalitet',
        DE: 'Funktionelle Cookies anzeigen',
        EE: 'Kuva täiustatud funktsionaalsusega küpsiste loend',
        FI: 'Näytä lisätoimintoevästeiden luettelo',
        FR: 'Afficher la liste des cookies de fonctionnalité',
        HU: 'A bővített funkcionalitású cookie-k listájának megjelenítése',
        IT: "Mostrare l'elenco dei cookie di funzionalità avanzata",
        LT: 'Rodyti išplėsto funkcionalumo slapukų sąrašą',
        LV: 'Parādīt uzlabotas funkcionalitātes sīkdatņu sarakstu',
        NL: 'Lijst met cookies voor een betere functionaliteit weergeven',
        NO: 'Vis liste over informasjonskapsler for forbedret funksjonalitet',
        PL: 'Pokaż listę plików cookie zwiększających funkcjonalność',
        RO: 'Afişează lista cu module cookie pentru funcţionalitate îmbunătăţită',
        SK: 'Zobraziť zoznam súborov cookie na zlepšenie funkčnosti',
        SV: 'Visa lista över cookies för utökad funktionalitet',
        default: 'Show list of enhanced functionality cookies',
      },
      {
        EN: 'Show list of measurement and optimisation cookies',
        CS: 'Zobrazit seznam souborů cookie pro měření a optimalizaci',
        DK: 'Vis liste over måling- og optimeringscookies',
        DE: 'Performance-Cookies anzeigen',
        EE: 'Kuva mõõtmis- ja optimeerimisküpsiste loend',
        FI: 'Näytä mittaus- ja optimointievästeiden luettelo',
        FR: "Afficher la liste des cookies de mesure et d'optimisation",
        HU: 'Mérési és optimalizálási cookie-k listájának megjelenítése',
        IT: "Mostrare l'elenco dei cookie di misurazione e ottimizzazione",
        LT: 'Rodyti matavimo ir optimizavimo slapukų sąrašą',
        LV: 'Parādīt mērījumu un optimizācijas sīkdatņu sarakstu',
        NL: 'Lijst met analytische en optimalisatiecookies weergeven',
        NO: 'Vis liste over informasjonskapsler for måling og optimalisering',
        PL: 'Pokaż listę plików cookie służących do pomiarów i optymalizacji',
        RO: 'Afişează lista cu modulele cookie de măsurători şi optimizare',
        SK: 'Zobraziť zoznam súborov cookie na meranie a optimalizáciu',
        SV: 'Visa lista över mät- och optimeringscookies',
        default: 'Show list of measurement and optimisation cookies',
      },
      {
        EN: 'Show list of personalised content and advertising cookies',
        CS: 'Zobrazit seznam personalizovaného obsahu a reklamních souborů cookie',
        DK: 'Vis liste over cookies til personligt tilpasset indhold og annoncer',
        DE: 'Marketing-Cookies anzeigen',
        EE: 'Kuva isikupärastatud sisu küpsiste ja reklaamiküpsiste loend',
        FI: 'Näytä henkilökohtaiseen sisältöön ja mainontaan liittyvien evästeiden luettelo',
        FR: 'Afficher la liste des cookies de contenu personnalisé et de publicité',
        HU: 'A személyre szabott tartalmak és hirdetési cookie-k listájának megjelenítése',
        IT: "Mostrare l'elenco dei contenuti personalizzati e dei cookie pubblicitari",
        LT: 'Rodyti suasmeninto turinio ir reklamos slapukų sąrašą',
        LV: 'Parādīt personalizēta satura un reklāmu sīkdatnes',
        NL: 'Lijst met gepersonaliseerde inhoud en reclamecookies weergeven',
        NO: 'Vis liste over informasjonskapsler for tilpasset innhold og reklame',
        PL: 'Pokaż listę plików cookie służących do personalizacji treści i reklam',
        RO: 'Afişează lista cu modulele cookie de conţinut şi publicitate personalizate',
        SK: 'Zobraziť zoznam súborov cookie na poskytovanie personalizovaného obsahu a reklamy',
        SV: 'Visa lista över personligt innehåll och reklamcookies',
        default: 'Show list of personalised content and advertising cookies',
      },
    ],
    hide_services: [
      {
        EN: 'Hide list of essential cookies',
        CS: 'Skrýt seznam základních souborů cookie',
        DK: 'Skjul liste over nødvendige cookies',
        DE: 'Unbedingt erforderliche Cookies verbergen',
        EE: 'Peida oluliste küpsiste loend',
        FI: 'Piilota välttämättömien evästeiden luettelo',
        FR: 'Masquer la liste des cookies essentiels',
        HU: 'Az alapvető cookie-k listájának elrejtése',
        IT: "Nascondere l'elenco dei cookie essenziali",
        LT: 'Slėpti būtinųjų slapukų sąrašą',
        LV: 'Paslēpt noteikti nepieciešamo sīkdatņu sarakstu',
        NL: 'Lijst met essentiële cookies verbergen',
        NO: 'Skjul liste over grunnleggende informasjonskapsler',
        PL: 'Ukryj listę niezbędnych plików cookie',
        RO: 'Ascunde lista cu modulele cookie esenţiale',
        SK: 'Skryť zoznam nevyhnutných súborov cookie',
        SV: 'Dölj listan över nödvändiga cookies',
        default: 'Hide list of essential cookies',
      },
      {
        EN: 'Hide list of enhanced functionality cookies',
        CS: 'Skrýt seznam souborů cookie s rozšířenými funkcemi',
        DK: 'Skjul liste over cookies, der giver forbedret funktionalitet',
        DE: 'Funktionelle Cookies verbergen',
        EE: 'Peida täiustatud funktsionaalsusega küpsiste loend',
        FI: 'Piilota lisätoimintoevästeiden luettelo',
        FR: 'Masquer la liste des cookies de fonctionnalité',
        HU: 'A bővített funkcionalitású cookie-k listájának elrejtése',
        IT: "Nascondere l'elenco dei cookie di funzionalità avanzata",
        LT: 'Slėpti išplėsto funkcionalumo slapukų sąrašą',
        LV: 'Paslēpt uzlabotas funkcionalitātes sīkdatņu sarakstu',
        NL: 'Lijst met cookies voor een betere functionaliteit verbergen',
        NO: 'Skjul liste over informasjonskapsler for forbedret funksjonalitet',
        PL: 'Ukryj listę plików cookie zwiększających funkcjonalność',
        RO: 'Ascunde lista cu module cookie pentru funcţionalitate îmbunătăţită',
        SK: 'Skryť zoznam súborov cookie na zlepšenie funkčnosti',
        SV: 'Dölj lista över cookies för utökad funktionalitet',
        default: 'Hide list of enhanced functionality cookies',
      },
      {
        EN: 'Hide list of measurement and optimisation cookies',
        CS: 'Skrýt seznam souborů cookie pro měření a optimalizaci',
        DK: 'Skjul liste over måling- og optimeringscookies',
        DE: 'Performance-Cookies verbergen',
        EE: 'Peida mõõtmis- ja optimeerimisküpsiste loend',
        FI: 'Piilota mittaus- ja optimointievästeiden luettelo',
        FR: "Masquer la liste des cookies de mesure et d'optimisation",
        HU: 'Mérési és optimalizálási cookie-k listájának elrejtése',
        IT: "Nascondere l'elenco dei cookie di misurazione e ottimizzazione",
        LT: 'Slėpti matavimo ir optimizavimo slapukų sąrašą',
        LV: 'Paslēpt mērījumu un optimizācijas sīkdatņu sarakstu',
        NL: 'Lijst met analytische en optimalisatiecookies verbergen',
        NO: 'Skjul liste over informasjonskapsler for måling og optimalisering',
        PL: 'Ukryj listę plików cookie służących do pomiarów i optymalizacji',
        RO: 'Ascunde lista cu modulele cookie de măsurători şi optimizare',
        SK: 'Skryť zoznam súborov cookie nameranie a optimalizáciu',
        SV: 'Dölj lista över mät- och optimeringscookies',
        default: 'Hide list of measurement and optimisation cookies',
      },
      {
        EN: 'Hide list of personalised content and advertising cookies',
        CS: 'Skrýt seznam personalizovaného obsahu a reklamních souborů cookie',
        DK: 'Skjul liste over cookies til personligt tilpasset indhold og annoncer',
        DE: 'Marketing-Cookies verbergen',
        EE: 'Peida isikupärastatud sisu küpsiste ja reklaamiküpsiste loend',
        FI: 'Piilota henkilökohtaiseen sisältöön ja mainontaan liittyvien evästeiden luettelo',
        FR: 'Masquer la liste des cookies de contenu personnalisé et de publicité',
        HU: 'A személyre szabott tartalmak és hirdetési cookie-k listájának elrejtése',
        IT: "Nascondere l'elenco dei contenuti personalizzati e dei cookie pubblicitari",
        LT: 'Slėpti suasmeninto turinio ir reklamos slapukų sąrašą',
        LV: 'Paslēpt personalizēta satura un reklāmu sīkdatnes',
        NL: 'Lijst met gepersonaliseerde inhoud en reclamecookies verbergen',
        NO: 'Skjul liste over informasjonskapsler for tilpasset innhold og reklame',
        PL: 'Ukryj listę plików cookie służących do personalizacji treści i reklam',
        RO: 'Ascunde lista cu modulele cookie de conţinut şi publicitate personalizate',
        SK: 'Skryť zoznam súborov cookie na poskytovanie personalizovaného obsahu a reklamy',
        SV: 'Dölj lista över personligt innehåll och reklamcookies',
        default: 'Hide list of personalised content and advertising cookies',
      },
    ],
    cookie_purpose: {
      EN: 'Purpose of cookie',
      CS: 'Účel souboru cookie',
      DK: 'Cookiens formål',
      DE: 'Zweck des Cookies',
      EE: 'Küpsiste eesmärk',
      FI: 'Evästeen tarkoitus',
      FR: 'Description du cookie',
      HU: 'A cookie célja',
      IT: 'Scopo dei cookie',
      LT: 'Slapuko paskirtis',
      LV: 'Sīkdatņu mērķis',
      NL: 'Doel van de cookie',
      NO: 'Hva brukes denne informasjonskapselen til',
      PL: 'Zastosowanie pliku cookie',
      RO: 'Scopul modulului cookie',
      SK: 'Účel súboru cookie',
      SV: "Syftet med cookie'n",
      default: 'Purpose of cookie',
    },
  },
  Preferences: {
    'Google GStatic': {
      EN: {
        description:
          'Google uses this domain to off-loaded static content (JavaScript code, images and CSS) to a different domain name in an effort to reduce bandwidth usage and increase network performance for the end user. gstatic.com is a cookieless domain to deliver static content for Google. One benefit of hosting static components on a cookie-free domain is that some proxies might refuse to cache the components that are requested with cookies.',
        dataShare: 'This cookie shares data with our trusted third party partners and/or outside the EU/EEA.',
      },
      CS: {
        description:
          'Společnost Google používá tuto doménu pro načítání statického obsahu (kódu JavaScript, obrázků a kaskádových stylů CSS) z domény s jiným názvem ve snaze omezit využívání šířky pásma a zvýšit výkon sítě pro koncové uživatele. Doména gstatic.com nepoužívá soubory cookie a slouží k poskytování statického obsahu společnosti Google. Jednou z výhod hostování statických komponent v doménách bez souborů cookie je fakt, že některé servery proxy mohou odmítnout uložit do mezipaměti komponenty vyžadované spolu se soubory cookie.',
        dataShare:
          'Tento soubor cookie sdílí data s našimi důvěryhodnými partnery z řad třetích stran a /nebo mimo EU/EHP.',
      },
      DK: {
        description:
          'Google bruger dette domæne til ikke-indlæst statisk indhold (JavaScript-kode, billeder og CSS) til et andet domænenavn i et forsøg på at reducere brugen af ​​båndbredde og øge netværksydelsen for slutbrugeren. gstatic.com er et cookiefrit domaine for statiske komponenter til Google. En fordel ved at have statiske kompenter på et cookie-frit domæne er, at nogle proxies muligvis nægter at cache de komponenter, der anmodes om med cookies.',
        dataShare: 'Denne cookie deler data med vores betroede tredjepartspartnere og/eller uden for EU/EØS.',
      },
      DE: {
        description:
          'Google verwendet diese Domain, um statische Inhalte (JavaScript-Code, Bilder und CSS) auf einen anderen Domainnamen auszulagern, um die Bandbreitennutzung zu reduzieren und die Netzwerkleistung für den Endnutzer zu erhöhen. gstatic.com ist eine cookielose Domain, um statische Inhalte für Google bereitzustellen. Ein Vorteil des Hostings von statischen Komponenten auf einer Cookie-freien Domain ist, dass einige Proxies sich weigern könnten, die Komponenten, die mit Cookies angefordert werden, in den Cache zu stellen.',
        dataShare:
          'Dieses Cookie teilt Daten mit unseren vertrauenswürdigen Drittpartnern und/oder außerhalb der EU/EWR.',
      },
      EE: {
        description:
          "Google kasutab seda domeeni staatilise sisu (JavaScript-kood, pildid ja CSS) ümberpaigutamiseks teisele domeeninimele, et vähendada ribalaiuse kasutamist ja suurendada võrgu jõudlust lõppkasutaja jaoks. gstatic.com on Google'i jaoks staatilise sisu edastamiseks kasutatav küpsiseta domeen. Üks kasu staatiliste komponentide majutamisest küpsiseta domeenis on see, et mõned vahendajad võivad keelduda küpsistega taotletud komponentide vahemällu salvestamisest.",
        dataShare:
          'See küpsis jagab andmeid meie usaldatud kolmandast isikust partneritega ja/või väljaspool EL-i/EMP-d.',
      },
      FI: {
        description:
          'Google käyttää tätä verkkotunnusta ladatun staattisen sisällön (JavaScript-koodi, kuvat ja CSS) siirtämiseen eri verkkotunnukseen pyrkien vähentämään kaistanleveyden käyttöä ja parantamaan loppukäyttäjän verkon suorituskykyä. gstatic.com on evästeetön verkkotunnus, joka toimittaa staattista sisältöä Googlelle. Staattisten komponenttien isännöinnistä evästeettömälle verkkotunnukselle on yksi etu, että jotkut välityspalvelimet saattavat kieltäytyä välimuistista evästeiden kanssa pyydettyjä osia.',
        dataShare:
          'Tämä eväste jakaa tietoja yrityksen kumppaneina toimivien luotettavien kolmansien osapuolten kanssa ja/tai EU:n/ETA:n ulkopuolelle.',
      },
      FR: {
        description:
          "Google utilise ce domaine pour décharger du contenu statique (code JavaScript, images et CSS) vers un nom de domaine différent dans le but de réduire la consommation de bande passante et d'améliorer les performances du réseau pour l'utilisateur final. gstatic.com est un domaine sans cookie permettant de transmettre du contenu statique pour Google. L'un des avantages de l'hébergement de composants statiques sur un domaine sans cookies est que certains mandataires peuvent refuser de mettre en cache les composants qui sont demandés avec des cookies.",
        dataShare: "Ce cookie partage des données avec nos partenaires tiers de confiance et/ou en dehors de l'UE/EEE.",
      },
      HU: {
        description:
          'A Google ezt a tartományt használja a nem betöltött statikus tartalmak (JavaScript kód, képek és CSS) másik tartománynévvel jelölésére, hogy csökkentse a sávszélesség-használatot és növelje a hálózati teljesítményt a végfelhasználó számára. A gstatic.com egy cookie-mentes tartomány, amely statikus tartalmat biztosít a Google számára. A statikus összetevők cookie-mentes tartományban való elhelyezésének egyik előnye, hogy egyes proxyk visszautasíthatják a cookie-kkal igényelt összetevők gyorsítótárazását.',
        dataShare: 'Ez a cookie adatokat oszt meg megbízható harmadik fél partnereinkkel és/vagy az EU/EGT-n kívül.',
      },
      IT: {
        description:
          "Google utilizza questo dominio per scaricare il contenuto statico (codice JavaScript, immagini e CSS) su un nome di dominio diverso nel tentativo di ridurre l'utilizzo della larghezza di banda e aumentare le prestazioni di rete per l'utente finale. gstatic.com è un dominio senza cookie per fornire contenuti statici per Google. Un vantaggio dell'hosting di componenti statici su un dominio privo di cookie è che alcuni proxy potrebbero rifiutarsi di memorizzare nella cache i componenti richiesti con i cookie.",
        dataShare:
          "Questo cookie condivide i dati con i nostri partner terze parti fidate e/o al di fuori dell'UE/SEE.",
      },
      LT: {
        description:
          'Google naudoja šį domeną perkeldama statinį turinį (JavaScript kodą, vaizdus ir CSS) į kitą domeno vardą, siekdama sumažinti pralaidumo naudojimą ir padidinti tinklo našumą galutiniam vartotojui. gstatic.com yra domenas be slapukų, teikiantis statinį turinį Google. Vienas statinių komponentų talpinimo be slapukų domeno privalumas yra tas, kad kai kurie įgaliotieji gali atsisakyti talpinti komponentus, kurių prašoma su slapukais.',
        dataShare:
          'Šis slapukas bendrina duomenis su patikimais trečiųjų šalių partneriais ir (arba) už ES / EEE ribų.',
      },
      LV: {
        description:
          'Google izmanto šo domēnu, lai statisko saturu (JavaScript kodu, attēlus un CSS) pārceltu uz citu domēna nosaukumu, tādējādi cenšoties samazināt joslas platuma izmantošanu un palielināt tīkla veiktspēju galalietotājam. gstatic.com ir domēns bez sīkfailiem, kas nodrošina statisko saturu Google vajadzībām. Viena no priekšrocībām, ko dod statisko komponentu izvietošana bez sīkfailu domēnā, ir tā, ka daži starpniekserveri var atteikties kešēt komponentus, kas tiek pieprasīti ar sīkfailiem.',
        dataShare: 'Šīs sīkdatnes kopīgo informāciju ar mūsu uzticamiem trešās puses partneriem un/vai ārpus ES/EEZ.',
      },
      NL: {
        description:
          "Google gebruikt dit domein om statische inhoud (JavaScript-code, afbeeldingen en CSS) te off-loaden naar een andere domeinnaam in een poging om het bandbreedtegebruik te verminderen en de netwerkprestaties voor de eindgebruiker te verbeteren. gstatic.com is een cookieloos domein om statische inhoud voor Google te leveren. Een voordeel van het hosten van statische componenten op een cookieloos domein is dat sommige proxy's zouden kunnen weigeren om de componenten die met cookies worden opgevraagd in de cache op te slaan.",
        dataShare: 'Deze cookie deelt gegevens met onze vertrouwde externe partners en/of buiten de EU/EER.',
      },
      NO: {
        description:
          'Google bruker dette domenet til å laste ned statisk innhold (JavaScript-kode, bilder og CSS) til et annet domenenavn i et forsøk på å redusere bruk av båndbredde og øke nettverksytelsen for sluttbrukeren. gstatic.com er et cookieless domene for å levere statisk innhold til Google. En fordel ved å være vert for statiske komponenter på et informasjonskapselfritt domene er at noen fullmakter kan nekte å cache komponentene som blir bedt om med informasjonskapsler.',
        dataShare:
          'Denne informasjonskapselen deler data med våre pålitelige tredjepartsleverandører og/eller utenfor EU/EEA.',
      },
      PL: {
        description:
          'Google używa tej domeny do przeładowania statycznej zawartości (kod JavaScript, obrazy i CSS) do innej nazwy domeny w celu zmniejszenia zużycia pasma i zwiększenia wydajności sieci dla użytkownika końcowego. gstatic.com jest domeną bez plików cookie do dostarczania statycznej zawartości dla Google. Jedną z zalet hostowania statycznych komponentów w domenie bez plików cookie jest to, że niektórzy pośrednicy mogą odmówić buforowania komponentów wymaganych przez pliki cookie.',
        dataShare: 'Ten plik cookie udostępnia dane naszym zaufanym partnerom lub podmiotom spoza UE/EOG.',
      },
      RO: {
        description:
          'Google utilizează acest domeniu pentru conţinut static descărcat (cod JavaScript, imagini şi CSS) pentru un nume de domeniu diferit, într-un efort de a reduce utilizarea lăţimii de bandă şi a creşte performanţa reţelei pentru utilizatorul final. gstatic.com este un domeniu fără module cookie pentru livrarea conţinutului static pentru Google. Un avantaj al găzduirii componentelor statice pe un domeniu fără module cookie este că unele proxy-uri ar putea refuza să mascheze componentele care sunt solicitate cu module cookie.',
        dataShare: 'Acest modul cookie partajează date cu partenerii noştri terţi de încredere şi/sau în afara UE/SEE.',
      },
      SK: {
        description:
          'Spoločnosť Google používa túto doménu na načítanie statického obsahu (kód JavaScript, obrázky a CSS) do iného názvu domény v snahe znížiť využitie šírky pásma a zvýšiť výkon siete pre koncového používateľa. gstatic.com je doména bez súborov cookie, ktorá umožňuje dodávať statický obsah pre spoločnosť Google. Jednou z výhod hostingu statických komponentov na doméne bez súborov cookie je, že niektoré servery proxy môžu odmietnuť ukladať do vyrovnávacej pamäte komponenty, ktoré sú požadované so súbormi cookie.',
        dataShare:
          'Tento súbor cookie zdieľa údaje s našimi dôveryhodnými partnermi tretích strán a/alebo mimo EÚ/EHP.',
      },
      SV: {
        description:
          'Google använder den här domänen för att ladda statiskt innehåll (JavaScript-kod, bilder och CSS) till ett annat domännamn i ett försök att minska bandbreddsanvändning och öka nätverksprestanda för slutanvändaren. gstatic.com är en cookie-fri domän för att leverera statiskt innehåll till Google. En fördel med att hålla statiska komponenter på en cookie-fri domän är att vissa proxyservrar kan vägra att cacha de komponenter som begärs med cookies.',
        dataShare: 'Denna cookie delar data med våra betrodda tredjepartspartners och/eller utanför EU/EES.',
      },
      default: {
        description:
          'Google uses this domain to off-loaded static content (JavaScript code, images and CSS) to a different domain name in an effort to reduce bandwidth usage and increase network performance for the end user. gstatic.com is a cookieless domain to deliver static content for Google. One benefit of hosting static components on a cookie-free domain is that some proxies might refuse to cache the components that are requested with cookies.',
        dataShare: 'This cookie shares data with our trusted third party partners and/or outside the EU/EEA.',
      },
    },
    'Parcel Labs': {
      EN: {
        description:
          'Distrelec partner with Parcel Labs to provide a seamless delivery experience. These cookies enable us to track your session in relation to your order and delivery options.',
        dataShare: 'This cookie shares data with our trusted third party partners and/or outside the EU/EEA.',
      },
      CS: {
        description:
          'Společnost Distrelec spolupracuje se společností Parcel Labs na zajištění bezproblémového doručování. Tyto soubory cookie nám umožňují sledovat vaši relaci s ohledem na možnosti vaší objednávky a doručení.',
        dataShare:
          'Tento soubor cookie sdílí data s našimi důvěryhodnými partnery z řad třetích stran a /nebo mimo EU/EHP.',
      },
      DK: {
        description:
          'Elfa Distrelec samarbejder med Parcel Labs for at yde en effektiv leverings oplevelse. Disse cookies gør os i stand til at følge din session i relation til din ordre og leveringsmuligheder.',
        dataShare: 'Denne cookie deler data med vores betroede tredjepartspartnere og/eller uden for EU/EØS.',
      },
      DE: {
        description:
          'Distrelec arbeitet mit Parcel Labs zusammen, um eine nahtlose Zustellung zu ermöglichen. Diese Cookies ermöglichen es uns, Ihre Sitzung in Bezug auf Ihre Bestellung und Ihre Lieferoptionen zu verfolgen.',
        dataShare:
          'Dieses Cookie teilt Daten mit unseren vertrauenswürdigen Drittpartnern und/oder außerhalb der EU/EWR.',
      },
      EE: {
        description:
          "Elfa Distrelec teeb koostööd Parcel Labs'iga, et tagada sujuvat tarnekogemust. Need küpsised võimaldavad meil jälgida teie seanssi seoses teie tellimuse ja tarnevõimalustega.",
        dataShare:
          'See küpsis jagab andmeid meie usaldatud kolmandast isikust partneritega ja/või väljaspool EL-i/EMP-d.',
      },
      FI: {
        description:
          'Distrelec tarjoaa Parcel Labsin kanssa saumattoman toimituskokemuksen. Näiden evästeiden avulla voimme seurata istuntoasi suhteessa tilaukseesi ja toimitusvaihtoehtoihisi.',
        dataShare:
          'Tämä eväste jakaa tietoja yrityksen kumppaneina toimivien luotettavien kolmansien osapuolten kanssa ja/tai EU:n/ETA:n ulkopuolelle.',
      },
      FR: {
        description:
          "Distrelec s'est associé à Parcel Labs pour offrir une expérience de livraison optimale. Ces cookies nous permettent de suivre votre session en relation avec votre commande et vos options de livraison.",
        dataShare: "Ce cookie partage des données avec nos partenaires tiers de confiance et/ou en dehors de l'UE/EEE.",
      },
      HU: {
        description:
          'A Distrelec összefogott a Parcel Labs-zel a zökkenőmentes szállítás érdekében. Ezek a cookie-k lehetővé teszik számunkra, hogy nyomon kövessük az Ön munkamenetét a rendeléssel és a szállítási lehetőségekkel kapcsolatban.',
        dataShare: 'Ez a cookie adatokat oszt meg megbízható harmadik fél partnereinkkel és/vagy az EU/EGT-n kívül.',
      },
      IT: {
        description:
          "Distrelec collabora con Parcel Labs per fornire un'esperienza di consegna senza interruzioni. Questi cookie ci consentono di monitorare la tua sessione in relazione al tuo ordine e alle opzioni di consegna.",
        dataShare:
          "Questo cookie condivide i dati con i nostri partner terze parti fidate e/o al di fuori dell'UE/SEE.",
      },
      LT: {
        description:
          'Distrelec partneris su Parcel Labs teikia sklandų pristatymo patirtį. Šie slapukai leidžia mums stebėti jūsų ssesiją, atsižvelgiant į jūsų užsakymą ir pristatymo galimybes.',
        dataShare:
          'Šis slapukas bendrina duomenis su patikimais trečiųjų šalių partneriais ir (arba) už ES / EEE ribų.',
      },
      LV: {
        description:
          'Elfa Distrelec sadarbojas ar Parcel Labs, lai nodrošinātu nevainojamu piegādi. Šie sīkfaili ļauj mums izsekot jūsu sesijai saistībā ar jūsu pasūtījumu un piegādes iespējām.',
        dataShare: 'Šīs sīkdatnes kopīgo informāciju ar mūsu uzticamiem trešās puses partneriem un/vai ārpus ES/EEZ.',
      },
      NL: {
        description:
          'Distrelec werkt samen met Parcel Labs om een naadloze leveringservaring te bieden. Deze cookies laten ons toe uw sessie te volgen in relatie tot uw bestelling en leveringsopties.',
        dataShare: 'Deze cookie deelt gegevens met onze vertrouwde externe partners en/of buiten de EU/EER.',
      },
      NO: {
        description:
          'Elfa Distrelec samarbeider med Parcel Labs for å gi en sømløs leveringsopplevelse. Disse informasjonskapslene gjør det mulig for oss å spore økten din i forhold til bestillings- og leveringsalternativene.',
        dataShare:
          'Denne informasjonskapselen deler data med våre pålitelige tredjepartsleverandører og/eller utenfor EU/EEA.',
      },
      PL: {
        description:
          'Partner Distrelec z Parcel Labs zapewnia bezproblemową dostawę. Te pliki cookie umożliwiają nam śledzenie sesji w odniesieniu do opcji zamówienia i dostawy.',
        dataShare: 'Ten plik cookie udostępnia dane naszym zaufanym partnerom lub podmiotom spoza UE/EOG.',
      },
      RO: {
        description:
          'Distrelec a încheiat un parteneriat cu Parcel Labs pentru a oferi o experienţă de livrare fără probleme. Aceste module cookie ne permit să urmărim sesiunea dvs. în legătură cu comanda dvs. şi opţiunile dvs. de livrare.',
        dataShare: 'Acest modul cookie partajează date cu partenerii noştri terţi de încredere şi/sau în afara UE/SEE.',
      },
      SK: {
        description:
          'Spoločnosť Distrelec spolupracuje so spoločnosťou Parcel Labs pri zabezpečovaní plynulého doručovania produktov. Tieto súbory cookie nám umožňujú sledovať vašu reláciu v súvislosti s vašou objednávkou a možnosťami doručenia.',
        dataShare:
          'Tento súbor cookie zdieľa údaje s našimi dôveryhodnými partnermi tretích strán a/alebo mimo EÚ/EHP.',
      },
      SV: {
        description:
          'Elfa Distrelec samarbetar med Parcel Labs för att ge en sömlös leveransupplevelse. Dessa kakor gör det möjligt för oss att spåra din session i förhållande till dina beställnings- och leveransalternativ.',
        dataShare: 'Denna cookie delar data med våra betrodda tredjepartspartners och/eller utanför EU/EES.',
      },
      default: {
        description:
          'Distrelec partner with Parcel Labs to provide a seamless delivery experience. These cookies enable us to track your session in relation to your order and delivery options.',
        dataShare: 'This cookie shares data with our trusted third party partners and/or outside the EU/EEA.',
      },
    },
  },
  Statistics: {
    'Adobe Visitor ID Service': {
      EN: {
        description:
          'Adobe Analytics cookies allow Distrelec to collect information about how visitors use our Webshop. They keep track of when a visitor enters and leaves the Webshop and what pages they view and actions they perform.',
        dataShare: 'This cookie shares data with our trusted third party partners and/or outside the EU/EEA.',
      },
      CS: {
        description:
          'Soubory cookie služby Adobe Analytics umožňují společnosti Distrelec shromažďovat informace o tom, jakým způsobem návštěvníci náš internetový obchod využívají. Tyto soubory sledují, kdy návštěvník vstoupí do internetového obchodu a opustí jej, jaké stránky si prohlíží a jaké akce provádí.',
        dataShare:
          'Tento soubor cookie sdílí data s našimi důvěryhodnými partnery z řad třetích stran a /nebo mimo EU/EHP.',
      },
      DK: {
        description:
          'Adobe Analytics-cookies giver Elfa Distrelec mulighed for at indsamle oplysninger om, hvordan besøgende bruger vores webshop. De holder styr på, hvornår en besøgende kommer ind og forlader webshoppen, og hvilke sider de ser og handlinger, de udfører.',
        dataShare: 'Denne cookie deler data med vores betroede tredjepartspartnere og/eller uden for EU/EØS.',
      },
      DE: {
        description:
          'Adobe Analytics-Cookies ermöglichen es Distrelec, Informationen darüber zu sammeln, wie Besucher unseren Webshop nutzen. Sie verfolgen, wann ein Besucher den Webshop betritt und verlässt und welche Seiten er ansieht und welche Aktionen er ausführt.',
        dataShare:
          'Dieses Cookie teilt Daten mit unseren vertrauenswürdigen Drittpartnern und/oder außerhalb der EU/EWR.',
      },
      EE: {
        description:
          'Adobe Analyticsi küpsised võimaldavad Elfa Distrelecil koguda teavet, kuidas külastajad meie veebipoodi kasutavad. Need jälgivad külastajate veebipoodi sisenemist ja sealt lahkumist ning nende vaadatavaid lehti ja tehtavaid toiminguid.',
        dataShare:
          'See küpsis jagab andmeid meie usaldatud kolmandast isikust partneritega ja/või väljaspool EL-i/EMP-d.',
      },
      FI: {
        description:
          'Adobe Analytics -evästeiden avulla Distrelec voi kerätä tietoja siitä, miten kävijät käyttävät verkkokauppaa. He seuraavat, milloin kävijä tulee verkkokauppaan ja poistuu siitä, mitä sivuja hän tarkastelee ja mitä toimintoja he tekevät.',
        dataShare:
          'Tämä eväste jakaa tietoja yrityksen kumppaneina toimivien luotettavien kolmansien osapuolten kanssa ja/tai EU:n/ETA:n ulkopuolelle.',
      },
      FR: {
        description:
          "Les cookies Adobe Analytics permettent à Distrelec de collecter des informations sur la manière dont les visiteurs utilisent la boutique en ligne. Ils gardent une trace de l'heure à laquelle un visiteur se rend sur la boutique en ligne et quitte celle-ci, ainsi que des pages qu'il a consultées et des actions qu'il a effectuées.",
        dataShare: "Ce cookie partage des données avec nos partenaires tiers de confiance et/ou en dehors de l'UE/EEE.",
      },
      HU: {
        description:
          'Az Adobe Analytics lehetővé teszi a Distrelec számára, hogy információt gyűjtsön a látogatók szokásairól a webáruház használata közben. Nyomon követik, mikor lép be egy látogató a webáruházba és mikor hagyja el azt, illetve, hogy mely oldalakat tekinti meg és milyen műveleteket végez a weboldalon.',
        dataShare: 'Ez a cookie adatokat oszt meg megbízható harmadik fél partnereinkkel és/vagy az EU/EGT-n kívül.',
      },
      IT: {
        description:
          'I cookie di Adobe Analytics consentono a Distrelec di raccogliere informazioni su come i visitatori utilizzano il nostro Webshop. Tengono traccia di quando un visitatore entra ed esce dal Webshop e quali pagine visualizzano e le azioni che eseguono.',
        dataShare:
          "Questo cookie condivide i dati con i nostri partner terze parti fidate e/o al di fuori dell'UE/SEE.",
      },
      LT: {
        description:
          'Adobe Analytics slapukai leidžia Distrelec rinkti informaciją apie tai, kaip lankytojai naudojasi mūsų internetine parduotuve. Jie stebi, kada lankytojas įeina į internetinę parduotuvę ir iš jos išeina, kokius puslapius peržiūri ir kokius veiksmus atlieka.',
        dataShare:
          'Šis slapukas bendrina duomenis su patikimais trečiųjų šalių partneriais ir (arba) už ES / EEE ribų.',
      },
      LV: {
        description:
          'Adobe Analytics sīkfaili ļauj Elfa Distrelec ievākt informāciju par to, kā apmeklētāji izmanto mūsu interneta veikalu. Tie izseko, kad apmeklētājs atver un aizver interneta veikalu, kā arī apmeklētāja skatītās lapas un veiktās darbības.',
        dataShare: 'Šīs sīkdatnes kopīgo informāciju ar mūsu uzticamiem trešās puses partneriem un/vai ārpus ES/EEZ.',
      },
      NL: {
        description:
          "Adobe Analytics cookies stellen Distrelec in staat om informatie te verzamelen over hoe bezoekers onze Webshop gebruiken. Ze houden bij wanneer een bezoeker de Webshop binnenkomt en verlaat, welke pagina's hij bekijkt en welke acties hij uitvoert.",
        dataShare: 'Deze cookie deelt gegevens met onze vertrouwde externe partners en/of buiten de EU/EER.',
      },
      NO: {
        description:
          'Adobe Analytics-informasjonskapsler tillater Elfa Distrelec å samle inn informasjon om hvordan besøkende bruker nettbutikken vår. De holder rede på når en besøkende kommer inn og forlater nettbutikken og hvilke sider de viser og handlinger de utfører.',
        dataShare:
          'Denne informasjonskapselen deler data med våre pålitelige tredjepartsleverandører og/eller utenfor EU/EEA.',
      },
      PL: {
        description:
          'Pliki cookie programu Adobe Analytics umożliwiają firmie Distrelec zbieranie informacji na temat tego, w jakich celach użytkownicy odwiedzają nasz sklep internetowy. Pliki te śledzą, kiedy odwiedzający wchodzi na stronę naszego sklepu internetowego i kiedy go opuszcza oraz jakie strony odwiedza i jakie przeprowadza czynności.',
        dataShare: 'Ten plik cookie udostępnia dane naszym zaufanym partnerom lub podmiotom spoza UE/EOG.',
      },
      RO: {
        description:
          'Modulele cookie Adobe Analytics permit Distrelec să colecteze informaţii despre modul în care vizitatorii utilizează magazinul nostru web. Acestea monitorizează momentele în care un vizitator intră sau iese din magazinul web şi paginile pe care aceştia le vizualizează şi acţiunile pe care le efectuează.',
        dataShare: 'Acest modul cookie partajează date cu partenerii noştri terţi de încredere şi/sau în afara UE/SEE.',
      },
      SK: {
        description:
          'Súbory cookie služby Adobe Analytics umožňujú spoločnosti Distrelec zhromažďovať informácie o tom, ako návštevníci používajú náš internetový obchod. Sledujú, kedy návštevník vstúpi do internetového obchodu a kedy ho opustí, aké stránky zobrazuje a aké akcie vykonáva.',
        dataShare:
          'Tento súbor cookie zdieľa údaje s našimi dôveryhodnými partnermi tretích strán a/alebo mimo EÚ/EHP.',
      },
      SV: {
        description:
          'Adobe Analytics kakor ger Elfa Distrelec möjlighet att samla in information om hur besökare använder vår webbplats. De håller koll på när besökare kommer in och lämnar webbutiken, vilka sidor de tittar på och vilka handlingar de genomför.',
        dataShare: 'Denna cookie delar data med våra betrodda tredjepartspartners och/eller utanför EU/EES.',
      },
      default: {
        description:
          'Adobe Analytics cookies allow Distrelec to collect information about how visitors use our Webshop. They keep track of when a visitor enters and leaves the Webshop and what pages they view and actions they perform.',
        dataShare: 'This cookie shares data with our trusted third party partners and/or outside the EU/EEA.',
      },
    },
    'Google Analytics': {
      EN: {
        description:
          'Google Analytics cookies allow Distrelec to collect information about how visitors use our Webshop. They keep track of when a visitor enters and leaves the Webshop and what pages they view and actions they perform.',
        dataShare: 'This cookie shares data with our trusted third party partners and/or outside the EU/EEA.',
      },
      CS: {
        description:
          'Soubory cookie služby Google Analytics umožňují společnosti Distrelec shromažďovat informace o tom, jakým způsobem návštěvníci náš internetový obchod používají. Tyto soubory sledují, kdy návštěvník vstoupí do internetového obchodu a opustí jej, jaké stránky si prohlíží a jaké akce provádí.',
        dataShare:
          'Tento soubor cookie sdílí data s našimi důvěryhodnými partnery z řad třetích stran a /nebo mimo EU/EHP.',
      },
      DK: {
        description:
          'Google Analytics-cookies giver Elfa Distrelec mulighed for at indsamle oplysninger om, hvordan besøgende bruger vores webshop. De holder styr på, hvornår en besøgende kommer ind og forlader webshoppen, og hvilke sider de ser og handlinger, de udfører.',
        dataShare: 'Denne cookie deler data med vores betroede tredjepartspartnere og/eller uden for EU/EØS.',
      },
      DE: {
        description:
          'Google Analytics-Cookies ermöglichen es Distrelec, Informationen darüber zu sammeln, wie Besucher unseren Webshop nutzen. Sie verfolgen, wann ein Besucher den Webshop betritt und verlässt und welche Seiten er ansieht und welche Aktionen er durchführt.',
        dataShare:
          'Dieses Cookie teilt Daten mit unseren vertrauenswürdigen Drittpartnern und/oder außerhalb der EU/EWR.',
      },
      EE: {
        description:
          'Google Analyticsi küpsised võimaldavad Elfa Distrelecil koguda teavet, kuidas külastajad meie veebipoodi kasutavad. Need jälgivad külastajate veebipoodi sisenemist ja sealt lahkumist ning nende vaadatavaid lehti ja tehtavaid toiminguid.',
        dataShare:
          'See küpsis jagab andmeid meie usaldatud kolmandast isikust partneritega ja/või väljaspool EL-i/EMP-d.',
      },
      FI: {
        description:
          'Google Analytics -evästeiden avulla Distrelec voi kerätä tietoja siitä, kuinka kävijät käyttävät verkkokauppaa. He seuraavat, milloin kävijä tulee verkkokauppaan ja poistuu siitä, mitä sivuja hän tarkastelee ja mitä toimintoja he tekevät.',
        dataShare:
          'Tämä eväste jakaa tietoja yrityksen kumppaneina toimivien luotettavien kolmansien osapuolten kanssa ja/tai EU:n/ETA:n ulkopuolelle.',
      },
      FR: {
        description:
          "Les cookies Google Analytics permettent à Distrelec de collecter des informations sur la manière dont les visiteurs utilisent la boutique en ligne. Ils gardent une trace de l'heure à laquelle un visiteur se rend sur la boutique en ligne et quitte celle-ci, ainsi que des pages qu'il a consultées et des actions qu'il a effectuées.",
        dataShare: "Ce cookie partage des données avec nos partenaires tiers de confiance et/ou en dehors de l'UE/EEE.",
      },
      HU: {
        description:
          'Ezek a cookie-k lehetővé teszik a Distrelec számára, hogy információt gyűjtsön azzal kapcsolatban, hogyan használják látogatóink a webáruházat. Nyomon követik, mikor lép be egy látogató a webáruházba és mikor hagyja el azt, illetve, hogy mely oldalakat tekinti meg és milyen műveleteket végez a weboldalon.',
        dataShare: 'Ez a cookie adatokat oszt meg megbízható harmadik fél partnereinkkel és/vagy az EU/EGT-n kívül.',
      },
      IT: {
        description:
          'I cookie di Google Analytics consentono a Distrelec di raccogliere informazioni su come i visitatori utilizzano il nostro Webshop. Tengono traccia di quando un visitatore entra ed esce dal Webshop e quali pagine visualizzano e le azioni che eseguono.',
        dataShare:
          "Questo cookie condivide i dati con i nostri partner terze parti fidate e/o al di fuori dell'UE/SEE.",
      },
      LT: {
        description:
          'Google Analytics slapukai leidžia Distrelec rinkti informaciją apie tai, kaip lankytojai naudojasi mūsų internetine parduotuve. Jie stebi, kada lankytojas įeina į internetinę parduotuvę ir iš jos išeina, kokius puslapius peržiūri ir kokius veiksmus atlieka.',
        dataShare:
          'Šis slapukas bendrina duomenis su patikimais trečiųjų šalių partneriais ir (arba) už ES / EEE ribų.',
      },
      LV: {
        description:
          'Google Analytics sīkfaili ļauj Elfa Distrelec ievākt informāciju par to, kā apmeklētāji izmanto mūsu interneta veikalu. Tie izseko, kad apmeklētājs atver un aizver interneta veikalu, kā arī apmeklētāja skatītās lapas un veiktās darbības.',
        dataShare: 'Šīs sīkdatnes kopīgo informāciju ar mūsu uzticamiem trešās puses partneriem un/vai ārpus ES/EEZ.',
      },
      NL: {
        description:
          "Google Analytics cookies stellen Distrelec in staat om informatie te verzamelen over hoe bezoekers onze Webshop gebruiken. Ze houden bij wanneer een bezoeker de Webwinkel binnenkomt en verlaat, welke pagina's hij bekijkt en welke acties hij uitvoert.",
        dataShare: 'Deze cookie deelt gegevens met onze vertrouwde externe partners en/of buiten de EU/EER.',
      },
      NO: {
        description:
          'Google Analytics-informasjonskapsler tillater Distrelec å samle inn informasjon om hvordan besøkende bruker nettbutikken vår. De holder rede på når en besøkende kommer inn og forlater nettbutikken og hvilke sider de viser og handlinger de utfører.',
        dataShare:
          'Denne informasjonskapselen deler data med våre pålitelige tredjepartsleverandører og/eller utenfor EU/EEA.',
      },
      PL: {
        description:
          'Pliki cookie programu Google Analytics umożliwiają firmie Distrelec zbieranie informacji na temat tego, w jakich celach użytkownicy odwiedzają nasz sklep internetowy. Pliki te śledzą, kiedy odwiedzający wchodzi na stronę naszego sklepu internetowego i kiedy go opuszcza oraz jakie strony odwiedza i jakie przeprowadza czynności.',
        dataShare: 'Ten plik cookie udostępnia dane naszym zaufanym partnerom lub podmiotom spoza UE/EOG.',
      },
      RO: {
        description:
          'Modulele cookie Google Analytics permit Distrelec să colecteze informaţii despre modul în care utilizează magazinul nostru web. Acestea monitorizează momentele în care un vizitator intră sau iese din magazinul web şi paginile pe care aceştia le vizualizează şi acţiunile pe care le efectuează.',
        dataShare: 'Acest modul cookie partajează date cu partenerii noştri terţi de încredere şi/sau în afara UE/SEE.',
      },
      SK: {
        description:
          'Modulele cookie Google Analytics permit Distrelec să colecteze informaţii despre modul în care utilizează magazinul nostru web. Acestea monitorizează momentele în care un vizitator intră sau iese din magazinul web şi paginile pe care aceştia le vizualizează şi acţiunile pe care le efectuează.',
        dataShare:
          'Tento súbor cookie zdieľa údaje s našimi dôveryhodnými partnermi tretích strán a/alebo mimo EÚ/EHP.',
      },
      SV: {
        description:
          'Google Analytics cookies ger Elfa Distrelec möjlighet att samla in information om hur besökare använder vår webbplats. De håller koll på när besökare kommer in och lämnar webbutiken, vilka sidor de tittar på och vilka handlingar de genomför.',
        dataShare: 'Denna cookie delar data med våra betrodda tredjepartspartners och/eller utanför EU/EES.',
      },
      default: {
        description:
          'Google Analytics cookies allow Distrelec to collect information about how visitors use our Webshop. They keep track of when a visitor enters and leaves the Webshop and what pages they view and actions they perform.',
        dataShare: 'This cookie shares data with our trusted third party partners and/or outside the EU/EEA.',
      },
    },
    'Adobe Analytics': {
      EN: {
        description:
          'Adobe Analytics cookies allow Distrelec to collect information about how visitors use our Webshop. They keep track of when a visitor enters and leaves the Webshop and what pages they view and actions they perform.',
        dataShare: 'This cookie shares data with our trusted third party partners and/or outside the EU/EEA.',
      },
      CS: {
        description:
          'Soubory cookie služby Adobe Analytics umožňují společnosti Distrelec shromažďovat informace o tom, jakým způsobem návštěvníci náš internetový obchod využívají. Tyto soubory sledují, kdy návštěvník vstoupí do internetového obchodu a opustí jej, jaké stránky si prohlíží a jaké akce provádí.',
        dataShare:
          'Tento soubor cookie sdílí data s našimi důvěryhodnými partnery z řad třetích stran a /nebo mimo EU/EHP.',
      },
      DK: {
        description:
          'Adobe Analytics-cookies giver Elfa Distrelec mulighed for at indsamle oplysninger om, hvordan besøgende bruger vores webshop. De holder styr på, hvornår en besøgende kommer ind og forlader webshoppen, og hvilke sider de ser og handlinger, de udfører.',
        dataShare: 'Denne cookie deler data med vores betroede tredjepartspartnere og/eller uden for EU/EØS.',
      },
      DE: {
        description:
          'Adobe-Analytics-Cookies ermöglichen es Distrelec, Informationen darüber zu sammeln, wie Besucher unseren Webshop nutzen. Sie verfolgen, wann ein Besucher den Webshop betritt und verlässt und welche Seiten er ansieht und welche Aktionen er ausführt.',
        dataShare:
          'Dieses Cookie teilt Daten mit unseren vertrauenswürdigen Drittpartnern und/oder außerhalb der EU/EWR.',
      },
      EE: {
        description:
          'Adobe Analyticsi küpsised võimaldavad Elfa Distrelecil koguda teavet, kuidas külastajad meie veebipoodi kasutavad. Need jälgivad külastajate veebipoodi sisenemist ja sealt lahkumist ning nende vaadatavaid lehti ja tehtavaid toiminguid.',
        dataShare:
          'See küpsis jagab andmeid meie usaldatud kolmandast isikust partneritega ja/või väljaspool EL-i/EMP-d.',
      },
      FI: {
        description:
          'Adobe Analytics -evästeiden avulla Distrelec voi kerätä tietoja siitä, miten kävijät käyttävät verkkokauppaa. He seuraavat, milloin kävijä tulee verkkokauppaan ja poistuu siitä, mitä sivuja hän tarkastelee ja mitä toimintoja he tekevät.',
        dataShare:
          'Tämä eväste jakaa tietoja yrityksen kumppaneina toimivien luotettavien kolmansien osapuolten kanssa ja/tai EU:n/ETA:n ulkopuolelle.',
      },
      FR: {
        description:
          "Les cookies Adobe Analytics permettent à Distrelec de collecter des informations sur la manière dont les visiteurs utilisent la boutique en ligne. Ils gardent une trace de l'heure à laquelle un visiteur se rend sur la boutique en ligne et quitte celle-ci, ainsi que des pages qu'il a consultées et des actions qu'il a effectuées.",
        dataShare: "Ce cookie partage des données avec nos partenaires tiers de confiance et/ou en dehors de l'UE/EEE.",
      },
      HU: {
        description:
          'Az Adobe Analytics lehetővé teszi a Distrelec számára, hogy információt gyűjtsön a látogatók szokásairól a webáruház használata közben. Nyomon követik, mikor lép be egy látogató a webáruházba és mikor hagyja el azt, illetve, hogy mely oldalakat tekinti meg és milyen műveleteket végez a weboldalon.',
        dataShare: 'Ez a cookie adatokat oszt meg megbízható harmadik fél partnereinkkel és/vagy az EU/EGT-n kívül.',
      },
      IT: {
        description:
          'I cookie di Adobe Analytics consentono a Distrelec di raccogliere informazioni su come i visitatori utilizzano il nostro Webshop. Tengono traccia di quando un visitatore entra ed esce dal Webshop e quali pagine visualizzano e le azioni che eseguono.',
        dataShare:
          "Questo cookie condivide i dati con i nostri partner terze parti fidate e/o al di fuori dell'UE/SEE.",
      },
      LT: {
        description:
          'Adobe Analytics slapukai leidžia Distrelec rinkti informaciją apie tai, kaip lankytojai naudojasi mūsų internetine parduotuve. Jie stebi, kada lankytojas įeina į internetinę parduotuvę ir iš jos išeina, kokius puslapius peržiūri ir kokius veiksmus atlieka.',
        dataShare:
          'Šis slapukas bendrina duomenis su patikimais trečiųjų šalių partneriais ir (arba) už ES / EEE ribų.',
      },
      LV: {
        description:
          'Adobe Analytics sīkfaili ļauj Elfa Distrelec ievākt informāciju par to, kā apmeklētāji izmanto mūsu interneta veikalu. Tie izseko, kad apmeklētājs atver un aizver interneta veikalu, kā arī apmeklētāja skatītās lapas un veiktās darbības.',
        dataShare: 'Šīs sīkdatnes kopīgo informāciju ar mūsu uzticamiem trešās puses partneriem un/vai ārpus ES/EEZ.',
      },
      NL: {
        description:
          "Adobe Analytics cookies stellen Distrelec in staat om informatie te verzamelen over hoe bezoekers onze Webshop gebruiken. Ze houden bij wanneer een bezoeker de Webshop binnenkomt en verlaat, welke pagina's hij bekijkt en welke acties hij uitvoert.",
        dataShare: 'Deze cookie deelt gegevens met onze vertrouwde externe partners en/of buiten de EU/EER.',
      },
      NO: {
        description:
          'Adobe Analytics-informasjonskapsler tillater Elfa Distrelec å samle inn informasjon om hvordan besøkende bruker nettbutikken vår. De holder rede på når en besøkende kommer inn og forlater nettbutikken og hvilke sider de viser og handlinger de utfører.',
        dataShare:
          'Denne informasjonskapselen deler data med våre pålitelige tredjepartsleverandører og/eller utenfor EU/EEA.',
      },
      PL: {
        description:
          'Pliki cookie programu Adobe Analytics umożliwiają firmie Distrelec zbieranie informacji na temat tego, w jakich celach użytkownicy odwiedzają nasz sklep internetowy. Pliki te śledzą, kiedy odwiedzający wchodzi na stronę naszego sklepu internetowego i kiedy go opuszcza oraz jakie strony odwiedza i jakie przeprowadza czynności.',
        dataShare: 'Ten plik cookie udostępnia dane naszym zaufanym partnerom lub podmiotom spoza UE/EOG.',
      },
      RO: {
        description:
          'Modulele cookie Adobe Analytics permit Distrelec să colecteze informaţii despre modul în care vizitatorii utilizează magazinul nostru web. Acestea monitorizează momentele în care un vizitator intră sau iese din magazinul web şi paginile pe care aceştia le vizualizează şi acţiunile pe care le efectuează.',
        dataShare: 'Acest modul cookie partajează date cu partenerii noştri terţi de încredere şi/sau în afara UE/SEE.',
      },
      SK: {
        description:
          'Súbory cookie služby Adobe Analytics umožňujú spoločnosti Distrelec zhromažďovať informácie o tom, ako návštevníci používajú náš internetový obchod. Sledujú, kedy návštevník vstúpi do internetového obchodu a kedy ho opustí, aké stránky zobrazuje a aké akcie vykonáva.',
        dataShare:
          'Tento súbor cookie zdieľa údaje s našimi dôveryhodnými partnermi tretích strán a/alebo mimo EÚ/EHP.',
      },
      SV: {
        description:
          'Adobe Analytics kakor ger Elfa Distrelec möjlighet att samla in information om hur besökare använder vår webbplats. De håller koll på när besökare kommer in och lämnar webbutiken, vilka sidor de tittar på och vilka handlingar de genomför.',
        dataShare: 'Denna cookie delar data med våra betrodda tredjepartspartners och/eller utanför EU/EES.',
      },
      default: {
        description:
          'Adobe Analytics cookies allow Distrelec to collect information about how visitors use our Webshop. They keep track of when a visitor enters and leaves the Webshop and what pages they view and actions they perform.',
        dataShare: 'This cookie shares data with our trusted third party partners and/or outside the EU/EEA.',
      },
    },
    HotJar: {
      EN: {
        description:
          'These cookies allows Distrelec to gather user insight and feedback on how people use our site. This is through on-site polls, surveys and user behaviour tracking.',
        dataShare: 'This cookie shares data with our trusted third party partners and/or outside the EU/EEA.',
      },
      CS: {
        description:
          'Tyto soubory cookie umožňují společnosti Distrelec porozumět uživatelům a získat zpětnou vazbu o tom, jak lidé používají naše webové stránky. To se provádí pomocí hlasování, průzkumů a sledování chování uživatelů na webu.',
        dataShare:
          'Tento soubor cookie sdílí data s našimi důvěryhodnými partnery z řad třetích stran a /nebo mimo EU/EHP.',
      },
      DK: {
        description:
          'Disse cookies giver Elfa Distrelec mulighed for at indsamle brugerindsigt og feedback om, hvordan folk bruger vores websted. Dette sker gennem afstemninger på stedet, undersøgelser og sporing af brugeradfærd.',
        dataShare: 'Denne cookie deler data med vores betroede tredjepartspartnere og/eller uden for EU/EØS.',
      },
      DE: {
        description:
          'Diese Cookies ermöglichen es Distrelec, Einblicke und Rückmeldungen darüber zu erhalten, wie die Besucher unsere Website nutzen. Dies geschieht durch On-Site-Umfragen, Umfragen und die Verfolgung des Nutzerverhaltens.',
        dataShare:
          'Dieses Cookie teilt Daten mit unseren vertrauenswürdigen Drittpartnern und/oder außerhalb der EU/EWR.',
      },
      EE: {
        description:
          'Need küpsised võimaldavad Elfa Distrelecil koguda kasutajateavet ja tagasisidet selle kohta, kuidas inimesed meie saiti kasutavad. Seda tehakse saidisiseste küsitluste, uuringute ja kasutajate käitumise jälgimise kaudu.',
        dataShare:
          'See küpsis jagab andmeid meie usaldatud kolmandast isikust partneritega ja/või väljaspool EL-i/EMP-d.',
      },
      FI: {
        description:
          'Näiden evästeiden avulla Distrelec voi kerätä käyttäjien näkemyksiä ja palautetta siitä, miten ihmiset käyttävät sivustoamme. Tämä tapahtuu paikan päällä tehtävien kyselyjen, kyselyjen ja käyttäjien käyttäytymisen seurannan avulla.',
        dataShare:
          'Tämä eväste jakaa tietoja yrityksen kumppaneina toimivien luotettavien kolmansien osapuolten kanssa ja/tai EU:n/ETA:n ulkopuolelle.',
      },
      FR: {
        description:
          "Ces cookies permettent à Distrelec de recueillir des informations et des commentaires sur l'utilisation du site. Cette action est assurée au travers de sondages sur site, d'enquêtes et du suivi du comportement des utilisateurs.",
        dataShare: "Ce cookie partage des données avec nos partenaires tiers de confiance et/ou en dehors de l'UE/EEE.",
      },
      HU: {
        description:
          'Ezek a cookie-k lehetővé teszik a Distrelec számára, hogy információkat gyűjtsön a felhasználói élményről, illetve arról, hogyan használják a látogatók a weboldalunkat. Ez a weboldalon található kérdőívek, kutatások és a felhasználói viselkedés nyomon követése által történik.',
        dataShare: 'Ez a cookie adatokat oszt meg megbízható harmadik fél partnereinkkel és/vagy az EU/EGT-n kívül.',
      },
      IT: {
        description:
          'Questi cookie consentono a Distrelec di raccogliere informazioni e feedback sugli utenti su come le persone utilizzano il nostro sito. Ciò avviene attraverso sondaggi in loco, sondaggi e monitoraggio del comportamento degli utenti.',
        dataShare:
          "Questo cookie condivide i dati con i nostri partner terze parti fidate e/o al di fuori dell'UE/SEE.",
      },
      LT: {
        description:
          'Šie slapukai leidžia Distrelec rinkti vartotojų įžvalgas ir atsiliepimus apie tai, kaip žmonės naudojasi mūsų svetaine. Tai atliekama apklausų vietoje, apklausų ir vartotojų elgsenos stebėjimo būdu.',
        dataShare:
          'Šis slapukas bendrina duomenis su patikimais trečiųjų šalių partneriais ir (arba) už ES / EEE ribų.',
      },
      LV: {
        description:
          'Šie sīkfaili ļauj Elfa Distrelec ievākt lietotāju ieskatus un atsauksmes par vietnes lietojumu. Tas tiek darīts, izmantojot vietnē izvietotas aptaujas, pētījumus un lietotāju rīcības izsekošanu.',
        dataShare: 'Šīs sīkdatnes kopīgo informāciju ar mūsu uzticamiem trešās puses partneriem un/vai ārpus ES/EEZ.',
      },
      NL: {
        description:
          'Deze cookies laten Distrelec toe om gebruikersinzichten en feedback te verzamelen over hoe mensen onze site gebruiken. Dit gebeurt via on-site polls, enquêtes en tracking van gebruikersgedrag.',
        dataShare: 'Deze cookie deelt gegevens met onze vertrouwde externe partners en/of buiten de EU/EER.',
      },
      NO: {
        description:
          'Disse informasjonskapslene gjør det mulig for Distrelec å samle brukerinnsikt og tilbakemeldinger på hvordan folk bruker nettstedet vårt. Dette skjer gjennom avstemninger på stedet, undersøkelser og sporing av brukeratferd.',
        dataShare:
          'Denne informasjonskapselen deler data med våre pålitelige tredjepartsleverandører og/eller utenfor EU/EEA.',
      },
      PL: {
        description:
          'Te pliki cookie umożliwiają firmie Distrelec gromadzenie informacji o użytkowniku oraz śledzenie, w jaki sposób używana jest nasza strona. Dzieje się tak dzięki ankietom na stronie oraz śledzeniu działań użytkownika.',
        dataShare: 'Ten plik cookie udostępnia dane naszym zaufanym partnerom lub podmiotom spoza UE/EOG.',
      },
      RO: {
        description:
          'Aceste module cookie permit Distrelec să colecteze informaţii şi feedback despre modul în care persoanele utilizează site-ul nostru. Acest lucru se realizează prin sondaje cu voturi, sondaje de opinie pe site şi monitorizarea comportamentului.',
        dataShare: 'Acest modul cookie partajează date cu partenerii noştri terţi de încredere şi/sau în afara UE/SEE.',
      },
      SK: {
        description:
          'Tieto súbory cookie umožňujú spoločnosti Distrelec zhromažďovať informácie o používateľoch a získať spätnú väzbu, ako ľudia používajú našu lokalitu. Informácie získavame prostredníctvom prieskumov na lokalite, štatistík a na základe sledovania správania používateľov.',
        dataShare:
          'Tento súbor cookie zdieľa údaje s našimi dôveryhodnými partnermi tretích strán a/alebo mimo EÚ/EHP.',
      },
      SV: {
        description:
          'Dessa kakor gör det möjligt för Elfa Distrelec att ge användarinsikt och feedback om hur människor använder vår webbplats. Det görs via opinionsmätning på sajten, enkäter och spårning av användarbeteende.',
        dataShare: 'Denna cookie delar data med våra betrodda tredjepartspartners och/eller utanför EU/EES.',
      },
      default: {
        description:
          'These cookies allows Distrelec to gather user insight and feedback on how people use our site. This is through on-site polls, surveys and user behaviour tracking.',
        dataShare: 'This cookie shares data with our trusted third party partners and/or outside the EU/EEA.',
      },
    },
    Productsup: {
      EN: {
        description:
          "Productsup cookies are data stored by the Internet browser on the user's computer system. The cookies can be transmitted to a page when they are accessed and thus allow an assignment of the user. Cookies help to simplify the use of websites for users.",
        dataShare: 'This cookie shares data with our trusted third party partners and/or outside the EU/EEA.',
      },
      CS: {
        description:
          'Soubory cookie Productsup jsou data uložená v internetovém prohlížeči počítačového systému uživatele. Tyto soubory cookie lze při přístupu k nim přenášet na stránku a umožnit tak přiřazení uživatele. Soubory cookie pomáhají zjednodušit používání webových stránek pro uživatele.',
        dataShare:
          'Tento soubor cookie sdílí data s našimi důvěryhodnými partnery z řad třetích stran a /nebo mimo EU/EHP.',
      },
      DK: {
        description:
          'Productsup-cookies er data gemt af internetbrowseren på brugerens computersystem. Cookies kan overføres til en side, når de er tilgængelige, og således tillade en tildeling af brugeren. Cookies hjælper med at forenkle brugen af ​​websteder for brugerne.',
        dataShare: 'Denne cookie deler data med vores betroede tredjepartspartnere og/eller uden for EU/EØS.',
      },
      DE: {
        description:
          'Productsup-Cookies sind Daten, die vom Internetbrowser auf dem Computersystem des Benutzers gespeichert werden. Die Cookies können beim Zugriff auf eine Seite übermittelt werden und ermöglichen so eine Zuordnung des Nutzers. Cookies helfen, die Nutzung von Websites für den Benutzer zu vereinfachen.',
        dataShare:
          'Dieses Cookie teilt Daten mit unseren vertrauenswürdigen Drittpartnern und/oder außerhalb der EU/EWR.',
      },
      EE: {
        description:
          'Productsup küpsised on andmed, mida internetibrauser salvestab kasutaja arvutisüsteemi. Küpsiseid saab edastada leheküljele, kui neid kasutatakse. Küpsised aitavad lihtsustada kasutajate jaoks veebisaitide kasutamist.',
        dataShare:
          'See küpsis jagab andmeid meie usaldatud kolmandast isikust partneritega ja/või väljaspool EL-i/EMP-d.',
      },
      FI: {
        description:
          'Tuotekohtaiset evästeet ovat tietoja, jotka Internet-selain tallentaa käyttäjän tietokonejärjestelmään. Evästeet voidaan lähettää sivulle, kun niitä käytetään, jolloin käyttäjät voivat määrittää ne. Evästeet auttavat yksinkertaistamaan verkkosivustojen käyttöä käyttäjille.',
        dataShare:
          'Tämä eväste jakaa tietoja yrityksen kumppaneina toimivien luotettavien kolmansien osapuolten kanssa ja/tai EU:n/ETA:n ulkopuolelle.',
      },
      FR: {
        description:
          "Les cookies Productsup sont des données stockées par le navigateur Internet sur le système informatique de l'utilisateur. Les cookies peuvent être transmis à une page lorsqu'elle est consultée et permettent ainsi une attribution de l'utilisateur. Les cookies permettent de simplifier l'utilisation des sites web pour les utilisateurs.",
        dataShare: "Ce cookie partage des données avec nos partenaires tiers de confiance et/ou en dehors de l'UE/EEE.",
      },
      HU: {
        description:
          'A Productsup cookie-k a felhasználó számítógépes rendszerén az internetböngésző által tárolt adatok. A cookie-k akkor továbbíthatók egy oldalra, amikor azok elérhetőek, és így lehetővé teszik a felhasználói hozzárendelést. A cookie-k segítenek egyszerűsíteni a weboldalak használatát a felhasználók számára.',
        dataShare: 'Ez a cookie adatokat oszt meg megbízható harmadik fél partnereinkkel és/vagy az EU/EGT-n kívül.',
      },
      IT: {
        description:
          "I cookie di Productsup sono dati memorizzati dal browser Internet sul sistema informatico dell'utente. I cookie possono essere trasmessi ad una pagina quando vi si accede e quindi consentono un'assegnazione dell'utente. I cookie aiutano a semplificare l'utilizzo dei siti web per gli utenti.",
        dataShare:
          "Questo cookie condivide i dati con i nostri partner terze parti fidate e/o al di fuori dell'UE/SEE.",
      },
      LT: {
        description:
          'Productsup slapukai yra duomenys, kuriuos interneto naršyklė saugo vartotojo kompiuterio sistemoje. Slapukai gali būti perduodami į puslapį, kai jie pasiekiami, ir tokiu būdu leidžia vartotojui priskirti. Slapukai padeda supaprastinti svetainių naudojimą vartotojams.',
        dataShare:
          'Šis slapukas bendrina duomenis su patikimais trečiųjų šalių partneriais ir (arba) už ES / EEE ribų.',
      },
      LV: {
        description:
          'Productsup sīkfaili ir dati, ko interneta pārlūkprogramma saglabā lietotāja datorsistēmā. Sīkdatnes var tikt pārsūtītas uz lapu, kad tai tiek piekļūt. Sīkdatnes palīdz lietotājiem vienkāršot tīmekļa vietņu lietošanu.',
        dataShare: 'Šīs sīkdatnes kopīgo informāciju ar mūsu uzticamiem trešās puses partneriem un/vai ārpus ES/EEZ.',
      },
      NL: {
        description:
          'Productsup cookies zijn gegevens die door de internetbrowser op het computersysteem van de gebruiker worden opgeslagen. De cookies kunnen bij het oproepen van een pagina worden doorgestuurd en maken zo een toewijzing van de gebruiker mogelijk. Cookies helpen om het gebruik van websites voor gebruikers te vereenvoudigen.',
        dataShare: 'Deze cookie deelt gegevens met onze vertrouwde externe partners en/of buiten de EU/EER.',
      },
      NO: {
        description:
          'Productsup-informasjonskapsler er data lagret av nettleseren på brukerens datasystem. Informasjonskapslene kan overføres til en side når de er tilgjengelige, og dermed tillate en tildeling av brukeren. Cookies hjelper til med å forenkle bruken av nettsteder for brukerne.',
        dataShare:
          'Denne informasjonskapselen deler data med våre pålitelige tredjepartsleverandører og/eller utenfor EU/EEA.',
      },
      PL: {
        description:
          'Pliki cookie productsup to dane przechowywane przez przeglądarkę internetową w systemie komputerowym użytkownika. Pliki cookie mogą być przesyłane na stronę, gdy są one dostępne, a tym samym umożliwiają przypisanie użytkownika. Pliki cookie pomagają ułatwić użytkownikom korzystanie ze stron internetowych.',
        dataShare: 'Ten plik cookie udostępnia dane naszym zaufanym partnerom lub podmiotom spoza UE/EOG.',
      },
      RO: {
        description:
          'Modulele Productsup sunt date stocate de browserul de internet în sistemul informatic al utilizatorului. Modulele cookie pot fi transmise unei pagini atunci sunt accesate, permiţând astfel o alocare a utilizatorului. Modulele cookie ajută la simplificarea utilizării website-urilor pentru utilizatori.',
        dataShare: 'Acest modul cookie partajează date cu partenerii noştri terţi de încredere şi/sau în afara UE/SEE.',
      },
      SK: {
        description:
          'Súbory cookie spoločnosti Productsup sú údaje uložené internetovým prehliadačom v počítačovom systéme používateľa. Po prístupe k týmto súborom cookie sa tieto súbory cookie môžu prenášať na stránku, a tak umožňujú priradenie používateľa. Súbory cookie pomáhajú používateľom zjednodušiť používanie webových lokalít.',
        dataShare:
          'Tento súbor cookie zdieľa údaje s našimi dôveryhodnými partnermi tretích strán a/alebo mimo EÚ/EHP.',
      },
      SV: {
        description:
          'Productsup-kakor är data som lagras av webbläsaren i användarens datorsystem. Kakorna kan överföras till en sida när de öppnas och därmed tillåta användaren tilldelning. kakor hjälper till att förenkla användningen av webbplatser för användaren.',
        dataShare: 'Denna cookie delar data med våra betrodda tredjepartspartners och/eller utanför EU/EES.',
      },
      default: {
        description:
          "Productsup cookies are data stored by the Internet browser on the user's computer system. The cookies can be transmitted to a page when they are accessed and thus allow an assignment of the user. Cookies help to simplify the use of websites for users.",
        dataShare: 'This cookie shares data with our trusted third party partners and/or outside the EU/EEA.',
      },
    },
    'Google Apps': {
      EN: {
        description:
          'Google Apps cookies are used to maintain a user’s preferences, to maintain and enhance a user’s experience during a specific browsing session and to improve the performance of Google services.',
        dataShare: 'This cookie shares data with our trusted third party partners and/or outside the EU/EEA.',
      },
      CS: {
        description:
          'Soubory cookie služby Google Apps se používají pro uchování preferencí uživatele, zachování a zlepšování uživatelského prostředí během dané relace procházení a ke zlepšování výkonu služeb Google.',
        dataShare:
          'Tento soubor cookie sdílí data s našimi důvěryhodnými partnery z řad třetích stran a /nebo mimo EU/EHP.',
      },
      DK: {
        description:
          'Google Apps-cookies bruges til at opretholde en brugers præferencer, til at vedligeholde og forbedre en brugers oplevelse under en bestemt browsersession og til at forbedre ydeevnen for Google-tjenester.',
        dataShare: 'Denne cookie deler data med vores betroede tredjepartspartnere og/eller uden for EU/EØS.',
      },
      DE: {
        description:
          'Google Apps-Cookies werden verwendet, um die Einstellungen eines Nutzers zu erhalten, um die Erfahrung eines Nutzers während einer bestimmten Browsing-Sitzung zu erhalten und zu verbessern und um die Leistung der Google-Dienste zu verbessern.',
        dataShare:
          'Dieses Cookie teilt Daten mit unseren vertrauenswürdigen Drittpartnern und/oder außerhalb der EU/EWR.',
      },
      EE: {
        description:
          "Google Apps'i küpsiseid kasutatakse kasutaja eelistuste säilitamiseks, kasutaja kogemuse parandamiseks konkreetse sirvimissessiooni ajal ning Google'i teenuste jõudluse parandamiseks.",
        dataShare:
          'See küpsis jagab andmeid meie usaldatud kolmandast isikust partneritega ja/või väljaspool EL-i/EMP-d.',
      },
      FI: {
        description:
          'Google Apps -evästeitä käytetään ylläpitämään käyttäjän mieltymyksiä, ylläpitämään ja parantamaan käyttäjän kokemusta tietyn selausistunnon aikana ja parantamaan Google-palveluiden suorituskykyä.',
        dataShare:
          'Tämä eväste jakaa tietoja yrityksen kumppaneina toimivien luotettavien kolmansien osapuolten kanssa ja/tai EU:n/ETA:n ulkopuolelle.',
      },
      FR: {
        description:
          "Les cookies Google Apps sont utilisés pour conserver les préférences d'un utilisateur, pour maintenir et améliorer l'expérience d'un utilisateur au cours d'une session de navigation spécifique et pour améliorer les performances des services Google.",
        dataShare: "Ce cookie partage des données avec nos partenaires tiers de confiance et/ou en dehors de l'UE/EEE.",
      },
      HU: {
        description:
          'A Google Apps cookie-k a felhasználó preferenciáinak megtartására, a felhasználói élmény fenntartására és javítására szolgálnak egy adott böngészési munkamenet során, valamint a Google-szolgáltatások teljesítményének javítását is elősegítik.',
        dataShare: 'Ez a cookie adatokat oszt meg megbízható harmadik fél partnereinkkel és/vagy az EU/EGT-n kívül.',
      },
      IT: {
        description:
          "I cookie di Google Apps vengono utilizzati per mantenere le preferenze di un utente, per mantenere e migliorare l'esperienza di un utente durante una specifica sessione di navigazione e per migliorare le prestazioni dei servizi Google.",
        dataShare:
          "Questo cookie condivide i dati con i nostri partner terze parti fidate e/o al di fuori dell'UE/SEE.",
      },
      LT: {
        description:
          'Google Apps slapukai naudojami palaikyti vartotojo pageidavimus, palaikyti ir pagerinti vartotojo patirtį tam tikros naršymo sesijos metu ir pagerinti Google paslaugų našumą.',
        dataShare:
          'Šis slapukas bendrina duomenis su patikimais trečiųjų šalių partneriais ir (arba) už ES / EEE ribų.',
      },
      LV: {
        description:
          'Google Apps sīkfaili tiek izmantoti, lai saglabātu lietotāja preferences, uzturētu un uzlabotu lietotāja pieredzi konkrētas pārlūkošanas sesijas laikā un uzlabotu Google pakalpojumu veiktspēju.',
        dataShare: 'Šīs sīkdatnes kopīgo informāciju ar mūsu uzticamiem trešās puses partneriem un/vai ārpus ES/EEZ.',
      },
      NL: {
        description:
          'Google Apps cookies worden gebruikt om de voorkeuren van een gebruiker te behouden, om de ervaring van een gebruiker tijdens een specifieke browsersessie te behouden en te verbeteren en om de prestaties van Google-diensten te verbeteren.',
        dataShare: 'Deze cookie deelt gegevens met onze vertrouwde externe partners en/of buiten de EU/EER.',
      },
      NO: {
        description:
          'Google Apps-informasjonskapsler brukes til å opprettholde en brukers preferanser, for å opprettholde og forbedre brukeropplevelsen under en spesifikk nettlesingsøkt og for å forbedre ytelsen til Google-tjenester.',
        dataShare:
          'Denne informasjonskapselen deler data med våre pålitelige tredjepartsleverandører og/eller utenfor EU/EEA.',
      },
      PL: {
        description:
          'Pliki cookie Google Apps są używane do zachowywania preferencji użytkownika, utrzymywania i poprawiania jakości podczas określonej sesji przeglądania oraz udoskonalania usług Google.',
        dataShare: 'Ten plik cookie udostępnia dane naszym zaufanym partnerom lub podmiotom spoza UE/EOG.',
      },
      RO: {
        description:
          'Modulele cookie Google Apps sunt utilizate pentru a menţine preferinţele unui utilizator, pentru a menţine şi a îmbunătăţi experienţa unui utilizator în timpul unei anumite sesiuni de navigare şi pentru a îmbunătăţi performanţa serviciilor Google.',
        dataShare: 'Acest modul cookie partajează date cu partenerii noştri terţi de încredere şi/sau în afara UE/SEE.',
      },
      SK: {
        description:
          'Súbory cookie služby Google Apps sa používajú na zachovanie preferencií používateľa, na udržanie a zlepšenie skúsenosti používateľa počas konkrétnej relácie prehliadania a na zlepšenie výkonnosti služieb Google.',
        dataShare:
          'Tento súbor cookie zdieľa údaje s našimi dôveryhodnými partnermi tretích strán a/alebo mimo EÚ/EHP.',
      },
      SV: {
        description:
          'Google Apps-kakor används för att upprätthålla en användares preferenser, upprätthålla och förbättra användarens upplevelse under en specifik surfningssession och för att förbättra prestanda för Googles tjänster.',
        dataShare: 'Denna cookie delar data med våra betrodda tredjepartspartners och/eller utanför EU/EES.',
      },
      default: {
        description:
          'Google Apps cookies are used to maintain a user’s preferences, to maintain and enhance a user’s experience during a specific browsing session and to improve the performance of Google services.',
        dataShare: 'This cookie shares data with our trusted third party partners and/or outside the EU/EEA.',
      },
    },
    Optimizely: {
      EN: {
        description:
          'Optimizely is a tool used by Distrelec for A/B testing to optimise our website content for improved user experience.',
        dataShare: 'This cookie shares data with our trusted third party partners and/or outside the EU/EEA.',
      },
      CS: {
        description:
          'Optimizely je nástroj, který společnost Distrelec používá pro A/B testování s cílem optimalizovat obsah našich webových stránek pro lepší uživatelskou zkušenost.',
        dataShare:
          'Tento soubor cookie sdílí data s našimi důvěryhodnými partnery z řad třetích stran a /nebo mimo EU/EHP.',
      },
      DK: {
        description:
          'Optimizely er et værktøj, der anvendes af Distrelec til A/B-testning for at optimere hjemmesideindhold for en forbedret brugeroplevelse.',
        dataShare: 'Denne cookie deler data med vores betroede tredjepartspartnere og/eller uden for EU/EØS.',
      },
      DE: {
        description:
          'Optimizely wird von Distrelec für A/B-Tests verwendet, um den Webshop für eine bessere Benutzererfahrung zu optimieren.',
        dataShare:
          'Dieses Cookie teilt Daten mit unseren vertrauenswürdigen Drittpartnern und/oder außerhalb der EU/EWR.',
      },
      EE: {
        description:
          'Optimizely on tööriist, mida kasutab Distrelec A/B testimiseks, et optimeerida veebilehe sisu, et parandada seeläbi kasutajakogemust.',
        dataShare:
          'See küpsis jagab andmeid meie usaldatud kolmandast isikust partneritega ja/või väljaspool EL-i/EMP-d.',
      },
      FI: {
        description:
          'Optimizely on Elfa Distrelecin A/B-testaukseen käyttämä työkalu, jolla optimoidaan verkkosivustomme sisältöä käyttäjäkokemuksen parantamiseksi.',
        dataShare:
          'Tämä eväste jakaa tietoja yrityksen kumppaneina toimivien luotettavien kolmansien osapuolten kanssa ja/tai EU:n/ETA:n ulkopuolelle.',
      },
      FR: {
        description:
          "Optimizely est un outil utilisé par Distrelec pour les tests A/B afin d'optimiser le contenu de notre site web pour une meilleure expérience utilisateur.",
        dataShare: "Ce cookie partage des données avec nos partenaires tiers de confiance et/ou en dehors de l'UE/EEE.",
      },
      HU: {
        description:
          'Az Optimizely a Distrelec által A/B teszteléshez használt eszköz, amellyel optimalizáljuk weboldalunk tartalmát a jobb felhasználói élmény érdekében.',
        dataShare: 'Ez a cookie adatokat oszt meg megbízható harmadik fél partnereinkkel és/vagy az EU/EGT-n kívül.',
      },
      IT: {
        description:
          "Optimizely è uno strumento impiegato da Distrelec per condurre test A/B, al fine di ottimizzare i contenuti del nostro sito web e migliorare l'esperienza dell'utente.",
        dataShare:
          "Questo cookie condivide i dati con i nostri partner terze parti fidate e/o al di fuori dell'UE/SEE.",
      },
      LT: {
        description:
          '„Optimizely“ yra įrankis, kurį „Distrelec“ naudoja A/B testavimui, kad optimizuotų mūsų svetainės turinį ir pagerintų vartotojo patirtį.',
        dataShare:
          'Šis slapukas bendrina duomenis su patikimais trečiųjų šalių partneriais ir (arba) už ES / EEE ribų.',
      },
      LV: {
        description:
          'Optimizely ir rīks, ko Elfa Distrelec izmanto A/B testēšanai, lai optimizētu mūsu vietnes saturu un uzlabotu lietotāja pieredzi.',
        dataShare: 'Šīs sīkdatnes kopīgo informāciju ar mūsu uzticamiem trešās puses partneriem un/vai ārpus ES/EEZ.',
      },
      NL: {
        description:
          'Optimizely is een tool die door Distrelec wordt gebruikt voor A/B-testen om de inhoud van onze website te optimaliseren voor een betere gebruikerservaring.',
        dataShare: 'Deze cookie deelt gegevens met onze vertrouwde externe partners en/of buiten de EU/EER.',
      },
      NO: {
        description:
          'Optimizely er et verktøy Elfa Distrelec bruker for A/B-testing for å optimalisere innholdet på vårt nettsted for en forbedret brukeropplevelse.',
        dataShare:
          'Denne informasjonskapselen deler data med våre pålitelige tredjepartsleverandører og/eller utenfor EU/EEA.',
      },
      PL: {
        description:
          'Optimizely to narzędzie, wykorzystywane przez Elfę Distrelec do przeprowadzania testów A/B służących optymalizacji zawartości naszej strony internetowej z myślą o poprawie jakości usług.',
        dataShare: 'Ten plik cookie udostępnia dane naszym zaufanym partnerom lub podmiotom spoza UE/EOG.',
      },
      RO: {
        description:
          'Optimizely este un instrument folosit de Distrelec pentru testarea A/B în vederea optimizării conținutului site-ului nostru web pentru îmbunătățirea experienței utilizatorilor.',
        dataShare: 'Acest modul cookie partajează date cu partenerii noştri terţi de încredere şi/sau în afara UE/SEE.',
      },
      SK: {
        description:
          'Optimizely je nástroj používaný spoločnosťou Distrelec pre A/B testovanie na optimalizáciu obsahu našej webovej stránky na zlepšenie skúseností používateľa.',
        dataShare:
          'Tento súbor cookie zdieľa údaje s našimi dôveryhodnými partnermi tretích strán a/alebo mimo EÚ/EHP.',
      },
      SV: {
        description:
          'Optimizely är ett verktyg som används av Elfa Distrelec till A/B-tester för att optimera innehållet på vår webbplats för förbättrad användarupplevelse.',
        dataShare: 'Denna cookie delar data med våra betrodda tredjepartspartners och/eller utanför EU/EES.',
      },
      default: {
        description:
          'Optimizely is a tool used by Distrelec for A/B testing to optimise our website content for improved user experience. ',
        dataShare: 'This cookie shares data with our trusted third party partners and/or outside the EU/EEA.',
      },
    },
    'Microsoft Clarity': {
      EN: {
        description:
          'It helps Distrelec understand how users view and use our website across all modern devices and browsers and also gather user insight and feedback on how people use our site. These cookies will expire after one year.',
        dataShare: 'This cookie shares data with our trusted third party partners and/or outside the EU/EEA.',
      },
      CS: {
        description:
          'Pomáhá společnosti Distrelec pochopit způsob, kterým si uživatelé prohlížejí a používají naše webové stránky ve všech moderních zařízeních a prohlížečích, a také získávat informace o uživatelích a zpětnou vazbu ohledně toho, jak lidé naše stránky používají. Platnost těchto souborů cookie je jeden rok.',
        dataShare:
          'Tento soubor cookie sdílí data s našimi důvěryhodnými partnery z řad třetích stran a /nebo mimo EU/EHP.',
      },
      DK: {
        description:
          'Det hjælper Elfa Distrelec med at forstå, hvordan brugerne ser og bruger vores websted på tværs af alle moderne enheder og browsere og samler også brugerindsigt og feedback om, hvordan folk bruger vores websted. Disse cookies udløber efter et år.',
        dataShare: 'Denne cookie deler data med vores betroede tredjepartspartnere og/eller uden for EU/EØS.',
      },
      DE: {
        description:
          'Diese Cookies helfen Distrelec zu verstehen, wie Benutzer unsere Website auf allen modernen Geräten und Browsern betrachten und verwenden, und sie helfen uns auch dabei, Einblicke und Rückmeldungen darüber zu erhalten, wie die Benutzer unsere Website nutzen. Diese Cookies verfallen nach einem Jahr.',
        dataShare:
          'Dieses Cookie teilt Daten mit unseren vertrauenswürdigen Drittpartnern und/oder außerhalb der EU/EWR.',
      },
      EE: {
        description:
          'Need küpsised aitavad Distrelecil mõista, kuidas kasutajad meie veebisaiti erinevates seadmetes ja brauserites vaatavad ja kasutavad, ning koguda kasutajate arvamusi ja tagasisidet veebisaidi kasutamise kohta. Neid küpsiseid säilitatakse üks aasta.',
        dataShare:
          'See küpsis jagab andmeid meie usaldatud kolmandast isikust partneritega ja/või väljaspool EL-i/EMP-d.',
      },
      FI: {
        description:
          'Se auttaa Distreleciä ymmärtämään, miten käyttäjät tarkastelevat verkkosivustoamme ja käyttävät sitä kaikilla nykyaikaisilla laitteilla ja selaimilla, sekä keräämään käyttäjille tietoa ja palautetta siitä, miten ihmiset käyttävät sivustoamme. Nämä evästeet vanhenevat vuoden kuluttua.',
        dataShare:
          'Tämä eväste jakaa tietoja yrityksen kumppaneina toimivien luotettavien kolmansien osapuolten kanssa ja/tai EU:n/ETA:n ulkopuolelle.',
      },
      FR: {
        description:
          'Il permet à Distrelec de comprendre comment les utilisateurs visualisent et utilisent notre site Web sur tous les appareils et navigateurs modernes et de recueillir des informations sur la façon dont les gens utilisent notre site. Ces cookies expireront après un an.',
        dataShare: "Ce cookie partage des données avec nos partenaires tiers de confiance et/ou en dehors de l'UE/EEE.",
      },
      HU: {
        description:
          'Segít a Distrelec-nek megérteni, hogy a felhasználók hogyan jelenítik meg és használják a weboldalt a modern eszközökön és böngészőkben, valamint felhasználói adatokat és visszajelzést gyűjt a weboldalunk használatának módjáról. Ezek a sütik egy év múlva lejárnak.',
        dataShare: 'Ez a cookie adatokat oszt meg megbízható harmadik fél partnereinkkel és/vagy az EU/EGT-n kívül.',
      },
      IT: {
        description:
          'Aiuta Distrelec a capire come gli utenti visualizzano e utilizzano il nostro sito Web su tutti i dispositivi e browser moderni e raccoglie anche informazioni e feedback sugli utenti su come le persone utilizzano il nostro sito. Questi cookie scadranno dopo un anno.',
        dataShare:
          "Questo cookie condivide i dati con i nostri partner terze parti fidate e/o al di fuori dell'UE/SEE.",
      },
      LT: {
        description:
          'Tai padeda Distrelec suprasti, kaip vartotojai peržiūri ir naudoja mūsų svetainę visuose šiuolaikiniuose įrenginiuose ir naršyklėse, taip pat rinkti vartotojų įžvalgas ir atsiliepimus apie tai, kaip žmonės naudojasi mūsų svetaine. Šie slapukai nustos galioti po vienerių metų.',
        dataShare:
          'Šis slapukas bendrina duomenis su patikimais trečiųjų šalių partneriais ir (arba) už ES / EEE ribų.',
      },
      LV: {
        description:
          'Šie sīkfaili palīdz Distrelec saprast, kā lietotāji skatās un izmanto mūsu vietni dažādās ierīcēs un pārlūkprogrammās, kā arī apkopot lietotāju viedokli un atsauksmes par vietnes izmantošanu. Šie sīkfaili tiek uzglabāti gadu.',
        dataShare: 'Šīs sīkdatnes kopīgo informāciju ar mūsu uzticamiem trešās puses partneriem un/vai ārpus ES/EEZ.',
      },
      NL: {
        description:
          'Ze helpen Distrelec te begrijpen hoe gebruikers onze website bekijken en gebruiken op alle moderne toestellen en browsers en ook inzicht te verzamelen in de gebruikers en feedback te krijgen over hoe mensen onze site gebruiken. Deze cookies vervallen na één jaar.',
        dataShare: 'Deze cookie deelt gegevens met onze vertrouwde externe partners en/of buiten de EU/EER.',
      },
      NO: {
        description:
          'Det hjelper Elfa Distrelec å forstå hvordan brukere ser på og bruker nettstedet vårt på tvers av alle moderne enheter og nettlesere, og samler også brukerinnsikt og tilbakemeldinger på hvordan folk bruker nettstedet vårt. Disse informasjonskapslene utløper etter ett år.',
        dataShare:
          'Denne informasjonskapselen deler data med våre pålitelige tredjepartsleverandører og/eller utenfor EU/EEA.',
      },
      PL: {
        description:
          'Wskazują firmie Distrelec, w jaki sposób użytkownicy mogą przeglądać i korzystać ze trony internetowej we wszystkich nowoczesnych urządzeniach i przeglądarkach, a także gromadzą informacje o użytkownikach i opinie na temat korzystania z witryny. Te pliki cookie wygasają po upływie jednego roku.',
        dataShare: 'Ten plik cookie udostępnia dane naszym zaufanym partnerom lub podmiotom spoza UE/EOG.',
      },
      RO: {
        description:
          'Acesta ajută Distrelec să înţeleagă modul în care utilizatorii văd şi utilizează site-ul nostru web pe toate dispozitivele şi browserele moderne şi, de asemenea, să adune informaţii despre utilizatori şi feedback despre modul în care oamenii folosesc site-ul nostru. Aceste module cookie vor expira după un an.',
        dataShare: 'Acest modul cookie partajează date cu partenerii noştri terţi de încredere şi/sau în afara UE/SEE.',
      },
      SK: {
        description:
          'Pomáha spoločnosti Distrelec pochopiť, ako si používatelia prezerajú a používajú našu webovú lokalitu vo všetkých moderných zariadeniach a prehliadačoch, a tiež zhromažďovať informácie o používateľoch a získať ich spätnú väzbu, ako ľudia používajú našu lokalitu. Platnosť týchto súborov cookie uplynie po jednom roku.',
        dataShare:
          'Tento súbor cookie zdieľa údaje s našimi dôveryhodnými partnermi tretích strán a/alebo mimo EÚ/EHP.',
      },
      SV: {
        description:
          'Hjälper Elfa Distrelec att förstå hur användare ser och använder vår webbplats på alla moderna enheter och webbläsare, och ger även användarinsikt och feedback om hur människor använder vår webbplats. Dessa kakor upphör efter ett år.',
        dataShare: 'Denna cookie delar data med våra betrodda tredjepartspartners och/eller utanför EU/EES.',
      },
      default: {
        description:
          'It helps Distrelec understand how users view and use our website across all modern devices and browsers and also gather user insight and feedback on how people use our site. These cookies will expire after one year.',
        dataShare: 'This cookie shares data with our trusted third party partners and/or outside the EU/EEA.',
      },
    },
  },
  Marketing: {
    Vimeo: {
      EN: {
        description:
          'Vimeo OTT sets first-party, essential cookies to enable certain features and remember your preferences. For example, cookies keep you logged in, allow you to purchase items, and maintain your language and volume settings.',
        dataShare: 'This cookie shares data with our trusted third party partners and/or outside the EU/EEA.',
      },
      CS: {
        description:
          'Vimeo OTT nastavuje základní soubory cookie první strany pro aktivaci určitých funkcí a uložení vašich preferencí. Soubory cookie například zajišťují vaše trvalé přihlášení, umožňují nakupovat položky a uchovávají nastavení jazyka a hlasitosti.',
        dataShare:
          'Tento soubor cookie sdílí data s našimi důvěryhodnými partnery z řad třetích stran a /nebo mimo EU/EHP.',
      },
      DK: {
        description:
          'Vimeo OTT indstiller førsteparts, vigtige cookies for at aktivere bestemte funktioner og huske dine præferencer. For eksempel holder cookies dig logget ind, giver dig mulighed for at købe varer og vedligeholde dine sprog- og lydstyrkeindstillinger.',
        dataShare: 'Denne cookie deler data med vores betroede tredjepartspartnere og/eller uden for EU/EØS.',
      },
      DE: {
        description:
          'Vimeo OTT setzt unverzichtbare Cookies von Erstanbietern, um bestimmte Funktionen zu ermöglichen und sich Ihre Einstellungen zu merken. Cookies sorgen beispielsweise dafür, dass Sie eingeloggt bleiben, dass Sie Artikel kaufen können und dass Ihre Sprach- und Lautstärkeeinstellungen erhalten bleiben.',
        dataShare:
          'Dieses Cookie teilt Daten mit unseren vertrauenswürdigen Drittpartnern und/oder außerhalb der EU/EWR.',
      },
      EE: {
        description:
          'Vimeo OTT seab esimese osapoolega seotud, olulised küpsised, et võimaldada teatud funktsioone ja jätta meelde teie eelistused. Näiteks hoiavad küpsised teid sisse logitud, võimaldavad teil osta tooteid ning säilitada keele sätted.',
        dataShare:
          'See küpsis jagab andmeid meie usaldatud kolmandast isikust partneritega ja/või väljaspool EL-i/EMP-d.',
      },
      FI: {
        description:
          'Vimeo OTT asettaa ensimmäisen osapuolen välttämättömät evästeet tiettyjen ominaisuuksien ottamiseksi käyttöön ja muistamaan mieltymyksesi. Esimerkiksi evästeet pitävät sinut kirjautuneena sisään, antavat sinun ostaa tuotteita ja ylläpitävät kieli- ja äänenvoimakkuuden asetuksia.',
        dataShare:
          'Tämä eväste jakaa tietoja yrityksen kumppaneina toimivien luotettavien kolmansien osapuolten kanssa ja/tai EU:n/ETA:n ulkopuolelle.',
      },
      FR: {
        description:
          "Vimeo OTT définit des cookies essentiels de première partie pour permettre certaines fonctionnalités et se souvenir de vos préférences. Par exemple, les cookies vous permettent de vous connecter, d'acheter des articles et de conserver vos paramètres de langue et de niveau sonore.",
        dataShare: "Ce cookie partage des données avec nos partenaires tiers de confiance et/ou en dehors de l'UE/EEE.",
      },
      HU: {
        description:
          'A Vimeo OTT az első féllel kapcsolatos, alapvető cookie-k esetében engedélyez bizonyos funkciókat, továbbá megjegyzi a felhasználó preferenciáit. A cookie-k például bejelentkezve tartják Önt, lehetővé teszik dolgok megvásárlását, valamint a nyelv- és hangerő-beállításokat is jegyzik.',
        dataShare: 'Ez a cookie adatokat oszt meg megbízható harmadik fél partnereinkkel és/vagy az EU/EGT-n kívül.',
      },
      IT: {
        description:
          'Vimeo OTT imposta cookie proprietari essenziali per abilitare determinate funzionalità e ricordare le tue preferenze. Ad esempio, i cookie ti tengono connesso, ti consentono di acquistare articoli e mantengono le impostazioni di lingua e volume.',
        dataShare:
          "Questo cookie condivide i dati con i nostri partner terze parti fidate e/o al di fuori dell'UE/SEE.",
      },
      LT: {
        description:
          'Vimeo OTT nustato pirmosios šalies svarbiausius slapukus, kad įgalintų tam tikras funkcijas ir prisimintų jūsų nuostatas. Pavyzdžiui, slapukai palaiko jus prisijungę, leidžia pirkti daiktus ir palaiko kalbos ir garsumo nustatymus.',
        dataShare:
          'Šis slapukas bendrina duomenis su patikimais trečiųjų šalių partneriais ir (arba) už ES / EEE ribų.',
      },
      LV: {
        description:
          'Vimeo OTT nosaka pirmās puses būtiskus sīkfailus, lai iespējotu noteiktas funkcijas un atcerētos jūsu preferences. Piemēram, sīkfaili nodrošina jūsu pieteikšanos, ļauj jums iegādāties',
        dataShare: 'Šīs sīkdatnes kopīgo informāciju ar mūsu uzticamiem trešās puses partneriem un/vai ārpus ES/EEZ.',
      },
      NL: {
        description:
          'Vimeo OTT stelt first-party, essentiële cookies in om bepaalde functies mogelijk te maken en uw voorkeuren te onthouden. Cookies zorgen er bijvoorbeeld voor dat u ingelogd blijft, dat u items kunt kopen en dat uw taal- en volume-instellingen behouden blijven.',
        dataShare: 'Deze cookie deelt gegevens met onze vertrouwde externe partners en/of buiten de EU/EER.',
      },
      NO: {
        description:
          'Vimeo OTT setter førsteparts, viktige informasjonskapsler for å aktivere visse funksjoner og huske dine preferanser. For eksempel holder cookies deg pålogget, lar deg kjøpe varer og opprettholder språk- og voluminnstillinger.',
        dataShare:
          'Denne informasjonskapselen deler data med våre pålitelige tredjepartsleverandører og/eller utenfor EU/EEA.',
      },
      PL: {
        description:
          'Vimeo OTT ustawia pliki cookie pierwszej strony, które są niezbędne do włączania określonych funkcji i zapamiętywania preferencji użytkownika. Pliki cookie pozwalają między innymi na logowanie, zakupy oraz zachowanie ustawień języka i głośności.',
        dataShare: 'Ten plik cookie udostępnia dane naszym zaufanym partnerom lub podmiotom spoza UE/EOG.',
      },
      RO: {
        description:
          'Vimeo OTT setează module cookie esenţiale primare pentru a permite anumite caracteristici şi a-şi aminti preferinţele dvs. De exemplu, modulele cookie vă menţin autentificaţi, vă permit să achiziţionaţi articole şi menţin setările dvs. de limbă şi volum.',
        dataShare: 'Acest modul cookie partajează date cu partenerii noştri terţi de încredere şi/sau în afara UE/SEE.',
      },
      SK: {
        description:
          'OTT Vimeo nastavuje základné súbory cookie prvej strany, ktoré umožňujú určité funkcie a pamätajú si vaše preferencie. Súbory cookie vás napríklad udržiavajú prihlásených, umožňujú vám nakupovať položky a zachovávajú vaše nastavenie jazyka a hlasitosti.',
        dataShare:
          'Tento súbor cookie zdieľa údaje s našimi dôveryhodnými partnermi tretích strán a/alebo mimo EÚ/EHP.',
      },
      SV: {
        description:
          'Vimeo OTT ställer in första-parts huvudkakor för att aktivera vissa funktioner och komma ihåg dina preferenser. Till exempel håller kakor dig inloggad, låter dig köpa varor och behålla dina språk- och volyminställningar.',
        dataShare: 'Denna cookie delar data med våra betrodda tredjepartspartners och/eller utanför EU/EES.',
      },
      default: {
        description:
          'Vimeo OTT sets first-party, essential cookies to enable certain features and remember your preferences. For example, cookies keep you logged in, allow you to purchase items, and maintain your language and volume settings.',
        dataShare: 'This cookie shares data with our trusted third party partners and/or outside the EU/EEA.',
      },
    },
    LinkedIn: {
      EN: {
        description:
          'These cookies allow Distrelec to track your journey when you click through to the site from adverts externally.',
        dataShare: 'This cookie shares data with our trusted third party partners and/or outside the EU/EEA.',
      },
      CS: {
        description:
          'Tyto soubory cookie umožňují společnosti Distrelec sledovat vaše kroky, když se na web dostanete kliknutím na externí reklamu.',
        dataShare:
          'Tento soubor cookie sdílí data s našimi důvěryhodnými partnery z řad třetích stran a /nebo mimo EU/EHP.',
      },
      DK: {
        description:
          'Disse cookies giver Elfa Distrelec mulighed for at spore din rejse, når du klikker igennem til webstedet fra eksterne annoncer.',
        dataShare: 'Denne cookie deler data med vores betroede tredjepartspartnere og/eller uden for EU/EØS.',
      },
      DE: {
        description:
          'Diese Cookies ermöglichen es Distrelec, Ihren Weg zu verfolgen, wenn Sie sich von externen Anzeigen auf die Website durchklicken.',
        dataShare:
          'Dieses Cookie teilt Daten mit unseren vertrauenswürdigen Drittpartnern und/oder außerhalb der EU/EWR.',
      },
      EE: {
        description:
          'Need küpsised võimaldavad Elfa Distrelecil jälgida Teie teekonda, kui Te välistes reklaamides klõpsates saidile saabute.',
        dataShare:
          'See küpsis jagab andmeid meie usaldatud kolmandast isikust partneritega ja/või väljaspool EL-i/EMP-d.',
      },
      FI: {
        description:
          'Näiden evästeiden avulla Distrelec voi seurata matkaasi, kun napsautat sivustolle ulkoisista ilmoituksista.',
        dataShare:
          'Tämä eväste jakaa tietoja yrityksen kumppaneina toimivien luotettavien kolmansien osapuolten kanssa ja/tai EU:n/ETA:n ulkopuolelle.',
      },
      FR: {
        description:
          "Ces cookies permettent à Distrelec de suivre votre parcours lorsque vous accédez au site en cliquant sur un lien provenant d'une publicité externe.",
        dataShare: "Ce cookie partage des données avec nos partenaires tiers de confiance et/ou en dehors de l'UE/EEE.",
      },
      HU: {
        description:
          'Ezek a cookie-k lehetővé teszik a Distrelec számára, hogy nyomon kövesse az Ön által megtett útvonalat, amikor egy külső hirdetésre kattintva lép a weboldalra.',
        dataShare: 'Ez a cookie adatokat oszt meg megbízható harmadik fél partnereinkkel és/vagy az EU/EGT-n kívül.',
      },
      IT: {
        description:
          'Questi cookie consentono a Distrelec di tenere traccia del tuo viaggio quando fai clic sul sito da annunci esterni.',
        dataShare:
          "Questo cookie condivide i dati con i nostri partner terze parti fidate e/o al di fuori dell'UE/SEE.",
      },
      LT: {
        description:
          'Šie slapukai leidžia Distrelec stebėti jūsų kelią, kai spustelėjate svetainę iš išorinių skelbimų.',
        dataShare:
          'Šis slapukas bendrina duomenis su patikimais trečiųjų šalių partneriais ir (arba) už ES / EEE ribų.',
      },
      LV: {
        description:
          'Šie sīkfaili ļauj Elfa Distrelec izsekot jūsu pārvietošanos, kad atverat vietni, noklikšķinot uz ārējām reklāmām.',
        dataShare: 'Šīs sīkdatnes kopīgo informāciju ar mūsu uzticamiem trešās puses partneriem un/vai ārpus ES/EEZ.',
      },
      NL: {
        description:
          'Deze cookies laten Distrelec toe uw traject te volgen wanneer u vanuit externe advertenties doorklikt naar de site.',
        dataShare: 'Deze cookie deelt gegevens met onze vertrouwde externe partners en/of buiten de EU/EER.',
      },
      NO: {
        description:
          'Disse informasjonskapslene gjør det mulig for Elfa Distrelec å spore reisen din når du klikker deg gjennom til siden fra eksterne annonser.',
        dataShare:
          'Denne informasjonskapselen deler data med våre pålitelige tredjepartsleverandører og/eller utenfor EU/EEA.',
      },
      PL: {
        description:
          'Te pliki cookie umożliwiają firmie Distrelec śledzenie, w jaki sposób trafiasz na stronę, klikając na reklamy z zewnętrznych stron.',
        dataShare: 'Ten plik cookie udostępnia dane naszym zaufanym partnerom lub podmiotom spoza UE/EOG.',
      },
      RO: {
        description:
          'Aceste module cookie permit Distrelec să monitorizeze navigaţia dvs. atunci când faceţi clic către site din reclame externe.',
        dataShare: 'Acest modul cookie partajează date cu partenerii noştri terţi de încredere şi/sau în afara UE/SEE.',
      },
      SK: {
        description:
          'Tieto súbory cookie umožňujú spoločnosti Distrelec sledovať vašu cestu, keď navštívite lokalitu prostredníctvom externých reklám.',
        dataShare:
          'Tento súbor cookie zdieľa údaje s našimi dôveryhodnými partnermi tretích strán a/alebo mimo EÚ/EHP.',
      },
      SV: {
        description:
          'Dessa kakor ger Elfa Distrelec möjlighet att spåra din väg till webbplatsen när du klickar på externa annonser.',
        dataShare: 'Denna cookie delar data med våra betrodda tredjepartspartners och/eller utanför EU/EES.',
      },
      default: {
        description:
          'These cookies allow Distrelec to track your journey when you click through to the site from adverts externally.',
        dataShare: 'This cookie shares data with our trusted third party partners and/or outside the EU/EEA.',
      },
    },
    'Google Ads': {
      EN: {
        description:
          'These cookies allow Distrelec to show you relevant adverts when you navigate away from the site, also to track your journey when you click through to the site from external adverts.',
        dataShare: 'This cookie shares data with our trusted third party partners and/or outside the EU/EEA.',
      },
      CS: {
        description:
          'Tyto soubory cookie umožňují společnosti Distrelec zobrazovat vám relevantní reklamy po opuštění internetového obchodu a také sledovat vaše kroky, když se na web dostanete kliknutím na externí reklamu.',
        dataShare:
          'Tento soubor cookie sdílí data s našimi důvěryhodnými partnery z řad třetích stran a /nebo mimo EU/EHP.',
      },
      DK: {
        description:
          'Disse cookies giver Elfa Distrelec mulighed for at vise dig relevante annoncer, når du navigerer væk fra webstedet, også for at spore din rejse, når du klikker igennem til webstedet fra eksterne annoncer.',
        dataShare: 'Denne cookie deler data med vores betroede tredjepartspartnere og/eller uden for EU/EØS.',
      },
      DE: {
        description:
          'Diese Cookies ermöglichen es Distrelec, Ihnen relevante Werbung zu zeigen, wenn Sie von der Website weg navigieren, und Ihre Reise zu verfolgen, wenn Sie von externen Anzeigen auf die Website klicken.',
        dataShare:
          'Dieses Cookie teilt Daten mit unseren vertrauenswürdigen Drittpartnern und/oder außerhalb der EU/EWR.',
      },
      EE: {
        description:
          "Need küpsised võimaldavad Distrelec'il näidata teile asjakohaseid reklaame, kui navigeerite veebilehelt eemale, samuti jälgida teie teekonda, kui klõpsate veebilehele välisreklaamilt.",
        dataShare:
          'See küpsis jagab andmeid meie usaldatud kolmandast isikust partneritega ja/või väljaspool EL-i/EMP-d.',
      },
      FI: {
        description:
          'Näiden evästeiden avulla Distrelec voi näyttää sinulle asiaankuuluvia mainoksia, kun siirryt pois sivustolta, ja myös seurata matkaa, kun napsautat sivustolle ulkoisista ilmoituksista.',
        dataShare:
          'Tämä eväste jakaa tietoja yrityksen kumppaneina toimivien luotettavien kolmansien osapuolten kanssa ja/tai EU:n/ETA:n ulkopuolelle.',
      },
      FR: {
        description:
          "Ces cookies permettent à Distrelec de vous afficher des publicités pertinentes lors de votre navigation hors du site, ainsi que de suivre votre parcours lorsque vous accédez au site en cliquant sur un lien provenant d'une publicité externe.",
        dataShare: "Ce cookie partage des données avec nos partenaires tiers de confiance et/ou en dehors de l'UE/EEE.",
      },
      HU: {
        description:
          'Ezek a cookie-k lehetővé teszik a Distrelec számára, hogy kapcsolódó hirdetéseket mutasson Önnek, amikor elhagyja a weboldalt, illetve nyomon követi az Ön által megtett útvonalat, amikor egy külső hirdetésre kattintva lép a weboldalra.',
        dataShare: 'Ez a cookie adatokat oszt meg megbízható harmadik fél partnereinkkel és/vagy az EU/EGT-n kívül.',
      },
      IT: {
        description:
          'Questi cookie consentono a Distrelec di mostrarti annunci pertinenti quando esci dal sito, anche per tenere traccia del tuo viaggio quando fai clic sul sito da annunci esterni.',
        dataShare:
          "Questo cookie condivide i dati con i nostri partner terze parti fidate e/o al di fuori dell'UE/SEE.",
      },
      LT: {
        description:
          'Šie slapukai leidžia Distrelec rodyti jums aktualius skelbimus, kai einate toliau nuo svetainės, taip pat sekti savo kelią, kai spustelite svetainę iš išorinių skelbimų.',
        dataShare:
          'Šis slapukas bendrina duomenis su patikimais trečiųjų šalių partneriais ir (arba) už ES / EEE ribų.',
      },
      LV: {
        description:
          'Šīs sīkdatnes ļauj Distrelec rādīt jums atbilstošas reklāmas, kad darbojaties ārpus vietnes, kā arī sekot gaitai, kad no ārējām reklāmām nokļūstat mūsu vietnē.',
        dataShare: 'Šīs sīkdatnes kopīgo informāciju ar mūsu uzticamiem trešās puses partneriem un/vai ārpus ES/EEZ.',
      },
      NL: {
        description:
          'Deze cookies laten Distrelec toe om u relevante advertenties te tonen wanneer u weg surft van de site, alsook om uw reis te volgen wanneer u doorklikt naar de site vanuit externe advertenties.',
        dataShare: 'Deze cookie deelt gegevens met onze vertrouwde externe partners en/of buiten de EU/EER.',
      },
      NO: {
        description:
          'Disse informasjonskapslene tillater Elfa Distrelec å vise deg relevante annonser når du navigerer vekk fra nettstedet, også for å spore reisen din når du klikker deg gjennom til siden fra eksterne annonser.',
        dataShare:
          'Denne informasjonskapselen deler data med våre pålitelige tredjepartsleverandører og/eller utenfor EU/EEA.',
      },
      PL: {
        description:
          'Te pliki cookie umożliwiają firmie Distrelec wyświetlanie powiązanych reklam, kiedy przeglądasz inne strony internetowe oraz śledzenie, w jaki sposób trafiasz na stronę, klikając na reklamy z zewnętrznych stron.',
        dataShare: 'Ten plik cookie udostępnia dane naszym zaufanym partnerom lub podmiotom spoza UE/EOG.',
      },
      RO: {
        description:
          'Aceste module cookie permit Distrelec să afişeze reclamele relevante atunci când navigaţi în afara site-ului şi să monitorizeze navigarea dvs. în site atunci când faceţi clic din reclamele externe.',
        dataShare: 'Acest modul cookie partajează date cu partenerii noştri terţi de încredere şi/sau în afara UE/SEE.',
      },
      SK: {
        description:
          'Tieto súbory cookie umožňujú spoločnosti Distrelec zobrazovať relevantné reklamy, keď odídete z lokality, a taktiež sledovať cestu, keď navštívite lokalitu prostredníctvom externých reklám.',
        dataShare:
          'Tento súbor cookie zdieľa údaje s našimi dôveryhodnými partnermi tretích strán a/alebo mimo EÚ/EHP.',
      },
      SV: {
        description:
          'Kakor som ger Elfa Distrelec möjlighet att visa dig relevanta annonser när du lämnar webbplatsen och även spårar din väg till webbplatsen när du klickat på externa annonser.',
        dataShare: 'Denna cookie delar data med våra betrodda tredjepartspartners och/eller utanför EU/EES.',
      },
      default: {
        description:
          'These cookies allow Distrelec to show you relevant adverts when you navigate away from the site, also to track your journey when you click through to the site from external adverts.',
        dataShare: 'This cookie shares data with our trusted third party partners and/or outside the EU/EEA.',
      },
    },
    'Facebook Custom Audiences': {
      EN: {
        description:
          'These cookies allow Distrelec to show you relevant adverts when you navigate away from the site, also to track your journey when you click through to the site from external adverts.',
        dataShare: 'This cookie shares data with our trusted third party partners and/or outside the EU/EEA.',
      },
      CS: {
        description:
          'Tyto soubory cookie umožňují společnosti Distrelec zobrazovat vám relevantní reklamy po opuštění internetového obchodu a také sledovat vaše kroky, když se na web dostanete kliknutím na externí reklamu.',
        dataShare:
          'Tento soubor cookie sdílí data s našimi důvěryhodnými partnery z řad třetích stran a /nebo mimo EU/EHP.',
      },
      DK: {
        description:
          'Disse cookies giver Elfa Distrelec mulighed for at vise dig relevante annoncer, når du navigerer væk fra webstedet, også for at spore din rejse, når du klikker igennem til webstedet fra eksterne annoncer. ',
        dataShare: 'Denne cookie deler data med vores betroede tredjepartspartnere og/eller uden for EU/EØS.',
      },
      DE: {
        description:
          'Diese Cookies ermöglichen es Distrelec, Ihnen relevante Werbung zu zeigen, wenn Sie die Website verlassen, und Ihren Weg zu verfolgen, wenn Sie sich von externen Anzeigen auf die Website durchklicken.',
        dataShare:
          'Dieses Cookie teilt Daten mit unseren vertrauenswürdigen Drittpartnern und/oder außerhalb der EU/EWR.',
      },
      EE: {
        description:
          "Need küpsised võimaldavad Distrelec'il näidata teile asjakohaseid reklaame, kui navigeerite veebilehelt eemale, samuti jälgida teie teekonda, kui klõpsate veebilehele välisreklaamilt.",
        dataShare:
          'See küpsis jagab andmeid meie usaldatud kolmandast isikust partneritega ja/või väljaspool EL-i/EMP-d.',
      },
      FI: {
        description:
          'Näiden evästeiden avulla Distrelec voi näyttää sinulle asiaankuuluvia mainoksia, kun siirryt pois sivustolta, ja myös seurata matkaa, kun napsautat sivustolle ulkoisista ilmoituksista.',
        dataShare:
          'Tämä eväste jakaa tietoja yrityksen kumppaneina toimivien luotettavien kolmansien osapuolten kanssa ja/tai EU:n/ETA:n ulkopuolelle.',
      },
      FR: {
        description:
          "Ces cookies permettent à Distrelec de vous afficher des publicités pertinentes lors de votre navigation hors du site, ainsi que de suivre votre parcours lorsque vous accédez au site en cliquant sur un lien provenant d'une publicité externe.",
        dataShare: "Ce cookie partage des données avec nos partenaires tiers de confiance et/ou en dehors de l'UE/EEE.",
      },
      HU: {
        description:
          'Ezek a cookie-k lehetővé teszik a Distrelec számára, hogy kapcsolódó hirdetéseket mutasson Önnek, amikor elhagyja a weboldalt, illetve nyomon követi az Ön által megtett útvonalat, amikor egy külső hirdetésre kattintva lép a weboldalra.',
        dataShare: 'Ez a cookie adatokat oszt meg megbízható harmadik fél partnereinkkel és/vagy az EU/EGT-n kívül.',
      },
      IT: {
        description:
          'Questi cookie consentono a Distrelec di mostrarti annunci pertinenti quando esci dal sito, anche per tenere traccia del tuo viaggio quando fai clic sul sito da annunci esterni.',
        dataShare:
          "Questo cookie condivide i dati con i nostri partner terze parti fidate e/o al di fuori dell'UE/SEE.",
      },
      LT: {
        description:
          'Šie slapukai leidžia Distrelec rodyti jums aktualius skelbimus, kai einate toliau nuo svetainės, taip pat sekti savo kelią, kai spustelite svetainę iš išorinių skelbimų.',
        dataShare:
          'Šis slapukas bendrina duomenis su patikimais trečiųjų šalių partneriais ir (arba) už ES / EEE ribų.',
      },
      LV: {
        description:
          'Šīs sīkdatnes ļauj Distrelec rādīt jums atbilstošas reklāmas, kad darbojaties ārpus vietnes, kā arī sekot gaitai, kad no ārējām reklāmām nokļūstat mūsu vietnē.',
        dataShare: 'Šīs sīkdatnes kopīgo informāciju ar mūsu uzticamiem trešās puses partneriem un/vai ārpus ES/EEZ.',
      },
      NL: {
        description:
          'Deze cookies laten Distrelec toe om u relevante advertenties te tonen wanneer u weg surft van de site, alsook om uw reis te volgen wanneer u doorklikt naar de site vanuit externe advertenties.',
        dataShare: 'Deze cookie deelt gegevens met onze vertrouwde externe partners en/of buiten de EU/EER.',
      },
      NO: {
        description:
          'Disse informasjonskapslene tillater Elfa Distrelec å vise deg relevante annonser når du navigerer vekk fra nettstedet, også for å spore reisen din når du klikker deg gjennom til siden fra eksterne annonser.',
        dataShare:
          'Denne informasjonskapselen deler data med våre pålitelige tredjepartsleverandører og/eller utenfor EU/EEA.',
      },
      PL: {
        description:
          'Te pliki cookie umożliwiają firmie Distrelec wyświetlanie powiązanych reklam, kiedy przeglądasz inne strony internetowe oraz śledzenie, w jaki sposób trafiasz na stronę, klikając na reklamy z zewnętrznych stron.',
        dataShare: 'Ten plik cookie udostępnia dane naszym zaufanym partnerom lub podmiotom spoza UE/EOG.',
      },
      RO: {
        description:
          'Aceste module cookie permit Distrelec să afişeze reclamele relevante atunci când navigaţi în afara site-ului şi să monitorizeze navigarea dvs. în site atunci când faceţi clic din reclamele externe.',
        dataShare: 'Acest modul cookie partajează date cu partenerii noştri terţi de încredere şi/sau în afara UE/SEE.',
      },
      SK: {
        description:
          'Tieto súbory cookie umožňujú spoločnosti Distrelec zobrazovať relevantné reklamy, keď odídete z lokality, a taktiež sledovať cestu, keď navštívite lokalitu prostredníctvom externých reklám.',
        dataShare:
          'Tento súbor cookie zdieľa údaje s našimi dôveryhodnými partnermi tretích strán a/alebo mimo EÚ/EHP.',
      },
      SV: {
        description:
          'Kakor som ger Elfa Distrelec möjlighet att visa dig relevanta annonser när du lämnar webbplatsen och även spårar din väg till webbplatsen när du klickat på externa annonser.',
        dataShare: 'Denna cookie delar data med våra betrodda tredjepartspartners och/eller utanför EU/EES.',
      },
      default: {
        description:
          'These cookies allow Distrelec to show you relevant adverts when you navigate away from the site, also to track your journey when you click through to the site from external adverts.',
        dataShare: 'This cookie shares data with our trusted third party partners and/or outside the EU/EEA.',
      },
    },
    'Google Doubleclick': {
      EN: {
        description:
          'These cookies allow Distrelec to track your journey when you click through to the site from adverts externally.',
        dataShare: 'This cookie shares data with our trusted third party partners and/or outside the EU/EEA.',
      },
      CS: {
        description:
          'Tyto soubory cookie umožňují společnosti Distrelec sledovat vaše kroky, když se na web dostanete kliknutím na externí reklamu.',
        dataShare:
          'Tento soubor cookie sdílí data s našimi důvěryhodnými partnery z řad třetích stran a /nebo mimo EU/EHP.',
      },
      DK: {
        description:
          'Disse cookies giver Elfa Distrelec mulighed for at spore din rejse, når du klikker igennem til webstedet fra eksterne annoncer.',
        dataShare: 'Denne cookie deler data med vores betroede tredjepartspartnere og/eller uden for EU/EØS.',
      },
      DE: {
        description:
          'Diese Cookies ermöglichen es Distrelec, Ihre Reise zu verfolgen, wenn Sie sich von externen Anzeigen auf die Seite durchklicken.',
        dataShare:
          'Dieses Cookie teilt Daten mit unseren vertrauenswürdigen Drittpartnern und/oder außerhalb der EU/EWR.',
      },
      EE: {
        description:
          "Need küpsised võimaldavad Distrelec'il jälgida teie teekonda, kui klõpsate veebilehele välisreklaamilt.",
        dataShare:
          'See küpsis jagab andmeid meie usaldatud kolmandast isikust partneritega ja/või väljaspool EL-i/EMP-d.',
      },
      FI: {
        description:
          'Näiden evästeiden avulla Distrelec voi seurata matkaasi, kun napsautat sivustolle ulkoisista ilmoituksista.',
        dataShare:
          'Tämä eväste jakaa tietoja yrityksen kumppaneina toimivien luotettavien kolmansien osapuolten kanssa ja/tai EU:n/ETA:n ulkopuolelle.',
      },
      FR: {
        description:
          "Ces cookies permettent à Distrelec de suivre votre parcours lorsque vous accédez au site en cliquant sur un lien provenant d'une publicité externe.",
        dataShare: "Ce cookie partage des données avec nos partenaires tiers de confiance et/ou en dehors de l'UE/EEE.",
      },
      HU: {
        description:
          'Ezek a cookie-k lehetővé teszik a Distrelec számára, hogy nyomon kövesse az Ön által megtett útvonalat, amikor egy külső hirdetésre kattintva lép a weboldalra.',
        dataShare: 'Ez a cookie adatokat oszt meg megbízható harmadik fél partnereinkkel és/vagy az EU/EGT-n kívül.',
      },
      IT: {
        description:
          'Questi cookie consentono a Distrelec di tenere traccia della tua navigazione quando fai clic sul sito da annunci esterni.',
        dataShare:
          "Questo cookie condivide i dati con i nostri partner terze parti fidate e/o al di fuori dell'UE/SEE.",
      },
      LT: {
        description:
          'Šie slapukai leidžia Distrelec stebėti jūsų kelią, kai spustelėjate svetainę iš išorinių skelbimų.',
        dataShare:
          'Šis slapukas bendrina duomenis su patikimais trečiųjų šalių partneriais ir (arba) už ES / EEE ribų.',
      },
      LV: {
        description: 'Šie sīkfaili ļauj Distrelec  sekot jūsu gaitai, kad no ārējām reklāmām nokļūstat mūsu vietnē.',
        dataShare: 'Šīs sīkdatnes kopīgo informāciju ar mūsu uzticamiem trešās puses partneriem un/vai ārpus ES/EEZ.',
      },
      NL: {
        description:
          'Deze cookies laten Distrelec toe uw traject te volgen wanneer u vanuit externe advertenties doorklikt naar de site.',
        dataShare: 'Deze cookie deelt gegevens met onze vertrouwde externe partners en/of buiten de EU/EER.',
      },
      NO: {
        description:
          'Disse informasjonskapslene gjør det mulig for Elfa Distrelec å spore reisen din når du klikker deg gjennom til siden fra eksterne annonser.',
        dataShare:
          'Denne informasjonskapselen deler data med våre pålitelige tredjepartsleverandører og/eller utenfor EU/EEA.',
      },
      PL: {
        description:
          'Te pliki cookie umożliwiają firmie Distrelec śledzenie, w jaki sposób trafiasz na stronę, klikając na reklamy z zewnętrznych stron.',
        dataShare: 'Ten plik cookie udostępnia dane naszym zaufanym partnerom lub podmiotom spoza UE/EOG.',
      },
      RO: {
        description:
          'Aceste module cookie permit Distrelec să monitorizeze navigaţia dvs. atunci când faceţi clic către site din reclame externe.',
        dataShare: 'Acest modul cookie partajează date cu partenerii noştri terţi de încredere şi/sau în afara UE/SEE.',
      },
      SK: {
        description:
          'Tieto súbory cookie umožňujú spoločnosti Distrelec sledovať vašu cestu, keď navštívite lokalitu prostredníctvom externých reklám.',
        dataShare:
          'Tento súbor cookie zdieľa údaje s našimi dôveryhodnými partnermi tretích strán a/alebo mimo EÚ/EHP.',
      },
      SV: {
        description:
          'Cookies som ger Elfa Distrelec möjlighet att spåra din väg till webbplatsen när du klickar på externa annonser.',
        dataShare: 'Denna cookie delar data med våra betrodda tredjepartspartners och/eller utanför EU/EES.',
      },
      default: {
        description:
          'These cookies allow Distrelec to track your journey when you click through to the site from adverts externally.',
        dataShare: 'This cookie shares data with our trusted third party partners and/or outside the EU/EEA.',
      },
    },
    'Push Engage': {
      EN: {
        description: 'Distrelec uses this to get the status of the subscriber. It includes domain pushengage.com',
        dataShare: 'This cookie shares data with our trusted third party partners and/or outside the EU/EEA.',
      },
      CS: {
        description:
          'Společnost Distrelec tento prvek využívá k získání stavu předplatitele. Zahrnuje doménu pushengage.com',
        dataShare:
          'Tento soubor cookie sdílí data s našimi důvěryhodnými partnery z řad třetích stran a /nebo mimo EU/EHP.',
      },
      DK: {
        description: 'Elfa Distrelec bruger dette til at få status på abonnenten. Det inkluderer domæne pushengage.com',
        dataShare: 'Denne cookie deler data med vores betroede tredjepartspartnere og/eller uden for EU/EØS.',
      },
      DE: {
        description:
          'Distrelec verwendet dies, um den Status des Abonnenten zu erhalten. Es umfasst die Domain pushengage.com',
        dataShare:
          'Dieses Cookie teilt Daten mit unseren vertrauenswürdigen Drittpartnern und/oder außerhalb der EU/EWR.',
      },
      EE: {
        description: 'Distrelec kasutab seda abonendi staatuse saamiseks. See sisaldab domeeni pushengage.com',
        dataShare:
          'See küpsis jagab andmeid meie usaldatud kolmandast isikust partneritega ja/või väljaspool EL-i/EMP-d.',
      },
      FI: {
        description: 'Distrelec käyttää tätä saadakseen tilaajan tilan. Se sisältää verkkotunnuksen pushengage.com',
        dataShare:
          'Tämä eväste jakaa tietoja yrityksen kumppaneina toimivien luotettavien kolmansien osapuolten kanssa ja/tai EU:n/ETA:n ulkopuolelle.',
      },
      FR: {
        description: "Distrelec l'utilise pour obtenir le statut de l'abonné. Il comprend le domaine pushengage.com",
        dataShare: "Ce cookie partage des données avec nos partenaires tiers de confiance et/ou en dehors de l'UE/EEE.",
      },
      HU: {
        description:
          'A Distrelec ezzel a feliratkozó állapotáról kap információt. Magában foglalja a pushengage.com tartományt',
        dataShare: 'Ez a cookie adatokat oszt meg megbízható harmadik fél partnereinkkel és/vagy az EU/EGT-n kívül.',
      },
      IT: {
        description: "Distrelec lo utilizza per ottenere lo stato dell'abbonato. Include il dominio pushengage.com",
        dataShare:
          "Questo cookie condivide i dati con i nostri partner terze parti fidate e/o al di fuori dell'UE/SEE.",
      },
      LT: {
        description: 'Distrelec tai naudoja norėdama gauti abonento būseną. Tai apima domeną pushengage.com',
        dataShare:
          'Šis slapukas bendrina duomenis su patikimais trečiųjų šalių partneriais ir (arba) už ES / EEE ribų.',
      },
      LV: {
        description: 'Distrelec to izmanto, lai iegūtu abonenta statusu. Tas ietver domēnu pushengage.com',
        dataShare: 'Šīs sīkdatnes kopīgo informāciju ar mūsu uzticamiem trešās puses partneriem un/vai ārpus ES/EEZ.',
      },
      NL: {
        description:
          'Distrelec gebruikt dit om de status van de abonnee te krijgen. Het omvat het domein pushengage.com',
        dataShare: 'Deze cookie deelt gegevens met onze vertrouwde externe partners en/of buiten de EU/EER.',
      },
      NO: {
        description: 'Elfa Distrelec bruker dette for å få status som abonnent. Det inkluderer domenet pushengage.com',
        dataShare:
          'Denne informasjonskapselen deler data med våre pålitelige tredjepartsleverandører og/eller utenfor EU/EEA.',
      },
      PL: {
        description: 'Distrelec używa tego do uzyskania statusu klienta. Zawiera domenę pushengage.com',
        dataShare: 'Ten plik cookie udostępnia dane naszym zaufanym partnerom lub podmiotom spoza UE/EOG.',
      },
      RO: {
        description:
          'Distrelec utilizează acest modul cookie pentru a obţine starea abonatului. Acesta include domeniul pushengage.com',
        dataShare: 'Acest modul cookie partajează date cu partenerii noştri terţi de încredere şi/sau în afara UE/SEE.',
      },
      SK: {
        description:
          'Spoločnosť Distrelec túto službu používa na získanie stavu odberateľa. Zahŕňa doménu pushengage.com.',
        dataShare:
          'Tento súbor cookie zdieľa údaje s našimi dôveryhodnými partnermi tretích strán a/alebo mimo EÚ/EHP.',
      },
      SV: {
        description:
          'Elfa Distrelec använder denna för att få status på prenumeranten. Det inkluderar domain pushengage.com',
        dataShare: 'Denna cookie delar data med våra betrodda tredjepartspartners och/eller utanför EU/EES.',
      },
      default: {
        description: 'Distrelec uses this to get the status of the subscriber. It includes domain pushengage.com',
        dataShare: 'This cookie shares data with our trusted third party partners and/or outside the EU/EEA.',
      },
    },
    Reevoo: {
      EN: {
        description:
          'These cookies allow us to integrate into our website customer reviews of our products left on the Reevoo website.',
        dataShare: 'This cookie shares data with our trusted third party partners and/or outside the EU/EEA.',
      },
      CS: {
        description:
          'Tyto soubory cookie nám umožňují integrovat zákaznická hodnocení našich produktů z webových stránek Reevoo do našich webových stránek.',
        dataShare:
          'Tento soubor cookie sdílí data s našimi důvěryhodnými partnery z řad třetích stran a /nebo mimo EU/EHP.',
      },
      DK: {
        description:
          'Disse cookies giver os mulighed for at integrere de anmeldelser af vores produkter som vores kunder laver på Reevoo-webstedet.',
        dataShare: 'Denne cookie deler data med vores betroede tredjepartspartnere og/eller uden for EU/EØS.',
      },
      DE: {
        description:
          'Diese Cookies ermöglichen es uns, Kundenrezensionen zu unseren Produkten, die auf der Reevoo-Website hinterlassen wurden, in unsere Website zu integrieren.',
        dataShare:
          'Dieses Cookie teilt Daten mit unseren vertrauenswürdigen Drittpartnern und/oder außerhalb der EU/EWR.',
      },
      EE: {
        description:
          'Need küpsised võimaldavad meil integreerida oma veebisaidile klientide kommentaare meie toodete kohta, mis on jäetud Reevoo veebisaidil.',
        dataShare:
          'See küpsis jagab andmeid meie usaldatud kolmandast isikust partneritega ja/või väljaspool EL-i/EMP-d.',
      },
      FI: {
        description:
          'Näiden evästeiden avulla voimme integroida verkkosivustollemme asiakasarvostelut tuotteistamme, jotka on jätetty Reevoo-verkkosivustolle.',
        dataShare:
          'Tämä eväste jakaa tietoja yrityksen kumppaneina toimivien luotettavien kolmansien osapuolten kanssa ja/tai EU:n/ETA:n ulkopuolelle.',
      },
      FR: {
        description:
          "Ces cookies nous permettent d'intégrer à notre site web les avis des clients sur nos produits laissés sur le site web Reevoo.",
        dataShare: "Ce cookie partage des données avec nos partenaires tiers de confiance et/ou en dehors de l'UE/EEE.",
      },
      HU: {
        description:
          'Ezek a cookie-k lehetővé teszik számunkra, hogy a Reevoo honlapján található, termékeinkkel kapcsolatos vásárlói véleményeket is megjeleníthessük a weboldalunkon.',
        dataShare: 'Ez a cookie adatokat oszt meg megbízható harmadik fél partnereinkkel és/vagy az EU/EGT-n kívül.',
      },
      IT: {
        description:
          'Questi cookie ci consentono di integrare nel nostro sito Web le recensioni dei clienti dei nostri prodotti lasciate sul sito Reevoo.',
        dataShare:
          "Questo cookie condivide i dati con i nostri partner terze parti fidate e/o al di fuori dell'UE/SEE.",
      },
      LT: {
        description:
          'Šie slapukai leidžia mums integruoti į savo svetainę klientų atsiliepimus apie mūsų produktus, paliktus Reevoo svetainėje.',
        dataShare:
          'Šis slapukas bendrina duomenis su patikimais trečiųjų šalių partneriais ir (arba) už ES / EEE ribų.',
      },
      LV: {
        description:
          'Šīs sīkdatnes ļauj mums mūsu vietnē integrēt klientu atsauksmes par mūsu produktiem, kas atstātas Reevoo vietnē.',
        dataShare: 'Šīs sīkdatnes kopīgo informāciju ar mūsu uzticamiem trešās puses partneriem un/vai ārpus ES/EEZ.',
      },
      NL: {
        description:
          'Met deze cookies kunnen wij beoordelingen van klanten over onze producten die op de Reevoo-website zijn achtergelaten, in onze website integreren.',
        dataShare: 'Deze cookie deelt gegevens met onze vertrouwde externe partners en/of buiten de EU/EER.',
      },
      NO: {
        description:
          'Disse informasjonskapslene tillater oss å integrere kundevurderinger av produktene våre på Reevoo-nettstedet på nettstedet vårt.',
        dataShare:
          'Denne informasjonskapselen deler data med våre pålitelige tredjepartsleverandører og/eller utenfor EU/EEA.',
      },
      PL: {
        description:
          'Te pliki cookie pozwalają nam na integrację z naszą stroną internetową recenzji naszych produktów pozostawionych na stronie Reevoo.',
        dataShare: 'Ten plik cookie udostępnia dane naszym zaufanym partnerom lub podmiotom spoza UE/EOG.',
      },
      RO: {
        description:
          'Aceste module cookie ne permit să integrăm în site-ul nostru web părerile pe care clienţii noştri le-au scris despre produsele noastre pe site-ul Reevoo.',
        dataShare: 'Acest modul cookie partajează date cu partenerii noştri terţi de încredere şi/sau în afara UE/SEE.',
      },
      SK: {
        description:
          'Tieto súbory cookie nám umožňujú integrovať do našej webovej lokality recenzie našich produktov, ktoré zverejnili zákazníci na webovej lokalite Reevoo.',
        dataShare:
          'Tento súbor cookie zdieľa údaje s našimi dôveryhodnými partnermi tretích strán a/alebo mimo EÚ/EHP.',
      },
      SV: {
        description:
          'Dessa kakor gör det möjligt för oss att integrera kundrecensioner på våra webbplatser på våra produkter. Recensioner som gjorts på Reevoos webbplats.',
        dataShare: 'Denna cookie delar data med våra betrodda tredjepartspartners och/eller utanför EU/EES.',
      },
      default: {
        description:
          'These cookies allow us to integrate into our website customer reviews of our products left on the Reevoo website.',
        dataShare: 'This cookie shares data with our trusted third party partners and/or outside the EU/EEA.',
      },
    },
    Twitter: {
      EN: {
        description:
          'These cookies allow Distrelec to show you relevant adverts when you navigate away from the site, also to track your journey when you click through to the site from external adverts.',
        dataShare: 'This cookie shares data with our trusted third party partners and/or outside the EU/EEA.',
      },
      CS: {
        description:
          'Tyto soubory cookie umožňují společnosti Distrelec zobrazovat vám relevantní reklamy po opuštění internetového obchodu a také sledovat vaše kroky, když se na web dostanete kliknutím na externí reklamu.',
        dataShare:
          'Tento soubor cookie sdílí data s našimi důvěryhodnými partnery z řad třetích stran a /nebo mimo EU/EHP.',
      },
      DK: {
        description:
          'Disse cookies giver Elfa Distrelec mulighed for at vise dig relevante annoncer, når du navigerer væk fra webstedet, også for at spore din rejse, når du klikker igennem til webstedet fra eksterne annoncer. ',
        dataShare: 'Denne cookie deler data med vores betroede tredjepartspartnere og/eller uden for EU/EØS.',
      },
      DE: {
        description:
          'Diese Cookies ermöglichen es Distrelec, Ihnen relevante Werbung zu zeigen, wenn Sie von der Website weg navigieren, und auch, um Ihre Reise zu verfolgen, wenn Sie sich von externen Anzeigen auf die Website durchklicken.',
        dataShare:
          'Dieses Cookie teilt Daten mit unseren vertrauenswürdigen Drittpartnern und/oder außerhalb der EU/EWR.',
      },
      EE: {
        description:
          'Need küpsised võimaldavad Elfa Distrelecil näidata Teile asjakohaseid reklaame, kui Te saidilt lahkute, ja jälgida Teie teekonda, kui Te välistes reklaamides klõpsates saidile saabute.',
        dataShare:
          'See küpsis jagab andmeid meie usaldatud kolmandast isikust partneritega ja/või väljaspool EL-i/EMP-d.',
      },
      FI: {
        description:
          'Näiden evästeiden avulla Distrelec voi näyttää sinulle asiaankuuluvia mainoksia, kun siirryt pois sivustolta, ja myös seurata matkaa, kun napsautat sivustolle ulkoisista ilmoituksista.',
        dataShare:
          'Tämä eväste jakaa tietoja yrityksen kumppaneina toimivien luotettavien kolmansien osapuolten kanssa ja/tai EU:n/ETA:n ulkopuolelle.',
      },
      FR: {
        description:
          "Ces cookies permettent à Distrelec de vous afficher des publicités pertinentes lors de votre navigation hors du site, ainsi que de suivre votre parcours lorsque vous accédez au site en cliquant sur un lien provenant d'une publicité externe.",
        dataShare: "Ce cookie partage des données avec nos partenaires tiers de confiance et/ou en dehors de l'UE/EEE.",
      },
      HU: {
        description:
          'Ezek a cookie-k lehetővé teszik a Distrelec számára, hogy kapcsolódó hirdetéseket mutasson Önnek, amikor elhagyja a weboldalt, illetve nyomon követi az Ön által megtett útvonalat, amikor egy külső hirdetésre kattintva lép a weboldalra.',
        dataShare: 'Ez a cookie adatokat oszt meg megbízható harmadik fél partnereinkkel és/vagy az EU/EGT-n kívül.',
      },
      IT: {
        description:
          'Questi cookie consentono a Distrelec di mostrarti annunci pertinenti quando esci dal sito, anche per tenere traccia del tuo viaggio quando fai clic sul sito da annunci esterni.',
        dataShare:
          "Questo cookie condivide i dati con i nostri partner terze parti fidate e/o al di fuori dell'UE/SEE.",
      },
      LT: {
        description:
          'Šie slapukai leidžia Distrelec rodyti jums aktualius skelbimus, kai einate toliau nuo svetainės, taip pat sekti savo kelią, kai spustelite svetainę iš išorinių skelbimų.',
        dataShare:
          'Šis slapukas bendrina duomenis su patikimais trečiųjų šalių partneriais ir (arba) už ES / EEE ribų.',
      },
      LV: {
        description:
          'Šie sīkfaili ļauj Elfa Distrelec parādīt jums noderīgas reklāmas, kad dodaties prom no vietnes, kā arī izsekot jūsu pārvietošanos, kad atverat vietni, noklikšķinot uz ārējām reklāmām.',
        dataShare: 'Šīs sīkdatnes kopīgo informāciju ar mūsu uzticamiem trešās puses partneriem un/vai ārpus ES/EEZ.',
      },
      NL: {
        description:
          'Deze cookies laten Distrelec toe om u relevante advertenties te tonen wanneer u weg surft van de site, alsook om uw reis te volgen wanneer u doorklikt naar de site vanuit externe advertenties.',
        dataShare: 'Deze cookie deelt gegevens met onze vertrouwde externe partners en/of buiten de EU/EER.',
      },
      NO: {
        description:
          'Disse informasjonskapslene tillater Elfa  Distrelec å vise deg relevante annonser når du navigerer vekk fra nettstedet, også for å spore reisen din når du klikker deg gjennom til siden fra eksterne annonser.',
        dataShare:
          'Denne informasjonskapselen deler data med våre pålitelige tredjepartsleverandører og/eller utenfor EU/EEA.',
      },
      PL: {
        description:
          'Te pliki cookie umożliwiają firmie Distrelec wyświetlanie powiązanych reklam, kiedy przeglądasz inne strony internetowe oraz śledzenie, w jaki sposób trafiasz na stronę, klikając na reklamy z zewnętrznych stron.',
        dataShare: 'Ten plik cookie udostępnia dane naszym zaufanym partnerom lub podmiotom spoza UE/EOG.',
      },
      RO: {
        description:
          'Aceste module cookie permit Distrelec să afişeze reclamele relevante atunci când navigaţi în afara site-ului şi să monitorizeze navigarea dvs. în site atunci când faceţi clic din reclamele externe.',
        dataShare: 'Acest modul cookie partajează date cu partenerii noştri terţi de încredere şi/sau în afara UE/SEE.',
      },
      SK: {
        description:
          'Tieto súbory cookie umožňujú spoločnosti Distrelec zobrazovať relevantné reklamy, keď odídete z lokality, a taktiež sledovať cestu, keď navštívite lokalitu prostredníctvom externých reklám.',
        dataShare:
          'Tento súbor cookie zdieľa údaje s našimi dôveryhodnými partnermi tretích strán a/alebo mimo EÚ/EHP.',
      },
      SV: {
        description:
          'Kakor som ger Elfa Distrelec möjlighet att visa dig relevanta annonser när du lämnar webbplatsen och även spåra din väg till webbplatsen när du klickat på externa annonser.',
        dataShare: 'Denna cookie delar data med våra betrodda tredjepartspartners och/eller utanför EU/EES.',
      },
      default: {
        description:
          'These cookies allow Distrelec to show you relevant adverts when you navigate away from the site, also to track your journey when you click through to the site from external adverts.',
        dataShare: 'This cookie shares data with our trusted third party partners and/or outside the EU/EEA.',
      },
    },
    Bing: {
      EN: {
        description:
          'These cookies allow Distrelec to show you relevant adverts when you navigate away from the site, also to track your journey when you click through to the site from external adverts.',
        dataShare: 'This cookie shares data with our trusted third party partners and/or outside the EU/EEA.',
      },
      CS: {
        description:
          'Tyto soubory cookie umožňují společnosti Distrelec zobrazovat vám relevantní reklamy po opuštění internetového obchodu a také sledovat vaše kroky, když se na web dostanete kliknutím na externí reklamu.',
        dataShare:
          'Tento soubor cookie sdílí data s našimi důvěryhodnými partnery z řad třetích stran a /nebo mimo EU/EHP.',
      },
      DK: {
        description:
          'Disse cookies giver Elfa Distrelec mulighed for at vise dig relevante annoncer, når du navigerer væk fra webstedet, også for at spore din rejse, når du klikker igennem til webstedet fra eksterne annoncer. ',
        dataShare: 'Denne cookie deler data med vores betroede tredjepartspartnere og/eller uden for EU/EØS.',
      },
      DE: {
        description:
          'Diese Cookies ermöglichen es Distrelec, Ihnen relevante Werbung zu zeigen, wenn Sie von der Website weg navigieren, sowie Ihre Reise zu verfolgen, wenn Sie sich von externen Anzeigen auf die Website durchklicken.',
        dataShare:
          'Dieses Cookie teilt Daten mit unseren vertrauenswürdigen Drittpartnern und/oder außerhalb der EU/EWR.',
      },
      EE: {
        description:
          'Need küpsised võimaldavad Elfa Distrelecil näidata Teile asjakohaseid reklaame, kui Te saidilt lahkute, ja jälgida Teie teekonda, kui Te välistes reklaamides klõpsates saidile saabute.',
        dataShare:
          'See küpsis jagab andmeid meie usaldatud kolmandast isikust partneritega ja/või väljaspool EL-i/EMP-d.',
      },
      FI: {
        description:
          'Näiden evästeiden avulla Distrelec voi näyttää sinulle asiaankuuluvia mainoksia, kun siirryt pois sivustolta, ja myös seurata matkaa, kun napsautat sivustolle ulkoisista ilmoituksista.',
        dataShare:
          'Tämä eväste jakaa tietoja yrityksen kumppaneina toimivien luotettavien kolmansien osapuolten kanssa ja/tai EU:n/ETA:n ulkopuolelle.',
      },
      FR: {
        description:
          "Ces cookies permettent à Distrelec de vous afficher des publicités pertinentes lors de votre navigation hors du site, ainsi que de suivre votre parcours lorsque vous accédez au site en cliquant sur un lien provenant d'une publicité externe.",
        dataShare: "Ce cookie partage des données avec nos partenaires tiers de confiance et/ou en dehors de l'UE/EEE.",
      },
      HU: {
        description:
          'Ezek a cookie-k lehetővé teszik a Distrelec számára, hogy kapcsolódó hirdetéseket mutasson Önnek, amikor elhagyja a weboldalt, illetve nyomon követi az Ön által megtett útvonalat, amikor egy külső hirdetésre kattintva lép a weboldalra.',
        dataShare: 'Ez a cookie adatokat oszt meg megbízható harmadik fél partnereinkkel és/vagy az EU/EGT-n kívül.',
      },
      IT: {
        description:
          'Questi cookie consentono a Distrelec di mostrarti annunci pertinenti quando esci dal sito, anche per tenere traccia del tuo viaggio quando fai clic sul sito da annunci esterni.',
        dataShare:
          "Questo cookie condivide i dati con i nostri partner terze parti fidate e/o al di fuori dell'UE/SEE.",
      },
      LT: {
        description:
          'Šie slapukai leidžia Distrelec rodyti jums aktualius skelbimus, kai einate toliau nuo svetainės, taip pat sekti savo kelią, kai spustelite svetainę iš išorinių skelbimų.',
        dataShare:
          'Šis slapukas bendrina duomenis su patikimais trečiųjų šalių partneriais ir (arba) už ES / EEE ribų.',
      },
      LV: {
        description:
          'Šie sīkfaili ļauj Elfa Distrelec parādīt jums noderīgas reklāmas, kad dodaties prom no vietnes, kā arī izsekot jūsu pārvietošanos, kad atverat vietni, noklikšķinot uz ārējām reklāmām.',
        dataShare: 'Šīs sīkdatnes kopīgo informāciju ar mūsu uzticamiem trešās puses partneriem un/vai ārpus ES/EEZ.',
      },
      NL: {
        description:
          'Deze cookies laten Distrelec toe om u relevante advertenties te tonen wanneer u weg surft van de site, alsook om uw reis te volgen wanneer u doorklikt naar de site vanuit externe advertenties.',
        dataShare: 'Deze cookie deelt gegevens met onze vertrouwde externe partners en/of buiten de EU/EER.',
      },
      NO: {
        description:
          'Disse informasjonskapslene tillater Elfa Distrelec å vise deg relevante annonser når du navigerer vekk fra nettstedet, også for å spore reisen din når du klikker deg gjennom til siden fra eksterne annonser.',
        dataShare:
          'Denne informasjonskapselen deler data med våre pålitelige tredjepartsleverandører og/eller utenfor EU/EEA.',
      },
      PL: {
        description:
          'Te pliki cookie umożliwiają firmie Distrelec wyświetlanie powiązanych reklam, kiedy przeglądasz inne strony internetowe oraz śledzenie, w jaki sposób trafiasz na stronę, klikając na reklamy z zewnętrznych stron.',
        dataShare: 'Ten plik cookie udostępnia dane naszym zaufanym partnerom lub podmiotom spoza UE/EOG.',
      },
      RO: {
        description:
          'Aceste module cookie permit Distrelec să afişeze reclamele relevante atunci când navigaţi în afara site-ului şi să monitorizeze navigarea dvs. în site atunci când faceţi clic din reclamele externe.',
        dataShare: 'Acest modul cookie partajează date cu partenerii noştri terţi de încredere şi/sau în afara UE/SEE.',
      },
      SK: {
        description:
          'Tieto súbory cookie umožňujú spoločnosti Distrelec zobrazovať relevantné reklamy, keď odídete z lokality, a taktiež sledovať cestu, keď navštívite lokalitu prostredníctvom externých reklám.',
        dataShare:
          'Tento súbor cookie zdieľa údaje s našimi dôveryhodnými partnermi tretích strán a/alebo mimo EÚ/EHP.',
      },
      SV: {
        description:
          'Kakor som ger Elfa Distrelec möjlighet att visa dig relevanta annonser när du lämnar webbplatsen och även spåra din väg till webbplatsen när du klickat på externa annonser.',
        dataShare: 'Denna cookie delar data med våra betrodda tredjepartspartners och/eller utanför EU/EES.',
      },
      default: {
        description:
          'These cookies allow Distrelec to show you relevant adverts when you navigate away from the site, also to track your journey when you click through to the site from external adverts.',
        dataShare: 'This cookie shares data with our trusted third party partners and/or outside the EU/EEA.',
      },
    },
    YouTube: {
      EN: {
        description:
          'YouTube is used by the Distrelec to store and show video content. These cookies are set by YouTube to track usage of its services. The YouTube Cookies are only installed when you press play.',
        dataShare: 'This cookie shares data with our trusted third party partners and/or outside the EU/EEA.',
      },
      CS: {
        description:
          'Společnost Distrelec využívá službu YouTube k ukládání a zobrazování videoobsahu. Tyto soubory cookie jsou společností YouTube nastaveny tak, aby sledovaly využívání jejích služeb. Soubory cookie služby YouTube se nainstalují pouze po stisknutí tlačítka přehrávání.',
        dataShare:
          'Tento soubor cookie sdílí data s našimi důvěryhodnými partnery z řad třetích stran a /nebo mimo EU/EHP.',
      },
      DK: {
        description:
          'YouTube bruges af Elfa Distrelec til at gemme og vise videoindhold. Disse cookies indstilles af YouTube til at spore brugen af ​​dets tjenester. YouTube-cookies installeres kun, når du trykker på play.',
        dataShare: 'Denne cookie deler data med vores betroede tredjepartspartnere og/eller uden for EU/EØS.',
      },
      DE: {
        description:
          'YouTube wird von Distrelec genutzt, um Videoinhalte zu speichern und anzuzeigen. Diese Cookies werden von YouTube gesetzt, um die Nutzung seiner Dienste zu verfolgen. Die YouTube-Cookies werden nur installiert, wenn Sie auf "Play" drücken.',
        dataShare:
          'Dieses Cookie teilt Daten mit unseren vertrauenswürdigen Drittpartnern und/oder außerhalb der EU/EWR.',
      },
      EE: {
        description:
          "Distrelec kasutab YouTube'i videosisu salvestamiseks ja näitamiseks. Need küpsised seab YouTube oma teenuste kasutamise jälgimiseks. YouTube'i küpsised paigaldatakse ainult siis, kui vaatate videoid.",
        dataShare:
          'See küpsis jagab andmeid meie usaldatud kolmandast isikust partneritega ja/või väljaspool EL-i/EMP-d.',
      },
      FI: {
        description:
          'Distrelec käyttää YouTubea videosisällön tallentamiseen ja näyttämiseen. YouTube on asettanut nämä evästeet seuraamaan palveluidensa käyttöä. YouTube-evästeet asennetaan vain, kun painat Play-painiketta.',
        dataShare:
          'Tämä eväste jakaa tietoja yrityksen kumppaneina toimivien luotettavien kolmansien osapuolten kanssa ja/tai EU:n/ETA:n ulkopuolelle.',
      },
      FR: {
        description:
          'YouTube est utilisé par Distrelec pour stocker et diffuser des contenus vidéo. Ces cookies sont mis en place par YouTube pour suivre l\'utilisation de ses services. Les cookies YouTube ne sont installés que lorsque vous appuyez sur "play".',
        dataShare: "Ce cookie partage des données avec nos partenaires tiers de confiance et/ou en dehors de l'UE/EEE.",
      },
      HU: {
        description:
          'A YouTube-ot a Distrelec használja a videotartalom tárolására és bemutatására. Ezeket a cookie-kat a YouTube állítja be, hogy nyomon követhesse a szolgáltatásai használatát. A YouTube cookie-k csak akkor kerülnek telepítésre, ha megnyomja a lejátszás gombot.',
        dataShare: 'Ez a cookie adatokat oszt meg megbízható harmadik fél partnereinkkel és/vagy az EU/EGT-n kívül.',
      },
      IT: {
        description:
          "YouTube è utilizzato da Distrelec per archiviare e mostrare contenuti video. Questi cookie sono impostati da YouTube per monitorare l'utilizzo dei suoi servizi. I cookie di YouTube vengono installati solo quando premi play.",
        dataShare:
          "Questo cookie condivide i dati con i nostri partner terze parti fidate e/o al di fuori dell'UE/SEE.",
      },
      LT: {
        description:
          'Distrelec naudoja YouTube vaizdo įrašų turinio saugojimui ir rodymui. Šiuos slapukus YouTube nustato savo paslaugų naudojimui stebėti. YouTube slapukai įdiegiami tik tada, kai paspaudžiate „Rodyti“.',
        dataShare:
          'Šis slapukas bendrina duomenis su patikimais trečiųjų šalių partneriais ir (arba) už ES / EEE ribų.',
      },
      LV: {
        description:
          'Distrelec izmanto YouTube, lai uzglabātu un rādītu video saturu. Šos sīkfailus nosaka YouTube, lai sekotu savu pakalpojumu izmantošanai. YouTube sīkfaili tiek instalēti tikai tad, kad skatāties videoklipus.',
        dataShare: 'Šīs sīkdatnes kopīgo informāciju ar mūsu uzticamiem trešās puses partneriem un/vai ārpus ES/EEZ.',
      },
      NL: {
        description:
          'YouTube wordt door de Distrelec gebruikt om video-inhoud op te slaan en te tonen. Deze cookies worden door YouTube geplaatst om het gebruik van haar diensten bij te houden. De YouTube Cookies worden alleen geplaatst wanneer u op play drukt.',
        dataShare: 'Deze cookie deelt gegevens met onze vertrouwde externe partners en/of buiten de EU/EER.',
      },
      NO: {
        description:
          'YouTube brukes av Elfa Distrelec til å lagre og vise videoinnhold. Disse informasjonskapslene stilles inn av YouTube for å spore bruken av tjenestene. YouTube-informasjonskapslene installeres bare når du trykker på play.',
        dataShare:
          'Denne informasjonskapselen deler data med våre pålitelige tredjepartsleverandører og/eller utenfor EU/EEA.',
      },
      PL: {
        description:
          'YouTube jest używany przez Distrelec do przechowywania i wyświetlania materiałów wideo. Te pliki cookie są ustawiane przez YouTube w celu śledzenia korzystania z jego usług. Pliki cookie YouTube są instalowane tylko po naciśnięciu przycisku play.',
        dataShare: 'Ten plik cookie udostępnia dane naszym zaufanym partnerom lub podmiotom spoza UE/EOG.',
      },
      RO: {
        description:
          'YouTube este utilizat de Distrelec pentru a stoca şi a afişa conţinut video. Aceste module cookie sunt setate de YouTube pentru a urmări utilizarea serviciilor sale. Modulele cookie YouTube se instalează doar atunci când apăsaţi pe redare.',
        dataShare: 'Acest modul cookie partajează date cu partenerii noştri terţi de încredere şi/sau în afara UE/SEE.',
      },
      SK: {
        description:
          'Spoločnosť Distrelec používa službu YouTube na ukladanie a zobrazovanie video obsahu. Tieto súbory cookie nastavuje služba YouTube na sledovanie využívania jej služieb. Súbory cookie služby YouTube sa nainštalujú iba po stlačení tlačidla Prehrať.',
        dataShare:
          'Tento súbor cookie zdieľa údaje s našimi dôveryhodnými partnermi tretích strán a/alebo mimo EÚ/EHP.',
      },
      SV: {
        description:
          'YouTube används av Elfa Distrelec för att lagra och visa videoinnehåll. Dessa kakor ställs in av YouTube för att spåra användningen av deras tjänster. YouTube-kakorna installeras bara när du trycker på play.',
        dataShare: 'Denna cookie delar data med våra betrodda tredjepartspartners och/eller utanför EU/EES.',
      },
      default: {
        description:
          'YouTube is used by the Distrelec to store and show video content. These cookies are set by YouTube to track usage of its services. The YouTube Cookies are only installed when you press play.',
        dataShare: 'This cookie shares data with our trusted third party partners and/or outside the EU/EEA.',
      },
    },
    'Wistia Videos': {
      EN: {
        description:
          "This cookie is placed by our video hosting site Wistia and is used to support video functionality that may be found throughout the website. In addition to keeping track of the website visitor's position in a video should playback be interrupted, this cookie also notes user behaviour regarding the video itself.",
        dataShare: 'This cookie shares data with our trusted third party partners and/or outside the EU/EEA.',
      },
      CS: {
        description:
          'Tento soubor cookie je vytvářen naší webovou stránkou Wistia pro hostování videa a slouží pro podporu funkcí videa napříč našimi webovými stránkami. Kromě sledování pozice videa při přerušení přehrávání návštěvníkem webu tento soubor cookie zaznamenává také interakci uživatele se samotným videem.',
        dataShare:
          'Tento soubor cookie sdílí data s našimi důvěryhodnými partnery z řad třetích stran a /nebo mimo EU/EHP.',
      },
      DK: {
        description:
          'Denne cookie placeres af vores videohosting-site Wistia og bruges til at understøtte videofunktionalitet, som findes på hele hjemmesiden. Ud over at holde styr på webstedsbesøgendes position i en video, hvis afspilning afbrydes, noterer denne cookie også brugeradfærd med hensyn til selve videoen.',
        dataShare: 'Denne cookie deler data med vores betroede tredjepartspartnere og/eller uden for EU/EØS.',
      },
      DE: {
        description:
          'Dieses Cookie wird von unserer Video-Hosting-Site Wistia gesetzt und dient der Unterstützung von Videofunktionen, die auf der gesamten Website zu finden sind. Dieses Cookie verfolgt nicht nur die Position des Website-Besuchers in einem Video, falls die Wiedergabe unterbrochen wird, sondern notiert auch das Benutzerverhalten in Bezug auf das Video selbst.',
        dataShare:
          'Dieses Cookie teilt Daten mit unseren vertrauenswürdigen Drittpartnern und/oder außerhalb der EU/EWR.',
      },
      EE: {
        description:
          'Selle küpsise paigaldab veebisait Wistia ja seda kasutatakse kogu veebisaidil olevate videote funktsionaalsuse toetamiseks. See küpsis ei jälgi mitte ainult vaataja asukohta video sees, kui esitus katkestatakse, vaid märgib ka kasutaja tegevusi seoses video endaga.',
        dataShare:
          'See küpsis jagab andmeid meie usaldatud kolmandast isikust partneritega ja/või väljaspool EL-i/EMP-d.',
      },
      FI: {
        description:
          'Tämän evästeen sijoittaa videopalvelusivustomme Wistia, ja sitä käytetään videotoimintojen tukemiseen, joita saattaa olla kaikkialla verkkosivustossa. Sen lisäksi, että seurataan verkkosivuston kävijän asemaa videossa, keskeytetään toisto, tämä eväste huomauttaa myös käyttäjän käyttäytymisen itse videon suhteen.',
        dataShare:
          'Tämä eväste jakaa tietoja yrityksen kumppaneina toimivien luotettavien kolmansien osapuolten kanssa ja/tai EU:n/ETA:n ulkopuolelle.',
      },
      FR: {
        description:
          "Ce cookie est placé par notre site d'hébergement vidéo Wistia et est utilisé pour prendre en charge la fonctionnalité vidéo qui peut se trouver sur l'ensemble du site Web. En plus de garder la position du visiteur du site web dans une vidéo si la lecture est interrompue, ce cookie note également le comportement de l'utilisateur concernant la vidéo elle-même.",
        dataShare: "Ce cookie partage des données avec nos partenaires tiers de confiance et/ou en dehors de l'UE/EEE.",
      },
      HU: {
        description:
          'Ezt a cookie-t a Wistia videotárhely-oldalunk helyezi el, és a weboldalon megtalálható videó funkciók támogatására szolgál. Amellett, hogy a lejátszás esetleges megszakadása miatt nyomon követi a weboldal látogatójának pozícióját a videókban, a süti a videóval kapcsolatos felhasználói viselkedés adatait is tárolja.',
        dataShare: 'Ez a cookie adatokat oszt meg megbízható harmadik fél partnereinkkel és/vagy az EU/EGT-n kívül.',
      },
      IT: {
        description:
          "Questo cookie viene inserito dal nostro sito di hosting video Wistia e viene utilizzato per supportare la funzionalità video che può essere trovata in tutto il sito web. Oltre a tenere traccia della posizione del visitatore del sito web in un video in caso di interruzione della riproduzione, questo cookie rileva anche il comportamento dell'utente riguardo al video stesso.",
        dataShare:
          "Questo cookie condivide i dati con i nostri partner terze parti fidate e/o al di fuori dell'UE/SEE.",
      },
      LT: {
        description:
          'Šį slapuką įdėjo mūsų vaizdo įrašų talpinimo svetainė Wistia ir jis naudojamas palaikyti vaizdo funkciją, kurią galima rasti visoje svetainėje. Šis slapukas ne tik stebi svetainės lankytojo padėtį vaizdo įraše, bet ir turi nutraukti atkūrimą, taip pat atkreipia dėmesį į vartotojo elgesį dėl paties vaizdo įrašo.',
        dataShare:
          'Šis slapukas bendrina duomenis su patikimais trečiųjų šalių partneriais ir (arba) už ES / EEE ribų.',
      },
      LV: {
        description:
          'Šo sīkfailu ievieto videoklipu izvietošanas vietne Wistia, un tas tiek izmantots, lai atbalstītu to videoklipu funkcionalitāti, ko var atrast visā vietnē. Šis sīkfails ne tikai seko līdzi video skatītāja atrašanās vietai videoklipā, ja atskaņošana tiek pārtraukta, bet arī atzīmē lietotāja darbības saistībā ar pašu videoklipu.',
        dataShare: 'Šīs sīkdatnes kopīgo informāciju ar mūsu uzticamiem trešās puses partneriem un/vai ārpus ES/EEZ.',
      },
      NL: {
        description:
          'Deze cookie wordt geplaatst door onze video-hostingsite Wistia en wordt gebruikt ter ondersteuning van videofunctionaliteit die op de website kan worden aangetroffen. Naast het bijhouden van de positie van de websitebezoeker in een video indien het afspelen wordt onderbroken, noteert deze cookie ook gebruikersgedrag met betrekking tot de video zelf.',
        dataShare: 'Deze cookie deelt gegevens met onze vertrouwde externe partners en/of buiten de EU/EER.',
      },
      NO: {
        description:
          'Denne informasjonskapselen plasseres av vårt videostedingsnettsted Wistia og brukes til å støtte videofunksjonalitet som finnes på hele nettstedet. I tillegg til å holde rede på besøkendes posisjon i en video hvis avspillingen skulle bli avbrutt, noterer denne informasjonskapselen også brukeratferd angående selve videoen.',
        dataShare:
          'Denne informasjonskapselen deler data med våre pålitelige tredjepartsleverandører og/eller utenfor EU/EEA.',
      },
      PL: {
        description:
          'Ten plik cookie jest umieszczany przez naszą stronę hostingową wideo Wistia i służy do obsługi funkcji wideo, które można znaleźć na stronie internetowej. Oprócz śledzenia pozycji użytkownika w filmie w przypadku przerwania odtwarzania, ten plik cookie rejestruje również zachowanie użytkownika w odniesieniu do samego filmu.',
        dataShare: 'Ten plik cookie udostępnia dane naszym zaufanym partnerom lub podmiotom spoza UE/EOG.',
      },
      RO: {
        description:
          'Acest modul cookie este plasat de site-ul de găzduire video Wistia şi este utilizat pentru a susţine funcţionalitatea video care se poate regăsi peste pe tot site-ul web. Pe lângă urmărirea poziţiei vizitatorului site-ului web în cazul în care redarea videoclipului se întrerupe, acest modul cookie observă, de asemenea, comportamentul utilizatorului cu privire la videoclipul în sine.',
        dataShare: 'Acest modul cookie partajează date cu partenerii noştri terţi de încredere şi/sau în afara UE/SEE.',
      },
      SK: {
        description:
          'Tento súbor cookie umiestňuje naša lokalita na hosting videa Wistia a používa sa na podporu funkcií videa, ktoré sa môžu nachádzať na tejto webovej lokalite. Okrem sledovania polohy, v ktorej sa návštevník webovej lokality nachádza v rámci videa v prípade, že dôjde k prerušeniu prehrávania, tento súbor cookie zaznamenáva aj správanie používateľa týkajúce sa samotného videa.',
        dataShare:
          'Tento súbor cookie zdieľa údaje s našimi dôveryhodnými partnermi tretích strán a/alebo mimo EÚ/EHP.',
      },
      SV: {
        description:
          'Denna kaka placeras av vårt webbhotell Wistia och används för att stödja videofunktionalitet som finns på webbplatsen. Förutom att hålla reda på webbplatsens besökares position i en video om uppspelningen skulle avbrytas, noterar denna cookie också användarnas beteende för själva videon.',
        dataShare: 'Denna cookie delar data med våra betrodda tredjepartspartners och/eller utanför EU/EES.',
      },
      default: {
        description:
          "This cookie is placed by our video hosting site Wistia and is used to support video functionality that may be found throughout the website. In addition to keeping track of the website visitor's position in a video should playback be interrupted, this cookie also notes user behaviour regarding the video itself.",
        dataShare: 'This cookie shares data with our trusted third party partners and/or outside the EU/EEA.',
      },
    },
  },
  default: {
    Fusion: {
      SV_EN: {
        description:
          'Fusion is an AI-powered technology for search, navigation, merchandising, personalization and recommendations. Most of the cookies "session cookies." They are automatically deleted after your visit. Other cookies remain in your device\'s memory until you delete them. These cookies make it possible to recognize your browser when you next visit the site. Cookies help website more user-friendly, efficient, and secure. It is used to track clicks, carts and checkouts.',
      },
      SV: {
        description:
          'Fusion är en AI-driven teknik för sökning, navigering, marknadsföring, personalisering och rekommendationer. De flesta av kakorna är "sessionskakor." De raderas automatiskt efter ditt besök. Andra kakor finns kvar i enhetens minne tills du tar bort dem. Dessa kakor gör det möjligt att känna igen din webbläsare när du besöker webbplatsen nästa gång. Kakor gör webbplatsen mer användarvänlig, effektiv och säker. Används för att spåra klick, kundvagnar och kassor.',
      },
    },
    FactFinder: {
      EN: {
        description:
          'FACT-Finder is an AI-powered technology for search, navigation, merchandising, personalization and recommendations. Most of the cookies "session cookies." They are automatically deleted after your visit. Other cookies remain in your device\'s memory until you delete them. These cookies make it possible to recognize your browser when you next visit the site. Cookies help website more user-friendly, efficient, and secure. It is used to track clicks, carts and checkouts.',
        dataShare: 'This cookie shares data with our trusted third party partners and/or outside the EU/EEA.',
      },
      CS: {
        description:
          'FACT-Finder je technologie využívající umělou inteligenci, která slouží pro vyhledávání, navigaci, obchodování, personalizaci a doporučení. Většinu souborů cookie tvoří „soubory cookie aktuální relace“. Po vaší návštěvě se automaticky odstraní. Ostatní soubory cookie zůstanou v paměti zařízení, dokud je neodstraníte. Tyto soubory cookie umožňují rozpoznat váš prohlížeč při následující návštěvě webu. Soubory cookie webové stránce pomáhají, aby byla uživatelsky přívětivější, efektivnější a bezpečnější. Používají se ke sledování klikání, košíků a pokladen.',
        dataShare:
          'Tento soubor cookie sdílí data s našimi důvěryhodnými partnery z řad třetích stran a /nebo mimo EU/EHP.',
      },
      DK: {
        description:
          'FACT-Finder er en AI-drevet teknologi til søgning, navigation, merchandising, personalisering og anbefalinger. De fleste af cookies er "sessionscookies". De slettes automatisk efter dit besøg. Andre cookies forbliver i enhedens hukommelse, indtil du sletter dem. Disse cookies gør det muligt at genkende din browser, når du næste gang besøger webstedet. Cookies gør hjemmesiden mere brugervenligt, effektivt og sikkert. Det bruges til at spore klik, indkøbsvogne og checkouts.',
        dataShare: 'Denne cookie deler data med vores betroede tredjepartspartnere og/eller uden for EU/EØS.',
      },
      DE: {
        description:
          'FACT-Finder ist eine KI-gestützte Technologie für Suche, Navigation, Merchandising, Personalisierung und Empfehlungen. Die meisten dieser Cookies sind "Session-Cookies". Sie werden nach Ihrem Besuch automatisch gelöscht. Andere Cookies verbleiben im Speicher Ihres Geräts, bis Sie diese löschen. Diese Cookies ermöglichen es, Ihren Browser beim nächsten Besuch wiederzuerkennen. Cookies dienen dazu, unser Angebot nutzerfreundlicher, effektiver und sicherer zu machen. Sie werden verwendet, um Klicks, Warenkörbe und Checkouts zu verfolgen.',
        dataShare:
          'Dieses Cookie teilt Daten mit unseren vertrauenswürdigen Drittpartnern und/oder außerhalb der EU/EWR.',
      },
      EE: {
        description:
          'FACT-Finder on tehisintellektipõhine tehnoloogia otsinguks, navigeerimiseks, müügiks, personaliseerimiseks ja soovituste andmiseks. Enamik küpsiseid on "seansiküpsised". Need kustutatakse automaatselt pärast teie külastust. Teised küpsised jäävad teie seadme mällu, kuni need kustutate. Need küpsised võimaldavad teie brauseri ära tunda, kui külastate veebilehte järgmine kord. Küpsised aitavad veebilehte kasutajasõbralikumaks, tõhusamaks ja turvalisemaks muuta. Neid kasutatakse klikkide, ostukorvide ja tellimuste jälgimiseks.',
        dataShare:
          'See küpsis jagab andmeid meie usaldatud kolmandast isikust partneritega ja/või väljaspool EL-i/EMP-d.',
      },
      FI: {
        description:
          'FACT-Finder on tekoälypohjainen tekniikka etsintään, navigointiin, markkinointiin, personointiin ja suosituksiin. Suurin osa evästeistä on "istuntoevästeitä". Ne poistetaan automaattisesti vierailusi jälkeen. Muut evästeet säilyvät laitteen muistissa, kunnes poistat ne. Nämä evästeet mahdollistavat selaimesi tunnistamisen, kun seuraavan kerran vierailet sivustolla. Evästeiden avulla verkkosivusto on käyttäjäystävällisempi, tehokkaampi ja turvallisempi. Sitä käytetään napsautusten, kärryjen ja kassalle seuraamiseen.',
        dataShare:
          'Tämä eväste jakaa tietoja yrityksen kumppaneina toimivien luotettavien kolmansien osapuolten kanssa ja/tai EU:n/ETA:n ulkopuolelle.',
      },
      FR: {
        description:
          "FACT-Finder est une technologie alimentée par l'IA pour la recherche, la navigation, le merchandising, la personnalisation et les recommandations. La plupart des cookies sont des \"cookies de session\". Ils sont automatiquement supprimés après votre visite. D'autres cookies restent dans la mémoire de votre appareil jusqu'à ce que vous les supprimiez. Ces cookies permettent de reconnaître votre navigateur lors de votre prochaine visite sur le site.  Les cookies rendent le site web plus convivial, plus efficace et plus sûr. Ils sont utilisés pour suivre les clics, les paniers et les commandes.",
        dataShare: "Ce cookie partage des données avec nos partenaires tiers de confiance et/ou en dehors de l'UE/EEE.",
      },
      HU: {
        description:
          'A FACT-Finder egy AI-alapú technológia kereséshez, navigációhoz, reklámanyagok árusításához, személyre szabáshoz és ajánlásokhoz. A cookie-k többsége „munkamenet cookie”. Ezek a felhasználói látogatás után automatikusan törlődnek. A többi cookie a készülék memóriájában marad, amíg nem törli őket. Ezek a cookie-k lehetővé teszik a böngésző felismerését az oldal legközelebbi felkeresésekor. A cookie-k segítségével a weboldal felhasználóbarátabb, hatékonyabb és biztonságosabb. A kattintások, bevásárlókosarak és pénztárak nyomon követésére szolgál.',
        dataShare: 'Ez a cookie adatokat oszt meg megbízható harmadik fél partnereinkkel és/vagy az EU/EGT-n kívül.',
      },
      IT: {
        description:
          'FACT-Finder è una tecnologia basata sull\'intelligenza artificiale per la ricerca, la navigazione, il merchandising, la personalizzazione e i consigli. La maggior parte dei cookie "cookie di sessione". Vengono automaticamente cancellati dopo la tua visita. Altri cookie rimangono nella memoria del tuo dispositivo finché non li elimini. Questi cookie consentono di riconoscere il tuo browser la prossima volta che visiti il sito. I cookie aiutano il sito web a essere più user-friendly, efficiente e sicuro. Viene utilizzato per tenere traccia di clic, carrelli e checkout.',
        dataShare:
          "Questo cookie condivide i dati con i nostri partner terze parti fidate e/o al di fuori dell'UE/SEE.",
      },
      LT: {
        description:
          'FACT-Finder yra dirbtiniu intelektu paremta paieškos, naršymo, prekių pardavimo, personalizavimo ir rekomendacijų technologija. Dauguma slapukų yra „sesijos slapukai“. Po apsilankymo jie automatiškai ištrinami. Kiti slapukai lieka jūsų įrenginio atmintyje, kol jų neištrinsite. Šie slapukai leidžia atpažinti jūsų naršyklę, kai kitą kartą lankotės svetainėje. Slapukai padeda svetainei patogiau naudotis, efektyviau ir saugiau. Jis naudojamas paspaudimams, prekių krepšeliams ir išsiregistravimui stebėti.',
        dataShare:
          'Šis slapukas bendrina duomenis su patikimais trečiųjų šalių partneriais ir (arba) už ES / EEE ribų.',
      },
      LV: {
        description:
          'FACT-Finder ir mākslīgā intelekta darbināta tehnoloģija meklēšanai, navigācijai, preču atlasei, personalizācijai un ieteikumiem. Lielākā daļa sīkfailu ir "sesijas sīkfaili". Tie tiek automātiski dzēsti pēc vietnes apmeklējuma. Citi sīkfaili paliek jūsu ierīces atmiņā līdz tie tiek izdzēsti. Šie sīkfaili ļauj atpazīt jūsu pārlūkprogrammu, kad nākamreiz apmeklējat vietni. Sīkfaili palīdz vietnei būt lietotājam draudzīgākai, efektīvākai un drošākai. Tie tiek izmantoti, lai izsekotu klikšķus, grozus un pasūtījumus.',
        dataShare: 'Šīs sīkdatnes kopīgo informāciju ar mūsu uzticamiem trešās puses partneriem un/vai ārpus ES/EEZ.',
      },
      NL: {
        description:
          'FACT-Finder is een AI-aangedreven technologie voor zoeken, navigatie, merchandising, personalisatie en aanbevelingen. De meeste van deze cookies zijn "sessie cookies." Ze worden automatisch verwijderd na uw bezoek. Andere cookies blijven in het geheugen van uw apparaat totdat u ze verwijdert. Deze cookies maken het mogelijk uw browser te herkennen bij uw volgende bezoek aan de site. Cookies helpen websites gebruiksvriendelijker, efficiënter en veiliger te maken. Ze worden gebruikt om klikken, winkelwagens en kassa\'s te volgen.',
        dataShare: 'Deze cookie deelt gegevens met onze vertrouwde externe partners en/of buiten de EU/EER.',
      },
      NO: {
        description:
          'FACT-Finder er en AI-drevet teknologi for søk, navigering, merchandising, personalisering og anbefalinger. De fleste av informasjonskapslene "sesjonskapsler." De slettes automatisk etter besøket. Andre informasjonskapsler forblir i enhetens minne til du sletter dem. Disse informasjonskapslene gjør det mulig å gjenkjenne nettleseren din neste gang du besøker nettstedet. Cookies hjelper nettstedet mer brukervennlig, effektivt og sikkert. Den brukes til å spore klikk, vogner og kasser.',
        dataShare:
          'Denne informasjonskapselen deler data med våre pålitelige tredjepartsleverandører og/eller utenfor EU/EEA.',
      },
      PL: {
        description:
          'FACT-Finder to technologia wykorzystująca SI, umożliwiająca wyszukiwanie, nawigację, merchandising, indywidualizację i rekomendacje. Większość plików cookie „pliki cookie sesji”. Są one automatycznie usuwane po wizycie. Pozostałe pliki cookie pozostają w pamięci urządzenia do czasu ich usunięcia. Te pliki cookie umożliwiają rozpoznanie przeglądarki podczas następnej wizyty na stronie. Pliki cookie zapewniają, że strona internetowa jest bardziej intuicyjna dla użytkownika, wydajniejsza i bezpieczniejsza. Służy do śledzenia kliknięć, koszyków i zakupów.',
        dataShare: 'Ten plik cookie udostępnia dane naszym zaufanym partnerom lub podmiotom spoza UE/EOG.',
      },
      RO: {
        description:
          'FACT-Finder este o tehnologie bazată pe IA pentru căutare, navigare, comercializare, personalizare şi recomandări. Majoritatea modulelor cookie sunt „module cookie de sesiune.” Acestea sunt şterse automat după vizita dvs. Alte module cookie rămân în memoria dispozitivului dvs. până când le ştergeţi. Aceste module cookie fac posibilă recunoaşterea browserului dvs. data viitoare când vizitaţi site-ul. Modulele cookie ajută site-ul web să fie mai uşor de folosit, eficient şi sigur. Este folosit pentru a urmări clicurile, coşurile de cumpărături şi cumpărăturile finalizate.',
        dataShare: 'Acest modul cookie partajează date cu partenerii noştri terţi de încredere şi/sau în afara UE/SEE.',
      },
      SK: {
        description:
          'FACT-Finder je technológia založená na umelej inteligencii určená na vyhľadávanie, navigáciu, podporu predaja, prispôsobenie a odporúčania. Väčšina súborov cookie sú „súbory cookie relácie“. Po vašej návšteve sa automaticky odstránia. Ostatné súbory cookie zostávajú v pamäti vášho zariadenia, kým ich neodstránite. Tieto súbory cookie umožňujú rozpoznať váš prehliadač pri ďalšej návšteve lokality. Súbory cookie pomáhajú webovým lokalitám byť používateľsky prívetivejšie, efektívnejšie a bezpečnejšie. Používajú sa na sledovanie kliknutí, nákupných košíkov a platieb.',
        dataShare:
          'Tento súbor cookie zdieľa údaje s našimi dôveryhodnými partnermi tretích strán a/alebo mimo EÚ/EHP.',
      },
      SV: {
        description:
          'FACT-Finder är en AI-driven teknik för sökning, navigering, marknadsföring, personalisering och rekommendationer. De flesta av kakorna är "sessionskakor." De raderas automatiskt efter ditt besök. Andra kakor finns kvar i enhetens minne tills du tar bort dem. Dessa kakor gör det möjligt att känna igen din webbläsare när du besöker webbplatsen nästa gång. Kakor gör webbplatsen mer användarvänlig, effektiv och säker. Används för att spåra klick, kundvagnar och kassor.',
        dataShare: 'Denna cookie delar data med våra betrodda tredjepartspartners och/eller utanför EU/EES.',
      },
      default: {
        description:
          'FACT-Finder is an AI-powered technology for search, navigation, merchandising, personalization and recommendations. Most of the cookies "session cookies." They are automatically deleted after your visit. Other cookies remain in your device\'s memory until you delete them. These cookies make it possible to recognize your browser when you next visit the site. Cookies help website more user-friendly, efficient, and secure. It is used to track clicks, carts and checkouts.',
        dataShare: 'This cookie shares data with our trusted third party partners and/or outside the EU/EEA.',
      },
    },
    'Component Search': {
      EN: {
        description:
          'Triggered as part of traceparts integration (if this is not accepted then the footprint icon is not displayed). It includes componentsearchengine.com domain.',
        dataShare: 'This cookie shares data with our trusted third party partners and/or outside the EU/EEA.',
      },
      CS: {
        description:
          'Spouští se jako integrovaná součást služby Traceparts (pokud nejsou podmínky přijaty, nezobrazí se ikona stopy). Zahrnuje doménu componentsearchengine.com',
        dataShare:
          'Tento soubor cookie sdílí data s našimi důvěryhodnými partnery z řad třetích stran a /nebo mimo EU/EHP.',
      },
      DK: {
        description:
          'Udløst som en del af integrationen af ​​sporstykker (hvis dette ikke accepteres, vises fodaftrykikonet ikke). Det inkluderer componentearchengine.com domæne',
        dataShare: 'Denne cookie deler data med vores betroede tredjepartspartnere og/eller uden for EU/EØS.',
      },
      DE: {
        description:
          'Wird als Teil der Traceparts-Integration ausgelöst (wenn diese nicht akzeptiert wird, wird das Footprint-Symbol nicht angezeigt). Sie umfasst die Domain componentsearchengine.com',
        dataShare:
          'Dieses Cookie teilt Daten mit unseren vertrauenswürdigen Drittpartnern und/oder außerhalb der EU/EWR.',
      },
      EE: {
        description:
          "Käivitatakse Traceparts'i integreerimise osana (kui seda ei aktsepteerita, siis ei kuvata ikooni). See sisaldab componentsearchengine.com domeeni",
        dataShare:
          'See küpsis jagab andmeid meie usaldatud kolmandast isikust partneritega ja/või väljaspool EL-i/EMP-d.',
      },
      FI: {
        description:
          'Käynnistetty osana seurantaosien integraatiota (jos tätä ei hyväksytä, jalanjälkikuvaketta ei näytetä). Se sisältää componentsearchengine.com-verkkotunnuksen',
        dataShare:
          'Tämä eväste jakaa tietoja yrityksen kumppaneina toimivien luotettavien kolmansien osapuolten kanssa ja/tai EU:n/ETA:n ulkopuolelle.',
      },
      FR: {
        description:
          "Déclenché dans le cadre de l'intégration de Traceparts (si cela n'est pas accepté, l'icône d'empreinte n'est pas affichée). Il comprend le domaine componentsearchengine.com",
        dataShare: "Ce cookie partage des données avec nos partenaires tiers de confiance et/ou en dehors de l'UE/EEE.",
      },
      HU: {
        description:
          'A traceparts-integráció részeként aktiválódik (ha nem kerül elfogadásra, a lábnyom ikon nem jelenik meg). Ez magában foglalja a componentsearchengine.com tartományt',
        dataShare: 'Ez a cookie adatokat oszt meg megbízható harmadik fél partnereinkkel és/vagy az EU/EGT-n kívül.',
      },
      IT: {
        description:
          "Attivato come parte dell'integrazione di traceparts (se non viene accettato, l'icona dell'impronta non viene visualizzata). Include il dominio componentsearchengine.com",
        dataShare:
          "Questo cookie condivide i dati con i nostri partner terze parti fidate e/o al di fuori dell'UE/SEE.",
      },
      LT: {
        description:
          'Suaktyvinamas kaip sekimo dalių integravimo dalis (jei tai nepriimta, pėdsako piktograma nerodoma). Jame yra domenas componentsearchengine.com',
        dataShare:
          'Šis slapukas bendrina duomenis su patikimais trečiųjų šalių partneriais ir (arba) už ES / EEE ribų.',
      },
      LV: {
        description:
          'Tiek aktivizēts kā daļa no Traceparts integrācijas (ja tas nav pieņemts, ikona netiek rādīta). Tas ietver componentsearchengine.com domēnu',
        dataShare: 'Šīs sīkdatnes kopīgo informāciju ar mūsu uzticamiem trešās puses partneriem un/vai ārpus ES/EEZ.',
      },
      NL: {
        description:
          'Getriggerd als onderdeel van traceparts integratie (als dit niet wordt geaccepteerd dan wordt het footprint icoontje niet getoond). Het omvat componentsearchengine.com domein',
        dataShare: 'Deze cookie deelt gegevens met onze vertrouwde externe partners en/of buiten de EU/EER.',
      },
      NO: {
        description:
          'Utløses som en del av integrering av sporstykker (hvis dette ikke godtas, vises ikke fotavtrykksikonet). Det inkluderer componentearchengine.com domene',
        dataShare:
          'Denne informasjonskapselen deler data med våre pålitelige tredjepartsleverandører og/eller utenfor EU/EEA.',
      },
      PL: {
        description:
          'Wyzwalane w ramach integracji z Traceparts (jeśli nie jest to zaakceptowane, ikona śladu nie jest wyświetlana). Zawiera domenę componentsearchengine.com',
        dataShare: 'Ten plik cookie udostępnia dane naszym zaufanym partnerom lub podmiotom spoza UE/EOG.',
      },
      RO: {
        description:
          'Declanşat ca parte a integrării de traceparts (dacă acest lucru nu este acceptat, atunci pictograma amprentă nu este afişată). Include domeniul componentsearchengine.com',
        dataShare: 'Acest modul cookie partajează date cu partenerii noştri terţi de încredere şi/sau în afara UE/SEE.',
      },
      SK: {
        description:
          'Spustí sa ako súčasť integrácie služieb Traceparts (ak sa neschváli, ikona stopy sa nezobrazuje). Zahŕňa doménu componentsearchengine.com',
        dataShare:
          'Tento súbor cookie zdieľa údaje s našimi dôveryhodnými partnermi tretích strán a/alebo mimo EÚ/EHP.',
      },
      SV: {
        description:
          'Utlöses som en del av integrationen av spårdelar (om detta inte accepteras visas inte fotavtrycksikonen). Det inkluderar komponenterearchengine.com domain',
        dataShare: 'Denna cookie delar data med våra betrodda tredjepartspartners och/eller utanför EU/EES.',
      },
      default: {
        description:
          'Triggered as part of traceparts integration (if this is not accepted then the footprint icon is not displayed). It includes componentsearchengine.com domain.',
        dataShare: 'This cookie shares data with our trusted third party partners and/or outside the EU/EEA.',
      },
    },
    'Javascript Crypto Library': {
      EN: {
        description:
          "These cookies are used to maximize network resources, manage traffic, and protect customer's sites from malicious traffic.",
        dataShare: 'This cookie shares data outside the EU/EEA.',
      },
      CS: {
        description:
          'Tyto soubory cookie se využívají pro maximalizaci síťových zdrojů, správu provozu a ochranu webů zákazníků před škodlivým provozem.',
        dataShare: 'Tento soubor cookie sdílí data mimo EU/EHP.',
      },
      DK: {
        description:
          'Disse cookies bruges til at maksimere netværksressourcer, styre trafik og beskytte kundens websteder mod ondsindet trafik.',
        dataShare: 'Denne cookie deler data uden for EU/EØS.',
      },
      DE: {
        description:
          'Diese Cookies werden verwendet, um die Netzwerkressourcen zu maximieren, den Datenverkehr zu verwalten und die Websites der Kunden vor bösartigem Datenverkehr zu schützen.',
        dataShare: 'Dieses Cookie gibt Daten nach ausserhalb der EU/EWR weiter.',
      },
      EE: {
        description:
          'Neid küpsiseid kasutatakse võrguressursside maksimeerimiseks, andmevoo haldamiseks ja kliendi veebisaitide kaitsmiseks pahatahtliku andmevoo eest.',
        dataShare: 'See küpsis jagab andmeid väljaspool EL-i/EMP-d.',
      },
      FI: {
        description:
          'Näitä evästeitä käytetään maksimoimaan verkkoresurssit, hallitsemaan liikennettä ja suojaamaan asiakkaan sivustoja haitalliselta liikenteeltä.',
        dataShare: 'Tämä eväste jakaa tietoja EU:n/ETA:n ulkopuolelle.',
      },
      FR: {
        description:
          'Ces cookies sont utilisés pour optimiser les ressources du réseau, gérer le trafic et protéger les sites des clients contre le trafic malveillant.',
        dataShare: "Ce cookie partage des données en dehors de l'UE/EEE.",
      },
      HU: {
        description:
          'Ezek a cookie-k a hálózati erőforrások maximalizálására, forgalomkezelésre és az ügyfelek webhelyeinek rosszindulatú forgalomtól való védelmére szolgálnak.',
        dataShare: 'Ez a cookie az EU-n/EGT-n kívül oszt meg adatokat.',
      },
      IT: {
        description:
          'Questi cookie vengono utilizzati per massimizzare le risorse di rete, gestire il traffico e proteggere i siti dei clienti da traffico dannoso.',
        dataShare: "Questo cookie condivide i dati al di fuori dell'UE/SEE.",
      },
      LT: {
        description:
          'Šie slapukai naudojami siekiant padidinti tinklo išteklius, valdyti srautą ir apsaugoti kliento svetaines nuo kenkėjiško srauto.',
        dataShare: 'Šis slapukas bendrina duomenis už ES / EEE ribų.',
      },
      LV: {
        description:
          'Šie sīkfaili tiek lietoti, lai maksimāli izmantotu tīkla resursus, pārvaldītu datplūsmu un aizsargātu klienta vietnes no ļaunprātīgas datplūsmas.',
        dataShare: 'Šīs sīkdatnes kopīgo informāciju ārpus ES/EEZ.',
      },
      NL: {
        description:
          'Deze cookies worden gebruikt om de netwerkbronnen te maximaliseren, het verkeer te beheren en de sites van de klant te beschermen tegen kwaadwillig verkeer.',
        dataShare: 'Deze cookie deelt gegevens buiten de EU/EER.',
      },
      NO: {
        description:
          'Disse informasjonskapslene brukes til å maksimere nettverksressurser, administrere trafikk og beskytte kundens nettsteder mot skadelig trafikk.',
        dataShare: 'Denne informasjonskapselen deler data utenfor EU/EEA.',
      },
      PL: {
        description:
          'Te pliki cookie są używane do maksymalnego wykorzystania zasobów sieciowych, zarządzania ruchem i ochrony witryn klientów przed szkodliwym działaniem.',
        dataShare: 'Ten plik cookie udostępnia dane poza UE/EOG.',
      },
      RO: {
        description:
          'Aceste module cookie sunt utilizate pentru a maximiza resursele reţelei, a gestiona traficul şi a proteja site-urile clientului de traficul rău intenţionat.',
        dataShare: 'Acest modul cookie partajează date în afara UE/SEE.',
      },
      SK: {
        description:
          'Tieto súbory cookie sa používajú na maximalizáciu sieťových zdrojov, správu prenosov a ochranu lokalít zákazníka pred škodlivými prenosmi.',
        dataShare: 'Tento súbor cookie zdieľa údaje mimo EÚ/EHP.',
      },
      SV: {
        description:
          'Dessa kakor används för att maximera nätverksresurser, hantera trafik och skydda kundens platser från skadlig trafik.',
        dataShare: 'Denna cookie delar data utanför EU/EES.',
      },
      default: {
        description:
          "These cookies are used to maximize network resources, manage traffic, and protect customer's sites from malicious traffic.",
        dataShare: 'This cookie shares data outside the EU/EEA.',
      },
    },
    Distrelec: {
      EN: {
        description:
          'We use cookies to help us remember and process the items in your shopping cart. They are also used to help us understand your preferences based on previous or current site activity, which enables us to provide you with improved services. We also use cookies to help us compile aggregate data about site traffic and site interaction so that we can offer better site experiences and tools in the future.',
        dataShare: 'This cookie shares data outside the EU/EEA.',
      },
      CS: {
        description:
          'Soubory cookie nám pomáhají zapamatovat si položky ve vašem nákupním košíku a zpracovávat je. Využíváme je také k tomu, aby nám pomohly porozumět vašim preferencím na základě předchozí nebo aktuální aktivity webu, což nám umožňuje vám poskytovat vylepšené služby. Soubory cookie nám pomáhají shromažďovat agregované údaje o provozu na stránkách a interakci s nimi, abychom vám mohli v budoucnu nabízet lepší služby a nástroje.',
        dataShare: 'Tento soubor cookie sdílí data mimo EU/EHP.',
      },
      DK: {
        description:
          'Vi bruger cookies til at hjælpe os med at huske og behandle varerne i din indkøbskurv. De bruges også til at hjælpe os med at forstå dine præferencer baseret på tidligere eller aktuelle webstedsaktivitet, hvilket gør det muligt for os at give dig forbedrede tjenester. Vi bruger også cookies til at hjælpe os med at samle samlede data om webstrafik og webstedsinteraktion, så vi kan tilbyde bedre webstedsoplevelser og værktøjer i fremtiden.',
        dataShare: 'Denne cookie deler data uden for EU/EØS.',
      },
      DE: {
        description:
          'Wir verwenden Cookies, um uns an die Artikel in Ihrem Einkaufswagen zu erinnern und diese zu verarbeiten. Sie werden auch verwendet, um uns zu helfen, Ihre Präferenzen basierend auf früheren oder aktuellen Website-Aktivitäten zu verstehen, was es uns ermöglicht, Ihnen verbesserte Dienstleistungen anzubieten. Wir verwenden Cookies auch, um aggregierte Daten über den Website-Verkehr und die Interaktion mit der Website zu sammeln, damit wir in Zukunft bessere Website-Erfahrungen und -Tools anbieten können.',
        dataShare: 'Dieses Cookie gibt Daten nach ausserhalb der EU/EWR weiter.',
      },
      EE: {
        description:
          'Me kasutame küpsiseid, mis aitavad meil meeles pidada ja töödelda teie ostukorvis olevaid kaupu. Neid kasutatakse ka selleks, et aidata meil mõista teie eelistusi, mis põhinevad teie varasemal või praegusel veebilehel toimuval tegevusel, mis võimaldab meil pakkuda teile paremaid teenuseid. Samuti kasutame küpsiseid, et aidata meil koguda koondandmeid saidi liikluse ja saidi suhtluse kohta, et saaksime tulevikus pakkuda paremaid saitikogemusi ja -vahendeid.',
        dataShare: 'See küpsis jagab andmeid väljaspool EL-i/EMP-d.',
      },
      FI: {
        description:
          'Käytämme evästeitä auttaaksemme meitä muistamaan ja käsittelemään ostoskorissasi olevat tuotteet. Niitä käytetään myös auttamaan meitä ymmärtämään asetuksesi aiemman tai nykyisen sivuston toiminnan perusteella, mikä antaa meille mahdollisuuden tarjota sinulle parempia palveluja. Käytämme myös evästeitä auttaaksemme meitä keräämään kokonaistietoja sivuston liikenteestä ja sivuston vuorovaikutuksesta, jotta voimme tarjota parempia sivustokokemuksia ja työkaluja tulevaisuudessa.',
        dataShare: 'Tämä eväste jakaa tietoja EU:n/ETA:n ulkopuolelle.',
      },
      FR: {
        description:
          "Nous utilisons des cookies pour nous aider à nous souvenir et à traiter les articles de votre panier. Ils sont également utilisés pour nous aider à comprendre vos préférences en fonction de l'activité antérieure ou actuelle sur le site, ce qui nous permet de vous fournir des services améliorés. Nous utilisons également des cookies pour nous aider à compiler des données globales sur le trafic et l'interaction sur le site afin de pouvoir offrir de meilleures expériences et de meilleurs outils à l'avenir.",
        dataShare: "Ce cookie partage des données en dehors de l'UE/EEE.",
      },
      HU: {
        description:
          'Cookie-kat használunk annak érdekében, hogy megjegyezzük és feldolgozzuk a bevásárlókosárban lévő tételeket. Arra is használják őket, hogy segítsenek megérteni az Ön preferenciáit a korábbi vagy aktuális webhely-tevékenység alapján, amely lehetővé teszi számunkra, hogy jobb szolgáltatásokat nyújtsunk Önnek. A cookie-kat arra is használjuk, hogy segítsünk összesített adatokat összeállítani a webhely forgalmáról és a webhely interakciójáról, hogy a jövőben jobb felhasználói élményt és eszközöket kínálhassunk.',
        dataShare: 'Ez a cookie az EU-n/EGT-n kívül oszt meg adatokat.',
      },
      IT: {
        description:
          "Utilizziamo i cookie per aiutarci a ricordare ed elaborare gli articoli nel tuo carrello. Sono inoltre utilizzati per aiutarci a comprendere le tue preferenze in base all'attività del sito precedente o corrente, il che ci consente di fornirti servizi migliori. Utilizziamo i cookie anche per aiutarci a compilare dati aggregati sul traffico del sito e sull'interazione del sito in modo da poter offrire esperienze e strumenti migliori del sito in futuro.",
        dataShare: "Questo cookie condivide i dati al di fuori dell'UE/SEE.",
      },
      LT: {
        description:
          'Mes naudojame slapukus, kad galėtume prisiminti ir apdoroti jūsų pirkinių krepšelio prekes. Jie taip pat naudojami mums padėti suprasti jūsų nuostatas remiantis ankstesne ar dabartine svetainės veikla, o tai leidžia mums teikti jums patobulintas paslaugas. Mes taip pat naudojame slapukus, kad galėtume rinkti suvestinius duomenis apie svetainės srautą ir sąveiką su svetaine, kad ateityje galėtume pasiūlyti geresnę svetainės patirtį ir įrankius.',
        dataShare: 'Šis slapukas bendrina duomenis už ES / EEE ribų.',
      },
      LV: {
        description:
          'Mēs izmantojam sīkfailus, lai palīdzētu mums atcerēties un apstrādāt jūsu iepirkumu grozā esošās preces. Tās tiek izmantotas arī, lai palīdzētu mums izprast jūsu preferences, pamatojoties uz iepriekšējām vai pašreizējām vietnes darbībām, kas ļauj mums sniegt jums labākus pakalpojumus. Mēs arī izmantojam sīkfailus, lai palīdzētu mums apkopot apkopotos datus par vietnes datplūsmu un mijiedarbību ar vietni, lai nākotnē nodrošinātu ērtāku vietni un rīkus.',
        dataShare: 'Šīs sīkdatnes kopīgo informāciju ārpus ES/EEZ.',
      },
      NL: {
        description:
          'Wij gebruiken cookies om ons te helpen de artikelen in uw winkelwagen te onthouden en te verwerken. Ze worden ook gebruikt om ons te helpen uw voorkeuren te begrijpen op basis van vorige of huidige site-activiteiten, waardoor wij u betere diensten kunnen aanbieden. Wij gebruiken ook cookies om ons te helpen samengestelde gegevens te verzamelen over siteverkeer en site-interactie, zodat wij in de toekomst betere site-ervaringen en tools kunnen aanbieden.',
        dataShare: 'Deze cookie deelt gegevens buiten de EU/EER.',
      },
      NO: {
        description:
          'Vi bruker informasjonskapsler for å hjelpe oss med å huske og behandle varene i handlekurven din. De brukes også til å hjelpe oss med å forstå dine preferanser basert på tidligere eller nåværende nettstedaktivitet, som gjør at vi kan tilby deg forbedrede tjenester. Vi bruker også informasjonskapsler for å hjelpe oss med å samle samlede data om nettstedstrafikk og nettstedsinteraksjon, slik at vi kan tilby bedre nettstedopplevelser og verktøy i fremtiden.',
        dataShare: 'Denne informasjonskapselen deler data utenfor EU/EEA.',
      },
      PL: {
        description:
          'Używamy plików cookie, aby ułatwić zapamiętywanie i przetwarzanie produktów znajdujących się w koszyku. Są one również używane, aby lepiej rozeznawać się w preferencjach użytkownika w oparciu o poprzednią lub bieżącą aktywność na stronie, co pozwala zapewniać lepsze usługi. Używamy również plików cookie, aby umożliwić kompilację zbiorczych danych o ruchu na stronie i interakcji z witryną, abyśmy mogli w przyszłości zaoferować lepsze doświadczenia i udoskonalone narzędzia.',
        dataShare: 'Ten plik cookie udostępnia dane poza UE/EOG.',
      },
      RO: {
        description:
          'Utilizăm module cookie pentru a ne ajuta să ne amintim şi a procesa articolele din coşul dvs. de cumpărături. De asemenea, acestea sunt utilizate pentru a ne ajuta să înţelegem preferinţele dvs. pe baza activităţii anterioare sau curente de pe site, ceea ce ne permite să vă oferim servicii îmbunătăţite. Utilizăm module cookie pentru a ne ajuta să compilăm date agregate despre traficul şi interacţiunea de pe site, astfel încât să oferim experienţe şi instrumente mai bune pe site în viitor.',
        dataShare: 'Acest modul cookie partajează date în afara UE/SEE.',
      },
      SK: {
        description:
          'Súbory cookie používame na to, aby nám pomohli zapamätať si a spracovať položky vo vašom nákupnom košíku. Používajú sa tiež na to, aby nám pomohli pochopiť vaše preferencie na základe predchádzajúcej alebo aktuálnej aktivite na stránke, čo nám umožňuje poskytovať vám vylepšené služby. Súbory cookie používame aj na to, aby nám pomohli zhromažďovať súhrnné údaje o prenosoch a interakciách na lokalite, aby sme mohli v budúcnosti na lokalite ponúkať lepšiu používateľskú skúsenosť a nástroje.',
        dataShare: 'Tento súbor cookie zdieľa údaje mimo EÚ/EHP.',
      },
      SV: {
        description:
          'Vi använder kakor för att komma ihåg och bearbeta artiklarna i din kundvagn. De används också för att hjälpa oss att förstå dina preferenser baserat på tidigare eller aktuell webbplatsaktivitet, vilket gör att vi kan erbjuda dig förbättrade tjänster. Vi använder också cookies för att sammanställa sammanlagda data om webbplatstrafik och webbplatsinteraktion för att kunna erbjuda bättre webbplatsupplevelser och verktyg i framtiden.',
        dataShare: 'Denna cookie delar data utanför EU/EES.',
      },
      default: {
        description:
          'We use cookies to help us remember and process the items in your shopping cart. They are also used to help us understand your preferences based on previous or current site activity, which enables us to provide you with improved services. We also use cookies to help us compile aggregate data about site traffic and site interaction so that we can offer better site experiences and tools in the future.',
        dataShare: 'This cookie shares data outside the EU/EEA.',
      },
    },
    Ensighten: {
      EN: {
        description:
          "Ensighten is a container tag management system. It addresses the problems associated with site tagging and tracking of online marketing campaigns by providing universal, or server side, tag solutions. Sometimes we'll advertise on 3rd party websites. Ensighten Cookies are used to help us and our advertisers see which advertisements you click on and interact with. Each individual advertiser uses its own tracking cookies and the data taken is not confidential or interchangeable.",
        dataShare: 'This cookie shares data with our trusted third party partners and/or outside the EU/EEA.',
      },
      CS: {
        description:
          'Ensighten je systém pro správu štítků kontejnerů. Řeší problémy spojené se štítky na stránkách a sledováním online marketingových kampaní tím, že nabízí univerzální nebo serverové řešení pro správu štítků. Někdy inzerujeme na webových stránkách třetích stran. Soubory cookie Ensighten nám a našim inzerentům pomáhají zjistit, na které reklamy kliknete nebo na ně reagujete. Jednotliví inzerenti používají své vlastní sledovací soubory cookie a přijatá data nejsou důvěrná ani zaměnitelná.',
        dataShare:
          'Tento soubor cookie sdílí data s našimi důvěryhodnými partnery z řad třetích stran a /nebo mimo EU/EHP.',
      },
      DK: {
        description:
          'Ensighten er et container tag management system. Den løser de problemer, der er forbundet med webstedsmærkning og sporing af online marketingkampagner ved at tilbyde universelle tag-løsninger, eller på serversiden. Nogle gange annoncerer vi på tredjepartswebsteder. Ensighten Cookies bruges til at hjælpe os og vores annoncører med at se, hvilke annoncer du klikker på og interagerer med. Hver enkelt annoncør bruger sine egne sporingscookies, og de data, der er taget, er ikke fortrolige eller udskiftelige.',
        dataShare: 'Denne cookie deler data med vores betroede tredjepartspartnere og/eller uden for EU/EØS.',
      },
      DE: {
        description:
          'Ensighten ist ein Container-Tag-Management-System. Es löst die Probleme, die mit dem Tagging von Websites und dem Tracking von Online-Marketing-Kampagnen verbunden sind, indem es universelle oder serverseitige Tag-Lösungen anbietet. Manchmal werben wir auf Websites von Drittanbietern. Ensighten-Cookies werden verwendet, um uns und unseren Werbekunden zu helfen, zu sehen, welche Werbung Sie anklicken und mit ihr interagieren. Jeder einzelne Werbetreibende verwendet seine eigenen Tracking-Cookies und die erfassten Daten sind nicht vertraulich oder austauschbar.',
        dataShare:
          'Dieses Cookie teilt Daten mit unseren vertrauenswürdigen Drittpartnern und/oder außerhalb der EU/EWR.',
      },
      EE: {
        description:
          'Ensighten on sildihaldussüsteem. See lahendab veebilehtede märgistamise ja veebiturunduskampaaniate jälgimisega seotud probleeme, pakkudes universaalseid ehk serveripoolseid märgistuslahendusi. Mõnikord reklaamime kolmanda osapoole veebisaitidel. Ensighteni küpsiseid kasutatakse selleks, et aidata meil ja meie reklaamijatel näha, millistele reklaamidele te klõpsate ja milliste reklaamidega te suhtlete. Iga üks reklaamija kasutab oma jälgimisküpsiseid ja võetud andmed ei ole konfidentsiaalsed ega vahetatavad.',
        dataShare:
          'See küpsis jagab andmeid meie usaldatud kolmandast isikust partneritega ja/või väljaspool EL-i/EMP-d.',
      },
      FI: {
        description:
          'Ensighten on konttien tunnistehallintajärjestelmä. Se korjaa ongelmat, jotka liittyvät sivuston merkitsemiseen ja verkkomarkkinointikampanjoiden seurantaan tarjoamalla universaalit tai palvelinpuolen tag-ratkaisut. Joskus mainostamme kolmansien osapuolten verkkosivustoilla. Ensighten-evästeitä käytetään auttamaan meitä ja mainostajiamme näkemään, mitä mainoksia napsautat ja joiden kanssa olet tekemisissä. Jokainen mainostaja käyttää omia seurantaevästeitään, ja otetut tiedot eivät ole luottamuksellisia tai vaihdettavissa.',
        dataShare:
          'Tämä eväste jakaa tietoja yrityksen kumppaneina toimivien luotettavien kolmansien osapuolten kanssa ja/tai EU:n/ETA:n ulkopuolelle.',
      },
      FR: {
        description:
          'Ensighten apporte des solutions de tag management. Il répond aux problèmes liés au tagging des sites et au suivi des campagnes de marketing en ligne en fournissant des solutions de tagging universel, ou côté serveur. Parfois, nous faisons de la publicité sur des sites Web tiers. Les cookies Ensighten sont utilisés pour nous aider, ainsi que nos annonceurs, à voir sur quelles publicités vous cliquez et avec lesquelles vous interagissez. Chaque annonceur individuel utilise ses propres cookies de suivi et les données recueillies ne sont ni confidentielles ni interchangeables.',
        dataShare: "Ce cookie partage des données avec nos partenaires tiers de confiance et/ou en dehors de l'UE/EEE.",
      },
      HU: {
        description:
          'Az Ensighten egy tárolócímke-kezelő rendszer. Univerzális vagy szerver oldali címkérési megoldások biztosításával megoldást nyújt az internetes marketingkampányok helycímkézésével és nyomon követésével kapcsolatos problémákra. Néha harmadik fél weboldalain hirdetünk. Az Ensighten cookie-k segítenek nekünk és hirdetőinknek látni, hogy mely hirdetésekre kattint, és mely hirdetésekkel lép kapcsolatba. Minden egyes hirdető saját nyomkövető cookie-kat használ; a kapott adatok nem bizalmasak vagy felcserélhetők.',
        dataShare: 'Ez a cookie adatokat oszt meg megbízható harmadik fél partnereinkkel és/vagy az EU/EGT-n kívül.',
      },
      IT: {
        description:
          'Ensighten è un sistema di gestione dei tag del contenitore. Risolve i problemi associati al tagging del sito e al monitoraggio delle campagne di marketing online fornendo soluzioni di tag universali o lato server. A volte faremo pubblicità su siti Web di terze parti. I cookie di Ensighten vengono utilizzati per aiutare noi e i nostri inserzionisti a vedere su quali annunci fai clic e con cui interagisci. Ogni singolo inserzionista utilizza i propri cookie di tracciamento ei dati raccolti non sono confidenziali o intercambiabili.',
        dataShare:
          "Questo cookie condivide i dati con i nostri partner terze parti fidate e/o al di fuori dell'UE/SEE.",
      },
      LT: {
        description:
          'Ensighten yra konteinerių žymių valdymo sistema. Joje sprendžiamos problemos, susijusios su svetainės žymėjimu ir internetinės rinkodaros kampanijų stebėjimu, teikiant universalius arba serverio pusės žymos sprendimus. Kartais mes reklamuojamės trečiųjų šalių svetainėse. Ensighten slapukai naudojami tam, kad mums ir mūsų reklamuotojams būtų lengviau pamatyti, kuriuos skelbimus spustelite ir su kuriais bendraujate. Kiekvienas reklamuotojas naudoja savo stebėjimo slapukus, o paimti duomenys nėra konfidencialūs ar keičiami.',
        dataShare:
          'Šis slapukas bendrina duomenis su patikimais trečiųjų šalių partneriais ir (arba) už ES / EEE ribų.',
      },
      LV: {
        description:
          'Ensighten ir birku pārvaldības sistēma. Tā risina problēmas, kas saistītas ar vietņu marķēšanu un tiešsaistes mārketinga kampaņu izsekošanu, nodrošinot universālus jeb servera puses tagu risinājumus. Dažkārt mēs reklamējamies trešo pušu vietnēs. Ensighten sīkfaili tiek izmantoti, lai palīdzētu mums un mūsu reklāmdevējiem redzēt, uz kurām reklāmām jūs noklikšķināt un ar kurām mijiedarbojaties. Katrs atsevišķs reklāmdevējs izmanto savas izsekošanas sīkdatnes, un iegūtie dati nav konfidenciāli vai savstarpēji aizstājami.',
        dataShare: 'Šīs sīkdatnes kopīgo informāciju ar mūsu uzticamiem trešās puses partneriem un/vai ārpus ES/EEZ.',
      },
      NL: {
        description:
          'Ensighten is een tagbeheersysteem voor containers. Het pakt de problemen aan die gepaard gaan met site tagging en tracking van online marketing campagnes door universele, of server side, tag oplossingen aan te bieden. Soms zullen we adverteren op websites van derden. Ensighten Cookies worden gebruikt om ons en onze adverteerders te helpen zien op welke advertenties u klikt en welke interactie u heeft. Elke individuele adverteerder gebruikt zijn eigen tracking cookies en de verzamelde gegevens zijn niet vertrouwelijk of onderling uitwisselbaar.',
        dataShare: 'Deze cookie deelt gegevens met onze vertrouwde externe partners en/of buiten de EU/EER.',
      },
      NO: {
        description:
          'Ensighten er et container tag management system. Den løser problemene knyttet til nettstedstagging og sporing av online markedsføringskampanjer ved å tilby universelle taggløsninger, eller på serversiden. Noen ganger vil vi annonsere på tredjeparts nettsteder. Ensighten Cookies brukes til å hjelpe oss og annonsørene våre med å se hvilke annonser du klikker på og samhandler med. Hver enkelt annonsør bruker sine egne sporingscookies, og dataene som tas er ikke konfidensielle eller utskiftbare.',
        dataShare:
          'Denne informasjonskapselen deler data med våre pålitelige tredjepartsleverandører og/eller utenfor EU/EEA.',
      },
      PL: {
        description:
          'Ensighten jest systemem zarządzania tagami typu container. Rozwiązanie to eliminuje problemy związane z oznaczaniem witryn i śledzeniem kampanii marketingowych online, dostarczając uniwersalne, znajdujące się po stronie serwera, rozwiązania w zakresie tagów. Czasami będziemy reklamować się na stronach internetowych innych stron. Pliki cookie Ensighten są używane, aby pomóc nam i naszym reklamodawcom zobaczyć, które reklamy klika użytkownik i z którymi wchodzi on w interakcję. Każdy reklamodawca korzysta z własnych plików cookie dotyczących śledzenia, a pobrane dane nie są poufne ani zamienne.',
        dataShare: 'Ten plik cookie udostępnia dane naszym zaufanym partnerom lub podmiotom spoza UE/EOG.',
      },
      RO: {
        description:
          'Ensighten este un sistem de gestionare a tagurilor de tip container. Acesta soluţionează problemele legate de marcarea site-urilor şi urmărirea campaniilor de marketing online furnizând soluţii de marcare universale sau server side. Uneori, facem reclamă pe site-uri web terţe. Modulele cookie Ensighten sunt utilizate pentru a ne ajuta pe noi şi pe agenţiile noastre de publicitate să vedem pe ce reclame faceţi clic şi cu care interacţionaţi. Fiecare agenţie de publicitate individuală utilizează propriile sale module cookie de urmărire, iar datele preluate nu sunt confidenţiale sau interschimbabile.',
        dataShare: 'Acest modul cookie partajează date cu partenerii noştri terţi de încredere şi/sau în afara UE/SEE.',
      },
      SK: {
        description:
          'Ensighten je systém správy kontajnerových značiek. Rieši problémy spojené s označovaním lokality a sledovaním online marketingových kampaní poskytovaním univerzálnych riešení označovania, alebo riešení označovania na strane servera. Niekedy sa naše reklamy objavia na webových lokalitách tretích strán. Súbory cookie systému Ensighten sa používajú, aby nám a našim inzerentom pomohli zistiť, na ktoré reklamy klikáte a s ktorými interagujete. Každý jeden inzerent používa svoje vlastné sledovacie súbory cookie a získané údaje nie sú dôverné ani zameniteľné.',
        dataShare:
          'Tento súbor cookie zdieľa údaje s našimi dôveryhodnými partnermi tretích strán a/alebo mimo EÚ/EHP.',
      },
      SV: {
        description:
          'Ensighten är ett hanteringssystem för containertaggar. Det löser problemen förknippade med webbplatstaggar och spårning av marknadsföringskampanjer online, genom att tillhandahålla generella tagglösningar, eller servertagglösningar. Ibland annonserar vi på webbplatser från tredje part. Ensighten-kakor används för att hjälpa oss och våra annonsörer att se vilka annonser du klickar på och interagerar med. Varje enskild annonsör använder sina egna spårningskakor och de data som tas är inte konfidentiella eller utbytbara.',
        dataShare: 'Denna cookie delar data med våra betrodda tredjepartspartners och/eller utanför EU/EES.',
      },
      default: {
        description:
          "Ensighten is a container tag management system. It addresses the problems associated with site tagging and tracking of online marketing campaigns by providing universal, or server side, tag solutions. Sometimes we'll advertise on 3rd party websites. Ensighten Cookies are used to help us and our advertisers see which advertisements you click on and interact with. Each individual advertiser uses its own tracking cookies and the data taken is not confidential or interchangeable.",
        dataShare: 'This cookie shares data with our trusted third party partners and/or outside the EU/EEA.',
      },
    },
    SnapEDA: {
      EN: {
        description:
          'SnapEDA is an electronic design library - used to show 3D images and provide CAD files for products. SnapEDA use cookies to report which products were viewed and interacted with. They use this data to improve their services and they share the data with Distrelec to allow Distrelec to improve its products and web services.',
        dataShare: 'This cookie shares data with our trusted third party partners and/or outside the EU/EEA.',
      },
      CS: {
        description:
          'SnapEDA je knihovna elektronických návrhů - slouží k zobrazování 3D obrázků a poskytování souborů CAD pro výrobky. SnapEDA používá soubory cookie k hlášení, které produkty byly zobrazeny a s nimiž byla provedena interakce. Tyto údaje používají ke zlepšení svých služeb a sdílejí je se společností Distrelec, aby jí umožnili zlepšovat své produkty a webové služby.',
        dataShare:
          'Tento soubor cookie sdílí data s našimi důvěryhodnými partnery z řad třetích stran a /nebo mimo EU/EHP.',
      },
      DK: {
        description:
          'SnapEDA er et elektronisk håndbogsbibliotek, der bruges til at vise 3D-billeder og CAD-filer for produkter. SnapEDA bruger cookies til at rapportere, hvilke produkter, der blev vist og interageret med. De bruger disse data for at forbedre deres tjenester, og de deler dataene med Distrelec for at gøre det muligt for Distrelec at forbedre deres produkter og webtjenester.',
        dataShare: 'Denne cookie deler data med vores betroede tredjepartspartnere og/eller uden for EU/EØS.',
      },
      DE: {
        description:
          'SnapEDA ist eine elektronische Designbibliothek, mit der 3D-Bilder angezeigt und CAD-Dateien für Produkte bereitgestellt werden können. Bei SnapEDA werden Cookies verwendet, um zu protokollieren, welche Produkte angesehen wurden und mit welchen Produkten interagiert wurde. Diese Daten werden zur Verbesserung ihrer Dienstleistungen verwendet und an Distrelec weitergegeben, damit Distrelec seine Produkte und Webservices verbessern kann.',
        dataShare:
          'Dieses Cookie teilt Daten mit unseren vertrauenswürdigen Drittpartnern und/oder außerhalb der EU/EWR.',
      },
      EE: {
        description:
          'SnapEDA on elektrooniline projekteerimise raamatukogu, mida kasutatakse toodete 3D kujutiste kuvamiseks ja CAD failide pakkumiseks. SnapEDA kasutab küpsiseid, et teavitada milliseid tooteid vaadati ja suheldi. Nad kasutavad neid andmeid oma teenuste parandamiseks ning jagavad andmeid Elfa Distreleciga, et võimaldada Elfa Distrelecil oma tooteid ja veebiteenuseid parandada.',
        dataShare:
          'See küpsis jagab andmeid meie usaldatud kolmandast isikust partneritega ja/või väljaspool EL-i/EMP-d.',
      },
      FI: {
        description:
          'SnapEDA on elektroninen suunnittelukirjasto - käytetään 3D-kuvien näyttämiseen ja CAD-tiedostojen hankkimiseen tuotteita varten. SnapEDA käyttää evästeitä raportoidakseen, mitä tuotteita on katsottu ja minkä kanssa on oltu vuorovaikutuksessa. He käyttävät näitä tietoja palvelujensa parantamiseen ja jakavat tiedot Elfa Distrelecin kanssa, jotta Elfa Distrelec voi parantaa tuotteitaan ja verkkopalvelujaan.',
        dataShare:
          'Tämä eväste jakaa tietoja yrityksen kumppaneina toimivien luotettavien kolmansien osapuolten kanssa ja/tai EU:n/ETA:n ulkopuolelle.',
      },
      FR: {
        description:
          "SnapEDA est une bibliothèque de conception électronique - utilisée pour montrer des images 3D et fournir des fichiers CAO pour les produits. SnapEDA utilise des cookies pour signaler les produits qui ont été consultés et avec lesquels une interaction a eu lieu. Ils utilisent ces données pour améliorer leurs services et les partagent avec Distrelec pour permettre à Distrelec d'améliorer ses produits et services web.",
        dataShare: "Ce cookie partage des données avec nos partenaires tiers de confiance et/ou en dehors de l'UE/EEE.",
      },
      HU: {
        description:
          'A SnapEDA elektronikus tervezőkönyvtár - a termékekhez 3D-s képek megjelenítésére és CAD-fájlok biztosítására. A SnapEDA sütiket használ, hogy jelentse, melyik termékeket tekintették meg, és melyekkel léptek kapcsolatba. Ezeket az adatokat szolgáltatásaik fejlesztésére használják fel, és megosztják azokat a Distrelec-kel, hogy a Distrelec fejleszthesse termékeit és webes szolgáltatásait.',
        dataShare: 'Ez a cookie adatokat oszt meg megbízható harmadik fél partnereinkkel és/vagy az EU/EGT-n kívül.',
      },
      IT: {
        description:
          "SnapEDA è una libreria di progettazione elettronica che fornisce immagini 3D e file CAD per una vasta gamma di prodotti. Per tracciare l'interazione degli utenti, SnapEDA fa uso di cookie al fine di registrare i prodotti visualizzati e con cui è stata interagita.  Questi dati vengono impiegati per il miglioramento continuo dei propri servizi e vengono condivisi con Distrelec al fine di consentire un miglioramento dei prodotti e dei servizi web offerti da quest'ultima.",
        dataShare:
          "Questo cookie condivide i dati con i nostri partner terze parti fidate e/o al di fuori dell'UE/SEE.",
      },
      LT: {
        description:
          '„SnapEDA“ yra elektroninio projektavimo biblioteka, naudojama 3D vaizdams rodyti ir gaminių CAD failams pateikti. „SnapEDA“ naudoja slapukus, kad praneštų, kurie produktai buvo peržiūrėti ir su kuriais buvo sąveikauta. Jie naudoja šiuos duomenis savo paslaugoms tobulinti ir bendrina juos su „Distrelec“, kad „Distrelec“ galėtų tobulinti savo gaminius ir žiniatinklio paslaugas.',
        dataShare:
          'Šis slapukas bendrina duomenis su patikimais trečiųjų šalių partneriais ir (arba) už ES / EEE ribų.',
      },
      LV: {
        description:
          'SnapEDA ir elektroniskā dizaina bibliotēka, ko izmanto, lai parādītu 3D attēlus un nodrošinātu CAD failus produktiem. SnapEDA izmanto sīkfailus, lai ziņotu, kuri produkti tika apskatīti un ar kuriem tika klienti saskārās Viņi izmanto šos datus, lai uzlabotu savus pakalpojumus, un viņi dalās ar šiem datiem ar Elfa Distrelec, lai Elfa Distrelec varētu uzlabot savus produktus un tīmekļa pakalpojumus.',
        dataShare: 'Šīs sīkdatnes kopīgo informāciju ar mūsu uzticamiem trešās puses partneriem un/vai ārpus ES/EEZ.',
      },
      NL: {
        description:
          'SnapEDA is een elektronische ontwerpbibliotheek - gebruikt om 3D-afbeeldingen weer te geven en CAD-bestanden voor producten te leveren. SnapEDA gebruikt cookies om te rapporteren welke producten zijn bekeken en gebruikt. Ze gebruiken deze gegevens om hun dienstverlening te verbeteren en ze delen deze gegevens met Distrelec zodat Distrelec haar producten en webservices kan verbeteren.',
        dataShare: 'Deze cookie deelt gegevens met onze vertrouwde externe partners en/of buiten de EU/EER.',
      },
      NO: {
        description:
          'SnapEDA er et elektronisk designbibliotek som brukes for å vise 3D-bilder og skaffe til veie CAD-filer for produktet. SnapEDA bruker informasjonskapsler for å rapportere hvilke produkter som blir vist og samhandlet med. De bruker disse opplysningene til å forbedre tjenestene sine, og de deler opplysningene med Elfa Distrelec slik at vi kan forbedre våre produkter og nettjenester.',
        dataShare:
          'Denne informasjonskapselen deler data med våre pålitelige tredjepartsleverandører og/eller utenfor EU/EEA.',
      },
      PL: {
        description:
          'SnapEDA to biblioteka projektów elektronicznych, która służy do wyświetlania obrazów 3D i udostępniania projektów produktów w plikach CAD. SnapEDA korzysta z plików cookie do tworzenia raportów dotyczących przeglądanych produktów i interakcji z nimi. SnapEDA wykorzystuje te dane do usprawnienia swoich usług i przekazuje je firmie Elfa Distrelec, aby ta mogła udoskonalać swoje produkty i usługi internetowe.',
        dataShare: 'Ten plik cookie udostępnia dane naszym zaufanym partnerom lub podmiotom spoza UE/EOG.',
      },
      RO: {
        description:
          'SnapEDA este o bibliotecă de proiectare electronică - utilizată pentru a afișa imagini 3D și a furniza fișiere CAD pentru produse. SnapEDA utilizează cookie-uri pentru a raporta ce produse au fost vizualizate și cu care s-a interacționat. Aceștia folosesc aceste date pentru a-și îmbunătăți serviciile și le împărtășesc cu Distrelec pentru a permite Distrelec să își îmbunătățească produsele și serviciile web.',
        dataShare: 'Acest modul cookie partajează date cu partenerii noştri terţi de încredere şi/sau în afara UE/SEE.',
      },
      SK: {
        description:
          'SnapEDA je elektronická knižnica dizajnu – používa sa na zobrazenie 3D obrázkov a poskytuje CAD súbory pre produkty. SnapEDA používa cookies na ohlásenie toho, ktoré produkty boli zobrazené či s nimi došlo k interakcii. Tieto údaje používajú na zlepšenie svojich služieb a zdieľajú ich so spoločnosťou Distrelec, aby umožnili spoločnosti Distrelec zlepšovať svoje produkty a webové služby.',
        dataShare:
          'Tento súbor cookie zdieľa údaje s našimi dôveryhodnými partnermi tretích strán a/alebo mimo EÚ/EHP.',
      },
      SV: {
        description:
          'SnapEDA är ett elektroniskt designbibliotek – används för att visa 3D-bilder och tillhandahålla CAD-filer för produkter. SnapEDA använder cookies för att rapportera vilka produkter som har setts och interagerats med. De använder denna data för att förbättra sina tjänster och de delar data med Elfa Distrelec för att tillåta Elfa Distrelec att förbättra sina produkter och webbtjänster.',
        dataShare: 'Denna cookie delar data med våra betrodda tredjepartspartners och/eller utanför EU/EES.',
      },
      default: {
        description:
          'SnapEDA is an electronic design library - used to show 3D images and provide CAD files for products. SnapEDA use cookies to report which products were viewed and interacted with. They use this data to improve their services and they share the data with Distrelec to allow Distrelec to improve its products and web services.',
        dataShare: 'This cookie shares data with our trusted third party partners and/or outside the EU/EEA.',
      },
    },
    Rapidspike: {
      EN: {
        description:
          'We use cookies to help us remember and process the items in your shopping cart. They are also used to help us understand your preferences based on previous or current site activity, which enables us to provide you with improved services. We also use cookies to help us compile aggregate data about site traffic and site interaction so that we can offer better site experiences and tools in the future.',
        dataShare: 'This cookie shares data outside the EU/EEA.',
      },
      CS: {
        description:
          'soubory cookie nám pomáhají zapamatovat si položky ve vašem nákupním košíku a zpracovávat je. Využíváme je také k tomu, aby nám pomohly porozumět vašim preferencím na základě předchozí nebo aktuální aktivity webu, což nám umožňuje vám poskytovat vylepšené služby. Soubory cookie nám pomáhají shromažďovat agregované údaje o provozu na stránkách a interakci s nimi, abychom vám mohli v budoucnu nabízet lepší služby a nástroje.',
        dataShare: 'Tento soubor cookie sdílí data mimo EU/EHP.',
      },
      DK: {
        description:
          'vi bruger cookies til at hjælpe os med at huske og behandle varerne i din indkøbskurv. De bruges også til at hjælpe os med at forstå dine præferencer baseret på tidligere eller aktuelle webstedsaktivitet, hvilket gør det muligt for os at give dig forbedrede tjenester. Vi bruger også cookies til at hjælpe os med at samle samlede data om webstrafik og webstedsinteraktion, så vi kan tilbyde bedre webstedsoplevelser og værktøjer i fremtiden.',
        dataShare: 'Denne cookie deler data uden for EU/EØS.',
      },
      DE: {
        description:
          'Wir verwenden Cookies, um uns zu helfen, die Artikel in Ihrem Warenkorb zu speichern und zu verarbeiten. Sie werden auch verwendet, um uns zu helfen, Ihre Präferenzen auf der Grundlage früherer oder aktueller Website-Aktivitäten zu verstehen, was es uns ermöglicht, Ihnen verbesserte Dienstleistungen zu bieten. Wir verwenden Cookies auch, um aggregierte Daten über den Website-Verkehr und die Interaktion auf der Website zu sammeln, damit wir in Zukunft bessere Website-Erfahrungen und -Tools anbieten können.',
        dataShare: 'Dieses Cookie gibt Daten nach ausserhalb der EU/EWR weiter.',
      },
      EE: {
        description:
          'Me kasutame küpsiseid, mis aitavad meil meeles pidada ja töödelda teie ostukorvis olevaid kaupu. Neid kasutatakse ka selleks, et aidata meil mõista teie eelistusi, mis põhinevad teie varasemal või praegusel veebilehel toimuval tegevusel, mis võimaldab meil pakkuda teile paremaid teenuseid. Samuti kasutame küpsiseid, et aidata meil koguda koondandmeid saidi liikluse ja saidi suhtluse kohta, et saaksime tulevikus pakkuda paremaid saitikogemusi ja -vahendeid.',
        dataShare: 'See küpsis jagab andmeid väljaspool EL-i/EMP-d.',
      },
      FI: {
        description:
          'käytämme evästeitä auttaaksemme meitä muistamaan ja käsittelemään ostoskorissasi olevat tuotteet. Niitä käytetään myös auttamaan meitä ymmärtämään asetuksesi aiemman tai nykyisen sivuston toiminnan perusteella, mikä antaa meille mahdollisuuden tarjota sinulle parempia palveluja. Käytämme myös evästeitä auttaaksemme meitä keräämään kokonaistietoja sivuston liikenteestä ja sivuston vuorovaikutuksesta, jotta voimme tarjota parempia sivustokokemuksia ja työkaluja tulevaisuudessa.',
        dataShare: 'Tämä eväste jakaa tietoja EU:n/ETA:n ulkopuolelle.',
      },
      FR: {
        description:
          "Nous utilisons des cookies pour nous aider à nous souvenir et à traiter les articles de votre panier. Ils sont également utilisés pour nous aider à comprendre vos préférences en fonction de l'activité antérieure ou actuelle sur le site, ce qui nous permet de vous fournir des services améliorés. Nous utilisons également des cookies pour nous aider à compiler des données globales sur le trafic et l'interaction sur le site afin de pouvoir offrir de meilleures expériences et de meilleurs outils à l'avenir.",
        dataShare: "Ce cookie partage des données en dehors de l'UE/EEE.",
      },
      HU: {
        description:
          'cookie-kat használunk annak érdekében, hogy tároljuk és feldolgozzuk a bevásárlókosárban lévő tételeket. Arra is használják őket, hogy segítsenek megérteni az Ön preferenciáit a korábbi vagy aktuális webhely-tevékenység alapján, amely lehetővé teszi számunkra, hogy jobb szolgáltatásokat nyújtsunk Önnek. A cookie-kat arra is használjuk, hogy segítsünk összesített adatokat összeállítani a webhely forgalmáról és a webhely interakciójáról, hogy a jövőben jobb felhasználói élményt és eszközöket kínálhassunk.',
        dataShare: 'Ez a cookie az EU-n/EGT-n kívül oszt meg adatokat.',
      },
      IT: {
        description:
          "Utilizziamo i cookie per aiutarci a ricordare ed elaborare gli articoli nel tuo carrello. Sono inoltre utilizzati per aiutarci a comprendere le tue preferenze in base all'attività del sito precedente o corrente, il che ci consente di fornirti servizi migliori. Utilizziamo i cookie anche per aiutarci a compilare dati aggregati sul traffico del sito e sull'interazione del sito in modo da poter offrire esperienze e strumenti migliori del sito in futuro.",
        dataShare: "Questo cookie condivide i dati al di fuori dell'UE/SEE.",
      },
      LT: {
        description:
          'mes naudojame slapukus, kad padėtų mums prisiminti ir apdoroti jūsų pirkinių krepšelio prekes. Jie taip pat naudojami mums padėti suprasti jūsų nuostatas remiantis ankstesne ar dabartine svetainės veikla, o tai leidžia mums teikti jums patobulintas paslaugas. Mes taip pat naudojame slapukus, kad galėtume rinkti suvestinius duomenis apie svetainės srautą ir sąveiką su svetaine, kad ateityje galėtume pasiūlyti geresnę svetainės patirtį ir įrankius.',
        dataShare: 'Šis slapukas bendrina duomenis už ES / EEE ribų.',
      },
      LV: {
        description:
          'Mēs izmantojam sīkfailus, lai palīdzētu mums atcerēties un apstrādāt jūsu iepirkumu grozā esošās preces. Tās tiek izmantotas arī, lai palīdzētu mums izprast jūsu preferences, pamatojoties uz iepriekšējām vai pašreizējām vietnes darbībām, kas ļauj mums sniegt jums labākus pakalpojumus. Mēs arī izmantojam sīkfailus, lai palīdzētu mums apkopot apkopotos datus par vietnes datplūsmu un mijiedarbību ar vietni, lai nākotnē nodrošinātu ērtāku vietni un rīkus.',
        dataShare: 'Šīs sīkdatnes kopīgo informāciju ārpus ES/EEZ.',
      },
      NL: {
        description:
          'Wij gebruiken cookies om ons te helpen de artikelen in uw winkelwagen te onthouden en te verwerken. Ze worden ook gebruikt om ons te helpen uw voorkeuren te begrijpen op basis van vorige of huidige site-activiteiten, waardoor wij u betere diensten kunnen aanbieden. Wij gebruiken cookies ook om gegevens te verzamelen over het verkeer en de interactie met de site, zodat wij in de toekomst betere site-ervaringen en tools kunnen aanbieden.',
        dataShare: 'Deze cookie deelt gegevens buiten de EU/EER.',
      },
      NO: {
        description:
          'vi bruker informasjonskapsler for å hjelpe oss med å huske og behandle varene i handlekurven din. De brukes også til å hjelpe oss med å forstå dine preferanser basert på tidligere eller nåværende nettstedaktivitet, som gjør at vi kan tilby deg forbedrede tjenester. Vi bruker også informasjonskapsler for å hjelpe oss med å samle samlede data om nettstedstrafikk og nettstedsinteraksjon, slik at vi kan tilby bedre nettstedopplevelser og verktøy i fremtiden.',
        dataShare: 'Denne informasjonskapselen deler data utenfor EU/EEA.',
      },
      PL: {
        description:
          'używamy plików cookie, aby umożliwić zapamiętywanie i przetwarzanie produktów znajdujących się w koszyku. Są one również używane, aby lepiej rozeznawać się w preferencjach użytkownika w oparciu o poprzednią lub bieżącą aktywność na stronie, co pozwala zapewniać lepsze usługi. Używamy również plików cookie, aby umożliwić kompilację zbiorczych danych o ruchu na stronie i interakcji z witryną, abyśmy mogli w przyszłości zaoferować lepsze doświadczenia i udoskonalone narzędzia.',
        dataShare: 'Ten plik cookie udostępnia dane poza UE/EOG.',
      },
      RO: {
        description:
          'utilizăm module cookie pentru a ne ajuta să ne amintim şi să procesăm articolele din coşul dvs. de cumpărături. De asemenea, acestea sunt utilizate pentru a ne ajuta să înţelegem preferinţele dvs. pe baza activităţii anterioare sau curente de pe site, ceea ce ne permite să vă oferim servicii îmbunătăţite. Utilizăm module cookie pentru a ne ajuta să compilăm date agregate despre traficul şi interacţiunea de pe site, astfel încât să oferim experienţe şi instrumente mai bune pe site în viitor.',
        dataShare: 'Acest modul cookie partajează date în afara UE/SEE.',
      },
      SK: {
        description:
          'Súbory cookie používame, aby nám pomohli zapamätať si a spracovať položky vo vašom nákupnom košíku. Používajú sa tiež na to, aby nám pomohli pochopiť vaše preferencie na základe predchádzajúcej alebo aktuálnej aktivite na stránke, čo nám umožňuje poskytovať vám vylepšené služby. Súbory cookie používame aj na to, aby nám pomohli zhromažďovať súhrnné údaje o prenosoch a interakciách na lokalite, aby sme mohli v budúcnosti na lokalite ponúkať lepšiu používateľskú skúsenosť a nástroje.',
        dataShare: 'Tento súbor cookie zdieľa údaje mimo EÚ/EHP.',
      },
      SV: {
        description:
          'Vi använder kakor för att komma ihåg och bearbeta artiklarna i din kundvagn. De används också för att hjälpa oss att förstå dina preferenser baserat på tidigare eller aktuell webbplatsaktivitet, vilket gör att vi kan erbjuda dig förbättrade tjänster. Vi använder även kakor för att sammanställa sammanlagda data om webbplatstrafik och webbplatsinteraktion för att kunna erbjuda bättre webbplatsupplevelser och verktyg i framtiden.',
        dataShare: 'Denna cookie delar data utanför EU/EES.',
      },
      default: {
        description:
          'We use cookies to help us remember and process the items in your shopping cart. They are also used to help us understand your preferences based on previous or current site activity, which enables us to provide you with improved services. We also use cookies to help us compile aggregate data about site traffic and site interaction so that we can offer better site experiences and tools in the future.',
        dataShare: 'This cookie shares data outside the EU/EEA.',
      },
    },
    'Google ReCaptcha': {
      EN: {
        description:
          'These are essential site cookies, used by the google reCAPTCHA. These cookies use an unique identifier for tracking purposes.',
        dataShare: 'This cookie shares data with our trusted third party partners and/or outside the EU/EEA.',
      },
      CS: {
        description:
          'Jedná se o základní webové soubory cookie, které využívá služba Google reCAPTCHA. Tyto soubory cookie používají pro účely sledování jedinečný identifikátor.',
        dataShare:
          'Tento soubor cookie sdílí data s našimi důvěryhodnými partnery z řad třetích stran a /nebo mimo EU/EHP.',
      },
      DK: {
        description:
          'Disse er vigtige webstedscookies, der bruges af google reCAPTCHA. Disse cookies bruger en unik identifikator til sporingsformål.',
        dataShare: 'Denne cookie deler data med vores betroede tredjepartspartnere og/eller uden for EU/EØS.',
      },
      DE: {
        description:
          'Dies sind essentielle Website-Cookies, die von Google ReCAPTCHA verwendet werden. Diese Cookies verwenden eine eindeutige Kennung für Tracking-Zwecke.',
        dataShare:
          'Dieses Cookie teilt Daten mit unseren vertrauenswürdigen Drittpartnern und/oder außerhalb der EU/EWR.',
      },
      EE: {
        description:
          'Need on olulised veebisaidi küpsised, mida kasutab Google reCAPTCHA. Need küpsised kasutavad jälgimise eesmärgil unikaalset identifikaatorit.',
        dataShare:
          'See küpsis jagab andmeid meie usaldatud kolmandast isikust partneritega ja/või väljaspool EL-i/EMP-d.',
      },
      FI: {
        description:
          'Nämä ovat välttämättömiä sivuston evästeitä, joita google reCAPTCHA käyttää. Nämä evästeet käyttävät yksilöllistä tunnistetta seurantatarkoituksiin.',
        dataShare:
          'Tämä eväste jakaa tietoja yrityksen kumppaneina toimivien luotettavien kolmansien osapuolten kanssa ja/tai EU:n/ETA:n ulkopuolelle.',
      },
      FR: {
        description:
          "Il s'agit de cookies de site essentiels, utilisés par Google reCAPTCHA.Ces cookies utilisent un identifiant unique à des fins de suivi.",
        dataShare: "Ce cookie partage des données avec nos partenaires tiers de confiance et/ou en dehors de l'UE/EEE.",
      },
      HU: {
        description:
          'Ezek a Google reCAPTCHA által használt alapvető webhelycookie-k. Ezek a cookie-k egyedi azonosítót használnak nyomon követési célokra.',
        dataShare: 'Ez a cookie adatokat oszt meg megbízható harmadik fél partnereinkkel és/vagy az EU/EGT-n kívül.',
      },
      IT: {
        description:
          'Si tratta di cookie essenziali del sito, utilizzati da google reCAPTCHA. Questi cookie utilizzano un identificatore univoco per scopi di tracciamento.',
        dataShare:
          "Questo cookie condivide i dati con i nostri partner terze parti fidate e/o al di fuori dell'UE/SEE.",
      },
      LT: {
        description:
          'Tai yra būtini svetainės slapukai, kuriuos naudoja Google reCAPTCHA. Šie slapukai naudoja unikalų identifikatorių stebėjimo tikslais.',
        dataShare:
          'Šis slapukas bendrina duomenis su patikimais trečiųjų šalių partneriais ir (arba) už ES / EEE ribų.',
      },
      LV: {
        description:
          'Tie ir būtiski vietnes sīkfaili, ko izmanto Google reCAPTCHA. Šie sīkfaili izmanto unikālu identifikatoru izsekošanas nolūkiem.',
        dataShare: 'Šīs sīkdatnes kopīgo informāciju ar mūsu uzticamiem trešās puses partneriem un/vai ārpus ES/EEZ.',
      },
      NL: {
        description:
          'Dit zijn essentiële site cookies, gebruikt door de google reCAPTCHA. Deze cookies gebruiken een unieke identificatie voor tracking doeleinden.',
        dataShare: 'Deze cookie deelt gegevens met onze vertrouwde externe partners en/of buiten de EU/EER.',
      },
      NO: {
        description:
          'Dette er viktige nettstedskapsler som brukes av google reCAPTCHA. Disse informasjonskapslene bruker en unik identifikator for sporingsformål.',
        dataShare:
          'Denne informasjonskapselen deler data med våre pålitelige tredjepartsleverandører og/eller utenfor EU/EEA.',
      },
      PL: {
        description:
          'Są to niezbędne pliki cookie wykorzystywane przez google reCAPTCHA. Te pliki cookie wykorzystują unikatowy identyfikator do celów śledzenia.',
        dataShare: 'Ten plik cookie udostępnia dane naszym zaufanym partnerom lub podmiotom spoza UE/EOG.',
      },
      RO: {
        description:
          'Acestea sunt module cookie esenţiale de site, utilizate de Google ReCAPTCHA. Aceste module cookie utilizează un identificator unic în scopul urmăririi.',
        dataShare: 'Acest modul cookie partajează date cu partenerii noştri terţi de încredere şi/sau în afara UE/SEE.',
      },
      SK: {
        description:
          'Toto sú základné súbory cookie, ktoré používa služba reCAPTCHA od spoločnosti Google. Tieto súbory cookie používajú na účely sledovania jedinečný identifikátor.',
        dataShare:
          'Tento súbor cookie zdieľa údaje s našimi dôveryhodnými partnermi tretích strán a/alebo mimo EÚ/EHP.',
      },
      SV: {
        description:
          'Dessa är nödvändiga webbplatskakor som används av google reCAPTCHA. Dessa kakor använder en unik identifierare för spårningsändamål.',
        dataShare: 'Denna cookie delar data med våra betrodda tredjepartspartners och/eller utanför EU/EES.',
      },
      default: {
        description:
          'These are essential site cookies, used by the google reCAPTCHA. These cookies use an unique identifier for tracking purposes.',
        dataShare: 'This cookie shares data with our trusted third party partners and/or outside the EU/EEA.',
      },
    },
    'nexus.ensighten.com': {
      EN: {
        description:
          'Ensighten is a container tag management system. It addresses the problems associated with site tagging and tracking of online marketing campaigns by providing universal, or server side, tag solutions. Sometimes we will advertise on 3rd party websites. Ensighten Cookies are used to help us and our advertisers see which advertisements you click on and interact with. Each individual advertiser uses its own tracking cookies and the data taken is not confidential or interchangeable.',
        dataShare: 'This cookie shares data with our trusted third party partners and/or outside the EU/EEA.',
      },
      CS: {
        description:
          'Ensighten je systém pro správu štítků kontejnerů. Řeší problémy spojené se štítky na stránkách a sledováním online marketingových kampaní tím, že nabízí univerzální nebo serverové řešení pro správu štítků. Někdy inzerujeme na webových stránkách třetích stran. Soubory cookie Ensighten nám a našim inzerentům pomáhají zjistit, na které reklamy kliknete nebo na ně reagujete. Jednotliví inzerenti používají své vlastní sledovací soubory cookie a přijatá data nejsou důvěrná ani zaměnitelná.',
        dataShare:
          'Tento soubor cookie sdílí data s našimi důvěryhodnými partnery z řad třetích stran a /nebo mimo EU/EHP.',
      },
      DK: {
        description:
          'Ensighten er et container tag management system. Den løser de problemer, der er forbundet med webstedsmærkning og sporing af online marketingkampagner ved at tilbyde universelle tag-løsninger, eller på serversiden. Nogle gange annoncerer vi på tredjepartswebsteder. Ensighten Cookies bruges til at hjælpe os og vores annoncører med at se, hvilke annoncer du klikker på og interagerer med. Hver enkelt annoncør bruger sine egne sporingscookies, og de data, der er taget, er ikke fortrolige eller udskiftelige.',
        dataShare: 'Denne cookie deler data med vores betroede tredjepartspartnere og/eller uden for EU/EØS.',
      },
      DE: {
        description:
          'Ensighten ist ein Container-Tag-Management-System. Es adressiert die Probleme, die mit Site-Tagging und Tracking von Online-Marketing-Kampagnen verbunden sind, indem es universelle oder serverseitige Tag-Lösungen bereitstellt. Manchmal werben wir auf Websites von Drittanbietern. Ensighten-Cookies werden verwendet, um uns und unseren Werbekunden zu helfen, zu sehen, welche Werbung Sie anklicken und mit ihr interagieren. Jeder einzelne Werbetreibende verwendet seine eigenen Tracking-Cookies und die erfassten Daten sind nicht vertraulich oder austauschbar.',
        dataShare:
          'Dieses Cookie teilt Daten mit unseren vertrauenswürdigen Drittpartnern und/oder außerhalb der EU/EWR.',
      },
      EE: {
        description:
          'Ensighten on sildihaldussüsteem. See lahendab veebilehtede märgistamise ja veebiturunduskampaaniate jälgimisega seotud probleeme, pakkudes universaalseid ehk serveripoolseid märgistuslahendusi. Mõnikord reklaamime kolmanda osapoole veebisaitidel. Ensighteni küpsiseid kasutatakse selleks, et aidata meil ja meie reklaamijatel näha, millistele reklaamidele te klõpsate ja milliste reklaamidega te suhtlete. Iga üks reklaamija kasutab oma jälgimisküpsiseid ja võetud andmed ei ole konfidentsiaalsed ega vahetatavad.',
        dataShare:
          'See küpsis jagab andmeid meie usaldatud kolmandast isikust partneritega ja/või väljaspool EL-i/EMP-d.',
      },
      FI: {
        description:
          'Ensighten on konttien tunnistehallintajärjestelmä. Se korjaa ongelmat, jotka liittyvät sivuston merkitsemiseen ja verkkomarkkinointikampanjoiden seurantaan tarjoamalla universaalit tai palvelinpuolen tag-ratkaisut. Joskus mainostamme kolmansien osapuolten verkkosivustoilla. Ensighten-evästeitä käytetään auttamaan meitä ja mainostajiamme näkemään, mitä mainoksia napsautat ja joiden kanssa olet tekemisissä. Jokainen mainostaja käyttää omia seurantaevästeitään, ja otetut tiedot eivät ole luottamuksellisia tai vaihdettavissa.',
        dataShare:
          'Tämä eväste jakaa tietoja yrityksen kumppaneina toimivien luotettavien kolmansien osapuolten kanssa ja/tai EU:n/ETA:n ulkopuolelle.',
      },
      FR: {
        description:
          'Ensighten apporte des solutions de tag management. Il répond aux problèmes liés au tagging des sites et au suivi des campagnes de marketing en ligne en fournissant des solutions de tagging universel, ou côté serveur. Parfois, nous faisons de la publicité sur des sites Web tiers. Les cookies Ensighten sont utilisés pour nous aider, ainsi que nos annonceurs, à voir sur quelles publicités vous cliquez et avec lesquelles vous interagissez. Chaque annonceur individuel utilise ses propres cookies de suivi et les données recueillies ne sont ni confidentielles ni interchangeables.',
        dataShare: "Ce cookie partage des données avec nos partenaires tiers de confiance et/ou en dehors de l'UE/EEE.",
      },
      HU: {
        description:
          'Az Ensighten egy tárolócímke-kezelő rendszer. Univerzális vagy szerver oldali címkérési megoldások biztosításával megoldást nyújt az internetes marketingkampányok helycímkézésével és nyomon követésével kapcsolatos problémákra. Néha harmadik fél weboldalain hirdetünk. Az Ensighten cookie-k segítenek nekünk és hirdetőinknek látni, hogy mely hirdetésekre kattint, és mely hirdetésekkel lép kapcsolatba. Minden egyes hirdető saját nyomkövető cookie-kat használ; a kapott adatok nem bizalmasak vagy felcserélhetők.',
        dataShare: 'Ez a cookie adatokat oszt meg megbízható harmadik fél partnereinkkel és/vagy az EU/EGT-n kívül.',
      },
      IT: {
        description:
          'Ensighten è un sistema di gestione dei tag del contenitore. Risolve i problemi associati al tagging del sito e al monitoraggio delle campagne di marketing online fornendo soluzioni di tag universali o lato server. A volte faremo pubblicità su siti Web di terze parti. I cookie di Ensighten vengono utilizzati per aiutare noi e i nostri inserzionisti a vedere su quali annunci fai clic e con cui interagisci. Ogni singolo inserzionista utilizza i propri cookie di tracciamento ei dati raccolti non sono confidenziali o intercambiabili.',
        dataShare:
          "Questo cookie condivide i dati con i nostri partner terze parti fidate e/o al di fuori dell'UE/SEE.",
      },
      LT: {
        description:
          'Ensighten yra konteinerių žymių valdymo sistema. Joje sprendžiamos problemos, susijusios su svetainės žymėjimu ir internetinės rinkodaros kampanijų stebėjimu, teikiant universalius arba serverio pusės žymos sprendimus. Kartais mes reklamuojamės trečiųjų šalių svetainėse. Ensighten“slapukai naudojami tam, kad mums ir mūsų reklamuotojams būtų lengviau pamatyti, kuriuos skelbimus spustelite ir su kuriais bendraujate. Kiekvienas reklamuotojas naudoja savo stebėjimo slapukus, o paimti duomenys nėra konfidencialūs ar keičiami.',
        dataShare:
          'Šis slapukas bendrina duomenis su patikimais trečiųjų šalių partneriais ir (arba) už ES / EEE ribų.',
      },
      LV: {
        description:
          'Ensighten ir birku pārvaldības sistēma. Tā risina problēmas, kas saistītas ar vietņu marķēšanu un tiešsaistes mārketinga kampaņu izsekošanu, nodrošinot universālus jeb servera puses tagu risinājumus. Dažkārt mēs reklamējamies trešo pušu vietnēs. Ensighten sīkfaili tiek izmantoti, lai palīdzētu mums un mūsu reklāmdevējiem redzēt, uz kurām reklāmām jūs noklikšķināt un ar kurām mijiedarbojaties. Katrs atsevišķs reklāmdevējs izmanto savas izsekošanas sīkdatnes, un iegūtie dati nav konfidenciāli vai savstarpēji aizstājami.',
        dataShare: 'Šīs sīkdatnes kopīgo informāciju ar mūsu uzticamiem trešās puses partneriem un/vai ārpus ES/EEZ.',
      },
      NL: {
        description:
          'Ensighten is een tagbeheersysteem voor containers. Het pakt de problemen aan die gepaard gaan met site tagging en tracking van online marketing campagnes door universele, of server side, tag oplossingen aan te bieden. Soms zullen we adverteren op websites van derden. Ensighten Cookies worden gebruikt om ons en onze adverteerders te helpen zien op welke advertenties u klikt en welke interactie u heeft. Elke individuele adverteerder gebruikt zijn eigen tracking cookies en de verzamelde gegevens zijn niet vertrouwelijk of onderling uitwisselbaar.',
        dataShare: 'Deze cookie deelt gegevens met onze vertrouwde externe partners en/of buiten de EU/EER.',
      },
      NO: {
        description:
          'Ensighten er et container tag management system. Den løser problemene knyttet til nettstedstagging og sporing av online markedsføringskampanjer ved å tilby universelle taggløsninger, eller på serversiden. Noen ganger vil vi annonsere på tredjeparts nettsteder. Ensighten Cookies brukes til å hjelpe oss og annonsørene våre med å se hvilke annonser du klikker på og samhandler med. Hver enkelt annonsør bruker sine egne sporingscookies, og dataene som tas er ikke konfidensielle eller utskiftbare.',
        dataShare:
          'Denne informasjonskapselen deler data med våre pålitelige tredjepartsleverandører og/eller utenfor EU/EEA.',
      },
      PL: {
        description:
          'Ensighten jest systemem zarządzania tagami typu container. Rozwiązanie to eliminuje problemy związane z oznaczaniem witryn i śledzeniem kampanii marketingowych online, dostarczając uniwersalne, znajdujące się po stronie serwera, rozwiązania w zakresie tagów. Czasami będziemy reklamować się na stronach internetowych innych stron. Pliki cookie Ensighten są używane, aby pomóc nam i naszym reklamodawcom zobaczyć, które reklamy klika użytkownik i z którymi wchodzi on w interakcję. Każdy reklamodawca korzysta z własnych plików cookie dotyczących śledzenia, a pobrane dane nie są poufne ani zamienne.',
        dataShare: 'Ten plik cookie udostępnia dane naszym zaufanym partnerom lub podmiotom spoza UE/EOG.',
      },
      RO: {
        description:
          'Ensighten este un sistem de gestionare a tagurilor de tip container. Acesta soluţionează problemele legate de marcarea site-urilor şi urmărirea campaniilor de marketing online furnizând soluţii de marcare universale sau server side. Uneori, facem reclamă pe site-uri web terţe. Modulele cookie Ensighten sunt utilizate pentru a ne ajuta pe noi şi pe agenţiile noastre de publicitate să vedem pe ce reclame faceţi clic şi cu care interacţionaţi. Fiecare agenţie de publicitate individuală utilizează propriile sale module cookie de urmărire, iar datele preluate nu sunt confidenţiale sau interschimbabile.',
        dataShare: 'Acest modul cookie partajează date cu partenerii noştri terţi de încredere şi/sau în afara UE/SEE.',
      },
      SK: {
        description:
          'Ensighten je systém správy kontajnerových značiek. Rieši problémy spojené s označovaním lokality a sledovaním online marketingových kampaní poskytovaním univerzálnych riešení označovania, alebo riešení označovania na strane servera. Niekedy sa naše reklamy objavia na webových lokalitách tretích strán. Súbory cookie systému Ensighten sa používajú, aby nám a našim inzerentom pomohli zistiť, na ktoré reklamy klikáte a s ktorými interagujete. Každý jeden inzerent používa svoje vlastné sledovacie súbory cookie a získané údaje nie sú dôverné ani zameniteľné.',
        dataShare:
          'Tento súbor cookie zdieľa údaje s našimi dôveryhodnými partnermi tretích strán a/alebo mimo EÚ/EHP.',
      },
      SV: {
        description:
          'Ensighten är ett hanteringssystem för containertaggar. Det löser problemen förknippade med webbplatstaggar och spårning av marknadsföringskampanjer online, genom att tillhandahålla generella tagglösningar, eller servertagglösningar. Ibland annonserar vi på webbplatser från tredje part. Ensighten-kakor används för att hjälpa oss och våra annonsörer att se vilka annonser du klickar på och interagerar med. Varje enskild annonsör använder sina egna spårningskakor och de data som tas är inte konfidentiella eller utbytbara.',
        dataShare: 'Denna cookie delar data med våra betrodda tredjepartspartners och/eller utanför EU/EES.',
      },
      default: {
        description:
          'Ensighten is a container tag management system. It addresses the problems associated with site tagging and tracking of online marketing campaigns by providing universal, or server side, tag solutions. Sometimes we will advertise on 3rd party websites. Ensighten Cookies are used to help us and our advertisers see which advertisements you click on and interact with. Each individual advertiser uses its own tracking cookies and the data taken is not confidential or interchangeable.',
        dataShare: 'This cookie shares data with our trusted third party partners and/or outside the EU/EEA.',
      },
    },
    Incapsula: {
      EN: {
        description:
          'Incapsula DDoS protection and web application firewall cookie. Cookie is set to allow a visitor to receive site content from one out of multiple servers as the visitor browses the site. cookie is set to allow a visitor to receive site content from one out of multiple servers as the visitor browses the site. Allows the visitors session to be maintained. Cookie is removed after browser is closed while some persisted for 12 months.',
        dataShare: '',
      },
      CS: {
        description:
          'Ochrana Incapsula DDoS a soubor cookie brány firewall webových aplikací. Soubor cookie je nastaven tak, aby při procházení webu návštěvníkovi umožnil přijímání webového obsahu z jednoho z několika serverů. Soubor cookie je nastaven tak, aby při procházení webu návštěvníkovi umožnil přijímání webového obsahu z jednoho z několika serverů. Umožňuje udržování relací návštěvníků. Soubor cookie se odstraní po zavření prohlížeče, zatímco některé přetrvávají po dobu 12 měsíců.',
        dataShare: '',
      },
      DK: {
        description:
          'Incapsula DDoS-beskyttelse og firewall-cookie til webapplikationer. Cookie er indstillet til at give en besøgende mulighed for at modtage webstedsindhold fra en ud af flere servere, når den besøgende gennemsøger webstedet. cookie er indstillet til at tillade en besøgende at modtage webstedsindhold fra en ud af flere servere, når den besøgende gennemsøger webstedet. Tillader, at besøgssessionen opretholdes. Cookie fjernes, når browseren er lukket, mens nogle vedvarede i 12 måneder.',
        dataShare: '',
      },
      DE: {
        description:
          'Incapsula DDoS-Schutz und Web Application Firewall Cookie. Das Cookie wird gesetzt, um einem Besucher zu ermöglichen, den Inhalt der Website von einem von mehreren Servern zu erhalten, während der Besucher auf der Website surft. Ermöglicht die Aufrechterhaltung der Sitzung des Besuchers. Das Cookie wird nach dem Schließen des Browsers entfernt, während einige für 12 Monate bestehen bleiben.',
        dataShare: '',
      },
      EE: {
        description:
          'Incapsula DDoS-kaitse ja veebirakenduste tulemüüri küpsis. Küpsis on seatud selleks, et külastaja saaks veebilehe sisu ühest mitmest serverist, kui ta veebilehte sirvib. Võimaldab külastaja sessiooni säilitamist. Küpsis eemaldatakse pärast brauseri sulgemist, samas kui mõned püsivad 12 kuud.',
        dataShare: '',
      },
      FI: {
        description:
          'Incapsula DDoS -suojaus ja verkkosovelluksen palomuurieväste. Eväste on asetettu sallimaan kävijän vastaanottamaan sivuston sisältö yhdeltä palvelimelta, kun vierailija selaa sivustoa. eväste on asetettu sallimaan kävijän vastaanottamaan sivuston sisältö yhdeltä useista palvelimista, kun kävijä selaa sivustoa. Antaa vierailijaistunnon ylläpitää. Eväste poistetaan, kun selain on suljettu, kun taas jotkut jatkuivat 12 kuukautta.',
        dataShare: '',
      },
      FR: {
        description:
          "Protection DDoS Incapsula et cookie pare-feu d'application web. Le cookie est configuré pour permettre à un visiteur de recevoir le contenu du site à partir d'un des multiples serveurs lorsque le visiteur navigue sur le site. Le cookie est configuré pour permettre à un visiteur de recevoir le contenu du site à partir d'un des multiples serveurs lorsque le visiteur navigue sur le site. Permet de maintenir la session des visiteurs. Le cookie est supprimé après la fermeture du navigateur, tandis que certains persistent pendant 12 mois.",
        dataShare: '',
      },
      HU: {
        description:
          'Incapsula DDoS védelmi és webes alkalmazások tűzfal cookie-ja. A cookie-k lehetővé teszik a látogató számára, hogy a webhely tartalmát több szerver valamelyikéről kapja, miközben az oldalt böngészi. A cookie-k lehetővé teszik a látogató számára, hogy a webhely tartalmát több szerver valamelyikéről kapja, miközben az oldalt böngészi. Lehetővé teszi a látogatók számára a munkamenet folyamatosságát. A cookie-t a böngésző bezárása után eltávolítják, bár némelyik 12 hónapig is megmaradhat.',
        dataShare: '',
      },
      IT: {
        description:
          'Protezione DDoS Incapsula e cookie firewall per applicazioni web. Il cookie è impostato per consentire a un visitatore di ricevere il contenuto del sito da uno tra più server mentre il visitatore naviga nel sito. cookie è impostato per consentire a un visitatore di ricevere il contenuto del sito da uno tra più server mentre il visitatore naviga nel sito. Consente di mantenere la sessione dei visitatori. I cookie vengono rimossi dopo la chiusura del browser mentre alcuni persistono per 12 mesi.',
        dataShare: '',
      },
      LT: {
        description:
          'Incapsula DDoS apsauga ir žiniatinklio programų užkardos slapukas. Slapukas nustatytas leisti lankytojui gauti svetainės turinį iš vieno iš kelių serverių, kai lankytojas naršo svetainėje. Slapukas nustatytas leisti lankytojui gauti svetainės turinį iš vieno iš kelių serverių, kai lankytojas naršo svetainėje. Leidžia išlaikyti lankytojų sesiją. Slapukas pašalinamas uždarius naršyklę, o kai kurie iš jų išlieka 12 mėnesių.',
        dataShare: '',
      },
      LV: {
        description:
          'Incapsula DDoS aizsardzība un tīmekļa lietojumprogrammu ugunsmūria sīkfails. Sīkfails ir iestatīts, lai apmeklētājs varētu saņemt vietnes saturu no viena no vairākiem serveriem, kad apmeklētājs pārlūko vietni.  Ļauj saglabāt apmeklētāja sesiju. Sīkdatne tiek dzēsta pēc pārlūkprogrammas aizvēršanas, bet dažas saglabājas 12 mēnešus.',
        dataShare: '',
      },
      NL: {
        description:
          'Incapsula DDoS-bescherming en webapplicatie-firewall cookie. Cookie is ingesteld om een bezoeker in staat te stellen inhoud van de site te ontvangen van één van meerdere servers terwijl de bezoeker door de site bladert. cookie is ingesteld om een bezoeker in staat te stellen inhoud van de site te ontvangen van één van meerdere servers terwijl de bezoeker door de site bladert. Hiermee kan de bezoekerssessie in stand worden gehouden. Cookie wordt verwijderd nadat de browser is gesloten, terwijl sommige gedurende 12 maanden blijven bestaan.',
        dataShare: '',
      },
      NO: {
        description:
          'Incapsula DDoS-beskyttelse og webapplikasjon brannmur informasjonskapsel. Cookie er satt til å tillate en besøkende å motta nettstedinnhold fra en av flere servere når den besøkende surfer på nettstedet. informasjonskapsel er satt til å tillate en besøkende å motta innhold fra en av flere servere når den besøkende surfer på nettstedet. Tillater at besøkendes økt opprettholdes. Informasjonskapsel fjernes etter at nettleseren er lukket mens noen vedvarte i 12 måneder.',
        dataShare: '',
      },
      PL: {
        description:
          'Plik cookie Incapsula DDoS protection and web application. Plik cookie jest ustawiony tak, aby umożliwić odwiedzającemu otrzymywanie zawartości strony z jednego z wielu serwerów podczas przeglądania strony. Plik cookie jest ustawiony tak, aby umożliwić odwiedzającemu otrzymywanie zawartości strony z jednego z wielu serwerów podczas przeglądania strony. Umożliwia utrzymanie sesji odwiedzających. Pliki cookie są usuwane po zamknięciu przeglądarki, a niektóre są przechowywane przez 12 miesięcy.',
        dataShare: '',
      },
      RO: {
        description:
          'Modul cookie Incapsula de firewall pentru aplicaţiile web şi de protecţie DDoS. Modulul cookie este setat să permită unui vizitator să primească conţinut de pe site de la unul dintre mai multe servere, în timp ce vizitatorul navighează pe site. Modulul cookie este setat să permită unui vizitator să primească conţinut de pe site de la unul dintre mai multe servere, în timp ce vizitatorul navighează pe site. Permite menţinerea sesiunii vizitatorilor. Modulul cookie este eliminat după ce browserul este închis, însă unele se menţin timp de 12 luni.',
        dataShare: '',
      },
      SK: {
        description:
          'Ochrana pred útokmi DDoS Incapsula a súbory cookie brány firewall webovej aplikácie Súbor cookie sa nastavuje s cieľom umožniť návštevníkovi počas prehliadania lokality prijímať obsah lokality z jedného z viacerých serverov. Súbor cookie sa nastavuje s cieľom umožniť návštevníkovi počas prehliadania lokality prijímať obsah lokality z jedného z viacerých serverov. Umožňuje uchovať reláciu návštevníkov. Súbor cookie sa odstráni po zatvorení prehliadača, zatiaľ čo niektoré zostávajú 12 mesiacov.',
        dataShare: '',
      },
      SV: {
        description:
          'Incapsula DDoS-skydd och brandväggskaka för webbapplikationer. kakan är inställd för att tillåta en besökare att ta emot webbplatsinnehåll från en av flera servrar när besökaren surfar på webbplatsen. kakan är inställd för att tillåta en besökare att ta emot webbplatsinnehåll från en av flera servrar när besökaren surfar på webbplatsen. Tillåter att besökarsessionen bibehålls. kakan tas bort efter att webbläsaren stängts medan en del kvarstår i 12 månader.',
        dataShare: '',
      },
      default: {
        description:
          'Incapsula DDoS protection and web application firewall cookie. Cookie is set to allow a visitor to receive site content from one out of multiple servers as the visitor browses the site. cookie is set to allow a visitor to receive site content from one out of multiple servers as the visitor browses the site. Allows the visitors session to be maintained. Cookie is removed after browser is closed while some persisted for 12 months.',
        dataShare: 'This cookie shares data with our trusted third party partners',
      },
    },
    Google: {
      EN: {
        description:
          'It is used to determine if a user will receive a captcha. Something which is used for bot protection.',
        dataShare: '',
      },
      CS: {
        description:
          'Používá se k určení, zda se uživateli zobrazí test captcha. Něco, co se používá k ochraně proti botům ',
        dataShare: '',
      },
      DK: {
        description:
          'Det bruges til at afgøre, om en bruger vil modtage en captcha. Noget, der bruges til botbeskyttelse',
        dataShare: '',
      },
      DE: {
        description:
          'Es wird verwendet, um festzustellen, ob ein Benutzer ein Captcha erhält. Etwas, das für den Bot-Schutz verwendet wird ',
        dataShare: '',
      },
      EE: {
        description: 'Seda kasutatakse selleks, et määrata, kas kasutaja saab captcha. Kasutatakse botide kaitseks.',
        dataShare: '',
      },
      FI: {
        description:
          'Sitä käytetään määrittämään, saako käyttäjä captchan. Jotain, jota käytetään bottien suojaamiseen',
        dataShare: '',
      },
      FR: {
        description:
          'Il est utilisé pour déterminer si un utilisateur recevra un captcha. Un programme qui protège les sites Web contre les robots ',
        dataShare: '',
      },
      HU: {
        description:
          'Annak meghatározására szolgál, hogy a felhasználó kap-e captcha-t. Valami, amit botvédelemre használnak ',
        dataShare: '',
      },
      IT: {
        description:
          'Viene utilizzato per determinare se un utente riceverà un captcha. Qualcosa che viene utilizzato per la protezione dei bot',
        dataShare: '',
      },
      LT: {
        description: 'Jis naudojamas nustatyti, ar vartotojas gaus captcha. Kažkas, kuris naudojamas apsaugai',
        dataShare: '',
      },
      LV: {
        description: 'To izmanto, lai noteiktu, vai lietotājs saņems captcha. Tiek izmantots botu aizsardzībai.',
        dataShare: '',
      },
      NL: {
        description:
          'Het wordt gebruikt om te bepalen of een gebruiker een captcha zal ontvangen. Iets dat wordt gebruikt voor bot bescherming ',
        dataShare: '',
      },
      NO: {
        description: 'Den brukes til å avgjøre om en bruker vil motta en captcha. Noe som brukes til botbeskyttelse',
        dataShare: '',
      },
      PL: {
        description: 'Służy do określenia, czy użytkownik otrzyma captcha. Służy do ochrony przed botami ',
        dataShare: '',
      },
      RO: {
        description:
          'Este folosit pentru a determina dacă un utilizator va primi un captcha. Ceva utilizat pentru protecţia împotriva boţilor ',
        dataShare: '',
      },
      SK: {
        description: 'Retrieving data. Wait a few seconds and try to cut or copy again.',
        dataShare: '',
      },
      SV: {
        description:
          'Den används för att avgöra om en användare kommer att få en captcha. Något som används för bot-skydd ',
        dataShare: '',
      },
      default: {
        description:
          'It is used to determine if a user will receive a captcha. Something which is used for bot protection.',
        dataShare: 'This cookie shares data with our trusted third party partners',
      },
    },
    Piwik: {
      EN: {
        description:
          'This cookie stores unique ID of the user. It is used to recognize visitors and hold their various properties. It counts and tracks the pageviews. By default it expires after 13 months, but it can be changes.',
        dataShare: '',
      },
      CS: {
        description:
          'V tomto souboru cookie se ukládá jedinečné ID uživatele. Používá se k rozpoznávání návštěvníků a uložení různých vlastností. Počítá a sleduje zobrazování stránek. Ve výchozím nastavení platnost vyprší po 13 měsících, ale toto lze změnit.',
        dataShare: '',
      },
      DK: {
        description:
          'Denne cookie gemmer unikt id for brugeren. Det bruges til at genkende besøgende og holde deres forskellige egenskaber. Det tæller og sporer sidevisningerne. Som standard udløber den efter 13 måneder, men det kan være ændringer.',
        dataShare: '',
      },
      DE: {
        description:
          'Dieses Cookie speichert die eindeutige ID des Benutzers. Es wird verwendet, um Besucher zu erkennen und ihre verschiedenen Eigenschaften festzuhalten. Es zählt und verfolgt die Seitenaufrufe. Standardmäßig läuft es nach 13 Monaten ab, aber es kann geändert werden.',
        dataShare: '',
      },
      EE: {
        description:
          'See küpsis salvestab kasutaja unikaalse ID. Seda kasutatakse külastajate äratundmiseks ja nende erinevate seadete salvestamiseks. See salvestab ja jälgib lehekülje vaatamisi.  Aegub see 13 kuu pärast, kuid seda saab muuta.',
        dataShare: '',
      },
      FI: {
        description:
          'Tämä eväste tallentaa käyttäjän yksilöllisen tunnuksen. Sitä käytetään vierailijoiden tunnistamiseen ja heidän eri ominaisuuksiensa pitämiseen. Se laskee ja seuraa sivun katseluja. Oletuksena se vanhenee 13 kuukauden kuluttua, mutta se voi olla muutoksia.',
        dataShare: '',
      },
      FR: {
        description:
          "Ce cookie stocke un identifiant unique de l'utilisateur. Il est utilisé pour reconnaître les visiteurs et retenir leurs différentes propriétés. Il compte et suit les pages vues. Par défaut, il expire après 13 mois, mais il est possible de modifier ce délai.",
        dataShare: '',
      },
      HU: {
        description:
          'Ez a cookie a felhasználó egyedi azonosítóját tárolja. A látogatók felismerésére és különböző tulajdonaik megtartására használják. Számolja és nyomon követi az oldal látogatottságát. Alapértelmezetten 13 hónap után lejár, de ebben lehet változás.',
        dataShare: '',
      },
      IT: {
        description:
          "Questo cookie memorizza l'ID univoco dell'utente. È usato per riconoscere i visitatori e mantenere le loro varie proprietà. Conta e tiene traccia delle visualizzazioni di pagina. Per impostazione predefinita, scade dopo 13 mesi, ma può essere modificato.",
        dataShare: '',
      },
      LT: {
        description:
          'Šis slapukas saugo unikalų vartotojo ID. Jis naudojamas atpažinti lankytojus ir išlaikyti jų įvairias savybes. Jis skaičiuoja ir stebi puslapių peržiūras. Pagal numatytuosius nustatymus jis baigiasi po 13 mėnesių, tačiau tai gali būti pakeitimai.',
        dataShare: '',
      },
      LV: {
        description:
          'Šajā sīkfailā tiek saglabāts lietotāja unikālais ID. Tas tiek izmantots, lai atpazītu apmeklētājus un saglabātu to dažādos iestatījumus. Tas uzskaita un izseko lapu skatījumus. Pēc noklusējuma tā derīguma termiņš beidzas pēc 13 mēnešiem, taču to var mainīt.',
        dataShare: '',
      },
      NL: {
        description:
          'Deze cookie slaat de unieke ID van de gebruiker op. Het wordt gebruikt om bezoekers te herkennen en hun verschillende eigenschappen vast te houden. Het telt en houdt de pageviews bij. Standaard verloopt het na 13 maanden, maar dit kan gewijzigd worden.',
        dataShare: '',
      },
      NO: {
        description:
          'Denne informasjonskapselen lagrer brukerens unike ID. Den brukes til å gjenkjenne besøkende og beholde deres forskjellige eiendommer. Den teller og sporer sidevisningene. Som standard utløper den etter 13 måneder, men det kan være endringer.',
        dataShare: '',
      },
      PL: {
        description:
          'Ten plik cookie przechowuje unikatowy identyfikator użytkownika. Jest on używany do rozpoznawania odwiedzających i przechowywania ich różnych właściwości. Liczy i śledzi odwiedziny strony. Domyślnie wygasa po 13 miesiącach, ale można to zmienić.',
        dataShare: '',
      },
      RO: {
        description:
          'Acest modul cookie stochează ID-ul unic al utilizatorului. Este utilizat pentru a recunoaşte vizitatorii şi a reţine diversele caracteristici ale acestora. Acesta numără şi urmăreşte vizualizările. Expiră implicit după 13 luni, dar pot apărea modificări.',
        dataShare: '',
      },
      SK: {
        description:
          'Tento súbor cookie ukladá jedinečné ID používateľa. Používa sa na rozpoznanie návštevníkov a uchovanie ich rôznych charakteristík. Počíta a sleduje zobrazenia stránok. Predvolene vyprší jeho platnosť po 13 mesiacoch, ale táto lehota sa dá zmeniť.',
        dataShare: '',
      },
      SV: {
        description:
          'Denna kaka lagrar unikt ID för användaren. Den används för att känna igen besökare och behålla deras egenskaper. Den räknar och spårar sidvisningar. Upphör som regel efter 13 månader, men det kan komma förändringar.',
        dataShare: '',
      },
      default: {
        description:
          'This cookie stores unique ID of the user. It is used to recognize visitors and hold their various properties. It counts and tracks the pageviews. By default it expires after 13 months, but it can be changes.',
        dataShare: 'This cookie shares data with our trusted third party partners',
      },
    },
  },
};
/**
 * SOW Translation Object end here
 */

/**
 * SOW Code here to bottom
 */

var _private = {};

/**
 * Function used to customise the Privacy Modal HTML
 * @return  {undefined}
 */
Bootstrapper.privacy.customise_modal = function () {
  //Get all elements
  var modal_wrapper = document.getElementById('ensModalWrapper'),
    modal = document.querySelectorAll('#ensModalWrapper .ensModal')[0],
    toggle_rows = document.querySelectorAll('#ensModalWrapper .ensToggleRow'),
    toggle_labels = document.querySelectorAll('#ensModalWrapper .ensToggleRow > .ensToggleLabel .ensCheckbox'),
    btn_container = document.querySelectorAll('#ensModalWrapper .ensButtons')[0],
    show_expand_hide_btn =
      typeof Bootstrapper.gateway.environment.allowPerTagOptOut !== 'undefined'
        ? Bootstrapper.gateway.environment.allowPerTagOptOut
        : window.ensClientConfig.featureToggles.allowPerTagOptOut;

  //Loop around each category
  for (var i = 0; i < toggle_rows.length; i++) {
    var current_toggle_row = toggle_rows[i];

    // Add expand/hide elements
    if (show_expand_hide_btn) {
      //Create new elements
      var elContainer = document.createElement('div'),
        btn_expand_hide = document.createElement('button'),
        new_toggle_label = document.createElement('span'),
        old_toggle_label = current_toggle_row.getElementsByClassName('ensToggleLabel')[0],
        new_card = document.createElement('label'),
        old_card = current_toggle_row.getElementsByClassName('card')[0];

      elContainer.className = 'ens-toggle-row-item';

      //expand/hide button properties
      btn_expand_hide.type = 'button';
      btn_expand_hide.classList.add('btn-expand-hide');
      btn_expand_hide.innerHTML =
        '<span class="btn-expand-hide-text"></span><i class="icon-chevron chevron right"></i>';

      //New label without click interaction
      new_toggle_label.classList.add('ensToggleLabel');
      new_toggle_label.innerHTML = old_toggle_label.textContent || old_toggle_label.innerText;

      //New card (toggle)
      new_card.classList.add('card');
      if (old_toggle_label.getAttribute('for')) {
        new_card.setAttribute('for', old_toggle_label.getAttribute('for'));
      }
      new_card.innerHTML = old_card.innerHTML;

      //Remove old labels
      old_toggle_label.parentNode.removeChild(old_toggle_label);
      old_card.parentNode.removeChild(old_card);

      //Add and move elements to correct place
      elContainer.appendChild(new_toggle_label);
      elContainer.appendChild(new_card);
      current_toggle_row.appendChild(elContainer);
      current_toggle_row.appendChild(current_toggle_row.getElementsByClassName('description')[0]);
      current_toggle_row.appendChild(btn_expand_hide);
      current_toggle_row.appendChild(current_toggle_row.getElementsByClassName('ensTagsContent')[0]);
    }
  }

  //Expand/hide functionality
  if (show_expand_hide_btn) {
    Bootstrapper.on('click', '#ensModalWrapper .btn-expand-hide', function () {
      this.getElementsByClassName('icon-chevron')[0].classList.remove('right');
      this.getElementsByClassName('icon-chevron')[0].classList.remove('bottom');

      if (this.parentNode.classList.contains('active')) {
        this.parentNode.classList.remove('active');
        this.getElementsByClassName('btn-expand-hide-text')[0].innerHTML = this.getAttribute('data-expand');
        this.getElementsByClassName('icon-chevron')[0].classList.add('right');
      } else {
        this.parentNode.classList.add('active');
        this.getElementsByClassName('btn-expand-hide-text')[0].innerHTML = this.getAttribute('data-hide');
        this.getElementsByClassName('icon-chevron')[0].classList.add('bottom');
      }
    });
  }

  //Add Always allowed element to trusted domains (Strictly Necessary Cookies)
  var defaultDescription = document.getElementById('defaultdescription');
  var alwaysAllowedText = document.createElement('div');
  alwaysAllowedText.className = 'alwaysAllowedText';
  defaultDescription.parentNode.insertBefore(alwaysAllowedText, defaultDescription);

  //Run custom Text
  Bootstrapper.privacy.add_services_translations();
  window.dispatchEvent(new CustomEvent('cookieBannerLoaded', null));
};

/**
 * Function to add descriptions for individual services within consent categories
 * @return  {undefined}
 */
Bootstrapper.privacy.add_services_translations = function () {
  //Get Custom Text from
  var translations = Bootstrapper.privacy.services ? JSON.parse(JSON.stringify(Bootstrapper.privacy.services)) : {},
    environment = Bootstrapper.gateway.environment,
    services_list = environment.whitelist || window.ensClientConfig.whitelist || [],
    environment_name = environment.name,
    environment_language = environment.languageCode ? environment.languageCode.split('-')[0] : '',
    environment_country = environment.countryCode,
    settings_translations = translations.settings || {};
  delete translations.settings;

  //Add Tag Descriptions
  for (var category in services_list) {
    if (services_list.hasOwnProperty(category) && services_list[category].length > 0 && translations[category]) {
      var services = services_list[category],
        category_translations = translations[category];

      //Loop around each Category
      for (var i = 0; i < services.length; i++) {
        var current_service = services[i],
          current_service_translations =
            current_service.displayName && category_translations[current_service.displayName]
              ? category_translations[current_service.displayName]
              : {},
          localized_description = '',
          localized_data_share = '',
          cookie_purpose = '';

        //return environment descriptions for each cateogry. Default if no environment matches
        if (environment_name && current_service_translations[environment_name]) {
          if (current_service_translations[environment_name].description)
            localized_description = current_service_translations[environment_name].description;
          if (current_service_translations[environment_name].dataShare)
            localized_data_share = current_service_translations[environment_name].dataShare;
        } else if (current_service_translations.default) {
          if (current_service_translations.default.description)
            localized_description = current_service_translations.default.description;
          if (current_service_translations.default.dataShare)
            localized_data_share = current_service_translations.default.dataShare;
        }

        //If description found
        if (localized_description) {
          var slide_id = (category + '-' + current_service.displayName + '-' + current_service.tag.split('\\')[0])
              .replace(/[,.]/g, '')
              .replace(/\s/g, '-')
              .replace(/\/\//g, '/'),
            target = document.querySelectorAll(
              '#ensModalWrapper .tagContainer .ensToggleLabel[for^="' + slide_id + '"]',
            )[0];

          //If tag found
          if (target) {
            //create and add elements for description header, description and data share
            var elPurpose = document.createElement('div'),
              elDescription = document.createElement('div'),
              elDataShare = document.createElement('div');
            elPurpose.className = 'description header-descripion';
            elPurpose.innerHTML =
              settings_translations.cookie_purpose[environment_name] || settings_translations.cookie_purpose.default;
            elDescription.className = 'description services-description';
            elDescription.innerHTML = localized_description;
            elDataShare.className = 'description data-share-description';
            elDataShare.innerHTML = localized_data_share;
            target.parentNode.appendChild(elPurpose);
            target.parentNode.appendChild(elDescription);
            target.parentNode.appendChild(elDataShare);
          }
        }
      }
    }
  }

  //Expand and Hide text
  var show_expand_hide_btns = document.querySelectorAll('#ensModalWrapper .btn-expand-hide');
  for (var i = 0; i < show_expand_hide_btns.length; i++) {
    if (settings_translations && settings_translations.expand_services[i] && settings_translations.hide_services[i]) {
      var expand_text = '',
        hide_text = '';

      //return environment matches. If no match default used
      if (
        settings_translations &&
        environment_name &&
        settings_translations.expand_services[i][environment_name] &&
        settings_translations.hide_services[i][environment_name]
      ) {
        expand_text = settings_translations.expand_services[i][environment_name];
        hide_text = settings_translations.hide_services[i][environment_name];
      } else if (settings_translations.expand_services[i].default && settings_translations.hide_services[i].default) {
        expand_text = settings_translations.expand_services[i].default;
        hide_text = settings_translations.hide_services[i].default;
      }

      //If text found add to element
      if (expand_text && hide_text) {
        var labelText = '';
        // for (var i = 0; i < show_expand_hide_btns.length; i++) {
        //Loop around each expand button and add text and data attributes. Replace {category} with category label text
        labelText = show_expand_hide_btns[i].closest('.ensToggleRow').querySelector('.ensToggleLabel');
        show_expand_hide_btns[i].setAttribute('data-expand', expand_text);
        show_expand_hide_btns[i].setAttribute('data-hide', hide_text);
        show_expand_hide_btns[i].getElementsByClassName('btn-expand-hide-text')[0].innerHTML = expand_text;
        // }
      }
    }
  }

  //Always Allowed Text
  document.querySelector('.alwaysAllowedText').innerHTML =
    settings_translations.always_allowed[environment_name] || settings_translations.always_allowed.default;
};

/**
 * Function to run custom code
 * @return  {undefined}
 */
Bootstrapper.privacy.initCustomisedModal = function () {
  Bootstrapper.privacy.customise_modal();
};

/**
 * Wait for Privacy to initialise and Modal to be created before running custom code
 */
Bootstrapper.privacy.onAfterInit = function (config) {
  Bootstrapper.bindDOMParsed(function () {
    var waitForModal = setInterval(function () {
      if (document.querySelectorAll('#ensModalWrapper .ensModal')[0]) {
        clearInterval(waitForModal);
        Bootstrapper.privacy.initCustomisedModal();
      }
    }, 250);
  });
};

// DISTRELEC-29085
Bootstrapper.privacy.onBeforeInit = function (ensClientConfig) {
  ensClientConfig.featureToggles.disableEscapeKeyboardCommands = true;
};

Bootstrapper.privacy.openBanner = function () {
  injectModal();
  if (document.querySelectorAll('.ensNotifyBanner')[0] !== undefined) {
    document.querySelectorAll('.ensNotifyBanner')[0].classList.remove('hidden');
    document.querySelectorAll('.ensNotifyBanner')[0].style.display = 'block';
  }
  if (document.querySelectorAll('.cookie-modal')[0] !== undefined) {
    document.querySelectorAll('.cookie-modal')[0].classList.remove('d-none');
    document.querySelectorAll('.cookie-modal')[0].classList.remove('hidden');
  }
};

Bootstrapper.privacy.onAfterInit = function (ensClientConfig) {
  if (onAfterInit_old) {
    onAfterInit_old.apply(this, arguments);
  }

  var getConsentData = function () {
    var returnVal = googleConsentOptions.defaults || {};
    if (googleConsentOptions.analytics_storage_category) {
      var googleBlocked = isGoogleBlocked();
      var googleAllowed = isGoogleAllowed();

      returnVal.security_storage = 'granted';

      if (
        (Bootstrapper.gateway.getUserPreference(googleConsentOptions.analytics_storage_category) === '1' &&
          googleBlocked === false) ||
        googleAllowed
      ) {
        returnVal.analytics_storage = 'granted';
      } else {
        returnVal.analytics_storage = 'denied';
      }

      if (
        (Bootstrapper.gateway.getUserPreference(googleConsentOptions.advertising_storage_category) === '1' &&
          googleBlocked === false) ||
        googleAllowed
      ) {
        returnVal.ad_storage = 'granted';
        returnVal.personalization_storage = 'granted';

        // add in user data and personalization permissions for V2 consent
        returnVal.ad_user_data = 'granted';
        returnVal.ad_personalization = 'granted';
      } else {
        returnVal.ad_storage = 'denied';
        returnVal.personalization_storage = 'denied';

        // add in user data and personalization permissions for V2 consent
        returnVal.ad_user_data = 'denied';
        returnVal.ad_personalization = 'denied';
      }

      if (
        (Bootstrapper.gateway.getUserPreference(googleConsentOptions.enhanced_storage_category) === '1' &&
          googleBlocked === false) ||
        googleAllowed
      ) {
        returnVal.functionality_storage = 'granted';
      } else {
        returnVal.functionality_storage = 'denied';
      }
    }
    return returnVal;
  };

  gtag('consent', 'default', getConsentData());

  gtag('js', new Date());
  if (googleConsentOptions.url_passthrough) {
    gtag('set', 'url_passthrough', googleConsentOptions.url_passthrough);
  }
  if (googleConsentOptions.ads_data_redaction) {
    gtag('set', 'ads_data_redaction', googleConsentOptions.ads_data_redaction);
  }
  Bootstrapper.bindDOMParsed(function () {
    var waitForModal = setInterval(function () {
      if (document.querySelectorAll('#ensModalWrapper .ensModal')[0]) {
        clearInterval(waitForModal);
        Bootstrapper.privacy.initCustomisedModal();
      }
    }, 250);
  });
};

Bootstrapper.on('click', '#ensConsentWidget', function () {
  setChildCookies(false);
  if (document.querySelectorAll('.cookie-modal')[0] !== undefined) {
    document.querySelectorAll('.cookie-modal')[0].classList.remove('hidden');
  }
});

Bootstrapper.on('click', '#ensModalLink', function () {
  //force cookie sliders to actually update what the cookie status is set too
  //updatePreferences does not work in this case

  var privacyCookiesArr = Bootstrapper.gateway.getCookieTypes();
  privacyCookiesArr.reduce(function (index, value) {
    if (Bootstrapper.gateway.getCookie(value) === '1') {
      Bootstrapper.gateway.setCookie(value, '1'); //this forces it to update to correct status of sliders on FE
    }
  }, {});

  document.querySelectorAll('.cookie-modal')[0].classList.remove('hidden');
  setChildCookies(false);

  delete_cookie('DISTRELEC_ENSIGHTEN_PRIVACY_BANNER_VIEWED');
});

// Accept only essential cookies and consents
Bootstrapper.on('click', '#btn-accept-essential-cookies, #close-banner, #ensCancel', function () {
  CancelAndOpenBanner();
  hasBannerBeenViewed();
  injectBloomreachScriptAndTrack(false);

  if (document.querySelectorAll('.cookie-modal')[0] !== undefined) {
    document.querySelectorAll('.cookie-modal')[0].classList.remove('hidden');
  }

  var closing_message = {
    EN: 'By closing the cookie selection screen you have rejected all but essential cookies.',
    CS: 'Zavřením obrazovky pro výběr souborů cookie jste zamítli všechny, kromě základních souborů cookie.',
    DK: 'Lukker du "Valg af cookies"-skærmen afviser du alle cookies, bortset fra de nødvendige.',
    DK_EN: 'By closing the cookie selection screen you have rejected all but essential cookies.',
    DE: 'Durch das Schliessen der Cookie-Auswahlmaske haben Sie alle Cookies ausser den unbedingt erforderlichen Cookies abgelehnt.',
    EE: 'Küpsiste valikuekraani sulgemisel keeldute kõigist, välja arvatud olulistest küpsistest.',
    FI: 'Evästeiden valintanäytön sulkeminen hylkää kaikki muut paitsi välttämättömät evästeet.',
    FR: "En fermant l'écran de sélection des cookies, vous avez rejeté tous les cookies sauf ceux qui sont essentiels.",
    HU: 'A cookie-k kiválasztására szolgáló képernyő bezárásával az alapvető cookie-k kivételével Ön minden cookie-t elutasított.',
    IT: 'Chiudendo la schermata di selezione dei cookie si rifiutano tutti i cookie tranne quelli essenziali.',
    LT: 'Uždarydami slapukų pasirinkimo ekraną atmesite visus slapukus, išskyrus būtinuosius.',
    LV: 'Aizverot sīkdatņu atlases ekrānu, jūs atsakāties no visām sīkdatnēm, izņemot stingri nepieciešamās sīkdatnes.',
    NL: 'Door het selectiescherm voor cookies te sluiten, weigert u alle cookies, behalve essentiële cookies.',
    NO: 'Ved å lukke valgmenyen har du avvist alle bortsett fra grunnleggende informasjonskapsler',
    NO_EN: 'By closing the cookie selection screen you have rejected all but essential cookies.',
    PL: 'Zamykając ekran wyboru plików cookie, użytkownik akceptuje tylko niezbędne pliki cookie.',
    PL_EN: 'By closing the cookie selection screen you have rejected all but essential cookies.',
    RO: 'Prin închiderea ecranului de selecţie a modulelor cookie, respingeţi toate modulele cookie, în afara celor esenţiale.',
    SK: 'Zatvorením obrazovky výberu súborov cookie odmietate všetky súbory cookie okrem nevyhnutných.',
    SV: 'Genom att stänga skärmen för val av kakor har du avvisat alla utom nödvändiga cookies.',
    SV_EN: 'By closing the cookie selection screen you have rejected all but essential cookies.',
    default: 'By closing the cookie selection screen you have rejected all but essential cookies.',
  };
  acceptEssentialCookies();
  rejectPrivacyCookies();
  getChildCookieStates();
  setChildCookies(false);
  sendCustomEventOnBannerClick();

  if (document.querySelectorAll('.ensNotifyBanner')[0] !== undefined) {
    document.querySelectorAll('.ensNotifyBanner')[0].classList.add('hidden');
  }

  var closingElContainer = document.createElement('div');
  closingElContainer.style.display = 'none';
  closingElContainer.className = 'closing-banner-message fadeable';
  closingElContainer.innerHTML = closing_message[Bootstrapper.gateway.environment.name];

  document
    .getElementsByClassName('breadcrumb')[0]
    .parentNode.insertBefore(closingElContainer, document.getElementsByClassName('breadcrumb')[0]);
  fadeInElement(closingElContainer);

  var closingBannerDiv = document.getElementsByClassName('closing-banner-message')[0];
  setTimeout(function () {
    if (closingBannerDiv) {
      closingBannerDiv.classList.add('fade');
      closingBannerDiv.remove();
    }
  }, 5000);
});

// Accept all cookies and consents
Bootstrapper.on('click', '#ensCloseBannerBtn', function () {
  setCookie('DISTRELEC_ENSIGHTEN_PRIVACY_BANNER_VIEWED', 1, 365, '/');
  CancelAndOpenBanner();
  hasBannerBeenViewed();
  if (document.querySelectorAll('.cookie-modal')[0] !== undefined) {
    document.querySelectorAll('.cookie-modal')[0].classList.add('hidden');
  }
  acceptPrivacyCookies();
  if (document.querySelectorAll('.ensNotifyBanner')[0] !== undefined) {
    document.querySelectorAll('.ensNotifyBanner')[0].classList.add('hidden');
  }
  setChildCookies(true);
  checkIfBloomreachWasAcceptedBeforeAndTrack();
  updateAllConsent(true);
});

// Accept specific cookies and consents
Bootstrapper.on('click', '#ensSave', function () {
  checkForBloomreachConsentAndHandleTracking();
  setCookie('DISTRELEC_ENSIGHTEN_PRIVACY_BANNER_VIEWED', 1, 365, '/');
  hasBannerBeenViewed();
  getChildCookieStates();
  setChildCookies(false);
  updateIndividualConsent();
  sendCustomEventOnBannerClick();
});

/**************** END OF BOOTSTRAPPER FUNCTIONS & EVENT LISTENERS  ****************/

/**************** START OF DISTRELEC CUSTOM FUNCTIONS ****************/

function getBrowserLang(countrycode) {
  window.userLang = 'OTHER';
  // Get language
  var language = window.navigator.userLanguage || window.navigator.language;
  // Get primary standard
  language = language.substring(0, 2);

  if (language === 'cs' || digitalData.page.pageInfo.language === 'czech') {
    window.userLang = 'CS';
  }
  if (language === 'de' || digitalData.page.pageInfo.language === 'german') {
    window.userLang = 'DE';
  }
  if (language === 'da' || digitalData.page.pageInfo.language === 'danish') {
    window.userLang = 'DK';
  }
  if (language === 'et' || digitalData.page.pageInfo.language === 'estonian') {
    window.userLang = 'EE';
  }
  if (language === 'fi' || digitalData.page.pageInfo.language === 'finnish') {
    window.userLang = 'FI';
  }
  if (language === 'fr' || digitalData.page.pageInfo.language === 'french') {
    window.userLang = 'FR';
  }
  if (language === 'hu' || digitalData.page.pageInfo.language === 'hungarian') {
    window.userLang = 'HU';
  }
  if (language === 'it' || digitalData.page.pageInfo.language === 'italian') {
    window.userLang = 'IT';
  }
  if (language === 'lt' || digitalData.page.pageInfo.language === 'lithuanian') {
    window.userLang = 'LT';
  }
  if (language === 'lv' || digitalData.page.pageInfo.language === 'latvian') {
    window.userLang = 'LV';
  }
  if (language === 'nl' || digitalData.page.pageInfo.language === 'dutch') {
    window.userLang = 'NL';
  }
  if (
    language === 'no' ||
    language === 'nb' ||
    language === 'nn' ||
    digitalData.page.pageInfo.language === 'norwegian'
  ) {
    window.userLang = 'NO';
  }
  if (language === 'pl' || digitalData.page.pageInfo.language === 'polish') {
    window.userLang = 'PL';
  }
  if (language === 'ro' || digitalData.page.pageInfo.language === 'romanian') {
    window.userLang = 'RO';
  }
  if (
    language === 'sk' ||
    language === 'sl' ||
    digitalData.page.pageInfo.language === 'slovenčina' ||
    digitalData.page.pageInfo.language === 'slovenian'
  ) {
    window.userLang = 'SK';
  }
  if (language === 'sv' || digitalData.page.pageInfo.language === 'swedish') {
    window.userLang = 'SE';
  }
  return window.userLang;
}

window.userLang = getBrowserLang(digitalData.page.pageInfo.countryCode);
window.browserLang = digitalData.page.pageInfo.countryCode.concat(window.userLang);

if (window.userLang === 'OTHER') {
  //not a webshop language
  //set to DISTOTHER for non elfa sites (elfa sites have their own english language)
  if (
    digitalData.page.pageInfo.countryCode !== 'PL' ||
    digitalData.page.pageInfo.countryCode !== 'NO' ||
    digitalData.page.pageInfo.countryCode !== 'SE' ||
    digitalData.page.pageInfo.countryCode !== 'DK'
  ) {
    window.browserLang = 'DISTOTHER';
  }
}

//hide modal if banner has been interacted with (saved or accepted) as this cookie wont be here otherwise
function hasBannerBeenViewed() {
  if (document.querySelectorAll('.cookie-modal')[0] !== undefined) {
    var bannerViewedMatch = /^(.*;)?\s*DISTRELEC_ENSIGHTEN_PRIVACY_BANNER_VIEWED\s*=/.test(document.cookie);

    if (bannerViewedMatch) {
      document.querySelectorAll('.cookie-modal')[0].classList.add('d-none');
    } else {
      document.querySelectorAll('.cookie-modal')[0].classList.remove('d-none');
    }
  }
}

function delete_cookie(name) {
  document.cookie =
    name +
    '=; Path=/; Expires=Thu, 01 Jan 1970 00:00:01 GMT; domain=' +
    '.' +
    window.location.host.split('.')[1] +
    '.' +
    window.location.host.split('.')[2];
}

function setCookie(name, value, days, path) {
  var expires = new Date(Date.now() + days * 864e5).toUTCString();
  document.cookie = name + '=' + encodeURIComponent(value) + '; expires=' + expires + '; path=' + path;
}

function setChildCookies(isAllAccepted, state) {
  //fix for child cookie preferences not being checked on first click of modal
  var cookieInfo = window.localStorage.getItem('cookieInfo');
  var parsedInfo = JSON.parse(cookieInfo);

  var childCookieState = [];
  var toggleRow = document.querySelectorAll('.ensToggleRow .ens-toggle-row-item input');

  for (var i = 0; i < toggleRow.length; i++) {
    if (toggleRow[i].checked) {
      var childInputs = toggleRow[i].parentNode.parentNode.parentNode.querySelectorAll(
        '.ensTagsContent > .tagContainer input',
      );
      for (var j = 0; j < childInputs.length; j++) {
        if (isAllAccepted) {
          childInputs[j].checked = true;
        } else {
          if (parsedInfo) {
            for (var k = 0; k < parsedInfo.length; k++) {
              if (parsedInfo[k].name === childInputs[j].id) {
                childInputs[j].checked = parsedInfo[k].value;
              }
            }
          }
        }
        childCookieState.push({
          name: childInputs[j].id,
          value: childInputs[j].checked,
        });
      }
    }
  }
  window.localStorage.setItem('cookieInfo', JSON.stringify(childCookieState));
}

function getChildCookieStates() {
  var cookieInfo = window.localStorage.getItem('cookieInfo');
  var parsedInfo = JSON.parse(cookieInfo);
  var toggleRow = document.querySelectorAll('.ensToggleRow .ens-toggle-row-item input');
  var childCookieState = [];
  for (var i = 0; i < toggleRow.length; i++) {
    if (toggleRow[i].checked) {
      var childInputs = toggleRow[i].parentNode.parentNode.parentNode.querySelectorAll(
        '.ensTagsContent > .tagContainer input',
      );
      for (var j = 0; j < childInputs.length; j++) {
        if (parsedInfo) {
          for (var k = 0; k < parsedInfo.length; k++) {
            if (parsedInfo[k].name === childInputs[j].id) {
              parsedInfo[k].value = childInputs[j].checked;
            }
          }
        } else {
          childCookieState.push({
            name: childInputs[j].id,
            value: childInputs[j].checked,
          });
        }
      }
    }
  }
  window.localStorage.setItem('cookieInfo', parsedInfo ? JSON.stringify(parsedInfo) : JSON.stringify(childCookieState));
}

function acceptPrivacyCookies() {
  var privacyCookiesArr = Bootstrapper.gateway.getCookieTypes();
  var privacyCookiesObj = privacyCookiesArr.reduce(function (acc, curr) {
    acc[curr] = '1';
    return acc;
  }, {});
  Bootstrapper.gateway.setCookies(privacyCookiesObj);
  Bootstrapper.gateway.updatePreferences();
  hasBannerBeenViewed();
  sendCustomEventOnBannerClick();
}

function checkIfBloomreachWasAcceptedBeforeAndTrack() {
  if (window.localStorage.getItem('distrelec_ENSIGHTEN_INTERACTED_URLS')) {
    injectBloomreachScriptAndTrack(true);
  } else {
    window.exponea.start();
  }
}

function sendCustomEventOnBannerClick() {
  window.dispatchEvent(new CustomEvent('cookieBannerClicked', null));
}

function rejectPrivacyCookies() {
  var privacyCookiesArr = Bootstrapper.gateway.getCookieTypes();
  var privacyCookiesObj = privacyCookiesArr.reduce(function (acc, curr) {
    acc[curr] = '0';
    return acc;
  }, {});
  Bootstrapper.gateway.setCookies(privacyCookiesObj);
  Bootstrapper.gateway.updatePreferences();
  hasBannerBeenViewed();
}

function CancelAndOpenBanner() {
  Bootstrapper.gateway.openBanner();
}

function isGoogleBlocked() {
  return Bootstrapper.gateway.getList().CUSTOM_BLOCK.some(function (element) {
    return ~element.tag.indexOf('google-analytics');
  });
}

function isGoogleAllowed() {
  return Bootstrapper.gateway.getList().CUSTOM_ALLOW.some(function (element) {
    return ~element.tag.indexOf('google-analytics');
  });
}

function sendConsentUpdate(values) {
  if (typeof gtag !== 'undefined') {
    gtag('consent', 'update', values);
  }
}

function updateIndividualConsent() {
  var consentValues = {};
  var googleBlocked = isGoogleBlocked();
  var googleAllowed = isGoogleAllowed();

  if (!googleBlocked || googleAllowed) {
    consentValues['security_storage'] = 'granted';
  }

  if (
    (window.gateway.consentCookies.getCookie(googleConsentOptions.analytics_storage_category) === '1' &&
      googleBlocked === false) ||
    googleAllowed
  ) {
    consentValues['analytics_storage'] = 'granted';
  } else {
    consentValues['analytics_storage'] = 'denied';
  }

  if (
    (window.gateway.consentCookies.getCookie(googleConsentOptions.advertising_storage_category) === '1' &&
      googleBlocked === false) ||
    googleAllowed
  ) {
    consentValues['ad_storage'] = 'granted';
    consentValues['personalization_storage'] = 'granted';

    // add in user data and personalization permissions for V2 consent
    consentValues['ad_user_data'] = 'granted';
    consentValues['ad_personalization'] = 'granted';
  } else {
    consentValues['ad_storage'] = 'denied';
    consentValues['personalization_storage'] = 'denied';

    // add in user data and personalization permissions for V2 consent
    consentValues['ad_user_data'] = 'denied';
    consentValues['ad_personalization'] = 'denied';
  }

  if (
    (window.gateway.consentCookies.getCookie(googleConsentOptions.enhanced_storage_category) === '1' &&
      googleBlocked === false) ||
    googleAllowed
  ) {
    consentValues['functionality_storage'] = 'granted';
  } else {
    consentValues['functionality_storage'] = 'denied';
  }

  sendConsentUpdate(consentValues);
}

function updateAllConsent(value) {
  var categories = ['analytics', 'ad', 'personalization', 'functionality'];
  var googleBlocked = isGoogleBlocked();
  var googleAllowed = isGoogleAllowed();
  var consentValues = {};

  if (!googleBlocked || googleAllowed) {
    consentValues['security_storage'] = 'granted';
    consentValues['ad_user_data'] = value ? 'granted' : 'denied';
    consentValues['ad_personalization'] = value ? 'granted' : 'denied';
    for (var i = 0; i < categories.length; i++) {
      consentValues[categories[i] + '_storage'] = value ? 'granted' : 'denied';
    }
  }

  sendConsentUpdate(consentValues);
}

function acceptEssentialCookies() {
  setCookie('DISTRELEC_ENSIGHTEN_PRIVACY_BANNER_VIEWED', 1, 365, '/');
  hasBannerBeenViewed();
  updateAllConsent(false);
}

function fadeInElement(element) {
  element.style.removeProperty('display');
}

function checkForBloomreachConsentAndHandleTracking() {
  if (document.querySelector('#Marketing-Bloomreach-apiukexponeacom-slide') && window.exponea) {
    if (document.querySelector('#Marketing-Bloomreach-apiukexponeacom-slide').checked) {
      injectBloomreachScriptAndTrack(true);
    } else if (!document.querySelector('#Marketing-Bloomreach-apiukexponeacom-slide').checked) {
      injectBloomreachScriptAndTrack(false);
    }
  }
}

function injectBloomreachScriptAndTrack(isStartOfTracking) {
  var localEnvironment = document.head
    .querySelector('[name~=occ-backend-base-url][content]')
    .content.includes('OCC_BACKEND_BASE_URL_VALUE');
  var devEnvironment = document.head.querySelector('[name~=occ-backend-base-url][content]').content.includes('dev');
  var pretestEnvironment = document.head
    .querySelector('[name~=occ-backend-base-url][content]')
    .content.includes('pretest');
  var testEnvironment =
    !pretestEnvironment &&
    document.head.querySelector('[name~=occ-backend-base-url][content]').content.includes('test');
  var token = '';

  if (localEnvironment || devEnvironment || pretestEnvironment) {
    token = 'ed7433e4-14f9-11ee-80e1-ee1b690c1c7b';
  } else if (testEnvironment) {
    token = '263c5e0e-14fa-11ee-b6fd-a6946f2537d4';
  } else {
    token = '60208000-14fa-11ee-b6fd-a6946f2537d4';
  }

  !(function (e, n, t, i, r, o) {
    function s(e) {
      if ('number' != typeof e) return e;
      var n = new Date();
      return new Date(n.getTime() + 1e3 * e);
    }
    var a = 4e3,
      c = 'xnpe_async_hide';
    function p(e) {
      return e.reduce(
        function (e, n) {
          return (
            (e[n] = function () {
              e._.push([n.toString(), arguments]);
            }),
            e
          );
        },
        { _: [] },
      );
    }
    function m(e, n, t) {
      var i = t.createElement(n);
      i.src = e;
      var r = t.getElementsByTagName(n)[0];
      return r.parentNode.insertBefore(i, r), i;
    }
    function u(e) {
      return '[object Date]' === Object.prototype.toString.call(e);
    }
    (o.target = o.target || 'https://api.exponea.com'),
      (o.file_path = o.file_path || o.target + '/js/exponea.min.js'),
      (r[n] = p([
        'anonymize',
        'initialize',
        'identify',
        'getSegments',
        'update',
        'track',
        'trackLink',
        'trackEnhancedEcommerce',
        'getHtml',
        'showHtml',
        'showBanner',
        'showWebLayer',
        'ping',
        'getAbTest',
        'loadDependency',
        'getRecommendation',
        'reloadWebLayers',
        '_preInitialize',
        '_initializeConfig',
      ])),
      (r[n].notifications = p(['isAvailable', 'isSubscribed', 'subscribe', 'unsubscribe'])),
      (r[n].segments = p(['subscribe'])),
      (r[n]['snippetVersion'] = 'v2.7.0'),
      (function (e, n, t) {
        (e[n]['_' + t] = {}),
          (e[n]['_' + t].nowFn = Date.now),
          (e[n]['_' + t].snippetStartTime = e[n]['_' + t].nowFn());
      })(r, n, 'performance'),
      (function (e, n, t, i, r, o) {
        e[r] = {
          sdk: e[i],
          sdkObjectName: i,
          skipExperiments: !!t.new_experiments,
          sign: t.token + '/' + (o.exec(n.cookie) || ['', 'new'])[1],
          path: t.target,
        };
      })(r, e, o, n, i, RegExp('__exponea_etc__' + '=([\\w-]+)')),
      (function (e, n, t) {
        m(e.file_path, n, t);
      })(o, t, e),
      (function (e, n, t, i, r, o, p) {
        if (e.new_experiments) {
          !0 === e.new_experiments && (e.new_experiments = {});
          var l,
            f = e.new_experiments.hide_class || c,
            _ = e.new_experiments.timeout || a,
            g = encodeURIComponent(o.location.href.split('#')[0]);
          e.cookies &&
            e.cookies.expires &&
            ('number' == typeof e.cookies.expires || u(e.cookies.expires)
              ? (l = s(e.cookies.expires))
              : e.cookies.expires.tracking &&
                ('number' == typeof e.cookies.expires.tracking || u(e.cookies.expires.tracking)) &&
                (l = s(e.cookies.expires.tracking))),
            l && l < new Date() && (l = void 0);
          var d =
            e.target +
            '/webxp/' +
            n +
            '/' +
            o[t].sign +
            '/modifications.min.js?http-referer=' +
            g +
            '&timeout=' +
            _ +
            'ms' +
            (l ? '&cookie-expires=' + Math.floor(l.getTime() / 1e3) : '');
          'sync' === e.new_experiments.mode && o.localStorage.getItem('__exponea__sync_modifications__')
            ? (function (e, n, t, i, r) {
                (t[r][n] = '<' + n + ' src="' + e + '"></' + n + '>'),
                  i.writeln(t[r][n]),
                  i.writeln(
                    '<' +
                      n +
                      '>!' +
                      r +
                      '.init && document.writeln(' +
                      r +
                      '.' +
                      n +
                      '.replace("/' +
                      n +
                      '/", "/' +
                      n +
                      '-async/").replace("><", " async><"))</' +
                      n +
                      '>',
                  );
              })(d, n, o, p, t)
            : (function (e, n, t, i, r, o, s, a) {
                o.documentElement.classList.add(e);
                var c = m(t, i, o);
                function p() {
                  r[a].init || m(t.replace('/' + i + '/', '/' + i + '-async/'), i, o);
                }
                function u() {
                  o.documentElement.classList.remove(e);
                }
                (c.onload = p), (c.onerror = p), r.setTimeout(u, n), (r[s]._revealPage = u);
              })(f, _, d, n, o, p, r, t);
        }
      })(o, t, i, 0, n, r, e),
      (function (e, n, t) {
        var i;
        e[n]._initializeConfig(t),
          (null === (i = t.experimental) || void 0 === i ? void 0 : i.non_personalized_weblayers) &&
            e[n]._preInitialize(t),
          (e[n].start = function (i) {
            i &&
              Object.keys(i).forEach(function (e) {
                return (t[e] = i[e]);
              }),
              e[n].initialize(t);
          });
      })(r, n, o);
  })(document, 'exponea', 'script', 'webxpClient', window, {
    target: 'https://api.uk.exponea.com',
    // The token is used according to the environment
    token: token,
    compliance: { opt_in: true },
    experimental: {
      non_personalized_weblayers: true,
    },
    track: {
      google_analytics: false,
    },
  });

  if (isStartOfTracking) {
    window.exponea.start();
  }
}

function injectModal() {
  //inject modal in to page

  var blockCookieFence =
    window.location.href.indexOf('distrelec.com') == -1 || window.location.href.indexOf('rnd-electronics') == -1;
  if (blockCookieFence && document.querySelectorAll('.cookie-modal').length <= 0) {
    var elem = document.createElement('div');
    elem.classList.add('cookie-modal');
    document.body.appendChild(elem);
  }

  hasBannerBeenViewed();

  //remove hybris cookie banner
  if (document.querySelectorAll('.mod-cookie-notice')[0] !== undefined && !blockCookieFence) {
    document.querySelectorAll('.mod-cookie-notice')[0].remove();
  }

  //moving DY code over to fire modal to open on privacysettings page
  if (window.location.href.indexOf('privacysettings') !== -1) {
    Bootstrapper.gateway.openModal();
    Bootstrapper.gateway.closeBanner();
  }
}

/****************** END OF DISTRELEC CUSTOM FUNCTIONS **************/

injectModal();
