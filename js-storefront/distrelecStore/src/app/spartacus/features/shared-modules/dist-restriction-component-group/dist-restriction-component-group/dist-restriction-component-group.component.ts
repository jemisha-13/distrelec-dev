import { Component } from '@angular/core';
import { CmsComponentData } from '@spartacus/storefront';
import { CmsService, ContentSlotComponentData, OccEndpointsService, UserIdService } from '@spartacus/core';
import { combineLatest, Observable, of } from 'rxjs';
import { filter, map, switchMap } from 'rxjs/operators';
import { HttpClient } from '@angular/common/http';
import { CmsDistRestrictionComponentGroup } from '@model/cms.model';

@Component({
  selector: 'app-dist-restriction-component-group',
  templateUrl: './dist-restriction-component-group.component.html',
})
export class DistRestrictionComponentGroupComponent {
  isRestricted$: Observable<boolean> = this.userIdService.getUserId().pipe(
    map((userId: string) =>
      this.occEndpoints.buildUrl(`/cms/isRestricted/${userId}`, { queryParams: { componentId: this.data.uid } }),
    ),
    switchMap((url: string) => this.http.get<boolean>(url)),
  );

  componentData$: Observable<ContentSlotComponentData[]> = this.data.data$.pipe(
    map((data: CmsDistRestrictionComponentGroup) => data.components.split(' ')),
    switchMap((componentIds: string[]) =>
      combineLatest<ContentSlotComponentData[]>(componentIds.map((componentId) => this.getComponentData(componentId))),
    ),
  );

  visibleComponents$: Observable<ContentSlotComponentData[]> = this.isRestricted$.pipe(
    switchMap((isRestricted: boolean) => (isRestricted ? of([]) : this.componentData$)),
  );

  constructor(
    private data: CmsComponentData<CmsDistRestrictionComponentGroup>,
    private occEndpoints: OccEndpointsService,
    private userIdService: UserIdService,
    private http: HttpClient,
    private cmsService: CmsService,
  ) {}

  private getComponentData(id: string): Observable<ContentSlotComponentData> {
    return this.cmsService.getComponentData(id).pipe(
      filter<ContentSlotComponentData>(Boolean),
      map((componentData: ContentSlotComponentData) => ({
        ...componentData,
        flexType: componentData.flexType ?? componentData.typeCode,
      })),
    );
  }
}
