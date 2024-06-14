import { Component, Input, OnInit, AfterViewChecked, OnDestroy } from '@angular/core';
import { UntypedFormBuilder, UntypedFormGroup } from '@angular/forms';
import { faPrint, faSearch } from '@fortawesome/free-solid-svg-icons';
import { createFrom, EventService, Feature, Product, WindowRef } from '@spartacus/core';
import { ProductSpecificationsService } from '@features/pages/product/pdp/product-specifications/product-specifications.service';
import { SessionStorageService } from '@services/session-storage.service';
import { SearchQueryService } from '../../core/services/abstract-product-search-query.service';
import { PrintPageEvent } from '@features/tracking/events/print-page-event';
import { ItemListEntity } from '@features/tracking/model/generic-event-types';

@Component({
  selector: 'app-product-specifications',
  templateUrl: './product-specifications.component.html',
  styleUrls: ['./product-specifications.component.scss'],
})
export class ProductSpecificationsComponent implements OnInit, AfterViewChecked, OnDestroy {
  @Input() productData: Product;

  productAttributesForm: UntypedFormGroup = this.fb.group({});

  faSearch = faSearch;
  faPrint = faPrint;

  isDisabledProductSearch = true;
  features;
  featuresUncollapsed;
  filteredFeatures = [];

  productCategoryCode: string;

  windowOrigin = this.winRef.location.origin;

  searchQueryParams = { useTechnicalView: 'true' };

  readMoreCollapsedState = true;
  readMoreCountVisible = 5;
  showFilteredFeatures = false;

  constructor(
    private winRef: WindowRef,
    private fb: UntypedFormBuilder,
    private productSpecificationsService: ProductSpecificationsService,
    private sessionStorageService: SessionStorageService,
    private searchQueryService: SearchQueryService,
    private eventService: EventService,
  ) {}

  print() {
    this.eventService.dispatch(createFrom(PrintPageEvent, { context: { pageType: ItemListEntity.PDP } }));
    window.print();
  }

  ngOnInit() {
    if (this.productData) {
      this.features = this.productData.classifications?.[0]?.features.filter((item) => {
        if (!item.searchable) {
          this.filteredFeatures.push(item);
          return false;
        }
        return true;
      });

      this.featuresUncollapsed = this.features;
      this.productCategoryCode = this.productData?.categories[0]?.code;
    }
  }

  ngOnDestroy(): void {
    this.productSpecificationsService.resetCheckBoxState();
  }

  ngAfterViewChecked(): void {
    if (this.readMoreCollapsedState) {
      this.loadCollapseState();
      this.loadFilteredFeaturesState();
    }
    this.collapseStateHideElements();
  }

  sortFeatureValues(featureValues) {
    return featureValues.slice().sort((a, b) => (a.value < b.value ? -1 : 1));
  }

  getTransformedId(checkboxId: string): string {
    return checkboxId.toString().split(' ').join('-');
  }

  isCheckedBefore(feature: Feature): string {
    if (this.productSpecificationsService.isProductFeaturesCheckboxPreviouslyChecked(feature?.name)) {
      this.onCheckFeature(feature, true);
      return 'checked';
    }

    this.onCheckFeature(feature, false);
    return '';
  }

  addProductAttributesForm(transformedId: string): void {
    this.productAttributesForm.addControl(`attribute-${transformedId}`, this.fb.control(transformedId));
  }

  removeProductAttributesForm(transformedId: string): void {
    this.productAttributesForm.removeControl(`attribute-${transformedId}`);
  }

  checkIsDisabledProductSearch(): void {
    this.isDisabledProductSearch = Object.keys(this.productAttributesForm.controls).length === 0;
  }

  onCheck(item, event): void {
    if (event.target.checked) {
      this.productSpecificationsService.saveCheckedCheckboxId(item.name);
    } else {
      this.productSpecificationsService.removeCheckedCheckboxId(item.name);
    }

    this.onCheckFeature(item, event.target.checked);
  }

  toggleCollapsedState(): void {
    this.readMoreCollapsedState = !this.readMoreCollapsedState;
    this.showFilteredFeatures = !this.readMoreCollapsedState;
    this.saveCollapseState();
    this.collapseStateHideElements();
    this.saveFilteredFeaturesState();
  }

  loadCollapseState(): void {
    const sessionCollapseState = this.sessionStorageService.getItem(this.getReadMoreLessSessionId());
    if (sessionCollapseState) {
      this.readMoreCollapsedState = sessionCollapseState === '1';
    }
  }

  loadFilteredFeaturesState(): void {
    const sessionFilteredFeaturesState = this.sessionStorageService.getItem('filteredFeaturesState');
    if (sessionFilteredFeaturesState) {
      this.showFilteredFeatures = sessionFilteredFeaturesState === '1';
    }
  }

  saveCollapseState(): void {
    this.sessionStorageService.setItem(this.getReadMoreLessSessionId(), this.readMoreCollapsedState ? '1' : '0');
  }

  saveFilteredFeaturesState(): void {
    this.sessionStorageService.setItem('filteredFeaturesState', this.showFilteredFeatures ? '1' : '0');
  }

  collapseStateHideElements(): void {
    if (this.readMoreCollapsedState) {
      this.features = this.featuresUncollapsed?.slice(0, this.readMoreCountVisible);
    } else {
      this.features = this.featuresUncollapsed;
    }
  }

  getReadMoreLessSessionId(): string {
    return 'readMoreLessColapseState_pdp_product_specifications_' + this.productData.id;
  }

  private onCheckFeature(feature: Feature, isChecked: boolean): void {
    const transformedId = this.getTransformedId(feature.name);
    const { key, value } = this.searchQueryService.buildProductSpecificationsQuery(feature);

    if (isChecked) {
      this.addProductAttributesForm(transformedId);
      this.searchQueryParams[key] = value;
    } else {
      this.removeProductAttributesForm(transformedId);
      delete this.searchQueryParams[key];
    }

    this.isDisabledProductSearch = Object.keys(this.productAttributesForm.controls).length === 0;
  }
}
