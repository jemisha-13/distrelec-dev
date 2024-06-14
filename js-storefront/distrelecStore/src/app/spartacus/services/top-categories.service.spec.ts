import { inject, TestBed } from '@angular/core/testing';
import { TopCategoriesService } from './top-categories.service';
import { CommonTestingModule } from '@features/shared-modules/common-testing.module';

describe('Service: TopCategories', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [CommonTestingModule],
      providers: [TopCategoriesService],
    });
  });

  it('should ...', inject([TopCategoriesService], (service: TopCategoriesService) => {
    expect(service).toBeTruthy();
  }));
});
