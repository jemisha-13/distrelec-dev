import { Country } from '@spartacus/core';

export interface FunctionList {
  code: string;
  name: string;
  disabled: boolean;
}

export interface DepartmentList {
  code: string;
  name: string;
  disabled: boolean;
}

export interface TitleList {
  code: string;
  name: string;
}

export interface CountryResponse {
  countries: Country[];
}

export interface DashboardContents {
  appReqCount: number;
  newInvoicesCount: number;
  openOrdersCount: number;
  quoteCount: number;
}
