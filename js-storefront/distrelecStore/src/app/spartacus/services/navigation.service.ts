import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { CmsNavigationComponent, CmsService, OccEndpointsService, SemanticPathService } from '@spartacus/core';
import { combineLatest, Observable, of } from 'rxjs';
import { filter, map, switchMap, take, tap } from 'rxjs/operators';
import { NavigationNode } from '@spartacus/storefront';

@Injectable({
  providedIn: 'root',
})
export class NavigationService {
  constructor(
    protected cmsService: CmsService,
    protected semanticPathService: SemanticPathService,
    private http: HttpClient,
    protected occEndpoints: OccEndpointsService,
  ) {}

  getMultipleNavigationNodes(ids: string): Observable<NavigationNode[]> {
    return this.http.get<NavigationNode[]>(this.occEndpoints.buildUrl(`/navigation-nodes`, { queryParams: { ids } }));
  }

  getNavigationNodeChildren(uid: string): Observable<NavigationNode> {
    return this.http.get<NavigationNode>(this.occEndpoints.buildUrl(`/navigation-node/${uid}`));
  }

  public createNavigation(data$: Observable<CmsNavigationComponent>): Observable<NavigationNode> {
    return combineLatest([data$, this.getNavigationNode(data$)]).pipe(
      map(([data, nav]) => ({
        title: data?.name ?? nav.title,
        children: [nav],
      })),
      take(1),
    );
  }

  /**
   * returns an observable with the `NavigationNode` for the given `CmsNavigationComponent`.
   * This function will load the navigation underlying entries and children if they haven't been
   * loaded so far.
   */
  public getNavigationNode(data$: Observable<CmsNavigationComponent>): Observable<NavigationNode> {
    if (!data$) {
      return of();
    }
    return data$.pipe(
      filter((data) => !!data),
      switchMap((data) => {
        const navigation = data.navigationNode ? data.navigationNode : data;
        return this.cmsService.getNavigationEntryItems(navigation.uid).pipe(
          tap((entryData) => {
            if (entryData === undefined) {
              this.loadNavigationEntryItems(navigation, true);
            }
          }),
          map((items) => this.populateNavigationNode(navigation, items ?? {})),
        );
      }),
    );
  }

  // reimplements sorting implemented within DistMainNavigationComponentController#sortByTitle(..)
  sortTopLevelCategoryNavData(categoryNavigationNodes: NavigationNode[]): NavigationNode[] {
    if (categoryNavigationNodes.length > 3) {
      const nodesBySortingNumber = Array.from(categoryNavigationNodes).sort((a, b) =>
        (a.sortingNumber ?? '0').localeCompare(b.sortingNumber ?? '0'),
      );
      const result = nodesBySortingNumber
        .slice(0, categoryNavigationNodes.length - 3)
        .sort((a, b) => {
          const titleA = a.title ?? a.localizedTitle;
          const titleB = b.title ?? b.localizedTitle;
          return titleA.localeCompare(titleB);
        })
        .concat(nodesBySortingNumber.slice(-3));
      return result;
    }

    return Array.from(categoryNavigationNodes).sort((a, b) => {
      const titleA = a.title ?? a.localizedTitle;
      const titleB = b.title ?? b.localizedTitle;
      return titleA.localeCompare(titleB);
    });
  }

  /**
   * Loads all navigation entry items' type and id. Dispatch action to load all these items
   *
   * @param nodeData
   * @param root
   * @param itemsList
   */
  private loadNavigationEntryItems(nodeData: any, root: boolean, itemsList = []): void {
    if (nodeData.entries && nodeData.entries.length > 0) {
      nodeData.entries.forEach((entry) => {
        itemsList.push({
          superType: 'AbstractCMSComponent',
          id: entry.itemId,
        });
      });
    }

    if (nodeData.children && nodeData.children.length > 0) {
      nodeData.children.forEach((child) => {
        if (child.entries && child.entries.length > 0) {
          child.entries.forEach((entry) => {
            itemsList.push({
              superType: 'AbstractCMSComponent',
              id: entry.itemId,
            });
          });
        }

        // this.loadNavigationEntryItems(child, false, itemsList)
      });
    }

    if (root) {
      this.cmsService.loadNavigationItems(nodeData.uid, itemsList);
    }
  }

  /**
   * Create a new node tree for the view
   *
   * @param nodeData
   * @param items
   */
  private populateNavigationNode(nodeData: any, items: any): NavigationNode {
    const node: NavigationNode = {};

    if (nodeData.title) {
      // the node title will be populated by the first entry (if any)
      // if there's no nodeData.title available
      node.title = nodeData.title;
    }

    if (nodeData.sortingNumber) {
      node.sortingNumber = nodeData.sortingNumber;
    }

    if (nodeData.uid) {
      node.uid = nodeData.uid;
    }

    if (nodeData.name) {
      node.name = nodeData.name;
    }

    if (nodeData.entries?.length > 0) {
      this.populateLink(node, nodeData.entries[0], items);
    }

    if (nodeData.children?.length > 0) {
      const children = nodeData.children.map((child) => this.populateNavigationNode(child, items)).filter(Boolean);
      if (children.length > 0) {
        node.children = children;
      }
    }

    // return null in case there are no children
    return Object.keys(node).length === 0 ? null : node;
  }

  /**
   * The node link is driven by the first entry.
   */
  private populateLink(node: NavigationNode, entry, items) {
    const item = items[`${entry.itemId}_${entry.itemSuperType}`];

    // now we only consider CMSLinkComponent
    if (item && entry.itemType === 'CMSLinkComponent') {
      node.url = item.localizedUrl;
    } else {
      node.url = entry.localizedUrl;
    }
  }
}
