import { Params } from '@angular/router';
import { Category } from '@spartacus/core';

declare module '@spartacus/core' {
  interface BreadcrumbMeta {
    queryParams?: Params;
    disabled?: boolean;
  }
}

export interface Breadcrumb extends Category {
  queryParams?: Params;
}

export interface BreadcrumbConfig {
  ignoredPageLabels: string[];
}
