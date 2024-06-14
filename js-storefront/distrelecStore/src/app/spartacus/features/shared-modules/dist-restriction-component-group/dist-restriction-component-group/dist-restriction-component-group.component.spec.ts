import { ComponentFixture, fakeAsync, TestBed, tick } from '@angular/core/testing';
import { HttpClient } from '@angular/common/http';
import { CmsService, OccEndpointsService, UserIdService } from '@spartacus/core';
import { CmsComponentData } from '@spartacus/storefront';
import { BehaviorSubject, of } from 'rxjs';

import { DistRestrictionComponentGroupComponent } from './dist-restriction-component-group.component';
import { CmsDistRestrictionComponentGroup } from '@model/cms.model';
import { CommonTestingModule } from '@features/shared-modules/common-testing.module';
import { DistRestrictionComponentGroupModule } from '@features/shared-modules/dist-restriction-component-group/dist-restriction-component-group/dist-restriction-component-group.module';

const RESTRICTION_GROUP_COMPONENT_DATA = {
  uid: 'upload-page-link-group',
  uuid: 'eyJpdGVtSWQiOiJ1cGxvYWQtcGFnZS1saW5rLWdyb3VwIiwiY2F0YWxvZ0lkIjoiZGlzdHJlbGVjX0ludENvbnRlbnRDYXRhbG9nIiwiY2F0YWxvZ1ZlcnNpb24iOiJPbmxpbmUifQ==',
  typeCode: 'DistRestrictionComponentGroup',
  modifiedtime: '2024-04-09T17:56:42.313+02:00',
  name: 'upload-page-link-group',
  container: 'false',
  components: 'upload-page-link',
  synchronizationBlocked: 'false',
};

const CHILD_COMPONENT_DATA = {
  uid: 'upload-page-link',
  uuid: 'eyJpdGVtSWQiOiJ1cGxvYWQtcGFnZS1saW5rIiwiY2F0YWxvZ0lkIjoiZGlzdHJlbGVjX0ludENvbnRlbnRDYXRhbG9nIiwiY2F0YWxvZ1ZlcnNpb24iOiJPbmxpbmUifQ==',
  typeCode: 'CMSParagraphComponent',
  modifiedtime: '2024-04-09T15:56:42+0000',
  name: 'upload-page-link',
  container: 'false',
  synchronizationBlocked: 'false',
  content:
    '<div class="aside-quality-link">\n<div class="row">\n<div class="col-md-12">\n<h3 id="quality_report_download_page_label">RoHS/REACH - SCIP</h3>\n<p><a href="/environmental-documentation-download" id="quality_report_download_page_link">Download your Environmental Documentation here</a></p>\n</div>\n</div>\n</div>',
};

const CMS_COMPONENT_DATA: CmsComponentData<CmsDistRestrictionComponentGroup> = {
  data$: of(RESTRICTION_GROUP_COMPONENT_DATA),
  uid: 'upload-page-link-group',
};

describe('DistRestrictionComponentGroupComponent', () => {
  let component: DistRestrictionComponentGroupComponent;
  let fixture: ComponentFixture<DistRestrictionComponentGroupComponent>;

  let userId$: BehaviorSubject<string>;
  let userIdServiceSpy: jasmine.SpyObj<UserIdService>;
  let httpSpy: jasmine.SpyObj<HttpClient>;
  let occEndpointsSpy: jasmine.SpyObj<OccEndpointsService>;
  let cmsServiceSpy: jasmine.SpyObj<CmsService>;

  beforeEach(async () => {
    userIdServiceSpy = jasmine.createSpyObj('UserIdService', ['getUserId']);
    httpSpy = jasmine.createSpyObj('HttpClient', ['get']);
    occEndpointsSpy = jasmine.createSpyObj('OccEndpointsService', ['buildUrl']);
    cmsServiceSpy = jasmine.createSpyObj('CmsService', ['getComponentData']);

    userId$ = new BehaviorSubject<string>('anonymous');
    userIdServiceSpy.getUserId.and.returnValue(userId$.asObservable());
    cmsServiceSpy.getComponentData.and.returnValue(of(CHILD_COMPONENT_DATA));
    occEndpointsSpy.buildUrl.and.callFake((path) => path);

    await TestBed.configureTestingModule({
      imports: [CommonTestingModule, DistRestrictionComponentGroupModule],
      providers: [
        { provide: CmsComponentData, useValue: CMS_COMPONENT_DATA },
        { provide: OccEndpointsService, useValue: occEndpointsSpy },
        { provide: UserIdService, useValue: userIdServiceSpy },
        { provide: HttpClient, useValue: httpSpy },
        { provide: CmsService, useValue: cmsServiceSpy },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(DistRestrictionComponentGroupComponent);
    component = fixture.componentInstance;
  });

  it('should create', () => {
    fixture.detectChanges();
    expect(component).toBeTruthy();
  });

  it('should not show components when a restriction is active', fakeAsync(() => {
    httpSpy.get.and.returnValue(of(true));

    component.visibleComponents$.subscribe((visibleComponents) => {
      expect(visibleComponents).toEqual([]);
    });

    tick();
    fixture.detectChanges();
  }));

  it('should show components when a restriction is not active', fakeAsync(() => {
    httpSpy.get.and.returnValue(of(false));

    component.visibleComponents$.subscribe((visibleComponents) => {
      expect(visibleComponents.length).toBe(1);
    });

    tick();
    fixture.detectChanges();
  }));

  it('should re-evaluate restriction when user logs in', fakeAsync(() => {
    httpSpy.get.and.returnValue(of(false));

    tick();
    fixture.detectChanges();
    expect(httpSpy.get).toHaveBeenCalledTimes(1);

    userId$.next('current');

    tick();
    fixture.detectChanges();
    expect(httpSpy.get).toHaveBeenCalledTimes(2);
  }));
});
