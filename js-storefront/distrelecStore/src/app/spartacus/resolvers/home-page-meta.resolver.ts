import { Injectable } from '@angular/core';
import { DistContentPageMetaResolver } from './content-page-meta.resolver';

@Injectable({
  providedIn: 'root',
})
export class DistHomePageMetaResolver extends DistContentPageMetaResolver {
  pageTemplate = 'HomePage2018Template';
}
