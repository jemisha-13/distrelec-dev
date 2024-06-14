import { Component } from '@angular/core';
import { CategoriesService } from '@services/categories.service';

@Component({
  selector: 'app-category-index',
  templateUrl: './category-index.component.html',
  styleUrls: ['./category-index.component.scss'],
})
export class CategoryIndexComponent {
  categories$ = this.categoryService.getCategoryIndex();

  constructor(private categoryService: CategoriesService) {}
}
