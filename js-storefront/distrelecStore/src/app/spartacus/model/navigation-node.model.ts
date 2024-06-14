import { CmsNavigationEntry } from '@spartacus/core';
import { NavigationNode } from '@spartacus/storefront';

declare module '@spartacus/storefront' {
  export interface NavigationNode {
    active?: boolean;
    localizedTitle?: string;
    sortingNumber?: string;
    uid?: string;
    name?: string;
    entries?: Array<CmsNavigationEntry>;
    children?: Array<NavigationNode>;
  }
}

export interface ContentPageNavNodes {
  navRootNodes: NavigationNode[];
  pageRootNavNode: NavigationNode;
}

export {}; // Empty export to keep file a module
