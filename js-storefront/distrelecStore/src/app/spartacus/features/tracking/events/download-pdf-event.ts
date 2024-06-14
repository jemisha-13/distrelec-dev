/* eslint-disable @typescript-eslint/naming-convention */
import { ICustomProduct } from '@model/product.model';
import { CxEvent, Product } from '@spartacus/core';
import { EventProductGA4 } from '../model/event-product';
import { EventUserDetails } from '../model/event-user-details';
import { ItemListEntity } from '../model/generic-event-types';
import { PDFType } from '../model/pdf-types';
import { EventPageDetails } from '../model/event-page-details';

export class Ga4DownloadPDFEvent extends CxEvent {
  event = 'download_PDF';
  user: EventUserDetails;
  page: EventPageDetails;
  PDF_type: PDFType;
  pageType: ItemListEntity;
  ecommerce: {
    items: EventProductGA4[];
  };
}

export class DownloadPDFEvent extends CxEvent {
  context: {
    pageType: ItemListEntity;
    PDF_type: PDFType;
    product: Product | ICustomProduct;
  };
}
