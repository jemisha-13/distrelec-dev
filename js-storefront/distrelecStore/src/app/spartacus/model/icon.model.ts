export enum DistIconCategory {
  ICON = 'icon',
  ICON_SOLID = 'icon-solid',
  BRANDS = 'brands',
  SOCIAL = 'social',
  HAZARD_SYMBOLS = 'hazard-symbols',
  FLAGS = 'flags',
  ENERGY_RATING = 'energy-rating',
  PAYMENT = 'payment',
}

export type DistIconType = 'svg' | 'png';

type DistElement = {
  type: string;
  tagName: string;
  properties: any;
  children: DistElement[];
};

export type DistIcon = {
  name: string;
  viewbox: string;
  dimensions: number[];
  colour: string;
  elements: DistElement[];
};
