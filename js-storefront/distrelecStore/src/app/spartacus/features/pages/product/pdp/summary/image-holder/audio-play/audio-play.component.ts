import {
  ChangeDetectorRef,
  Component,
  DestroyRef,
  HostListener,
  Input,
  OnDestroy,
  OnInit,
  inject,
} from '@angular/core';
import { NavigationStart, Router } from '@angular/router';
import { arrowUp, arrowDown, audio, audioBlack } from '@assets/icons/icon-index';
import { AudioData } from '@model/product.model';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';

@Component({
  selector: 'app-audio-play',
  templateUrl: './audio-play.component.html',
  styleUrls: ['./audio-play.component.scss'],
})
export class AudioPlayComponent implements OnInit, OnDestroy {
  @Input() audioFiles: AudioData[];

  iconArrowUp = arrowUp;
  iconArrowDown = arrowDown;
  iconAudio = audio;
  iconAudioBlack = audioBlack;

  showDropdown = false;

  audioInstances: HTMLAudioElement[] = [];
  playbackStates: boolean[] = [];

  private destroyRef: DestroyRef = inject(DestroyRef);

  constructor(
    private changeDetectorRef: ChangeDetectorRef,
    private router: Router,
  ) {}

  @HostListener('document: visibilitychange', ['$event'])
  appVisibility(): void {
    if (document.hidden) {
      this.stopAudio();
    }
  }

  @HostListener('document:click', ['$event'])
  onClick(event: MouseEvent): void {
    const clickedElement = event.target as HTMLElement;

    if (this.audioFiles.length > 1) {
      let isDropdownClicked =
        (clickedElement.closest('.audio-item') || clickedElement.closest('.icon-audio-list')) !== null;

      if (!isDropdownClicked) {
        isDropdownClicked = false;
        this.stopAudio();
      }
    }

    if (this.audioFiles.length === 1) {
      let isAudioBoxClicked = clickedElement.closest('.audio-box') || clickedElement.closest('.icon-audio') !== null;

      if (!isAudioBoxClicked) {
        isAudioBoxClicked = false;
        this.stopAudio();
      }
    }
  }

  ngOnInit(): void {
    this.router.events.pipe(takeUntilDestroyed(this.destroyRef)).subscribe((event) => {
      if (event instanceof NavigationStart) {
        this.stopAudio();
      }
    });
  }

  toggleDropDown(): void {
    this.showDropdown = !this.showDropdown;
  }

  toggleAudioBoxFunctions(): void {
    if (this.audioFiles?.length === 1) {
      this.toggleAudios(0);
    } else if (this.audioFiles?.length > 1) {
      this.toggleDropDown();
    }
  }

  toggleAudios(i: number): void {
    this.audioInstances.forEach((instance, index) => {
      if (index !== i && this.playbackStates[index]) {
        instance.pause();
        this.playbackStates[index] = false;
      }
    });

    const audioItem = this.audioFiles[i];
    if (!this.audioInstances[i]) {
      this.audioInstances[i] = new Audio(audioItem.audioUrl);
      this.audioInstances[i].onended = () => {
        this.playbackStates[i] = false;
        this.changeDetectorRef.detectChanges();
      };
    }

    const audioInstance = this.audioInstances[i];
    if (audioInstance.paused) {
      audioInstance.play();
      this.playbackStates[i] = true;
    } else {
      audioInstance.pause();
      this.playbackStates[i] = false;
    }
  }

  stopAudio(): void {
    this.audioInstances.forEach((instance, index) => {
      if (instance) {
        instance.pause();
        this.playbackStates[index] = false;
      }
    });
  }

  ngOnDestroy(): void {
    this.stopAudio();
  }
}
