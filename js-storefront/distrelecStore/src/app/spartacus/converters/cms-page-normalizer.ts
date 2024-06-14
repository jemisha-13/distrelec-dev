import { Injectable } from '@angular/core';
import { CmsStructureModel, Converter, Occ } from '@spartacus/core';

@Injectable({
  providedIn: 'root',
})
export class CmsPageNormalizer implements Converter<Occ.CMSPage, CmsStructureModel> {
  convert(source: any, target: CmsStructureModel = {}): CmsStructureModel {
    target.page.properties = {
      ...target.page.properties,
      languageEN: source.languageEN,
      seo: source.properties?.seo,
    };
    return target;
  }
}
