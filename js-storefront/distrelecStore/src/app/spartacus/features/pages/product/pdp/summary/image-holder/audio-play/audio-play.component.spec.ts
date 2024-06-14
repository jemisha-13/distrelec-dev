import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AudioPlayComponent } from './audio-play.component';
import { DistrelecSummaryModule } from '../../summary.module';
import { MOCK_PRODUCT_WITH_AUDIOS } from 'src/app/spartacus/features/mocks/mock-product-with-audios';
import { TranslationService } from '@spartacus/core';
import { Observable, of } from 'rxjs';

describe('AudioPlayComponent', () => {
  let component: AudioPlayComponent;
  let fixture: ComponentFixture<AudioPlayComponent>;

  const MockTranslationService = {
    translate(key: string): Observable<string> {
      return of('Home');
    },
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DistrelecSummaryModule],
      declarations: [AudioPlayComponent],
      providers: [{ provide: TranslationService, useValue: MockTranslationService }],
    }).compileComponents();

    fixture = TestBed.createComponent(AudioPlayComponent);
    component = fixture.componentInstance;

    component.showDropdown = true;
    component.audioFiles = MOCK_PRODUCT_WITH_AUDIOS.audios;

    fixture.detectChanges();
  });

  it('product should have more than one audio', () => {
    component.audioFiles = MOCK_PRODUCT_WITH_AUDIOS.audios;

    fixture.detectChanges();

    expect(component.audioFiles?.length).toBeGreaterThan(1);
  });

  it('dropdown should open if showDropdown && audioFiles.length are true', () => {
    const dropdownElement = fixture.debugElement.nativeElement.querySelector('.dropdown');

    fixture.detectChanges();

    expect(dropdownElement).toBeTruthy();
  });

  it('should play audio if toggleAudios is clicked', () => {
    component.audioFiles = MOCK_PRODUCT_WITH_AUDIOS.audios;
    spyOn(window.HTMLAudioElement.prototype, 'play').and.stub();

    const audioButton = fixture.nativeElement.querySelector('.icon-audio-list');
    audioButton.click();

    expect(window.HTMLAudioElement.prototype.play).toHaveBeenCalled();
  });
});
