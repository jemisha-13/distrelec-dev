import { I18nConfig } from '@spartacus/core';
import { translationChunksConfig } from '@assets/translations/translation-chunks-config';

// Force type as any to ignore definition in I18nConfig (string | boolean)
// Value is passed into i18next config, which allows an object
// https://github.com/SAP/spartacus/blob/37dffa198e31ddfb89e4cc03c0481410e395243b/projects/core/src/i18n/i18next/i18next-init.ts
// https://www.i18next.com/principles/fallback#fallback-to-different-languages
/* eslint-disable @typescript-eslint/naming-convention */
const fallbackLang: any = {
  cs_CZ: ['cs'],

  da_DK: ['da'],

  de_AT: ['de'],
  de_CH: ['de'],
  de_DE: ['de'],
  de_LI: ['de'],

  en_BE: ['en'],
  en_CH: ['en'],
  en_DE: ['en'],
  en_DK: ['en'],
  en_EE: ['en'],
  en_EX: ['en'],
  en_FR: ['en'],
  en_LI: ['en'],
  en_LV: ['en'],
  en_NL: ['en'],
  en_NO: ['en'],
  en_PL: ['en'],
  en_RO: ['en'],
  en_SE: ['en'],

  et_EE: ['et'],

  fi_FI: ['fi'],

  fr_BE: ['fr', 'en'],
  fr_CH: ['fr', 'en'],
  fr_FR: ['fr'],
  fr_LI: ['fr', 'en'],

  hu_HU: ['hu'],

  it_IT: ['it'],
  it_SM: ['it', 'en'],
  it_VA: ['it', 'en'],

  lt_LT: ['lt'],

  lv_LV: ['lv'],

  nl_BE: ['nl', 'en'],
  nl_NL: ['nl'],

  no_NO: ['no'],

  pl_PL: ['pl', 'en'],

  ro_RO: ['ro'],

  sk_SK: ['sk'],

  sv_FI: ['sv', 'en'],
  sv_SE: ['sv', 'en'],

  // International webshop countries should fall back to en_EX
  en_BG: ['en_EX', 'en'],
  en_HR: ['en_EX', 'en'],
  en_CY: ['en_EX', 'en'],
  en_EL: ['en_EX', 'en'],
  en_IE: ['en_EX', 'en'],
  en_LU: ['en_EX', 'en'],
  en_MT: ['en_EX', 'en'],
  en_PT: ['en_EX', 'en'],
  en_SI: ['en_EX', 'en'],
  en_ES: ['en_EX', 'en'],
  en_GB: ['en_EX', 'en'],
  en_XI: ['en_EX', 'en'],

  default: ['en'],
};
/* eslint-enable @typescript-eslint/naming-convention */

export const i18nConfig: I18nConfig = {
  i18n: {
    backend: {
      loader: (language: string, namespace: string) => import(`../assets/translations/${language}/${namespace}.json`),
    },
    chunks: translationChunksConfig,
    fallbackLang,
  },
};
