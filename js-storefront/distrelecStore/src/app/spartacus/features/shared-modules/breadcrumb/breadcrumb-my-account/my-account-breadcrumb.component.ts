import { Component, OnDestroy, OnInit } from '@angular/core';
import { ActivatedRoute, NavigationEnd, Router } from '@angular/router';
import { DistrelecUserService } from '@services/user.service';
import { Subscription } from 'rxjs';
import { distinctUntilChanged, filter } from 'rxjs/operators';

interface IBreadCrumb {
  label: string;
  url: string;
  isChild: boolean;
  key: string;
  hideLastLabel: boolean;
  selectedParam: string;
  parent: any;
}

@Component({
  selector: 'app-my-account-breadcrumb',
  templateUrl: './my-account-breadcrumb.component.html',
  styleUrls: ['./my-account-breadcrumb.component.scss'],
})
export class MyAccountBreadCrumbComponent implements OnInit, OnDestroy {
  public breadcrumbs: IBreadCrumb[];
  private subscription: Subscription = new Subscription();

  constructor(
    private router: Router,
    private activatedRoute: ActivatedRoute,
    private userDetailsService: DistrelecUserService,
  ) {
    this.breadcrumbs = this.buildBreadCrumb(this.activatedRoute.root);
  }

  ngOnInit() {
    this.subscription.add(
      this.router.events
        .pipe(
          filter((event) => event instanceof NavigationEnd),
          distinctUntilChanged(),
        )
        .subscribe(() => {
          this.breadcrumbs = this.buildBreadCrumb(this.activatedRoute.root);
        }),
    );
  }

  /**
   * Recursively build breadcrumb according to activated route.
   *
   * @param route
   * @param url
   * @param breadcrumbs
   */
  buildBreadCrumb(route: ActivatedRoute, url: string = '', breadcrumbs: IBreadCrumb[] = []): IBreadCrumb[] {
    //If no routeConfig is avalailable we are on the root path

    let label = route.routeConfig && route.routeConfig.data ? route.routeConfig.data.breadcrumb : '';
    let path = route.routeConfig && route.routeConfig.data ? route.routeConfig.path : '';
    const isChild = route.routeConfig && route.routeConfig.data ? route.routeConfig.data.isChild : false;
    const hideLastLabel = route.routeConfig && route.routeConfig.data ? route.routeConfig.data.hideLastLabel : false;
    const selectedParam = route.routeConfig && route.routeConfig.data ? route.routeConfig.data.selectedParam : '';

    // If the route is dynamic route such as ':id', remove it
    const lastRoutePart = path.split('/').pop();
    const isDynamicRoute = lastRoutePart.startsWith(':');
    if (isDynamicRoute && !!route.snapshot) {
      const paramName = lastRoutePart.split(':')[1];

      path = path.replace(lastRoutePart, route.snapshot.params[paramName]);
      label = route.snapshot.params[paramName];
    }

    //In the routeConfig the complete path is not available,
    //so we rebuild it each time
    const nextUrl = path ? `${url}/${path}` : url;

    const breadcrumb: IBreadCrumb = {
      label,
      url: nextUrl,
      isChild,
      key: route.routeConfig?.data?.key,
      hideLastLabel,
      selectedParam,
      parent: route.routeConfig?.data?.parent?.map((x) => ({
        parentUrl: this.getParentUrlIfB2C(x.url),
        label: x.label,
        key: x.key,
        type: x.type,
        prefix: x.prefix,
        param: x.param,
      })),
    };
    // Only adding route with non-empty label
    const newBreadcrumbs = breadcrumb.label ? [...breadcrumbs, breadcrumb] : [...breadcrumbs];
    if (route.firstChild) {
      //If we are not on our current path yet,
      //there will be more children to look after, to build our breadcumb
      return this.buildBreadCrumb(route.firstChild, nextUrl, newBreadcrumbs);
    }

    return newBreadcrumbs;
  }

  getParentUrlIfB2C(url): string {
    if (url?.includes('my-account/company/information') && !this.userDetailsService.isB2B()) {
      return 'my-account/my-account-information';
    }
    return url;
  }

  getSelectedParam(item: string): string {
    ////if custom selection to be shown, By default last param will be rendered
    return this.activatedRoute.snapshot.params[item];
  }

  getParamLabel(item: string): string {
    return this.getSelectedParam(item);
  }

  getParamId(id: string) {
    if (id) {
      return id.split(' ').join('-');
    }
  }

  goTo(parent: any) {
    let url = parent.parentUrl;
    url = url.replace(':' + parent.param, this.activatedRoute.snapshot.params[parent.param]);
    this.router.navigate([url]);
  }

  ngOnDestroy(): void {
    this.subscription.unsubscribe();
  }
}
