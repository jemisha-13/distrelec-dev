import { Component, OnInit } from '@angular/core';
import { RoutingService } from '@spartacus/core';
import { Observable } from 'rxjs';
import { filter, map, switchMap } from 'rxjs/operators';
import { CategoriesService } from '@services/categories.service';
import { CategoryPageData } from '@model/category.model';

@Component({
  selector: 'app-content',
  templateUrl: './content.component.html',
  styleUrls: ['./content.component.scss'],
})
export class ContentComponent implements OnInit {
  categoryData$: Observable<CategoryPageData>;

  constructor(
    private categoriesService: CategoriesService,
    private routingService: RoutingService,
  ) {}

  ngOnInit() {
    this.categoryData$ = this.routingService.getRouterState().pipe(
      filter((routerState) => !routerState.nextState),
      map((routerState) => routerState.state),
      switchMap((state) => this.categoriesService.getCategoryData(state.params.categoryCode)),
    );
  }
}
