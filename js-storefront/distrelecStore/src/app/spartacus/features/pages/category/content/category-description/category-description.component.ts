import { Component, Input } from '@angular/core';
import { CategoryPageData } from '@model/category.model';
import { arrowDown, arrowUp } from '@assets/icons/icon-index';

@Component({
  selector: 'app-category-description',
  templateUrl: './category-description.component.html',
  styleUrls: ['./category-description.component.scss'],
})
export class CategoryDescriptionComponent {
  @Input() categoryData: CategoryPageData;

  isExpanded: boolean = false;
  arrowUp = arrowUp;
  arrowDown = arrowDown;

  toggleReadMore() {
    this.isExpanded = !this.isExpanded;
  }
}
