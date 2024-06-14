import { Component, Input, NgZone, OnChanges, OnInit, ViewChild } from '@angular/core';
import { faBars, faChevronRight, faAngleDown } from '@fortawesome/free-solid-svg-icons';
import { BehaviorSubject, Observable } from 'rxjs';
import { first } from 'rxjs/operators';
import { Router } from '@angular/router';
import { NavigationService } from '@services/navigation.service';
import { SlideDrawerService } from '@design-system/slide-drawer/slide-drawer.service';
import { DistBreakpointService } from '@services/breakpoint.service';
import { SlideDrawerComponent } from '@design-system/slide-drawer/slide-drawer.component';
import { BREAKPOINT, NavigationNode } from '@spartacus/storefront';

@Component({
  selector: 'app-category-nav',
  templateUrl: './category-nav.component.html',
  styleUrls: ['./category-nav.component.scss'],
})
export class CategoryNavComponent implements OnInit, OnChanges {
  //TODO: @CLEANUP: the category nav data we get is too heavily nested, we should look at improving the data model?
  //maybe once we get the data we should clean it and only keep the bits we need?
  private static categoryNav_: BehaviorSubject<NavigationNode[]> = new BehaviorSubject([]);

  @Input() categoryNavData: NavigationNode;
  @ViewChild('flyout') flyout: SlideDrawerComponent;

  isMobileBreakpoint$: Observable<boolean> = this.breakpointService.isMobileBreakpoint();
  isTablet$: Observable<boolean> = this.breakpointService.isDown(BREAKPOINT.md);
  faBars = faBars;
  faChevronRight = faChevronRight;
  faAngleDown = faAngleDown;
  secondPanelTitle = '';
  selectedChildIndex: number;
  haveChildrenData = false;
  mainCategoryId: string;

  static count = 0;

  private flyoutTimer: ReturnType<typeof setTimeout>;

  constructor(
    private router: Router,
    private navigationService: NavigationService,
    private breakpointService: DistBreakpointService,
    private slideDrawerService: SlideDrawerService,
    private ngZone: NgZone,
  ) {
    this.mainCategoryId = `main-category-level-${CategoryNavComponent.count}`;
    CategoryNavComponent.count++;
  }

  public ngOnInit(): void {
    if (!CategoryNavComponent.categoryNav_.value.length) {
      CategoryNavComponent.categoryNav_.next(this.sortCategories());
    }
  }

  ngOnChanges(): void {
    this.haveChildrenData = false;
    CategoryNavComponent.categoryNav_.next(this.sortCategories());
  }

  public handleClick(event: Event, i: number): void {
    event.stopPropagation();
    const url: string | undefined = (CategoryNavComponent.categoryNav_?.value[i]?.url as string) || undefined;
    this.router.navigateByUrl(url);
  }

  public handleMouseOut(): void {
    clearTimeout(this.flyoutTimer);
  }

  public handleSecondPanelClick(event: Event, i: number): void {
    event.stopPropagation();
    this.router.navigateByUrl(undefined);

    const url: string | undefined =
      CategoryNavComponent.categoryNav_?.value[this.selectedChildIndex]?.children[0]?.children[i]?.entries[0]
        ?.localizedUrl || undefined;

    this.router.navigateByUrl(url);
  }

  public openSecondLevel(event: Event, childIndex: number): void {
    event.stopPropagation();
    if (!this.haveChildrenData) {
      return;
    }

    this.selectedChildIndex = childIndex;
    this.secondPanelTitle = CategoryNavComponent.categoryNav_.value[childIndex].title;

    const childData = CategoryNavComponent.categoryNav_.value[childIndex].children[0];
    if (!childData?.children?.length) {
      this.flyout.closeSecondPanel();
      return;
    }

    if (this.flyout.showSecondPanel_.value) {
      return;
    }
    clearTimeout(this.flyoutTimer);

    if (event.type !== 'click') {
      this.ngZone.run(() => (this.flyoutTimer = setTimeout(() => this.flyout.openSecondPanel(), 500)));
    } else {
      this.flyout.openSecondPanel();
    }
  }

  public openPanel(event: Event): void {
    //@FIXME: closing second panel below is a bandage fix for rxjs not updating showSecondPanel_ in the template when navigating or pressing escape,
    this.flyout.closeSecondPanel();
    if (!this.haveChildrenData) {
      this.getSecondLevelData();
    }
    this.slideDrawerService.openPanel(event, 'products-flyout', 'LEFT');
  }

  public get navData_(): BehaviorSubject<NavigationNode[]> {
    return CategoryNavComponent.categoryNav_;
  }

  private getSecondLevelData(): void {
    const uids: string = (CategoryNavComponent.categoryNav_.value as NavigationNode[])
      .map((node: NavigationNode) => node.uid)
      .toString();

    this.navigationService
      .getMultipleNavigationNodes(uids)
      .pipe(first())
      .subscribe((data: NavigationNode[]) => {
        const navData: NavigationNode[] = CategoryNavComponent.categoryNav_.value;

        for (let i = 0; i < data.length; i++) {
          navData[i].children = [data[i]];
        }
        this.haveChildrenData = true;
        CategoryNavComponent.categoryNav_.next(navData);
      });
  }

  private sortCategories(): NavigationNode[] {
    const extracts = ['450', '460', '470']; //clearance, newProducts, extendedRanges
    const extracted: [sort: number, data: NavigationNode][] = [];

    const categories: NavigationNode[] = this.categoryNavData.children[0].children
      .map((node: NavigationNode) => {
        if (!extracts.includes(node.sortingNumber)) {
          return [node.title, node];
        }
        extracted.push([Number(node.sortingNumber), node]);
      })
      .sort()
      .filter(Boolean)
      .map((pair: [title: string, node: NavigationNode]) => pair[1]) as NavigationNode[];

    return categories.concat(
      extracted.sort().map((pair: [sort: number, data: NavigationNode]) => pair[1]),
    ) as unknown as NavigationNode[];
  }
}
