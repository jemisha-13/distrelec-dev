export const getLocaleImport = (language: string, country: string): (() => Promise<any> | undefined) => {
  const code = `${language}-${country}`;

  switch (code) {
    case 'en-CH':
      return () => import('@angular/common/locales/en-CH');
    case 'de-CH':
      return () => import('@angular/common/locales/de-CH');
    case 'fr-CH':
      return () => import('@angular/common/locales/fr-CH');
    case 'cs-CZ':
      return () => import('@angular/common/locales/cs');
    case 'en-NO':
    case 'no-NO':
      return () => import('@angular/common/locales/nb');
    case 'sk-SK':
      return () => import('@angular/common/locales/sk');
    case 'sv-SE':
    case 'en-SE':
      return () => import('@angular/common/locales/sv');
    case 'ro-RO':
      return () => import('@angular/common/locales/ro');
    case 'it-IT':
      return () => import('@angular/common/locales/it');
    case 'it-SM':
      return () => import('@angular/common/locales/it-SM');
    case 'it-VA':
      return () => import('@angular/common/locales/it-VA');
    case 'en-PL':
    case 'pl-PL':
      return () => import('@angular/common/locales/pl');
    case 'lv-LV':
      return () => import('@angular/common/locales/lv');
    case 'en-LI':
    case 'fr-LI':
    case 'de-LI':
      return () => import('@angular/common/locales/de-LI');
    case 'da-DK':
      return () => import('@angular/common/locales/da');
    case 'en-DK':
      return () => import('@angular/common/locales/en-DK');
    case 'hu-HU':
      return () => import('@angular/common/locales/hu');
    case 'lt-LT':
      return () => import('@angular/common/locales/lt');
    case 'de-AT':
      return () => import('@angular/common/locales/de-AT');
    case 'sv-FI':
      return () => import('@angular/common/locales/sv-FI');
    case 'fi-FI':
      return () => import('@angular/common/locales/fi');
    case 'en-NL':
      return () => import('@angular/common/locales/en-NL');
    case 'nl-NL':
      return () => import('@angular/common/locales/nl');
    case 'nl-BE':
      return () => import('@angular/common/locales/nl-BE');
    case 'fr-BE':
      return () => import('@angular/common/locales/fr-BE');
    case 'en-BE':
      return () => import('@angular/common/locales/en-BE');
    case 'fr-FR':
      return () => import('@angular/common/locales/fr');
    case 'et-EE':
      return () => import('@angular/common/locales/et');
    case 'de-DE':
      return () => import('@angular/common/locales/de');
    case 'en-DE':
      return () => import('@angular/common/locales/en-DE');
    default:
      return undefined;
  }
};
