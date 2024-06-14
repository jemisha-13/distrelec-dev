import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { Breadcrumb } from '@model/breadcrumb.model';
import { BreadcrumbService } from '@services/breadcrumb.service';
import { Observable } from 'rxjs';
import { filter, map, tap } from 'rxjs/operators';

@Component({
  selector: 'app-back-to-category-cta',
  templateUrl: './back-to-category-cta.component.html',
  styleUrls: ['./back-to-category-cta.component.scss'],
})
export class BackToCategoryComponent {
  private category: Breadcrumb;
  category$ = this.breadcrumbService.getBreadcrumbs().pipe(
    filter((breadcrumbs) => breadcrumbs.length > 0 && breadcrumbs[breadcrumbs.length - 1].url !== '/'),
    map((breadcrumbs) => breadcrumbs[breadcrumbs.length - 1]),
    tap((category) => (this.category = category)),
  );

  constructor(
    private breadcrumbService: BreadcrumbService,
    private router: Router,
  ) {}

  redirectToCategoryPage(): void {
    this.router.navigate([this.category.url]);
  }
}
