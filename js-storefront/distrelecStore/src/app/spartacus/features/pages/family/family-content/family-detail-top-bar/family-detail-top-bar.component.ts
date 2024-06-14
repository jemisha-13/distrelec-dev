import { ChangeDetectorRef, Component, OnDestroy, OnInit, ViewEncapsulation } from '@angular/core';
import { ProductFamilyService } from '@features/pages/product/core/services/product-family.service';
import { OccEndpointsService, RoutingService, WindowRef } from '@spartacus/core';
import { BehaviorSubject, Observable, Subscription } from 'rxjs';
import { concatMap, map } from 'rxjs/operators';
import { Meta } from '@angular/platform-browser';
import { ExternalSource, ProductFamilyData } from '@model/product-family.model';
import { ActivatedRoute, Router } from '@angular/router';
import { HeaderService } from '@features/shared-modules/header/header.service';
import { DistProductListComponentService } from '@features/pages/product/core/services/dist-product-list-component.service';

@Component({
  selector: 'app-family-detail-top-bar',
  templateUrl: './family-detail-top-bar.component.html',
  styleUrls: ['./family-detail-top-bar.component.scss'],
  encapsulation: ViewEncapsulation.None,
})
export class ProductFamilyDetailTopBarComponent implements OnInit, OnDestroy {
  public familyData$: Observable<ProductFamilyData>;
  public pdfTitle = '';
  public familyImage = '';
  public familyMedia = '';
  public videoUrls: string[] = [];
  public isBrowser = false;

  familyImageLarge = '';
  displayPreview$: BehaviorSubject<string> = new BehaviorSubject<string>('');
  manufacturerUrl$: Observable<string> = this.productListComponentService.searchResults$.pipe(
    map((model) => model?.products[0]?.distManufacturer?.url ?? ''),
  );

  private subscriptions: Subscription = new Subscription();

  constructor(
    private routingService: RoutingService,
    protected occEndpoints: OccEndpointsService,
    private familyService: ProductFamilyService,
    private changeDetector: ChangeDetectorRef,
    private metaService: Meta,
    private winRef: WindowRef,
    private router: Router,
    private activatedRoute: ActivatedRoute,
    private headerService: HeaderService,
    private productListComponentService: DistProductListComponentService,
  ) {}

  ngOnInit() {
    if (this.winRef.isBrowser()) {
      this.isBrowser = true;
    }
    this.familyData$ = this.routingService.getRouterState().pipe(
      concatMap((data) => {
        const ctxArr: string[] = data.state.context.id.split('/');
        const familyCode: string = ctxArr[ctxArr.length - 1];
        return this.familyService.getFamilyData(familyCode);
      }),
    );

    this.subscriptions.add(
      this.familyData$.subscribe((res: ProductFamilyData) => {
        const currentPath = '/' + this.activatedRoute.snapshot.url.join('/');
        if (currentPath !== res.url) {
          this.router.navigate([res.url], {
            replaceUrl: true,
            queryParams: this.activatedRoute.snapshot.queryParams,
          });
        }
      }),
    );

    this.subscriptions.add(
      this.familyData$.subscribe((res: ProductFamilyData) => {
        this.familyService.getFamilyBreadcrumb(res.familyCategoryCode).subscribe();

        if (res.familyDatasheet) {
          const arr: string[] = res.familyDatasheet[0].value.url.split('/');
          this.pdfTitle = arr[arr.length - 1];
        }
        this.getImage(res);
        this.formatVideoUrls(res);
        this.addMetaData(res);

        this.changeDetector.detectChanges();
      }),
    );
  }

  jumpTo(elementId: string): void {
    if (!elementId) {
      return;
    }

    if (this.isBrowser) {
      const headerHeight = this.headerService.headerHeight;
      const elementPosition: number = document.getElementById(elementId).getBoundingClientRect().top;
      const windowPosition: number = window.scrollY;
      const amount: number = elementPosition + windowPosition - headerHeight;

      window.scrollTo({ top: amount, behavior: 'smooth' });
    }
  }

  onImageClick(imageUrl) {
    this.displayPreview$.next(imageUrl);
  }

  ngOnDestroy() {
    this.subscriptions.unsubscribe();
  }

  private addMetaData(data: ProductFamilyData): void {
    if (data?.seoMetaTitle) {
      this.metaService.updateTag({
        name: 'og:title',
        content: data.seoMetaTitle,
      });
    }
    if (data?.seoMetaDescription) {
      this.metaService.updateTag({
        name: 'og:description',
        content: data.seoMetaDescription,
      });
    }
    if (this.familyImage) {
      this.metaService.updateTag({
        name: 'og:image',
        content: this.familyImage,
      });
    }
  }

  private formatVideoUrls(data: ProductFamilyData): void {
    if (data?.familyVideo.length) {
      for (const video of data?.familyVideo) {
        const arr: string[] = video.youtubeUrl.split('/');
        const code: string = arr[arr.length - 1];
        this.videoUrls.push(`https://www.youtube.com/embed/${code}`);
      }
    }
  }

  private getImage(data: ProductFamilyData): void {
    if (data?.familyImage) {
      data.familyImage.forEach((image: ExternalSource, i: number) => {
        if (data?.familyImage.length === 5 && i === 4) {
          this.familyImage = encodeURI(image.value.url);
        }
        if (data?.familyImage.length > 1 && data.familyImage.length < 5 && data.familyImage.length - 1 === i) {
          this.familyImage = encodeURI(image.value.url);
        } else {
          this.familyImage = encodeURI(image.value.url);
        }
        if (image.value.format === 'landscape_large') {
          this.familyImageLarge = encodeURI(image.value.url);
        }
      });
    }

    if (data?.familyMedia) {
      data?.familyMedia.forEach((image: ExternalSource): void => {
        if (image.key === 'landscape_large') {
          this.familyMedia = encodeURI(image.value.url);
        }
      });
    }
  }
}
